package icu.zawarudo.fishloaf.handler;

import cn.zhouyafeng.itchat4j.api.MessageTools;
import cn.zhouyafeng.itchat4j.beans.BaseMsg;
import cn.zhouyafeng.itchat4j.core.Core;
import cn.zhouyafeng.itchat4j.face.IMsgHandlerFace;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import icu.zawarudo.fishloaf.commons.TraceContextUtil;
import icu.zawarudo.fishloaf.object.ActionResult;
import icu.zawarudo.fishloaf.object.ContactUserInfo;
import icu.zawarudo.fishloaf.server.PushNotificationHandler;
import icu.zawarudo.fishloaf.server.ServerHandler;
import icu.zawarudo.fishloaf.server.ServerNetty;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class MessageHandler implements IMsgHandlerFace, TCPDataHandler {

    //缓冲区的长度
    private static final int BUFSIZE = 1024;
    //select方法等待信道准备好的最长时间
    private static final int TIMEOUT = 3000;

    private static Logger LOG = LoggerFactory.getLogger(MessageHandler.class);

    private PushNotificationHandler pushNotificationHandler = new PushNotificationHandler();

    private Core core = Core.getInstance();

    public MessageHandler(int port0, int port1) throws InterruptedException {
        startMessageProcess(port0);
        startPushNotificationProcess(port1);
    }

    private void startMessageProcess(int port) {
        ServerNetty nettyServer = new ServerNetty(port);
        final MessageHandler that = this;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    nettyServer.action(new ServerHandler(that));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void startPushNotificationProcess(int port) {
        ServerNetty nettyServer = new ServerNetty(port);
        final MessageHandler that = this;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    nettyServer.action(pushNotificationHandler);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    @Override
    public String textMsgHandle(BaseMsg baseMsg) {
        LOG.info(JSON.toJSONString(baseMsg));
        Map<String, Object> messageMap = new HashMap<>();
        if (core.getUserName().equals(baseMsg.getFromUserName())) {
            //message from yourself, echo
            messageMap.put("text", baseMsg.getText());
            messageMap.put("userId", baseMsg.getToUserName());
            messageMap.put("displayName", core.getNickName());
            messageMap.put("echo", true);
            sendMessage(JSON.toJSONString(messageMap));
            return null;
        }
        if (!baseMsg.isGroupMsg()) {
            messageMap.put("text", baseMsg.getText());
            messageMap.put("userId", baseMsg.getFromUserName());
            JSONObject job = core.getUserInfoMap().get(baseMsg.getFromUserName());
            String nickName = job.getString("NickName");
            String remarkName = job.getString("RemarkName");
            messageMap.put("displayName", StringUtils.isBlank(remarkName) ? nickName : remarkName);
            messageMap.put("isGroupMsg", false);
            sendMessage(JSON.toJSONString(messageMap));
        } else {
            Optional<JSONObject> r = core.getGroupList().stream().filter(m -> m.getString("UserName").equals(baseMsg.getFromUserName())).findFirst();
            if (!r.isPresent()) {
                return null;
            }
            JSONArray jarray = core.getGroupMemeberMap().get(baseMsg.getFromUserName());
            if (jarray == null) {
                return null;
            }
            baseMsg.getSubFromUserName();
            JSONObject job = r.get();
            String groupName = job.getString("NickName");

            JSONObject subJob = null;
            for (int i = 0; i < jarray.size(); i++) {
                subJob = jarray.getJSONObject(i);
                if (StringUtils.equals(baseMsg.getSubFromUserName(), subJob.getString("UserName"))) {
                    break;
                }
            }
            messageMap.put("text", baseMsg.getText());
            messageMap.put("userId", baseMsg.getFromUserName());
            messageMap.put("groupName", groupName);
            if (subJob != null) {
                String nickName = subJob.getString("NickName");
                String displayName = subJob.getString("DisplayName");
                messageMap.put("displayName", StringUtils.isNotBlank(displayName) ? displayName : nickName);
            }
            messageMap.put("isGroupMsg", true);
            sendMessage(JSON.toJSONString(messageMap));
        }
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

    private ActionResult<List<ContactUserInfo>> getUsersAndGroups() {
        List<ContactUserInfo> userInfos = core.getContactList().stream().map(a -> {
            String userId = a.getString("UserName");
            String nickName = a.getString("NickName");
            String remarkName = a.getString("RemarkName");
            ContactUserInfo info = new ContactUserInfo();
            info.setDisplayName(StringUtils.isNotBlank(remarkName) ? remarkName : nickName);
            info.setUniqueId(userId);
            return info;
        }).collect(Collectors.toList());

        List<ContactUserInfo> groupInfos = core.getGroupList().stream().map(a -> {
            String userId = a.getString("UserName");
            String nickName = a.getString("NickName");
            ContactUserInfo info = new ContactUserInfo();
            info.setDisplayName(nickName);
            info.setUniqueId(userId);
            return info;
        }).collect(Collectors.toList());
        userInfos.addAll(groupInfos);
        return ActionResult.createSuccess(userInfos);
    }

    @Override
    public String onMessage(String message) {
        System.err.println("server 接收到客户端的请求： " + message);
        BaseRequest request = JSON.parseObject(message, BaseRequest.class);
        TraceContextUtil.setTraceId(request.getTraceId());
        try {
            ActionResult<?> result;
            switch (request.getMethod()) {
                case "listUserAndGroup":
                    result = getUsersAndGroups();
                    break;
                case "sendChatMessage":
                    MessageSendRequest sendRequest = JSON.parseObject(message, MessageSendRequest.class);
                    MessageTools.sendMsgById(sendRequest.getMessage(), sendRequest.getTargetId());
                    result = ActionResult.createSuccess(true);
                    break;
                default:
                    result = ActionResult.createError("no method match");
            }
            result.setMethod(request.getMethod());
            return JSONObject.toJSONString(result);
        } finally {
            TraceContextUtil.clear();
        }
    }

    @Override
    public void sendMessage(String message) {
        LOG.info("message send to client=" + message);
        pushNotificationHandler.writeToClient(message);
    }

//    // 开启netty服务线程
//    public static void main(String[] args) throws InterruptedException {
//        MessageHandler handler = new MessageHandler(9999);
//        Thread.sleep(1000 * 20);
//        System.out.println("发送到客户端的消息");
//        handler.sendMessage("发送到客户端的消息");
//    }

}
