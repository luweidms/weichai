//package com.youming.youche.order.provider.service.order;
//
//import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
//import com.baomidou.mybatisplus.core.toolkit.Wrappers;
//import com.youming.youche.commons.base.BaseServiceImpl;
//import com.youming.youche.order.api.order.IUserReceiverInfoService;
//import com.youming.youche.order.domain.order.UserReceiverInfo;
//import com.youming.youche.order.provider.mapper.order.UserReceiverInfoMapper;
//import org.apache.dubbo.config.annotation.DubboService;
//import org.springframework.stereotype.Service;
//
//
///**
// * <p>
// * 收款人(代收人) 服务实现类
// * </p>
// *
// * @author liangyan
// * @since 2022-04-20
// */
//@DubboService(version = "1.0.0")
//@Service
//public class UserReceiverInfoServiceImpl extends BaseServiceImpl<UserReceiverInfoMapper, UserReceiverInfo> implements IUserReceiverInfoService {
//
//
//    @Override
//    public UserReceiverInfo getUserReceiverInfoByUserId(Long id) {
//        LambdaQueryWrapper<UserReceiverInfo> lambda= Wrappers.lambdaQuery();
//        lambda.eq(UserReceiverInfo::getUserId,id);
//        return this.getOne(lambda);
//    }
//}
