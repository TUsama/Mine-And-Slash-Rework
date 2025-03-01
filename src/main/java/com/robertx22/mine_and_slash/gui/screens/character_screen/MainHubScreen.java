package com.robertx22.mine_and_slash.gui.screens.character_screen;

import com.robertx22.dungeon_realm.main.DungeonMain;
import com.robertx22.library_of_exile.main.Packets;
import com.robertx22.library_of_exile.utils.RenderUtils;
import com.robertx22.library_of_exile.utils.TextUTIL;
import com.robertx22.library_of_exile.wrappers.ExileText;
import com.robertx22.mine_and_slash.aoe_data.database.stats.DefenseStats;
import com.robertx22.mine_and_slash.aoe_data.database.stats.OffenseStats;
import com.robertx22.mine_and_slash.aoe_data.database.stats.ResourceStats;
import com.robertx22.mine_and_slash.aoe_data.database.stats.SpellChangeStats;
import com.robertx22.mine_and_slash.aoe_data.database.stats.old.DatapackStats;
import com.robertx22.mine_and_slash.characters.reworked_gui.ToonScreen;
import com.robertx22.mine_and_slash.database.data.game_balance_config.PlayerPointsType;
import com.robertx22.mine_and_slash.database.data.stats.Stat;
import com.robertx22.mine_and_slash.database.data.stats.datapacks.stats.CoreStat;
import com.robertx22.mine_and_slash.database.data.stats.effects.defense.MaxElementalResist;
import com.robertx22.mine_and_slash.database.data.stats.types.core_stats.AllAttributes;
import com.robertx22.mine_and_slash.database.data.stats.types.defense.Armor;
import com.robertx22.mine_and_slash.database.data.stats.types.defense.ArmorPenetration;
import com.robertx22.mine_and_slash.database.data.stats.types.defense.BlockChance;
import com.robertx22.mine_and_slash.database.data.stats.types.defense.DodgeRating;
import com.robertx22.mine_and_slash.database.data.stats.types.generated.ElementalPenetration;
import com.robertx22.mine_and_slash.database.data.stats.types.generated.ElementalResist;
import com.robertx22.mine_and_slash.database.data.stats.types.offense.SkillDamage;
import com.robertx22.mine_and_slash.database.data.stats.types.offense.WeaponDamage;
import com.robertx22.mine_and_slash.database.data.stats.types.resources.energy.Energy;
import com.robertx22.mine_and_slash.database.data.stats.types.resources.energy.EnergyRegen;
import com.robertx22.mine_and_slash.database.data.stats.types.resources.health.Health;
import com.robertx22.mine_and_slash.database.data.stats.types.resources.health.HealthRegen;
import com.robertx22.mine_and_slash.database.data.stats.types.resources.magic_shield.MagicShield;
import com.robertx22.mine_and_slash.database.data.stats.types.resources.magic_shield.MagicShieldRegen;
import com.robertx22.mine_and_slash.database.data.stats.types.resources.mana.Mana;
import com.robertx22.mine_and_slash.database.data.stats.types.resources.mana.ManaRegen;
import com.robertx22.mine_and_slash.database.data.talent_tree.TalentTree;
import com.robertx22.mine_and_slash.database.registry.ExileDB;
import com.robertx22.mine_and_slash.gui.bases.BaseScreen;
import com.robertx22.mine_and_slash.gui.bases.INamedScreen;
import com.robertx22.mine_and_slash.gui.buttons.CharacterStatsButtons;
import com.robertx22.mine_and_slash.gui.buttons.FavorButton;
import com.robertx22.mine_and_slash.gui.buttons.ProfessionLevelsButton;
import com.robertx22.mine_and_slash.gui.inv_gui.GuiInventoryGrids;
import com.robertx22.mine_and_slash.gui.screens.OpenInvGuiScreen;
import com.robertx22.mine_and_slash.gui.screens.OpenJewelsScreen;
import com.robertx22.mine_and_slash.gui.screens.OpenSkillGems;
import com.robertx22.mine_and_slash.gui.screens.skill_tree.AscendancyTree;
import com.robertx22.mine_and_slash.gui.screens.skill_tree.TalentsScreen;
import com.robertx22.mine_and_slash.gui.screens.spell.SpellSchoolScreen;
import com.robertx22.mine_and_slash.gui.screens.stat_gui.StatScreen;
import com.robertx22.mine_and_slash.gui.wiki.reworked.NewWikiScreen;
import com.robertx22.mine_and_slash.mmorpg.SlashRef;
import com.robertx22.mine_and_slash.prophecy.gui.ProphecyScreen;
import com.robertx22.mine_and_slash.uncommon.datasaving.Load;
import com.robertx22.mine_and_slash.uncommon.enumclasses.Elements;
import com.robertx22.mine_and_slash.uncommon.localization.Gui;
import com.robertx22.mine_and_slash.uncommon.localization.Words;
import com.robertx22.mine_and_slash.vanilla_mc.packets.AllocateStatPacket;
import com.robertx22.mine_and_slash.vanilla_mc.packets.proxies.OpenGuiWrapper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;


