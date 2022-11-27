package icu.zawarudo.fishloaf.commons;

public class TraceContextUtil {
    private static ThreadLocal<String> traceContextThreadLocal = new ThreadLocal<String>();

    public static void setTraceId(String traceId) {
        traceContextThreadLocal.set(traceId);
    }

    public static String getTraceId() {
        return traceContextThreadLocal.get();
    }

    public static void clear() {
        traceContextThreadLocal.remove();
    }
}
