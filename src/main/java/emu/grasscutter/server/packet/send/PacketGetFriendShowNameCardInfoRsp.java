package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.community.friends.GetFriendShowNameCardInfoRsp;
import org.anime_game_servers.multi_proto.gi.messages.general.Retcode;

import java.util.List;

public class PacketGetFriendShowNameCardInfoRsp extends BaseTypedPacket<GetFriendShowNameCardInfoRsp> {
    public PacketGetFriendShowNameCardInfoRsp(int uid, List<Integer> cardIds) {
        super(new GetFriendShowNameCardInfoRsp());
        proto.setUid(uid);
        proto.setShowNameCardIdList(cardIds);
        proto.setRetcode(Retcode.RET_SUCC);
    }
}
