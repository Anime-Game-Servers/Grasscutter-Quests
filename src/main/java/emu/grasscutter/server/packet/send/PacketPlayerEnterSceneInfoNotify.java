package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.player.Player;
import emu.grasscutter.net.packet.BaseTypedPacket;
import lombok.val;
import org.anime_game_servers.multi_proto.gi.messages.general.ability.AbilityControlBlock;
import org.anime_game_servers.multi_proto.gi.messages.general.ability.AbilitySyncStateInfo;
import org.anime_game_servers.multi_proto.gi.messages.scene.AvatarEnterSceneInfo;
import org.anime_game_servers.multi_proto.gi.messages.scene.PlayerEnterSceneInfoNotify;
import org.anime_game_servers.multi_proto.gi.messages.scene.entity.MPLevelEntityInfo;

public class PacketPlayerEnterSceneInfoNotify extends BaseTypedPacket<PlayerEnterSceneInfoNotify> {

	public PacketPlayerEnterSceneInfoNotify(Player player) {
		super(new PlayerEnterSceneInfoNotify());

		val empty = new AbilitySyncStateInfo();

        proto.setCurAvatarEntityId(player.getTeamManager().getCurrentAvatarEntity().getId());
        proto.setEnterSceneToken(player.getEnterSceneToken());

		proto.setTeamEnterInfo(
				new org.anime_game_servers.multi_proto.gi.messages.scene.TeamEnterSceneInfo(
                    player.getTeamManager().getEntity().getId(), // 150995833
                    empty,
                    new AbilityControlBlock()
                )
		);
		proto.setMpLevelEntityInfo(
				new MPLevelEntityInfo(
                    player.getWorld().getLevelEntityId(), // 184550274
                    player.getWorld().getHostPeerId(),
                    empty
                )
		);

        proto.setAvatarEnterInfo(player.getTeamManager().getActiveTeam().stream().map(avatarEntity -> {
            val weapon = avatarEntity.getAvatar().getWeapon();
            long weaponGuid = weapon != null ? weapon.getGuid() : 0;

            val avatarInfo = new AvatarEnterSceneInfo(avatarEntity.getAvatar().getGuid(), avatarEntity.getId(), empty);
            avatarInfo.setWeaponGuid(weaponGuid);
            avatarInfo.setWeaponEntityId(avatarEntity.getWeaponEntityId());
            avatarInfo.setWeaponAbilityInfo(empty);
            return avatarInfo;
        }).toList());
	}
}
