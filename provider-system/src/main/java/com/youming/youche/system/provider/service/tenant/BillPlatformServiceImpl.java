package com.youming.youche.system.provider.service.tenant;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.system.api.tenant.IBillPlatformService;
import com.youming.youche.system.domain.tenant.BillPlatform;
import com.youming.youche.system.dto.BillPlatformDto;
import com.youming.youche.system.dto.Rate;
import com.youming.youche.system.provider.mapper.tenant.BillPlatformMapper;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 票据平台表 服务实现类
 * </p>
 *
 * @author Terry
 * @since 2022-01-22
 */
@DubboService(version = "1.0.0")
public class BillPlatformServiceImpl extends BaseServiceImpl<BillPlatformMapper, BillPlatform> implements IBillPlatformService {

    @Resource
    private BillPlatformMapper billPlatformMapper;

    @Override
    public List<BillPlatformDto> queryBillPlatformList(BillPlatform param) {
        return billPlatformMapper.queryBillPlatformList(param);
    }

    @Override
    public BillPlatform queryAllBillPlatformByUserId(long userId) {
        if (userId <= 0) {
            return null;
        }

        QueryWrapper<BillPlatform> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);

        List<BillPlatform> resultList = billPlatformMapper.selectList(queryWrapper);
        if (resultList != null && resultList.size() > 0)
            return resultList.get(0);
        else
            return null;
    }
}
