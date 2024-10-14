package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import emu.grasscutter.utils.Utils;
import lombok.val;
import org.anime_game_servers.multi_proto.gi.messages.activity.announce.AnnounceData;
import org.anime_game_servers.multi_proto.gi.messages.activity.announce.ServerAnnounceNotify;

import java.util.List;

public class PacketServerAnnounceNotify extends BaseTypedPacket<ServerAnnounceNotify> {
    public PacketServerAnnounceNotify(List<AnnounceData> data) {
        super(new ServerAnnounceNotify());
        proto.setAnnounceDataList(data);
    }

	public PacketServerAnnounceNotify(String msg, int configId) {
        super(new ServerAnnounceNotify());
        val announceData = new AnnounceData();
        announceData.setConfigId(configId);
        announceData.setBeginTime(Utils.getCurrentSeconds() + 1);
        announceData.setEndTime(Utils.getCurrentSeconds() + 2);
        announceData.setCenterSystemText(msg);
        announceData.setCenterSystemFrequency(1);

        proto.setAnnounceDataList(List.of(announceData));
	}
}
