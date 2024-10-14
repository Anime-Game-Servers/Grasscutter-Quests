package emu.grasscutter.server.packet.recv;

import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.PacketPlayerHomeCompInfoNotify;
import emu.grasscutter.server.packet.send.PacketSetFriendEnterHomeOptionRsp;
import org.anime_game_servers.multi_proto.gi.messages.serenitea_pot.player.SetFriendEnterHomeOptionReq;

public class HandlerSetFriendEnterHomeOptionReq extends TypedPacketHandler<SetFriendEnterHomeOptionReq> {
    @Override
    public void handle(GameSession session, byte[] header, SetFriendEnterHomeOptionReq req) throws Exception {
        session.getPlayer().getHome().setEnterHomeOption(req.getOption().ordinal());
        session.getPlayer().getHome().save();
        session.send(new PacketPlayerHomeCompInfoNotify(session.getPlayer()));
        session.send(new PacketSetFriendEnterHomeOptionRsp());
    }
}
