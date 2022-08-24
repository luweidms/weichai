package com.youming.youche.market.api.facilitator;

import com.youming.youche.market.domain.facilitator.BillSetting;
import com.youming.youche.commons.base.IBaseService;

/**
 * <p>
 * 车队的开票设置 服务类
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-02-14
 */
public interface IBillSettingService extends IBaseService<BillSetting> {

    /**
     * 车队开票信息
     *
     * @param tenantId 车队id
     */
    BillSetting getBillSetting(Long tenantId);

}
