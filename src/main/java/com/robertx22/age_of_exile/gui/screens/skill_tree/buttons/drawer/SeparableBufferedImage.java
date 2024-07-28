package com.robertx22.age_of_exile.gui.screens.skill_tree.buttons.drawer;

import com.google.common.collect.ImmutableList;
import net.minecraft.resources.ResourceLocation;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class SeparableBufferedImage {

    private final BufferedImage originalImage;
    private final byte[] bytes;
    private final int width;
    private final int height;

    private final int separateTo;

    public SeparableBufferedImage(BufferedImage originalImage) throws IOException {
        this.originalImage = originalImage;
        this.width = originalImage.getWidth();
        this.height = originalImage.getHeight();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(originalImage, "PNG", byteArrayOutputStream);
        this.bytes = byteArrayOutputStream.toByteArray();
        byteArrayOutputStream.close();
        byteArrayOutputStream = null;
        System.out.println("length is " + this.bytes.length);
        this.separateTo = this.bytes.length / (25 * 1024);
        System.out.println("separate to " + separateTo);
    }

    public List<BufferedImage> getSeparatedImage() {
        if (this.separateTo == 0) return Collections.singletonList(this.originalImage);
        ImmutableList.Builder<BufferedImage> builder = ImmutableList.builder();
        int handleWidth;
        int leftPart = 0;
        int singleWidth = 0;
        if (width % separateTo != 0) {
            handleWidth = width - (width % separateTo);
            leftPart = width % separateTo;
        } else {
            handleWidth = width;
        }
        singleWidth = handleWidth / separateTo;
        int a = 0;
        while (a < separateTo) {
            builder.add(originalImage.getSubimage(a * singleWidth, 0, singleWidth, height));
            a++;
        }
        if (leftPart != 0){
            builder.add(originalImage.getSubimage(width - handleWidth, 0, leftPart, height));
        }

        return builder.build();
    }

}
