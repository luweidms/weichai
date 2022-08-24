package com.youming.youche.record.provider.service.impl.tenant;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youming.youche.record.api.tenant.ITenantServiceRelService;
import com.youming.youche.record.domain.tenant.TenantServiceRel;
import com.youming.youche.record.provider.mapper.tenant.TenantServiceRelMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * <p>
 * 服务商与租户关系 服务实现类
 * </p>
 *
 * @author Terry
 * @since 2021-11-22
 */
@DubboService(version = "1.0.0")
public class TenantServiceRelServiceImpl extends ServiceImpl<TenantServiceRelMapper, TenantServiceRel>
		implements ITenantServiceRelService {

	@Autowired
	TenantServiceRelMapper tenantServiceRelMapper;

	@Override
	public List<TenantServiceRel> getTenantServiceRelList(Long serviceUserId, Long tenantId) throws Exception {
		QueryWrapper<TenantServiceRel> queryWrapper=new QueryWrapper<>();
		queryWrapper.eq("SERVICE_USER_ID",serviceUserId);
		if(null != tenantId){
			queryWrapper.eq("TENANT_ID",tenantId);
		}
		return tenantServiceRelMapper.selectList(queryWrapper);
	}

	@Override
	public TenantServiceRel getTenantServiceRel(long tenantId, long serviceId) {
		LambdaQueryWrapper<TenantServiceRel> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.eq(TenantServiceRel::getTenantId, tenantId);
		queryWrapper.eq(TenantServiceRel::getServiceUserId, serviceId);
		List<TenantServiceRel> list = this.list(queryWrapper);
		if (list != null && list.size() > 0) {
			return list.get(0);
		}
		return null;
	}

}
