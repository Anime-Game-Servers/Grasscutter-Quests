package emu.grasscutter.game.expedition;

import dev.morphia.annotations.Entity;
import lombok.Getter;
import lombok.Setter;
import lombok.val;
import org.anime_game_servers.multi_proto.gi.messages.team.avatar.expedition.AvatarExpeditionInfo;
import org.anime_game_servers.multi_proto.gi.messages.general.avatar.AvatarExpeditionState;

@Entity
@Getter @Setter
public class ExpeditionInfo {
    private AvatarExpeditionState state;
    private int expId;
    private int hourTime;
    private int startTime;

    public AvatarExpeditionInfo toProto() {
        val avatarExpeditionInfo = new AvatarExpeditionInfo();
        avatarExpeditionInfo.setState(this.getState());
        avatarExpeditionInfo.setExpId(this.getExpId());
        avatarExpeditionInfo.setHourTime(this.getHourTime());
        avatarExpeditionInfo.setStartTime(this.getStartTime());
        return avatarExpeditionInfo;
    }
}
