package com.robertx22.age_of_exile.gui.screens.skill_tree.buttons.buttondrawer;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

public class PerkButtonDrawer {

    private int i = 0;


    public static NativeImage tryDrawWholeIcon(ResourceLocation color, ResourceLocation border, ResourceLocation perk, int width, int height) throws IOException {
        ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();

        try(InputStream colorStream = resourceManager.open(color)) {
            try (InputStream borderStream = resourceManager.open(border)){
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

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    ImageIO.write(finalImage, "PNG", baos);
                    return NativeImage.read(new ByteArrayInputStream(baos.toByteArray()));

                }

            }
        }
    }
    public static void tryDrawWholeIcon(ResourceLocation color, ResourceLocation border, ResourceLocation perk, int a) throws IOException {
        ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();

        try(InputStream colorStream = resourceManager.open(color)) {
            try (InputStream borderStream = resourceManager.open(border)){
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

}
