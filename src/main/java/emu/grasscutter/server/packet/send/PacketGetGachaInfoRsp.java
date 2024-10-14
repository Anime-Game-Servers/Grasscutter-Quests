package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.gacha.GachaSystem;
import emu.grasscutter.game.player.Player;
import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.wishing.GetGachaInfoRsp;

public class PacketGetGachaInfoRsp extends BaseTypedPacket<GetGachaInfoRsp> {
    public PacketGetGachaInfoRsp(GachaSystem manager, Player player) {
        super(manager.toProto(player));
    }
}
