package edu.hubu.websocket;

import cn.hutool.extra.spring.SpringUtil;
import com.auth0.jwt.interfaces.DecodedJWT;
import edu.hubu.service.WebSocketService;
import edu.hubu.utils.AccountAccessClientsUtils;
import edu.hubu.utils.JwtUtils;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import jakarta.websocket.CloseReason;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Objects;

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
        log.info("收到数据:{}", text);
        webSocketService.onMessage(channelHandlerContext.channel(),text);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        webSocketService.onClose(ctx.channel());
        ctx.channel().close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("用户WebSock连接出现错误",cause.getCause());
        ctx.channel().close();
    }
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        // ctx.channel().id() 表示唯一的值
        log.info("handlerAdded 被调用， channel.id.longText = " + ctx.channel().id().asLongText());
    }
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        log.info("channelActive 被调用， channel.id.longText = " + ctx.channel().id().asLongText());

    }

    // 处理WebSocket握手成功事件
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if(evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if(event.state() == IdleState.WRITER_IDLE) {
                System.out.println("好久都没写了，看视频的你真的有认真在跟着敲吗");
            } else if(event.state() == IdleState.READER_IDLE) {
                System.out.println("已经很久很久没有读事件发生了，好寂寞");
            }
        }
        if (evt instanceof WebSocketServerProtocolHandler.HandshakeComplete) {
            // 握手成功后发送消息
            log.info("握手成功");
            JwtUtils jwtUtils = SpringUtil.getBean(JwtUtils.class);
            String token = NettyUtil.getAttr(ctx.channel(), NettyUtil.TOKEN);
            DecodedJWT jwt = jwtUtils.resolveJwt(token);
            if (Objects.nonNull(jwt)) {
                UserDetails user = jwtUtils.toUser(jwt);
                String authority = new ArrayList<>(user.getAuthorities()).get(0).getAuthority();
                String clientId = NettyUtil.getAttr(ctx.channel(), NettyUtil.CLIENTID);
                if (AccountAccessClientsUtils.permissionCheck(jwtUtils.toInt(jwt),authority, Integer.parseInt(clientId))) {
                    webSocketService.addChannel(ctx.channel(), clientId);
                    log.info("channel:{} 登录成功",ctx.channel().id().asLongText());
                    webSocketService.executeReadAsync(ctx.channel());
                }else {
                    ctx.channel().write(new CloseReason(CloseReason.CloseCodes.CANNOT_ACCEPT,"登录SSH失败,没有权限"));
                    ctx.pipeline().remove(this);
                }
            }
            else {
                ctx.pipeline().remove(this);
                ctx.channel().write(new CloseReason(CloseReason.CloseCodes.CANNOT_ACCEPT,"登录SSH失败,token错误"));
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

}
