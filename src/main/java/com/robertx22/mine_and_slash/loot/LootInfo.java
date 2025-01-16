package com.robertx22.mine_and_slash.loot;

import com.robertx22.library_of_exile.events.base.ExileEvents;
import com.robertx22.mine_and_slash.capability.entity.EntityData;
import com.robertx22.mine_and_slash.database.data.league.LeagueMechanic;
import com.robertx22.mine_and_slash.database.data.league.LeagueMechanics;
import com.robertx22.mine_and_slash.database.data.league.LeagueStructure;
import com.robertx22.mine_and_slash.database.data.stats.types.loot.TreasureQuantity;
import com.robertx22.mine_and_slash.database.data.stats.types.misc.ExtraMobDropsStat;
import com.robertx22.mine_and_slash.database.registry.ExileDB;
import com.robertx22.mine_and_slash.loot.generators.BaseLootGen;
import com.robertx22.mine_and_slash.maps.MapData;
import com.robertx22.mine_and_slash.uncommon.datasaving.Load;
import com.robertx22.mine_and_slash.uncommon.utilityclasses.LevelUtils;
import com.robertx22.mine_and_slash.uncommon.utilityclasses.WorldUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.Optional;

public class LootInfo {

    private LootInfo(LootOrigin lootOrigin) {
        this.lootOrigin = lootOrigin;
    }

    public enum LootOrigin {
        CHEST, MOB, PLAYER, OTHER, LOOT_CRATE;
    }

    public int amount = 0;
    public int level = 0;
    public int map_tier = 0;

    public LootOrigin lootOrigin;
    public EntityData mobData;
    public EntityData playerEntityData;
    public LivingEntity mobKilled;
    public Player player;
    public Level world;
    private int minItems = 0;
    private int maxItems = 20;
    public boolean isMapWorld = false;
    public MapData map;
    public BlockPos pos;


    public LeagueMechanic league = LeagueMechanics.NONE;


    public int getMinItems() {
        return minItems;

    }


    public int getMaxItems() {
        return maxItems;
    }

    public static LootInfo ofMobKilled(Player player, LivingEntity mob) {

        LootInfo info = new LootInfo(LootOrigin.MOB);

        try {
            info.world = mob.level();
            info.mobData = Load.Unit(mob);
            info.playerEntityData = Load.Unit(player);
            info.mobKilled = mob;
            info.player = player;
            info.pos = mob.blockPosition();

            info.setupAllFields();


        } catch (Exception e) {
            e.printStackTrace();
        }

        return info;
    }

    public static LootInfo ofPlayer(Player player) {
        LootInfo info = new LootInfo(LootOrigin.PLAYER);
        info.world = player.level();
        info.pos = player.blockPosition();
        info.setupAllFields();
        return info;
    }

    public static LootInfo ofChestLoot(Player player, BlockPos pos) {
        LootInfo info = new LootInfo(LootOrigin.CHEST);
        info.player = player;
        info.world = player.level();
        info.pos = pos;
        info.maxItems = 7;
        info.setupAllFields();

        if (WorldUtils.isMapWorldClass(player.level())) {
            info.lootMods.add(new LootModifier(LootModifierEnum.MAP_CHEST, 10));
        } else {
            info.lootMods.add(new LootModifier(LootModifierEnum.CHEST, 5));
        }

        return info;
    }

    public static LootInfo ofDummyForClient(int level) {
        LootInfo info = new LootInfo(LootOrigin.OTHER);
        info.level = level;
        info.setupAllFields();
        return info;
    }

    public static LootInfo ofLevel(int level) {
        LootInfo info = new LootInfo(LootOrigin.OTHER);
        info.level = level;
        info.setupAllFields();
        return info;
    }


    public static LootInfo ofSpawner(Player player, Level world, BlockPos pos) {
        LootInfo info = new LootInfo(LootOrigin.OTHER);
        info.world = world;
        info.pos = pos;
        info.player = player;
        info.setupAllFields();
        info.maxItems = 1;
        return info;
    }

    private void setupAllFields() {
        // order matters
        errorIfClient();
        setWorld();
        setTier();
        setLevel();

        if (player != null) {
            playerEntityData = Load.Unit(player);
        }
    }

    private LootInfo setTier() {
        if (map != null) {
            this.map_tier = map.map.tier;
        }


        return this;

    }


