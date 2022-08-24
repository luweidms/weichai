package com.youming.youche.record.provider.service.impl.user;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.youming.youche.record.api.user.IUserSalaryInfoVerService;
import com.youming.youche.record.common.SysStaticDataEnum;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.record.domain.user.UserSalaryInfoVer;
import com.youming.youche.record.provider.mapper.user.UserSalaryInfoVerMapper;
import org.apache.dubbo.config.annotation.DubboService;

import java.util.List;

/**
 * <p>
 * 司机里程模式版本表 服务实现类
 * </p>
 *
 * @author Terry
 * @since 2021-11-22
 */
@DubboService(version = "1.0.0")
public class UserSalaryInfoVerServiceImpl extends BaseServiceImpl<UserSalaryInfoVerMapper, UserSalaryInfoVer>
		implements IUserSalaryInfoVerService {

	@Override
	public List<UserSalaryInfoVer> getUserSalaryInfoVers(long userId) throws Exception {
		QueryWrapper<UserSalaryInfoVer>queryWrapper=new QueryWrapper<>();
		queryWrapper.eq("user_id",userId);
		queryWrapper.eq("ver_state", SysStaticDataEnum.VER_STATE.VER_STATE_Y);
		queryWrapper.orderByAsc("start_num");
		return baseMapper.selectList(queryWrapper);
	}

	@Override
	public List<UserSalaryInfoVer> getUserSalaryInfoVer(Long userId, int verState) {
		QueryWrapper<UserSalaryInfoVer>queryWrapper=new QueryWrapper<>();
		queryWrapper.eq("user_id",userId);
		queryWrapper.eq("ver_state", verState);
		queryWrapper.orderByAsc("start_num");
		return baseMapper.selectList(queryWrapper);
	}
}
