package icu.zawarudo.fishloaf.server;

import icu.zawarudo.fishloaf.commons.ProtocolUtils;
import icu.zawarudo.fishloaf.handler.TCPDataHandler;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 处理某个客户端的请求
 *
 * @author pmx
 */
@ChannelHandler.Sharable
public class ServerHandler extends ChannelInboundHandlerAdapter {

    private static Logger LOG = LoggerFactory.getLogger(ServerHandler.class);

    private TCPDataHandler handler;

    // 用于记录和管理所有客户端的channel
//    public static ChannelGroup users = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    public ServerHandler(TCPDataHandler handler) {
        this.handler = handler;
    }

    // 读取数据
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // 普通的处理 及过滤器不多
        simpleRead(ctx, msg);
        // 有分隔符处理信息
//      Delimiterread(ctx, msg);
    }

//    public void writeToClient(String message) {
//        byte[] bytes = ProtocolUtils.encode(message);
//        for (Channel c : users) {
//            ByteBuf byteBuf = Unpooled.copiedBuffer(bytes);
//            c.writeAndFlush(byteBuf);
//        }
//
//    }


    /**
     * 最简单的处理
     *
     * @param ctx
     * @param msg
     */
    public void simpleRead(ChannelHandlerContext ctx, Object msg) {
        try {
            String reqStr = (String) msg;
            String respStr = handler.onMessage(reqStr);
            byte[] data = ProtocolUtils.encode(respStr);
            // 返回给客户端响应                                                                                                                                                       和客户端链接中断即短连接，当信息返回给客户端后中断
            ctx.writeAndFlush(Unpooled.copiedBuffer(data));//.addListener(ChannelFutureListener.CLOSE);
            LOG.info("返回给客户端: " + respStr);
        } catch (Throwable e) {
            LOG.error("message delivery exception!", e);
        }
    }

//    /**
//     * 有分隔符的请求信息分包情况处理，包含了转码
//     *
//     * @param ctx
//     * @param msg
//     */
//    private void Delimiterread(ChannelHandlerContext ctx, Object msg) {
//        // 如果把msg直接转成字符串，必须在服务中心添加 socketChannel.pipeline().addLast(new StringDecoder());
//        String reqStr = (String) msg;
//        System.err.println("server 接收到请求信息是：" + reqStr);
//        String respStr = new StringBuilder("来自服务器的响应").append(reqStr).append("$_").toString();
//
//        // 返回给客户端响应                                                                                                                                                       和客户端链接中断即短连接，当信息返回给客户端后中断
//        ctx.writeAndFlush(Unpooled.copiedBuffer(respStr.getBytes())).addListener(ChannelFutureListener.CLOSE);
//    }


    // 数据读取完毕的处理
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        System.err.println("服务端读取数据完毕");
    }

//    // 出现异常的处理
//    @Override
//    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
//        System.err.println("server 读取数据出现异常");
//        cause.printStackTrace();
//        // 发生异常之后关键channel。随后从ChannelGroup 中移除
//        ctx.channel().close();
//        users.remove(ctx.channel());
//        ctx.close();
//    }

//    /**
//     * 当客户连接服务端之后（打开链接） 获取客户端的channel，并且放到ChannelGroup中去进行管理
//     */
//    @Override
//    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
//        users.add(ctx.channel());
//    }

//    @Override
//    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
//
//        String channelId = ctx.channel().id().asLongText();
//        System.out.println("客户端被移除，channelId为：" + channelId);
//
//        // 当触发handlerRemoved，ChannelGroup会自动移除对应的客户端channel
//        users.remove(ctx.channel());
//    }


//    /**
//     * 将请求信息直接转成对象
//     *
//     * @param ctx
//     * @param msg
//     */
//    private void handlerObject(ChannelHandlerContext ctx, Object msg) {
//        // 需要序列化 直接把msg转成对象信息，一般不会用，可以用json字符串在不同语言中传递信息
//        Student student = (Student) msg;
//        System.err.println("server 获取信息：" + student.getId() + student.getName());
//        student.setName("李四");
//        ctx.write(student);
//    }


}