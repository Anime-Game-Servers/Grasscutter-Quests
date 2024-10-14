package emu.grasscutter.server.packet.recv;

import emu.grasscutter.game.entity.EntityGadget;
import emu.grasscutter.game.entity.GameEntity;
import emu.grasscutter.game.player.Player;
import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.PacketExecuteGadgetLuaRsp;
import org.anime_game_servers.multi_proto.gi.messages.general.Retcode;
import org.anime_game_servers.multi_proto.gi.messages.scene.script.ExecuteGadgetLuaReq;

public class HandlerExecuteGadgetLuaReq extends TypedPacketHandler<ExecuteGadgetLuaReq> {
    @Override
    public void handle(GameSession session, byte[] header, ExecuteGadgetLuaReq req) throws Exception {
        Player player = session.getPlayer();
        GameEntity entity = player.getScene().getEntities().get(req.getSourceEntityId());

        int result = 1;
        if(entity instanceof EntityGadget gadget) result = gadget.onClientExecuteRequest(req.getParam1(), req.getParam2(), req.getParam3());

        player.sendPacket(new PacketExecuteGadgetLuaRsp(result == 0 ? Retcode.RET_SUCC : Retcode.RET_FAIL));
    }
}
