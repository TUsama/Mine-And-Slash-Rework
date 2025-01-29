package com.robertx22.mine_and_slash.maps.room_adders;


import com.robertx22.mine_and_slash.maps.dungeon_generation.RoomType;

public class SandstoneRoomAdder extends BaseRoomAdder {

    public SandstoneRoomAdder() {
        super();
    }

    @Override
    public void addAllRooms() {

        add("boss_and_skellies", RoomType.END);
        add("treasure0", RoomType.FOUR_WAY);
        add("treasure_lava_pyramid", RoomType.STRAIGHT_HALLWAY);
        add("dorm0", RoomType.TRIPLE_HALLWAY);

        add("trap", RoomType.FOUR_WAY);
        add("basic", RoomType.ENTRANCE);
        add("boss", RoomType.END);
        add("cactus", RoomType.TRIPLE_HALLWAY);
        add("prison", RoomType.CURVED_HALLWAY);
        add("puzzle0", RoomType.STRAIGHT_HALLWAY);

        add("trap_puzzle0", RoomType.CURVED_HALLWAY);
        add("trap_prison0", RoomType.END);
        add("simple0", RoomType.STRAIGHT_HALLWAY);
        add("simple_maze0", RoomType.TRIPLE_HALLWAY);

        add("hidden_button0", RoomType.CURVED_HALLWAY);
        add("tnt_pyramid", RoomType.END);
        add("simple1", RoomType.FOUR_WAY);
        add("simple_magma0", RoomType.STRAIGHT_HALLWAY);
        add("parkour_puzzle_magma", RoomType.TRIPLE_HALLWAY);

    }
}

