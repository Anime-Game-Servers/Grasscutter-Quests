package emu.grasscutter.server.packet.recv;

import emu.grasscutter.game.player.Player;
import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.PacketSetWidgetSlotRsp;
import emu.grasscutter.server.packet.send.PacketWidgetSlotChangeNotify;
import org.anime_game_servers.multi_proto.gi.messages.item.widget.manage_slot.SetWidgetSlotReq;
import org.anime_game_servers.multi_proto.gi.messages.item.widget.manage_slot.WidgetSlotOp;

public class HandlerSetWidgetSlotReq extends TypedPacketHandler<SetWidgetSlotReq> {
    @Override
    public void handle(GameSession session, byte[] header, SetWidgetSlotReq req) throws Exception {
        Player player = session.getPlayer();
        player.setWidgetId(req.getMaterialId());

        // WidgetSlotChangeNotify op & slot key
        session.send(new PacketWidgetSlotChangeNotify(WidgetSlotOp.WIDGET_SLOT_OP_DETACH));

        //only attaching the widget can set it
        if (req.getOp() == WidgetSlotOp.WIDGET_SLOT_OP_ATTACH) {
            // WidgetSlotChangeNotify slot
            session.send(new PacketWidgetSlotChangeNotify(req.getMaterialId()));
        }

        // SetWidgetSlotRsp
        session.send(new PacketSetWidgetSlotRsp(req.getMaterialId()));
    }
}
