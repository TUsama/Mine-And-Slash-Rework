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
// this class is a trash because the opacity conditions is so complex.
public class OpacityController {

    private final ButtonIdentifier button;
    private final String search = SkillTreeScreen.SEARCH.getValue();
    private float opacity;

    private final PlayerData playerData = Load.player(ClientOnly.getPlayer());

    private boolean shouldBeHighlight = false;

    public final float HIGHLIGHT = 1.0f;
    public final float HIDE = 1.0f;

    private boolean forcedOpacity;
    public OpacityController(ButtonIdentifier button) {
        this.button = button;
        PerkStatus status = playerData.talents.getStatus(Minecraft.getInstance().player, button.tree(), button.point());
        opacity = status.getOpacity();
    }


    public OpacityController searchResultCheck() {

        boolean containsSearchStat = button.perk().stats.stream()
                .anyMatch(item -> item.getStat().locName().getString().toLowerCase().contains(search.toLowerCase()));

        boolean containsName = button.perk().locName().getString().toLowerCase().contains(search.toLowerCase());


        shouldBeHighlight = shouldBeHighlight || search.isEmpty() || containsSearchStat || containsName;

        return this;
    }


    public OpacityController cachedSearchResultCheck(PerkButton button) {
        if (search.isEmpty()) return this;
        SearchHandler searchHandler = button.getScreen().searchHandler;

        shouldBeHighlight = shouldBeHighlight || searchHandler.checkThisButtonIsSearchResult(button);

        return this;
    }

    public OpacityController keywordSearchResultCheck() {
        if (search.isEmpty()) return this;
        PerkStatus status = playerData.talents.getStatus(Minecraft.getInstance().player, button.tree(), button.point());
        if (!search.isEmpty()) {
            if (search.equals("all")) {
                shouldBeHighlight = status == PerkStatus.CONNECTED;
            }
        } else {
            // this is copy from the original code
            // the logic here is involved when init this class;

            //opacity = status.getOpacity();
        }

        return this;
    }

    private boolean forcedNewbieCheck() {
        return playerData.talents.getAllocatedPoints(TalentTree.SchoolType.TALENTS)  < 1;

    }


    public static OpacityController newOpacityController(ButtonIdentifier button){
        return new OpacityController(button);
    }

    public void setOpacity(float opacity) {
        this.opacity = opacity;
    }

    public float get() {
        boolean b = forcedNewbieCheck();
        if (b){
            Perk.PerkType type = button.perk().getType();
            return type == Perk.PerkType.START ? HIGHLIGHT : HIDE;
        }
        // in this case, perk button should always render by using its opacity.
        if (search.isEmpty()) return opacity;

        return shouldBeHighlight ? HIGHLIGHT : HIDE;
    }


}