public class MainHubScreen extends BaseScreen implements INamedScreen {
    private static final ResourceLocation LEFT = new ResourceLocation(SlashRef.MODID, "textures/gui/main_hub/buttons_backwards.png");
    static ResourceLocation RIGHT = new ResourceLocation(SlashRef.MODID, "textures/gui/main_hub/buttons.png");

    static int sizeX = 256;
    static int sizeY = 219;

    Minecraft mc = Minecraft.getInstance();


    public enum StatType {
        RESOURCE("resource"),
        DAMAGE("damage"),
        ELE_DAMAGE("ele_damage"),
        DEFENSE("defense"),
        RECOVERY("recovery"),
        MISC("misc");

        String id;

        StatType(String id) {
            this.id = id;
        }

        public ResourceLocation getIcon() {

            return new ResourceLocation(SlashRef.MODID, "textures/gui/stat_groups/" + id + ".png");
        }
    }


    public static List<List<Stat>> leftStats = new ArrayList<>();
    public static List<List<Stat>> rightStats = new ArrayList<>();

    // todo implement this elsewhere
    public static HashMap<StatType, List<List<Stat>>> STAT_MAP = new HashMap<>();

    static <T extends Stat> void addTo(StatType type, List<T> stats) {

        List<Stat> list = stats.stream()
                .map(x -> (Stat) x)
                .collect(Collectors.toList());

        if (!STAT_MAP.containsKey(type)) {
            STAT_MAP.put(type, new ArrayList<>());
        }
        STAT_MAP.get(type)
                .add(list);
    }

    static <T extends Stat> void addRemaining(StatType type) {

        List<Stat> list = new ArrayList<>();

        for (List<List<Stat>> l : STAT_MAP.values()) {
            for (List<Stat> s : l) {
                for (Stat st : s) {
                    list.add(st);
                }
            }
        }

        var v = Load.Unit(Minecraft.getInstance().player).getUnit().getStats().stats.values().stream()
                .filter(x -> x.isNotZero() && x.GetStat() != null && x.GetStat().show_in_gui && !x.GetStat().is_long && list.stream().noneMatch(e -> e.GUID().equals(x.getId())))
                .map(t -> t.GetStat()).collect(Collectors.toList());

        if (!STAT_MAP.containsKey(type)) {
            STAT_MAP.put(type, new ArrayList<>());
        }
        STAT_MAP.get(StatType.MISC).add(v);

    }

