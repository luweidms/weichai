package com.youming.youche.order.provider.service.order;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.order.api.order.IOrderOilDepotSchemeVerService;
import com.youming.youche.order.domain.order.OrderOilDepotSchemeVer;
import com.youming.youche.order.provider.mapper.order.OrderOilDepotSchemeVerMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;

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
public class OrderOilDepotSchemeVerServiceImpl extends BaseServiceImpl<OrderOilDepotSchemeVerMapper, OrderOilDepotSchemeVer> implements IOrderOilDepotSchemeVerService {


    @Override
    public List<OrderOilDepotSchemeVer> getOrderOilDepotSchemeVerByOrderId(Long orderId, Boolean isUpdate, LoginInfo user) {
        List<OrderOilDepotSchemeVer> list = null;
        LambdaQueryWrapper<OrderOilDepotSchemeVer> lambda= Wrappers.lambdaQuery();
        lambda.eq(OrderOilDepotSchemeVer::getOrderId, orderId);
        if (isUpdate) {
            lambda.eq(OrderOilDepotSchemeVer::getIsUpdate, 1);//查询已修改的
        }
        lambda.eq(OrderOilDepotSchemeVer::getTenantId, user.getTenantId());
        lambda.orderByAsc(OrderOilDepotSchemeVer::getCreateTime);
        list = this.list(lambda);
        return list;
    }

    @Override
    public void saveOrderOilDepotSchemeVer(OrderOilDepotSchemeVer schemeVer) {
        this.saveOrUpdate(schemeVer);
    }

    @Override
    public void updateSchemeVerIsUpdate(Long orderId, int isUpdate,LoginInfo user) {
        List<OrderOilDepotSchemeVer>  list = this.getOrderOilDepotSchemeVerByOrderId(orderId, true,user);
        if (list != null && list.size() >0 ) {
            for (OrderOilDepotSchemeVer schemeVer : list) {
                schemeVer.setIsUpdate(isUpdate);
                this.saveOrderOilDepotSchemeVer(schemeVer);
            }
        }
    }
}
