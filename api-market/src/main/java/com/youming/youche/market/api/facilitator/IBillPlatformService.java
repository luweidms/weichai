package com.youming.youche.market.api.facilitator;

import com.youming.youche.market.domain.facilitator.BillPlatform;
import com.youming.youche.commons.base.IBaseService;

/**
 * <p>
 * 票据平台表 服务类
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-02-14
 */
public interface IBillPlatformService extends IBaseService<BillPlatform> {
    /**
     * 根据用户编号查询票据配置信息
     * @param userId 用户编号
     * @return sysPre 系统前缀
     */
    BillPlatform queryBillPlatformByUserId(Long userId);
}
