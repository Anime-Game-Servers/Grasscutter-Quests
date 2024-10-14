package emu.grasscutter.server.packet.recv;

import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.PacketHomeSceneJumpRsp;
import lombok.val;
import org.anime_game_servers.multi_proto.gi.messages.serenitea_pot.scene.HomeSceneJumpReq;

public class HandlerHomeSceneJumpReq extends TypedPacketHandler<HomeSceneJumpReq> {
    @Override
    public void handle(GameSession session, byte[] header, HomeSceneJumpReq req) throws Exception {
        int realmId = 2000 + session.getPlayer().getCurrentRealmId();
        val home = session.getPlayer().getHome();
        val homeScene = home.getHomeSceneItem(realmId);
        home.save();

        // the function should be able to get pos and rot from scriptManager config
        session.getPlayer().getWorld().transferPlayerToScene(
            session.getPlayer(),
            req.isEnterRoomScene() ? homeScene.getRoomSceneId() : realmId,
            null, null
        );

        session.send(new PacketHomeSceneJumpRsp(req.isEnterRoomScene()));
    }

}
