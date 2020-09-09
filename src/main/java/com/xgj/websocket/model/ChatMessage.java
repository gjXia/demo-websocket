package com.xgj.websocket.model;

import com.xgj.websocket.enums.MessageType;
import lombok.Data;


/**
 * @author gjXia
 * @version 1.0
 * @date 2020/9/8 11:31
 */
@Data
public class ChatMessage {

    /**
     * 消息类型
     */
    private MessageType type;

    /**
     * 发送者
     */
    private String sender;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 在线人数
     */
    private int onlineSize;

    public ChatMessage() {
    }

    public ChatMessage(MessageType type, String sender, String content, int onlineSize) {
        this.type = type;
        this.sender = sender;
        this.content = content;
        this.onlineSize = onlineSize;
    }
}
