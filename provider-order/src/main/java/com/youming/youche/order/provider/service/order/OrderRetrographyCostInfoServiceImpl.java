package com.youming.youche.order.provider.service.order;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.order.api.order.IOrderCostReportService;
import com.youming.youche.order.api.order.IOrderRetrographyCostInfoService;
import com.youming.youche.order.commons.OrderConsts;
import com.youming.youche.order.domain.OrderRetrographyCostInfo;
import com.youming.youche.order.domain.order.OrderCostReport;
import com.youming.youche.order.provider.mapper.order.OrderRetrographyCostInfoMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;


/**
 * <p>
 * 订单校准里程表 服务实现类
 * </p>
 *
 * @author chenzhe
 * @since 2022-03-20
 */
@DubboService(version = "1.0.0")
@Service
public class OrderRetrographyCostInfoServiceImpl extends BaseServiceImpl<OrderRetrographyCostInfoMapper, OrderRetrographyCostInfo> implements IOrderRetrographyCostInfoService {

    @Resource
    IOrderCostReportService iOrderCostReportService;

    @Override
    public OrderRetrographyCostInfo queryOrderRetrographyCostInfoByCheck(Long orderId) {
        OrderRetrographyCostInfo orderRetrographyCostInfo = queryOrderRetrographyCostInfo(orderId);
        if (orderRetrographyCostInfo == null) {
            orderRetrographyCostInfo = new OrderRetrographyCostInfo();
        }
        //查找校准里程
        List<OrderCostReport> list = iOrderCostReportService.getOrderCostReportByOrderId(orderId);
        if (list != null && list.size() > 0) {
            OrderCostReport orderCostReport = list.get(0);
            if (OrderConsts.ORDER_COST_REPORT.AUDIT_PASS == orderCostReport.getState()) {
                if (orderCostReport.getLoadMileage() != null && orderCostReport.getCapacityLoadMileage() != null) {
                    orderRetrographyCostInfo.setCheckRunDistance(orderCostReport.getLoadMileage().longValue());
                    orderRetrographyCostInfo.setCheckEmptyRunDistance(orderCostReport.getCapacityLoadMileage().longValue());
                    orderRetrographyCostInfo.setCheckTime(orderCostReport.getCheckTime());
                }
                if (orderCostReport.getUnloadingKm() == null && orderCostReport.getLoadingKm() == null) {
                    orderRetrographyCostInfo.setRunDistance(orderCostReport.getEndKm() - orderCostReport.getStartKm());
                    orderRetrographyCostInfo.setEmptyRunDistance(0L);
                } else if (orderCostReport.getUnloadingKm() == null) {
                    orderRetrographyCostInfo.setEmptyRunDistance(orderCostReport.getLoadingKm() - orderCostReport.getStartKm());
                    orderRetrographyCostInfo.setRunDistance(orderCostReport.getEndKm() - orderCostReport.getLoadingKm());
                } else if (orderCostReport.getLoadingKm() == null) {
                    orderRetrographyCostInfo.setRunDistance(orderCostReport.getUnloadingKm() - orderCostReport.getStartKm());
                    orderRetrographyCostInfo.setEmptyRunDistance(orderCostReport.getEndKm() - orderCostReport.getUnloadingKm());
                } else {
                    orderRetrographyCostInfo.setRunDistance(orderCostReport.getUnloadingKm() - orderCostReport.getLoadingKm());
                    orderRetrographyCostInfo.setEmptyRunDistance(orderCostReport.getEndKm() - orderCostReport.getStartKm() - orderRetrographyCostInfo.getRunDistance());
                }

            }
        }
        return orderRetrographyCostInfo;
    }

    @Override
    public OrderRetrographyCostInfo queryOrderRetrographyCostInfo(Long orderId) {

        LambdaQueryWrapper<OrderRetrographyCostInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderRetrographyCostInfo::getOrderId, orderId);

        List<OrderRetrographyCostInfo> list = this.list(queryWrapper);
        if (list != null && list.size() > 0) {
            return list.get(0);
        } else {
            return null;
        }

    }


}
