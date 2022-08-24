package com.youming.youche.finance.provider.service.ac;

import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.finance.api.ac.IAcBusiOrderLimitRelService;
import com.youming.youche.finance.domain.ac.AcBusiOrderLimitRel;
import com.youming.youche.finance.provider.mapper.ac.AcBusiOrderLimitRelMapper;
import org.apache.dubbo.config.annotation.DubboService;

import java.util.List;


/**
 * <p>
 * 支付限制表 服务实现类
 * </p>
 *
 * @author hzx
 * @since 2022-04-14
 */
@DubboService(version = "1.0.0")
public class AcBusiOrderLimitRelServiceImpl extends BaseServiceImpl<AcBusiOrderLimitRelMapper, AcBusiOrderLimitRel> implements IAcBusiOrderLimitRelService {


    @Override
    public List<AcBusiOrderLimitRel> getBusiOrderLimitRels() {
        return this.list();
    }
}
