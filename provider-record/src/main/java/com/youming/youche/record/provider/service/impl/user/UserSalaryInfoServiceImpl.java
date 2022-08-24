package com.youming.youche.record.provider.service.impl.user;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.youming.youche.record.api.user.IUserSalaryInfoService;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.record.domain.user.UserSalaryInfo;
import com.youming.youche.record.provider.mapper.user.UserSalaryInfoMapper;
import org.apache.dubbo.config.annotation.DubboService;

import java.util.List;

/**
 * <p>
 * 司机里程模式表 服务实现类
 * </p>
 *
 * @author Terry
 * @since 2021-11-22
 */
@DubboService(version = "1.0.0")
public class UserSalaryInfoServiceImpl extends BaseServiceImpl<UserSalaryInfoMapper, UserSalaryInfo>
		implements IUserSalaryInfoService {

	@Override
	public List<UserSalaryInfo> queryUserSalaryInfos(long userId, int salaryPattern) {
		QueryWrapper<UserSalaryInfo> queryWrapper=new QueryWrapper<>();
		queryWrapper.eq("user_id",userId).eq("salary_Pattern",salaryPattern).orderByAsc("start_Num");
		return baseMapper.selectList(queryWrapper);
	}

	@Override
	public List<UserSalaryInfo> getuserSalarInfo(Long userId) {
		QueryWrapper<UserSalaryInfo> queryWrapper=new QueryWrapper<>();
		queryWrapper.eq("user_id",userId).orderByAsc("start_Num");
		return baseMapper.selectList(queryWrapper);
	}
}
