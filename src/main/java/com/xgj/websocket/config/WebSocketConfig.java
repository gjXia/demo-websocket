package com.xgj.websocket.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * socket配置类,往 spring 容器中注入ServerEndpointExporter实例
 *
 * @author gjXia
 * @version 1.0
 * @date 2020/9/8 10:31
 */
@Configuration
public class WebSocketConfig {

    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }
}
