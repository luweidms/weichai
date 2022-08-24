package com.youming.youche.finance.provider.service.order;

import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.finance.api.order.IOrderCostOtherReportService;
import com.youming.youche.finance.domain.order.OrderCostOtherReport;
import com.youming.youche.finance.provider.mapper.order.OrderCostOtherReportMapper;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;
import java.util.List;


/**
 * <p>
 *  服务实现类
 * </p>
 * @author Terry
 * @since 2022-03-09
 */
@DubboService(version = "1.0.0")
public class OrderCostOtherReportServiceImpl extends BaseServiceImpl<OrderCostOtherReportMapper, OrderCostOtherReport> implements IOrderCostOtherReportService {

    @Resource
    OrderCostOtherReportMapper orderCostOtherReportMapper;

    @Override
    public List<OrderCostOtherReport> getOrderCostOtherReport(long relId) {
        return null;
    }

    @Override
    public List<OrderCostOtherReport> selectFee(Long id,Long tenantId,String beginDate, String endDate) {
        List<OrderCostOtherReport> orderCostOtherReport = orderCostOtherReportMapper.selectFee(id,tenantId,beginDate,endDate);
        return orderCostOtherReport;
    }
}
