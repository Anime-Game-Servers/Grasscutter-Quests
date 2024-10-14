package emu.grasscutter.server.packet.recv;

import emu.grasscutter.Grasscutter;
import emu.grasscutter.game.entity.EntityGadget;
import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.PacketProjectorOptionRsp;
import org.anime_game_servers.multi_proto.gi.messages.gadget.ProjectorOpType;
import org.anime_game_servers.multi_proto.gi.messages.gadget.ProjectorOptionReq;
import org.anime_game_servers.gi_lua.models.constants.ScriptGadgetState;
import org.anime_game_servers.multi_proto.gi.messages.general.Retcode;

public class HandlerProjectorOptionReq extends TypedPacketHandler<ProjectorOptionReq> {

    @Override
    public void handle(GameSession session, byte[] header, ProjectorOptionReq req) throws Exception {

        Grasscutter.getLogger().debug("JUST SOME DEBUG " + req.getOpType());

        if(session.getPlayer().getScene().getEntityById(req.getEntityId()) instanceof EntityGadget gadget) {
            ProjectorOpType type = req.getOpType();
            if(type == ProjectorOpType.PROJECTOR_OP_CREATE) {
                if(gadget.getState() != ScriptGadgetState.GearStart)
                    gadget.setState(ScriptGadgetState.GearStart);
            } else if(type == ProjectorOpType.PROJECTOR_OP_DESTROY) {
                if(gadget.getState() != ScriptGadgetState.GearStop)
                    gadget.setState(ScriptGadgetState.GearStop);
            }

            session.send(new PacketProjectorOptionRsp(req.getEntityId(), req.getOpType(), Retcode.RET_SUCC));
        } else {
            session.send(new PacketProjectorOptionRsp(req.getEntityId(), req.getOpType(), Retcode.RET_FAIL));
        }
    }

}
