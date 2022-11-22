package icu.zawarudo.fishloaf.handler;

public interface TCPDataHandler {
    String onMessage(String message);
    void sendMessage(String message);
}
