package com.youming.youche.order.provider.service.order;

import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.conts.EnumConsts;
import com.youming.youche.order.api.order.IOilCardLogService;
import com.youming.youche.order.api.order.IOrderSchedulerService;
import com.youming.youche.order.domain.order.OilCardLog;
import com.youming.youche.order.domain.order.OrderScheduler;
import com.youming.youche.order.provider.mapper.order.OilCardLogMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;


/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-03-22
 */
@DubboService(version = "1.0.0")
@Service
public class OilCardLogServiceImpl extends BaseServiceImpl<OilCardLogMapper, OilCardLog> implements IOilCardLogService {
    @Lazy
    @Autowired
    private IOrderSchedulerService orderSchedulerService;

    @Override
    public void saveCardLog(Long cardId, Long balance, String logDesc, Long orderId, LoginInfo baseUser) {
        OilCardLog oilCardLog = new OilCardLog();
        oilCardLog.setCardId(cardId);
        oilCardLog.setLogDesc(logDesc);
        oilCardLog.setTenantId(baseUser.getTenantId());
        oilCardLog.setUserId(baseUser.getId());
        oilCardLog.setOilFee(balance);
        if(orderId!=null&&orderId>0) {
            oilCardLog.setOrderId(orderId);
            OrderScheduler orderScheduler = orderSchedulerService.getOrderScheduler(orderId);
            if(orderScheduler!=null) {
                oilCardLog.setPlateNumber(orderScheduler.getPlateNumber());
                oilCardLog.setCarDriverMan(orderScheduler.getCarDriverMan());
            }
        }
        oilCardLog.setLogType(EnumConsts.OIL_LOG_TYPE.ADD_OR_REDUCE);
        oilCardLog.setLogDate(LocalDateTime.now());
        oilCardLog.setCreateTime(LocalDateTime.now());
        this.saveOrUpdate(oilCardLog);
    }

    @Override
    public void saveOrdUpdate(OilCardLog oilCardLog, Integer type) {
        oilCardLog.setLogType(type);
        oilCardLog.setLogDate(LocalDateTime.now());
        oilCardLog.setCreateTime(LocalDateTime.now());
        this.saveOrUpdate(oilCardLog);
    }
}
