package com.youming.youche.market.business.controller.facilitator;


import com.youming.youche.commons.base.BaseController;
import com.youming.youche.commons.response.ResponseResult;
import com.youming.youche.commons.web.Header;
import com.youming.youche.market.annotation.Dict;
import com.youming.youche.market.api.facilitator.IServiceInvitationService;
import com.youming.youche.market.domain.facilitator.ServiceInvitation;
import com.youming.youche.market.dto.facilitator.InvitationVo;
import com.youming.youche.market.dto.facilitator.InvitationVx;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 服务商申请合作 前端控制器
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-01-28
 */
@RestController
@RequestMapping("/facilitator/serviceinvitation")
public class ServiceInvitationController extends BaseController<ServiceInvitation, IServiceInvitationService> {

    @DubboReference(version = "1.0.0")
    IServiceInvitationService serviceInvitationService;

    @Override
    public IServiceInvitationService getService() {
        return serviceInvitationService;
    }

    /**
     * 微信-商家查询邀请
     *
     * @param pageNum      分页参数
     * @param pageSize     分页参数
     * @param invitationVo 查询条件
     */
    @GetMapping("queryBusinessInvitationVx")
    @Dict
    public ResponseResult queryBusinessInvitationVx(@RequestParam(value = "pageNum", defaultValue = "1", required = false) Integer pageNum,
                                                    @RequestParam(value = "pageSize", defaultValue = "20", required = false) Integer pageSize,
                                                    InvitationVo invitationVo) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        return ResponseResult.success(
                serviceInvitationService.queryBusinessInvitationVx(pageNum, pageSize, invitationVo.getTenantName(),
                        invitationVo.getCooperationType(), invitationVo.getAuthState(),
                        invitationVo.getLinkPhone(), invitationVo.getLinkman(), accessToken)
        );

    }

    /**
     * 微信获取合作详情
     *
     * @param id              邀请ID
     * @param cooperationType 申请合作类型 1：初次合作，2：修改合作'
     * @return
     */
    @GetMapping("getBusinessInvitation")
    public ResponseResult getBusinessInvitation(Long id, Integer cooperationType) {
        return ResponseResult.success(
                serviceInvitationService.getBusinessInvitation(id, cooperationType)
        );
    }

    /**
     * 40016 审核 邀请
     */
    @PostMapping("auditInvitation")
    public ResponseResult auditInvitation(@RequestBody InvitationVx invitationVx) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        return ResponseResult.success(
                serviceInvitationService.auditInvitation(invitationVx.getId(), invitationVx.getAuthState(), invitationVx.getRemark(),
                        invitationVx.getCooperationType(), invitationVx.getQuotaAmt(), accessToken) ? "成功" : "失败"
        );
    }

}
