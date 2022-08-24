package com.youming.youche.order.business.controller.order;


import com.youming.youche.commons.base.BaseController;
import com.youming.youche.commons.response.ResponseResult;
import com.youming.youche.commons.web.Header;
import com.youming.youche.order.api.order.IOrderSchedulerService;
import com.youming.youche.order.domain.order.OrderScheduler;
import com.youming.youche.order.dto.CheckConflictDto;
import com.youming.youche.order.vo.CheckConflictVo;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * <p>
 * 订单调度表 前端控制器
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-03-15
 */
@RestController
@RequestMapping("orderScheduler/info")
public class OrderSchedulerController extends BaseController<OrderScheduler, IOrderSchedulerService> {
    @DubboReference(version = "1.0.0")
    IOrderSchedulerService orderSchedulerService;
    @Override
    public IOrderSchedulerService getService() {
        return orderSchedulerService;
    }

    /**
     * 校验车辆、主驾、副驾是否冲突
     * @return
     * @throws Exception
     */
    @GetMapping("/checkDependTimeConflict")
    public ResponseResult checkDependTimeConflict(CheckConflictDto checkConflictDto) {
        CheckConflictVo checkConflictVo = orderSchedulerService.checkDependTimeConflict(checkConflictDto);
        return ResponseResult.success(checkConflictVo);
    }

    /**
     * 校验车辆上一单是否勾选满油操作完成切换其他模式
     * @return
     * @throws Exception
     */
    @GetMapping("/checkPayMentWaySwitchover")
    public ResponseResult checkPayMentWaySwitchover(String dependTime,String plateNumber) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));

        Boolean aBoolean = orderSchedulerService.checkPayMentWaySwitchover(plateNumber, dependTime, accessToken, null);
        return ResponseResult.success(aBoolean);
    }

    /**
     * 检验付款方式切换-WX接口[30048]
     */
    @GetMapping("/checkPayMentWaySwitchoverWX")
    public ResponseResult checkPayMentWaySwitchoverWX(String dependTime,String plateNumber, Long orderId) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));

        Boolean aBoolean = orderSchedulerService.checkPayMentWaySwitchover(plateNumber, dependTime, accessToken, orderId);
        return ResponseResult.success(aBoolean);
    }

    /**
     * 订单录入-所选司机或车辆有未完成的订单,提交订单时要有提示
     * @return
     * @throws Exception
     */
    @GetMapping("/judgeVehicleOrder")
    public ResponseResult judgeVehicleOrder(String plateNumbers,Long userId) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));

        orderSchedulerService.judgeVehicleOrder(plateNumbers, userId, accessToken);
        return ResponseResult.success();
    }

    /**
     * 获取订单调度信息 30063
     */
    @GetMapping("getOrderSchedulerByOrderId")
    public ResponseResult getOrderSchedulerByOrderId(Long orderId) {
        OrderScheduler orderScheduler = orderSchedulerService.getOrderSchedulerByOrderIdWX(orderId);
        return ResponseResult.success(orderScheduler);
    }
}
