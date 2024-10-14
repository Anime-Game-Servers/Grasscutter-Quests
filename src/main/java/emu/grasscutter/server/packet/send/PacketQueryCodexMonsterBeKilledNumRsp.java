package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.player.Player;
import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.codex.QueryCodexMonsterBeKilledNumRsp;

import java.util.ArrayList;
import java.util.List;

public class PacketQueryCodexMonsterBeKilledNumRsp extends BaseTypedPacket<QueryCodexMonsterBeKilledNumRsp> {
    public PacketQueryCodexMonsterBeKilledNumRsp(Player player, List<Integer> codexList) {
        super(new QueryCodexMonsterBeKilledNumRsp());
        List<Integer> codexIdList = new ArrayList<>();
        List<Integer> beKilledNumList = new ArrayList<>();
        List<Integer> beCapturedNumList = new ArrayList<>();
        codexList.stream()
            .filter(animal -> player.getCodex().getUnlockedAnimal().containsKey(animal))
            .forEach(animal -> {
                codexIdList.add(animal);
                beKilledNumList.add(player.getCodex().getUnlockedAnimal().get(animal));
                beCapturedNumList.add(0);
            });
        proto.setCodexIdList(codexIdList);
        proto.setBeKilledNumList(beKilledNumList);
        proto.setBeCapturedNumList(beCapturedNumList);
    }
}
