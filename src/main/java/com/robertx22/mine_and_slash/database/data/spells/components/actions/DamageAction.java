package com.robertx22.mine_and_slash.database.data.spells.components.actions;

import com.robertx22.mine_and_slash.database.data.game_balance_config.GameBalanceConfig;
import com.robertx22.mine_and_slash.database.data.spells.components.MapHolder;
import com.robertx22.mine_and_slash.database.data.spells.map_fields.MapField;
import com.robertx22.mine_and_slash.database.data.spells.spell_classes.SpellCtx;
import com.robertx22.mine_and_slash.database.data.value_calc.ValueCalculation;
import com.robertx22.mine_and_slash.mmorpg.MMORPG;
import com.robertx22.mine_and_slash.uncommon.MathHelper;
import com.robertx22.mine_and_slash.uncommon.datasaving.Load;
import com.robertx22.mine_and_slash.uncommon.effectdatas.DamageEvent;
import com.robertx22.mine_and_slash.uncommon.effectdatas.EventBuilder;
import com.robertx22.mine_and_slash.uncommon.effectdatas.rework.EventData;
import com.robertx22.mine_and_slash.uncommon.enumclasses.Elements;
import net.minecraft.world.entity.LivingEntity;

import java.util.Arrays;
import java.util.Collection;

import static com.robertx22.mine_and_slash.database.data.spells.map_fields.MapField.ELEMENT;
import static com.robertx22.mine_and_slash.database.data.spells.map_fields.MapField.VALUE_CALCULATION;

public class DamageAction extends SpellAction {

    public DamageAction() {
        super(Arrays.asList(ELEMENT, VALUE_CALCULATION));
    }

    @Override
    public void tryActivate(Collection<LivingEntity> targets, SpellCtx ctx, MapHolder data) {

        if (!ctx.world.isClientSide) {

            Collection<LivingEntity> targetList = targets;


            Elements ele = data.getElement();
            ValueCalculation calc = data.get(VALUE_CALCULATION);

            float dmgEffectiveness = calc.getDamageEffectiveness(ctx.caster, ctx.calculatedSpellData.getSpell());

            int value = calc.getCalculatedValue(ctx.caster, ctx.calculatedSpellData.getSpell());

            if (ctx.calculatedSpellData.chains_did > 0) {
                float dmgMulti = MathHelper.clamp(1F - (GameBalanceConfig.get().DMG_REDUCT_PER_CHAIN * ctx.calculatedSpellData.chains_did), GameBalanceConfig.get().MIN_CHAIN_DMG, 1F);
                value *= dmgMulti;
            }

            for (LivingEntity t : targetList) {

                if (t == null) {
                    continue;
                }

                DamageEvent dmg = EventBuilder.ofSpellDamage(ctx.caster, t, value, ctx.calculatedSpellData.getSpell())
                        .build();

                dmg.allowSelfDamage = data.getOrDefault(MapField.ALLOW_SELF_DAMAGE, false);

                if (MMORPG.RUN_DEV_TOOLS) {
                    //     dmg.allowSelfDamage = true; // todo
                }


                dmg.data.setupNumber(EventData.DMG_EFFECTIVENESS, dmgEffectiveness);

                if (ctx.calculatedSpellData.getSpell().usesWeaponForDamage()) {
                    var gear = ctx.getWeapon();
                    if (gear != null) {
                        dmg.data.setString(EventData.WEAPON_TYPE, gear.GetBaseGearType().weapon_type);
                    }
                }

                if (data.has(MapField.DMG_EFFECT_TYPE)) {
                    dmg.data.setString(EventData.ATTACK_TYPE, data.getDmgEffectType().name());
                }
                if (data.getOrDefault(MapField.DISABLE_KNOCKBACK, false)) {
                    dmg.data.setBoolean(EventData.DISABLE_KNOCKBACK, true);
                }
                if (ctx.sourceEntity instanceof LivingEntity && Load.Unit(ctx.sourceEntity).isSummon()) {
                    dmg.petEntity = (LivingEntity) ctx.sourceEntity;
                    dmg.data.setBoolean(EventData.IS_SUMMON_ATTACK, true);
                }


                dmg.setElement(ele);
                dmg.Activate();
            }
        }

    }

    public MapHolder create(ValueCalculation calc, Elements ele) {
        MapHolder dmg = new MapHolder();
        dmg.type = GUID();
        dmg.put(VALUE_CALCULATION, calc);
        dmg.put(ELEMENT, ele.name());
        return dmg;
    }

    @Override
    public String GUID() {
        return "damage";
    }

}
