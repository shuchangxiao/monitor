package edu.hubu.websocket;

import cn.hutool.core.net.url.UrlBuilder;
import cn.hutool.extra.spring.SpringUtil;
import com.auth0.jwt.interfaces.DecodedJWT;
import edu.hubu.service.WebSocketService;
import edu.hubu.utils.AccountAccessClientsUtils;
import edu.hubu.utils.JwtUtils;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;
import jakarta.websocket.CloseReason;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;

/**
 * @Package edu.hubu.websocket
 * @Author 舒聪聪
 * @Date 2024/8/19 15:02
 * @description: 处理websocket的token
 */
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
            JwtUtils jwtUtils = SpringUtil.getBean(JwtUtils.class);
            DecodedJWT jwt = jwtUtils.resolveJwt(token);
            if (Objects.nonNull(jwt)) {
                UserDetails user = jwtUtils.toUser(jwt);
                String clientId = Optional.ofNullable(urlBuilder.getQuery())
                        .map(k -> k.get("clientId"))
                        .map(CharSequence::toString)
                        .orElse("");
                String authority = new ArrayList<>(user.getAuthorities()).get(0).getAuthority();
                if (AccountAccessClientsUtils.permissionCheck(jwtUtils.toInt(jwt),authority, Integer.parseInt(clientId))) {
                    webSocketService.addChannel(ctx.channel(), clientId);
                    // 从处理管道中移除当前处理器
                    ctx.pipeline().remove(this);
                    // 继续传递FullHttpRequest到下一个处理器
                    ctx.fireChannelRead(request);
                }else {
                    ctx.pipeline().remove(this);
                    ctx.channel().write(new CloseReason(CloseReason.CloseCodes.CANNOT_ACCEPT,"登录SSH失败,没有权限"));
                }
            }else {
                ctx.pipeline().remove(this);
                ctx.channel().write(new CloseReason(CloseReason.CloseCodes.CANNOT_ACCEPT,"登录SSH失败,未注册"));
            }
        } else {
            // 如果消息不是FullHttpRequest类型，则继续传递到下一个处理器
            ctx.fireChannelRead(msg);
        }
    }

}
