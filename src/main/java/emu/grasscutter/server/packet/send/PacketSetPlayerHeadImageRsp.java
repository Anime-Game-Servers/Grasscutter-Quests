package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.player.Player;
import emu.grasscutter.net.packet.BaseTypedPacket;
import lombok.val;
import org.anime_game_servers.multi_proto.gi.messages.community.player_presentation.SetPlayerHeadImageRsp;
import org.anime_game_servers.multi_proto.gi.messages.general.ProfilePicture;

public class PacketSetPlayerHeadImageRsp extends BaseTypedPacket<SetPlayerHeadImageRsp> {
	public PacketSetPlayerHeadImageRsp(Player player) {
        super(new SetPlayerHeadImageRsp());
        val profilePicture = new ProfilePicture();
        profilePicture.setAvatarId(player.getHeadImage());
        proto.setProfilePicture(profilePicture);
	}
}
