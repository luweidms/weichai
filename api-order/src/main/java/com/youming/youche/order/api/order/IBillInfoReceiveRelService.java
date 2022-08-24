package com.youming.youche.order.api.order;

import com.baomidou.mybatisplus.extension.service.IService;
import com.youming.youche.order.domain.order.BillInfoReceiveRel;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-03-19
 */
public interface IBillInfoReceiveRelService extends IService<BillInfoReceiveRel> {
    /**
     * 根据车队ID获取别设为默认收票主体的开票信息、收件人信息
     */
    BillInfoReceiveRel getDefaultBillInfoByTenantId(Long tenantId);

    /**
     * 一个车队的同名公户，关联到相同的票据&接收地址
     * @param billInfoId
     * @param tenantId
     * @return
     */
    BillInfoReceiveRel getBillInfoReceiveRelByBillInfoId(Long billInfoId, Long tenantId);

    /**
     * 获取对应的收件人信息
     * @param relId
     * @return
     */
    BillInfoReceiveRel getBillInfoReceiveRelByRelId(Long relId);

    /**
     * WX接口-获取车队默认收票主体 30079
     */
    BillInfoReceiveRel getDefaultBillInfo(String accessToken);
}
