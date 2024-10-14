package emu.grasscutter.game.home;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import lombok.val;
import org.anime_game_servers.multi_proto.gi.messages.serenitea_pot.furniture.FurnitureMakeData;

@Entity
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder(builderMethodName = "of")
public class FurnitureMakeSlotItem {
    @Id
    int index;
    int makeId;
    int avatarId;
    int beginTime;
    int durTime;

    public FurnitureMakeData toProto() {
        val proto = new FurnitureMakeData();
        proto.setIndex(index);
        proto.setAvatarId(avatarId);
        proto.setMakeId(makeId);
        proto.setBeginTime(beginTime);
        proto.setDurTime(durTime);
        return proto;
    }
}
