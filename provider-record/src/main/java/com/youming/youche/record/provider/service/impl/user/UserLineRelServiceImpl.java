package com.youming.youche.record.provider.service.impl.user;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.record.api.tenant.ITenantUserSalaryRelService;
import com.youming.youche.record.api.tenant.ITenantUserSalaryRelVerService;
import com.youming.youche.record.api.user.IUserDataInfoRecordService;
import com.youming.youche.record.api.user.IUserLineRelService;
import com.youming.youche.record.api.user.IUserLineRelVerService;
import com.youming.youche.record.api.user.IUserSalaryInfoService;
import com.youming.youche.record.api.user.IUserSalaryInfoVerService;
import com.youming.youche.record.common.SysStaticDataEnum;
import com.youming.youche.record.domain.tenant.TenantUserRel;
import com.youming.youche.record.domain.tenant.TenantUserSalaryRel;
import com.youming.youche.record.domain.tenant.TenantUserSalaryRelVer;
import com.youming.youche.record.domain.user.UserLineRel;
import com.youming.youche.record.domain.user.UserLineRelVer;
import com.youming.youche.record.domain.user.UserSalaryInfo;
import com.youming.youche.record.domain.user.UserSalaryInfoVer;
import com.youming.youche.record.provider.mapper.user.UserLineRelMapper;
import com.youming.youche.util.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 用户心愿线路表 服务实现类
 * </p>
 *
 * @author Terry
 * @since 2021-11-22
 */
@DubboService(version = "1.0.0")
public class UserLineRelServiceImpl extends BaseServiceImpl<UserLineRelMapper, UserLineRel> implements IUserLineRelService {


    @Resource
    IUserLineRelVerService iUserLineRelVerService;

    @Resource
    IUserDataInfoRecordService iUserDataInfoRecordService;

    @Resource
    ITenantUserSalaryRelVerService iTenantUserSalaryRelVerService;

    @Resource
    IUserSalaryInfoVerService iUserSalaryInfoVerService;

    @Resource
    ITenantUserSalaryRelService iTenantUserSalaryRelService;

    @Resource
    IUserSalaryInfoService iUserSalaryInfoService;

    @Override
    public List<UserLineRel> getUserLineRelByUserId(Long userId, Long tenantId) {
        QueryWrapper<UserLineRel> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("user_id",userId).eq("tenant_id",tenantId);
        return baseMapper.selectList(queryWrapper);
    }

    @Override
    public void removeUserLineRel(Long userId, Long tenantId,String accessToken) throws Exception {
        List<UserLineRelVer> verList =iUserLineRelVerService.getUserLineRelVer(userId, tenantId, SysStaticDataEnum.VER_STATE.VER_STATE_Y);
        if (CollectionUtils.isNotEmpty(verList)) {
            for (UserLineRelVer ver : verList) {
                ver.setVerState(SysStaticDataEnum.VER_STATE.VER_STATE_N);
                iUserLineRelVerService.updateById(ver);
            }
        }
        LoginInfo userDataInfo= iUserDataInfoRecordService.getLoginInfoByAccessToken(accessToken);
        List<UserLineRel> userLineRelList =getUserLineRelByUserId(userId, tenantId);
        Date date = new Date();
        Instant instant = date.toInstant();
        ZoneId zoneId = ZoneId.systemDefault();
        LocalDateTime localDateTime = instant.atZone(zoneId).toLocalDateTime();
        if (CollectionUtils.isNotEmpty(userLineRelList)) {
            for (UserLineRel userLineRel : userLineRelList) {
                UserLineRelVer userLineRelVer = new UserLineRelVer();
                BeanUtils.copyProperties(userLineRelVer, userLineRel);
                userLineRelVer.setUpdateOpId(userDataInfo.getUserInfoId());
                userLineRelVer.setUpdateDate(localDateTime);
                userLineRelVer.setVerState(SysStaticDataEnum.VER_STATE.VER_STATE_HIS);
                iUserLineRelVerService.save(userLineRelVer);
                this.removeById(userLineRel);
            }
        }
    }

    @Override
    public void removeTenantUserSalaryRel(TenantUserRel tenantUserRel) throws Exception {
        //把审核表可以审的数据改为不可审
        //处理工资信息
        List<TenantUserSalaryRelVer> verList = iTenantUserSalaryRelVerService.getTenantUserSalaryRelVer(tenantUserRel.getId(), SysStaticDataEnum.VER_STATE.VER_STATE_Y);
        if (CollectionUtils.isNotEmpty(verList)) {
            for (TenantUserSalaryRelVer ver : verList) {
                ver.setVerState(SysStaticDataEnum.VER_STATE.VER_STATE_N);
                iTenantUserSalaryRelVerService.updateById(ver);
            }
        }
        List<UserSalaryInfoVer> userSalaryInfoVerList = iUserSalaryInfoVerService.getUserSalaryInfoVer(tenantUserRel.getUserId(), SysStaticDataEnum.VER_STATE.VER_STATE_Y);
        if (CollectionUtils.isNotEmpty(userSalaryInfoVerList)) {
            for (UserSalaryInfoVer ver : userSalaryInfoVerList) {
                ver.setVerState(SysStaticDataEnum.VER_STATE.VER_STATE_N);
                iUserSalaryInfoVerService.updateById(ver);
            }
        }
        TenantUserSalaryRel salary =  iTenantUserSalaryRelService.getTenantUserRalaryRelByRelId(tenantUserRel.getId());
        if(salary!=null){
            TenantUserSalaryRelVer salaryRelVer = new TenantUserSalaryRelVer();
            BeanUtils.copyProperties(salaryRelVer,salary);
            salaryRelVer.setVerState(SysStaticDataEnum.VER_STATE.VER_STATE_HIS);
            iTenantUserSalaryRelVerService.save(salaryRelVer);
            iTenantUserSalaryRelService.removeById(salary);
            if (salary.getSalaryPattern()!=null
                    &&(salary.getSalaryPattern()== SysStaticDataEnum.SALARY_PATTERN.MILEAGE
                    ||salary.getSalaryPattern()== SysStaticDataEnum.SALARY_PATTERN.TIMES)){
                List<UserSalaryInfo> salaryInfos = iUserSalaryInfoService.getuserSalarInfo(tenantUserRel.getUserId());
                //处理区间信息
                if(salaryInfos!=null&&!salaryInfos.isEmpty()){
                    for (UserSalaryInfo salaryInfo : salaryInfos) {
                        UserSalaryInfoVer userSalaryInfoVer = new UserSalaryInfoVer();
                        BeanUtils.copyProperties(userSalaryInfoVer,salaryInfo);
                        userSalaryInfoVer.setVerState(SysStaticDataEnum.VER_STATE.VER_STATE_HIS);
                        iUserSalaryInfoVerService.save(userSalaryInfoVer);
                        iUserSalaryInfoService.removeById(salaryInfo);
                    }

                }
            }

        }

    }

}
