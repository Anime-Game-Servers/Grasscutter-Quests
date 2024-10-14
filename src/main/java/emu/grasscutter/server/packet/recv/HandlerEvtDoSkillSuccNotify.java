package emu.grasscutter.server.packet.recv;

import emu.grasscutter.game.quest.enums.QuestContent;
import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import org.anime_game_servers.multi_proto.gi.messages.battle.event.EvtDoSkillSuccNotify;

public class HandlerEvtDoSkillSuccNotify extends TypedPacketHandler<EvtDoSkillSuccNotify> {

    @Override
    public void handle(GameSession session, byte[] header, EvtDoSkillSuccNotify notify) throws Exception {
        var player = session.getPlayer();
        int skillId = notify.getSkillId();
        int casterId = notify.getCasterId();

        // Call skill perform in the player's ability manager.
        player.getAbilityManager().onSkillStart(session.getPlayer(), skillId, casterId);

        // Handle skill notify in other managers.
        player.getStaminaManager().handleEvtDoSkillSuccNotify(session, skillId, casterId);
        player.getEnergyManager().handleEvtDoSkillSuccNotify(session, skillId, casterId);
        player.getQuestManager().queueEvent(QuestContent.QUEST_CONTENT_SKILL, skillId);
    }
}
