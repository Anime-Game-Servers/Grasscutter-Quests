package emu.grasscutter.net.packet;

import emu.grasscutter.server.game.GameSession;
import org.anime_game_servers.core.base.Version;
import org.anime_game_servers.multi_proto.core.interfaces.ProtoModel;

public abstract class BaseTypedPacket<Packet extends ProtoModel> extends BasePacket {

    protected Packet proto;
    protected BaseTypedPacket(Packet proto) {
        super();
        this.proto = proto;
    }
    protected BaseTypedPacket(Packet proto, int clientSequence) {
        super();
        this.proto = proto;
        this.buildHeader(clientSequence);
    }
    protected BaseTypedPacket(Packet proto, boolean shouldBuildHeader) {
        super(shouldBuildHeader);
        this.proto = proto;
    }

    @Override
    public byte[] getData(Version version) {
        return proto.encodeToByteArray(version);
    }

    @Override
    public int getOpcode(GameSession session) {
        return session.getPackageIdProvider().getPacketId(proto.getClass().getSimpleName());
    }

    @Override @Deprecated
    public void setData(byte[] data) {
        throw new UnsupportedOperationException("Not supported, why are you doing this.");
    }
}
