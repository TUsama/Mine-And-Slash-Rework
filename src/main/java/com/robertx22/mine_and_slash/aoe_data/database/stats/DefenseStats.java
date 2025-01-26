package com.robertx22.mine_and_slash.aoe_data.database.stats;

import com.robertx22.mine_and_slash.aoe_data.database.stat_conditions.StatConditions;
import com.robertx22.mine_and_slash.aoe_data.database.stat_effects.StatEffects;
import com.robertx22.mine_and_slash.aoe_data.database.stats.base.DatapackStatBuilder;
import com.robertx22.mine_and_slash.aoe_data.database.stats.base.EmptyAccessor;
import com.robertx22.mine_and_slash.database.data.stats.Stat;
import com.robertx22.mine_and_slash.database.data.stats.StatScaling;
import com.robertx22.mine_and_slash.database.data.stats.datapacks.test.DataPackStatAccessor;
import com.robertx22.mine_and_slash.database.data.stats.priority.StatPriority;
import com.robertx22.mine_and_slash.tags.all.SpellTags;
import com.robertx22.mine_and_slash.tags.imp.SpellTag;
import com.robertx22.mine_and_slash.uncommon.effectdatas.DamageEvent;
import com.robertx22.mine_and_slash.uncommon.effectdatas.DamageInitEvent;
import com.robertx22.mine_and_slash.uncommon.effectdatas.rework.EventData;
import com.robertx22.mine_and_slash.uncommon.enumclasses.Elements;
import com.robertx22.mine_and_slash.uncommon.interfaces.EffectSides;

public class DefenseStats {

    public static DataPackStatAccessor<EmptyAccessor> NO_SELF_DAMAGE_STATS = DatapackStatBuilder
            .ofSingle("no_attacker_stats_on_selfdmg", Elements.Physical)
            .worksWithEvent(DamageInitEvent.ID)
            .setPriority(StatPriority.Spell.FIRST)
            .setSide(EffectSides.Source)
            .addCondition(StatConditions.SPELL_HAS_TAG.get(SpellTags.SELF_DAMAGE))
            .addCondition(StatConditions.SOURCE_IS_TARGET)
            .addEffect(StatEffects.DISABLE_ATTACKER_STAT_EFFECTS)
            .setLocName(x -> "Spells tagged as [Self Damage] do not trigger attacker's stat effects on the attacker")
            .setLocDesc(x -> "")
            .modifyAfterDone(x -> {
                x.is_long = true;
                x.scaling = StatScaling.NONE;
                x.group = Stat.StatGroup.Misc;
                x.min = 0;
                x.max = 1;
            })
            .build();

    public static DataPackStatAccessor<Elements> ALWAYS_CRIT_WHEN_HIT_BY_ELEMENT = DatapackStatBuilder
            .<Elements>of(x -> x.guidName + "_vuln_crit", x -> x)
            .addAllOfType(Elements.values())
            .worksWithEvent(DamageEvent.ID)
            .setPriority(StatPriority.Damage.BEFORE_DAMAGE_LAYERS)
            .setSide(EffectSides.Target)
            .addCondition(StatConditions.ELEMENT_MATCH_STAT)
            .addEffect(StatEffects.SET_BOOLEAN.get(EventData.CRIT))
            .setLocName(x -> Stat.format(x.dmgName + " Damage always crits you."))
            .setLocDesc(x -> "")
            .modifyAfterDone(x -> {
                x.is_perc = true;
                x.is_long = true;
            })
            .build();
    public static DataPackStatAccessor<EmptyAccessor> DAMAGE_RECEIVED = DatapackStatBuilder
            .ofSingle("dmg_received", Elements.Physical)
            .worksWithEvent(DamageEvent.ID)
            .setPriority(StatPriority.Damage.DAMAGE_LAYERS)
            .setSide(EffectSides.Target)
            .addEffect(StatEffects.Layers.ADDITIVE_DAMAGE_PERCENT)
            .setLocName(x -> "Damage Received")
            .setLocDesc(x -> "")
            .modifyAfterDone(x -> {
                x.is_perc = true;
                x.scaling = StatScaling.NONE;
                x.group = Stat.StatGroup.Misc;
                x.max = 90;
            })
            .build();

