package emu.grasscutter.server.packet.send;

import emu.grasscutter.data.GameData;
import emu.grasscutter.game.quest.GameMainQuest;
import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.codex.CodexDataUpdateNotify;
import org.anime_game_servers.multi_proto.gi.messages.codex.CodexType;

public class PacketCodexDataUpdateNotify extends BaseTypedPacket<CodexDataUpdateNotify> {
    public PacketCodexDataUpdateNotify(GameMainQuest quest) {
        super(new CodexDataUpdateNotify(), true);
        var codexQuest = GameData.getCodexQuestDataIdMap().get(quest.getParentQuestId());
        if (codexQuest == null) return;
        proto.setType(CodexType.CODEX_QUEST);
        proto.setId(codexQuest.getId());
    }

    public PacketCodexDataUpdateNotify(CodexType codexType, int codexId) {
        super(new CodexDataUpdateNotify(), true);
        proto.setType(codexType);
        proto.setId(codexId);
    }
}
