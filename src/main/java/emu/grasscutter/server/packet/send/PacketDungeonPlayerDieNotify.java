package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.dungeon.DungeonPlayerDieNotify;
import org.anime_game_servers.multi_proto.gi.messages.general.PlayerDieType;

public class PacketDungeonPlayerDieNotify extends BaseTypedPacket<DungeonPlayerDieNotify> {
    public PacketDungeonPlayerDieNotify(PlayerDieType playerDieType, int killerId, int dungeonId, int waitTime, int reviveCount, boolean isGadget) {
        super(new DungeonPlayerDieNotify());
        proto.setDieType(playerDieType);
        proto.setReviveCount(reviveCount);
        proto.setWaitTime(waitTime);
        proto.setDungeonId(dungeonId);
        proto.setMurdererEntityId(killerId);

        if (isGadget) {
            DungeonPlayerDieNotify.Entity.GadgetId gadgetId = new DungeonPlayerDieNotify.Entity.GadgetId(killerId);
            proto.setEntity(gadgetId);
        } else {
            DungeonPlayerDieNotify.Entity.MonsterId monsterId = new DungeonPlayerDieNotify.Entity.MonsterId(killerId);
            proto.setEntity(monsterId);
        }
    }
}
