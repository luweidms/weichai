package com.youming.youche.market.provider.service.facilitator;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.market.api.facilitator.IUserDataInfoVerService;
import com.youming.youche.market.domain.facilitator.UserDataInfoVer;
import com.youming.youche.market.provider.mapper.facilitator.UserDataInfoVerMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.youming.youche.conts.SysStaticDataEnum.VER_STATE.VER_STATE_Y;


/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-01-25
 */
@DubboService(version = "1.0.0")
@Service
public class UserDataInfoVerServiceImpl extends BaseServiceImpl<UserDataInfoVerMapper, UserDataInfoVer> implements IUserDataInfoVerService {


    @Override
    public UserDataInfoVer getUserDataInfoVerNoTenant(Long userId) {
        LambdaQueryWrapper<UserDataInfoVer> lambdaQueryWrapper=new QueryWrapper<UserDataInfoVer>().lambda();
        lambdaQueryWrapper.eq(UserDataInfoVer::getUserId,userId)
                          .eq(UserDataInfoVer::getVerState,VER_STATE_Y)
                          .orderByDesc(UserDataInfoVer::getId);
        List<UserDataInfoVer> userDataInfoVer = this.list(lambdaQueryWrapper);
        if(userDataInfoVer==null||userDataInfoVer.isEmpty()){
            return null;
        }
        return userDataInfoVer.get(0);
    }
}
