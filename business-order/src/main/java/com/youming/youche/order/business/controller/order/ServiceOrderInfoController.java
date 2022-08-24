package com.youming.youche.order.business.controller.order;


import com.youming.youche.commons.base.BaseController;
import com.youming.youche.commons.response.ResponseResult;
import com.youming.youche.commons.web.Header;
import com.youming.youche.order.api.order.other.IServiceOrderInfoService;
import com.youming.youche.order.domain.order.ServiceOrderInfo;
import com.youming.youche.order.dto.order.QueryServiceOrderInfoDetailsDto;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author wuhao
 * @since 2022-05-18
 */
@RestController
@RequestMapping("service/order/info")
public class ServiceOrderInfoController extends BaseController<ServiceOrderInfo, IServiceOrderInfoService> {

    @DubboReference(version = "1.0.0")
    IServiceOrderInfoService iServiceOrderInfoService;

    @Override
    public IServiceOrderInfoService getService() {
        return iServiceOrderInfoService;
    }

    /**
     * 查询扫码加油支付详情
     * 40038
     */
    @GetMapping("zhaoYouPayDetails")
    public ResponseResult zhaoYouPayDetails(String orderId) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));

        return ResponseResult.success(
                iServiceOrderInfoService.zhaoYouPayDetails(orderId, accessToken)
        );

    }

    /**
     * 接口编号：40039
     * 扫码加油获取司机相关车辆以及车辆归属车队
     */
    @GetMapping("getAddOilVehiclesAndTenant")
    public ResponseResult getAddOilVehiclesAndTenant() {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));

        return ResponseResult.success(
                iServiceOrderInfoService.getAddOilVehiclesAndTenant(accessToken)
        );

    }

    /**
     * 加油记录(新)-- 40040
     */
    @GetMapping("queryServiceOrderInfoList")
    public ResponseResult queryServiceOrderInfoList(@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                                    @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));

        return ResponseResult.success(
                iServiceOrderInfoService.queryServiceOrderInfoList(accessToken, pageNum, pageSize)
        );

    }


    /**
     * 加油评价接口-App接口 40042
     */
    @PostMapping("evaluateServiceOrderInfo")
    public ResponseResult evaluateServiceOrderInfo(Integer evaluateService, Integer evaluateQuality, Integer evaluatePrice, Long serviceOrderId) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        iServiceOrderInfoService.evaluateServiceOrderInfo(evaluateService, evaluateQuality, evaluatePrice, serviceOrderId, accessToken);
        return ResponseResult.success();
    }

    /**
     * 加油记录详情-App接口  40041
     */
    @GetMapping("queryServiceOrderInfoDetails")
    public ResponseResult queryServiceOrderInfoDetails(Long serviceOrderId) {
        QueryServiceOrderInfoDetailsDto dto = iServiceOrderInfoService.queryServiceOrderInfoDetails(serviceOrderId);
        return ResponseResult.success(dto);
    }
}
