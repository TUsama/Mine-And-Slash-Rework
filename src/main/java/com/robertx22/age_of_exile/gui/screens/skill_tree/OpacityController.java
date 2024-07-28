package com.robertx22.age_of_exile.gui.screens.skill_tree;

import com.google.common.util.concurrent.RateLimiter;
import com.robertx22.age_of_exile.capability.player.PlayerData;
import com.robertx22.age_of_exile.database.data.perks.Perk;
import com.robertx22.age_of_exile.database.data.perks.PerkStatus;
import com.robertx22.age_of_exile.database.data.talent_tree.TalentTree;
import com.robertx22.age_of_exile.gui.screens.skill_tree.buttons.PerkButton;
import com.robertx22.age_of_exile.gui.screens.skill_tree.buttons.drawer.ButtonIdentifier;
import com.robertx22.age_of_exile.uncommon.MathHelper;
import com.robertx22.age_of_exile.uncommon.datasaving.Load;
import com.robertx22.age_of_exile.uncommon.utilityclasses.ClientOnly;
import net.minecraft.client.Minecraft;

import java.util.concurrent.TimeUnit;

public class OpacityController {

    private final ButtonIdentifier button;
    private final String search = SkillTreeScreen.SEARCH.getValue();
    private float opacity = 1f;

    private final PlayerData playerData = Load.player(ClientOnly.getPlayer());
    @SuppressWarnings("all")
    private static RateLimiter searchLimiter = RateLimiter.create(50000);
    public OpacityController(ButtonIdentifier button) {
        this.button = button;
    }


    public OpacityController searchResultCheck() {

        boolean containsSearchStat = button.perk().stats.stream()
                .anyMatch(item -> item.getStat().locName().getString().toLowerCase().contains(search.toLowerCase()));

        boolean containsName = button.perk().locName().getString().toLowerCase().contains(search.toLowerCase());


        opacity = search.isEmpty() || containsSearchStat || containsName ? 1F : 0.2f;

        return this;
    }


    public OpacityController cachedSearchResultCheck(PerkButton button) {

        if (!button.lastSearchString.equals(search)){
            boolean containsSearchStat = button.matchStrings.stream().anyMatch(x -> x.contains(search.toLowerCase()));
            boolean containsName = button.perk.locName().getString().toLowerCase().contains(search.toLowerCase());
            button.lastSearchString = search;
            button.searchCache = containsSearchStat || containsName;
        }


        opacity = search.isEmpty() || button.searchCache ? 1F : 0.2f;

        return this;
    }

    public OpacityController keywordSearchResultCheck() {
        var search = SkillTreeScreen.SEARCH.getValue();
        PerkStatus status = playerData.talents.getStatus(Minecraft.getInstance().player, button.tree(), button.point());
        if (!search.isEmpty()) {
            if (search.equals("all")) {
                if (status != PerkStatus.CONNECTED) {
                    opacity = 0.2F;
                } else {
                    opacity = 1;
                }
            }
        } else {
            opacity = status.getOpacity();
        }

        return this;
    }

    public OpacityController newbieCheck() {
        if (playerData.talents.getAllocatedPoints(TalentTree.SchoolType.TALENTS) < 1) {
            Perk.PerkType type = button.perk().getType();
            opacity = type == Perk.PerkType.START ? 1 : 0.2F;
        }

        return this;
    }

    public OpacityController highlightPerk() {
        if (search.isEmpty()) {
            opacity += 0.2F;
        }
        return this;
    }

    public static OpacityController newOpacityController(ButtonIdentifier button){
        return new OpacityController(button);
    }

    public void setOpacity(float opacity) {
        this.opacity = opacity;
    }

    public float get() {
        return MathHelper.clamp(opacity, 0, 1);
    }


}
