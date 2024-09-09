package edu.hubu.service;

import io.netty.channel.Channel;

import java.io.IOException;

/**
 * @Package edu.hubu.service
 * @Author 舒聪聪
 * @Date 2024/8/19 20:23
 * @description: WebSocketService接口
 */
public interface WebSocketService {
    void addChannel(Channel channel, String clientId) throws IOException;
    void onMessage(Channel channel, String message) throws IOException;
    void onClose(Channel channel) throws IOException;
    void executeReadAsync(Channel channel);
}
