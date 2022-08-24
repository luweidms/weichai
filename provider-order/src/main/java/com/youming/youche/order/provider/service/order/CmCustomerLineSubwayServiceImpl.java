package com.youming.youche.order.provider.service.order;

import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.order.domain.order.CmCustomerLineSubway;
import com.youming.youche.order.provider.mapper.order.CmCustomerLineSubwayMapper;
import org.apache.dubbo.config.annotation.DubboService;


/**
* <p>
    * 客户线路经停点表 服务实现类
    * </p>
* @author wuhao
* @since 2022-03-30
*/
@DubboService(version = "1.0.0")
    public class CmCustomerLineSubwayServiceImpl extends BaseServiceImpl<CmCustomerLineSubwayMapper, CmCustomerLineSubway> implements IBaseService<CmCustomerLineSubway> {


    @Override
    public boolean save(CmCustomerLineSubway entity) {
        return false;
    }
}
