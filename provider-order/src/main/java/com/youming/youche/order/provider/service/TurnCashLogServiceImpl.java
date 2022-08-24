package com.youming.youche.order.provider.service;

import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.util.LoginUtils;
import com.youming.youche.order.api.ITurnCashLogService;
import com.youming.youche.order.domain.TurnCashLog;
import com.youming.youche.order.provider.mapper.TurnCashLogMapper;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Date;


/**
* <p>
    *  服务实现类
    * </p>
* @author wuhao
* @since 2022-04-25
*/
@DubboService(version = "1.0.0")
    public class TurnCashLogServiceImpl extends BaseServiceImpl<TurnCashLogMapper, TurnCashLog> implements ITurnCashLogService {

    @Resource
    LoginUtils loginUtils;

    @Override
    public TurnCashLog createTurnCashLog(Long userId, Long batchId, Long balance, Long marginBalance, Long oilBalance,
                                         Long etcBalance, Long orderOil, Long orderEtc, Long turnBalance,
                                         Long turnDiscountDouble, String turnType, String turnMonth,
                                         String vehicleAffiliation, Long tenantId, Integer userType,String accessToken) {
        //操作日志
        LoginInfo user = loginUtils.get(accessToken);
        TurnCashLog turnCashLog = new TurnCashLog();
        turnCashLog.setUserId(userId);
        //会员体系改造开始
        turnCashLog.setUserType(userType);
        //会员体系改造结束
        turnCashLog.setBatchId(batchId);
        turnCashLog.setBalance(balance);
        turnCashLog.setMarginBalance(marginBalance);
        turnCashLog.setOilBalance(oilBalance);
        turnCashLog.setEtcBalance(etcBalance);
        turnCashLog.setOrderOilBalance(orderOil);
        turnCashLog.setOrderEtcBalance(orderEtc);
        turnCashLog.setTurnBalance(turnBalance);
        turnCashLog.setTurnDiscount(turnDiscountDouble);
        turnCashLog.setTurnType(Integer.parseInt(turnType));
        turnCashLog.setTurnMonth(turnMonth);
        turnCashLog.setVehicleAffiliation(vehicleAffiliation);
        turnCashLog.setTenantId(tenantId);
        turnCashLog.setCreateTime(LocalDateTime.now());
        turnCashLog.setOpDate(LocalDateTime.now());
        if (user != null) {
            turnCashLog.setOpUserId(user.getId());
            turnCashLog.setOpUserName(user.getName());
            turnCashLog.setTenantId(user.getTenantId());
        }
        return turnCashLog;
    }
}
