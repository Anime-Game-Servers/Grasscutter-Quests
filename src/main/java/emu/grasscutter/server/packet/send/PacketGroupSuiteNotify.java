package emu.grasscutter.server.packet.send;

import emu.grasscutter.data.binout.SceneNpcBornEntry;
import emu.grasscutter.game.quest.QuestGroupSuite;
import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.scene.group.GroupSuiteNotify;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PacketGroupSuiteNotify extends BaseTypedPacket<GroupSuiteNotify> {

	/**
	 * Real control which npc suite is loaded
     * EntityNPC is useless
	 */
	public PacketGroupSuiteNotify(List<SceneNpcBornEntry> npcBornEntries) {
        super(new GroupSuiteNotify());
        Map<Integer, Integer> groupMap = new HashMap<>();
        npcBornEntries.stream()
            .filter(x -> x.getGroupId() > 0 && x.getSuiteIdList() != null)
            .forEach(x -> x.getSuiteIdList().forEach(y ->
                groupMap.put(x.getGroupId(), y) //TODO: this only saves one value of y??
            ));
        proto.setGroupMap(groupMap);
	}

    public PacketGroupSuiteNotify(int groupId, int suiteId) {
        super(new GroupSuiteNotify());
        proto.setGroupMap(Map.of(groupId, suiteId));
    }

    public PacketGroupSuiteNotify(Collection<QuestGroupSuite> questGroupSuites) {
        super(new GroupSuiteNotify());
        Map<Integer, Integer> groupMap = new HashMap<>();
        questGroupSuites.forEach(i -> groupMap.put(i.getGroup(), i.getSuite()));
        proto.setGroupMap(groupMap);
    }
}