    static {

        addTo(StatType.RESOURCE, Arrays.asList(Health.getInstance(), HealthRegen.getInstance(), MagicShield.getInstance(), MagicShieldRegen.getInstance(), Mana.getInstance(), ManaRegen.getInstance(), Energy.getInstance(), EnergyRegen.getInstance()));
        addTo(StatType.RESOURCE, Arrays.asList(DatapackStats.STR, DatapackStats.INT, DatapackStats.DEX));

        addTo(StatType.DAMAGE, Arrays.asList(WeaponDamage.getInstance(), SkillDamage.getInstance()));
        addTo(StatType.DAMAGE, OffenseStats.STYLE_DAMAGE.getAll());
        addTo(StatType.DAMAGE, Arrays.asList(OffenseStats.ACCURACY.get(), OffenseStats.CRIT_CHANCE.get(), OffenseStats.CRIT_DAMAGE.get()));
        addTo(StatType.DAMAGE, Arrays.asList(SpellChangeStats.COOLDOWN_REDUCTION.get(), SpellChangeStats.CAST_SPEED.get()));

        addTo(StatType.ELE_DAMAGE, OffenseStats.ELEMENTAL_DAMAGE.getAll().stream().filter(x -> x.getElement().isValid()).collect(Collectors.toList()));
        // addTo(StatType.ELE_DAMAGE, Stats.ELEMENTAL_ANY_WEAPON_DAMAGE.getAll());
        addTo(StatType.ELE_DAMAGE, OffenseStats.ELEMENTAL_SPELL_DAMAGE.getAll().stream().filter(x -> x.getElement().isValid()).collect(Collectors.toList()));
        addTo(StatType.ELE_DAMAGE, Arrays.asList(ArmorPenetration.getInstance()));
        addTo(StatType.ELE_DAMAGE, new ElementalPenetration(Elements.Elemental).generateAllSingleVariations());

        addTo(StatType.DEFENSE, Arrays.asList(Armor.getInstance(), DodgeRating.getInstance(), BlockChance.getInstance()));
        addTo(StatType.DEFENSE, Arrays.asList(DefenseStats.DAMAGE_RECEIVED.get()));
        addTo(StatType.DEFENSE, Arrays.asList(DefenseStats.DAMAGE_REDUCTION.get()));
        addTo(StatType.DEFENSE, Arrays.asList(DefenseStats.DAMAGE_REDUCTION_CHANCE.get()));
        addTo(StatType.DEFENSE, new ElementalResist(Elements.Elemental).generateAllSingleVariations());
        addTo(StatType.DEFENSE, new MaxElementalResist(Elements.Elemental).generateAllSingleVariations());

        addTo(StatType.RECOVERY, Arrays.asList(ResourceStats.HEAL_STRENGTH.get(), ResourceStats.HEALING_RECEIVED.get()));
        addTo(StatType.RECOVERY, Arrays.asList(ResourceStats.LIFESTEAL.get(), ResourceStats.MANASTEAL.get(), ResourceStats.SPELL_LIFESTEAL.get(), ResourceStats.SPELL_MSSTEAL.get(), ResourceStats.DOT_LIFESTEAL.get()));
        addTo(StatType.RECOVERY, ResourceStats.RESOURCE_ON_HIT.getAll());
        addTo(StatType.RECOVERY, ResourceStats.RESOURCE_ON_KILL.getAll());
        addTo(StatType.RECOVERY, Arrays.asList(ResourceStats.INCREASED_LEECH.get()));
        addTo(StatType.RECOVERY, ResourceStats.LEECH_CAP.getAll());

        addRemaining(StatType.MISC);

        leftStats.add(Arrays.asList(Health.getInstance(), MagicShield.getInstance(), Mana.getInstance(), Energy.getInstance()));
        leftStats.add(Arrays.asList(HealthRegen.getInstance(), MagicShieldRegen.getInstance(), ManaRegen.getInstance(), EnergyRegen.getInstance()));

        rightStats.add(Arrays.asList(new ElementalResist(Elements.Fire), new ElementalResist(Elements.Cold), new ElementalResist(Elements.Nature), new ElementalResist(Elements.Shadow)));
        rightStats.add(Arrays.asList(OffenseStats.CRIT_CHANCE.get(), OffenseStats.CRIT_DAMAGE.get(), Armor.getInstance(), DodgeRating.getInstance()));

    }

    public MainHubScreen() {
        super(sizeX, sizeY);
    }

