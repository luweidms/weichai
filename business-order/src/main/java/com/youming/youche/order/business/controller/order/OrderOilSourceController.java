package com.youming.youche.order.business.controller.order;


import com.youming.youche.commons.base.BaseController;
import com.youming.youche.commons.response.ResponseResult;
import com.youming.youche.commons.web.Header;
import com.youming.youche.conts.SysStaticDataEnum;
import com.youming.youche.order.api.order.IOrderOilSourceService;
import com.youming.youche.order.domain.order.OrderOilSource;
import com.youming.youche.order.vo.OrderPaymentWayOilOut;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-03-19
 */
@RestController
@RequestMapping("order/oil/source")
public class OrderOilSourceController extends BaseController<OrderOilSource, IOrderOilSourceService> {
    @DubboReference(version = "1.0.0")
    IOrderOilSourceService orderOilSourceService;
    @Override
    public IOrderOilSourceService getService() {
        return orderOilSourceService;
    }

    /**
     * 获取司机各模式下的油[30049]
     * @return
     */
    @GetMapping("/getDriverOilForPayWay")
    public ResponseResult queryMemberBenefits(@RequestParam("userId") Long userId)  {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        OrderPaymentWayOilOut orderPaymentWayOil = orderOilSourceService.getOrderPaymentWayOil(userId, accessToken, SysStaticDataEnum.USER_TYPE.DRIVER_USER);

        return ResponseResult.success(orderPaymentWayOil);
    }
}
