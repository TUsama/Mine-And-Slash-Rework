package com.robertx22.mine_and_slash.database.data.stats.types.loot;

import com.robertx22.mine_and_slash.database.data.stats.Stat;
import com.robertx22.mine_and_slash.uncommon.enumclasses.Elements;
import net.minecraft.ChatFormatting;

public class TreasureQuantity extends Stat {

    private TreasureQuantity() {
        this.group = StatGroup.Misc;
        this.icon = "\u2663";
        this.format = ChatFormatting.YELLOW.getName();
    }

    public static TreasureQuantity getInstance() {
        return SingletonHolder.INSTANCE;
    }

    @Override
    public boolean IsPercent() {
        return true;
    }

    @Override
    public Elements getElement() {
        return null;
    }

    @Override
    public String locDescForLangFile() {
        return "Increases amount of loot found";
    }

    @Override
    public String GUID() {
        return "increased_quantity";
    }

    @Override
    public String locNameForLangFile() {
        return "Item Find";
    }

    private static class SingletonHolder {
        private static final TreasureQuantity INSTANCE = new TreasureQuantity();
    }
}
