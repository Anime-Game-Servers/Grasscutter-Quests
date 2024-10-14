package emu.grasscutter.server.packet.send;

import emu.grasscutter.data.GameData;
import emu.grasscutter.data.excels.CodexAnimalData;
import emu.grasscutter.data.excels.CodexMaterialData;
import emu.grasscutter.data.excels.CodexWeaponData;
import emu.grasscutter.game.player.Player;
import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.codex.CodexDataFullNotify;
import org.anime_game_servers.multi_proto.gi.messages.codex.CodexType;
import org.anime_game_servers.multi_proto.gi.messages.codex.CodexTypeData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class PacketCodexDataFullNotify extends BaseTypedPacket<CodexDataFullNotify> {
    public PacketCodexDataFullNotify(Player player) {
        super(new CodexDataFullNotify(), true);

        //Quests
        CodexTypeData questTypeData = new CodexTypeData();
        questTypeData.setType(CodexType.CODEX_QUEST);

        //Weapons
        CodexTypeData weaponTypeData = new CodexTypeData();
        weaponTypeData.setType(CodexType.CODEX_WEAPON);

        //Animals
        CodexTypeData animalTypeData = new CodexTypeData();
        animalTypeData.setType(CodexType.CODEX_ANIMAL);

        //Materials
        CodexTypeData materialTypeData = new CodexTypeData();
        materialTypeData.setType(CodexType.CODEX_MATERIAL);

        //Books
        CodexTypeData bookTypeData = new CodexTypeData();
        bookTypeData.setType(CodexType.CODEX_BOOKS);

        //Tips
        CodexTypeData pushTipsTypeData = new CodexTypeData();
        pushTipsTypeData.setType(CodexType.CODEX_PUSHTIPS);

        //Views
        CodexTypeData viewTypeData = new CodexTypeData();
        viewTypeData.setType(CodexType.CODEX_VIEW);

        //Reliquary
        CodexTypeData reliquaryData = new CodexTypeData();
        reliquaryData.setType(CodexType.CODEX_RELIQUARY);

        //Quests
        List<Integer> questCodexIdList = new ArrayList<>();
        player.getQuestManager().forEachMainQuest(mainQuest -> {
            if(mainQuest.isFinished()){
                var codexQuest = GameData.getCodexQuestDataIdMap().get(mainQuest.getParentQuestId());
                if(codexQuest != null){
                    questCodexIdList.add(codexQuest.getId());
                }
            }
        });
        questTypeData.setCodexIdList(questCodexIdList);
        questTypeData.setHaveViewedList(Collections.nCopies(questCodexIdList.size(), true));

        //Weapons
        List<Integer> weaponCodexIdList = new ArrayList<>(player.getCodex().getUnlockedWeapon().stream()
            .map(weapon -> GameData.getCodexWeaponDataIdMap().get((int) weapon))
            .filter(Objects::nonNull)
            .map(CodexWeaponData::getId)
            .toList());
        weaponTypeData.setCodexIdList(weaponCodexIdList);
        weaponTypeData.setHaveViewedList(Collections.nCopies(weaponCodexIdList.size(), true));

        //Animals
        List<Integer> animalCodexIdList = new ArrayList<>(player.getCodex().getUnlockedAnimal().keySet().stream()
            .map(animal -> GameData.getCodexAnimalDataMap().get((int) animal))
            .filter(Objects::nonNull)
            .map(CodexAnimalData::getId)
            .toList());
        animalTypeData.setCodexIdList(animalCodexIdList);
        animalTypeData.setHaveViewedList(Collections.nCopies(animalCodexIdList.size(), true));

        //Materials
        List<Integer> materialCodexIdList = new ArrayList<>(player.getCodex().getUnlockedMaterial().stream()
            .map(material -> GameData.getCodexMaterialDataIdMap().get((int) material))
            .filter(Objects::nonNull)
            .map(CodexMaterialData::getId)
            .toList());
        materialTypeData.setCodexIdList(materialCodexIdList);
        materialTypeData.setHaveViewedList(Collections.nCopies(materialCodexIdList.size(), true));

        //Books
        bookTypeData.setCodexIdList(player.getCodex().getUnlockedBook().stream().toList());
        bookTypeData.setHaveViewedList(Collections.nCopies(player.getCodex().getUnlockedBook().size(), true));

        //Tips
        //TODO: Tips

        //Views
        viewTypeData.setCodexIdList(player.getCodex().getUnlockedView().stream().toList());
        viewTypeData.setHaveViewedList(Collections.nCopies(player.getCodex().getUnlockedView().size(), true));

        //Reliquary
        reliquaryData.setCodexIdList(player.getCodex().getUnlockedReliquarySuitCodex().stream().toList());
        reliquaryData.setHaveViewedList(Collections.nCopies(player.getCodex().getUnlockedReliquarySuitCodex().size(), true));

        List<CodexTypeData> codexTypeDataList = new ArrayList<>();
        codexTypeDataList.add(questTypeData);
        codexTypeDataList.add(weaponTypeData);
        codexTypeDataList.add(animalTypeData);
        codexTypeDataList.add(materialTypeData);
        codexTypeDataList.add(bookTypeData);
        codexTypeDataList.add(pushTipsTypeData);
        codexTypeDataList.add(viewTypeData);
        codexTypeDataList.add(reliquaryData);
        proto.setTypeDataList(codexTypeDataList);
    }
}
