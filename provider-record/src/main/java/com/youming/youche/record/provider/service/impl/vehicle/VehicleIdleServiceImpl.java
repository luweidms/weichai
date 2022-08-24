package com.youming.youche.record.provider.service.impl.vehicle;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.util.LoginUtils;
import com.youming.youche.commons.util.RedisUtil;
import com.youming.youche.record.api.vehicle.IVehicleIdleService;
import com.youming.youche.record.domain.vehicle.VehicleIdle;
import com.youming.youche.record.provider.mapper.vehicle.VehicleIdleMapper;
import com.youming.youche.record.provider.utils.SysStaticDataRedisUtils;
import com.youming.youche.record.vo.IdleVehiclesVo;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>
 * 闲置车辆表 服务实现类
 * </p>
 *
 * @author wuhao
 * @since 2022-05-18
 */
@DubboService(version = "1.0.0")
public class VehicleIdleServiceImpl extends BaseServiceImpl<VehicleIdleMapper, VehicleIdle> implements IVehicleIdleService {

    @Resource
    private RedisUtil redisUtil;

    @Resource
    LoginUtils loginUtils;

    @Override
    public List<IdleVehiclesVo> queryIdleVehicles(IdleVehiclesVo idleVehiclesVo, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        if (loginInfo == null) {
            throw new BusinessException("用户信息实效、请重新登陆");
        }
        List<IdleVehiclesVo> idleVehiclesVos = baseMapper.queryIdleVehicles(idleVehiclesVo, loginInfo.getTenantId());
        //获取车辆位置信息、判断闲置车辆位置信息（需对接车联网开发）
        if (!StrUtil.isEmpty(idleVehiclesVo.getLatStr()) || !StrUtil.isEmpty(idleVehiclesVo.getLngStr())) {
            //暂时过滤数据，等对接车联网完善开发

            return new ArrayList<>();
        } else {
            if (idleVehiclesVos != null) {
                for (IdleVehiclesVo idlevehicle : idleVehiclesVos
                ) {
                    if (idlevehicle.getVehicleStatus() != null) {
                        idlevehicle.setVehicleStatusName(SysStaticDataRedisUtils.getSysStaticDataById2String(redisUtil, "VEHICLE_STATUS", idlevehicle.getVehicleStatus()));
                    }else{
                        idlevehicle.setVehicleStatusName("");
                    }
                    if (idlevehicle.getVehicleLength() != null) {
                        idlevehicle.setVehicleLengthName(SysStaticDataRedisUtils.getSysStaticDataById2String(redisUtil, "VEHICLE_LENGTH", idlevehicle.getVehicleLength()));
                    }else{
                        idlevehicle.setVehicleLengthName("");
                    }
                }
            }
        }
        return idleVehiclesVos;
    }

    @Override
    public List<VehicleIdle> queryVehicleIdle(Long vid) {
        LambdaQueryWrapper<VehicleIdle> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.eq(VehicleIdle::getVid,vid).orderByDesc(VehicleIdle::getCreateTime);
        return super.list(lambdaQueryWrapper);
    }
}
