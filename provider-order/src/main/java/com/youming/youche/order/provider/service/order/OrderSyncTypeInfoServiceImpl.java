package com.youming.youche.order.provider.service.order;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.util.LoginUtils;
import com.youming.youche.order.api.order.IOrderSyncTypeInfoService;
import com.youming.youche.order.commons.OrderConsts;
import com.youming.youche.order.domain.order.OrderSyncTypeInfo;
import com.youming.youche.order.provider.mapper.order.OrderSyncTypeInfoMapper;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;


/**
 * <p>
 * 订单同步表 服务实现类
 * </p>
 *
 * @author hzx
 * @since 2022-03-25
 */
@DubboService(version = "1.0.0")
public class OrderSyncTypeInfoServiceImpl extends BaseServiceImpl<OrderSyncTypeInfoMapper, OrderSyncTypeInfo> implements IOrderSyncTypeInfoService {

    @Resource
    LoginUtils loginUtils;

    @Override
    public void saveOrderSyncTypeInfo(Long orderId, Integer syncType, Integer billType, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        List<OrderSyncTypeInfo> typeInfos = this.queryOrderSyncTypeInfoList(orderId, syncType, billType, OrderConsts.TASK_TRACK_STS.NO_TRACK);
        if (typeInfos == null || typeInfos.size() == 0) {
            OrderSyncTypeInfo orderSyncTypeInfo = new OrderSyncTypeInfo();
            orderSyncTypeInfo.setBillType(billType);
            orderSyncTypeInfo.setCreateTime(getDateToLocalDateTime(new Date()));
            orderSyncTypeInfo.setOrderId(orderId);
            orderSyncTypeInfo.setSyncType(syncType);
            orderSyncTypeInfo.setOpId(loginInfo != null ? loginInfo.getId() : null);
            orderSyncTypeInfo.setTaskDisposeSts(OrderConsts.TASK_TRACK_STS.NO_TRACK);
            this.saveOrUpdate(orderSyncTypeInfo);
        }
    }

    @Override
    public List<OrderSyncTypeInfo> queryOrderSyncTypeInfoList(Long orderId, Integer syncType, Integer billType, Integer taskDisposeSts) {
        LambdaQueryWrapper<OrderSyncTypeInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(orderId != null && orderId > 0, OrderSyncTypeInfo::getOrderId, orderId);
        queryWrapper.eq(syncType != null && syncType > 0, OrderSyncTypeInfo::getSyncType, syncType);
        queryWrapper.eq(billType != null && billType > 0, OrderSyncTypeInfo::getBillType, billType);
        queryWrapper.eq(taskDisposeSts != null && taskDisposeSts > 0, OrderSyncTypeInfo::getTaskDisposeSts, taskDisposeSts);

        List<OrderSyncTypeInfo> list = this.list(queryWrapper);
        if (list != null && list.isEmpty()) {
            return list;
        }

        return null;
    }

    private LocalDateTime getDateToLocalDateTime(Date date) {
        Instant instant = date.toInstant();
        ZoneId zoneId = ZoneId.systemDefault();
        LocalDateTime localDateTime = instant.atZone(zoneId).toLocalDateTime();
        return localDateTime;
    }

}
