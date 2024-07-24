package com.robertx22.age_of_exile.gui.screens.skill_tree.connections;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.platform.Window;
import com.robertx22.age_of_exile.database.data.perks.Perk;
import com.robertx22.age_of_exile.gui.screens.skill_tree.ExileTreeTexture;
import com.robertx22.age_of_exile.gui.screens.skill_tree.PainterController;
import com.robertx22.age_of_exile.gui.screens.skill_tree.SkillTreeScreen;
import com.robertx22.age_of_exile.gui.screens.skill_tree.buttons.PerkButton;
import com.robertx22.age_of_exile.mmorpg.SlashRef;
import com.robertx22.age_of_exile.saveclasses.PointData;
import it.unimi.dsi.fastutil.ints.Int2ReferenceOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;

public class PerkConnectionPainter {

    private static final Int2ReferenceOpenHashMap<ResourceLocation> wholeImage = new Int2ReferenceOpenHashMap<>(4);
    private static final Int2ReferenceOpenHashMap<HashMap<PointData, PerkButton>> pointPerkButtonMap = new Int2ReferenceOpenHashMap<>(4);
    private static final Int2ReferenceOpenHashMap<ConcurrentLinkedQueue<BufferedImage>> waitingToBeRegistered = new Int2ReferenceOpenHashMap<>(4);
    private static final Int2ReferenceOpenHashMap<ConcurrentLinkedQueue<ResourceLocation>> waitingToBeReplaced = new Int2ReferenceOpenHashMap<>(4);
    //todo is this really a good collection to store changed renderers? Slow in searching.
    //anyway we prob won't have that much data at the same time.
    public static Int2ReferenceOpenHashMap<ConcurrentLinkedQueue<PerkConnectionRenderer>> updates = new Int2ReferenceOpenHashMap<>(4);
    public static Int2ReferenceOpenHashMap<HashSet<PerkConnectionRenderer>> updating = new Int2ReferenceOpenHashMap<>(4);
    private static BufferedImage connection = null;

    public static BufferedImage tryPaint(int typeHash) {
        BufferedImage image;
        ResourceLocation location = wholeImage.getOrDefault(typeHash, null);
        if (location == null) {
            Window window = Minecraft.getInstance().getWindow();
            image = new BufferedImage(window.getWidth(), window.getHeight(), BufferedImage.TYPE_INT_ARGB);
        } else {
            try (InputStream inputStream = Minecraft.getInstance().getResourceManager().open(location)) {
                image = ImageIO.read(inputStream);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        Graphics2D graphics = image.createGraphics();
        updates.forEach((k, v) -> {
            while (!v.isEmpty()) {
                //this need to be blocked.
                PainterController.paintLimiter.acquire();
                System.out.println("paint one connection!");
                PerkConnectionRenderer renderer = v.poll();
                int i = k;
                if (!updating.containsKey(i)) {
                    updating.put(i, new HashSet<>(4000));
                }
                updating.get(i).add(renderer);
                PerkButton button1 = pointPerkButtonMap.get(i).get(renderer.pair.perk1);
                PerkButton button2 = pointPerkButtonMap.get(i).get(renderer.pair.perk2);
                Perk.Connection connection1 = renderer.connection;

                double xadd = button1.perk.getType().size / 2F;
                double yadd = button1.perk.getType().size / 2F;


                double connectionX = button1.getX() + xadd;
                double connectionY = button1.getY() + yadd;

                AffineTransform affineTransform = new AffineTransform();

                affineTransform.translate(connectionX, connectionY);

                float x1 = button1.getX() + button1.getWidth() / 2F;
                float y1 = button1.getY() + button1.getHeight() / 2F;
                float x2 = button2.getX() + button2.getWidth() / 2F;
                float y2 = button2.getY() + button2.getHeight() / 2F;

                double rotation = Mth.atan2(y2 - y1, x2 - x1);
                affineTransform.rotate(rotation);

                float distance = Mth.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));

                if (connection == null) throw new RuntimeException("try to paint connections without preparation!");
                BufferedImage finalConnectionTexture = null;
                System.out.println("width is " + distance);
                if (distance > connection.getWidth()){
                    finalConnectionTexture = new BufferedImage((int) distance, connection.getHeight(), BufferedImage.TYPE_INT_ARGB);
                    BufferedImage connectionTexture = null;
                    switch (connection1) {
                        case LINKED -> connectionTexture = connection.getSubimage(0, 0, 100, 6);
                        case BLOCKED -> connectionTexture = connection.getSubimage(0, 6 + 5, 100, 6);
                        case POSSIBLE -> connectionTexture = connection.getSubimage(0, 6, 100, 6);
                    }
                    Graphics2D graphics1 = finalConnectionTexture.createGraphics();
                    graphics1.setRenderingHint(java.awt.RenderingHints.KEY_INTERPOLATION,
                            java.awt.RenderingHints.VALUE_INTERPOLATION_BILINEAR);

                    graphics1.drawImage(connectionTexture, 0, 0, (int) distance, connection.getHeight(), null);
                    graphics1.dispose();

                } else {
                    switch (connection1) {
                        case LINKED -> finalConnectionTexture = connection.getSubimage(0, 0, (int) distance, 6);
                        case BLOCKED -> finalConnectionTexture = connection.getSubimage(0, 6 + 5, (int) distance, 6);
                        case POSSIBLE -> finalConnectionTexture = connection.getSubimage(0, 6, (int) distance, 6);
                    }
                }
                graphics.drawImage(finalConnectionTexture, affineTransform, null);

            }

        });
        graphics.dispose();

        return image;

    }

