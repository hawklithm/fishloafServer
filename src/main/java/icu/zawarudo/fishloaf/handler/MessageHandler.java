package icu.zawarudo.fishloaf.handler;

import cn.zhouyafeng.itchat4j.beans.BaseMsg;
import cn.zhouyafeng.itchat4j.core.Core;
import cn.zhouyafeng.itchat4j.face.IMsgHandlerFace;
import com.alibaba.fastjson.JSON;
import icu.zawarudo.fishloaf.server.ServerNetty;

import java.util.HashMap;
import java.util.Map;

public class MessageHandler implements IMsgHandlerFace, TCPDataHandler {

    //缓冲区的长度
    private static final int BUFSIZE = 1024;
    //select方法等待信道准备好的最长时间
    private static final int TIMEOUT = 3000;

    private Core core = Core.getInstance();

    private int port;
    private ServerNetty nettyServer;

    public MessageHandler(int port) throws InterruptedException {
        this.port = port;
        startServer(port);
    }

    private void startServer(int port) {
        nettyServer = new ServerNetty(port, this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    nettyServer.action();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    @Override
    public String textMsgHandle(BaseMsg baseMsg) {
        System.out.println(JSON.toJSONString(baseMsg));
        Map<String, String> messageMap = new HashMap<>();
        messageMap.put("text", baseMsg.getText());
        messageMap.put("userId", baseMsg.getFromUserName());
        System.out.println(JSON.toJSONString(core.getContactList()));
        sendMessage(JSON.toJSONString(messageMap));
        return null;
    }

    @Override
    public String picMsgHandle(BaseMsg baseMsg) {
        return null;
    }

    @Override
    public String voiceMsgHandle(BaseMsg baseMsg) {
        return null;
    }

    @Override
    public String viedoMsgHandle(BaseMsg baseMsg) {
        return null;
    }

    @Override
    public String nameCardMsgHandle(BaseMsg baseMsg) {
        return null;
    }

    @Override
    public void sysMsgHandle(BaseMsg baseMsg) {

    }

    @Override
    public String verifyAddFriendMsgHandle(BaseMsg baseMsg) {
        return null;
    }

    @Override
    public String mediaMsgHandle(BaseMsg baseMsg) {
        return null;
    }

    @Override
    public String onMessage(String message) {
        return new StringBuilder("来自服务器的响应").append(message).append("$_").toString();
    }

    @Override
    public void sendMessage(String message) {
        nettyServer.writeToClient(message);
    }

    // 开启netty服务线程
    public static void main(String[] args) throws InterruptedException {
        MessageHandler handler = new MessageHandler(9999);
        Thread.sleep(1000 * 20);
        System.out.println("发送到客户端的消息");
        handler.sendMessage("发送到客户端的消息");
    }

}
