package com.robertx22.age_of_exile.gui.screens.skill_tree.opacity.states;

import com.robertx22.age_of_exile.database.data.perks.Perk;
import com.robertx22.age_of_exile.gui.screens.skill_tree.buttons.PerkButton;
import com.robertx22.age_of_exile.gui.screens.skill_tree.opacity.OpacityController;

public class NonSearching extends OpacityControllerState {

    public NonSearching(OpacityController opacityController, PerkButton button) {
        super(opacityController, button);
    }

    @Override
    public float onWholeImage() {
        return super.isRefresh ? 0.5f : 0.95f;
    }

    @Override
    public float onSingleButton() {
        if (super.isRefresh) {
            Perk.PerkType type = button.perk.getType();
            return type == Perk.PerkType.START ? OpacityController.HIGHLIGHT : OpacityController.HIDE;
        }
        return button.getLazyStatus().getOpacity();
    }

    @Override
    public float onSingleButtonWhenWholeImage() {
        Perk.PerkType type = button.perk.getType();
        return type == Perk.PerkType.START ? OpacityController.HIGHLIGHT : OpacityController.HIDE;
    }
}
