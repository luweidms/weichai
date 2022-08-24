package com.youming.youche.order.api.order;

import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.order.domain.order.ConsumeOilFlowExt;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-03-22
 */
public interface IConsumeOilFlowExtService extends IBaseService<ConsumeOilFlowExt> {

    /**
     *
     * @param otherFlowIds
     * @param sourceRecordType
     * @return
     * @throws Exception
     */
    List<ConsumeOilFlowExt> getConsumeOilFlowExtByFlowId(List<Long> otherFlowIds, Integer sourceRecordType);

    /**
     *
     * @param flowId
     * @return
     * @throws Exception
     */
    ConsumeOilFlowExt queryConsumeOilFlowExtByFlowId(Long flowId);

    /**
     * @param flowId 加油记录id
     * @return 加油记录扩展数据
     */
    ConsumeOilFlowExt getConsumeOilFlowExtByFlowId(Long flowId);


    /**
     *
     * @param flowId
     * @param otherFlowId
     * @param sourceRecordType
     * @param oilConsumer
     * @param creditLimit
     * @param rechargeOil
     * @param rebateOil
     * @param creditOil
     * @param tenantId
     * @param userType 收款方用户类型
     * @param payUserType 付款方用户类型
     * @return
     * @throws Exception
     */
    ConsumeOilFlowExt createConsumeOilFlowExt(Long flowId,Long otherFlowId,Integer sourceRecordType,
                                              Integer oilConsumer,Integer creditLimit,Long rechargeOil,
                                              Long rebateOil,Long creditOil,Long tenantId);
}
