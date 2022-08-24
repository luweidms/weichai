package com.youming.youche.market.provider.service.facilitator;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.market.api.facilitator.IDriverInfoExtService;
import com.youming.youche.market.api.facilitator.IUserDataInfoMarketService;
import com.youming.youche.market.domain.facilitator.DriverInfoExt;
import com.youming.youche.market.domain.facilitator.UserDataInfo;
import com.youming.youche.market.provider.mapper.facilitator.DriverInfoExtMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


/**
 * <p>
 * 司机信息扩展表 服务实现类
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-02-01
 */
@DubboService(version = "1.0.0")
@Service
public class DriverInfoExtServiceImpl extends BaseServiceImpl<DriverInfoExtMapper, DriverInfoExt> implements IDriverInfoExtService {

    @Autowired
    private IUserDataInfoMarketService userDataInfoService;

    @Override
    public void updateLuGeAuthState(Long userId, Boolean authState, String remark, Integer processState) {
        if (null == userId) {
            throw new BusinessException("错误的参数");
        }

        DriverInfoExt driverInfoExt =getDriverInfoExtByUserId(userId);
        if (null == driverInfoExt) {
            driverInfoExt = createDriverInfoExt(userId);
        }

        if (null != authState) {
            driverInfoExt.setLuGeAuthState(authState);
        }
        if (null != processState) {
            driverInfoExt.setProcessState(processState);
        }
        if (null != remark) {
            driverInfoExt.setRemark(remark);
        }
        driverInfoExt.setUpdateTime(LocalDateTime.now());
        this.update(driverInfoExt);
    }

    @Override
    public DriverInfoExt getDriverInfoExtByUserId(Long userId) {
        if (null == userId) {
            return null;
        }
        LambdaQueryWrapper<DriverInfoExt> lambda=new QueryWrapper<DriverInfoExt>().lambda();
        lambda.eq(DriverInfoExt::getUserId,userId);
        return this.getOne(lambda);
    }

    @Override
    public DriverInfoExt createDriverInfoExt(Long userId) {
        UserDataInfo userDataInfo = userDataInfoService.get(userId);
        if (null == userDataInfo) {
            throw new BusinessException("不存在的用户");
        }

        DriverInfoExt driverInfoExt = new DriverInfoExt();
        driverInfoExt.setCreateTime(LocalDateTime.now());
        driverInfoExt.setUpdateTime(LocalDateTime.now());
        driverInfoExt.setLuGeAuthState(false);                      //路哥认证状态默认false
        driverInfoExt.setProcessState(0);
        driverInfoExt.setUserId(userDataInfo.getId());
        this.save(driverInfoExt);

        return driverInfoExt;
    }
}
