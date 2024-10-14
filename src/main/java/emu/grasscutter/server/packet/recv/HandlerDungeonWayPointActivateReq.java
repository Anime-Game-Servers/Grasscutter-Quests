package emu.grasscutter.server.packet.recv;

import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.PacketDungeonWayPointActivateRsp;
import org.anime_game_servers.multi_proto.gi.messages.dungeon.progression.DungeonWayPointActivateReq;

public class HandlerDungeonWayPointActivateReq extends TypedPacketHandler<DungeonWayPointActivateReq> {

    @Override
    public void handle(GameSession session, byte[] header, DungeonWayPointActivateReq req) throws Exception {
        var dungeonManager = session.getPlayer().getScene().getDungeonManager();

        boolean success = false;
        if(dungeonManager != null){
            success = dungeonManager.activateRespawnPoint(req.getWayPointId());
        }

        session.send(new PacketDungeonWayPointActivateRsp(success, req.getWayPointId()));
    }

}
