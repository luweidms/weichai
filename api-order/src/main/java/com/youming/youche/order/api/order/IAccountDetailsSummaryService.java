package com.youming.youche.order.api.order;

import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.order.domain.order.AccountDetails;
import com.youming.youche.order.domain.order.AccountDetailsSummary;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-03-22
 */
public interface IAccountDetailsSummaryService extends IBaseService<AccountDetailsSummary> {

    /**
     * 保存资金账户信息
     * @param details
     */
    void saveAccountDetailsSummary(AccountDetails details);

    /**
     * 已付尾款
     * @param orderId 订单号
     * @param tenantId 租户Id
     * @param userId 用户Id
     * @param userType 用户类型
     * @return
     * @throws Exception
     */
    List doQueryPaidPayment(Long orderId, Long tenantId, Long userId, int userType);

    /**
     * App接口-已付金额 28318
     */
    List<AccountDetailsSummary> doQueryAmountPaidApp(Long orderId, Long payeeUserId, String accessToken);
}
