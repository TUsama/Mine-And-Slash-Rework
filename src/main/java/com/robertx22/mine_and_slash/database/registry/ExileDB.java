package com.robertx22.mine_and_slash.database.registry;

import com.robertx22.addons.orbs_of_crafting.currency.reworked.addon.ExtendedOrb;
import com.robertx22.library_of_exile.main.ExileLog;
import com.robertx22.library_of_exile.registry.Database;
import com.robertx22.library_of_exile.registry.ExileRegistryContainer;
import com.robertx22.library_of_exile.registry.ExileRegistryType;
import com.robertx22.mine_and_slash.aoe_data.database.ailments.Ailment;
import com.robertx22.mine_and_slash.aoe_data.database.boss_spell.BossSpell;
import com.robertx22.mine_and_slash.capability.entity.EntityData;
import com.robertx22.mine_and_slash.content.ubers.UberBossArena;
import com.robertx22.mine_and_slash.database.data.DimensionConfig;
import com.robertx22.mine_and_slash.database.data.EntityConfig;
import com.robertx22.mine_and_slash.database.data.affixes.Affix;
import com.robertx22.mine_and_slash.database.data.aura.AuraGem;
import com.robertx22.mine_and_slash.database.data.auto_item.AutoItem;
import com.robertx22.mine_and_slash.database.data.base_stats.BaseStatsConfig;
import com.robertx22.mine_and_slash.database.data.boss_arena.BossArena;
import com.robertx22.mine_and_slash.database.data.chaos_stats.ChaosStat;
import com.robertx22.mine_and_slash.database.data.custom_item.CustomItem;
import com.robertx22.mine_and_slash.database.data.exile_effects.ExileEffect;
import com.robertx22.mine_and_slash.database.data.gear_slots.GearSlot;
import com.robertx22.mine_and_slash.database.data.gear_types.bases.BaseGearType;
import com.robertx22.mine_and_slash.database.data.gems.Gem;
import com.robertx22.mine_and_slash.database.data.league.LeagueMechanic;
import com.robertx22.mine_and_slash.database.data.loot_chest.base.LootChest;
import com.robertx22.mine_and_slash.database.data.map_affix.MapAffix;
import com.robertx22.mine_and_slash.database.data.mob_affixes.MobAffix;
import com.robertx22.mine_and_slash.database.data.omen.Omen;
import com.robertx22.mine_and_slash.database.data.perks.Perk;
import com.robertx22.mine_and_slash.database.data.profession.Profession;
import com.robertx22.mine_and_slash.database.data.profession.ProfessionRecipe;
import com.robertx22.mine_and_slash.database.data.profession.buffs.StatBuff;
import com.robertx22.mine_and_slash.database.data.prophecy.ProphecyModifier;
import com.robertx22.mine_and_slash.database.data.prophecy.ProphecyStart;
import com.robertx22.mine_and_slash.database.data.rarities.GearRarity;
import com.robertx22.mine_and_slash.database.data.rarities.MobRarity;
import com.robertx22.mine_and_slash.database.data.runes.Rune;
import com.robertx22.mine_and_slash.database.data.runewords.RuneWord;
import com.robertx22.mine_and_slash.database.data.spell_school.SpellSchool;
import com.robertx22.mine_and_slash.database.data.spells.components.Spell;
import com.robertx22.mine_and_slash.database.data.stat_compat.StatCompat;
import com.robertx22.mine_and_slash.database.data.stats.Stat;
import com.robertx22.mine_and_slash.database.data.stats.layers.StatLayer;
import com.robertx22.mine_and_slash.database.data.support_gem.SupportGem;
import com.robertx22.mine_and_slash.database.data.talent_tree.TalentTree;
import com.robertx22.mine_and_slash.database.data.unique_items.UniqueGear;
import com.robertx22.mine_and_slash.database.data.value_calc.ValueCalculation;
import com.robertx22.mine_and_slash.maps.dungeon_reg.Dungeon;
import com.robertx22.mine_and_slash.maps.spawned_map_mobs.SpawnedMobList;
import com.robertx22.mine_and_slash.mmorpg.MMORPG;
import com.robertx22.mine_and_slash.uncommon.effectdatas.rework.action.StatEffect;
import com.robertx22.mine_and_slash.uncommon.effectdatas.rework.condition.StatCondition;
import com.robertx22.mine_and_slash.uncommon.enumclasses.WeaponTypes;
import com.robertx22.mine_and_slash.uncommon.utilityclasses.MapManager;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.registries.ForgeRegistries;

public class ExileDB {


