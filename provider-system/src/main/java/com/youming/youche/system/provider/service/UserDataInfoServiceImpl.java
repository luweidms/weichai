package com.youming.youche.system.provider.service;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.domain.SysStaticData;
import com.youming.youche.commons.domain.SysUser;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.response.ResponseCode;
import com.youming.youche.commons.util.LoginUtils;
import com.youming.youche.commons.util.RedisUtil;
import com.youming.youche.conts.EnumConsts;
import com.youming.youche.conts.SysStaticDataEnum;
import com.youming.youche.encrypt.K;
import com.youming.youche.record.api.tenant.ITenantVehicleRelService;
import com.youming.youche.record.api.tenant.IUserReceiverInfoService;
import com.youming.youche.record.api.vehicle.IVehicleDataInfoService;
import com.youming.youche.record.common.EncryPwd;
import com.youming.youche.record.domain.tenant.TenantVehicleRel;
import com.youming.youche.record.domain.user.UserReceiverInfo;
import com.youming.youche.record.domain.vehicle.VehicleDataInfo;
import com.youming.youche.record.dto.StaffDataInfoDto;
import com.youming.youche.system.api.*;
import com.youming.youche.system.domain.SysTenantDef;
import com.youming.youche.system.domain.TenantStaffRel;
import com.youming.youche.system.domain.UserDataInfo;
import com.youming.youche.system.dto.*;
import com.youming.youche.system.dto.user.LocalUserInfoDto;
import com.youming.youche.system.provider.mapper.UserDataInfoMapper;
import com.youming.youche.system.provider.utis.ReadisUtil;
import com.youming.youche.system.utils.CommonUtils;
import com.youming.youche.system.vo.CreateUserVo;
import com.youming.youche.system.vo.UpdateUserVo;
import org.apache.commons.collections.CollectionUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 用户资料信息 服务实现类
 * </p>
 *
 * @author Terry
 * @since 2021-12-25
 */
