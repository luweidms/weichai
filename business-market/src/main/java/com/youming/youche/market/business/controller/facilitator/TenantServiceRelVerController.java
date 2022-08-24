package com.youming.youche.market.business.controller.facilitator;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.youming.youche.commons.response.ResponseResult;
import com.youming.youche.commons.web.Header;
import com.youming.youche.market.api.facilitator.ITenantServiceRelVerService;
import com.youming.youche.market.dto.facilitator.ServiceProductDto;
import com.youming.youche.market.dto.facilitator.ServiceProductInfoDto;
import com.youming.youche.market.dto.facilitator.TenantServiceDto;
import com.youming.youche.market.dto.facilitator.TenantServiceRelVerDto;
import com.youming.youche.market.vo.facilitator.ServiceInfoVo;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * 服务商与租户关系 前端控制器
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-01-25
 */
@RestController
@RequestMapping("/facilitator/tenantservicerelver")
public class TenantServiceRelVerController  {
    @DubboReference(version = "1.0.0")
    ITenantServiceRelVerService tenantServiceRelVerService;
    @Resource
    HttpServletRequest request;

    public ITenantServiceRelVerService getService() {
        return tenantServiceRelVerService;
    }

    /**
     * 查看服务商历史档案
     *
     * @param tenantServiceRelVerDto 服务商历史档案查询条件
     * @param pageNum                分页参数
     * @param pageSize               分页参数
     * @return
     */
    @GetMapping({"queryTenantServiceRelVer"})
    public ResponseResult queryTenantServiceRelVer(TenantServiceRelVerDto tenantServiceRelVerDto,
                                                   @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                                   @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize){
        IPage<TenantServiceDto> tenantServiceDtoIPage=null;
        try {
            String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
             tenantServiceDtoIPage = tenantServiceRelVerService.queryTenantServiceHis(pageNum,pageSize,tenantServiceRelVerDto.getLoginAcct(),tenantServiceRelVerDto.getServiceName(),
                                                                                                           tenantServiceRelVerDto.getLinkman(),tenantServiceRelVerDto.getServiceType(),accessToken);
        } catch (Exception e) {
            return ResponseResult.failure("网络异常");
        }
        return ResponseResult.success(tenantServiceDtoIPage);
    }

    /**
     * 查看站点历史档案
     * @return
     */
    @GetMapping({"queryProductHis"})
    public ResponseResult queryProductHis(ServiceProductInfoDto tenantServiceRelVerDto,
                                          @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                          @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize){
        IPage<ServiceProductDto> serviceProductDtoIPage=null;
        try {
            String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
            serviceProductDtoIPage = tenantServiceRelVerService.queryProductHis(pageNum,pageSize,tenantServiceRelVerDto.getProductName(),tenantServiceRelVerDto.getServiceCall()
            ,tenantServiceRelVerDto.getAddress(),tenantServiceRelVerDto.getServiceName(),accessToken);
        } catch (Exception e) {
            return ResponseResult.failure("网络异常");
        }
        return ResponseResult.success(serviceProductDtoIPage);
    }

    /**
     * 查看历史档案详情
     *
     * @param serviceUserId 服务商用户编号
     * @param relId         租户与产品关系主键id
     * @return
     */
    @GetMapping("seeServiceInfoHis")
    public  ResponseResult seeServiceInfoHis(@Param("serviceUserId") Long serviceUserId,
                                             @Param("relId") Long relId){
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        ServiceInfoVo serviceInfoVo = tenantServiceRelVerService.seeServiceInfoHis(serviceUserId, relId, accessToken);
        return ResponseResult.success(serviceInfoVo);
    }
}
