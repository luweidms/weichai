package com.youming.youche.record.provider.service.impl.user;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.cloud.api.sms.ISysSmsSendService;
import com.youming.youche.cloud.domin.sms.SysSmsSend;
import com.youming.youche.commons.constant.SysOperLogConst;
import com.youming.youche.commons.domain.*;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.util.DataFormat;
import com.youming.youche.commons.util.LoginUtils;
import com.youming.youche.commons.util.RedisUtil;
import com.youming.youche.components.fdfs.FastDFSHelper;
import com.youming.youche.components.workbench.WorkbenchDto;
import com.youming.youche.conts.OrderAccountConst;
import com.youming.youche.conts.SysStaticDataEnum;
import com.youming.youche.market.api.facilitator.IServiceProductService;
import com.youming.youche.record.api.account.IAccountBankRelService;
import com.youming.youche.record.api.account.IOrderAccountService;
import com.youming.youche.record.api.apply.IApplyRecordService;
import com.youming.youche.record.api.cm.ICmCustomerLineService;
import com.youming.youche.record.api.dirverInfo.IDriverInfoExtService;
import com.youming.youche.record.api.driver.IAbstractDriverService;
import com.youming.youche.record.api.order.IOrderSchedulerService;
import com.youming.youche.record.api.pay.IPayFeeLimitService;
import com.youming.youche.record.api.sys.ISysUserService;
import com.youming.youche.record.api.tenant.ITenantStaffRelService;
import com.youming.youche.record.api.tenant.*;
import com.youming.youche.record.api.user.*;
import com.youming.youche.record.api.vehicle.ITenantVehicleRelVerService;
import com.youming.youche.record.api.vehicle.IVehicleDataInfoService;
import com.youming.youche.record.api.vehicle.IVehicleDataInfoVerService;
import com.youming.youche.record.common.*;
import com.youming.youche.record.domain.account.AccountBankRel;
import com.youming.youche.record.domain.apply.ApplyRecord;
import com.youming.youche.record.domain.cm.CmCustomerLine;
import com.youming.youche.record.domain.tenant.TenantStaffRel;
import com.youming.youche.record.domain.tenant.*;
import com.youming.youche.record.domain.user.UserDataInfo;
import com.youming.youche.record.domain.user.*;
import com.youming.youche.record.domain.vehicle.VehicleDataInfo;
import com.youming.youche.record.domain.vehicle.VehicleDataInfoVer;
import com.youming.youche.record.dto.ApplyRecordQueryDto;
import com.youming.youche.record.dto.UserInfoDto;
import com.youming.youche.record.dto.driver.DoQueryDriversDto;
import com.youming.youche.record.dto.driver.DoQueryOBMDriversDto;
import com.youming.youche.record.dto.driver.DriverApplyRecordNumDto;
import com.youming.youche.record.dto.driver.DriverQueryDto;
import com.youming.youche.record.provider.mapper.tenant.TenantUserRelMapper;
import com.youming.youche.record.provider.mapper.user.UserDataInfoRecordMapper;
import com.youming.youche.record.provider.utils.ExcelUtils;
import com.youming.youche.record.provider.utils.ReadisUtil;
import com.youming.youche.record.provider.utils.SysCfgUtil;
import com.youming.youche.record.provider.utils.SysStaticDataRedisUtils;
import com.youming.youche.record.vo.ApplyRecordVo;
import com.youming.youche.record.vo.*;
import com.youming.youche.record.vo.driver.*;
import com.youming.youche.system.api.*;
import com.youming.youche.system.api.audit.IAuditNodeInstService;
import com.youming.youche.system.api.audit.IAuditService;
import com.youming.youche.system.api.audit.IAuditSettingService;
import com.youming.youche.system.api.ioer.ImportOrExportRecordsService;
import com.youming.youche.system.domain.*;
import com.youming.youche.system.utils.excel.ExportExcel;
import com.youming.youche.util.CommonUtils;
import com.youming.youche.util.DateUtil;
import com.youming.youche.util.ExcelFilesVaildate;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.common.utils.CollectionUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;


/**
 * @version:
 * @Title: UserServiceImpl
 * @Package: com.youming.youche.record.provider.service.impl.user
 * @Description: TODO(????????????????????????????????????)
 * @author:DengYuanYe
 * @date: 2021/12/20 15:50
 * @company:
 */
@DubboService(version = "1.0.0")
public class UserServiceImpl implements IUserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);


    @DubboReference(version = "1.0.0")
    ISysStaticDataService isysStaticDataService;

    @DubboReference(version = "1.0.0")
    IAuditNodeInstService iAuditNodeInstService;


    @Resource
    IUserDataInfoRecordService iUserDataInfoRecordService;

    @DubboReference(version = "1.0.0")
    ISysOrganizeService iSysOrganizeService;

    @DubboReference(version = "1.0.0")
    ISysOperLogService sysOperLogService;

    @DubboReference(version = "1.0.0")
    IAuditSettingService iAuditSettingService;

    @Autowired
    IOrderSchedulerService orderSchedulerService;

    @Resource
    IAccountBankRelService iAccountBankRelService;

    @Resource
    TenantUserRelMapper tenantUserRelMapper;

    @Resource
    ITenantUserRelService iTenantUserRelService;

    @Resource
    ITenantServiceRelService iTenantServiceRelService;

    @Resource
    UserDataInfoRecordMapper userDataInfoRecordMapper;
    @Lazy
    @Resource
    ISysUserService iSysUserService;

    @Resource
    ITenantStaffRelService iTenantStaffRelService;

    @DubboReference(version = "1.0.0")
    ISysTenantDefService sysTenantDefService;

    @Resource
    IUserDataInfoVerService iUserDataInfoVerService;

    @Resource
    ITenantUserRelVerService iTenantUserRelVerService;

    @DubboReference(version = "1.0.0")
    IAuditService iAuditService;

    @Resource
    RedisUtil redisUtil;

    @Lazy
    @Resource
    IApplyRecordService iApplyRecordService;

    @Lazy
    @Resource
    IVehicleDataInfoService iVehicleDataInfoService;

    @Resource
    ITenantVehicleRelService iTenantVehicleRelService;

    @Resource
    IVehicleDataInfoVerService iVehicleDataInfoVerService;

    @Resource
    ITenantVehicleRelVerService iTenantVehicleRelVerService;

    @Resource
    ITenantUserSalaryRelService iTenantUserSalaryRelService;

    @Resource
    ITenantUserSalaryRelVerService iTenantUserSalaryRelVerService;

    @Resource
    IUserSalaryInfoService iUserSalaryInfoService;

    @Resource
    IUserSalaryInfoVerService iUserSalaryInfoVerService;

    @Resource
    IUserLineRelService iUserLineRelService;

    @Resource
    IUserLineRelVerService iUserLineRelVerService;

    @Resource
    IAbstractDriverService iAbstractDriverService;

    @Resource
    IDriverInfoExtService iDriverInfoExtService;

    @Resource
    IPayFeeLimitService iPayFeeLimitService;

    @DubboReference(version = "1.0.0")
    ISysSmsSendService iSysSmsSendService;

    @DubboReference(version = "1.0.0")
    ImportOrExportRecordsService importOrExportRecordsService;

    @DubboReference(version = "1.0.0")
    IWechatUserRelService wechatUserRelService;

    @Resource
    IOrderAccountService iOrderAccountService;

    @Resource
    ICmCustomerLineService cmCustomerInfoLineSV;


    @Resource
    LoginUtils loginUtils;

    @DubboReference(version = "1.0.0")
    ISysAttachService iSysAttachService;

