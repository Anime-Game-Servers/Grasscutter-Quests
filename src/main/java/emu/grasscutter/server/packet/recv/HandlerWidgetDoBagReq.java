package emu.grasscutter.server.packet.recv;

import emu.grasscutter.Grasscutter;
import emu.grasscutter.game.entity.EntityVehicle;
import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.PacketWidgetCoolDownNotify;
import emu.grasscutter.server.packet.send.PacketWidgetDoBagRsp;
import emu.grasscutter.server.packet.send.PacketWidgetGadgetDataNotify;
import emu.grasscutter.utils.Position;
import org.anime_game_servers.multi_proto.gi.messages.item.widget.use.WidgetDoBagReq;

public class HandlerWidgetDoBagReq extends TypedPacketHandler<WidgetDoBagReq> {

    @Override
    public void handle(GameSession session, byte[] header, WidgetDoBagReq req) throws Exception {
        if (req.getOpInfo() instanceof WidgetDoBagReq.OpInfo.WidgetCreatorInfo widgetCreatorInfo) {
            var locationInfo = widgetCreatorInfo.getValue().getLocationInfo();
            Position pos = new Position(locationInfo.getPos());
            Position rot = new Position(locationInfo.getRot());
            switch (req.getMaterialId()) {
                case 220026 -> {
                    this.spawnVehicle(session, 70500025, pos, rot);
                    session.send(new PacketWidgetCoolDownNotify(15, System.currentTimeMillis() + 5000L, true));
                    session.send(new PacketWidgetCoolDownNotify(15, System.currentTimeMillis() + 5000L, true));
                }
                case 220047 -> this.spawnVehicle(session, 70800058, pos, rot);
                default -> {
                }
            }
            session.send(new PacketWidgetDoBagRsp());
        } else if (req.getOpInfo() instanceof WidgetDoBagReq.OpInfo.LocationInfo locationInfo) {
            Grasscutter.getLogger().warn("Encountered unimplemented OneOfType LocationInfo in WidgetDoBagReq: {}", locationInfo);
        } else {
            Grasscutter.getLogger().warn("Encountered new(!!) OneOfType in WidgetDoBagReq");
        }
    }

    private void spawnVehicle(GameSession session, int gadgetId, Position pos, Position rot) {
        var player = session.getPlayer();
        var scene = player.getScene();
        var entity = new EntityVehicle(scene, player, gadgetId, 0, pos, rot);
        scene.addEntity(entity);
        session.send(new PacketWidgetGadgetDataNotify(gadgetId, entity.getId()));
    }
}