    public static void handleUpdateQueue() {
        CompletableFuture.runAsync(() -> {
            updates.forEach((k, v) -> {
                if (v.isEmpty()) return;
                int i = k;
                BufferedImage image = tryPaint(i);
                if (!waitingToBeRegistered.containsKey(i)) {
                    waitingToBeRegistered.put(i, new ConcurrentLinkedQueue<>());
                }
                waitingToBeRegistered.get(i).add(image);
            });

        });

    }

    public static void handleRegisterQueue() {
        waitingToBeRegistered.forEach((k, v) -> {
            if (v.isEmpty()) return;
            System.out.println("register one!");
            int i = k;
            BufferedImage poll = v.poll();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try {
                ImageIO.write(poll, "PNG", baos);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try (NativeImage image = NativeImage.read(baos.toByteArray())) {
                ResourceLocation location = new ResourceLocation(PainterController.nameSpace, "" + k);
                ExileTreeTexture exileTreeTexture = new ExileTreeTexture(location, image);

                TextureManager textureManager = Minecraft.getInstance().getTextureManager();
                textureManager.release(location);
                textureManager.register(location, exileTreeTexture);
                wholeImage.put(i, location);

                updating.get(i).clear();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        });

    }

    public static void addToUpdate(int typeHash, PerkConnectionRenderer renderer) {
        if (!updates.containsKey(typeHash)){
            updates.put(typeHash, new ConcurrentLinkedQueue<>());
        }
        updates.get(typeHash).add(renderer);
    }

    //copy the pointPerkButtonMap in skillScreen to another place for avoiding NPE
    //also read the connection texture
    private static void prepare(int typeHash, HashMap<PointData, PerkButton> map) {
        if (!pointPerkButtonMap.containsKey(typeHash)) pointPerkButtonMap.put(typeHash, map);

        try (InputStream image = Minecraft.getInstance().getResourceManager().open(SlashRef.id("textures/gui/skill_tree/skill_connection.png"))) {

            connection = ImageIO.read(image);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public static void init(SkillTreeScreen screen) {
        int typeHash = screen.schoolType.toString().hashCode();

        prepare(typeHash, new HashMap<>(screen.pointPerkButtonMap));
        if (PerkConnectionCache.renderersCache.isEmpty()) throw new RuntimeException("init ConnectionPainter before ConnectionCache!");
        PerkConnectionCache.renderersCache.forEach((k,v) -> {
            v.forEach((k2, v2) -> {

                addToUpdate(k, v2);
            });
        });
        handleUpdateQueue();
    }

    public static ResourceLocation getCurrentScreenTextureLocation(SkillTreeScreen screen) {
        int typeHash = screen.schoolType.toString().hashCode();
        return new ResourceLocation(PainterController.nameSpace, "" + typeHash);
    }

    public static boolean checkInUpdate(SkillTreeScreen screen, PerkConnectionRenderer renderer){
        int typeHash = screen.schoolType.toString().hashCode();
        ConcurrentLinkedQueue<PerkConnectionRenderer> perkConnectionRenderers = PerkConnectionPainter.updates.get(typeHash);
        HashSet<PerkConnectionRenderer> perkConnectionRenderers1 = PerkConnectionPainter.updating.get(typeHash);
        return perkConnectionRenderers.contains(renderer) || perkConnectionRenderers1.contains(renderer);
    }

    public static boolean checkIfRegistered(SkillTreeScreen screen){
        int typeHash = screen.schoolType.toString().hashCode();
        return wholeImage.containsKey(typeHash);
    }
}
