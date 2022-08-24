package com.youming.youche.market.api.facilitator;

import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.market.domain.facilitator.TenantProductRel;
import com.youming.youche.market.domain.facilitator.TenantProductRelVer;
import com.youming.youche.market.dto.facilitator.ProductSaveDto;

/**
 * <p>
 * 租户与站点关系表 服务类
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-01-29
 */
public interface ITenantProductRelVerService extends IBaseService<TenantProductRelVer> {
    /**
     * 保存站点与租户关系历史
     *
     * @param tenantProductRel
     * @throws Exception
     */
    void saveProductVer(TenantProductRel tenantProductRel, ProductSaveDto productSaveIn, Boolean isUpdate, LoginInfo baseUser);

    /**
     * 新增共享的时候，只记录版本表
     *
     * @param productSaveIn
     * @throws Exception
     */
    void saveProductRelVelShare(ProductSaveDto productSaveIn, TenantProductRel tenantProductRel,LoginInfo baseUser);

    /**
     * 保存历史
     * @param tenantProductRelVer
     */
     void saveProductHis(TenantProductRelVer tenantProductRelVer,LoginInfo baseUser);

    /**
     *  获取站点关系历史
     * @param relId
     * @return
     */
    TenantProductRelVer getTenantProductRelVer(Long relId);
}
