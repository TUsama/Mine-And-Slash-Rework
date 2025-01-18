package com.robertx22.mine_and_slash.aoe_data.database.gear_rarities;

import com.robertx22.library_of_exile.registry.ExileRegistryEvent;
import com.robertx22.library_of_exile.registry.ExileRegistryEventClass;
import com.robertx22.library_of_exile.registry.ExileRegistryType;
import com.robertx22.mine_and_slash.database.data.MinMax;
import com.robertx22.mine_and_slash.database.data.omen.OmenDifficulty;
import com.robertx22.mine_and_slash.database.data.rarities.GearRarity;
import com.robertx22.mine_and_slash.database.data.rarities.GearRarityType;
import com.robertx22.mine_and_slash.database.data.rarities.MapRarityRewardData;
import com.robertx22.mine_and_slash.database.registry.ExileRegistryTypes;
import com.robertx22.mine_and_slash.maps.processors.reward.ModLootTables;
import com.robertx22.mine_and_slash.mmorpg.MMORPG;
import com.robertx22.mine_and_slash.uncommon.interfaces.data_items.IRarity;

// check if loot drops in maps
public class GearRaritiesAdder extends ExileRegistryEventClass {

    @Override
    public ExileRegistryType getType() {
        return ExileRegistryTypes.GEAR_TYPE;
    }

