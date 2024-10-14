package emu.grasscutter.server.packet.recv;

import emu.grasscutter.Grasscutter.ServerDebugMode;
import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import lombok.val;
import org.anime_game_servers.multi_proto.gi.messages.other.UnionCmd;
import org.anime_game_servers.multi_proto.gi.messages.other.UnionCmdNotify;

import static emu.grasscutter.config.Configuration.GAME_INFO;
import static emu.grasscutter.config.Configuration.SERVER;


public class HandlerUnionCmdNotify extends TypedPacketHandler<UnionCmdNotify> {
    @Override
    public void handle(GameSession session, byte[] header, UnionCmdNotify req) throws Exception {

        for (UnionCmd cmd : req.getCmdList()) {
            int cmdOpcode = cmd.getMessageId();
            val cmdName = session.getPackageIdProvider().getPacketName(cmdOpcode);
            byte[] cmdPayload = cmd.getBody();
            if (GAME_INFO.logPackets == ServerDebugMode.WHITELIST && SERVER.debugWhitelist.contains(cmdName)) {
                session.logPacket("RECV in Union", cmdOpcode, cmdPayload);
            } else if (GAME_INFO.logPackets ==  ServerDebugMode.BLACKLIST && !SERVER.debugBlacklist.contains(cmdName)) {
                session.logPacket("RECV in Union", cmdOpcode, cmdPayload);
            }
            //debugLevel ALL ignores UnionCmdNotify, so we will also ignore the contained opcodes
            session.getServer().getPacketHandler().handle(session, cmd.getMessageId(), EMPTY_BYTE_ARRAY, cmd.getBody());
        }

        // Update
        session.getPlayer().getCombatInvokeHandler().update(session.getPlayer());
        session.getPlayer().getAbilityInvokeHandler().update(session.getPlayer());

        // Handle attack results last
        while (!session.getPlayer().getAttackResults().isEmpty()) {
            session.getPlayer().getScene().handleAttack(session.getPlayer().getAttackResults().poll());
        }
    }
}
