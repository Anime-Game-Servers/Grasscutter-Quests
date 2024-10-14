package emu.grasscutter.server.packet.recv;

import emu.grasscutter.net.packet.BaseTypedPacket;
import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import org.anime_game_servers.multi_proto.gi.messages.dungeon.PlayerQuitDungeonReq;
import org.anime_game_servers.multi_proto.gi.messages.dungeon.PlayerQuitDungeonRsp;

public class HandlerPlayerQuitDungeonReq extends TypedPacketHandler<PlayerQuitDungeonReq> {

    @Override
    public void handle(GameSession session, byte[] header, PlayerQuitDungeonReq req) throws Exception {
        session.getPlayer().getServer().getDungeonSystem().exitDungeon(session.getPlayer(), req.isQuitImmediately());
        session.getPlayer().sendPacket(new BaseTypedPacket<>(new PlayerQuitDungeonRsp()) {
        });
    }
}
