package com.youming.youche.record.business.controller.tenant;

import com.youming.youche.commons.response.ResponseResult;
import com.youming.youche.commons.web.Header;
import com.youming.youche.record.api.tenant.ITenantUserRelService;
import com.youming.youche.record.common.BaseController;
import com.youming.youche.record.dto.tenant.QueryAllTenantDto;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("tenant/user/rel")
public class TenantUserRelController extends BaseController {

    @DubboReference(version = "1.0.0")
    ITenantUserRelService iTenantUserRelService;

    @Resource
    HttpServletRequest request;

    /**
     * 接口编号：10069
     * <p>
     * 接口入参：
     * userId 用户编号
     * tenantId 车队id
     * <p>
     * 接口出参：
     * result true,是自有司机；false，非自有司机
     */
    @GetMapping("checkOwnDriver")
    public ResponseResult checkOwnDriver(Long userId, Long tenantId) {

        return ResponseResult.success(
                iTenantUserRelService.checkOwnDriver(userId, tenantId)
        );

    }

    /**
     * 获取所有与司机关联的车队  40047
     */
    @GetMapping("queryAllTenant")
    public ResponseResult queryAllTenant() {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        List<QueryAllTenantDto> queryAllTenantDtos = iTenantUserRelService.queryAllTenant(accessToken);
        return ResponseResult.success(queryAllTenantDtos);
    }

}
