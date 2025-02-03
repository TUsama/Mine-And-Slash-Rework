package com.robertx22.mine_and_slash.mmorpg;

import net.minecraftforge.fml.ModList;

public class MnsAddons {
    public static boolean isObeliskLoaded() {
        return ModList.get().isLoaded("ancient_obelisks");
    }
}
