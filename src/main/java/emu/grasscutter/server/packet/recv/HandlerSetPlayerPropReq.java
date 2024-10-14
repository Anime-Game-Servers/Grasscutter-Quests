package emu.grasscutter.server.packet.recv;

import emu.grasscutter.game.player.Player;
import emu.grasscutter.game.props.PlayerProperty;
import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.PacketSetPlayerPropRsp;
import org.anime_game_servers.multi_proto.gi.messages.general.PropValue;
import org.anime_game_servers.multi_proto.gi.messages.general.Retcode;
import org.anime_game_servers.multi_proto.gi.messages.player.SetPlayerPropReq;

public class HandlerSetPlayerPropReq extends TypedPacketHandler<SetPlayerPropReq> {
    @Override
    public void handle(GameSession session, byte[] header, SetPlayerPropReq req) throws Exception {
        // Auto template
        Player player = session.getPlayer();
        for (PropValue p : req.getPropList()) {
            PlayerProperty prop = PlayerProperty.getPropById(p.getType());
            if (prop == PlayerProperty.PROP_IS_MP_MODE_AVAILABLE) {
                if (!player.setProperty(prop, (int) p.getVal(), false)) {
                    session.send(new PacketSetPlayerPropRsp(Retcode.RET_FAIL));
                    return;
                }
            }
        }
        player.save();
        session.send(new PacketSetPlayerPropRsp(Retcode.RET_SUCC));
    }
}
