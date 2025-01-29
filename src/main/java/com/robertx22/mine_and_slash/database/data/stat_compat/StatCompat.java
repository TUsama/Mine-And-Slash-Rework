package com.robertx22.mine_and_slash.database.data.stat_compat;

import com.robertx22.library_of_exile.main.ExileLog;
import com.robertx22.library_of_exile.registry.ExileRegistryType;
import com.robertx22.library_of_exile.registry.IAutoGson;
import com.robertx22.library_of_exile.registry.JsonExileRegistry;
import com.robertx22.mine_and_slash.database.data.stats.StatScaling;
import com.robertx22.mine_and_slash.database.data.stats.datapacks.stats.AttributeStat;
import com.robertx22.mine_and_slash.database.registry.ExileDB;
import com.robertx22.mine_and_slash.database.registry.ExileRegistryTypes;
import com.robertx22.mine_and_slash.mixin_ducks.IDirty;
import com.robertx22.mine_and_slash.mmorpg.MMORPG;
import com.robertx22.mine_and_slash.saveclasses.ExactStatData;
import com.robertx22.mine_and_slash.uncommon.MathHelper;
import com.robertx22.mine_and_slash.uncommon.datasaving.Load;
import com.robertx22.mine_and_slash.uncommon.enumclasses.ModType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.function.Consumer;

public class StatCompat implements JsonExileRegistry<StatCompat>, IAutoGson<StatCompat> {

    public static StatCompat SERIALIZER = new StatCompat();

    public String id = "";

    public String attribute_id = "";
    public String enchant_id = "";
    public StatScaling scaling = StatScaling.NONE;
    public String mns_stat_id = "";
    public float conversion = 0.5F;
    public int minimum_cap = 0;
    public int maximum_cap = 100;

    public int per_item_min = 0;
    public int per_item_max = 100;

    public ModType mod_type = ModType.PERCENT;

    public StatCompat(String id) {
        this.id = id + "_compat";
    }

    private StatCompat() {
    }


    public void editAndReg(Consumer<StatCompat> co) {
        co.accept(this);
        addToSerializables(MMORPG.SERIAZABLE_REGISTRATION_INFO);
    }

    private Attribute getAttribute() {
        return ForgeRegistries.ATTRIBUTES.getValue(new ResourceLocation(attribute_id));
    }

    public boolean isAttributeCompat() {
        return !attribute_id.isEmpty();
    }

    public boolean isEnchantCompat() {
        return !enchant_id.isEmpty();
    }


    public ExactStatData getEnchantCompatResult(List<ItemStack> stacks, int lvl) {
        if (ExileDB.Stats().get(mns_stat_id) instanceof AttributeStat) {
            return null;
        }
        Enchantment ench = ForgeRegistries.ENCHANTMENTS.getValue(new ResourceLocation(enchant_id));

        float value = 0;

        for (ItemStack stack : stacks) {
            int enchlvl = stack.getEnchantmentLevel(ench);

            if (enchlvl < 1) {
                continue;
            }
            int val = (int) (enchlvl * conversion);
            value += MathHelper.clamp(val, per_item_min, per_item_max);
        }

        if (value != 0) {
            value = MathHelper.clamp(value, minimum_cap, maximum_cap);
            value = (int) scaling.scale(value, lvl);
            var data = ExactStatData.noScaling(value, mod_type, mns_stat_id);
            return data;
        } else {
            return null;
        }
    }

    public ExactStatData getResult(LivingEntity en, int lvl) {
        try {
            if (ExileDB.Stats().get(mns_stat_id) instanceof AttributeStat) {
                return null;
            }
            var at = getAttribute();

            if (at == null) {
                ExileLog.get().warn(this.attribute_id + " is a null attribute in Stat Compat");
                return null;
            }

            int val = (int) (en.getAttributeValue(at) * conversion);
            int value = MathHelper.clamp(val, minimum_cap, maximum_cap);

            if (value != 0) {
                value = (int) scaling.scale(value, lvl);
                var data = ExactStatData.noScaling(value, mod_type, mns_stat_id);
                return data;

            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public static void onTick(Player en) {

        IDirty check = (IDirty) en.getAttributes();

        if (check.isAttribDirty()) {
            check.setAttribDirty(false);
            Load.player(en).cachedStats.STAT_COMPAT.setDirty();
        }
    }

    @Override
    public ExileRegistryType getExileRegistryType() {
        return ExileRegistryTypes.STAT_COMPAT;
    }


    @Override
    public String GUID() {
        return id;
    }

    @Override
    public int Weight() {
        return 1;
    }

    @Override
    public Class<StatCompat> getClassForSerialization() {
        return StatCompat.class;
    }
}
