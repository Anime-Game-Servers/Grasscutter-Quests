package emu.grasscutter.server.packet.recv;

import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.PacketFireworksLaunchDataNotify;
import emu.grasscutter.server.packet.send.PacketLaunchFireworksRsp;
import org.anime_game_servers.multi_proto.gi.messages.item.widget.firework.LaunchFireworksReq;

public class HandlerLaunchFireworksReq extends TypedPacketHandler<LaunchFireworksReq> {
    @Override
    public void handle(GameSession session, byte[] header, LaunchFireworksReq req) throws Exception {
        session.send(new PacketFireworksLaunchDataNotify(req.getSchemeData()));
        session.send(new PacketLaunchFireworksRsp());
    }
}
