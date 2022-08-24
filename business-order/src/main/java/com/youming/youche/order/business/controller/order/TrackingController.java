//package com.youming.youche.order.business.controller.order;
//
//
//import com.youming.youche.commons.base.BaseController;
//import com.youming.youche.commons.response.ResponseResult;
//import com.youming.youche.commons.web.Header;
//import com.youming.youche.order.api.order.IOrderDriverSwitchInfoService;
//import com.youming.youche.order.domain.order.OrderDriverSwitchInfo;
//import org.apache.dubbo.config.annotation.DubboReference;
//import org.springframework.web.bind.annotation.*;
//
///**
// * <p>
// * 前端控制器
// * </p>
// *
// * @author CaoYaJie
// * @since 2022-03-24
// */
//@RestController
//@RequestMapping("order/tracking")
//public class TrackingController extends BaseController<OrderDriverSwitchInfo, IOrderDriverSwitchInfoService> {
//
//    @DubboReference(version = "1.0.0")
//    IOrderDriverSwitchInfoService orderDriverSwitchInfoService;
//
//
//    @Override
//    public IOrderDriverSwitchInfoService getService() {
//        return orderDriverSwitchInfoService;
//    }
//
//    /**
//     * @author 卢威
//     * 切换司机分页列表查询
//     * @date 16:48 2022/2/25
//     * @Param []
//     */
//    @GetMapping("/doQuery")
//    public ResponseResult doQuery(@RequestParam(value = "orderId", required = false) Long orderId, @RequestParam(value = "receiveUserName", required = false) String receiveUserName,
//                                  @RequestParam(value = "formerUserName", required = false) String formerUserName, @RequestParam(value = "originUserName", required = false) String originUserName, @RequestParam(value = "state", required = false) Integer state, @RequestParam(value = "pageNum", defaultValue = "1", required = false) Integer pageNum,
//                                  @RequestParam(value = "pageSize", defaultValue = "20", required = false) Integer pageSize) {
//        return ResponseResult.success(orderDriverSwitchInfoService.getOrderDriverSwitchInfos(orderId, formerUserName, receiveUserName, originUserName, state, pageNum, pageSize));
//    }
//
//    /**
//     * @author 卢威
//     * 切换司机详情查询
//     * @date 16:48 2022/2/25
//     * @Param []
//     */
//    @GetMapping("/queryDetails")
//    public ResponseResult queryDetails(@RequestParam("switchId") Long switchId) {
//        return ResponseResult.success(orderDriverSwitchInfoService.queryDetails(switchId));
//    }
//
//    /**
//     * @author 卢威
//     * 切换司机详情查询
//     * @date 16:48 2022/2/25
//     * @Param []
//     */
//    @GetMapping("/getDriverByPhone")
//    public ResponseResult getDriverByPhone(@RequestParam("billId") String billId) {
//        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
//        return ResponseResult.success(orderDriverSwitchInfoService.getDriverByPhone(billId, accessToken));
//    }
//
//    /**
//     * @author 卢威
//     * 切换司机
//     * @date 16:48 2022/2/25
//     * @Param []
//     */
//    @PostMapping("/doDriverSwitch")
//    public ResponseResult doDriverSwitch(@RequestParam(value = "orderId", required = false) Long orderId,
//                                         @RequestParam(value = "receiveUserId", required = false) Long receiveUserId,
//                                         @RequestParam(value = "recevieMileage", required = false) String recevieMileage,
//                                         @RequestParam(value = "mileageFileId", required = false) String mileageFileId,
//                                         @RequestParam(value = "mileageFileUrl", required = false) String mileageFileUrl,
//                                         @RequestParam(value = "receiveRemark", required = false) String receiveRemark) {
//        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
//        orderDriverSwitchInfoService.doDriverSwitch(orderId, receiveUserId, recevieMileage, mileageFileId, mileageFileUrl, receiveRemark, accessToken);
//        return ResponseResult.success("切换成功");
//    }
//
//}
