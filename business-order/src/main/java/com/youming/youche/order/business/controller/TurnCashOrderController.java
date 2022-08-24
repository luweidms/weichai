package com.youming.youche.order.business.controller;


import com.youming.youche.commons.base.BaseController;
import com.youming.youche.commons.response.ResponseResult;
import com.youming.youche.order.api.ITurnCashOrderService;
import com.youming.youche.order.domain.TurnCashOrder;
import com.youming.youche.order.dto.order.TurnCashDto;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
* <p>
*  前端控制器
* </p>
* @author wuhao
* @since 2022-04-25
*/
@RestController
@RequestMapping("turn/cash/order")
public class TurnCashOrderController extends BaseController<TurnCashOrder, ITurnCashOrderService> {

    @DubboReference(version = "1.0.0")
    ITurnCashOrderService turnCashOrderService;

    @Override
    public ITurnCashOrderService getService() {
        return turnCashOrderService;
    }

    /**
     * App接口-ETC转现  28316
     */
    @GetMapping("doQueryEtcCashover")
    public ResponseResult doQueryEtcCashover(Long orderId, Long tenantId) {
        List<TurnCashDto> turnCashDtos = turnCashOrderService.doQueryEtcCashover(orderId, tenantId);
        return ResponseResult.success(turnCashDtos);
    }

    /**
     * App接口-油转现   28314
     */
    @GetMapping("doQueryOilTransfer")
    public ResponseResult doQueryOilTransfer(Long orderId, Long tenantId) {
        List<TurnCashDto> turnCashDtos = turnCashOrderService.doQueryOilTransfer(orderId, tenantId);
        return ResponseResult.success(turnCashDtos);
    }

}
