package icu.zawarudo.fishloaf.server;

import icu.zawarudo.fishloaf.commons.ProtocolUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class MessageProtocolEncoder extends MessageToByteEncoder<byte[]> {
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, byte[] bytes, ByteBuf byteBuf) throws Exception {
        byte[] data = ProtocolUtils.encode(bytes);
        byteBuf.writeBytes(data);
    }
}
