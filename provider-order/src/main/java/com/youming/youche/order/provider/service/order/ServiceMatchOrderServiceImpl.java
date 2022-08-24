package com.youming.youche.order.provider.service.order;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.conts.OrderAccountConst;
import com.youming.youche.conts.SysStaticDataEnum;
import com.youming.youche.order.api.order.IServiceMatchOrderService;
import com.youming.youche.order.domain.order.ServiceMatchOrder;
import com.youming.youche.order.provider.mapper.order.ServiceMatchOrderMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;


/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author liangyan
 * @since 2022-03-23
 */
@DubboService(version = "1.0.0")
@Service
public class ServiceMatchOrderServiceImpl extends BaseServiceImpl<ServiceMatchOrderMapper, ServiceMatchOrder> implements IServiceMatchOrderService {

    @Resource
    private ServiceMatchOrderMapper serviceMatchOrderMapper;

    @Override
    public List<ServiceMatchOrder> getServiceMatchOrderByOtherFlowId(Long otherFlowId, Long userId, String vehicleAffiliation, String oilAffiliation, Long tenantId, int serviceType) {
        QueryWrapper<ServiceMatchOrder> serviceMatchOrderQueryWrapper = new QueryWrapper<>();
        serviceMatchOrderQueryWrapper.eq("other_flow_id",otherFlowId)
                .eq("service_user_id",userId)
                .eq("vehicle_affiliation",vehicleAffiliation)
                .eq("oil_affiliation",oilAffiliation)
                .eq("service_type",serviceType)
                .eq("tenant_id",tenantId);
        List<ServiceMatchOrder> serviceMatchOrders = serviceMatchOrderMapper.selectList(serviceMatchOrderQueryWrapper);
        return serviceMatchOrders;
    }

    @Override
    public List<ServiceMatchOrder> getServiceMatchOrder(List<Long> orderId, Date beginDate, Date endDate, Long userId, Long tenantId) {
        LambdaQueryWrapper<ServiceMatchOrder> qw = new LambdaQueryWrapper<>();
        qw.eq(ServiceMatchOrder::getUserId, userId);
        if (tenantId != null && tenantId.longValue() > 0) {
            qw.eq(ServiceMatchOrder::getTenantId, tenantId);
        }
        qw.eq(ServiceMatchOrder::getServiceType, SysStaticDataEnum.SERVICE_BUSI_TYPE.OIL);
        if (beginDate != null) {
            qw.gt(ServiceMatchOrder::getCreateTime,beginDate);
        }
        if (endDate != null) {
            qw.le(ServiceMatchOrder::getCreateTime, endDate);
        }
        return this.list(qw);
    }

    @Override
    public List<ServiceMatchOrder> getHaServiceMatchOrder(Long userId) {

        QueryWrapper<ServiceMatchOrder> wrapper = new QueryWrapper<>();
        wrapper.eq("service_user_id",userId)
                .ne("oil_affiliation", OrderAccountConst.VEHICLE_AFFILIATION.VEHICLE_AFFILIATION0)
                .ne("oil_affiliation", OrderAccountConst.VEHICLE_AFFILIATION.VEHICLE_AFFILIATION1)
                .gt("account_balance",0L)
                .orderByAsc("create_date");
        List<ServiceMatchOrder> serviceMatchOrders = baseMapper.selectList(wrapper);
        return serviceMatchOrders;
    }

    @Override
    public List<ServiceMatchOrder> getServiceMatchOrder(Long serviceUserId, int state, String vehicleAffiliation, String oilAffiliation, Long tenantId) {
      QueryWrapper<ServiceMatchOrder> wrapper = new QueryWrapper<>();
      wrapper.eq("service_user_id",serviceUserId)
              .eq("state",state)
              .gt("account_balance",0l)
              .eq("vehicle_affiliation",vehicleAffiliation);
        if(StringUtils.isNoneBlank(oilAffiliation)){
          wrapper.eq("oil_affiliation", oilAffiliation);
        }
        wrapper.eq("tenant_id",tenantId);
        List<ServiceMatchOrder> serviceMatchOrders = baseMapper.selectList(wrapper);
        return serviceMatchOrders;
    }
}
