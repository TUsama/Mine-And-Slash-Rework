package com.robertx22.mine_and_slash.uncommon.effectdatas.rework;

import com.robertx22.mine_and_slash.database.data.exile_effects.ExileEffect;
import com.robertx22.mine_and_slash.database.registry.ExileDB;
import com.robertx22.mine_and_slash.mmorpg.MMORPG;
import com.robertx22.mine_and_slash.saveclasses.unit.ResourceType;
import com.robertx22.mine_and_slash.uncommon.effectdatas.GiveOrTake2;
import com.robertx22.mine_and_slash.uncommon.enumclasses.AttackType;
import com.robertx22.mine_and_slash.uncommon.enumclasses.Elements;
import com.robertx22.mine_and_slash.uncommon.enumclasses.PlayStyle;
import com.robertx22.mine_and_slash.uncommon.enumclasses.WeaponTypes;

import java.util.HashMap;

public class EventData {
    public static String AILMENT = "ailment";

    public static String NUMBER = "number";
    public static String BEFORE_CONVERSION_NUMBER = "before_conversion_number";
    public static String ITEM_ID = "item_id";
    public static String IS_SUMMON_ATTACK = "is_summon_attack";
    public static String IS_BONUS_ELEMENT_DAMAGE = "is_bonus_element_damage";
    //public static String BONUS_MAX_SUMMONS = "bonus_max_summons";
    public static String BONUS_TOTAL_SUMMONS = "bonus_total_summons";
    public static String SUMMON_TYPE = "summon_type";
    public static String CANCELED = "canceled";
    public static String DMG_EFFECTIVENESS = "dmg_effectiveness";
    public static String CRIT = "crit";
    public static String ELEMENT = "element";
    public static String ATTACK_TYPE = "attack_type";
    public static String RESOURCE_TYPE = "resource_type";
    public static String RESTORE_TYPE = "restore_type";
    public static String ACCURACY = "accuracy";
    public static String SPELL = "spell";
    public static String WEAPON_TYPE = "weapon_type";
    public static String UNARMED_ATTACK = "unarmed_attack";
    public static String IS_BASIC_ATTACK = "is_basic_atk";
    public static String IS_ATTACK_FULLY_CHARGED = "is_charged_atk";
    public static String ATTACK_COOLDOWN = "attack_cooldown";
    public static String STYLE = "style";
    public static String THREAT_GEN_TYPE = "threat_gen_type";
    public static String EXILE_EFFECT = "exile_effect";
    public static String GIVE_OR_TAKE = "give_or_take";
    public static String STACKS = "stacks";

    public static String IS_HIT_AVOIDED = "is_hit_avoided";
    public static String IS_DODGED = "is_dodged";
    public static String IS_BLOCKED = "is_blocked";
    public static String DISABLE_KNOCKBACK = "disable_knockback";
    public static String PENETRATION = "penetration";
    public static String RESISTED_ALREADY = "resisted_already";
    public static String SECONDS = "seconds";

    public static String CAST_TICKS = "cast_ticks";
    public static String EFFECT_DURATION_TICKS = "effect_duration_ticks";
    public static String COOLDOWN_TICKS = "cd_ticks";
    public static String CHARGE_COOLDOWN_TICKS = "charge_cd_ticks";
    public static String MANA_COST = "mana_cost";
    public static String ENERGY_COST = "energy_cost";
    public static String AREA_MULTI = "area";
    public static String PIERCE = "pierce";
    public static String BARRAGE = "barrage";
    public static String PROJECTILE_SPEED_MULTI = "proj_speed";
    public static String BONUS_PROJECTILES = "bonus_proj";
    public static String BONUS_CHAINS = "bonus_chains";
    public static String DURATION_MULTI = "duration_multi";
    public static String AGGRO_RADIUS = "aggro_radius";

    private boolean isFrozen = false;

    private HashMap<String, WrappedFloat> floats = new HashMap<>();
    private HashMap<String, Boolean> bools = new HashMap<>();
    private HashMap<String, String> strings = new HashMap<>();

