package emu.grasscutter.server.packet.recv;

import emu.grasscutter.Grasscutter;
import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import lombok.val;
import org.anime_game_servers.gi_lua.models.ScriptArgs;
import org.anime_game_servers.gi_lua.models.constants.EventType;
import org.anime_game_servers.multi_proto.gi.messages.scene.script.ClientScriptEventNotify;

public class HandlerClientScriptEventNotify extends TypedPacketHandler<ClientScriptEventNotify> {
	@Override
    public void handle(GameSession session, byte[] header, ClientScriptEventNotify req) throws Exception {
        Grasscutter.getLogger().info("called ClientScriptEventNotify with type {}", req.getEventType());
        val scriptManager = session.getPlayer().getScene().getScriptManager();
        val args = new ScriptArgs(0, req.getEventType())
            .setSourceEntityId(req.getSourceEntityId())
            .setTargetEntityId(req.getTargetEntityId());

        for (int i = 0; i < req.getParamList().size(); i++) {
            switch (i) {
                case 0 -> args.setParam1(req.getParamList().get(i));
                case 1 -> args.setParam2(req.getParamList().get(i));
                case 2 -> args.setParam3(req.getParamList().get(i));
            }
        }
        if (req.getEventType() == EventType.EVENT_AVATAR_NEAR_PLATFORM) {
            val entity = scriptManager.getScene().getEntityById(req.getSourceEntityId());
            if(entity != null){
                args.setParam1(entity.getConfigId());
            }
        }

        scriptManager.callEvent(args);
	}
}
