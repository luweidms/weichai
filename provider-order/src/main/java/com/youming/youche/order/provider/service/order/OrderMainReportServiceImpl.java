package com.youming.youche.order.provider.service.order;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.order.api.order.IOrderMainReportService;
import com.youming.youche.order.domain.order.OrderMainReport;
import com.youming.youche.order.provider.mapper.order.OrderMainReportMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;


/**
* <p>
    *  服务实现类
    * </p>
* @author liangyan
* @since 2022-03-22
*/
@DubboService(version = "1.0.0")
@Service
    public class OrderMainReportServiceImpl extends BaseServiceImpl<OrderMainReportMapper, OrderMainReport> implements IOrderMainReportService {

    @Resource
    private OrderMainReportMapper orderMainReportMapper;

    @Override
    public OrderMainReport getObjectByOrderId(long orderId) {

        QueryWrapper<OrderMainReport> orderMainReportQueryWrapper = new QueryWrapper<>();
        orderMainReportQueryWrapper.eq("order_id",orderId);
        List<OrderMainReport> orderMainReports = orderMainReportMapper.selectList(orderMainReportQueryWrapper);
        if(orderMainReports != null && orderMainReports.size() > 0){
            return orderMainReports.get(0);
        }
        return null;
    }
}
