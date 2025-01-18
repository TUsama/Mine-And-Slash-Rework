package com.robertx22.mine_and_slash.vanilla_mc.items;

import com.robertx22.mine_and_slash.uncommon.IShapedRecipe;
import com.robertx22.mine_and_slash.uncommon.datasaving.Load;
import com.robertx22.mine_and_slash.uncommon.interfaces.data_items.IRarity;
import com.robertx22.mine_and_slash.uncommon.localization.Chats;
import com.robertx22.mine_and_slash.uncommon.localization.Itemtips;
import com.robertx22.mine_and_slash.uncommon.utilityclasses.AllyOrEnemy;
import com.robertx22.mine_and_slash.uncommon.utilityclasses.EntityFinder;
import com.robertx22.mine_and_slash.uncommon.utilityclasses.WorldUtils;
import com.robertx22.mine_and_slash.vanilla_mc.items.misc.AutoItem;
import com.robertx22.mine_and_slash.vanilla_mc.items.misc.RarityStoneItem;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

import static com.robertx22.library_of_exile.tooltip.ExileTooltipUtils.splitLongText;

public class TpBackItem extends AutoItem implements IShapedRecipe {

    public TpBackItem() {
        super(new Properties());

    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player p, InteractionHand pUsedHand) {
        ItemStack itemstack = p.getItemInHand(pUsedHand);

        if (!pLevel.isClientSide) {

            if (WorldUtils.isMapWorldClass(pLevel)) {


                if (!EntityFinder.start(p, Mob.class, p.blockPosition()).radius(5).searchFor(AllyOrEnemy.enemies).build().isEmpty()) {
                    p.sendSystemMessage(Chats.ENEMY_TOO_CLOSE.locName());
                    return InteractionResultHolder.pass(p.getItemInHand(pUsedHand));
                }
                itemstack.shrink(1);


                Load.player(p).map.teleportBack(p);

                return InteractionResultHolder.success(p.getItemInHand(pUsedHand));
            }
        }
        return InteractionResultHolder.pass(p.getItemInHand(pUsedHand));
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        pTooltipComponents.addAll(splitLongText(Itemtips.TP_BACK_ITEM.locName()));
    }


    @Override
    public String locNameForLangFile() {
        return "Pearl of Home";
    }

    @Override
    public ShapedRecipeBuilder getRecipe() {
        return shaped(this)
                .define('X', RarityStoneItem.of(IRarity.COMMON_ID))
                .define('O', Items.IRON_INGOT)
                .pattern(" O ")
                .pattern("XXX")
                .pattern("X X")
                .unlockedBy("player_level", trigger());
    }

    @Override
    public String GUID() {
        return "tpback";
    }
}
