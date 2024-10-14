package emu.grasscutter.game.inventory;

import dev.morphia.annotations.*;
import emu.grasscutter.data.GameData;
import emu.grasscutter.data.GameDepot;
import emu.grasscutter.data.common.ItemParamData;
import emu.grasscutter.data.excels.ItemData;
import emu.grasscutter.data.excels.ReliquaryAffixData;
import emu.grasscutter.data.excels.ReliquaryMainPropData;
import emu.grasscutter.database.DatabaseHelper;
import emu.grasscutter.game.entity.EntityWeapon;
import emu.grasscutter.game.player.Player;
import emu.grasscutter.game.props.FightProperty;
import emu.grasscutter.utils.WeightedList;
import lombok.Getter;
import lombok.Setter;
import lombok.val;
import org.anime_game_servers.multi_proto.gi.messages.general.ability.AbilitySyncStateInfo;
import org.anime_game_servers.multi_proto.gi.messages.general.entity.SceneReliquaryInfo;
import org.anime_game_servers.multi_proto.gi.messages.general.entity.SceneWeaponInfo;
import org.anime_game_servers.multi_proto.gi.messages.general.item.*;
import org.anime_game_servers.multi_proto.gi.messages.item.ItemHint;
import org.bson.types.ObjectId;

import java.util.*;
import java.util.stream.Collectors;

@Entity(value = "items", useDiscriminator = false)
public class GameItem {
    @Id private ObjectId id;
    @Indexed protected int ownerId;
    @Getter @Setter protected int itemId;
    @Getter @Setter protected int count;

    @Transient @Getter protected long guid; // Player unique id
    @Transient @Getter @Setter protected ItemData itemData;

    // Equips
    @Getter @Setter protected int level;
    @Getter @Setter protected int exp;
    @Getter @Setter protected int totalExp;
    @Getter @Setter protected int promoteLevel;
    @Getter @Setter protected boolean locked;

    // Weapon
    @Getter protected final List<Integer> affixes = new ArrayList<>();
    @Getter @Setter protected int refinement = 0;

    // Relic
    @Getter @Setter protected int mainPropId;
    @Getter protected final List<Integer> appendPropIdList = new ArrayList<>();

    @Getter @Setter protected int equipCharacter;
    @Transient @Getter @Setter private EntityWeapon weaponEntity;
    @Transient @Getter protected boolean newItem = false;

    public GameItem() {
        // Morphia only
    }

    public GameItem(int itemId) {
        this(GameData.getItemDataMap().get(itemId));
    }

    public GameItem(int itemId, int count) {
        this(GameData.getItemDataMap().get(itemId), count);
    }

    public GameItem(ItemParamData itemParamData) {
        this(itemParamData.getId(), itemParamData.getCount());
    }

    public GameItem(ItemData data) {
        this(data, 1);
    }

    public GameItem(ItemData data, int count) {
        this.itemId = data.getId();
        this.itemData = data;

        switch (data.getItemType()) {
            case ITEM_VIRTUAL -> this.count = count;
            case ITEM_WEAPON -> {
                this.count = 1;
                this.level = 1;
                Optional.ofNullable(data.getSkillAffix()).stream().flatMapToInt(Arrays::stream)
                    .filter(skillAffix -> skillAffix > 0).forEach(this.affixes::add);
            }
            case ITEM_RELIQUARY -> {
                this.count = 1;
                this.level = 1;
                // Create main property
                Optional.ofNullable(GameDepot.getRandomRelicMainProp(data.getMainPropDepotId()))
                    .ifPresent(mainPropData -> this.mainPropId = mainPropData.getId());
                // Create extra stats
                addAppendProps(data.getAppendPropNum());
            }
            default -> this.count = Math.min(count, data.getStackLimit());
        }
    }

    public void setOwner(Player player) {
        this.ownerId = player.getUid();
        this.guid = player.getNextGameGuid();
    }

    public void checkIsNew(Inventory inventory){
        // display notification when player obtain new item
        this.newItem = inventory.getItemCountById(this.itemId) < 1;
    }

    public ObjectId getObjectId() {
        return id;
    }

    public ItemType getItemType() {
        return this.itemData.getItemType();
    }

    public static int getMinPromoteLevel(int level) {
        if (level > 80) {
            return 6;
        } else if (level > 70) {
            return 5;
        } else if (level > 60) {
            return 4;
        } else if (level > 50) {
            return 3;
        } else if (level > 40) {
            return 2;
        } else if (level > 20) {
            return 1;
        }
        return 0;
    }

    public int getEquipSlot() {
        return this.itemData.getEquipType().getValue();
    }

    public boolean isEquipped() {
        return this.equipCharacter > 0;
    }

    public boolean isDestroyable() {
        return !this.isLocked() && !this.isEquipped();
    }

