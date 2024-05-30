package edu.hubu.service.Imp;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.hubu.entity.dto.Client;
import edu.hubu.mapper.ClientMapper;
import edu.hubu.service.ClientService;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Date;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ClientServiceImp extends ServiceImpl<ClientMapper, Client> implements ClientService {

    private String registerToken;
    private final Map<Integer,Client> clientCache = new ConcurrentHashMap<>();
    private final Map<String,Client> clientTokenCache = new ConcurrentHashMap<>();

    @Override
    public boolean verifyAndRegister(String token){
        if (this.registerToken.equals(token)) {
            int id = this.randomClientId();
            Client client = new Client(id,"未命名主机",token,new Date());
            if (this.save(client)) {
                registerToken = this.generateNewToken();
                this.addClientCache(client);
                return true;
            }
        }
        return false;
    }
    private void addClientCache(Client client){
        clientCache.put(client.getId(),client);
        clientTokenCache.put(client.getToken(),client);
    }
    @Override
    public Client findClientById(int id){
        return clientCache.get(id);
    }
    @Override
    public Client findClientByToken(String token){
        return clientTokenCache.get(token);
    }
    private int randomClientId(){
        return new Random().nextInt(900000)+100000;
    }
    @Override
    public String registerToke() {
        return this.registerToken;
    }

    @PostConstruct
    public void initialize(){
        this.list().forEach(this::addClientCache);
        this.registerToken = this.generateNewToken();
        System.out.println(this.registerToken);
    }
    private String generateNewToken(){
        String Characters = "abcdefghijklmnopqrstuvwxzyABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder();
        for (int i=0;i < 24;i++){
            sb.append(Characters.charAt(random.nextInt(Characters.length())));
        }
        return sb.toString();
    }
}
