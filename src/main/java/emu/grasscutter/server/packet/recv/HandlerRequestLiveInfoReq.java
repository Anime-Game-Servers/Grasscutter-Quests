package emu.grasscutter.server.packet.recv;

import emu.grasscutter.Grasscutter;
import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.PacketLiveStartNotify;
import emu.grasscutter.server.packet.send.PacketRequestLiveInfoRsp;
import org.anime_game_servers.multi_proto.gi.messages.gadget.RequestLiveInfoReq;

public class HandlerRequestLiveInfoReq extends TypedPacketHandler<RequestLiveInfoReq> {
    @Override
    public void handle(GameSession session, byte[] header, RequestLiveInfoReq req) throws Exception {
        Grasscutter.getLogger().warn("STARTING LIVE ID " + req.getLiveId());

        //idc of live_id
        session.send(new PacketRequestLiveInfoRsp(req.getLiveId(), "https://cdn.flowplayer.com/a30bd6bc-f98b-47bc-abf5-97633d4faea0/hls/de3f6ca7-2db3-4689-8160-0f574a5996ad/playlist.m3u8"));
        Grasscutter.getGameServer().getScheduler().scheduleDelayedTask(() -> {
            session.send(new PacketLiveStartNotify(req.getLiveId()));
        }, 10);
    }

}
