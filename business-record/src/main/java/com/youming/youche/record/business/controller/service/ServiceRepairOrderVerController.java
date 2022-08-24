package com.youming.youche.record.business.controller.service;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.commons.response.ResponseResult;
import com.youming.youche.commons.web.Header;
import com.youming.youche.record.api.service.IServiceRepairOrderService;
import com.youming.youche.record.api.service.IServiceRepairOrderVerService;
import com.youming.youche.record.domain.service.ServiceRepairOrder;
import com.youming.youche.record.domain.service.ServiceRepairOrderVer;
import com.youming.youche.record.dto.RepairOrderDetailVerDto;
import com.youming.youche.record.dto.RepairOrderDto;
import com.youming.youche.record.dto.service.GetRepairOrderPartsVerDto;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
    import com.youming.youche.commons.base.BaseController;

/**
* <p>
* 维修保养订单 前端控制器
* </p>
* @author zengwen
* @since 2022-05-23
*/
@RestController
@RequestMapping("service/repair/order/ver")
public class ServiceRepairOrderVerController extends BaseController<ServiceRepairOrderVer, IServiceRepairOrderVerService> {

    @DubboReference(version = "1.0.0")
    IServiceRepairOrderVerService serviceRepairOrderVerService;

    @Override
    public IServiceRepairOrderVerService getService() {
        return serviceRepairOrderVerService;
    }

    /**
     * 维修保养工单配件详情  82016
     */
    @GetMapping("getRepairOrderPartsVer")
    public ResponseResult getRepairOrderPartsVer(Long hisId, Long flowId, Long orderItemId) {
        GetRepairOrderPartsVerDto repairOrderPartsVer = serviceRepairOrderVerService.getRepairOrderPartsVer(hisId, flowId, orderItemId);
        return ResponseResult.success(repairOrderPartsVer);
    }


    /**
     * 获取维修保养工单列表(82014)
     * @param orderStatus 维保状态
     * @param pageNum 页码
     * @param pageSize 页面展示条数
     * @return
     */
    @GetMapping("/doQueryOrderListApp")
    public ResponseResult doQueryOrderListApp(Integer orderStatus,
                                              @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                              @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        Page<ServiceRepairOrder> serviceRepairOrderPage = serviceRepairOrderVerService.doQueryOrderListApp(orderStatus, pageNum, pageSize,accessToken);
        return ResponseResult.success(serviceRepairOrderPage);
    }



    /**
     * 维修保养工单详情(82015)
     * @param flowId 维保主键id
     * @return
     */
    @GetMapping("/getRepairOrderDetailVerApp")
    public ResponseResult getRepairOrderDetailVerApp(Long flowId){
        RepairOrderDetailVerDto repairOrderDetailVerApp = serviceRepairOrderVerService.getRepairOrderDetailVerApp(flowId);
        return ResponseResult.success(repairOrderDetailVerApp);
    }

    /**
     * 发起维修保养(82012)
     * @param repairOrderDto
     * @return
     */
    @PostMapping("/doSaveOrUpdateServiceRepairOrder")
    public ResponseResult doSaveOrUpdateServiceRepairOrder(@RequestBody RepairOrderDto repairOrderDto){
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        boolean b = serviceRepairOrderVerService.doSaveOrUpdateServiceRepairOrder(repairOrderDto, accessToken);
        return  ResponseResult.success(b);
    }


}
