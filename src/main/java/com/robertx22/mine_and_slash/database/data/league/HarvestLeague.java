package com.robertx22.mine_and_slash.database.data.league;

import com.robertx22.library_of_exile.utils.RandomUtils;
import com.robertx22.mine_and_slash.database.data.spells.map_fields.MapField;
import com.robertx22.mine_and_slash.loot.LootInfo;
import com.robertx22.mine_and_slash.maps.LeagueData;
import com.robertx22.mine_and_slash.maps.MapData;
import com.robertx22.mine_and_slash.maps.MapItemData;
import com.robertx22.mine_and_slash.maps.processors.helpers.MobBuilder;
import com.robertx22.mine_and_slash.mechanics.base.LeagueBlockData;
import com.robertx22.mine_and_slash.mechanics.base.LeagueControlBlockEntity;
import com.robertx22.mine_and_slash.mechanics.harvest.HarvestItems;
import com.robertx22.mine_and_slash.mmorpg.registers.common.SlashBlocks;
import com.robertx22.mine_and_slash.uncommon.localization.Chats;
import com.robertx22.mine_and_slash.uncommon.utilityclasses.OnScreenMessageUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

import java.util.Arrays;
import java.util.List;

public class HarvestLeague extends LeagueMechanic {

    public static MapField<Double> KILLS = new MapField<>("kills");

    public int ticksLast = 20 * 60 * 1;
    int maxKills = 200;
    int maximumBonusLootTimes = 30;

    @Override
    public int getDefaultSpawns() {
        return 1;
    }

    @Override
    public void onMapStartSetup(LeagueData data) {


    }

    @Override
    public Block getTeleportBlock() {
        return SlashBlocks.HARVEST_TELEPORT.get();
    }


    @Override
    public LeagueStructure getStructure(MapItemData map) {
        return new LeagueStructure(this) {

            @Override
            public LeaguePiecesList getPieces(MapItemData map) {
                return new LeaguePiecesList(Arrays.asList(
                        // todo this will be used by uber for now new LeagueStructurePieces(2, "harvest/river"),
                        new LeagueStructurePieces(2, "harvest/circle"),
                        new LeagueStructurePieces(2, "harvest/aztec"),
                        new LeagueStructurePieces(2, "harvest/theevent")
                ));
            }

            @Override
            public int startY() {
                return 85;
            }


        };
    }

    @Override
    public float getBaseSpawnChance() {
        return 30;
    }

    @Override
    public void onKillMob(MapData map, LootInfo info) {
        map.leagues.get(this).map.modify(HarvestLeague.KILLS, 0D, x -> x + 1D);


        float lootChance = (maximumBonusLootTimes * (100F / maxKills)); // i hope this formula isn't wrong

        if (RandomUtils.roll(lootChance)) {
            for (ItemStack stack : generateMobLoot(info)) {
                info.mobKilled.spawnAtLocation(stack, 1);
            }
        }
    }


    private List<ItemStack> generateMobLoot(LootInfo info) {

        Item seeditem = RandomUtils.randomFromList(Arrays.asList(HarvestItems.BLUE_INGOT, HarvestItems.GREEN_INGOT, HarvestItems.PURPLE_INGOT)).get();

        ItemStack seeds = new ItemStack(seeditem, RandomUtils.RandomRange(1, 5));

        return Arrays.asList(seeds);
    }

    @Override
    public void spawnMechanicInMap(ServerLevel level, BlockPos pos) {

        level.setBlock(pos, SlashBlocks.HARVEST_TELEPORT.get().defaultBlockState(), 2);
    }


    @Override
    public void onTick(MapData map, ServerLevel level, BlockPos pos, LeagueControlBlockEntity be, LeagueBlockData data) {

        data.ticks++;

        if (data.ticks > ticksLast || map.leagues.get(this).map.getOrDefault(KILLS, 0D).intValue() > maxKills) {
            data.finished = true;
            for (Player p : be.getPlayers()) {
                p.sendSystemMessage(Chats.VINES_SHRINK.locName().withStyle(ChatFormatting.GREEN));
            }
            return;
        } else {

            int secleft = (ticksLast - data.ticks) / 20;
            int maxsec = ticksLast / 20;

            for (Player p : be.getPlayers()) {
                OnScreenMessageUtils.actionBar((ServerPlayer) p, Component.literal(secleft + "/" + maxsec + "s Remaining").withStyle(ChatFormatting.RED));
            }

            if (data.ticks % 20 == 0) {

                int mobs = be.getMobs().size();

                if (mobs < 20) {

                    int tospawn = 4;

                    for (int i = 0; i < tospawn; i++) {

                        var type = getRandomMobToSpawn();
                        var spawnpos = be.getRandomMobSpawnPos(type);

                        if (spawnpos != null) {
                            for (LivingEntity mob : MobBuilder.of(getRandomMobToSpawn(), x -> {
                                x.amount = 1;
                            }).summonMobs(level, spawnpos)) {

                                if (RandomUtils.roll(25)) {
                                    mob.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 20 * 60, RandomUtils.RandomRange(1, 4)));
                                }
                            }
                        }

                    }
                }

            }

        }

    }

    @Override
    public ChatFormatting getTextColor() {
        return ChatFormatting.GREEN;
    }

    public EntityType getRandomMobToSpawn() {
        if (RandomUtils.roll(10)) {
            return EntityType.CAVE_SPIDER;
        }
        if (RandomUtils.roll(5)) {
            return EntityType.WITCH;
        }
        if (RandomUtils.roll(5)) {
            return EntityType.PIGLIN_BRUTE;
        }
        if (RandomUtils.roll(2)) {
            return EntityType.STRAY;
        }
        return EntityType.SPIDER;
    }


    @Override
    public String GUID() {
        return LeagueMechanics.HARVEST_ID;
    }

    @Override
    public int Weight() {
        return 1000;
    }

    @Override
    public String locNameForLangFile() {
        return "Harvest League";
    }
}
