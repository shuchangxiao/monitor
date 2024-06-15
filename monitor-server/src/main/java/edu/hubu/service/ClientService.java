package edu.hubu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import edu.hubu.entity.dto.Client;
import edu.hubu.entity.vo.request.*;
import edu.hubu.entity.vo.response.*;

import java.util.List;

public interface ClientService extends IService<Client> {
    boolean verifyAndRegister(String token);
    String registerToke();
    Client findClientById(int id);
    Client findClientByToken(String token);
    void updateClientDetails( int id, ClientDetailVO vo);
    void runtimeUpdateDetail(int id, RuntimeDetailVO vo);
    List<ClientPreviewVO> getListClientPreview();
    void renameClient(RenameClientVO vo);
    void renameNode(RenameNodeVO vo);
    ClientDetailsVO clientDetails(int id);
    RuntimeHistoryVO clientRuntimeDetailsHistory(int clientId);
    RuntimeDetailVO clientRuntimeDetailsNow(int clientId);
    void deleteClient(int clientId);
    List<ClientSimpleVO> getListSimpleClientPreview();
    void saveClientSshConnection(SshConnectionVO vo);
    SshSettingVO sshSetting(int id);
}
