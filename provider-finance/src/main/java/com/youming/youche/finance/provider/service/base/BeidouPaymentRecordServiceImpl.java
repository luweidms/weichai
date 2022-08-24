package com.youming.youche.finance.provider.service.base;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.conts.OrderAccountConst;
import com.youming.youche.finance.api.base.IBeidouPaymentRecordService;
import com.youming.youche.finance.constant.OrderConsts;
import com.youming.youche.finance.domain.base.BeidouPaymentRecord;
import com.youming.youche.finance.domain.munual.PayoutIntf;
import com.youming.youche.finance.provider.mapper.base.BeidouPaymentRecordMapper;
import com.youming.youche.record.api.vehicle.IVehicleDataInfoService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;


/**
 * <p>
 * 中交北斗缴费记录 服务实现类
 * </p>
 *
 * @author hzx
 * @since 2022-04-18
 */
@DubboService(version = "1.0.0")
public class BeidouPaymentRecordServiceImpl extends BaseServiceImpl<BeidouPaymentRecordMapper, BeidouPaymentRecord> implements IBeidouPaymentRecordService {

    @DubboReference(version = "1.0.0")
    IVehicleDataInfoService iVehicleDataInfoService;

    @Override
    public BeidouPaymentRecord queryBeidouPaymentRecordByBusiCode(String busiCode) {
        LambdaQueryWrapper<BeidouPaymentRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BeidouPaymentRecord::getBusiCode, busiCode);
        queryWrapper.eq(BeidouPaymentRecord::getState, OrderAccountConst.BEIDOU_PAYMENT.STATE1);
        List<BeidouPaymentRecord> list = this.list();
        if (list != null && list.size() > 0) {
            return list.get(0);
        }
        return new BeidouPaymentRecord();
    }

    @Override
    public void beidouPaymentWriteBack(PayoutIntf payout) {
        if (payout == null) {
            throw new BusinessException("请输入开票申请记录流水id");
        }
        BeidouPaymentRecord bpr = this.queryBeidouPaymentRecordByBusiCode(payout.getBusiCode());
        if (bpr == null) {
            throw new BusinessException("根据打款记录业务编号：" + payout.getBusiCode() + " 未找到北斗缴费记录");
        }
        bpr.setPayState(OrderAccountConst.BEIDOU_PAYMENT.PAY_STATE1);
        bpr.setPayTime(getDateToLocalDateTime(payout.getPayTime()));
        bpr.setUpdateDate(LocalDateTime.now());
        this.saveOrUpdate(bpr);
        iVehicleDataInfoService.updateEquipmentCode(bpr.getPlateNumber(), "", OrderConsts.GPS_TYPE.BD);
    }

    private LocalDateTime getDateToLocalDateTime(Date date) {
        Instant instant = date.toInstant();
        ZoneId zoneId = ZoneId.systemDefault();
        LocalDateTime localDateTime = instant.atZone(zoneId).toLocalDateTime();
        return localDateTime;
    }

}
