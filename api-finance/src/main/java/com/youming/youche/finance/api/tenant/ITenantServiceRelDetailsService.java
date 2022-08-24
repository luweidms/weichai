package com.youming.youche.finance.api.tenant;

import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.finance.domain.munual.PayoutIntf;
import com.youming.youche.finance.domain.tenant.TenantServiceRelDetails;

/**
 * <p>
 * 授信额度已用流水表 服务类
 * </p>
 *
 * @author hzx
 * @since 2022-04-16
 */
public interface ITenantServiceRelDetailsService extends IBaseService<TenantServiceRelDetails> {

    /**
     * 油到期打款成功回调
     */
    void oilExpireWriteBack(PayoutIntf pay);

}
