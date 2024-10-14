package emu.grasscutter.server.packet.recv;

import emu.grasscutter.game.entity.EntityMonster;
import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import lombok.val;
import org.anime_game_servers.gi_lua.models.ScriptArgs;
import org.anime_game_servers.gi_lua.models.constants.EventType;
import org.anime_game_servers.multi_proto.gi.messages.battle.MonsterAlertChangeNotify;

public class HandlerMonsterAlertChangeNotify extends TypedPacketHandler<MonsterAlertChangeNotify> {
	@Override
    public void handle(GameSession session, byte[] header, MonsterAlertChangeNotify req) throws Exception {
        val player = session.getPlayer();
        if (req.isAlert() != 0) {
            for (var monsterId : req.getMonsterEntityList()) {
                val monster = (EntityMonster) player.getScene().getEntityById(monsterId);
                if (monster == null) continue;
                if (monster.getPlayerOnBattle().isEmpty()) {
                    monster.getScene().getScriptManager().callEvent(new ScriptArgs(monster.getGroupId(), EventType.EVENT_MONSTER_BATTLE, monster.getConfigId()));
                }
                monster.getPlayerOnBattle().add(player);
            }
        }
        //TODO: Research invisible monsters
	}
}
