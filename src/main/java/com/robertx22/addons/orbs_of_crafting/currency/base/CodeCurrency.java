package com.robertx22.addons.orbs_of_crafting.currency.base;

import com.robertx22.addons.orbs_of_crafting.currency.IItemAsCurrency;
import com.robertx22.library_of_exile.registry.IGUID;
import com.robertx22.library_of_exile.registry.IWeighted;
import com.robertx22.library_of_exile.util.ExplainedResult;
import com.robertx22.mine_and_slash.gui.texts.textblocks.WorksOnBlock;
import com.robertx22.mine_and_slash.loot.req.DropRequirement;
import com.robertx22.mine_and_slash.mmorpg.SlashRef;
import com.robertx22.mine_and_slash.uncommon.interfaces.IAutoLocDesc;
import com.robertx22.mine_and_slash.uncommon.interfaces.IAutoLocName;
import com.robertx22.orbs_of_crafting.misc.LocReqContext;
import com.robertx22.orbs_of_crafting.misc.ModifyResult;
import com.robertx22.orbs_of_crafting.misc.ResultItem;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public abstract class CodeCurrency implements IWeighted, IAutoLocName, IAutoLocDesc, IGUID {

    public static class Weights {

        public static int COMMON = 1000;
        public static int RARE = 250;
        public static int UBER = 50;
        public static int MEGA_UBER = 10;

    }


    public abstract WorksOnBlock.ItemType usedOn();


    @Override
    public AutoLocGroup locDescGroup() {
        return AutoLocGroup.Currency_Items;
    }

    @Override
    public String locDescLangFileGUID() {
        return SlashRef.MODID + ".currency.desc." + GUID();
    }

    public DropRequirement getDropReq() {
        return DropRequirement.Builder.of().build();
    }


    public abstract void internalModifyMethod(LocReqContext ctx);


    @Override
    public AutoLocGroup locNameGroup() {
        return AutoLocGroup.Currency_Items;
    }

    @Override
    public String locNameLangFileGUID() {
        return SlashRef.MODID + ".currency." + GUID();
    }

    public ExplainedResult canItemBeModified(LocReqContext context) {


        return ExplainedResult.success();
    }

    public void addToTooltip(List<Component> tooltip) {

    }

    public ResultItem modifyItem(LocReqContext context) {
        if (context.Currency.getItem() instanceof IItemAsCurrency cur) {
            var effect = cur.currencyEffect(context.Currency);

            var can = effect.canItemBeModified(context);
            if (can.can) {
                //ExileStack copy = ExileStack.of(context.stack.getStack());
                effect.internalModifyMethod(context);
                return new ResultItem(context.stack, ModifyResult.SUCCESS, can);
            } else {
                return new ResultItem(ItemStack.EMPTY, ModifyResult.NONE, can);
            }
        }
        return new ResultItem(ItemStack.EMPTY, ModifyResult.NONE, ExplainedResult.silentlyFail());

    }

}
