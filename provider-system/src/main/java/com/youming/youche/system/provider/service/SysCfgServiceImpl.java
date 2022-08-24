package com.youming.youche.system.provider.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.domain.SysCfg;
import com.youming.youche.commons.util.RedisUtil;
import com.youming.youche.conts.EnumConsts;
import com.youming.youche.system.api.ISysCfgService;
import com.youming.youche.system.domain.UserDataInfo;
import com.youming.youche.system.provider.mapper.SysCfgMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 系统配置表 服务实现类
 * </p>
 *
 * @author Terry
 * @since 2022-01-12
 */
@DubboService(version = "1.0.0")
@Service
public class SysCfgServiceImpl extends BaseServiceImpl<SysCfgMapper, SysCfg> implements ISysCfgService {

	@Resource
	RedisUtil redisUtil;

	@Resource
	SysCfgMapper sysCfgMapper;


	@Override
	public List<SysCfg> selectAll() {
		return baseMapper.selectList(null);
	}

	@Override
	public SysCfg get(String cfgName) {
		return (SysCfg) redisUtil.get(EnumConsts.SysCfg.SYS_CFG_NAME.concat(cfgName));
	}

	@Override
	public Object getCfgVal(String cfgName, int system, Class type) {
		SysCfg sysCfg = (SysCfg) redisUtil.get(EnumConsts.SysCfg.SYS_CFG_NAME.concat(cfgName));
		if (sysCfg != null && (system == -1 || system == sysCfg.getCfgSystem())) {
			if (type.equals(Integer.class)) {
				return Integer.valueOf(sysCfg.getCfgValue());
			} else if (type.equals(Double.class)) {
				return Double.parseDouble(sysCfg.getCfgValue());
			} else if (type.equals(Float.class)) {
				return Float.parseFloat(sysCfg.getCfgValue());
			} else {
				return !type.equals(Boolean.class) ? String.valueOf(sysCfg.getCfgValue()) : "1".equals(sysCfg.getCfgValue()) || "true".equalsIgnoreCase(sysCfg.getCfgValue());
			}
		} else {
			return null;
		}
	}

	@Override
	public Double getCfgVal(Long tenantId, String cfgName, Integer system) {
		return sysCfgMapper.getCfgVal(tenantId,cfgName,system);
	}

	@Override
	public SysCfg getSysCfg(String cfgName, String accessToken) {
		long tenantId = -1L;
		UserDataInfo user = (UserDataInfo) redisUtil.get("user_info:" + accessToken);
		if (user != null && user.getTenantId() != null) {
			tenantId = user.getTenantId();
		}
		QueryWrapper<SysCfg> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("cfg_name", cfgName).eq("TENANT_ID", tenantId);
		List<SysCfg> list = super.list(queryWrapper);
		if (list != null && list.size() > 0) {
			return list.get(0);
		}
		return null;
	}

	@Override
	public Boolean getCfgBooleanVal(String cfgName, Integer system) {
		Boolean flag = false;
		SysCfg cfg = sysCfgMapper.getCfgBooleanVal(cfgName);
		if (cfg != null && (system == -1 || system.equals(cfg.getCfgSystem()))) {
			flag = true;
		}
		return flag;
	}
}
