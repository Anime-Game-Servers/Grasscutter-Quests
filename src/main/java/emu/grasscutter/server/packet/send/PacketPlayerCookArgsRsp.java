package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.item.cooking.PlayerCookArgsRsp;

public class PacketPlayerCookArgsRsp extends BaseTypedPacket<PlayerCookArgsRsp> {
    public PacketPlayerCookArgsRsp() {
        super(new PlayerCookArgsRsp());
    }
}
