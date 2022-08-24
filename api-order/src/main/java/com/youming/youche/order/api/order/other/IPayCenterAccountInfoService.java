package com.youming.youche.order.api.order.other;

import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.order.domain.order.PayCenterAccountInfo;

/**
* <p>
    *  服务类
    * </p>
* @author liangyan
* @since 2022-03-29
*/
    public interface IPayCenterAccountInfoService extends IBaseService<PayCenterAccountInfo> {

    /**
     * 根据租户id和开票方式获取支付中心的账号密码
     */
    PayCenterAccountInfo getPayCenterAccountInfo(Long billMethodId, Long tenantId);

    }
