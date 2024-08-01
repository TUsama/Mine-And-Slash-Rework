package com.robertx22.age_of_exile.gui.screens.skill_tree.buttons.drawer;

import com.google.common.util.concurrent.RateLimiter;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.platform.Window;
import com.robertx22.age_of_exile.database.data.perks.Perk;
import com.robertx22.age_of_exile.database.data.talent_tree.TalentTree;
import com.robertx22.age_of_exile.event_hooks.ontick.OnClientTick;
import com.robertx22.age_of_exile.gui.screens.skill_tree.ExileTreeTexture;
import com.robertx22.age_of_exile.gui.screens.skill_tree.PainterController;
import com.robertx22.age_of_exile.gui.screens.skill_tree.buttons.PerkButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

// todo maybe need rewrite this with state machine design mode
public class AllPerkButtonPainter {

    private final int typeHash;

    //todo is this really a good collection to store changed renderers? Slow in searching.
    //anyway we prob won't have that much data at the same time.

    private final HashSet<ButtonIdentifier> updating = new HashSet<>();
    private final HashSet<ButtonIdentifier> updateOnThisScreen = new HashSet<>();
    public PainterState state;
    public int maxX = 0;
    public int minX = 10000;
    public int maxY = 0;
    public int minY = 10000;
    private final InitState initState;
    private final PaintState paintState;
    private final RegisterState registerState;

    private final StandbyState standByState;
    private final ExecutorService paintThread = Executors.newSingleThreadExecutor();
    private int drawInWindowWidth = 0;
    private boolean isPainting = false;
    private boolean isRepainting = false;

    public AllPerkButtonPainter(TalentTree.SchoolType type) {
        this.typeHash = type.toString().hashCode();
        this.initState = new InitState(this);
        this.paintState = new PaintState(this);
        this.registerState = new RegisterState(this);
        this.standByState = new StandbyState(this);
        this.state = this.initState;
    }

    public static AllPerkButtonPainter getPainter(TalentTree.SchoolType schoolType) {
        AllPerkButtonPainter allPerkButtonPainter;
        int i = schoolType.toString().hashCode();
        if (!OnClientTick.container.containsKey(i)) {
            allPerkButtonPainter = new AllPerkButtonPainter(schoolType);
            OnClientTick.container.put(i, allPerkButtonPainter);
        }
        return OnClientTick.container.get(i);
    }




    public void addToUpdate(ButtonIdentifier identifier) {
        this.updateOnThisScreen.add(identifier);

    }

    public void updateOnScreenClose() {
        if (updateOnThisScreen.isEmpty()) return;
        this.cache.addAll(updateOnThisScreen);
        this.waitingToBeUpdated.addAll(this.cache);
        updateOnThisScreen.clear();
    }



    public ResourceLocation getThisLocation() {
        return new ResourceLocation(PainterController.nameSpace, "" + typeHash);
    }

    public boolean isAllowedToPaint() {
        return !locations.isEmpty();
    }

    public boolean isThisButtonIsUpdating(PerkButton button) {
        ButtonIdentifier buttonIdentifier = button.buttonIdentifier;
        return updateOnThisScreen.contains(buttonIdentifier) || this.waitingToBeUpdated.contains(buttonIdentifier) || this.updating.contains(buttonIdentifier);
    }

    public void checkIfNeedRepaint() {
        // due to window size change
        if (!this.isPainting && !this.isRepainting && this.drawInWindowWidth != 0 && Minecraft.getInstance().getWindow().getGuiScaledWidth() != this.drawInWindowWidth) {
            this.isRepainting = true;
            repaint();
        }
    }

    @SuppressWarnings("all")
    private  <T> T changeState(T state) {
        this.state = (PainterState)state;
        return state;
    }

    public record ResourceLocationAndSize(ResourceLocation location, int width, int height) {

    }

    abstract class PainterState<T> {

        public final AllPerkButtonPainter painter;

        public PainterState(AllPerkButtonPainter painter) {
            this.painter = painter;
        }

        public abstract void onInit(Collection<ButtonIdentifier> identifiers);

        public abstract void onPaint();

        public abstract void onRegister();

        public abstract void onRepaint();
        public abstract void onOpenSkillScreen();
        public abstract void onCloseSkillScreen();

        public abstract Collection<T> getHandledContainer();
        public PainterState transferData(Collection<T> data){
            this.getHandledContainer().addAll(data);
            return this;
        }

    }

    class InitState extends PainterState<ButtonIdentifier> {

        private final HashSet<ButtonIdentifier> cache = new HashSet<>(3000);

