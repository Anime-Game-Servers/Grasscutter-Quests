package emu.grasscutter.server.packet.recv;

import emu.grasscutter.data.common.ItemParamData;
import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.PacketCombineRsp;
import org.anime_game_servers.multi_proto.gi.messages.general.item.ItemParam;
import org.anime_game_servers.multi_proto.gi.messages.item.combine.CombineReq;

import java.util.List;

public class HandlerCombineReq extends TypedPacketHandler<CombineReq> {
    @Override
    public void handle(GameSession session, byte[] header, CombineReq req) throws Exception {
        var result = session.getServer().getCombineSystem()
                .combineItem(session.getPlayer(), req.getCombineId(), req.getCombineCount());

        if (result == null) {
            return;
        }

        session.send(new PacketCombineRsp(req,
                toItemParamList(result.getMaterial()),
                toItemParamList(result.getResult()),
                toItemParamList(result.getExtra()),
                toItemParamList(result.getBack()),
                toItemParamList(result.getBack())));
    }

    private List<ItemParam> toItemParamList(List<ItemParamData> list) {
        return list.stream()
            .map(item -> new ItemParam(item.getId(), item.getCount()))
            .toList();
    }
}

