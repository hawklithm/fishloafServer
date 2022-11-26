package icu.zawarudo.fishloaf;

import cn.zhouyafeng.itchat4j.Wechat;
import cn.zhouyafeng.itchat4j.face.IMsgHandlerFace;
import icu.zawarudo.fishloaf.commons.Constants;
import icu.zawarudo.fishloaf.handler.MessageHandler;

import java.io.File;

public class Boot {
    public static void start() throws InterruptedException {
        String qrPath = "login"; // 保存登陆二维码图片的路径，这里需要在本地新建目录
        File f = new File(qrPath);
        if (!f.exists()) {
            f.mkdirs();
        }
        f.deleteOnExit();
        IMsgHandlerFace msgHandler = new MessageHandler(Constants.serverSocketPort, Constants.pushNotificationPort);
        Wechat wechat = new Wechat(msgHandler, f.getAbsolutePath());
        wechat.start();
    }
}