        public InitState(AllPerkButtonPainter painter) {
            super(painter);
        }

        @Override
        public void onInit(Collection<ButtonIdentifier> identifiers) {
            System.out.println("init all painter");
            cache.addAll(identifiers);
            System.out.println("at this time, input amount is: " + identifiers.size());
            System.out.println("cached amount is: " + cache.size());
            painter.changeState(painter.paintState).transferData(cache).onPaint();
        }

        @Override
        public void onPaint() {

        }

        @Override
        public void onRegister() {

        }

        @Override
        public void onRepaint() {

        }

        @Override
        public void onOpenSkillScreen() {

        }

        @Override
        public void onCloseSkillScreen() {

        }

        @Override
        public Set<ButtonIdentifier> getHandledContainer() {
            return cache;
        }
    }

    class PaintState extends PainterState<ButtonIdentifier> {
        private RateLimiter limiter = RateLimiter.create(5);

        private CompletableFuture<Void> voidCompletableFuture;
        public final ConcurrentLinkedQueue<ButtonIdentifier> waitingToBeUpdated = new ConcurrentLinkedQueue<>();

        public PaintState(AllPerkButtonPainter painter) {
            super(painter);
        }

        @Override
        public void onInit(Collection<ButtonIdentifier> identifiers) {

        }

        @Override
        public void onPaint() {
            if (!voidCompletableFuture.isDone()) return;
            AtomicReference<SeparableBufferedImage> image = null;
            voidCompletableFuture = CompletableFuture.runAsync(() -> {
                try {
                    image.set(tryPaint());
                } catch (InterruptedException | IOException e) {
                    throw new RuntimeException(e);
                }
                System.out.println("add to register!");

            }, paintThread).thenRun(() -> {
                painter.changeState(painter.registerState).transferData(image.get().getSeparatedImage()).onRegister();
            });
        }

        @Override
        public void onRegister() {

        }

        @Override
        public void onRepaint() {
            paintThread.shutdown();
            waitingToBeUpdated.clear();
            painter.changeState(painter.registerState).onRepaint();
        }

        @Override
        public void onOpenSkillScreen() {

        }

        @Override
        public void onCloseSkillScreen() {

        }

        @Override
        public Queue<ButtonIdentifier> getHandledContainer() {
            return this.waitingToBeUpdated;
        }

        private SeparableBufferedImage tryPaint() throws InterruptedException, IOException {
            Minecraft mc = Minecraft.getInstance();
            Window window = mc.getWindow();
            BufferedImage image = new BufferedImage(window.getGuiScaledWidth() * PerkButton.SPACING, window.getGuiScaledHeight() * PerkButton.SPACING, BufferedImage.TYPE_INT_ARGB);

            painter.drawInWindowWidth = window.getGuiScaledWidth();
            Graphics2D graphics = image.createGraphics();

            float halfx = mc.getWindow().getGuiScaledWidth() / 2F;
            float halfy = mc.getWindow().getGuiScaledHeight() / 2F;
            int maxX = 0;
            int minX = 10000;
            int maxY = 0;
            int minY = 10000;
            int painted = 0;
            // prefer a little multi.
            float singleButtonZoom = 1.4f;
            while (!waitingToBeUpdated.isEmpty()) {
                PainterController.paintLimiter.acquire();
                ButtonIdentifier identifier = waitingToBeUpdated.poll();
                Perk.PerkType type = identifier.perk().getType();
                ResourceLocation wholeTexture = identifier.getCurrentButtonLocation();


                BufferedImage singleButton = PerkButtonPainter.handledBufferedImage.get(wholeTexture);
                if (singleButton == null) {
                    waitingToBeUpdated.add(identifier);
                    Thread.sleep(2000);
                    continue;
                }

                int singleButtonSize = (int) (type.size * singleButtonZoom + 1);
                BufferedImage redesignSingleButton = new BufferedImage(singleButtonSize, singleButtonSize, BufferedImage.TYPE_INT_ARGB);
                Graphics2D redesignSingleButtonGraphics = redesignSingleButton.createGraphics();

                redesignSingleButtonGraphics.drawImage(singleButton, 0, 0, singleButtonSize, singleButtonSize, null);

                redesignSingleButtonGraphics.dispose();
                singleButton = redesignSingleButton;

                float x = (identifier.point().x) * PerkButton.SPACING;
                float y = (identifier.point().y) * PerkButton.SPACING;

                x -= singleButtonSize / 2F;
                y -= singleButtonSize / 2F;

                int tx = (int) (halfx + x);
                int ty = (int) (halfy + y);
                maxX = Math.max(tx, maxX);
                minX = Math.min(tx, minX);
                maxY = Math.max(ty, maxY);
                minY = Math.min(ty, minY);
                AffineTransform affineTransform = new AffineTransform();

                affineTransform.translate(tx, ty);

                graphics.drawImage(singleButton, affineTransform, null);
                painted++;
                updating.add(identifier);
            }
            graphics.dispose();
            //unless there are some icons bigger than 50
            var newImage = image.getSubimage(minX, minY, (int) (maxX - minX + 50 * singleButtonZoom), (int) (maxY - minY + 50 * singleButtonZoom));
            painter.minX = minX;
            painter.minY = minY;
            painter.maxX = maxX;
            painter.maxY = maxY;
            painter.isPainting = false;
            return new SeparableBufferedImage(newImage);

        }
    }

