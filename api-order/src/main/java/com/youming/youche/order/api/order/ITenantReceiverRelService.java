package com.youming.youche.order.api.order;

import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.order.domain.order.TenantReceiverRel;

/**
 * <p>
 * 车队与收款人的关联关系 服务类
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-03-19
 */
public interface ITenantReceiverRelService extends IBaseService<TenantReceiverRel> {
    /**
     * 创建tenant_receiver_rel
     * @param userReceiverId
     * @param remark
     * @param tenantId
     * @param user
     * @return
     */
    TenantReceiverRel createTenantReceiverRel(Long userReceiverId, String remark,
                                              Long tenantId, LoginInfo user);

    /**
     * 获取车队与收款人关联关系
     * @param receiverId
     * @param tenantId
     * @return
     */
    TenantReceiverRel getTenantReceiverRel(long receiverId, long tenantId);


    /**
     * 若手机号码在系统已经存在，姓名将会被忽略
     * @param phone
     * @param receiverName
     * @param linkman
     * @param remark
     * @param tenantId
     * @param user
     */
    void createUserReceiverInfo(String phone, String receiverName, String linkman, String remark,
                                Long tenantId,LoginInfo user);
}
