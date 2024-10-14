package emu.grasscutter.net.packet;

import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import emu.grasscutter.GameConstants;
import emu.grasscutter.Grasscutter;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.utils.JsonUtils;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.val;

public class PacketOpcodesUtils {
    private static Int2ObjectMap<String> opcodeMap;

    public static final Set<String> BANNED_PACKETS = Set.of(
        "WindSeedClientNotify",
        "PlayerLuaShellNotify"
    );

    public static final Set<String> LOOP_PACKETS = Set.of(
        "PingReq",
        "PingRsp",
        "WorldPlayerRTTNotify",
        "UnionCmdNotify",
        "QueryPathReq",
        "QueryPathRsp",

        // Satiation sends these every tick
        "PlayerTimeNotify",
        "PlayerGameTimeNotify",
        "AvatarPropNotify",
        "AvatarSatiationDataNotify"
    );

    static {
        opcodeMap = new Int2ObjectOpenHashMap<String>();

        Field[] fields = PacketOpcodes.class.getFields();

        for (Field f : fields) {
            if (f.getType().equals(int.class)) {
                try {
                    opcodeMap.put(f.getInt(null), f.getName());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static String getOpcodeName(int opcode, GameSession session) {
        if (opcode <= 0) return "UNKNOWN";
        val name = session.getPackageIdProvider().getPacketName(opcode);
        return name != null ? name : opcodeMap.getOrDefault(opcode, "UNKNOWN");
    }

    public static void dumpPacketIds() {
        try (FileWriter writer = new FileWriter("./PacketIds_" + GameConstants.VERSION + ".json")) {
            // Create sorted tree map
            Map<Integer, String> packetIds = opcodeMap.int2ObjectEntrySet().stream()
                    .filter(e -> e.getIntKey() > 0)
                    .collect(Collectors.toMap(Int2ObjectMap.Entry::getIntKey, Int2ObjectMap.Entry::getValue, (k, v) -> v, TreeMap::new));
            // Write to file
            writer.write(JsonUtils.encode(packetIds));
            Grasscutter.getLogger().info("Dumped packet ids.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
