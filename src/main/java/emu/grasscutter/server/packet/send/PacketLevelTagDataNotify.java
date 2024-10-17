package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.player.Player;
import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.scene.LevelTagDataNotify;

import java.util.Map;

public class PacketLevelTagDataNotify extends BaseTypedPacket<LevelTagDataNotify> {
    public PacketLevelTagDataNotify(Player player) {
        super(new LevelTagDataNotify());
        proto.setLevelTagIdList(player.getLevelTags().entrySet().stream()
            .filter(Map.Entry::getValue)
            .map(Map.Entry::getKey)
            .toList());
    }
}
