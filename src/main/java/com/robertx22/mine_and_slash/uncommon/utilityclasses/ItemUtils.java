package com.robertx22.mine_and_slash.uncommon.utilityclasses;

import com.robertx22.mine_and_slash.config.forge.ServerContainer;
import com.robertx22.mine_and_slash.database.data.rarities.GearRarity;
import com.robertx22.mine_and_slash.saveclasses.item_classes.GearItemData;
import com.robertx22.mine_and_slash.uncommon.datasaving.StackSaving;
import com.robertx22.mine_and_slash.uncommon.localization.Chats;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class ItemUtils {
    public static Item.Properties getDefaultGearProperties() {

        Item.Properties prop = new Item.Properties();

        return prop;
    }

    public static void tryAnnounceItem(ItemStack stack, Player player) {

        try {
            if (player == null) {
                return;
            }
            if (!ServerContainer.get().ENABLE_LOOT_ANNOUNCEMENTS.get()) {
                return;
            }

            
            GearItemData gear = StackSaving.GEARS.loadFrom(stack);

            if (gear != null) {

                GearRarity rar = (GearRarity) gear.getRarity();

                if (rar.announce_in_chat) {
                    MutableComponent message = Chats.FOUND_ITEM.locName(player.getName().plainCopy().withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.LIGHT_PURPLE), rar.locName()
                                    .withStyle(rar.textFormatting())
                                    .withStyle(ChatFormatting.BOLD))
                            .withStyle(ChatFormatting.BOLD)
                            .withStyle(ChatFormatting.LIGHT_PURPLE);


                    player.getServer()
                            .getPlayerList()
                            .getPlayers()
                            .forEach(x -> x.displayClientMessage(message, false));

                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
