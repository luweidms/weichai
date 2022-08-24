package com.youming.youche.market.provider.mapper.facilitator;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.components.workbench.WorkbenchDto;
import com.youming.youche.market.domain.facilitator.ServiceProduct;
import com.youming.youche.market.domain.user.UserRepairInfo;
import com.youming.youche.market.dto.facilitator.CooperationNumDto;
import com.youming.youche.market.dto.facilitator.CooperationProductDto;
import com.youming.youche.market.dto.facilitator.CooperationTenantDto;
import com.youming.youche.market.dto.facilitator.ProductQueryDto;
import com.youming.youche.market.dto.facilitator.ServiceProductDto;
import com.youming.youche.market.dto.facilitator.ServiceProductInfoDto;
import com.youming.youche.market.dto.facilitator.ServiceProductWxDto;
import com.youming.youche.market.dto.youca.ProductNearByDto;
import com.youming.youche.market.vo.facilitator.ServiceProductInfoVo;
import com.youming.youche.market.vo.facilitator.ServiceProductVxVoOK;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 服务商站点表Mapper接口
 * </p>
 *
 * @author Terry
 * @since 2022-01-22
 */
public interface ServiceProductMapper extends BaseMapper<ServiceProduct> {

    /**
     * 站点列表查询
     * @return
     */
    Page<ServiceProductDto> queryServiceProductList(Page<ServiceProductDto> page,
                                                    @Param("productQueryDto") ProductQueryDto productQueryDto,
                                                    @Param("tenantid")Long tenantid,
                                                    @Param("user") LoginInfo user);

    List<ServiceProductDto> getServiceProductApply(@Param("serviceUserId") Long serviceUserId,
                                                   @Param("serviceType") Integer serviceType,
                                                   @Param("authState") Integer authState,
                                                   @Param("state") Integer state,
                                                   @Param("tenantId") Long tenantId);
    /**
     * 运营后台-站点查询
     *
     * @return
     */
    Page<ServiceProductInfoVo> queryObmsProduct(Page<ServiceProductInfoVo> page, @Param("serviceProductInfoDto") ServiceProductInfoDto serviceProductInfoDto,
                                                @Param("tenantId") Long tenantId);

    CooperationNumDto countCooperationNum(@Param("serviceProductInfoVo") ServiceProductInfoVo serviceProductInfoVo, @Param("tenantId") Long tenantId, @Param("authState") Integer authState);

    /**
     * 查询合作车队列表
     *
     * @return
     */
    Page<CooperationTenantDto> queryCooperationTenant(
            Page<CooperationTenantDto> page,
            @Param("cooperationProductDto") CooperationProductDto cooperationProductDto,
            @Param("authState") Integer authState,
            @Param("tenantId") Long tenantId);


    Page<ServiceProductWxDto> queryWxProduct(Page<ServiceProductWxDto> page,
                                             @Param("authStates") List<Integer> authStates,
                                             @Param("serviceUserId") Long serviceUserId,
                                             @Param("state") List<Integer> state,
                                             @Param("serviceType") Integer serviceType,
                                             @Param("productName") String productName);


    /**
     * 查询服务商商品信息
     *
     * @param cardNum  卡号
     * @param cardType 卡类型(中石油 中石化)
     * @param custTime 下发时间
     */
    ServiceProduct getServiceProduct(@Param("cardNum") String cardNum, @Param("cardType") int cardType, @Param("custTime") String custTime);

    /**
     * 获取车队下用户信息
     *
     * @param userId   用户编号
     * @param tenantId 车队id
     */
     List<ServiceProduct> getServiceProductList(@Param("userId") long userId, @Param("tenantId") Long tenantId);


     List<ServiceProductVxVoOK>  queryCooperationProductVx(@Param("states") List<String> states,
                                                           @Param("productId") Long productId,
                                                           @Param("tenantName") String tenantName);

    /**
     * 获取服务商信息和站点子账号信息
     *
     * @param userId
     * @return serviceType 服务商类型
     * serviceName 服务商名称（子账号归属服务商）
     * mobilePhone 联系号码
     * userId 用户id
     * serviceTypeName 服务商类型（子账号类型）
     * linkman 联系人
     */
    Map<String, Object> queryChildCompanyName(@Param("userId") Long userId, @Param("userType") Integer userType);

    /**
     * 营运工作台  服务站点数量
     */
    List<WorkbenchDto> getTableManagerSiteCount();


    Page<ServiceProduct> doQueryPrivateProduct( Page<ServiceProduct> page,
                                                @Param("tenantId")Long tenantId);
//
//    List<ProductNearByDto> getServiceNearBy (@Param("tenantId") Long tenantId,
//                                             @Param("sourceNand")  String sourceNand,
//                                             @Param("sourceEand")  String sourceEand,
//                                             @Param("maxDistance") Double maxDistance,
//                                             @Param("serviceType")  Integer serviceType,
//                                             @Param("cityId")  Long cityId,
//                                             @Param("locationType")  Integer locationType,
//                                             @Param("oilCardType")  Long oilCardType);


    List<ProductNearByDto> getServiceNearBy2(
            @Param("tenantId")Long tenantId,
            @Param("isShare") Boolean isShare,
            @Param("longitude") String longitude,
            @Param("latitude")String latitude,
            @Param("state")Integer state,
            @Param("businessType")Integer businessType ,
            @Param("authState")Integer authState );


    List<ProductNearByDto>getServiceNearBy1(@Param("nand1") String nand1,
                                                          @Param("eand2") String eand2,
                                                          @Param("nand2") String nand2,
                                                          @Param("eand1") String eand1,
                                                          @Param("tenantIds") List<Long> tenantIds,
                                                          @Param("state") Integer state,
                                                          @Param("businessType") Integer businessType,
                                                          @Param("authState") Integer authState,
                                                          @Param("cityId") Long cityId,
                                                          @Param("locationType") Integer locationType,
                                                          @Param("oilCardType") Long oilCardType,
                                                          @Param("serviceType") Integer serviceType);


    IPage<UserRepairInfo> queryAppRepair (@Param("appRepairStates") List<String> appRepairStates,
                                          @Param("userInfoId") Long userInfoId,
                                          Page<UserRepairInfo> page);
}
