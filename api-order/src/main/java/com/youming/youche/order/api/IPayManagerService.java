package com.youming.youche.order.api;

import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.order.domain.PayForFinalChargeIn;
import com.youming.youche.order.domain.PayManager;


/**
* <p>
    *  服务类
    * </p>
* @author caoyajie
* @since 2022-04-15
*/
    public interface IPayManagerService extends IBaseService<PayManager> {


    /**
     * 支付尾款
     * @param userId  出帐用户编号
     * @param finalFee 尾款金额单位(分)
     * @param insuranceFee 保费金额单位(分)
     * @param exceptionFeeList 异常罚款金额集合单位(分)
     * @param orderId 订单id
     * @param tenantId 开单的租户id(而不是尾款收入人的租户id)
     * @param paymentDay 账期
     * @param prescriptionFine 时效罚款
     * @throws Exception
     * @return void
     */
    void payForFinalCharge(PayForFinalChargeIn in,String accessToken);

    /**
     * 根据id修改付款管理状态为已付款
     *
     * @param flowId
     */
    void updatePayManagerState(Long flowId);

    /**
     * 根据预支金额获取预支手续费
     * @param userId  预支用户编号
     * @param amountFee 预支金额单位(分)
     * @param tenantId 租户id
     * @throws Exception
     * @return long
     */
    Long getAdvanceServiceCharge(long userId, long amountFee, Long tenantId,Integer userType) ;

    /**
     * 预支接口
     * @param userId  预支用户编号
     * @param amountFee 预支金额单位(分)
     * @param objId 预支业务编号
     * @param tenantId 租户id
     * @throws Exception
     * @return void
     */
    void advancePayMarginBalance(Long userId, Long amountFee, Long objId, Long tenantId,Integer userType,String accessToken);
}
