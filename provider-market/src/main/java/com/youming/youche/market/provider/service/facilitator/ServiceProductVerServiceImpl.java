package com.youming.youche.market.provider.service.facilitator;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.market.api.facilitator.IServiceProductVerService;
import com.youming.youche.market.domain.facilitator.ServiceProduct;
import com.youming.youche.market.domain.facilitator.ServiceProductVer;
import com.youming.youche.market.dto.facilitator.ProductSaveDto;
import com.youming.youche.market.provider.mapper.facilitator.ServiceProductVerMapper;
import com.youming.youche.market.provider.utis.LocalDateTimeUtil;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;


/**
 * <p>
 * 服务商站点版本表 服务实现类
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-01-28
 */
@DubboService(version = "1.0.0")
@Service
public class ServiceProductVerServiceImpl extends BaseServiceImpl<ServiceProductVerMapper, ServiceProductVer> implements IServiceProductVerService {


    @Override
    public void saveServiceProductVer(ProductSaveDto productSaveIn, ServiceProduct serviceProduct, Boolean isUpdate, LoginInfo baseUser) {
        ServiceProductVer serviceProductVer = new ServiceProductVer();
        BeanUtils.copyProperties(serviceProduct,serviceProductVer);
        serviceProductVer.setProductId(serviceProduct.getId());
        if (isUpdate) {
            if (productSaveIn != null) {
                BeanUtils.copyProperties(productSaveIn,serviceProductVer);
            }
            serviceProductVer.setState(serviceProduct.getState());
            serviceProductVer.setProductId(serviceProduct.getId());
        } else {
            if (productSaveIn != null) {
                serviceProductVer.setIsShare(productSaveIn.getIsShare());
                serviceProductVer.setIsBillAbility(productSaveIn.getIsBillAbility());
            }
        }
        serviceProductVer.setUpdateDate(LocalDateTimeUtil.presentTime());
        serviceProductVer.setOpId(baseUser.getId());
        this.save(serviceProductVer);
    }

    @Override
    public ServiceProductVer getServiceProductVer(Long productId) {
        LambdaQueryWrapper<ServiceProductVer> lambda=new QueryWrapper<ServiceProductVer>().lambda();
        lambda.eq(ServiceProductVer::getProductId,productId)
              .orderByDesc(ServiceProductVer::getId)
                .last("limit 1");
        return this.getOne(lambda);
    }

}
