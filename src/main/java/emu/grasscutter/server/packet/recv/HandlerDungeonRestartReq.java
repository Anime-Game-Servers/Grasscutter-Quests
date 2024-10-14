package emu.grasscutter.server.packet.recv;

import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.PacketDungeonRestartRsp;
import org.anime_game_servers.multi_proto.gi.messages.dungeon.progression.DungeonRestartReq;

public class HandlerDungeonRestartReq extends TypedPacketHandler<DungeonRestartReq> {
    @Override
    public void handle(GameSession session, byte[] header, DungeonRestartReq req) throws Exception {
        session.getServer().getDungeonSystem().restartDungeon(session.getPlayer());
        session.send(new PacketDungeonRestartRsp());
    }
}
