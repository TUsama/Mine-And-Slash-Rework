package com.robertx22.age_of_exile.vanilla_mc.items.gemrunes;

import com.robertx22.age_of_exile.aoe_data.database.stats.Stats;
import com.robertx22.age_of_exile.aoe_data.database.stats.base.ResourceAndAttack;
import com.robertx22.age_of_exile.aoe_data.database.stats.old.DatapackStats;
import com.robertx22.age_of_exile.aoe_data.datapacks.models.IAutoModel;
import com.robertx22.age_of_exile.aoe_data.datapacks.models.ItemModelManager;
import com.robertx22.age_of_exile.database.data.BaseRuneGem;
import com.robertx22.age_of_exile.database.data.StatModifier;
import com.robertx22.age_of_exile.database.data.currency.IItemAsCurrency;
import com.robertx22.age_of_exile.database.data.currency.base.Currency;
import com.robertx22.age_of_exile.database.data.currency.base.GearCurrency;
import com.robertx22.age_of_exile.database.data.currency.base.GearOutcome;
import com.robertx22.age_of_exile.database.data.currency.loc_reqs.LocReqContext;
import com.robertx22.age_of_exile.database.data.gear_types.bases.SlotFamily;
import com.robertx22.age_of_exile.database.data.gems.Gem;
import com.robertx22.age_of_exile.database.data.stats.types.generated.ElementalResist;
import com.robertx22.age_of_exile.database.data.stats.types.offense.SkillDamage;
import com.robertx22.age_of_exile.database.data.stats.types.resources.energy.Energy;
import com.robertx22.age_of_exile.database.data.stats.types.resources.energy.EnergyRegen;
import com.robertx22.age_of_exile.database.data.stats.types.resources.health.HealthRegen;
import com.robertx22.age_of_exile.database.data.stats.types.resources.mana.ManaRegen;
import com.robertx22.age_of_exile.database.registry.ExileDB;
import com.robertx22.age_of_exile.saveclasses.gearitem.gear_parts.SocketData;
import com.robertx22.age_of_exile.saveclasses.item_classes.GearItemData;
import com.robertx22.age_of_exile.saveclasses.unit.ResourceType;
import com.robertx22.age_of_exile.uncommon.datasaving.StackSaving;
import com.robertx22.age_of_exile.uncommon.enumclasses.AttackType;
import com.robertx22.age_of_exile.uncommon.enumclasses.Elements;
import com.robertx22.age_of_exile.uncommon.enumclasses.ModType;
import com.robertx22.age_of_exile.uncommon.interfaces.IAutoLocName;
import com.robertx22.age_of_exile.uncommon.localization.Words;
import com.robertx22.age_of_exile.uncommon.utilityclasses.PlayerUtils;
import com.robertx22.age_of_exile.vanilla_mc.packets.TotemAnimationPacket;
import com.robertx22.library_of_exile.main.Packets;
import com.robertx22.library_of_exile.registry.IGUID;
import com.robertx22.library_of_exile.registry.IWeighted;
import com.robertx22.library_of_exile.utils.RandomUtils;
import com.robertx22.library_of_exile.utils.SoundUtils;
import com.robertx22.library_of_exile.vanilla_util.main.VanillaUTIL;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class GemItem extends BaseGemRuneItem implements IGUID, IAutoModel, IAutoLocName, IItemAsCurrency, IWeighted {

    @Override
    public AutoLocGroup locNameGroup() {
        return AutoLocGroup.Misc;
    }

    @Override
    public Component getName(ItemStack stack) {
        return Component.translatable(this.getDescriptionId()).withStyle(gemType.format);
    }

    @Override
    public int Weight() {
        return this.weight;
    }

    @Override
    public String locNameLangFileGUID() {
        return VanillaUTIL.REGISTRY.items().getKey(this)
                .toString();
    }

    @Override
    public String locNameForLangFile() {
        return gemRank.locName + " " + gemType.locName;
    }

    @Override
    public void generateModel(ItemModelManager manager) {
        manager.generated(this);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player user, InteractionHand hand) {
        ItemStack itemStack = user.getItemInHand(hand);
        user.startUsingItem(hand);
        return InteractionResultHolder.success(itemStack);
    }

    @Override
    public UseAnim getUseAnimation(ItemStack p_77661_1_) {
        return UseAnim.BOW;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level world, LivingEntity en) {

        if (world.isClientSide) {
            return stack;
        }

        if (en instanceof Player) {
            Player p = (Player) en;

            if (!getGem().hasHigherTierGem()) {
                p.displayClientMessage(Component.literal(ChatFormatting.RED + "These gems are already maximum rank."), false);
                return stack;
            }
            if (stack.getCount() < 3) {
                p.displayClientMessage(Component.literal(ChatFormatting.RED + "You need 3 gems to attempt upgrade."), false);
                return stack;
            }

            Gem gem = getGem();

            if (stack.getCount() > 2) {
                if (getGem().hasHigherTierGem()) {
                    boolean success = RandomUtils.roll(gem.perc_upgrade_chance);

                    stack.shrink(3);

                    Item old = stack.getItem();

                    if (success) {
                        ItemStack newstack = new ItemStack(getGem().getHigherTierGem()
                                .getItem());
                        Packets.sendToClient(p, new TotemAnimationPacket(newstack));
                        p.displayClientMessage(Component.literal(ChatFormatting.GREEN + "").append(old.getName(new ItemStack(old)))
                                .append(" has been upgraded to ")
                                .append(newstack.getDisplayName()), false);
                        PlayerUtils.giveItem(newstack, p);

                    } else {
                        SoundUtils.playSound(p, SoundEvents.VILLAGER_NO, 1, 1);

                        p.displayClientMessage(Component.literal(ChatFormatting.RED + "").append(old.getName(new ItemStack(old)))
                                .append(" has failed the upgrade and was destroyed."), false);
                    }
                }
            }
        }

        return stack;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 40;
    }

    static float MIN_WEP_DMG = 2;
    static float MAX_WEP_DMG = 15;
    static float MIN_RES = 5;
    static float MAX_RES = 25;
    static float MIN_ELE_DMG = 2;
    static float MAX_ELE_DMG = 10;


    @Override
    public BaseRuneGem getBaseRuneGem() {
        return getGem();
    }

    @Override
    public float getStatValueMulti() {
        return this.gemRank.statmulti;
    }

    @Override
    public List<StatModifier> getStatModsForSerialization(SlotFamily family) {
        return gemType.stats.getFor(family);
    }

    @Override
    public Currency currencyEffect() {
        return new GearCurrency() {
            @Override
            public List<GearOutcome> getOutcomes() {
                return Arrays.asList(
                        new GearOutcome() {
                            @Override
                            public Words getName() {
                                return Words.None;
                            }

                            @Override
                            public OutcomeType getOutcomeType() {
                                return OutcomeType.GOOD;
                            }

                            @Override
                            public ItemStack modify(LocReqContext ctx, GearItemData gear, ItemStack stack) {
                                GemItem gitem = (GemItem) stack.getItem();
                                Gem gem = gitem.getGem();

                                SocketData socket = new SocketData();
                                socket.gem = gem.identifier;

                                gear.sockets.sockets.add(socket);

                                ctx.player.displayClientMessage(Component.literal("Gem Socketed"), false);

                                
                                StackSaving.GEARS.saveTo(stack, gear);


                                return stack;
                            }

                            @Override
                            public int Weight() {
                                return 1000;
                            }
                        }
                );
            }

            @Override
            public boolean canBeModified(GearItemData data) {
                return data.getEmptySockets() > 0;
            }


            @Override
            public String locDescForLangFile() {
                return "Sockets the gem";
            }

            @Override
            public String locNameForLangFile() {
                return locNameForLangFile();
            }

            @Override
            public String GUID() {
                return GUID();
            }

            @Override
            public int Weight() {
                return weight;
            }
        };
    }


    public static class EleGem extends GemStatPerTypes {
        public Elements ele;

        public EleGem(Elements ele) {
            this.ele = ele;
        }

        @Override
        public List<StatModifier> onArmor() {
            return Arrays.asList(new StatModifier(MIN_RES, MAX_RES, new ElementalResist(ele), ModType.FLAT));
        }

        @Override
        public List<StatModifier> onJewelry() {
            return Arrays.asList(new StatModifier(MIN_ELE_DMG, MAX_ELE_DMG, Stats.ELEMENTAL_SPELL_DAMAGE.get(ele)));
        }

        @Override
        public List<StatModifier> onWeapons() {
            return Arrays.asList(new StatModifier(MIN_WEP_DMG, MAX_WEP_DMG, Stats.ELEMENTAL_DAMAGE.get(ele), ModType.FLAT));
        }
    }

    public enum GemType {

        TOURMALINE("tourmaline", "Tourmaline", ChatFormatting.LIGHT_PURPLE, new GemStatPerTypes() {
            @Override
            public List<StatModifier> onArmor() {
                return Arrays.asList(new StatModifier(1, 5, DatapackStats.STR));
            }

            @Override
            public List<StatModifier> onJewelry() {
                return Arrays.asList(new StatModifier(2, 15, HealthRegen.getInstance(), ModType.PERCENT));
            }

            @Override
            public List<StatModifier> onWeapons() {
                return Arrays.asList(new StatModifier(1, 5, Stats.LIFESTEAL.get()));
            }
        }),
        AZURITE("azurite", "Azurite", ChatFormatting.AQUA, new GemStatPerTypes() {
            @Override
            public List<StatModifier> onArmor() {
                return Arrays.asList(new StatModifier(1, 5, DatapackStats.INT));
            }

            @Override
            public List<StatModifier> onJewelry() {
                return Arrays.asList(new StatModifier(2, 15, ManaRegen.getInstance(), ModType.PERCENT));
            }

            @Override
            public List<StatModifier> onWeapons() {
                return Arrays.asList(new StatModifier(1, 3, Stats.RESOURCE_ON_HIT.get(new ResourceAndAttack(ResourceType.mana, AttackType.attack))));
            }
        }),

        GARNET("garnet", "Garnet", ChatFormatting.GREEN, new GemStatPerTypes() {
            @Override
            public List<StatModifier> onArmor() {
                return Arrays.asList(new StatModifier(1, 5, DatapackStats.DEX));
            }

            @Override
            public List<StatModifier> onJewelry() {
                return Arrays.asList(new StatModifier(2, 15, EnergyRegen.getInstance(), ModType.PERCENT));
            }

            @Override
            public List<StatModifier> onWeapons() {
                return Arrays.asList(new StatModifier(2, 8, Stats.CRIT_CHANCE.get()));
            }
        }),
        OPAL("opal", "Opal", ChatFormatting.GOLD, new GemStatPerTypes() {
            @Override
            public List<StatModifier> onArmor() {
                return Arrays.asList(new StatModifier(1, 5, DatapackStats.STR));
            }

            @Override
            public List<StatModifier> onJewelry() {
                return Arrays.asList(new StatModifier(1, 6, Stats.CRIT_CHANCE.get()));
            }

            @Override
            public List<StatModifier> onWeapons() {
                return Arrays.asList(new StatModifier(3, 12, Stats.CRIT_DAMAGE.get()));
            }
        }),
        TOPAZ("topaz", "Topaz", ChatFormatting.YELLOW, new GemStatPerTypes() {
            @Override
            public List<StatModifier> onArmor() {
                return Arrays.asList(new StatModifier(MIN_RES, MAX_RES, new ElementalResist(Elements.Lightning)));
            }

            @Override
            public List<StatModifier> onJewelry() {
                return Arrays.asList(new StatModifier(2, 15, Energy.getInstance(), ModType.PERCENT));
            }

            @Override
            public List<StatModifier> onWeapons() {
                return Arrays.asList(new StatModifier(1, 3, Stats.RESOURCE_ON_HIT.get(new ResourceAndAttack(ResourceType.energy, AttackType.all))));
            }
        }),
        AMETHYST("amethyst", "Amethyst", ChatFormatting.DARK_PURPLE, new GemStatPerTypes() {
            @Override
            public List<StatModifier> onArmor() {
                return Arrays.asList(new StatModifier(1, 5, DatapackStats.INT));
            }

            @Override
            public List<StatModifier> onJewelry() {
                return Arrays.asList(new StatModifier(1, 6, SkillDamage.getInstance(), ModType.FLAT));
            }

            @Override
            public List<StatModifier> onWeapons() {
                return Arrays.asList(new StatModifier(2, 10, Stats.CRIT_DAMAGE.get()));
            }
        }),
        RUBY("ruby", "Ruby", ChatFormatting.RED, new EleGem(Elements.Fire)),
        EMERALD("emerald", "Emerald", ChatFormatting.GREEN, new EleGem(Elements.Chaos)),
        SAPPHIRE("sapphire", "Sapphire", ChatFormatting.BLUE, new EleGem(Elements.Cold));

        public String locName;
        public String id;
        public ChatFormatting format;
        public GemStatPerTypes stats;

        GemType(String id, String locName, ChatFormatting format, GemStatPerTypes stats) {
            this.locName = locName;
            this.id = id;
            this.format = format;
            this.stats = stats;
        }
    }

    public enum GemRank {
        CRACKED("Cracked", 0, 0.1F, 100, 100999, 0F),
        CHIPPED("Chipped", 1, 0.2F, 75, 25999, 0.1F),
        FLAWED("Flawed", 2, 0.3F, 50, 5000, 0.2F),
        REGULAR("Regular", 3, 0.4F, 25, 1000, 0.5F),
        GRAND("Grand", 4, 0.6F, 10, 200, 0.75F),
        GLORIOUS("Glorious", 5, 0.8F, 5, 25, 0.9F),
        DIVINE("Divine", 6, 1F, 0, 1, 0.95F);

        public String locName;
        public int tier;
        public float statmulti;
        public int upgradeChance;
        public int weight;
        public float lvlToDrop;

        GemRank(String locName, int tier, float statmulti, int upgradeChance, int weight, float lvlToDrop) {
            this.locName = locName;
            this.weight = weight;
            this.lvlToDrop = lvlToDrop;
            this.tier = tier;
            this.statmulti = statmulti;
            this.upgradeChance = upgradeChance;
        }

        public static GemRank ofTier(int tier) {

            for (GemRank gr : GemRank.values()) {
                if (gr.tier == tier) {
                    return gr;
                }
            }

            return GemRank.CHIPPED;

        }
    }

    public GemItem(GemType type, GemRank gemRank) {
        super(new Properties()
                .stacksTo(16));

        this.gemType = type;
        this.gemRank = gemRank;

        this.weight = gemRank.weight;
        this.levelToStartDrop = gemRank.lvlToDrop;

    }

    public GemType gemType;
    public GemRank gemRank;
    public float levelToStartDrop;

    @Override
    public String GUID() {
        return "gems/" + gemType.id + "/" + gemRank.tier;
    }

    public Gem getGem() {
        String id = VanillaUTIL.REGISTRY.items().getKey(this)
                .toString();

        Optional<Gem> opt = ExileDB.Gems()
                .getList()
                .stream()
                .filter(x -> id.equals(x.item_id))
                .findFirst();

        return opt.orElse(new Gem());
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, Level world, List<Component> tooltip, TooltipFlag context) {

        try {

            tooltip.addAll(getBaseTooltip());

            tooltip.add(Component.literal(""));

            if (getGem().hasHigherTierGem()) {
                tooltip.add(Component.literal("Hold 3 gems to attempt upgrade"));
                tooltip.add(Component.literal("Upgrade chance: " + getGem().perc_upgrade_chance + "%"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
