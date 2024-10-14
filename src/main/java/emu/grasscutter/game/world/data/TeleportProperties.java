package emu.grasscutter.game.world.data;

import emu.grasscutter.game.props.EnterReason;
import emu.grasscutter.server.event.player.PlayerTeleportEvent;
import emu.grasscutter.utils.Position;
import lombok.Builder;
import lombok.Data;
import org.anime_game_servers.multi_proto.gi.messages.scene.EnterType;

@Data
@Builder
public class TeleportProperties {
    private final int sceneId;
    private final PlayerTeleportEvent.TeleportType teleportType;
    private final EnterReason enterReason;
    private Position teleportTo;
    private Position teleportRot;
    private EnterType enterType;
    private int dungeonId;
    private Position prevPos;
    private int prevSceneId;
    private int worldType;
    private boolean isSkipUi;
}
