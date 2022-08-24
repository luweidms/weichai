package com.youming.youche.system.provider.service.tenant;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.system.api.tenant.IBillSettingService;
import com.youming.youche.system.domain.tenant.BillSetting;
import com.youming.youche.system.dto.Rate;
import com.youming.youche.system.dto.RateItem;
import com.youming.youche.system.provider.mapper.tenant.BillSettingMapper;
import org.apache.dubbo.config.annotation.DubboService;

import java.util.List;

/**
 * <p>
 * 车队的开票设置 服务实现类
 * </p>
 *
 * @author Terry
 * @since 2022-01-09
 */
@DubboService(version = "1.0.0")
public class BillSettingServiceImpl extends BaseServiceImpl<BillSettingMapper, BillSetting>
		implements IBillSettingService {

	@Override
	public Rate getRateById(Long rateId) {
		return baseMapper.getRateById(rateId);
	}

	@Override
	public List<RateItem> queryRateItem(Long rateId) {
		return baseMapper.queryRateItem(rateId);
	}

	@Override
	public List<Rate> queryRateAll() {
		return baseMapper.queryRateAll();
	}

	@Override
	public BillSetting getBillSetting(Long tenantId) {
		QueryWrapper<BillSetting> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("tenant_id", tenantId);

		BillSetting info = baseMapper.selectOne(queryWrapper);
		if (null != info && null != info.getRateId()) {
			Rate rate = getRateById(info.getRateId());
			info.setRateName(rate.getRateName());
		}

		return baseMapper.selectOne(queryWrapper);
	}
}
