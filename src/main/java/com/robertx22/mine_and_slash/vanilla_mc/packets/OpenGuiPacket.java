package com.robertx22.mine_and_slash.vanilla_mc.packets;

import com.robertx22.library_of_exile.main.MyPacket;
import com.robertx22.library_of_exile.packets.ExilePacketContext;
import com.robertx22.mine_and_slash.mmorpg.SlashRef;
import com.robertx22.mine_and_slash.vanilla_mc.packets.proxies.OpenGuiWrapper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

// todo pick curse gui
public class OpenGuiPacket extends MyPacket<OpenGuiPacket> {

    public enum GuiType {
        PICK_PROPHECY_CURSE,
        MAIN_HUB;


    }

    GuiType type;

    public OpenGuiPacket() {

    }

    public OpenGuiPacket(GuiType type) {
        this.type = type;
    }

    @Override
    public void loadFromData(FriendlyByteBuf buf) {
        type = GuiType.valueOf(buf.readUtf());
    }

    @Override
    public void saveToData(FriendlyByteBuf buf) {
        buf.writeUtf(type.name());
    }

    @Override
    public void onReceived(ExilePacketContext ctx) {
        if (type == GuiType.MAIN_HUB) {
            OpenGuiWrapper.openMainHub();
        }
        if (type == GuiType.PICK_PROPHECY_CURSE) {
            OpenGuiWrapper.openProphecyCards();
        }

    }

    @Override
    public MyPacket<OpenGuiPacket> newInstance() {
        return new OpenGuiPacket();
    }

    @Override
    public ResourceLocation getIdentifier() {
        return new ResourceLocation(SlashRef.MODID, "opengui");
    }

}