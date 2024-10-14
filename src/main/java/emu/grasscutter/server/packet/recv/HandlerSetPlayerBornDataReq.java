package emu.grasscutter.server.packet.recv;

import emu.grasscutter.Grasscutter;
import emu.grasscutter.command.commands.SendMailCommand.MailBuilder;
import emu.grasscutter.data.GameData;
import emu.grasscutter.game.avatar.Avatar;
import emu.grasscutter.game.mail.Mail;
import emu.grasscutter.game.player.Player;
import emu.grasscutter.net.packet.BaseTypedPacket;
import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.game.GameSession;
import org.anime_game_servers.multi_proto.gi.messages.player.SetPlayerBornDataReq;
import org.anime_game_servers.multi_proto.gi.messages.player.SetPlayerBornDataRsp;

import java.util.Arrays;

import static emu.grasscutter.config.Configuration.GAME_INFO;

public class HandlerSetPlayerBornDataReq extends TypedPacketHandler<SetPlayerBornDataReq> {

    @Override
    public void handle(GameSession session, byte[] header, SetPlayerBornDataReq req) throws Exception {

        // Sanity checks
        int avatarId = req.getAvatarId();

        // Make sure resources folder is set
        if (!GameData.getAvatarDataMap().containsKey(avatarId)) {
            Grasscutter.getLogger().error("No avatar data found! Please check your ExcelBinOutput folder.");
            session.close();
            return;
        }

        // Get player object
        Player player = session.getPlayer();
        player.setNickname(req.getNickName());

        // Create avatar
        if (player.getAvatars().getAvatarCount() == 0) {
            Avatar mainCharacter = new Avatar(avatarId);
            int startingSkillDepot = mainCharacter.getData().getSkillDepotId();
            mainCharacter.setSkillDepotData(GameData.getAvatarSkillDepotDataMap().get(startingSkillDepot), false);
            // Manually handle adding to team
            player.addAvatar(mainCharacter, false);
            player.setMainCharacterId(avatarId);
            player.setHeadImage(avatarId);
            player.getTeamManager().getCurrentSinglePlayerTeamInfo().getAvatars().add(mainCharacter.getAvatarId());
            player.save(); // TODO save player team in different object
        } else {
            return;
        }

        // Login done
        session.getPlayer().onLogin();

        // Created done
        session.getPlayer().onPlayerBorn();

        // Born resp packet
        session.send(new BaseTypedPacket<>(new SetPlayerBornDataRsp()) {});

        // Default mail
        var welcomeMail = GAME_INFO.joinOptions.welcomeMail;
        MailBuilder mailBuilder = new MailBuilder(player.getUid(), new Mail());
        mailBuilder.mail.mailContent.title = welcomeMail.title;
        mailBuilder.mail.mailContent.sender = welcomeMail.sender;
        // Please credit Grasscutter if changing something here. We don't condone commercial use of the project.
        mailBuilder.mail.mailContent.content = welcomeMail.content + "\n<type=\"browser\" text=\"GitHub\" href=\"https://github.com/Grasscutters/Grasscutter\"/>";
        mailBuilder.mail.itemList.addAll(Arrays.asList(welcomeMail.items));
        mailBuilder.mail.importance = 1;
        player.sendMail(mailBuilder.mail);
    }
}
