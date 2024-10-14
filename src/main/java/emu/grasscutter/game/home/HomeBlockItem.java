package emu.grasscutter.game.home;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import emu.grasscutter.data.binout.HomeworldDefaultSaveData;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.anime_game_servers.multi_proto.gi.messages.serenitea_pot.arangement.HomeBlockArrangementInfo;

import java.util.List;

@Entity
@Data
@Builder(builderMethodName = "of")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class HomeBlockItem {
    @Id
    int blockId;
    boolean unlocked;
    List<HomeFurnitureItem> deployFurnitureList;
    List<HomeFurnitureItem> persistentFurnitureList;
    List<HomeAnimalItem> deployAnimalList;
    List<HomeNPCItem> deployNPCList;

    public void update(HomeBlockArrangementInfo homeBlockArrangementInfo) {
        this.blockId = homeBlockArrangementInfo.getBlockId();

        this.deployFurnitureList = homeBlockArrangementInfo.getDeployFurnitureList().stream()
                .map(HomeFurnitureItem::parseFrom)
                .toList();

        this.persistentFurnitureList = homeBlockArrangementInfo.getPersistentFurnitureList().stream()
                .map(HomeFurnitureItem::parseFrom)
                .toList();

        this.deployAnimalList = homeBlockArrangementInfo.getDeployAnimalList().stream()
                .map(HomeAnimalItem::parseFrom)
                .toList();

        this.deployNPCList = homeBlockArrangementInfo.getDeployNpcList().stream()
                .map(HomeNPCItem::parseFrom)
                .toList();
    }

    public int calComfort() {
        return this.deployFurnitureList.stream()
                .mapToInt(HomeFurnitureItem::getComfort)
                .sum();
    }

    public HomeBlockArrangementInfo toProto() {
        var proto = new HomeBlockArrangementInfo();
        proto.setBlockId(blockId);
        proto.setUnlocked(unlocked);
        proto.setComfortValue(calComfort());

        proto.setDeployFurnitureList(this.deployFurnitureList.stream().map(HomeFurnitureItem::toProto).toList());
        proto.setPersistentFurnitureList(this.persistentFurnitureList.stream().map(HomeFurnitureItem::toProto).toList());
        proto.setDeployAnimalList(this.deployAnimalList.stream().map(HomeAnimalItem::toProto).toList());
        proto.setDeployNpcList(this.deployNPCList.stream().map(HomeNPCItem::toProto).toList());

        return proto;
    }

    public static HomeBlockItem parseFrom(HomeworldDefaultSaveData.HomeBlock homeBlock) {
        // create from default setting
        return HomeBlockItem.of()
                .blockId(homeBlock.getBlockId())
                .unlocked(homeBlock.getFurnitures() != null)
                .deployFurnitureList(
                        homeBlock.getFurnitures() == null ? List.of() :
                                homeBlock.getFurnitures().stream()
                                        .map(HomeFurnitureItem::parseFrom)
                                        .toList())
                .persistentFurnitureList(
                        homeBlock.getPersistentFurnitures() == null ? List.of() :
                                homeBlock.getPersistentFurnitures().stream()
                                        .map(HomeFurnitureItem::parseFrom)
                                        .toList())
                .deployAnimalList(List.of())
                .deployNPCList(List.of())
                .build();
    }
}
