package com.robertx22.mine_and_slash.database.data.profession.buffs;

import com.robertx22.mine_and_slash.aoe_data.database.stats.OffenseStats;
import com.robertx22.mine_and_slash.aoe_data.database.stats.ResourceStats;
import com.robertx22.mine_and_slash.aoe_data.database.stats.old.DatapackStats;
import com.robertx22.mine_and_slash.database.data.StatMod;
import com.robertx22.mine_and_slash.database.data.profession.all.ProfessionProductItems;
import com.robertx22.mine_and_slash.database.data.stats.types.loot.TreasureQuantity;
import com.robertx22.mine_and_slash.database.data.stats.types.misc.BonusExp;
import com.robertx22.mine_and_slash.database.data.stats.types.resources.energy.Energy;
import com.robertx22.mine_and_slash.database.data.stats.types.resources.energy.EnergyRegen;
import com.robertx22.mine_and_slash.database.data.stats.types.resources.health.Health;
import com.robertx22.mine_and_slash.database.data.stats.types.resources.health.HealthRegen;
import com.robertx22.mine_and_slash.database.data.stats.types.resources.magic_shield.MagicShield;
import com.robertx22.mine_and_slash.database.data.stats.types.resources.magic_shield.MagicShieldRegen;
import com.robertx22.mine_and_slash.database.data.stats.types.resources.mana.Mana;
import com.robertx22.mine_and_slash.database.data.stats.types.resources.mana.ManaRegen;
import com.robertx22.mine_and_slash.mmorpg.MMORPG;
import com.robertx22.mine_and_slash.mmorpg.registers.common.items.RarityItemHolder;
import com.robertx22.mine_and_slash.tags.all.SpellTags;
import com.robertx22.mine_and_slash.uncommon.enumclasses.Elements;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.function.Supplier;

public class StatBuffs {

    // we create these independent of the actual serializables so we can init before vanilla items are registered
    public static HashSet<AlchemyBuff> ALCHEMY = new HashSet<>();
    public static HashSet<FoodBuff> FOOD_BUFFS = new HashSet<>();
    public static HashSet<SeaFoodBuff> SEAFOOD_BUFFS = new HashSet<>();

    // have to manually then give recipes for each
    public static AlchemyBuff INT = new AlchemyBuff("int", "Intelligence", () -> DatapackStats.INT.mod(5, 15).percent()); // todo test if % more hp gives more hp
    public static AlchemyBuff DEX = new AlchemyBuff("dex", "Dexterity", () -> DatapackStats.DEX.mod(5, 15).percent());
    public static AlchemyBuff STR = new AlchemyBuff("str", "Strength", () -> DatapackStats.STR.mod(5, 15).percent());

    public static AlchemyBuff ARCANE = new AlchemyBuff("arcane", "Arcane", () -> OffenseStats.DAMAGE_PER_SPELL_TAG.get(SpellTags.magic).mod(5, 25));
    public static AlchemyBuff MIGHT = new AlchemyBuff("might", "Strength", () -> OffenseStats.ELEMENTAL_DAMAGE.get(Elements.Physical).mod(5, 25));
    public static AlchemyBuff CRIT = new AlchemyBuff("crit", "Criticals", () -> OffenseStats.CRIT_DAMAGE.get().mod(10, 30));

    public static class AlchemyBuff {
        public String id;
        public String name;
        public Supplier<StatMod> mod;

        public AlchemyBuff(String id, String name, Supplier<StatMod> mod) {
            this.id = id;
            this.name = name;
            this.mod = mod;

            ALCHEMY.add(this);
        }

        public RarityItemHolder getHolder() {
            return ProfessionProductItems.POTIONS.get(this);
        }
    }

    public static class SeaFoodBuff {
        public String id;
        public String name;
        public Supplier<List<StatMod>> mod;

        public SeaFoodBuff(String id, String name, Supplier<List<StatMod>> mod) {
            this.id = id;
            this.name = name;
            this.mod = mod;

            SEAFOOD_BUFFS.add(this);
        }

        public RarityItemHolder getHolder() {
            return ProfessionProductItems.SEAFOOD.get(this);
        }
    }

    public static class FoodBuff {
        public String id;
        public String name;
        public Supplier<List<StatMod>> mod;

        public FoodBuff(String id, String name, Supplier<List<StatMod>> mod) {
            this.id = id;
            this.name = name;
            this.mod = mod;

            FOOD_BUFFS.add(this);
        }

        public RarityItemHolder getHolder() {
            return ProfessionProductItems.FOODS.get(this);
        }
    }

    public static FoodBuff HEALTH = new FoodBuff("life", "Life's Joy", () -> Arrays.asList(
            ResourceStats.OUT_OF_COMBAT_REGEN.get().mod(20, 50),
            Health.getInstance().mod(5, 10).percent(),
            HealthRegen.getInstance().mod(5, 15).percent(),
            HealthRegen.getInstance().mod(1, 3)
    ));
    public static FoodBuff MANA = new FoodBuff("mana", "Mana's Inspiration", () -> Arrays.asList(
            ResourceStats.OUT_OF_COMBAT_REGEN.get().mod(20, 50),
            Mana.getInstance().mod(5, 10).percent(),
            ManaRegen.getInstance().mod(5, 15).percent(),
            ManaRegen.getInstance().mod(1, 3)
    ));
    public static FoodBuff ENERGY = new FoodBuff("energy", "Boundlessness of Energy", () -> Arrays.asList(
            ResourceStats.OUT_OF_COMBAT_REGEN.get().mod(20, 50),
            Energy.getInstance().mod(5, 10).percent(),
            EnergyRegen.getInstance().mod(5, 15).percent(),
            EnergyRegen.getInstance().mod(1, 3)
    ));
    public static FoodBuff MAGIC = new FoodBuff("magic", "Depth of Magic", () -> Arrays.asList(
            ResourceStats.OUT_OF_COMBAT_REGEN.get().mod(20, 50),
            MagicShield.getInstance().mod(5, 10).percent(),
            MagicShieldRegen.getInstance().mod(5, 15).percent(),
            MagicShieldRegen.getInstance().mod(1, 3)
    ));

    // seafood
    public static SeaFoodBuff EXP = new SeaFoodBuff("exp", "Seeker of Knowledge", () -> Arrays.asList(
            BonusExp.getInstance().mod(3, 15),
            OffenseStats.TOTAL_DAMAGE.get().mod(5, 25)
    ));
    public static SeaFoodBuff LOOT = new SeaFoodBuff("loot", "Seeker of Wealth", () -> Arrays.asList(
            TreasureQuantity.getInstance().mod(3, 15),
            OffenseStats.TOTAL_DAMAGE.get().mod(5, 25)
    ));

    public static void load() {

    }

    public static void init() {

        for (AlchemyBuff al : ALCHEMY) {
            StatBuff buff = new StatBuff();
            buff.id = al.id;
            buff.mods.add(al.mod.get());
            buff.addToSerializables(MMORPG.SERIAZABLE_REGISTRATION_INFO);
        }
        for (FoodBuff al : FOOD_BUFFS) {
            StatBuff buff = new StatBuff();
            buff.id = al.id;
            buff.mods.addAll(al.mod.get());
            buff.addToSerializables(MMORPG.SERIAZABLE_REGISTRATION_INFO);
        }
        for (SeaFoodBuff al : SEAFOOD_BUFFS) {
            StatBuff buff = new StatBuff();
            buff.id = al.id;
            buff.mods.addAll(al.mod.get());
            buff.addToSerializables(MMORPG.SERIAZABLE_REGISTRATION_INFO);
        }

    }
}
