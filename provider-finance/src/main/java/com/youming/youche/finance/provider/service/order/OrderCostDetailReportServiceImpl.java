package com.youming.youche.finance.provider.service.order;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.util.LoginUtils;
import com.youming.youche.finance.api.order.IOrderCostDetailReportService;
import com.youming.youche.finance.domain.order.OrderCostDetailReport;
import com.youming.youche.finance.provider.mapper.order.OrderCostDetailReportMapper;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;
import java.util.List;


/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author Terry
 * @since 2022-03-09
 */
@DubboService(version = "1.0.0")
public class OrderCostDetailReportServiceImpl extends BaseServiceImpl<OrderCostDetailReportMapper, OrderCostDetailReport> implements IOrderCostDetailReportService {
    @Resource
    LoginUtils loginUtils;

    @Override
    public List<OrderCostDetailReport> getOrderCostDetailReportByOrderId(Long orderId, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);;//当前用户信息
        QueryWrapper<OrderCostDetailReport> wrapper = new QueryWrapper<>();
        wrapper.eq("order_id",orderId)
                .eq("tenant_id",loginInfo.getTenantId());
        List<OrderCostDetailReport> orderCostDetailReports = baseMapper.selectList(wrapper);
        return orderCostDetailReports;
    }
}
