package com.robertx22.age_of_exile.gui.screens.skill_tree.buttons.buttondrawer;

import it.unimi.dsi.fastutil.ints.Int2ReferenceOpenHashMap;
import net.minecraft.resources.ResourceLocation;

public class PerkButtonTextureContainer {
    public static Int2ReferenceOpenHashMap<ResourceLocation> allTexture = new Int2ReferenceOpenHashMap<>(100);

    public static String nameSpace = "etexture";

    public static void register(ResourceLocation location){
        allTexture.put(location.hashCode(), location);
    }
}
