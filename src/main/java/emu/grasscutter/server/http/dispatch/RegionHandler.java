package emu.grasscutter.server.http.dispatch;

import emu.grasscutter.Grasscutter;
import emu.grasscutter.Grasscutter.ServerRunMode;
import emu.grasscutter.server.event.dispatch.QueryAllRegionsEvent;
import emu.grasscutter.server.event.dispatch.QueryCurrentRegionEvent;
import emu.grasscutter.server.http.Router;
import emu.grasscutter.server.http.objects.QueryCurRegionRspJson;
import emu.grasscutter.utils.Crypto;
import emu.grasscutter.utils.Utils;
import io.javalin.Javalin;
import io.javalin.http.Context;
import lombok.val;
import org.anime_game_servers.multi_proto.gi.messages.general.server.RegionInfo;
import org.anime_game_servers.multi_proto.gi.messages.login.QueryCurrRegionHttpRsp;
import org.anime_game_servers.core.base.Game;
import org.anime_game_servers.core.base.Version;
import org.anime_game_servers.multi_proto.gi.messages.login.QueryRegionListHttpRsp;
import org.anime_game_servers.multi_proto.gi.messages.login.RegionSimpleInfo;

import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.security.Signature;
import java.util.regex.Pattern;

import static emu.grasscutter.config.Configuration.*;

/**
 * Handles requests related to region queries.
 */
public final class RegionHandler implements Router {
    private static final Map<String, RegionData> regions = new ConcurrentHashMap<>();
    private static final Map<RegionType, String> regionListResponses = new EnumMap<>(RegionType.class);

    public RegionHandler() {
        try { // Read & initialize region data.
            this.initialize();
        } catch (Exception exception) {
            Grasscutter.getLogger().error("Failed to initialize region data.", exception);
        }
    }

    enum RegionType {
        OS,
        CN
    }

    /**
     * Configures region data according to configuration.
     */
    private void initialize() {
        String dispatchDomain = "http" + (HTTP_ENCRYPTION.useInRouting ? "s" : "") + "://"
                + lr(HTTP_INFO.accessAddress, HTTP_INFO.bindAddress) + ":"
                + lr(HTTP_INFO.accessPort, HTTP_INFO.bindPort);

        // Create regions.
        List<RegionSimpleInfo> servers = new ArrayList<>();
        List<String> usedNames = new ArrayList<>(); // List to check for potential naming conflicts.

        var configuredRegions = new ArrayList<>(List.of(DISPATCH_INFO.regions));
        if (SERVER.runMode != ServerRunMode.HYBRID && configuredRegions.size() == 0) {
            Grasscutter.getLogger().error("[Dispatch] There are no game servers available. Exiting due to unplayable state.");
            System.exit(1);
        } else if (configuredRegions.size() == 0)
            configuredRegions.add(new Region("os_usa", DISPATCH_INFO.defaultName,
                    lr(GAME_INFO.accessAddress, GAME_INFO.bindAddress),
                    lr(GAME_INFO.accessPort, GAME_INFO.bindPort)));

        configuredRegions.forEach(region -> {
            if (usedNames.contains(region.Name)) {
                Grasscutter.getLogger().error("Region name already in use.");
                return;
            }

            // Create a region identifier.
            val identifier = new RegionSimpleInfo();
            identifier.setName(region.Name);
            identifier.setTitle(region.Title);
            identifier.setType("DEV_PUBLIC");
            identifier.setDispatchUrl(dispatchDomain + "/query_cur_region/" + region.Name);
            usedNames.add(region.Name); servers.add(identifier);

            // Create a region info object.
            var regionInfo = new RegionInfo();
            regionInfo.setGateserverIp(region.Ip);
            regionInfo.setGateserverPort(region.Port);
            regionInfo.setSecretKey(Crypto.DISPATCH_SEED);

            // Create an updated region query.
            val updatedQuery = new QueryCurrRegionHttpRsp();
            updatedQuery.setRegionInfo(regionInfo);
            regions.put(region.Name, new RegionData(updatedQuery));
        });

        // Create a config object.
        byte[] customConfig = "{\"sdkenv\":\"2\",\"checkdevice\":\"false\",\"loadPatch\":\"false\",\"showexception\":\"false\",\"regionConfig\":\"pm|fk|add\",\"downloadMode\":\"0\",\"debugmenu\":\"true\",\"debuglog\":\"true\"}".getBytes();
        Crypto.xor(customConfig, Crypto.DISPATCH_KEY); // XOR the config with the key.

        byte[] customConfigCn = "{\"sdkenv\":\"2\",\"checkdevice\":\"false\",\"loadPatch\":\"false\",\"showexception\":\"false\",\"regionConfig\":\"pm|fk|add\",\"downloadMode\":\"0\",\"debugmenu\":\"true\",\"debuglog\":\"true\"}".getBytes();
        Crypto.xor(customConfigCn, Crypto.DISPATCH_KEY); // XOR the config with the key.

        // Create an updated region list.
        val updatedRegionList = new QueryRegionListHttpRsp();
        updatedRegionList.setRegionList(servers);
        updatedRegionList.setClientSecretKey(Crypto.DISPATCH_SEED);
        updatedRegionList.setEnableLoginPc(true);

        // Set the region list response.
        setRegionListResponses(RegionType.OS, customConfig, updatedRegionList);
        setRegionListResponses(RegionType.CN, customConfigCn, updatedRegionList);
    }

