package com.youming.youche.order.provider.service.order;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.order.api.order.IOrderAccountOilSourceService;
import com.youming.youche.order.domain.order.OrderAccountOilSource;
import com.youming.youche.order.provider.mapper.order.OrderAccountOilSourceMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-03-22
 */
@DubboService(version = "1.0.0")
@Service
public class OrderAccountOilSourceServiceImpl extends BaseServiceImpl<OrderAccountOilSourceMapper, OrderAccountOilSource> implements IOrderAccountOilSourceService {


    @Override
    public OrderAccountOilSource getOrderAccountOilSource(Long accId, Long userId, Long tenantId, Integer userType) {
        LambdaQueryWrapper<OrderAccountOilSource> lambda= Wrappers.lambdaQuery();
        lambda.eq(OrderAccountOilSource::getAccId,accId)
              .eq(OrderAccountOilSource::getUserId,userId)
              .eq(OrderAccountOilSource::getTenantId,tenantId);
        if(userType != null && userType > 0){
            lambda.eq(OrderAccountOilSource::getUserType, userType);
        }
        return this.getOne(lambda);
    }

    @Override
    public OrderAccountOilSource createOrderAccountOilSource(Long parentId, Long accId, Long userId,
                                                             Long oilBalance, Long rechargeOilBalance,
                                                             Long tenantId, Integer userType, LoginInfo baseUser) {
        OrderAccountOilSource oaos = new OrderAccountOilSource();
        oaos.setParentId(parentId);
        oaos.setAccId(accId);
        oaos.setUserId(userId);
        //会员体系改造开始
        oaos.setUserType(userType);
        //会员体系改造结束
        oaos.setOilBalance(oilBalance);
        oaos.setRechargeOilBalance(rechargeOilBalance);
        oaos.setTenantId(tenantId);
        oaos.setOilBalance(oilBalance);
        oaos.setRechargeOilBalance(rechargeOilBalance);
        oaos.setCreateTime(LocalDateTime.now());
        oaos.setUpdateTime(LocalDateTime.now());
        if (baseUser != null) {
            oaos.setOpId(baseUser.getId());
            oaos.setUpdateOpId(baseUser.getId());
        }
        return oaos;
    }

    @Override
    public void clearOilBalance(Long userId, Long tenantId) {

        LambdaUpdateWrapper<OrderAccountOilSource> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(OrderAccountOilSource::getRechargeOilBalance, 0);
        updateWrapper.set(OrderAccountOilSource::getOilBalance, 0);
        updateWrapper.set(OrderAccountOilSource::getUpdateTime, LocalDateTime.now());
        updateWrapper.eq(OrderAccountOilSource::getTenantId, tenantId);
        updateWrapper.eq(OrderAccountOilSource::getUserId, userId);
        updateWrapper.eq(OrderAccountOilSource::getUserType, 3);

        this.update(updateWrapper);
    }

}
