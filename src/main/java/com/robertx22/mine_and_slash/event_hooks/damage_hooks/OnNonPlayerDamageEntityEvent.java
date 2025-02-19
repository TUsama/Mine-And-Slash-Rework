package com.robertx22.mine_and_slash.event_hooks.damage_hooks;

import com.robertx22.library_of_exile.events.base.EventConsumer;
import com.robertx22.library_of_exile.events.base.ExileEvents;
import com.robertx22.mine_and_slash.event_hooks.damage_hooks.util.AttackInformation;
import com.robertx22.mine_and_slash.event_hooks.damage_hooks.util.DmgSourceUtils;
import com.robertx22.mine_and_slash.mixin_ducks.DamageSourceDuck;
import com.robertx22.mine_and_slash.uncommon.UnstuckMobs;
import com.robertx22.mine_and_slash.uncommon.datasaving.Load;
import com.robertx22.mine_and_slash.uncommon.utilityclasses.WorldUtils;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class OnNonPlayerDamageEntityEvent extends EventConsumer<ExileEvents.OnDamageEntity> {

    @Override
    public void accept(ExileEvents.OnDamageEntity event) {

        if (event.mob.level().isClientSide) {
            event.canceled = true;
            event.damage = 0;
            return;
        }
        if (event.source.is(DamageTypes.FELL_OUT_OF_WORLD)) {
            return;
        }
        if (event.source.getEntity() instanceof LivingEntity == false) {
            return;
        }
        if (event.mob instanceof Player == false) {
            if (WorldUtils.isMapWorldClass(event.mob.level(), event.mob.blockPosition())) {
                if (event.source.is(DamageTypes.IN_WALL)) {
                    UnstuckMobs.unstuckFromWalls(event.mob);
                    return;
                }
            }
        }
        if (DmgSourceUtils.isMyDmgSource(event.source)) {
            return;
        }

        if (!(event.source.getEntity() instanceof Player)) {
            if (event.source.getEntity() instanceof LivingEntity en && Load.Unit(en).isSummon()) {
                LivingEntity caster = Load.Unit(en).getSummonClass().getOwner();
                if (caster != null) {
                    PetAttackUTIL.tryAttack(en, caster, event.mob);
                    event.damage = 0;
                    event.canceled = true;
                }
            } else {
                LivingHurtUtils.tryAttack(new AttackInformation(event, AttackInformation.Mitigation.PRE, event.mob, event.source, event.damage));

                var duck = (DamageSourceDuck) event.source;
                duck.tryOverrideDmgWithMns(event);
            }
        }
    }
}