    public static DimensionConfig getDimensionConfig(LevelAccessor world) {
        String id = MapManager.getResourceLocation((Level) world)
                .toString();

        if (!DimensionConfigs().isRegistered(id)) {
            return ExileDB.DimensionConfigs().get(DimensionConfig.DEFAULT_ID);
        }

        return DimensionConfigs().get(id);
    }

    // todo

    public static EntityConfig getEntityConfig(LivingEntity entity, EntityData data) {

        var id = ForgeRegistries.ENTITY_TYPES.getKey(entity.getType());
        String monster_id = id.toString();
        String mod_id = id.getNamespace();

        EntityConfig config = null;

        if (EntityConfigs().isRegistered(monster_id)) {
            config = EntityConfigs().get(monster_id);
            if (config != null) {
                return config;
            }
        } else {
            if (EntityConfigs().isRegistered(mod_id)) {
                config = EntityConfigs().get(mod_id);

                if (config != null) {
                    return config;
                }

            } else {
                config = EntityConfigs().get(data.getType().id);

                if (config != null) {
                    return config;
                }
            }
        }

        return EntityConfigs().getDefault();

    }

    public static ExileRegistryContainer<GearSlot> GearSlots() {
        return Database.getRegistry(ExileRegistryTypes.GEAR_SLOT);
    }

    public static ExileRegistryContainer<UniqueGear> UniqueGears() {
        return Database.getRegistry(ExileRegistryTypes.UNIQUE_GEAR);
    }

    public static ExileRegistryContainer<ExtendedOrb> OrbExtension() {
        return Database.getRegistry(ExileRegistryTypes.ORB_EXTEND);
    }


    /*
    public static ExileRegistryContainer<Currency> CurrencyItems() {
        return Database.getRegistry(ExileRegistryTypes.CURRENCY_ITEMS);
    }


     */
    public static ExileRegistryContainer<DimensionConfig> DimensionConfigs() {
        return Database.getRegistry(ExileRegistryTypes.DIMENSION_CONFIGS);
    }

    public static ExileRegistryContainer<StatCondition> StatConditions() {
        return Database.getRegistry(ExileRegistryTypes.STAT_CONDITION);
    }


    public static ExileRegistryContainer<StatEffect> StatEffects() {
        return Database.getRegistry(ExileRegistryTypes.STAT_EFFECT);
    }

    public static ExileRegistryContainer<Gem> Gems() {
        return Database.getRegistry(ExileRegistryTypes.GEM);
    }

    public static ExileRegistryContainer<ExileEffect> ExileEffects() {
        return Database.getRegistry(ExileRegistryTypes.EXILE_EFFECT);
    }


    public static ExileRegistryContainer<TalentTree> TalentTrees() {
        return Database.getRegistry(ExileRegistryTypes.TALENT_TREE);
    }


    public static ExileRegistryContainer<Perk> Perks() {
        return Database.getRegistry(ExileRegistryTypes.PERK);
    }

    public static ExileRegistryContainer<Rune> Runes() {
        return Database.getRegistry(ExileRegistryTypes.RUNE);
    }

    public static ExileRegistryContainer<RuneWord> RuneWords() {
        return Database.getRegistry(ExileRegistryTypes.RUNEWORDS);
    }

    public static ExileRegistryContainer<Affix> Affixes() {
        return Database.getRegistry(ExileRegistryTypes.AFFIX);
    }

    public static ExileRegistryContainer<GearRarity> GearRarities() {
        return (ExileRegistryContainer<GearRarity>) Database.getRegistry(ExileRegistryTypes.GEAR_RARITY);
    }

    public static ExileRegistryContainer<MobRarity> MobRarities() {
        return Database.getRegistry(ExileRegistryTypes.MOB_RARITY);
    }

    public static ExileRegistryContainer<ProphecyModifier> ProphecyModifiers() {
        return Database.getRegistry(ExileRegistryTypes.PROPHECY_MODIFIER);
    }

    public static ExileRegistryContainer<ProphecyStart> ProphecyStarts() {
        return Database.getRegistry(ExileRegistryTypes.PROPHECY_START);
    }

    public static ExileRegistryContainer<BaseGearType> GearTypes() {
        return Database.getRegistry(ExileRegistryTypes.GEAR_TYPE);
    }


    public static ExileRegistryContainer<Spell> Spells() {
        return Database.getRegistry(ExileRegistryTypes.SPELL);
    }

    public static ExileRegistryContainer<MobAffix> MobAffixes() {
        return Database.getRegistry(ExileRegistryTypes.MOB_AFFIX);
    }

