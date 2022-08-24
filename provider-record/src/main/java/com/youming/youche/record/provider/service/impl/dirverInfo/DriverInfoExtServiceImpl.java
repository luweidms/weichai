package com.youming.youche.record.provider.service.impl.dirverInfo;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youming.youche.record.api.dirverInfo.IDriverInfoExtService;
import com.youming.youche.record.api.user.IUserDataInfoRecordService;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.record.domain.dirverInfo.DriverInfoExt;
import com.youming.youche.record.domain.user.UserDataInfo;
import com.youming.youche.record.provider.mapper.dirverInfo.DriverInfoExtMapper;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;


/**
 * <p>
 * 司机信息扩展表 服务实现类
 * </p>
 *
 * @author Terry
 * @since 2021-11-22
 */
@DubboService(version = "1.0.0")
public class DriverInfoExtServiceImpl extends ServiceImpl<DriverInfoExtMapper, DriverInfoExt>
        implements IDriverInfoExtService {

    @Resource
    IUserDataInfoRecordService iUserDataInfoRecordService;

    @Override
    public void updateLuGeAuthState(Long userId, Boolean authState, String remark, Integer processState) {
        if (null == userId) {
            throw new BusinessException("错误的参数");
        }
        DriverInfoExt driverInfoExt = getDriverInfoExtByUserId(userId);
        if (null == driverInfoExt) {
            driverInfoExt = create(userId);
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
        Date date = new Date();
        Instant instant = date.toInstant();
        ZoneId zoneId = ZoneId.systemDefault();
        LocalDateTime localDateTime = instant.atZone(zoneId).toLocalDateTime();
        driverInfoExt.setLgUpdateDate(localDateTime);
        this.updateById(driverInfoExt);
    }

    /**
     * 根据userId创建一条扩展记录
     * @param userId
     * @return
     */
    private DriverInfoExt create(Long userId) {
        UserDataInfo userDataInfo = iUserDataInfoRecordService.getUserDataInfo(userId);
        if (null == userDataInfo) {
            throw new BusinessException("不存在的用户");
        }
        Date date = new Date();
        Instant instant = date.toInstant();
        ZoneId zoneId = ZoneId.systemDefault();
        LocalDateTime now = instant.atZone(zoneId).toLocalDateTime();
        DriverInfoExt driverInfoExt = new DriverInfoExt();
        driverInfoExt.setCreateDate(now);
        driverInfoExt.setLgUpdateDate(now);
        driverInfoExt.setLuGeAuthState(false);                      //路哥认证状态默认false
        driverInfoExt.setProcessState(0);
        driverInfoExt.setUserId(userDataInfo.getId());
        this.save(driverInfoExt);
        return driverInfoExt;
    }



    public DriverInfoExt getDriverInfoExtByUserId(Long userId) {
        if (null == userId) {
            return null;
        }
        QueryWrapper<DriverInfoExt> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("user_id",userId);
        List<DriverInfoExt> list=baseMapper.selectList(queryWrapper);
        if(list!=null && list.size()>0){
            return list.get(0);
        }
        return null;
    }

    @Override
    public void resetLuGeAuthState(Long userId) {
        //修改为未认证、未处理
        updateLuGeAuthState(userId, false, null, 0);
    }

}
