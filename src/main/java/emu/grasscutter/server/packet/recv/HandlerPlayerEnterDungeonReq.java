package emu.grasscutter.server.packet.recv;

import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.PacketPlayerEnterDungeonRsp;
import lombok.val;
import org.anime_game_servers.multi_proto.gi.messages.dungeon.PlayerEnterDungeonReq;

public class HandlerPlayerEnterDungeonReq extends TypedPacketHandler<PlayerEnterDungeonReq> {

    @Override
    public void handle(GameSession session, byte[] header, PlayerEnterDungeonReq req) throws Exception {
        val success = session.getServer().getDungeonSystem().enterDungeon(session.getPlayer(), req.getPointId(), req.getDungeonId());
        session.getPlayer().sendPacket(new PacketPlayerEnterDungeonRsp(req.getPointId(), req.getDungeonId(), success));
    }

}
