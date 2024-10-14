
package emu.grasscutter.server.packet.recv;

import emu.grasscutter.game.player.Player;
import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.PacketDungeonPlayerDieRsp;
import org.anime_game_servers.multi_proto.gi.messages.dungeon.DungeonPlayerDieReq;
import org.anime_game_servers.multi_proto.gi.messages.general.Retcode;

public class HandlerDungeonPlayerDieReq extends TypedPacketHandler<DungeonPlayerDieReq> {

    @Override
    public void handle(GameSession session, byte[] header, DungeonPlayerDieReq req) throws Exception {
        Player player = session.getPlayer();

        boolean result = player.getScene().respawnPlayer(player);

        player.sendPacket(new PacketDungeonPlayerDieRsp(result ? Retcode.RET_SUCC : Retcode.RET_FAIL));
    }

}
