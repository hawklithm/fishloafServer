package icu.zawarudo.fishloaf.server;

import icu.zawarudo.fishloaf.commons.ProtocolUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

@ChannelHandler.Sharable
public class PushNotificationHandler extends ChannelInboundHandlerAdapter {


    // 用于记录和管理所有客户端的channel
    public static ChannelGroup users = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    public PushNotificationHandler() {
    }

    // 不处理接收到的数据
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    }

    public void writeToClient(String message) {
        byte[] bytes = ProtocolUtils.encode(message);
        for (Channel c : users) {
            ByteBuf byteBuf = Unpooled.copiedBuffer(bytes);
            c.writeAndFlush(byteBuf);
        }
    }


    // 数据读取完毕的处理
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        System.err.println("服务端读取数据完毕");
    }

    // 出现异常的处理
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.err.println("server 读取数据出现异常");
        cause.printStackTrace();
        // 发生异常之后关键channel。随后从ChannelGroup 中移除
        ctx.channel().close();
        users.remove(ctx.channel());
        ctx.close();
    }

    /**
     * 当客户连接服务端之后（打开链接） 获取客户端的channel，并且放到ChannelGroup中去进行管理
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        users.add(ctx.channel());
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {

        String channelId = ctx.channel().id().asLongText();
        System.out.println("客户端被移除，channelId为：" + channelId);

        // 当触发handlerRemoved，ChannelGroup会自动移除对应的客户端channel
        users.remove(ctx.channel());
    }


}