    public void addAppendProp() {
        if (this.appendPropIdList.size() < 4) {
            addNewAppendProp();
        } else {
            upgradeRandomAppendProp();
        }
    }

    public void addAppendProps(int quantity) {
        for (int i = 0; i < Math.max(quantity, 0); i++) {
            addAppendProp();
        }
    }

    private Set<FightProperty> getAppendFightProperties() {
        // Previously this would check no more than the first four affixes, however custom artifacts may not respect this order.
        return this.appendPropIdList.stream().map(appendPropId -> GameData.getReliquaryAffixDataMap().get(appendPropId.intValue()))
            .filter(Objects::nonNull).map(ReliquaryAffixData::getFightProp).collect(Collectors.toSet());
    }

    private void addNewAppendProp() {
        val affixList = GameDepot.getRelicAffixList(this.itemData.getAppendPropDepotId());
        if (affixList == null) return;

        // Build blacklist - Don't add same stat as main/sub stat
        val blacklist = getAppendFightProperties();
        Optional.ofNullable(GameData.getReliquaryMainPropDataMap().get(this.mainPropId))
            .map(ReliquaryMainPropData::getFightProp).ifPresent(blacklist::add);

        // Build random list
        val randomList = new WeightedList<ReliquaryAffixData>();
        for (val affix : affixList) {
            if (!blacklist.contains(affix.getFightProp())) {
                randomList.add(affix.getWeight(), affix);
            }
        }

        if (randomList.size() == 0) return;

        // Add random stat
        this.appendPropIdList.add(randomList.next().getId());
    }

    private void upgradeRandomAppendProp() {
        val affixList = GameDepot.getRelicAffixList(this.itemData.getAppendPropDepotId());

        if (affixList == null) return;

        // Build whitelist
        val whitelist = getAppendFightProperties();

        // Build random list
        val randomList = new WeightedList<ReliquaryAffixData>();
        for (val affix : affixList) {
            if (whitelist.contains(affix.getFightProp())) {
                randomList.add(affix.getUpgradeWeight(), affix);
            }
        }

        // Add random stat
        this.appendPropIdList.add(randomList.next().getId());
    }

    @PostLoad
    public void onLoad() {
        if (this.itemData == null) {
            this.itemData = GameData.getItemDataMap().get(getItemId());
        }
    }

    public void save() {
        if (this.count > 0 && this.ownerId > 0) {
            DatabaseHelper.saveItem(this);
        } else if (this.id != null) {
            DatabaseHelper.deleteItem(this);
        }
    }

    public SceneWeaponInfo createSceneWeaponInfo() {
        val entityId = Optional.ofNullable(this.weaponEntity).map(EntityWeapon::getId).orElse(0);
        val weaponInfo = new SceneWeaponInfo(entityId, this.itemData.getGadgetId(), this.itemId, this.guid, this.level);
        weaponInfo.setAbilityInfo(new AbilitySyncStateInfo(!this.affixes.isEmpty()));

        weaponInfo.setAffixMap(this.affixes.stream().collect(Collectors.toMap(affix->affix, affix->getRefinement())));

        return weaponInfo;
    }

    public SceneReliquaryInfo createSceneReliquaryInfo() {
        return new SceneReliquaryInfo(this.itemId, this.guid, this.level);
    }

    public Weapon toWeaponProto() {
        val weapon = new Weapon(this.level, this.exp, this.promoteLevel);

        weapon.setAffixMap(getAffixes().stream().collect(Collectors.toMap(affix -> affix, affix -> getRefinement())));

        return weapon;
    }

    public Reliquary toReliquaryProto() {
        return new Reliquary(this.level, this.exp, this.promoteLevel, this.mainPropId, this.appendPropIdList);
    }

    public Item toProto() {
        val proto = new Item(this.itemId, this.guid);

        proto.setDetail(switch (getItemType()) {
            case ITEM_WEAPON -> {
                val weapon = new Equip.Detail.Weapon(toWeaponProto());
                val equip = new Equip(weapon, this.locked);
                yield new Item.Detail.Equip(equip);
            }
            case ITEM_RELIQUARY -> {
                val reliquary = new Equip.Detail.Reliquary(toReliquaryProto());
                val equip = new Equip(reliquary, this.locked);
                yield new Item.Detail.Equip(equip);
            }
            case ITEM_FURNITURE -> {
                val furniture = new Furniture(this.count);
                yield new Item.Detail.Furniture(furniture);
            }
            default -> {
                val material = new Material(this.count);
                yield new Item.Detail.Material(material);
            }
        });

        return proto;
    }

    public ItemHint toItemHintProto() {
        ItemHint proto = new ItemHint();
        proto.setCount(this.count);
        proto.setNew(this.newItem);
        proto.setItemId(this.itemId);
        return proto;

    }

    public ItemParam toItemParam() {
        return new ItemParam(this.itemId, this.count);
    }
}
