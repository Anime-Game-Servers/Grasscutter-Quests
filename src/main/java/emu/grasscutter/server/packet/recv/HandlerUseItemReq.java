package emu.grasscutter.server.packet.recv;

import emu.grasscutter.game.inventory.GameItem;
import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.PacketUseItemRsp;
import org.anime_game_servers.multi_proto.gi.messages.item.UseItemReq;

public class HandlerUseItemReq extends TypedPacketHandler<UseItemReq> {
    @Override
    public void handle(GameSession session, byte[] header, UseItemReq req) throws Exception {
        GameItem useItem = session.getServer().getInventorySystem().useItem(session.getPlayer(), req.getTargetGuid(), req.getGuid(), req.getCount(), req.getOptionIdx(), req.isEnterMpDungeonTeam());
        if (useItem != null) {
            session.send(new PacketUseItemRsp(req.getTargetGuid(), useItem));
        } else {
            session.send(new PacketUseItemRsp());
        }
    }
}
