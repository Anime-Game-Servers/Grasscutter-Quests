package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.item.cooking.CookDataNotify;
import org.anime_game_servers.multi_proto.gi.messages.item.cooking.CookRecipeData;

import java.util.List;

public class PacketCookDataNotify extends BaseTypedPacket<CookDataNotify> {
    public PacketCookDataNotify(List<CookRecipeData> recipes) {
        super(new CookDataNotify());
        proto.setRecipeDataList(recipes);
    }
}
