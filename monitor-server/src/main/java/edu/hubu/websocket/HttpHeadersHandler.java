package edu.hubu.websocket;

import cn.hutool.core.net.url.UrlBuilder;
import cn.hutool.extra.spring.SpringUtil;
import edu.hubu.service.WebSocketService;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

/**
 * @Package edu.hubu.websocket
 * @Author 舒聪聪
 * @Date 2024/8/19 15:02
 * @description: 处理websocket的token
 */
@Slf4j
public class HttpHeadersHandler extends ChannelInboundHandlerAdapter {
    private final WebSocketService webSocketService = SpringUtil.getBean(WebSocketService.class);
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof FullHttpRequest) {
            // 将消息转换为FullHttpRequest类型
            FullHttpRequest request = (FullHttpRequest) msg;
            // 从请求URI中构建UrlBuilder对象
            UrlBuilder urlBuilder = UrlBuilder.ofHttp(request.uri());
            // 获取token参数并设置到上下文中
            String token = Optional.ofNullable(urlBuilder.getQuery())
                    .map(k -> k.get("token"))
                    .map(CharSequence::toString)
                    .orElse("");
            String clientId = Optional.ofNullable(urlBuilder.getQuery())
                    .map(k -> k.get("clientId"))
                    .map(CharSequence::toString)
                    .orElse("");
            NettyUtil.setAttr(ctx.channel(), NettyUtil.TOKEN, token);
            NettyUtil.setAttr(ctx.channel(), NettyUtil.CLIENTID, clientId);
            request.setUri(String.valueOf(urlBuilder.getPath()));
            ctx.pipeline().remove(this);
            ctx.fireChannelRead(msg);
        } else {
            // 如果消息不是FullHttpRequest类型，则继续传递到下一个处理器
            ctx.fireChannelRead(msg);
        }
    }

}
