package com.youming.youche.record.provider.service.impl.user;



import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.youming.youche.record.api.user.IUserLineRelVerService;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.record.domain.user.UserLineRelVer;
import com.youming.youche.record.provider.mapper.user.UserLineRelVerMapper;
import org.apache.dubbo.config.annotation.DubboService;

import java.util.List;

/**
 * <p>
 * 用户心愿线路版本表 服务实现类
 * </p>
 *
 * @author Terry
 * @since 2021-11-22
 */
@DubboService(version = "1.0.0")
public class UserLineRelVerServiceImpl extends BaseServiceImpl<UserLineRelVerMapper, UserLineRelVer>
		implements IUserLineRelVerService {

	@Override
	public List<UserLineRelVer> getUserLineRelVer(Long userId, Long tenantId, int verState) {
		QueryWrapper<UserLineRelVer> queryWrapper=new QueryWrapper<>();
		queryWrapper.eq("user_id",userId)
				.eq("tenant_id",tenantId)
				.eq("ver_state",verState);
		return baseMapper.selectList(queryWrapper);
	}
}
