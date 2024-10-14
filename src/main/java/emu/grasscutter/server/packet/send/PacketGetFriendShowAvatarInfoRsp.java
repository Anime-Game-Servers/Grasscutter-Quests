package emu.grasscutter.server.packet.send;

import java.util.List;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.community.friends.GetFriendShowAvatarInfoRsp;
import org.anime_game_servers.multi_proto.gi.messages.general.Retcode;
import org.anime_game_servers.multi_proto.gi.messages.general.avatar.ShowAvatarInfo;

public class PacketGetFriendShowAvatarInfoRsp extends BaseTypedPacket<GetFriendShowAvatarInfoRsp> {

	public PacketGetFriendShowAvatarInfoRsp(int uid, List<ShowAvatarInfo> showAvatarInfoList) {
		super(new GetFriendShowAvatarInfoRsp(Retcode.RET_SUCC, uid, showAvatarInfoList));
	}

}
