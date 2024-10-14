package emu.grasscutter.server.packet.recv;

import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.PacketDungeonSlipRevivePointActivateRsp;
import org.anime_game_servers.multi_proto.gi.messages.dungeon.DungeonSlipRevivePointActivateReq;

public class HandlerDungeonSlipRevivePointActivateReq extends TypedPacketHandler<DungeonSlipRevivePointActivateReq> {

    @Override
    public void handle(GameSession session, byte[] header, DungeonSlipRevivePointActivateReq req) throws Exception {
        var dungeonManager = session.getPlayer().getScene().getDungeonManager();

        boolean success = false;
        if (dungeonManager != null) {
            success = dungeonManager.activateRespawnPoint(req.getSlipRevivePointId());
        }

        session.send(new PacketDungeonSlipRevivePointActivateRsp(success, req.getSlipRevivePointId()));
    }

}
