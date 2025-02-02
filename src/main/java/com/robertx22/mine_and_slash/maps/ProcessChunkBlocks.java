package com.robertx22.mine_and_slash.maps;


import com.robertx22.library_of_exile.main.ExileLog;
import com.robertx22.mine_and_slash.capability.world.WorldData;
import com.robertx22.mine_and_slash.config.forge.ServerContainer;
import com.robertx22.mine_and_slash.database.data.league.LeagueMechanic;
import com.robertx22.mine_and_slash.database.registry.ExileDB;
import com.robertx22.mine_and_slash.maps.dungeon_generation.BuiltRoom;
import com.robertx22.mine_and_slash.maps.dungeon_generation.ChunkProcessData;
import com.robertx22.mine_and_slash.maps.dungeon_generation.DungeonBuilder;
import com.robertx22.mine_and_slash.maps.dungeon_generation.DungeonRoomPlacer;
import com.robertx22.mine_and_slash.maps.processors.DataProcessor;
import com.robertx22.mine_and_slash.maps.processors.DataProcessors;
import com.robertx22.mine_and_slash.maps.processors.league.LeagueSpawnPos;
import com.robertx22.mine_and_slash.tags.imp.DungeonTag;
import com.robertx22.mine_and_slash.uncommon.datasaving.Load;
import com.robertx22.mine_and_slash.uncommon.localization.Chats;
import com.robertx22.mine_and_slash.uncommon.utilityclasses.WorldUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;

import java.util.ArrayList;
import java.util.List;

public class ProcessChunkBlocks {


