package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.general.Retcode;
import org.anime_game_servers.multi_proto.gi.messages.quest.entities.QuestDestroyEntityReq;
import org.anime_game_servers.multi_proto.gi.messages.quest.entities.QuestDestroyEntityRsp;

public class PacketQuestDestroyEntityRsp extends BaseTypedPacket<QuestDestroyEntityRsp> {

	public PacketQuestDestroyEntityRsp(boolean success, QuestDestroyEntityReq req) {
        super(new QuestDestroyEntityRsp());
        proto.setEntityId(req.getEntityId());
        proto.setQuestId(req.getQuestId());
        proto.setSceneId(req.getSceneId());
        proto.setRetCode(success ? Retcode.RET_SUCC : Retcode.RET_FAIL);
	}

}
