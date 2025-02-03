package com.robertx22.mine_and_slash.database.data.league;

import com.robertx22.mine_and_slash.loot.LootInfo;
import com.robertx22.mine_and_slash.maps.LeagueData;
import com.robertx22.mine_and_slash.maps.MapData;
import com.robertx22.mine_and_slash.maps.MapItemData;
import com.robertx22.mine_and_slash.mechanics.base.LeagueBlockData;
import com.robertx22.mine_and_slash.mechanics.base.LeagueControlBlockEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

// todo rework mechanics and make each mod implement their own mechanic!
// designate dimensions/structures as ones that give mechanics and ones that dont spawn them
public class ObeliskLeague extends LeagueMechanic {
    @Override
    public LeagueStructure getStructure(MapItemData map) {
        return LeagueStructure.EMPTY;
    }

    @Override
    public int getDefaultSpawns() {
        return 1;
    }

    @Override
    public float getBaseSpawnChance() {
        return 20;
    }

    @Override
    public void onMapStartSetup(LeagueData data) {

    }

    @Override
    public void onKillMob(MapData map, LootInfo info) {

    }

    @Override
    public void spawnMechanicInMap(ServerLevel level, BlockPos pos) {
        var block = BuiltInRegistries.BLOCK.get(new ResourceLocation("ancient_obelisks:obelisk"));
        level.setBlock(pos, block.defaultBlockState(), Block.UPDATE_ALL);
    }

    @Override
    public void onTick(MapData map, ServerLevel level, BlockPos pos, LeagueControlBlockEntity be, LeagueBlockData data) {

    }

    @Override
    public Block getTeleportBlock() {
        return Blocks.AIR;
    }

    @Override
    public ChatFormatting getTextColor() {
        return ChatFormatting.LIGHT_PURPLE;
    }

    @Override
    public String locNameForLangFile() {
        return "Ancient Obelisk";
    }

    @Override
    public String GUID() {
        return "ancient_obelisk";
    }

    @Override
    public int Weight() {
        return 1000;
    }
}
