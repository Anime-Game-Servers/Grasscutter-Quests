package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.community.player_presentation.UpdatePlayerShowAvatarListRsp;
import org.anime_game_servers.multi_proto.gi.messages.general.Retcode;

import java.util.List;

public class PacketUpdatePlayerShowAvatarListRsp extends BaseTypedPacket<UpdatePlayerShowAvatarListRsp> {
    public PacketUpdatePlayerShowAvatarListRsp(boolean isShowAvatar, List<Integer> avatarIds) {
        super(new UpdatePlayerShowAvatarListRsp());
        proto.setShowAvatar(isShowAvatar);
        proto.setShowAvatarIdList(avatarIds);
        proto.setRetcode(Retcode.RET_SUCC);
    }
}
