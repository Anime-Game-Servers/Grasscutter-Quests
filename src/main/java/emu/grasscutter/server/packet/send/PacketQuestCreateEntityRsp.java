package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.general.Retcode;
import org.anime_game_servers.multi_proto.gi.messages.quest.entities.QuestCreateEntityReq;
import org.anime_game_servers.multi_proto.gi.messages.quest.entities.QuestCreateEntityRsp;

public class PacketQuestCreateEntityRsp extends BaseTypedPacket<QuestCreateEntityRsp> {

	public PacketQuestCreateEntityRsp(int entityId, QuestCreateEntityReq req) {
        super(new QuestCreateEntityRsp());
        proto.setEntityId(entityId);
        proto.setEntity(req.getEntity());
        proto.setQuestId(req.getQuestId());
        proto.setRewind(req.isRewind());
        proto.setParentQuestId(req.getParentQuestId());
        proto.setRetCode(entityId != -1 ? Retcode.RET_SUCC : Retcode.RET_FAIL);
	}

}
