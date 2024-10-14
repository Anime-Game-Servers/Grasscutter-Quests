package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.activity.user_generated_content.SaveUgcRsp;
import org.anime_game_servers.multi_proto.gi.messages.activity.user_generated_content.UgcType;
import org.anime_game_servers.multi_proto.gi.messages.general.Retcode;

public class PacketMusicGameCreateBeatmapRsp extends BaseTypedPacket<SaveUgcRsp> {

	public PacketMusicGameCreateBeatmapRsp(long musicShareId, UgcType ugcType) {
		super(new SaveUgcRsp(Retcode.RET_SUCC, musicShareId, ugcType));
	}

	public PacketMusicGameCreateBeatmapRsp(Retcode retCode, UgcType ugcType) {
		super(new SaveUgcRsp(retCode));

        proto.setUgcType(ugcType);
	}
}
