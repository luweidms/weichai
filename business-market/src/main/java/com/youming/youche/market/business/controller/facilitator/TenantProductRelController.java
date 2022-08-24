package com.youming.youche.market.business.controller.facilitator;


import com.youming.youche.commons.base.BaseController;
import com.youming.youche.commons.response.ResponseResult;
import com.youming.youche.commons.web.Header;
import com.youming.youche.market.api.facilitator.ITenantProductRelService;
import com.youming.youche.market.domain.facilitator.TenantProductRel;
import com.youming.youche.market.vo.facilitator.TenantProductVo;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 租户与站点关系表 前端控制器
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-01-23
 */
@RestController
@RequestMapping("/facilitator/tenantproductrel/data/info")
public class TenantProductRelController extends BaseController<TenantProductRel, ITenantProductRelService> {
    @DubboReference(version = "1.0.0")
    ITenantProductRelService tenantProductRelService;


    @Override
    public ITenantProductRelService getService() {
        return tenantProductRelService;
    }

    /**
     * 查看合作商商品
     *
     * @param productId 产品id
     * @param isShare   是否共享
     * @return
     */
    @GetMapping("getTenantProduct")
    public ResponseResult getTenantProduct(@RequestParam("productId")Long productId,
                                           @RequestParam("isShare") Integer isShare){
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        TenantProductVo tenantProduct = tenantProductRelService.getTenantProduct(productId, isShare, accessToken);
        return ResponseResult.success(tenantProduct);
    }

    /**
     * 删除 站点与租户关系
     *
     * @param productId 服务商产品id
     * @return
     */
    @GetMapping("delTenantProduct")
    public ResponseResult delTenantProduct(@RequestParam("productId") Long productId){
        try {
            String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
            return tenantProductRelService.delTenantProduct(productId,accessToken);
        } catch (Exception e) {
            return ResponseResult.failure("删除异常");
        }
    }

    /**
     * 车队合作详情
     *
     * @param relId 租户与站点关系主键id
     * @return
     */
    @GetMapping("getCooperationProduct")
    public ResponseResult getCooperationProduct(@RequestParam("relId") long relId){
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        return ResponseResult.success(
                tenantProductRelService.getCooperationProduct(relId,accessToken)
        );
    }
}
