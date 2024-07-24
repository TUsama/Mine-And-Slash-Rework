package com.robertx22.age_of_exile.gui.screens.skill_tree.buttons.drawer;

import com.mojang.blaze3d.platform.NativeImage;
import com.robertx22.age_of_exile.database.data.perks.Perk;
import com.robertx22.age_of_exile.database.data.perks.PerkStatus;
import com.robertx22.age_of_exile.gui.screens.skill_tree.ExileTreeTexture;
import com.robertx22.age_of_exile.gui.screens.skill_tree.PainterController;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PerkButtonPainter {


    public final static HashSet<ResourceLocation> handledTextures = new HashSet<>(2000);

    public final static ConcurrentLinkedQueue<DrawInformation> waitingToBePaintedQueue = new ConcurrentLinkedQueue<>();
    public final static ConcurrentLinkedQueue<PainterController.BufferedImagePack> waitingToBeRegisteredQueue = new ConcurrentLinkedQueue<>();
    public static final IntOpenHashSet addHistory = new IntOpenHashSet(500);
    private final static ExecutorService executor = Executors.newFixedThreadPool(2);

    private static BufferedImage tryPaintWholeIcon(ResourceLocation color, ResourceLocation border, ResourceLocation perk, Perk.PerkType type) throws IOException {
        ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();

        try (InputStream colorStream = resourceManager.open(color)) {
            try (InputStream borderStream = resourceManager.open(border)) {
                try (InputStream perkStream = resourceManager.open(perk)) {
                    System.out.println("paint one button!");
                    BufferedImage co = ImageIO.read(colorStream);
                    BufferedImage bo = ImageIO.read(borderStream);
                    BufferedImage pe = ImageIO.read(perkStream);
                    int baseSize = type.size;
                    int offColor = (baseSize - 20) / 2;
                    int offPerk = (int) type.getOffset();

                    if (type.size - bo.getWidth() != 0D) {
                        int newWidth = type.size;
                        int newHeight = type.size;
                        BufferedImage scaledImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);

                        Graphics2D g2d = scaledImage.createGraphics();

                        g2d.setRenderingHint(java.awt.RenderingHints.KEY_INTERPOLATION,
                                java.awt.RenderingHints.VALUE_INTERPOLATION_BILINEAR);

                        g2d.drawImage(bo, 0, 0, newWidth, newHeight, null);

                        g2d.dispose();

                        bo = scaledImage;

                    }

                    if (type.iconSize - pe.getWidth() != 0D) {
                        int newWidth = type.iconSize;
                        int newHeight = type.iconSize;
                        BufferedImage scaledImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);

                        Graphics2D g2d = scaledImage.createGraphics();

                        g2d.setRenderingHint(java.awt.RenderingHints.KEY_INTERPOLATION,
                                java.awt.RenderingHints.VALUE_INTERPOLATION_BILINEAR);

                        g2d.drawImage(pe, 0, 0, newWidth, newHeight, null);

                        g2d.dispose();

                        pe = scaledImage;

                    }

                    BufferedImage finalImage = new BufferedImage(baseSize, baseSize, BufferedImage.TYPE_INT_ARGB);
                    Graphics2D g = finalImage.createGraphics();
                    g.drawImage(co, offColor, offColor, null);
                    g.drawImage(pe, offPerk, offPerk, null);
                    g.drawImage(bo, 0, 0, null);
                    g.dispose();


                    return finalImage;
                }

            }
        }
    }

    public static void tryPaintWholeIcon(ResourceLocation color, ResourceLocation border, ResourceLocation perk, int a) throws IOException {
        ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();

        try (InputStream colorStream = resourceManager.open(color)) {
            try (InputStream borderStream = resourceManager.open(border)) {
                try (InputStream perkStream = resourceManager.open(perk)) {
                    BufferedImage co = ImageIO.read(colorStream);
                    BufferedImage bo = ImageIO.read(borderStream);
                    BufferedImage pe = ImageIO.read(perkStream);

                    BufferedImage finalImage = new BufferedImage(bo.getWidth(), bo.getHeight(), BufferedImage.TYPE_INT_ARGB);
                    Graphics2D g = finalImage.createGraphics();
                    g.drawImage(co, 0, 0, null);
                    g.drawImage(pe, 0, 0, null);
                    g.drawImage(bo, 0, 0, null);
                    g.dispose();

                    ImageIO.write(finalImage, "PNG", new File("D:\\drawtest\\final" + a + ".png"));

                }

            }
        }
    }

    public static boolean addToWait(DrawInformation information) {
        //no equal button allow go into the queue
        int hash = information.hashCode();
        if (!addHistory.contains(hash)) {
            waitingToBePaintedQueue.add(information);
            addHistory.add(hash);
            return true;
        }
        return false;
    }

    public static void handlePaintQueue() {
        while (!waitingToBePaintedQueue.isEmpty()) {
            //though use acquire() directly is ok here, but I still don't wanna block any thread.
            PainterController.paintLimiter.tryAcquire(Duration.ofNanos(300));
            DrawInformation info = waitingToBePaintedQueue.poll();
            HashMap<PerkStatus, List<ResourceLocation>> allNewLocation = info.getAllNewLocation();
            allNewLocation.values().forEach((v) -> {
                try {
                    BufferedImage image = PerkButtonPainter.tryPaintWholeIcon(v.get(0), v.get(1), v.get(2), info.perk().type);
                    ResourceLocation newLocation = (new ResourceLocation(PainterController.nameSpace, v.get(0).getPath() + "_" + v.get(1).getPath() + "_" + v.get(2).getPath()));
                    waitingToBeRegisteredQueue.add(new PainterController.BufferedImagePack(image, newLocation));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }


    }

    public static void handleRegisterQueue() {

        while (!waitingToBeRegisteredQueue.isEmpty()){
            //can't use acquire() here, I run this on main thread.
            if (PainterController.registerLimiter.tryAcquire(Duration.ofNanos(300))){
                break;
            }
            PainterController.BufferedImagePack pack = waitingToBeRegisteredQueue.poll();
            ResourceLocation resourceLocation = pack.resourceLocation();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try {
                ImageIO.write(pack.image(), "PNG", baos);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try (NativeImage image = NativeImage.read(baos.toByteArray())) {
                ExileTreeTexture exileTreeTexture = new ExileTreeTexture(resourceLocation, image);
                Minecraft.getInstance().getTextureManager().register(resourceLocation, exileTreeTexture);
                handledTextures.add(resourceLocation);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }


    }

}
