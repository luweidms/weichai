package com.youming.youche.system.provider.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.youming.youche.cloud.api.sms.ISysSmsSendService;
import com.youming.youche.cloud.domin.sms.SysSmsSend;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.constant.SysOperLogConst;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.domain.SysStaticData;
import com.youming.youche.commons.domain.SysUser;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.util.LoginUtils;
import com.youming.youche.commons.util.RedisUtil;
import com.youming.youche.components.fdfs.FastDFSHelper;
import com.youming.youche.conts.EnumConsts;
import com.youming.youche.conts.SysStaticDataEnum;
import com.youming.youche.order.api.ICreditRatingRuleFeeService;
import com.youming.youche.order.api.ICreditRatingRuleService;
import com.youming.youche.order.api.order.IAccountBankUserTypeRelService;
import com.youming.youche.order.domain.CreditRatingRule;
import com.youming.youche.order.domain.CreditRatingRuleFee;
import com.youming.youche.record.api.tenant.ITenantUserRelService;
import com.youming.youche.record.api.user.IUserService;
import com.youming.youche.record.domain.tenant.TenantUserRel;
import com.youming.youche.system.api.*;
import com.youming.youche.system.api.ac.IPayFeeLimitService;
import com.youming.youche.system.api.audit.IAuditSettingService;
import com.youming.youche.system.api.ioer.ImportOrExportRecordsService;
import com.youming.youche.system.api.tenant.*;
import com.youming.youche.system.domain.*;
import com.youming.youche.system.domain.ac.PayFeeLimit;
import com.youming.youche.system.domain.ioer.ImportOrExportRecords;
import com.youming.youche.system.domain.tenant.*;
import com.youming.youche.system.dto.CheckPhoneDto;
import com.youming.youche.system.dto.Rate;
import com.youming.youche.system.dto.SysTenantOutDto;
import com.youming.youche.system.dto.user.TenantOrUserBindCardDto;
import com.youming.youche.system.provider.mapper.SysTenantDefMapper;
import com.youming.youche.system.utils.excel.ExportExcel;
import com.youming.youche.system.vo.SysTenantInfoVo;
import com.youming.youche.system.vo.SysTenantVo;
import com.youming.youche.system.vo.TenantQueryVo;
import com.youming.youche.util.DateUtil;
import com.youming.youche.util.SysMagUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.sql.Date;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

/**
 * <p>
 * ????????? ???????????????
 * </p>
 *
 * @author Terry
 * @since 2022-01-04
 */
