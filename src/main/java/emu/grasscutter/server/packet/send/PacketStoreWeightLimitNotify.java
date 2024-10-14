package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import org.anime_game_servers.multi_proto.gi.messages.storage.StoreType;
import org.anime_game_servers.multi_proto.gi.messages.storage.StoreWeightLimitNotify;

import static emu.grasscutter.config.Configuration.INVENTORY_LIMITS;

public class PacketStoreWeightLimitNotify extends BaseTypedPacket<StoreWeightLimitNotify> {
    public PacketStoreWeightLimitNotify() {
        super(new StoreWeightLimitNotify());
        proto.setStoreType(StoreType.STORE_PACK);
        proto.setWeightLimit(INVENTORY_LIMITS.all);
        proto.setWeaponCountLimit(INVENTORY_LIMITS.weapons);
        proto.setReliquaryCountLimit(INVENTORY_LIMITS.relics);
        proto.setMaterialCountLimit(INVENTORY_LIMITS.materials);
        proto.setFurnitureCountLimit(INVENTORY_LIMITS.furniture);
    }
}
