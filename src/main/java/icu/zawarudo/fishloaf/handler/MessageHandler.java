package icu.zawarudo.fishloaf.handler;

import cn.zhouyafeng.itchat4j.beans.BaseMsg;
import cn.zhouyafeng.itchat4j.face.IMsgHandlerFace;
import icu.zawarudo.fishloaf.server.ServerNetty;

public class MessageHandler implements IMsgHandlerFace, TCPDataHandler {

    //缓冲区的长度
    private static final int BUFSIZE = 1024;
    //select方法等待信道准备好的最长时间
    private static final int TIMEOUT = 3000;

    private int port;

    public MessageHandler(int port) throws InterruptedException {
        this.port = port;
        startServer(port);
    }

    private void startServer(int port) throws InterruptedException {
        new ServerNetty(port, this).action();
    }


    @Override
    public String textMsgHandle(BaseMsg baseMsg) {
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
}
