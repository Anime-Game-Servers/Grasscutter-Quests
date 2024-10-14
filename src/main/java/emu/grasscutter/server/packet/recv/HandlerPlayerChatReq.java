package emu.grasscutter.server.packet.recv;

import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.PacketPlayerChatRsp;
import org.anime_game_servers.multi_proto.gi.messages.community.chat.ChatInfo;
import org.anime_game_servers.multi_proto.gi.messages.community.chat.PlayerChatReq;

public class HandlerPlayerChatReq extends TypedPacketHandler<PlayerChatReq> {

    @Override
    public void handle(GameSession session, byte[] header, PlayerChatReq req) throws Exception {
        ChatInfo.Content<?> content = req.getChatInfo().getContent();

        if (content instanceof ChatInfo.Content.Text text) {
            session.getServer().getChatSystem().sendTeamMessage(session.getPlayer(), req.getChannelId(), text.getValue());
        } else if (content instanceof ChatInfo.Content.Icon icon) {
            session.getServer().getChatSystem().sendTeamMessage(session.getPlayer(), req.getChannelId(), icon.getValue());
        }

        session.send(new PacketPlayerChatRsp());
    }

}
