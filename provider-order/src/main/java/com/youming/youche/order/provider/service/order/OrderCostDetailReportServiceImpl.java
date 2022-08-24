package com.youming.youche.order.provider.service.order;

import com.youming.youche.order.api.order.IOrderCostDetailReportService;
import com.youming.youche.order.domain.order.OrderCostDetailReport;
import com.youming.youche.order.provider.mapper.order.OrderCostDetailReportMapper;
import com.youming.youche.commons.base.BaseServiceImpl;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;


/**
* <p>
    *  服务实现类
    * </p>
* @author xxx
* @since 2022-03-29
*/
@DubboService(version = "1.0.0")
    public class OrderCostDetailReportServiceImpl extends BaseServiceImpl<OrderCostDetailReportMapper, OrderCostDetailReport> implements IOrderCostDetailReportService {


    }
