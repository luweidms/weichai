package com.youming.youche.market.business.controller.facilitator;


import com.alibaba.nacos.api.utils.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.commons.base.BaseController;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.response.ResponseResult;
import com.youming.youche.commons.web.Header;
import com.youming.youche.market.annotation.Dict;
import com.youming.youche.market.api.facilitator.IServiceProductService;
import com.youming.youche.market.domain.facilitator.ServiceProduct;
import com.youming.youche.market.domain.user.UserRepairInfo;
import com.youming.youche.market.dto.facilitator.*;
import com.youming.youche.market.dto.youca.ServiceProductOutDto;
import com.youming.youche.market.vo.facilitator.ServiceDetailVo;
import com.youming.youche.market.vo.facilitator.ServiceProductInfoVo;
import com.youming.youche.market.vo.facilitator.ServiceProductVo;
import com.youming.youche.market.vo.youca.ProductNearByVo;
import com.youming.youche.market.vo.youca.ServiceProductOutVo;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 服务商站点表 前端控制器
 * </p>
 *
 * @author Terry
 * @since 2022-01-22
 */
@RestController
@RequestMapping("/facilitator/serviceproduct/data/info")
public class ServiceProductController extends BaseController<ServiceProduct, IServiceProductService> {
    @DubboReference(version = "1.0.0")
    private IServiceProductService serviceProductService;

    @Override
    public IServiceProductService getService() {
        return serviceProductService;
    }

    /**
     * 点击合作站点时 查询
     *
     * @param productQueryDto
     * @return
     */
    @GetMapping("queryServiceProduct")
    public ResponseResult queryServiceProduct(ProductQueryDto productQueryDto,
                                              @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                              @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        try {
            String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
            Page<ServiceProductVo> productVoPage = serviceProductService.queryServiceProductList(productQueryDto, pageNum, pageSize, accessToken);
            return ResponseResult.success(productVoPage);
        } catch (Exception e) {
            return ResponseResult.failure("网络异常");
        }
    }

    /**
     * 新增后服站点
     *
     * @param productSaveDto
     * @return
     */
    @PostMapping("saveProduct")
    public ResponseResult saveProduct(@RequestBody ProductSaveDto productSaveDto) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        ResponseResult responseResult = serviceProductService.saveProduct(productSaveDto, accessToken);
        return responseResult;
    }

    /**
     * 修改后服站点
     *
     * @param productSaveDto
     * @return
     */
    @PostMapping("saveOrUpdate")
    public ResponseResult saveOrUpdate(@RequestBody ProductSaveDto productSaveDto) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        ResponseResult responseResult = serviceProductService.saveOrUpdate(productSaveDto, accessToken);
        return responseResult;
    }

    /**
     * 查看后服产品
     *
     * @param pageNum
     * @param pageSize
     * @param serviceProductInfoDto
     * @return
     */
    @GetMapping("queryServiceProductList")
    public ResponseResult queryServiceProductList(@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                                  @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                                                  ServiceProductInfoDto serviceProductInfoDto) {
        Page<ServiceProductInfoVo> infoVoPage = serviceProductService.queryServiceProductList(pageNum, pageSize, serviceProductInfoDto);
        return ResponseResult.success(infoVoPage);
    }

