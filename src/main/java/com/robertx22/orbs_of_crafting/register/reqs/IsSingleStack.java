package com.robertx22.orbs_of_crafting.register.reqs;

import com.robertx22.addons.orbs_of_crafting.currency.reworked.item_req.ItemReqSers;
import com.robertx22.addons.orbs_of_crafting.currency.reworked.item_req.custom.MaximumUsesReq;
import com.robertx22.library_of_exile.localization.ExileTranslation;
import com.robertx22.library_of_exile.localization.TranslationBuilder;
import com.robertx22.library_of_exile.localization.TranslationType;
import com.robertx22.mine_and_slash.mmorpg.SlashRef;
import com.robertx22.orbs_of_crafting.main.StackHolder;
import com.robertx22.orbs_of_crafting.register.reqs.base.ItemRequirement;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;

public class IsSingleStack extends ItemRequirement {


    public MaximumUsesReq.Data data;


    public IsSingleStack(String id) {
        super(ItemReqSers.IS_SINGLE_ITEM, id);
    }

    @Override
    public Class<?> getClassForSerialization() {
        return IsSingleStack.class;
    }

    @Override
    public MutableComponent getDescWithParams() {
        return this.getTranslation(TranslationType.DESCRIPTION).getTranslatedName();
    }

    @Override
    public TranslationBuilder createTranslationBuilder() {
        return TranslationBuilder.of(SlashRef.MODID)
                .desc(ExileTranslation.registry(SlashRef.MODID, this, "Must be a Single Item")
                );
    }


    @Override
    public boolean isValid(Player p, StackHolder stack) {
        return stack.stack.getCount() == 1;
    }
}
