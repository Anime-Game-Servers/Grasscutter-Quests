package emu.grasscutter.game.avatar;

import emu.grasscutter.Grasscutter;
import emu.grasscutter.data.GameData;
import emu.grasscutter.data.common.BaseTrialAvatarData;
import emu.grasscutter.data.common.BaseTrialAvatarTemplateData;
import emu.grasscutter.data.excels.AvatarCostumeData;
import emu.grasscutter.data.excels.TrialReliquaryData;
import emu.grasscutter.game.entity.EntityWeapon;
import emu.grasscutter.game.inventory.EquipType;
import emu.grasscutter.game.inventory.GameItem;
import emu.grasscutter.server.packet.send.PacketAvatarEquipChangeNotify;
import lombok.Getter;
import lombok.Setter;
import lombok.val;
import org.anime_game_servers.multi_proto.gi.messages.general.avatar.AvatarInfo;
import org.anime_game_servers.multi_proto.gi.messages.general.avatar.GrantReason;
import org.anime_game_servers.multi_proto.gi.messages.general.avatar.TrialAvatarGrantRecord;
import org.anime_game_servers.multi_proto.gi.messages.general.avatar.TrialAvatarInfo;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.List;

public class TrialAvatar extends Avatar{
    // trial avatar property
    @Getter @Setter private int trialAvatarId = 0;
    // cannot store to db if grant reason is not integer
    @Getter @Setter private GrantReason grantReason = GrantReason.INVALID;
    @Getter @Setter private int fromParentQuestId = 0;
    // so far no outer class or prop value has information of this, but from packet I sniff
    // 1 = normal, 2 = trial avatar
    @Getter private final int avatarType = 2;

    public TrialAvatar(@NotNull List<Integer> trialAvatarParam, int trialAvatarId, @NotNull GrantReason grantReason, int fromParentQuestId) {
        super(trialAvatarParam.get(0));
        this.setLevel(trialAvatarParam.get(1));
        this.setPromoteLevel(getMinPromoteLevel(trialAvatarParam.get(1)));
        this.setTrialAvatarId(trialAvatarId);
        this.setGrantReason(grantReason);
        this.setFromParentQuestId(fromParentQuestId);
        this.setTrialSkillLevel();
        this.setTrialItems();
    }

    public static boolean useCustomData() {
        return !(GameData.getTrialAvatarCustomData() == null || GameData.getTrialAvatarCustomData().isEmpty());
    }

    public static List<Integer> getTrialAvatarParam(int trialAvatarId) {
        val trialData = TrialAvatar.useCustomData() ? GameData.getTrialAvatarCustomData() : GameData.getTrialAvatarDataMap();
        BaseTrialAvatarData trialAvatarData = trialData.get(trialAvatarId);
        return (trialAvatarData == null) ? List.of() : trialAvatarData.getTrialAvatarParamList();
    }

    private int getTrialAvatarTemplateLevel(){
        return GameData.getTrialAvatarTemplateDataMap().keySet().stream()
            .min(Comparator.comparingInt(value -> Math.abs(value - getLevel())))
            .stream().findFirst().orElse(0);
    }

    public int getTrialSkillLevel() {
        val trialData = useCustomData() ? GameData.getTrialAvatarCustomData() : GameData.getTrialAvatarTemplateDataMap();
        int skillOrId = useCustomData() ? getTrialAvatarId() : getTrialAvatarTemplateLevel();
        BaseTrialAvatarTemplateData trialAvatarData = trialData.get(skillOrId);
        return (trialAvatarData == null) ? 1 : trialAvatarData.getTrialAvatarSkillLevel();
    }

    public void setTrialSkillLevel() {
        getSkillLevelMap().keySet().forEach(skill -> setSkillLevel(skill, getTrialSkillLevel()));
    }

