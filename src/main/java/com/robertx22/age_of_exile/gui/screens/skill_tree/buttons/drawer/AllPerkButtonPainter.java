package com.robertx22.age_of_exile.gui.screens.skill_tree.buttons.drawer;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.platform.Window;
import com.robertx22.age_of_exile.database.data.perks.Perk;
import com.robertx22.age_of_exile.database.data.perks.PerkStatus;
import com.robertx22.age_of_exile.database.data.talent_tree.TalentTree;
import com.robertx22.age_of_exile.event_hooks.ontick.OnClientTick;
import com.robertx22.age_of_exile.gui.screens.skill_tree.ExileTreeTexture;
import com.robertx22.age_of_exile.gui.screens.skill_tree.PainterController;
import com.robertx22.age_of_exile.gui.screens.skill_tree.buttons.PerkButton;
import com.robertx22.age_of_exile.saveclasses.perks.SchoolData;
import com.robertx22.age_of_exile.uncommon.datasaving.Load;
import com.robertx22.age_of_exile.uncommon.utilityclasses.ClientOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

public class AllPerkButtonPainter {

    private final ConcurrentLinkedQueue<SeparableBufferedImage> waitingToBeRegistered = new ConcurrentLinkedQueue<>();

    private final int typeHash;

    private TalentTree.SchoolType type;
    private final HashMap<ButtonIdentifier, ResourceLocation> cache = new HashMap<>(3000);
    //todo is this really a good collection to store changed renderers? Slow in searching.
    //anyway we prob won't have that much data at the same time.
    private final ConcurrentLinkedQueue<ButtonIdentifier> waitingToBeUpdated = new ConcurrentLinkedQueue<>();
    private final HashSet<ButtonIdentifier> updating = new HashSet<>();
    public int imageWidth = 0;
    public int imageHeight = 0;

    public int maxX = 0;
    public int minX = 10000;
    public int maxY = 0;
    public int minY = 10000;
    public List<ResourceLocationAndSize> locations = new ArrayList<>();
    private BufferedImage lastWholeImage = null;

    private int drawInWindowWidth = 0;

    private boolean isPainting = false;
    private boolean isRepainting = false;

    private int allocatedPointSetHash;

