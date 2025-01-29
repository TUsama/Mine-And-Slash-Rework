package com.robertx22.mine_and_slash.maps.room_adders;


import com.robertx22.mine_and_slash.maps.dungeon_generation.RoomType;

public class StoneBrickRoomAdder extends BaseRoomAdder {

    public StoneBrickRoomAdder() {
        super();
    }

    @Override
    public void addAllRooms() {

        add("drowned_puzzle", RoomType.CURVED_HALLWAY);

        add("dark_lever", RoomType.FOUR_WAY);
        add("hidden_ceiling", RoomType.FOUR_WAY);

        add("crypt", RoomType.STRAIGHT_HALLWAY);
        add("fast_or_slow", RoomType.STRAIGHT_HALLWAY);

        add("shooting_gallery", RoomType.CURVED_HALLWAY);
        add("throne_boss", RoomType.END);
        add("loot_behind_portal", RoomType.ENTRANCE);
        add("blocked_spiders", RoomType.STRAIGHT_HALLWAY);

        add("hidden_button2", RoomType.TRIPLE_HALLWAY);
        add("boss_trap", RoomType.STRAIGHT_HALLWAY);
        add("trader", RoomType.END);

        add("slime_puzzle", RoomType.CURVED_HALLWAY);
        add("hidden_lever0", RoomType.END);
        add("simple1", RoomType.FOUR_WAY);
        add("simple2", RoomType.STRAIGHT_HALLWAY);
        add("puzzle_plus_web", RoomType.TRIPLE_HALLWAY);

        add("basic", RoomType.FOUR_WAY);
        add("basic", RoomType.ENTRANCE);
        add("boss", RoomType.END);
        add("parkour", RoomType.TRIPLE_HALLWAY);
        add("basic", RoomType.CURVED_HALLWAY);
        add("basic", RoomType.STRAIGHT_HALLWAY);

        add("simple_trap0", RoomType.CURVED_HALLWAY);
        add("simple_prison0", RoomType.END);
        add("simple0", RoomType.FOUR_WAY);
        add("simple0", RoomType.STRAIGHT_HALLWAY);
        add("simple_maze0", RoomType.TRIPLE_HALLWAY);

    }
}