    public int getTrialWeaponId() {
        val trialData = useCustomData() ? GameData.getTrialAvatarCustomData() : GameData.getTrialAvatarDataMap();
        BaseTrialAvatarData trialAvatarData = trialData.get(getTrialAvatarId());

        return (trialAvatarData == null || trialAvatarData.getTrialAvatarWeaponList().size() < 1) ?
            getAvatarData().getInitialWeapon() + 100 : trialAvatarData.getTrialAvatarWeaponList().get(0);
    }

    public List<Integer> getTrialReliquary() {
        val trialData = useCustomData() ? GameData.getTrialAvatarCustomData() : GameData.getTrialAvatarTemplateDataMap();
        int skillOrId = useCustomData() ? getTrialAvatarId() : getTrialAvatarTemplateLevel();
        BaseTrialAvatarTemplateData trialAvatarData = trialData.get(skillOrId);

        return (trialAvatarData == null || trialAvatarData.getTrialReliquaryList().isEmpty()) ?
            GameData.getTrialAvatarTemplateDataMap().get(getTrialAvatarTemplateLevel()).getTrialReliquaryList() :
            trialAvatarData.getTrialReliquaryList();
    }

    public void setTrialItems(){
        // add enhanced version of trial weapon
        GameItem weapon = new GameItem(getTrialWeaponId());
        weapon.setLevel(getLevel());
        weapon.setExp(0);
        Grasscutter.getLogger().info("Min promote level: {}", getMinPromoteLevel(getLevel()));
        weapon.setPromoteLevel(getMinPromoteLevel(getLevel()));
        getEquips().put(weapon.getEquipSlot(), weapon);

        // add Trial Artifacts
        getTrialReliquary().forEach(id -> {
            TrialReliquaryData reliquaryData = GameData.getTrialReliquaryDataMap().get(id.intValue());
            if (reliquaryData == null) return;

            GameItem relic = new GameItem(reliquaryData.getReliquaryId());
            relic.setLevel(reliquaryData.getLevel());
            relic.setMainPropId(reliquaryData.getMainPropId());
            relic.getAppendPropIdList().addAll(reliquaryData.getAppendPropList());
            getEquips().put(relic.getEquipSlot(), relic);
        });

        // add costume if any (Amber, Rosaria, Mona, Jean)
        this.setCostume(GameData.getAvatarCostumeDataItemIdMap().values()
            .stream().map(AvatarCostumeData::getCharacterId)
            .filter(characterId -> characterId == this.getAvatarId())
            .findAny().orElse(0));
    }

    public void equipTrialItems(){
        getEquips().forEach((itemEquipTypeValues, item) -> {
            item.setEquipCharacter(getAvatarId());
            item.setOwner(getPlayer());
            if (item.getItemData().getEquipType() == EquipType.EQUIP_WEAPON && getPlayer().getWorld() != null) {
                if(!(item.getWeaponEntity() != null && item.getWeaponEntity().getScene() == getPlayer().getScene())) {
                    item.setWeaponEntity(new EntityWeapon(this.getPlayer().getScene(), item.getItemData().getGadgetId()));
                    getPlayer().getScene().getWeaponEntities().put(item.getWeaponEntity().getId(), item.getWeaponEntity());
                }
                getPlayer().sendPacket(new PacketAvatarEquipChangeNotify(this, item));
            }
        });
    }

    public TrialAvatarInfo trialAvatarInfoProto(){
        val trialAvatar = new TrialAvatarInfo(this.getTrialAvatarId());

        trialAvatar.setGrantRecord(new TrialAvatarGrantRecord(this.getGrantReason().encodeToByteArray(getPlayer().getSession().getVersion()), this.getFromParentQuestId()));

        if (this.getTrialAvatarId() > 0){ // if it is actual trial avatar
            // add artifacts and weapon for trial character
            trialAvatar.setTrialEquipList(this.getEquips().values().stream().map(GameItem::toProto).toList());
        }

        return trialAvatar;
    }

    @Override
    public AvatarInfo protoBuilder() {
        val proto =  super.protoBuilder();

        proto.setAvatarType(getAvatarType());
        proto.setTrialAvatarInfo(this.trialAvatarInfoProto());
        return proto;
    }
}
