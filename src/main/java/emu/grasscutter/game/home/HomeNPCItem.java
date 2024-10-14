package emu.grasscutter.game.home;

import dev.morphia.annotations.Entity;
import emu.grasscutter.utils.Position;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import lombok.val;
import org.anime_game_servers.multi_proto.gi.messages.serenitea_pot.HomeNpcData;

@Entity
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder(builderMethodName = "of")
public class HomeNPCItem {
    int avatarId;
    Position spawnPos;
    Position spawnRot;
    int costumeId;

    public HomeNpcData toProto() {
        val proto = new HomeNpcData();
        proto.setAvatarId(avatarId);
        proto.setSpawnPos(spawnPos.toProto());
        proto.setSpawnRot(spawnRot.toProto());
        proto.setCostumeId(costumeId);
        return proto;
    }

    public static HomeNPCItem parseFrom(HomeNpcData homeNpcData) {
        return HomeNPCItem.of()
                .avatarId(homeNpcData.getAvatarId())
                .spawnPos(new Position(homeNpcData.getSpawnPos()))
                .spawnRot(new Position(homeNpcData.getSpawnRot()))
                .costumeId(homeNpcData.getCostumeId())
                .build();
    }
}
