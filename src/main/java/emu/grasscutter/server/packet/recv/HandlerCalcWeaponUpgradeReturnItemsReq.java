package emu.grasscutter.server.packet.recv;

import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.PacketCalcWeaponUpgradeReturnItemsRsp;
import org.anime_game_servers.multi_proto.gi.messages.general.item.ItemParam;
import org.anime_game_servers.multi_proto.gi.messages.item.upgrade.CalcWeaponUpgradeReturnItemsReq;

import java.util.List;

public class HandlerCalcWeaponUpgradeReturnItemsReq extends TypedPacketHandler<CalcWeaponUpgradeReturnItemsReq> {
    @Override
    public void handle(GameSession session, byte[] header, CalcWeaponUpgradeReturnItemsReq req) throws Exception {
        List<ItemParam> returnOres = session.getServer().getInventorySystem().calcWeaponUpgradeReturnItems(
                session.getPlayer(),
                req.getTargetWeaponGuid(),
            req.getFoodWeaponGuidList(),
            req.getItemParamList()
        );
        if (returnOres != null) {
            session.send(new PacketCalcWeaponUpgradeReturnItemsRsp(req.getTargetWeaponGuid(), returnOres));
        } else {
            session.send(new PacketCalcWeaponUpgradeReturnItemsRsp());
        }
    }
}
