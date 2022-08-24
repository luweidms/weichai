package com.youming.youche.market.api.facilitator;

import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.market.domain.facilitator.TenantProductRel;
import com.youming.youche.market.domain.facilitator.TenantServiceRel;
import com.youming.youche.market.domain.facilitator.TenantServiceRelVer;
import com.youming.youche.market.dto.facilitator.ProductSaveDto;

/**
 * <p>
 * 服务商与租户关系 服务类
 * </p>
 *
 * @author Terry
 * @since 2022-01-22
 */
public interface ITenantServiceRelService extends IBaseService<TenantServiceRel> {
    /**
     * 保存或修改
     * @param tenantServiceRel
     * @param isUpdate
     * @param user
     */
    void saveOrUpdateTenantServiceRel(TenantServiceRel tenantServiceRel, Boolean isUpdate, LoginInfo user);

    /**
     * 查询租户与服务商关系
     * @param tenantId
     * @param serviceUserId
     */
    TenantServiceRel getTenantServiceRel (Long tenantId, Long serviceUserId);


    /**
     * 查询租户与服务商关系
     * @param serviceUserId
     */
    TenantServiceRel getTenantServiceRel (Long serviceUserId);

    /**
     * 校验租户服务商关系有效性
     * @param tenantId
     * @param productSaveIn
     * @param isUpdate
     * @return
     */
    Boolean checkTenantService(Long tenantId, ProductSaveDto productSaveIn, Boolean isUpdate);
    /**
     * 保存与修改租户与服务商关系
     * @param tenantServiceRel
     * @param isUpdate
     */
    void saveOrUpdate(TenantServiceRel tenantServiceRel, Boolean isUpdate,LoginInfo user);


    /**
     * 修改租户服务商关联状态
     * @param userId
     * @param state
     * @return
     */
    void updateTenantServiceRelState(Long userId,Integer state);



}
