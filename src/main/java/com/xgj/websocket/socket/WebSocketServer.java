package com.xgj.websocket.socket;

import com.alibaba.fastjson.JSONObject;
import com.xgj.websocket.enums.MessageType;
import com.xgj.websocket.model.ChatMessage;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * WebSocket服务端代码，包含接收消息，推送消息等接口
 *
 * @author gjXia
 * @version 1.0
 * @date 2020/9/8 10:43
 */
@Component
@ServerEndpoint(value = "/socket/{name}")
public class WebSocketServer {

    /**
     * 静态变量，用来记录当前在线连接数。应该把它设计成线程安全的
     */
    private static AtomicInteger online = new AtomicInteger();

    /**
     * concurrent包的线程安全Map，用来存放每个客户端对应的WebSocketServer对象
     */
    private static Map<String, Session> sessionMap = new ConcurrentHashMap<>();

    /**
     * 连接建立成功调用
     *
     * @param session  客户端与socket建立的会话
     * @param userName 客户端的userName
     */
    @OnOpen
    public void onOpen(Session session, @PathParam(value = "name") String userName) {
        sessionMap.put(userName, session);
        addOnlineCount();
        System.out.println(userName + "加入webSocket！当前人数为" + online);
        try {
            ChatMessage chatMessage = new ChatMessage(MessageType.JOIN, "", "欢迎" + userName + "加入聊天室！", online.get());
            sendMessageToAll(JSONObject.toJSONString(chatMessage));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnMessage
    public void onMessage(Session session, String jsonStr) {
        sendMessageToAll(jsonStr);
    }

    /**
     * 关闭连接时调用
     *
     * @param userName 关闭连接的客户端的姓名
     */
    @OnClose
    public void onClose(@PathParam(value = "name") String userName) {
        sessionMap.remove(userName);
        subOnlineCount();
        System.out.println(userName + "断开webSocket连接！当前人数为" + online);
        try {
            ChatMessage chatMessage = new ChatMessage(MessageType.LEAVE, "", userName + "离开了聊天室！", online.get());
            sendMessageToAll(JSONObject.toJSONString(chatMessage));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 发生错误时候调用
     */
    @OnError
    public void onError(Session session, Throwable throwable) {
        System.out.println("发生错误");
        throwable.printStackTrace();
    }

    /**
     * 给指定用户发送消息
     *
     * @param userName 用户名
     * @param message  消息
     */
    public void sendInfo(String userName, String message) {
        Session session = sessionMap.get(userName);
        try {
            sendMessage(session, message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送消息方法
     *
     * @param session 客户端与socket建立的会话
     * @param message 消息
     * @throws IOException 异常信息
     */
    private void sendMessage(Session session, String message) throws IOException {
        if (session != null) {
            session.getBasicRemote().sendText(message);
        }
    }

    /**
     * 给所有人发送消息
     *
     * @param message 消息内容
     */
    private void sendMessageToAll(String message) {
        sessionMap.forEach((userName, session) -> {
            try {
                session.getBasicRemote().sendText(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * 有新的用户连接时，连接数自加1
     */
    public static void addOnlineCount() {
        online.incrementAndGet();
    }

    /**
     * 断开连接时，连接数自减1
     */
    public static void subOnlineCount() {
        online.decrementAndGet();
    }
}
