package com.youming.youche.market.provider.service.facilitator;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.market.api.facilitator.IBillPlatformService;
import com.youming.youche.market.domain.facilitator.BillPlatform;
import com.youming.youche.market.provider.mapper.facilitator.BillPlatformMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * <p>
 * 票据平台表 服务实现类
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-02-14
 */
@DubboService(version = "1.0.0")
@Service
public class BillPlatformServiceImpl extends BaseServiceImpl<BillPlatformMapper, BillPlatform> implements IBillPlatformService {


    @Override
    public BillPlatform queryBillPlatformByUserId(Long userId) {
        if (userId <= 0) {
            return null;
        }
        LambdaQueryWrapper<BillPlatform> lambda=new QueryWrapper<BillPlatform>().lambda();
        lambda.eq(BillPlatform::getUserId,userId);
        List<BillPlatform> resultList = this.list(lambda);
        if (resultList != null && resultList.size() > 0)
            return (BillPlatform) resultList.get(0);
        else
            return null;
    }
}