    class RegisterState extends PainterState<BufferedImage> {
        private final ConcurrentLinkedQueue<BufferedImage> needRegister = new ConcurrentLinkedQueue<>();

        private List<ResourceLocationAndSize> results = new ArrayList<>();
        private final int max = 5;

        private int timer = max;

        private int counter = 0;

        public RegisterState(AllPerkButtonPainter painter) {
            super(painter);
        }

        @Override
        public void onInit(Collection<ButtonIdentifier> identifiers) {

        }

        @Override
        public void onPaint() {

        }

        @Override
        public void onRegister() {
            if (timer > 0) {
                timer--;
                return;
            }
            handleOne();
            tryCleanUp();
        }

        @Override
        public void onRepaint() {
            cleanUpAndChangeState();
        }

        @Override
        public void onOpenSkillScreen() {

        }

        @Override
        public void onCloseSkillScreen() {

        }

        @Override
        public Queue<BufferedImage> getHandledContainer() {
            return needRegister;
        }


        private void handleOne() {
            if (needRegister.isEmpty()) return;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            BufferedImage image2 = needRegister.poll();
            try {
                ImageIO.write(image2, "PNG", baos);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try (NativeImage image1 = NativeImage.read(baos.toByteArray())) {
                baos.close();
                ResourceLocation location = new ResourceLocation(getThisLocation().getNamespace(), getThisLocation().getPath() + "_" + counter);
                ExileTreeTexture exileTreeTexture = new ExileTreeTexture(location, image1);
                TextureManager textureManager = Minecraft.getInstance().getTextureManager();
                textureManager.release(location);
                textureManager.register(location, exileTreeTexture);
                results.add(new ResourceLocationAndSize(location, image2.getWidth(), image2.getHeight()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }

        private void tryCleanUp(){
            if (!needRegister.isEmpty()) return;
            painter.updating.clear();
            this.counter = 0;
            this.timer = max;
            painter.changeState(painter.standByState).transferData(results);
        }

        private void cleanUpAndChangeState(){
            painter.updating.clear();
            this.counter = 0;
            this.timer = max;
            painter.changeState(painter.standByState);
            painter.state.onRepaint();
        }
    }

    class StandbyState extends PainterState<ResourceLocationAndSize> {
        private List<ResourceLocationAndSize> locations = new ArrayList<>();

        public StandbyState(AllPerkButtonPainter painter) {
            super(painter);
        }

        @Override
        public void onInit(Collection<ButtonIdentifier> identifiers) {

        }

        @Override
        public void onPaint() {

        }

        @Override
        public void onRegister() {

        }

        @Override
        public void onRepaint() {
            System.out.println("repaint!");
            locations.clear();
        }

        @Override
        public void onOpenSkillScreen() {

        }

        @Override
        public void onCloseSkillScreen() {

        }

        @Override
        public List<ResourceLocationAndSize> getHandledContainer() {
            return locations;
        }

    }

    class ReceiveNewButtonState extends PainterState<PerkButton> {

        private HashSet<PerkButton> updateInThisScreen = new HashSet<>();
        public ReceiveNewButtonState(AllPerkButtonPainter painter) {
            super(painter);
        }

        @Override
        public void onInit(Collection<ButtonIdentifier> identifiers) {

        }

        @Override
        public void onPaint() {

        }

        @Override
        public void onRegister() {
            updateInThisScreen.clear();
            painter.changeState(painter.standByState).onRepaint();
        }

        @Override
        public void onRepaint() {

        }

        @Override
        public void onOpenSkillScreen() {

        }

        @Override
        public void onCloseSkillScreen() {

        }

        @Override
        public Set<PerkButton> getHandledContainer() {
            return updateInThisScreen;
        }
    }

}
