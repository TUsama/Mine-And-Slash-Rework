package com.robertx22.mine_and_slash.maps.dungeon_reg;

import com.robertx22.library_of_exile.registry.ExileRegistryType;
import com.robertx22.library_of_exile.registry.IAutoGson;
import com.robertx22.library_of_exile.registry.JsonExileRegistry;
import com.robertx22.library_of_exile.utils.RandomUtils;
import com.robertx22.mine_and_slash.database.registry.ExileDB;
import com.robertx22.mine_and_slash.database.registry.ExileRegistryTypes;
import com.robertx22.mine_and_slash.maps.DungeonRoom;
import com.robertx22.mine_and_slash.maps.generator.RoomType;
import com.robertx22.mine_and_slash.maps.room_adders.BaseRoomAdder;
import com.robertx22.mine_and_slash.mmorpg.MMORPG;
import com.robertx22.mine_and_slash.mmorpg.SlashRef;
import com.robertx22.mine_and_slash.tags.TagList;
import com.robertx22.mine_and_slash.tags.all.DungeonTags;
import com.robertx22.mine_and_slash.tags.imp.DungeonTag;
import com.robertx22.mine_and_slash.uncommon.interfaces.IAutoLocName;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class Dungeon implements IAutoGson<Dungeon>, JsonExileRegistry<Dungeon>, IAutoLocName {

    public static Dungeon SERIALIZER = new Dungeon();

    public String id = "";
    public int weight = 1000;
    public String name = "";

    public boolean can_be_main = true;


    public TagList<DungeonTag> tags = new TagList<DungeonTag>(Arrays.asList(DungeonTags.DEFAULT));

    public List<String> entrances = new ArrayList<>();
    public List<String> four_ways = new ArrayList<>();
    public List<String> straight_hallways = new ArrayList<>();
    public List<String> curved_hallways = new ArrayList<>();
    public List<String> triple_hallway = new ArrayList<>();
    public List<String> ends = new ArrayList<>();

    public List<String> other_tileset = new ArrayList<>();

    public float other_tileset_chance = 5;

    public boolean allowsOtherTilesets() {
        return !other_tileset.isEmpty();
    }

    public List<Dungeon> getPossibleOtherTilesets() {

        var list = other_tileset.stream().map(x -> ExileDB.Dungeons().get(x)).collect(Collectors.toList());

        return list;
    }


    public Dungeon getFallbackGroup(Random rand) {
        if (!this.allowsOtherTilesets()) {
            return ExileDB.Dungeons().get("misc");
        } else {
            return RandomUtils.weightedRandom(getPossibleOtherTilesets(), rand.nextDouble());
        }
    }

    private transient List<DungeonRoom> rooms = new ArrayList<>();


    public List<DungeonRoom> getRooms() {
        if (rooms.isEmpty()) {
            addRooms(RoomType.ENTRANCE, entrances);
            addRooms(RoomType.FOUR_WAY, four_ways);
            addRooms(RoomType.STRAIGHT_HALLWAY, straight_hallways);
            addRooms(RoomType.CURVED_HALLWAY, curved_hallways);
            addRooms(RoomType.TRIPLE_HALLWAY, triple_hallway);
            addRooms(RoomType.END, ends);
        }
        return rooms;
    }

    public List<DungeonRoom> getRoomsOfType(RoomType type) {
        return getRooms()
                .stream()
                .filter(x -> x.type.equals(type)).collect(Collectors.toList());

    }

    public List<String> getRoomList(RoomType type) {

        if (type == RoomType.END) {
            return ends;
        }
        if (type == RoomType.ENTRANCE) {
            return entrances;
        }
        if (type == RoomType.FOUR_WAY) {
            return four_ways;
        }
        if (type == RoomType.CURVED_HALLWAY) {
            return curved_hallways;
        }
        if (type == RoomType.STRAIGHT_HALLWAY) {
            return straight_hallways;
        }
        if (type == RoomType.TRIPLE_HALLWAY) {
            return triple_hallway;
        }

        return Arrays.asList();
    }


    private void addRooms(RoomType type, List<String> list) {
        for (String room : list) {
            DungeonRoom b = new DungeonRoom(this, room, type);
            this.rooms.add(b);
        }
    }

    public final boolean hasRoomFor(RoomType type) {
        return getRooms()
                .stream()
                .anyMatch(x -> x.type.equals(type));

    }

    @Override
    public ExileRegistryType getExileRegistryType() {
        return ExileRegistryTypes.DUNGEON;
    }

    @Override
    public Class<Dungeon> getClassForSerialization() {
        return Dungeon.class;
    }

    @Override
    public String GUID() {
        return id;
    }

    @Override
    public int Weight() {
        return weight;
    }

    @Override
    public AutoLocGroup locNameGroup() {
        return AutoLocGroup.DUNGEON;
    }

    @Override
    public String locNameLangFileGUID() {
        return SlashRef.MODID + ".mmorpg." + GUID();
    }

    @Override
    public String locNameForLangFile() {
        return name;
    }

    public static class Builder {

        Dungeon dungeon = new Dungeon();

        public static Builder of(String id, String name, BaseRoomAdder adder) {
            Builder b = new Builder();
            b.dungeon.id = id;
            b.dungeon.name = name;
            adder.addRoomsToDungeon(b.dungeon);
            return b;
        }

        public Builder weight(int w) {
            this.dungeon.weight = w;
            return this;
        }

        public Builder tags(DungeonTag... tags) {
            this.dungeon.tags.addAll(Arrays.asList(tags));
            return this;
        }

        public Builder setIsOnlyAsAdditionalRooms() {
            this.dungeon.can_be_main = false;
            return this;
        }

        public Dungeon build() {
            dungeon.addToSerializables(MMORPG.SERIAZABLE_REGISTRATION_INFO);
            return dungeon;
        }


    }
}
