package edu.hubu.websocket;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;

/**
 * Description: netty工具类
 * Author: <a href="https://github.com/zongzibinbin">abin</a>
 * Date: 2023-04-18
 */

public class NettyUtil {

    // 定义一个属性键，用于存储令牌
    public static AttributeKey<String> TOKEN = AttributeKey.valueOf("token");
    // 定义一个属性键，用于存储IP地址

    public static AttributeKey<String> CLIENTID = AttributeKey.valueOf("clientId");
    // 定义一个属性键，用于存储WebSocketServerHandshaker对象
    public static AttributeKey<WebSocketServerHandshaker> HANDSHAKER_ATTR_KEY = AttributeKey.valueOf(WebSocketServerHandshaker.class, "HANDSHAKER");

    /**
     * 设置Channel的属性
     *
     * @param channel 要设置属性的Channel
     * @param attributeKey 属性键
     * @param data 属性值
     * @param <T> 属性值的类型
     */
    public static <T> void setAttr(Channel channel, AttributeKey<T> attributeKey, T data) {
        Attribute<T> attr = channel.attr(attributeKey);
        attr.set(data);
    }

    /**
     * 获取Channel的属性
     *
     * @param channel 要获取属性的Channel
     * @param <T> 属性值的类型
     * @return 属性值，如果属性不存在则返回null
     */
    public static <T> T getAttr(Channel channel, AttributeKey<T> ip) {
        return channel.attr(ip).get();
    }
}
