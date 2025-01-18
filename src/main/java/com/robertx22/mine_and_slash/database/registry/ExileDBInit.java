package com.robertx22.mine_and_slash.database.registry;

import com.robertx22.library_of_exile.registry.Database;
import com.robertx22.library_of_exile.registry.ExileRegistryContainer;
import com.robertx22.mine_and_slash.aoe_data.database.base_gear_types.BaseGearTypes;
import com.robertx22.mine_and_slash.aoe_data.database.base_stats.BaseStatsAdder;
import com.robertx22.mine_and_slash.aoe_data.database.boss_spell.SummonExplodyMobs;
import com.robertx22.mine_and_slash.aoe_data.database.gear_slots.GearSlots;
import com.robertx22.mine_and_slash.aoe_data.database.mob_affixes.MobAffixes;
import com.robertx22.mine_and_slash.aoe_data.database.perks.Perks;
import com.robertx22.mine_and_slash.aoe_data.database.runewords.Runewords;
import com.robertx22.mine_and_slash.aoe_data.database.spell_schools.SpellSchoolsAdder;
import com.robertx22.mine_and_slash.aoe_data.database.spells.impl.IntSpells;
import com.robertx22.mine_and_slash.aoe_data.database.unique_gears.UniqueGearReg;
import com.robertx22.mine_and_slash.database.data.aura.AuraGems;
import com.robertx22.mine_and_slash.database.data.game_balance_config.GameBalanceConfig;
import com.robertx22.mine_and_slash.database.data.league.HarvestLeague;
import com.robertx22.mine_and_slash.database.data.loot_chest.GearLootChest;
import com.robertx22.mine_and_slash.database.data.map_affix.MapAffixes;
import com.robertx22.mine_and_slash.database.data.support_gem.SupportGems;
import com.robertx22.mine_and_slash.database.empty_entries.EmptyStat;
import com.robertx22.mine_and_slash.uncommon.enumclasses.WeaponTypes;
import com.robertx22.mine_and_slash.uncommon.interfaces.data_items.IRarity;
import com.robertx22.mine_and_slash.vanilla_mc.items.gemrunes.GemItem;
import com.robertx22.mine_and_slash.vanilla_mc.items.gemrunes.RuneType;

public class ExileDBInit {


    public static String UNKNOWN_ID = "unknown";

