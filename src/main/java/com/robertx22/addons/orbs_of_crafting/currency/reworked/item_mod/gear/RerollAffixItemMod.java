package com.robertx22.addons.orbs_of_crafting.currency.reworked.item_mod.gear;

import com.robertx22.addons.orbs_of_crafting.currency.reworked.item_mod.GearModification;
import com.robertx22.addons.orbs_of_crafting.currency.reworked.item_mod.ItemModificationSers;
import com.robertx22.library_of_exile.localization.ExileTranslation;
import com.robertx22.library_of_exile.localization.TranslationBuilder;
import com.robertx22.library_of_exile.localization.TranslationType;
import com.robertx22.mine_and_slash.database.registry.ExileDB;
import com.robertx22.mine_and_slash.itemstack.ExileStack;
import com.robertx22.mine_and_slash.itemstack.StackKeys;
import com.robertx22.mine_and_slash.mmorpg.SlashRef;
import com.robertx22.mine_and_slash.saveclasses.gearitem.gear_parts.AffixData;
import com.robertx22.mine_and_slash.uncommon.localization.Words;
import com.robertx22.orbs_of_crafting.register.mods.base.ItemModificationResult;
import net.minecraft.network.chat.MutableComponent;

public class RerollAffixItemMod extends GearModification {


    public Data data;

    public static record Data(UpgradeAffixItemMod.AffixFinderData finder_data, String result_rar) {
    }


    transient String rarname;

    public RerollAffixItemMod(String id, Data data, String rarname) {
        super(ItemModificationSers.REROLL_AFFIX, id);
        this.data = data;

        this.rarname = rarname;
    }

    @Override
    public void modifyGear(ExileStack stack, ItemModificationResult r) {
        stack.get(StackKeys.GEAR).edit(gear -> {
            var opt = data.finder_data.finder().getAffix(gear.affixes.getPrefixesAndSuffixes(), data.finder_data);

            if (opt.isPresent()) {
                AffixData affixData = opt.get();
                affixData.RerollFully(gear);

                if (ExileDB.GearRarities().isRegistered(data.result_rar)) {
                    affixData.rar = data.result_rar;
                    affixData.RerollNumbers();
                }
            }
        });

    }

    @Override
    public Class<?> getClassForSerialization() {
        return RerollAffixItemMod.class;
    }


    @Override
    public OutcomeType getOutcomeType() {
        return OutcomeType.GOOD;
    }

    @Override
    public MutableComponent getDescWithParams() {
        MutableComponent rar = Words.RANDOM_RARITY.locName();
        if (ExileDB.GearRarities().isRegistered(data.result_rar)) {
            rar = ExileDB.GearRarities().get(data.result_rar).coloredName();
        }
        rar.append(" ").append(Words.AFFIX.locName());

        return this.getTranslation(TranslationType.DESCRIPTION).getTranslatedName(data.finder_data.finder().getTooltip(data.finder_data), rar);
    }

    @Override
    public TranslationBuilder createTranslationBuilder() {
        return TranslationBuilder.of(SlashRef.MODID)
                .desc(ExileTranslation.registry(this, "Rerolls %1$s into a new %2$s"));
    }

}
