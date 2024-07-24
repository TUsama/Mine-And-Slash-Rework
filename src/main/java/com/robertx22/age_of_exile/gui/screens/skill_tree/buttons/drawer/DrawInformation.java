package com.robertx22.age_of_exile.gui.screens.skill_tree.buttons.drawer;

import com.google.common.collect.ImmutableList;
import com.robertx22.age_of_exile.database.data.perks.Perk;
import com.robertx22.age_of_exile.database.data.perks.PerkStatus;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public record DrawInformation(Perk perk) {

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DrawInformation that = (DrawInformation) o;

        return Objects.equals(perk, that.perk);
    }

    @Override
    public int hashCode() {
        return perk != null ? perk.hashCode() : 0;
    }
}
