package com.robertx22.mine_and_slash.database.data.profession.all;

import com.robertx22.library_of_exile.deferred.RegObj;
import com.robertx22.library_of_exile.recipe.RecipeGenerator;
import com.robertx22.mine_and_slash.database.data.profession.items.ProfTierMatItem;
import com.robertx22.mine_and_slash.mmorpg.SlashRef;
import com.robertx22.mine_and_slash.mmorpg.registers.deferred_wrapper.Def;
import com.robertx22.temp.SkillItemTier;
import net.minecraft.advancements.critereon.EnchantedItemTrigger;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.world.item.Item;

import java.util.HashMap;
import java.util.Map;

public class ProfessionMatItems {

    public static HashMap<String, HashMap<SkillItemTier, RegObj<Item>>> TIERED_MAIN_MATS = new HashMap<>();


    public static void init() {


        for (String prof : Professions.ALL) {
            TIERED_MAIN_MATS.put(prof, new HashMap<>());
        }

        for (SkillItemTier tier : SkillItemTier.values()) {

            TIERED_MAIN_MATS.get(Professions.MINING).put(tier, Def.item("material/mining/" + tier.tier, () -> new ProfTierMatItem(Professions.MINING, tier, "Ore")));
            TIERED_MAIN_MATS.get(Professions.FARMING).put(tier, Def.item("material/farming/" + tier.tier, () -> new ProfTierMatItem(Professions.FARMING, tier, "Produce")));
            TIERED_MAIN_MATS.get(Professions.HUSBANDRY).put(tier, Def.item("material/meat/" + tier.tier, () -> new ProfTierMatItem(Professions.HUSBANDRY, tier, "Raw Meat")));
            TIERED_MAIN_MATS.get(Professions.FISHING).put(tier, Def.item("material/fishing/" + tier.tier, () -> new ProfTierMatItem(Professions.FISHING, tier, "Raw Fish")));
        }

    }

    public static void addDownRankRecipes() {
        for (Map.Entry<String, HashMap<SkillItemTier, RegObj<Item>>> en : TIERED_MAIN_MATS.entrySet()) {
            for (Map.Entry<SkillItemTier, RegObj<Item>> e : en.getValue().entrySet()) {

                if (e.getKey().tier != SkillItemTier.TIER0.tier) {

                    RecipeGenerator.addRecipe(SlashRef.MODID, () -> {

                        var lower = e.getKey().lowerTier();

                        var loweritem = en.getValue().get(lower).get();

                        return ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, loweritem, 1)
                                .unlockedBy("player_level", EnchantedItemTrigger.TriggerInstance.enchantedItem())
                                .requires(e.getValue().get(), 1);
                    });


                }
            }
        }

    }
}
