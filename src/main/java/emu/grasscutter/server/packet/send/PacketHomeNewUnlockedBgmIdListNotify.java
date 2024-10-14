package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.serenitea_pot.bgm.HomeNewUnlockedBgmIdListNotify;

import java.util.List;

public class PacketHomeNewUnlockedBgmIdListNotify extends BaseTypedPacket<HomeNewUnlockedBgmIdListNotify> {
    public PacketHomeNewUnlockedBgmIdListNotify(int homeBgmId) {
        super(new HomeNewUnlockedBgmIdListNotify());
        proto.setNewUnlockedBgmIdList(List.of(homeBgmId));
    }
}
