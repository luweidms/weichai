//package com.youming.youche.record.provider.service.impl.sys;
//
//
//import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
//import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
//import com.youming.youche.record.api.sys.ISysTenantDefService;
//import com.youming.youche.record.common.SysStaticDataEnum;
//import com.youming.youche.record.domain.sys.SysTenantDef;
//import com.youming.youche.record.provider.mapper.sys.SysTenantDefMapper;
//import org.apache.dubbo.config.annotation.DubboService;
//import org.springframework.beans.factory.annotation.Autowired;
//
///**
// * <p>
// * 车队表 服务实现类
// * </p>
// *
// * @author Terry
// * @since 2021-11-22
// */
//@DubboService(version = "1.0.0")
//public class SysTenantDefServiceImpl extends ServiceImpl<SysTenantDefMapper, SysTenantDef>
//		implements ISysTenantDefService {
//
//	@Autowired
//	SysTenantDefMapper sysTenantDefMapper;
//
//	@Override
//	public SysTenantDef getSysTenantDefByAdminUserId(Long userId) {
//		QueryWrapper<SysTenantDef> queryWrapper=new QueryWrapper<>();
//		queryWrapper.eq("ADMIN_USER",userId);
//		queryWrapper.eq("STATE",1);
//		return sysTenantDefMapper.selectOne(queryWrapper);
//	}
//
//	@Override
//	public SysTenantDef getSysTenantDef(Long tenantId) {
//		QueryWrapper<SysTenantDef> queryWrapper=new QueryWrapper<>();
//		queryWrapper.eq("Id",tenantId);
//		queryWrapper.eq("STATE", SysStaticDataEnum.SYS_STATE_DESC.STATE_YES);
//		return sysTenantDefMapper.selectOne(queryWrapper);
//	}
//
//	@Override
//	public SysTenantDef getSysTenantDef(long tenantId, boolean isAllTenant) {
//		if(!isAllTenant){
//			return this.getSysTenantDef(tenantId);
//		}
//		QueryWrapper<SysTenantDef> queryWrapper=new QueryWrapper<>();
//		queryWrapper.eq("Id",tenantId);
//		return baseMapper.selectOne(queryWrapper);
//	}
//}
