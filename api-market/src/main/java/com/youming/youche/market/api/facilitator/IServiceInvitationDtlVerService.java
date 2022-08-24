package com.youming.youche.market.api.facilitator;

import com.baomidou.mybatisplus.extension.service.IService;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.market.domain.facilitator.ServiceInvitationDtlVer;

/**
 * <p>
 * 服务商申请合作明细版本 服务类
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-01-28
 */
public interface IServiceInvitationDtlVerService extends IService<ServiceInvitationDtlVer> {
    /**
     * 把旧的历史记录记为不可用
     * @param id
     */
    void checkHisSetNot(Long id, LoginInfo user);
}
