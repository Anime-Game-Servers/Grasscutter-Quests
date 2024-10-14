package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.player.Player;
import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.multiplayer.PlayerApplyEnterMpResultNotify;
import org.anime_game_servers.multi_proto.gi.messages.multiplayer.MpEnterResultReason;

public class PacketPlayerApplyEnterMpResultNotify extends BaseTypedPacket<PlayerApplyEnterMpResultNotify> {
    public PacketPlayerApplyEnterMpResultNotify(Player target, boolean isAgreed, MpEnterResultReason reason) {
        this(target.getUid(), target.getNickname(), isAgreed, reason);
	}

    public PacketPlayerApplyEnterMpResultNotify(int targetId, String targetName, boolean isAgreed, MpEnterResultReason reason) {
        super(new PlayerApplyEnterMpResultNotify());
        proto.setTargetUid(targetId);
        proto.setTargetNickname(targetName);
        proto.setAgreed(isAgreed);
        proto.setReason(reason);
	}
}
