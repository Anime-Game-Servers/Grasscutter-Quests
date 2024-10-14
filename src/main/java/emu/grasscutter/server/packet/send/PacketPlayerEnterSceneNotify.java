package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.player.Player;
import emu.grasscutter.game.player.Player.SceneLoadState;
import emu.grasscutter.game.props.EnterReason;
import emu.grasscutter.game.world.data.TeleportProperties;
import emu.grasscutter.net.packet.BaseTypedPacket;
import emu.grasscutter.utils.Position;
import emu.grasscutter.utils.Utils;
import org.anime_game_servers.multi_proto.gi.messages.scene.EnterType;
import org.anime_game_servers.multi_proto.gi.messages.scene.PlayerEnterSceneNotify;

public class PacketPlayerEnterSceneNotify extends BaseTypedPacket<PlayerEnterSceneNotify> {

    // Login
    public PacketPlayerEnterSceneNotify(Player player) {
        super(new PlayerEnterSceneNotify(player.getSceneId(), player.getPosition().toProto(), System.currentTimeMillis(), org.anime_game_servers.multi_proto.gi.messages.scene.EnterType.ENTER_SELF));

        player.setSceneLoadState(SceneLoadState.LOADING);
        player.setEnterSceneToken(Utils.randomRange(1000, 99999));

        proto.setSkipUi(true);
        proto.setTargetUid(player.getUid());
        proto.setEnterSceneToken(player.getEnterSceneToken());
        proto.setWorldLevel(player.getWorldLevel());
        proto.setEnterReason(EnterReason.Login.getValue());
        proto.setFirstLoginEnterScene(player.isFirstLoginEnterScene());
        proto.setWorldType(1);
        proto.setSceneTransaction(player.getSceneId() + "-" + player.getUid() + "-" + (int) (System.currentTimeMillis() / 1000) + "-" + 18402);
        proto.setSceneTagIdList(player.getSceneTagList(player.getSceneId()));
    }

    public PacketPlayerEnterSceneNotify(Player player, EnterType type, EnterReason reason, int newScene, Position newPos) {
        this(player, player, type, reason, newScene, newPos);
    }
    public PacketPlayerEnterSceneNotify(Player player, TeleportProperties teleportProperties) {
        this(player, player, teleportProperties);
    }

    public PacketPlayerEnterSceneNotify(Player player, Player target, EnterType type, EnterReason reason, int newScene, Position newPos) {
        this(player, target, TeleportProperties.builder()
            .enterType(type)
            .enterReason(reason)
            .sceneId(newScene)
            .teleportTo(newPos)
            .build());
    }

    // Teleport or go somewhere
    public PacketPlayerEnterSceneNotify(Player player, Player target, TeleportProperties teleportProperties) {
        super(new PlayerEnterSceneNotify(teleportProperties.getSceneId(), teleportProperties.getTeleportTo().toProto(), System.currentTimeMillis(), teleportProperties.getEnterType()));

        player.setSceneLoadState(SceneLoadState.LOADING);
        player.setEnterSceneToken(Utils.randomRange(1000, 99999));

        proto.setSkipUi(teleportProperties.isSkipUi());
        proto.setPrevSceneId(teleportProperties.getPrevSceneId());
        proto.setPrevPos(teleportProperties.getPrevPos().toProto());
        proto.setSceneId(teleportProperties.getSceneId());
        proto.setPos(teleportProperties.getTeleportTo().toProto());
        proto.setSceneBeginTime(System.currentTimeMillis());
        proto.setType(teleportProperties.getEnterType());
        proto.setTargetUid(target.getUid());
        proto.setEnterSceneToken(player.getEnterSceneToken());
        proto.setWorldLevel(teleportProperties.getDungeonId() > 0 ? target.getWorldLevel() : 0);
        proto.setEnterReason(teleportProperties.getEnterReason().getValue());
        proto.setWorldType(teleportProperties.getWorldType()) ;// TODO
        proto.setDungeonId(teleportProperties.getDungeonId());
        proto.setSceneTransaction(teleportProperties.getSceneId() + "-" + target.getUid() + "-" + (int) (System.currentTimeMillis() / 1000) + "-" + 18402);
        proto.setSceneTagIdList(player.getSceneTagList(player.getSceneId()));
    }
}
