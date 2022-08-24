package com.youming.youche.record.provider.service.impl.user;



import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.youming.youche.record.api.user.IUserDataInfoVerService;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.conts.SysStaticDataEnum;
import com.youming.youche.record.domain.user.UserDataInfoVer;
import com.youming.youche.record.provider.mapper.user.UserDataInfoVerMapper;
import org.apache.dubbo.config.annotation.DubboService;

import java.util.List;

/**
 * <p>
 * 用户资料信息 服务实现类
 * </p>
 *
 * @author Terry
 * @since 2021-11-22
 */
@DubboService(version = "1.0.0")
public class UserDataInfoVerServiceImpl extends BaseServiceImpl<UserDataInfoVerMapper, UserDataInfoVer>
		implements IUserDataInfoVerService {




	@Override
	public UserDataInfoVer getUserDataInfoVerNoTenant(Long userId) {
		QueryWrapper<UserDataInfoVer> queryWrapper=new QueryWrapper<>();
		queryWrapper.eq("user_id",userId).eq("ver_state", SysStaticDataEnum.VER_STATE.VER_STATE_Y).orderByDesc("id");
		List<UserDataInfoVer> userDataInfoVer=baseMapper.selectList(queryWrapper);
		if(userDataInfoVer==null||userDataInfoVer.isEmpty()){
			return null;
		}
		return userDataInfoVer.get(0);
	}

	@Override
	public Long saveById(UserDataInfoVer userDataInfoVer) {
		save(userDataInfoVer);
		return userDataInfoVer.getId();
	}

    @Override
    public UserDataInfoVer getUserDataInfoVer(Long userId) {
        LambdaQueryWrapper<UserDataInfoVer> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserDataInfoVer::getVerState, SysStaticDataEnum.VER_STATE.VER_STATE_Y);
        queryWrapper.eq(UserDataInfoVer::getUserId, userId);
        List<UserDataInfoVer> userDataInfoVer = this.list(queryWrapper);
        if (userDataInfoVer == null || userDataInfoVer.isEmpty()) {
            return null;
        }
        return userDataInfoVer.get(0);
    }

    @Override
    public List<UserDataInfoVer> getUserDataInfoVerList(Long userId, Integer verState) {
        LambdaQueryWrapper<UserDataInfoVer> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserDataInfoVer::getVerState, verState);
        queryWrapper.eq(UserDataInfoVer::getUserId, userId);
        return this.list(queryWrapper);
    }

}
