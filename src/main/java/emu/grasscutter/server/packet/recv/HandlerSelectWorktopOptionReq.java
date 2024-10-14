package emu.grasscutter.server.packet.recv;

import emu.grasscutter.game.entity.EntityGadget;
import emu.grasscutter.game.entity.GameEntity;
import emu.grasscutter.game.quest.enums.QuestContent;
import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.PacketSelectWorktopOptionRsp;
import org.anime_game_servers.multi_proto.gi.messages.gadget.SelectWorktopOptionReq;
import org.anime_game_servers.gi_lua.models.ScriptArgs;
import org.anime_game_servers.gi_lua.models.constants.EventType;

public class HandlerSelectWorktopOptionReq extends TypedPacketHandler<SelectWorktopOptionReq> {

    @Override
    public void handle(GameSession session, byte[] header, SelectWorktopOptionReq req) throws Exception {

        try {
            GameEntity entity = session.getPlayer().getScene().getEntityById(req.getGadgetEntityId());

            if (!(entity instanceof EntityGadget)) {
                return;
            }
            session.getPlayer().getScene().selectWorktopOptionWith(req);
            session.getPlayer().getScene().getScriptManager().callEvent(
                    new ScriptArgs(entity.getGroupId(), EventType.EVENT_SELECT_OPTION, entity.getConfigId(), req.getOptionId())
            );
            session.getPlayer().getQuestManager().queueEvent(QuestContent.QUEST_CONTENT_WORKTOP_SELECT, entity.getConfigId(), req.getOptionId());
        } finally {
            // Always send packet
            session.send(new PacketSelectWorktopOptionRsp(req.getGadgetEntityId(), req.getOptionId()));
        }
    }

}
