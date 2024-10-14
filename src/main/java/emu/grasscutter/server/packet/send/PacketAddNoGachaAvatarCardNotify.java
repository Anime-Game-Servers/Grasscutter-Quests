package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.avatar.Avatar;
import emu.grasscutter.game.inventory.GameItem;
import emu.grasscutter.game.props.ActionReason;
import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.team.avatar.AddNoGachaAvatarCardNotify;

public class PacketAddNoGachaAvatarCardNotify extends BaseTypedPacket<AddNoGachaAvatarCardNotify> {

	public PacketAddNoGachaAvatarCardNotify(Avatar avatar, ActionReason reason, GameItem item) {
        this(avatar.getAvatarId(), reason, item);
	}

	public PacketAddNoGachaAvatarCardNotify(int avatarId, ActionReason reason, GameItem item) {
        super(new AddNoGachaAvatarCardNotify(), true);
        proto.setAvatarId(avatarId);
        proto.setReason(reason.getValue());
        proto.setInitialLevel(1);
        proto.setItemId(item.getItemId());
        proto.setInitialPromoteLevel(0);
	}
}
