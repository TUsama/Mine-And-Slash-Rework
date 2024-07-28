package com.robertx22.age_of_exile.gui.screens.skill_tree.buttons;

import com.mojang.blaze3d.systems.RenderSystem;
import com.robertx22.age_of_exile.capability.player.PlayerData;
import com.robertx22.age_of_exile.database.data.perks.Perk;
import com.robertx22.age_of_exile.database.data.perks.PerkStatus;
import com.robertx22.age_of_exile.database.data.stats.types.UnknownStat;
import com.robertx22.age_of_exile.database.data.talent_tree.TalentTree;
import com.robertx22.age_of_exile.gui.screens.skill_tree.OpacityController;
import com.robertx22.age_of_exile.gui.screens.skill_tree.SkillTreeScreen;
import com.robertx22.age_of_exile.gui.screens.skill_tree.buttons.drawer.ButtonIdentifier;
import com.robertx22.age_of_exile.gui.screens.skill_tree.buttons.drawer.PerkButtonPainter;
import com.robertx22.age_of_exile.gui.screens.skill_tree.connections.PerkConnectionCache;
import com.robertx22.age_of_exile.gui.screens.skill_tree.connections.PerkConnectionRenderer;
import com.robertx22.age_of_exile.mmorpg.MMORPG;
import com.robertx22.age_of_exile.mmorpg.SlashRef;
import com.robertx22.age_of_exile.saveclasses.PointData;
import com.robertx22.age_of_exile.saveclasses.gearitem.gear_bases.TooltipInfo;
import com.robertx22.age_of_exile.uncommon.MathHelper;
import com.robertx22.age_of_exile.uncommon.datasaving.Load;
import com.robertx22.age_of_exile.uncommon.utilityclasses.ClientOnly;
import com.robertx22.age_of_exile.vanilla_mc.packets.perks.PerkChangePacket;
import com.robertx22.library_of_exile.main.Packets;
import com.robertx22.library_of_exile.utils.GuiUtils;
import com.robertx22.library_of_exile.utils.TextUTIL;
import com.robertx22.library_of_exile.utils.Watch;
import it.unimi.dsi.fastutil.ints.Int2ReferenceOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class PerkButton extends ImageButton {

    public static int SPACING = 26;
    public static int BIGGEST = 33;
    public static ResourceLocation LOCKED_TEX = new ResourceLocation(SlashRef.MODID, "textures/gui/locked.png");
    static ResourceLocation ID = new ResourceLocation(SlashRef.MODID, "textures/gui/skill_tree/perk_buttons.png");
    public Perk perk;
    public PointData point;
    public TalentTree school;
    public PlayerData playerData;

    public int originalWidth;
    public int originalHeight;

    public int origX;
    public int origY;
    public String perkid = "";
    public ButtonIdentifier buttonIdentifier;
    SkillTreeScreen screen;
    private ResourceLocation wholeTexture = null;
    private PerkStatus status;

    private final List<String> matchStrings;

    private boolean searchCache = false;

    private String lastSearchString = "";

    public PerkButton(SkillTreeScreen screen, PlayerData playerData, TalentTree school, PointData point, Perk perk, int x, int y) {
        super(x, y, perk.getType().size, perk.getType().size, 0, 0, 1, ID, (action) -> {
        });
        this.perk = perk;
        this.point = point;
        this.school = school;
        this.playerData = playerData;

        this.origX = x;
        this.origY = y;
        this.originalWidth = this.width;
        this.originalHeight = this.height;
        this.screen = screen;

        this.status = playerData.talents.getStatus(Minecraft.getInstance().player, school, point);

        Perk.PerkType type = this.perk.getType();
        ResourceLocation colorTexture = type.getColorTexture(status);
        ResourceLocation borderTexture = type.getBorderTexture(status);
        ResourceLocation perkIcon = perk.getIcon();
        this.wholeTexture = PerkButtonPainter.getNewLocation(colorTexture, borderTexture, perkIcon);
        this.buttonIdentifier = new ButtonIdentifier(school, point, perk);

        this.matchStrings = perk.stats.stream()
                .map(item -> item.getStat().locName().getString().toLowerCase()).toList();

    }

    public ResourceLocation getWholeTexture() {
        return wholeTexture;
    }

    public boolean isInside(int x, int y) {

        float scale = 2 - screen.zoom;
        return GuiUtils.isInRect((int) (this.getX() - ((width / 4) * scale)), (int) (this.getY() - ((height / 4) * scale)), (int) (width * scale), (int) (height * scale), x, y);
    }

    private void setTooltipMOD(GuiGraphics gui, int mouseX, int mouseY) {

        int MmouseX = (int) (1F / screen.zoom * mouseX);
        int MmouseY = (int) (1F / screen.zoom * mouseY);

        if (this.isInside(MmouseX, MmouseY)) {

            List<Component> tooltip = perk.GetTooltipString(new TooltipInfo(Minecraft.getInstance().player));

            if (perk.stats.stream().anyMatch(x -> x.stat.equals(new UnknownStat().GUID()))) {
                tooltip.add(Component.literal("No Perk of this ID found: " + perkid));
            } else {
                if (MMORPG.RUN_DEV_TOOLS || Screen.hasShiftDown()) {
                    tooltip.add(Component.literal("Perk ID: " + perkid));
                }
            }

            setTooltip(Tooltip.create(TextUTIL.mergeList(tooltip)));

            Screen screen = Minecraft.getInstance().screen;
            if (screen != null) {
                screen.setTooltipForNextRenderPass(this.getTooltip(), this.createTooltipPositioner(), true);
            }
            //GuiUtils.renderTooltip(gui, tooltip, mouseX, mouseY);
        } else {
            setTooltip(null);
        }
    }

    // copied from abstractbutton
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        //this.wholeTexture = null;

        screen.mouseRecentlyClickedTicks = 25;
        screen.pointClicked = this.point;

        mouseX = 1F / screen.zoom * mouseX;
        mouseY = 1F / screen.zoom * mouseY;

        if (this.active && this.visible) {
            boolean bl = this.clicked(mouseX, mouseY);
            if (bl) {
//                System.out.println(this.getX() + "_" + getY() + " : " + perk.GUID());


                this.playDownSound(Minecraft.getInstance()
                        .getSoundManager());

                if (button == 0) {
                    Packets.sendToServer(new PerkChangePacket(school, point, PerkChangePacket.ACTION.ALLOCATE));
                }
                if (button == 1) {
                    Packets.sendToServer(new PerkChangePacket(school, point, PerkChangePacket.ACTION.REMOVE));
                }
                this.onClick(mouseX, mouseY);

                PerkConnectionCache.addToUpdate(this);
                System.out.println("this point is: " + getX() + " " + getY());
                ArrayList<PointData> pointData = new ArrayList<>(screen.school.calcData.connections.get(point));
                System.out.println("this point relate to these point: " + pointData);
                System.out.println("related renderers' hash: " + pointData.stream().map(x -> new PerkPointPair(point, x).hashCode()).toList());
                ArrayList<PerkConnectionRenderer> perkConnectionRenderers = new ArrayList<>();
                Int2ReferenceOpenHashMap<PerkConnectionRenderer> perkConnectionRendererInt2ReferenceOpenHashMap = PerkConnectionCache.renderersCache.get(screen.schoolType.toString().hashCode());
                Set<PointData> connections = screen.school.calcData.connections.getOrDefault(this.point, Collections.EMPTY_SET);

                for (PointData p : connections) {

                    PerkButton sb = screen.pointPerkButtonMap.get(p);
                    PerkPointPair pair = new PerkPointPair(this.point, sb.point);

                    var con = Load.player(ClientOnly.getPlayer()).talents.getConnection(screen.school, sb.point, this.point);
                    var result = new PerkConnectionRenderer(pair, con);
                    perkConnectionRenderers.add(perkConnectionRendererInt2ReferenceOpenHashMap.get(result.hashCode()));
                }

                System.out.println("this point has these renderers in cache: " + perkConnectionRenderers);
                System.out.println("related renderers' hash are: " + perkConnectionRenderers.stream().map(x -> x.hashCode()).toList());
                return true;
            }

            return false;
        } else {
            return false;
        }
    }

    int xPos(float offset, float multi) {
        return (int) ((this.getX() * multi) + offset);

    }

    int yPos(float offset, float multi) {
        return (int) ((getY() * multi) + offset);
    }

    @Override
    public void renderWidget(GuiGraphics gui, int mouseX, int mouseY, float pPartialTick) {

        if (!screen.shouldRender(origX, origY, screen.ctx)) return;

        setTooltipMOD(gui, mouseX, mouseY);


        if (screen.painter.isAllowedToPaint()) {
            var search = SkillTreeScreen.SEARCH.getValue();
            int MmouseX = (int) (1F / screen.zoom * mouseX);
            int MmouseY = (int) (1F / screen.zoom * mouseY);
            //System.out.println("1");
            if (search.isEmpty() && !isInside(MmouseX, MmouseY)) return;
            boolean shouldUseNormalRender;
            //System.out.println("2");
            //check if this is a valid search result.

            if (!this.lastSearchString.equals(search)){
                boolean containsSearchStat = matchStrings.stream().anyMatch(x -> x.contains(search.toLowerCase()));
                boolean containsName = perk.locName().getString().toLowerCase().contains(search.toLowerCase());
                this.searchCache = containsSearchStat || containsName;
            }
            shouldUseNormalRender = this.searchCache;

            if (search.equals("all")) {
                if (status == PerkStatus.CONNECTED) {
                    shouldUseNormalRender = true;
                }
            }

            if (playerData.talents.getAllocatedPoints(TalentTree.SchoolType.TALENTS) < 1) {
                Perk.PerkType type = perk.getType();
                shouldUseNormalRender = shouldUseNormalRender || type == Perk.PerkType.START;
            }

            // the principle of this whole image render is keep the whole image at a low opacity, and check if any button needs to use normal render system.
            if (shouldUseNormalRender || isInside(MmouseX, MmouseY)){
                gui.pose().pushPose();
                normalRender(gui, mouseX, mouseY);
                gui.pose().popPose();
            }

        } else {
            gui.pose().pushPose();
            normalRender(gui, mouseX, mouseY);
            gui.pose().popPose();
        }


    }

    private void normalRender(GuiGraphics gui, int mouseX, int mouseY) {
        float scale = this.getSmoothScale(mouseX, mouseY);

        float posMulti = 1F / scale;


        float add = MathHelper.clamp(scale - 1, 0, 2);
        float off = width / -2F * add;

        gui.pose().translate(off, off, 0);
        gui.pose().scale(scale, scale, scale);

        Perk.PerkType type = perk.getType();

        PerkStatus status = playerData.talents.getStatus(Minecraft.getInstance().player, school, point);

        float offset = type.getOffset();

        // background

        RenderSystem.enableDepthTest();

        OpacityController opacityController = OpacityController.normalCheck(this);
        // if newbie, show only the starter perks he can pick
        opacityController.newbieCheck();


        //gui.blit(ID, xPos(0, posMulti), yPos(0, posMulti), perk.getType().getXOffset(), status.getYOffset(), this.width, this.height);

        int offcolor = (int) ((type.size - 20) / 2F);

        gui.setColor(1.0F, 1.0F, 1.0F, opacityController.get());


        ButtonIdentifier buttonIdentifier = new ButtonIdentifier(this.school, point, perk);
        if (this.wholeTexture == null || status != this.status) {
            ResourceLocation colorTexture = type.getColorTexture(status);
            ResourceLocation borderTexture = type.getBorderTexture(status);
            ResourceLocation perkIcon = perk.getIcon();
            this.wholeTexture = PerkButtonPainter.getNewLocation(colorTexture, borderTexture, perkIcon);
            this.status = status;
        }

        if (PerkButtonPainter.handledBufferedImage.containsKey(this.wholeTexture)) {

            gui.setColor(1.0F, 1.0F, 1.0F, opacityController.highlightPerk().get());

            gui.blit(this.wholeTexture, (int) xPos(0, posMulti), (int) yPos(0, posMulti), 0, 0, this.width, this.height, this.width, this.height);

            gui.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        } else {

            PerkButtonPainter.addToWait(buttonIdentifier);

            ResourceLocation colorTexture = type.getColorTexture(status);
            ResourceLocation borderTexture = type.getBorderTexture(status);
            ResourceLocation perkIcon = perk.getIcon();

            gui.blit(colorTexture, xPos(offcolor, posMulti), yPos(offcolor, posMulti), 20, 20, 0, 0, 20, 20, 20, 20);
            gui.blit(borderTexture, (int) xPos(0, posMulti), (int) yPos(0, posMulti), 0, 0, this.width, this.height, this.width, this.height);


            gui.setColor(1.0F, 1.0F, 1.0F, opacityController.highlightPerk().get());

            gui.blit(perkIcon, (int) xPos(offset, posMulti), (int) yPos(offset, posMulti), 0, 0, type.iconSize, type.iconSize, type.iconSize, type.iconSize);


            //   gui.pose().scale(1F / scale, 1F / scale, 1F / scale);
            gui.setColor(1.0F, 1.0F, 1.0F, 1.0F);

        }
    }

    private float getSmoothScale(int mouseX, int mouseY) {
        float scale = 2 - screen.zoom;
        if (isInside((int) (1F / screen.zoom * mouseX), (int) (1F / screen.zoom * mouseY))) {
            scale = MathHelper.clamp(scale * 1.3f, 1.7f, 2.0f);
        }

        return scale;
    }


}