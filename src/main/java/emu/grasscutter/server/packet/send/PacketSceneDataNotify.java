package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.player.Player;
import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.scene.SceneDataNotify;

public class PacketSceneDataNotify extends BaseTypedPacket<SceneDataNotify> {

    public PacketSceneDataNotify(Player player) {
        super(new SceneDataNotify());

        proto.setSceneTagIdList(player.getSceneTagList(player.getSceneId()));
    }
}
