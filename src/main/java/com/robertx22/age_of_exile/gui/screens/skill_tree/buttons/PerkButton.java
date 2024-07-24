package com.robertx22.age_of_exile.gui.screens.skill_tree.buttons;

import com.mojang.blaze3d.systems.RenderSystem;
import com.robertx22.age_of_exile.capability.player.PlayerData;
import com.robertx22.age_of_exile.database.data.perks.Perk;
import com.robertx22.age_of_exile.database.data.perks.PerkStatus;
import com.robertx22.age_of_exile.database.data.stats.types.UnknownStat;
import com.robertx22.age_of_exile.database.data.talent_tree.TalentTree;
import com.robertx22.age_of_exile.gui.screens.skill_tree.PainterController;
import com.robertx22.age_of_exile.gui.screens.skill_tree.SkillTreeScreen;
import com.robertx22.age_of_exile.gui.screens.skill_tree.buttons.drawer.DrawInformation;
import com.robertx22.age_of_exile.gui.screens.skill_tree.buttons.drawer.PerkButtonPainter;
import com.robertx22.age_of_exile.gui.screens.skill_tree.connections.PerkConnectionCache;
import com.robertx22.age_of_exile.mmorpg.MMORPG;
import com.robertx22.age_of_exile.mmorpg.SlashRef;
import com.robertx22.age_of_exile.saveclasses.PointData;
import com.robertx22.age_of_exile.saveclasses.gearitem.gear_bases.TooltipInfo;
import com.robertx22.age_of_exile.uncommon.MathHelper;
import com.robertx22.age_of_exile.vanilla_mc.packets.perks.PerkChangePacket;
import com.robertx22.library_of_exile.main.Packets;
import com.robertx22.library_of_exile.utils.GuiUtils;
import com.robertx22.library_of_exile.utils.TextUTIL;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import java.util.List;
import java.util.concurrent.CompletableFuture;

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
    SkillTreeScreen screen;
    private ResourceLocation wholeTexture = null;
    private CompletableFuture<Void> drawTextureTask = null;


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

    private PerkStatus status = null;

    @Override
    public void renderWidget(GuiGraphics gui, int mouseX, int mouseY, float pPartialTick) {


        if (!screen.shouldRender(origX, origY, screen.ctx)) {
            return;
        }

        setTooltipMOD(gui, mouseX, mouseY);


        gui.pose().pushPose();

        float scale = 2 - screen.zoom;
        float target = scale * 1.3f;
        if (isInside((int) (1F / screen.zoom * mouseX), (int) (1F / screen.zoom * mouseY))) {
            float lerp = Mth.lerp(0.5f, scale, target);
            scale = lerp;
            if (lerp / scale * 0.15f > 0.999f) {
                scale = target;
            }
        }

        float posMulti = 1F / scale;


        float add = MathHelper.clamp(scale - 1, 0, 2);
        float off = width / -2F * add;

        gui.pose().translate(off, off, 0);
        gui.pose().scale(scale, scale, scale);

        Perk.PerkType type1 = perk.getType();

        PerkStatus status = playerData.talents.getStatus(Minecraft.getInstance().player, school, point);

        float offset = type1.getOffset();

        // background

        RenderSystem.enableDepthTest();

        var search = SkillTreeScreen.SEARCH.getValue();

        boolean containsSearchStat = perk.stats.stream()
                .anyMatch(item -> item.getStat().locName().getString().toLowerCase().contains(search.toLowerCase()));

        boolean containsName = perk.locName().getString().toLowerCase().contains(search.toLowerCase());


        float opacity = search.isEmpty() || containsSearchStat || containsName ? 1F : 0.2f;


        if (!search.isEmpty()) {
            if (search.equals("all")) {
                if (status != PerkStatus.CONNECTED) {
                    opacity = 0.2F;
                } else {
                    opacity = 1;
                }
            }
        } else {
            opacity = status.getOpacity();
        }

        // if newbie, show only the starter perks he can pick
        if (playerData.talents.getAllocatedPoints(TalentTree.SchoolType.TALENTS) < 1) {
            opacity = type1 == Perk.PerkType.START ? 1 : 0.2F;
        }

        var type = perk.type;

        //gui.blit(ID, xPos(0, posMulti), yPos(0, posMulti), perk.getType().getXOffset(), status.getYOffset(), this.width, this.height);

        int offcolor = (int) ((type1.size - 20) / 2F);

        gui.setColor(1.0F, 1.0F, 1.0F, opacity);


        DrawInformation drawInformation = new DrawInformation(perk);
        if (status != this.status){
            ResourceLocation colorTexture = type1.getColorTexture(status);
            ResourceLocation borderTexture = type1.getBorderTexture(status);
            ResourceLocation perkIcon = perk.getIcon();
            this.wholeTexture = new ResourceLocation(PainterController.nameSpace, colorTexture.getPath() + "_" + borderTexture.getPath() + "_" + perkIcon.getPath());
            this.status = status;
        }

        try {
            gui.blit(this.wholeTexture, (int) xPos(0, posMulti), (int) yPos(0, posMulti), 0, 0, this.width, this.height, this.width, this.height);
            if (search.isEmpty()) {
                opacity += 0.1F;
            }
            gui.setColor(1.0F, 1.0F, 1.0F, MathHelper.clamp(opacity, 0, 1));
            gui.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        } catch (RuntimeException ignored){

            PerkButtonPainter.addToWait(drawInformation);

            ResourceLocation colorTexture = type1.getColorTexture(status);
            ResourceLocation borderTexture = type1.getBorderTexture(status);
            ResourceLocation perkIcon = perk.getIcon();

            gui.blit(colorTexture, xPos(offcolor, posMulti), yPos(offcolor, posMulti), 20, 20, 0, 0, 20, 20, 20, 20);
            gui.blit(borderTexture, (int) xPos(0, posMulti), (int) yPos(0, posMulti), 0, 0, this.width, this.height, this.width, this.height);

            if (search.isEmpty()) {
                opacity += 0.2F;
            }

            gui.blit(perkIcon, (int) xPos(offset, posMulti), (int) yPos(offset, posMulti), 0, 0, type.iconSize, type.iconSize, type.iconSize, type.iconSize);

            gui.setColor(1.0F, 1.0F, 1.0F, MathHelper.clamp(opacity, 0, 1));

            //   gui.pose().scale(1F / scale, 1F / scale, 1F / scale);
            gui.setColor(1.0F, 1.0F, 1.0F, 1.0F);

        }


        gui.pose().popPose();

    }


}