    private static void logRoomForPos(Level world, BlockPos pos) {

        try {
            ChunkPos cpos = new ChunkPos(pos);

            DungeonBuilder builder = new DungeonBuilder(DungeonBuilder.mineAndSlashDungeonSettings(world, cpos)); // todo
            builder.build();
            BuiltRoom room = builder.builtDungeon.getRoomForChunk(cpos);

            ExileLog.get().log("Room affected: " + room.getStructure()
                    .toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void process(Player p, ServerLevel level, BlockPos pos) {

        try {
            if (level.isClientSide) {
                return;
            }
            if (WorldUtils.isMapWorldClass(level)) {
                WorldData mapdata = Load.worldData(level);

                ChunkPos start = new ChunkPos(pos);

                var opt = mapdata.map.getMap(start);

                if (!opt.isPresent()) {
                    return;
                }

                List<ChunkPos> terrainChunks = new ArrayList<>();
                terrainChunks.add(start);
                List<ChunkPos> mobChunks = new ArrayList<>();
                mobChunks.add(start);

                int terrain = ServerContainer.get().MAP_GEN_TERRAIN_RADIUS.get();
                int mob = ServerContainer.get().MAP_GEN_MOB_RADIUS.get();

                for (int x = -terrain; x < terrain; x++) {
                    for (int z = -terrain; z < terrain; z++) {
                        terrainChunks.add(new ChunkPos(start.x + x, start.z + z));
                    }
                }
                for (int x = -mob; x < mob; x++) {
                    for (int z = -mob; z < mob; z++) {
                        mobChunks.add(new ChunkPos(start.x + x, start.z + z));
                    }
                }

                int gened = 0;

                int maxtogen = 3;

                DungeonBuilder builder = new DungeonBuilder(DungeonBuilder.mineAndSlashDungeonSettings(level, start));
                builder.build();

                var map = Load.mapAt(level, pos);

                if (map == null || map.map == null) {
                    return;
                }

                map.dungeonid = builder.dungeon.GUID();

                if (map.mobs.isEmpty()) {
                    var mobs = ExileDB.MapMobs().getFilterWrapped(x -> builder.dungeon.tags.containsAny(x.possible_dungeon_tags.getTags(DungeonTag.SERIALIZER))).random();
                    map.mobs = mobs.GUID();
                    map.rooms.rooms.total = builder.builtDungeon.amount;
                }

                for (ChunkPos cpos : terrainChunks) {
                    if (!level.hasChunk(cpos.x, cpos.z)) {
                        continue;
                    }
                    ChunkAccess c = level.getChunk(cpos.x, cpos.z);

                    if (c instanceof LevelChunk chunk) {

                        var chunkdata = Load.chunkData(chunk);

                        if (!chunkdata.generatedTerrain) {
                            chunkdata.generatedTerrain = true;

                            DungeonRoomPlacer.place(opt.get(), level, level.getRandom(), cpos.getBlockAt(0, 0, 0));

                            //BuiltRoom room = builder.builtDungeon.getRoomForChunk(cpos);

                            for (LeagueMechanic mech : opt.get().leagues.getLeagueMechanics()) {
                                if (mech.gensRightAway(map)) {
                                    mech.getStructure(map.map).tryGenerate(level, cpos);
                                    // todo maybe this is genning mobs too soon outside the league content??
                                    leagueSpawn(level, chunk); // for league mechanics we instantly gen the data because we need to know the spawn pos, which is gained by processing the spawn block..
                                }
                            }
                            gened++;
                            if (gened >= maxtogen) {
                                return;
                            }
                        }
                    }
                }

                for (ChunkPos cpos : mobChunks) {
                    if (!level.hasChunk(cpos.x, cpos.z)) {
                        continue;
                    }
                    ChunkAccess c = level.getChunk(cpos.x, cpos.z);

                    if (c instanceof LevelChunk chunk) {

                        var chunkdata = Load.chunkData(chunk);

                        if (!chunkdata.generatedMobs) {

                            chunkdata.generatedMobs = true;

                            BuiltRoom room = builder.builtDungeon.getRoomForChunk(cpos);

                            // this will gen both the league mechs and the dungeon if it runs after the league mechs gen
                            generateData(level, chunk);

                            map.leagues.processedChunks++;

                            if (!room.room.isBarrier) {
                                map.rooms.addRoom(cpos);
                                map.rooms.rooms.done++;

                                var color = ChatFormatting.LIGHT_PURPLE;
                                var tc = ChatFormatting.YELLOW;

                                for (ServerPlayer player : level.getPlayers(x -> {
                                    return MapData.getStartChunk(new ChunkPos(x.blockPosition()), MapData.DUNGEON_LENGTH).equals(MapData.getStartChunk(start, MapData.DUNGEON_LENGTH));
                                })) {
                                    player.sendSystemMessage(Chats.EXPLORED_X_MAP_ROOMS.locName(
                                            Component.literal(map.rooms.rooms.done + "").withStyle(tc),
                                            Component.literal(map.rooms.rooms.total + "").withStyle(tc)
                                    ).withStyle(color));

                                    if (map.rooms.isDoneGenerating()) {
                                        player.sendSystemMessage(Chats.MAP_FINISHED_SPAWNING.locName().withStyle(ChatFormatting.DARK_PURPLE));
                                        // player.sendSystemMessage(Chats.TOTAL_MOBS.locName(map.rooms.mobs.done, map.rooms.mobs.total).withStyle(color));
                                        // player.sendSystemMessage(Chats.TOTAL_CHESTS.locName(map.rooms.chests.done, map.rooms.chests.total).withStyle(color));
                                    }
                                }

                            }
                        }

                        if (p.tickCount % 40 == 0) {
                            MobUnloading.loadBackMobs(level, cpos);
                        }
                    }


                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void generateData(ServerLevel level, LevelChunk chunk) {


        ChunkProcessData data = new ChunkProcessData(chunk);


        for (BlockPos tilePos : chunk.getBlockEntitiesPos()) {

            BlockEntity tile = level.getBlockEntity(tilePos);
            var text = DataProcessor.getData(tile);
            if (!text.isEmpty()) {


                boolean any = false;

                // todo make this work on either signs or these blocks

                for (DataProcessor processor : DataProcessors.getAll()) {
                    boolean did = processor.process(text, tilePos, level, data);
                    if (did) {
                        any = true;
                    }
                }

                if (!any) {
                    // todo do i just summon mobs when the tag fails?
                }

                if (any) {
                    // only set to air if the processor didnt turn it into another block
                    if (level.getBlockState(tilePos).getBlock() == Blocks.STRUCTURE_BLOCK || level.getBlockState(tilePos).getBlock() == Blocks.COMMAND_BLOCK) {
                        level.removeBlockEntity(tilePos);
                        level.setBlock(tilePos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL); // delete data block
                    }

                } else {
                    ExileLog.get().warn("Data block with tag: " + text + " matched no processors! " + tilePos.toString());
                    logRoomForPos(level, tilePos);
                }
            }


        }
    }

    public static void leagueSpawn(ServerLevel level, LevelChunk chunk) {


        ChunkProcessData data = new ChunkProcessData(chunk);


        for (BlockPos tilePos : chunk.getBlockEntitiesPos()) {

            BlockEntity tile = level.getBlockEntity(tilePos);
            var text = DataProcessor.getData(tile);
            if (!text.isEmpty()) {
                boolean any = false;

                // todo make this work on either signs or these blocks
                DataProcessor processor = new LeagueSpawnPos();
                boolean did = processor.process(text, tilePos, level, data);
                if (did) {
                    any = true;
                }


                if (any) {
                    // only set to air if the processor didnt turn it into another block
                    if (level.getBlockState(tilePos).getBlock() == Blocks.STRUCTURE_BLOCK || level.getBlockState(tilePos).getBlock() == Blocks.COMMAND_BLOCK) {
                        level.setBlock(tilePos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL); // delete data block
                        level.removeBlockEntity(tilePos);
                    }

                } else {
                    //  ExileLog.get().log("Data block with tag: " + text + " matched no processors! " + tilePos.toString());
                    //logRoomForPos(level, tilePos);
                }
            }


        }
    }
}