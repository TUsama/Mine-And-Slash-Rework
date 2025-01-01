package com.robertx22.mine_and_slash.gui.screens.skill_tree;

import com.google.common.collect.HashMultimap;
import net.minecraft.resources.ResourceLocation;

public class VertexContainer {

    public HashMultimap<ResourceLocation, BufferInfo> map = HashMultimap.create(100, 500);

    public void refresh(){
        this.map = HashMultimap.create(100, 500);
    }

}
