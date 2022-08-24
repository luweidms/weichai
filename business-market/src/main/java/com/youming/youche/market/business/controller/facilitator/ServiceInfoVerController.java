package com.youming.youche.market.business.controller.facilitator;


import com.youming.youche.commons.response.ResponseResult;
import com.youming.youche.commons.web.Header;
import com.youming.youche.market.api.facilitator.IServiceInfoVerService;
import com.youming.youche.market.dto.facilitator.AuditServiceInfoDto;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * 服务商版本表 前端控制器
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-01-26
 */
@RestController
@RequestMapping("/facilitator/serviceinfover/data/info")
public class ServiceInfoVerController {
    @DubboReference(version = "1.0.0")
    private IServiceInfoVerService serviceInfoVerService;
    @Resource
    HttpServletRequest request;
    public IServiceInfoVerService getService() {
        return serviceInfoVerService;
    }

    /**
     * 服務商審核
     *
     * @param auditServiceInfoDto 审核参数
     * @return
     */
    @PostMapping("auditServiceInfo")
    public ResponseResult auditServiceInfo(@RequestBody AuditServiceInfoDto auditServiceInfoDto) {
        try {
            String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
            return serviceInfoVerService.auditServiceInfo(auditServiceInfoDto.getServiceUserId(),
                    auditServiceInfoDto.getIsPass(),
                    auditServiceInfoDto.getAuditReason(),accessToken);
        } catch (Exception e) {
            return  ResponseResult.failure("网络异常");
        }
    }
}
