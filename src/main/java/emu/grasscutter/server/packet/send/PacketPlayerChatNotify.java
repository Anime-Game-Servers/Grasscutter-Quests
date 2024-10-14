package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.player.Player;
import emu.grasscutter.net.packet.BaseTypedPacket;
import lombok.val;
import org.anime_game_servers.multi_proto.gi.messages.community.chat.ChatInfo;
import org.anime_game_servers.multi_proto.gi.messages.community.chat.PlayerChatNotify;
import org.anime_game_servers.multi_proto.gi.messages.community.chat.SystemHint;

public class PacketPlayerChatNotify extends BaseTypedPacket<PlayerChatNotify> {

	public PacketPlayerChatNotify(Player sender, int channelId, String message) {
		this(sender, channelId, new ChatInfo.Content.Text(message));
	}

	public PacketPlayerChatNotify(Player sender, int channelId, int emote) {
        this(sender, channelId, new ChatInfo.Content.Icon(emote));
	}

	public PacketPlayerChatNotify(Player sender, int channelId, SystemHint systemHint) {

        this(sender, channelId, new ChatInfo.Content.SystemHint(systemHint));
	}


    public PacketPlayerChatNotify(Player sender, int channelId, ChatInfo.Content<?> content) {
        super(new PlayerChatNotify());

        val info = new ChatInfo((int) (System.currentTimeMillis() / 1000), sender.getUid());
        info.setContent(content);

        proto.setChannelId(channelId);
        proto.setChatInfo(info);
    }
}
