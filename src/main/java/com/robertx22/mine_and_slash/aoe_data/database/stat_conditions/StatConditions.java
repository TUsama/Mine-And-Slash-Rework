package com.robertx22.mine_and_slash.aoe_data.database.stat_conditions;

import com.robertx22.library_of_exile.registry.ExileRegistryInit;
import com.robertx22.mine_and_slash.aoe_data.DataHolder;
import com.robertx22.mine_and_slash.aoe_data.database.ailments.Ailment;
import com.robertx22.mine_and_slash.aoe_data.database.ailments.Ailments;
import com.robertx22.mine_and_slash.aoe_data.database.exile_effects.adders.ModEffects;
import com.robertx22.mine_and_slash.aoe_data.database.spells.SummonType;
import com.robertx22.mine_and_slash.aoe_data.database.spells.schools.ProcSpells;
import com.robertx22.mine_and_slash.aoe_data.database.spells.schools.WaterSpells;
import com.robertx22.mine_and_slash.aoe_data.database.stats.base.EffectCtx;
import com.robertx22.mine_and_slash.mmorpg.MMORPG;
import com.robertx22.mine_and_slash.saveclasses.unit.ResourceType;
import com.robertx22.mine_and_slash.tags.ModTag;
import com.robertx22.mine_and_slash.tags.TagType;
import com.robertx22.mine_and_slash.tags.all.SpellTags;
import com.robertx22.mine_and_slash.tags.imp.SpellTag;
import com.robertx22.mine_and_slash.uncommon.effectdatas.ThreatGenType;
import com.robertx22.mine_and_slash.uncommon.effectdatas.rework.EventData;
import com.robertx22.mine_and_slash.uncommon.effectdatas.rework.RestoreType;
import com.robertx22.mine_and_slash.uncommon.effectdatas.rework.condition.*;
import com.robertx22.mine_and_slash.uncommon.enumclasses.AttackType;
import com.robertx22.mine_and_slash.uncommon.enumclasses.PlayStyle;
import com.robertx22.mine_and_slash.uncommon.enumclasses.WeaponTypes;
import com.robertx22.mine_and_slash.uncommon.interfaces.EffectSides;

import java.util.Arrays;

public class StatConditions implements ExileRegistryInit {

    public static DataHolder<String, StatCondition> IS_BOOLEAN = new DataHolder<>(Arrays.asList(
            EventData.IS_BLOCKED,
            EventData.IS_DODGED,
            EventData.CRIT,
            EventData.IS_SUMMON_ATTACK,
            EventData.IS_BASIC_ATTACK
    ), x -> new IsBooleanTrueCondition(x));

    public static DataHolder<String, StatCondition> IS_FALSE = new DataHolder<>(Arrays.asList(
            EventData.IS_BLOCKED,
            EventData.IS_BONUS_ELEMENT_DAMAGE,
            EventData.IS_DODGED,
            EventData.CRIT,
            EventData.IS_BASIC_ATTACK,
            EventData.IS_SUMMON_ATTACK
    ), x -> new IsBooleanTrueCondition(x).flipCondition());

    public static StatCondition IF_RANDOM_ROLL = new RandomRollCondition();
    public static StatCondition IS_SPELL = new IsSpellCondition();
    public static StatCondition ELEMENT_MATCH_STAT = new ElementMatchesStat();
    public static StatCondition IS_DAY = new IsDayCondition();
    public static StatCondition IS_NIGHT = new IsDayCondition().flipCondition();
    public static StatCondition IS_RANGED_WEAPON = new IsRangedWeapon();
    public static StatCondition IS_TARGET_UNDEAD = new IsUndeadCondition();
    public static StatCondition IS_TARGET_CURSED = new IsTargetCursed();
    public static StatCondition IS_TARGET_NOT_UNDEAD = new IsUndeadCondition().flipCondition();
    public static StatCondition IS_IN_COMBAT = new IsInCombatCondition();
    public static StatCondition IS_NOT_IN_COMBAT = new IsInCombatCondition().flipCondition();
    public static StatCondition IS_TARGET_LOW_HP = new IsHealthBellowPercentCondition("is_target_low_hp", 30, EffectSides.Target);
    public static StatCondition IS_TARGET_LOW_MAGIC_SHIELD = new IsMSBellowPercentCondition("is_target_low_magic_shield", 30, EffectSides.Target);
    public static StatCondition IS_TARGET_LOW = new IsTargetLow("is_target_low", 30, EffectSides.Target);
    public static StatCondition IS_SOURCE_LOW_HP = new IsHealthBellowPercentCondition("is_source_low_hp", 30, EffectSides.Source);
    public static StatCondition IS_TARGET_NEAR_FULL_HP = new IsHealthAbovePercentCondition("is_target_near_full_hp", 70, EffectSides.Target);
    public static StatCondition IS_ELEMENTAL = new IsElementalDamageCondition();
    public static StatCondition IS_ATTACK_DAMAGE = new StringMatchesCondition(EventData.STYLE, PlayStyle.INT.id).flipCondition();

