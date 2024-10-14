package emu.grasscutter.server.packet.recv;

import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import org.anime_game_servers.multi_proto.gi.messages.item.upgrade.ReliquaryUpgradeReq;

public class HandlerReliquaryUpgradeReq extends TypedPacketHandler<ReliquaryUpgradeReq> {
    @Override
    public void handle(GameSession session, byte[] header, ReliquaryUpgradeReq req) throws Exception {
        session.getServer().getInventorySystem().upgradeRelic(session.getPlayer(), req.getTargetReliquaryGuid(), req.getFoodReliquaryGuidList(), req.getItemParamList());
    }
}
