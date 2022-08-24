package com.youming.youche.record.provider.service.impl.tenant;


import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.util.DataFormat;
import com.youming.youche.commons.util.LoginUtils;
import com.youming.youche.commons.util.RedisUtil;
import com.youming.youche.conts.SysStaticDataEnum;
import com.youming.youche.record.api.tenant.ITenantUserRelService;
import com.youming.youche.record.api.tenant.ITenantUserSalaryRelService;
import com.youming.youche.record.api.tenant.ITenantUserSalaryRelVerService;
import com.youming.youche.record.api.user.IUserDataInfoRecordService;
import com.youming.youche.record.api.user.IUserLineRelService;
import com.youming.youche.record.api.user.IUserLineRelVerService;
import com.youming.youche.record.api.user.IUserSalaryInfoService;
import com.youming.youche.record.api.user.IUserSalaryInfoVerService;
import com.youming.youche.record.common.CommonUtil;
import com.youming.youche.record.domain.tenant.TenantUserRel;
import com.youming.youche.record.domain.tenant.TenantUserSalaryRel;
import com.youming.youche.record.domain.tenant.TenantUserSalaryRelVer;
import com.youming.youche.record.domain.user.UserDataInfo;
import com.youming.youche.record.domain.user.UserLineRel;
import com.youming.youche.record.domain.user.UserLineRelVer;
import com.youming.youche.record.domain.user.UserSalaryInfo;
import com.youming.youche.record.domain.user.UserSalaryInfoVer;
import com.youming.youche.record.dto.tenant.QueryAllTenantDto;
import com.youming.youche.record.provider.mapper.tenant.TenantUserRelMapper;
import com.youming.youche.record.provider.utils.SysStaticDataRedisUtils;
import com.youming.youche.system.api.ISysTenantDefService;
import com.youming.youche.system.domain.SysTenantDef;
import com.youming.youche.util.HtmlEncoder;
import com.youming.youche.util.JsonHelper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.youming.youche.conts.SysStaticDataEnum.SYS_USER_STATE.AUDIT_APPROVE;

/**
 * <p>
 * 租户会员关系 服务实现类
 * </p>
 *
 * @author Terry
 * @since 2021-11-22
 */
