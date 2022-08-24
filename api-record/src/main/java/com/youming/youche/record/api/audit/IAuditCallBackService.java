package com.youming.youche.record.api.audit;

import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.record.domain.apply.ApplyRecord;

import java.util.Map;

public interface IAuditCallBackService {

    /**
     * 流程结束，审核通过的回调方法
     *
     * @param busiId 业务的主键
     * @param desc   结果的描述
     * @return
     */
    public void sucess(Long busiId, String desc, Map paramsMap, String token) throws BusinessException;

    /**
     * 流程结束，审核不通过的回调方法
     *
     * @param busiId 业务的主键
     * @param desc   结果的描述
     * @return
     */
    public void fail(Long busiId, String desc, Map paramsMap, String token) throws BusinessException;

    /**
     * 自有车--有车主(转移自有车)
     *
     * @param applyRecord 邀请申请记录
     * @param token
     */
    void transferTenant(ApplyRecord applyRecord, String token);

    /**
     * 邀请加入自有车
     *
     * @param applyRecord 邀请记录
     * @param token
     */
    void invitationiJoinOwnApp(ApplyRecord applyRecord, String token);

    /**
     * 邀请成为外调车,招商车或挂靠车，车辆有归属车队
     *
     * @param applyRecord 邀请记录
     * @param token
     */
    void invitationiJoinTmp(ApplyRecord applyRecord, String token);

}
