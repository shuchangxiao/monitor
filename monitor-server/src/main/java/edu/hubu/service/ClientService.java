package edu.hubu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import edu.hubu.entity.dto.Client;
import edu.hubu.entity.vo.request.ClientDetailVO;
import edu.hubu.entity.vo.request.RenameClientVO;
import edu.hubu.entity.vo.request.RenameNodeVO;
import edu.hubu.entity.vo.request.RuntimeDetailVO;
import edu.hubu.entity.vo.response.ClientDetailsVO;
import edu.hubu.entity.vo.response.ClientPreviewVO;
import edu.hubu.entity.vo.response.ClientSimpleVO;
import edu.hubu.entity.vo.response.RuntimeHistoryVO;

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
}
