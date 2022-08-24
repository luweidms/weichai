package com.youming.youche.record.provider.service.impl.cm;

import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.record.api.cm.ICmCustomerLineSubwayVerService;
import com.youming.youche.record.domain.cm.CmCustomerLineSubwayVer;
import com.youming.youche.record.provider.mapper.cm.CmCustomerLineSubwayVerMapper;
import org.apache.dubbo.config.annotation.DubboService;

/**
 * <p>
 * 客户线路经停点历史表 服务实现类
 * </p>
 *
 * @author 向子俊
 * @since 2022-01-22
 */
@DubboService(version = "1.0.0")
public class CmCustomerLineSubwayVerServiceImpl extends BaseServiceImpl<CmCustomerLineSubwayVerMapper, CmCustomerLineSubwayVer>
        implements ICmCustomerLineSubwayVerService {


}
