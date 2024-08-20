package edu.hubu.service.Imp;

import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import edu.hubu.entity.dto.ClientDetail;
import edu.hubu.entity.dto.ClientSsh;
import edu.hubu.mapper.ClientDetailMapper;
import edu.hubu.mapper.ClientSshMapper;
import edu.hubu.service.WebSocketService;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import jakarta.annotation.Resource;
import jakarta.websocket.CloseReason;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Package edu.hubu.websocket
 * @Author 舒聪聪
 * @Date 2024/8/19 15:11
 * @description: 用于websock服务消息发送
 */
@Service
@Slf4j
public class WebSocketServerImp implements WebSocketService {
    @Resource
    private ClientDetailMapper clientDetailMapper;
    @Resource
    private ClientSshMapper clientSshMapper;

    private final ExecutorService service = Executors.newCachedThreadPool();
    private final ConcurrentHashMap<Channel,WebSocketServerImp.Shell> chanelMap = new ConcurrentHashMap<>();
    @Override
    public void onClose(Channel channel) throws IOException {
        Shell shell = chanelMap.get(channel);
        if(shell !=null){
            shell.close();
            chanelMap.remove(channel);
            log.info("主机 {} 的SSH连接已断开",shell.js.getHost());
        }
    }
    @Override
    public void onMessage(Channel channel, String message) throws IOException {
        Shell shell = chanelMap.get(channel);
        OutputStream output = shell.output;
        output.write(message.getBytes(StandardCharsets.UTF_8));
        output.flush();
    }
    @Override
    public void addChannel(Channel channel, String clientId) throws IOException {
        ClientDetail clientDetail = clientDetailMapper.selectById(clientId);
        ClientSsh ssh = clientSshMapper.selectById(clientId);
        if(ssh == null || clientDetail == null){
            channel.write(new CloseReason(CloseReason.CloseCodes.CANNOT_ACCEPT,"无法识别此主机"));
            return;
        }
        if(this.createSshConnection(channel,ssh,clientDetail.getIp())){
            log.info("主机 {} 的SSH连接已经创建",clientDetail.getIp());
        }
    }
    private boolean createSshConnection(Channel channel, ClientSsh ssh, String ip) throws IOException {
        JSch jSch = new JSch();
        try {
            com.jcraft.jsch.Session js = jSch.getSession(ssh.getUsername(), ip, ssh.getPort());
            js.setPassword(ssh.getPassword());
            js.setConfig("StrictHostKeyChecking","no");
            js.setTimeout(3000);
            js.connect();
            ChannelShell channelShell = (ChannelShell) js.openChannel("shell");
            channelShell.setPtyType("xterm");
            channelShell.connect(1000);
            chanelMap.put(channel, new WebSocketServerImp.Shell(channel, js, channelShell));
            return true;
        } catch (JSchException e) {
            String message = e.getMessage();
            if(message.equals("Auth fail")){
                channel.write(new CloseReason(CloseReason.CloseCodes.CANNOT_ACCEPT,"登录SSH失败，用户民或密码错误"));
                log.error("登录SSH失败，用户民或密码错误");
            }else if (message.contains("Connection refused")){
                channel.write(new CloseReason(CloseReason.CloseCodes.CANNOT_ACCEPT,"连接被拒绝，可能未启动SSH服务或未开发端口"));
                log.error("登录SSH失败，连接被拒绝，可能未启动SSH服务或未开发端口");
            }else {
                channel.write(new CloseReason(CloseReason.CloseCodes.CANNOT_ACCEPT,message));
                log.error("登录SSH时错误"+message);
            }
            return false;
        }
    }

    private class Shell{
        private final Channel channel;
        private final com.jcraft.jsch.Session js;
        private final ChannelShell channelShell;
        private final InputStream input;
        private final OutputStream output;
        public Shell(Channel channel, com.jcraft.jsch.Session js,ChannelShell channelShell) throws IOException {
            this.js = js;
            this.channel = channel;
            this.channelShell = channelShell;
            this.input = channelShell.getInputStream();
            this.output = channelShell.getOutputStream();
            service.submit(this::read);
        }
        private void read(){
            try {
                byte[] buffer = new byte[1024*1024];
                int i;
                while ((i = input.read(buffer)) != -1){
                    String text = new String(Arrays.copyOfRange(buffer,0,i), StandardCharsets.UTF_8);
                    channel.writeAndFlush(new TextWebSocketFrame(text));
                }
            } catch (IOException e) {
                log.error("读取SSH输入流时出现问题");
            }
        }
        public void close() throws IOException{
            input.close();
            output.close();
            channelShell.disconnect();
            js.disconnect();
//            service.shutdown();
        }
    }

}
