package icu.zawarudo.fishloaf.object;

import icu.zawarudo.fishloaf.commons.TraceContextUtil;

public class ActionResult<T> {
    private T data;
    private boolean success;
    private String message;
    private String method;

    private String traceId;


    private ActionResult(T data, boolean success, String message) {
        this.data = data;
        this.success = success;
        this.message = message;
        this.traceId = TraceContextUtil.getTraceId();
    }

    public static <T> ActionResult<T> createSuccess(T data) {
        return new ActionResult<T>(data, true, null);
    }

    public static <T> ActionResult<T> createError(String errorMsg) {
        return new ActionResult<T>(null, false, errorMsg);
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }
}
