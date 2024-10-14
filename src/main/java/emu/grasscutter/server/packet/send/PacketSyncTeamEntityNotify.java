package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.player.Player;
import emu.grasscutter.net.packet.BaseTypedPacket;
import lombok.val;
import org.anime_game_servers.multi_proto.gi.messages.general.ability.AbilitySyncStateInfo;
import org.anime_game_servers.multi_proto.gi.messages.team.SyncTeamEntityNotify;
import org.anime_game_servers.multi_proto.gi.messages.team.TeamEntityInfo;

import java.util.ArrayList;

public class PacketSyncTeamEntityNotify extends BaseTypedPacket<SyncTeamEntityNotify> {

	public PacketSyncTeamEntityNotify(Player player) {
		super(new SyncTeamEntityNotify(player.getSceneId()));

		if (player.getWorld().isMultiplayer()) {
            val infoList = new ArrayList<TeamEntityInfo>();
			for (Player p : player.getWorld().getPlayers()) {
				// Skip if same player
				if (player == p) {
					continue;
				}

				// Set info
				val info = new TeamEntityInfo();
                info.setTeamEntityId(p.getTeamManager().getEntity().getId());
                info.setAuthorityPeerId(p.getPeerId());
                info.setTeamAbilityInfo(new AbilitySyncStateInfo());

                infoList.add(info);
			}
            proto.setTeamEntityInfoList(infoList);
		}
	}
}
