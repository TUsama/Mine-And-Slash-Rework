package com.robertx22.age_of_exile.gui.screens.skill_tree.buttons.drawer;

import com.mojang.blaze3d.platform.Window;
import com.robertx22.age_of_exile.database.data.talent_tree.TalentTree;
import com.robertx22.age_of_exile.event_hooks.ontick.OnClientTick;
import com.robertx22.age_of_exile.gui.screens.skill_tree.PainterController;
import com.robertx22.age_of_exile.gui.screens.skill_tree.buttons.PerkButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;

public class AllPerkButtonPainter {

    private final ConcurrentLinkedQueue<BufferedImage> waitingToBeRegistered = new ConcurrentLinkedQueue<>();

    private final int typeHash;
    private final HashMap<ButtonIdentifier, ResourceLocation> cache = new HashMap<>(3000);
    private ResourceLocation lastWholeImage = TextureManager.INTENTIONAL_MISSING_TEXTURE;
    //todo is this really a good collection to store changed renderers? Slow in searching.
    //anyway we prob won't have that much data at the same time.
    private ConcurrentLinkedQueue<ButtonIdentifier> needPaint = new ConcurrentLinkedQueue<>();

    public AllPerkButtonPainter(TalentTree.SchoolType type) {
        this.typeHash = type.toString().hashCode();
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

    private BufferedImage tryPaint() throws InterruptedException {
        BufferedImage image;
        if (lastWholeImage == TextureManager.INTENTIONAL_MISSING_TEXTURE) {
            Window window = Minecraft.getInstance().getWindow();
            image = new BufferedImage(window.getGuiScaledWidth() * PerkButton.SPACING, window.getGuiScaledHeight() * PerkButton.SPACING, BufferedImage.TYPE_INT_ARGB);
        } else {
            try (InputStream inputStream = Minecraft.getInstance().getResourceManager().open(lastWholeImage)) {
                image = ImageIO.read(inputStream);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        Graphics2D graphics = image.createGraphics();
        while (!needPaint.isEmpty()) {
            System.out.println("currently the queue has " + needPaint.size() + " point data need to be handle.");
            System.out.println("try get one!");
            PainterController.paintLimiter.acquire();
            ButtonIdentifier identifier = needPaint.poll();
            ResourceLocation wholeTexture = identifier.getCurrentButtonLocation();
            BufferedImage singleButton = PerkButtonPainter.handledBufferedImage.get(wholeTexture);
            Minecraft mc = Minecraft.getInstance();
            float halfx = mc.getWindow().getGuiScaledWidth() / 2F;
            float halfy = mc.getWindow().getGuiScaledHeight() / 2F;

            float x = (identifier.point().x - identifier.tree().calcData.center.x) * PerkButton.SPACING;
            float y = (identifier.point().y - identifier.tree().calcData.center.y) * PerkButton.SPACING;

            // todo
            x -= identifier.perk().getType().size / 2F;
            y -= identifier.perk().getType().size / 2F;

            float tx = (int) (halfx + x);
            float ty = (int) (halfy + y);
            AffineTransform affineTransform = new AffineTransform();

            affineTransform.translate(tx, ty);
            graphics.drawImage(singleButton, affineTransform, null);
            System.out.println("actually paint one!");
        }
        graphics.dispose();
        System.out.println("done paint!");
        return image;

    }

    public void handlePaintQueue() {
        if (needPaint.isEmpty()) return;
        if (!PainterController.isAllowedUpdate(this)) return;
        PainterController.passOnePaintAction(this);
        CompletableFuture.runAsync(() -> {
            BufferedImage image;
            try {
                image = tryPaint();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            //waitingToBeRegistered.add(image);
            try {
                ImageIO.write(image, "PNG", new File("D:\\drawtest.png"));
                System.out.println("output!");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

    }

    public void handleRegisterQueue() {

    }

    public void addToUpdate(ButtonIdentifier identifier) {
        //todo idk if this could be buggy
        this.needPaint.add(identifier);

    }

    public void init(Collection<ButtonIdentifier> identifiers){
        if (!this.cache.isEmpty()) return;
        System.out.println("init all painter");
        identifiers.forEach(x -> {
            this.cache.put(x, x.getCurrentButtonLocation());
            this.needPaint.add(x);
        });
        PainterController.setThisAllowedUpdate(this);
    }

}
