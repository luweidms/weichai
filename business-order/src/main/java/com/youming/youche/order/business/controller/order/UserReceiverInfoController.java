//package com.youming.youche.order.business.controller.order;
//
//
//import com.youming.youche.commons.base.BaseController;
//import com.youming.youche.order.api.order.IUserReceiverInfoService;
//import com.youming.youche.order.domain.order.UserReceiverInfo;
//import org.apache.dubbo.config.annotation.DubboReference;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
///**
// * <p>
// * 收款人(代收人) 前端控制器
// * </p>
// *
// * @author liangyan
// * @since 2022-04-20
// */
//@RestController
//@RequestMapping("user-receiver-info")
//public class UserReceiverInfoController extends BaseController<UserReceiverInfo, IUserReceiverInfoService> {
//    @DubboReference(version = "1.0.0")
//    IUserReceiverInfoService userReceiverInfoService;
//    @Override
//    public IUserReceiverInfoService getService() {
//        return userReceiverInfoService;
//    }
//}