    /**
     * Pre generates the region list responses for each version
     * @param regionType The region type
     * @param customConfig The  custom configuration, XOR'd with the dispatch key
     * @param baseRegionList The filled out region list response model
     */
    private void setRegionListResponses(RegionType regionType, byte[] customConfig, QueryRegionListHttpRsp baseRegionList) {
        baseRegionList.setClientCustomConfigEncrypted(customConfig);
        Arrays.stream(Version.values()).forEach(version -> {
            val encoded = baseRegionList.encodeToByteArray(version);
            if (encoded != null) {
                // todo actually save region specific responses, and not just the newest success
                regionListResponses.put(regionType, Utils.base64Encode(encoded));
            }
        });
    }

    @Override public void applyRoutes(Javalin javalin) {
        javalin.get("/query_region_list", RegionHandler::queryRegionList);
        javalin.get("/query_cur_region/{region}", RegionHandler::queryCurrentRegion);
    }

    private static final String VERSION_KEY = "version";
    private static final String PLATFORM_KEY = "platform";

    /**
     * @route /query_region_list
     */
    private static void queryRegionList(Context ctx) {

        // use OS as default fallback
        RegionType targetRegion = RegionType.OS;

        // Respond with event result.
        if (ctx.queryParamMap().containsKey(VERSION_KEY) && ctx.queryParamMap().containsKey(PLATFORM_KEY)) {
            String versionName = ctx.queryParam(VERSION_KEY);
            String versionCode = versionName!=null ?  versionName.replaceAll("[/.0-9]*", "") : "";
            String platformName = ctx.queryParam(PLATFORM_KEY);

            // Determine the region list to use based on the version and platform.
            if ("CNRELiOS".equals(versionCode) || "CNRELWin".equals(versionCode)
                || "CNRELAndroid".equals(versionCode)) {
                targetRegion = RegionType.CN;
            } else if ("OSRELiOS".equals(versionCode) || "OSRELWin".equals(versionCode)
                || "OSRELAndroid".equals(versionCode)) {
                // Use the OS region list.
                targetRegion = RegionType.OS;
            }
        }

        // Invoke event.
        QueryAllRegionsEvent event = new QueryAllRegionsEvent(regionListResponses.get(targetRegion));
        event.call();
        // Respond with event result.
        ctx.result(event.getRegionList());

        // Log to console.
        Grasscutter.getLogger().info("[Dispatch] Client {} request: query_region_list for region {}", ctx.ip(), targetRegion.name());
    }