//    @PostMapping("saveProduct")
//    public ResponseResult saveProduct(){
//        serviceProductService
//    }

    /**
     * 查看站点
     *
     * @param productId 服务商站点主键id
     * @return
     */
    @GetMapping("seeProduct")
    public ResponseResult seeProduct(@RequestParam("productId") Long productId) {
        try {
            ServiceDetailVo serviceDetailVo = serviceProductService.seeProduct(productId);
            return ResponseResult.success(serviceDetailVo);
        } catch (Exception e) {
            return ResponseResult.failure("网络异常");
        }
    }

    /**
     * 点击合作车队数的时候 分页
     *
     * @param pageNum               分页参数
     * @param pageSize              分页参数
     * @param cooperationProductDto
     * @return
     */
    @GetMapping("queryCooperationProduct")
    public ResponseResult queryCooperationProduct(@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                                  @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                                                  CooperationProductDto cooperationProductDto) {
        try {
            Page<CooperationTenantDto> cooperationTenantDtoPage = serviceProductService.queryCooperationProduct(pageNum, pageSize, cooperationProductDto);
            return ResponseResult.success(cooperationTenantDtoPage);
        } catch (Exception e) {
            return ResponseResult.failure("网络异常");
        }
    }

    /**
     * 合作站点审核
     *
     * @param auditShareProductDto 审核参数
     * @return
     */
    @PostMapping("auditShareProduct")
    public ResponseResult auditShareProduct(@RequestBody AuditShareProductDto auditShareProductDto) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        return serviceProductService.auditShareProduct(auditShareProductDto.getProductId(), auditShareProductDto.getAuthState(), auditShareProductDto.getAuthRemark(), accessToken);
    }

    /**
     * 查看站点历史档案详情
     *
     * @param relId     关系主键
     * @param productId 服务商产品主键id
     * @return
     */
    @GetMapping("getTenantProductHisOut")
    public ResponseResult getTenantProductHisOut(Long relId, Long productId) {
        try {
            String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
            return ResponseResult.success(serviceProductService.getTenantProductHisOut(relId, productId));
        } catch (Exception e) {
            return ResponseResult.failure("网络异常");
        }
    }

    /**
     * 站点启动或停用
     *
     * @param serviceProductSure 审核参数
     * @return
     */
    @PostMapping("sure")
    public ResponseResult sure(@RequestBody ServiceProductSure serviceProductSure) {
        try {
            String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
            return serviceProductService.sure(serviceProductSure.getProductId(), serviceProductSure.getPass(), serviceProductSure.getDescribe(), accessToken);
        } catch (Exception e) {
            return ResponseResult.failure("网络异常");
        }
    }

    /**
     * 微信查询站点列表
     *
     * @param pageNum     分页参数
     * @param pageSize    分页参数
     * @param authStates  审核状态
     * @param state       是否有效，1为有效，2为无效
     * @param serviceType 服务商类型
     * @param productName 产品名称
     * @return
     */
    @GetMapping("queryWxProduct")
    @Dict
    public ResponseResult queryWxProduct(@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                         @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                                         @RequestParam(value = "authStates") List<Integer> authStates,
                                         @RequestParam(value = "state") List<Integer> state,
                                         Integer serviceType, String productName) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        return ResponseResult.success(
                serviceProductService.queryWxProduct(pageNum, pageSize, authStates, state, serviceType, productName, accessToken)
        );
    }

    /**
     * 40017 解除合作
     *
     * @param relId     租户与站点关系表主键id
     * @param productId 服务商产品主键
     * @return
     */
    @GetMapping("releaseCooperation")
    public ResponseResult releaseCooperation(@RequestParam Long relId,
                                             @RequestParam Long productId) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        return ResponseResult.success(
                serviceProductService.releaseCooperation(relId, productId, accessToken)
        );
    }

    /**
     * 40008 站点详情-微信
     *
     * @param productId 产品id
     */
    @GetMapping("getProductDetail")
    @Dict
    public ResponseResult getProductDetail(@RequestParam Long productId) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        return ResponseResult.success(
                serviceProductService.getProductDetail(productId, accessToken)
        );
    }

    /**
     * 40002 新增修改站点
     */
    @PostMapping("saveOrUpdateProduct")
    public ResponseResult saveOrUpdateProduct(@RequestBody ProductSaveDto productSaveIn) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        return ResponseResult.success(
                serviceProductService.saveOrUpdateProduct(productSaveIn, accessToken)
        );
    }

    /**
     * 合作车队 -站点
     *
     * @param productId  产品id
     * @param state      合作状态 1：合作中  2：已解约
     * @param tenantName 用户名称
     * @return
     */
    @GetMapping("queryCooperationProductVx")
    public ResponseResult queryCooperationProductVx(@RequestParam("productId") Long productId,
                                                    @RequestParam("state") List<String> state,
                                                    String tenantName) {
        return ResponseResult.success(
                serviceProductService.queryCooperationProductVx(state, productId, tenantName)
        );
    }

    /**
     * niejiewei 40000 司机小程序 查询附近油站
     *
     * @return
     */
    @GetMapping("queryNearbyOil")
    public ResponseResult queryNearbyOil(ServiceProductOutVo vo) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        ProductNearByVo productNearByOutDtos = serviceProductService.queryNearbyOil(vo, accessToken);
        return ResponseResult.success(productNearByOutDtos);
    }

    /**
     * 40001 扫码支付详情接口
     *
     * @param oilId    油站id
     * @param tenantId 租户id
     * @return
     */
    @GetMapping("scanCodePayment")
    public ResponseResult scanCodePayment(@RequestParam("oilId") String oilId,
                                          @RequestParam("tenantId") Long tenantId) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        Long oilIds = null;
        if (!StringUtils.isBlank(oilId)) {
            try {
                oilIds = Long.valueOf(oilId);
            } catch (Exception e) {
                throw new BusinessException("无效站点");
            }
        }
        ServiceProductOutDto serviceProductOutDto = serviceProductService.scanCodePayment(oilIds, tenantId, accessToken);
        return ResponseResult.success(serviceProductOutDto);
    }

    /**
     * 40024 附近维修站查询
     */
    @GetMapping("/queryNearbyRepair")
    public ResponseResult queryNearbyRepair(ServiceProductOutVo vo) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        ProductNearByVo productNearByOutDtos = serviceProductService.queryNearbyRepair(vo, accessToken);
        return ResponseResult.success(productNearByOutDtos);
    }

    /**
     * niejiewei 40025 查询app维修保养记录
     *
     * @param appRepairState App维修状态
     * @return
     */
    @GetMapping("/queryAppRepair")
    public ResponseResult queryAppRepair(String appRepairState,
                                         @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                         @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        IPage<UserRepairInfo> userRepairInfoIPage = serviceProductService.queryAppRepair(appRepairState, accessToken, pageNum, pageSize);
        return ResponseResult.success(userRepairInfoIPage);
    }

}
