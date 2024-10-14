package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.community.chat.ChatInfo;
import org.anime_game_servers.multi_proto.gi.messages.community.chat.PullRecentChatRsp;
import org.anime_game_servers.multi_proto.gi.messages.general.Retcode;

import java.util.List;

public class PacketPullRecentChatRsp extends BaseTypedPacket<PullRecentChatRsp> {
    public PacketPullRecentChatRsp(List<ChatInfo> messages) {
        super(new PullRecentChatRsp(Retcode.RET_SUCC, messages));
    }
}
