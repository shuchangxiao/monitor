package edu.hubu.controller;

import edu.hubu.entity.RestBean;
import edu.hubu.entity.vo.request.RenameClientVO;
import edu.hubu.entity.vo.request.RenameNodeVO;
import edu.hubu.entity.vo.request.RuntimeDetailVO;
import edu.hubu.entity.vo.response.ClientDetailsVO;
import edu.hubu.entity.vo.response.ClientPreviewVO;
import edu.hubu.entity.vo.response.RuntimeHistoryVO;
import edu.hubu.service.ClientService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/monitor")
public class MonitorController {
    @Resource
    ClientService clientService;
    @GetMapping("/list")
    public RestBean<List<ClientPreviewVO>> getListClientPreview(){
        return RestBean.success(clientService.getListClientPreview());
    }
    @PostMapping("/rename")
    public RestBean<Void> rename(@RequestBody @Valid RenameClientVO vo){
        clientService.renameClient(vo);
        return RestBean.success();
    }
    @PostMapping("/rename-node")
    public RestBean<Void> renameNode(@RequestBody @Valid RenameNodeVO vo){
        clientService.renameNode(vo);
        return RestBean.success();
    }
    @GetMapping("/details")
    public RestBean<ClientDetailsVO> getDetails(@RequestParam int id){
        return RestBean.success(clientService.clientDetails(id));
    }
    @GetMapping("/runtime-history")
    public RestBean<RuntimeHistoryVO> runtimeDetailsHistory(@RequestParam int id){
        return RestBean.success(clientService.clientRuntimeDetailsHistory(id));
    }
    @GetMapping("/runtime-now")
    public RestBean<RuntimeDetailVO> runtimeDetailsNow(@RequestParam int id){
        return RestBean.success(clientService.clientRuntimeDetailsNow(id));
    }
}
