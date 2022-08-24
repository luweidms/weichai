package com.youming.youche.finance.api.base;

import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.finance.domain.base.BeidouPaymentRecord;
import com.youming.youche.finance.domain.munual.PayoutIntf;

/**
 * <p>
 * 中交北斗缴费记录 服务类
 * </p>
 *
 * @author hzx
 * @since 2022-04-18
 */
public interface IBeidouPaymentRecordService extends IBaseService<BeidouPaymentRecord> {

    /**
     * 根据业务单号查询缴费记录
     */
    BeidouPaymentRecord queryBeidouPaymentRecordByBusiCode(String busiCode);

    /**
     * 北斗缴费打款成功回写
     */
    void beidouPaymentWriteBack(PayoutIntf payout);

}
