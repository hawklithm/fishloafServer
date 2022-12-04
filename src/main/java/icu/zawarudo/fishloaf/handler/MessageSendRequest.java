package icu.zawarudo.fishloaf.handler;

public class MessageSendRequest extends BaseRequest {
    private String message;
    private String targetId;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }
}
