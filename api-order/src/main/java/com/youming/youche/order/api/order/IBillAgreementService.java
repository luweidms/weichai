package com.youming.youche.order.api.order;

import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.order.domain.order.BillAgreement;

import java.util.Map;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-03-19
 */
public interface IBillAgreementService extends IBaseService<BillAgreement> {

    /**
     * 委托协议
     * @param billMethod
     * @param billInfoId
     * @return
     */
    BillAgreement createOrSelectBillAgreement(Long billMethod, Long billInfoId);


    /**
     * 计算开票服务费
     * @param openUserId 开票平台id
     * @param cash 现金
     * @param oil 油
     * @param etc etc
     * @param billAmount 开票金额
     * @param type 1计算票据服务费 2计算票据成本
     * @return
     * @throws Exception
     */
    Map<String, Object> calculationServiceFee(Long openUserId, Long cash, Long oil, Long etc,
                                              Long billAmount, Long tenantId,
                                              Map<String, Object> inParam);

}