    public static void initRegistries() {
        // data pack ones


        // todo make sure all have valid empties!!!

        Database.addRegistry(new ExileRegistryContainer<>(ExileRegistryTypes.GEAR_RARITY, IRarity.COMMON_ID).setIsDatapack());
        Database.addRegistry(new ExileRegistryContainer<>(ExileRegistryTypes.WEAPON_TYPE, WeaponTypes.sword.GUID()).setIsDatapack());
        Database.addRegistry(new ExileRegistryContainer<>(ExileRegistryTypes.GEAR_SLOT, GearSlots.SWORD).setIsDatapack());
        Database.addRegistry(new ExileRegistryContainer<>(ExileRegistryTypes.GEAR_TYPE, BaseGearTypes.SWORD.GUID()).setIsDatapack());
        Database.addRegistry(new ExileRegistryContainer<>(ExileRegistryTypes.RUNEWORDS, Runewords.EMPTY).setIsDatapack());
        Database.addRegistry(new ExileRegistryContainer<>(ExileRegistryTypes.EXILE_EFFECT, "").setIsDatapack());
        Database.addRegistry(new ExileRegistryContainer<>(ExileRegistryTypes.AFFIX, ExileDBInit.UNKNOWN_ID).setIsDatapack())
        ;
        Database.addRegistry(new ExileRegistryContainer<>(ExileRegistryTypes.MOB_AFFIX, MobAffixes.FULL_COLD).setIsDatapack());
        Database.addRegistry(new ExileRegistryContainer<>(ExileRegistryTypes.UNIQUE_GEAR, UniqueGearReg.EMPTY).setIsDatapack());
        Database.addRegistry(new ExileRegistryContainer<>(ExileRegistryTypes.GEM, GemItem.GemType.RUBY.GUID()).setIsDatapack());
        Database.addRegistry(new ExileRegistryContainer<>(ExileRegistryTypes.RUNE, RuneType.ANO.id).setIsDatapack());
        Database.addRegistry(new ExileRegistryContainer<>(ExileRegistryTypes.SPELL, IntSpells.BLACK_HOLE).setIsDatapack());
        Database.addRegistry(new ExileRegistryContainer<>(ExileRegistryTypes.PERK, Perks.UNKNOWN_ID).setIsDatapack());
        Database.addRegistry(new ExileRegistryContainer<>(ExileRegistryTypes.TALENT_TREE, UNKNOWN_ID).setIsDatapack());
        Database.addRegistry(new ExileRegistryContainer<>(ExileRegistryTypes.SPELL_SCHOOL, SpellSchoolsAdder.SORCERER).setIsDatapack());
        Database.addRegistry(new ExileRegistryContainer<>(ExileRegistryTypes.BASE_STATS, BaseStatsAdder.EMPTY).setIsDatapack());
        Database.addRegistry(new ExileRegistryContainer<>(ExileRegistryTypes.GAME_BALANCE, GameBalanceConfig.BalanceEnum.ORIGINAL_BALANCE.id).setIsDatapack());
        Database.addRegistry(new ExileRegistryContainer<>(ExileRegistryTypes.VALUE_CALC, UNKNOWN_ID).setIsDatapack());
        Database.addRegistry(new ExileRegistryContainer<>(ExileRegistryTypes.STAT_EFFECT, "").setIsDatapack());
        Database.addRegistry(new ExileRegistryContainer<>(ExileRegistryTypes.STAT_CONDITION, "").setIsDatapack());
        Database.addRegistry(new ExileRegistryContainer<>(ExileRegistryTypes.DIMENSION_CONFIGS, "").logAdditions().setIsDatapack().dontErrorMissingEntriesOnAccess());
        Database.addRegistry(new ExileRegistryContainer<>(ExileRegistryTypes.ENTITY_CONFIGS, "").logAdditions()
                .setIsDatapack());

        Database.addRegistry(new ExileRegistryContainer<>(ExileRegistryTypes.STAT, EmptyStat.getInstance().GUID()));
        Database.addRegistry(new ExileRegistryContainer<>(ExileRegistryTypes.AILMENT, ""));
        Database.addRegistry(new ExileRegistryContainer<>(ExileRegistryTypes.SUPPORT_GEM, SupportGems.PROJ_COUNT));
        Database.addRegistry(new ExileRegistryContainer<>(ExileRegistryTypes.AURA, AuraGems.health_reg.id));
        Database.addRegistry(new ExileRegistryContainer<>(ExileRegistryTypes.MAP_AFFIX, MapAffixes.crit));
        Database.addRegistry(new ExileRegistryContainer<>(ExileRegistryTypes.BOSS_SPELL, new SummonExplodyMobs().GUID()));
        Database.addRegistry(new ExileRegistryContainer<>(ExileRegistryTypes.LEAGUE_MECHANIC, new HarvestLeague().GUID()));
        Database.addRegistry(new ExileRegistryContainer<>(ExileRegistryTypes.LOOT_CHEST, new GearLootChest().GUID()));
        Database.addRegistry(new ExileRegistryContainer<>(ExileRegistryTypes.DUNGEON, ""));
        Database.addRegistry(new ExileRegistryContainer<>(ExileRegistryTypes.RECIPE, ""));
        Database.addRegistry(new ExileRegistryContainer<>(ExileRegistryTypes.PROFESSION, ""));
        Database.addRegistry(new ExileRegistryContainer<>(ExileRegistryTypes.STAT_BUFF, ""));
        Database.addRegistry(new ExileRegistryContainer<>(ExileRegistryTypes.AUTO_ITEM, ""));
        Database.addRegistry(new ExileRegistryContainer<>(ExileRegistryTypes.CUSTOM_ITEM, ""));
        Database.addRegistry(new ExileRegistryContainer<>(ExileRegistryTypes.MOB_RARITY, IRarity.COMMON_ID));
        Database.addRegistry(new ExileRegistryContainer<>(ExileRegistryTypes.PROPHECY_MODIFIER, ""));
        Database.addRegistry(new ExileRegistryContainer<>(ExileRegistryTypes.PROPHECY_START, ""));
        Database.addRegistry(new ExileRegistryContainer<>(ExileRegistryTypes.CHAOS_STAT, ""));
        Database.addRegistry(new ExileRegistryContainer<>(ExileRegistryTypes.STAT_COMPAT, ""));
        Database.addRegistry(new ExileRegistryContainer<>(ExileRegistryTypes.STAT_LAYER, ""));
        Database.addRegistry(new ExileRegistryContainer<>(ExileRegistryTypes.UBER_BOSS, ""));
        Database.addRegistry(new ExileRegistryContainer<>(ExileRegistryTypes.SPAWNED_MOBS, UNKNOWN_ID));
        Database.addRegistry(new ExileRegistryContainer<>(ExileRegistryTypes.OMEN, ""));

        Database.addRegistry(new ExileRegistryContainer<>(ExileRegistryTypes.BOSS_ARENA, "sandstone"));
        Database.addRegistry(new ExileRegistryContainer<>(ExileRegistryTypes.ORB_EXTEND, ""));

    }
}
