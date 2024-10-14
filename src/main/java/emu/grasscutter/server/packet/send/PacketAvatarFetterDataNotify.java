package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.avatar.Avatar;
import emu.grasscutter.game.props.FetterState;
import emu.grasscutter.net.packet.BaseTypedPacket;
import lombok.val;
import org.anime_game_servers.multi_proto.gi.messages.general.avatar.AvatarFetterInfo;
import org.anime_game_servers.multi_proto.gi.messages.general.avatar.FetterData;
import org.anime_game_servers.multi_proto.gi.messages.team.avatar.friendship.AvatarFetterDataNotify;

import java.util.List;
import java.util.Map;

public class PacketAvatarFetterDataNotify extends BaseTypedPacket<AvatarFetterDataNotify> {
	public PacketAvatarFetterDataNotify(Avatar avatar) {
        super(new AvatarFetterDataNotify());

		int fetterLevel = avatar.getFetterLevel();

        val avatarFetter = new AvatarFetterInfo();
        avatarFetter.setExpLevel(fetterLevel);
		if (fetterLevel != 10) {
            avatarFetter.setExpNumber(avatar.getFetterExp());
        }

        avatarFetter.setFetterList(avatar.getFetters().stream().map(id -> {
            val fetterData = new FetterData();
            fetterData.setFetterId(id);
            fetterData.setFetterState(FetterState.FINISH.getValue());
            return fetterData;
        }).toList());

		if (avatar.getPlayer().getNameCardList().contains(avatar.getNameCardId())) {
            avatarFetter.setRewardedFetterLevelList(List.of(10));
        }

        proto.setFetterInfoMap(Map.of(avatar.getGuid(), avatarFetter));
	}
}
