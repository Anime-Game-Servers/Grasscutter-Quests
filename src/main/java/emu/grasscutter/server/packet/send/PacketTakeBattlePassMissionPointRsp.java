package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.battle_pass.TakeBattlePassMissionPointRsp;

public class PacketTakeBattlePassMissionPointRsp extends BaseTypedPacket<TakeBattlePassMissionPointRsp> {
    public PacketTakeBattlePassMissionPointRsp() {
        super(new TakeBattlePassMissionPointRsp()); // TODO also send missions?
    }
}
