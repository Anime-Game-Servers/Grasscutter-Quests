package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.battle.MonsterForceAlertNotify;

//Sets openState to value
public class PacketMonsterForceAlertNotify extends BaseTypedPacket<MonsterForceAlertNotify> {
    public PacketMonsterForceAlertNotify(int monsterId) {
        super(new MonsterForceAlertNotify());
        proto.setMonsterEntityId(monsterId);
    }
}
