package com.robertx22.age_of_exile.gui.screens.skill_tree.buttons.drawer;

import com.robertx22.age_of_exile.capability.player.PlayerData;
import com.robertx22.age_of_exile.database.data.perks.Perk;
import com.robertx22.age_of_exile.database.data.perks.PerkStatus;
import com.robertx22.age_of_exile.database.data.talent_tree.TalentTree;
import com.robertx22.age_of_exile.saveclasses.PointData;
import com.robertx22.age_of_exile.uncommon.datasaving.Load;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public record ButtonIdentifier(TalentTree tree, PointData point, Perk perk) {

    public ResourceLocation getCurrentButtonLocation(){
        PlayerData playerData = Load.player(Minecraft.getInstance().player);
        PerkStatus status = playerData.talents.getStatus(Minecraft.getInstance().player, tree, point);
        return PerkButtonPainter.getNewLocation(perk.type.getColorTexture(status), perk.type.getBorderTexture(status), perk.getIcon());
    }

    public HashMap<PerkStatus, List<ResourceLocation>> getAllNewLocation(){
        HashMap<PerkStatus, List<ResourceLocation>> perkStatusResourceLocationHashMap = new HashMap<>();
        ResourceLocation perkIcon = perk.getIcon();
        for (PerkStatus status : PerkStatus.values()) {
            ArrayList<ResourceLocation> resourceLocations = new ArrayList<>();
            ResourceLocation colorTexture = perk.type.getColorTexture(status);
            ResourceLocation borderTexture = perk.type.getBorderTexture(status);

            resourceLocations.add(colorTexture);
            resourceLocations.add(borderTexture);
            resourceLocations.add(perkIcon);
            perkStatusResourceLocationHashMap.put(status, resourceLocations);
        }

        return perkStatusResourceLocationHashMap;
    }
}
