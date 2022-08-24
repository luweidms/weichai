package com.youming.youche.finance.api.etc;

import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.finance.domain.etc.EtcBillInfo;
import com.youming.youche.finance.domain.munual.PayoutIntf;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author hzx
 * @since 2022-04-16
 */
public interface IEtcBillInfoService extends IBaseService<EtcBillInfo> {

    /**
     * @Description: 该函数的功能描述:付款成功回写
     */
    void payForEtcBillWriteBack(PayoutIntf payoutIntf);

    /**
     * @Description: 该函数的功能描述:根据busiCode查询ETC账单
     */
    EtcBillInfo getEtcBillInfoByBusiCode(String busiCode);

}