    private void setLevel() {
        if (level <= 0) {
            if (mobData != null) {
                level = mobData.getLevel();
            } else {
                level = LevelUtils.determineLevel(null, world, pos, player, false).getLevel();
            }
        }

    }

    private void errorIfClient() {
        if (world != null && world.isClientSide) {
            throw new RuntimeException("Can't use Loot Info on client side!!!");
        }
    }

    private void setWorld() {
        if (world != null) {
            if (WorldUtils.isMapWorldClass(world)) {

                Optional<MapData> data = Load.worldData(world).map.getMap(this.pos);

                if (data.get() != null) {
                    this.isMapWorld = true;
                    this.map = data.get();
                    this.league = LeagueStructure.getMechanicFromPosition((ServerLevel) world, pos);
                }

            }
        }
    }

    public LootModifiersList lootMods = new LootModifiersList();


    private boolean gatheredLootMultis = false;

    public void gatherLootMultipliers() {

        if (gatheredLootMultis) {
            return;
        }

        this.gatheredLootMultis = true;

        if (league != null && league == LeagueMechanics.MAP_REWARD) {
            try {
                lootMods.add(new LootModifier(LootModifierEnum.MAP_COMPLETITION_RARITY_REWARD, ExileDB.GearRarities().get(map.completion_rarity).map_reward.loot_multi));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (mobKilled != null && mobData != null) {
            if (this.playerEntityData != null) {
                // todo ideally this would be sepearate and per lootgen, as some have distance punishment and some dont. or maybe just leave it?
                lootMods.add(new LootModifier(LootModifierEnum.LEVEL_DISTANCE_PENALTY, LootUtils.getLevelDistancePunishmentMulti(mobData.getLevel(), playerEntityData.getLevel())));
            }

            lootMods.add(new LootModifier(LootModifierEnum.MOB_HEALTH, LootUtils.getMobHealthBasedLootMulti(mobKilled)));
            lootMods.add(new LootModifier(LootModifierEnum.MOB_DATAPACK, (float) ExileDB.getEntityConfig(mobKilled, this.mobData).loot_multi));
            lootMods.add(new LootModifier(LootModifierEnum.MOB_BONUS_LOOT_STAT, mobData.getUnit().getCalculatedStat(ExtraMobDropsStat.getInstance()).getMultiplier()));
            lootMods.add(new LootModifier(LootModifierEnum.MOB_RARITY, mobData.getMobRarity().LootMultiplier()));

        }

        if (this.playerEntityData != null) {

            if (playerEntityData.getLevel() < 10) {
                lootMods.add(new LootModifier(LootModifierEnum.LOW_LEVEL_BOOST, 2));

            }
            lootMods.add(new LootModifier(LootModifierEnum.FAVOR, Load.player(player).favor.getLootExpMulti()));

            if (lootOrigin != LootOrigin.LOOT_CRATE) {
                lootMods.add(new LootModifier(LootModifierEnum.PLAYER_LOOT_QUANTITY, playerEntityData.getUnit().getCalculatedStat(TreasureQuantity.getInstance()).getMultiplier()));
            }
        }

        if (world != null) {
            lootMods.add(new LootModifier(LootModifierEnum.DIMENSION_LOOT, ExileDB.getDimensionConfig(world).all_drop_multi));
        }

        if (this.isMapWorld) {
            lootMods.add(new LootModifier(LootModifierEnum.ADVENTURE_MAP, this.map.map.getBonusLootMulti()));
        } else {
            float chance = ExileEvents.SETUP_LOOT_CHANCE.callEvents(new ExileEvents.OnSetupLootChance(mobKilled, player, 1)).lootChance;
            lootMods.add(new LootModifier(LootModifierEnum.ANTI_MOB_FARM_MOD, chance));
        }

    }

    // todo this doesnt have to be done 10 times per mob kill, maybe just once
    public void setup(BaseLootGen gen) {

        float multiplicativeMod = 1F;

        this.gatherLootMultipliers();

        for (LootModifier mod : this.lootMods.all) {
            multiplicativeMod *= mod.multi;
        }

        float chance = gen.baseDropChance();

        if (gen.chanceIsModified()) {
            chance *= multiplicativeMod;
        }

        amount = LootUtils.WhileRoll(chance);

        if (gen.onlyOneDropAllowed()) {
            if (amount > 1) {
                amount = 1;
            }
        }
    }

}
