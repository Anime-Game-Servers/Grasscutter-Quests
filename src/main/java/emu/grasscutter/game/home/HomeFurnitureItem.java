package emu.grasscutter.game.home;

import dev.morphia.annotations.Entity;
import emu.grasscutter.data.GameData;
import emu.grasscutter.data.binout.HomeworldDefaultSaveData;
import emu.grasscutter.data.excels.ItemData;
import emu.grasscutter.utils.Position;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import lombok.val;
import org.anime_game_servers.multi_proto.gi.messages.serenitea_pot.furniture.HomeFurnitureData;
import org.anime_game_servers.multi_proto.gi.messages.serenitea_pot.mark_point.HomeMarkPointFurnitureData;

@Entity
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder(builderMethodName = "of")
public class HomeFurnitureItem {
    int furnitureId;
    int guid;
    int parentFurnitureIndex;
    Position spawnPos;
    Position spawnRot;
    int version;

    public HomeFurnitureData toProto() {
        val proto = new HomeFurnitureData();
        proto.setFurnitureId(furnitureId);
        proto.setGuid(guid);
        proto.setParentFurnitureIndex(parentFurnitureIndex);
        proto.setSpawnPos(spawnPos.toProto());
        proto.setSpawnRot(spawnRot.toProto());
        proto.setVersion(version);
        return proto;
    }

    public HomeMarkPointFurnitureData toMarkPointProto(int type) {
        val proto = new HomeMarkPointFurnitureData();
        proto.setFurnitureId(furnitureId);
        proto.setGuid(guid);
        proto.setFurnitureType(type);
        proto.setPos(spawnPos.toProto());
                // TODO NPC and farm
        return proto;
    }

    public static HomeFurnitureItem parseFrom(HomeFurnitureData homeFurnitureData) {
        return HomeFurnitureItem.of()
                .furnitureId(homeFurnitureData.getFurnitureId())
                .guid(homeFurnitureData.getGuid())
                .parentFurnitureIndex(homeFurnitureData.getParentFurnitureIndex())
                .spawnPos(new Position(homeFurnitureData.getSpawnPos()))
                .spawnRot(new Position(homeFurnitureData.getSpawnRot()))
                .version(homeFurnitureData.getVersion())
                .build();
    }

    public static HomeFurnitureItem parseFrom(HomeworldDefaultSaveData.HomeFurniture homeFurniture) {
        return HomeFurnitureItem.of()
                .furnitureId(homeFurniture.getId())
                .parentFurnitureIndex(1)
                .spawnPos(homeFurniture.getPos() == null ? new Position() : homeFurniture.getPos())
                .spawnRot(homeFurniture.getRot() == null ? new Position() : homeFurniture.getRot())
                .build();
    }

    public ItemData getAsItem() {
        return GameData.getItemDataMap().get(this.furnitureId);
    }

    public int getComfort() {
        var item = getAsItem();

        if (item == null){
            return 0;
        }
        return item.getComfort();
    }
}