    public static DataHolder<String, StatCondition> IS_NOT_ON_COOLDOWN = new DataHolder<>(Arrays.asList(
            ProcSpells.PROFANE_EXPLOSION,
            ProcSpells.IGNITE_EXPLOSION,
            ProcSpells.BLOOD_EXPLOSION,
            WaterSpells.BONE_SHATTER_PROC

    ), x -> new IsNotOnCooldownCondition(x));


    public static DataHolder<EffectCtx, StatCondition> TARGET_HAS_EFFECT = new DataHolder<>(Arrays.asList(ModEffects.BONE_CHILL), x -> new IsUnderExileEffect(x, EffectSides.Target));

    public static DataHolder<EffectCtx, StatCondition> IS_SOURCE_MAX_CHARGES = new DataHolder<>(Arrays.asList(ModEffects.ESSENCE_OF_FROST), x -> new IsAtMaxCharges(x, EffectSides.Source));


    public static DataHolder<Ailment, StatCondition> IS_EVENT_AILMENT = new DataHolder<>(Ailments.ALL, x -> new IsAilmentCondition(x));

    public static DataHolder<ModTag, StatCondition> EFFECT_HAS_TAG = new DataHolder<>(ModTag.MAP.get(TagType.Effect), x -> new EffectHasTagCondition(x));

    public static DataHolder<EffectCtx, StatCondition> IS_EFFECT = new DataHolder<>(ModEffects.getCurses(), x -> new IsEffectCondition(x));


    public static DataHolder<ResourceType, StatCondition> IS_RESOURCE = new DataHolder<>(ResourceType.values(), x -> new StringMatchesCondition(EventData.RESOURCE_TYPE, x.name()));
    public static DataHolder<ResourceType, StatCondition> SPELL_HAS_RESOURCE_TYPE_COST = new DataHolder<>(ResourceType.values(), x -> new SpellHasResourceCost(x));

    public static StatCondition IS_HEALTH_OR_MS_RESTORE = new EitherIsTrueCondition(
            "is_hp_or_ms_restore",
            Arrays.asList(
                    IS_RESOURCE.get(ResourceType.health).GUID(),
                    IS_RESOURCE.get(ResourceType.magic_shield).GUID()
            )
    );

    public static DataHolder<ThreatGenType, StatCondition> IS_THREAT_GEN_TYPE = new DataHolder<>(
            ThreatGenType.values()
            , x -> new StringMatchesCondition(EventData.THREAT_GEN_TYPE, x.name()));

    public static DataHolder<SpellTag, StatCondition> SPELL_HAS_TAG = new DataHolder<>(
            SpellTag.getAll()
            , x -> new SpellHasTagCondition(x));

    public static DataHolder<SpellTag, StatCondition> SPELL_NOT_HAVE_TAG = new DataHolder<>(
            SpellTag.getAll()
            , x -> new SpellHasTagCondition(x).flipCondition());

    public static DataHolder<SummonType, StatCondition> IS_SUMMON_TYPE = new DataHolder<>(
            SummonType.values()
            , x -> new StringMatchesCondition(EventData.SUMMON_TYPE, x.id));

    public static DataHolder<RestoreType, StatCondition> IS_RESTORE_TYPE = new DataHolder<>(
            RestoreType.values()
            , x -> new StringMatchesCondition(EventData.RESTORE_TYPE, x.name()));


    public static StatCondition IS_ANY_PROJECTILE = new EitherIsTrueCondition("is_projectile",
            Arrays.asList(SPELL_HAS_TAG.get(SpellTags.projectile).GUID(), IS_RANGED_WEAPON.GUID()));

    public static DataHolder<AttackType, StatCondition> ATTACK_TYPE_MATCHES = new DataHolder<>(
            AttackType.values()
            , x -> new StringMatchesCondition(EventData.ATTACK_TYPE, x.name()));

    public static DataHolder<WeaponTypes, StatCondition> WEAPON_TYPE_MATCHES = new DataHolder<>(
            WeaponTypes.getAll()
            , x -> new WeaponTypeMatches(x));

    // todo
    public static StatCondition IS_ATTACK_OR_SPELL_ATTACK = new EitherIsTrueCondition(
            "is_attack_or_spell_attack",
            Arrays.asList(ATTACK_TYPE_MATCHES.get(AttackType.hit).GUID())
    );
    public static StatCondition IS_HIT_OR_BONUS = new EitherIsTrueCondition(
            "is_hit_or_bonus",
            Arrays.asList(
                    ATTACK_TYPE_MATCHES.get(AttackType.hit).GUID(),
                    ATTACK_TYPE_MATCHES.get(AttackType.bonus_dmg).GUID()
            )
    );

    public static StatCondition IS_NOT_DOT = new StringMatchesCondition(EventData.ATTACK_TYPE, AttackType.dot.name()).flipCondition();

    public static StatCondition SOURCE_IS_TARGET = new SourceAndTargetAreSame();

    public static void loadClass() {
    }

    @Override
    public void registerAll() {
        for (StatCondition c : StatCondition.ALL) {
            c.addToSerializables(MMORPG.SERIAZABLE_REGISTRATION_INFO);
        }
    }
}
