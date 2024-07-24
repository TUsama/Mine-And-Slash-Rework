package com.robertx22.age_of_exile.gui.screens.skill_tree;

import com.google.common.util.concurrent.RateLimiter;
import net.minecraft.resources.ResourceLocation;

import java.awt.image.BufferedImage;
import java.util.concurrent.TimeUnit;

public class PainterController {

    public final static String nameSpace = "etexture:";
    public static RateLimiter paintLimiter = RateLimiter.create(500.0,3, TimeUnit.SECONDS);

    public static RateLimiter registerLimiter = RateLimiter.create(80, 2, TimeUnit.SECONDS);

    public record BufferedImagePack(BufferedImage image, ResourceLocation resourceLocation) {
        @Override
        public BufferedImage image() {
            return image;
        }

        @Override
        public ResourceLocation resourceLocation() {
            return resourceLocation;
        }
    }
}
