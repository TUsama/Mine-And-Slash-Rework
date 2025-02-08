package com.robertx22.mine_and_slash.capability.player.data;

import com.robertx22.mine_and_slash.config.forge.ServerContainer;
import com.robertx22.mine_and_slash.database.data.rarities.GearRarity;
import com.robertx22.mine_and_slash.database.registry.ExileDB;
import com.robertx22.mine_and_slash.mmorpg.SlashRef;
import com.robertx22.mine_and_slash.uncommon.MathHelper;
import com.robertx22.mine_and_slash.uncommon.interfaces.data_items.IRarity;
import com.robertx22.mine_and_slash.uncommon.localization.Chats;
import com.robertx22.mine_and_slash.uncommon.localization.Gui;
import com.robertx22.mine_and_slash.uncommon.utilityclasses.OnScreenMessageUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;

public class DeathFavorData {

    private float favor = 250;

    public void set(Player p, float f) {
        GearRarity rar = getRarity();

        boolean upped = f > favor;

        this.favor = MathHelper.clamp(f, 0, ServerContainer.get().MAX_POSSIBLE_FAVOR.get());

        if (rar != getRarity()) {
            if (upped) {
                p.sendSystemMessage(Chats.FAVOR_UP.locName(getRarity().locName().withStyle(getRarity().textFormatting())).withStyle(ChatFormatting.GREEN));
            } else {
                p.sendSystemMessage(Chats.FAVOR_DOWN.locName(getRarity().locName().withStyle(getRarity().textFormatting())).withStyle(ChatFormatting.RED));
            }
        }
    }

    public void onSecond(Player p) {
        set(p, favor + getRarity().getFavorGainEverySecond());
    }

    public void onDeath(Player p) {
        float loss = ServerContainer.get().FAVOR_DEATH_LOSS.get().floatValue();
        set(p, favor - loss);

        p.sendSystemMessage(Chats.FAVOR_DEATH_MSG.locName((int) loss).withStyle(ChatFormatting.DARK_PURPLE));
    }

    public void onLootChest(Player p) {

        float favorGain = ServerContainer.get().FAVOR_CHEST_GAIN.get().floatValue();

        set(p, favor + favorGain);
        OnScreenMessageUtils.actionBar((ServerPlayer) p, Chats.GAIN_FAVOR_ON_LOOT.locName(favorGain, favor).withStyle(ChatFormatting.GREEN));
        //  Load.player(p).prophecy.favor += ServerContainer.get().FAVOR_CHEST_GAIN.get();
    }

    public GearRarity getRarity() {
        GearRarity rar = ExileDB.GearRarities().get(IRarity.COMMON_ID);
        while (rar.hasHigherRarity() && favor >= rar.getHigherRarity().favor_needed) {
            rar = rar.getHigherRarity();
        }

        return rar;
    }

    public List<Component> getTooltip() {
        List<Component> tooltip = new ArrayList<>();

        GearRarity rar = getRarity();

        tooltip.add(rar.locName().append(Gui.Favor_In_GUI.locName()).withStyle(rar.textFormatting()));

        tooltip.add(Component.empty());

        tooltip.add(Gui.Current_Favor.locName((int) favor).withStyle(ChatFormatting.YELLOW));

        tooltip.add(Component.empty());

        tooltip.add(Gui.Loot_Exp_Multiplier.locName(getLootExpMulti()).withStyle(rar.textFormatting()));

        tooltip.add(Component.empty());

        if (this.getRarity().getFavorGainEverySecond() > 0) {
            int perhour = (int) (getRarity().getFavorGainEverySecond() * 60F * 60f);
            tooltip.add(Gui.FAVOR_REGEN_PER_HOUR.locName(getRarity().textFormatting() + String.valueOf(perhour)));
        }
        tooltip.add(Gui.FAVOR_PER_CHEST.locName(ServerContainer.get().FAVOR_CHEST_GAIN.get()).withStyle(ChatFormatting.GREEN));
        tooltip.add(Gui.FAVOR_PER_DEATH.locName(ServerContainer.get().FAVOR_DEATH_LOSS.get()).withStyle(ChatFormatting.RED));

        return tooltip;
    }

    public ResourceLocation getTexture() {
        return SlashRef.guiId("favor/" + getRarity().item_tier);
    }

    public float getLootExpMulti() {
        return getRarity().favor_loot_multi;
    }
}
