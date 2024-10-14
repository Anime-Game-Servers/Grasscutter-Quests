package emu.grasscutter.server.packet.recv;

import emu.grasscutter.data.GameData;
import emu.grasscutter.data.excels.WorldAreaData;
import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.PacketAvatarChangeElementTypeRsp;
import lombok.val;
import org.anime_game_servers.multi_proto.gi.messages.general.Retcode;
import org.anime_game_servers.multi_proto.gi.messages.team.avatar.AvatarChangeElementTypeReq;

/**
 * Changes the currently active avatars Element if possible
 */
public class HandlerAvatarChangeElementTypeReq extends TypedPacketHandler<AvatarChangeElementTypeReq> {
    @Override
    public void handle(GameSession session, byte[] header, AvatarChangeElementTypeReq req) throws Exception {
        WorldAreaData area = GameData.getWorldAreaDataMap().get(req.getAreaId());

        if (area == null || area.getElementType() == null || area.getElementType().getDepotIndex() <= 0) {
            session.send(new PacketAvatarChangeElementTypeRsp(Retcode.RET_SVR_ERROR));
            return;
        }

        val avatar = session.getPlayer().getTeamManager().getCurrentAvatarEntity().getAvatar();
        if (!avatar.changeElement(area.getElementType())) {
            session.send(new PacketAvatarChangeElementTypeRsp(Retcode.RET_SVR_ERROR));
            return;
        }

        // Success
        session.send(new PacketAvatarChangeElementTypeRsp());
    }

}
