package com.youming.youche.order.provider.service.order;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.order.api.order.IOrderPaymentDaysInfoVerService;
import com.youming.youche.order.commons.OrderConsts;
import com.youming.youche.order.domain.order.OrderPaymentDaysInfo;
import com.youming.youche.order.domain.order.OrderPaymentDaysInfoVer;
import com.youming.youche.order.dto.UpdateOrderPaymentDaysInfoInDto;
import com.youming.youche.order.provider.mapper.order.OrderPaymentDaysInfoVerMapper;
import com.youming.youche.order.vo.UpdateOrderPaymentDaysInfoVo;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;


/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-03-21
 */
@DubboService(version = "1.0.0")
@Service
public class OrderPaymentDaysInfoVerServiceImpl extends BaseServiceImpl<OrderPaymentDaysInfoVerMapper, OrderPaymentDaysInfoVer> implements IOrderPaymentDaysInfoVerService {


    @Override
    public List<OrderPaymentDaysInfoVer> queryOrderPaymentDaysInfoVer(Long orderId, Integer paymentDaysType, Integer isUpdate) {
        LambdaQueryWrapper<OrderPaymentDaysInfoVer> lambda= Wrappers.lambdaQuery();
        if (orderId != null && orderId > 0) {
            lambda.eq(OrderPaymentDaysInfoVer::getOrderId, orderId);
        }
        if (paymentDaysType != null && paymentDaysType > 0) {
            lambda.eq(OrderPaymentDaysInfoVer::getPaymentDaysType, paymentDaysType);
        }
        if (isUpdate != null && isUpdate > 0) {
            lambda.eq(OrderPaymentDaysInfoVer::getIsUpdate, isUpdate);
        }
        return this.list(lambda);
    }

    @Override
    public void saveOrUpdateOrderPaymentDaysInfoVer(OrderPaymentDaysInfoVer info) {
        this.saveOrUpdate(info);
    }

    @Override
    public void loseEfficacyPaymentDaysVerUpdate(Long orderId) {
        List<OrderPaymentDaysInfoVer>  list = this.queryOrderPaymentDaysInfoVer(orderId, null, OrderConsts.IS_UPDATE.UPDATE);
        for (OrderPaymentDaysInfoVer orderPaymentDaysInfoVer : list) {
            orderPaymentDaysInfoVer.setIsUpdate(OrderConsts.IS_UPDATE.NOT_UPDATE);
            this.saveOrUpdateOrderPaymentDaysInfoVer(orderPaymentDaysInfoVer);
        }
    }

    @Override
    public void setOrderPaymentDaysInfoVer(OrderPaymentDaysInfo oldObj, OrderPaymentDaysInfo newObj) {
        OrderPaymentDaysInfoVer infoVer = new OrderPaymentDaysInfoVer();
        UpdateOrderPaymentDaysInfoVo in = new UpdateOrderPaymentDaysInfoVo();
        BeanUtils.copyProperties(oldObj,infoVer);
        BeanUtils.copyProperties(newObj,in);
        BeanUtils.copyProperties(in,infoVer);
        infoVer.setOpDate(LocalDateTime.now());
        infoVer.setIsUpdate(OrderConsts.IS_UPDATE.UPDATE);
        this.saveOrUpdateOrderPaymentDaysInfoVer(infoVer);
    }
}