    public static DataPackStatAccessor<Elements> ELEMENTAL_DAMAGE_REDUCTION = DatapackStatBuilder
            .<Elements>of(x -> x.guidName + "_dmg_reduction", x -> x)
            .addAllOfType(Elements.values())
            .worksWithEvent(DamageEvent.ID)
            .setPriority(StatPriority.Damage.DAMAGE_LAYERS)
            .setSide(EffectSides.Target)
            .addCondition(x -> StatConditions.ELEMENT_MATCH_STAT)
            .addEffect(StatEffects.Layers.DAMAGE_REDUCTION)
            .setLocName(x -> x.dmgName + " Damage Reduction")
            .setLocDesc(x -> "")
            .modifyAfterDone(x -> {
                x.is_perc = true;
                x.scaling = StatScaling.NONE;
                x.group = Stat.StatGroup.Misc;
                x.min = -500;
                x.max = 50;
            })
            .build();

    public static DataPackStatAccessor<EmptyAccessor> DAMAGE_REDUCTION = DatapackStatBuilder
            .<EmptyAccessor>ofSingle("dmg_reduction", Elements.Physical)
            .worksWithEvent(DamageEvent.ID)
            .setPriority(StatPriority.Damage.DAMAGE_LAYERS)
            .setSide(EffectSides.Target)
            .addEffect(StatEffects.Layers.DAMAGE_REDUCTION)
            .setLocName(x -> "Damage Reduction")
            .setLocDesc(x -> "")
            .modifyAfterDone(x -> {
                x.is_perc = true;
                x.scaling = StatScaling.NONE;
                x.group = Stat.StatGroup.Misc;
                x.min = -500;
                x.max = 50;
            })
            .build();

    public static DataPackStatAccessor<EmptyAccessor> DAMAGE_REDUCTION_CHANCE = DatapackStatBuilder
            .<EmptyAccessor>ofSingle("dmg_reduction_chance", Elements.Physical)
            .worksWithEvent(DamageEvent.ID)
            .setPriority(StatPriority.Damage.DAMAGE_LAYERS)
            .setSide(EffectSides.Target)
            .addCondition(StatConditions.IF_RANDOM_ROLL)
            .addEffect(StatEffects.Layers.DAMAGE_SUPPRESSION_50)
            .setLocName(x -> "Damage Suppression Chance").
            setLocDesc(x -> "Chance to reduce damage by 50%.").
            modifyAfterDone(x ->
            {
                x.is_perc = true;
                x.scaling = StatScaling.NONE;
                x.group = Stat.StatGroup.Misc;
                x.min = 0;
                x.max = 100;
            }).
            build();


    public static DataPackStatAccessor<EmptyAccessor> PROJECTILE_DAMAGE_RECEIVED = DatapackStatBuilder
            .ofSingle("proj_dmg_received", Elements.Physical)
            .worksWithEvent(DamageEvent.ID)
            .setPriority(StatPriority.Damage.DAMAGE_LAYERS)
            .setSide(EffectSides.Target)
            .addCondition(StatConditions.IS_ANY_PROJECTILE)
            .addEffect(StatEffects.Layers.ADDITIVE_DAMAGE_PERCENT)
            .setLocName(x -> "Projectile Damage Receieved")
            .setLocDesc(x -> "Affects projectile damage, includes projectile spells like fireballs, and ranged basic attacks.")
            .modifyAfterDone(x -> {
                x.is_perc = true;
                x.base = 0;
            })
            .build();
    public static DataPackStatAccessor<SpellTag> DAMAGE_TAKEN_PER_SPELL_TAG = DatapackStatBuilder
            .<SpellTag>of(x -> x.GUID() + "_spell_dmg_taken", x -> Elements.Physical)
            .addAllOfType(SpellTag.getAll())
            .worksWithEvent(DamageEvent.ID)
            .setPriority(StatPriority.Damage.DAMAGE_LAYERS)
            .setSide(EffectSides.Target)
            .setUsesMoreMultiplier()
            .addCondition(x -> StatConditions.SPELL_HAS_TAG.get(x))
            .addEffect(StatEffects.Layers.ADDITIVE_DAMAGE_PERCENT)
            .setLocName(x -> x.locNameForLangFile() + " Damage Taken")
            .setLocDesc(x -> "")
            .modifyAfterDone(x -> {
                x.is_perc = true;
            })
            .build();

    public static void init() {

    }
}
