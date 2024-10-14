package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.player.Player;
import emu.grasscutter.game.world.WorldDataSystem;
import emu.grasscutter.net.packet.BaseTypedPacket;
import lombok.val;
import org.anime_game_servers.multi_proto.gi.messages.world.investigation.GetInvestigationMonsterRsp;
import org.anime_game_servers.multi_proto.gi.messages.world.investigation.InvestigationMonster;

import java.util.ArrayList;
import java.util.List;

public class PacketGetInvestigationMonsterRsp extends BaseTypedPacket<GetInvestigationMonsterRsp> {
    public PacketGetInvestigationMonsterRsp(Player player, WorldDataSystem worldDataManager, List<Integer> cityIdListList) {
        super(new GetInvestigationMonsterRsp());
        val monsterList = new ArrayList<InvestigationMonster>();
        cityIdListList.forEach(id -> monsterList.addAll(worldDataManager.getInvestigationMonstersByCityId(player, id)));
        proto.setMonsterList(monsterList);
    }
}
