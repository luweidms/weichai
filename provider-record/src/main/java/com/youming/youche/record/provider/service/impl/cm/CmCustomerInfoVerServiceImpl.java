package com.youming.youche.record.provider.service.impl.cm;


import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.record.api.cm.ICmCustomerInfoVerService;
import com.youming.youche.record.domain.cm.CmCustomerInfoVer;
import com.youming.youche.record.provider.mapper.cm.CmCustomerInfoVerMapper;
import org.apache.dubbo.config.annotation.DubboService;

/**
 * <p>
 * 客户信息历史表/客户档案历史表 服务实现类
 * </p>
 *
 * @author Terry
 * @since 2022-02-20
 */
@DubboService(version = "1.0.0")
public class CmCustomerInfoVerServiceImpl extends BaseServiceImpl<CmCustomerInfoVerMapper, CmCustomerInfoVer> implements ICmCustomerInfoVerService {


}
