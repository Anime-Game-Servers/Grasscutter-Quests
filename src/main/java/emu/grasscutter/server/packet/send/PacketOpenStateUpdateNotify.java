package emu.grasscutter.server.packet.send;

import static emu.grasscutter.config.Configuration.GAME_OPTIONS;

import emu.grasscutter.data.GameData;
import emu.grasscutter.data.excels.OpenStateData;
import emu.grasscutter.game.player.Player;
import emu.grasscutter.game.player.PlayerProgressManager;
import emu.grasscutter.net.packet.BaseTypedPacket;
import lombok.val;
import org.anime_game_servers.multi_proto.gi.messages.player.OpenStateUpdateNotify;

import java.util.HashMap;

/*
    Must be sent on login for openStates to work
    Tells the client to update its openStateMap for the keys sent. value is irrelevant
 */
public class PacketOpenStateUpdateNotify extends BaseTypedPacket<OpenStateUpdateNotify> {

    public PacketOpenStateUpdateNotify(Player player) {
        super(new OpenStateUpdateNotify());
        val openStateMap = new HashMap<Integer, Integer>();

        GameData.getOpenStateList().stream().map(OpenStateData::getId).forEach(id -> {
            if ((id == 45) && !GAME_OPTIONS.resinOptions.resinUsage) {
                openStateMap.put(45, 0);  // Remove resin from map
                return;
            }
            // If the player has an open state stored in their map, then it would always override any default value
            if (player.getOpenStates().containsKey(id)) {
                openStateMap.put(id, player.getProgressManager().getOpenState(id));
            }
            // Otherwise, add the state if it is contained in the set of default open states.
            else if (PlayerProgressManager.DEFAULT_OPEN_STATES.contains(id)) {
                openStateMap.put(id, 1);
            }
        });

        proto.setOpenStateMap(openStateMap);
    }
}
