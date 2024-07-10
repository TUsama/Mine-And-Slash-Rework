package com.robertx22.age_of_exile.saveclasses.gearitem.gear_bases;

import com.robertx22.age_of_exile.capability.entity.EntityData;
import com.robertx22.age_of_exile.database.data.MinMax;
import com.robertx22.age_of_exile.database.data.stats.tooltips.StatTooltipType;
import com.robertx22.age_of_exile.uncommon.datasaving.Load;
import com.robertx22.age_of_exile.uncommon.utilityclasses.ClientOnly;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.entity.player.Player;

public class TooltipInfo implements Cloneable {

    public TooltipInfo(EntityData unitdata, MinMax minmax) {
        this.minmax = minmax;

        this.unitdata = unitdata;

        this.hasAltDown = Screen.hasAltDown();
        this.hasShiftDown = Screen.hasShiftDown();
        this.player = ClientOnly.getPlayer();
    }

    public TooltipInfo(EntityData unitdata) {
        this.minmax = new MinMax(100, 100);

        this.unitdata = unitdata;

        this.hasAltDown = Screen.hasAltDown();
        this.hasShiftDown = Screen.hasShiftDown();
        this.player = ClientOnly.getPlayer();

    }

    public TooltipInfo() {
        this.hasAltDown = Screen.hasAltDown();
        this.hasShiftDown = Screen.hasShiftDown();

        this.player = ClientOnly.getPlayer();
        this.unitdata = Load.Unit(player);
    }

    public TooltipInfo(Player player) {
        this.player = player;
        this.unitdata = Load.Unit(player);

        this.minmax = new MinMax(100, 100);

        this.hasAltDown = Screen.hasAltDown();
        this.hasShiftDown = Screen.hasShiftDown();
    }
    public TooltipInfo(boolean onServer) {
        this.hasAltDown = false;
        this.hasShiftDown = false;

        this.player = null;
        this.unitdata = null;
    }

    public TooltipInfo setIsSet() {
        this.isSet = true;
        return this;
    }

    public boolean showAbilityExtraInfo = true;

    public Player player;
    public EntityData unitdata;
    public MinMax minmax = new MinMax(0, 100);
    public boolean isSet = false;
    public StatTooltipType statTooltipType = StatTooltipType.NORMAL;

    public boolean hasAltDown = false;
    public boolean hasShiftDown = false;

    public boolean shouldShowDescriptions() {
        return hasAltDown && !hasShiftDown;
    }

    public boolean useInDepthStats() {
        return !hasAltDown && hasShiftDown;
    }

}
