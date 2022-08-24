package com.youming.youche.system.api.ac;

import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.system.domain.ac.OrderAccount;
import com.youming.youche.system.dto.ac.AccountDetailsWXDto;

import java.util.List;

/**
 * <p>
 * 订单账户表 服务类
 * </p>
 *
 * @author Terry
 * @since 2022-01-24
 */
public interface IOrderAccountService extends IBaseService<OrderAccount> {

    /**
     *
     * 查询OrderAccount 只提供查询时用
     * @param userId
     * @param tenantId
     * @param userType
     * @return
     * @throws Exception
     */
    List<OrderAccount> getOrderAccount(Long userId, Long tenantId, Integer userType);

    /**
     * 微信接口-账户明细(商家) 【21115】
     * @param name
     * @param accessToken
     */
    AccountDetailsWXDto getAccountDetailsWX(String name, String accessToken);

}
