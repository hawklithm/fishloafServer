package icu.zawarudo.fishloaf.commons;

import org.apache.commons.codec.Charsets;

public class ProtocolUtils {
    private static final byte[] magic = new byte[]{(byte) 0xf1, (byte) 0x60, (byte) 0x6f};

    public static byte[] encode(String message) {
        byte[] msgByte = message.getBytes(Charsets.UTF_8);
        return encode(msgByte);
    }

    public static boolean checkMessage(byte[] message) {
        for (int i = 0; i < magic.length; i++) {
            if (message[i] != magic[i]) {
                return false;
            }
        }
        int data = 0;
        for (int i = magic.length; i < magic.length + 4; i++) {
            data <<= 8;
            data += message[i] & 0xff;
        }
        return message.length == magic.length + 4 + data;
    }

    public static byte[] decode(byte[] message) {
        int dataLength = 0;
        for (int i = magic.length; i < magic.length + 4; i++) {
            dataLength <<= 8;
            dataLength += message[i] & 0xff;
        }
        byte[] retData = new byte[dataLength];
        System.arraycopy(message, magic.length + 4, retData, 0, dataLength);
        return retData;
    }


    public static byte[] encode(byte[] msgByte) {
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
