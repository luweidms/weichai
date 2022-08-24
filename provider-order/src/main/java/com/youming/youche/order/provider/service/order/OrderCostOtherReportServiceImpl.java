package com.youming.youche.order.provider.service.order;

import com.youming.youche.order.api.order.IOrderCostOtherReportService;
import com.youming.youche.order.domain.order.OrderCostOtherReport;
import com.youming.youche.order.provider.mapper.order.OrderCostOtherReportMapper;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.order.vo.OrderCostOtherReportVO;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author xxx
 * @since 2022-03-29
 */
@DubboService(version = "1.0.0")
public class OrderCostOtherReportServiceImpl extends BaseServiceImpl<OrderCostOtherReportMapper, OrderCostOtherReport> implements IOrderCostOtherReportService {


    @Override
    public List<OrderCostOtherReportVO> getOrderCostOtherReport(Long relId, boolean b) {
        List<OrderCostOtherReportVO> list =null ;
        if(b){
            list = baseMapper.getOrderCostOtherReport(relId);
        }
        return list;
    }
}
