package com.youming.youche.record.api.account;

import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.record.domain.account.OrderAccount;

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

    /***
     * @Description: 查询账户
     * @Author: luwei
     * @Date: 2022/7/27 10:34
     * @Param userId:
      * @Param vehicleAffiliation:
      * @Param tenantId:
      * @Param sourceTenantId:
      * @Param oilAffiliation:
      * @Param userType:
     * @return: com.youming.youche.record.domain.account.OrderAccount
     * @Version: 1.0
     **/
    OrderAccount queryOrderAccount(long userId, String vehicleAffiliation, long tenantId, long sourceTenantId, String oilAffiliation,Integer userType)  throws BusinessException;
}
