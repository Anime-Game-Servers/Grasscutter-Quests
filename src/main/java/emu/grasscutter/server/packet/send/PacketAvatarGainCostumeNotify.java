package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.team.avatar.cosmetic.AvatarGainCostumeNotify;

public class PacketAvatarGainCostumeNotify extends BaseTypedPacket<AvatarGainCostumeNotify> {
	public PacketAvatarGainCostumeNotify(int costumeId) {
        super(new AvatarGainCostumeNotify());
        proto.setCostumeId(costumeId);
	}
}
