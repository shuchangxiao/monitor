//package edu.hubu.config;
//
//import edu.hubu.filter.MyWebSocketInterceptor;
//import edu.hubu.websocket.MyWebSocketHandler;
//import jakarta.annotation.Resource;
//import org.springframework.context.ApplicationContext;
//import org.springframework.context.ApplicationContextAware;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.stereotype.Component;
//import org.springframework.web.socket.CloseStatus;
//import org.springframework.web.socket.WebSocketHandler;
//import org.springframework.web.socket.WebSocketMessage;
//import org.springframework.web.socket.WebSocketSession;
//import org.springframework.web.socket.config.annotation.EnableWebSocket;
//import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
//import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
//
//@EnableWebSocket
//@Configuration
//@Component
//public class WebSocketConfig implements WebSocketConfigurer, ApplicationContextAware {
//    @Resource
//    MyWebSocketInterceptor myWebSocketInterceptor;
//    private ApplicationContext applicationContext;
//
//    @Override
//    public void setApplicationContext(ApplicationContext applicationContext) {
//        this.applicationContext = applicationContext;
//    }
//
//    @Override
//    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
//        registry.addHandler(new WebSocketHandler() {
//                    private MyWebSocketHandler handler;
//                    @Override
//                    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
//                        handler = applicationContext.getBean(MyWebSocketHandler.class);
//                        // 动态获取myWebSocketHandler的新实例
//                        handler.afterConnectionEstablished(session);
//                    }
//
//                    @Override
//                    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
//                        handler.handleMessage(session,message);
//                    }
//
//                    @Override
//                    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
//                        handler.handleTransportError(session,exception);
//                    }
//
//                    @Override
//                    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
//                        handler.afterConnectionClosed(session,closeStatus);
//                    }
//
//                    @Override
//                    public boolean supportsPartialMessages() {
//                        return false;
//                    }
//
//                    // 其他方法也可以用类似的方式委派给新实例
//
//                }, "/terminal/{clientId}")//设置连接路径和处理
//                .setAllowedOrigins("*")
//                .addInterceptors(myWebSocketInterceptor);//设置拦截器
//    }
//
////    @Bean
////    public ServerEndpointExporter serverEndpointExporter(){
////        return new ServerEndpointExporter();
////    }
//
//
//}
