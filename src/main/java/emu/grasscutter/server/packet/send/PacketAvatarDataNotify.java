package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.avatar.Avatar;
import emu.grasscutter.game.player.Player;
import emu.grasscutter.net.packet.BaseTypedPacket;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.val;
import org.anime_game_servers.multi_proto.gi.messages.team.AvatarDataNotify;
import org.anime_game_servers.multi_proto.gi.messages.team.AvatarTeam;

import java.util.ArrayList;

public class PacketAvatarDataNotify extends BaseTypedPacket<AvatarDataNotify> {

    public PacketAvatarDataNotify(Player player) {
        super(new AvatarDataNotify(), true);

        proto.setCurAvatarTeamId(player.getTeamManager().getCurrentTeamId());
                //.setChooseAvatarGuid(player.getTeamManager().getCurrentCharacterGuid())
        proto.setOwnedFlycloakList(player.getFlyCloakList().stream().toList());
        proto.setOwnedFlycloakList(player.getCostumeList().stream().toList());

        proto.setAvatarList(player.getAvatars().getAvatars().values().stream().map(Avatar::toProto).toList());

        val avatarTeamMap = new Int2ObjectOpenHashMap<AvatarTeam>();
        val backupAvatarTeamOrderList = new ArrayList<Integer>();
        player.getTeamManager().getTeams().forEach((id, teamInfo) -> {
            avatarTeamMap.put((int)id, teamInfo.toProto(player));
            if (id > 4) {  // Add the id list for custom teams.
                backupAvatarTeamOrderList.add(id);
            }
        });
        proto.setAvatarTeamMap(avatarTeamMap);
        proto.setBackupAvatarTeamOrderList(backupAvatarTeamOrderList);

        // Set main character
        Avatar mainCharacter = player.getAvatars().getAvatarById(player.getMainCharacterId());
        if (mainCharacter != null) {
            proto.setChooseAvatarGuid(mainCharacter.getGuid());
        }
    }

}
