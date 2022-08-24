package com.youming.youche.market.api.facilitator;

import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.market.domain.facilitator.ServiceProduct;
import com.youming.youche.market.domain.facilitator.ServiceProductVer;
import com.youming.youche.market.dto.facilitator.ProductSaveDto;

/**
 * <p>
 * 服务商站点版本表 服务类
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-01-28
 */
public interface IServiceProductVerService extends IBaseService<ServiceProductVer> {

    /**
     * 保存历史记录
     * @param productSaveIn
     * @param serviceProduct
     * @param isUpdate
     */
   void saveServiceProductVer(ProductSaveDto productSaveIn, ServiceProduct serviceProduct, Boolean isUpdate, LoginInfo baseUser);

    /**
     * 查询版本数据
     *
     * @return
     */
     ServiceProductVer getServiceProductVer(Long productId);


}
