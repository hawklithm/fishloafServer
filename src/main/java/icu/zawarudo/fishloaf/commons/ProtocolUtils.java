package icu.zawarudo.fishloaf.commons;

import org.apache.commons.codec.Charsets;

public class ProtocolUtils {
    private static final byte[] magic = new byte[]{(byte) 0xf1, (byte) 0x60, (byte) 0x6f};

    public static byte[] encode(String message) {
        byte[] msgByte = message.getBytes(Charsets.UTF_8);
        int len = msgByte.length;
        byte[] data = new byte[3 + 4 + len];
        System.arraycopy(magic, 0, data, 0, 3);

        byte[] lenByte = new byte[4];
        lenByte[0] = (byte) ((len >> 24) & 0xff);
        lenByte[1] = (byte) ((len >> 16) & 0xff);
        lenByte[2] = (byte) ((len >> 8) & 0xff);
        lenByte[3] = (byte) (len & 0xff);
        System.arraycopy(lenByte, 0, data, 3, 4);

        System.arraycopy(msgByte, 0, data, 3 + 4, msgByte.length);

        return data;

    }
}
