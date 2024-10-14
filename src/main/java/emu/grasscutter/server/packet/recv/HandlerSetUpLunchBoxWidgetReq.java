package emu.grasscutter.server.packet.recv;

import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.PacketSetUpLunchBoxWidgetRsp;
import org.anime_game_servers.multi_proto.gi.messages.item.widget.lunch_box.SetUpLunchBoxWidgetReq;

public class HandlerSetUpLunchBoxWidgetReq extends TypedPacketHandler<SetUpLunchBoxWidgetReq> {
    @Override
    public void handle(GameSession session, byte[] header, SetUpLunchBoxWidgetReq req) throws Exception {
        session.send(new PacketSetUpLunchBoxWidgetRsp(req.getLunchBoxData()));
    }
}
