package com.youming.youche.order.api.order;

import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.order.domain.order.BillInfo;
import com.youming.youche.order.domain.order.BillInfoReceiveRel;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-03-19
 */
public interface IBillInfoService extends IBaseService<BillInfo> {

    /**
     * 根据抬头获取开票信息、收件人信息
     * @param lookUp
     * @return
     */
    BillInfo getBillInfoByLookUp(String lookUp);

    /**
     * 根据车队ID获取别设为默认收票主体的开票信息、收件人信息
     */
    BillInfoReceiveRel getDefaultBillInfoByTenantId(Long tenantId);

    /**
     * 判断开票信息是否完整
     */
    boolean completeness(BillInfo billInfo);
}
