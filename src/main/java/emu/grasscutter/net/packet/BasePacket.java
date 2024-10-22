package emu.grasscutter.net.packet;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.utils.Crypto;
import lombok.val;
import org.anime_game_servers.core.base.Version;
import org.anime_game_servers.multi_proto.gi.messages.packet_head.PacketHead;

public class BasePacket {
    private static final int const1 = 17767; // 0x4567
    private static final int const2 = -30293; // 0x89ab

    private int opcode;
    private boolean shouldBuildHeader = false;

    private PacketHead header;
    private byte[] data;

    // Encryption
    private boolean useDispatchKey;
    public boolean shouldEncrypt = true;

    protected BasePacket(){}

    public BasePacket(boolean shouldBuildHeader) {
        this.shouldBuildHeader = shouldBuildHeader;
    }


    public BasePacket(int opcode) {
        this.opcode = opcode;
    }
    public BasePacket(int opcode, int clientSequence) {
        this.opcode = opcode;
        this.buildHeader(clientSequence);
    }

    public BasePacket(int opcode, boolean buildHeader) {
        this.opcode = opcode;
        this.shouldBuildHeader = buildHeader;
    }

    public int getOpcode(GameSession session) {
        return opcode;
    }

    public boolean useDispatchKey() {
        return useDispatchKey;
    }

    public void setUseDispatchKey(boolean useDispatchKey) {
        this.useDispatchKey = useDispatchKey;
    }

    public PacketHead getHeader() {
        return header;
    }

    public void setHeader(PacketHead header) {
        this.header = header;
    }

    public boolean shouldBuildHeader() {
        return shouldBuildHeader;
    }

    public byte[] getData(Version version) {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public BasePacket buildHeader(int clientSequence) {
        if (this.getHeader() != null && clientSequence == 0) {
            return this;
        }
        val packetHead = new PacketHead();
        packetHead.setClientSequenceId(clientSequence);
        packetHead.setSentMs(System.currentTimeMillis());
        setHeader(packetHead);
        return this;
    }

    public byte[] build(GameSession session) {
        byte[] headerBytes;
        val header = getHeader();
        if (header == null) {
            headerBytes = new byte[0];
        } else {
            headerBytes = header.encodeToByteArray(session.getVersion());
        }

        var data = getData(session.getVersion());
        val opcode = getOpcode(session);

        if (data == null) {
            data = new byte[0];
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream(2 + 2 + 2 + 4 + headerBytes.length + data.length + 2);

        this.writeUint16(baos, const1);
        this.writeUint16(baos, opcode);
        this.writeUint16(baos, headerBytes.length);
        this.writeUint32(baos, data.length);
        this.writeBytes(baos, headerBytes);
        this.writeBytes(baos, data);
        this.writeUint16(baos, const2);

        byte[] packet = baos.toByteArray();

        if (this.shouldEncrypt) {
            Crypto.xor(packet, this.useDispatchKey() ? Crypto.DISPATCH_KEY : Crypto.ENCRYPT_KEY);
        }

        return packet;
    }

    public void writeUint16(ByteArrayOutputStream baos, int i) {
        // Unsigned short
        baos.write((byte) ((i >>> 8) & 0xFF));
        baos.write((byte) (i & 0xFF));
    }

    public void writeUint32(ByteArrayOutputStream baos, int i) {
        // Unsigned int (long)
        baos.write((byte) ((i >>> 24) & 0xFF));
        baos.write((byte) ((i >>> 16) & 0xFF));
        baos.write((byte) ((i >>> 8) & 0xFF));
        baos.write((byte) (i & 0xFF));
    }

    public void writeBytes(ByteArrayOutputStream baos, byte[] bytes) {
        try {
            baos.write(bytes);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