//    @DubboReference(version = "1.0.0")
//    IWechatUserRelService iWechatUserRelService;
//
//    @DubboReference(version = "1.0.0")
//    IAccountBankUserTypeRelService iAccountBankUserTypeRelService;
//
//    @DubboReference(version = "1.0.0")
//    IServiceInfoService iServiceInfoService;

    @Resource
    IUserReceiverInfoService iUserReceiverInfoService;

    @DubboReference(version = "1.0.0")
    ISysUserOrgRelService iSysUserOrgRelService;

    @DubboReference(version = "1.0.0")
    IServiceProductService iServiceProductService;

    @DubboReference(version = "1.0.0")
    ISysCfgService sysCfgService;

    @DubboReference(version = "1.0.0")
    IUserDataInfoService userDataInfoService;

    /**
     * ??????????????????????????????????????????
     *
     * @param
     * @return key:driverInvite(????????????) value:?????? key:driverBeInvite(????????????) value:??????
     */
    @Override
    public DriverApplyRecordNumDto queryInviteNum(String accessToken) throws Exception {
        LoginInfo user = iUserDataInfoRecordService.getLoginInfoByAccessToken(accessToken);
        Long tenantId = user.getTenantId();
        DriverApplyRecordNumDto driverApplyRecordNumDto = new DriverApplyRecordNumDto();
        QueryWrapper<ApplyRecord> driverInviteQw = new QueryWrapper<>();
        driverInviteQw.eq("APPLY_TYPE", 1);
        driverInviteQw.eq("READ_STATE", 0);
        driverInviteQw.notIn("STATE", 0, 3);
        driverInviteQw.eq("APPLY_TENANT_ID", tenantId);
        List<ApplyRecord> driverInviteList = iApplyRecordService.getBaseMapper().selectList(driverInviteQw);
        driverApplyRecordNumDto.setDriverInvite(0);
        if (driverInviteList != null && driverInviteList.size() > 0) {
            driverApplyRecordNumDto.setDriverInvite(driverInviteList.size());
        }
        QueryWrapper<ApplyRecord> driverBeInviteQw = new QueryWrapper<>();
        driverBeInviteQw.eq("APPLY_TYPE", 1);
        driverBeInviteQw.eq("STATE", 0);
        driverBeInviteQw.eq("BE_APPLY_TENANT_ID", tenantId);
        List<ApplyRecord> driverBeInviteList = iApplyRecordService.getBaseMapper().selectList(driverBeInviteQw);
        driverApplyRecordNumDto.setDriverBeInvite(0);
        if (driverBeInviteList != null && driverBeInviteList.size() > 0) {
            driverApplyRecordNumDto.setDriverBeInvite(driverBeInviteList.size());
        }
        return driverApplyRecordNumDto;
    }

    /**
     * /**
     * ??????????????????
     * ?????????loginAcct ??????
     * linkman ??????
     * carUserType ????????????
     * attachTenantName ??????????????????
     * attachTennantLinkman ???????????????
     * attachTennantLinkPhone ?????????????????????
     * state ????????????
     * ?????????loginAcct ??????
     * linkman ??????
     * carUserType ????????????
     * carUserTypeName ??????????????????
     * attachTenantId ????????????id
     * attachTenantName ??????????????????
     * attachTennantLinkman ???????????????
     * attachTennantLinkPhone ?????????????????????
     * vehicleNum ????????????
     * state ????????????
     * stateName ??????????????????
     * hasVer ????????????
     * userId ????????????
     * createDate ????????????
     */
    @Override
    public Page doQueryCarDriver(DoQueryDriversVo doQueryDriversVo, Integer page, Integer pageSize, String accessToken) throws Exception {
        LoginInfo loginInfo = iUserDataInfoRecordService.getLoginInfoByAccessToken(accessToken);
        Long tenantId = loginInfo.getTenantId();
        Date expiredDate = getExpiredNotifyDate(accessToken);
        String userType = "1,3";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = simpleDateFormat.format(expiredDate);
        Page<DoQueryDriversDto> pageInfo = new Page<>(page, pageSize);
        List<Long> busiIds = new ArrayList<>();
        Page<DoQueryDriversDto> driversDtoPage = userDataInfoRecordMapper.doQueryCarDriver(pageInfo, tenantId, userType, date, doQueryDriversVo);
        if (driversDtoPage.getRecords() != null && driversDtoPage.getRecords().size() > 0) {
            for (DoQueryDriversDto doQueryDriversDto : driversDtoPage.getRecords()) {
                busiIds.add(doQueryDriversDto.getRelId());
            }
            Map<Long, Boolean> hasPermissionMap = null;
            if (!busiIds.isEmpty()) {
                hasPermissionMap = iAuditNodeInstService.isHasPermission(AuditConsts.AUDIT_CODE.AUDIT_CODE_USER, busiIds, accessToken);
            }
            for (DoQueryDriversDto doQueryDriversDto : driversDtoPage.getRecords()) {
                if (doQueryDriversDto.getCarUserType() > 0) {
                    doQueryDriversDto.setCarUserTypeName(isysStaticDataService.getSysStaticData("DRIVER_TYPE", String.valueOf(doQueryDriversDto.getCarUserType())).getCodeName());
                }
                if (doQueryDriversDto.getAttachTenantId() == -1) {
                    doQueryDriversDto.setAttachTenantName("???????????????");
                }
                if (doQueryDriversDto.getState() >= 0) {
                    if (doQueryDriversDto.getState() > 1) {
                        doQueryDriversDto.setStateName(isysStaticDataService.getSysStaticData("USER_STATE", String.valueOf(SysStaticDataEnum.SYS_USER_STATE.AUDIT_APPROVE)).getCodeName());
                    }
                    doQueryDriversDto.setStateName(isysStaticDataService.getSysStaticData("USER_STATE", String.valueOf(doQueryDriversDto.getState())).getCodeName());
                }
                if (hasPermissionMap != null && hasPermissionMap.containsKey(doQueryDriversDto.getRelId()) && hasPermissionMap.get(doQueryDriversDto.getRelId())) {
                    doQueryDriversDto.setHasVer("0");
                } else {
                    doQueryDriversDto.setHasVer("1");
                }
                expired(doQueryDriversDto, accessToken);
                if (doQueryDriversDto.getAuthReason() != null) {
                    getAuthReason(doQueryDriversDto, loginInfo.getTenantId());
                }
                if (iAccountBankRelService.isUserTypeBindCardAll(doQueryDriversDto.getUserId(), SysStaticDataEnum.USER_TYPE.DRIVER_USER)) {
                    doQueryDriversDto.setBindState(EnumConsts.BIND_STATE.BIND_STATE_ALL);
                    doQueryDriversDto.setBindStateName(isysStaticDataService.getSysStaticData("BIND_STATE", EnumConsts.BIND_STATE.BIND_STATE_ALL + "").getCodeName());
                } else {
                    doQueryDriversDto.setBindState(EnumConsts.BIND_STATE.BIND_STATE_NONE);
                    doQueryDriversDto.setBindStateName(isysStaticDataService.getSysStaticData("BIND_STATE", EnumConsts.BIND_STATE.BIND_STATE_NONE + "").getCodeName());
                }
            }
        }
        return driversDtoPage;
    }

    @Override
    @Async
    public void doQueryCarDriverList(DoQueryDriversVo doQueryDriversVo, String accessToken, ImportOrExportRecords record) throws Exception {

        LoginInfo loginInfo = iUserDataInfoRecordService.getLoginInfoByAccessToken(accessToken);
        try {
            Long tenantId = loginInfo.getTenantId();
            Date expiredDate = getExpiredNotifyDate(accessToken);
            String userType = "1,3";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String date = simpleDateFormat.format(expiredDate);
            List<Long> busiIds = new ArrayList<>();
            List<DoQueryDriversDto> driversDtoList = userDataInfoRecordMapper.doQueryCarDriverExport(tenantId, userType, date, doQueryDriversVo);
            if (driversDtoList != null && driversDtoList.size() > 0) {
                for (DoQueryDriversDto doQueryDriversDto : driversDtoList) {
                    busiIds.add(doQueryDriversDto.getRelId());
                }
                Map<Long, Boolean> hasPermissionMap = null;
                if (!busiIds.isEmpty()) {
                    hasPermissionMap = iAuditNodeInstService.isHasPermission(AuditConsts.AUDIT_CODE.AUDIT_CODE_USER, busiIds, accessToken);
                }
                for (DoQueryDriversDto doQueryDriversDto : driversDtoList) {
                    if (doQueryDriversDto.getCarUserType() > 0) {
                        doQueryDriversDto.setCarUserTypeName(isysStaticDataService.getSysStaticData("DRIVER_TYPE", String.valueOf(doQueryDriversDto.getCarUserType())).getCodeName());
                    }
                    if (doQueryDriversDto.getAttachTenantId() == -1) {
                        doQueryDriversDto.setAttachTenantName("???????????????");
                    }
                    if (doQueryDriversDto.getState() >= 0) {
                        if (doQueryDriversDto.getState() > 1) {
                            doQueryDriversDto.setStateName(isysStaticDataService.getSysStaticData("USER_STATE", String.valueOf(SysStaticDataEnum.SYS_USER_STATE.AUDIT_APPROVE)).getCodeName());
                        }
                        doQueryDriversDto.setStateName(isysStaticDataService.getSysStaticData("USER_STATE", String.valueOf(doQueryDriversDto.getState())).getCodeName());
                    }
                    expired(doQueryDriversDto, accessToken);
                    if (doQueryDriversDto.getAuthReason() != null) {
                        getAuthReason(doQueryDriversDto, loginInfo.getTenantId());
                    }
                    if (iAccountBankRelService.isUserTypeBindCardAll(doQueryDriversDto.getUserId(), SysStaticDataEnum.USER_TYPE.DRIVER_USER)) {
                        doQueryDriversDto.setBindState(EnumConsts.BIND_STATE.BIND_STATE_ALL);
                        doQueryDriversDto.setBindStateName(isysStaticDataService.getSysStaticData("BIND_STATE", EnumConsts.BIND_STATE.BIND_STATE_ALL + "").getCodeName());
                    } else {
                        doQueryDriversDto.setBindState(EnumConsts.BIND_STATE.BIND_STATE_NONE);
                        doQueryDriversDto.setBindStateName(isysStaticDataService.getSysStaticData("BIND_STATE", EnumConsts.BIND_STATE.BIND_STATE_NONE + "").getCodeName());
                    }
                }
                String[] showName = null;
                String[] resourceFild = null;
                showName = new String[]{"????????????", "????????????", "????????????", "????????????", "???????????????", "????????????", "????????????", "????????????", "????????????"};
                resourceFild = new String[]{"getLoginAcct", "getLinkman", "getCarUserTypeName", "getAttachTenantName", "getAttachTennantLinkman", "getAttachTennantLinkPhone", "getExpiredString", "getVehicleNum", "getStateName"};
                XSSFWorkbook workbook = ExportExcel.getWorkbookXlsx(driversDtoList, showName, resourceFild, DoQueryDriversDto.class, null);
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                workbook.write(os);
                byte[] b = os.toByteArray();
                InputStream inputStream = new ByteArrayInputStream(b);
                //????????????
                FastDFSHelper client = FastDFSHelper.getInstance();
                String path = client.upload(inputStream, "????????????.xlsx", inputStream.available());
                os.close();
                inputStream.close();
                record.setMediaUrl(path);
                record.setState(2);
                importOrExportRecordsService.update(record);
            } else {
                record.setState(4);
                importOrExportRecordsService.update(record);
            }
        } catch (Exception e) {
            record.setState(3);
            importOrExportRecordsService.update(record);
            e.printStackTrace();
        }
    }

    @Transactional
    @Async
    @Override
    public void driverImport(byte[] byteBuffer, ImportOrExportRecords records, String token) {
        LoginInfo loginInfo = iUserDataInfoRecordService.getLoginInfoByAccessToken(token);
        List<DoAddDriverVo> failureList = new ArrayList<>();
        try {
            StringBuffer reasonFailure = new StringBuffer();
            InputStream is = new ByteArrayInputStream(byteBuffer);
            List<List<String>> lists = ExcelUtils.getExcelContent(is, 1, (ExcelFilesVaildate[]) null);
            //???????????????
            List<String> listsRows = lists.remove(0);
            int maxNum = 300;
            if (lists.size() == 0) {
                reasonFailure.append("??????????????????0???????????????!");
            }
            if (lists.size() > maxNum) {
                reasonFailure.append("?????????????????????" + maxNum + "???????????????????????????[" + lists.size() + "]");
            }
            int j = 1;
            int success = 0;
            for (List<String> rowData : lists) {
                int count = rowData.size();
                reasonFailure = checkDate(rowData);
                Map<String, Object> inParam = new HashMap();
                String loginAcct = rowData.get(0).trim();//????????????
                if (StrUtil.isEmpty(loginAcct)) {
                    reasonFailure.append("?????????????????????");
                }
                String carUserTypeString = rowData.get(1).trim();//????????????
                String linkman = rowData.get(2).trim();//????????????
                String identification = rowData.get(3).trim();//????????????
                if (StrUtil.isEmpty(identification)) {
                    reasonFailure.append("?????????????????????");
                }
                String adriverLicenseSn = rowData.get(4).trim();//????????????
                String vehicleType = rowData.get(5).trim();//????????????
                String orgName = rowData.get(6).trim();//????????????
                String attachedUserPhone = rowData.get(7).trim();//?????????
                String lineCodeRules = rowData.get(8).trim();//????????????
                String salaryPatternString = rowData.get(9).trim();//????????????
                String salary = rowData.get(10).trim();//????????????
                String subsidy = rowData.get(11).trim();//????????????
                String driverLicenseTime = "";
                String driverLicenseExpiredTime = "";
                String qcCertiTime = "";
                String qcCertiExpiredTime = "";
                if (count > 12) {
                    String[] s = rowData.get(12).trim().split(" ");
                    driverLicenseTime = s[0];
                    String[] s1 = rowData.get(13).trim().split(" ");
                    driverLicenseExpiredTime = s1[0];
                    String[] s2 = rowData.get(14).trim().split(" ");
                    qcCertiTime = s2[0];
                    String[] s3 = rowData.get(15).trim().split(" ");
                    qcCertiExpiredTime = s3[0];
                }
                Integer carUserType = getCarUserTypeByName(carUserTypeString);
                if (null == carUserType) {
                    reasonFailure.append("?????????????????????");
                }
                UserDataInfo userDataInfo = iUserDataInfoRecordService.getDriver(loginAcct);
                TenantUserRel tenantUserRel = null;
                if (null != userDataInfo) {
                    tenantUserRel = iTenantUserRelService.getAllTenantUserRelByUserId(userDataInfo.getId(), loginInfo.getTenantId());
                    if (null != tenantUserRel) {
                        reasonFailure.append("????????????????????????");
                    }

                    if (null != tenantUserRel && !tenantUserRel.getCarUserType().equals(carUserType)) {
                        reasonFailure.append("??????????????????????????????????????????????????????");
                    }

                    if (null == tenantUserRel && isDriver(userDataInfo.getId(), null)) {
                        //?????????????????????????????????????????????????????????????????????
                        reasonFailure.append("??????????????????????????????????????????");
                    }
                    if (null != tenantUserRel) {
                        inParam.put("relId", tenantUserRel.getId());
                        inParam.put("mobilePhone", loginAcct);
                        inParam.put("userId", userDataInfo.getId());
                    }
                }
                inParam.put("loginAcct", loginAcct);
                inParam.put("carUserType", carUserType);
                inParam.put("linkman", linkman);
                UserDataInfo checkReuslt = iUserDataInfoRecordService.isExistIdentification(identification, userDataInfo);
                if (null != checkReuslt) {
                    reasonFailure.append("?????????" + identification.substring(identification.length() - 4) + "????????????????????????" + checkReuslt.getLinkman() + "???" + checkReuslt.getMobilePhone() + "???????????????????????????");
                }
                inParam.put("identification", identification);
                inParam.put("adriverLicenseSn", adriverLicenseSn);
                inParam.put("vehicleType", vehicleType);
                if (null != carUserType) {
                    if (carUserType == SysStaticDataEnum.CAR_USER_TYPE.OWN_CAR || carUserType == SysStaticDataEnum.CAR_USER_TYPE.BUSINESS_CAR) {
                        if (StrUtil.isEmpty(orgName)) {
                            reasonFailure.append("????????????????????????!");
                        }
                        List<SysOrganize> list = iSysOrganizeService.querySysOrganize(loginInfo.getTenantId(), 1);
                        if (null == list || list.size() == 0) {
                            reasonFailure.append("?????????????????????????????????????????????");
                        } else {
                            boolean flag = false;
                            for (SysOrganize sysOrganize : list) {
                                if (sysOrganize.getOrgName().equals(orgName)) {
                                    inParam.put("attachedOrgId", sysOrganize.getId());
                                    flag = true;
                                    break;
                                }
                            }
                            if (!flag) {
                                reasonFailure.append("????????????????????????");
                            }
                        }
                        if (StrUtil.isNotBlank(attachedUserPhone)) {

                            List<String> info = iTenantStaffRelService.queryStaffInfo(attachedUserPhone);
                            if (info != null && info.size() > 0) {
                                inParam.put("attachedUserId", info.get(0));
                            }else {
                                reasonFailure.append("???????????????????????????");
                            }
                        }
                    }

                    if (carUserType == SysStaticDataEnum.CAR_USER_TYPE.OWN_CAR) {
                        if (StrUtil.isNotBlank(lineCodeRules)) {
                            String[] lineCodeRuleList = lineCodeRules.split("\\|");
                            QueryWrapper<CmCustomerLine> lineQueryWrapper = new QueryWrapper<>();
                            lineQueryWrapper.in("line_code_rule", lineCodeRuleList);
                            lineQueryWrapper.eq("state", 1);
                            List<CmCustomerLine> lines = cmCustomerInfoLineSV.list(lineQueryWrapper);
                            if (org.apache.commons.collections.CollectionUtils.isNotEmpty(lines) && lineCodeRuleList.length == lines.size()) {
                                inParam.put("lineRels", lines);
                            } else {
                                Set tmp = new HashSet();
                                for (CmCustomerLine line : lines) {
                                    tmp.add(line.getLineCodeRule());
                                }
                                String lineMsg = "";
                                for (String s1 : lineCodeRuleList) {
                                    if (!tmp.contains(s1)) {
                                        lineMsg += s1 + ",";
                                    }
                                }
                                if (org.apache.commons.lang.StringUtils.isNotEmpty(lineMsg)) {
                                    reasonFailure.append("???????????????" + lineMsg.substring(0, lineMsg.length() - 1) + "?????????");
                                }
                            }
                        }

                        String salaryPattern = getCodeValue("SALARY_PATTERN_STS", salaryPatternString);
                        inParam.put("salaryPattern", salaryPattern);
                        inParam.put("salary", ((Double) (Double.parseDouble(salary) * 100)).longValue());
                        inParam.put("subsidy", ((Double) (Double.parseDouble(subsidy) * 100)).longValue());
                        inParam.put("userSalaryInfoList", new ArrayList());

                    }
                }

                inParam.put("driverLicenseTime", driverLicenseTime);
                inParam.put("driverLicenseExpiredTime", driverLicenseExpiredTime);
                inParam.put("qcCertiTime", qcCertiTime);
                inParam.put("qcCertiExpiredTime", qcCertiExpiredTime);
                DoAddDriverVo doAddDriverVo = new DoAddDriverVo();
                BeanUtil.copyProperties(inParam, doAddDriverVo);
                if (StrUtil.isEmpty(reasonFailure)) {
                    //??????
                    if (null == tenantUserRel) {
                        this.doAddDriver(doAddDriverVo, token);
                    } else {
                        this.doUpdateDriver(doAddDriverVo, token);
                    }
                    //??????????????????
                    success++;
                } else {
                    doAddDriverVo.setReasonFailure(reasonFailure.toString());
                    failureList.add(doAddDriverVo);
                }
            }
            if (org.apache.commons.collections.CollectionUtils.isNotEmpty(failureList)) {
                String[] showName;
                String[] resourceFild;
                showName = new String[]{"????????????", "????????????", "????????????", "????????????"};
                resourceFild = new String[]{"getLoginAcct", "getLinkman", "getCarUserTypeName", "getReasonFailure"};
                XSSFWorkbook workbook = ExportExcel.getWorkbookXlsx(failureList, showName, resourceFild, DoAddDriverVo.class, null);
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                workbook.write(os);
                byte[] b = os.toByteArray();
                InputStream inputStream1 = new ByteArrayInputStream(b);
                //????????????
                FastDFSHelper client = FastDFSHelper.getInstance();
                String failureExcelPath = client.upload(inputStream1, "????????????.xlsx", inputStream1.available());
                os.close();
                is.close();
                records.setFailureUrl(failureExcelPath);
                if (success > 0) {
                    records.setRemarks(success + "?????????" + failureList.size() + "?????????");
                    records.setState(2);
                }
                if (success == 0) {
                    records.setRemarks(failureList.size() + "?????????");
                    records.setState(4);
                }
            } else {
                records.setRemarks(success + "?????????");
                records.setState(3);
            }
            importOrExportRecordsService.update(records);
        } catch (Exception e) {
            records.setRemarks("????????????,????????????????????????????????????????????????");
            records.setFailureReason("????????????,????????????????????????????????????????????????");
            records.setState(5);
            importOrExportRecordsService.update(records);
            e.printStackTrace();
        }
    }

    @Override
    public Page doQueryOBMCarDriver(DoQueryOBMDriversVo doQueryOBMDriversVo, String token) throws Exception {
        Page<DoQueryOBMDriversDto> page = new Page<>(doQueryOBMDriversVo.getPage(), doQueryOBMDriversVo.getPageSize());
        Integer hasVer = -1;
        if (doQueryOBMDriversVo.getHasVer()) {
            hasVer = 1;
        }
        userDataInfoRecordMapper.doQueryOBMCarDriver(page, hasVer, doQueryOBMDriversVo);
        if (page != null && page.getRecords().size() > 0) {
            for (DoQueryOBMDriversDto doQueryOBMDriversDto : page.getRecords()) {
                if (doQueryOBMDriversDto.getAuthUserId() > 0) {
                    UserDataInfo userDataInfo = iUserDataInfoRecordService.getById(doQueryOBMDriversDto.getAuthUserId());
                    if (userDataInfo != null) {
                        doQueryOBMDriversDto.setAuthUserName(userDataInfo.getLinkman());
                    }
                }
                if (doQueryOBMDriversDto.getState() > 0) {
                    if (doQueryOBMDriversDto.getState() > 1) {
                        doQueryOBMDriversDto.setState(2);
                    }
                    doQueryOBMDriversDto.setStateName(isysStaticDataService.getSysStaticData("USER_STATE", String.valueOf(doQueryOBMDriversDto.getState())).getCodeName());
                }
                if (doQueryOBMDriversDto.getTenantId() == null || doQueryOBMDriversDto.getTenantId() <= 0) {
                    doQueryOBMDriversDto.setAttachTenantName("???????????????");
                }
                if (iAccountBankRelService.isUserTypeBindCardAll(doQueryOBMDriversDto.getUserId(), SysStaticDataEnum.USER_TYPE.DRIVER_USER)) {
                    doQueryOBMDriversDto.setBindState(EnumConsts.BIND_STATE.BIND_STATE_ALL);
                    doQueryOBMDriversDto.setBindStateName(isysStaticDataService.getSysStaticData("BIND_STATE", EnumConsts.BIND_STATE.BIND_STATE_ALL + "").getCodeName());
                } else {
                    doQueryOBMDriversDto.setBindState(EnumConsts.BIND_STATE.BIND_STATE_NONE);
                    doQueryOBMDriversDto.setBindStateName(isysStaticDataService.getSysStaticData("BIND_STATE", EnumConsts.BIND_STATE.BIND_STATE_NONE + "").getCodeName());
                }
            }
        }
        return page;
    }


    @Override
    public List<TenantUserRel> getAllTenantUserRels(long userId) {
        QueryWrapper<TenantUserRel> qw = new QueryWrapper<>();
        qw.eq("USER_ID", userId);
        return tenantUserRelMapper.selectList(qw);
    }

    @Override
    public Map isExistMobile(String loginAcct, String accessToken, boolean againFlag) throws Exception {
        Map result = new HashMap();
        Map userInfo = new HashMap();
        LoginInfo user = iUserDataInfoRecordService.getLoginInfoByAccessToken(accessToken);
        UserDataInfo userDataInfo = iUserDataInfoRecordService.getUserDataInfoByMoblile(loginAcct, false, accessToken);
        if (null == userDataInfo) {
            result.put("type", 1); //?????????????????????
            return result;
        }
        userInfo.put("userId", userDataInfo.getId());
        userInfo.put("linkman", userDataInfo.getLinkman());
        userInfo.put("identification", userDataInfo.getIdentification());
        userInfo.put("userPrice", userDataInfo.getUserPrice());//????????????
        result.put("type", 4);
        //???????????????
        if (isDriver(userDataInfo.getId(), null)) {
            TenantUserRel tenantUserRel = iTenantUserRelService.getTenantUserRelByUserIdAndType(userDataInfo.getId(), SysStaticDataEnum.CAR_USER_TYPE.OWN_CAR);
            if (null != tenantUserRel) {
                SysTenantDef sysTenantDef = sysTenantDefService.getSysTenantDef(tenantUserRel.getTenantId(), true);
                if (tenantUserRel.getState() == SysStaticDataEnum.SYS_USER_STATE.AUDIT_NOT
                        && sysTenantDef.getState().equals(SysStaticDataEnum.SYS_STATE_DESC.STATE_YES)) {
                    throw new BusinessException("???????????????????????????????????????????????????????????????");

                }
                if (!againFlag && userDataInfo.getTenantId().longValue() == user.getTenantId().longValue()) {
                    throw new BusinessException("??????????????????????????????????????????????????????????????????");
                }
                if (sysTenantDef != null) {
                    userInfo.put("tenantId", sysTenantDef.getId());//??????id
                    userInfo.put("tenantName", sysTenantDef.getName());//????????????
                    userInfo.put("tenantLinkMan", sysTenantDef.getLinkMan());//??????????????????
                    userInfo.put("linkPhone", sysTenantDef.getLinkPhone());//????????????
                    userInfo.put("tenantState", sysTenantDef.getState());//????????????
                    result.put("type", 3);
                }
            } else {
                result.put("type", 2);
            }
            List<VehicleDataInfo> vehicleDataInfoList = iVehicleDataInfoService.getDriverAllRelVehicleList(userDataInfo.getId());
            if (org.apache.commons.collections.CollectionUtils.isNotEmpty(vehicleDataInfoList)) {
                for (VehicleDataInfo vehicleDataInfo : vehicleDataInfoList) {
                    String vehicleLength = vehicleDataInfo.getVehicleLength();
                    String vehicleStatusName = "";
                    if (null != vehicleDataInfo.getVehicleStatus()) {
                        vehicleStatusName = isysStaticDataService.getSysStaticDatas("VEHICLE_STATUS", vehicleDataInfo.getVehicleStatus());
                    }
                    if (null != vehicleLength) {
                        vehicleStatusName = ((vehicleLength + "???/").equals("0???/") ? "" : vehicleLength + "???/") + vehicleStatusName;
                    }
                    vehicleDataInfo.setVehicleStatusName(vehicleStatusName);
                }
            }
            result.put("vehicleList", vehicleDataInfoList);
        }
        result.put("userInfo", userInfo);
        return result;
    }

    @Override
    public Map isExistMobile(String loginAcct) throws BusinessException {
        Map map = new HashMap();
        Map userInfo = new HashMap();
        String result = "1";
        QueryWrapper<UserDataInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("mobile_Phone", loginAcct);
        List<UserDataInfo> userDataInfoList = iUserDataInfoRecordService.getBaseMapper().selectList(queryWrapper);
        if (userDataInfoList != null && !userDataInfoList.isEmpty()) {
            result = "2";
            if (userDataInfoList.get(0).getUserType() == SysStaticDataEnum.USER_TYPE.DRIVER_USER) {
                throw new BusinessException("???????????????????????????????????????????????????");
            }
            userInfo.put("userId", userDataInfoList.get(0).getId());
            userInfo.put("linkman", userDataInfoList.get(0).getLinkman());
            userInfo.put("identification", userDataInfoList.get(0).getIdentification());
        }
        map.put("type", result);
        map.put("userInfo", userInfo);
        return map;
    }

    @Override
    public String checkExistIdentification(String identification, Long userId) throws BusinessException {
        UserDataInfo userDataInfo = iUserDataInfoRecordService.getById(userId);
        UserDataInfo checkReuslt = iUserDataInfoRecordService.isExistIdentification(identification, userDataInfo);
        if (null != checkReuslt) {
            return "?????????" + identification.substring(identification.length() - 4) + "????????????????????????" + checkReuslt.getLinkman() + "???" + checkReuslt.getMobilePhone() + "???????????????????????????";
        }
        return null;
    }


    @Override
    @Transactional
    public void doAddDriver(DoAddDriverVo doAddDriverVo, String accessToken) throws BusinessException {
        LoginInfo user = iUserDataInfoRecordService.getLoginInfoByAccessToken(accessToken);
        if (doAddDriverVo.getCarUserType().equals("-1")) {
            user.setTenantId(1L);
        }
        SysUser sysUser = iUserDataInfoRecordService.getSysUserByAccessToken(accessToken);
        boolean firstAdd = false;
//        boolean ocrFlag =doAddDriverVo.getOcrFlag();
        String mobilePhone = doAddDriverVo.getLoginAcct();
        UserDataInfo userDataInfo = iUserDataInfoRecordService.getDriver(mobilePhone);
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        SysUser sysOperator;
        String carUserType = doAddDriverVo.getCarUserType();
        if (null == userDataInfo) {//??????
            userDataInfo = new UserDataInfo();
            userDataInfo.setMobilePhone(doAddDriverVo.getLoginAcct());
            userDataInfo.setLinkman(doAddDriverVo.getLinkman());//??????
            userDataInfo.setIdentification(doAddDriverVo.getIdentification());//?????????
            userDataInfo.setUserPrice(doAddDriverVo.getUserPrice());//????????????id
            userDataInfo.setUserPriceUrl(doAddDriverVo.getUserPriceUrl());//??????????????????
            userDataInfo.setIdenPictureFront(doAddDriverVo.getIdenPictureFront());//?????????????????????id
            userDataInfo.setIdenPictureFrontUrl(doAddDriverVo.getIdenPictureFrontUrl());//???????????????????????????
            userDataInfo.setAdriverLicenseSn(doAddDriverVo.getAdriverLicenseSn());//????????????
            //???????????????
            userDataInfo.setIdenPictureBack(doAddDriverVo.getIdenPictureBack());
            userDataInfo.setIdenPictureBackUrl(doAddDriverVo.getIdenPictureBackUrl());

            //???????????????
            userDataInfo.setAdriverLicenseOriginal(doAddDriverVo.getAdriverLicenseOriginal());
            userDataInfo.setAdriverLicenseOriginalUrl(doAddDriverVo.getAdriverLicenseOriginalUrl());

            //???????????????
            userDataInfo.setAdriverLicenseDuplicate(doAddDriverVo.getAdriverLicenseDuplicate());
            userDataInfo.setAdriverLicenseDuplicateUrl(doAddDriverVo.getAdriverLicenseDuplicateUrl());

            //???????????????
            if (!StringUtils.isEmpty(doAddDriverVo.getDriverLicenseTime())) {
                userDataInfo.setDriverLicenseTime(LocalDateTime.parse(doAddDriverVo.getDriverLicenseTime() + " 00:00:00", df));
            }
            if (!StringUtils.isEmpty(doAddDriverVo.getDriverLicenseExpiredTime())) {
                userDataInfo.setDriverLicenseExpiredTime(LocalDateTime.parse(doAddDriverVo.getDriverLicenseExpiredTime() + " 00:00:00", df));
            }
            //?????????????????????
            userDataInfo.setQcCerti(doAddDriverVo.getQcCerti());
            userDataInfo.setQcCertiUrl(doAddDriverVo.getQcCertiUrl());
            //?????????????????????
            if (!StringUtils.isEmpty(doAddDriverVo.getQcCertiTime())) {
                userDataInfo.setQcCertiTime(LocalDateTime.parse(doAddDriverVo.getQcCertiTime() + " 00:00:00", df));
            }
            if (!StringUtils.isEmpty(doAddDriverVo.getQcCertiExpiredTime())) {
                userDataInfo.setQcCertiExpiredTime(LocalDateTime.parse(doAddDriverVo.getQcCertiExpiredTime() + " 00:00:00", df));
            }

            //??????????????????
            userDataInfo.setLugeAgreement(doAddDriverVo.getLugeAgreement());
            userDataInfo.setLugeAgreementUrl(doAddDriverVo.getLugeAgreementUrl());


            //????????????
            userDataInfo.setAttachedOrgId(doAddDriverVo.getAttachedOrgId());
            userDataInfo.setAttachedUserId(doAddDriverVo.getAttachedUserId());

            //????????????
            userDataInfo.setVehicleType(doAddDriverVo.getVehicleType());


            userDataInfo.setUserType(SysStaticDataEnum.USER_TYPE.DRIVER_USER);
            userDataInfo.setSourceFlag(SysStaticDataEnum.SOURCE_FLAG.PLATFORM);

            userDataInfo.setCreateTime(LocalDateTime.now());

            iUserDataInfoRecordService.save(userDataInfo);
            afterCreateUserDataInfo(userDataInfo, carUserType, user.getTenantId());

//            UpdateWrapper<UserDataInfo> updateWrapper = new UpdateWrapper();
//            updateWrapper.eq("IDENTIFICATION",userDataInfo.getIdentification()).
//                          eq("LINKMAN",userDataInfo.getLinkman());

            iUserDataInfoRecordService.update(userDataInfo);

            sysOperator = initSysOperator(userDataInfo.getMobilePhone(), userDataInfo.getLinkman(), user);
            sysOperator.setUserInfoId(userDataInfo.getId());

            iSysUserService.save(sysOperator);
            firstAdd = true;
        } else {

            userDataInfo.setMobilePhone(doAddDriverVo.getLoginAcct());
            userDataInfo.setLinkman(doAddDriverVo.getLinkman());//??????
            userDataInfo.setIdentification(doAddDriverVo.getIdentification());//?????????
            userDataInfo.setUserPrice(doAddDriverVo.getUserPrice());//????????????id
            userDataInfo.setUserPriceUrl(doAddDriverVo.getUserPriceUrl());//??????????????????
            userDataInfo.setIdenPictureFront(doAddDriverVo.getIdenPictureFront());//?????????????????????id
            userDataInfo.setIdenPictureFrontUrl(doAddDriverVo.getIdenPictureFrontUrl());//???????????????????????????
            userDataInfo.setAdriverLicenseSn(doAddDriverVo.getAdriverLicenseSn());//????????????
            //???????????????
            userDataInfo.setIdenPictureBack(doAddDriverVo.getIdenPictureBack());
            userDataInfo.setIdenPictureBackUrl(doAddDriverVo.getIdenPictureBackUrl());

            //???????????????
            userDataInfo.setAdriverLicenseOriginal(doAddDriverVo.getAdriverLicenseOriginal());
            userDataInfo.setAdriverLicenseOriginalUrl(doAddDriverVo.getAdriverLicenseOriginalUrl());

            //???????????????
            userDataInfo.setAdriverLicenseDuplicate(doAddDriverVo.getAdriverLicenseDuplicate());
            userDataInfo.setAdriverLicenseDuplicateUrl(doAddDriverVo.getAdriverLicenseDuplicateUrl());

            //???????????????
            if (!StringUtils.isEmpty(doAddDriverVo.getDriverLicenseTime())) {
                userDataInfo.setDriverLicenseTime(LocalDateTime.parse(doAddDriverVo.getDriverLicenseTime() + " 00:00:00", df));
            }
            if (!StringUtils.isEmpty(doAddDriverVo.getDriverLicenseExpiredTime())) {
                userDataInfo.setDriverLicenseExpiredTime(LocalDateTime.parse(doAddDriverVo.getDriverLicenseExpiredTime() + " 00:00:00", df));
            }
            //?????????????????????
            userDataInfo.setQcCerti(doAddDriverVo.getQcCerti());
            userDataInfo.setQcCertiUrl(doAddDriverVo.getQcCertiUrl());
            //?????????????????????
            if (!StrUtil.isEmpty(doAddDriverVo.getQcCertiTime())) {
                userDataInfo.setQcCertiTime(LocalDateTime.parse(doAddDriverVo.getQcCertiTime() + " 00:00:00", df));
            }
            if (!StrUtil.isEmpty(doAddDriverVo.getQcCertiExpiredTime())) {
                userDataInfo.setQcCertiExpiredTime(LocalDateTime.parse(doAddDriverVo.getQcCertiExpiredTime() + " 00:00:00", df));
            }

            //??????????????????
            userDataInfo.setLugeAgreement(doAddDriverVo.getLugeAgreement());
            userDataInfo.setLugeAgreementUrl(doAddDriverVo.getLugeAgreementUrl());


            //????????????
            userDataInfo.setAttachedOrgId(doAddDriverVo.getAttachedOrgId());
            userDataInfo.setAttachedUserId(doAddDriverVo.getAttachedUserId());

            //????????????
            userDataInfo.setVehicleType(doAddDriverVo.getVehicleType());

            userDataInfo.setUserType(SysStaticDataEnum.USER_TYPE.DRIVER_USER);
            afterCreateUserDataInfo(userDataInfo, carUserType, user.getTenantId());
            iUserDataInfoRecordService.updateById(userDataInfo);
            sysOperator = iSysUserService.getSysUserByUserId(userDataInfo.getId());
            sysOperator.setLockFlag(SysStaticDataEnum.LOCK_FLAG.LOCK_YES);
            sysOperator.setName(userDataInfo.getLinkman());
            List<TenantStaffRel> list = iTenantStaffRelService.getStaffRel(userDataInfo.getId());
            if (list != null && list.size() > 0) {
                for (TenantStaffRel rel : list) {
                    rel.setStaffName(userDataInfo.getLinkman());
                }
            }
            SysTenantDef tenantDef = sysTenantDefService.getSysTenantDefByAdminUserId(userDataInfo.getId());
            if (null != tenantDef) {
                tenantDef.setLinkMan(userDataInfo.getLinkman());
                sysTenantDefService.updateById(tenantDef);
            }
        }
        //???????????????????????????????????????????????????????????????
        if (SysStaticDataEnum.PT_TENANT_ID == user.getTenantId()) {
            saveSysOperLog(SysOperLogConst.BusiCode.DriverOBMS, SysOperLogConst.OperType.Add, "????????????", accessToken, userDataInfo.getId(), null);
        }
        UserDataInfoVer userDataInfoVer = new UserDataInfoVer();
        BeanUtil.copyProperties(userDataInfo, userDataInfoVer);
        userDataInfoVer.setUserId(userDataInfo.getId());
        iUserDataInfoVerService.save(userDataInfoVer);
        afterCreateUserDataInfoVer(userDataInfoVer, carUserType, accessToken);
        iUserDataInfoVerService.updateById(userDataInfoVer);
        //????????????C???????????????????????????
        if (SysStaticDataEnum.PT_TENANT_ID != user.getTenantId()) {
            TenantUserRel tenantUserRel = new TenantUserRel();//??????????????????
            if (doAddDriverVo.getCarUserType().equals("-1")) {
                tenantUserRel.setCarUserType(1);
            } else {
                tenantUserRel.setCarUserType(Integer.valueOf(doAddDriverVo.getCarUserType()));
            }
            tenantUserRel.setAttachedOrgId(doAddDriverVo.getAttachedOrgId());
            tenantUserRel.setAttachedUserId(doAddDriverVo.getAttachedUserId());
            tenantUserRel.setTenantId(user.getTenantId());
            tenantUserRel.setHasVer(EnumConsts.HAS_VER_STATE.HAS_VER_NO);
            tenantUserRel.setState(SysStaticDataEnum.SYS_USER_STATE.AUDIT_NOT);
            tenantUserRel.setOrgId(sysUser.getOrgId());
            tenantUserRel.setUserId(userDataInfo.getId());
            iTenantUserRelService.save(tenantUserRel);
            afterCreateTenantUserRel(tenantUserRel, carUserType);
            iTenantUserRelService.updateById(tenantUserRel);
            TenantUserRelVer tenantUserRelVer = new TenantUserRelVer();
            BeanUtil.copyProperties(tenantUserRel, tenantUserRelVer);
            tenantUserRelVer.setRelId(tenantUserRel.getId());
            tenantUserRelVer.setVerState(SysStaticDataEnum.VER_STATE.VER_STATE_Y);
            iTenantUserRelVerService.save(tenantUserRelVer);
            afterCreateTenantUserRelVer(tenantUserRelVer, carUserType);
            iTenantUserRelVerService.updateById(tenantUserRelVer);
            //?????? ???????????????
            completeCreateTenantUserRel(userDataInfo, tenantUserRel, doAddDriverVo, carUserType, accessToken);
            saveSysOperLog(SysOperLogConst.BusiCode.Driver, SysOperLogConst.OperType.Add, "????????????", accessToken, tenantUserRel.getId(), null);
            //????????????
            startAuditProcessForAdd(tenantUserRel, accessToken);
        }
        //??????????????????C??????????????????????????????
        if (firstAdd && SysStaticDataEnum.PT_TENANT_ID != user.getTenantId()) {
            SysTenantDef sysTenantDef = sysTenantDefService.getSysTenantDef(user.getTenantId());
            long templateId = getSmsTemplateId(carUserType);
            try {
                Map<String, Object> paramMap = new HashMap<>();
                paramMap.put("carDriverName", userDataInfo.getLinkman());
                paramMap.put("tenantName", sysTenantDef.getShortName());
                SysSmsSend sysSmsSend = new SysSmsSend();
                //??????id
                sysSmsSend.setTemplateId(templateId);
                //?????????
                sysSmsSend.setBillId(userDataInfo.getMobilePhone());
                //????????????
                sysSmsSend.setObjType(String.valueOf(SysStaticDataEnum.OBJ_TYPE.NOTIFY));
                //???????????????
                sysSmsSend.setParamMap(paramMap);
                sysSmsSend.setSmsType(SysStaticDataEnum.SMS_TYPE.EVENT_ANNOUNCEMENTS);
                iSysSmsSendService.sendSms(sysSmsSend);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void doAddDriverForOBMS(DoAddOBMDriver doAddOBMDriver, String token) throws BusinessException {
        if (doAddOBMDriver != null) {
            DoAddDriverVo doAddDriverVo = new DoAddDriverVo();
            BeanUtil.copyProperties(doAddOBMDriver, doAddDriverVo);
            doAddDriverVo.setCarUserType("-1");

            this.doAddDriver(doAddDriverVo, token);
            return;
        }

    }

    @Override
    public String quick(QuickAddDriverVo quickAddDriverVo, String accessToken) throws Exception {
        String message = null;
        LoginInfo user = iUserDataInfoRecordService.getLoginInfoByAccessToken(accessToken);
        if (user == null) {
            return "????????????";
        }
        //????????????
        UserDataInfo userDataInfo = iUserDataInfoRecordService.getDriver(quickAddDriverVo.getLoginAcct());
        if (userDataInfo != null && isDriver(userDataInfo.getId(), null)) {
            message = checkQuickApplyDriver(userDataInfo.getId(), user.getTenantId());
            if (message != null) {
                return message;
            }
        }
        //????????????
        VehicleDataInfo vehicleDataInfo = iVehicleDataInfoService.getVehicleDataInfo(quickAddDriverVo.getPlateNumber());
        if (null != vehicleDataInfo) {
            message = checkQuickApplyVehicle(vehicleDataInfo.getId(), user.getTenantId());
            if (message != null) {
                return message;
            }
        }
        if (userDataInfo != null && isDriver(userDataInfo.getId(), null)) {
            //??????????????????
            quickAppyDriver(userDataInfo.getId(), null == userDataInfo.getTenantId() ? -1L : userDataInfo.getTenantId(), quickAddDriverVo.getPlateNumber(), accessToken);
            //??????????????????
            TenantUserRel tenantUserRel = quickCreateTenantUserRel(userDataInfo.getId(), user.getTenantId());
//          iSysOperLogService.saveSysOperLog(SysOperLogConst.BusiCode.Driver, tenantUserRel.getId(), SysOperLogConst.OperType.Add, "????????????????????????");
            saveSysOperLog(SysOperLogConst.BusiCode.Driver, SysOperLogConst.OperType.Add, "????????????????????????", accessToken, tenantUserRel.getId(), null);
        } else {
            //??????????????????
            if (null != userDataInfo) {
                //??????????????????????????????????????????
                message = quickChangeToDriver(userDataInfo);
                if (message != null) {
                    return message;
                }
            } else {
                //???????????????????????????????????????
                userDataInfo = quickCreateUser(quickAddDriverVo.getLoginAcct(), quickAddDriverVo.getLinkman(), accessToken);
                iSysUserService.insertSysUser(quickAddDriverVo.getLoginAcct(), userDataInfo.getId(),
                        userDataInfo.getLinkman(), 1, null, -1L,
                        SysStaticDataEnum.VERIFY_STS.UNAUTHORIZED, null, accessToken);
            }
            //??????
            quickAddDriver(userDataInfo, quickAddDriverVo.getQrCodeUrl(), accessToken);
        }
        if (null != vehicleDataInfo) {
            //??????????????????
            VehicleDataInfoVer vehicleDataInfoVer = iVehicleDataInfoVerService.getVehicleDataInfoVer(vehicleDataInfo.getPlateNumber(), null);
            if (null == vehicleDataInfoVer) {
                LOGGER.error("???????????????plateNumber=" + vehicleDataInfo.getPlateNumber() + "??? vehicleCode = " + vehicleDataInfo.getId() + " ?????????ver??????");
                return "???????????????plateNumber=" + vehicleDataInfo.getPlateNumber() + "??? vehicleCode = " + vehicleDataInfo.getId() + " ?????????ver??????";
            }
            quickApplyVehicle(vehicleDataInfo.getId(), null == userDataInfo.getTenantId() ? -1L : userDataInfo.getTenantId(), vehicleDataInfo.getPlateNumber(), vehicleDataInfoVer.getId(), accessToken);
            //??????????????????
            TenantVehicleRel tenantVehicleRel = quicCreateTenantVehicleRel(vehicleDataInfo.getId(), userDataInfo.getId(), vehicleDataInfo.getPlateNumber(), user.getTenantId(), vehicleDataInfoVer.getId());
            //????????????id
            vehicleDataInfo.setDriverUserId(userDataInfo.getId());
            iVehicleDataInfoService.update(vehicleDataInfo);
//            iSysOperLogService.saveSysOperLog(SysOperLogConst.BusiCode.Vehicle, tenantVehicleRel.getId(), SysOperLogConst.OperType.Add, "?????????????????????");
            saveSysOperLog(SysOperLogConst.BusiCode.Vehicle, SysOperLogConst.OperType.Add, "?????????????????????", accessToken, tenantVehicleRel.getId(), null);
        } else {
            message = iVehicleDataInfoService.doSaveQuickVehicle(user.getTenantId(), quickAddDriverVo.getPlateNumber(), userDataInfo.getId(),
                    quickAddDriverVo.getVehicleStatus(), quickAddDriverVo.getVehicleLength(), quickAddDriverVo.getLicenceType());
            if (message != null) {
                return message;
            }
        }
        return message;
    }

    @Resource
    private ReadisUtil readisUtil;

    @Override
    public Page doQueryVehicleForQuick(String plateNumber) throws Exception {
        Page<VehicleDataInfo> page = new Page<>(1, 10);
        QueryWrapper<VehicleDataInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.like("plate_Number", plateNumber);
        Page<VehicleDataInfo> vehicleDataInfoPage = iVehicleDataInfoService.getBaseMapper().selectPage(page, queryWrapper);
        List<VehicleDataInfo> vehicleDataInfoList = vehicleDataInfoPage.getRecords();
        if (vehicleDataInfoList != null && vehicleDataInfoList.size() > 0) {
            for (VehicleDataInfo vehicleDataInfo : vehicleDataInfoList) {
                if (null != vehicleDataInfo.getVehicleStatus()) {
                    vehicleDataInfo.setVehicleStatusName(SysStaticDataRedisUtils.getSysStaticDataById2String(redisUtil, "VEHICLE_STATUS", vehicleDataInfo.getVehicleStatus()));
                }


                if (vehicleDataInfo.getVehicleStatus() != null && vehicleDataInfo.getVehicleStatus() == SysStaticDataEnum.VEHICLE_STATUS_ENUM.VEHICLE_CAR_BOX.getType()) {
                    SysStaticData vehicleStatusSubtype = readisUtil.getSysStaticData("VEHICLE_STATUS_SUBTYPE", vehicleDataInfo.getVehicleLength() + "");
                    if (vehicleStatusSubtype != null) {
                        vehicleDataInfo.setVehicleLengthName(vehicleStatusSubtype.getCodeName());
                    }
                } else {
                    vehicleDataInfo.setVehicleLengthName(SysStaticDataRedisUtils.getSysStaticDataById2String(redisUtil, "VEHICLE_LENGTH", vehicleDataInfo.getVehicleLength() != null && !vehicleDataInfo.getVehicleLength().equals("") ? vehicleDataInfo.getVehicleLength() : "0"));

                }
                TenantVehicleRel tenantVehicleRel = iTenantVehicleRelService.getZYVehicleByVehicleCode(vehicleDataInfo.getId());
                if (null == tenantVehicleRel) {
                    vehicleDataInfo.setAuditContent("?????????");
                    vehicleDataInfo.setTenantName("?????????");
                } else {
                    SysTenantDef sysTenantDef = sysTenantDefService.getById(tenantVehicleRel.getTenantId());
                    if (sysTenantDef != null) {
                        vehicleDataInfo.setAuditContent(sysTenantDef.getName());
                        vehicleDataInfo.setTenantName(sysTenantDef.getName());
                    }
                }
            }
            vehicleDataInfoPage.setRecords(vehicleDataInfoList);
        }

        return vehicleDataInfoPage;
    }

    @Override
    public Page doQueryModernVehicleForQuick(String plateNumber, String accessToken) throws Exception {
        LoginInfo user = iUserDataInfoRecordService.getLoginInfoByAccessToken(accessToken);
        Page<VehicleDataInfo> page = new Page<>(1, 10);
        QueryWrapper<VehicleDataInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.like("plate_Number", plateNumber);
        queryWrapper.eq("tenant_id", user.getTenantId());
        Page<VehicleDataInfo> vehicleDataInfoPage = iVehicleDataInfoService.getBaseMapper().selectPage(page, queryWrapper);
        List<VehicleDataInfo> vehicleDataInfoList = vehicleDataInfoPage.getRecords();
        if (vehicleDataInfoList != null && vehicleDataInfoList.size() > 0) {
            for (VehicleDataInfo vehicleDataInfo : vehicleDataInfoList) {
                vehicleDataInfo.setVehicleLengthName(isysStaticDataService.getSysStaticDataId("VEHICLE_LENGTH", vehicleDataInfo.getVehicleLength() == null ? "0" : vehicleDataInfo.getVehicleLength()));
                vehicleDataInfo.setVehicleStatusName(isysStaticDataService.getSysStaticDataId("VEHICLE_STATUS", vehicleDataInfo.getVehicleStatus() == null ? "0" : vehicleDataInfo.getVehicleStatus() + ""));
                TenantVehicleRel tenantVehicleRel = iTenantVehicleRelService.getZYVehicleByVehicleCode(vehicleDataInfo.getId());
                if (null == tenantVehicleRel) {
                    vehicleDataInfo.setAuditContent("?????????");
                } else {
                    SysTenantDef sysTenantDef = sysTenantDefService.getById(tenantVehicleRel.getTenantId());
                    if (sysTenantDef != null) {
                        vehicleDataInfo.setAuditContent(sysTenantDef.getShortName());
                    }
                }
            }
            vehicleDataInfoPage.setRecords(vehicleDataInfoList);
        }

        return vehicleDataInfoPage;
    }

    @Override
    public Map doGetDriverForOBMS(long userId, String accessToken) throws Exception {
        Map resultMap = new HashMap();
        UserDataInfo userDataInfo = iUserDataInfoRecordService.getUserDataInfo(userId);
        UserDataInfoVo out = new UserDataInfoVo();//????????????
        UserDataInfoVo ver = new UserDataInfoVo();//????????????????????????????????????????????????????????????
        //??????????????????
        BeanUtil.copyProperties(userDataInfo, out);
        out.setUserId(userDataInfo.getId());
        //??????????????????
        //????????????????????????????????????
        long sourceTenantId = -1L;
        if (userDataInfo.getTenantId() != null && userDataInfo.getTenantId() != -1 && userDataInfo.getTenantId() != SysStaticDataEnum.PT_TENANT_ID) {
            sourceTenantId = userDataInfo.getTenantId();
        }
        out.setSourceTenantId(sourceTenantId);
        if (sourceTenantId != -1L) {
            SysTenantDef sysTenantDef = sysTenantDefService.getSysTenantDef(sourceTenantId);
            if (sysTenantDef != null) {
                out.setTenantName(sysTenantDef.getName());
                if (sysTenantDef.getAdminUser() != null) {
                    out.setAdminUserName(iUserDataInfoRecordService.getUserName(sysTenantDef.getAdminUser()));
                }
                out.setLinkPhone(sysTenantDef.getLinkPhone());
                if (sysTenantDef.getAdminUser() != null) {
                    List<AccountBankRel> tenantBankInfo = iAccountBankRelService.queryAccountBankRel(sysTenantDef.getAdminUser(), SysStaticDataEnum.USER_TYPE.ADMIN_USER, null);
                    for (AccountBankRel accountBankRel : tenantBankInfo) {
                        if (accountBankRel.getBankType() == EnumConsts.ACCOUNT_BANK_REL.BANK_TYPE_0 && accountBankRel.getIsDefaultAcct() == 1) {
                            out.setPrivateAcctNo(accountBankRel.getAcctNo());
                        } else if (accountBankRel.getBankType() == EnumConsts.ACCOUNT_BANK_REL.BANK_TYPE_1 && accountBankRel.getIsDefaultAcct() == 1) {
                            out.setCorporateAcctNo(accountBankRel.getAcctNo());
                        }
                    }
                }
            }
        }
        //??????????????????
        //??????????????????
        List<Map> vehicleList = new ArrayList<>();
        QueryWrapper<VehicleDataInfo> vehicleQueryWrapper = new QueryWrapper<>();
        vehicleQueryWrapper.eq("driver_User_Id", userId);
        List<VehicleDataInfo> vehicle = iVehicleDataInfoService.getBaseMapper().selectList(vehicleQueryWrapper);
        if (vehicle != null && !vehicle.isEmpty()) {
            for (VehicleDataInfo vehicleDataInfo : vehicle) {
                QueryWrapper<TenantVehicleRel> tenantVehicleRelQueryWrapper = new QueryWrapper<>();
                tenantVehicleRelQueryWrapper.eq("vehicle_Code", vehicleDataInfo.getId()).eq("tenant_Id", sourceTenantId);
                TenantVehicleRel tenantVehicleRel = iTenantVehicleRelService.getBaseMapper().selectOne(tenantVehicleRelQueryWrapper);
                if (tenantVehicleRel == null) {
                    continue;
                }
                TenantVehicleRelVer tenantVehicleRelVer = iTenantVehicleRelVerService.getTenantVehicleRelVer(tenantVehicleRel.getVehicleCode());
                Map vehicleMap = new HashMap();
                vehicleMap.put("plateNumber", vehicleDataInfo.getPlateNumber());
                vehicleMap.put("vehicleState", tenantVehicleRel.getState());
                vehicleMap.put("vehicleClass", String.valueOf(tenantVehicleRel.getVehicleClass()));
                vehicleMap.put("vehicleClassName", isysStaticDataService.getSysStaticData("VEHICLE_CLASS", String.valueOf(tenantVehicleRel.getVehicleClass())).getCodeName());
                vehicleMap.put("licenceType", String.valueOf(vehicleDataInfo.getLicenceType()));
                vehicleMap.put("licenceTypeName", isysStaticDataService.getSysStaticData("LICENCE_TYPE", String.valueOf(vehicleDataInfo.getLicenceType())).getCodeName());
                vehicleMap.put("vehicleLength", vehicleDataInfo.getVehicleLength());
                vehicleMap.put("vehicleLengthName", isysStaticDataService.getSysStaticDataId("VEHICLE_LENGTH", vehicleDataInfo.getVehicleLength() == null ? "0" : vehicleDataInfo.getVehicleLength()));
                vehicleMap.put("vehicleStatus", String.valueOf(vehicleDataInfo.getVehicleStatus()));
                vehicleMap.put("vehicleStatusName", isysStaticDataService.getSysStaticDataId("VEHICLE_STATUS", vehicleDataInfo.getVehicleStatus().toString()));
                vehicleMap.put("authState", tenantVehicleRel.getAuthState());
//                vehicleMap.put("billReceiverUserId", tenantVehicleRel.getBillReceiverUserId());
//                vehicleMap.put("billReceiverMobile", tenantVehicleRel.getBillReceiverMobile());
//                vehicleMap.put("billReceiverName", tenantVehicleRel.getBillReceiverName());
                //?????????????????????????????????
//                vehicleMap.put("canModify", null == vehicleDataInfo.getTenantId() || vehicleDataInfo.getTenantId() <= 0);
//                UserDataInfo u = iUserDataInfoRecordService.getUserDataInfoByMoblile(tenantVehicleRel.getBillReceiverMobile(), false,accessToken);
//                vehicleMap.put("canModifyName",  null == u);
                vehicleList.add(vehicleMap);
            }
            out.setVehicleList(vehicleList);
        }
        resultMap.put("user", out);
        //??????????????????
        if (userDataInfo.getHasVer() != null && userDataInfo.getHasVer().intValue() == EnumConsts.HAS_VER_STATE.HAS_VER_NO) {
            //???????????????
            UserDataInfoVer userDataInfoVer = iUserDataInfoVerService.getUserDataInfoVerNoTenant(userId);
            if (userDataInfoVer != null) {
                CommonUtils.copyProperties(ver, userDataInfoVer, out);
                userDataInfoVer.setUserId(out.getUserId());
            }
        }
        resultMap.put("ver", ver);
        return resultMap;
    }

    @Override
    public Map doGetDriver(long userId, String accessToken) throws Exception {
        Map resultMap = new HashMap();
        LoginInfo userDataInfoByAccessToken = iUserDataInfoRecordService.getLoginInfoByAccessToken(accessToken);
        UserDataInfo user = iUserDataInfoRecordService.getById(userId);
        QueryWrapper<TenantUserRel> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId).eq("tenant_Id", userDataInfoByAccessToken.getTenantId());
        TenantUserRel tenantUserRel = iTenantUserRelService.getBaseMapper().selectOne(queryWrapper);
        //????????????????????????
        if (user == null || tenantUserRel == null) {
            return this.doGetDriverForOBMS(userId, accessToken);
        }
        UserDataInfoVo out = new UserDataInfoVo();//????????????
        UserDataInfoVo ver = new UserDataInfoVo();//????????????????????????????????????????????????????????????
        //??????????????????
        BeanUtil.copyProperties(user, out);
        BeanUtil.copyProperties(tenantUserRel, out);
        out.setUserId(user.getId());
        out.setRelId(tenantUserRel.getId());
        if (tenantUserRel.getCarUserType() == SysStaticDataEnum.CAR_USER_TYPE.OWN_CAR) {
            //???????????????????????????
            TenantUserSalaryRel salary = iTenantUserSalaryRelService.getTenantUserRalaryRelByRelId(tenantUserRel.getId());
            if (salary != null && salary.getSalaryPattern() != null) {
                if (salary.getSalaryPattern() == SysStaticDataEnum.SALARY_PATTERN.MILEAGE
                        || salary.getSalaryPattern() == SysStaticDataEnum.SALARY_PATTERN.TIMES) {
                    List<UserSalaryInfo> salaryInfos = iUserSalaryInfoService.queryUserSalaryInfos(userId, salary.getSalaryPattern());
                    out.setUserSalaryInfoList(salaryInfos);
                }
                //??????????????????
                BeanUtil.copyProperties(salary, out);
            }
        }
        //??????????????????
        //????????????????????????????????????
        long sourceTenantId = -1L;
        resultMap.put("ownFlg", false);
        List<TenantUserRel> tenantUserRelOwn = iTenantUserRelService.getTenantUserRel(userId, sourceTenantId);
        if (tenantUserRelOwn != null && !tenantUserRelOwn.isEmpty()) {
            if (tenantUserRelOwn.get(0).getTenantId().longValue() != tenantUserRel.getTenantId().longValue()) {
                sourceTenantId = tenantUserRelOwn.get(0).getTenantId().longValue();
                resultMap.put("ownFlg", true);
            }
        }
        out.setSourceTenantId(sourceTenantId);
        //userDataInfo.getTenantId()  ????????????
        if (sourceTenantId != -1L) {
            SysTenantDef sysTenantDef = sysTenantDefService.getSysTenantDef(sourceTenantId, true);
            out.setTenantName(sysTenantDef.getName());
            if (sysTenantDef.getAdminUser() != null) {
                out.setAdminUserName(iUserDataInfoRecordService.getUserName(sysTenantDef.getAdminUser()));
            }
            out.setLinkPhone(sysTenantDef.getLinkPhone());

            if (sysTenantDef.getAdminUser() != null) {
                List<AccountBankRel> tenantBankInfo = iAccountBankRelService.queryAccountBankRel(sysTenantDef.getAdminUser(), SysStaticDataEnum.USER_TYPE.ADMIN_USER, null);
                for (AccountBankRel accountBankRel : tenantBankInfo) {
                    if (accountBankRel.getBankType() == EnumConsts.ACCOUNT_BANK_REL.BANK_TYPE_0 && accountBankRel.getIsDefaultAcct() == 1) {
                        out.setPrivateAcctNo(accountBankRel.getAcctNo());
                    } else if (accountBankRel.getBankType() == EnumConsts.ACCOUNT_BANK_REL.BANK_TYPE_1 && accountBankRel.getIsDefaultAcct() == 1) {
                        out.setCorporateAcctNo(accountBankRel.getAcctNo());
                    }
                }
            }
        }
        //??????????????????
        List<Map> vehicleList = new ArrayList<>();
        QueryWrapper<VehicleDataInfo> vehicleQueryWrapper = new QueryWrapper<>();
        vehicleQueryWrapper.eq("driver_User_Id", userId);
        List<VehicleDataInfo> vehicle = iVehicleDataInfoService.getBaseMapper().selectList(vehicleQueryWrapper);
        if (vehicle != null && !vehicle.isEmpty()) {
            for (VehicleDataInfo vehicleDataInfo : vehicle) {
                QueryWrapper<TenantVehicleRel> tenantVehicleRelQueryWrapper = new QueryWrapper<>();
                tenantVehicleRelQueryWrapper.eq("vehicle_Code", vehicleDataInfo.getId()).eq("tenant_Id", userDataInfoByAccessToken.getTenantId());
                TenantVehicleRel tenantVehicleRel = iTenantVehicleRelService.getBaseMapper().selectOne(tenantVehicleRelQueryWrapper);
                if (tenantVehicleRel == null) {
                    continue;
                }
                TenantVehicleRelVer tenantVehicleRelVer = iTenantVehicleRelVerService.getTenantVehicleRelVer(tenantVehicleRel.getVehicleCode());
                Map vehicleMap = new HashMap();
                vehicleMap.put("plateNumber", vehicleDataInfo.getPlateNumber());
                vehicleMap.put("vehicleState", tenantVehicleRel.getState());
                vehicleMap.put("vehicleClass", String.valueOf(tenantVehicleRel.getVehicleClass()));
                vehicleMap.put("vehicleClassName", isysStaticDataService.getSysStaticData("VEHICLE_CLASS", String.valueOf(tenantVehicleRel.getVehicleClass())).getCodeName());
                vehicleMap.put("licenceType", String.valueOf(vehicleDataInfo.getLicenceType()));
                vehicleMap.put("licenceTypeName", isysStaticDataService.getSysStaticData("LICENCE_TYPE", String.valueOf(vehicleDataInfo.getLicenceType())).getCodeName());
                vehicleMap.put("vehicleLength", vehicleDataInfo.getVehicleLength());
                vehicleMap.put("vehicleLengthName", isysStaticDataService.getSysStaticDataId("VEHICLE_LENGTH", vehicleDataInfo.getVehicleLength() == null ? "0" : vehicleDataInfo.getVehicleLength()));
                vehicleMap.put("vehicleStatus", String.valueOf(vehicleDataInfo.getVehicleStatus()));
                vehicleMap.put("vehicleStatusName", isysStaticDataService.getSysStaticDataId("VEHICLE_STATUS", vehicleDataInfo.getVehicleStatus() == null ? "0" : vehicleDataInfo.getVehicleStatus().toString()));
                vehicleMap.put("authState", tenantVehicleRel.getAuthState());
                vehicleMap.put("billReceiverUserId", tenantVehicleRel.getBillReceiverUserId());
                vehicleMap.put("billReceiverMobile", tenantVehicleRel.getBillReceiverMobile());
                vehicleMap.put("billReceiverName", tenantVehicleRel.getBillReceiverName());
                //?????????????????????????????????
                vehicleMap.put("canModify", null == vehicleDataInfo.getTenantId() || vehicleDataInfo.getTenantId() <= 0);
                UserDataInfo u = iUserDataInfoRecordService.getUserDataInfoByMoblile(tenantVehicleRel.getBillReceiverMobile(), false, accessToken);
                vehicleMap.put("canModifyName", null == u);
                vehicleList.add(vehicleMap);
            }
            out.setVehicleList(vehicleList);
        }
        if (out.getSalaryPattern() != null) {
            if (out.getSalaryPattern() == 1) {
                out.setSalaryPatternName("????????????");
            } else if (out.getSalaryPattern() == 2) {
                out.setSalaryPatternName("????????????");
            } else if (out.getSalaryPattern() == 3) {
                out.setSalaryPatternName("????????????");
            }
        }
        resultMap.put("user", out);
        List<UserLineRel> userLineRels = iUserLineRelService.getUserLineRelByUserId(tenantUserRel.getUserId(), tenantUserRel.getTenantId());
        out.setUserLineRelList(userLineRels);
        //??????????????? ??????????????????
        if (out.getAttachedOrgId() != null && out.getAttachedOrgId() > 0) {
            out.setAttachedOrgName(iSysOrganizeService.getOrgNameByOrgId(out.getAttachedOrgId(), userDataInfoByAccessToken.getTenantId()));
        }
        if (out.getAttachedUserId() != null && out.getAttachedUserId() > 0) {
            UserDataInfo info = iUserDataInfoRecordService.getUserDataInfo(out.getAttachedUserId());
            if (info != null) {
                out.setAttachedUserName(info.getLinkman());
            }
        }
        //??????????????????
        if (tenantUserRel.getHasVer() != null && tenantUserRel.getHasVer().intValue() == EnumConsts.HAS_VER_STATE.HAS_VER_NO) {
            UserDataInfoVer userDataInfoVer = iUserDataInfoVerService.getUserDataInfoVerNoTenant(userId);
            if (userDataInfoVer != null) {
//                BeanUtil.copyProperties(userDataInfoVer,ver);
                CommonUtils.copyProperties(ver, userDataInfoVer);
            }
            if (tenantUserRel.getCarUserType() == SysStaticDataEnum.CAR_USER_TYPE.BUSINESS_CAR || tenantUserRel.getCarUserType() == SysStaticDataEnum.CAR_USER_TYPE.OWN_CAR) {
                TenantUserRelVer tenantUserRelVer = iTenantUserRelVerService.getTenantUserRelVerByUserId(userId);
                if (tenantUserRelVer != null) {
                    CommonUtils.copyProperties(ver, tenantUserRelVer, out);
                    if (ver.getAttachedOrgId() != null) {
                        ver.setAttachedOrgName(iSysOrganizeService.getOrgNameByOrgId(tenantUserRelVer.getAttachedOrgId(), userDataInfoByAccessToken.getTenantId()));
                    }
                }
                if (ver.getAttachedUserId() != null) {
                    if (tenantUserRelVer.getAttachedUserId() != null) {
                        UserDataInfo info = iUserDataInfoRecordService.getUserDataInfo(tenantUserRelVer.getAttachedUserId());
                        if (info != null) {
                            ver.setAttachedUserName(info.getLinkman());
                        }
                    }
                }
            }
            if (tenantUserRel.getCarUserType() == SysStaticDataEnum.CAR_USER_TYPE.OWN_CAR) {
                //???????????????
                TenantUserRelVer tenantUserRelVer = iTenantUserRelVerService.getTenantUserRelVerByUserId(userId);
                if (tenantUserRelVer != null && out.getCarUserType() != tenantUserRelVer.getCarUserType()) {
                    ver.setVehicleClassName(isysStaticDataService.getSysStaticData("VEHICLE_CLASS", String.valueOf(tenantUserRelVer.getCarUserType())).getCodeName());
                }
//
                List<UserLineRelVer> userLineRelVers = iUserLineRelVerService.getUserLineRelVer(tenantUserRel.getUserId(), tenantUserRel.getTenantId(), SysStaticDataEnum.VER_STATE.VER_STATE_Y);
                ver.setUserLineRelVerList(userLineRelVers);
                //????????????????????????
                //??????????????????????????????????????????
                if (userLineRelVers != null && userLineRels != null) {
                    if (userLineRelVers.size() != userLineRels.size()) {
                        ver.setShowLineVer(true);
                    } else if (userLineRelVers.size() == userLineRels.size()) {
                        Set<String> lineCodeSet = new HashSet<>();
                        for (UserLineRel userLineRel : userLineRels) {
                            lineCodeSet.add(userLineRel.getLineCodeRule());
                        }
                        for (int i = 0; i < userLineRelVers.size(); i++) {
                            UserLineRelVer userLineRelVer = userLineRelVers.get(i);
                            if (!lineCodeSet.contains(userLineRelVer.getLineCodeRule())) {
                                ver.setShowLineVer(true);
                                break;
                            }
                        }
                    }
                }

                if (tenantUserRelVer != null) {
                    CommonUtils.copyProperties(ver, tenantUserRelVer, out);
                }

                //???????????????????????????
                List<TenantUserSalaryRelVer> tenantUserSalaryRelVerList = iTenantUserSalaryRelVerService.getTenantUserSalaryRelVers(tenantUserRel.getId());
                if (tenantUserSalaryRelVerList != null && tenantUserSalaryRelVerList.size() > 0) {
                    TenantUserSalaryRelVer tenantUserSalaryRelVer = tenantUserSalaryRelVerList.get(0);
                    CommonUtils.copyProperties(ver, tenantUserSalaryRelVer, out);
                    List<UserSalaryInfoVer> userSalaryInfoVerList = iUserSalaryInfoVerService.getUserSalaryInfoVers(userId);
                    ver.setUserSalaryInfoVerList(userSalaryInfoVerList);

                    //??????????????????????????????????????????
                    if (userSalaryInfoVerList != null && out.getUserSalaryInfoList() != null) {
                        if (userSalaryInfoVerList.size() != out.getUserSalaryInfoList().size()) {
                            ver.setShowSalary(tenantUserSalaryRelVer.getSalaryPattern());
                        } else if (userSalaryInfoVerList.size() == out.getUserSalaryInfoList().size()) {
                            for (int i = 0; i < userSalaryInfoVerList.size(); i++) {
                                UserSalaryInfoVer userSalaryInfoVer = userSalaryInfoVerList.get(i);
                                UserSalaryInfo userSalaryInfo = out.getUserSalaryInfoList().get(i);
                                if ((null != userSalaryInfoVer.getPrice() && userSalaryInfoVer.getPrice().compareTo(userSalaryInfo.getPrice()) != 0) ||
                                        (null != userSalaryInfoVer.getStartNum() && userSalaryInfoVer.getStartNum().compareTo(userSalaryInfo.getStartNum()) != 0) ||
                                        (null != userSalaryInfoVer.getEndNum() && userSalaryInfoVer.getEndNum().compareTo(userSalaryInfo.getEndNum()) != 0)) {
                                    ver.setShowSalary(tenantUserSalaryRelVer.getSalaryPattern());
                                    break;
                                }
                            }
                        }

                    }

                }

            }
            if (ver.getSalaryPattern() != null) {
                if (ver.getSalaryPattern().equals("1")) {
                    ver.setSalaryPatternName("????????????");
                } else if (ver.getSalaryPattern().equals("2")) {
                    ver.setSalaryPatternName("????????????");
                } else if (ver.getSalaryPattern().equals("3")) {
                    ver.setSalaryPatternName("????????????");
                }
            }
            if (ver != null && ver.getUserPrice() != null && ver.getUserPrice() < 1L) {
                ver.setUserPrice(null);
            }
        }
        resultMap.put("ver", ver);
        return resultMap;
    }

    @Override
    public List<TenantVehicleRel> getDriverAllVehicle(Long userId, Long tenantId) {
        if (null == userId || null == tenantId) {
            throw new BusinessException("????????????");
        }
        return iTenantVehicleRelService.getTenantVehicleRelByDriverUserId(userId, tenantId);
    }

    @Override
    public void deleteDriver(Long tenantUserRelId, String vehicleCode, String accessToken) throws Exception {
        iAbstractDriverService.delete(tenantUserRelId, vehicleCode, accessToken);
    }


    @Override
    public String doUpdateDriverMobile(String mobilePhone, String oldMobilePhone, Long userId, Long relId, String accessToken) {
        if (org.apache.commons.lang.StringUtils.isEmpty(mobilePhone)) {
            return "???????????????????????????";
        }
        TenantUserRel tenantUserRel = null;
        UserDataInfo userDataInfo = iUserDataInfoRecordService.getUserDataInfo(userId);
        if (userDataInfo == null) {
            return "???????????????";
        }
        String sourcePhone = userDataInfo.getMobilePhone();
        if (relId > 0) {
            tenantUserRel = iTenantUserRelService.getTenantUserRelByRelId(relId);
            if (null == tenantUserRel) {
                return "???????????????";
            }
        }
        SysUser operater = iSysUserService.getSysUserByUserIdOrPhone(null, mobilePhone);
        if (operater != null) {
            return "?????????????????????????????????????????????";
        }
        //??????????????????????????????????????????
        Integer hasDriverOrder = orderSchedulerService.queryOrderdriverInfo(userId, OrderConsts.ORDER_STATE.RECIVE_AUDIT_NOT);
        if (hasDriverOrder != null && hasDriverOrder > 0) {
            return "???????????????????????????????????????????????????";
        }
        if (null != tenantUserRel) {
            //??????????????????????????????
            TenantUserRelVer userRelVer = iTenantUserRelVerService.getTenantUserRelVer(tenantUserRel.getUserId(), tenantUserRel.getTenantId());
            if (null != userRelVer) {
                userRelVer.setVerState(SysStaticDataEnum.VER_STATE.VER_STATE_N);
                iTenantUserRelVerService.updateById(userRelVer);
            }

            TenantUserRelVer tenantUserRelVer = new TenantUserRelVer();
            BeanUtil.copyProperties(tenantUserRel, tenantUserRelVer);
            tenantUserRelVer.setVerState(SysStaticDataEnum.VER_STATE.VER_STATE_Y);
            tenantUserRel.setHasVer(EnumConsts.HAS_VER_STATE.HAS_VER_NO);
            iTenantUserRelService.updateById(tenantUserRel);
            iTenantUserRelVerService.save(tenantUserRelVer);
        }
        UserDataInfoVer ver = iUserDataInfoVerService.getUserDataInfoVerNoTenant(userId);
        if (ver != null) {
            ver.setVerState(SysStaticDataEnum.VER_STATE.VER_STATE_N);
            iUserDataInfoVerService.updateById(ver);
        }
        UserDataInfoVer userDataInfoVer = new UserDataInfoVer();
        BeanUtil.copyProperties(userDataInfo, userDataInfoVer);
        userDataInfoVer.setUserId(userDataInfo.getId());
        userDataInfoVer.setMobilePhone(mobilePhone);
        userDataInfoVer.setVerState(SysStaticDataEnum.VER_STATE.VER_STATE_Y);
        iUserDataInfoVerService.save(userDataInfoVer);
        if (null != tenantUserRel) {
            String logDesc = "????????????[" + userDataInfo.getMobilePhone() + "]??????[" + userDataInfoVer.getMobilePhone() + "]";
            //     iSysOperLogService.saveSysOperLog(SysOperLogConst.BusiCode.Driver, tenantUserRel.getId(), SysOperLogConst.OperType.Update, logDesc);
            saveSysOperLog(SysOperLogConst.BusiCode.Driver, SysOperLogConst.OperType.Update, logDesc, accessToken, tenantUserRel.getId(), null);
            boolean bool = iAuditService.startProcess(AuditConsts.AUDIT_CODE.AUDIT_CODE_USER, tenantUserRel.getId(), SysOperLogConst.BusiCode.Driver, new HashMap<>(), accessToken);
            if (!bool) {
                return "???????????????????????????";
            }
        } else {
            String logDesc = "????????????[" + userDataInfo.getMobilePhone() + "]??????[" + userDataInfoVer.getMobilePhone() + "]";
//            iSysOperLogService.saveSysOperLog(SysOperLogConst.BusiCode.DriverOBMS, userDataInfo.getId(), SysOperLogConst.OperType.Update, logDesc);
            saveSysOperLog(SysOperLogConst.BusiCode.DriverOBMS, SysOperLogConst.OperType.Update, logDesc, accessToken, userDataInfo.getId(), null);
            BeanUtil.copyProperties(userDataInfoVer, userDataInfo);
            userDataInfo.setId(userDataInfoVer.getUserId());
            iUserDataInfoRecordService.updateById(userDataInfo);
            overrideMobilePhoneAndLinkman(userDataInfo.getId(), userDataInfo.getMobilePhone(), userDataInfo.getLinkman());
            iDriverInfoExtService.updateLuGeAuthState(userId, false, null, 0);
        }
        try {
            //????????????
            wechatUserRelService.unbind(sourcePhone);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "????????????";
    }

    @Override
    public DriverUserInfoQuickVo getDriverUserInfoForQuick(String loginAcct, String accessToken) throws Exception {
        DriverUserInfoQuickVo driverUserInfoQuick = new DriverUserInfoQuickVo();
        LoginInfo loginInfo = iUserDataInfoRecordService.getLoginInfoByAccessToken(accessToken);
        UserDataInfo userDataInfo = iUserDataInfoRecordService.getUserDataInfoByMoblile(loginAcct, false, accessToken);
        if (null == userDataInfo) {
            return driverUserInfoQuick;
        }
        driverUserInfoQuick.setUserId(userDataInfo.getId());
        driverUserInfoQuick.setLinkman(userDataInfo.getLinkman());
        if (!isDriver(userDataInfo.getId(), loginInfo.getTenantId())) {
            //????????????
            driverUserInfoQuick.setDriverFlag(false);
            return driverUserInfoQuick;
        } else {
            driverUserInfoQuick.setDriverFlag(true);
        }

        if (null != userDataInfo.getTenantId() && userDataInfo.getTenantId() > 0) {
            //????????????
            driverUserInfoQuick.setOwnFlag(true);
            driverUserInfoQuick.setTenantId(userDataInfo.getTenantId());
            SysTenantDef sysTenantDef = sysTenantDefService.getSysTenantDef(userDataInfo.getTenantId(), true);
            driverUserInfoQuick.setTenantName(sysTenantDef.getName());
        } else {
            //???????????????
            driverUserInfoQuick.setOwnFlag(false);
        }
        //??????????????????
        List<VehicleDataInfo> vehicleDataInfoList = iVehicleDataInfoService.getDriverAllRelVehicleList(userDataInfo.getId());
        formatVehcile(vehicleDataInfoList);
        driverUserInfoQuick.setVehicleDataInfoList(vehicleDataInfoList);
        return driverUserInfoQuick;
    }

    private void formatVehcile(List<VehicleDataInfo> vehicleDataInfoList) throws Exception {
        if (org.apache.commons.collections.CollectionUtils.isNotEmpty(vehicleDataInfoList)) {
            for (VehicleDataInfo vehicleDataInfo : vehicleDataInfoList) {
                vehicleDataInfo.setVehicleLengthName(isysStaticDataService.getSysStaticDataId("VEHICLE_LENGTH", vehicleDataInfo.getVehicleLength() == null ? "0" : vehicleDataInfo.getVehicleLength()));
                vehicleDataInfo.setVehicleStatusName(isysStaticDataService.getSysStaticDataId("VEHICLE_STATUS", vehicleDataInfo.getVehicleStatus() == null ? "0" : vehicleDataInfo.getVehicleStatus() + ""));
                TenantVehicleRel tenantVehicleRel = iTenantVehicleRelService.getZYVehicleByVehicleCode(vehicleDataInfo.getId());
                if (null == tenantVehicleRel) {
                    vehicleDataInfo.setEquipmentName("?????????");
                } else {
                    SysTenantDef sysTenantDef = sysTenantDefService.getSysTenantDef(tenantVehicleRel.getTenantId(), true);
                    vehicleDataInfo.setEquipmentName(sysTenantDef.getName());
                }
            }
        }
    }

    @Override
    @Transactional
    public void doUpdateDriver(DoAddDriverVo doAddDriverVo, String accessToken) throws Exception {

        int carUserType = Integer.valueOf(doAddDriverVo.getCarUserType());

        checkParamForUpdate(doAddDriverVo);
//        afterCheckParamForUpdate(doAddDriverVo,accessToken);
        if (doAddDriverVo.getCarUserType().equals("1")) {
            afterCheckParamForUpdate(doAddDriverVo, accessToken); //????????????????????????????????????????????????
        }
        TenantUserRel tenantUserRel = null; //????????????tenantUserRel?????????????????????????????????????????????
        UserDataInfo userDataInfo = iUserDataInfoRecordService.getUserDataInfo(doAddDriverVo.getUserId());
        if (null == userDataInfo) {
            throw new BusinessException("??????????????????");
        }

        if (doAddDriverVo.getRelId() != null && doAddDriverVo.getRelId() > 0) {
            tenantUserRel = iTenantUserRelService.getTenantUserRelByRelId(doAddDriverVo.getRelId());
        }

        UserDataInfoVer ver = iUserDataInfoVerService.getUserDataInfoVerNoTenant(doAddDriverVo.getUserId());
        if (ver != null) {
            ver.setVerState(SysStaticDataEnum.VER_STATE.VER_STATE_N);
            iUserDataInfoVerService.updateById(ver);
        }

        UserDataInfoVer userDataInfoVer = new UserDataInfoVer();
        BeanUtil.copyProperties(userDataInfo, userDataInfoVer);
        BeanUtil.copyProperties(doAddDriverVo, userDataInfoVer, CopyOptions.create().setIgnoreNullValue(true).setIgnoreError(true));
        if (StrUtil.isEmpty(doAddDriverVo.getDriverLicenseExpiredTime())) {
            userDataInfoVer.setDriverLicenseExpiredTime(null);
        }
        if (StrUtil.isEmpty(doAddDriverVo.getDriverLicenseTime())) {
            userDataInfoVer.setDriverLicenseTime(null);
        }
        if (StrUtil.isEmpty(doAddDriverVo.getQcCertiTime())) {
            userDataInfoVer.setQcCertiTime(null);
        }
        if (StrUtil.isEmpty(doAddDriverVo.getQcCertiExpiredTime())) {
            userDataInfoVer.setQcCertiExpiredTime(null);
        }
        userDataInfoVer.setUserId(userDataInfo.getId());
        userDataInfoVer.setTenantId(userDataInfo.getTenantId());
        userDataInfoVer.setVerState(SysStaticDataEnum.VER_STATE.VER_STATE_Y);
        if (!doAddDriverVo.getCarUserType().equals("1")) {
            afterCreateUserDataInfoVerForUpdate(tenantUserRel, userDataInfoVer);    //??????????????????????????????????????????????????????????????????ID
        }
        iUserDataInfoVerService.save(userDataInfoVer);
        TenantUserRelVer tenantUserRelVer = new TenantUserRelVer();
        if (null != tenantUserRel) {
            TenantUserRelVer userRelVer = iTenantUserRelVerService.getTenantUserRelVer(tenantUserRel.getUserId(), tenantUserRel.getTenantId());
            if (null != userRelVer) {
                userRelVer.setVerState(SysStaticDataEnum.VER_STATE.VER_STATE_N);
                iTenantUserRelVerService.updateById(userRelVer);
            }

            BeanUtil.copyProperties(tenantUserRel, tenantUserRelVer);
            BeanUtil.copyProperties(doAddDriverVo, tenantUserRelVer, CopyOptions.create().setIgnoreNullValue(true).setIgnoreError(true));
            tenantUserRelVer.setVerState(SysStaticDataEnum.VER_STATE.VER_STATE_Y);
            tenantUserRelVer.setTenantId(tenantUserRel.getTenantId());
            tenantUserRel.setHasVer(EnumConsts.HAS_VER_STATE.HAS_VER_NO);
            iTenantUserRelService.updateById(tenantUserRel);
            iTenantUserRelVerService.save(tenantUserRelVer);
        }

        afterUpdateTenantUserRel(tenantUserRel, userDataInfo, doAddDriverVo, accessToken);
        //??????????????????????????????????????????????????????
        if (userDataInfo.getAuthState() == null || userDataInfo.getAuthState() == SysStaticDataEnum.AUTH_STATE.AUTH_STATE1) {
            long id = userDataInfo.getId();
            BeanUtil.copyProperties(userDataInfoVer, userDataInfo);
            userDataInfo.setId(id);
            //?????????????????????????????????
            iUserDataInfoRecordService.overrideMobilePhoneAndLinkman(userDataInfo.getId(), userDataInfo.getMobilePhone(), userDataInfo.getLinkman());
        }
        iUserDataInfoRecordService.saveOrUpdate(userDataInfo);
        if (null != tenantUserRel) {
            if (tenantUserRel.getState() == SysStaticDataEnum.AUTH_STATE.AUTH_STATE1) {
                Long id = tenantUserRel.getId();
                BeanUtil.copyProperties(tenantUserRelVer, tenantUserRel);
                tenantUserRel.setId(id);
                iTenantUserRelService.updateById(tenantUserRel);
            }
        }

        afterUpdateTenantUserRelVer(tenantUserRel, carUserType, accessToken);
        if (null != tenantUserRel) {
//            iSysOperLogService.saveSysOperLog(SysOperLogConst.BusiCode.Driver, tenantUserRel.getId(), SysOperLogConst.OperType.Update, "????????????");
            saveSysOperLog(SysOperLogConst.BusiCode.Driver, SysOperLogConst.OperType.Update, "????????????", accessToken, tenantUserRel.getId(), null);
            boolean bool = iAuditService.startProcess(AuditConsts.AUDIT_CODE.AUDIT_CODE_USER, tenantUserRel.getId(), SysOperLogConst.BusiCode.Driver, new HashMap<>(), accessToken);
            if (!bool) {
                throw new BusinessException("???????????????????????????");
            }
        } else {
            //???????????????????????????????????????
            Long id = userDataInfoVer.getUserId();
            BeanUtil.copyProperties(userDataInfoVer, userDataInfo);
            userDataInfo.setHasVer(EnumConsts.HAS_VER_STATE.HAS_VER_YES);
            userDataInfo.setAuthState(SysStaticDataEnum.SYS_USER_STATE.AUDIT_APPROVE);
            //?????????
//            iSysOperLogService.saveSysOperLog(SysOperLogConst.BusiCode.DriverOBMS, userDataInfo.getId(), SysOperLogConst.OperType.Audit, "??????????????????");
            userDataInfoVer.setVerState(SysStaticDataEnum.VER_STATE.VER_STATE_Y);
            userDataInfo.setId(id);
            iUserDataInfoVerService.save(userDataInfoVer);
            iUserDataInfoRecordService.updateById(userDataInfo);
            overrideMobilePhoneAndLinkman(userDataInfo.getId(), userDataInfo.getMobilePhone(), userDataInfo.getLinkman());
            saveSysOperLog(SysOperLogConst.BusiCode.DriverOBMS, SysOperLogConst.OperType.Update, "????????????", accessToken, userDataInfo.getId(), null);
        }
    }

    @Override
    public Page doQueryApplyRecords(ApplyRecordQueryDto applyRecordQueryDto, Integer page, Integer pageSize, String accessToken) throws Exception {
        LoginInfo userDataInfo = iUserDataInfoRecordService.getLoginInfoByAccessToken(accessToken);
        Page<ApplyRecordVo> pageInfo = new Page<>(page, pageSize);
        Page<ApplyRecordVo> list = userDataInfoRecordMapper.doQueryApplyRecords(pageInfo, applyRecordQueryDto, userDataInfo.getTenantId());
        List<ApplyRecordVo> out = new ArrayList<ApplyRecordVo>();
        //????????????
        List<ApplyRecordVo> rtnMapList = list.getRecords();
        if (rtnMapList != null && rtnMapList.size() > 0) {
            for (int i = 0; i < rtnMapList.size(); i++) {
                ApplyRecordVo rtnMap = rtnMapList.get(i);
                if (rtnMap.getState() != null && rtnMap.getState() > -1) {
                    rtnMap.setStateName(isysStaticDataService.getSysStaticData("APPLY_STATE", rtnMap.getState() + "").getCodeName());
                }
                if (rtnMap.getApplyCarUserType() != null && rtnMap.getApplyCarUserType() > -1) {
                    rtnMap.setApplyCarUserTypeName(isysStaticDataService.getSysStaticData("DRIVER_TYPE", rtnMap.getApplyCarUserType() + "").getCodeName());
                }
                out.add(rtnMap);
            }
        }
        list.setRecords(out);
        return list;
    }

    @Override
    public Page doQueryBeApplyRecords(ApplyRecordQueryDto applyRecordQueryDto, Integer page, Integer pageSize, String accessToken) throws Exception {
        LoginInfo userDataInfo = iUserDataInfoRecordService.getLoginInfoByAccessToken(accessToken);
        Page<ApplyRecordVo> pageInfo = new Page<>(page, pageSize);
        Page<ApplyRecordVo> list = userDataInfoRecordMapper.doQueryBeApplyRecords(pageInfo, applyRecordQueryDto, userDataInfo.getTenantId());
        List<ApplyRecordVo> listOut = new ArrayList<ApplyRecordVo>();
        //????????????
        List<ApplyRecordVo> rtnMapList = list.getRecords();
        if (rtnMapList != null && rtnMapList.size() > 0) {
            List<Long> busiIds = new ArrayList<>();
            for (ApplyRecordVo recordVo : rtnMapList) {
                if (SysStaticDataEnum.APPLY_STATE.APPLY_STATE0 == recordVo.getState()) {
                    busiIds.add(recordVo.getId());
                }
            }
            Map<Long, Boolean> hasPermissionMap = null;
            if (!busiIds.isEmpty()) {
                hasPermissionMap = iAuditNodeInstService.isHasPermission(AuditConsts.AUDIT_CODE.AUDIT_CODE_APPLY, busiIds, accessToken);
            }

            for (int i = 0; i < rtnMapList.size(); i++) {
                ApplyRecordVo rtnMap = rtnMapList.get(i);
                if (rtnMap.getState() != null && rtnMap.getState() > -1) {
                    rtnMap.setStateName(isysStaticDataService.getSysStaticData("APPLY_STATE", rtnMap.getState() + "").getCodeName());
                }
                if (rtnMap.getApplyCarUserType() != null && rtnMap.getApplyCarUserType() > -1) {
                    rtnMap.setApplyCarUserTypeName(isysStaticDataService.getSysStaticData("DRIVER_TYPE", rtnMap.getApplyCarUserType() + "").getCodeName());
                }
                if (hasPermissionMap != null && hasPermissionMap.containsKey(rtnMap.getId()) && hasPermissionMap.get(rtnMap.getId())) {
                    rtnMap.setAuditFlg(1);
                } else {
                    rtnMap.setAuditFlg(0);
                }
                listOut.add(rtnMap);
            }
        }
        list.setRecords(listOut);
        return list;
    }

    @Override
    public Page doQueryDriverHis(DriverQueryDto driverQueryDto, Integer page, Integer pageSize, String accessToken) throws Exception {
        LoginInfo userDataInfo = iUserDataInfoRecordService.getLoginInfoByAccessToken(accessToken);
        Page<DriverQueryVo> pageInfo = new Page<>(page, pageSize);
        Page<DriverQueryVo> list = userDataInfoRecordMapper.doQueryDriverHis(pageInfo, driverQueryDto, userDataInfo.getTenantId());
        List<DriverQueryVo> listOut = new ArrayList<DriverQueryVo>();
        //????????????
        List<DriverQueryVo> rtnMapList = list.getRecords();
        if (rtnMapList != null && rtnMapList.size() > 0) {
            for (DriverQueryVo m : rtnMapList) {
                if (m.getCarUserType() != null && m.getCarUserType() > 0) {
                    m.setCarUserTypeName(isysStaticDataService.getSysStaticData("DRIVER_TYPE", String.valueOf(m.getCarUserType())).getCodeName());
                }
                if (userDataInfo.getTenantId().equals(m.getAttachTenantId())) {
                    //???????????????????????????????????????
                    m.setAttachTenantName("-");
                    m.setAttachTennantLinkman("-");
                    m.setAttachTennantLinkPhone("-");
                }
                if (m.getAttachTenantId() == -1) {
                    m.setAttachTenantName("???????????????");
                    m.setAttachTennantLinkman("-");
                    m.setAttachTennantLinkPhone("-");
                }
                listOut.add(m);
            }
            list.setRecords(listOut);
        }
        return pageInfo;
    }

    @Override
    @Transactional
    public DriverApplyRecordVo selectApplyRecordVo(Long applyId) throws Exception {
        DriverApplyRecordVo driverApplyRecordVo = userDataInfoRecordMapper.selectDriverApplyRecordVo(applyId);
        if (driverApplyRecordVo != null) {
            if (driverApplyRecordVo.getState() != null && driverApplyRecordVo.getState() > -1) {
                driverApplyRecordVo.setStateName(isysStaticDataService.getSysStaticData("APPLY_STATE", driverApplyRecordVo.getState() + "").getCodeName());
            }
            if (driverApplyRecordVo.getApplyCarUserType() != null && driverApplyRecordVo.getApplyCarUserType() > -1) {
                driverApplyRecordVo.setApplyCarUserTypeName(isysStaticDataService.getSysStaticData("DRIVER_TYPE", driverApplyRecordVo.getApplyCarUserType() + "").getCodeName());
            }
        }
        this.updateReadState(applyId);
        return driverApplyRecordVo;
    }

    @Override
    public BeApplyRecordVo getBeApplyRecord(Long applyId, String token) throws Exception {
        BeApplyRecordVo beApplyRecordVo = userDataInfoRecordMapper.getBeApplyRecord(applyId);
        LoginInfo loginInfo = iUserDataInfoRecordService.getLoginInfoByAccessToken(token);
        if (beApplyRecordVo != null) {
            if (beApplyRecordVo.getState() != null && beApplyRecordVo.getState() > -1) {
                beApplyRecordVo.setStateName(isysStaticDataService.getSysStaticData("APPLY_STATE", beApplyRecordVo.getState() + "").getCodeName());
                if (beApplyRecordVo.getState() == 1) {
                    beApplyRecordVo.setStateName("???????????????");
                } else if (beApplyRecordVo.getState() == 2) {
                    beApplyRecordVo.setStateName("???????????????");
                }
            }

            if (beApplyRecordVo.getApplyCarUserType() != null && beApplyRecordVo.getApplyCarUserType() > -1) {
                beApplyRecordVo.setApplyCarUserTypeName(isysStaticDataService.getSysStaticData("DRIVER_TYPE", beApplyRecordVo.getApplyCarUserType() + "").getCodeName());
            }

            if ((beApplyRecordVo.getApplyDriverUserId() != null && beApplyRecordVo.getApplyDriverUserId() > 0)
                    || org.apache.commons.lang.StringUtils.isNotEmpty(beApplyRecordVo.getApplyPlateNumbers())
                    || beApplyRecordVo.getVehicleNum() > 0) {

                List<BeApplyRecordVehicleVo> list = userDataInfoRecordMapper.getBeApplyRecordVehicle(beApplyRecordVo.getUserId(), beApplyRecordVo.getBeApplyTenantId());
                Set<String> plateNumbserSet = new HashSet();
                if (org.apache.commons.lang.StringUtils.isNotEmpty(beApplyRecordVo.getApplyPlateNumbers())) {
                    String[] plateNumbers = beApplyRecordVo.getApplyPlateNumbers().split(",");
                    for (String plateNumber : plateNumbers) {
                        plateNumbserSet.add(plateNumber);
                    }
                }

                if (beApplyRecordVo.getApplyDriverUserId() != null && beApplyRecordVo.getApplyDriverUserId() > 0) {
                    UserDataInfo userDataInfo = iUserDataInfoRecordService.getUserDataInfo(beApplyRecordVo.getApplyDriverUserId());
                    beApplyRecordVo.setApplyDriverPhone(userDataInfo.getMobilePhone());
                    beApplyRecordVo.setApplyDriverName(userDataInfo.getLinkman());

                }

                SysTenantDef sysTenantDef = sysTenantDefService.getById(loginInfo.getTenantId());
                UserDataInfo userDataInfo = iUserDataInfoRecordService.getById(sysTenantDef.getAdminUser());

                if (list != null && list.size() > 0) {
                    for (BeApplyRecordVehicleVo vehicle : list) {
                        vehicle.setVehicleStatusName(isysStaticDataService.getSysStaticData("VEHICLE_STATUS", vehicle.getVehicleStatus()).getCodeName());
                        vehicle.setVehicleTypeString((StringUtils.isEmpty(vehicle.getVehicleLength()) ? "" : vehicle.getVehicleLength() + "???  ") + (vehicle.getVehicleStatusName() != null ? vehicle.getVehicleStatusName() : ""));
                        if (plateNumbserSet.contains(vehicle.getPlateNumber())) {
                            vehicle.setChecked(true);
                        }
                        vehicle.setBillReceiverMobile(userDataInfo.getMobilePhone());
                        vehicle.setBillReceiverName(sysTenantDef.getName());
                    }
                    beApplyRecordVo.setVehicleList(list);
                } else {
                    BeApplyRecordVehicleVo vehicleVo = new BeApplyRecordVehicleVo();
                    list.add(vehicleVo);
                    beApplyRecordVo.setVehicleList(list);
                }

            }
        }
        return beApplyRecordVo;
    }

    @Override
    @Transactional
    public String doAddApplyAgain(DoAddApplyAgainVo doAddApplyAgainVo, String accessToken) throws Exception {
        ApplyRecord applyRecord = iApplyRecordService.getById(doAddApplyAgainVo.getApplyId());
        if (null == applyRecord) {
            return "????????????????????????";
        }
        UserDataInfo userDataInfo = iUserDataInfoRecordService.getById(applyRecord.getBusiId());
        ApplyRecord record = new ApplyRecord();
        BeanUtil.copyProperties(applyRecord, record);
        record.setId(null);
        record.setCreateDate(LocalDateTime.now());
        record.setCreateTime(LocalDateTime.now());
        record.setUpdateTime(null);
        record.setApplyRemark(doAddApplyAgainVo.getApplyRemark());
        record.setApplyFileId(doAddApplyAgainVo.getApplyFileId());
        record.setAuditDate(null);
        record.setAuditOpId(null);
        record.setAuditRemark(null);
        record.setBeApplyTenantId(userDataInfo.getTenantId());
        record.setState(SysStaticDataEnum.APPLY_STATE.APPLY_STATE0);
        //??????????????????
        if (2 == doAddApplyAgainVo.getType()) {
            record.setState(SysStaticDataEnum.APPLY_STATE.APPLY_STATE3);
        }
        if (record.getApplyCarUserType() == null) {
            return "?????????????????????";
        }
        if (!(record.getApplyCarUserType() == 1) && !(record.getApplyCarUserType() == 2)
                && !(record.getApplyCarUserType() == 3) && !(record.getApplyCarUserType() == 4)) {
            return "?????????????????????";
        }
        List<ApplyRecord> applyRecordList = iApplyRecordService.getApplyRecord(record.getBusiId(), record.getApplyTenantId(), SysStaticDataEnum.APPLY_STATE.APPLY_STATE0);
        if (org.apache.commons.collections.CollectionUtils.isNotEmpty(applyRecordList)) {
            return "???????????????????????????";
        }
        applyRecordList = iApplyRecordService.getApplyRecord(record.getBusiId(), SysStaticDataEnum.CAR_USER_TYPE.OWN_CAR, SysStaticDataEnum.APPLY_STATE.APPLY_STATE0);
        if (org.apache.commons.collections.CollectionUtils.isNotEmpty(applyRecordList)) {
            return "?????????????????????????????????????????????";
        }
        UserDataInfo user = iUserDataInfoRecordService.getUserDataInfo(record.getBusiId());
        if (null == user) {
            return "??????????????????";
        }
        TenantUserRel tenantUserRel = iTenantUserRelService.getAllTenantUserRelByUserId(record.getBusiId(), record.getApplyTenantId());
        if (null != tenantUserRel && tenantUserRel.getCarUserType().intValue() == record.getApplyCarUserType().intValue()) {
            return "????????????????????????????????????";
        }
        //??????????????????
        if (null == record.getState()) {
            applyRecord.setState(SysStaticDataEnum.APPLY_STATE.APPLY_STATE0);
        }
        iApplyRecordService.save(record);
        afterSaveApplyRecord(record, accessToken);
        applyRecord.setNewApplyId(record.getId());
        iApplyRecordService.saveOrUpdate(applyRecord);
        return null;
    }

    @Override
    @Transactional
    public String doAddApply(DoAddApplyVo doAddApplyVo, String accessToken) {
        LoginInfo user = iUserDataInfoRecordService.getLoginInfoByAccessToken(accessToken);
        ApplyRecord applyRecord = new ApplyRecord();
        applyRecord.setApplyType(1);
        applyRecord.setApplyCarUserType(doAddApplyVo.getCarUserType());
        applyRecord.setBusiId(doAddApplyVo.getBusiId());
        applyRecord.setApplyRemark(doAddApplyVo.getApplyRemark());
        applyRecord.setApplyFileId(doAddApplyVo.getApplyFileId());
        applyRecord.setApplyTenantId(user.getTenantId());
        applyRecord.setBeApplyTenantId(doAddApplyVo.getTenantId());
        if (doAddApplyVo.getUpdate() == 1) {
            applyRecord.setBusiCode(2);//??????
        } else {
            applyRecord.setBusiCode(1);//??????
        }
        applyRecord.setState(SysStaticDataEnum.APPLY_STATE.APPLY_STATE0);
        applyRecord.setApplyPlateNumbers(doAddApplyVo.getPlateNumbers());
        if (applyRecord.getApplyCarUserType() == null) {
            return "?????????????????????";
        }
        if (!(applyRecord.getApplyCarUserType() == 1) && !(applyRecord.getApplyCarUserType() == 2)
                && !(applyRecord.getApplyCarUserType() == 3) && !(applyRecord.getApplyCarUserType() == 4)) {
            return "?????????????????????";
        }
        List<ApplyRecord> applyRecordList = iApplyRecordService.getApplyRecord(applyRecord.getBusiId(), applyRecord.getApplyTenantId(), SysStaticDataEnum.APPLY_STATE.APPLY_STATE0);
        if (org.apache.commons.collections.CollectionUtils.isNotEmpty(applyRecordList)) {
            return "???????????????????????????";
        }
        applyRecordList = iApplyRecordService.getApplyRecord(applyRecord.getBusiId(), SysStaticDataEnum.CAR_USER_TYPE.OWN_CAR, SysStaticDataEnum.APPLY_STATE.APPLY_STATE0);
        if (org.apache.commons.collections.CollectionUtils.isNotEmpty(applyRecordList)) {
            return "?????????????????????????????????????????????";
        }
        UserDataInfo userDataInfo = iUserDataInfoRecordService.getUserDataInfo(applyRecord.getBusiId());
        if (null == userDataInfo) {
            return "??????????????????";
        }
        //??????????????????
        if (null == applyRecord.getState()) {
            applyRecord.setState(SysStaticDataEnum.APPLY_STATE.APPLY_STATE0);
        }
        applyRecord = iApplyRecordService.saveApplyRecord(applyRecord);
        afterSaveApplyRecord(applyRecord, accessToken);
        return null;

    }

    @Override
    @Transactional
    public boolean doAuditForOBMS(DriverAuditVo driverAuditVo, String token) throws Exception {
        LoginInfo user = iUserDataInfoRecordService.getLoginInfoByAccessToken(token);
        UserDataInfoVer userDataInfoVer = iUserDataInfoVerService.getUserDataInfoVerNoTenant(driverAuditVo.getUserId());
        UserDataInfo userDataInfo = iUserDataInfoRecordService.getUserDataInfo(driverAuditVo.getUserId());
        if (AuditConsts.RESULT.SUCCESS == driverAuditVo.getChooseResult()) {
            if (userDataInfoVer != null) {
                Long id = user.getId();
                BeanUtil.copyProperties(userDataInfoVer, userDataInfo);
                userDataInfo.setId(id);
            }
            userDataInfo.setAuthState(SysStaticDataEnum.AUTH_STATE.AUTH_STATE2);
            userDataInfo.setHasVer(EnumConsts.HAS_VER_STATE.HAS_VER_YES);
            userDataInfo.setVerifReason(driverAuditVo.getDesc());
            userDataInfo.setAuthManId(user.getUserInfoId());
            iUserDataInfoRecordService.updateById(userDataInfo);
            SysUser sysOperator = iSysUserService.getSysUserByUserIdOrPhone(userDataInfo.getId(), null);
            if (sysOperator != null && !userDataInfo.getMobilePhone().equals(sysOperator.getLoginAcct())) {
                sysOperator.setBillId(userDataInfo.getMobilePhone());
                sysOperator.setLoginAcct(userDataInfo.getMobilePhone());
                iSysUserService.updateById(sysOperator);
            }
            try {
                Map<String, Object> paramMap = new HashMap<>();
                paramMap.put("userName", userDataInfo.getLinkman());
                SysSmsSend sysSmsSend = new SysSmsSend();
                //??????id
                sysSmsSend.setTemplateId(EnumConsts.SmsTemplate.AUDIT_DRIVER_YES);
                //?????????
                sysSmsSend.setBillId(userDataInfo.getMobilePhone());
                //????????????
                sysSmsSend.setObjType(String.valueOf(SysStaticDataEnum.OBJ_TYPE.NOTIFY));
                //???????????????
                sysSmsSend.setParamMap(paramMap);
                sysSmsSend.setSmsType(SysStaticDataEnum.SMS_TYPE.PROGRESS_NOTIFICATIONS);
                iSysSmsSendService.sendSms(sysSmsSend);
            } catch (Exception e) {
                e.printStackTrace();
            }
            saveSysOperLog(SysOperLogConst.BusiCode.DriverOBMS, SysOperLogConst.OperType.Audit, "????????????", token, driverAuditVo.getUserId(), null);
        } else {
            if (userDataInfoVer != null) {
                userDataInfoVer.setVerState(SysStaticDataEnum.VER_STATE.VER_STATE_N);
                iUserDataInfoVerService.updateById(userDataInfoVer);
            }
            userDataInfo.setHasVer(EnumConsts.HAS_VER_STATE.HAS_VER_YES);
            userDataInfo.setVerifReason(driverAuditVo.getDesc());
            userDataInfo.setAuthManId(user.getUserInfoId());
            iUserDataInfoRecordService.updateById(userDataInfo);
            try {
                Map<String, Object> paramMap = new HashMap<>();
                paramMap.put("userName", userDataInfo.getLinkman());
                paramMap.put("desc", driverAuditVo.getDesc());
                SysSmsSend sysSmsSend = new SysSmsSend();
                //??????id
                sysSmsSend.setTemplateId(EnumConsts.SmsTemplate.AUDIT_DRIVER_NO);
                //?????????
                sysSmsSend.setBillId(userDataInfo.getMobilePhone());
                //???????????????
                sysSmsSend.setParamMap(paramMap);
                sysSmsSend.setSmsType(SysStaticDataEnum.SMS_TYPE.PROGRESS_NOTIFICATIONS);
                sysSmsSend.setObjType(String.valueOf(SysStaticDataEnum.OBJ_TYPE.NOTIFY));
                iSysSmsSendService.sendSms(sysSmsSend);
            } catch (Exception e) {
                e.printStackTrace();
            }
            saveSysOperLog(SysOperLogConst.BusiCode.DriverOBMS, SysOperLogConst.OperType.Audit, "???????????????", token, driverAuditVo.getUserId(), null);
        }
        return false;
    }

    @Override
    public void sucess(Long busiId, String desc, Map paramsMap, String token) throws BusinessException {
        LoginInfo loginInfo = iUserDataInfoRecordService.getLoginInfoByAccessToken(token);
        TenantUserRel tenantUserRel = iTenantUserRelService.getTenantUserRelByRelId(busiId);

        TenantUserRelVer tenantUserRelVer = iTenantUserRelVerService.getTenantUserRelVer(tenantUserRel.getUserId(), tenantUserRel.getTenantId());

        if (null == tenantUserRel || null == tenantUserRelVer) {
            throw new BusinessException("?????????????????????");
        }
        if (tenantUserRel.getCarUserType() == SysStaticDataEnum.CAR_USER_TYPE.OWN_CAR && tenantUserRelVer.getCarUserType() != SysStaticDataEnum.CAR_USER_TYPE.OWN_CAR) {
            //????????????????????????
            removeTenantUserSalaryRel(tenantUserRel);
        }

        if (tenantUserRel.getCarUserType() == SysStaticDataEnum.CAR_USER_TYPE.OWN_CAR
                && tenantUserRelVer.getCarUserType() != SysStaticDataEnum.CAR_USER_TYPE.OWN_CAR
                && tenantUserRelVer.getCarUserType() != SysStaticDataEnum.CAR_USER_TYPE.BUSINESS_CAR) {
            //????????????????????????
            removeUserLineRel(tenantUserRel.getUserId(), tenantUserRel.getTenantId(), loginInfo);
        }

        Long relId = tenantUserRel.getId();
        BeanUtil.copyProperties(tenantUserRelVer, tenantUserRel);
        tenantUserRel.setId(relId);
        tenantUserRel.setHasVer(EnumConsts.HAS_VER_STATE.HAS_VER_YES);
        tenantUserRel.setState(SysStaticDataEnum.SYS_USER_STATE.AUDIT_APPROVE);
        tenantUserRel.setStateReason("");
        iTenantUserRelService.updateById(tenantUserRel);

        //?????????????????????????????????????????????????????????
        iOrderAccountService.queryOrderAccount(tenantUserRel.getUserId(), OrderAccountConst.VEHICLE_AFFILIATION.VEHICLE_AFFILIATION0, 0, tenantUserRel.getTenantId(), OrderAccountConst.VEHICLE_AFFILIATION.VEHICLE_AFFILIATION0, SysStaticDataEnum.USER_TYPE.DRIVER_USER);
        UserDataInfo userDataInfo = iUserDataInfoRecordService.getUserDataInfo(tenantUserRel.getUserId());
        UserDataInfoVer userDataInfoVer = iUserDataInfoVerService.getUserDataInfoVerNoTenant(tenantUserRel.getUserId());
        if (null != userDataInfoVer) {
            if ((null != userDataInfo.getIdentification() && !userDataInfo.getIdentification().equals(userDataInfoVer.getIdentification()))
                    || (null == userDataInfo.getIdentification() && null != userDataInfoVer.getIdentification())
                    || !userDataInfo.getMobilePhone().equals(userDataInfoVer.getMobilePhone())
                    || (null != userDataInfo.getLinkman() && !userDataInfo.getLinkman().equals(userDataInfoVer.getLinkman()))
                    || (null != userDataInfo.getLugeAgreement() && !userDataInfo.getLugeAgreement().equals(userDataInfoVer.getLugeAgreement()))
                    || (null == userDataInfo.getLugeAgreement() && null != userDataInfoVer.getLugeAgreement())
            ) {
                iDriverInfoExtService.updateLuGeAuthState(userDataInfo.getId(), false, null, 0);
            }
            Long userVerId = userDataInfoVer.getId();
            BeanUtil.copyProperties(userDataInfoVer, userDataInfo);
            userDataInfo.setId(userDataInfoVer.getUserId());
            userDataInfoVer.setId(userVerId);
            userDataInfoVer.setVerState(SysStaticDataEnum.VER_STATE.VER_STATE_N);
            iUserDataInfoVerService.updateById(userDataInfoVer);
        }
        if (tenantUserRel.getCarUserType() == SysStaticDataEnum.CAR_USER_TYPE.OWN_CAR) {
            userDataInfo.setAuthState(SysStaticDataEnum.AUTH_STATE.AUTH_STATE2);
        }
        overrideMobilePhoneAndLinkman(userDataInfo.getId(), userDataInfo.getMobilePhone(), userDataInfo.getLinkman());
        iUserDataInfoRecordService.updateById(userDataInfo);

        updateUserSalary(tenantUserRel);
        updateUserLine(tenantUserRel);

        modifyVerDate(tenantUserRel);
    }


    /**
     * ???????????????
     */
    @Override
    public void fail(Long busiId, String desc, Map paramsMap, String token) throws BusinessException {
        LoginInfo user = iUserDataInfoRecordService.getLoginInfoByAccessToken(token);
        TenantUserRel tenantUserRel = iTenantUserRelService.getTenantUserRelByRelId(busiId);
        if (null == tenantUserRel) {
            throw new BusinessException("?????????????????????");
        }

        tenantUserRel.setAuthManId(user.getUserInfoId());
        tenantUserRel.setAuthOrgId(user.getOrgId());
        tenantUserRel.setStateReason(desc);
        tenantUserRel.setHasVer(EnumConsts.HAS_VER_STATE.HAS_VER_YES);
        iTenantUserRelService.updateById(tenantUserRel);

        if (tenantUserRel.getCarUserType() == SysStaticDataEnum.CAR_USER_TYPE.OWN_CAR) {
            UserDataInfoVer ver = iUserDataInfoVerService.getUserDataInfoVerNoTenant(tenantUserRel.getUserId());
            ver.setVerState(SysStaticDataEnum.VER_STATE.VER_STATE_N);
            iUserDataInfoVerService.updateById(ver);
        }
        modifyVerDate(tenantUserRel);
    }

    @Override
    @Transactional
    public boolean doAuditApply(AuditApplyVo auditApplyVo, String token) throws Exception {
        ApplyRecord applyRecord = iApplyRecordService.getById(auditApplyVo.getBusiId());
        if (null == applyRecord) {
            throw new BusinessException("????????????????????????");
        }
        Long driverId = null;
        if (org.apache.commons.lang.StringUtils.isNotBlank(auditApplyVo.getDriverUserId())) {
            driverId = Long.parseLong(auditApplyVo.getDriverUserId());
        }
        auditApplyRecord(auditApplyVo, driverId, token);
        return true;
    }

    @Override
    public Page doQueryBeApplyRecordsNew(ApplyRecordQueryVo vo, Integer pageNum, Integer pageSize, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);

        if (StringUtils.isNotBlank(vo.getCarUserTypes())) {
            StringBuffer sb1 = new StringBuffer();
            String[] split1 = vo.getCarUserTypes().split(",");
            for (String str : split1) {
                sb1.append("'").append(str).append("',");
            }
            vo.setCarUserTypes(sb1.substring(0, sb1.length() - 1));
        }

        StringBuffer sb2 = new StringBuffer();
        String[] split2 = vo.getStates().split(",");
        for (String str : split2) {
            sb2.append("'").append(str).append("',");
        }
        vo.setStates(sb2.substring(0, sb2.length() - 1));

        Page<ApplyRecordVo> list = userDataInfoRecordMapper.doQueryBeApplyRecordsNew(new Page<>(pageNum, pageSize), vo, loginInfo.getTenantId());

        List<ApplyRecordVo> listOut = new ArrayList<ApplyRecordVo>();

        //????????????
        List<ApplyRecordVo> rtnMapList = list.getRecords();
        if (rtnMapList != null && rtnMapList.size() > 0) {
            List<Long> busiIds = new ArrayList<>();
            for (ApplyRecordVo recordVo : rtnMapList) {
                if (SysStaticDataEnum.APPLY_STATE.APPLY_STATE0 == recordVo.getState()) {
                    busiIds.add(recordVo.getId());
                }
            }
            Map<Long, Boolean> hasPermissionMap = null;
            if (!busiIds.isEmpty()) {
                hasPermissionMap = iAuditNodeInstService.isHasPermission(AuditConsts.AUDIT_CODE.AUDIT_CODE_APPLY, busiIds, accessToken);
            }

            for (int i = 0; i < rtnMapList.size(); i++) {
                ApplyRecordVo rtnMap = rtnMapList.get(i);
                if (rtnMap.getState() != null && rtnMap.getState() > -1) {
                    try {
                        rtnMap.setStateName(isysStaticDataService.getSysStaticData("APPLY_STATE", rtnMap.getState() + "").getCodeName());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (rtnMap.getApplyCarUserType() != null && rtnMap.getApplyCarUserType() > -1) {
                    try {
                        rtnMap.setApplyCarUserTypeName(isysStaticDataService.getSysStaticData("DRIVER_TYPE", rtnMap.getApplyCarUserType() + "").getCodeName());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (hasPermissionMap != null && hasPermissionMap.containsKey(rtnMap.getId()) && hasPermissionMap.get(rtnMap.getId())) {
                    rtnMap.setAuditFlg(1);
                } else {
                    rtnMap.setAuditFlg(0);
                }
                listOut.add(rtnMap);
            }
        }
        list.setRecords(listOut);
        return list;
    }

    public void auditApplyRecord(AuditApplyVo auditApplyVo, Long driverUserId, String token) throws Exception {
        ApplyRecord applyRecord = iApplyRecordService.getById(auditApplyVo.getBusiId());
        if (null == applyRecord) {
            throw new BusinessException("????????????????????????");
        }
        applyRecord.setApplyPlateNumbers(auditApplyVo.getPlateNumbers());
        //applyRecord.setApplyPlateNumberList(CommonUtil.convertStringList(auditApplyVo.getPlateNumbers()));
        applyRecord.setApplyDriverUserId(driverUserId);
        auditApplyRecord(applyRecord, auditApplyVo.getDesc(), auditApplyVo.getChooseResult(), applyRecord.getApplyCarUserType(), token);
    }

    /**
     * ????????????????????????
     */
    public void auditApplyRecordWx(AuditApplyVo auditApplyVo, Long driverUserId, String token) throws Exception {
        ApplyRecord applyRecord = iApplyRecordService.getById(auditApplyVo.getBusiId());
        if (null == applyRecord) {
            throw new BusinessException("????????????????????????");
        }
        applyRecord.setApplyPlateNumbers(auditApplyVo.getPlateNumbers());
        applyRecord.setApplyDriverUserId(driverUserId);
//        auditApplyRecord(applyRecord, auditApplyVo.getDesc(), auditApplyVo.getChooseResult(), applyRecord.getApplyCarUserType(), token);
        auditApplyRecordWx(applyRecord, auditApplyVo.getDesc(), auditApplyVo.getChooseResult(), applyRecord.getApplyCarUserType(), token);
    }

    /**
     * ????????????
     * ??????????????????C????????????
     * ?????????????????????C????????????????????????
     * ?????????????????????C????????????????????????
     */
    protected void auditApplyRecord(ApplyRecord applyRecord, String desc, int chooseResult, Integer applyCarUserType, String token) throws Exception {
        LoginInfo loginInfo = iUserDataInfoRecordService.getLoginInfoByAccessToken(token);
        UserDataInfo userDataInfo = iUserDataInfoRecordService.getUserDataInfo(applyRecord.getBusiId());
        SysTenantDef applyTenant = sysTenantDefService.getSysTenantDef(applyRecord.getApplyTenantId(), true);
        SysTenantDef beApplyTenant = sysTenantDefService.getSysTenantDef(applyRecord.getBeApplyTenantId(), true);
        if (null == applyRecord.getBeApplyTenantId() || applyRecord.getBeApplyTenantId() == -1 || beApplyTenant.getState().equals(SysStaticDataEnum.SYS_STATE_DESC.STATE_NO)) {
            //?????????????????????????????????????????????????????????
        } else {
            iAuditSettingService.surePri(AuditConsts.AUDIT_CODE.AUDIT_CODE_APPLY, applyRecord.getId(), desc, chooseResult, null, null, null, token, false);
        }
        updateApplyRecordByChooseResult(applyRecord, desc, chooseResult, loginInfo);
        if (AuditConsts.RESULT.SUCCESS != chooseResult) {
            //?????????????????????????????????????????????
            return;
        }
        //???????????????????????????
        TenantUserRel tenantUserRel = iTenantUserRelService.getAllTenantUserRelByUserId(applyRecord.getBusiId(), applyRecord.getBeApplyTenantId());
//        // ????????????????????????
        if (applyCarUserType == 1) {
            beforeCreateTenantUserRelForAuditSuccess(applyRecord);
        }
        createTenantUserRelAfterAuditSuccess(applyRecord, userDataInfo, beApplyTenant, token);
        afterCreateTenantUserRelForAuditSuccess(tenantUserRel, userDataInfo, beApplyTenant, applyTenant, token, applyCarUserType);
//        // ????????????????????????
        beforeCreateTenantVehicleRelForAuditSuccess(applyRecord, beApplyTenant, applyTenant, applyCarUserType, token);
        createTenantVehicleRelAfterAuditSuccess(applyRecord, beApplyTenant, applyTenant, applyCarUserType, token);

    }

    /**
     * ????????????????????????
     */
    protected void auditApplyRecordWx(ApplyRecord applyRecord, String desc, int chooseResult, Integer applyCarUserType, String token) throws Exception {
        LoginInfo loginInfo = iUserDataInfoRecordService.getLoginInfoByAccessToken(token);
        UserDataInfo userDataInfo = iUserDataInfoRecordService.getUserDataInfo(applyRecord.getBusiId());
        SysTenantDef applyTenant = sysTenantDefService.getSysTenantDef(applyRecord.getApplyTenantId(), true);
        SysTenantDef beApplyTenant = sysTenantDefService.getSysTenantDef(applyRecord.getBeApplyTenantId(), true);

        updateApplyRecordByChooseResult(applyRecord, desc, chooseResult, loginInfo);

        if (AuditConsts.RESULT.SUCCESS != chooseResult) { //?????????????????????????????????????????????
            return;
        }
        //???????????????????????????
        TenantUserRel tenantUserRel = iTenantUserRelService.getAllTenantUserRelByUserId(applyRecord.getBusiId(), applyRecord.getBeApplyTenantId());
        // ????????????????????????
        if (applyCarUserType == 1) {
            beforeCreateTenantUserRelForAuditSuccess(applyRecord);
        }
        createTenantUserRelAfterAuditSuccess(applyRecord, userDataInfo, beApplyTenant, token);
        afterCreateTenantUserRelForAuditSuccess(tenantUserRel, userDataInfo, beApplyTenant, applyTenant, token, applyCarUserType);
        // ????????????????????????
        beforeCreateTenantVehicleRelForAuditSuccess(applyRecord, beApplyTenant, applyTenant, applyCarUserType, token);
        createTenantVehicleRelAfterAuditSuccess(applyRecord, beApplyTenant, applyTenant, applyCarUserType, token);
    }

    protected void createTenantVehicleRelAfterAuditSuccess(ApplyRecord applyRecord, SysTenantDef beApplyTenant,
                                                           SysTenantDef applyTenant, Integer applyCarUserType, String token) throws Exception {
        if (StringUtils.isEmpty(applyRecord.getApplyPlateNumbers())) {
            return;
        }
        List<String> pateNum = CommonUtil.convertStringList(applyRecord.getApplyPlateNumbers());
        for (String plateNumber : pateNum) {
            VehicleDataInfo vehicleDataInfo = iVehicleDataInfoService.getVehicleDataInfo(plateNumber);
            //?????????????????????????????????????????????????????????????????????
            TenantVehicleRel tenantVehicleRel = iTenantVehicleRelService.getTenantVehicleRel(vehicleDataInfo.getId(), applyTenant.getId());
            if (null != tenantVehicleRel) {
                tenantVehicleRel.setVehicleClass(applyRecord.getApplyCarUserType());
                if (needSetBillReceiver(applyCarUserType)) {
                    setBillReceiver(tenantVehicleRel, vehicleDataInfo, beApplyTenant);
                }
                TenantVehicleRelVer tenantVehicleRelVer = new TenantVehicleRelVer();
                BeanUtils.copyProperties(tenantVehicleRelVer, tenantVehicleRel);
                tenantVehicleRelVer.setVerState(SysStaticDataEnum.VER_STATE.VER_STATE_Y);
                tenantVehicleRel.setIsAuth(SysStaticDataEnum.IS_AUTH.IS_AUTH1);//???????????????
                tenantVehicleRelVer.setCreateTime(LocalDateTime.now());
                iTenantVehicleRelVerService.save(tenantVehicleRelVer);
                iTenantVehicleRelService.updateById(tenantVehicleRel);
                continue;
            }

            //????????????
            tenantVehicleRel = initNotApproveTenantVehicle(vehicleDataInfo, applyRecord.getApplyCarUserType(), applyTenant.getId());
            if (needSetBillReceiver(applyCarUserType)) {
                setBillReceiver(tenantVehicleRel, vehicleDataInfo, beApplyTenant);
            }
            iTenantVehicleRelService.save(tenantVehicleRel);
            TenantVehicleRelVer tenantVehicleRelVer = new TenantVehicleRelVer();
            BeanUtil.copyProperties(tenantVehicleRel, tenantVehicleRelVer);
            tenantVehicleRelVer.setVerState(SysStaticDataEnum.VER_STATE.VER_STATE_Y);
            iTenantVehicleRelVerService.save(tenantVehicleRelVer);

            SysUser operator = iSysUserService.getById(applyRecord.getOpId());
            if (null != beApplyTenant) {
                TenantVehicleRel rel = iTenantVehicleRelService.getTenantVehicleRel(vehicleDataInfo.getId(), beApplyTenant.getId());
                saveSysOperLog(SysOperLogConst.BusiCode.Vehicle, SysOperLogConst.OperType.Add, "????????????" + applyTenant.getName(), token, rel.getId(), null);
            }
            saveSysOperLog(SysOperLogConst.BusiCode.Vehicle, SysOperLogConst.OperType.Add, "????????????" + applyTenant.getName(), token, tenantVehicleRel.getId(), null);
            //??????????????????
            Map iMap = new HashMap();
            iMap.put("svName", "vehicleTF");
            iMap.put("tenantVehicleRelId", tenantVehicleRel.getId());
            boolean bool = iAuditService.startProcess(AuditConsts.AUDIT_CODE.AUDIT_CODE_VEHICLE, tenantVehicleRel.getId(), SysOperLogConst.BusiCode.Vehicle, iMap, token);
            if (!bool) {
                throw new BusinessException("???????????????????????????");
            }
        }
    }

    protected boolean needSetBillReceiver(Integer applyCarUserType) {
        if (applyCarUserType == 4) {
            return true;
        } else if (applyCarUserType == 2) {
            return true;
        }
        return false;
    }

    protected TenantVehicleRel initNotApproveTenantVehicle(VehicleDataInfo vehicleDataInfo, int vehicleClass, Long tenantId) {
        TenantVehicleRel tenantVehicleRel = new TenantVehicleRel();
        tenantVehicleRel.setVehicleCode(vehicleDataInfo.getId());
        tenantVehicleRel.setTenantId(tenantId);
        tenantVehicleRel.setDriverUserId(vehicleDataInfo.getDriverUserId());
        tenantVehicleRel.setVehicleClass(vehicleClass);
        tenantVehicleRel.setPlateNumber(vehicleDataInfo.getPlateNumber());
        tenantVehicleRel.setAuthState(SysStaticDataEnum.AUTH_STATE.AUTH_STATE1);
        tenantVehicleRel.setIsAuth(SysStaticDataEnum.IS_AUTH.IS_AUTH1);
        tenantVehicleRel.setOperCerti(vehicleDataInfo.getOperCerti());
        tenantVehicleRel.setDrivingLicense(vehicleDataInfo.getDrivingLicense());
        tenantVehicleRel.setCreateTime(LocalDateTime.now());
        return tenantVehicleRel;
    }

    protected void setBillReceiver(TenantVehicleRel tenantVehicleRel, VehicleDataInfo vehicleDataInfo, SysTenantDef beApplyTenant) {
        UserDataInfo userDataInfo;
        if (null != beApplyTenant) {
            userDataInfo = iUserDataInfoRecordService.getUserDataInfo(beApplyTenant.getAdminUser());
        } else {
            userDataInfo = iUserDataInfoRecordService.getUserDataInfo(vehicleDataInfo.getDriverUserId());
        }
        tenantVehicleRel.setBillReceiverName(userDataInfo.getLinkman());
        tenantVehicleRel.setBillReceiverUserId(userDataInfo.getId());
        tenantVehicleRel.setBillReceiverMobile(userDataInfo.getMobilePhone());
    }

    void beforeCreateTenantVehicleRelForAuditSuccess(ApplyRecord applyRecord, SysTenantDef beApplyTenant, SysTenantDef applyTenant, Integer carUserType, String token) throws Exception {
        if (carUserType == 1) {
            //?????????????????????C????????????
            if (null != beApplyTenant) {
                if (beApplyTenant.getState().equals(SysStaticDataEnum.SYS_STATE_DESC.STATE_NO)) {
                    //????????????????????????????????????
                    iVehicleDataInfoService.doTransferOwnCar(applyRecord.getBusiId(), beApplyTenant.getId(), applyTenant.getId(), token);
                } else {
                    //?????????????????????????????????????????????????????????????????????????????????
                    //???????????????????????????????????????????????????????????????
                    iTenantVehicleRelService.changeVehicleDriver(applyRecord.getBeApplyTenantId(), applyRecord.getBusiId(), applyRecord.getApplyDriverUserId());
                }
                //??????????????????????????????
                iTenantVehicleRelService.clearCopilotDriverAndFollowDriver(applyRecord.getBusiId());
            } else {
                //????????????????????????tenantId=1?????????
                if (!StringUtils.isEmpty(applyRecord.getApplyPlateNumbers())) {
                    List<String> pateNum = CommonUtil.convertStringList(applyRecord.getApplyPlateNumbers());
                    for (String plateNumber : pateNum) {
                        iTenantVehicleRelService.removePtVehicle(plateNumber, null);
                    }
                }
            }
        } else if (carUserType == 4) {
            if (StringUtils.isEmpty(applyRecord.getApplyPlateNumbers())) {
                return;
            }
            List<String> pateNum = CommonUtil.convertStringList(applyRecord.getApplyPlateNumbers());
            for (String plateNumber : pateNum) {
                iTenantVehicleRelService.removePtVehicle(plateNumber, null);
            }
        }
    }

    /**
     * ?????????????????????????????????????????????????????????????????????
     */
    @Transactional
    protected void afterCreateTenantUserRelForAuditSuccess(TenantUserRel tenantUserRel, UserDataInfo userDataInfo, SysTenantDef beApplyTenant, SysTenantDef applyTenant, String token, Integer applyCarUserType) throws Exception {
        /**
         * ???????????????????????????
         * @param tenantUserRel  ?????????????????????
         * @param userDataInfo ????????????
         * @param beApplyTenant  ??????????????????
         * @param applyTenant ???????????????
         */
        if (applyCarUserType == 1) {
            //??????????????????????????????
            if (null != tenantUserRel) {
                TenantUserRelVer tenantUserRelVer = new TenantUserRelVer();
                BeanUtil.copyProperties(tenantUserRel, tenantUserRelVer);
                tenantUserRelVer.setRelId(tenantUserRel.getId());
                tenantUserRelVer.setVerState(SysStaticDataEnum.VER_STATE.VER_STATE_HIS);
                iTenantUserRelVerService.save(tenantUserRelVer);
                iTenantUserRelService.removeById(tenantUserRel.getId());
            }

            UserDataInfoVer userDataInfoVer = iUserDataInfoVerService.getUserDataInfoVerNoTenant(userDataInfo.getId());
            if (userDataInfoVer != null) {
                userDataInfoVer.setVerState(SysStaticDataEnum.VER_STATE.VER_STATE_N);
                iUserDataInfoVerService.updateById(userDataInfoVer);
            }
            userDataInfo.setTenantId(applyTenant.getId());
            iUserDataInfoRecordService.updateById(userDataInfo);

            //?????????????????????????????????
            if (null != tenantUserRel) {
                kickUserEnds(tenantUserRel.getUserId(), "APP");
                saveSysOperLog(SysOperLogConst.BusiCode.Driver, SysOperLogConst.OperType.Remove, "????????????", token, tenantUserRel.getId(), null);
            }
        }
        if (null != tenantUserRel) {
            saveSysOperLog(SysOperLogConst.BusiCode.Driver, SysOperLogConst.OperType.Add, "???????????????" + applyTenant.getName(), token, tenantUserRel.getId(), null);
        }
    }

    private boolean kickUserEnds(long userId, String channleType) {
        Set<Object> tokens = redisUtil.sgetAll("USER_TOKEN_MAP" + userId);
        if (!tokens.isEmpty()) {
            Iterator var4 = tokens.iterator();
            while (var4.hasNext()) {
                String tokenId = (String) var4.next();
                boolean isrem = false;
                if (org.apache.commons.lang.StringUtils.isNotEmpty(tokenId) && tokenId.startsWith(channleType)) {
                    isrem = true;
                }
                if (isrem) {
                    redisUtil.del("APP_TOKEN_" + tokenId);
                    redisUtil.sRemove("USER_TOKEN_MAP" + userId, new String[]{tokenId});
                }
            }
        }
        return true;
    }

    /**
     * ???????????????????????????????????????????????????????????????
     */
    void createTenantUserRelAfterAuditSuccess(ApplyRecord applyRecord, UserDataInfo userDataInfo, SysTenantDef beApplyTenant, String token) throws Exception {
        //???????????????????????????????????????????????????????????????????????????
        TenantUserRel tenantUserRel = iTenantUserRelService.getAllTenantUserRelByUserId(applyRecord.getBusiId(), applyRecord.getApplyTenantId());
        if (null != tenantUserRel) {
            tenantUserRel.setCarUserType(applyRecord.getApplyCarUserType());
            iTenantUserRelService.saveOrUpdate(tenantUserRel);
            return;
        }

        //????????????????????????????????????
        TenantUserRel tenantUserRelNew = initNotApproveTenantUserRel(applyRecord.getBusiId(), applyRecord.getApplyCarUserType(), applyRecord.getApplyTenantId(), applyRecord.getBeApplyTenantId());
        iTenantUserRelService.save(tenantUserRelNew);

        TenantUserRelVer tenantUserRelVer = new TenantUserRelVer();
        BeanUtil.copyProperties(tenantUserRelNew, tenantUserRelVer);
        tenantUserRelVer.setRelId(tenantUserRelNew.getId());
        tenantUserRelVer.setVerState(SysStaticDataEnum.VER_STATE.VER_STATE_Y);
        iTenantUserRelVerService.save(tenantUserRelVer);

        SysUser operator = iSysUserService.getById(applyRecord.getOpId());
        if (null != beApplyTenant) {
            saveSysOperLog(SysOperLogConst.BusiCode.Driver, SysOperLogConst.OperType.Add,
                    "????????????????????????????????????" + beApplyTenant.getName() + "???", token, tenantUserRelNew.getId(), applyRecord.getApplyTenantId());
        } else {
            saveSysOperLog(SysOperLogConst.BusiCode.Driver, SysOperLogConst.OperType.Add,
                    "??????????????????", token, tenantUserRelNew.getId(), applyRecord.getApplyTenantId());
        }
        boolean bool = iAuditService.startProcess(AuditConsts.AUDIT_CODE.AUDIT_CODE_USER, tenantUserRelNew.getId(), SysOperLogConst.BusiCode.Driver, new HashMap<>(), token, applyRecord.getApplyTenantId());
        if (!bool) {
            throw new BusinessException("???????????????????????????");
        }
    }

    TenantUserRel initNotApproveTenantUserRel(Long userId, int carUserType, Long tenantId, Long sourceTenantId) {
        TenantUserRel tenantUserRel = new TenantUserRel();
        tenantUserRel.setUserId(userId);
        tenantUserRel.setCarUserType(carUserType);
        tenantUserRel.setTenantId(tenantId);
        tenantUserRel.setHasVer(EnumConsts.HAS_VER_STATE.HAS_VER_NO);
        tenantUserRel.setState(SysStaticDataEnum.SYS_USER_STATE.AUDIT_NOT);
        tenantUserRel.setSourceTenantId(sourceTenantId);
        return tenantUserRel;
    }


    private void updateApplyRecordByChooseResult(ApplyRecord applyRecord, String desc, int chooseResult, LoginInfo loginInfo) {
        applyRecord.setAuditRemark(desc);
        applyRecord.setAuditOpId(loginInfo.getId());
        applyRecord.setAuditDate(LocalDateTime.now());
        applyRecord.setState(AuditConsts.RESULT.SUCCESS == chooseResult ? SysStaticDataEnum.APPLY_STATE.APPLY_STATE1 : SysStaticDataEnum.APPLY_STATE.APPLY_STATE2);
        iApplyRecordService.updateById(applyRecord);
    }

    @Transactional
    void beforeCreateTenantUserRelForAuditSuccess(ApplyRecord applyRecord) throws Exception {
        TenantUserRel tenantUserRel = iTenantUserRelService.getAllTenantUserRelByUserId(applyRecord.getBusiId(), applyRecord.getApplyTenantId());
        if (null != tenantUserRel) {
            TenantUserRelVer tenantUserRelVer = new TenantUserRelVer();
            BeanUtil.copyProperties(tenantUserRel, tenantUserRelVer);
            tenantUserRelVer.setRelId(tenantUserRel.getId());
            if (tenantUserRel.getState() == SysStaticDataEnum.SYS_USER_STATE.AUDIT_APPROVE) {
                tenantUserRelVer.setVerState(SysStaticDataEnum.VER_STATE.VER_STATE_HIS);
            }
            iTenantUserRelVerService.save(tenantUserRelVer);
            iTenantUserRelService.removeById(tenantUserRel.getId());

        }
    }


    private void modifyVerDate(TenantUserRel tenantUserRel) throws BusinessException {
        List<TenantUserRelVer> tenantUserRelVerList = iTenantUserRelVerService.getTenantUserRelVer(tenantUserRel.getUserId(), tenantUserRel.getTenantId(), SysStaticDataEnum.VER_STATE.VER_STATE_Y);
        if (org.apache.commons.collections.CollectionUtils.isNotEmpty(tenantUserRelVerList)) {
            for (TenantUserRelVer userRelVer : tenantUserRelVerList) {
                userRelVer.setVerState(SysStaticDataEnum.VER_STATE.VER_STATE_N);
                iTenantUserRelVerService.updateById(userRelVer);
            }
        }

        //??????????????????
        List<TenantUserSalaryRelVer> userSalaryRelVerList = iTenantUserSalaryRelVerService.getTenantUserSalaryRelVer(tenantUserRel.getId(), SysStaticDataEnum.VER_STATE.VER_STATE_Y);
        if (org.apache.commons.collections.CollectionUtils.isNotEmpty(userSalaryRelVerList)) {
            for (TenantUserSalaryRelVer ver : userSalaryRelVerList) {
                ver.setVerState(SysStaticDataEnum.VER_STATE.VER_STATE_N);
                iTenantUserSalaryRelVerService.updateById(ver);
            }
        }

        List<UserSalaryInfoVer> userSalaryInfoVerList = iUserSalaryInfoVerService.getUserSalaryInfoVer(tenantUserRel.getUserId(), SysStaticDataEnum.VER_STATE.VER_STATE_Y);
        if (org.apache.commons.collections.CollectionUtils.isNotEmpty(userSalaryInfoVerList)) {
            for (UserSalaryInfoVer ver : userSalaryInfoVerList) {
                ver.setVerState(SysStaticDataEnum.VER_STATE.VER_STATE_N);
                iUserSalaryInfoVerService.updateById(ver);
            }
        }

        List<UserLineRelVer> userLineRelVerList = iUserLineRelVerService.getUserLineRelVer(tenantUserRel.getUserId(), tenantUserRel.getTenantId(), SysStaticDataEnum.VER_STATE.VER_STATE_Y);
        if (org.apache.commons.collections.CollectionUtils.isNotEmpty(userLineRelVerList)) {
            for (UserLineRelVer ver : userLineRelVerList) {
                ver.setVerState(SysStaticDataEnum.VER_STATE.VER_STATE_N);
                iUserLineRelVerService.updateById(ver);
            }
        }
    }


    /**
     * ???????????????????????????????????????????????????????????????????????????
     */
    public void updateUserLine(TenantUserRel tenantUserRel) throws BusinessException {
        List<UserLineRelVer> verList = iUserLineRelVerService.getUserLineRelVer(tenantUserRel.getUserId(), tenantUserRel.getTenantId(), SysStaticDataEnum.VER_STATE.VER_STATE_Y);
        if (org.apache.commons.collections.CollectionUtils.isNotEmpty(verList)) {
            List<UserLineRel> userLineRelList = iUserLineRelService.getUserLineRelByUserId(tenantUserRel.getUserId(), tenantUserRel.getTenantId());
            if (org.apache.commons.collections.CollectionUtils.isNotEmpty(userLineRelList)) {
                for (UserLineRel lineRel : userLineRelList) {
                    iUserLineRelService.removeById(lineRel);
                }
            }

            for (UserLineRelVer userLineRelVer : verList) {
                UserLineRel userLineRel = new UserLineRel();
                BeanUtil.copyProperties(userLineRelVer, userLineRel);
                userLineRel.setId(userLineRelVer.getRelId());
                iUserLineRelService.save(userLineRel);
            }
        } else {
            QueryWrapper<UserLineRel> userLineRelQueryWrapper = new QueryWrapper<>();
            userLineRelQueryWrapper.eq("user_id", tenantUserRel.getUserId());
            iUserLineRelService.remove(userLineRelQueryWrapper);
        }
    }

    /**
     * ???????????????????????????????????????????????????????????????????????????
     */
    public void updateUserSalary(TenantUserRel tenantUserRel) throws BusinessException {
        TenantUserSalaryRelVer tenantUserSalaryRelVer = iTenantUserSalaryRelVerService.getTenantUserSalaryRelVer(tenantUserRel.getId());
        if (null != tenantUserSalaryRelVer) {
            TenantUserSalaryRel tenantUserSalaryRel = iTenantUserSalaryRelService.getTenantUserRalaryRelByUserId(tenantUserRel.getUserId(), tenantUserRel.getTenantId());
            if (null == tenantUserSalaryRel) {
                tenantUserSalaryRel = new TenantUserSalaryRel();
            }
            BeanUtil.copyProperties(tenantUserSalaryRelVer, tenantUserSalaryRel);
            tenantUserSalaryRel.setId(tenantUserSalaryRelVer.getSalaryId());
//            if(tenantUserSalaryRelVer.getRelId() != null) {
//               tenantUserSalaryRel.setId(tenantUserSalaryRelVer.getRelId());
//            }
            iTenantUserSalaryRelService.saveOrUpdate(tenantUserSalaryRel);
        }

        List<UserSalaryInfoVer> userSalaryInfoVerList = iUserSalaryInfoVerService.getUserSalaryInfoVer(tenantUserRel.getUserId(), SysStaticDataEnum.VER_STATE.VER_STATE_Y);
        if (org.apache.commons.collections.CollectionUtils.isNotEmpty(userSalaryInfoVerList)) {
            List<UserSalaryInfo> userSalaryInfoList = iUserSalaryInfoService.getuserSalarInfo(tenantUserRel.getUserId());
            if (org.apache.commons.collections.CollectionUtils.isNotEmpty(userSalaryInfoList)) {
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


    /**
     * ?????????????????????????????????????????????????????????????????????
     */
    public void removeUserLineRel(Long userId, Long tenantId, LoginInfo loginInfo) throws BusinessException {
        List<UserLineRelVer> verList = iUserLineRelVerService.getUserLineRelVer(userId, tenantId, SysStaticDataEnum.VER_STATE.VER_STATE_Y);
        if (org.apache.commons.collections.CollectionUtils.isNotEmpty(verList)) {
            for (UserLineRelVer ver : verList) {
                ver.setVerState(SysStaticDataEnum.VER_STATE.VER_STATE_N);
                iUserLineRelVerService.updateById(ver);
            }
        }
        List<UserLineRel> userLineRelList = iUserLineRelService.getUserLineRelByUserId(userId, tenantId);
        if (org.apache.commons.collections.CollectionUtils.isNotEmpty(userLineRelList)) {
            for (UserLineRel userLineRel : userLineRelList) {
                UserLineRelVer userLineRelVer = new UserLineRelVer();
                try {
                    BeanUtils.copyProperties(userLineRel, userLineRelVer);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                userLineRelVer.setRelId(userLineRel.getId());
                userLineRelVer.setUpdateOpId(loginInfo.getUserInfoId());
                userLineRelVer.setUpdateDate(LocalDateTime.now());
                userLineRelVer.setUpdateTime(LocalDateTime.now());
                userLineRelVer.setVerState(SysStaticDataEnum.VER_STATE.VER_STATE_HIS);
                iUserLineRelVerService.save(userLineRelVer);
                iUserLineRelService.removeById(userLineRel);
            }
        }

    }

    /**
     * ?????????????????????????????????????????????????????????????????????
     */
    public void removeTenantUserSalaryRel(TenantUserRel tenantUserRel) throws BusinessException {

        List<TenantUserSalaryRelVer> verList = iTenantUserSalaryRelVerService.getTenantUserSalaryRelVer(tenantUserRel.getId(), SysStaticDataEnum.VER_STATE.VER_STATE_Y);
        if (org.apache.commons.collections.CollectionUtils.isNotEmpty(verList)) {
            for (TenantUserSalaryRelVer ver : verList) {
                ver.setVerState(SysStaticDataEnum.VER_STATE.VER_STATE_N);
                iTenantUserSalaryRelVerService.updateById(ver);
            }
        }
        List<UserSalaryInfoVer> userSalaryInfoVerList = iUserSalaryInfoVerService.getUserSalaryInfoVer(tenantUserRel.getUserId(), SysStaticDataEnum.VER_STATE.VER_STATE_Y);
        if (org.apache.commons.collections.CollectionUtils.isNotEmpty(userSalaryInfoVerList)) {
            for (UserSalaryInfoVer ver : userSalaryInfoVerList) {
                ver.setVerState(SysStaticDataEnum.VER_STATE.VER_STATE_N);
                iUserSalaryInfoVerService.updateById(ver);
            }
        }
        TenantUserSalaryRel salary = iTenantUserSalaryRelService.getTenantUserRalaryRelByRelId(tenantUserRel.getId());
        if (salary != null) {
            TenantUserSalaryRelVer salaryRelVer = new TenantUserSalaryRelVer();
            BeanUtil.copyProperties(salary, salaryRelVer);
            salaryRelVer.setVerState(SysStaticDataEnum.VER_STATE.VER_STATE_HIS);
            iTenantUserSalaryRelVerService.save(salaryRelVer);
            iTenantUserSalaryRelService.removeById(salary);
            if (salary.getSalaryPattern() != null
                    && (salary.getSalaryPattern() == SysStaticDataEnum.SALARY_PATTERN.MILEAGE
                    || salary.getSalaryPattern() == SysStaticDataEnum.SALARY_PATTERN.TIMES)) {
                List<UserSalaryInfo> salaryInfos = iUserSalaryInfoService.getuserSalarInfo(tenantUserRel.getUserId());
                //??????????????????
                if (salaryInfos != null && !salaryInfos.isEmpty()) {
                    for (UserSalaryInfo salaryInfo : salaryInfos) {
                        UserSalaryInfoVer userSalaryInfoVer = new UserSalaryInfoVer();
                        try {
                            BeanUtils.copyProperties(salaryInfo, userSalaryInfoVer);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        userSalaryInfoVer.setMId(salaryInfo.getId());
                        userSalaryInfoVer.setVerState(SysStaticDataEnum.VER_STATE.VER_STATE_HIS);
                        iUserSalaryInfoVerService.save(userSalaryInfoVer);
                        iUserSalaryInfoService.removeById(salaryInfo);
                    }
                }
            }

        }
    }


    private void afterSaveApplyRecord(ApplyRecord applyRecord, String accessToken) {
        if (applyRecord.getApplyCarUserType() == 3) {
            applyRecord.setState(SysStaticDataEnum.APPLY_STATE.APPLY_STATE1);
            applyRecord.setAuditRemark("?????????????????????????????????");
            iApplyRecordService.saveOrUpdate(applyRecord);
            //???????????????????????????
            TenantUserRel tenantUserRelNew = iTenantUserRelService.getAllTenantUserRelByUserId(applyRecord.getBusiId(), applyRecord.getApplyTenantId());
            if (null == tenantUserRelNew) {
                tenantUserRelNew = initApproveTenantUserRel(applyRecord.getBusiId(), applyRecord.getApplyCarUserType(), applyRecord.getApplyTenantId(), applyRecord.getBeApplyTenantId());
            }
            tenantUserRelNew.setCarUserType(applyRecord.getApplyCarUserType());
            iTenantUserRelService.saveOrUpdate(tenantUserRelNew);
            iOrderAccountService.queryOrderAccount(tenantUserRelNew.getUserId(), "0", 0, tenantUserRelNew.getTenantId(), "0", SysStaticDataEnum.USER_TYPE.DRIVER_USER);
            SysTenantDef applyTenant = sysTenantDefService.getSysTenantDef(applyRecord.getApplyTenantId(), true);
            SysTenantDef beApplyTenant = sysTenantDefService.getSysTenantDef(applyRecord.getBeApplyTenantId(), true);
            SysUser sysUser = iSysUserService.getById(applyRecord.getOpId());
            if (null != beApplyTenant) {
//                iSysOperLogService.saveSysOperLog(SysOperLogConst.BusiCode.Driver, tenantUserRelNew.getId(), SysOperLogConst.OperType.Add,
//                        "????????????????????????????????????" + beApplyTenant.getName() + "???", applyRecord.getOpId(), applyRecord.getApplyTenantId());
                saveSysOperLog(SysOperLogConst.BusiCode.Driver, SysOperLogConst.OperType.Add, "????????????????????????????????????" + beApplyTenant.getName() + "???", accessToken, tenantUserRelNew.getId(), applyRecord.getApplyTenantId());
            } else {
//                iSysOperLogService.saveSysOperLog(SysOperLogConst.BusiCode.Driver, tenantUserRelNew.getId(), SysOperLogConst.OperType.Add,
//                        "????????????????????????????????????", applyRecord.getOpId(), applyRecord.getApplyTenantId());
                saveSysOperLog(SysOperLogConst.BusiCode.Driver, SysOperLogConst.OperType.Add, "????????????????????????????????????", accessToken, tenantUserRelNew.getId(), applyRecord.getApplyTenantId());
            }
            //???????????????????????????????????????????????????????????????????????????

            if (StringUtils.isNotEmpty(applyRecord.getApplyPlateNumbers())) {
                List<String> applyPlateNumbers = Arrays.asList(applyRecord.getApplyPlateNumbers().split(","));
                for (String plateNumber : applyPlateNumbers) {
                    VehicleDataInfo vehicleDataInfo = iVehicleDataInfoService.getVehicleDataInfo(plateNumber);
                    TenantVehicleRel tenantVehicleRel = iTenantVehicleRelService.getTenantVehicleRel(vehicleDataInfo.getId(), applyTenant.getId());
                    if (null == tenantVehicleRel) {
                        tenantVehicleRel = initApproveTenantVehilce(vehicleDataInfo, SysStaticDataEnum.CAR_USER_TYPE.SOCIAL_CAR, applyTenant.getId());
                    }
                    tenantVehicleRel.setDriverUserId(applyRecord.getBusiId());
                    tenantVehicleRel.setVehicleClass(SysStaticDataEnum.CAR_USER_TYPE.SOCIAL_CAR);
                    iTenantVehicleRelService.saveOrUpdate(tenantVehicleRel);

                    TenantVehicleRelVer tenantVehicleRelVer = new TenantVehicleRelVer();
                    BeanUtil.copyProperties(tenantVehicleRel, tenantVehicleRelVer);
                    tenantVehicleRelVer.setVerState(1);
                    iTenantVehicleRelVerService.save(tenantVehicleRelVer);
                    TenantVehicleRel rel = iTenantVehicleRelService.getZYVehicleByVehicleCode(vehicleDataInfo.getId());
                    if (null != rel) {
                        //    iSysOperLogService.saveSysOperLog(SysOperLogConst.BusiCode.Vehicle, rel.getId(), SysOperLogConst.OperType.Add, "???????????????" + applyTenant.getName() + "??????????????????", rel.getTenantId());
                        saveSysOperLog(SysOperLogConst.BusiCode.Vehicle, SysOperLogConst.OperType.Add, "???????????????" + applyTenant.getName() + "??????????????????", accessToken, rel.getId(), rel.getTenantId());
                    }
                    //iSysOperLogService.saveSysOperLog(SysOperLogConst.BusiCode.Vehicle, tenantVehicleRel.getId(), SysOperLogConst.OperType.Add, "????????????" + applyTenant.getName(), applyTenant.getId());
                    saveSysOperLog(SysOperLogConst.BusiCode.Vehicle, SysOperLogConst.OperType.Add, "????????????" + applyTenant.getName(), accessToken, tenantVehicleRel.getId(), applyTenant.getId());
                }
            }
        } else {
            UserDataInfo userDataInfo = iUserDataInfoRecordService.getUserDataInfo(applyRecord.getBusiId());
            SysTenantDef applyTenant = sysTenantDefService.getSysTenantDef(applyRecord.getApplyTenantId(), true);
            SysTenantDef beApplyTenant = sysTenantDefService.getSysTenantDef(applyRecord.getBeApplyTenantId(), true);
            if (null == applyRecord.getBeApplyTenantId() || applyRecord.getBeApplyTenantId() == -1 || beApplyTenant.getState().equals(SysStaticDataEnum.SYS_STATE_DESC.STATE_NO)) {
                long templateId = EnumConsts.SmsTemplate.ADD_DRIVER_NO_TENANT;
                try {
                    SysSmsSend sysSmsSend = new SysSmsSend();
                    Map map = new HashMap();
                    map.put("userName", userDataInfo.getLinkman());
                    map.put("tenantName", applyTenant.getName());
                    //??????id
                    sysSmsSend.setTemplateId(templateId);
                    //?????????
                    sysSmsSend.setBillId(userDataInfo.getMobilePhone());
                    //????????????
                    sysSmsSend.setObjType(String.valueOf(SysStaticDataEnum.OBJ_TYPE.TENANT_INVITE));
                    //???????????????
                    sysSmsSend.setParamMap(map);
                    sysSmsSend.setSmsType(SysStaticDataEnum.SMS_TYPE.INVITE_INFO);
                    sysSmsSend.setObjId(String.valueOf(applyRecord.getId()));
                    iSysSmsSendService.sendSms(sysSmsSend);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                if (null == beApplyTenant) {
//                    logger.error("?????????????????????????????????????????????,beApplyTenantId:{}, driverUserId:{}",applyRecord.getBeApplyTenantId(), applyRecord.getBusiId());
                    throw new BusinessException("?????????????????????????????????????????????");
                }
                UserDataInfo adminUser = iUserDataInfoRecordService.getUserDataInfo(beApplyTenant.getAdminUser());
                Map map = new HashMap();
                map.put("userName", adminUser.getMobilePhone());
                map.put("tenantName", applyTenant.getName());
                map.put("Driver", userDataInfo.getLinkman() + ":" + userDataInfo.getMobilePhone());
                long templateId = EnumConsts.SmsTemplate.ADD_DRIVER_HAVE_TENANT;
                SysSmsSend sysSmsSend = new SysSmsSend();
                //??????id
                sysSmsSend.setTemplateId(templateId);
                //?????????
                sysSmsSend.setBillId(adminUser.getMobilePhone());
                //????????????
                sysSmsSend.setObjType(String.valueOf(SysStaticDataEnum.OBJ_TYPE.TENANT_INVITE));
                //???????????????
                sysSmsSend.setParamMap(map);
                sysSmsSend.setSmsType(SysStaticDataEnum.SMS_TYPE.INVITE_INFO);
                sysSmsSend.setObjId(String.valueOf(applyRecord.getId()));
                iSysSmsSendService.sendSms(sysSmsSend);
                boolean bool = iAuditService.startProcess(AuditConsts.AUDIT_CODE.AUDIT_CODE_APPLY, applyRecord.getId(), SysOperLogConst.BusiCode.Driver, new HashMap<>(), accessToken, applyRecord.getBeApplyTenantId());
                if (!bool) {
                    throw new BusinessException("???????????????????????????");
                }
            }
        }


    }


    private TenantVehicleRel initApproveTenantVehilce(VehicleDataInfo vehicleDataInfo, int vehicleClass, Long tenantId) {
        TenantVehicleRel tenantVehicleRel = new TenantVehicleRel();
        tenantVehicleRel.setVehicleCode(vehicleDataInfo.getId());
        tenantVehicleRel.setTenantId(tenantId);
        tenantVehicleRel.setDriverUserId(vehicleDataInfo.getDriverUserId());
        tenantVehicleRel.setVehicleClass(vehicleClass);
        tenantVehicleRel.setPlateNumber(vehicleDataInfo.getPlateNumber());
        tenantVehicleRel.setAuthState(SysStaticDataEnum.AUTH_STATE.AUTH_STATE2);
        tenantVehicleRel.setIsAuth(SysStaticDataEnum.IS_AUTH.IS_AUTH0);
        tenantVehicleRel.setOperCerti(vehicleDataInfo.getOperCerti());
        tenantVehicleRel.setDrivingLicense(vehicleDataInfo.getDrivingLicense());
        tenantVehicleRel.setCreateTime(LocalDateTime.now());
        return tenantVehicleRel;
    }


    /**
     * ?????????????????????????????????????????????
     */
    private TenantUserRel initApproveTenantUserRel(Long userId, int carUserType, Long tenantId, Long sourceTenantId) {
        TenantUserRel tenantUserRel = new TenantUserRel();
        tenantUserRel.setUserId(userId);
        tenantUserRel.setCarUserType(carUserType);
        tenantUserRel.setTenantId(tenantId);
        tenantUserRel.setHasVer(EnumConsts.HAS_VER_STATE.HAS_VER_YES);
        tenantUserRel.setState(SysStaticDataEnum.SYS_USER_STATE.AUDIT_APPROVE);
        tenantUserRel.setSourceTenantId(sourceTenantId);
        return tenantUserRel;
    }


    private void updateReadState(long applyId) {
        ApplyRecord applyRecord = iApplyRecordService.getById(applyId);
        if (applyRecord != null && applyRecord.getState() != 0 && applyRecord.getState() != 3 && applyRecord.getReadState() == 0) {
            applyRecord.setReadState(1);
            iApplyRecordService.updateById(applyRecord);
        }
    }

    private void afterUpdateTenantUserRelVer(TenantUserRel tenantUserRel, int carUserType, String accessToken) {
        if (carUserType == 1) {
            if (null == tenantUserRel || tenantUserRel.getState() == SysStaticDataEnum.AUTH_STATE.AUTH_STATE1) {
                return;
            }
//            if (null == tenantUserRel) {
//                return;
//            }
            //     iTenantUserRelService.updateUserSalary(tenantUserRel);
            //    iTenantUserRelService.updateUserLine(tenantUserRel);
        } else if (carUserType == 2) {
            if (null == tenantUserRel || tenantUserRel.getState() == SysStaticDataEnum.AUTH_STATE.AUTH_STATE1) {
                return;
            }
            iTenantUserRelService.updateUserLine(tenantUserRel);
        }
    }


    void afterUpdateTenantUserRel(TenantUserRel tenantUserRel, UserDataInfo userDataInfo, DoAddDriverVo doAddDriverVo, String accessToken) {
        if (doAddDriverVo.getCarUserType().equals("1")) {
            if (null == tenantUserRel) {
                return;
            }
            //??????????????????
            List<TenantUserSalaryRelVer> verList = iTenantUserSalaryRelVerService.getTenantUserSalaryRelVer(tenantUserRel.getId(), SysStaticDataEnum.VER_STATE.VER_STATE_Y);
            if (org.apache.commons.collections.CollectionUtils.isNotEmpty(verList)) {
                for (TenantUserSalaryRelVer ver : verList) {
                    ver.setVerState(SysStaticDataEnum.VER_STATE.VER_STATE_N);
                    iTenantUserSalaryRelVerService.updateById(ver);
                }
            }
            TenantUserSalaryRel tenantUserSalaryRel = iTenantUserSalaryRelService.getTenantUserRalaryRelByUserId(tenantUserRel.getUserId(), tenantUserRel.getTenantId());
            TenantUserSalaryRelVer tenantUserSalaryRelVer = new TenantUserSalaryRelVer();
            if (null == tenantUserSalaryRel) {
                tenantUserSalaryRelVer.setRelId(tenantUserRel.getId());
                tenantUserSalaryRelVer.setUserId(tenantUserRel.getUserId());
            } else {
                BeanUtil.copyProperties(tenantUserSalaryRel, tenantUserSalaryRelVer);
                tenantUserSalaryRelVer.setSalaryId(tenantUserSalaryRel.getId());
            }
            tenantUserSalaryRelVer.setVerState(SysStaticDataEnum.VER_STATE.VER_STATE_Y);
            BeanUtil.copyProperties(doAddDriverVo, tenantUserSalaryRelVer, CopyOptions.create().setIgnoreNullValue(true).setIgnoreError(true));
            tenantUserSalaryRelVer.setTenantId(tenantUserRel.getTenantId());
            tenantUserSalaryRelVer.setCreateTime(LocalDateTime.now());
            tenantUserSalaryRelVer.setUpdateTime(LocalDateTime.now());
            iTenantUserSalaryRelVerService.save(tenantUserSalaryRelVer);
            List<UserSalaryInfoVer> userSalaryInfoVerList = iUserSalaryInfoVerService.getUserSalaryInfoVer(tenantUserRel.getUserId(), SysStaticDataEnum.VER_STATE.VER_STATE_Y);
            if (org.apache.commons.collections.CollectionUtils.isNotEmpty(userSalaryInfoVerList)) {
                for (UserSalaryInfoVer ver : userSalaryInfoVerList) {
                    ver.setVerState(SysStaticDataEnum.VER_STATE.VER_STATE_N);
                    iUserSalaryInfoVerService.updateById(ver);
                }
            }
            if (doAddDriverVo.getSalaryPattern() == SysStaticDataEnum.SALARY_PATTERN.MILEAGE
                    || doAddDriverVo.getSalaryPattern() == SysStaticDataEnum.SALARY_PATTERN.TIMES) {//????????????
                List<UserSalaryInfoVer> list = doAddDriverVo.getUserSalaryInfoVerList();
                if (list != null) {
                    for (UserSalaryInfoVer userSalaryInfoVer : list) {
                        //ver???
                        if (userSalaryInfoVer.getMId() == null) {
                            userSalaryInfoVer.setMId(-1L);
                            userSalaryInfoVer.setSalaryPattern(doAddDriverVo.getSalaryPattern());
                            userSalaryInfoVer.setUserId(userDataInfo.getId());
                            userSalaryInfoVer.setUserName(userDataInfo.getLinkman());
                        }
                        if (userSalaryInfoVer.getPrice() != null) {
                            Double priceDouble = userSalaryInfoVer.getPrice().doubleValue();
                            userSalaryInfoVer.setPrice(priceDouble);
                        }
                        userSalaryInfoVer.setVerState(SysStaticDataEnum.VER_STATE.VER_STATE_Y);
                        iUserSalaryInfoVerService.save(userSalaryInfoVer);
                    }
                }
            }
            updateUserLineVer(tenantUserRel.getUserId(), tenantUserRel.getTenantId(), doAddDriverVo, accessToken);
        } else if (doAddDriverVo.getCarUserType().equals("2")) {
            if (null == tenantUserRel) {
                return;
            }
            updateUserLineVer(tenantUserRel.getUserId(), tenantUserRel.getTenantId(), doAddDriverVo, accessToken);
        }
    }


    /**
     * ??????????????????????????????????????????????????????????????????????????????????????????
     */
    public void updateUserLineVer(Long userId, Long tenantId, DoAddDriverVo doAddDriverVo, String accessToken) {
        LoginInfo userDataInfo = iUserDataInfoRecordService.getLoginInfoByAccessToken(accessToken);
        List<UserLineRelVer> verList = iUserLineRelVerService.getUserLineRelVer(userId, tenantId, SysStaticDataEnum.VER_STATE.VER_STATE_Y);
        if (org.apache.commons.collections.CollectionUtils.isNotEmpty(verList)) {
            for (UserLineRelVer ver : verList) {
                ver.setVerState(SysStaticDataEnum.VER_STATE.VER_STATE_N);
                iUserLineRelVerService.updateById(ver);
            }
        }
        List<UserLineRelVer> userLineRelVerList = doAddDriverVo.getUserLineRelVerList();
        String lineCodeRule = null;
        Long lineld = null;
        if (userLineRelVerList != null) {
            for (UserLineRelVer userLineRelVer : userLineRelVerList) {
                lineCodeRule = userLineRelVer.getLineCodeRule();
                lineld = userLineRelVer.getLineId();
                userLineRelVer = new UserLineRelVer();
                userLineRelVer.setUserId(userId);
                userLineRelVer.setTenantId(tenantId);
                userLineRelVer.setState(1);
                userLineRelVer.setLineId(lineld);
                userLineRelVer.setLineCodeRule(lineCodeRule);
                userLineRelVer.setOpId(userDataInfo.getUserInfoId());
                userLineRelVer.setOpDate(LocalDateTime.now());
                userLineRelVer.setUpdateDate(LocalDateTime.now());
                userLineRelVer.setUpdateOpId(userDataInfo.getUserInfoId());
                userLineRelVer.setVerState(SysStaticDataEnum.VER_STATE.VER_STATE_Y);
                iUserLineRelVerService.save(userLineRelVer);
            }
        } else {
            //??????????????????
            QueryWrapper<UserLineRelVer> userLineRelVerQueryWrapper = new QueryWrapper<>();
            userLineRelVerQueryWrapper.eq("user_id", userId);
            iUserLineRelVerService.remove(userLineRelVerQueryWrapper);
        }

    }


    /**
     * ??????????????????????????????????????????tenantId
     */
    private void afterCreateUserDataInfoVerForUpdate(TenantUserRel tenantUserRel, UserDataInfoVer userDataInfoVer) {
        if (null != tenantUserRel && tenantUserRel.getCarUserType() == SysStaticDataEnum.CAR_USER_TYPE.OWN_CAR) {
            userDataInfoVer.setTenantId(-1L);
        }
    }

    /**
     * ????????????????????????
     */
    private void afterCheckParamForUpdate(DoAddDriverVo doAddDriverVo, String accessToken) {
        LoginInfo userDataInfo = iUserDataInfoRecordService.getLoginInfoByAccessToken(accessToken);
        if (doAddDriverVo.getSalaryPattern() != null && doAddDriverVo.getSalaryPattern() < 0) {
            throw new BusinessException("????????????????????????");
        }
        String message = null;
        if (doAddDriverVo.getSalary() != null && doAddDriverVo.getSalary() > 0) {
            message = iPayFeeLimitService.judgePayFeeLimit(doAddDriverVo.getSalary(), EnumConsts.PAY_FEE_LIMIT.TYPE2, EnumConsts.PAY_FEE_LIMIT.SUB_TYPE6, userDataInfo.getTenantId(), accessToken);
            if (!StringUtils.isEmpty(message)) {
                throw new BusinessException(message);
            }
        }
        if (doAddDriverVo.getSubsidy() != null && doAddDriverVo.getSubsidy() > 0) {
            message = iPayFeeLimitService.judgePayFeeLimit(doAddDriverVo.getSubsidy(), EnumConsts.PAY_FEE_LIMIT.TYPE2, EnumConsts.PAY_FEE_LIMIT.SUB_TYPE7, userDataInfo.getTenantId(), accessToken);
            if (!StringUtils.isEmpty(message)) {
                throw new BusinessException(message);
            }
        }
        //??????????????????????????????????????????
        if (doAddDriverVo.getSalaryPattern() != null && doAddDriverVo.getSalaryPattern() == SysStaticDataEnum.SALARY_PATTERN.MILEAGE) {//????????????
            List<UserSalaryInfoVer> userSalaryInfoList = doAddDriverVo.getUserSalaryInfoVerList();
            if (userSalaryInfoList != null && userSalaryInfoList.size() > 0) {
                for (UserSalaryInfoVer userSalaryInfoVer : userSalaryInfoList) {
                    Double priceDouble = userSalaryInfoVer.getPrice();
                    if (priceDouble != null) {
                        message = iPayFeeLimitService.judgePayFeeLimit(priceDouble.longValue(), EnumConsts.PAY_FEE_LIMIT.TYPE2, EnumConsts.PAY_FEE_LIMIT.SUB_TYPE10, userDataInfo.getTenantId(), accessToken);
                        if (message != null && !"".equals(message)) {
                            throw new BusinessException(message);
                        }
                    }
                }
            }
        }
    }

    //================================================????????????===========================================================
    private void checkParamForUpdate(DoAddDriverVo doAddDriverVo) {
        if (org.apache.commons.lang.StringUtils.isEmpty(doAddDriverVo.getLinkman())) {
            throw new BusinessException("???????????????????????????");
        }

        if (org.apache.commons.lang.StringUtils.isEmpty(doAddDriverVo.getLoginAcct())) {
            throw new BusinessException("???????????????????????????");
        }
        UserDataInfo userDataInfo = iUserDataInfoRecordService.getUserDataInfo(doAddDriverVo.getUserId());
        if (null == userDataInfo) {
            throw new BusinessException("?????????????????????");
        }
        UserDataInfo checkReuslt = iUserDataInfoRecordService.isExistIdentification(doAddDriverVo.getIdentification(), userDataInfo);
        if (null != checkReuslt) {
            throw new BusinessException("?????????" + doAddDriverVo.getIdentification().substring(doAddDriverVo.getIdentification().length() - 4) + "????????????????????????" + checkReuslt.getLinkman() + "???" + checkReuslt.getMobilePhone() + "???????????????????????????");
        }
    }


    public void overrideMobilePhoneAndLinkman(Long userId, String mobilePhone, String linkman) throws BusinessException {
        SysUser sysOperator = iSysUserService.getSysUserByUserIdOrPhone(userId, null);
        if (sysOperator != null) {
            if (!mobilePhone.equals(sysOperator.getLoginAcct())) {
                sysOperator.setBillId(mobilePhone);
                sysOperator.setLoginAcct(mobilePhone);
                iSysUserService.updateById(sysOperator);
            }
            if (StringUtils.isNotEmpty(linkman)
                    && !linkman.equals(sysOperator.getName())) {
                sysOperator.setName(linkman);
                iSysUserService.updateById(sysOperator);
                QueryWrapper<TenantStaffRel> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("user_info_id", userId);
                List<TenantStaffRel> tenantStaffRelList = iTenantStaffRelService.list(queryWrapper);
                if (tenantStaffRelList != null && !tenantStaffRelList.isEmpty()) {
                    for (TenantStaffRel tenantStaffRel : tenantStaffRelList) {
                        tenantStaffRel.setStaffName(linkman);
                        iTenantStaffRelService.updateStaffInfo(tenantStaffRel);
                    }
                }
                SysTenantDef tenantDef = sysTenantDefService.getSysTenantDefByAdminUserId(userId);
                if (null != tenantDef) {
                    tenantDef.setLinkMan(linkman);
                    sysTenantDefService.updateById(tenantDef);
                }
            }

        }
    }


    private TenantVehicleRel quicCreateTenantVehicleRel(long vehicleCode, long driverUserId, String plateNumber, long tenantId, long hisId) throws InvocationTargetException, IllegalAccessException {
        TenantVehicleRel rel = new TenantVehicleRel();
        rel.setVehicleClass(SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS5);
        rel.setTenantId(tenantId);
        rel.setAuthState(SysStaticDataEnum.AUTH_STATE.AUTH_STATE2);
        rel.setIsAuth(SysStaticDataEnum.IS_AUTH.IS_AUTH0);
        rel.setShareFlg(SysStaticDataEnum.SHARE_FLG.NO);
        rel.setVehicleCode(vehicleCode);
        rel.setDriverUserId(driverUserId);
        rel.setPlateNumber(plateNumber);
        java.util.Date date = new java.util.Date();
        Instant instant = date.toInstant();
        ZoneId zone = ZoneId.systemDefault();
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, zone);
        rel.setCreateTime(localDateTime);

        Long relId = iTenantVehicleRelService.saveById(rel);
        TenantVehicleRelVer tenantVehicleRelVer = new TenantVehicleRelVer();
        BeanUtil.copyProperties(rel, tenantVehicleRelVer);
        tenantVehicleRelVer.setCreateTime(localDateTime);
        tenantVehicleRelVer.setVehicleClass(SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS5);
        tenantVehicleRelVer.setTenantId(tenantId);
        tenantVehicleRelVer.setShareFlg(SysStaticDataEnum.SHARE_FLG.NO);
        tenantVehicleRelVer.setVehicleCode(vehicleCode);
        tenantVehicleRelVer.setDriverUserId(driverUserId);
        tenantVehicleRelVer.setPlateNumber(plateNumber);
        tenantVehicleRelVer.setRelId(relId);
        tenantVehicleRelVer.setVerState(SysStaticDataEnum.VER_STATE.VER_STATE_Y);
        tenantVehicleRelVer.setVehicleHisId(hisId);
        tenantVehicleRelVer.setIsAuthSucc(SysStaticDataEnum.IS_AUTH_SUCC.YES_3);
        iTenantVehicleRelVerService.save(tenantVehicleRelVer);
        return rel;
    }


    private void quickApplyVehicle(long vehicleCode, long beApplyTenantId, String plateNumbers, long hisId, String accessToken) {
        LoginInfo user = iUserDataInfoRecordService.getLoginInfoByAccessToken(accessToken);
        ApplyRecord applyRecord = new ApplyRecord();
        applyRecord.setApplyType(2);
        applyRecord.setApplyVehicleClass(SysStaticDataEnum.CAR_USER_TYPE.SOCIAL_CAR);
        applyRecord.setBusiId(vehicleCode);
        applyRecord.setApplyRemark("?????????????????????");
        applyRecord.setApplyTenantId(user.getTenantId());
        applyRecord.setBeApplyTenantId(beApplyTenantId);
        applyRecord.setState(SysStaticDataEnum.APPLY_STATE.APPLY_STATE1);
        applyRecord.setReadState(0);
        applyRecord.setApplyPlateNumbers(plateNumbers);
        applyRecord.setBusiCode(1); //??????
        applyRecord.setHisId(hisId);
        applyRecord.setAuditRemark("???????????????????????????????????????");
        java.util.Date date = new java.util.Date();
        Instant instant = date.toInstant();
        ZoneId zone = ZoneId.systemDefault();
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, zone);
        applyRecord.setAuditDate(localDateTime);
        iApplyRecordService.save(applyRecord);
    }


    /**
     * ??????????????????????????????????????????
     */
    private String checkQuickApplyVehicle(long vehicleCode, long applyTenantId) {
        List<TenantVehicleRel> tenantVehicleRelList = iTenantVehicleRelService.getTenantVehicleRelList(vehicleCode, applyTenantId);
        if (tenantVehicleRelList != null && !tenantVehicleRelList.isEmpty()) {
            return "????????????????????????????????????";
        }
        List<ApplyRecord> applyRecordList = iApplyRecordService.getApplyRecordList(vehicleCode, applyTenantId, null, 0);
        if (applyRecordList != null && !applyRecordList.isEmpty()) {
            return "???????????????????????????";
        }
        applyRecordList = iApplyRecordService.getApplyRecordList(vehicleCode, null, 1, 0);
        if (applyRecordList != null && !applyRecordList.isEmpty()) {
            return "?????????????????????????????????????????????";
        }
        return null;
    }


    /**
     * ???????????????????????????????????????????????????????????????????????????????????????
     * 1???????????????????????????
     * 2??????????????????
     * 3??????????????????????????????????????????
     */
    private UserDataInfo quickAddDriver(UserDataInfo userDataInfo, String url, String accessToken) throws Exception {
        LoginInfo user = iUserDataInfoRecordService.getLoginInfoByAccessToken(accessToken);
//        QueryWrapper<SysTenantDef> queryWrapper = new QueryWrapper<>();
//        queryWrapper.eq("ID", user.getTenantId());
        SysTenantDef sysTenantDef = sysTenantDefService.getById(user.getTenantId());
        try {
            Map<String, Object> paramMap = new HashMap();
            paramMap.put("tenantName", sysTenantDef.getName());
            paramMap.put("url", url);
            SysSmsSend sysSmsSend = new SysSmsSend();
            //??????id
            sysSmsSend.setTemplateId(EnumConsts.SmsTemplate.QUICK_ADD_OTHER_DRIVER_ATTENTION);
            //?????????
            sysSmsSend.setBillId(userDataInfo.getMobilePhone());
            //????????????
            sysSmsSend.setObjType(String.valueOf(SysStaticDataEnum.OBJ_TYPE.NOTIFY));
            //???????????????
            sysSmsSend.setParamMap(paramMap);
            sysSmsSend.setSmsType(SysStaticDataEnum.SMS_TYPE.MESSAGE_AUTHENTICATION);
            iSysSmsSendService.sendSms(sysSmsSend);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //??????????????????
        TenantUserRel tenantUserRel = quickCreateTenantUserRel(userDataInfo.getId(), user.getTenantId());
        saveSysOperLog(SysOperLogConst.BusiCode.Driver, SysOperLogConst.OperType.Add, "????????????????????????", accessToken, tenantUserRel.getId(), null);
        return userDataInfo;
    }

    /**
     * ????????????????????????????????????????????????
     */
    private String quickChangeToDriver(UserDataInfo userDataInfo) {
        //??????????????????????????????????????????
        List<TenantUserRel> tenantUserRelList = iTenantUserRelService.getTenantUserRelListByUserId(userDataInfo.getId(), null);
        if (!CollectionUtils.isEmpty(tenantUserRelList) || (userDataInfo.getUserType() != null && userDataInfo.getUserType().equals(SysStaticDataEnum.USER_TYPE.DRIVER_USER))) {
            //?????????????????????
            return "??????????????????????????????????????????";
        }
        userDataInfo.setUserType(SysStaticDataEnum.USER_TYPE.DRIVER_USER);
        userDataInfo.setTenantId(-1L);
        userDataInfo.setAuthState(SysStaticDataEnum.AUTH_STATE.AUTH_STATE2);
        return null;
    }


    protected void afterCreateTenantUserRel(TenantUserRel tenantUserRel, String carUserType) {
        if (carUserType.equals("3")) {
            tenantUserRel.setHasVer(EnumConsts.HAS_VER_STATE.HAS_VER_YES);
            tenantUserRel.setState(SysStaticDataEnum.SYS_USER_STATE.AUDIT_APPROVE);
        }
    }

    protected void afterCreateTenantUserRelVer(TenantUserRelVer tenantUserRelVer, String carUserType) {
        if (carUserType.equals("3")) {
            tenantUserRelVer.setState(SysStaticDataEnum.VER_STATE.VER_STATE_N);
        }
    }

    //??????
    protected void completeCreateTenantUserRel(UserDataInfo userDataInfo, TenantUserRel tenantUserRel,
                                               DoAddDriverVo doAddDriverVo, String carUserType, String accessToken) throws BusinessException {
        if (carUserType.equals("1")) {//????????????
            //????????????
            createTenantUserSalaryRel(tenantUserRel, userDataInfo, doAddDriverVo);
        }
        //?????????????????????????????????
        if (!carUserType.equals("3")) {
            createUserLine(tenantUserRel.getUserId(), tenantUserRel.getTenantId(), doAddDriverVo, accessToken);
        }
    }

    /**
     * ??????????????????????????? ????????????
     */
    public void createTenantUserSalaryRel(TenantUserRel tenantUserRel, UserDataInfo userDataInfo, DoAddDriverVo doAddDriverVo) throws BusinessException {
        TenantUserSalaryRel tenantUserSalaryRel = new TenantUserSalaryRel();//???????????????


        tenantUserSalaryRel.setSalaryPattern(doAddDriverVo.getSalaryPattern());
        tenantUserSalaryRel.setSalary(doAddDriverVo.getSalary());
        tenantUserSalaryRel.setSubsidy(doAddDriverVo.getSubsidy());
        tenantUserSalaryRel.setTenantId(tenantUserRel.getTenantId());
        tenantUserSalaryRel.setRelId(tenantUserRel.getId());
        tenantUserSalaryRel.setUserId(tenantUserRel.getUserId());
        iTenantUserSalaryRelService.save(tenantUserSalaryRel);
        //ver???

        TenantUserSalaryRelVer tenantUserSalaryRelVer = new TenantUserSalaryRelVer();
        BeanUtil.copyProperties(tenantUserSalaryRel, tenantUserSalaryRelVer);
        tenantUserSalaryRelVer.setRelId(tenantUserRel.getId());
        tenantUserSalaryRelVer.setSalaryId(tenantUserSalaryRel.getId());
        tenantUserSalaryRelVer.setVerState(SysStaticDataEnum.VER_STATE.VER_STATE_Y);
        iTenantUserSalaryRelVerService.save(tenantUserSalaryRelVer);
        int salaryPattern = doAddDriverVo.getSalaryPattern();//????????????
        if (salaryPattern == SysStaticDataEnum.SALARY_PATTERN.MILEAGE
                || salaryPattern == SysStaticDataEnum.SALARY_PATTERN.TIMES) {//????????????
            List<UserSalaryInfoVo> userSalaryInfoList = doAddDriverVo.getUserSalaryInfoList();

            for (UserSalaryInfoVo userSalaryInfovo : userSalaryInfoList) {
                UserSalaryInfo userSalaryInfo = new UserSalaryInfo();//??????????????????????????????
                BeanUtil.copyProperties(userSalaryInfovo, userSalaryInfo);
                userSalaryInfo.setSalaryPattern(salaryPattern);
                userSalaryInfo.setUserId(userDataInfo.getId());
                userSalaryInfo.setUserName(userDataInfo.getLinkman());
                userSalaryInfo.setCreateDate(LocalDateTime.now());
                userSalaryInfo.setCreateTime(LocalDateTime.now());
                userSalaryInfo.setTenantId(userDataInfo.getTenantId());
                String price = userSalaryInfovo.getPrice();
                if (price != null && !"".equals(price)) {
                    Double priceDouble = Double.parseDouble(price);
                    userSalaryInfo.setPrice(priceDouble);
                }
                iUserSalaryInfoService.save(userSalaryInfo);
                //ver???
                UserSalaryInfoVer userSalaryInfoVer = new UserSalaryInfoVer();
                BeanUtil.copyProperties(userSalaryInfo, userSalaryInfoVer);
                userSalaryInfoVer.setMId(userSalaryInfo.getId());
                userSalaryInfoVer.setVerState(SysStaticDataEnum.VER_STATE.VER_STATE_Y);
                iUserSalaryInfoVerService.save(userSalaryInfoVer);
            }
        }
    }


    /**
     * ??????????????????????????????????????????
     */
    public void createUserLine(Long userId, Long tenantId, DoAddDriverVo doAddDriverVo, String accessToken) {
        LoginInfo user = iUserDataInfoRecordService.getLoginInfoByAccessToken(accessToken);
        UserLineRel userLineRel = null;
        UserLineRelVer userLineRelVer = null;
        List<LineRels> lineRels = doAddDriverVo.getLineRels();
        if (lineRels != null && lineRels.size() > 0) {
            for (int i = 0; i < lineRels.size(); i++) {
                Number lineId = CommonUtil.transStringToNumber(lineRels.get(i).getLineId());
                String lineCodeRule = lineRels.get(i).getLineCodeRule();
                userLineRel = new UserLineRel();
                userLineRel.setLineId(lineId == null ? null : lineId.longValue());
                userLineRel.setLineCodeRule(lineCodeRule);
                userLineRel.setUserId(userId);
                userLineRel.setTenantId(tenantId);
                userLineRel.setState(1);
                userLineRel.setOpId(user.getUserInfoId());
                java.util.Date date = new java.util.Date();
                Instant instant = date.toInstant();
                ZoneId zone = ZoneId.systemDefault();
                LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, zone);
                userLineRel.setOpDate(localDateTime);
                iUserLineRelService.save(userLineRel);
                userLineRelVer = new UserLineRelVer();
                userLineRelVer.setRelId(userLineRel.getId());
                userLineRelVer.setLineCodeRule(lineCodeRule);
                userLineRelVer.setLineId(lineId == null ? null : lineId.longValue());
                userLineRelVer.setUserId(userId);
                userLineRelVer.setTenantId(tenantId);
                userLineRelVer.setState(1);
                userLineRelVer.setOpId(user.getUserInfoId());
                userLineRelVer.setOpDate(localDateTime);
                userLineRelVer.setVerState(SysStaticDataEnum.VER_STATE.VER_STATE_Y);
                iUserLineRelVerService.save(userLineRelVer);
            }
        }
    }


    private long getSmsTemplateId(String carUserType) {
        if (carUserType.equals("1")) {
            return EnumConsts.SmsTemplate.ADD_OWN_DRIVER_ATTENTION;
        }
        return EnumConsts.SmsTemplate.ADD_OTHER_DRIVER_ATTENTION;
    }

    private void startAuditProcessForAdd(TenantUserRel tenantUserRel, String accessToken) throws BusinessException {
        boolean bool = iAuditService.startProcess(AuditConsts.AUDIT_CODE.AUDIT_CODE_USER, tenantUserRel.getId(), SysOperLogConst.BusiCode.Driver, new HashMap<>(), accessToken);
        if (!bool) {
            throw new BusinessException("???????????????????????????");
        }
    }

    /**
     * ???????????????
     * ???????????????userDataInfoVer???????????????
     */
    protected void afterCreateUserDataInfoVer(UserDataInfoVer userDataInfoVer, String carUserType, String accessToken) {
        if (carUserType.equals("1")) {//?????????????????????ID
            userDataInfoVer.setVerState(SysStaticDataEnum.VER_STATE.VER_STATE_Y);
        } else {
            userDataInfoVer.setVerState(SysStaticDataEnum.VER_STATE.VER_STATE_N);
            //iSysOperLogService.saveSysOperLog(SysOperLogConst.BusiCode.DriverOBMS, userDataInfoVer.getUserId(), SysOperLogConst.OperType.Audit, "??????????????????(OCR)", SysStaticDataEnum.PT_TENANT_ID);
            saveSysOperLog(SysOperLogConst.BusiCode.DriverOBMS, SysOperLogConst.OperType.Add, "??????????????????(OCR)", accessToken, userDataInfoVer.getUserId(), null);
        }
    }

    protected SysUser initSysOperator(String phone, String name, LoginInfo user) {
        SysUser sysUser = new SysUser();
        sysUser.setTenantId(user.getTenantId());
        sysUser.setTenantCode(user.getTenantCode());
        sysUser.setBillId(phone);
        sysUser.setLoginAcct(phone);
        sysUser.setName(name);
        sysUser.setState(SysStaticDataEnum.SYS_STATE_DESC.STATE_YES);
        sysUser.setLockFlag(SysStaticDataEnum.LOCK_FLAG.LOCK_YES);
        sysUser.setCreateTime(LocalDateTime.now());
        sysUser.setPlatformType(0);
        return sysUser;
    }


    /**
     * ????????????????????????????????????????????????????????????
     * ?????????userId userId
     * ?????????authReason ????????????
     * authUserId ????????????Id
     * authUserName ????????????
     * authOrgId ????????????id
     * authOrgName ??????????????????
     */
    public void getAuthReason(DoQueryDriversDto doQueryDriversDto, Long tenantId) throws Exception {
        if (StringUtils.isNotEmpty(doQueryDriversDto.getAuthUserId())) {
            UserDataInfo userDataInfo = iUserDataInfoRecordService.getUserDataInfo(Long.parseLong(doQueryDriversDto.getAuthUserId()));
            if (userDataInfo != null) {
                doQueryDriversDto.setAuthUserName(userDataInfo.getLinkman());
            }
        }
        if (StringUtils.isNotEmpty(doQueryDriversDto.getAuthOrgId())) {
            // doQueryDriversDto.setAuthOrgIdName(iSysOrganizeService.getCurrentTenantOrgNameById(tenantId,Long.parseLong(doQueryDriversDto.getAuthOrgId())));
        }
    }


    private void expired(DoQueryDriversDto doQueryDriversDto, String accessToken) throws Exception {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        int day = 30;
        SysStaticData sysStaticData = isysStaticDataService.getSysStaticData("DRIVER_EXPIRED", "DRIVER_EXPIRED");
        if (null != sysStaticData && StringUtils.isNumeric(sysStaticData.getCodeName())) {
            day = Integer.valueOf(sysStaticData.getCodeName());
        }
        Date now = new Date();
        String str = null;
        if (null != doQueryDriversDto.getDriverLicenseExpiredTime()) {
            int diff = DateUtil.diffDate(simpleDateFormat.parse(doQueryDriversDto.getDriverLicenseExpiredTime()), now);
            if (diff <= day) {
                str = diff > 0 ? diff + "?????????????????????" : "??????????????????";
                doQueryDriversDto.setDriverLicenseExpiredDay(str);
            } else {
                doQueryDriversDto.setDriverLicenseExpiredDay(null);
            }
        }
        if (null != doQueryDriversDto.getQcCertiExpriedTime()) {
            int diff = DateUtil.diffDate(simpleDateFormat.parse(doQueryDriversDto.getQcCertiExpriedTime()), now);
            if (diff <= day) {
                str = str == null ? "" : str + ",";
                str = str + (diff > 0 ? diff + "???????????????????????????" : "????????????????????????");
                doQueryDriversDto.setQcCertiExpiredDay(diff > 0 ? diff + "???????????????????????????" : "????????????????????????");
            } else {
                doQueryDriversDto.setQcCertiExpiredDay(null);
            }
        }
        //????????????
        doQueryDriversDto.setExpiredString(str);
    }


    private Date getExpiredNotifyDate(String accessToken) throws Exception {
        int day = 30;
        SysStaticData sysStaticData = isysStaticDataService.getSysStaticData("DRIVER_EXPIRED", "DRIVER_EXPIRED");
        if (null != sysStaticData && StringUtils.isNumeric(sysStaticData.getCodeName())) {
            day = Integer.valueOf(sysStaticData.getCodeName());
        }
        return DateUtil.addDate(new Date(), day);
    }


    /**
     * ????????????????????????
     * ????????????????????????
     * ????????????????????????
     * ???????????????userDataInfo????????????????????????
     */
    protected void afterCreateUserDataInfo(UserDataInfo userDataInfo, String carUserType, Long tenantId) {
        if (carUserType.equals("1")) {//?????????????????????ID
            userDataInfo.setTenantId(tenantId);
        } else {
            userDataInfo.setTenantId(-1L);
            userDataInfo.setHasVer(EnumConsts.HAS_VER_STATE.HAS_VER_YES);
            userDataInfo.setAuthState(SysStaticDataEnum.SYS_USER_STATE.AUDIT_APPROVE);
        }
    }


    public boolean isDriver(Long uerId, Long tenantId) {
        List<TenantUserRel> list = iTenantUserRelService.getTenantUserRelListByUserId(uerId, tenantId);
        if (CollectionUtils.isNotEmpty(list)) {
            return true;
        }
        UserDataInfo userDataInfo = iUserDataInfoRecordService.getById(uerId);
        return null != userDataInfo.getUserType() && userDataInfo.getUserType().equals(SysStaticDataEnum.USER_TYPE.DRIVER_USER);
    }


    /**
     * ?????????????????????????????????????????????
     */
    private String checkQuickApplyDriver(long driverUserId, long applyTenantId) {
        List<TenantUserRel> tenantUserRelList = iTenantUserRelService.getTenantUserRelListByUserId(driverUserId, applyTenantId);
        if (tenantUserRelList != null && !tenantUserRelList.isEmpty()) {
            return "????????????????????????????????????";
        }
        List<ApplyRecord> applyRecordList = iApplyRecordService.getApplyRecordList(driverUserId, applyTenantId, null, 0);
        if (applyRecordList != null && !applyRecordList.isEmpty()) {
            return "???????????????????????????";
        }
        applyRecordList = iApplyRecordService.getApplyRecordList(driverUserId, null, 1, 0);
        if (applyRecordList != null && !applyRecordList.isEmpty()) {
            return "?????????????????????????????????????????????";
        }
        return null;
    }


    /**
     * ????????????????????????????????????
     */
    private void quickAppyDriver(long driverUserId, long beApplyTenantId, String plateNumbers, String accessToken) {
        LoginInfo user = iUserDataInfoRecordService.getLoginInfoByAccessToken(accessToken);
        ApplyRecord applyRecord = new ApplyRecord();
        applyRecord.setApplyType(1);
        applyRecord.setApplyCarUserType(SysStaticDataEnum.CAR_USER_TYPE.SOCIAL_CAR);
        applyRecord.setBusiId(driverUserId);
        applyRecord.setApplyRemark("?????????????????????");
        applyRecord.setApplyTenantId(user.getTenantId());
        applyRecord.setBeApplyTenantId(beApplyTenantId);
        applyRecord.setState(SysStaticDataEnum.APPLY_STATE.APPLY_STATE1);
        applyRecord.setReadState(0);
        applyRecord.setApplyPlateNumbers(plateNumbers);
        applyRecord.setBusiCode(1); //??????
        applyRecord.setAuditRemark("???????????????????????????????????????");
        Instant instant = new Date().toInstant();
        ZoneId zone = ZoneId.systemDefault();
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, zone);
        applyRecord.setAuditDate(localDateTime);
        iApplyRecordService.save(applyRecord);
    }

    /**
     * ?????????????????????????????????????????????
     */
    private TenantUserRel quickCreateTenantUserRel(long userId, long tenantId) {


        TenantUserRel tenantUserRel = new TenantUserRel();
        tenantUserRel.setCarUserType(SysStaticDataEnum.CAR_USER_TYPE.SOCIAL_CAR);
        tenantUserRel.setUserId(userId);
        tenantUserRel.setTenantId(tenantId);
        Instant instant = new Date().toInstant();
        ZoneId zone = ZoneId.systemDefault();
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, zone);
        tenantUserRel.setCreateDate(localDateTime);
//      tenantUserRel.setUpdateDate(new Date());
        tenantUserRel.setHasVer(EnumConsts.HAS_VER_STATE.HAS_VER_YES);
        tenantUserRel.setState(SysStaticDataEnum.SYS_USER_STATE.AUDIT_APPROVE);
        iTenantUserRelService.save(tenantUserRel);
        return tenantUserRel;
    }

    /**
     * ???????????????????????????
     */
    private UserDataInfo quickCreateUser(String loginAcct, String linkman, String accessToken) {
        LoginInfo user = iUserDataInfoRecordService.getLoginInfoByAccessToken(accessToken);
        UserDataInfo userDataInfo = new UserDataInfo();
        userDataInfo.setTenantId(-1L);
        userDataInfo.setMobilePhone(loginAcct);
        userDataInfo.setLinkman(linkman);
        userDataInfo.setUserType(SysStaticDataEnum.USER_TYPE.DRIVER_USER);
        userDataInfo.setLoginName(loginAcct);
        userDataInfo.setSourceFlag(0);
        userDataInfo.setOpId(user.getId());
        Instant instant = new Date().toInstant();
        ZoneId zone = ZoneId.systemDefault();
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, zone);
        userDataInfo.setCreateTime(localDateTime);
//      userDataInfo.setUpdateDate(new Date());
        userDataInfo.setQuickFlag(SysStaticDataEnum.QUICK_FLAG.IS_QUICK);
        userDataInfo.setAuthState(SysStaticDataEnum.AUTH_STATE.AUTH_STATE2);
        iUserDataInfoRecordService.save(userDataInfo);
        return userDataInfo;
    }


    public void saveSysOperLog(SysOperLogConst.BusiCode busiCode, SysOperLogConst.OperType operType, String operCommet, String accessToken, Long busid, Long tid) {
        SysOperLog operLog = new SysOperLog();
        operLog.setBusiCode(busiCode.getCode());
        operLog.setBusiName(busiCode.getName());
        operLog.setBusiId(busid);
        operLog.setOperType(operType.getCode());
        operLog.setOperTypeName(operType.getName());
        operLog.setOperComment(operCommet);
        operLog.setTenantId(tid);
        sysOperLogService.save(operLog, accessToken);
    }


    private Integer getCarUserTypeByName(String carUserTypeName) {

        List<com.youming.youche.commons.domain.SysStaticData> sysStaticDatas = (List<com.youming.youche.commons.domain.SysStaticData>) redisUtil.get(SysStaticDataEnum.SYS_STATIC_DATA_NAME.concat("DRIVER_TYPE"));

        if (StrUtil.isEmpty(carUserTypeName) || sysStaticDatas == null) {
            return null;
        }

        String codeValue = null;
        for (com.youming.youche.commons.domain.SysStaticData sysStaticData : sysStaticDatas) {
            if (carUserTypeName.equals(sysStaticData.getCodeName())) {
                codeValue = sysStaticData.getCodeValue();
                break;
            }
        }
        if (codeValue == null) {
            return null;
        }
        Integer type = Integer.valueOf(codeValue);
        if (type.equals(SysStaticDataEnum.CAR_USER_TYPE.OWN_CAR)) {
            return SysStaticDataEnum.CAR_USER_TYPE.OWN_CAR;
        }
        if (type.equals(SysStaticDataEnum.CAR_USER_TYPE.BUSINESS_CAR)) {
            return SysStaticDataEnum.CAR_USER_TYPE.BUSINESS_CAR;
        }
        if (type.equals(SysStaticDataEnum.CAR_USER_TYPE.SOCIAL_CAR)) {
            return SysStaticDataEnum.CAR_USER_TYPE.SOCIAL_CAR;
        }
        if (type.equals(SysStaticDataEnum.CAR_USER_TYPE.ATTACH_CAR)) {
            return SysStaticDataEnum.CAR_USER_TYPE.ATTACH_CAR;
        }
        return null;
    }

    private String getCodeValue(String codeType, String codeName) {
        List<com.youming.youche.commons.domain.SysStaticData> sysStaticDatas = (List<com.youming.youche.commons.domain.SysStaticData>) redisUtil.get(SysStaticDataEnum.SYS_STATIC_DATA_NAME.concat(codeType));
        if (StrUtil.isEmpty(codeName) || sysStaticDatas == null) {
            return null;
        }
        String codeValue = null;
        for (com.youming.youche.commons.domain.SysStaticData sysStaticData : sysStaticDatas) {
            if (codeName.equals(sysStaticData.getCodeName())) {
                codeValue = sysStaticData.getCodeValue();
                break;
            }
        }
        return codeValue;
    }

    StringBuffer checkDate(List<String> strings) throws Exception {
        StringBuffer message = new StringBuffer();
        if (StrUtil.isEmpty(strings.get(0))) {
            message.append("????????????????????????!");
        } else {
            if (strings.get(0).length() == 11) {
                UserDataInfo user = iUserDataInfoRecordService.getDriver(strings.get(0)); //??????????????????
                if (null != user) {
                    List<TenantServiceRel> tenantUserRelList = iTenantServiceRelService.getTenantServiceRelList(user.getId(), null);
                    if (tenantUserRelList != null && tenantUserRelList.size() > 0) {
                        if (null != user.getUserType() && user.getUserType().equals(com.youming.youche.record.common.SysStaticDataEnum.USER_TYPE.DRIVER_USER)) {//??????????????????
                            message.append("????????????????????????");
                        }
                    }
                    if (StrUtil.isEmpty(strings.get(3))) {
                        message.append("????????????????????????!");
                    } else {
                        if (!IDCardUtil.isIDCard(strings.get(3))) {
                            message.append("??????????????????????????????");
                        } else {
                            if (!StrUtil.isEmpty(strings.get(0))) {
                                UserDataInfo checkReuslt = iUserDataInfoRecordService.isExistIdentification(strings.get(3), user);
                                //?????????????????????????????????????????????
                                if (null != checkReuslt) {
                                    message.append("????????????" + strings.get(3).substring(strings.get(3).length() - 4) + "????????????????????????" + checkReuslt.getLinkman() + "(" + checkReuslt.getMobilePhone() + ")?????????????????????!");
                                }
                            }
                        }
                    }
                }
            } else {
                message.append("??????????????????????????????");
            }
        }
        if (StrUtil.isEmpty(strings.get(1))) {
            message.append("????????????????????????!");
        }
        if (StrUtil.isEmpty(strings.get(2))) {
            message.append("????????????????????????!");
        }
        if (StrUtil.isEmpty(strings.get(4))) {
            message.append("????????????????????????!");
        }
        if (StrUtil.isEmpty(strings.get(5))) {
            message.append("????????????????????????!");
        }
//        if (StrUtil.isEmpty(strings.get(6))) {
//            message.append("????????????????????????!");
//        }
        for (int i = 7; i < 15; i++) {
            if (ExcelUtils.getExcelValue(strings, i) == null) {
                strings.add(i, "");
            }
        }
        return message;
    }

    @Override
    public List<SysCfg> getSysCfgInfo(CfgVO cfgVO, String accessToken) {
        List<String> cfgNames = new ArrayList<>();
        try {
            cfgNames = cfgVO.getCfgNames();
        } catch (Exception e) {
            String cfgName = cfgVO.getCfgNames().get(0);
            cfgNames.add(cfgName);
        }
        //LoginInfo user = loginUtils.get(accessToken);
        //Long tenantId = user.getTenantId();
        long tenantId = cfgVO.getTenantId();
        int system = cfgVO.getSystem();
        List<SysCfg> list = new ArrayList<SysCfg>();
        SysCfg cfg = null;
        if (cfgNames.size() != 0) {
            for (String cn : cfgNames) {
                //??????????????????
                try {
                    if (system > -1) {
                        cfg = SysCfgUtil.getSysCfg(tenantId, cn, system, redisUtil);
                    } else {
                        cfg = SysCfgUtil.getSysCfg(tenantId, cn, redisUtil);
                    }
                    if (cfg == null) {
                        cfg = SysCfgUtil.getSysCfg(cn, accessToken, loginUtils, redisUtil);
                    }
                    if (cfg != null) {
                        list.add(cfg);
                    }
                } catch (Exception e) {
                    //????????????????????????????????????????????????????????????????????????
                    if (cfgNames.size() == 1) {
                        throw new BusinessException("?????????????????????[" + cn + "]?????????????????????");
                    } else {
                        LOGGER.error("???????????????[" + cn + "]???????????????????????????????????????????????????????????????");
                    }

                }
            }

        } else {
//            //????????????APP??????????????????
//            SysCfg sysCfg = (SysCfg) redisUtil.get(EnumConsts.SysCfg.SYS_CFG_NAME.concat(cfgName));
//            Cache cache = CacheFactory.getCache(SysCfgCache.class.getName());
//            redisUtil.sgetAll(EnumConsts.SysCfg.SYS_CFG_NAME);
//            List<String> keyList = cache.getKeys();
            List<SysCfg> sysCfgs = sysCfgService.list();
            for (SysCfg scfg1 : sysCfgs) {
                int cfgValue = scfg1.getCfgSystem();
                if (cfgValue == SysStaticDataEnum.SYSTEM_CFG.CFG_0
                        || cfgValue == SysStaticDataEnum.SYSTEM_CFG.CFG_3
                        || "APP_VERSION_IOS".equals(scfg1.getCfgName())
                        || "APP_VERSION_ANDROID".equals(scfg1.getCfgName())) {
                    list.add(scfg1);
                }
            }
//            for (String key : keyList) {
//                cfg = SysCfgUtil.getSysCfg(key);
//                int cfgValue =  cfg.getCfgSystem();
//                if (cfgValue == SysStaticDataEnum.SYSTEM_CFG.CFG_0
//                        || cfgValue == SysStaticDataEnum.SYSTEM_CFG.CFG_3
//                        || "APP_VERSION_IOS".equals(cfg.getCfgName())
//                        || "APP_VERSION_ANDROID".equals(cfg.getCfgName())) {
//                    list.add(cfg);
//                }
//            }
        }
//        List listOut = new ArrayList();
//        for(SysCfg sc : list){
//            Map map = new HashMap();
//            map.put("cfgName", sc.getCfgName());
//            map.put("cfgValue", sc.getCfgValue());
//            if(cfg.getCfgRemark() != null){
//                map.put("cfgRemark", sc.getCfgRemark());
//            }else{
//                map.put("cfgRemark", "");
//            }
//            map.put("cfgSystem", sc.getCfgSystem()+"");
//            listOut.add(map);
//        }
        return list;
    }

    @Override
    public BeApplyRecordVo getBeApplyRecordWx(Long id, String accessToken) {
        BeApplyRecordVo beApplyRecord = null;
        try {
            beApplyRecord = this.getBeApplyRecord(id, accessToken);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (beApplyRecord != null) {

            if (StringUtils.isEmpty(beApplyRecord.getApplyDriverPhone())) {
                beApplyRecord.setApplyDriverPhone("");
            }
            if (StringUtils.isEmpty(beApplyRecord.getApplyDriverName())) {
                beApplyRecord.setApplyDriverName("");
            }

            int stateRtn = beApplyRecord.getState();
            if (stateRtn > -1) {
                beApplyRecord.setStateName(isysStaticDataService.getSysStaticData("APPLY_STATE", "" + stateRtn).getCodeName());

                String applyPlateNumbers = beApplyRecord.getApplyPlateNumbers();
                if (org.apache.commons.lang.StringUtils.isNotEmpty(applyPlateNumbers)) {
                    String[] plateNumbers = applyPlateNumbers.split(",");
                    List list = new ArrayList();
                    for (String plateNumber : plateNumbers) {
                        Map map = new HashMap();
                        map.put("plateNumber", plateNumber);
                        list.add(map);
                    }
                    beApplyRecord.setCheckedVehicles(list);
                }
                String applyFile = beApplyRecord.getApplyFile();
                if (org.apache.commons.lang.StringUtils.isNotBlank(applyFile)) {
                    FastDFSHelper client = null;
                    try {
                        client = FastDFSHelper.getInstance();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    SysAttach sysAttach = iSysAttachService.getById(Long.valueOf(applyFile));

                    if (sysAttach != null) {
                        String httpURL = null;
                        try {
                            httpURL = client.getHttpURL(sysAttach.getStorePath());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (org.apache.commons.lang.StringUtils.isNotEmpty(httpURL)) {
                            String url = httpURL.split("\\?")[0];
                            String bigUrl = null;
                            try {
                                bigUrl = client.getHttpURL(sysAttach.getStorePath(), true).split("\\?")[0];
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            beApplyRecord.setApplyFileUrl(url);
                            beApplyRecord.setApplyFileBigUrl(bigUrl);
                        }
                    }
                }
            }
        }

        return beApplyRecord;
    }

    @Override
    public Page<DoQueryDriversDto> doQueryCarDrivers(String plateNumber, Integer vehicleClass, String linkman, String loginAcct, Integer pageNum, Integer pageSize, String accessToken, Long userId) {
        if (StringUtils.isNotEmpty(plateNumber)) {
            VehicleDataInfo vehicleDataInfo = iVehicleDataInfoService.getVehicleDataInfo(plateNumber);
            if (vehicleDataInfo.getTenantId() == null || vehicleDataInfo.getTenantId() <= 1) {
                throw new BusinessException("????????????????????????");
            }
            return this.doQueryCarDrivers(vehicleClass, linkman, loginAcct, pageNum, pageSize, accessToken, userId, vehicleDataInfo.getTenantId());
        }
        return this.doQueryCarDrivers(vehicleClass, linkman, loginAcct, pageNum, pageSize, accessToken, userId);
    }

    @Override
    public Page<DoQueryDriversDto> doQueryCarDrivers(Integer vehicleClass, String linkman, String loginAcct, Integer pageNum, Integer pageSize, String accessToken, Long... userId) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        DoQueryDriversVo doQueryDriversVo = new DoQueryDriversVo();

        int type1 = this.transVehicleClass(vehicleClass); // ?????????
        int type2 = this.transVehicleClass(com.youming.youche.record.common.SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS2); // ?????????
        int type3 = this.transVehicleClass(com.youming.youche.record.common.SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS5); // ?????????
        String carUserTypeStr = type1 + "," + type2 + "," + type3;
//        doQueryDriversVo.setCarUserType(type);
        doQueryDriversVo.setLinkman(linkman);
        doQueryDriversVo.setLoginAcct(loginAcct);
        doQueryDriversVo.setState(SysStaticDataEnum.SYS_USER_STATE.AUDIT_APPROVE);
//        if (type == SysStaticDataEnum.CAR_USER_TYPE.SOCIAL_CAR) {
//            doQueryDriversVo.setOnlyC("true");
//        }
        if (userId != null && userId.length == 1) {
            doQueryDriversVo.setDriverUserId(userId[0]);
        }
        if (userId != null && userId.length == 2) {
            doQueryDriversVo.setDriverUserId(userId[0]);
            doQueryDriversVo.setQueryTenantId(userId[1]);
        }

        Date expiredDate = null;
        try {
            expiredDate = getExpiredNotifyDate(accessToken);
        } catch (Exception e) {
            e.printStackTrace();
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = simpleDateFormat.format(expiredDate);

        Page<DoQueryDriversDto> page = new Page<>(pageNum, pageSize);
        Page<DoQueryDriversDto> doQueryDriversDtoPage = userDataInfoRecordMapper.doQueryCarDriverWx(page, loginInfo.getTenantId(), "1,3", date, doQueryDriversVo, carUserTypeStr);

        //????????????
        List<DoQueryDriversDto> lists = doQueryDriversDtoPage.getRecords();
        List<DoQueryDriversDto> listOut = new ArrayList<DoQueryDriversDto>();

        for (DoQueryDriversDto os : lists) {
            DoQueryDriversDto dto = new DoQueryDriversDto();
            BeanUtil.copyProperties(os, dto);
            int carUserType = os.getCarUserType() != null ? os.getCarUserType() : -1;
            if (carUserType > 0) {
                dto.setCarUserTypeName(isysStaticDataService.getSysStaticData("DRIVER_TYPE", String.valueOf(carUserType)).getCodeName());
            }

            listOut.add(dto);
        }
        doQueryDriversDtoPage.setRecords(listOut);

        return doQueryDriversDtoPage;
    }

    @Override
    public Boolean doAudit(String busiCode, Long busiId, String desc, Integer chooseResult, String driverUserId, String plateNumbers, String accessToken) {
        ApplyRecord applyRecord = iApplyRecordService.getById(busiId);
        if (null == applyRecord) {
            throw new BusinessException("????????????????????????");
        }
        Long driverId = null;
        if (org.apache.commons.lang.StringUtils.isNotBlank(driverUserId)) {
            driverId = Long.parseLong(driverUserId);
        }
        AuditApplyVo vo = new AuditApplyVo();
        vo.setBusiId(busiId);
        vo.setBusiCode(busiCode);
        vo.setDesc(desc);
        vo.setChooseResult(chooseResult);
        vo.setPlateNumbers(plateNumbers);
        vo.setDriverUserId(driverUserId);
        try {
            this.auditApplyRecord(vo, driverId, accessToken);
            //   this.auditApplyRecordWx(vo, driverId, accessToken);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public UserInfoDto loginIn(Integer platformType, String accessToken) {
//        UserInfoDto dto = new UserInfoDto();
//        List<WechatUserRel> list = iWechatUserRelService.getWechatUserRelByAppCodeAndState(platformType, 1);
//        //??????
//        Map map = new HashMap();
//        if (list == null || list.isEmpty()) {
//            dto.setInfo(2);
//            return dto;
//        }

        //????????????????????????
//        WechatUserRel wechatUserRel = list.get(0);

        if (platformType == 3) {
            return this.doLogin(/*wechatUserRel,*/ platformType, accessToken);
        } else {
            return this.loginMain(/*wechatUserRel,*/ platformType, accessToken);
        }

    }

    @Override
    public UserInfoDto doLogin(/*WechatUserRel wechatUserRel, */int appCode, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);

        SysUser sysOperator = iSysUserService.getSysOperatorByUserIdOrPhone(loginInfo.getUserInfoId(), null);

//        if (sysOperator == null) {
//            throw new BusinessException("???????????????????????????????????????????????????????????????????????????");
//        }
//        if (sysOperator.getLockFlag() != null && sysOperator.getLockFlag() == 2) { //??????????????????????????????
//            throw new BusinessException("????????????????????????????????????!");
//        }

        //??????????????????
        UserDataInfo userDataInfo = iUserDataInfoRecordService.getUserDataInfo(sysOperator.getUserInfoId());

        if (userDataInfo == null) {
            throw new BusinessException("?????????????????????!");
        }
        Long tenantId = userDataInfo.getTenantId();
//        //????????????????????????????????????
//        List<TenantUserRel> tenantUserRelList = this.getAllTenantUserRels(sysOperator.getId());
//
//        if (org.apache.commons.collections.CollectionUtils.isEmpty(tenantUserRelList) && !userDataInfo.getUserType().equals(SysStaticDataEnum.USER_TYPE.DRIVER_USER)) {
//            throw new BusinessException("?????????????????????????????????????????????");
//        }

//        //?????????????????????????????????????????????
//        if (org.apache.commons.collections.CollectionUtils.isNotEmpty(tenantUserRelList) && tenantUserRelList.size() == 1) {
//            TenantUserRel userRel = tenantUserRelList.get(0);
//            tenantId = userRel.getTenantId();
//        } else {
//            tenantId = userDataInfo.getTenantId();
//        }

//        String isOK = "";
        long userId = sysOperator.getUserInfoId();
        UserInfoDto infoVO = new UserInfoDto();
        //????????????????????????????????????
        // SysContexts.kickUser(userId);
        //??????????????????????????????
        infoVO.setIsAuth("1");
        if (userDataInfo.getAuthState() == null || userDataInfo.getAuthState() == SysStaticDataEnum.AUTH_STATE.AUTH_STATE1
                || userDataInfo.getAuthState() == SysStaticDataEnum.AUTH_STATE.AUTH_STATE3 || StringUtils.isNotEmpty(userDataInfo.getVerifReason())) {
            infoVO.setIsAuth("0");
        } else {//???????????????????????????????????????
            if (userDataInfo.getTenantId() != null) {
                TenantUserRel tenantUserRel = iTenantUserRelService.getAllTenantUserRelByUserId(sysOperator.getUserInfoId(), userDataInfo.getTenantId());
                if (tenantUserRel != null) {
                    if (tenantUserRel.getState() == null || tenantUserRel.getState() == SysStaticDataEnum.AUTH_STATE.AUTH_STATE1
                            || tenantUserRel.getState() == SysStaticDataEnum.AUTH_STATE.AUTH_STATE3 || StringUtils.isNotEmpty(tenantUserRel.getStateReason())) {
                        infoVO.setIsAuth("0");
                    }
                }

            }
        }
//        //????????????????????????,????????????????????????????????????
//        infoVO.setIsBindCard("0");
//        if (iAccountBankUserTypeRelService.isUserTypeBindCard(sysOperator.getId(), EnumConsts.ACCOUNT_BANK_REL.BANK_TYPE_0, SysStaticDataEnum.USER_TYPE.DRIVER_USER)) {
//            infoVO.setIsBindCard("1");
//        }

        infoVO.setBillId(sysOperator.getBillId());

        infoVO.setUserName(userDataInfo.getLinkman());

        if (userDataInfo.getAuthState() != null) {
            infoVO.setCheckFlag(userDataInfo.getAuthState() + "");
        } else {
            infoVO.setCheckFlag(SysStaticDataEnum.SYS_USER_STATE.AUDIT_NOT + "");
        }

        if (StringUtils.isNotEmpty(userDataInfo.getUserPriceUrl())) {
            FastDFSHelper client = null;
            try {
                client = FastDFSHelper.getInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                infoVO.setUserPicture(client.getHttpURL(userDataInfo.getUserPriceUrl()).split("\\?")[0]);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (userDataInfo.getTenantId() != null && userDataInfo.getTenantId() > 1) {
            infoVO.setCarUserType(SysStaticDataEnum.CAR_USER_TYPE.OWN_CAR);
        } else {
            infoVO.setCarUserType(SysStaticDataEnum.CAR_USER_TYPE.SOCIAL_CAR);
        }

        sysOperator.setTenantId(tenantId);
        sysOperator.setId(userDataInfo.getId());
        sysOperator.setUserType(SysStaticDataEnum.USER_TYPE.DRIVER_USER);

        infoVO.setUserId(userId + "");
        infoVO.setTokenId(accessToken);
        infoVO.setIsSetPasswd(sysOperator.getPassword() == null ? "0" : "1");
        infoVO.setTenantId(userDataInfo.getTenantId());
        infoVO.setUserType(sysOperator.getUserType() + "");
//        infoVO.setOpenId(openId);
        infoVO.setTenantId(tenantId);

        //?????????????????????????????????????????????????????????????????????
//        if (userDataInfo.getUserType() != null && userDataInfo.getUserType() == SysStaticDataEnum.USER_TYPE.DRIVER_USER) {
//            userDataInfo.setQuickFlag(SysStaticDataEnum.QUICK_FLAG.NOT_QUICK);
//            iUserDataInfoService.update(userDataInfo);
//        }
        infoVO.setInfo(1);
        return infoVO;
    }

    @Override
    public UserInfoDto loginMain(/*WechatUserRel wechatUserRel,*/ int appCode, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        //??????tenantCode
        UserInfoDto infoVo = new UserInfoDto();
        infoVo.setInfo(1);

        //???????????????
        SysUser sysOperator = iSysUserService.getSysOperatorByUserIdOrPhone(loginInfo.getUserInfoId(), null);
//        if (sysOperator == null) {
//            throw new BusinessException("?????????????????????????????????");
//        }
        UserDataInfo userDataInfo = iUserDataInfoRecordService.getUserDataInfo(sysOperator.getUserInfoId());
//        if (userDataInfo == null) {
//            throw new BusinessException("????????????????????????????????????");
//        }
//
//        if (appCode == 1 && !iServiceInfoService.isService(userDataInfo.getId())) {
//            throw new BusinessException("???????????????????????????????????????!");
//        }
//        if (appCode == 1) {
//            ServiceInfo serviceInfo = iServiceInfoService.getServiceInfoByUserId(wechatUserRel.getUserId());
//            if (serviceInfo == null || serviceInfo.getState() == SysStaticDataEnum.SYS_STATE_DESC.STATE_NO) {
//                throw new BusinessException("????????????????????????????????????");
//            }
//            infoVo.setTenantId(serviceInfo.getTenantId());
//        }

        long orgId = -1L;
        Long tenantId = -1L;
        Integer userType = null;
//        Map<String, Object> attrs = new HashMap<String, Object>();
        infoVo.setUserName(userDataInfo.getLinkman());
        infoVo.setMobilePhone(userDataInfo.getMobilePhone());
        if (appCode == 2) {
            List<TenantStaffRel> tenantStaffRelList = iTenantStaffRelService.getTenantStaffRelByUserId(sysOperator.getUserInfoId());
            UserReceiverInfo receiverInfo = iUserReceiverInfoService.getUserReceiverInfoByUserId(sysOperator.getUserInfoId());

            List<TenantStaffRel> tenantStaffRelNewList = new ArrayList<>();
            //????????????
            if (null != tenantStaffRelList && !tenantStaffRelList.isEmpty()) {
                for (TenantStaffRel rel : tenantStaffRelList) {
                    if (rel.getTenantId() != SysStaticDataEnum.PT_TENANT_ID) {
                        tenantStaffRelNewList.add(rel);
                    }
                }
            }

            if (null == receiverInfo && tenantStaffRelNewList.isEmpty()) {
                throw new BusinessException("???????????????????????????????????????????????????????????????????????????");
            }

            infoVo.setIsAdminUser(sysTenantDefService.isAdminUser(userDataInfo.getId()));

            //?????????????????????????????????????????????????????????????????????????????????tenantId???orgId??????seesion???
            if (tenantStaffRelNewList.size() == 1 && null == receiverInfo) {
                TenantStaffRel tenantStaffRel = tenantStaffRelNewList.get(0);
//                if (null != tenantStaffRel.getState()
//                        && tenantStaffRel.getState() == SysStaticDataEnum.STAFF_STATE.DELETE) {
//                    throw new BusinessException("???????????????????????????????????????");
//                }
//                if (null != tenantStaffRel.getLockFlag()
//                        && tenantStaffRel.getLockFlag() == SysStaticDataEnum.LOCK_FLAG.LOCK_NO) {
//                    throw new BusinessException("???????????????????????????????????????");
//                }

                tenantId = tenantStaffRel.getTenantId();

                SysUserOrgRel sysUserOrgRel = iSysUserOrgRelService.getStasffDefaultOragnizeByUserId(sysOperator.getUserInfoId(), tenantId);

                if (null != sysUserOrgRel) {
                    orgId = sysUserOrgRel.getOrgId();
                }

//                List<Integer> rtnList = PermissionCacheUtil.getRoleIdsByOperatorId(tenantId, sysOperator.getOperatorId());
//                attrs.put("roleIds", rtnList);

                SysTenantDef sysTenantDef = sysTenantDefService.getSysTenantDef(tenantId);
//                attrs.put("tenantCode", sysTenantDef.getTenantCode());
                infoVo.setTenantId(tenantId);
                infoVo.setTenantName(sysTenantDef.getName());
                infoVo.setIsMultiTenantUser(false);

//                List<Long> entityIdList = PermissionCacheUtil.getEntityIdByRoles(tenantId, rtnList);
//                map.put("entityIds", entityIdList);

                Long tenantAdminUser = sysTenantDefService.getTenantAdminUser(tenantId);
                UserDataInfo dataInfo = iUserDataInfoRecordService.getUserDataInfo(tenantAdminUser);

                infoVo.setAdminUserId(tenantAdminUser);
                infoVo.setMobilePhone(dataInfo.getMobilePhone());

                //????????????
//                wechatUserRel.setTenantId(tenantId);
//                iWechatUserRelService.save(wechatUserRel);
                userType = this.getUserType(tenantStaffRel.getUserInfoId(), tenantId, SysStaticDataEnum.LOGIN_CHANNEL.WX_TENANT_4);
            } else if (tenantStaffRelNewList.isEmpty() && null != receiverInfo) {
                //????????????????????????????????????????????????????????????????????????ID
                SysTenantDef sysTenantDef = sysTenantDefService.getSysTenantDefByAdminUserId(receiverInfo.getUserId());
                if (null != sysTenantDef) {
                    infoVo.setTenantId(sysTenantDef.getId());
                }

                //???????????????????????????
//                attrs.put("roleIds", new ArrayList<Integer>());
//                attrs.put("tenantCode", "");
                tenantId = -1L;
                userType = this.getUserType(receiverInfo.getUserId(), tenantId, SysStaticDataEnum.LOGIN_CHANNEL.WX_RECEIVER_7);

            } else {//??????????????? ?????? ??????????????????????????????????????????????????????????????????????????????
                List<Map> tenantList = this.getTenantListMap(tenantStaffRelNewList);

                if (null != receiverInfo) {
                    Map receiverMap = new HashMap();
                    receiverMap.put("receiverId", receiverInfo.getId());
                    receiverMap.put("receiverName", receiverInfo.getReceiverName());
                    tenantList.add(receiverMap);
                }
                infoVo.setIsMultiTenantUser(true);
                infoVo.setTenantList(tenantList);
            }
        } else if (appCode == 1) {
            Map<String, Object> serviceMap = iServiceProductService.getServiceInfoByUserIdOrChild(sysOperator.getUserInfoId());
            infoVo.setServiceName(DataFormat.getStringKey(serviceMap, "serviceName"));
            infoVo.setServiceType(DataFormat.getStringKey(serviceMap, "serviceType"));
            infoVo.setUserId(DataFormat.getStringKey(serviceMap, "userId"));
            infoVo.setLinkman(DataFormat.getStringKey(serviceMap, "linkman"));
            infoVo.setMobilePhone(DataFormat.getStringKey(serviceMap, "mobilePhone"));
            infoVo.setAuthState(DataFormat.getStringKey(serviceMap, "authState"));
            infoVo.setServiceTypeName(DataFormat.getStringKey(serviceMap, "serviceTypeName"));
            infoVo.setAuthStateName(DataFormat.getStringKey(serviceMap, "authStateName"));
            infoVo.setUserType(DataFormat.getStringKey(serviceMap, "userType"));

            userType = this.getUserType(sysOperator.getUserInfoId(), null, SysStaticDataEnum.LOGIN_CHANNEL.WX_SERVICE_3);
        }

//        BaseUser user = new BaseUser();
//        user.setOperId(sysOperator.getOpUserId());
//        user.setUserId(sysOperator.getId());
//        user.setUserName(userDataInfo.getLinkman());
//        user.setUserType(null == userType ? userDataInfo.getUserType() : userType);
//        user.setTelphone(sysOperator.getBillId());
//        user.setOrgId(Long.parseLong(orgId + ""));//userDataInfo.getOrgId()
//        user.setTenantId(tenantId);
//        attrs.put(LoginConst.BaseUserAttr.loginAcct, sysOperator.getLoginAcct());
//        attrs.put(LoginConst.BaseUserAttr.userTypeName, sysOperator.getName());
//        user.setAttrs(attrs);

        infoVo.setTokenId(accessToken);
        infoVo.setUserType(null == userType ? userDataInfo.getUserType().toString() : userType.toString());

        infoVo.setUserId(Long.toString(userDataInfo.getId()));
        return infoVo;
    }

    @Override
    public Integer getUserType(long userId, Long tenantId, int loginFrom) {
        if (SysStaticDataEnum.LOGIN_CHANNEL.PC_1 == loginFrom
                || SysStaticDataEnum.LOGIN_CHANNEL.PC_OBMS_5 == loginFrom
                || SysStaticDataEnum.LOGIN_CHANNEL.WX_TENANT_4 == loginFrom) {
            SysTenantDef sysTenantDef = sysTenantDefService.getSysTenantDef(tenantId, true);
            if (null != sysTenantDef && sysTenantDef.getAdminUser().equals(userId)) {
                return SysStaticDataEnum.USER_TYPE.ADMIN_USER;
            }

            return SysStaticDataEnum.USER_TYPE.CUSTOMER_USER;
        }
        if (SysStaticDataEnum.LOGIN_CHANNEL.APP_2 == loginFrom) {
            return SysStaticDataEnum.USER_TYPE.DRIVER_USER;
        }
        if (SysStaticDataEnum.LOGIN_CHANNEL.PC_SERVICE_6 == loginFrom
                || SysStaticDataEnum.LOGIN_CHANNEL.WX_SERVICE_3 == loginFrom) {
            return SysStaticDataEnum.USER_TYPE.SERVICE_USER;
        }
        if (SysStaticDataEnum.LOGIN_CHANNEL.WX_RECEIVER_7 == loginFrom) {
            return SysStaticDataEnum.USER_TYPE.RECEIVER_USER;
        }
        return null;
    }

    @Override
    public List<WorkbenchDto> getTableUserDriverCount() {
        return userDataInfoRecordMapper.getTableUserDriverCount();
    }

    @Override
    public List<WorkbenchDto> getTableUserQcCertiCount() {
        LocalDateTime localDateTime = LocalDate.now().plusMonths(1).atStartOfDay();
        return userDataInfoRecordMapper.getTableUserQcCertiCount(localDateTime);
    }

    @Override
    public List<WorkbenchDto> getTableDriverCount() {
        return userDataInfoRecordMapper.getTableDriverCount();
    }

    private List<Map> getTenantListMap(List<TenantStaffRel> tenantStaffRelList) {
        List<Map> tenantList = new ArrayList<>();
        for (TenantStaffRel tenantStaffRel : tenantStaffRelList) {
            SysTenantDef sysTenantDef = sysTenantDefService.getSysTenantDef(tenantStaffRel.getTenantId());
            Map tenantMap = new HashMap();
            tenantMap.put(sysTenantDef.getId(), sysTenantDef.getName());
            tenantList.add(tenantMap);
        }
        return tenantList;
    }

    private int transVehicleClass(int vehicleClass) {
        return CommonUtil.vehicleType2CarType(vehicleClass);
    }

    public static IPage listToPage(List list, int pageNum, int pageSize) {
        List pageList = new ArrayList<>();
        int curIdx = pageNum > 1 ? (pageNum - 1) * pageSize : 0;
        for (int i = 0; i < pageSize && curIdx + i < list.size(); i++) {
            pageList.add(list.get(curIdx + i));
        }
        IPage page = new Page<>(pageNum, pageSize);
        page.setRecords(pageList);
        page.setTotal(list.size());
        return page;
    }


}