    private void tryFreezeErrorMessage() {
        if (isFrozen && MMORPG.RUN_DEV_TOOLS) {
            try {
                //throw new RuntimeException("Event data frozen but code tried to modify it.");
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        }
    }

    public void setHitAvoided(String avoidtype) {
        this.setBoolean(IS_HIT_AVOIDED, true);
        this.setBoolean(avoidtype, true);
        getNumber(EventData.NUMBER).number = 0;

    }

    public void setupNumber(String id, float num) {


        tryFreezeErrorMessage();

        if (floats.containsKey(id)) {
            throw new RuntimeException("Number is already setup: " + id);
        }

        this.getNumber(id).number = num;
        this.getOriginalNumber(id).number = num;
    }

    public WrappedFloat getNumber(String id) {
        if (!floats.containsKey(id)) {
            floats.put(id, new WrappedFloat(0));
        }
        return floats.get(id);
    }

    public boolean isNumberSetup(String id) {
        return floats.containsKey(id);
    }

    public WrappedFloat getNumber(String id, float defNum) {
        if (!floats.containsKey(id)) {
            floats.put(id, new WrappedFloat(defNum));
        }
        return floats.get(id);
    }

    public WrappedFloat getOriginalNumber(String id) {
        return getNumber("original_" + id);
    }

    public boolean getBoolean(String id) {
        return bools.getOrDefault(id, false);
    }

    public void setBoolean(String id, Boolean bool) {
        tryFreezeErrorMessage();

        bools.put(id, bool);
    }

    public float getNumber() {
        return getNumber(NUMBER).number;
    }

    public boolean isCanceled() {
        return getBoolean(CANCELED);
    }

    public boolean isHitAvoided() {
        return getBoolean(IS_HIT_AVOIDED);
    }

    public Elements getElement() {
        return Elements.valueOf(strings.getOrDefault(ELEMENT, Elements.Physical.name()));
    }

    public Elements getThreatGenType() {
        return Elements.valueOf(strings.getOrDefault(ELEMENT, Elements.Physical.name()));
    }

    public WeaponTypes getWeaponType() {
        return ExileDB.WeaponTypes().get(strings.getOrDefault(WEAPON_TYPE, WeaponTypes.none.id));
    }

    public void setElement(Elements ele) {
        setString(ELEMENT, ele.name());
    }

    public String getString(String id) {
        return strings.getOrDefault(id, "");
    }

    public AttackType getAttackType() {
        return AttackType.valueOf(strings.getOrDefault(ATTACK_TYPE, AttackType.hit.name()));
    }

    public PlayStyle getStyle() {
        return PlayStyle.fromID(strings.getOrDefault(STYLE, PlayStyle.STR.id));
    }

    public ExileEffect getExileEffect() {
        return ExileDB.ExileEffects()
                .get(getString(EXILE_EFFECT));
    }

    public boolean hasExileEffect() {
        return ExileDB.ExileEffects()
                .isRegistered(getString(EXILE_EFFECT));
    }

    public GiveOrTake2 getGiveOrTake() {
        return GiveOrTake2.valueOf(strings.getOrDefault(GIVE_OR_TAKE, GiveOrTake2.give.name()));
    }

    public ResourceType getResourceType() {
        return ResourceType.valueOf(strings.getOrDefault(RESOURCE_TYPE, ResourceType.health.name()));
    }

    public RestoreType getRestoreType() {
        return RestoreType.valueOf(strings.getOrDefault(RESTORE_TYPE, RestoreType.heal.name()));
    }

    public boolean isSpellEffect() {
        return ExileDB.Spells().isRegistered(getString(SPELL));
    }

    public boolean isBasicAttack() {
        return getBoolean(IS_BASIC_ATTACK);
    }

    public boolean isCrit() {
        return getBoolean(CRIT);
    }

    public void setString(String id, String str) {
        tryFreezeErrorMessage();
        // careful about order here
        this.strings.put(id, str);
    }

    public void freeze() {
        this.isFrozen = true;
    }
}

