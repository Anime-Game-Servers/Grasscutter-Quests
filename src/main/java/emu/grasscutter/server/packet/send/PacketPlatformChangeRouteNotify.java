package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.entity.EntityGadget;
import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.scene.PlatformChangeRouteNotify;

public class PacketPlatformChangeRouteNotify extends BaseTypedPacket<PlatformChangeRouteNotify> {

    public PacketPlatformChangeRouteNotify(EntityGadget gadgetEntity) {
        super(new PlatformChangeRouteNotify(gadgetEntity.getId(), gadgetEntity.getPlatformInfo(),
            gadgetEntity.getScene().getSceneTime()));
    }

}
