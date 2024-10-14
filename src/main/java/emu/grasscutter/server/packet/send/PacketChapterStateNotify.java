package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.quest.chapter.ChapterState;
import org.anime_game_servers.multi_proto.gi.messages.quest.chapter.ChapterStateNotify;

public class PacketChapterStateNotify extends BaseTypedPacket<ChapterStateNotify> {

    public PacketChapterStateNotify(int id, ChapterState state) {
        super(new ChapterStateNotify());
        proto.setChapterId(id);
        proto.setChapterState(state);
	}
}
