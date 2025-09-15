// package com.example.lz_java_test.websocket;

// import com.fasterxml.jackson.databind.ObjectMapper;
// import org.springframework.stereotype.Component;
// import org.springframework.web.socket.TextMessage;
// import org.springframework.web.socket.WebSocketSession;
// import org.springframework.web.socket.handler.TextWebSocketHandler;

// import java.io.IOException;
// import java.util.Map;
// import java.util.concurrent.ConcurrentHashMap;

// @Component
// public class ChatWebSocketHandler extends TextWebSocketHandler {

// // 存储所有在线用户 {sessionId: session}
// private static Map<String, WebSocketSession> sessions = new
// ConcurrentHashMap<>();

// private final ObjectMapper objectMapper = new ObjectMapper();

// @Override
// public void afterConnectionEstablished(WebSocketSession session) throws
// Exception {
// sessions.put(session.getId(), session);
// System.out.println("用户上线: " + session.getId());
// broadcast("【系统】" + session.getId().substring(0, 6) + " 加入聊天");
// }

// @Override
// protected void handleTextMessage(WebSocketSession session, TextMessage
// message) throws Exception {
// String payload = message.getPayload();
// String sender = session.getId().substring(0, 6);
// String msg = "[" + sender + "]: " + payload;
// broadcast(msg);
// }

// @Override
// public void afterConnectionClosed(WebSocketSession session, CloseStatus
// status) throws Exception {
// sessions.remove(session.getId());
// System.out.println("用户下线: " + session.getId());
// broadcast("【系统】" + session.getId().substring(0, 6) + " 离开聊天");
// }

// // 广播消息给所有人
// private void broadcast(String message) {
// sessions.values().parallelStream()
// .filter(WebSocketSession::isOpen)
// .forEach(session -> {
// try {
// session.sendMessage(new TextMessage(message));
// } catch (IOException e) {
// e.printStackTrace();
// }
// });
// }
// }