@DubboService(version = "1.0.0")
public class TenantUserRelServiceImpl extends ServiceImpl<TenantUserRelMapper, TenantUserRel>
        implements ITenantUserRelService {

    @Resource
    LoginUtils loginUtils;

    @Resource
    RedisUtil redisUtil;

    @Autowired
    TenantUserRelMapper tenantUserRelMapper;

    @Resource
    ITenantUserSalaryRelVerService iTenantUserSalaryRelVerService;

    @Resource
    ITenantUserSalaryRelService iTenantUserSalaryRelService;

    @Resource
    IUserSalaryInfoVerService iUserSalaryInfoVerService;

    @Resource
    IUserSalaryInfoService iUserSalaryInfoService;

    @Resource
    IUserLineRelVerService iUserLineRelVerService;

    @Resource
    IUserLineRelService iUserLineRelService;

    @Resource
    IUserDataInfoRecordService iUserDataInfoRecordService;

    @Resource
    ISysTenantDefService sysTenantDefService;


    @Override
    public List<TenantUserRel> getTenantUserRelListByUserId(Long userId, Long tenantId) {
        QueryWrapper<TenantUserRel> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("USER_ID", userId);
        if (null != tenantId) {
            queryWrapper.eq("TENANT_ID", tenantId);
        }
        return tenantUserRelMapper.selectList(queryWrapper);
    }

    @Override
    public List<TenantUserRel> getTenantUserRel(Long userId, long sourceTenantId) {
        QueryWrapper<TenantUserRel> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("USER_ID", userId);
        queryWrapper.eq("TENANT_ID", sourceTenantId);
        return baseMapper.selectList(queryWrapper);
    }

    @Override
    public TenantUserRel getTenantUserRelByRelId(Long relId) {
        return baseMapper.selectById(relId);
    }

    @Override
    public void updateUserSalary(TenantUserRel tenantUserRel) {
        TenantUserSalaryRelVer tenantUserSalaryRelVer = iTenantUserSalaryRelVerService.getTenantUserSalaryRelVer(tenantUserRel.getId());
        if (null != tenantUserSalaryRelVer) {
            TenantUserSalaryRel tenantUserSalaryRel = iTenantUserSalaryRelService.getTenantUserRalaryRelByUserId(tenantUserRel.getUserId(), tenantUserRel.getTenantId());
            if (null == tenantUserSalaryRel) {
                tenantUserSalaryRel = new TenantUserSalaryRel();
            }
            BeanUtil.copyProperties(tenantUserSalaryRelVer, tenantUserSalaryRel);
            tenantUserRel.setId(tenantUserSalaryRelVer.getRelId());
            iTenantUserSalaryRelService.saveOrUpdate(tenantUserSalaryRel);
            List<UserSalaryInfoVer> userSalaryInfoVerList = iUserSalaryInfoVerService.getUserSalaryInfoVer(tenantUserRel.getUserId(), SysStaticDataEnum.VER_STATE.VER_STATE_Y);
            if (CollectionUtils.isNotEmpty(userSalaryInfoVerList)) {
                List<UserSalaryInfo> userSalaryInfoList = iUserSalaryInfoService.getuserSalarInfo(tenantUserRel.getUserId());
                if (CollectionUtils.isNotEmpty(userSalaryInfoList)) {
                    for (UserSalaryInfo userSalaryInfo : userSalaryInfoList) {
                        iUserSalaryInfoService.removeById(userSalaryInfo);
                    }
                }
                for (UserSalaryInfoVer userSalaryInfoVer : userSalaryInfoVerList) {
                    UserSalaryInfo userSalaryInfo = new UserSalaryInfo();
                    BeanUtil.copyProperties(userSalaryInfoVer, userSalaryInfo);
                    userSalaryInfo.setId(userSalaryInfoVer.getMId());
                    iUserSalaryInfoService.save(userSalaryInfo);
                }
            }

        }

    }

    @Override
    public void updateUserLineVer(Long userId, Long tenantId, Map<String, Object> inParam, String accessToken) {
        List<UserLineRelVer> verList = iUserLineRelVerService.getUserLineRelVer(userId, tenantId, SysStaticDataEnum.VER_STATE.VER_STATE_Y);
        if (CollectionUtils.isNotEmpty(verList)) {
            for (UserLineRelVer ver : verList) {
                ver.setVerState(SysStaticDataEnum.VER_STATE.VER_STATE_N);
                iUserLineRelVerService.updateById(ver);
            }
        }
        String lineRelsArray = DataFormat.getStringKey(inParam, "lineRels");
        lineRelsArray = HtmlEncoder.decode(lineRelsArray);
        if (StringUtils.isNotEmpty(lineRelsArray)) {
            List<Map> linelist = JsonHelper.fromJson(lineRelsArray, List.class);
            UserLineRelVer userLineRelVer = null;
            UserDataInfo userDataInfo = iUserDataInfoRecordService.getUserDataInfoByAccessToken(accessToken);
            if (null != linelist) {
                for (int i = 0; i < linelist.size(); i++) {
                    Map<String, Object> inMap = linelist.get(i);
                    Number lineId = CommonUtil.transStringToNumber(DataFormat.getStringKey(inMap, "lineId"));
                    String lineCodeRule = DataFormat.getStringKey(inMap, "lineCodeRule");
                    Number relIdN = CommonUtil.transStringToNumber(DataFormat.getStringKey(inMap, "relId"));
                    Long relIdOld = null == relIdN ? null : relIdN.longValue();
                    userLineRelVer = new UserLineRelVer();
                    userLineRelVer.setRelId(relIdOld);
                    userLineRelVer.setLineCodeRule(lineCodeRule);
                    userLineRelVer.setLineId(lineId == null ? null : lineId.longValue());
                    userLineRelVer.setUserId(userId);
                    userLineRelVer.setTenantId(tenantId);
                    userLineRelVer.setState(1);
                    userLineRelVer.setOpId(userDataInfo.getOpId());
                    Date date = new Date();
                    Instant instant = date.toInstant();
                    ZoneId zoneId = ZoneId.systemDefault();
                    LocalDateTime localDateTime = instant.atZone(zoneId).toLocalDateTime();
                    userLineRelVer.setOpDate(localDateTime);
                    userLineRelVer.setUpdateDate(localDateTime);
                    userLineRelVer.setUpdateOpId(userDataInfo.getOpId());
                    userLineRelVer.setVerState(SysStaticDataEnum.VER_STATE.VER_STATE_Y);
                    iUserLineRelVerService.save(userLineRelVer);
                }
            }
        }
    }

    @Override
    public void updateUserLine(TenantUserRel tenantUserRel) {
        List<UserLineRelVer> verList = iUserLineRelVerService.getUserLineRelVer(tenantUserRel.getUserId(), tenantUserRel.getTenantId(), SysStaticDataEnum.VER_STATE.VER_STATE_Y);
        if (CollectionUtils.isNotEmpty(verList)) {
            List<UserLineRel> userLineRelList = iUserLineRelService.getUserLineRelByUserId(tenantUserRel.getUserId(), tenantUserRel.getTenantId());
            if (CollectionUtils.isNotEmpty(userLineRelList)) {
                for (UserLineRel lineRel : userLineRelList) {
                    iUserLineRelVerService.removeById(lineRel);
                }
                for (UserLineRelVer userLineRelVer : verList) {
                    UserLineRel userLineRel = new UserLineRel();
                    BeanUtil.copyProperties(userLineRelVer, userLineRel);
                    userLineRel.setId(userLineRelVer.getRelId());
                    iUserLineRelService.save(userLineRel);
                }
            }
        }
    }

    @Override
    public TenantUserRel getTenantUserRelByUserIdAndType(Long userId, Integer carUserType) {
        QueryWrapper<TenantUserRel> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        queryWrapper.eq("car_user_type", carUserType).orderByDesc("id");
        return CollectionUtils.isEmpty(baseMapper.selectList(queryWrapper)) || baseMapper.selectList(queryWrapper).size() <= 0 ? null : baseMapper.selectList(queryWrapper).get(0);
    }

    @Override
    public TenantUserRel getAllTenantUserRelByUserId(long userId, long tenantId) {
        QueryWrapper<TenantUserRel> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("USER_ID", userId);
        if(tenantId > -1) {
            queryWrapper.eq("TENANT_ID", tenantId);
        }
        List<TenantUserRel> list = baseMapper.selectList(queryWrapper);
        if (list == null || list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    @Override
    public Integer getCarUserType(Long driverUserId, Long tenantId) throws BusinessException {
        if (driverUserId <= 0 || tenantId <= 0) {
            throw new BusinessException("参数错误");
        }
        TenantUserRel tenantUserRel = getTenantUserRelByUserId(driverUserId, tenantId);
        if (tenantUserRel == null) {
            return null;
        }
        return tenantUserRel.getCarUserType();
    }

    @Override
    public TenantUserRel getTenantUserRelList(Long userId, Long tenantId, Integer state) {
        QueryWrapper<TenantUserRel> tenantUserRelQueryWrapper = new QueryWrapper<>();

        tenantUserRelQueryWrapper.eq("user_id", userId)
                .eq("tenant_id", tenantId)
                .eq("state", AUDIT_APPROVE);
        List<TenantUserRel> list = this.list(tenantUserRelQueryWrapper);
        if(list==null||list.isEmpty()){
            return null;
        }
        if (list.size() > 1) {
            throw new BusinessException("司机资料不全，请先核对司机资料");
        }
        TenantUserRel tenantUserRel =  list.get(0);
        return tenantUserRel;
    }

    @Override
    public Boolean isDriver(Long userId, Long tenantId) {
        QueryWrapper<TenantUserRel> tenantUserRelQueryWrapper = new QueryWrapper<>();
        tenantUserRelQueryWrapper.eq("user_id", userId);
        if (null != tenantId) {
            tenantUserRelQueryWrapper.eq("tenant_id", tenantId);
        }

        List<TenantUserRel> list = baseMapper.selectList(tenantUserRelQueryWrapper);
        if (CollectionUtils.isNotEmpty(list)) {
            return true;
        }

        UserDataInfo userDataInfo = iUserDataInfoRecordService.getUserDataInfo(userId);
        return null != userDataInfo.getUserType() && userDataInfo.getUserType().equals(SysStaticDataEnum.USER_TYPE.DRIVER_USER);
    }

    @Override
    public Boolean checkOwnDriver(Long userId, Long tenantId) {
        LambdaQueryWrapper<TenantUserRel> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TenantUserRel::getUserId, userId);
        queryWrapper.eq(TenantUserRel::getTenantId, tenantId);
        queryWrapper.orderByAsc(TenantUserRel::getId);
        List<TenantUserRel> list = this.list(queryWrapper);

        TenantUserRel tenantUserRel = null;
        if (list != null && list.size() != 0) {
            tenantUserRel = list.get(0);
        }

        if (null == tenantUserRel) {
            throw new BusinessException("不存在的司机");
        }

        return tenantUserRel.getCarUserType() == SysStaticDataEnum.CAR_USER_TYPE.OWN_CAR;
    }

    @Override
    public TenantUserRel getTenantUserRelByUserId(Long userId, Long tenantId) {
        LambdaQueryWrapper<TenantUserRel> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TenantUserRel::getUserId, userId);
        queryWrapper.eq(TenantUserRel::getTenantId, tenantId);
        queryWrapper.orderByAsc(TenantUserRel::getId);
        List<TenantUserRel> list = this.list(queryWrapper);
        if (list != null && list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

    @Override
    public List<TenantUserRel> getTenantUserRelsAllTenant(Long driverUserId, Integer carUserType) {
        LambdaQueryWrapper<TenantUserRel> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TenantUserRel::getUserId, driverUserId);
        queryWrapper.eq(TenantUserRel::getCarUserType, carUserType);
        queryWrapper.eq(TenantUserRel::getState, SysStaticDataEnum.SYS_USER_STATE.AUDIT_APPROVE);
        return this.list(queryWrapper);
    }

    @Override
    public List<QueryAllTenantDto> queryAllTenant(String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        Long userInfoId = loginInfo.getUserInfoId();

        LambdaQueryWrapper<TenantUserRel> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TenantUserRel::getUserId, userInfoId);
        queryWrapper.eq(TenantUserRel::getState, SysStaticDataEnum.SYS_USER_STATE.AUDIT_APPROVE);

        List<TenantUserRel> result = baseMapper.selectList(queryWrapper);
        List<QueryAllTenantDto> outList = new ArrayList<>();

        if (CollectionUtils.isNotEmpty(result)) {
            for (TenantUserRel tenantUserRel : result) {
                if (tenantUserRel.getCarUserType() == SysStaticDataEnum.CAR_USER_TYPE.SOCIAL_CAR) {
                    continue;
                }

                SysTenantDef tenantDef = sysTenantDefService.getSysTenantDef(tenantUserRel.getTenantId());
                if (null == tenantDef) {
                    continue;
                }

                QueryAllTenantDto out = new QueryAllTenantDto();
                out.setTenantId(tenantUserRel.getTenantId());
                out.setTenantName(tenantDef.getName());
                out.setLinkPhone(tenantDef.getLinkPhone());
                out.setCarUserType(tenantUserRel.getCarUserType());
                out.setCarUserTypeName(SysStaticDataRedisUtils.getSysStaticDataByCodeName(redisUtil, "DRIVER_TYPE", String.valueOf(tenantUserRel.getCarUserType())).getCodeName());
                outList.add(out);
            }
        }

        return outList;
    }

    /**
     * 获取租户司机关系，不包含未认证的关系
     *
     * @param userId
     * @param tenantId
     * @return
     * @throws Exception
     */
    public TenantUserRel getTenantUserRelByUserId(long userId, long tenantId) throws BusinessException {
        QueryWrapper<TenantUserRel> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("USER_ID", userId);
        queryWrapper.eq("TENANT_ID", tenantId);
        queryWrapper.eq("state", SysStaticDataEnum.SYS_USER_STATE.AUDIT_APPROVE);
        List<TenantUserRel> list = baseMapper.selectList(queryWrapper);
        if (list == null || list.isEmpty()) {
            return null;
        }
        if (list.size() > 1) {
            throw new BusinessException("司机资料不全，请先核对司机资料");
        }
        return list.get(0);
    }

}
