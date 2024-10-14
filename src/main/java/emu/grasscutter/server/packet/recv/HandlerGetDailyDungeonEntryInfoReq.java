package emu.grasscutter.server.packet.recv;

import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.PacketGetDailyDungeonEntryInfoRsp;
import org.anime_game_servers.multi_proto.gi.messages.dungeon.entry.GetDailyDungeonEntryInfoReq;

public class HandlerGetDailyDungeonEntryInfoReq extends TypedPacketHandler<GetDailyDungeonEntryInfoReq> {

    @Override
    public void handle(GameSession session, byte[] header, GetDailyDungeonEntryInfoReq req) throws Exception {
        session.send(new PacketGetDailyDungeonEntryInfoRsp(session.getPlayer(), req.getSceneId()));
    }
}
