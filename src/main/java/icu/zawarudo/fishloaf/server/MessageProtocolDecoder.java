package icu.zawarudo.fishloaf.server;

import icu.zawarudo.fishloaf.commons.ProtocolUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.apache.commons.codec.Charsets;

import java.util.List;

public class MessageProtocolDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        if (byteBuf.readableBytes() > 0) {
            // 待处理的消息包
            byte[] bytesReady = new byte[byteBuf.readableBytes()];
            byteBuf.readBytes(bytesReady);
            //进行具体的解码处理
            if (!ProtocolUtils.checkMessage(bytesReady)) {
                return;
            }
            byte[] dataBytes = ProtocolUtils.decode(bytesReady);
            String message = new String(dataBytes, Charsets.UTF_8);

            list.add(message);
        }

    }
}