    public AllPerkButtonPainter(TalentTree.SchoolType type) {
        this.type = type;
        this.typeHash = type.toString().hashCode();
        this.allocatedPointSetHash = Load.player(ClientOnly.getPlayer()).talents.getPerks().get(type).getAllocatedPoints().hashCode();
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

    private SeparableBufferedImage tryPaint() throws InterruptedException, IOException {
        BufferedImage image;
        Minecraft mc = Minecraft.getInstance();
        Window window = mc.getWindow();
        if (lastWholeImage == null) {
            image = new BufferedImage(window.getGuiScaledWidth() * PerkButton.SPACING, window.getGuiScaledHeight() * PerkButton.SPACING, BufferedImage.TYPE_INT_ARGB);
        } else {
            image = this.lastWholeImage;
        }
        this.drawInWindowWidth = window.getGuiScaledWidth();
        Graphics2D graphics = image.createGraphics();

        float halfx = mc.getWindow().getGuiScaledWidth() / 2F;
        float halfy = mc.getWindow().getGuiScaledHeight() / 2F;
        int maxX = 0;
        int minX = 10000;
        int maxY = 0;
        int minY = 10000;
        //todo I don't understand why this is 1.4, tbh it should be 1.7, which is from PerkButton render scale(2 - screen.zoom).
        float singleButtonZoom = 1.4f;
        while (!waitingToBeUpdated.isEmpty()) {
            PainterController.paintLimiter.acquire();
            ButtonIdentifier identifier = waitingToBeUpdated.poll();
            Perk.PerkType type = identifier.perk().getType();
            ResourceLocation wholeTexture = identifier.getCurrentButtonLocation();


            BufferedImage singleButton = PerkButtonPainter.handledBufferedImage.get(wholeTexture);
            if (singleButton == null) {
                waitingToBeUpdated.add(identifier);
                System.out.println(identifier);
                System.out.println("add result: " + PerkButtonPainter.addToWait(identifier));
                System.out.println("sleep...");
                Thread.sleep(2000);
                continue;
            }

            int singleButtonSize = (int) (type.size * singleButtonZoom + 1);
            BufferedImage redesignSingleButton = new BufferedImage(singleButtonSize, singleButtonSize, BufferedImage.TYPE_INT_ARGB);
            Graphics2D redesignSingleButtonGraphics = redesignSingleButton.createGraphics();

            // set alpha
            PerkStatus status = Load.player(mc.player).talents.getStatus(mc.player, identifier.tree(), identifier.point());

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
            System.out.println("actually paint one!");
            updating.add(identifier);
        }
        graphics.dispose();
        //todo the 32 * singleButtonZoom thing is only suitable for normal stat perk icon, but why is 33? should be 24(normal stat perk icon size) actually but it just works.
        // not hurt to cut it more bigger tho.
        image = image.getSubimage(minX, minY, (int) (maxX - minX + 33 * singleButtonZoom), (int) (maxY - minY + 32 * singleButtonZoom));
        this.minX = minX;
        this.minY = minY;
        this.maxX = maxX;
        this.maxY = maxY;
        this.imageWidth = image.getWidth();
        this.imageHeight = image.getHeight();
        this.lastWholeImage = image;
        this.isPainting = false;
        System.out.println("paint done!");
        return new SeparableBufferedImage(image);

    }

    public void handlePaintQueue() {
        if (waitingToBeUpdated.isEmpty()) return;
        System.out.println("is not empty");
        if (!PainterController.isAllowedUpdate(this)) return;
        PainterController.passOnePaintAction(this);
        CompletableFuture.runAsync(() -> {
            SeparableBufferedImage image;
            try {
                if (this.isPainting) return;
                this.isPainting = true;
                image = tryPaint();
            } catch (InterruptedException | IOException e) {
                throw new RuntimeException(e);
            }
            System.out.println("add to register!");
            waitingToBeRegistered.add(image);
        });

    }

    public void handleRegisterQueue() {
        if (waitingToBeRegistered.isEmpty()) return;
        System.out.println("register all button!");
        SeparableBufferedImage image = null;
        while (!this.waitingToBeRegistered.isEmpty()) {
            image = waitingToBeRegistered.poll();
        }
        List<BufferedImage> separatedImage = image.getSeparatedImage();
        this.locations.clear();
        for (int i = 0; i < separatedImage.size(); i++) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            BufferedImage image2 = separatedImage.get(i);
            try {
                ImageIO.write(image2, "PNG", baos);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try (NativeImage image1 = NativeImage.read(baos.toByteArray())) {
                ResourceLocation location = new ResourceLocation(getThisLocation().getNamespace(), getThisLocation().getPath() + "_" + i);
                ExileTreeTexture exileTreeTexture = new ExileTreeTexture(location, image1);
                TextureManager textureManager = Minecraft.getInstance().getTextureManager();
                textureManager.release(location);
                textureManager.register(location, exileTreeTexture);
                this.locations.add(new ResourceLocationAndSize(location, image2.getWidth(), image2.getHeight()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
        this.updating.clear();
        this.isRepainting = false;
    }

    public void addToUpdate(ButtonIdentifier identifier) {
        //todo idk if this could be buggy
        this.waitingToBeUpdated.add(identifier);

    }

    public void init(Collection<ButtonIdentifier> identifiers) {
        if (!this.cache.isEmpty()) return;
        System.out.println("init all painter");
        identifiers.forEach(x -> {
            this.cache.put(x, x.getCurrentButtonLocation());
            this.waitingToBeUpdated.add(x);
        });
        PainterController.setThisAllowedUpdate(this);
    }

    public ResourceLocation getThisLocation() {
        return new ResourceLocation(PainterController.nameSpace, "" + typeHash);
    }

    public boolean isAllowedToPaint() {
        return !locations.isEmpty();
    }

    public boolean isThisButtonIsUpdating(PerkButton button){
        return this.waitingToBeUpdated.contains(button.buttonIdentifier) || this.updating.contains(button.buttonIdentifier);
    }

    public void checkIfNeedRepaint() {
        // due to window size change
        if (!this.isRepainting && this.drawInWindowWidth != 0 && Minecraft.getInstance().getWindow().getGuiScaledWidth() != this.drawInWindowWidth) {
            repaint();
        }
    }

    public void repaint() {
        System.out.println("repaint!");
        this.locations.clear();
        this.waitingToBeUpdated.clear();
        this.waitingToBeRegistered.clear();
        this.lastWholeImage = null;
        this.waitingToBeUpdated.addAll(this.cache.keySet());
        PainterController.setThisAllowedUpdate(this);
        this.isRepainting = true;
    }

    public record ResourceLocationAndSize(ResourceLocation location, int width, int height) {

    }

}
