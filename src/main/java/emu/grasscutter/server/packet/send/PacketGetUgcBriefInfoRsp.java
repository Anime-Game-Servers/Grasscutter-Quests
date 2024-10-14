package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.activity.user_generated_content.GetUgcBriefInfoRsp;
import org.anime_game_servers.multi_proto.gi.messages.activity.user_generated_content.UgcType;
import org.anime_game_servers.multi_proto.gi.messages.activity.user_generated_content.music_game.UgcMusicBriefInfo;
import org.anime_game_servers.multi_proto.gi.messages.general.Retcode;

public class PacketGetUgcBriefInfoRsp extends BaseTypedPacket<GetUgcBriefInfoRsp> {

	public PacketGetUgcBriefInfoRsp(Retcode ret, UgcType ugcType) {
		super(new GetUgcBriefInfoRsp());

        proto.setRetcode(ret);
        proto.setUgcType(ugcType);
	}

    public PacketGetUgcBriefInfoRsp(UgcMusicBriefInfo briefInfo, UgcType ugcType) {
        super(new GetUgcBriefInfoRsp());

        proto.setBrief(new GetUgcBriefInfoRsp.Brief.MusicBriefInfo(briefInfo));
        proto.setUgcType(ugcType);
    }


}
