package com.robertx22.mine_and_slash.maps.dungeon_generation;


import com.robertx22.library_of_exile.utils.RandomUtils;
import com.robertx22.mine_and_slash.config.forge.ServerContainer;
import com.robertx22.mine_and_slash.database.registry.ExileDB;
import com.robertx22.mine_and_slash.maps.DungeonRoom;
import com.robertx22.mine_and_slash.maps.MapData;
import com.robertx22.mine_and_slash.maps.dungeon_reg.Dungeon;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelAccessor;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DungeonBuilder {

    public static Settings mineAndSlashDungeonSettings(LevelAccessor level, ChunkPos pos) {
        return new Settings(
                createRandom(level.getServer().getWorldData().worldGenOptions().seed(), pos),
                ServerContainer.get().MIN_MAP_ROOMS.get(),
                ServerContainer.get().MAX_MAP_ROOMS.get(),
                ExileDB.Dungeons().getFilterWrapped(x -> x.can_be_main).list);
    }

    public static class Settings {

        public Random ran;
        public int minRooms;
        public int maxRooms;
        public List<Dungeon> possibleDungeons;

        public Settings(Random ran, int minRooms, int maxRooms, List<Dungeon> possibleDungeons) {
            this.ran = ran;
            this.minRooms = minRooms;
            this.maxRooms = maxRooms;
            this.possibleDungeons = possibleDungeons;
        }
    }

    public static Random createRandom(long worldSeed, ChunkPos cpos) {
        int chunkX = MapData.getStartChunk(cpos.getMiddleBlockPosition(55), MapData.DUNGEON_LENGTH).x;
        int chunkZ = MapData.getStartChunk(cpos.getMiddleBlockPosition(55), MapData.DUNGEON_LENGTH).z;
        long newSeed = (worldSeed + (long) (chunkX * chunkX * 4987142) + (long) (chunkX * 5947611) + (long) (chunkZ * chunkZ) * 4392871L + (long) (chunkZ * 389711) ^ worldSeed);
        return new Random(newSeed);
    }

    // random must be deterministic, 1 dungeon = 1 random
    public DungeonBuilder(Settings settings) {
        this.rand = settings.ran;
        this.dungeon = RandomUtils.weightedRandom(settings.possibleDungeons, rand.nextDouble());
        this.size = RandomUtils.RandomRange(settings.minRooms, settings.maxRooms, rand);

    }


    public Dungeon dungeon;
    public BuiltDungeon builtDungeon;
    public final Random rand;
    public int size;


    public void build() {
        builtDungeon = new BuiltDungeon(size, this);
        builtDungeon.setupBarriers();
        setupEntrance();
        builtDungeon.fillWithBarriers();
    }


    public RoomRotation random(List<RoomRotation> list) {
        return RandomUtils.weightedRandom(list, rand.nextDouble());
    }

    private void setupEntrance() {
        DungeonRoom entranceRoom = RoomType.ENTRANCE.getRandomRoom(dungeon, this);
        List<RoomRotation> possible = new ArrayList<>();
        possible.addAll(RoomType.ENTRANCE.getRotations());
        RoomRotation rotation = random(possible);
        BuiltRoom entrance = new BuiltRoom(this.dungeon, rotation, entranceRoom);
        int mid = builtDungeon.getMiddle();
        builtDungeon.addRoom(mid, mid, entrance);
    }

}