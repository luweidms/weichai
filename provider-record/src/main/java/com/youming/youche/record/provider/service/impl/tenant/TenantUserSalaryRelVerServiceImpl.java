package com.youming.youche.record.provider.service.impl.tenant;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youming.youche.record.api.tenant.ITenantUserSalaryRelVerService;
import com.youming.youche.record.common.SysStaticDataEnum;
import com.youming.youche.record.domain.tenant.TenantUserSalaryRelVer;
import com.youming.youche.record.provider.mapper.tenant.TenantUserSalaryRelVerMapper;
import org.apache.dubbo.config.annotation.DubboService;

import java.util.List;

/**
 * <p>
 * 租户自有司机收入信息 服务实现类
 * </p>
 *
 * @author Terry
 * @since 2021-11-22
 */
@DubboService(version = "1.0.0")
public class TenantUserSalaryRelVerServiceImpl extends ServiceImpl<TenantUserSalaryRelVerMapper, TenantUserSalaryRelVer>
		implements ITenantUserSalaryRelVerService {

	@Override
	public List<TenantUserSalaryRelVer> getTenantUserSalaryRelVers(long relId) throws Exception {
		QueryWrapper<TenantUserSalaryRelVer> queryWrapper=new QueryWrapper<>();
		queryWrapper.eq("rel_id",relId).eq("ver_state", SysStaticDataEnum.VER_STATE.VER_STATE_Y).orderByDesc("id");
		return baseMapper.selectList(queryWrapper);
	}

	@Override
	public List<TenantUserSalaryRelVer> getTenantUserSalaryRelVer(Long tenantUserRelId, int verState) {
		QueryWrapper<TenantUserSalaryRelVer> queryWrapper=new QueryWrapper<>();
		queryWrapper.eq("rel_id",tenantUserRelId).eq("ver_state", verState);
		return baseMapper.selectList(queryWrapper);
	}

	@Override
	public TenantUserSalaryRelVer getTenantUserSalaryRelVer(long tenantUserRelId) {
		QueryWrapper<TenantUserSalaryRelVer> queryWrapper=new QueryWrapper<>();
		queryWrapper.eq("rel_id",tenantUserRelId).eq("ver_state", SysStaticDataEnum.VER_STATE.VER_STATE_Y);
		List <TenantUserSalaryRelVer> list=baseMapper.selectList(queryWrapper);
		if (list!=null && list.size()>0){
			return  list.get(0);
		}
		return null;
	}
}
