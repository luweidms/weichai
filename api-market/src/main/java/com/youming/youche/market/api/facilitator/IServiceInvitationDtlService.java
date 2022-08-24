package com.youming.youche.market.api.facilitator;

import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.market.domain.facilitator.ServiceInvitationDtl;

import java.util.List;

/**
 * <p>
 * 服务商申请合作明细表 服务类
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-01-28
 */
public interface IServiceInvitationDtlService extends IBaseService<ServiceInvitationDtl> {

    /**
     * 查询扩展表信息
     * @param id
     * @return
     */
     List<ServiceInvitationDtl> getInviteDtlList(Long id);

}
