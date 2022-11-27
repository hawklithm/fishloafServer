package icu.zawarudo.fishloaf.object;

import icu.zawarudo.fishloaf.commons.TraceContextUtil;

public class ActionResult<T> {
    private T data;
    private boolean success;
    private String message;

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
}
