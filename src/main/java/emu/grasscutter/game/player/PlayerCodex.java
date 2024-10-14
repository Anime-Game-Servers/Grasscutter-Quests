package emu.grasscutter.game.player;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Transient;
import emu.grasscutter.data.GameData;
import emu.grasscutter.data.excels.CodexAnimalData;
import emu.grasscutter.data.excels.CodexViewpointData;
import emu.grasscutter.game.entity.EntityMonster;
import emu.grasscutter.game.entity.GameEntity;
import emu.grasscutter.game.inventory.GameItem;
import emu.grasscutter.server.packet.send.PacketCodexDataUpdateNotify;
import lombok.Getter;
import lombok.val;
import org.anime_game_servers.multi_proto.gi.messages.codex.CodexType;

import java.util.*;

@Entity
public class PlayerCodex {
    @Transient private Player player;

    //itemId is not codexId!
    @Getter private Set<Integer> unlockedWeapon;
    @Getter private Map<Integer, Integer> unlockedAnimal;
    @Getter private Set<Integer> unlockedMaterial;
    @Getter private Set<Integer> unlockedBook;
    @Getter private Set<Integer> unlockedTip;
    @Getter private Set<Integer> unlockedView;
    @Getter private Set<Integer> unlockedReliquary;
    @Getter private Set<Integer> unlockedReliquarySuitCodex;

    public PlayerCodex() {
        this.unlockedWeapon = new HashSet<>();
        this.unlockedAnimal = new HashMap<>();
        this.unlockedMaterial = new HashSet<>();
        this.unlockedBook = new HashSet<>();
        this.unlockedTip = new HashSet<>();
        this.unlockedView = new HashSet<>();
        this.unlockedReliquary = new HashSet<>();
        this.unlockedReliquarySuitCodex = new HashSet<>();
    }

    public PlayerCodex(Player player) {
        this();
        this.player = player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public void checkAddedItem(GameItem item) {
        val itemData = item.getItemData();
        val itemId = item.getItemId();
        switch (itemData.getItemType()) {
            case ITEM_WEAPON -> {
                Optional.ofNullable(GameData.getCodexWeaponDataIdMap().get(itemId))
                    .ifPresent(codexData -> {
                        if (this.getUnlockedWeapon().add(itemId)) {
                            this.player.save();
                            this.player.sendPacket(new PacketCodexDataUpdateNotify(CodexType.CODEX_WEAPON, codexData.getId()));
                        }
                    });
            }
            case ITEM_MATERIAL -> {
                switch (itemData.getMaterialType()) {
                    // Is this check even needed?
                    case MATERIAL_FOOD, MATERIAL_WIDGET, MATERIAL_EXCHANGE, MATERIAL_AVATAR_MATERIAL, MATERIAL_NOTICE_ADD_HP -> {
                        Optional.ofNullable(GameData.getCodexMaterialDataIdMap().get(itemId))
                            .ifPresent(codexData -> {
                                if (this.getUnlockedMaterial().add(itemId)) {
                                    this.player.save();
                                    this.player.sendPacket(new PacketCodexDataUpdateNotify(CodexType.CODEX_MATERIAL, codexData.getId()));
                                }
                            });
                    }
                    default -> {}
                }
            }
            case ITEM_RELIQUARY -> {
                val reliquaryId = (itemId/10) * 10;  // Normalize to 0-substat form
                if (this.getUnlockedReliquary().add(reliquaryId))
                    checkUnlockedSuits(reliquaryId);
            }
            default -> {}
        }
    }

    public void checkAnimal(GameEntity target, CodexAnimalData.CountType countType) {
        if (target instanceof EntityMonster) {
            val monsterId = ((EntityMonster) target).getMonsterData().getId();
            val codexAnimal = GameData.getCodexAnimalDataMap().get(monsterId);
            if (codexAnimal == null) return;

            val animalCountType = codexAnimal.getCountType();
            if (animalCountType != countType && animalCountType != null) return;

            this.getUnlockedAnimal().merge(monsterId, 1, (i, j) -> i + 1);

            player.save();
            this.player.sendPacket(new PacketCodexDataUpdateNotify(CodexType.CODEX_ANIMAL, monsterId));
        }
    }

    public void checkUnlockedSuits(int reliquaryId) {
        GameData.getCodexReliquaryArrayList().stream()
            .filter(x -> !this.getUnlockedReliquarySuitCodex().contains(x.getId()))
            .filter(x -> x.containsId(reliquaryId))
            .filter(x -> this.getUnlockedReliquary().containsAll(x.getIds()))
            .forEach(x -> {
                int id = x.getId();
                this.getUnlockedReliquarySuitCodex().add(id);
                this.player.save();
                this.player.sendPacket(new PacketCodexDataUpdateNotify(CodexType.CODEX_RELIQUARY, id));
            });
    }

    public void checkUnlockedViewPoints(CodexViewpointData viewpoint) {
        this.getUnlockedView().add(viewpoint.getId());
        this.player.save();
        this.player.sendPacket(new PacketCodexDataUpdateNotify(CodexType.CODEX_VIEW, viewpoint.getId()));
    }

    public void checkBook(int bookId) {
        this.getUnlockedBook().add(bookId);
        this.player.save();
        this.player.sendPacket(new PacketCodexDataUpdateNotify(CodexType.CODEX_BOOKS, bookId));
    }
}
