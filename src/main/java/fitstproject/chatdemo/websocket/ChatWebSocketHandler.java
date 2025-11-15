package fitstproject.chatdemo.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import fitstproject.chatdemo.pojo.message;
import fitstproject.chatdemo.service.chatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class ChatWebSocketHandler extends TextWebSocketHandler {

    // 存储所有在线用户的 WebSocket 会话，key: userId, value: WebSocketSession
    private static final Map<String, WebSocketSession> ONLINE_USERS = new ConcurrentHashMap<>();

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private chatService chatService;

    /**
     * WebSocket 连接建立后调用
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String userId = getUserId(session);
        if (userId != null) {
            ONLINE_USERS.put(userId, session);
            log.info("用户 {} 上线，当前在线人数: {}", userId, ONLINE_USERS.size());

            // 广播用户上线消息
            broadcastUserStatus(userId, true);
        }
    }

    /**
     * 接收到客户端消息时调用
     */
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        log.info("收到消息: {}", payload);

        try {
            // 解析消息
            @SuppressWarnings("unchecked")
            Map<String, Object> msg = objectMapper.readValue(payload, Map.class);
            String type = (String) msg.get("type");

            switch (type) {
                case "private":
                    handlePrivateMessage(msg);
                    break;
                case "group":
                    handleGroupMessage(msg);
                    break;
                case "heartbeat":
                    // 心跳消息，回复 pong
                    session.sendMessage(new TextMessage("{\"type\":\"pong\"}"));
                    break;
                default:
                    log.warn("未知消息类型: {}", type);
            }
        } catch (Exception e) {
            log.error("处理消息失败", e);
        }
    }

    /**
     * WebSocket 连接关闭时调用
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String userId = getUserId(session);
        if (userId != null) {
            ONLINE_USERS.remove(userId);
            log.info("用户 {} 下线，当前在线人数: {}", userId, ONLINE_USERS.size());

            // 广播用户下线消息
            broadcastUserStatus(userId, false);
        }
    }

    /**
     * 处理单聊消息
     */
    private void handlePrivateMessage(Map<String, Object> msg) throws IOException {
        Integer sendId = (Integer) msg.get("sendId");
        Integer receiveId = (Integer) msg.get("receiveId");
        String content = (String) msg.get("content");

        log.info("单聊消息: {} -> {}: {}", sendId, receiveId, content);

        // 保存消息到数据库
        try {
            message messageEntity = new message();
            messageEntity.setSendId(sendId);
            messageEntity.setReceiveId(receiveId);
            messageEntity.setContent(content);
            chatService.send(messageEntity);
            log.info("消息已保存到数据库");
        } catch (Exception e) {
            log.error("保存消息到数据库失败", e);
        }

        // 构造消息对象
        Map<String, Object> messageData = Map.of(
                "type", "private",
                "sendId", sendId,
                "receiveId", receiveId,
                "content", content,
                "timestamp", System.currentTimeMillis());

        // 发送给接收者
        sendToUser(String.valueOf(receiveId), messageData);

        // 同时发送给发送者（用于多端同步）
        sendToUser(String.valueOf(sendId), messageData);
    }

    /**
     * 处理群聊消息
     */
    private void handleGroupMessage(Map<String, Object> msg) throws IOException {
        Integer sendId = (Integer) msg.get("sendId");
        String groupName = (String) msg.get("groupName");
        String content = (String) msg.get("content");

        log.info("群聊消息: {} -> {}: {}", sendId, groupName, content);

        // 先保存消息到数据库
        try {
            fitstproject.chatdemo.pojo.groupmessage gmessage = new fitstproject.chatdemo.pojo.groupmessage();
            gmessage.setSendId(sendId);
            gmessage.setGroupName(groupName);
            gmessage.setContent(content);
            chatService.send(gmessage);
            log.info("群聊消息已保存到数据库");
        } catch (Exception e) {
            log.error("保存群聊消息到数据库失败", e);
        }

        // 构造消息对象
        Map<String, Object> messageData = Map.of(
                "type", "group",
                "sendId", sendId,
                "groupName", groupName,
                "content", content,
                "timestamp", System.currentTimeMillis());

        // 获取群成员列表并发送给在线的群成员(排除发送者本人)
        try {
            java.util.List<Integer> groupMembers = chatService.getGroupMembers(groupName);
            log.info("群 {} 的成员列表: {}", groupName, groupMembers);
            if (groupMembers != null) {
                int sentCount = 0;
                for (Integer memberId : groupMembers) {
                    log.info("检查成员 {}, 发送者 {}, 是否相等: {}", memberId, sendId, memberId.equals(sendId));
                    // 跳过发送者本人,因为前端已经本地添加了消息
                    if (!memberId.equals(sendId)) {
                        log.info("准备发送消息给成员 {}", memberId);
                        sendToUser(String.valueOf(memberId), messageData);
                        sentCount++;
                    } else {
                        log.info("跳过发送者本人 {}", memberId);
                    }
                }
                log.info("群聊消息已发送给群 {} 的 {} 个成员(不包括发送者)", groupName, sentCount);
            } else {
                log.warn("群 {} 的成员列表为空", groupName);
            }
        } catch (Exception e) {
            log.error("发送群聊消息失败", e);
            // 降级方案:广播给所有在线用户
            broadcastMessage(messageData);
        }
    }

    /**
     * 发送消息给指定用户
     */
    private void sendToUser(String userId, Map<String, Object> message) throws IOException {
        WebSocketSession session = ONLINE_USERS.get(userId);
        if (session != null && session.isOpen()) {
            String json = objectMapper.writeValueAsString(message);
            session.sendMessage(new TextMessage(json));
            log.info("消息已发送给用户 {}", userId);
        } else {
            log.warn("用户 {} 不在线，消息未送达", userId);
        }
    }

    /**
     * 广播消息给所有在线用户
     */
    private void broadcastMessage(Map<String, Object> message) throws IOException {
        String json = objectMapper.writeValueAsString(message);
        TextMessage textMessage = new TextMessage(json);

        for (Map.Entry<String, WebSocketSession> entry : ONLINE_USERS.entrySet()) {
            WebSocketSession session = entry.getValue();
            if (session.isOpen()) {
                try {
                    session.sendMessage(textMessage);
                } catch (IOException e) {
                    log.error("广播消息失败，用户: {}", entry.getKey(), e);
                }
            }
        }
    }

    /**
     * 广播用户上线/下线状态
     */
    private void broadcastUserStatus(String userId, boolean online) throws IOException {
        Map<String, Object> statusMsg = Map.of(
                "type", "userStatus",
                "userId", userId,
                "online", online,
                "timestamp", System.currentTimeMillis());
        broadcastMessage(statusMsg);
    }

    /**
     * 从 WebSocket session 中获取用户ID
     */
    private String getUserId(WebSocketSession session) {
        // 从查询参数中获取 userId
        String query = session.getUri().getQuery();
        if (query != null && query.contains("userId=")) {
            String[] params = query.split("&");
            for (String param : params) {
                if (param.startsWith("userId=")) {
                    return param.substring(7);
                }
            }
        }
        return null;
    }

    /**
     * 获取在线用户数
     */
    public static int getOnlineCount() {
        return ONLINE_USERS.size();
    }
}
