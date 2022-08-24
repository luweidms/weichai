package com.youming.youche.market.api.facilitator;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.market.domain.facilitator.ServiceInvitation;
import com.youming.youche.market.dto.facilitator.CooperationDto;
import com.youming.youche.market.vo.facilitator.BusinessInvitationVo;
import com.youming.youche.market.vo.facilitator.ServiceInvitationVxVo;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 服务商申请合作 服务类
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-01-28
 */
public interface IServiceInvitationService extends IBaseService<ServiceInvitation> {


    /**
     * 保存邀请合作
     *
     * @param productId
     * @param tenantId
     * @param fixedBalance
     * @param floatBalance
     * @param serviceUserId
     * @throws Exception
     */
    ServiceInvitation saveInvitation(LoginInfo user, Long productId, Long tenantId, Long fixedBalance, String floatBalance, Long fixedBalanceBill, String floatBalanceBill, String serviceCharge, Long serviceUserId, Integer cooperationType, Integer localeBalanceState);

    /**
     * 查询合作列表
     * @param id
     * @return
     */
    List<CooperationDto> queryCooperationList(Long id);
    /**
     * 微信-商家查询邀请
     *
     * @param tenantName
     * @param cooperationType
     * @param authState
     * @return
     */
    Page<ServiceInvitationVxVo> queryBusinessInvitationVx(Integer pageNum, Integer pageSize,
                                                          String tenantName, List<Integer> cooperationType,
                                                          List<Integer> authState, String linkPhone,
                                                          String linkman,String token);
    /**
     * 微信获取合作详情
     */
    BusinessInvitationVo getBusinessInvitation(Long id, Integer cooperationType);


    /**
     * 接口编码：40016
     * 审核 邀请
     * @return
     */
    Boolean auditInvitation(Long id, Integer authState, String remark,
                            Integer cooperationType, String quotaAmt,String accessToken);

    /**
     * 统计邀请审核数量
     * @return
     */
    int countInvitationAuth(String accessToken);
}
