package com.youming.youche.order.business.controller.order;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.response.ResponseResult;
import com.youming.youche.commons.web.Header;
import com.youming.youche.order.api.order.IOrderOilCardInfoService;
import com.youming.youche.order.domain.Address;
import com.youming.youche.order.domain.order.OrderOilCardInfo;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.youming.youche.commons.base.BaseController;

import java.util.List;

/**
 * <p>
 * 订单油卡表 前端控制器
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-03-17
 */
@RestController
@RequestMapping("order/oil/card/info")
public class OrderOilCardInfoController extends BaseController<OrderOilCardInfo, IOrderOilCardInfoService> {
    @DubboReference(version = "1.0.0")
    IOrderOilCardInfoService orderOilCardInfoService;
    @Override
    public IOrderOilCardInfoService getService() {
        return orderOilCardInfoService;
    }


    /**
     * 获取订单使用的油卡
     * @return
     */
    @GetMapping("/getOrderOilCard")
    public ResponseResult queryAddress(@RequestParam("orderId") Long orderId) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        if(orderId==null||orderId<=0) {
            throw new BusinessException("请传入订单号！");
        }
        List<OrderOilCardInfo> orderOilCardInfos = orderOilCardInfoService.queryOrderOilCardInfoByOrderId(orderId, null);

        return ResponseResult.success(orderOilCardInfos);
    }
}
