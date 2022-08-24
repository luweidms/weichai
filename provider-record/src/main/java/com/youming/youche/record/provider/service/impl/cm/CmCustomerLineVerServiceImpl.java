package com.youming.youche.record.provider.service.impl.cm;

import com.youming.youche.record.api.cm.ICmCustomerLineVerService;
import com.youming.youche.record.domain.cm.CmCustomerLineVer;
import com.youming.youche.record.provider.mapper.cm.CmCustomerLineVerMapper;
import com.youming.youche.commons.base.BaseServiceImpl;
import org.apache.dubbo.config.annotation.DubboService;


/**
 * <p>
 * 客户线路信息历史表 服务实现类
 * </p>
 *
 * @author Terry
 * @since 2022-02-23
 */
@DubboService(version = "1.0.0")
public class CmCustomerLineVerServiceImpl extends BaseServiceImpl<CmCustomerLineVerMapper, CmCustomerLineVer> implements ICmCustomerLineVerService {


}
