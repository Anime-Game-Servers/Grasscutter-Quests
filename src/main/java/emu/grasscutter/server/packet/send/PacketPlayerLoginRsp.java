package emu.grasscutter.server.packet.send;

import emu.grasscutter.Grasscutter;
import emu.grasscutter.Grasscutter.ServerRunMode;
import emu.grasscutter.net.packet.BaseTypedPacket;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.http.dispatch.RegionHandler;
import emu.grasscutter.utils.Crypto;
import lombok.val;
import org.anime_game_servers.multi_proto.gi.messages.general.server.RegionInfo;
import org.anime_game_servers.multi_proto.gi.messages.login.QueryCurrRegionHttpRsp;
import org.anime_game_servers.multi_proto.gi.messages.player.PlayerLoginRsp;

import static emu.grasscutter.config.Configuration.*;

import java.util.Objects;

public class PacketPlayerLoginRsp extends BaseTypedPacket<PlayerLoginRsp> {

    private static QueryCurrRegionHttpRsp regionCache;

    public PacketPlayerLoginRsp(GameSession session) {
        super(new PlayerLoginRsp());

        this.setUseDispatchKey(true);

        RegionInfo info;

        if (SERVER.runMode == ServerRunMode.GAME_ONLY) {
            if (regionCache == null) {
                try {
                    // todo: we might want to push custom config to client
                    RegionInfo serverRegion = new RegionInfo();
                    serverRegion.setGateserverIp(lr(GAME_INFO.accessAddress, GAME_INFO.bindAddress));
                    serverRegion.setGateserverPort(lr(GAME_INFO.accessPort, GAME_INFO.bindPort));
                    serverRegion.setSecretKey(Crypto.DISPATCH_SEED);

                    val regionCache = new QueryCurrRegionHttpRsp();
                    regionCache.setRegionInfo(serverRegion);
                    PacketPlayerLoginRsp.regionCache = regionCache;
                } catch (Exception e) {
                    Grasscutter.getLogger().error("Error while initializing region cache!", e);
                }
            }

            info = regionCache.getRegionInfo();
        } else {
            info = Objects.requireNonNull(RegionHandler.getCurrentRegion()).getRegionInfo();
        }

        proto.setUseAbilityHash(true); // true
        proto.setAbilityHashCode(1844674); // 1844674
        proto.setGameBiz("hk4e_global");
        proto.setClientDataVersion(info.getClientDataVersion());
        proto.setClientSilenceDataVersion(info.getClientSilenceDataVersion());
        proto.setClientMd5(info.getClientDataMd5());
        proto.setClientSilenceMd5(info.getClientSilenceDataMd5());
        proto.setResVersionConfig(info.getResVersionConfig());
        proto.setClientVersionSuffix(info.getClientVersionSuffix());
        proto.setClientSilenceVersionSuffix(info.getClientSilenceVersionSuffix());
        proto.setScOpen(false);
                //.setScInfo(ByteString.copyFrom(new byte[] {}))
        proto.setRegisterCps("mihoyo");
        proto.setCountryCode("US");
    }
}
