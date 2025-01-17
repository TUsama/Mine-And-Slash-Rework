package com.robertx22.addons.orbs_of_crafting.currency.base;

import com.robertx22.library_of_exile.registry.IWeighted;
import com.robertx22.mine_and_slash.uncommon.localization.Itemtips;
import com.robertx22.mine_and_slash.uncommon.localization.Words;
import com.robertx22.orbs_of_crafting.misc.LocReqContext;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public abstract class GearOutcome implements IWeighted {

    public abstract Words getName();

    public abstract OutcomeType getOutcomeType();

    public abstract void modify(LocReqContext ctx);


    public MutableComponent getTooltip(int totalweight) {
        int chance = (int) ((float) Weight() / (float) totalweight * 100F);
        return Itemtips.OUTCOME_TIP.locName(getName().locName(), Component.literal(chance + "%").withStyle(ChatFormatting.GREEN));
    }

}
