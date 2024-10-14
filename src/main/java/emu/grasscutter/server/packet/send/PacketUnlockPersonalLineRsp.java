package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.general.Retcode;
import org.anime_game_servers.multi_proto.gi.messages.quest.personal.UnlockPersonalLineRsp;
import org.anime_game_servers.multi_proto.gi.messages.quest.personal.UnlockPersonalLineRsp.Param.ChapterId;

public class PacketUnlockPersonalLineRsp extends BaseTypedPacket<UnlockPersonalLineRsp> {
    public PacketUnlockPersonalLineRsp(int id, int chapterId) {
        super(new UnlockPersonalLineRsp());
        proto.setPersonalLineId(id);
        proto.setParam(new ChapterId(chapterId));
	}

	public PacketUnlockPersonalLineRsp(int id, Retcode retCode) {
        super(new UnlockPersonalLineRsp());
        proto.setPersonalLineId(id);
        proto.setRetCode(retCode);
	}
}
