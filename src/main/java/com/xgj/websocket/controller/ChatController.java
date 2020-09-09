package com.xgj.websocket.controller;

import com.alibaba.fastjson.JSONObject;
import com.xgj.websocket.model.ChatMessage;
import com.xgj.websocket.socket.WebSocketServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author gjXia
 * @version 1.0
 * @date 2020/9/8 10:51
 */
@RestController
public class ChatController {

    @Autowired
    private WebSocketServer webSocketServer;

    @PostMapping("/chat/{username}")
    public void chatOne(@PathVariable String username, @RequestBody ChatMessage chatMessage) {
        webSocketServer.sendInfo(username, JSONObject.toJSONString(chatMessage));
    }

}
