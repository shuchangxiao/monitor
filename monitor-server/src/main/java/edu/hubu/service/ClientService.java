package edu.hubu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import edu.hubu.entity.dto.Client;
import edu.hubu.mapper.ClientMapper;

public interface ClientService extends IService<Client> {
    boolean verifyAndRegister(String token);
    public String registerToke();
    Client findClientById(int id);
    Client findClientByToken(String token);

}
