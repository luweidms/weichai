package com.youming.youche.order.api;

import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.order.domain.PlatformServiceCharge;

import java.util.List;

/**
* <p>
    *  服务类
    * </p>
* @author wuhao
* @since 2022-04-27
*/
    public interface IPlatformServiceChargeService extends IBaseService<PlatformServiceCharge> {
    /**
     * 根据用户ID，查询待审核的申请核销服务费
     * @param userId
     * @return
     * @throws Exception
     */
    List<Object> getNoVerificationAmountByUserId(Long userId);
    /**
     * 根据用户ID，查询未核销平台服务费记录
     * @param userId
     * @return
     * @throws Exception
     */
    List<Object> getVerificationPlatformAmountByUserId(Long userId);
}
