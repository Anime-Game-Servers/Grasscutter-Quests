package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.entity.EntityGadget;
import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.scene.PlatformStartRouteNotify;

public class PacketPlatformStartRouteNotify extends BaseTypedPacket<PlatformStartRouteNotify> {
    public PacketPlatformStartRouteNotify(EntityGadget gadgetEntity) {
        super(new PlatformStartRouteNotify(gadgetEntity.getId(), gadgetEntity.getPlatformInfo(),
            gadgetEntity.getScene().getSceneTime()));
    }
}
