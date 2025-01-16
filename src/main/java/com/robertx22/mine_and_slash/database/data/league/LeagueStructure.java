package com.robertx22.mine_and_slash.database.data.league;

import com.robertx22.library_of_exile.main.ExileLog;
import com.robertx22.library_of_exile.util.ExplainedResult;
import com.robertx22.library_of_exile.utils.TeleportUtils;
import com.robertx22.mine_and_slash.database.registry.ExileDB;
import com.robertx22.mine_and_slash.maps.MapItemData;
import com.robertx22.mine_and_slash.mmorpg.ModErrors;
import com.robertx22.mine_and_slash.uncommon.datasaving.Load;
import com.robertx22.mine_and_slash.uncommon.localization.Chats;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import org.codehaus.plexus.util.StringUtils;

import java.util.Arrays;

public abstract class LeagueStructure {

    public static LeagueStructure EMPTY = new LeagueStructure(LeagueMechanics.NONE) {


        @Override
        public LeaguePiecesList getPieces(MapItemData map) {
            return new LeaguePiecesList(Arrays.asList());
        }

        @Override
        public int startY() {
            return 0;
        }

        @Override
        public boolean isInsideLeague(ServerLevel level, BlockPos pos) {
            return false;
        }
    };

    public LeagueMechanic league;

    public LeagueStructure(LeagueMechanic league) {
        this.league = league;
    }

    public static LeagueMechanic getMechanicFromPosition(ServerLevel sw, BlockPos pos) {

        var md = Load.mapAt(sw, pos);
        var map = md.map;

        var list = ExileDB.LeagueMechanics().getFilterWrapped(x -> x.getStructure(map) != null && x.getStructure(map).isInsideLeague(sw, pos)).list;

        if (!list.isEmpty()) {
            if (list.size() > 1) {
                throw new RuntimeException("Can't have more than 1 league structure in same position! " + StringUtils.join(list.iterator(), ","));
            }
            return list.get(0);
        }

        return LeagueMechanics.NONE;
    }


    public abstract LeaguePiecesList getPieces(MapItemData map);


    public abstract int startY();

    public int getYSize() {
        return 30;
    }

    public boolean isInsideLeague(ServerLevel level, BlockPos pos) {
        return pos.getY() >= startY() && pos.getY() <= (startY() + getYSize());
    }

    public final void tryGenerate(ServerLevel level, ChunkPos pos) {
        try {
            var md = Load.mapAt(level, pos.getBlockAt(0, 0, 0));

            if (md == null || md.map == null) {
                return;
            }

            var map = md.map;

            var list = getPieces(map);

            if (!getPieces(map).list.isEmpty()) {

                var s = md.leagues.get(league).map.get(this.league.getStructureId());

                LeagueStructurePieces pieces = list.get(s);

                var room = pieces.getRoomForChunk(pos, this);
                if (room != null) {
                    if (!getPieces(map).list.isEmpty()) {
                        generateStructure(level, level.getStructureManager(), room, pos);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean generateStructure(ServerLevelAccessor level, StructureTemplateManager man, ResourceLocation room, ChunkPos cpos) {


        try {

            var opt = man.get(room);
            if (opt.isPresent()) {
                var template = opt.get();
                StructurePlaceSettings settings = new StructurePlaceSettings().setMirror(Mirror.NONE).setIgnoreEntities(false);

                settings.setBoundingBox(settings.getBoundingBox());

                BlockPos position = cpos.getBlockAt(0, startY(), 0);

                if (template == null) {
                    ExileLog.get().warn("FATAL ERROR: Structure does not exist (" + room.toString() + ")");
                    return false;
                }
                settings.setRotation(Rotation.NONE);

                template.placeInWorld(level, position, position, settings, level.getRandom(), Block.UPDATE_CLIENTS | Block.UPDATE_KNOWN_SHAPE);
            }

        } catch (Exception e) {
            ModErrors.print(e);
            return false;
        }

        return true;
    }

    public static final ExplainedResult canTeleportToLeagueStart(Player p, LeagueMechanic league) {
        var map = Load.mapAt(p.level(), p.blockPosition());

        if (map != null) {
            var lo = map.leagues.get(league).spawn_pos;
            if (lo != 0L) {
                return ExplainedResult.success();
            } else {
                return ExplainedResult.failure(Component.literal("League Structure didn't spawn yet or at all. Teleport failed. Try Exploring more of the map"));

            }
        } else {
            return ExplainedResult.failure(Chats.NOT_INSIDE_MAP.locName());
        }

    }

    public final void teleportToStartOfLeague(Player p) {
        var map = Load.mapAt(p.level(), p.blockPosition());

        if (map != null) {
            var lo = map.leagues.get(league).spawn_pos;
            var tp = BlockPos.of(lo);

            if (lo != 0L) {
                Load.player(p).map.tp_back_from_league_pos = p.blockPosition().asLong();
                TeleportUtils.teleport((ServerPlayer) p, tp);
            }
        }

        // BlockPos tp = getTeleportPos(p.blockPosition());
        //Load.playerRPGData(p).map.tp_back_from_league_pos = p.blockPosition().asLong();
        //TeleportUtils.teleport((ServerPlayer) p, tp);
    }

    public final void teleportBackToDungeon(Player p) {
        Load.player(p).map.teleportBackFromLeagueToDungeon(p);
    }
}
