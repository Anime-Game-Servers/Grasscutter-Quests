package emu.grasscutter.game.home;

import dev.morphia.annotations.Entity;
import emu.grasscutter.utils.Position;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import lombok.val;
import org.anime_game_servers.multi_proto.gi.messages.serenitea_pot.arangement.HomeAnimalData;

@Entity
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder(builderMethodName = "of")
public class HomeAnimalItem {
    int furnitureId;
    Position spawnPos;
    Position spawnRot;

    public HomeAnimalData toProto() {
        val proto = new HomeAnimalData();
        proto.setFurnitureId(furnitureId);
        proto.setSpawnPos(spawnPos.toProto());
        proto.setSpawnRot(spawnRot.toProto());
        return proto;
    }

    public static HomeAnimalItem parseFrom(HomeAnimalData homeAnimalData) {
        return HomeAnimalItem.of()
                .furnitureId(homeAnimalData.getFurnitureId())
                .spawnPos(new Position(homeAnimalData.getSpawnPos()))
                .spawnRot(new Position(homeAnimalData.getSpawnRot()))
                .build();
    }

}
