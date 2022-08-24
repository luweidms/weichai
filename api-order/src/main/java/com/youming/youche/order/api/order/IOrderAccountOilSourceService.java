package com.youming.youche.order.api.order;

import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.order.domain.order.OrderAccountOilSource;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-03-22
 */
public interface IOrderAccountOilSourceService extends IBaseService<OrderAccountOilSource> {

    /**
     * 根据账户id、用户id、租户id查询账户油来源
     * @param accId
     * @param userId
     * @param tenantId
     * @param userType
     * @return
     */
    OrderAccountOilSource getOrderAccountOilSource(Long accId, Long userId, Long tenantId,Integer userType);


    /**
     * 创建油账户资金来源
     * @param parentId 上级id
     * @param accId 账户id
     * @param userId 用户id
     * @param oilBalance 订单油
     * @param rechargeOilBalance 报账模式油
     * @param tenantId
     * @param userType 用户类型
     * @return
     * @throws Exception
     */
    OrderAccountOilSource createOrderAccountOilSource(Long parentId, Long accId, Long userId,
                                                      Long oilBalance, Long rechargeOilBalance,
                                                      Long tenantId, Integer userType, LoginInfo baseUser);

    /**
     * 清楚账户邮费
     */
    void clearOilBalance(Long userId, Long tenantId);

}
