package com.robertx22.mine_and_slash.aoe_data.database.chaos_stat;

import com.robertx22.library_of_exile.registry.ExileRegistryInit;
import com.robertx22.mine_and_slash.database.data.affixes.Affix;
import com.robertx22.mine_and_slash.database.data.chaos_stats.ChaosStat;
import com.robertx22.mine_and_slash.mmorpg.MMORPG;
import com.robertx22.mine_and_slash.uncommon.interfaces.data_items.IRarity;

import java.util.List;

public class ChaosStats implements ExileRegistryInit {

    @Override
    public void registerAll() {

        new ChaosStat("normal_low", "Upgraded", 5000, 1, Affix.AffixSlot.chaos_stat, 0, IRarity.NORMAL_GEAR_RARITIES).addToSerializables(MMORPG.SERIAZABLE_REGISTRATION_INFO);
        new ChaosStat("normal_med", "Elevated", 1000, 2, Affix.AffixSlot.chaos_stat, 0, IRarity.NORMAL_GEAR_RARITIES).addToSerializables(MMORPG.SERIAZABLE_REGISTRATION_INFO);
        new ChaosStat("normal_high", "Ascended", 500, 1, Affix.AffixSlot.chaos_stat, 1, IRarity.NORMAL_GEAR_RARITIES).addToSerializables(MMORPG.SERIAZABLE_REGISTRATION_INFO);

        new ChaosStat("unique_low", "Upgraded", 5000, 1, Affix.AffixSlot.chaos_stat, 0, List.of(IRarity.UNIQUE_ID, IRarity.RUNEWORD_ID)).addToSerializables(MMORPG.SERIAZABLE_REGISTRATION_INFO);
        new ChaosStat("unique_med", "Elevated", 1000, 2, Affix.AffixSlot.chaos_stat, 0, List.of(IRarity.UNIQUE_ID, IRarity.RUNEWORD_ID)).addToSerializables(MMORPG.SERIAZABLE_REGISTRATION_INFO);
        new ChaosStat("unique_high", "Ascended", 250, 1, Affix.AffixSlot.chaos_stat, 1, List.of(IRarity.UNIQUE_ID, IRarity.RUNEWORD_ID)).addToSerializables(MMORPG.SERIAZABLE_REGISTRATION_INFO);

    }
}
