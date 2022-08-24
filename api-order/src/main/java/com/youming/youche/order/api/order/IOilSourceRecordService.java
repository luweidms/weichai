package com.youming.youche.order.api.order;

import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.order.domain.order.OilSourceRecord;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author liangyan
 * @since 2022-03-23
 */
public interface IOilSourceRecordService extends IBaseService<OilSourceRecord> {

    /**
     *
     * @param rechargeId
     * @param orderId
     * @param tenantId
     * @return
     * @throws Exception
     */
    List<OilSourceRecord> getOilSourceRecordNoPayBalance(String orderId, Long tenantId);



    /**
     * 分配油回退
     * @param userId
     * @param orderNum
     * @param sourceUserId
     * @param subjectsId
     * @param map
     * @param recallType 1原路回退，2回退到转移账户
     * @throws Exception
     */
    void recallOil(Long userId, String orderNum, Long sourceUserId, Long subjectsId,
                   Long tenantId, Map<String, Object> inparam, Integer recallType, LoginInfo user);


    /**
     * 获取分配油资金来源信息
     * @param rechargeId
     * @param orderId
     * @param tenantId
     * @return
     * @throws Exception
     */
    List<OilSourceRecord> getOilSourceRecordByOrderId(String orderId,Long tenantId);




    /**
     * 分配油资金来源
     * @param rechargeId
     * @param orderId
     * @param balance
     * @param noPayBalance
     * @param paidBalance
     * @param sourceRecordType
     * @param tenantId
     * @return
     * @throws Exception
     */
    OilSourceRecord createOilSourceRecord(Long rechargeId, String orderId,
                                          Long balance, Long noPayBalance,
                                          Long paidBalance, Integer sourceRecordType,
                                          Long tenantId,LoginInfo baseUser);


}
