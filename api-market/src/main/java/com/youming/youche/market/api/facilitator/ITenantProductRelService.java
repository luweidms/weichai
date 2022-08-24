package com.youming.youche.market.api.facilitator;

import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.response.ResponseResult;
import com.youming.youche.market.domain.facilitator.TenantProductRel;
import com.youming.youche.market.dto.facilitator.ProductSaveDto;
import com.youming.youche.market.dto.facilitator.TenantProductRelOutDto;
import com.youming.youche.market.vo.facilitator.CooperationProductVo;
import com.youming.youche.market.vo.facilitator.TenantProductVo;

import java.util.List;

/**
 * <p>
 * 租户与站点关系表 服务类
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-01-23
 */
public interface ITenantProductRelService extends IBaseService<TenantProductRel> {

    /**
     * 失效站点的合作车队关系
     * @param productId
     */
    void loseCooperationTenant(Long productId, LoginInfo user);
    /**
     * 修改关系
     * @param productSaveIn
     * @param tenantId
     * @param productId
     * @param productName
     * @param serviceUserId
     * @return
     * @throws Exception
     */
    TenantProductRel updateProductRel(LoginInfo user, ProductSaveDto productSaveIn, Long tenantId,
                                      Long productId, String productName, Long serviceUserId, Integer isFleet);

    /**
     * 租户和站点的关系 通过租户和站点id
     *
     * @param tenantId
     * @param productId
     * @return
     */
    TenantProductRel getTenantProductRel(Long tenantId, Long productId);


    /**
     * 服务商站点保存
     *
     * @param productSaveIn
     * @return
     * @throws Exception
     */
    TenantProductRel save(ProductSaveDto productSaveIn, LoginInfo baseUser);


    /**
     * 新增关系
     *
     * @param isSaveRel
     * @param productSaveIn
     * @param productId
     * @param tenantId
     * @return
     */
    TenantProductRel saveProductRel(Boolean isSaveRel, ProductSaveDto productSaveIn, Long productId, Long tenantId ,LoginInfo baseUser);

    /**
     * 查询服务商下的所有站点
     *
     * @param serviceUserId
     * @param serviceType
     * @return
     */
    List<TenantProductRel> getServiceProductList(Long serviceUserId, Integer serviceType, Long tenantId);
    /**
     * 新增修改
     *
     * @param tenantProductRel
     * @param isUpdate
     */
     void saveOrUpdate(TenantProductRel tenantProductRel, Boolean isUpdate,LoginInfo baseUser);


    /**
     * 查看合作站点
     *
     * @param productId
     * @return
     * @throws Exception
     */
    TenantProductVo getTenantProduct(Long productId, Integer isShare, String accessToken);
    /**
     * @return 数据的总条数
     */
   Integer cooperationNum(Long productId);

    /**
     * 保存历史记录
     *
     * @param tenantProductRel
     * @param isDel
     * @throws Exception
     */
    void saveTenantProductRel(TenantProductRel tenantProductRel, Boolean isDel,LoginInfo user);

    /**
     * 删除站点与租户的关系
     *
     * @return
     */
    ResponseResult delTenantProduct(Long productId, String accessToken);

    /**
     * 服务商站点修改
     *
     * @param productSaveIn
     * @return
     * @throws Exception
     */
    TenantProductRel update(ProductSaveDto productSaveIn,LoginInfo baseUser);


    /**
     * 删除掉租户下站点
     *
     * @param userId
     * @param serviceType
     * @param tenantId
     */
    void delTenantProduct(Long userId, Integer serviceType, Long tenantId,LoginInfo user);


    /**
     * 查询站点 结算价
     *
     * @param tenantId
     * @param productIds
     * @return
     * @throws Exception
     */
    List<TenantProductRelOutDto> getTenantProductList(Long tenantId, List<Long> productIds);

    /**
     * 车队合作详情
     *
     * @return
     */
    CooperationProductVo getCooperationProduct(Long relId,String accessToken);


    /**
     * 查询关系，默认返回 车队合作的 ，无车队合作 就返回共享站点
     * @param tenantId
     * @param productId
     * @return
     */
    TenantProductRel getProductRelIsShare(Long tenantId,Long productId);

    /**
     * 	//根据站点id查询站点与租户信息
     * @param productId
     * @return
     */
    List<TenantProductRel> getTenantProductRelList(Long productId);
}