@DubboService(version = "1.0.0")
public class UserDataInfoServiceImpl extends BaseServiceImpl<UserDataInfoMapper, UserDataInfo>
        implements IUserDataInfoService {

    @Resource
    public BCryptPasswordEncoder passwordEncoder;

    @Resource
    UserDataInfoMapper userDataInfoMapper;

    @Resource
    RedisUtil redisUtil;

    @Resource
    LoginUtils loginUtils;

    @Resource
    ITenantStaffRelService tenantStaffRelService;

    @Resource
    ISysUserOrgRelService sysUserOrgRelService;

    @Resource
    ISysTenantDefService sysTenantDefService;

    @Resource
    ISysUserService sysUserService;

    @Resource
    ISysUserRoleService sysUserRoleService;

    @DubboReference(version = "1.0.0")
    IUserReceiverInfoService userReceiverInfoService;

    @DubboReference(version = "1.0.0")
    IVehicleDataInfoService iVehicleDataInfoService;

    @Resource
    private ReadisUtil readisUtil;

    @DubboReference(version = "1.0.0")
    ITenantVehicleRelService iTenantVehicleRelService;

    private static final Logger LOGGER = LoggerFactory.getLogger(UserDataInfoServiceImpl.class);

    @Override
    public UserDataInfoAndOrganizeDto get(String accessToken) {
        UserDataInfo userDataInfo = null;
        SysUser sysUser = (SysUser) redisUtil.get("user:" + accessToken);
        userDataInfo = super.getById(sysUser.getUserInfoId());
        UserDataInfoAndOrganizeDto dto = new UserDataInfoAndOrganizeDto();
        BeanUtil.copyProperties(userDataInfo,dto);
        return dto;
    }

    @Override
    public IPage<UserDataInfo> get(String accessToken, Integer pageNum, Integer pageSize, String phone,
                                   String linkman) {

        LoginInfo loginInfo = loginUtils.get(accessToken);

        LambdaQueryWrapper<UserDataInfo> userDataInfoLambdaQueryWrapper = Wrappers.lambdaQuery();
        userDataInfoLambdaQueryWrapper.eq(UserDataInfo::getTenantId, loginInfo.getTenantId());
        if (StringUtils.isNotBlank(phone)) {
            userDataInfoLambdaQueryWrapper.like(UserDataInfo::getMobilePhone, phone);
        }
        if (StringUtils.isNotBlank(linkman)) {
            userDataInfoLambdaQueryWrapper.like(UserDataInfo::getLinkman, linkman);
        }

        Page<UserDataInfo> userDataInfoPage = new Page<>(pageNum, pageSize);
        return baseMapper.selectPage(userDataInfoPage, userDataInfoLambdaQueryWrapper);
    }

    @Override
    public IPage<OrganizeStaffDto> get(String accessToken, Long orgId, Integer pageNum, Integer pageSize) {
        LoginInfo loginInfo = loginUtils.get(accessToken);

        Page<UserDataInfo> userDataInfoPage = new Page<>(pageNum, pageSize);
        IPage<OrganizeStaffDto> userDataInfoIPage = baseMapper.selectByOrgIdAndTenantId(userDataInfoPage, orgId,
                loginInfo.getTenantId());
        return userDataInfoIPage;
    }

    @Override
    @Transactional
    public Long create(CreateUserVo createUserVo, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        // 根据登录账号查询信息
        LambdaQueryWrapper<UserDataInfo> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(UserDataInfo::getMobilePhone, createUserVo.getLoginAcct());
        UserDataInfo userDataInfo = baseMapper.selectOne(wrapper);

        if (null != userDataInfo) {
           return updateStaff(createUserVo, loginInfo, userDataInfo);
        } else {
            List<UserDataInfo> userDataInfos = getUserDataInfos(createUserVo);
            if (!CollectionUtils.isEmpty(userDataInfos)) {
                // 身份证已被使用
                UserDataInfo checkReuslt = userDataInfos.get(0);
                throw new BusinessException("尾号为"
                        + createUserVo.getIdentification().substring(createUserVo.getIdentification().length() - 4)
                        + "的身份证号已经被" + checkReuslt.getLinkman() + "（" + checkReuslt.getMobilePhone() + "）使用，请重新输入");
            } else {
                return createStaff(createUserVo, loginInfo);

            }
        }
    }

    /**
     * 新建账号，新建用户信息，新建车队员工信息
     *
     * @return
     */
    private Long createStaff(CreateUserVo createUserVo, LoginInfo loginInfo) {
        // 往user_data_info表创建共用数据，生成userId
        UserDataInfo userDataInfo1 = new UserDataInfo();
        userDataInfo1.setMobilePhone(createUserVo.getLoginAcct());
        userDataInfo1.setLoginName(createUserVo.getLoginAcct());
        userDataInfo1.setLinkman(createUserVo.getLinkman());
        userDataInfo1.setIdentification(createUserVo.getIdentification());
        userDataInfo1.setSourceFlag(0);// 数据来源：0，平台
        userDataInfo1.setTenantId(loginInfo.getTenantId());
        userDataInfo1.setUserType(SysStaticDataEnum.USER_TYPE.CUSTOMER_USER);
        baseMapper.insert(userDataInfo1);

        // 往操作员表插入登录信息
        SysTenantDef sysTenantDef = sysTenantDefService.get(loginInfo.getTenantId());
        SysUser sysOperator = new SysUser();
        sysOperator.setTenantId(loginInfo.getTenantId());
        sysOperator.setBillId(createUserVo.getLoginAcct());
        sysOperator.setCreateTime(LocalDateTime.now());
        sysOperator.setLoginAcct(createUserVo.getLoginAcct());
        sysOperator.setUserInfoId(userDataInfo1.getId());
        sysOperator.setName(createUserVo.getLinkman());
        sysOperator.setOpUserId(loginInfo.getId());
        sysOperator.setLockFlag(createUserVo.getLockFlag());
        sysOperator.setState(SysStaticDataEnum.VERIFY_STS.UNAUTHORIZED);
        sysOperator.setTenantCode(sysTenantDef.getTenantCode());
        if (null != createUserVo.getPassword()) {
            sysOperator.setPassword(passwordEncoder.encode(createUserVo.getPassword()));
        }
        sysUserService.create(sysOperator);

        // 往员工表添加记录
        TenantStaffRel tenantStaffRel = new TenantStaffRel();
        tenantStaffRel.setUserInfoId(userDataInfo1.getId());
        tenantStaffRel.setStaffName(userDataInfo1.getLinkman());
        tenantStaffRel.setStaffPosition(createUserVo.getStaffPosition());
        tenantStaffRel.setEmployeeNumber(createUserVo.getEmployeeNumber());
        tenantStaffRel.setLockFlag(createUserVo.getLockFlag());
        tenantStaffRel.setTenantId(loginInfo.getTenantId());
        tenantStaffRelService.save(tenantStaffRel);
        // 建立员工、组织 的默认关系
        Long id = tenantStaffRel.getId();
        List<Long> userId = Lists.newArrayList(userDataInfo1.getId());
        List<Long> orgIds = CommonUtils.convertLongIdList(createUserVo.getOrgIds());
        sysUserOrgRelService.saveUserOragnizeRel(orgIds, userId, loginInfo.getTenantId(), loginInfo.getId());
        return id;
    }

    /**
     * 平台已有用户信息，共享登录账号和用户信息；添加车队员工信息
     * @return
     */
    private Long updateStaff(CreateUserVo createUserVo, LoginInfo loginInfo, UserDataInfo userDataInfo) {
        // 判断是否重复添加一个员工
        TenantStaffRel tenantStaffRel = tenantStaffRelService.getTenantStaffByUserInfoIdAndTenantIdAndState(
                userDataInfo.getId(), loginInfo.getTenantId(), SysStaticDataEnum.SYS_STATE_DESC.STATE_YES);
        if (null != tenantStaffRel) {
            throw new BusinessException(ResponseCode.USER_HAS_EXIST);
        }
        // 根据身份证号码查询用户
        List<UserDataInfo> userDataInfos = getUserDataInfos(createUserVo);
        if (!CollectionUtils.isEmpty(userDataInfos)) {
            // 身份证已被使用
            UserDataInfo checkReuslt = userDataInfos.get(0);
            throw new BusinessException(
                    "尾号为" + createUserVo.getIdentification().substring(createUserVo.getIdentification().length() - 4)
                            + "的身份证号已经被" + checkReuslt.getLinkman() + "（" + checkReuslt.getMobilePhone() + "）使用，请重新输入");
        } else {
            TenantStaffRel tenantStaffRel1 = new TenantStaffRel();
            tenantStaffRel1.setUserInfoId(userDataInfo.getId());
            tenantStaffRel1.setStaffName(userDataInfo.getLinkman());
            tenantStaffRel1.setStaffPosition(createUserVo.getStaffPosition());
            tenantStaffRel1.setEmployeeNumber(createUserVo.getEmployeeNumber());
            tenantStaffRel1.setLockFlag(createUserVo.getLockFlag());
            tenantStaffRel1.setTenantId(loginInfo.getTenantId());

            tenantStaffRelService.save(tenantStaffRel1);
            List<Long> userId = Lists.newArrayList(userDataInfo.getId());
            List<Long> orgIds = CommonUtils.convertLongIdList(createUserVo.getOrgIds());
            sysUserOrgRelService.saveUserOragnizeRel(orgIds, userId, loginInfo.getTenantId(), loginInfo.getId());
            return tenantStaffRel.getId();

        }
    }

    /**
     * 根据身份证号码查询用户
     */
    private List<UserDataInfo> getUserDataInfos(CreateUserVo createUserVo) {
        LambdaQueryWrapper<UserDataInfo> wrapper;
        wrapper = Wrappers.lambdaQuery();
        wrapper.eq(UserDataInfo::getIdentification, createUserVo.getIdentification());
        return baseMapper.selectList(wrapper);
    }


    @Override
    @Transactional
    public Long update(UpdateUserVo updateUserVo, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);

        List<Long> orgIdList = CommonUtils.convertLongIdList(updateUserVo.getOrgIds());
        UserDataInfo userDataInfo = get(updateUserVo.getUserInfoId());

        //判断是否是车队管理员
        SysTenantDef sysTenantDef = sysTenantDefService.get(loginInfo.getTenantId());
        if (sysTenantDef.getAdminUser().equals(updateUserVo.getUserInfoId()) && updateUserVo.getLockFlag() == 2) {
            throw new BusinessException("不允许禁用超级管理员");
        }
        //更新员工信息
        TenantStaffRel tenantStaffRel = tenantStaffRelService.getTenantStaffByUserInfoIdAndTenantId(
                updateUserVo.getUserInfoId(), loginInfo.getTenantId());
        if (tenantStaffRel==null){
            tenantStaffRel = new TenantStaffRel();
            tenantStaffRel.setUserInfoId(userDataInfo.getId());
            tenantStaffRel.setTenantId(loginInfo.getTenantId());
        }
        tenantStaffRel.setLockFlag(updateUserVo.getLockFlag());
        tenantStaffRel.setState(1);
        tenantStaffRel.setEmployeeNumber(updateUserVo.getEmployeeNumber());
        tenantStaffRel.setStaffPosition(updateUserVo.getStaffPosition());

        //员工可以修改身份证和名字
        userDataInfo.setIdentification(updateUserVo.getIdentification());

        SysUser sysUser = sysUserService.getByUserInfoId(updateUserVo.getUserInfoId());


        if (org.apache.commons.lang.StringUtils.isNotBlank(updateUserVo.getLinkman())) {
            userDataInfo.setLinkman(updateUserVo.getLinkman());
            sysUser.setName(updateUserVo.getLinkman());
            tenantStaffRel.setStaffName(updateUserVo.getLinkman());

            List<TenantStaffRel> tenantStaffRelList = tenantStaffRelService.getTenantStaffByUserInfoId(updateUserVo.getUserInfoId());
            for (TenantStaffRel rel : tenantStaffRelList) {
                rel.setStaffName(updateUserVo.getLinkman());
            }
            SysTenantDef tenantDef = sysTenantDefService.selectByAdminUser(updateUserVo.getUserInfoId());
            if (null != tenantDef) {
                tenantDef.setLinkMan(updateUserVo.getLinkman());
                sysTenantDefService.saveOrUpdate(tenantDef);
            }
        }

        if (StringUtils.isNotBlank(updateUserVo.getLinkman()) && sysTenantDef.getAdminUser().equals(updateUserVo.getUserInfoId())) {
            sysTenantDef.setLinkMan(updateUserVo.getLinkman());
        }
//		if(userPrice!=null&&userPrice>0L) {
//			userDataInfo.setUserPrice(userPrice);
//		}
//		if(StringUtils.isNotEmpty(userPriceUrl)) {
//			userDataInfo.setUserPriceUrl(userPriceUrl);
//		}

        saveOrUpdate(userDataInfo);
        tenantStaffRelService.saveOrUpdate(tenantStaffRel);
        sysTenantDefService.saveOrUpdate(sysTenantDef);
        sysUserService.saveOrUpdate(sysUser);
//        tenantStaffRelService.saveOrUpdate(tenantStaffRel);

        //修改员工的关联组织
        List<Long> userIdList = new ArrayList<>(1);
        userIdList.add(updateUserVo.getUserInfoId());
        sysUserOrgRelService.updateUserOragnizeRel(orgIdList, userIdList, loginInfo.getTenantId(), loginInfo.getId());
        return tenantStaffRel.getId();

    }


    @Override
    public Long remove(Long userInfoId, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        //判断是否是车队管理员
        SysTenantDef sysTenantDef = sysTenantDefService.get(loginInfo.getTenantId());
        if (sysTenantDef.getAdminUser().equals(userInfoId)) {
            throw new BusinessException("不允许删除超级管理员");
        }
        SysUser sysUser = sysUserService.getByUserInfoId(userInfoId);
        if (null == sysUser) {
            throw new BusinessException("不存在的员工");
        }

        // 删除员工与部门关系
        List<Long> userIdList = new ArrayList<>(1);
        userIdList.add(userInfoId);
        tenantStaffRelService.delByUserInfoIdAndTenantId(userInfoId, loginInfo.getTenantId());
        sysUserOrgRelService.delUserOragnizeRel(userIdList, loginInfo.getTenantId());

        // 解除用户角色绑定
        boolean b = sysUserRoleService.removeByUserId(sysUser.getId());
        return sysUser.getId();
    }

    @Override
    public List<UserDataInfo> getUserDataInfoList(List<Long> userIdList) {
        if(userIdList!=null &&userIdList.size()>0){
            LambdaQueryWrapper<UserDataInfo> wrapper;
            wrapper = Wrappers.lambdaQuery();
            wrapper.in(UserDataInfo::getId, userIdList);
            return baseMapper.selectList(wrapper);
        }
       return null;
    }

    @Override
    public List<String> querStaffName(List<Long> userIdList) {
        QueryWrapper<UserDataInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("id",userIdList);
        List<UserDataInfo> list=baseMapper.selectList(queryWrapper);
        List<String> staffNameList = new ArrayList<>();
        if (com.alibaba.nacos.common.utils.CollectionUtils.isEmpty(list)) {
            return staffNameList;
        }
        for (UserDataInfo userDataInfo : list) {
            staffNameList.add(userDataInfo.getLinkman());
        }
        return staffNameList;
    }

    @Override
    public LoginInfo getLoginInfoByAccessToken(String accessToken) {
        return loginUtils.get(accessToken);
    }

    @Override
    public UserDataInfo getPhone(String phone) {
        LambdaQueryWrapper<UserDataInfo> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(UserDataInfo::getMobilePhone,phone);
        return baseMapper.selectOne(queryWrapper);
    }

    @Override
    public List<String> querStaffNameByUserIds(List<Long> userIdList) {
        LambdaQueryWrapper<UserDataInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(UserDataInfo::getId, userIdList);

        List<UserDataInfo> list = this.list(queryWrapper);
        List<String> staffNameList = new ArrayList<>();
        if (CollectionUtils.isEmpty(list)) {
            return staffNameList;
        }

        for (UserDataInfo userDataInfo : list) {
            staffNameList.add(userDataInfo.getLinkman());
        }

        return staffNameList;
    }

    @Override
    public Map<Long, String> querStaffNameAndIdByUserIds(List<Long> userIdList) {
        LambdaQueryWrapper<UserDataInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(UserDataInfo::getId, userIdList);

        List<UserDataInfo> list = this.list(queryWrapper);
        Map<Long,String> staffNameList = new HashMap<>();
        if (CollectionUtils.isEmpty(list)) {
            return staffNameList;
        }

        for (UserDataInfo userDataInfo : list) {
            staffNameList.put(userDataInfo.getId(),userDataInfo.getLinkman());
        }
        return staffNameList;
    }

    @Override
    public UserDataInfo getUserDataInfo(Long driverUserId) {
        return this.getById(driverUserId);
    }

    @Override
    public String getUserName(long userId)  {
        String userName="";
        if (userId > 0) {
            UserDataInfo userDataInfo = this.getUserDataInfo(userId);
            if (userDataInfo != null) {
                userName = userDataInfo.getLinkman();
                if (userName == null || userName.equals("")) {
                    userName = userDataInfo.getMobilePhone();
                }
            }
        } else {
            throw new BusinessException("用户名不能为空");
        }
        return userName;
    }

    @Override
    public Page<DriverAndReceiverInfoOutDto> getVirtualTenantList(Integer pageNum,Integer pageSize,
                                                                  String phone, String name,
                                                                  Boolean includeDriver) {
        Page<DriverAndReceiverInfoOutDto> page = new Page<>(pageNum, pageSize);
        return userDataInfoMapper.queryDriverAndReceiverInfo(page, phone, name, includeDriver);
    }

    @Override
    public LocalUserInfoDto getLocalUserInfo(Long userId, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        LocalUserInfoDto dto = new LocalUserInfoDto();

        UserDataInfo userDataInfo = this.getUserDataInfo(userId);
        StaffDataInfoDto out = iVehicleDataInfoService.getStaffInfoByUserId(userId, loginInfo.getTenantId());
        SysTenantDef sysTenantDef = sysTenantDefService.getSysTenantDef(loginInfo.getTenantId());
        UserReceiverInfo userReceiverInfo = userReceiverInfoService.getUserReceiverInfoByUserId(userId);
        Map<String, Object> result = new HashMap<>();

        if (null != userDataInfo) {
            dto.setLinkman(userDataInfo.getLinkman());
            dto.setIdentification(userDataInfo.getIdentification());
            dto.setLoginAcct(userDataInfo.getMobilePhone());
        }

        if (out != null) {
            dto.setEmployeeNumber(out.getEmployeeNumber());
            dto.setStaffPosition(out.getStaffPosition());
        }

        if (sysTenantDef != null) {
            dto.setTenantName(sysTenantDef != null ? sysTenantDef.getName() : "");
            UserDataInfo userInfo = this.getUserDataInfo(sysTenantDef.getAdminUser());
            dto.setAdminUserMobile(userInfo.getMobilePhone());
        }

        if (null != userReceiverInfo) {
            dto.setReceiverName(userReceiverInfo.getReceiverName());
        }
        return dto;
    }

    @Override
    public Boolean doInit(long userId) {
        if (userId <= 0) {
            throw new BusinessException("用户编号不能为空!");
        }
        boolean flag = true;
        UserDataInfo userDataInfo = this.getUserDataInfo(userId);
        if (userDataInfo == null) {
            throw new BusinessException("查找用户信息失败!");
        }
        if (StringUtils.isBlank(userDataInfo.getAccPassword())) {
            flag = false;
        }
//        if (userDataInfo.getAccPassword()!=null && org.apache.commons.lang.StringUtils.isNotEmpty(userDataInfo.getAccPassword())){
//            flag = false;
//        }
        return flag;
    }
    @Override
    public Boolean doInits(Long userId) {
        if (userId <= 0) {
            throw new BusinessException("用户编号不能为空!");
        }
        boolean flag = true;
        UserDataInfo userDataInfo = this.getUserDataInfo(userId);
        if (userDataInfo == null) {
            throw new BusinessException("查找用户信息失败!");
        }
        // TODO 2022-6-28   修改
        if (userDataInfo.getAccPassword()!=null && org.apache.commons.lang.StringUtils.isNotEmpty(userDataInfo.getAccPassword())){
            flag = false;
        }
        return flag;
    }
    @Override
    public boolean checkCompleteness(long userId, int goodsType) {
        SysStaticData sysStaticData = getSysStaticData("DRIVER_COMPLETENESS", goodsType+"");
        char[] config = sysStaticData.getCodeName().toCharArray();
        UserDataInfo userDataInfo = super.getById(userId);
        char[] completeness = userDataInfo.getCompleteness().toCharArray();
        if (config.length != completeness.length) {
            throw new BusinessException("配置错误");
        }
        for (int index = 0; index < config.length; index++) {
            if (config[index] > completeness[index]) {
                LOGGER.info("======司机信息不完整======" + userDataInfo.getCompleteness());
                return false;
            }
        }
        return true;
    }

    SysStaticData getSysStaticData(String codeType, String codeValue) {
        List<SysStaticData> list = (List<SysStaticData>) redisUtil.get(SysStaticDataEnum.SYS_STATIC_DATA_NAME.concat(codeType));
        if (list != null && list.size() > 0) {
            for (SysStaticData sysStaticData : list) {
                if (sysStaticData.getCodeValue().equals(codeValue)) {
                    return sysStaticData;
                }
            }
        }
        return new SysStaticData();
    }

    @Override
    public UserDataInfo selectUserType(Long userInfoId) {
        UserDataInfo userDataInfo = userDataInfoMapper.selectById(userInfoId);
        return userDataInfo;
    }

    @Override
    public Page<UserDataLinkManDto> doQueryBackUserList(String linkman, String mobilePhone,
                                                        String token, Integer pageSize, Integer pageNum) {
        LoginInfo loginInfo = loginUtils.get(token);
        Page<UserDataLinkManDto> page=new Page<>(pageNum,pageSize);
        Page<UserDataLinkManDto> dataInfoDtoPage = userDataInfoMapper.doQueryBackUserList(page, linkman, mobilePhone, loginInfo);
        List<UserDataLinkManDto> records = dataInfoDtoPage.getRecords();
        if(records!= null && records.size() >0){
            for (UserDataLinkManDto record : records) {
                Long backhaulGuideUserId = record.getUserId();
                String backhaulGuideName = record.getLinkMan();
                String backhaulGuidePhone = record.getMobilPhone();
                Integer carUserType = record.getCarUserType();
                record.setBackhaulGuideUserId(backhaulGuideUserId);
                record.setBackhaulGuideName(backhaulGuideName);
                record.setBackhaulGuidePhone(backhaulGuidePhone);
                if(carUserType != null && carUserType == 0){
                    record.setCarUserTypeName("员工");
                }else if(carUserType != null &&  carUserType > -1){
                    record.setCarUserTypeName(readisUtil.getSysStaticData("DRIVER_TYPE",carUserType+"").getCodeName());
                }
            }
            dataInfoDtoPage.setRecords(records);
        }
        return dataInfoDtoPage;
    }

    @Override
    public void checkCaptchaAndLoginPasswd(Long userId, String billId, String captcha, String loginPasswd, Integer channel) {
        //先校验短信验证码，再校验登录密码
        boolean isSendCodeTrue = checkCodeNotDel(billId, captcha, EnumConsts.RemoteCache.VALIDKEY_PAY);

        if (!isSendCodeTrue) {
            throw new BusinessException("短信验证码错误！");
        }

        //获取用户信息
        UserDataInfo userDataInfo = this.getUserDataInfo(userId);

        if (userDataInfo == null) {
            throw new BusinessException("用户不存在！");
        }

        if (channel != SysStaticDataEnum.CHANEL_TYPE.CHANEL_MINI_PROGREM) {//小程序不用校验登录密码

            if (userDataInfo.getUserType() != null && (userDataInfo.getUserType() == SysStaticDataEnum.USER_TYPE.DRIVER_USER
                    || userDataInfo.getUserType() == SysStaticDataEnum.USER_TYPE.CUSTOMER_USER)) {

                SysUser sysOperator = sysUserService.getSysOperatorByUserIdOrPhone(null, billId);

                if (org.apache.commons.lang3.StringUtils.isEmpty(loginPasswd)) {
                    throw new BusinessException("登录密码不能为空!");
                }

                loginPasswd = EncryPwd.pwdDecryp(loginPasswd);
                if (org.apache.commons.lang3.StringUtils.isEmpty(loginPasswd)) {
                    throw new BusinessException("登录密码不能为空!");
                }

                boolean isLoginTrue = false;
                try {
                    isLoginTrue = K.j_s(loginPasswd).equals(sysOperator.getPassword());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (!isLoginTrue) {
                    throw new BusinessException("登录密码错误!");
                }
            }
        }

    }

    @Override
    public void updateLugeAgreement(Long userId, Long lugeAgreement, String lugeAgreementUrl) {
        if (null == userId) {
            throw new BusinessException("参数错误");
        }
        UserDataInfo userDataInfo = this.getUserDataInfo(userId);
        if (null == userDataInfo) {
            throw new BusinessException("不存在的司机");
        }

        userDataInfo.setLugeAgreement(lugeAgreement);
        userDataInfo.setLugeAgreementUrl(lugeAgreementUrl);
        this.update(userDataInfo);
    }

    @Override
    public UserDataInfo getOwnDriver(Long userId, Long tenantId) {
        if(userId <= 0 || tenantId <= 0){
            throw  new BusinessException("参数错误");
        }
        LambdaQueryWrapper<UserDataInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserDataInfo::getId, userId);
        queryWrapper.eq(UserDataInfo::getTenantId, tenantId);
        return baseMapper.selectOne(queryWrapper);
    }

    @Override
    public OrCodeByZhaoYouDto orCodeByZhaoYou(String plateNumber, Long userId, String userName, String userPhone) {

        UserDataInfo user = this.getUserDataInfo(userId);
        if (user == null) {
            throw new BusinessException("未找到用户信息！");
        }
        VehicleDataInfo vehicleDataInfo = iVehicleDataInfoService.getVehicleDataInfo(plateNumber);
        if (vehicleDataInfo == null) {
            throw new BusinessException("未找到车辆[" + plateNumber + "]信息！");
        }

        // todo 生成二维码图片(暂未处理)
        String qrCodeUrl = "";
        String qrCodeId = "";
        OrCodeByZhaoYouDto codeMap = new OrCodeByZhaoYouDto();
        codeMap.setQrCodeUrl(qrCodeUrl);
        codeMap.setQrCodeId(qrCodeId);

        TenantVehicleRel tenantVehicleRel = iTenantVehicleRelService.getZYVehicleByPlateNumber(plateNumber);
        if (null == tenantVehicleRel) {
            codeMap.setTenantName("平台车");
        } else {
            codeMap.setTenantName(sysTenantDefService.getTenantName(tenantVehicleRel.getTenantId()));
        }

        return codeMap;
    }

    @Override
    public UserDataInfo saveReturnId(UserDataInfo userDataInfo) {
        this.save(userDataInfo);
        return userDataInfo;
    }

    /**
     * 检查验证码,但不删除缓存里的验证码，用于下一个操作继续验证，修改支付密码分二步走增加该方法
     */
    public Boolean checkCodeNotDel(String billId, String code, String businessType) {
        String randomCode = "";
        boolean isOk = false;
        randomCode = redisUtil.get(businessType + billId).toString();

        if (code != null && code.equals(randomCode)) {
            isOk = true;
        }
        return isOk;
    }

}