    /**
     * @route /query_cur_region/{region}
     */
    private static void queryCurrentRegion(Context ctx) {
        // Get region to query.
        String regionName = ctx.pathParam("region");
        String versionName = ctx.queryParam("version");
        var region = regions.get(regionName);

        String[] versionCode = versionName.replaceAll(Pattern.compile("[a-zA-Z]").pattern(), "").split("\\.");
        int versionMajor = Integer.parseInt(versionCode[0]);
        int versionMinor = Integer.parseInt(versionCode[1]);
        int versionFix   = Integer.parseInt(versionCode[2]);
        val versionId = Version.idFromVersion(Game.GI, versionMajor, versionMinor, versionFix);
        val version = Version.fromId(Game.GI, versionId);

        if (version == null){
            Grasscutter.getLogger().error("Client {} request: query_cur_region/{} with invalid version {}", ctx.ip(), regionName, versionName);
            return;
        }

        // Get region data.
        String regionData = "CAESGE5vdCBGb3VuZCB2ZXJzaW9uIGNvbmZpZw==";
        if (ctx.queryParamMap().values().size() > 0) {
            if (region != null)
                regionData = region.getBase64(version);
        }


        if (versionId > Version.GI_2_7_0.getId()) {
            try {
                QueryCurrentRegionEvent event = new QueryCurrentRegionEvent(regionData); event.call();

                Grasscutter.getLogger().warn("Params: {}", ctx.queryParamMap());
                if (ctx.queryParam("dispatchSeed") == null) {
                    // More love for UA Patch players
                    var rsp = new QueryCurRegionRspJson();

                    rsp.content = event.getRegionInfo();
                    rsp.sign = "TW9yZSBsb3ZlIGZvciBVQSBQYXRjaCBwbGF5ZXJz";

                    ctx.json(rsp);
                    return;
                }

                String key_id = ctx.queryParam("key_id");

                if (key_id == null)
                    throw new Exception("Key ID was not set");

                Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
                Grasscutter.getLogger().warn("Key id: {}", key_id);
                cipher.init(Cipher.ENCRYPT_MODE, Crypto.EncryptionKeys.get(Integer.valueOf(key_id)));
                var regionInfo = Utils.base64Decode(event.getRegionInfo());

                //Encrypt regionInfo in chunks
                ByteArrayOutputStream encryptedRegionInfoStream = new ByteArrayOutputStream();

                //Thank you so much GH Copilot
                int chunkSize = 256 - 11;
                int regionInfoLength = regionInfo.length;
                int numChunks = (int) Math.ceil(regionInfoLength / (double) chunkSize);

                for (int i = 0; i < numChunks; i++) {
                    byte[] chunk = Arrays.copyOfRange(regionInfo, i * chunkSize, Math.min((i + 1) * chunkSize, regionInfoLength));
                    byte[] encryptedChunk = cipher.doFinal(chunk);
                    encryptedRegionInfoStream.write(encryptedChunk);
                }

                Signature privateSignature = Signature.getInstance("SHA256withRSA");
                privateSignature.initSign(Crypto.CUR_SIGNING_KEY);
                privateSignature.update(regionInfo);

                var rsp = new QueryCurRegionRspJson();

                rsp.content = Utils.base64Encode(encryptedRegionInfoStream.toByteArray());
                rsp.sign = Utils.base64Encode(privateSignature.sign());

                ctx.json(rsp);
            }
            catch (Exception e) {
                Grasscutter.getLogger().error("An error occurred while handling query_cur_region.", e);
            }
        }
        else {
            // Invoke event.
            QueryCurrentRegionEvent event = new QueryCurrentRegionEvent(regionData); event.call();
            // Respond with event result.
            ctx.result(event.getRegionInfo());
        }
        // Log to console.
        Grasscutter.getLogger().info(String.format("Client %s request: query_cur_region/%s", ctx.ip(), regionName));
    }

    /**
     * Region data container.
     */
    public static class RegionData {
        private final QueryCurrRegionHttpRsp regionQuery;

        public RegionData(QueryCurrRegionHttpRsp prq) {
            this.regionQuery = prq;
        }

        public QueryCurrRegionHttpRsp getRegionQuery() {
            return this.regionQuery;
        }

        public String getBase64(Version version) {
            return Utils.base64Encode(regionQuery.encodeToByteArray(version));
        }
    }

    /**
     * Gets the current region query.
     * @return A {@link QueryCurrRegionHttpRsp} object.
     */
    public static QueryCurrRegionHttpRsp getCurrentRegion() {
        return SERVER.runMode == ServerRunMode.HYBRID ? regions.get("os_usa").getRegionQuery() : null;
    }
}
