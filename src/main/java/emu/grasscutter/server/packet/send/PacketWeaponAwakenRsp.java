package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.avatar.Avatar;
import emu.grasscutter.game.inventory.GameItem;
import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.item.upgrade.WeaponAwakenRsp;

import java.util.HashMap;
import java.util.Map;

public class PacketWeaponAwakenRsp extends BaseTypedPacket<WeaponAwakenRsp> {
	public PacketWeaponAwakenRsp(Avatar avatar, GameItem item, GameItem feedWeapon, int oldRefineLevel) {
        super(new WeaponAwakenRsp());
        proto.setTargetWeaponGuid(item.getGuid());
        proto.setTargetWeaponAwakenLevel(item.getRefinement());

        Map<Integer, Integer> oldAffixLevelMap = new HashMap<>();
        Map<Integer, Integer> curAffixLevelMap = new HashMap<>();
        item.getAffixes().forEach(affixId -> {
            oldAffixLevelMap.put(affixId, oldRefineLevel);
            curAffixLevelMap.put(affixId, item.getRefinement());
        });
        proto.setOldAffixLevelMap(oldAffixLevelMap);
        proto.setCurAffixLevelMap(curAffixLevelMap);

		if (avatar != null) {
			proto.setAvatarGuid(avatar.getGuid());
		}
	}
}
