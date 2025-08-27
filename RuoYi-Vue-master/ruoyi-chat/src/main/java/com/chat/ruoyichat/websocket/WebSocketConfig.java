package com.chat.ruoyichat.websocket;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;


/**
 * 开启websocket支持
 */
@Configuration
public class WebSocketConfig {
    @Bean
    public ServerEndpointExporter serverEndpointExporter(){
//        ServerEndpointExporter serverEndpointExporter = new ServerEndpointExporter();
//        serverEndpointExporter.setAnnotatedEndpointClasses(WebSocketService.class);
//        return serverEndpointExporter;
        return new ServerEndpointExporter();
    }

    //解决websocket 报Component错
//    @Bean
//    public ServletServerContainerFactoryBean createWebSocketContainer() {
//        ServletServerContainerFactoryBean container = new ServletServerContainerFactoryBean();
//        container.setMaxTextMessageBufferSize(8192);
//        container.setMaxBinaryMessageBufferSize(8192);
//        return container;
//    }
//
//    @Bean
//    public ServerEndpointConfig webSocketServiceEndpoint() {
//        return ServerEndpointConfig.Builder.create(WebSocketService.class, "/ruoyichat/chat/{id}").build();
//    }
}

