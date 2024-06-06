package edu.hubu.service.Imp;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.hubu.entity.dto.Client;
import edu.hubu.entity.dto.ClientDetail;
import edu.hubu.entity.vo.request.ClientDetailVO;
import edu.hubu.entity.vo.request.RenameClientVO;
import edu.hubu.entity.vo.request.RenameNodeVO;
import edu.hubu.entity.vo.request.RuntimeDetailVO;
import edu.hubu.entity.vo.response.ClientDetailsVO;
import edu.hubu.entity.vo.response.ClientPreviewVO;
import edu.hubu.entity.vo.response.RuntimeHistoryVO;
import edu.hubu.mapper.ClientDetailMapper;
import edu.hubu.mapper.ClientMapper;
import edu.hubu.service.ClientService;
import edu.hubu.utils.InfluxdbUtils;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ClientServiceImp extends ServiceImpl<ClientMapper, Client> implements ClientService {
    @Resource
    ClientDetailMapper clientDetailMapper;
    @Resource
    InfluxdbUtils influxdbUtils;
    private String registerToken;
    private final Map<Integer, Client> clientCache = new ConcurrentHashMap<>();
    private final Map<String, Client> clientTokenCache = new ConcurrentHashMap<>();
    private final Map<Integer,RuntimeDetailVO> runtimeMap = new ConcurrentHashMap<>();
    @Override
    public boolean verifyAndRegister(String token){
        if (this.registerToken.equals(token)) {
            int id = this.randomClientId();
            Client clientDetail = new Client(id,"未命名主机",token,new Date(),"cn","未命名节点");
            if (this.save(clientDetail)) {
                registerToken = this.generateNewToken();
                this.addClientCache(clientDetail);
                return true;
            }
        }
        return false;
    }

    @Override
    public Client findClientById(int id){
        return clientCache.get(id);
    }

    @Override
    public void renameNode(RenameNodeVO vo) {
        this.update(Wrappers.<Client>update().eq("id",vo.getId())
                .set("location",vo.getLocation())
                .set("node",vo.getNode()));
        this.initialCache();
    }

    @Override
    public RuntimeHistoryVO clientRuntimeDetailsHistory(int clientId) {
        RuntimeHistoryVO vo = influxdbUtils.readRuntimeData(clientId);
        ClientDetail clientDetail = clientDetailMapper.selectById(clientId);
        BeanUtils.copyProperties(clientDetail,vo);
        return vo;
    }

    @Override
    public void deleteClient(int clientId) {
        this.removeById(clientId);
        clientDetailMapper.deleteById(clientId);
        this.initialCache();
        runtimeMap.remove(clientId);
    }

    @Override
    public RuntimeDetailVO clientRuntimeDetailsNow(int clientId) {
        return runtimeMap.get(clientId);
    }

    @Override
    public void renameClient(RenameClientVO vo) {
        this.update(Wrappers.<Client>update().eq("id",vo.getId()).set("name",vo.getName()));
        this.initialCache();
    }

    @Override
    public List<ClientPreviewVO> getListClientPreview() {
        return clientCache.values().stream().map(client -> {
            ClientPreviewVO vo = client.asViewObject(ClientPreviewVO.class);
            BeanUtils.copyProperties(clientDetailMapper.selectById(vo.getId()), vo);
            RuntimeDetailVO runtime = runtimeMap.get(client.getId());
            if (this.isOnline(runtime)) {
                BeanUtils.copyProperties(runtime, vo);
                vo.setOnline(true);
            }
            return vo;
        }).toList();

    }

    @Override
    public void runtimeUpdateDetail(int id, RuntimeDetailVO vo) {
        runtimeMap.put(id, vo);
        influxdbUtils.writeRuntimeData(id,vo);
    }

    @Override
    public void updateClientDetails(int id, ClientDetailVO vo) {
        ClientDetail detail = new ClientDetail();
        BeanUtils.copyProperties(vo,detail);
        detail.setId(id);
        if(Objects.nonNull(clientDetailMapper.selectById(id))){
            clientDetailMapper.updateById(detail);
        }else {
            clientDetailMapper.insert(detail);
        }
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
        this.initialCache();
        this.registerToken = this.generateNewToken();
    }
    private void initialCache(){
        clientTokenCache.clear();
        clientCache.clear();
        this.list().forEach(this::addClientCache);
    }

    @Override
    public ClientDetailsVO clientDetails(int id) {
        ClientDetailsVO vo = this.clientCache.get(id).asViewObject(ClientDetailsVO.class);
        BeanUtils.copyProperties(clientDetailMapper.selectById(id),vo);
        vo.setOnline(this.isOnline(runtimeMap.get(id)));
        return vo;
    }
    private boolean isOnline(RuntimeDetailVO runtime){
        return runtime != null && System.currentTimeMillis() - runtime.getTimestamp() < 60 * 1000;
    }
    private void addClientCache(Client client){
        clientCache.put(client.getId(), client);
        clientTokenCache.put(client.getToken(), client);
    }
    private String generateNewToken(){
        String Characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder();
        for (int i=0;i < 24;i++){
            sb.append(Characters.charAt(random.nextInt(Characters.length())));
        }
        return sb.toString();
    }
}