    public static ExileRegistryContainer<Ailment> Ailments() {
        return Database.getRegistry(ExileRegistryTypes.AILMENT);
    }

    public static ExileRegistryContainer<ValueCalculation> ValueCalculations() {
        return Database.getRegistry(ExileRegistryTypes.VALUE_CALC);
    }

    public static ExileRegistryContainer<Dungeon> Dungeons() {
        return Database.getRegistry(ExileRegistryTypes.DUNGEON);
    }

    public static ExileRegistryContainer<SpawnedMobList> MapMobs() {
        return Database.getRegistry(ExileRegistryTypes.SPAWNED_MOBS);
    }

    public static ExileRegistryContainer<Omen> Omens() {
        return Database.getRegistry(ExileRegistryTypes.OMEN);
    }


    public static ExileRegistryContainer<StatLayer> StatLayers() {
        return Database.getRegistry(ExileRegistryTypes.STAT_LAYER);
    }

    public static ExileRegistryContainer<EntityConfig> EntityConfigs() {
        return Database.getRegistry(ExileRegistryTypes.ENTITY_CONFIGS);
    }


    public static ExileRegistryContainer<SpellSchool> SpellSchools() {
        return Database.getRegistry(ExileRegistryTypes.SPELL_SCHOOL);
    }

    public static ExileRegistryContainer<BossSpell> BossSpells() {
        return Database.getRegistry(ExileRegistryTypes.BOSS_SPELL);
    }


    public static ExileRegistryContainer<MapAffix> MapAffixes() {
        return Database.getRegistry(ExileRegistryTypes.MAP_AFFIX);
    }

    public static ExileRegistryContainer<LeagueMechanic> LeagueMechanics() {
        return Database.getRegistry(ExileRegistryTypes.LEAGUE_MECHANIC);
    }

    public static ExileRegistryContainer<UberBossArena> UberBoss() {
        return Database.getRegistry(ExileRegistryTypes.UBER_BOSS);
    }

    public static ExileRegistryContainer<LootChest> LootChests() {
        return Database.getRegistry(ExileRegistryTypes.LOOT_CHEST);
    }


    public static ExileRegistryContainer<SupportGem> SupportGems() {
        return Database.getRegistry(ExileRegistryTypes.SUPPORT_GEM);
    }

    public static ExileRegistryContainer<Profession> Professions() {
        return Database.getRegistry(ExileRegistryTypes.PROFESSION);
    }

    public static ExileRegistryContainer<AutoItem> AutoItems() {
        return Database.getRegistry(ExileRegistryTypes.AUTO_ITEM);
    }

    public static ExileRegistryContainer<CustomItem> CustomItemGenerations() {
        return Database.getRegistry(ExileRegistryTypes.CUSTOM_ITEM);
    }

    public static ExileRegistryContainer<StatBuff> StatBuffs() {
        return Database.getRegistry(ExileRegistryTypes.STAT_BUFF);
    }

    public static ExileRegistryContainer<ChaosStat> ChaosStats() {
        return Database.getRegistry(ExileRegistryTypes.CHAOS_STAT);
    }

    public static ExileRegistryContainer<ProfessionRecipe> Recipes() {
        return Database.getRegistry(ExileRegistryTypes.RECIPE);
    }

    public static ExileRegistryContainer<BossArena> BossArena() {
        return Database.getRegistry(ExileRegistryTypes.BOSS_ARENA);
    }

    public static ExileRegistryContainer<WeaponTypes> WeaponTypes() {
        return Database.getRegistry(ExileRegistryTypes.WEAPON_TYPE);
    }


    public static ExileRegistryContainer<StatCompat> StatCompat() {
        return Database.getRegistry(ExileRegistryTypes.STAT_COMPAT);
    }

    public static ExileRegistryContainer<AuraGem> AuraGems() {
        return Database.getRegistry(ExileRegistryTypes.AURA);
    }


    public static ExileRegistryContainer<Stat> Stats() {
        return Database.getRegistry(ExileRegistryTypes.STAT);
    }

    public static ExileRegistryContainer<BaseStatsConfig> BaseStats() {
        return Database.getRegistry(ExileRegistryTypes.BASE_STATS);
    }

    public static void checkAllDatabasesHaveDefaultEmpty() {

        for (ExileRegistryType type : ExileRegistryType.getAllInRegisterOrder()) {
            var reg = Database.getRegistry(type);
            var em = reg.getDefault();
            if (em == null) {
                if (MMORPG.RUN_DEV_TOOLS) {
                    ExileLog.get().warn(type.id + " default is null or not registered");
                }
            }
        }
    }

}
