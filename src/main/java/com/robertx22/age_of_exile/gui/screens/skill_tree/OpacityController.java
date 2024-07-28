package com.robertx22.age_of_exile.gui.screens.skill_tree;

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

public class OpacityController {

    private final ButtonIdentifier button;
    private final String search = SkillTreeScreen.SEARCH.getValue();
    private float opacity = 1f;

    private final PlayerData playerData = Load.player(ClientOnly.getPlayer());

    public OpacityController(ButtonIdentifier button) {
        this.button = button;
    }

    public static OpacityController normalCheck(PerkButton button) {
        return new OpacityController(button.buttonIdentifier).searchResultCheck()
                .keywordSearchResultCheck()
                .newbieCheck();
    }

    public static OpacityController normalCheck(ButtonIdentifier button) {
        return new OpacityController(button).searchResultCheck()
                .keywordSearchResultCheck()
                .newbieCheck();
    }

    public OpacityController searchResultCheck() {

        boolean containsSearchStat = button.perk().stats.stream()
                .anyMatch(item -> item.getStat().locName().getString().toLowerCase().contains(search.toLowerCase()));

        boolean containsName = button.perk().locName().getString().toLowerCase().contains(search.toLowerCase());


        opacity = search.isEmpty() || containsSearchStat || containsName ? 1F : 0.2f;

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

    public float get() {
        return MathHelper.clamp(opacity, 0, 1);
    }


}
