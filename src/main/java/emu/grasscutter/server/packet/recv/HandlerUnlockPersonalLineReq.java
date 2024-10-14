package emu.grasscutter.server.packet.recv;

import emu.grasscutter.data.GameData;
import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.PacketUnlockPersonalLineRsp;
import org.anime_game_servers.multi_proto.gi.messages.general.Retcode;
import org.anime_game_servers.multi_proto.gi.messages.quest.personal.UnlockPersonalLineReq;

public class HandlerUnlockPersonalLineReq extends TypedPacketHandler<UnlockPersonalLineReq> {
	@Override
    public void handle(GameSession session, byte[] header, UnlockPersonalLineReq req) throws Exception {
        var data = GameData.getPersonalLineDataMap().get(req.getPersonalLineId());
        if(data == null){
            session.send(new PacketUnlockPersonalLineRsp(req.getPersonalLineId(), Retcode.RET_FAIL));
            return;
        }

        //TODO check conditions

        session.getPlayer().addPersonalLine(data.getId());
        session.getPlayer().useLegendaryKey(1);

        session.send(new PacketUnlockPersonalLineRsp(data.getId(), data.getChapterId()));
	}
}
