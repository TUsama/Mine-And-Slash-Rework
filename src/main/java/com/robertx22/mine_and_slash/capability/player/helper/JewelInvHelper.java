package com.robertx22.mine_and_slash.capability.player.helper;

import com.robertx22.mine_and_slash.database.data.stats.types.JewelSocketStat;
import com.robertx22.mine_and_slash.saveclasses.gearitem.gear_bases.IStatCtx;
import com.robertx22.mine_and_slash.saveclasses.jewel.JewelItemData;
import com.robertx22.mine_and_slash.saveclasses.unit.stat_ctx.StatContext;
import com.robertx22.mine_and_slash.uncommon.ExplainedResultUtil;
import com.robertx22.mine_and_slash.uncommon.datasaving.Load;
import com.robertx22.mine_and_slash.uncommon.datasaving.StackSaving;
import com.robertx22.mine_and_slash.uncommon.localization.Chats;
import com.robertx22.mine_and_slash.uncommon.utilityclasses.PlayerUtils;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class JewelInvHelper implements IStatCtx {


    public MyInventory inv;

    public JewelInvHelper(MyInventory inv) {
        this.inv = inv;
    }


    public void unequip(Player p, int i) {

        var stack = inv.getItem(i);
        if (!stack.isEmpty()) {
            PlayerUtils.giveItem(stack.copy(), p);
            stack.shrink(100);
        }
    }


    public void socket(ItemStack stack) {
        for (int i = 0; i < inv.getTotalSlots(); i++) {
            if (inv.getItem(i).isEmpty()) {
                inv.setItem(i, stack.copy());
            }
        }
    }

    public int getJewelSocketsMaxStat(Player p) {
        int max = (int) Load.Unit(p).getUnit().getCalculatedStat(JewelSocketStat.getInstance()).getValue();
        return max;
    }

    public boolean hasFreeJewelSlots(Player p) {
        return this.getAllJewels().size() < getJewelSocketsMaxStat(p);
    }


    public void checkRemoveJewels(Player p) {
        int max = getJewelSocketsMaxStat(p);
        int total = 0;

        List<String> uniques = new ArrayList<>();

        for (int i = 0; i < inv.getTotalSlots(); i++) {
            var stack = inv.getItem(i);
            JewelItemData data = StackSaving.JEWEL.loadFrom(stack);

            if (data != null) {
                total++;

                if (!data.canWear(Load.Unit(p))) {
                    unequip(p, i);
                }
                if (!data.uniq.id.isEmpty()) {
                    if (uniques.contains(data.uniq.id)) {
                        unequip(p, i);
                    } else {
                        uniques.add(data.uniq.id);
                    }

                }
                if (total > max) {
                    ExplainedResultUtil.sendErrorMessage(p, Chats.EQUIP_JEWEL_ERROR, Chats.YOU_LACK_JEWEL_SLOTS);
                    unequip(p, i);
                }
            } else {
                unequip(p, i);
            }
        }
    }


    public List<JewelItemData> getAllJewels() {
        List<JewelItemData> list = new ArrayList<>();
        for (int i = 0; i < inv.getTotalSlots(); i++) {
            var stack = inv.getItem(i);
            JewelItemData data = StackSaving.JEWEL.loadFrom(stack);
            if (data != null) {
                list.add(data);
            }
        }
        return list;
    }

    @Override
    public List<StatContext> getStatAndContext(LivingEntity en) {
        List<StatContext> list = new ArrayList<>();
        for (JewelItemData jewel : this.getAllJewels()) {
            for (StatContext stac : jewel.getStatAndContext(en)) {
                list.add(stac);
            }
        }


        return list;
    }
}
