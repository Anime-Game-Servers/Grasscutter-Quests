package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.entity.EntityAvatar;
import emu.grasscutter.game.player.Player;
import emu.grasscutter.net.packet.BaseTypedPacket;
import emu.grasscutter.net.packet.PacketOpcodes;
import lombok.val;
import org.anime_game_servers.multi_proto.gi.messages.general.ability.AbilitySyncStateInfo;
import org.anime_game_servers.multi_proto.gi.messages.scene.SceneTeamAvatar;
import org.anime_game_servers.multi_proto.gi.messages.scene.SceneTeamUpdateNotify;

import java.util.ArrayList;

public class PacketSceneTeamUpdateNotify extends BaseTypedPacket<SceneTeamUpdateNotify> {

	public PacketSceneTeamUpdateNotify(Player player) {
		super(new SceneTeamUpdateNotify());

		proto.setInMp(player.getWorld().isMultiplayer());

        val avatarList = new ArrayList<SceneTeamAvatar>();
		for (Player p : player.getWorld().getPlayers()) {
            avatarList.addAll(p.getTeamManager().getActiveTeam().stream().map(entityAvatar -> {
                val avatarProto = new SceneTeamAvatar(
                    p.getUid(), entityAvatar.getAvatar().getGuid(),
                    p.getSceneId(), entityAvatar.getId());
                avatarProto.setSceneEntityInfo(entityAvatar.toProto());
                avatarProto.setWeaponGuid(entityAvatar.getAvatar().getWeapon().getGuid());
                avatarProto.setWeaponEntityId(entityAvatar.getWeaponEntityId());
                avatarProto.setPlayerCurAvatar(p.getTeamManager().getCurrentAvatarEntity() == entityAvatar);
                avatarProto.setOnScene(p.getTeamManager().getCurrentAvatarEntity() == entityAvatar);
                avatarProto.setAvatarAbilityInfo(new AbilitySyncStateInfo());
                avatarProto.setWeaponAbilityInfo(new AbilitySyncStateInfo());
                avatarProto.setAbilityControlBlock(entityAvatar.getAbilityControlBlock());

                if (player.getWorld().isMultiplayer()) {
                    avatarProto.setAvatarInfo(entityAvatar.getAvatar().toProto());
                    avatarProto.setSceneAvatarInfo(entityAvatar.getSceneAvatarInfo()); // why mihoyo...
                }
                return avatarProto;
            }).toList());
		}
        proto.setSceneTeamAvatarList(avatarList);
	}
}
