package com.youming.youche.order.provider.service.order;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.order.api.order.IOrderOilDepotSchemeService;
import com.youming.youche.order.domain.order.OrderOilDepotScheme;
import com.youming.youche.order.domain.order.OrderOilDepotSchemeVer;
import com.youming.youche.order.provider.mapper.order.OrderOilDepotSchemeMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * <p>
 * 订单油站分配表 服务实现类
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-03-17
 */
@DubboService(version = "1.0.0")
@Service
public class OrderOilDepotSchemeServiceImpl extends BaseServiceImpl<OrderOilDepotSchemeMapper, OrderOilDepotScheme> implements IOrderOilDepotSchemeService {


    @Override
    public List<OrderOilDepotScheme> getOrderOilDepotSchemeByOrderId(Long orderId, Boolean isUpdate, LoginInfo user) {
        List<OrderOilDepotScheme> list = null;
        LambdaQueryWrapper<OrderOilDepotScheme> lambda=new QueryWrapper<OrderOilDepotScheme>().lambda();
        lambda.eq(OrderOilDepotScheme::getOrderId, orderId);
        if (isUpdate) {
            lambda.eq(OrderOilDepotScheme::getIsUpdate, 1);//查询已修改的
        }
        lambda.eq(OrderOilDepotScheme::getTenantId, user.getTenantId());
        lambda.orderByAsc(OrderOilDepotScheme::getCreateTime);
        list = this.list(lambda);
        return list;
    }

    @Override
    public String deleteOrderOilDepotSchem(Long orderId) {
        if (orderId ==null || orderId <= 0) {
            throw new BusinessException("订单ID不存在!");
        }
        LambdaQueryWrapper<OrderOilDepotScheme> lambda=new QueryWrapper<OrderOilDepotScheme>().lambda();
        lambda.eq(OrderOilDepotScheme::getOrderId,orderId);
        this.remove(lambda);
        return "Y";
    }


}
