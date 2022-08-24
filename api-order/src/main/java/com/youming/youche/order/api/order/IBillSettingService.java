package com.youming.youche.order.api.order;

import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.order.domain.order.BillSetting;

/**
 * <p>
 * 车队的开票设置 服务类
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-03-19
 */
public interface IBillSettingService extends IBaseService<BillSetting> {

    /**
     * 判断租户是支持非外调车开平台票据
     * @param tenantId
     */
     Boolean supportNotOtherCarGetPlatformBill(Long tenantId);

    /**
     * 获取车队开票信息
     * @param tenantId
     * @return
     */
    BillSetting getBillSettingByTenantId(Long tenantId);

    /**
     * 查询租户开票能力
     * @param tenantId
     * @return
     * @throws Exception
     */
     Boolean getInvokeAble(Long tenantId);

    /**
     * 车队的开票设置信息
     * @param tenantId
     * @return
     */
    BillSetting getBillSetting(Long tenantId);

    /**
     * 获取对应的平台开票的渠道
     * @param tenantId
     * @return
     */
    Long getBillMethodByTenantId(Long tenantId);
}
