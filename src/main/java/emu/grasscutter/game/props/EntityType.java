package emu.grasscutter.game.props;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import emu.grasscutter.scripts.constants.IntValueEnum;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.Getter;


public enum EntityType implements IntValueEnum{
    None (0),
    Avatar (1),
    Monster (2),
    Bullet (3),
    AttackPhyisicalUnit (4),
    AOE (5),
    Camera (6),
    EnviroArea (7),
    Equip (8),
    MonsterEquip (9),
    Grass (10),
    Level (11),
    NPC (12),
    TransPointFirst (13),
    TransPointFirstGadget (14),
    TransPointSecond (15),
    TransPointSecondGadget (16),
    DropItem (17),
    Field (18),
    Gadget (19),
    Water (20),
    GatherPoint (21),
    GatherObject (22),
    AirflowField (23),
    SpeedupField (24),
    Gear (25),
    Chest (26),
    EnergyBall (27),
    ElemCrystal (28),
    Timeline (29),
    Worktop (30),
    Team (31),
    Platform (32),
    AmberWind (33),
    EnvAnimal (34),
    SealGadget (35),
    Tree (36),
    Bush (37),
    QuestGadget (38),
    Lightning (39),
    RewardPoint (40),
    RewardStatue (41),
    MPLevel (42),
    WindSeed (43),
    MpPlayRewardPoint (44),
    ViewPoint (45),
    RemoteAvatar (46),
    GeneralRewardPoint (47),
    PlayTeam (48),
    OfferingGadget (49),
    EyePoint (50),
    MiracleRing (51),
    Foundation (52),
    WidgetGadget (53),
    Vehicle (54),
    DangerZone (55),
    EchoShell (56),
    HomeGatherObject (57),
    Projector (58),
    Screen (59),
    CustomTile(60),
    FishPool (61),
    FishRod (62),
    CustomGadget (63),
    RoguelikeOperatorGadget (64),
    ActivityInteractGadget (65),
    BlackMud (66),
    SubEquip (67),
    UIInteractGadget (68),
    NightCrowGadget (69),
    Partner (70),
    DeshretObeliskGadget (71),
    CoinCollectLevelGadget (72),
    PlaceHolder (99);

    @Getter
    private final int value;
    private static final Int2ObjectMap<EntityType> map = new Int2ObjectOpenHashMap<>();
    private static final Map<String, EntityType> stringMap = new HashMap<>();

    static {
        Stream.of(values()).forEach(e -> {
            map.put(e.getValue(), e);
            stringMap.put(e.name(), e);
        });
    }

    private EntityType(int value) {
        this.value = value;
    }


    public static EntityType getTypeByValue(int value) {
        return map.getOrDefault(value, None);
    }

    public static EntityType getTypeByName(String name) {
        return stringMap.getOrDefault(name, None);
    }
}