    @Override
    public ResourceLocation iconLocation() {
        return new ResourceLocation(SlashRef.MODID, "textures/gui/main_hub/icons/stat_overview.png");
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public boolean mouseReleased(double x, double y, int ticks) {
        return super.mouseReleased(x, y, ticks);
    }

    @Override
    public Words screenName() {
        return Words.Character;
    }

    @Override
    public void init() {
        super.init();


        this.clearWidgets();

        //this.children.clear();

        // CORE STATS
        int xpos = guiLeft + 75;
        int ypos = guiTop + 25;


        xpos = guiLeft + 78;
        ypos = guiTop + 105;

        int YSEP = 20;

        // TODO MAKE STATIC IDS
        xpos = guiLeft + 28;
        ypos = guiTop + 21;

        publicAddButton(new AllocateStatButton(AllAttributes.STR_ID, xpos, ypos));
        ypos += YSEP;
        publicAddButton(new AllocateStatButton(AllAttributes.INT_ID, xpos, ypos));
        ypos += YSEP;
        publicAddButton(new AllocateStatButton(AllAttributes.DEX_ID, xpos, ypos));


        xpos = guiLeft + 12;
        ypos = guiTop + 90;


        // hub buttons

        List<INamedScreen> rightButtons = new ArrayList<>();
        rightButtons.add(new SpellSchoolScreen());
        rightButtons.add(new OpenSkillGems());
        rightButtons.add(new TalentsScreen());
        if (Load.player(mc.player).talents.getAllocatedPoints(TalentTree.SchoolType.TALENTS) > 0) {
            rightButtons.add(new AscendancyTree());
        }
        rightButtons.add(new OpenJewelsScreen());

        if (mc.level.dimension().location().equals(DungeonMain.DIMENSION_KEY)) {
            if (Load.player(mc.player).prophecy.affixOffers.isEmpty()) {
                rightButtons.add(new ProphecyScreen());
            } else {
                rightButtons.add(OpenGuiWrapper.getProphecyCardsScreen());
            }
        }

        List<INamedScreen> leftButtons = new ArrayList<>();

        leftButtons.add(new ToonScreen());
        leftButtons.add(new NewWikiScreen());
        leftButtons.add(new OpenInvGuiScreen(Words.Salvaging, "salvage", GuiInventoryGrids.ofSalvageConfig()));
        leftButtons.add(new OpenInvGuiScreen(Words.Configs, "configs", GuiInventoryGrids.ofConfigs()));
        leftButtons.add(new StatScreen());


        publicAddButton(new FavorButton(guiLeft + sizeX / 2 - FavorButton.FAVOR_BUTTON_SIZE_X / 2, guiTop - FavorButton.FAVOR_BUTTON_SIZE_Y));
        publicAddButton(new ProfessionLevelsButton(guiLeft + sizeX / 2 - ProfessionLevelsButton.SX / 2, guiTop + 147));


        int x = guiLeft + sizeX - 1;
        int y = guiTop + 20;

        for (INamedScreen screen : rightButtons) {
            publicAddButton(new MainHubButton(true, RIGHT, screen, x, y));
            y += MainHubButton.ySize + 0;
        }


        x = guiLeft - MainHubButton.xSize;
        y = guiTop + 20;
        for (INamedScreen screen : leftButtons) {
            this.publicAddButton(new MainHubButton(false, LEFT, screen, x, y));
            y += MainHubButton.ySize + 0;
        }

        int xp = guiLeft + 195;
        int yp = guiTop + 20;

        int i = 0;
        for (StatType type : StatType.values()) {
            publicAddButton(new CharacterStatsButtons(type, xp, yp));
            yp += 20;
            i++;

            if (i == 3) {
                xp += 25;
                yp = guiTop + 20;
            }
        }

        int statx = 17;
        int staty = 103;

        for (List<Stat> stat : leftStats) {
            addStatButton(false, statx, staty, stat);

            statx += (HubStatButton.xSize + 5);
        }
        statx = 200;
        staty = 103;

        for (List<Stat> stat : rightStats) {
            addStatButton(true, statx, staty, stat);
            statx -= (HubStatButton.xSize + 5);
        }
        publicAddButton(new PlayerGearButton(mc.player, this, this.guiLeft + MainHubScreen.sizeX / 2 - PlayerGearButton.xSize / 2, this.guiTop + 10));

    }

    void addStatButton(boolean isright, int x, int y, List<Stat> stats) {

        int yadd = 0;
        for (Stat stat : stats) {
            var statdata = Load.Unit(mc.player).getUnit().getCalculatedStat(stat);
            publicAddButton(new HubStatButton(isright, statdata, this.guiLeft + x, this.guiTop + y + yadd));
            yadd += MainHubButton.ySize;
        }

    }

    private static final ResourceLocation BACKGROUND = new ResourceLocation(SlashRef.MODID, "textures/gui/stats.png");

    @Override
    public void render(GuiGraphics gui, int x, int y, float ticks) {

        ResourceLocation loc;

        loc = BACKGROUND;


        gui.setColor(1.0F, 1.0F, 1.0F, 1.0F);

        gui.blit(loc, mc.getWindow()
                        .getGuiScaledWidth() / 2 - sizeX / 2,
                mc.getWindow()
                        .getGuiScaledHeight() / 2 - sizeY / 2, 0, 0, sizeX, sizeY
        );

        super.render(gui, x, y, ticks);


        int p = PlayerPointsType.STATS.getFreePoints(mc.player);
        if (p > 0) {
            MutableComponent points = Gui.STATS_POINTS.locName().append(String.valueOf(p));
            gui.drawString(mc.font, points, guiLeft + 40 - mc.font.width(points) / 2, guiTop + 10, ChatFormatting.GREEN.getColor());
        }

        int lvl = Load.player(mc.player).miscInfo.area_lvl;
        MutableComponent areaLevel = Gui.AREA_LEVEL.locName().append(String.valueOf(lvl));
        gui.drawString(mc.font, areaLevel, guiLeft + sizeX / 2 - mc.font.width(areaLevel) / 2, guiTop + sizeY + 5, ChatFormatting.YELLOW.getColor());

    }


    static int PLUS_BUTTON_SIZE_X = 13;
    static int PLUS_BUTTON_SIZE_Y = 13;

    public static class AllocateStatButton extends ImageButton {
        static int SIZEX = 18;
        static int SIZEY = 18;
        static ResourceLocation BUTTON_TEX = new ResourceLocation(SlashRef.MODID, "textures/gui/plus_button.png");

        Stat stat;

        public AllocateStatButton(String stat, int xPos, int yPos) {
            super(xPos, yPos, SIZEX, SIZEY, 0, 0, SIZEY, BUTTON_TEX, (button) -> {
                //  Packets.sendToServer(new AllocateStatPacket(ExileDB.Stats()                        .get(stat)));
            });
            this.stat = ExileDB.Stats()
                    .get(stat);
        }

        @Override
        protected ClientTooltipPositioner createTooltipPositioner() {
            return DefaultTooltipPositioner.INSTANCE;
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {

            if (this.active && this.visible) {
                boolean bl = this.clicked(mouseX, mouseY);
                if (bl) {
                    this.playDownSound(Minecraft.getInstance().getSoundManager());
                    if (button == 0) {
                        Packets.sendToServer(new AllocateStatPacket(stat, AllocateStatPacket.ACTION.ALLOCATE));
                    }
                    if (button == 1) {
                        Packets.sendToServer(new AllocateStatPacket(stat, AllocateStatPacket.ACTION.REMOVE));
                    }
                    this.onClick(mouseX, mouseY);
                    return true;
                }
                return false;
            } else {
                return false;
            }
        }

        public void setTooltipMod() {


            Minecraft mc = Minecraft.getInstance();

            List<Component> tooltip = new ArrayList<>();

            tooltip.add(stat
                    .locName()
                    .withStyle(ChatFormatting.GREEN));

            tooltip.add(ExileText.ofText("").get());

            tooltip.addAll(((CoreStat) stat).getCoreStatTooltip(Load.Unit(mc.player), Load.Unit(mc.player)
                    .getUnit()
                    .getCalculatedStat(stat)));

            setTooltip(Tooltip.create(TextUTIL.mergeList(tooltip)));
        }


        @Override
        public void render(GuiGraphics gui, int x, int y, float f) {

            setTooltipMod();

            super.render(gui, x, y, f);

            Minecraft mc = Minecraft.getInstance();

            String txt = ((int) Load.Unit(mc.player)
                    .getUnit()
                    .getCalculatedStat(stat)
                    .getValue()) + "";

            RenderUtils.render16Icon(gui, stat.getIconForRendering(), this.getX() - 17, this.getY() + 1);

            gui.drawCenteredString(mc.font, txt, this.getX() + SIZEX + 13, this.getY() + 5, ChatFormatting.WHITE.getColor());

        }

        @Override
        public void onRelease(double pMouseX, double pMouseY) {
            this.setFocused(false);
        }

    }


}


