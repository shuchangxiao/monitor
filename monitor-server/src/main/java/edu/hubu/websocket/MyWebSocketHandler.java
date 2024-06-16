package edu.hubu.websocket;

import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import edu.hubu.entity.dto.ClientDetail;
import edu.hubu.entity.dto.ClientSsh;
import edu.hubu.mapper.ClientDetailMapper;
import edu.hubu.mapper.ClientSshMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
@Scope("prototype")
@Slf4j
public class MyWebSocketHandler implements WebSocketHandler {
    private static ClientDetailMapper clientDetailMapper;

    @Resource
    public void setDetailMapper(ClientDetailMapper clientDetailMapper){
        MyWebSocketHandler.clientDetailMapper = clientDetailMapper;
    }
    private static ClientSshMapper clientSshMapper;;

    @Resource
    public void setClientSshMapper(ClientSshMapper clientSshMapper){
        MyWebSocketHandler.clientSshMapper = clientSshMapper;
    }

    private static final Map<WebSocketSession, MyWebSocketHandler.Shell> sessionMap = new ConcurrentHashMap<>();
    private final ExecutorService service = Executors.newSingleThreadExecutor();


    private boolean createSshConnection(WebSocketSession session, ClientSsh ssh, String ip) throws IOException {
        JSch jSch = new JSch();
        try {
            com.jcraft.jsch.Session js = jSch.getSession(ssh.getUsername(), ip, ssh.getPort());
            js.setPassword(ssh.getPassword());
            js.setConfig("StrictHostKeyChecking","no");
            js.setTimeout(3000);
            js.connect();
            ChannelShell channel = (ChannelShell) js.openChannel("shell");
            channel.setPtyType("xterm");
            channel.connect(1000);
            sessionMap.put(session, new MyWebSocketHandler.Shell(session, js, channel));
            return true;
        } catch (JSchException e) {
            String message = e.getMessage();
            if(message.equals("Auth fail")){
                CloseStatus closeStatus = new CloseStatus(1002, "登录SSH失败，用户名或密码错误");
                session.close(closeStatus);
                log.error("登录SSH失败，用户民或密码错误");
            }else if (message.contains("Connection refused")){
                CloseStatus closeStatus = new CloseStatus(1002, "连接被拒绝，可能未启动SSH服务或未开发端口");
                session.close(closeStatus);
                log.error("登录SSH失败，连接被拒绝，可能未启动SSH服务或未开发端口");
            }else {
                CloseStatus closeStatus = new CloseStatus(1002, "连接被拒绝，可能未启动SSH服务或未开发端口");
                session.close(closeStatus);
                log.error("登录SSH时错误"+message);
            }
            return false;
        }
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {

        String path = session.getUri().getPath();
        String[] split = path.split("/");
        int clientId = Integer.parseInt(split[split.length - 1].substring(0,6));
        ClientDetail clientDetail = clientDetailMapper.selectById(clientId);
        ClientSsh ssh = clientSshMapper.selectById(clientId);
        if(ssh == null || clientDetail == null){
            session.close(CloseStatus.NOT_ACCEPTABLE);
            return;
        }
        if(this.createSshConnection(session,ssh,clientDetail.getIp())){
            log.info("主机 {} 的SSH连接已经创建",clientDetail.getIp());
        }
    }


    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        MyWebSocketHandler.Shell shell = sessionMap.get(session);
        OutputStream output = shell.output;
        output.write(message.getPayload().toString().getBytes(StandardCharsets.UTF_8));
        output.flush();
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log.error("用户WebSock连接出现错误"+exception.getMessage());
        session.close();
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        MyWebSocketHandler.Shell shell = sessionMap.get(session);
        if(shell !=null){
            shell.close();
            sessionMap.remove(session);
            log.info("主机 {} 的SSH连接已断开",shell.js.getHost());
        }
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    private class Shell {
        private final WebSocketSession session;
        private final com.jcraft.jsch.Session js;
        private final ChannelShell channel;
        private final InputStream input;
        private final OutputStream output;

        public Shell(WebSocketSession session, com.jcraft.jsch.Session js, ChannelShell channel) throws IOException {
            this.js = js;
            this.session = session;
            this.channel = channel;
            this.input = channel.getInputStream();
            this.output = channel.getOutputStream();
            service.submit(this::read);
        }

        private void read() {
            try {
                byte[] buffer = new byte[1024 * 1024];
                int i;
                while ((i = input.read(buffer)) != -1) {
                    String text = new String(Arrays.copyOfRange(buffer, 0, i), StandardCharsets.UTF_8);
                    TextMessage textMessage = new TextMessage(text);
                    session.sendMessage(textMessage);
                }
            } catch (IOException e) {
                log.error("读取SSH输入流时出现问题");
            }
        }

        public void close() throws IOException {
            input.close();
            output.close();
            channel.disconnect();
            js.disconnect();
            service.shutdown();
        }
    }
}
