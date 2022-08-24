package com.youming.youche.finance.api.voucher;

import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.finance.domain.voucher.VoucherInfo;
import com.youming.youche.market.domain.youka.VoucherInfoRecord;

import java.util.List;

/**
 * <p>
 * 代金券信息表 服务类
 * </p>
 *
 * @author hzx
 * @since 2022-04-15
 */
public interface IVoucherInfoService extends IBaseService<VoucherInfo> {

    /**
     * 判断充值打款记录是否需要提现到实体卡
     *
     * @param busiCode 业务号(payoutIntf表字段busiCode)
     * @return true需要提现(需生成300打款记录)，false不需要提现(无需生成300打款记录)
     */
    boolean judgeRechargeIsNeedWithdrawal(String busiCode, String accessToken);

    List<VoucherInfoRecord> getVoucherInfoRecord(Long id);
}
