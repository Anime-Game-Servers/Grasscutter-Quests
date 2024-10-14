package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.entity.EntityGadget;
import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.scene.PlatformStopRouteNotify;

public class PacketPlatformStopRouteNotify extends BaseTypedPacket<PlatformStopRouteNotify> {
    public PacketPlatformStopRouteNotify(EntityGadget gadgetEntity) {
        super(new PlatformStopRouteNotify(gadgetEntity.getId(), gadgetEntity.getScene().getSceneTime(),
            gadgetEntity.getPlatformInfo()));
    }
}
