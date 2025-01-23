package com.robertx22.mine_and_slash.mmorpg.registers.common;

import com.robertx22.library_of_exile.deferred.RegObj;
import com.robertx22.mine_and_slash.database.data.spells.entities.*;
import com.robertx22.mine_and_slash.database.data.spells.summons.entity.SkeletonSummon;
import com.robertx22.mine_and_slash.database.data.spells.summons.entity.SpiderPet;
import com.robertx22.mine_and_slash.database.data.spells.summons.entity.WolfSummon;
import com.robertx22.mine_and_slash.database.data.spells.summons.entity.ZombieSummon;
import com.robertx22.mine_and_slash.database.data.spells.summons.entity.golems.ColdGolem;
import com.robertx22.mine_and_slash.database.data.spells.summons.entity.golems.FireGolem;
import com.robertx22.mine_and_slash.database.data.spells.summons.entity.golems.LightningGolem;
import com.robertx22.mine_and_slash.entity.minions.ExplodeMinion;
import com.robertx22.mine_and_slash.entity.minions.ThornyMinion;
import com.robertx22.mine_and_slash.mmorpg.registers.deferred_wrapper.Def;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

public class SlashEntities {


    public static RegObj<EntityType<SimpleProjectileEntity>> SIMPLE_PROJECTILE = projectile(SimpleProjectileEntity::new, "spell_projectile");
    public static RegObj<EntityType<SimpleArrowEntity>> SIMPLE_ARROW = projectile(SimpleArrowEntity::new, "spell_arrow");
    public static RegObj<EntityType<StationaryFallingBlockEntity>> SIMPLE_BLOCK_ENTITY = projectile(StationaryFallingBlockEntity::new, "spell_block_entity", false);
    public static RegObj<EntityType<SimpleTridentEntity>> SIMPLE_TRIDENT = projectile(SimpleTridentEntity::new, "spell_trident", false);
    public static RegObj<EntityType<AutoAimingProj>> AUTO_AIMING_SKELETON_SKULL = projectile(AutoAimingProj::new, "auto_aim_skull", false);


    // summons
    public static RegObj<EntityType<WolfSummon>> SPIRIT_WOLF = mob(WolfSummon::new, EntityType.WOLF, "spirit_wolf");
    public static RegObj<EntityType<ZombieSummon>> ZOMBIE = mob(ZombieSummon::new, EntityType.SKELETON, "zombie");
    public static RegObj<EntityType<SkeletonSummon>> SKELETON = mob(SkeletonSummon::new, EntityType.SKELETON, "skeleton");
    public static RegObj<EntityType<SpiderPet>> SPIDER = mob(SpiderPet::new, EntityType.CAVE_SPIDER, "spider");

    public static RegObj<EntityType<FireGolem>> FIRE_GOLEM = mob(FireGolem::new, EntityType.WOLF, "fire_golem");
    public static RegObj<EntityType<ColdGolem>> COLD_GOLEM = mob(ColdGolem::new, EntityType.WOLF, "cold_golem");
    public static RegObj<EntityType<LightningGolem>> LIGHTNING_GOLEM = mob(LightningGolem::new, EntityType.WOLF, "lightning_golem");


    //minions
    public static RegObj<EntityType<ThornyMinion>> THORNY_MINION = mob(ThornyMinion::new, EntityType.SKELETON, "thorny_minion");
    public static RegObj<EntityType<ExplodeMinion>> EXPLODE_MINION = mob(ExplodeMinion::new, EntityType.SKELETON, "explody_minion");


    private static <T extends Entity> RegObj<EntityType<T>> projectile(EntityType.EntityFactory<T> factory, String id) {
        return projectile(factory, id, true);

    }

    private static <T extends Entity> RegObj<EntityType<T>> mob(EntityType.EntityFactory<T> factory, EntityType like, String id) {

        RegObj<EntityType<T>> def = Def.entity(id, () -> EntityType.Builder.of(factory, MobCategory.MISC)
                .sized(like.getDimensions().width, like.getDimensions().height)
                .setTrackingRange(10)
                .build(id));


        return def;
    }

    private static <T extends Entity> RegObj<EntityType<T>> projectile(EntityType.EntityFactory<T> factory,
                                                                       String id, boolean itemRender) {

        RegObj<EntityType<T>> def = Def.entity(id, () -> EntityType.Builder.of(factory, MobCategory.MISC)
                .sized(0.5F, 0.5F)
                .setUpdateInterval(10)
                .setTrackingRange(4)
                .build(id));

        return def;
    }

    public static void init() {

    }

}


