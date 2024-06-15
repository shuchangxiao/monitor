package edu.hubu.controller;

import com.alibaba.fastjson2.JSONArray;
import edu.hubu.entity.RestBean;
import edu.hubu.entity.dto.AccountDto;
import edu.hubu.entity.vo.request.RenameClientVO;
import edu.hubu.entity.vo.request.RenameNodeVO;
import edu.hubu.entity.vo.request.RuntimeDetailVO;
import edu.hubu.entity.vo.request.SshConnectionVO;
import edu.hubu.entity.vo.response.*;
import edu.hubu.service.AccountService;
import edu.hubu.service.ClientService;
import edu.hubu.utils.Const;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/monitor")
public class MonitorController {
    @Resource
    ClientService clientService;
    @Resource
    AccountService accountService;
    @GetMapping("/list")
    public RestBean<List<ClientPreviewVO>> getListClientPreview(
            @RequestAttribute(Const.ATTR_USER_ID) int id,
            @RequestAttribute(Const.Attr_USER_ROLE) String role){
        List<ClientPreviewVO> clients = clientService.getListClientPreview();
        if(this.isAdminAccount(role)){
            return RestBean.success(clients);
        }else {
            List<Integer> ids = this.accountAccessClients(id);
            return RestBean.success(clients.stream()
                    .filter(item-> {
                        if (ids != null) {
                            return ids.contains(item.getId());
                        }else return false;
                    })
                    .toList());
        }
    }
    @GetMapping("/simple-list")
    public RestBean<List<ClientSimpleVO>> getSimpleListClientPreview(@RequestAttribute(Const.Attr_USER_ROLE) String role){
        if(this.isAdminAccount(role)){
            return RestBean.success(clientService.getListSimpleClientPreview());
        }
        return RestBean.noPermission();
    }
    @PostMapping("/rename")
    public RestBean<Void> rename(@RequestBody @Valid RenameClientVO vo,
                                 @RequestAttribute(Const.ATTR_USER_ID) int id,
                                 @RequestAttribute(Const.Attr_USER_ROLE) String role){
        if (permissionCheck(id,role,vo.getId())){
            clientService.renameClient(vo);
            return RestBean.success();
        }
        return RestBean.noPermission();
    }
    @PostMapping("/rename-node")
    public RestBean<Void> renameNode(@RequestBody @Valid RenameNodeVO vo,
                                     @RequestAttribute(Const.ATTR_USER_ID) int id,
    @RequestAttribute(Const.Attr_USER_ROLE) String role){
        if (permissionCheck(id,role,vo.getId())){
            clientService.renameNode(vo);
            return RestBean.success();
        }
        return RestBean.noPermission();
    }
    @GetMapping("/details")
    public RestBean<ClientDetailsVO> getDetails(@RequestParam int clientId,
                                                @RequestAttribute(Const.ATTR_USER_ID) int id,
    @RequestAttribute(Const.Attr_USER_ROLE) String role){
        if (permissionCheck(id,role,clientId)){
            return RestBean.success(clientService.clientDetails(clientId));
        }
        return RestBean.noPermission();
    }
    @GetMapping("/runtime-history")
    public RestBean<RuntimeHistoryVO> runtimeDetailsHistory(@RequestParam int clientId,
                                                            @RequestAttribute(Const.ATTR_USER_ID) int id,
                                                            @RequestAttribute(Const.Attr_USER_ROLE) String role){
        if (permissionCheck(id,role,clientId)){
            return RestBean.success(clientService.clientRuntimeDetailsHistory(clientId));
        }
        return RestBean.noPermission();
    }
    @GetMapping("/runtime-now")
    public RestBean<RuntimeDetailVO> runtimeDetailsNow(@RequestParam int clientId,
                                                       @RequestAttribute(Const.ATTR_USER_ID) int id,
                                                       @RequestAttribute(Const.Attr_USER_ROLE) String role){
        if (permissionCheck(id,role,clientId)){
            return RestBean.success(clientService.clientRuntimeDetailsNow(clientId));
        }
        return RestBean.noPermission();
    }
    @GetMapping("/register")
    public RestBean<String> registerToken(@RequestAttribute(Const.Attr_USER_ROLE) String role){
        if(isAdminAccount(role)){
            return RestBean.success(clientService.registerToke());
        }
        return RestBean.noPermission();
    }
    @GetMapping("/delete")
    public RestBean<Void> deleteClient(@RequestParam int clientId,
                                       @RequestAttribute(Const.Attr_USER_ROLE) String role){
        if (this.isAdminAccount(role)){
            clientService.deleteClient(clientId);
            return RestBean.success();
        }
        return RestBean.noPermission();
    }
    @PostMapping("/ssh-save")
    public RestBean<Void> saveSshConnect(@RequestBody @Valid SshConnectionVO vo,
                                         @RequestAttribute(Const.ATTR_USER_ID) int id,
                                         @RequestAttribute(Const.Attr_USER_ROLE) String role){
        if(permissionCheck(id,role,vo.getId())){
            clientService.saveClientSshConnection(vo);
            return RestBean.success();
        }
        return RestBean.noPermission();
    }
    @GetMapping("/ssh")
    public RestBean<SshSettingVO> saveSshConnect(int clientId,
                                                 @RequestAttribute(Const.ATTR_USER_ID) int id,
                                                 @RequestAttribute(Const.Attr_USER_ROLE) String role){
        if(permissionCheck(id,role,clientId)){
            return RestBean.success(clientService.sshSetting(clientId));
        }
        return RestBean.noPermission();
    }
    private List<Integer> accountAccessClients(int uid){
        AccountDto accountDto = accountService.getById(uid);
        if (accountDto==null) return null;
        return JSONArray.parseArray(accountDto.getClients()).toList(Integer.class);
    }
    private boolean isAdminAccount(String role){
        // 前面有 ROLE_ 5个字符
        role = role.substring(5);
        return Const.ROLE_ADMIN.equals(role);
    }
    public boolean permissionCheck(int uid,String role,int clientId){
        if(this.isAdminAccount(role)) return true;
        return Objects.requireNonNull(this.accountAccessClients(uid)).contains(clientId);
    }
}
