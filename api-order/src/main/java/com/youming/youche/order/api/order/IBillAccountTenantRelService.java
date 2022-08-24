package com.youming.youche.order.api.order;

import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.order.domain.order.BillAccountTenantRel;
import com.youming.youche.order.domain.order.BillInfoReceiveRel;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-03-19
 */
public interface IBillAccountTenantRelService extends IBaseService<BillAccountTenantRel> {

    /**
     * 根据车队ID获取别设为默认收票主体的开票信息、收件人信息
     */
    BillInfoReceiveRel getDefaultBillInfoByTenantId(Long tenantId);

    /**
     * 根据超管的userId获取默认收票主体
     * @param tenantId
     * @return
     */
    BillAccountTenantRel getDefaultBillAccount(Long tenantId);

    /**
     * 为一个开票信息获取一条关联关系
     * @param lookUp
     * @param tenantId
     * @return
     */
    BillInfoReceiveRel createOrSelect(String lookUp, Long tenantId);

    /**
     * 根据租户获取默认收票主体的关系
     */
    BillAccountTenantRel getDefaultBilltAccountByTenantId(Long tenantId);

    /**
     * 根据主键id获取车队对公账户开票信息
     * @param relId
     * @return
     */
    BillAccountTenantRel getBillAccountTenantRelByRelId(Long relId);

    /**
     * 获取账户对应的开票信息、收件人信息、是否为默认开票主体
     * @param billAccountTenantRel
     * @return
     */
    BillInfoReceiveRel getBillInfoReceiveRel(BillAccountTenantRel billAccountTenantRel);
}
