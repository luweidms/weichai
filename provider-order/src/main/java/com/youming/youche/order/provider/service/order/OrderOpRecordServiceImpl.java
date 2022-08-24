package com.youming.youche.order.provider.service.order;

import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.util.LoginUtils;
import com.youming.youche.order.api.order.IOrderOpRecordService;
import com.youming.youche.order.domain.order.OrderOpRecord;
import com.youming.youche.order.provider.mapper.order.OrderOpRecordMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;


/**
 * <p>
 * 订单变更操作记录表 服务实现类
 * </p>
 *
 * @author liangyan
 * @since 2022-03-23
 */
@DubboService(version = "1.0.0")
@Service
public class OrderOpRecordServiceImpl extends BaseServiceImpl<OrderOpRecordMapper, OrderOpRecord> implements IOrderOpRecordService {

    @Resource
    private LoginUtils loginUtils;

    @Override
    public void saveOrUpdate(Long orderId, int opType,String accessToken) {
        LoginInfo user = loginUtils.get(accessToken);
        OrderOpRecord orderOpRecord = new OrderOpRecord();
        orderOpRecord.setOrderId(orderId);
        orderOpRecord.setOpType(opType);
        orderOpRecord.setOpId(user.getId());
        orderOpRecord.setTenantId(user.getTenantId());
        orderOpRecord.setUpdateTime(LocalDateTime.now());
        this.saveOrUpdate(orderOpRecord);
    }
}
