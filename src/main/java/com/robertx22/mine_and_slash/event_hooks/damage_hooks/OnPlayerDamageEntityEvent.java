package com.robertx22.mine_and_slash.event_hooks.damage_hooks;

import com.robertx22.library_of_exile.events.base.EventConsumer;
import com.robertx22.library_of_exile.events.base.ExileEvents;
import com.robertx22.mine_and_slash.config.forge.compat.DamageConversion;
import com.robertx22.mine_and_slash.event_hooks.damage_hooks.util.AttackInformation;
import com.robertx22.mine_and_slash.event_hooks.damage_hooks.util.DmgSourceUtils;
import com.robertx22.mine_and_slash.mixin_ducks.DamageSourceDuck;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.player.Player;

public class OnPlayerDamageEntityEvent extends EventConsumer<ExileEvents.OnDamageEntity> {

    // this lock stops spawning new damage instances and infinite damage loops with other mods, hopefully
    static boolean lock = false;

    @Override

    public void accept(ExileEvents.OnDamageEntity event) {

        if (lock) {
            return;
        }
        lock = true;

        try {
            if (event.mob.level().isClientSide) {
                event.canceled = true;
                event.damage = 0;
                return;
            }
            if (event.source.is(DamageTypes.FELL_OUT_OF_WORLD)) {
                return;
            }
            // stop mobs from being killed b4 they even had time to calculate stats?
            // not sure yet why it happens but players report being able to instakill freshly spawned mobs
            if (event.mob.tickCount < 3) {
                event.damage = 0;
                event.canceled = true;
                return;
            }
            if (DmgSourceUtils.isMyDmgSource(event.source)) {
                return;
            }


            if (event.mob instanceof EnderDragon) {
                return; // todo temp fix
            }
            if (event.source.getEntity() instanceof Player) {

                var info = new AttackInformation(event, AttackInformation.Mitigation.PRE, event.mob, event.source, event.damage);

                if (!ValidDamageUtil.isValidAttack(info)) {
                    if (event.source != null && event.source.getEntity() instanceof LivingEntity caster) {
                        var num = DamageConversion.tryConvert(info, event.source, caster, event.mob, event.damage);
                        event.damage = num;

                        if (num <= 0) {
                            event.canceled = true;
                        }
                    }
                } else {

                    LivingHurtUtils.tryAttack(info);
                    var duck = (DamageSourceDuck) event.source;
                    duck.tryOverrideDmgWithMns(event);
                }
            }
        } finally {
            lock = false;
        }
    }

}
