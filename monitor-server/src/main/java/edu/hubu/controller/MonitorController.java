package edu.hubu.controller;

import edu.hubu.entity.RestBean;
import edu.hubu.entity.vo.request.RenameClientVO;
import edu.hubu.entity.vo.response.ClientDetailsVO;
import edu.hubu.entity.vo.response.ClientPreviewVO;
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
    @GetMapping("/details")
    public RestBean<ClientDetailsVO> getDetails(@RequestParam int id){
        return RestBean.success(clientService.clientDetails(id));
    }
}
