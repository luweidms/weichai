package com.youming.youche.record.provider.service.impl.tenant;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youming.youche.record.api.tenant.ITenantUserRelVerService;
import com.youming.youche.conts.SysStaticDataEnum;
import com.youming.youche.record.domain.tenant.TenantUserRelVer;
import com.youming.youche.record.provider.mapper.tenant.TenantUserRelVerMapper;
import org.apache.dubbo.config.annotation.DubboService;

import java.util.List;

/**
 * <p>
 * 租户会员关系 服务实现类
 * </p>
 *
 * @author Terry
 * @since 2021-11-22
 */
@DubboService(version = "1.0.0")
public class TenantUserRelVerServiceImpl extends ServiceImpl<TenantUserRelVerMapper, TenantUserRelVer>
		implements ITenantUserRelVerService {

	@Override
	public TenantUserRelVer getTenantUserRelVerByUserId(Long userId) throws Exception {
		QueryWrapper<TenantUserRelVer> queryWrapper=new QueryWrapper<>();
		queryWrapper.eq("user_id",userId).eq("ver_state", SysStaticDataEnum.VER_STATE.VER_STATE_Y);
		List<TenantUserRelVer> list=baseMapper.selectList(queryWrapper);
		if(list==null||list.isEmpty()){
			return null;
		}
		return list.get(0);
	}

	@Override
	public TenantUserRelVer getTenantUserRelVer(Long userId, Long tenantId) {
		QueryWrapper<TenantUserRelVer> queryWrapper=new QueryWrapper<>();
		queryWrapper.eq("user_id",userId)
				.eq("ver_state", SysStaticDataEnum.VER_STATE.VER_STATE_Y)
				.eq("tenant_id",tenantId).orderByDesc("id");
		List<TenantUserRelVer> list=baseMapper.selectList(queryWrapper);
		if(list==null||list.isEmpty()){
			return null;
		}
		return list.get(0);
	}

	@Override
	public List<TenantUserRelVer> getTenantUserRelVer(Long userId, Long tenantId, int verState) {
		QueryWrapper<TenantUserRelVer> queryWrapper=new QueryWrapper<>();
		queryWrapper.eq("user_id",userId)
				.eq("ver_state", verState)
				.eq("tenant_id",tenantId);
		return  baseMapper.selectList(queryWrapper);
	}

	@Override
	public List<TenantUserRelVer> getTenantUserRelVerByRelId(Long id) {
		LambdaQueryWrapper<TenantUserRelVer> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.eq(TenantUserRelVer::getRelId, id);
		return this.list(queryWrapper);
	}

}
