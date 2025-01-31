package com.robertx22.mine_and_slash.vanilla_mc.new_commands;

import com.mojang.brigadier.CommandDispatcher;
import com.robertx22.library_of_exile.command_wrapper.*;
import com.robertx22.mine_and_slash.database.registry.ExileRegistryTypes;
import com.robertx22.mine_and_slash.maps.DungeonRoom;
import com.robertx22.mine_and_slash.maps.dungeon_generation.RoomType;
import com.robertx22.mine_and_slash.maps.dungeon_reg.Dungeon;
import com.robertx22.mine_and_slash.vanilla_mc.commands.CommandRefs;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.StructureUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.StructureBlockEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BuilderToolCommands {
    public static void reg(CommandDispatcher dis) {
        CommandBuilder.of(CommandRefs.ID, dis, b -> {
            PlayerWrapper enarg = new PlayerWrapper();
            var RADIUS = new IntWrapper("radius");
            var HEIGHT = new IntWrapper("height");

            b.addLiteral("builder_tool_warning", PermWrapper.OP);
            b.addLiteral("generate_structure_blocks_per_chunk", PermWrapper.OP);
            b.addArg(RADIUS);
            b.addArg(HEIGHT);

            b.addArg(enarg);

            b.action(e -> {

                Player p = enarg.get(e);

                if (!p.isCreative()) {
                    p.sendSystemMessage(Component.literal("You must be in creative mode to use this command. This is extra safety to make sure this command isn't usable accidentally."));
                    return;
                }
                ServerLevel world = (ServerLevel) p.level();

                BlockPos pos = p.blockPosition();
                int radius = RADIUS.get(e);
                int height = HEIGHT.get(e);
                ChunkPos cp = new ChunkPos(pos);

                for (int x = 0; x < radius; x++) {
                    for (int z = 0; z < radius; z++) {
                        var cpos = new ChunkPos(cp.x + x, cp.z + z);
                        var fpos = cpos.getBlockAt(0, pos.getY(), 0);
                        //world.setBlock(fpos, Blocks.STRUCTURE_BLOCK.defaultBlockState(), 2);
                        StructureUtils.createNewEmptyStructureBlock(x + "_" + z, fpos, new BlockPos(16, height, 16), Rotation.NONE, world);
                    }
                }
            });

        }, "Gens structure blocks in radius for every chunk");

        CommandBuilder.of(CommandRefs.ID, dis, x -> {
            PlayerWrapper enarg = new PlayerWrapper();
            var DUNGEON = new RegistryWrapper<Dungeon>(ExileRegistryTypes.DUNGEON);

            x.addLiteral("builder_tool_warning", PermWrapper.OP);
            x.addLiteral("generate_dungeon_pieces", PermWrapper.OP);
            x.addArg(DUNGEON);

            x.addArg(enarg);

            x.action(e -> {

                Player p = enarg.get(e);

                if (!p.isCreative()) {
                    p.sendSystemMessage(Component.literal("You must be in creative mode to use this command. This is extra safety to make sure this command isn't usable accidentally."));
                    return;
                }
                var world = p.level();

                BlockPos pos = p.blockPosition();
                Dungeon dungeon = DUNGEON.getFromRegistry(e);
                ChunkPos cp = new ChunkPos(pos);

                int i = 0;
                int z = 0;
                for (RoomType type : RoomType.values()) {
                    z = 0;
                    for (String room : dungeon.getRoomList(type)) {
                        var aroom = new DungeonRoom(dungeon, room, type);
                        var roomPos = new BlockPos(cp.getMinBlockX() + (i * 20), pos.getY(), cp.getMaxBlockZ() + (z * 20));

                        world.setBlock(roomPos, Blocks.STRUCTURE_BLOCK.defaultBlockState(), 2);

                        if (world.getBlockEntity(roomPos) instanceof StructureBlockEntity be) {
                            be.setStructureName(aroom.loc);
                            be.setStructurePos(new BlockPos(0, 0, 0).above().north());
                            be.loadStructure((ServerLevel) world);
                        }

                        z++;
                    }
                    i++;
                }

                p.sendSystemMessage(Component.literal("Use the load_nearby_structures command if you want to load them too."));


            });

        }, "Gens all dungeon pieces sorting them by type");

        CommandBuilder.of(CommandRefs.ID, dis, x -> {
            PlayerWrapper enarg = new PlayerWrapper();

            x.addLiteral("builder_tool_warning", PermWrapper.OP);
            x.addLiteral("load_nearby_structures", PermWrapper.OP);

            x.addArg(enarg);

            x.action(e -> {

                Player p = enarg.get(e);

                if (!p.isCreative()) {
                    p.sendSystemMessage(Component.literal("You must be in creative mode to use this command. This is extra safety to make sure this command isn't usable accidentally."));
                    return;
                }

                var world = p.level();


                List<ChunkPos> terrainChunks = new ArrayList<>();
                terrainChunks.add(new ChunkPos(p.blockPosition()));
                ChunkPos start = new ChunkPos(p.blockPosition());

                int terrain = 5;

                for (int i = -terrain; i < terrain; i++) {
                    for (int z = -terrain; z < terrain; z++) {
                        terrainChunks.add(new ChunkPos(start.x + i, start.z + z));
                    }
                }

                for (ChunkPos cp : terrainChunks) {
                    var bes = new HashMap<>(world.getChunk(cp.x, cp.z).getBlockEntities());

                    for (Map.Entry<BlockPos, BlockEntity> en : bes.entrySet()) {
                        if (en.getValue() instanceof StructureBlockEntity be) {
                            be.loadStructure((ServerLevel) world);
                        }
                    }
                }

            });

        }, "Loads all structure blocks");
    }
}
