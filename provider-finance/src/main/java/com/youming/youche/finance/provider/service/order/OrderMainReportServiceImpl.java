package com.youming.youche.finance.provider.service.order;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.util.LoginUtils;
import com.youming.youche.commons.util.RedisUtil;
import com.youming.youche.commons.web.BaseUser;
import com.youming.youche.conts.AuditConsts;
import com.youming.youche.finance.api.order.IOrderMainReportService;
import com.youming.youche.finance.domain.order.OrderMainReport;
import com.youming.youche.finance.provider.mapper.order.OrderMainReportMapper;
import com.youming.youche.system.api.audit.IAuditOutService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * <p>
 * 服务实现类
 * 聂杰伟
 * 订单成本上报主表
 * </p>
 *
 * @author Terry
 * @since 2022-03-08
 */
@DubboService(version = "1.0.0")
public class OrderMainReportServiceImpl extends BaseServiceImpl<OrderMainReportMapper, OrderMainReport> implements IOrderMainReportService {


    @Resource
    LoginUtils loginUtils;

    @DubboReference(version = "1.0.0")
    IAuditOutService iAuditOutService;

    @Override
    public Long getOrderCostReportAuditCount(String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        Long userId = loginInfo.getUserInfoId();
        LambdaQueryWrapper<OrderMainReport> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(OrderMainReport::getState, 2, 3);
        queryWrapper.in(OrderMainReport::getTenantId, loginInfo.getTenantId());

        List<Long> lids = iAuditOutService.getBusiIdByUserId(AuditConsts.AUDIT_CODE.ORDER_COST_REPORT, userId, loginInfo.getTenantId());
        if (lids != null && lids.size() > 0) {
            queryWrapper.in(OrderMainReport::getId, lids);
        } /*else {
            queryWrapper.eq(OrderMainReport::getId, null);
        }*/
        return Long.valueOf(this.count(queryWrapper));
    }

}