    @Override
    public void init(ExileRegistryEvent event) {


        GearRarity common = new GearRarity().edit(x -> {

            x.omens = new OmenDifficulty(new MinMax(1, 1), new MinMax(1, 1), new MinMax(1, 1), new MinMax(0, 0), new MinMax(1, 1), 0.3f);
            x.map_resist_req = 0;
            x.map_tiers = new MinMax(0, 10);
            x.favor_per_hour = 60;
            x.favor_loot_multi = 1;
            x.map_xp_multi = 0.75F;
            x.favor_needed = 0;
            x.min_affixes = 1;
            x.weight = 5000;
            x.item_tier_power = 1;
            x.item_tier = 0;

            x.pot = new GearRarity.Potential(5);
            x.item_value_multi = 1;
            x.item_model_data_num = 1;
            x.sockets = new MinMax(0, 0);
            x.higher_rar = IRarity.UNCOMMON;
            x.stat_percents = new MinMax(0, 10);
            x.setCommonFields();
            x.addToSerializables(MMORPG.SERIAZABLE_REGISTRATION_INFO);

            x.map_reward = new MapRarityRewardData(0, ModLootTables.TIER_1_DUNGEON_CHEST, 1, 1);
        });

        GearRarity uncommon = new GearRarity().edit(x -> {

            x.omens = new OmenDifficulty(new MinMax(1, 1), new MinMax(1, 1), new MinMax(1, 1), new MinMax(0, 0), new MinMax(1, 1), 0.4F);

            x.map_reward = new MapRarityRewardData(50, ModLootTables.TIER_1_DUNGEON_CHEST, 2, 1.1F);


            x.map_resist_req = 10;

            x.map_tiers = new MinMax(10, 20);
            x.map_xp_multi = 0.9F;
            x.favor_per_hour = 60;
            x.favor_loot_multi = 1.05f;
            x.favor_needed = 50;
            x.sockets = new MinMax(0, 1);
            x.pot = new GearRarity.Potential(10);
            x.min_affixes = 2;
            x.weight = 2000;
            x.item_tier = 1;
            x.item_model_data_num = 2;
            x.item_tier_power = 1.25F;
            x.item_value_multi = 1.25F;
            x.higher_rar = IRarity.RARE_ID;
            x.stat_percents = new MinMax(10, 20);
            x.setUncommonFields();
            x.addToSerializables(MMORPG.SERIAZABLE_REGISTRATION_INFO);

        });

        GearRarity rar = new GearRarity().edit(x -> {
            x.map_reward = new MapRarityRewardData(60, ModLootTables.TIER_2_DUNGEON_CHEST, 3, 1.2F);

            x.omens = new OmenDifficulty(new MinMax(1, 2), new MinMax(1, 2), new MinMax(1, 2), new MinMax(1, 1), new MinMax(1, 2), 0.6F);

            x.map_resist_req = 20;

            x.map_tiers = new MinMax(20, 40);

            x.affix_rarity_weight = 750;
            x.map_xp_multi = 1;

            x.favor_loot_multi = 1.1F;
            x.favor_needed = 100;
            x.sockets = new MinMax(0, 1);
            x.min_lvl = 10;
            x.lootable_gear_tier = GearRarity.LootableGearTier.MID;
            x.pot = new GearRarity.Potential(25);
            x.item_tier = 2;
            x.item_model_data_num = 3;
            x.min_affixes = 3;
            x.weight = 500;
            x.item_tier_power = 1.5F;
            x.item_value_multi = 1.5F;
            x.higher_rar = IRarity.EPIC_ID;
            x.stat_percents = new MinMax(20, 40);
            x.setRareFields();

        });
        GearRarity epic = new GearRarity().edit(x -> {
            x.map_reward = new MapRarityRewardData(70, ModLootTables.TIER_3_DUNGEON_CHEST, 5, 1.2F);

            x.omens = new OmenDifficulty(new MinMax(1, 2), new MinMax(1, 2), new MinMax(1, 2), new MinMax(1, 1), new MinMax(1, 2), 1);

            x.map_resist_req = 30;

            x.map_tiers = new MinMax(40, 60);

            x.affix_rarity_weight = 500;
            x.map_xp_multi = 1.05F;

            x.favor_loot_multi = 1.15F;
            x.favor_needed = 250;
            x.sockets = new MinMax(0, 2);
            x.min_lvl = 25;
            x.lootable_gear_tier = GearRarity.LootableGearTier.MID;
            x.pot = new GearRarity.Potential(50);
            x.min_affixes = 4;
            x.weight = 100;
            x.map_lives = 4;
            x.item_tier = 3;
            x.item_model_data_num = 4;
            x.item_tier_power = 1.7F;
            x.item_value_multi = 1.7F;
            x.higher_rar = IRarity.LEGENDARY_ID;
            x.stat_percents = new MinMax(40, 60);
            x.setEpicFields();
        });

        GearRarity legendary = new GearRarity().edit(x -> {
            x.drops_uber_frags = true;

            x.omens = new OmenDifficulty(new MinMax(1, 3), new MinMax(1, 2), new MinMax(1, 2), new MinMax(2, 3), new MinMax(2, 3), 1.25F);

            x.map_reward = new MapRarityRewardData(80, ModLootTables.TIER_4_DUNGEON_CHEST, 7, 1.5F);

            x.map_resist_req = 40;

            x.map_tiers = new MinMax(60, 80);

            x.affix_rarity_weight = 200;
            x.map_xp_multi = 1.1F;

            x.favor_loot_multi = 1.2F;
            x.favor_needed = 500;
            x.sockets = new MinMax(1, 2);
            x.min_lvl = 40;
            x.min_map_rarity_to_drop = IRarity.COMMON_ID;
            x.lootable_gear_tier = GearRarity.LootableGearTier.HIGH;
            x.pot = new GearRarity.Potential(75);
            x.min_affixes = 5;
            x.weight = 50;
            x.map_lives = 3;
            x.item_tier = 4;
            x.item_model_data_num = 5;
            x.item_tier_power = 2;
            x.item_value_multi = 2;
            x.announce_in_chat = true;
            x.higher_rar = IRarity.MYTHIC_ID;
            x.stat_percents = new MinMax(60, 80);
            x.setLegendFields();
        });

        GearRarity mythic = new GearRarity().edit(x -> {

            x.drops_uber_frags = true;

            x.omens = new OmenDifficulty(new MinMax(1, 3), new MinMax(1, 2), new MinMax(1, 2), new MinMax(2, 3), new MinMax(2, 3), 1.5F);

            x.map_reward = new MapRarityRewardData(90, ModLootTables.TIER_5_DUNGEON_CHEST, 10, 2);


            x.map_resist_req = 50;

            x.map_tiers = new MinMax(80, 100);

            x.affix_rarity_weight = 100;

            x.map_xp_multi = 1.25F;

            x.favor_loot_multi = 1.25F;
            x.favor_needed = 1000;
            x.sockets = new MinMax(2, 2);
            x.min_lvl = 50;
            x.min_map_rarity_to_drop = IRarity.EPIC_ID;
            x.lootable_gear_tier = GearRarity.LootableGearTier.HIGH;
            x.pot = new GearRarity.Potential(100);
            x.min_affixes = 6;
            x.weight = 25;
            x.map_lives = 3;
            x.item_tier = 5;
            x.item_model_data_num = 6;
            x.item_tier_power = 3;
            x.item_value_multi = 3;
            x.announce_in_chat = true;
            x.stat_percents = new MinMax(80, 100);
            x.setMythicFields();
        });


        GearRarity unique = new GearRarity().edit(x -> {
            x.omens = new OmenDifficulty(new MinMax(1, 3), new MinMax(1, 2), new MinMax(1, 2), new MinMax(2, 3), new MinMax(2, 3), 1);

            x.map_resist_req = 50;
            x.type = GearRarityType.UNIQUE;
            x.sockets = new MinMax(1, 2);
            x.stat_percents = new MinMax(50, 100);
            x.pot = new GearRarity.Potential(50);
            x.lootable_gear_tier = GearRarity.LootableGearTier.HIGH;
            x.min_affixes = 0;
            x.weight = 25;
            x.item_tier_power = 2;
            x.item_value_multi = 2;
            x.item_tier = 5;
            x.setUniqueFields();
            x.announce_in_chat = true;
            x.is_unique_item = true;
        });

        // todo need to make separate maprarity etc or else this will roll somehow
        GearRarity runeword = new GearRarity().edit(x -> {
            x.omens = new OmenDifficulty(new MinMax(1, 3), new MinMax(1, 2), new MinMax(1, 2), new MinMax(2, 3), new MinMax(2, 3), 1);

            x.max_runes = 10;
            x.max_gems = 0;

            x.map_resist_req = 50;
            x.type = GearRarityType.RUNED;
            x.min_lvl = 15;
            x.sockets = new MinMax(2, 6);
            x.lootable_gear_tier = GearRarity.LootableGearTier.HIGH;
            x.pot = new GearRarity.Potential(30);
            x.min_affixes = 0;
            x.weight = 150;
            x.item_tier_power = 2;
            x.item_value_multi = 2;
            x.item_tier = 10;
            x.setRunewordFields();
            x.announce_in_chat = false;
            x.can_have_runewords = true;
            x.is_unique_item = false;
        });

        var info = MMORPG.SERIAZABLE_REGISTRATION_INFO;

        event.addSeriazable(common, info);
        event.addSeriazable(uncommon, info);
        event.addSeriazable(rar, info);
        event.addSeriazable(epic, info);
        event.addSeriazable(legendary, info);
        event.addSeriazable(mythic, info);
        event.addSeriazable(runeword, info);
        event.addSeriazable(unique, info);
    }
}
