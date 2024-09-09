package edu.hubu;

/**
 * @Package edu.hubu
 * @Author 舒聪聪
 * @Date 2024/8/20 14:21
 * @description: test
 */

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;

public class WebSocketServerHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    @Override
    public void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        // 处理消息
        System.out.println("Received message: " + msg.text());
        ctx.channel().writeAndFlush(new TextWebSocketFrame("Server received: " + msg.text()));
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // 添加连接
        System.out.println("Client connected: " + ctx.channel());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        // 断开连接
        System.out.println("Client disconnected: " + ctx.channel());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        // 异常处理
        cause.printStackTrace();
        ctx.close();
    }
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
//        if(evt instanceof IdleStateEvent) {
//            IdleStateEvent event = (IdleStateEvent) evt;
//            if(event.state() == IdleState.WRITER_IDLE) {
//                System.out.println("好久都没写了，看视频的你真的有认真在跟着敲吗");
//            } else if(event.state() == IdleState.READER_IDLE) {
//                System.out.println("已经很久很久没有读事件发生了，好寂寞");
//            }
//        }
        if (evt instanceof WebSocketServerProtocolHandler.HandshakeComplete) {
            // 握手成功后发送消息
            System.out.println("握手成功");
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
}
