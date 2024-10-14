package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.player.Player;
import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.serenitea_pot.bgm.HomeAllUnlockedBgmIdListNotify;

public class PacketHomeAllUnlockedBgmIdListNotify extends BaseTypedPacket<HomeAllUnlockedBgmIdListNotify> {
    public PacketHomeAllUnlockedBgmIdListNotify(Player player) {
        super(new HomeAllUnlockedBgmIdListNotify());
        if (player.getRealmList() == null) return;
        proto.setAllUnlockedBgmIdList(player.getHome().getUnlockedHomeBgmList().stream().toList());
    }
}
