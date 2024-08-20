package edu.hubu.websocket;

import cn.hutool.extra.spring.SpringUtil;
import edu.hubu.service.WebSocketService;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;

/**
 * @Package edu.hubu.websocket
 * @Author 舒聪聪
 * @Date 2024/8/19 15:18
 * @description: 用于处理成功的信息，进行ssh连接
 */
@Slf4j
public class NettyWebSocketServerHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {
    private final WebSocketService webSocketService = SpringUtil.getBean(WebSocketService.class);
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, TextWebSocketFrame textWebSocketFrame) throws Exception {
        String text = textWebSocketFrame.text();
        webSocketService.onMessage(channelHandlerContext.channel(),text);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        webSocketService.onClose(ctx.channel());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("用户WebSock连接出现错误",cause.getCause());
        ctx.channel().close();
    }
}
