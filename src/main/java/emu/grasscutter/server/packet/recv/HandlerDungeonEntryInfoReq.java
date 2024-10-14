package emu.grasscutter.server.packet.recv;

import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.PacketDungeonEntryInfoRsp;
import org.anime_game_servers.multi_proto.gi.messages.dungeon.entry.DungeonEntryInfoReq;
import org.anime_game_servers.core.base.Version;

public class HandlerDungeonEntryInfoReq extends TypedPacketHandler<DungeonEntryInfoReq> {

    @Override
    public void handle(GameSession session, byte[] header, DungeonEntryInfoReq req) throws Exception {
        if (session.getVersion().getId() >= Version.GI_2_7_0.getId()) {
            //sceneId was added in version 2.7
            session.getPlayer().sendPacket(new PacketDungeonEntryInfoRsp(session.getPlayer(), req.getSceneId(), req.getPointId()));
        } else {
            //hacky hack is to assume it is the scene the player is in
            session.getPlayer().sendPacket(new PacketDungeonEntryInfoRsp(session.getPlayer(), session.getPlayer().getSceneId(), req.getPointId()));
        }
    }
}
