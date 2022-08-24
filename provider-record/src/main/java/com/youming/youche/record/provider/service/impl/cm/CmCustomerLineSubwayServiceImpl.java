package com.youming.youche.record.provider.service.impl.cm;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.record.api.cm.ICmCustomerLineSubwayService;
import com.youming.youche.record.domain.cm.CmCustomerLineSubway;
import com.youming.youche.record.provider.mapper.cm.CmCustomerLineSubwayMapper;
import org.apache.dubbo.config.annotation.DubboService;

import java.util.List;

/**
 * <p>
 * 客户线路经停点表 服务实现类
 * </p>
 *
 * @author 向子俊
 * @since 2022-01-22
 */
@DubboService(version = "1.0.0")
public class CmCustomerLineSubwayServiceImpl extends BaseServiceImpl<CmCustomerLineSubwayMapper, CmCustomerLineSubway> implements ICmCustomerLineSubwayService {


    @Override
    public List<CmCustomerLineSubway> getCustomerLineSubwayList(long lineId) {
        LambdaQueryWrapper<CmCustomerLineSubway> lambda= Wrappers.lambdaQuery();
        lambda.eq(CmCustomerLineSubway::getLineId,lineId);
        return this.list(lambda);
    }
}
