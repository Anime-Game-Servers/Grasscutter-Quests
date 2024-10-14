package emu.grasscutter.server.packet.recv;

import emu.grasscutter.game.home.GameHome;
import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.event.player.PlayerTeleportEvent.TeleportType;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.PacketTryEnterHomeRsp;
import lombok.val;
import org.anime_game_servers.multi_proto.gi.messages.community.friends.FriendEnterHomeOption;
import org.anime_game_servers.multi_proto.gi.messages.serenitea_pot.player.TryEnterHomeReq;
import org.anime_game_servers.multi_proto.gi.messages.general.Retcode;

public class HandlerTryEnterHomeReq extends TypedPacketHandler<TryEnterHomeReq> {
    @Override
    public void handle(GameSession session, byte[] header, TryEnterHomeReq req) throws Exception {
        val targetPlayer = session.getServer().getPlayerByUid(req.getTargetUid(), true);

        if (req.getTargetUid() != session.getPlayer().getUid() && targetPlayer != null) {
            // I hope that tomorrow there will be a hero who can support multiplayer mode and write code like a poem
            val targetHome = GameHome.getByUid(req.getTargetUid());
            switch (FriendEnterHomeOption.values()[targetHome.getEnterHomeOption()]) {
                case FRIEND_ENTER_HOME_OPTION_NEED_CONFIRM -> {
                    if (targetPlayer.isOnline()) break;

                    session.send(new PacketTryEnterHomeRsp(Retcode.RET_HOME_OWNER_OFFLINE, req.getTargetUid()));
                }
                case FRIEND_ENTER_HOME_OPTION_REFUSE ->
                    session.send(new PacketTryEnterHomeRsp(Retcode.RET_HOME_HOME_REFUSE_GUEST_ENTER, req.getTargetUid()));
                case FRIEND_ENTER_HOME_OPTION_DIRECT -> session.send(new PacketTryEnterHomeRsp());
            }
            return;
        }

        final int realmId = 2000 + session.getPlayer().getCurrentRealmId();
        val home = session.getPlayer().getHome();

        // prepare the default arrangement for first come in
        home.getHomeSceneItem(realmId);
        home.save();

        // the function should be able to get pos and rot from scriptManager config
        final boolean result = session.getPlayer().getWorld().transferPlayerToScene(
            session.getPlayer(), realmId, TeleportType.WAYPOINT, null, null);

        if (result) session.send(new PacketTryEnterHomeRsp(req.getTargetUid()));
    }
}
