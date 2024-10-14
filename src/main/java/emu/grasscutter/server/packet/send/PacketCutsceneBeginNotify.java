package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.scene.cutscene.CutSceneBeginNotify;

public class PacketCutsceneBeginNotify extends BaseTypedPacket<CutSceneBeginNotify> {
    public PacketCutsceneBeginNotify(int cutsceneId) {
        super(new CutSceneBeginNotify());
        proto.setCutsceneId(cutsceneId);
    }
}
