package emu.grasscutter.game.systems;

import emu.grasscutter.Grasscutter;
import emu.grasscutter.data.DataLoader;
import emu.grasscutter.game.player.Player;
import emu.grasscutter.game.world.World;
import emu.grasscutter.server.game.BaseGameSystem;
import emu.grasscutter.server.game.GameServer;
import emu.grasscutter.server.packet.send.PacketServerAnnounceNotify;
import emu.grasscutter.server.packet.send.PacketServerAnnounceRevokeNotify;
import emu.grasscutter.utils.Utils;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.anime_game_servers.multi_proto.gi.messages.activity.announce.AnnounceData;

import java.util.*;

@Getter
public class AnnouncementSystem extends BaseGameSystem {
    private final Map<Integer, AnnounceConfigItem> announceConfigItemMap;

    public AnnouncementSystem(GameServer server) {
        super(server);
        this.announceConfigItemMap = new HashMap<>();
        loadConfig();
    }

    private int loadConfig() {
        try {
            List<AnnounceConfigItem> announceConfigItems = DataLoader.loadList("Announcement.json", AnnounceConfigItem.class);

            announceConfigItemMap.clear();
            announceConfigItems.forEach(i -> announceConfigItemMap.put(i.getTemplateId(), i));
        } catch (Exception e) {
            Grasscutter.getLogger().error("Unable to load server announce config.", e);
        }

        return announceConfigItemMap.size();
    }

    public List<Player> getOnlinePlayers() {
        return getServer().getWorlds().stream()
            .map(World::getPlayers)
            .flatMap(Collection::stream)
            .toList();
    }

    public void broadcast(List<AnnounceConfigItem> tpl) {
        if (tpl == null || tpl.size() == 0) {
            return;
        }

        var list = tpl.stream()
            .map(AnnounceConfigItem::toProto)
            .toList();

        getOnlinePlayers().forEach(i -> i.sendPacket(new PacketServerAnnounceNotify(list)));
    }

    public int refresh() {
        return loadConfig();
    }

    public void revoke(int tplId) {
        getOnlinePlayers().forEach(i -> i.sendPacket(new PacketServerAnnounceRevokeNotify(tplId)));
    }

    @Data
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public class AnnounceConfigItem{
        int templateId;
        AnnounceType type;
        int frequency;
        String content;
        Date beginTime;
        Date endTime;
        boolean tick;
        int interval;

        public AnnounceData toProto() {
            var proto = new AnnounceData();

            proto.setConfigId(templateId);
                // I found the time here is useless
            proto.setBeginTime(Utils.getCurrentSeconds() + 1);
            proto.setEndTime(Utils.getCurrentSeconds() + 10);

            if (type == AnnounceType.CENTER) {
                proto.setCenterSystemText(content);
                proto.setCenterSystemFrequency(frequency);
            }else {
                proto.setCountDownText(content);
                proto.setCountDownFrequency(frequency);
            }

            return proto;
        }
    }

    public enum AnnounceType{
        CENTER, COUNTDOWN
    }
}
