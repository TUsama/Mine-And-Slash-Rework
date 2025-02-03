package com.robertx22.mine_and_slash.database.data.league;

import com.robertx22.mine_and_slash.content.ubers.UberMechanic;
import com.robertx22.mine_and_slash.loot.LootInfo;
import com.robertx22.mine_and_slash.maps.LeagueData;
import com.robertx22.mine_and_slash.maps.MapData;
import com.robertx22.mine_and_slash.maps.MapItemData;
import com.robertx22.mine_and_slash.mechanics.base.LeagueBlockData;
import com.robertx22.mine_and_slash.mechanics.base.LeagueControlBlockEntity;
import com.robertx22.mine_and_slash.mmorpg.MMORPG;
import com.robertx22.mine_and_slash.mmorpg.MnsAddons;
import com.robertx22.mine_and_slash.mmorpg.SlashRef;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public class LeagueMechanics {

    public static String HARVEST_ID = "harvest";
    public static String UBER_ID = "uber";
    public static String MAP_BOSS_ID = "map_boss";
    public static String MAP_REWARD_ID = "map_reward";

    public static LeagueMechanic NONE = new LeagueMechanic() {


        @Override
        public AutoLocGroup locNameGroup() {
            return AutoLocGroup.LEAGUE;
        }

        @Override
        public ChatFormatting getTextColor() {
            return ChatFormatting.WHITE;
        }

        @Override
        public String locNameLangFileGUID() {
            return SlashRef.MODID + ".league." + GUID();
        }

        @Override
        public String locNameForLangFile() {
            return "empty";
        }

        @Override
        public LeagueStructure getStructure(MapItemData map) {
            return LeagueStructure.EMPTY;
        }

        @Override
        public int getDefaultSpawns() {
            return 0;
        }

        @Override
        public void onMapStartSetup(LeagueData data) {

        }

        @Override
        public void onKillMob(MapData map, LootInfo info) {

        }

        @Override
        public void spawnMechanicInMap(ServerLevel level, BlockPos pos) {


        }

        @Override
        public boolean isEmpty() {
            return true;
        }

        @Override
        public void onTick(MapData map, ServerLevel level, BlockPos pos, LeagueControlBlockEntity be, LeagueBlockData data) {

        }

        @Override
        public Block getTeleportBlock() {
            return Blocks.AIR;
        }


        @Override
        public String GUID() {
            return "none";
        }

        @Override
        public int Weight() {
            return 0;
        }
    };

    public static LeagueMechanic HARVEST = new HarvestLeague();
    public static LeagueMechanic PROPHECY = new ProphecyLeague();
    public static UberMechanic UBER = new UberMechanic();
    public static MapBossLeague MAP_BOSS = new MapBossLeague();
    public static MapRewardLeague MAP_REWARD = new MapRewardLeague();

    public static void init() {
        UBER.registerToExileRegistry(MMORPG.HARDCODED_REGISTRATION_INFO);
        HARVEST.registerToExileRegistry(MMORPG.HARDCODED_REGISTRATION_INFO);
        PROPHECY.registerToExileRegistry(MMORPG.HARDCODED_REGISTRATION_INFO);
        MAP_BOSS.registerToExileRegistry(MMORPG.HARDCODED_REGISTRATION_INFO);
        MAP_REWARD.registerToExileRegistry(MMORPG.HARDCODED_REGISTRATION_INFO);

        // todo i think this will load after mods because its called in lib event
        if (MnsAddons.isObeliskLoaded()) {
            new ObeliskLeague().registerToExileRegistry(MMORPG.HARDCODED_REGISTRATION_INFO);
        }
    }


}
