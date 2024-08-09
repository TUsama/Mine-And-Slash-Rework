package com.robertx22.age_of_exile.gui.screens.skill_tree.buttons.painter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.robertx22.age_of_exile.gui.screens.skill_tree.PainterController;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.lang.reflect.Type;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.robertx22.age_of_exile.gui.screens.skill_tree.buttons.painter.PerkButtonPainter.handledBufferedImage;
import static com.robertx22.age_of_exile.gui.screens.skill_tree.buttons.painter.PerkButtonPainter.waitingToBeRegisteredQueue;

public class PaintingTransformer {

    public static final ConcurrentHashMap<String, String> map = new ConcurrentHashMap<>(10000);
    private static int cacheSize = 0;

    public static final File cache = new File(Minecraft.getInstance().gameDirectory, "mas_talent_tree_cache.json");

    public static void outputPainting() throws IOException {
        Gson gson = new Gson();
        String json = gson.toJson(map);
        if (!cache.exists()) {
            cache.createNewFile();
            try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(cache))) {
                bufferedWriter.write(json);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        } else {
            if (cacheSize < map.size()) {
                try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(cache))) {
                    bufferedWriter.write(json);
                }
            }
        }
    }

    public static void readPainting() throws FileNotFoundException {
        Gson gson = new Gson();
        if (!isCacheExists()) return;
        JsonReader reader = new JsonReader(new FileReader(cache));
        Map<String, String> jsonMap = gson.fromJson(reader, new TypeToken<Map<String, String>>() {
        }.getType());
        cacheSize = jsonMap.size();
        ConcurrentHashMap.KeySetView<PainterController.BufferedImagePack, Boolean> bufferedImagePacks = ConcurrentHashMap.newKeySet();
        jsonMap.entrySet().parallelStream().forEach(x -> {

            ResourceLocation location = new ResourceLocation(x.getKey());
            byte[] decode = Base64.getDecoder().decode(x.getValue());
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(decode);
            try {
                BufferedImage read = ImageIO.read(byteArrayInputStream);
                handledBufferedImage.put(location, read);
                bufferedImagePacks.add(new PainterController.BufferedImagePack(read, location));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        waitingToBeRegisteredQueue.addAll(bufferedImagePacks);

    }

    public static void readCacheOnLogIn(){
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, ()->()->{
            if (isCacheExists()){
                try {
                    readPainting();
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    public static void exportCacheOnLogOut(){
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, ()->()->{
            try {
                outputPainting();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public static boolean isCacheExists(){
        return cache.exists();
    }


}