@DubboService(version = "1.0.0")
@Service
public class SysTenantDefServiceImpl extends BaseServiceImpl<SysTenantDefMapper, SysTenantDef>
        implements ISysTenantDefService {

    @Resource
    RedisUtil redisUtil;

    @Resource
    LoginUtils loginUtils;

    @Resource
    private IUserDataInfoService userDataInfoService;

    @Resource
    private ISysTenantRegisterService sysTenantRegisterService;

    @Resource
    private ISysTenantBusinessStateService sysTenantBusinessStateService;

    @Resource
    private BCryptPasswordEncoder passwordEncoder;

    @Resource
    private ITenantStaffRelService tenantStaffRelService;

    @Resource
    private ISysOperLogService sysOperLogService;

    @Resource
    private ISysRoleService roleService;

    @Resource
    private ISysExpenseService sysExpenseService;

    @DubboReference(version = "1.0.0")
    ISysSmsSendService sysSmsSendService;

    @Resource
    ISysUserRoleService sysUserRoleService;

    @Resource
    ISysOrganizeService sysOrganizeService;

    @Resource
    ISysUserOrgRelService sysUserOrgRelService;

    @Resource
    IBillSettingService billSettingService;

    @Resource
    ISysTenantVisitService sysTenantVisitService;

    @Resource
    IServiceChargeReminderService serviceChargeReminderService;

    @Resource
    IBillPlatformService billPlatformService;

    @Resource
    IAuditSettingService auditSettingService;

    @Resource
    IPayFeeLimitService iPayFeeLimitService;

    @Resource
    ImportOrExportRecordsService importOrExportRecordsService;

    @Resource
    ISysStaticDataService sysStaticDataService;

    @DubboReference(version = "1.0.0")
    IAccountBankUserTypeRelService iAccountBankUserTypeRelService;

    @DubboReference(version = "1.0.0")
    ITenantUserRelService iTenantUserRelService;

    @DubboReference(version = "1.0.0")
    ICreditRatingRuleService creditRatingRuleService;

    @DubboReference(version = "1.0.0")
    ICreditRatingRuleFeeService creditRatingRuleFeeService;

    @Resource
    ISysUserService sysUserService;

    private static final Logger LOGGER = LoggerFactory.getLogger(SysTenantDefServiceImpl.class);

    @Override
    public List<SysTenantDef> getByIds(List<TenantStaffRel> tenantStaffRelList) {

        List<SysTenantDef> sysTenantDefList = Lists.newArrayList();
        // ?????????????????????????????????????????????
        for (TenantStaffRel tenantStaffRel : tenantStaffRelList) {
            if (tenantStaffRel != null && tenantStaffRel.getTenantId() != SysStaticDataEnum.PT_TENANT_ID) {
                SysTenantDef sysTenantDef = baseMapper.selectById(tenantStaffRel.getTenantId());
                if (sysTenantDef != null
                        && sysTenantDef.getVirtualState().equals(SysStaticDataEnum.VIRTUAL_TENANT_STATE.NOT_VIRTUAL)) {
                    sysTenantDefList.add(sysTenantDef);
                }

            }
        }
        return sysTenantDefList;
    }

    @Override
    public List<SysTenantDef> selectByUserId(String accessToken) {
        SysUser sysUser = (SysUser) redisUtil.get("user:" + accessToken);
        List<SysTenantDef> sysTenantDefList = Lists.newArrayList();
        LoginInfo loginInfo = new LoginInfo();
        if (sysUser.getPlatformType() == 1) {
            BeanUtil.copyProperties(sysUser, loginInfo);
            redisUtil.setex("loginInfo:" + accessToken, loginInfo, 86400);
        } else {
            List<SysTenantDef> sysTenantDefs = baseMapper.selectAllByUserId(sysUser.getUserInfoId());
            for (SysTenantDef sysTenantDef : sysTenantDefs) {
                if (sysTenantDef != null && sysTenantDef.getVirtualState().equals(SysStaticDataEnum.VIRTUAL_TENANT_STATE.NOT_VIRTUAL)) {
                    if (sysTenantDef.getState() == 1) {
                        sysTenantDefList.add(sysTenantDef);
                    }
                }
            }
            // ??????????????????????????????redis???????????????????????????????????????
            if (sysTenantDefList.size() == 1) {
                BeanUtil.copyProperties(sysUser, loginInfo);
                loginInfo.setTenantId(sysTenantDefList.get(0).getId())
                        .setTenantCode(sysTenantDefList.get(0).getTenantCode()).setTelPhone(sysUser.getLoginAcct());
                List<SysUserOrgRel> sysUserOrgRel = sysUserOrgRelService.getByUserDataIdAndTenantId(sysUser.getUserInfoId(), loginInfo.getTenantId());
                List<Long> orgIds = Lists.newArrayList();
                for (SysUserOrgRel userOrgRel : sysUserOrgRel) {
                    orgIds.add(userOrgRel.getOrgId());
                }
                orgIds.removeAll(Collections.singleton(null));
                loginInfo.setOrgIds(orgIds);
                redisUtil.setex("loginInfo:" + accessToken, loginInfo, 86400);
            }
        }
        return sysTenantDefList;
    }

    @Override
    public SysTenantDef selectByAdminUser(Long userInfoId) {
        LambdaQueryWrapper<SysTenantDef> lambda = new QueryWrapper<SysTenantDef>().lambda();
        lambda.eq(SysTenantDef::getAdminUser, userInfoId);
        return this.getOne(lambda);

    }

    @Override
    public boolean choose(String accessToken, Long tenantId) {
        SysUser sysUser = (SysUser) redisUtil.get("user:" + accessToken);
        SysTenantDef sysTenantDef = get(tenantId);
        LoginInfo loginInfo = new LoginInfo();
        BeanUtil.copyProperties(sysUser, loginInfo);

        loginInfo.setTenantId(sysTenantDef.getId()).setTenantCode(sysTenantDef.getTenantCode())
                .setTelPhone(sysUser.getLoginAcct());
        List<SysUserOrgRel> sysUserOrgRel = sysUserOrgRelService.getByUserDataIdAndTenantId(sysUser.getUserInfoId(), loginInfo.getTenantId());
        List<Long> orgIds = Lists.newArrayList();
        for (SysUserOrgRel userOrgRel : sysUserOrgRel) {
            orgIds.add(userOrgRel.getOrgId());
        }
        orgIds.removeAll(Collections.singleton(null));
        if (orgIds == null || orgIds.size() < 1) {
            Long orgId = userDataInfoService.getUserDataInfo(sysUser.getUserInfoId()).getAttachedOrgId();
            if (orgId != null) {
                orgIds.add(orgId);
            }
        }
        loginInfo.setOrgIds(orgIds);
        boolean b = redisUtil.setex("loginInfo:" + accessToken, loginInfo, 86400);
        return b;
    }

    @Override
    public boolean selectByPreSaleOrgIdOrAfterSaleOrgId(List<Long> orgIds) {
        List<SysTenantDef> sysTenantDefList = baseMapper.selectByPreSaleOrgIdOrAfterSaleOrgId(orgIds);
        if (CollectionUtils.isNotEmpty(sysTenantDefList)) {
            return true;
        }
        return false;
    }

    @Override
    public PageInfo<SysTenantDef> queryTenant(TenantQueryVo tenantQueryVo, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<SysTenantDef> list = baseMapper.queryTenant(tenantQueryVo);
        PageInfo<SysTenantDef> customerInfoPageInfo = new PageInfo<>(list);
        queryTenantDef(list);
        return customerInfoPageInfo;
    }

    @Override
    public void queryTenantExport(TenantQueryVo tenantQueryVo, ImportOrExportRecords importOrExportRecords) {
        List<SysTenantDef> list = baseMapper.queryTenant(tenantQueryVo);
        try {
            queryTenantDef(list);
            String[] showName = null;
            String[] resourceFild = null;
            String fileName = null;
            showName = new String[]{"????????????", "????????????", "????????????", "???????????????", "?????????????????????", "?????????", "?????????????????????", "???????????????", "???????????????", "??????", "????????????", "????????????", "????????????", "???????????????0???????????????1???????????????2??????????????????3???????????????"};
            resourceFild = new String[]{"getName", "getLinkPhone", "getCreateTime", "getActualController", "getActualControllerPhone", "getStaffNumber", "getAnnualTurnover", "getVehicleNumber", "getOtherCar", "getTenantType", "getState", "getPreSaleServiceName", "getAfterSaleServiceName", "getPayState"};
            XSSFWorkbook workbook = ExportExcel.getWorkbookXlsx(list, showName, resourceFild, SysTenantDef.class,
                    null);
            ByteArrayOutputStream os = new ByteArrayOutputStream();

            workbook.write(os);
            byte[] b = os.toByteArray();
            InputStream inputStream = new ByteArrayInputStream(b);
            //????????????
            FastDFSHelper client = FastDFSHelper.getInstance();
            String path = client.upload(inputStream, "????????????.xlsx", inputStream.available());
            os.close();
            inputStream.close();
            importOrExportRecords.setMediaUrl(path);
            importOrExportRecords.setState(2);
            importOrExportRecordsService.update(importOrExportRecords);
        } catch (Exception e) {
            importOrExportRecords.setState(3);
            importOrExportRecordsService.update(importOrExportRecords);
            e.printStackTrace();
        }
    }

    @Override
    public List<SysTenantDef> queryTenantList(TenantQueryVo tenantQueryVo) {
        List<SysTenantDef> list = baseMapper.queryTenant(tenantQueryVo);
        queryTenantDef(list);
        return list;
    }

    @Override
    @Transactional
    public Integer saveAndBuild(SysTenantVo sysTenantVo, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        //????????????????????????????????????
        CheckPhoneDto out = checkPhone(sysTenantVo.getLinkPhone());
        if (!out.getAvailable()) {
            //??????????????????
            throw new BusinessException(out.getReason());
        }
        //?????????????????????????????????????????????????????????
        if (null != sysTenantVo.getRegisterId() && sysTenantVo.getRegisterId() > 0) {
            SysTenantRegister sysTenantRegister = sysTenantRegisterService.get(sysTenantVo.getRegisterId());
            Integer buildState = 0;
            if (sysTenantRegister != null) {
                buildState = sysTenantRegister.getBuildState();
            }
            if (SysStaticDataEnum.TENANT_BUILD_STATE.BUILT.equals(buildState)) {
                //?????????????????????
                throw new BusinessException("????????????????????????");
            }
        }
        //???????????????????????????,??????????????????????????????
        checkBusinessState(sysTenantVo.getBusinessState());
        //???????????????????????????????????????????????????
        SysTenantDef sysTenantDef = build(sysTenantVo, accessToken, loginInfo);
        //????????????????????????????????????
        //  getOpAccountTF().createOrderAccount(sysTenantDef.getAdminUser(), OrderAccountConst.VEHICLE_AFFILIATION.VEHICLE_AFFILIATION0, sysTenantDef.getTenantId(),SysStaticDataEnum.USER_TYPE.ADMIN_USER);

        //???????????????????????????(????????????)
        LOGGER.debug("----???????????????????????????----tenantId----" + sysTenantDef.getId());
        try {
            //????????????????????????????????????????????????
            QueryWrapper<PayFeeLimit> payFeeLimitQueryWrapper = new QueryWrapper<>();
            payFeeLimitQueryWrapper.eq("tenant_id", sysTenantDef.getId());
            List<PayFeeLimit> payFeeList = iPayFeeLimitService.list(payFeeLimitQueryWrapper);
            if (payFeeList != null && payFeeList.size() > 0) {
                throw new BusinessException("???????????????????????????????????????");
            }
            List<SysStaticData> sysStaticDataList = (List<SysStaticData>) redisUtil.get(SysStaticDataEnum.SYS_STATIC_DATA_NAME.concat("PAY_FEE_LIMIT_SUB_TYPE"));
            for (SysStaticData sysStaticData : sysStaticDataList) {
                PayFeeLimit payFeeLimit = new PayFeeLimit();
                payFeeLimit.setType(Integer.parseInt(sysStaticData.getCodeDesc()));
                payFeeLimit.setSubType(Integer.parseInt(sysStaticData.getCodeId() + ""));
                payFeeLimit.setSubTypeName(sysStaticData.getCodeName());
                payFeeLimit.setValue(Long.parseLong(sysStaticData.getCodeValue()));
                payFeeLimit.setTenantId(sysTenantDef.getId());
                payFeeLimit.setOpId(loginInfo.getId());
                payFeeLimit.setOpName(loginInfo.getName());
                payFeeLimit.setOpDate(LocalDateTime.now());
                iPayFeeLimitService.save(payFeeLimit);
            }
            List<CreditRatingRule> ruleList = creditRatingRuleService.getCreditRatingRules(-1L);
            for (CreditRatingRule creditRatingRule : ruleList) {
                creditRatingRule.setId(null);
                creditRatingRule.setTenantId(sysTenantDef.getId());
                creditRatingRule.setCreateTime(LocalDateTime.now());
                creditRatingRule.setUpdateTime(LocalDateTime.now());
                Long rid = creditRatingRuleService.saveUpdateCreditRatingRule(creditRatingRule);
                List<CreditRatingRuleFee> creditRatingRuleFees = creditRatingRuleFeeService.queryCreditRatingRuleFeeCopy(-1L);
                for (CreditRatingRuleFee c : creditRatingRuleFees
                ) {
                    c.setRuleId(rid);
                    c.setCreateTime(LocalDateTime.now());
                    c.setUpdateTime(LocalDateTime.now());
                    creditRatingRuleFeeService.save(c);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //????????????
        saveSysOperLog(SysOperLogConst.BusiCode.Tenant, SysOperLogConst.OperType.Add, "??????????????????", accessToken, sysTenantDef.getId());
        return 1;
    }

    @Override
    public CheckPhoneDto checkPhone(String phone) {
        CheckPhoneDto out = new CheckPhoneDto();
        out.setAvailable(true);
        //????????????????????? ???????????????
        QueryWrapper<SysTenantRegister> sysTenantRegisterQueryWrapper = new QueryWrapper();
        sysTenantRegisterQueryWrapper.eq("link_phone", phone);
        sysTenantRegisterQueryWrapper.eq("audit_state", SysStaticDataEnum.REGISTER_AUDIT_STATE.WAIT_AUDIT);
        SysTenantRegister register = sysTenantRegisterService.getOne(sysTenantRegisterQueryWrapper);
        if (null != register) {
            out.setAvailable(false);
            out.setReason("????????????????????????????????????????????????");
        }
        QueryWrapper<UserDataInfo> userDataInfoQueryWrapper = new QueryWrapper<>();
        userDataInfoQueryWrapper.eq("mobile_phone", phone);
        UserDataInfo userDataInfo = userDataInfoService.getOne(userDataInfoQueryWrapper);
        if (null != userDataInfo) {
//            if (StringUtils.isBlank(userDataInfo.getIdentification())) {
//                throw new BusinessException("??????????????????????????????");
//            }
            QueryWrapper<SysTenantDef> sysTenantDefQueryWrapper = new QueryWrapper<>();
            sysTenantDefQueryWrapper.eq("admin_user", userDataInfo.getId());
            SysTenantDef sysTenantDef = baseMapper.selectOne(sysTenantDefQueryWrapper);
            //    ????????????app???????????????????????????????????????????????????????????????????????????
            //   ????????????????????????????????????????????????????????????????????????????????????????????????
//            List<TenantUserRel> tenantUserRels = userService.getAllTenantUserRels(userDataInfo.getId());
//            if (CollectionUtils.isNotEmpty(tenantUserRels) || userDataInfo.getUserType().equals(SysStaticDataEnum.USER_TYPE.DRIVER_USER)) {
//                // ???????????????
//                out.setAvailable(false);
//                out.setReason("??????????????????????????????????????????????????????????????????");
//            } else if (userDataInfo.getUserType().equals(SysStaticDataEnum.USER_TYPE.SERVICE_USER)) {
//                //  ??????????????????
//                out.setAvailable(false);
//                out.setReason("???????????????????????????????????????");
//            }
//            else
            if (null != sysTenantDef && SysStaticDataEnum.VIRTUAL_TENANT_STATE.NOT_VIRTUAL.equals(sysTenantDef.getVirtualState())) {
                //????????????????????????????????????
                out.setAvailable(false);
                out.setReason("??????????????????????????????????????????");
            } else if (null != sysTenantDef && SysStaticDataEnum.VIRTUAL_TENANT_STATE.IS_VIRTUAL.equals(sysTenantDef.getVirtualState())) {
                //????????????????????????????????????
                out.setAvailable(false);
                out.setReason("????????????????????????????????????????????????");
            } else {
                out.setIdentification(userDataInfo.getIdentification());
                out.setLinkman(userDataInfo.getLinkman());
            }
        }
        return out;
    }

    @Override
    public SysTenantVo getTenantById(Long tenantId) {
        if (null == tenantId || tenantId <= 0) {
            return null;
        }
        //????????????????????????
        SysTenantDef sysTenantDef = baseMapper.selectById(tenantId);
        SysTenantVo sysTenantVo = new SysTenantVo();
        BeanUtil.copyProperties(sysTenantDef, sysTenantVo);
        sysTenantVo.setCompanyName(sysTenantDef.getName());
        sysTenantVo.setTenantId(sysTenantDef.getId());
        // ??????????????????
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("tenant_id", tenantId);
        BillSetting billSetting = billSettingService.getOne(queryWrapper);
        sysTenantVo.setBillSetting(billSetting);
        //??????????????????(????????????)
        if (billSetting != null && billSetting.getBillMethod() > 0) {
            QueryWrapper queryWrapperBill = new QueryWrapper();
            queryWrapperBill.eq("user_id", billSetting.getBillMethod());
            BillPlatform billPlatform = billPlatformService.getOne(queryWrapperBill);
            if (billPlatform != null && billPlatform.getPlatName() != null) {
                sysTenantVo.setPlateName(billPlatform.getPlatName());
            } else {
                sysTenantVo.setPlateName("");
            }
        }
        SysTenantBusinessState businessState = sysTenantBusinessStateService.getOne(queryWrapper);
        sysTenantVo.setBusinessState(businessState);
        //??????????????????
        UserDataInfo userDataInfo = userDataInfoService.getById(sysTenantVo.getAdminUser());
        if (userDataInfo != null) {
            if (userDataInfo.getIdentification() != null) {
                sysTenantVo.setLinkManIdentification(userDataInfo.getIdentification());
            }
            if (userDataInfo.getLinkman() != null) {
                sysTenantVo.setAdminUserName(userDataInfo.getLinkman());
            }
        }


        if (null != sysTenantDef.getPreSaleServiceId()) {
            UserDataInfo preSaleUser = userDataInfoService.getById(sysTenantDef.getPreSaleServiceId());
            if (preSaleUser != null) {
                sysTenantVo.setPreSaleServicePhone(preSaleUser.getMobilePhone());
                sysTenantVo.setPreSaleServiceName(preSaleUser.getLinkman());
            }
        }
        if (null != sysTenantDef.getAfterSaleServiceId()) {
            UserDataInfo afterSaleUser = userDataInfoService.getById(sysTenantDef.getAfterSaleServiceId());
            if (afterSaleUser != null) {
                sysTenantVo.setAfterSaleServicePhone(afterSaleUser.getMobilePhone());
                sysTenantVo.setAfterSaleServiceName(afterSaleUser.getLinkman());
            }
        }

        return sysTenantVo;
    }

    @Override
    @Transactional
    public Integer updateTenant(SysTenantVo sysTenantVo, String accessToken) {


        //??????????????????
        SysTenantDef sysTenantDef = baseMapper.selectById(sysTenantVo.getTenantId());
        updateTenantDef(sysTenantDef, sysTenantVo);
//        super.updateById(sysTenantDef);
        baseMapper.updateTenantDefById(sysTenantDef);
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("tenant_id", sysTenantVo.getTenantId());
        //??????????????????
        BillSetting billSetting = billSettingService.getOne(queryWrapper);
        if (null != billSetting && null != billSetting.getRateId()) {
            Rate rate = billSettingService.getRateById(billSetting.getRateId());
            rate.setRateItemList(billSettingService.queryRateItem(billSetting.getRateId()));
            billSetting.setRateName(rate.getRateName());
        }
        billSetting.setBillAbility(sysTenantVo.getBillSetting().getBillAbility());

        billSettingService.update(billSetting);

        //??????????????????
//        updateRootOragnizeName(sysTenantDef.getId(), sysTenantDef.getName());
        //????????????????????????
//        UpdateWrapper<SysTenantBusinessState> sysTenantBusinessStateUpdateWrapper = new UpdateWrapper<>();
//        sysTenantBusinessStateUpdateWrapper.eq("tenant_id", sysTenantDef.getId());
//        sysTenantBusinessStateService.update(sysTenantVo.getBusinessState(), sysTenantBusinessStateUpdateWrapper);
        sysTenantBusinessStateService.updateBusinessState(sysTenantVo.getBusinessState(), sysTenantDef.getId());
        //????????????
        saveSysOperLog(SysOperLogConst.BusiCode.Tenant, SysOperLogConst.OperType.Update, "??????????????????", accessToken, sysTenantDef.getId());
        return 1;
    }

    @Override
    @Transactional
    public Integer updateTenantInfo(SysTenantInfoVo sysTenantVo, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        //??????????????????
        SysTenantDef sysTenantDef = baseMapper.selectById(sysTenantVo.getBusinessState().getTenantId());
        updateTenantInfoDef(sysTenantDef, sysTenantVo);
        baseMapper.updateTenantDefById(sysTenantDef);

        SysUser sysUser = sysUserService.getByUserInfoId(loginInfo.getUserInfoId());
        sysUser.setName(sysTenantVo.getLinkMan());

        sysUserService.updateById(sysUser);
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("tenant_id", sysTenantVo.getTenantId());
        sysTenantBusinessStateService.updateBusinessState(sysTenantVo.getBusinessState(), sysTenantDef.getId());
        //????????????
        saveSysOperLog(SysOperLogConst.BusiCode.Tenant, SysOperLogConst.OperType.Update, "??????????????????", accessToken, sysTenantDef.getId());
        return 1;
    }

    @Override
    @Transactional
    public Integer resetPassword(Long tenantId, String accessToken) {
        SysTenantDef sysTenantDef = baseMapper.selectById(tenantId);
        if (null == sysTenantDef) {
            throw new BusinessException("?????????????????????");
        }
        //??????????????????
        // String password = SysMagUtil.getRandomNumber(6);
        String password = "888888";
        SysUser sysUser = baseMapper.getSysOperatorByUserId(sysTenantDef.getAdminUser());
        sysUser.setPassword(passwordEncoder.encode(password));
        baseMapper.updateSysOperator(sysUser);
        //????????????
        //????????????
        Map paramMap = new HashMap();
        paramMap.put("password", password);
        SysSmsSend sysSmsSend = new SysSmsSend();
        //???????????????
        sysSmsSend.setParamMap(paramMap);
        //??????id
        sysSmsSend.setUserId(sysUser.getUserInfoId());
        //?????????
        sysSmsSend.setBillId(sysUser.getBillId());
        //????????????
        sysSmsSend.setSmsType(SysStaticDataEnum.SMS_TYPE.MESSAGE_AUTHENTICATION);
        //??????id
        sysSmsSend.setTemplateId(EnumConsts.SmsTemplate.RESET_TENANT_SUPPER_MANAGER_PASSWORD);
        //????????????
        sysSmsSend.setObjType(String.valueOf(SysStaticDataEnum.OBJ_TYPE.NOTIFY));
        try {
            sysSmsSendService.sendSms(sysSmsSend);
        } catch (Exception e) {
            LOGGER.info("?????????????????????" + e);
        }
        saveSysOperLog(SysOperLogConst.BusiCode.Tenant, SysOperLogConst.OperType.Update, "??????????????????", accessToken, tenantId);
        return 1;
    }

    @Override
    @Transactional
    public Integer updateState(Long tenantId, Integer state, String reason, String accessToken) {
        if (null == state || (state != 0 && state != 1)) {
            throw new BusinessException("?????????????????????");
        }

        if (tenantId.equals(SysStaticDataEnum.PT_TENANT_ID)) {
            throw new BusinessException("????????????????????????");
        }
        LoginInfo loginInfo = loginUtils.get(accessToken);
        SysTenantDef sysTenantDef = super.getById(tenantId);
        sysTenantDef.setState(state);
        sysTenantDef.setStateReason(reason);
        sysTenantDef.setStateOperatorName(loginInfo.getName());
        super.update(sysTenantDef);
        //????????????
        SysOperLogConst.OperType operType;
        String reasonResult;

        if (state == 0) {
            operType = SysOperLogConst.OperType.Disable;
            reasonResult = "??????????????? ???" + (StrUtil.isBlank(reason) ? "???" : reason);
            //???????????????????????????????????????????????????????????????????????????????????????
            baseMapper.doVehicleApplyRecordLoseEfficacy("?????????????????????????????????", SysStaticDataEnum.APPLY_STATE.APPLY_STATE2, SysStaticDataEnum.APPLY_STATE.APPLY_STATE0, DateUtil.getCurrDateTime(), null, tenantId, 2);
        } else {
            operType = SysOperLogConst.OperType.Enable;
            reasonResult = "??????????????? ???" + (StrUtil.isBlank(reason) ? "???" : reason);
            //???????????????????????????????????????
            baseMapper.doVehicleApplyRecordLoseEfficacy("????????????,????????????????????????", SysStaticDataEnum.APPLY_STATE.APPLY_STATE2, SysStaticDataEnum.APPLY_STATE.APPLY_STATE0, DateUtil.getCurrDateTime(), null, tenantId, 1);
            //???????????????????????????????????????????????????????????????????????????????????????????????????
            baseMapper.doVehicleApplyRecordLoseEfficacy("?????????????????????????????????", SysStaticDataEnum.APPLY_STATE.APPLY_STATE2, SysStaticDataEnum.APPLY_STATE.APPLY_STATE0, DateUtil.getCurrDateTime(), null, tenantId, 2);
        }
        saveSysOperLog(SysOperLogConst.BusiCode.Tenant, operType, reasonResult, accessToken, tenantId);
        return 1;
    }

    @Override
    @Transactional
    public Integer doUpdateBillSetting(BillSetting paramBillSetting) {
        //??????????????????
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("tenant_id", paramBillSetting.getTenantId());
        BillSetting billSetting = billSettingService.getOne(queryWrapper);
        if (null != billSetting && null != billSetting.getRateId()) {
            Rate rate = billSettingService.getRateById(billSetting.getRateId());
            billSetting.setRateName(rate.getRateName());
        }
        if (null != billSetting) {
            billSetting.setBillMethod(paramBillSetting.getBillMethod());
            billSetting.setRateId(paramBillSetting.getRateId());
            billSetting.setLugeAcct(paramBillSetting.getLugeAcct());
            billSetting.setLugePassword(paramBillSetting.getLugePassword());
            billSetting.setLugePayPassword(paramBillSetting.getLugePayPassword());
//			billSetting.setPaymentAgreement(paramBillSetting.getPaymentAgreement());
            billSetting.setNotOtherCarGetPlatformBill(paramBillSetting.getNotOtherCarGetPlatformBill());
            billSetting.setAttachFree(paramBillSetting.getAttachFree());
            billSettingService.update(billSetting);
        } else {
            billSetting = paramBillSetting;
            billSettingService.save(billSetting);
        }
        return 1;
    }

    @Override
    @Transactional
    public void audit(Long id, Integer auditState, String auditContent, String accessToken) {
        //????????????????????????
        LoginInfo loginInfo = loginUtils.get(accessToken);
        if (null == id || id <= 0) {
            throw new BusinessException("??????????????????");
        }
        if (null == auditState || (auditState < 1 && auditState > 3)) {
            throw new BusinessException("?????????????????????");
        }
        String operatorName = loginInfo.getName();
        SysTenantRegister sysTenantRegister = sysTenantRegisterService.getById(id);
        sysTenantRegister.setAuditState(auditState);
        sysTenantRegister.setAuditContent(auditContent);
        sysTenantRegister.setBuildState(SysStaticDataEnum.TENANT_BUILD_STATE.UNBUILT);
        sysTenantRegister.setAuditContentName(operatorName);
        sysTenantRegisterService.update(sysTenantRegister);
        //????????????
        saveSysOperLog(SysOperLogConst.BusiCode.TenantRegister, SysOperLogConst.OperType.Audit, auditContent, accessToken, sysTenantRegister.getId());

    }

    @Override
    @Transactional
    public void updatePayDateAndLeaveDate(Long tenantId, String payDate, String leaveDate, String entranceDate, String accessToken) throws Exception {
        SysTenantDef sysTenantDef = baseMapper.selectById(tenantId);
        if (null == sysTenantDef) {
            throw new BusinessException("??????????????????");
        }
        String comment = "";
        if (StrUtil.isNotBlank(payDate)) {
            sysTenantDef.setPayDate(LocalDateTime.parse(payDate));
            sysTenantDef.setFirstPayDate(null == sysTenantDef.getFirstPayDate() ? LocalDateTime.parse(payDate) : sysTenantDef.getFirstPayDate());
            updatePayStateByPayDate(sysTenantDef);
            comment += "????????????";
        }
        if (null != leaveDate) {
            sysTenantDef.setLeaveDate(LocalDateTime.parse(leaveDate));
            comment += (comment == "" ? "????????????" : "???????????????");
        }
        if (null != entranceDate) {
            sysTenantDef.setEntranceDate(LocalDateTime.parse(entranceDate));
            comment += (comment == "" ? "????????????" : "???????????????");
        }
        baseMapper.updateById(sysTenantDef);
        saveSysOperLog(SysOperLogConst.BusiCode.Tenant, SysOperLogConst.OperType.Update, comment, accessToken, sysTenantDef.getId());
    }

    @Override
    public boolean export(ImportOrExportRecords records) {

        records.setState(2);
        return false;
    }

    /**
     * ????????????????????????????????????????????????????????????????????????????????????????????????????????????
     */
    private void checkBusinessState(SysTenantBusinessState businessState) {
        if (null == businessState) {
            LOGGER.error("????????????????????????");
            throw new BusinessException("????????????????????????");
        }

        if (null == businessState.getId()) {
            return;
        }

        SysTenantBusinessState state = sysTenantBusinessStateService.get(businessState.getId());
        if (null == state) {
            LOGGER.error("??????????????????????????????");
        }

        if (null != state.getTenantId()) {
            LOGGER.error("????????????????????????????????????businessStateId: {}", state.getId());
        }
    }

    /***
     * @Description: ????????????????????????
     * @Author: luwei
     * @Date: 2022/1/12 5:16 ??????
     * @Param sysTenantIn:
     * @return: com.youming.youche.system.domain.SysTenantDef
     * @Version: 1.0
     **/
    private SysTenantDef build(SysTenantVo sysTenantVo, String accessToken, LoginInfo loginInfo) {

        //????????????????????????
        SysTenantDef sysTenantDef = createTenantDef(sysTenantVo);
        baseMapper.insert(sysTenantDef);
        sysTenantDef.setTenantCode(sysTenantDef.getId() + "");
        //????????????????????????
        QueryWrapper<UserDataInfo> userDataInfoQueryWrapper = new QueryWrapper<>();
        userDataInfoQueryWrapper.eq("mobile_phone", sysTenantDef.getLinkPhone());
        UserDataInfo userDataInfo = userDataInfoService.getOne(userDataInfoQueryWrapper);
        SysUser sysOperator = null;
        if (userDataInfo == null) {
            //??????????????????????????????
            userDataInfo = initUserDataInfo(sysTenantVo, loginInfo.getId());
            sysTenantDef.setAdminUser(userDataInfo.getId());
            //????????????????????????????????????
            sysOperator = initSysOperator(sysTenantDef, userDataInfo, loginInfo.getId());
        } else {
            userDataInfo.setLinkman(sysTenantVo.getLinkMan());
            userDataInfo.setIdentification(sysTenantVo.getIdentification());
            // ???????????????????????????????????????
            LOGGER.info("???????????????userid={}?????????tenantId={}??????????????????", userDataInfo.getId(), sysTenantDef.getId());
            sysTenantDef.setAdminUser(userDataInfo.getId());
            sysOperator = baseMapper.getSysOperatorByUserId(userDataInfo.getId());
            //?????????????????????????????????????????????????????????
            generatePassword(sysOperator);
        }
        //????????????????????????
        List<SysExpense> sysExpenses = new ArrayList<>();
        sysExpenses.add(new SysExpense().setName("??????").setRequired(2).setTenantId(sysTenantDef.getId()));
        sysExpenses.add(new SysExpense().setName("?????????").setRequired(2).setTenantId(sysTenantDef.getId()));
        sysExpenses.add(new SysExpense().setName("?????????").setRequired(2).setTenantId(sysTenantDef.getId()));
        sysExpenses.add(new SysExpense().setName("?????????").setRequired(2).setTenantId(sysTenantDef.getId()));
        sysExpenses.add(new SysExpense().setName("?????????").setRequired(2).setTenantId(sysTenantDef.getId()));
        sysExpenses.add(new SysExpense().setName("??????").setRequired(2).setTenantId(sysTenantDef.getId()));
        sysExpenseService.createList(sysExpenses, accessToken);
        //??????????????????
        initStaffRel(userDataInfo, sysTenantDef.getId(), accessToken);

        //?????????????????????
        initPermission(sysTenantDef, sysOperator, accessToken);

//        //?????????????????????
        initOragnize(sysTenantDef, sysOperator);
//
//        //????????????????????? (????????????)
        auditSettingService.initAuditNode(sysTenantDef.getId(), sysTenantDef.getAdminUser(), accessToken);
//
//        //??????????????????
        BillSetting billSetting = sysTenantVo.getBillSetting();
        billSetting.setTenantId(sysTenantDef.getId());
        if (billSetting.getBillMethod() == null || billSetting.getBillMethod() < 0) {
            billSetting.setBillMethod(0L);
            billSetting.setNotOtherCarGetPlatformBill(0);
            billSetting.setAttachFree(0d);
        }
        billSettingService.save(billSetting);
//
//        //??????????????????
        saveOrCorrelateBusinessState(sysTenantDef.getId(), sysTenantVo.getBusinessState());
        baseMapper.updateById(sysTenantDef);
        return sysTenantDef;
    }

    private SysTenantDef createTenantDef(SysTenantVo sysTenantVo) {
        SysTenantDef sysTenantDef = new SysTenantDef();
        BeanUtil.copyProperties(sysTenantVo, sysTenantDef);
        sysTenantDef.setId(null);
        sysTenantDef.setName(sysTenantVo.getCompanyName());
        sysTenantDef.setState(SysStaticDataEnum.SYS_STATE_DESC.STATE_YES);
        sysTenantDef.setFrozenState(SysStaticDataEnum.TENANT_FORZEN_STATE.UNFORZEN);
        if (StrUtil.isNotBlank(sysTenantDef.getPayServiceFeeDate())) {
            LocalDate now = LocalDate.now();
            LocalDate d = LocalDate.parse(now.getYear() + "-" + sysTenantDef.getPayServiceFeeDate());
            Integer payState = now.compareTo(d) > 0 ? SysStaticDataEnum.TENANT_PAY_STATE.EXPIRED : SysStaticDataEnum.TENANT_PAY_STATE.UNEXPIRED;
            sysTenantDef.setPayState(payState);
        }
        sysTenantDef.setVirtualState(SysStaticDataEnum.VIRTUAL_TENANT_STATE.NOT_VIRTUAL);
        return sysTenantDef;
    }


    private void generatePassword(SysUser sysOperator) {
        if (null == sysOperator || StrUtil.isNotBlank(sysOperator.getPassword())) {
            return;
        }
        //????????????????????????
        //String password = SysMagUtil.getRandomNumber(6);
        String password = "888888";
        sysOperator.setPassword(passwordEncoder.encode(password));
        baseMapper.updateSysOperator(sysOperator);
        //????????????
        Map paramMap = new HashMap();
        paramMap.put("password", password);
        SysSmsSend sysSmsSend = new SysSmsSend();
        //???????????????
        sysSmsSend.setParamMap(paramMap);
        //??????id
        sysSmsSend.setUserId(sysOperator.getUserInfoId());
        //????????????
        sysSmsSend.setSmsType(SysStaticDataEnum.SMS_TYPE.MESSAGE_AUTHENTICATION);
        //?????????
        sysSmsSend.setBillId(sysOperator.getBillId());
        //??????id
        sysSmsSend.setTemplateId(EnumConsts.SmsTemplate.CREATE_TENANT_SUPPER_MANAGER_PASSWORD);
        //????????????
        sysSmsSend.setObjType(String.valueOf(SysStaticDataEnum.OBJ_TYPE.NOTIFY));
        sysSmsSendService.sendSms(sysSmsSend);
    }

    /**
     * ???user_data_info????????????????????????????????????
     */
    private UserDataInfo initUserDataInfo(SysTenantVo SysTenantIn, Long id) {
        LOGGER.debug("????????????????????????????????????");
        UserDataInfo userDataInfo = new UserDataInfo();
        userDataInfo.setTenantId(null);
        userDataInfo.setUserType(SysStaticDataEnum.USER_TYPE.CUSTOMER_USER);
        userDataInfo.setLinkman(SysTenantIn.getLinkMan());
        userDataInfo.setMobilePhone(SysTenantIn.getLinkPhone());
        userDataInfo.setLoginName(SysTenantIn.getLinkPhone());
        userDataInfo.setOpId(id);
        userDataInfo.setIdentification(SysTenantIn.getLinkManIdentification());
        userDataInfo.setSourceFlag(0);
        userDataInfoService.save(userDataInfo);
        LOGGER.debug(JSON.toJSONString(userDataInfo));
        return userDataInfo;
    }

    private SysUser initSysOperator(SysTenantDef sysTenantDef, UserDataInfo userDataInfo, Long id) {
        LOGGER.debug("?????????????????????");
        //????????????????????????
        String password = SysMagUtil.getRandomNumber(6);
        //String password = "888888";
        SysUser sysUser = new SysUser();
        sysUser.setTenantId(sysTenantDef.getId());
        sysUser.setBillId(sysTenantDef.getLinkPhone());
        sysUser.setLoginAcct(sysTenantDef.getLinkPhone());
        sysUser.setUserInfoId(sysTenantDef.getAdminUser());
        sysUser.setName(userDataInfo.getLinkman());
        sysUser.setPassword(passwordEncoder.encode(password));
        sysUser.setOpName(userDataInfo.getLinkman());
        sysUser.setOpUserId(id);
        sysUser.setLockFlag(1);
        sysUser.setState(SysStaticDataEnum.VERIFY_STS.UNAUTHORIZED);
        sysUser.setTenantCode(sysTenantDef.getTenantCode());
        baseMapper.addSysOperator(sysUser);
        //????????????
        Map paramMap = new HashMap();
        paramMap.put("password", password);
        SysSmsSend sysSmsSend = new SysSmsSend();
        //???????????????
        sysSmsSend.setParamMap(paramMap);
        //??????id
        sysSmsSend.setUserId(sysTenantDef.getAdminUser());
        //????????????
        sysSmsSend.setSmsType(SysStaticDataEnum.SMS_TYPE.MESSAGE_AUTHENTICATION);
        //?????????
        sysSmsSend.setBillId(sysTenantDef.getLinkPhone());
        //??????id
        sysSmsSend.setTemplateId(EnumConsts.SmsTemplate.CREATE_TENANT_SUPPER_MANAGER_PASSWORD);
        //????????????
        sysSmsSend.setObjType(String.valueOf(SysStaticDataEnum.OBJ_TYPE.NOTIFY));
        sysSmsSendService.sendSms(sysSmsSend);
        LOGGER.debug(JSON.toJSONString(sysUser));
        return sysUser;
    }

    /**
     * ???tenant_staff_rel???????????????????????????????????????????????????(????????????TF??????)
     */
    private TenantStaffRel initStaffRel(UserDataInfo userDataInfo, Long tenantId, String accessToken) {
        LOGGER.debug("??????????????????");
        TenantStaffRel tenantStaffRel = new TenantStaffRel();
        tenantStaffRel.setUserInfoId(userDataInfo.getId());
        tenantStaffRel.setStaffName(userDataInfo.getLinkman());
        tenantStaffRel.setLockFlag(1);
        tenantStaffRel.setTenantId(tenantId);
        tenantStaffRelService.save(tenantStaffRel);
        LOGGER.debug(JSON.toJSONString(tenantStaffRel));
        saveSysOperLog(SysOperLogConst.BusiCode.Staff, SysOperLogConst.OperType.Add, "?????????????????????????????????", accessToken, tenantStaffRel.getId(), tenantId);
        return tenantStaffRel;
    }

    /**
     * ??????????????????????????????
     */
    private SysRole initPermission(SysTenantDef sysTenantDef, SysUser sysUser, String accessToken) {
        //???????????????????????????
        SysRole sysRole = new SysRole();
        sysRole.setTenantId(sysTenantDef.getId());
        sysRole.setRoleType(1);//???????????????????????????
        sysRole.setState(SysStaticDataEnum.SYS_STATE_DESC.STATE_YES);
        sysRole.setUpdateTime(LocalDateTime.now());
        sysRole.setOpUserId(sysUser.getId());
        sysRole.setRoleName("???????????????");
        sysRole.setRemark("?????????????????????????????????");
        roleService.save(sysRole);
        //???????????????????????????????????????????????????????????????
        SysUserRole sysUserRole = new SysUserRole();
        sysUserRole.setUserId(sysUser.getId());
        sysUserRole.setRoleId(sysRole.getId());
        sysUserRole.setTenantId(sysTenantDef.getId());
        sysUserRoleService.save(sysUserRole);
//        //?????????????????????????????????????????????
//        RoleTemplateUtil roleTemplateUtil = new RoleTemplateUtil();
//        List<RoleEntityPairDto> defaultRoleList = roleTemplateUtil.getDefaultRoleFromTemplate(redisUtil, sysTenantDef.getId(), sysUser.getId());
//        for (RoleEntityPairDto pair : defaultRoleList) {
//            SysRole role = pair.getSysRole();
//            roleService.save(role);
//
//            //       getSysRoleGrantTF().saveSysRoleGrant(role.getRoleId(), pair.getEntityIdList(), sysTenantDef.getTenantId());
//        }
        saveSysOperLog(SysOperLogConst.BusiCode.Entity, SysOperLogConst.OperType.Add, "???????????????????????????????????????", accessToken, sysRole.getId());
        return sysRole;
    }

    /**
     * ??????????????????????????????
     *
     * @param sysTenantDef
     * @param sysOperator
     * @return
     * @throws Exception
     */
    private SysOrganize initOragnize(SysTenantDef sysTenantDef, SysUser sysOperator) {

        //???sys_oragnize????????????????????????????????????
        SysOrganize sysOrganize = new SysOrganize();
        sysOrganize.setTenantId(sysTenantDef.getId());
        sysOrganize.setOrgName(sysTenantDef.getName() + "???????????????");
        sysOrganize.setUserInfoId(sysOperator.getUserInfoId());
        sysOrganize.setParentOrgId(-1L);
        sysOrganize.setLinkMan(sysOperator.getOpName());
        sysOrganize.setState(SysStaticDataEnum.SYS_STATE_DESC.STATE_YES);
        sysOrganizeService.save(sysOrganize);
        SysOrganize root = new SysOrganize();
        root.setTenantId(sysTenantDef.getId());
        root.setParentOrgId(sysOrganize.getId());
        root.setOrgName(sysTenantDef.getName());
        root.setUserInfoId(sysOperator.getUserInfoId());
        root.setLinkMan(sysOperator.getOpName());
        root.setState(SysStaticDataEnum.SYS_STATE_DESC.STATE_YES);
        sysOrganizeService.save(root);
        SysOrganize deletedRoot = new SysOrganize();
        deletedRoot.setTenantId(sysTenantDef.getId());
        deletedRoot.setParentOrgId(root.getId());
        deletedRoot.setOrgName("????????????");
        deletedRoot.setUserInfoId(sysOperator.getUserInfoId());
        deletedRoot.setLinkMan(sysOperator.getOpName());
        deletedRoot.setState(SysStaticDataEnum.SYS_STATE_DESC.STATE_NO);
        sysOrganizeService.save(deletedRoot);
        SysUserOrgRel sysUserOrgRel = new SysUserOrgRel();
        sysUserOrgRel.setOrgId(root.getId());
        sysUserOrgRel.setUserInfoId(root.getUserInfoId());
        sysUserOrgRel.setTenantId(root.getTenantId());
        sysUserOrgRel.setState(SysStaticDataEnum.SYS_STATE_DESC.STATE_YES);
        sysUserOrgRel.setOpDate(LocalDateTime.now());
        sysUserOrgRelService.save(sysUserOrgRel);
        return root;
    }

    /**
     * ????????????????????????????????????
     */
    private void saveOrCorrelateBusinessState(Long tenantId, SysTenantBusinessState businessState) {
        Long businessStateId = businessState.getId();
//        businessState.setTenantId(tenantId);
//        getBusinessStateOBMSSV().saveOrUpdate(businessState);

        if (null == businessStateId) {
            //????????????????????????
            businessState.setTenantId(tenantId);
            sysTenantBusinessStateService.saveOrUpdate(businessState);
        } else {
            //????????????????????????????????????????????????????????????????????????????????????visitId
            if (null == businessState.getVisitId()) {
                throw new BusinessException("???????????????????????????");
            }

            SysTenantBusinessState state = sysTenantBusinessStateService.get(businessStateId);
            BeanUtil.copyProperties(state, businessState);
            state.setTenantId(tenantId);
            sysTenantBusinessStateService.saveOrUpdate(state);
            //?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????Id??????????????????????????????????????????
            sysTenantBusinessStateService.correlateVisitRecord(tenantId, businessState.getVisitId());
            //??????????????????
            SysTenantVisit visit = sysTenantVisitService.get(businessState.getVisitId());
            if (null == visit) {
                throw new BusinessException("????????????????????????");
            }
            visit.setSignState(1);
            sysTenantVisitService.saveOrUpdate(visit);
        }
    }

    /**
     * ??????????????????
     */
    private SysTenantDef updateTenantDef(SysTenantDef sysTenantDef, SysTenantVo sysTenantIn) {
        if (null == sysTenantDef) {
            return null;
        }
        UserDataInfo adminUser = userDataInfoService.getById(sysTenantDef.getAdminUser());

        if (StringUtils.isNotBlank(sysTenantIn.getLinkMan())) {
            sysTenantDef.setLinkMan(sysTenantIn.getLinkMan());
            adminUser.setLinkman(sysTenantIn.getLinkMan());
        }

        if (StringUtils.isNotBlank(sysTenantIn.getLinkManIdentification())) {
            adminUser.setIdentification(sysTenantIn.getLinkManIdentification());
        }
        userDataInfoService.saveOrUpdate(adminUser);


        //????????????
        sysTenantDef.setName(sysTenantIn.getCompanyName());
        sysTenantDef.setShortName(sysTenantIn.getShortName());
        sysTenantDef.setBusinessLicense(sysTenantIn.getBusinessLicense());
        sysTenantDef.setCompanyQualifications(sysTenantIn.getCompanyQualifications());
        sysTenantDef.setBusinessLicenseNo(sysTenantIn.getBusinessLicenseNo());
        sysTenantDef.setLogo(sysTenantIn.getLogo());

        //??????????????????
        sysTenantDef.setProvinceId(sysTenantIn.getProvinceId());
        sysTenantDef.setProvinceName(sysTenantIn.getProvinceName());
        sysTenantDef.setCityId(sysTenantIn.getCityId());
        sysTenantDef.setCityName(sysTenantIn.getCityName());
        sysTenantDef.setDistrictId(sysTenantIn.getDistrictId());
        sysTenantDef.setDistrictName(sysTenantIn.getDistrictName());
        sysTenantDef.setAddress(sysTenantIn.getAddress());
        //??????????????????
        sysTenantDef.setCompanyAddress(sysTenantIn.getCompanyAddress());
        //?????????????????????
        sysTenantDef.setIdentificationPicture(sysTenantIn.getIdentificationPicture());
        sysTenantDef.setIdentification(sysTenantIn.getIdentification());
        sysTenantDef.setActualController(sysTenantIn.getActualController());
        sysTenantDef.setActualControllerPhone(sysTenantIn.getActualControllerPhone());
        /**??????????????????*/
        sysTenantDef.setPreSaleServiceId(sysTenantIn.getPreSaleServiceId());
        sysTenantDef.setPreSaleServiceName(sysTenantIn.getPreSaleServiceName());
        sysTenantDef.setPreSaleOrgId(sysTenantIn.getPreSaleOrgId());
        sysTenantDef.setAfterSaleServiceId(sysTenantIn.getAfterSaleServiceId());
        sysTenantDef.setAfterSaleServiceName(sysTenantIn.getAfterSaleServiceName());
        sysTenantDef.setAfterSaleOrgId(sysTenantIn.getAfterSaleOrgId());
        sysTenantDef.setServiceFee(sysTenantIn.getServiceFee());
        sysTenantDef.setPayServiceFeeDate(sysTenantIn.getPayServiceFeeDate());
//        ????????????????????????
//        updatePayStateByServiceFeeDate(sysTenantDef, sysTenantIn.getPayServiceFeeDate());
        if (sysTenantIn.getSignDate() != null) {
            sysTenantDef.setSignDate(sysTenantIn.getSignDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
        } else {
            sysTenantDef.setSignDate(null);
        }
//        sysTenantDef.setPayDate(sysTenantIn.getPayDate());
//        sysTenantDef.setEntranceDate(sysTenantIn.getEntranceDate());
//        sysTenantDef.setLeaveDate(sysTenantIn.getLeaveDate());
        if (sysTenantIn.getAppointEntranceDate() != null) {
            sysTenantDef.setAppointEntranceDate(sysTenantIn.getAppointEntranceDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
        } else {
            sysTenantDef.setAppointEntranceDate(null);
        }
        sysTenantDef.setDeliveryCycle(sysTenantIn.getDeliveryCycle());

        sysTenantDef.setYlPrivateKey(sysTenantIn.getYlPrivateKey());
        sysTenantDef.setYlPublicKey(sysTenantIn.getYlPublicKey());
        sysTenantDef.setG7Code(sysTenantIn.getG7Code());

        sysTenantDef.setContractCode(sysTenantIn.getContractCode());

        return sysTenantDef;
    }


    private SysTenantDef updateTenantInfoDef(SysTenantDef sysTenantDef, SysTenantInfoVo sysTenantIn) {
        if (null == sysTenantDef) {
            return null;
        }
        UserDataInfo adminUser = userDataInfoService.getById(sysTenantDef.getAdminUser());

        if (StringUtils.isNotBlank(sysTenantIn.getLinkMan())) {
            sysTenantDef.setLinkMan(sysTenantIn.getLinkMan());
            adminUser.setLinkman(sysTenantIn.getLinkMan());
        }

        if (StringUtils.isNotBlank(sysTenantIn.getLinkManIdentification())) {
            adminUser.setIdentification(sysTenantIn.getLinkManIdentification());
        }
        userDataInfoService.saveOrUpdate(adminUser);


        //????????????
        sysTenantDef.setName(sysTenantIn.getCompanyName());
        sysTenantDef.setShortName(sysTenantIn.getShortName());
        sysTenantDef.setBusinessLicense(sysTenantIn.getBusinessLicense());
        sysTenantDef.setCompanyQualifications(sysTenantIn.getCompanyQualifications());
        sysTenantDef.setBusinessLicenseNo(sysTenantIn.getBusinessLicenseNo());
        sysTenantDef.setLogo(sysTenantIn.getLogo());

        //??????????????????
        sysTenantDef.setProvinceId(sysTenantIn.getProvinceId());
        sysTenantDef.setProvinceName(sysTenantIn.getProvinceName());
        sysTenantDef.setCityId(sysTenantIn.getCityId());
        sysTenantDef.setCityName(sysTenantIn.getCityName());
        sysTenantDef.setDistrictId(sysTenantIn.getDistrictId());
        sysTenantDef.setDistrictName(sysTenantIn.getDistrictName());
        sysTenantDef.setAddress(sysTenantIn.getAddress());
        //??????????????????
        sysTenantDef.setCompanyAddress(sysTenantIn.getCompanyAddress());
        //?????????????????????
        sysTenantDef.setIdentificationPicture(sysTenantIn.getIdentificationPicture());
        sysTenantDef.setIdentification(sysTenantIn.getIdentification());
        sysTenantDef.setActualController(sysTenantIn.getActualController());
        sysTenantDef.setActualControllerPhone(sysTenantIn.getActualControllerPhone());
        return sysTenantDef;
    }

    /**
     * ????????????????????????????????????
     */
    private void updatePayStateByServiceFeeDate(SysTenantDef sysTenantDef, String serviceFeeDate) {
        LocalDate now = LocalDate.now();
        String year;
        if (null != sysTenantDef.getPayDate()) {
            year = (DateUtil.getYear(Date.from(sysTenantDef.getPayDate().atZone(ZoneId.systemDefault()).toInstant())) + 1) + "";
        } else {
            year = now.getYear() + "";
        }
        LocalDate oldpayServiceFeeDate = LocalDate.parse(year + "-" + sysTenantDef.getPayServiceFeeDate());
        LocalDate newPayServiceFeeDate = LocalDate.parse(year + "-" + serviceFeeDate);
        int change = oldpayServiceFeeDate.compareTo(newPayServiceFeeDate);
        //?????????????????????????????????????????????
        if (change == 0) {
            return;
        }

        //?????????????????? ????????????????????????
        boolean isExpired = now.compareTo(oldpayServiceFeeDate) > 0;
        boolean willExpired = now.plusDays(3).compareTo(oldpayServiceFeeDate) > 0;

        //?????????????????????????????????????????????????????????????????????
        if (SysStaticDataEnum.TENANT_PAY_STATE.PAYING.equals(sysTenantDef.getPayState())) {
            sysTenantDef.setPayServiceFeeDate(serviceFeeDate);
            return;
        }

        //???????????? ????????????????????????Y?????????????????????????????????????????????????????????
        Integer payState = now.compareTo(newPayServiceFeeDate) > 0 ? SysStaticDataEnum.TENANT_PAY_STATE.EXPIRED : SysStaticDataEnum.TENANT_PAY_STATE.UNEXPIRED;
        sysTenantDef.setPayServiceFeeDate(serviceFeeDate);
        sysTenantDef.setPayState(payState);

        //?????????????????????????????????????????????????????????(???????????????????????????)
        if (isExpired || willExpired) {
//            IServiceChargeReminderTF chargeReminderTF = (IServiceChargeReminderTF) SysContexts.getBean("serviceChargeReminderTF");
//            chargeReminderTF.updatePayPlatformReminder(sysTenantDef);
        }
    }

    /**
     * ??????????????????
     */
    private void updateRootOragnizeName(Long teantId, String orgName) {
        //????????????????????????
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("parent_org_id", -1L);
        queryWrapper.eq("tenant_id", teantId);
        SysOrganize sysOrganize = sysOrganizeService.getOne(queryWrapper);
        queryWrapper.clear();
        queryWrapper.eq("parent_org_id", sysOrganize.getId());
        queryWrapper.eq("tenant_id", teantId);
        queryWrapper.eq("state", SysStaticDataEnum.SYS_STATE_DESC.STATE_YES);
        SysOrganize sysOragnizeOrg = sysOrganizeService.getOne(queryWrapper);
        sysOragnizeOrg.setOrgName(orgName);
        sysOrganizeService.update(sysOragnizeOrg);
    }

    /**
     * ????????????
     */
    private void saveSysOperLog(SysOperLogConst.BusiCode busiCode, SysOperLogConst.OperType operType, String operCommet, String accessToken, Long busid, Long tenantId) {
        SysOperLog operLog = new SysOperLog();
        operLog.setBusiCode(busiCode.getCode());
        operLog.setBusiName(busiCode.getName());
        operLog.setBusiId(busid);
        operLog.setOperType(operType.getCode());
        operLog.setOperTypeName(operType.getName());
        operLog.setOperComment(operCommet);
        sysOperLogService.save(operLog, tenantId, accessToken);
    }

    /**
     * ????????????
     */
    private void saveSysOperLog(SysOperLogConst.BusiCode busiCode, SysOperLogConst.OperType operType, String operCommet, String accessToken, Long busid) {
        SysOperLog operLog = new SysOperLog();
        operLog.setBusiCode(busiCode.getCode());
        operLog.setBusiName(busiCode.getName());
        operLog.setBusiId(busid);
        operLog.setOperType(operType.getCode());
        operLog.setOperTypeName(operType.getName());
        operLog.setOperComment(operCommet);
        sysOperLogService.save(operLog, accessToken);
    }

    private void updatePayStateByPayDate(SysTenantDef sysTenantDef) throws Exception {
        //?????????????????????????????????????????????
        if (SysStaticDataEnum.TENANT_PAY_STATE.PAYING.equals(sysTenantDef.getPayState())) {
            return;
        }

        if (StringUtils.isBlank(sysTenantDef.getPayServiceFeeDate())) {
            throw new BusinessException("????????????????????????");
        }

        LocalDate now = LocalDate.now();
        LocalDate payDate = sysTenantDef.getPayDate().toLocalDate();
        LocalDate nexPayDate = LocalDate.parse((payDate.getYear() + 1) + "-" + sysTenantDef.getPayServiceFeeDate());

        //?????????????????? ????????????????????????
        boolean isExpired = now.compareTo(nexPayDate) > 0;
        boolean willExpired = now.plusDays(3).compareTo(nexPayDate) > 0;

        Integer payState = now.compareTo(nexPayDate) > 0 ? SysStaticDataEnum.TENANT_PAY_STATE.EXPIRED : SysStaticDataEnum.TENANT_PAY_STATE.UNEXPIRED;
        sysTenantDef.setPayState(payState);

        //?????????????????????????????????????????????????????????
        if (isExpired || willExpired) {
            serviceChargeReminderService.operateServiceChargeReminder(sysTenantDef);
        }
    }

    /**
     * ?????????????????? ??????????????????????????????
     *
     * @param list
     * @return void
     * @throws
     * @author terry
     * @date 2022/5/31 14:53
     */
    private void queryTenantDef(List<SysTenantDef> list) {
        for (int i = 0; i < list.size(); i++) {
            SysTenantDef tenant = list.get(i);
            // ????????????????????? ???????????????????????????
            Integer bindCard = baseMapper.isTenantBindCard(tenant.getId());
            if (bindCard != null && bindCard > 0) {
                tenant.setBindCardStr(sysStaticDataService.getSysStaticData("BIND_STATE", EnumConsts.BIND_STATE.BIND_STATE_ALL + "").getCodeName());
                tenant.setBindState(EnumConsts.BIND_STATE.BIND_STATE_ALL);
            } else {
                tenant.setBindCardStr(sysStaticDataService.getSysStaticData("BIND_STATE", EnumConsts.BIND_STATE.BIND_STATE_NONE + "").getCodeName());
                tenant.setBindState(EnumConsts.BIND_STATE.BIND_STATE_NONE);
            }
            //????????????????????????
            UserDataInfo userDataInfo = userDataInfoService.getById(tenant.getAdminUser());
            if (userDataInfo != null) {
                tenant.setLinkManIdentification(userDataInfo.getIdentification());
            }
            if (null != tenant.getPreSaleServiceId()) {
                UserDataInfo preSaleUser = userDataInfoService.getById(tenant.getPreSaleServiceId());
                if (preSaleUser != null) {
                    tenant.setPreSaleServicePhone(preSaleUser.getMobilePhone());
                    tenant.setPreSaleServiceName(preSaleUser.getLinkman());
                }
            }
            if (null != tenant.getAfterSaleServiceId()) {
                UserDataInfo afterSaleUser = userDataInfoService.getById(tenant.getAfterSaleServiceId());
                if (afterSaleUser != null) {
                    tenant.setAfterSaleServicePhone(afterSaleUser.getMobilePhone());
                    tenant.setAfterSaleServiceName(afterSaleUser.getLinkman());
                }
            }
        }
    }

    @Override
    public SysTenantDef getSysTenantDef(long tenantId, boolean isAllTenant) {
        if (!isAllTenant) {
            return this.getSysTenantDef(tenantId);
        }
        SysTenantDef one = this.getById(tenantId);
        if (one != null) {
            return one;
        }
        return new SysTenantDef();
    }

    @Override
    public SysTenantDef getSysTenantDef(long tenantId) {
        LambdaQueryWrapper<SysTenantDef> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysTenantDef::getId, tenantId);
        queryWrapper.eq(SysTenantDef::getState, SysStaticDataEnum.SYS_STATE_DESC.STATE_YES);
        SysTenantDef one = this.getOne(queryWrapper);
        if (one != null) {
            return one;
        }
        return null;
    }

    @Override
    public Long getTenantAdminUser(Long tenantId) {
        SysTenantDef sysTenantDef = this.getSysTenantDef(tenantId, true);
        if (sysTenantDef == null) {
            return null;

        }
        return sysTenantDef.getAdminUser();
    }

    @Override
    public SysTenantDef getSysTenantDefByAdminUserId(Long userId) {
        LambdaQueryWrapper<SysTenantDef> lambda = new QueryWrapper<SysTenantDef>().lambda();
        lambda.eq(SysTenantDef::getAdminUser, userId)
                .eq(SysTenantDef::getState, SysStaticDataEnum.SYS_STATE_DESC.STATE_YES);
        SysTenantDef sysTenantDef = this.getOne(lambda);
        return sysTenantDef;
    }


    @Override
    public Map getTenantInfo(long tenantId) {
        SysTenantDef sysTenantDef = this.getSysTenantDef(tenantId, true);
        Map map = new HashMap();
        map.put("tenantName", sysTenantDef.getName());
        map.put("tenantLogo", sysTenantDef.getLogo());
        map.put("provinceId", sysTenantDef.getProvinceId());
        map.put("cityId", sysTenantDef.getCityId());
        map.put("detailAddr", sysTenantDef.getAddress());
        map.put("shortName", sysTenantDef.getShortName());
        return map;
    }

    @Override
    public SysTenantDef getSysTenantDef(String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        return getSysTenantDef(loginInfo.getTenantId());
    }

    @Override
    public Boolean isAdminUser(Long userId, Long tenantId) {
        LambdaQueryWrapper<SysTenantDef> lambda = new QueryWrapper<SysTenantDef>().lambda();
        lambda.eq(SysTenantDef::getAdminUser, userId);

        SysTenantDef sysTenantDef = this.getOne(lambda);

        if (null == sysTenantDef) {
            return false;
        }

        return null == tenantId || tenantId.equals(sysTenantDef.getId());
    }

    @Override
    public void updatePayDate(Long tenantId, Date payDate) {
        SysTenantDef sysTenantDef = this.getById(tenantId);
        if (null == sysTenantDef.getFirstPayDate()) {
            sysTenantDef.setFirstPayDate(getDateToLocalDateTime(payDate));
        }
        sysTenantDef.setPayDate(getDateToLocalDateTime(payDate));
        this.save(sysTenantDef);
    }

    @Override
    public void updatePayState(long tenantId, int state) {
        SysTenantDef sysTenantDef = this.getById(tenantId);
        sysTenantDef.setState(state);
        this.update(sysTenantDef);
    }

    @Override
    public Boolean isSetPayPasswd(Long userId, Integer userType, String accessToken) {
        Boolean flag = true;
        LoginInfo loginInfo = loginUtils.get(accessToken);
        if (userType == 1) {
//            SysTenantDef sysTenantDef = this.getSysTenantDef(loginInfo.getTenantId());
//            if (sysTenantDef != null) {
//                userId = this.getTenantAdminUser(loginInfo.getTenantId());//????????????UserId
//            } else {
            userId = loginInfo.getUserInfoId();
            //       }
            if (userId == null) {
                throw new BusinessException("??????????????????????????????!");
            }
        }

        Boolean notSetPayPasswd = userDataInfoService.doInit(userId);
        if (!notSetPayPasswd) {
            flag = false;
        }
        return flag;
    }

    @Override
    public SysTenantOutDto getSysTenantDefById(Long tenantId) {
        if (null == tenantId || tenantId <= 0) {
            return null;
        }

        SysTenantDef sysTenantDef = this.getSysTenantDef(tenantId, true);
        SysTenantOutDto sysTenantOut = new SysTenantOutDto();
        BeanUtils.copyProperties(sysTenantDef, sysTenantOut);
        sysTenantOut.setCompanyName(sysTenantDef.getName());

        //??????????????????
        BillSetting billSetting = billSettingService.getBillSetting(tenantId);
        sysTenantOut.setBillSetting(billSetting);

        //??????????????????
        UserDataInfo userDataInfo = userDataInfoService.getUserDataInfo(sysTenantDef.getAdminUser());
        sysTenantOut.setLinkManIdentification(userDataInfo.getIdentification());
        sysTenantOut.setAdminUser(userDataInfo.getId());

        SysTenantBusinessState businessState = sysTenantBusinessStateService.queryByTenantId(tenantId);
        sysTenantOut.setBusinessState(businessState);

        if (sysTenantDef.getPreSaleServiceId() != null) {
            UserDataInfo preSaleUser = userDataInfoService.getUserDataInfo(sysTenantDef.getPreSaleServiceId());
            sysTenantOut.setPreSaleServicePhone(preSaleUser.getMobilePhone());
            sysTenantOut.setPreSaleServiceName(preSaleUser.getLinkman());
        }
        if (sysTenantDef.getAfterSaleServiceId() != null) {
            UserDataInfo afterSaleUser = userDataInfoService.getUserDataInfo(sysTenantDef.getAfterSaleServiceId());
            sysTenantOut.setAfterSaleServicePhone(afterSaleUser.getMobilePhone());
            sysTenantOut.setAfterSaleServiceName(afterSaleUser.getLinkman());
        }
        return sysTenantOut;
    }

    private LocalDateTime getDateToLocalDateTime(Date date) {
        Instant instant = date.toInstant();
        ZoneId zoneId = ZoneId.systemDefault();
        LocalDateTime localDateTime = instant.atZone(zoneId).toLocalDateTime();
        return localDateTime;
    }


    @Override
    public List<SysTenantDef> getSysTenantDefByName(String name) {
        List<SysTenantDef> sysTenantDefByName = baseMapper.getSysTenantDefByName(name);
        return sysTenantDefByName;
    }

    @Override
    public Boolean isAdminUser(Long userId) {
        if (null == userId || userId <= 0) {
            return false;
        }
        LambdaQueryWrapper<SysTenantDef> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysTenantDef::getAdminUser, userId);
        List<SysTenantDef> list = this.list(queryWrapper);
        if (CollectionUtils.isNotEmpty(list)) {
            return true;
        }
        return false;
    }

    @Override
    public SysTenantDef getSysTenantDefByAdminUserId(Long userId, Boolean isAllTenant) {
        if (!isAllTenant) {
            return getSysTenantDefByAdminUserId(userId);
        }
        LambdaQueryWrapper<SysTenantDef> lambda = new QueryWrapper<SysTenantDef>().lambda();
        lambda.eq(SysTenantDef::getAdminUser, userId);
        SysTenantDef sysTenantDef = this.getOne(lambda);
        return sysTenantDef;
    }

    @Override
    public TenantOrUserBindCardDto isTenantOrUserBindCard(Long tenantId, Long userId, Integer type) {

        boolean isBind = false;
        TenantOrUserBindCardDto dto = new TenantOrUserBindCardDto();

        if (type == 1) {
            SysTenantDef tenantInfo = this.getSysTenantDef(tenantId);
            if (tenantInfo == null) {
                throw new BusinessException("??????????????????");
            }
            boolean isBandPriCard = iAccountBankUserTypeRelService.isUserTypeBindCard(tenantInfo.getAdminUser(), EnumConsts.ACCOUNT_BANK_REL.BANK_TYPE_0, SysStaticDataEnum.USER_TYPE.ADMIN_USER);
            boolean isBandPubCard = iAccountBankUserTypeRelService.isUserTypeBindCard(tenantInfo.getAdminUser(), EnumConsts.ACCOUNT_BANK_REL.BANK_TYPE_1, SysStaticDataEnum.USER_TYPE.ADMIN_USER);
            if (isBandPriCard && isBandPubCard) {
                isBind = true;
            }
        } else if (type == 2) {
            isBind = iAccountBankUserTypeRelService.isUserTypeBindCard(userId, EnumConsts.ACCOUNT_BANK_REL.BANK_TYPE_0, SysStaticDataEnum.USER_TYPE.DRIVER_USER);
        } else if (type == 3) {

            dto.setDriverVer(1);
            UserDataInfo userDataInfo = userDataInfoService.getUserDataInfo(userId);
            if (userDataInfo == null) {
                throw new BusinessException("???????????????????????????");
            }

            //??????????????????
            //state	               ????????????  1???????????? 2???????????? 3?????????????????????????????????  ??????????????????
            if (userDataInfo.getAuthState() != null
                    && userDataInfo.getAuthState() == SysStaticDataEnum.AUTH_STATE.AUTH_STATE1) {
                dto.setDriverVer(0);
            }
            if (userDataInfo.getTenantId() != null && userDataInfo.getTenantId() > 0) {
                TenantUserRel tenantUserRel = iTenantUserRelService.getAllTenantUserRelByUserId(userId, userDataInfo.getTenantId());
                if (tenantUserRel != null) {
                    if (tenantUserRel.getState() != null
                            && tenantUserRel.getState() == SysStaticDataEnum.AUTH_STATE.AUTH_STATE1) {
                        dto.setDriverVer(0);
                    }
                }
            }
            isBind = iAccountBankUserTypeRelService.isUserTypeBindCard(userId, EnumConsts.ACCOUNT_BANK_REL.BANK_TYPE_0, SysStaticDataEnum.USER_TYPE.DRIVER_USER);
        }

        dto.setResult("NO");

        if (isBind) {
            dto.setResult("YES");
        }

        return dto;
    }

    @Override
    public String getTenantName(Long tenantId) {
        SysTenantDef sysTenantDef = this.getSysTenantDef(tenantId);
        if (sysTenantDef == null) {
            return "";
        }
        return sysTenantDef.getName();
    }

    @Override
    public List<SysTenantDef> querySysTenantDef(String tenantName) {
        return this.querySysTenantDef(tenantName, null);
    }

    @Override
    public List<SysTenantDef> querySysTenantDef(String tenantName, Long excludeTenantId) {
        return this.querySysTenantDef(tenantName, excludeTenantId, null);
    }

    @Override
    public List<SysTenantDef> querySysTenantDef(String tenantName, Long excludeTenantId, Integer virtualState) {
        LambdaQueryWrapper<SysTenantDef> queryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotEmpty(tenantName)) {
            queryWrapper.like(SysTenantDef::getName, "%" + tenantName + "%");
        }
        if (null != excludeTenantId && excludeTenantId > 0) {
            queryWrapper.ne(SysTenantDef::getId, excludeTenantId);
        }
        if (null != virtualState) {
            queryWrapper.eq(SysTenantDef::getVirtualState, virtualState);
        }
        queryWrapper.eq(SysTenantDef::getState, SysStaticDataEnum.SYS_STATE_DESC.STATE_YES);
        return this.list(queryWrapper);
    }

    @Override
    public Long getTenantIdCount() {
        return baseMapper.getTenantIdCount();
    }

    @Override
    public List<Long> getTenantId(Integer startLimit, Integer endLimit) {
        return baseMapper.getTenantId(startLimit, endLimit);
    }
}
