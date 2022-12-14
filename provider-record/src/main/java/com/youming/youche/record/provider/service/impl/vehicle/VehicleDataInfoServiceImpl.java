package com.youming.youche.record.provider.service.impl.vehicle;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.common.utils.CollectionUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import com.youming.youche.cloud.api.sms.ISysSmsSendService;
import com.youming.youche.cloud.domin.sms.SysSmsSend;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.constant.SysOperLogConst;
import com.youming.youche.commons.domain.ImportOrExportRecords;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.domain.SysStaticData;
import com.youming.youche.commons.domain.SysUser;
import com.youming.youche.commons.dto.AuditDto;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.util.DataFormat;
import com.youming.youche.commons.util.LoginUtils;
import com.youming.youche.commons.util.RedisUtil;
import com.youming.youche.commons.vo.VehicleApplyRecordVo;
import com.youming.youche.components.fdfs.FastDFSHelper;
import com.youming.youche.conts.SysStaticDataEnum;
import com.youming.youche.record.api.apply.IApplyRecordService;
import com.youming.youche.record.api.audit.IAuditCallBackService;
import com.youming.youche.record.api.etc.IEtcMaintainService;
import com.youming.youche.record.api.sys.ISysUserService;
import com.youming.youche.record.api.tenant.*;
import com.youming.youche.record.api.user.IUserDataInfoRecordService;
import com.youming.youche.record.api.vehicle.*;
import com.youming.youche.record.common.AuditConsts;
import com.youming.youche.record.common.CommonUtil;
import com.youming.youche.record.common.EnumConsts;
import com.youming.youche.record.common.OrderConsts;
import com.youming.youche.record.domain.apply.ApplyRecord;
import com.youming.youche.record.domain.cm.CmCustomerLine;
import com.youming.youche.record.domain.tenant.*;
import com.youming.youche.record.domain.trailer.TenantTrailerRel;
import com.youming.youche.record.domain.trailer.TenantTrailerRelVer;
import com.youming.youche.record.domain.user.UserDataInfo;
import com.youming.youche.record.domain.user.UserReceiverInfo;
import com.youming.youche.record.domain.vehicle.*;
import com.youming.youche.record.dto.*;
import com.youming.youche.record.dto.vehicle.VehicleReportVehicleDataDto;
import com.youming.youche.record.provider.mapper.cm.CmCustomerInfoMapper;
import com.youming.youche.record.provider.mapper.cm.CmCustomerLineMapper;
import com.youming.youche.record.provider.mapper.order.OrderInfoMapper;
import com.youming.youche.record.provider.mapper.other.CityMapper;
import com.youming.youche.record.provider.mapper.other.DistrictMapper;
import com.youming.youche.record.provider.mapper.other.ProvinceMapper;
import com.youming.youche.record.provider.mapper.tenant.*;
import com.youming.youche.record.provider.mapper.trailer.TenantTrailerRelMapper;
import com.youming.youche.record.provider.mapper.trailer.TenantTrailerRelVerMapper;
import com.youming.youche.record.provider.mapper.user.UserDataInfoRecordMapper;
import com.youming.youche.record.provider.mapper.vehicle.*;
import com.youming.youche.record.provider.transfer.VehicleDataInfoiVoTransfer;
import com.youming.youche.record.provider.utils.ExcelUtils;
import com.youming.youche.record.provider.utils.ReadisUtil;
import com.youming.youche.record.provider.utils.SysCfgUtil;
import com.youming.youche.record.provider.utils.SysStaticDataRedisUtils;
import com.youming.youche.record.vo.*;
import com.youming.youche.system.api.*;
import com.youming.youche.system.api.audit.*;
import com.youming.youche.system.api.ioer.ImportOrExportRecordsService;
import com.youming.youche.system.domain.SysOperLog;
import com.youming.youche.system.domain.SysOrganize;
import com.youming.youche.system.domain.SysTenantDef;
import com.youming.youche.system.domain.audit.*;
import com.youming.youche.system.utils.excel.ExportExcel;
import com.youming.youche.util.ExcelFilesVaildate;
import com.youming.youche.util.JsonHelper;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.dubbo.rpc.RpcException;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.youming.youche.conts.SysStaticDataEnum.APPLY_STATE.*;
import static com.youming.youche.record.common.AuditConsts.AUDIT_CODE.AUDIT_CODE_APPLY_VEHICLE;
import static com.youming.youche.record.common.AuditConsts.AUDIT_CODE.AUDIT_CODE_VEHICLE;
import static com.youming.youche.record.common.SysStaticDataEnum.AUTH_STATE.*;
import static com.youming.youche.record.common.SysStaticDataEnum.AUTO_AUDIT.AUTO_AUDIT1;
import static com.youming.youche.record.common.SysStaticDataEnum.IS_AUTH.IS_AUTH0;
import static com.youming.youche.record.common.SysStaticDataEnum.VEHICLE_CLASS.*;
import static com.youming.youche.record.common.SysStaticDataEnum.VER_STATE.VER_STATE_HIS;
import static com.youming.youche.util.DateUtil.DATETIME12_FORMAT;

/**
 * <p>
 * ????????? ???????????????
 * </p>
 *
 * @author Terry
 * @since 2021-11-22
 */
@DubboService(version = "1.0.0")
public class VehicleDataInfoServiceImpl extends BaseServiceImpl<VehicleDataInfoMapper, VehicleDataInfo>
        implements IVehicleDataInfoService {

    private static final Integer YES = 1;

    //0 : ??????????????????????????????????????????
    public static final Integer VER_STATE_N = 0;

    //1 : ??????????????????
    public static final Integer VER_STATE_Y = 1;

    //???????????????????????? 0??? 1???
    public static final Integer IS_AUTH1 = 1;

    @Resource
    IAuditCallBackService auditCallBackService;

    @Lazy
    @Resource
    private IVehicleDataInfoVerService iVehicleDataInfoVerService;

    @Lazy
    @Resource
    private ITenantVehicleRelService iTenantVehicleRelService;

    @Lazy
    @Resource
    private ITenantVehicleRelVerService iTenantVehicleRelVerService;
    @Lazy
    @Resource
    private ITenantVehicleCostRelVerService iTenantVehicleCostRelVerService;

    @Lazy
    @Resource
    private ITenantVehicleCostRelService iTenantVehicleCostRelService;

    @DubboReference(version = "1.0.0")
    ISysOperLogService sysOperLogService;

    @Lazy
    @Resource
    private ITenantUserRelService iTenantUserRelService;

    @Lazy
    @Resource
    private ITenantUserRelVerService iTenantUserRelVerService;

    @Lazy
    @Resource
    private UserDataInfoRecordMapper userDataInfoRecordMapper;

    @Lazy
    @Resource
    private TenantUserRelMapper tenantUserRelMapper;

    @Lazy
    @Resource
    private TenantUserRelVerMapper tenantUserRelVerMapper;

    @Lazy
    @Resource
    private TenantVehicleCertRelMapper tenantVehicleCertRelMapper;

    @Lazy
    @DubboReference(version = "1.0.0")
    ISysOrganizeService iSysOrganizeService;

    @Lazy
    @DubboReference(version = "1.0.0")
    IAuditService iAuditService;

    @Lazy
    @Resource
    private VehicleObjectLineMapper vehicleObjectLineMapper;

    @Lazy
    @Resource
    private VehicleLineRelMapper vehicleLineRelMapper;

    @Lazy
    @Resource
    private TenantVehicleRelMapper tenantVehicleRelMapper;

    @Lazy
    @Resource
    private VehicleDataInfoVerMapper vehicleDataInfoVerMapper;

    @Lazy
    @Resource
    private VehicleObjectLineVerMapper vehicleObjectLineVerMapper;

    @Lazy
    @Resource
    private VehicleLineRelVerMapper vehicleLineRelVerMapper;

    @Lazy
    @Resource
    private TenantVehicleCostRelVerMapper tenantVehicleCostRelVerMapper;

    @Lazy
    @Resource
    private TenantVehicleRelVerMapper tenantVehicleRelVerMapper;

    @Lazy
    @Resource
    private TenantVehicleCertRelVerMapper tenantVehicleCertRelVerMapper;

    @Lazy
    @Resource
    private VehicleOrderPositionInfoMapper vehicleOrderPositionInfoMapper;

    @Lazy
    @Resource
    private TenantVehicleCostRelMapper tenantVehicleCostRelMapper;

    @Lazy
    @Resource
    private IApplyRecordService applyRecordService;

    @Lazy
    @Resource
    private VehicleUpdateNotifyMapper vehicleUpdateNotifyMapper;

    @Lazy
    @DubboReference(version = "1.0.0")
    ISysCfgService sysCfgService;

    @Lazy
    @Resource
    private RedisUtil redisUtil;

    @Lazy
    @Resource
    private LoginUtils loginUtils;


    @Lazy
    @Resource
    private CityMapper cityMapper;

    @Lazy
    @Resource
    private DistrictMapper districtMapper;

    @Lazy
    @Resource
    private ProvinceMapper provinceMapper;

    @Lazy
    @DubboReference(version = "1.0.0")
    IAuditNodeInstService auditNodeInstService;

    @Lazy
    @DubboReference(version = "1.0.0")
    IAuditUserService auditUserService;

    @Lazy
    @DubboReference(version = "1.0.0")
    IAuditConfigService auditConfigService;

    @Lazy
    @DubboReference(version = "1.0.0")
    IAuditNodeConfigService auditNodeConfigService;

    @Lazy
    @DubboReference(version = "1.0.0")
    IAuditNodeRuleConfigService auditNodeRuleConfigService;

    @Lazy
    @DubboReference(version = "1.0.0")
    ISysStaticDataService isysStaticDataService;

    @Lazy
    @DubboReference(version = "1.0.0")
    IAuditNodeConfigVerService auditNodeConfigVerService;

    @Lazy
    @Resource
    private IUserDataInfoRecordService iUserDataInfoRecordService;

    @Lazy
    @DubboReference(version = "1.0.0")
    ISysSmsSendService sysSmsSendService;

    @Lazy
    @Resource
    IVehicleObjectLineService iVehicleObjectLineService;

    @Lazy
    @Resource
    IVehicleObjectLineVerService iVehicleObjectLineVerService;

    @Lazy
    @Resource
    IVehicleLineRelVerService iVehicleLineRelVerService;

    @Lazy
    @Resource
    IVehicleLineRelService iVehicleLineRelService;

    @Lazy
    @Resource
    OrderInfoMapper orderInfoMapper;

    @Lazy
    @Resource
    IUserReceiverInfoService iUserReceiverInfoService;

    @Lazy
    @Resource
    ITenantVehicleCertRelVerService iTenantVehicleCertRelVerService;

    @Lazy
    @Resource
    ITenantVehicleCertRelService iTenantVehicleCertRelService;

    @Lazy
    @Resource
    private VehicleDataInfoMapper vehicleDataInfoMapper;

    @Lazy
    @Resource
    private VehicleDataInfoiVoTransfer vehicleDataInfoiVoTransfer;

    @Lazy
    @Resource
    private IEtcMaintainService iEtcMaintainService;

    @Lazy
    @Resource
    private ITenantVehicleRelService tenantVehicleRelService;

    @Lazy
    @Resource
    private CmCustomerLineMapper cmCustomerLineMapper;

    @Lazy
    @Resource
    private CmCustomerInfoMapper customerInfoMapper;

    @Lazy
    @DubboReference(version = "1.0.0")
    ImportOrExportRecordsService importOrExportRecordsService;


    @Lazy
    @DubboReference(version = "1.0.0")
    ISysTenantDefService sysTenantDefService;
    @Lazy
    @Resource
    ISysUserService sysUserService;

    @Lazy
    @Resource
    private ReadisUtil readisUtil;

    @Lazy
    @Resource
    private ITenantUserRelService tenantUserRelService;

    @Lazy
    @Resource
    private IVehicleObjectLineVerService vehicleObjectLineVerService;

    @Lazy
    @Resource
    private IVehicleObjectLineService vehicleObjectLineService;

    @Lazy
    @Resource
    private IVehicleIdleService vehicleIdleService;

//    @Lazy
//    @Resource
//    private ITenantVehicleRelVerService tenantVehicleRelVerService;

    @DubboReference(version = "1.0.0")
    IAuditNodeInstService iAuditNodeInstService;
    @DubboReference(version = "1.0.0")
    IUserDataInfoService userDataInfoService;
    private static final Logger LOGGER = LoggerFactory.getLogger(VehicleDataInfoServiceImpl.class);


    @Override
    public VehicleInfoDto getAllVehicleInfo(Integer vehicleClass, Long vehicleCode, String plateNumbers, String accessToken) {

        //????????????
        if (vehicleCode == null || vehicleCode < 0) {
            if (StringUtils.isBlank(plateNumbers)) {
                throw new BusinessException("????????????????????????????????????");
            } else {
                VehicleDataInfo vehicleDataInfo = getVehicleDataInfo(plateNumbers);
                if (null == vehicleDataInfo) {
                    throw new BusinessException("??????????????????");
                }
                vehicleCode = vehicleDataInfo.getId();
            }
        }
        //?????????????????????????????????
        LoginInfo loginInfo = loginUtils.get(accessToken);
        QueryWrapper<TenantVehicleRel> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("vehicle_code", vehicleCode)
                .eq("TENANT_ID", loginInfo.getTenantId());
        List<TenantVehicleRel> list = tenantVehicleRelMapper.selectList(queryWrapper);
        TenantVehicleRel vehicleRel = new TenantVehicleRel();
        if (list != null && list.size() > 0) {
            vehicleRel = list.get(0);
        }
        if (vehicleRel != null) {
            Integer tenantVehicleClass = vehicleRel.getVehicleClass();
            if (vehicleRel.getVehicleClass() != vehicleClass) {
                vehicleClass = tenantVehicleClass;
            }

//            if (tenantVehicleClass.equals(vehicleClass)) {
//                //???????????????????????????????????????????????????????????????????????????
//                vehicleClass = tenantVehicleClass;
//            }
        }

        //???????????????????????????
        VehicleInfoDto tenantVehicleInfo = baseMapper.getTenantVehicleInfo(vehicleClass, vehicleCode, vehicleRel.getTenantId());
        if (null == tenantVehicleInfo) {
            return null;
        }
        //?????????????????????????????????
        QueryWrapper<TenantVehicleRel> queryWrapper1 = new QueryWrapper<>();
        queryWrapper1.eq("vehicle_code", vehicleCode).eq("vehicle_class", VEHICLE_CLASS1)
                .orderByDesc("id").last("limit 1");

        TenantVehicleRel vehicleByVehicleCode = tenantVehicleRelMapper.selectOne(queryWrapper1);
        if (vehicleByVehicleCode != null) {
            SysTenantDef sysTenantDef = sysTenantDefService.getById(vehicleByVehicleCode.getTenantId());
            if (sysTenantDef != null) {
                tenantVehicleInfo.setTenantId(sysTenantDef.getId());
                tenantVehicleInfo.setTenantName(sysTenantDef.getName());
                tenantVehicleInfo.setLinkAdminUser(sysTenantDef.getAdminUser() + "");
                tenantVehicleInfo.setAdminUser(sysTenantDef.getAdminUser());
                tenantVehicleInfo.setLinkman(sysTenantDef.getLinkMan());
                tenantVehicleInfo.setLinkPhone(sysTenantDef.getLinkPhone());


                SysTenantDef tenantDef = sysTenantDefService.getById(sysTenantDef.getId());
                if (!tenantDef.getId().equals(loginInfo.getTenantId())) {
                    //????????????????????????
                    tenantVehicleInfo.setOtherOwnCar(true);
                } else {
                    //????????????????????????
                    tenantVehicleInfo.setOtherOwnCar(false);
                }
            }
            tenantVehicleInfo.setIsOwnCar(1);
        } else {
            tenantVehicleInfo.setTenantId(null);
            tenantVehicleInfo.setTenantName(null);
            tenantVehicleInfo.setLinkman(null);
            tenantVehicleInfo.setLinkPhone(null);
            tenantVehicleInfo.setIsOwnCar(0);

            //??????????????????????????????????????????????????????
            UserDataInfo userDataInfo = null;
            List<TenantVehicleRel> relServiceTenantVehicleRelList = iTenantVehicleRelService.getTenantVehicleRelList(vehicleCode);
            for (TenantVehicleRel tenantVehicleRel : relServiceTenantVehicleRelList) {
                if (tenantVehicleRel.getDriverUserId() != null && tenantVehicleRel.getDriverUserId() > 0) {
                    userDataInfo = userDataInfoRecordMapper.selectById(tenantVehicleRel.getDriverUserId());
                    tenantVehicleInfo.setBelongDriverUserId(tenantVehicleRel.getDriverUserId());
                    if (userDataInfo != null) {
                        if (StringUtils.isNotBlank(userDataInfo.getLinkman())) {
                            tenantVehicleInfo.setBelongDriverUserName(userDataInfo.getLinkman());
                        }
                        if (StringUtils.isNotBlank(userDataInfo.getMobilePhone())) {
                            tenantVehicleInfo.setBelongDriverMobile(userDataInfo.getMobilePhone());
                        }
                    }
                    break;
                }
            }
        }
        try {
            SysStaticData aClass = readisUtil.getSysStaticData("VEHICLE_CLASS", tenantVehicleInfo.getVehicleClass().toString());
            if (aClass != null) {
                tenantVehicleInfo.setVehicleClassName(aClass.getCodeName());
            }
            SysStaticData staticData = readisUtil.getSysStaticData("LICENCE_TYPE", tenantVehicleInfo.getLicenceType().toString());
            if (staticData != null) {
                tenantVehicleInfo.setLicenceTypeName(staticData.getCodeName());
            }
            if (null != tenantVehicleInfo.getVehicleStatus()) {
                tenantVehicleInfo.setVehicleStatusName(SysStaticDataRedisUtils.getSysStaticDataById2String(redisUtil, "VEHICLE_STATUS", tenantVehicleInfo.getVehicleStatus()));
            }


            if (tenantVehicleInfo.getVehicleStatus() != null && tenantVehicleInfo.getVehicleStatus() == SysStaticDataEnum.VEHICLE_STATUS_ENUM.VEHICLE_CAR_BOX.getType()) {
                SysStaticData vehicleStatusSubtype = readisUtil.getSysStaticData("VEHICLE_STATUS_SUBTYPE", tenantVehicleInfo.getVehicleLength() + "");
                if (vehicleStatusSubtype != null) {
                    tenantVehicleInfo.setVehicleLengthName(vehicleStatusSubtype.getCodeName());
                }
            } else {
                tenantVehicleInfo.setVehicleLengthName(SysStaticDataRedisUtils.getSysStaticDataById2String(redisUtil, "VEHICLE_LENGTH", tenantVehicleInfo.getVehicleLength()));

            }
            SysStaticData sysStaticData = readisUtil.getSysStaticData("CUSTOMER_AUTH_STATE", tenantVehicleInfo.getAuthState().toString());
            if (sysStaticData != null) {
                tenantVehicleInfo.setAuthStateName(sysStaticData.getCodeName());
            }

            //??????????????????
            UserDataInfo mainUserData = userDataInfoRecordMapper.selectById(tenantVehicleInfo.getDriverUserId());
            if (null != mainUserData) {

                tenantVehicleInfo.setDriverUserName(mainUserData.getLinkman());
                tenantVehicleInfo.setDriverMobilePhone(mainUserData.getMobilePhone());

                LambdaQueryWrapper<TenantUserRel> lambda = new QueryWrapper<TenantUserRel>().lambda();
                lambda.eq(TenantUserRel::getUserId, tenantVehicleInfo.getDriverUserId())
                        .eq(TenantUserRel::getTenantId, loginInfo.getTenantId());
                List<TenantUserRel> userRels = iTenantUserRelService.list(lambda);

                Integer carUserType = null;
                if (userRels != null && userRels.size() > 0) {
                    carUserType = userRels.get(0).getCarUserType();
                }
                if (carUserType != null && carUserType > 0) {
                    tenantVehicleInfo.setDriverCarUserType(carUserType);
                    SysStaticData data = readisUtil.getSysStaticData("DRIVER_TYPE", carUserType.toString());
                    if (data != null) {
                        tenantVehicleInfo.setDriverCarUserTypeName(readisUtil.getSysStaticData("DRIVER_TYPE", carUserType.toString()).getCodeName());
                    }
                } else {
                    tenantVehicleInfo.setDriverCarUserType(-1);
                    tenantVehicleInfo.setDriverCarUserTypeName("????????????");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

//        Long loadEmptyOilCostLong = Long.parseLong(tenantVehicleInfo.getLoadEmptyOilCost());
//        Long loadFullOilCostLong = Long.parseLong(tenantVehicleInfo.getLoadFullOilCost());
//        tenantVehicleInfo.setLoadEmptyOilCost(divide(loadEmptyOilCostLong));
//        tenantVehicleInfo.setLoadFullOilCost(divide(loadFullOilCostLong));

        tenantVehicleInfo.setLoadEmptyOilCost(tenantVehicleInfo.getLoadEmptyOilCost());
        tenantVehicleInfo.setLoadFullOilCost(tenantVehicleInfo.getLoadFullOilCost());
        //????????????????????????,?????????????????????
        if (vehicleClass == VEHICLE_CLASS1 || vehicleClass == VEHICLE_CLASS2 || vehicleClass == VEHICLE_CLASS4) {
            long relId = tenantVehicleInfo.getRelId();
            TenantVehicleRelInfoDto vehicleRelInfoMap = tenantVehicleCertRelMapper.getTenantVehicleRelInfo(vehicleCode, loginInfo.getTenantId(), relId);
            if (null != vehicleRelInfoMap) {
//                 if(null != vehicleRelInfoMap.getPrice()){
//
//                 }
                //??????????????????
                tenantVehicleInfo.setTenantVehicleRelInfoDto(vehicleRelInfoMap);
            } else {
                tenantVehicleInfo.setTenantVehicleRelInfoDto(new TenantVehicleRelInfoDto());
            }

            //??????????????????
            Long orgId = tenantVehicleInfo.getOrgId();
            if (orgId != null && orgId > -1) {
                try {
                    tenantVehicleInfo.setOrgName(iSysOrganizeService.getCurrentTenantOrgNameById(loginInfo.getTenantId(), orgId));
                } catch (RpcException e) {
                    e.printStackTrace();
                }
            }
            //?????????
            Long userId = tenantVehicleInfo.getUserId();
            if (userId != null && userId > -1) {
                UserDataInfo userSV = userDataInfoRecordMapper.selectById(userId);
                if (userSV != null && StringUtils.isNotBlank(userSV.getLinkman())) {
                    String userName = userSV.getLinkman();
                    if (userName == null || userName.equals("")) {
                        userName = userSV.getMobilePhone();
                    }
                    tenantVehicleInfo.setUserName(userName);
                }
            }

            //???????????????????????????
            Long copilotDriverId = tenantVehicleInfo.getCopilotDriverId();
            if (null != copilotDriverId && copilotDriverId > 0) {
                UserDataInfo copilotDriver = iUserDataInfoRecordService.getById(copilotDriverId);
                if (null != copilotDriver) {
                    tenantVehicleInfo.setCopilotDriverUserName(copilotDriver.getLinkman());
                    tenantVehicleInfo.setCopilotDriverMobilePhone(copilotDriver.getMobilePhone());
                    LambdaQueryWrapper<TenantUserRel> lambda = new QueryWrapper<TenantUserRel>().lambda();
                    lambda.eq(TenantUserRel::getUserId, copilotDriver.getId())
                            .eq(TenantUserRel::getTenantId, loginInfo.getTenantId())
                            .orderByDesc(TenantUserRel::getId)
                            .last("limit 1");
                    List<TenantUserRel> userRels = tenantUserRelService.list(lambda);
                    if (userRels == null || userRels.isEmpty()) {
                        userRels = null;
                    }
                    if (userRels != null && userRels.size() > 1) {
                        throw new BusinessException("?????????????????????????????????????????????");
                    }
                    Integer carUserType = null;
                    if (userRels != null) {
                        TenantUserRel tenantUserRel = userRels.get(0);
                        carUserType = tenantUserRel.getCarUserType();
                    }
                    if (carUserType != null && carUserType > 0) {
                        tenantVehicleInfo.setCopilotDriverUserType(carUserType);
                        SysStaticData staticData = readisUtil.getSysStaticData("DRIVER_TYPE", carUserType.toString());
                        tenantVehicleInfo.setCopilotDriverUserTypeName(staticData.getCodeName());
                    } else {
                        tenantVehicleInfo.setCopilotDriverUserType(-1);
                        tenantVehicleInfo.setCopilotDriverUserTypeName("????????????");
                    }
                }
            }
            Long followDriverId = tenantVehicleInfo.getFollowDriverId();
            if (null != followDriverId && followDriverId > 0) {
                //????????????????????????
                UserDataInfo followDriver = iUserDataInfoRecordService.getById(followDriverId);
                if (null != followDriver) {
                    tenantVehicleInfo.setFollowDriverUserName(followDriver.getLinkman());
                    tenantVehicleInfo.setFollowDriverMobilePhone(followDriver.getMobilePhone());

                    TenantUserRel tenantUserRel = getTenantUserRel(followDriver.getId(), loginInfo.getTenantId());
                    if (tenantUserRel != null && tenantUserRel.getCarUserType() != null && tenantUserRel.getCarUserType().intValue() > 0) {
                        tenantVehicleInfo.setFollowDriverUserType(tenantUserRel.getCarUserType());
                        SysStaticData sysStaticData = readisUtil.getSysStaticData("DRIVER_TYPE", tenantUserRel.getCarUserType().toString());
                        if (sysStaticData != null) {
                            tenantVehicleInfo.setFollowDriverUserTypeName(sysStaticData.getCodeName());
                        }

                    } else {
                        tenantVehicleInfo.setFollowDriverUserType(-1);
                        tenantVehicleInfo.setFollowDriverUserTypeName("????????????");
                    }
                }
            }
        } else {
            Long ownTenantId = tenantVehicleInfo.getTenantId();
            SysTenantDef sysTenantDef = sysTenantDefService.getById(ownTenantId);
            //?????????????????????????????????
            if (null != sysTenantDef) {
                tenantVehicleInfo.setTenantName(sysTenantDef.getName());
                tenantVehicleInfo.setTenantLinkMan(sysTenantDef.getLinkMan());
                tenantVehicleInfo.setTenantLinkPhone(sysTenantDef.getLinkPhone());
            }
        }
        //??????????????????
//        List<Map> rtnList = getVehicleObjectLineMap(vehicleCode);
        List<VehicleObjectLineVo> vehicleObjectLine = getVehicleObjectLine(vehicleCode);
//        List<VehicleOjbectLineVo> lineVos = Lists.newArrayList();
//        if (rtnList != null && rtnList.size() > 0) {
//            for (Map map : rtnList) {
//                VehicleOjbectLineVo vehicleOjbectLineVo = new VehicleOjbectLineVo();
//                BeanUtil.copyProperties(map, vehicleOjbectLineVo);
//                lineVos.add(vehicleOjbectLineVo);
//            }
//        }
        tenantVehicleInfo.setVehicleOjbectLineArray(vehicleObjectLine);
        //??????????????????
        List<VehicleLineRel> vehicleLineRelList = vehicleLineRelMapper.getVehiclelineRels(vehicleCode, 1);
//        List<VehicleLineRelsVo> lineRelsVos = Lists.newArrayList();
//        if (vehicleLineRelList != null && vehicleLineRelList.size() > 0) {
//            for (VehicleLineRel vehicleLineRel : vehicleLineRelList) {
//                VehicleLineRelsVo vehicleLineRelsVo = new VehicleLineRelsVo();
//                BeanUtil.copyProperties(vehicleLineRel, vehicleLineRelsVo);
//                lineRelsVos.add(vehicleLineRelsVo);
//            }
//        }
        //???????????????
        Integer shareFlg = tenantVehicleInfo.getShareFlg();
        if (null != shareFlg && YES.equals(shareFlg)) {
            Long linkUserId = tenantVehicleInfo.getLinkUserId();
            if (null != linkUserId && linkUserId > -1) {
                UserDataInfo shareLinkUser = userDataInfoRecordMapper.selectById(tenantVehicleInfo.getLinkUserId());
                if (null != shareLinkUser) {
                    tenantVehicleInfo.setShareUserName(shareLinkUser.getLinkman());
                    tenantVehicleInfo.setShareMobilePhone(shareLinkUser.getMobilePhone());
                }
            }
        }
        if (null != tenantVehicleInfo.getVehicleClass()) {
            if (null != tenantVehicleInfo.getVehicleClass()) {
                SysStaticData vehicleData = readisUtil.getSysStaticData("VEHICLE_CLASS", tenantVehicleInfo.getVehicleClass().toString());
                if (vehicleData != null) {
                    tenantVehicleInfo.setVehicleClassName(vehicleData.getCodeName());
                }
            }
        }
//        if (tenantVehicleInfo.getLicenceType() != null && tenantVehicleInfo.getLicenceType() == 1) {
//            tenantVehicleInfo.setLicenceTypeName("??????");
//        } else {
//            tenantVehicleInfo.setLicenceTypeName("??????");
//        }
        if (null != tenantVehicleInfo.getShareFlg() && tenantVehicleInfo.getShareFlg() == SysStaticDataEnum.SHARE_FLG.NO) {
            tenantVehicleInfo.setShareFlgName("???");
        } else {
            tenantVehicleInfo.setShareFlgName("???");
        }
        tenantVehicleInfo.setVehiclelineRels(vehicleLineRelList);
        return tenantVehicleInfo;
    }

    @Override
    public VehicleInfoDto getAllVehicleInfoVerHis(Integer vehicleClass, Long vehicleCode, String plateNumbers, String accessToken) {
        //????????????
        if (vehicleCode == null || vehicleCode < 0) {
            if (StringUtils.isBlank(plateNumbers)) {
                throw new BusinessException("????????????????????????????????????");
            } else {
                VehicleDataInfo vehicleDataInfo = getVehicleDataInfo(plateNumbers);
                if (null == vehicleDataInfo) {
                    throw new BusinessException("??????????????????");
                }
                vehicleCode = vehicleDataInfo.getId();
            }
        }
        //?????????????????????????????????
        LoginInfo loginInfo = loginUtils.get(accessToken);
//        QueryWrapper<TenantVehicleRelVer> wrapper = new QueryWrapper<>();
//        wrapper.eq("vehicle_code", vehicleCode)
//                .eq("TENANT_ID", loginInfo.getTenantId());
//        List<TenantVehicleRelVer> tenantVehicleRelVers = tenantVehicleRelVerMapper.selectList(wrapper);
//        TenantVehicleRelVer vehicleRelVer = new TenantVehicleRelVer();
//        if (tenantVehicleRelVers != null && tenantVehicleRelVers.size() > 0) {
//            vehicleRelVer = tenantVehicleRelVers.get(0);
//        }
//        if (vehicleRelVer != null) {
//            Integer tenantVehicleClass = vehicleRelVer.getVehicleClass();
//            if (vehicleRelVer.getVehicleClass() == vehicleClass) {
//                tenantVehicleClass = tenantVehicleClass;
//            }
////            if (tenantVehicleClass.equals(vehicleClass)) {
////                //???????????????????????????????????????????????????????????????????????????
////                vehicleClass = tenantVehicleClass;
////            }
//            // ??????????????? ????????????????????????
//            QueryWrapper<TenantVehicleRel> queryWrapper = new QueryWrapper<>();
//            queryWrapper.eq("id",vehicleRelVer.getVehicleHisId())
//                    .eq("vehicle_code",vehicleRelVer.getVehicleCode())
//                    .eq("TENANT_ID",vehicleRelVer.getTenantId());
//            TenantVehicleRel tenantVehicleRel = tenantVehicleRelMapper.selectOne(queryWrapper);
//            if (tenantVehicleRel!=null){// ?????????????????????????????? ??????????????????
//                vehicleRelVer=null;
//            }
//        }

        //???????????????????????????
//        VehicleInfoDto tenantVehicleInfo = baseMapper.getAllVehicleInfoVerHis(vehicleClass, vehicleCode, vehicleRelVer.getTenantId());
        List<VehicleInfoDto> allVehicleInfoVerHis = baseMapper.getAllVehicleInfoVerHis(vehicleClass, vehicleCode, loginInfo.getTenantId());
        VehicleInfoDto tenantVehicleInfo = null;
        if (allVehicleInfoVerHis.size() > 0 && allVehicleInfoVerHis != null) {
            tenantVehicleInfo = allVehicleInfoVerHis.get(0);
        }
        if (null == tenantVehicleInfo) {
            return null;
        }
        //?????????????????????????????????
        QueryWrapper<TenantVehicleRel> queryWrapper1 = new QueryWrapper<>();
        queryWrapper1.eq("vehicle_code", vehicleCode).eq("vehicle_class", VEHICLE_CLASS1)
                .orderByDesc("id").last("limit 1");

        TenantVehicleRel vehicleByVehicleCode = tenantVehicleRelMapper.selectOne(queryWrapper1);
        if (vehicleByVehicleCode != null) {
            SysTenantDef sysTenantDef = sysTenantDefService.getById(loginInfo.getTenantId());
            if (sysTenantDef != null) {
                tenantVehicleInfo.setTenantId(sysTenantDef.getId());
                tenantVehicleInfo.setTenantName(sysTenantDef.getName());
                tenantVehicleInfo.setLinkman(sysTenantDef.getLinkMan());
                tenantVehicleInfo.setLinkPhone(sysTenantDef.getLinkPhone());
                tenantVehicleInfo.setAdminUser(sysTenantDef.getAdminUser());
                SysTenantDef tenantDef = sysTenantDefService.getById(sysTenantDef.getId());
                if (!tenantDef.getId().equals(loginInfo.getTenantId())) {
                    //????????????????????????
                    tenantVehicleInfo.setOtherOwnCar(true);
                } else {
                    //????????????????????????
                    tenantVehicleInfo.setOtherOwnCar(false);
                }
            }

            tenantVehicleInfo.setIsOwnCar(1);
        } else {
//            tenantVehicleInfo.setTenantId(null);
            tenantVehicleInfo.setTenantName(null);
            tenantVehicleInfo.setLinkman(null);
            tenantVehicleInfo.setLinkPhone(null);
            tenantVehicleInfo.setIsOwnCar(0);

            //??????????????????????????????????????????????????????
            UserDataInfo userDataInfo = null;
            List<TenantVehicleRel> relServiceTenantVehicleRelList = iTenantVehicleRelService.getTenantVehicleRelList(vehicleCode);
            for (TenantVehicleRel tenantVehicleRel : relServiceTenantVehicleRelList) {
                if (tenantVehicleRel.getDriverUserId() != null && tenantVehicleRel.getDriverUserId() > 0) {
                    userDataInfo = userDataInfoRecordMapper.selectById(tenantVehicleRel.getUserId());
                    tenantVehicleInfo.setBelongDriverUserId(tenantVehicleRel.getDriverUserId());
                    if (userDataInfo != null) {
                        if (StringUtils.isNotBlank(userDataInfo.getLinkman())) {
                            tenantVehicleInfo.setBelongDriverUserName(userDataInfo.getLinkman());
                        }
                        if (StringUtils.isNotBlank(userDataInfo.getMobilePhone())) {
                            tenantVehicleInfo.setBelongDriverMobile(userDataInfo.getMobilePhone());
                        }
                    }
                    break;
                }
            }
        }
        try {
            SysStaticData aClass = readisUtil.getSysStaticData("VEHICLE_CLASS", tenantVehicleInfo.getVehicleClass().toString());
            if (aClass != null) {
                tenantVehicleInfo.setVehicleClassName(aClass.getCodeName());
            }
            SysStaticData staticData = readisUtil.getSysStaticData("LICENCE_TYPE", tenantVehicleInfo.getLicenceType().toString());
            if (staticData != null) {
                tenantVehicleInfo.setLicenceTypeName(staticData.getCodeName());
            }
            if (null != tenantVehicleInfo.getVehicleStatus()) {
                tenantVehicleInfo.setVehicleStatusName(SysStaticDataRedisUtils.getSysStaticDataById2String(redisUtil, "VEHICLE_STATUS", tenantVehicleInfo.getVehicleStatus()));
            }


            if (tenantVehicleInfo.getVehicleStatus() != null && tenantVehicleInfo.getVehicleStatus() == SysStaticDataEnum.VEHICLE_STATUS_ENUM.VEHICLE_CAR_BOX.getType()) {
                SysStaticData vehicleStatusSubtype = readisUtil.getSysStaticData("VEHICLE_STATUS_SUBTYPE", tenantVehicleInfo.getVehicleLength() + "");
                if (vehicleStatusSubtype != null) {
                    tenantVehicleInfo.setVehicleLengthName(vehicleStatusSubtype.getCodeName());
                }
            } else {
                tenantVehicleInfo.setVehicleLengthName(SysStaticDataRedisUtils.getSysStaticDataById2String(redisUtil, "VEHICLE_LENGTH", tenantVehicleInfo.getVehicleLength()));

            }
            SysStaticData sysStaticData = readisUtil.getSysStaticData("CUSTOMER_AUTH_STATE", tenantVehicleInfo.getAuthState().toString());
            if (sysStaticData != null) {
                tenantVehicleInfo.setAuthStateName(sysStaticData.getCodeName());
            }

            //??????????????????
            UserDataInfo mainUserData = userDataInfoRecordMapper.selectById(tenantVehicleInfo.getDriverUserId());
            if (null != mainUserData) {

                tenantVehicleInfo.setDriverUserName(mainUserData.getLinkman());
                tenantVehicleInfo.setDriverMobilePhone(mainUserData.getMobilePhone());

                LambdaQueryWrapper<TenantUserRel> lambda = new QueryWrapper<TenantUserRel>().lambda();
                lambda.eq(TenantUserRel::getUserId, tenantVehicleInfo.getDriverUserId())
                        .eq(TenantUserRel::getTenantId, loginInfo.getTenantId());
                List<TenantUserRel> userRels = iTenantUserRelService.list(lambda);

                Integer carUserType = null;
                if (userRels != null && userRels.size() > 0) {
                    carUserType = userRels.get(0).getCarUserType();
                }
                if (carUserType != null && carUserType > 0) {
                    tenantVehicleInfo.setDriverCarUserType(carUserType);
                    SysStaticData data = readisUtil.getSysStaticData("DRIVER_TYPE", carUserType.toString());
                    if (data != null) {
                        tenantVehicleInfo.setDriverCarUserTypeName(readisUtil.getSysStaticData("DRIVER_TYPE", carUserType.toString()).getCodeName());
                    }
                } else {
                    tenantVehicleInfo.setDriverCarUserType(-1);
                    tenantVehicleInfo.setDriverCarUserTypeName("????????????");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
//        Long loadEmptyOilCostLong = Long.parseLong(tenantVehicleInfo.getLoadEmptyOilCost());
//        Long loadFullOilCostLong = Long.parseLong(tenantVehicleInfo.getLoadFullOilCost());
//        tenantVehicleInfo.setLoadEmptyOilCost(divide(loadEmptyOilCostLong));
//        tenantVehicleInfo.setLoadFullOilCost(divide(loadFullOilCostLong));

        tenantVehicleInfo.setLoadEmptyOilCost(tenantVehicleInfo.getLoadEmptyOilCost());
        tenantVehicleInfo.setLoadFullOilCost(tenantVehicleInfo.getLoadFullOilCost());
        //????????????????????????,?????????????????????
        if (vehicleClass == VEHICLE_CLASS1 || vehicleClass == VEHICLE_CLASS2 || vehicleClass == VEHICLE_CLASS4) {
            long relId = tenantVehicleInfo.getRelId();
            TenantVehicleRelInfoDto vehicleRelInfoMap = tenantVehicleCertRelMapper.getTenantVehicleRelInfo(vehicleCode, loginInfo.getTenantId(), relId);
            if (null != vehicleRelInfoMap) {
//                 if(null != vehicleRelInfoMap.getPrice()){
//
//                 }
                //??????????????????
                tenantVehicleInfo.setTenantVehicleRelInfoDto(vehicleRelInfoMap);
            } else {
                tenantVehicleInfo.setTenantVehicleRelInfoDto(new TenantVehicleRelInfoDto());
            }

            //??????????????????
            Long orgId = tenantVehicleInfo.getOrgId();
            if (orgId != null && orgId > -1) {
                try {
                    tenantVehicleInfo.setOrgName(iSysOrganizeService.getCurrentTenantOrgNameById(loginInfo.getTenantId(), orgId));
                } catch (RpcException e) {
                    e.printStackTrace();
                }
            }
            //?????????
            Long userId = tenantVehicleInfo.getUserId();
            if (userId != null && userId > -1) {
                UserDataInfo userSV = userDataInfoRecordMapper.selectById(userId);
                if (userSV != null && StringUtils.isNotBlank(userSV.getLinkman())) {
                    String userName = userSV.getLinkman();
                    if (userName == null || userName.equals("")) {
                        userName = userSV.getMobilePhone();
                    }
                    tenantVehicleInfo.setUserName(userName);
                }
            }
            //??????????????????
            UserDataInfo mainUserData = userDataInfoRecordMapper.selectById(tenantVehicleInfo.getDriverUserId());
            if (null != mainUserData) {

                tenantVehicleInfo.setDriverUserName(mainUserData.getLinkman());
                tenantVehicleInfo.setDriverMobilePhone(mainUserData.getMobilePhone());

                LambdaQueryWrapper<TenantUserRel> lambda = new QueryWrapper<TenantUserRel>().lambda();
                lambda.eq(TenantUserRel::getUserId, tenantVehicleInfo.getDriverUserId())
                        .eq(TenantUserRel::getTenantId, loginInfo.getTenantId());
                List<TenantUserRel> userRels = iTenantUserRelService.list(lambda);

                Integer carUserType = null;
                if (userRels != null && userRels.size() > 0) {
                    carUserType = userRels.get(0).getCarUserType();
                }
                if (carUserType != null && carUserType > 0) {
                    tenantVehicleInfo.setDriverCarUserType(carUserType);
                    SysStaticData data = readisUtil.getSysStaticData("DRIVER_TYPE", carUserType.toString());
                    if (data != null) {
                        tenantVehicleInfo.setDriverCarUserTypeName(readisUtil.getSysStaticData("DRIVER_TYPE", carUserType.toString()).getCodeName());
                    }
                } else {
                    tenantVehicleInfo.setDriverCarUserType(-1);
                    tenantVehicleInfo.setDriverCarUserTypeName("????????????");
                }
            }
            //???????????????????????????
            Long copilotDriverId = tenantVehicleInfo.getCopilotDriverId();
            if (null != copilotDriverId && copilotDriverId > 0) {
                UserDataInfo copilotDriver = iUserDataInfoRecordService.getById(copilotDriverId);
                if (null != copilotDriver) {
                    tenantVehicleInfo.setCopilotDriverUserName(copilotDriver.getLinkman());
                    tenantVehicleInfo.setCopilotDriverMobilePhone(copilotDriver.getMobilePhone());
                    LambdaQueryWrapper<TenantUserRel> lambda = new QueryWrapper<TenantUserRel>().lambda();
                    lambda.eq(TenantUserRel::getUserId, copilotDriver.getId())
                            .eq(TenantUserRel::getTenantId, loginInfo.getTenantId())
                            .orderByDesc(TenantUserRel::getId)
                            .last("limit 1");
                    List<TenantUserRel> userRels = tenantUserRelService.list(lambda);
//                    if (tenantVehicleRelVers == null || tenantVehicleRelVers.isEmpty()) {
//                        userRels = null;
//                    }
//                    if (userRels.size() > 1) {
//                        throw new BusinessException("?????????????????????????????????????????????");
//                    }
                    TenantUserRel tenantUserRel = userRels.get(0);
                    if (tenantUserRel.getCarUserType() != null && tenantUserRel.getCarUserType() > 0) {
                        tenantVehicleInfo.setCopilotDriverUserType(tenantUserRel.getCarUserType());
                        SysStaticData staticData = readisUtil.getSysStaticData("DRIVER_TYPE", tenantUserRel.getCarUserType().toString());
                        tenantVehicleInfo.setCopilotDriverUserTypeName(staticData.getCodeName());
                    } else {
                        tenantVehicleInfo.setCopilotDriverUserType(-1);
                        tenantVehicleInfo.setCopilotDriverUserTypeName("????????????");
                    }
                }
                Long followDriverId = tenantVehicleInfo.getFollowDriverId();
                if (null != followDriverId && followDriverId > 0) {
                    //????????????????????????
                    UserDataInfo followDriver = iUserDataInfoRecordService.getById(followDriverId);
                    if (null != followDriver) {
                        tenantVehicleInfo.setFollowDriverUserName(followDriver.getLinkman());
                        tenantVehicleInfo.setFollowDriverMobilePhone(followDriver.getMobilePhone());

                        TenantUserRel tenantUserRel = getTenantUserRel(followDriver.getId(), loginInfo.getTenantId());
                        if (tenantUserRel != null && tenantUserRel.getCarUserType() != null && tenantUserRel.getCarUserType().intValue() > 0) {
                            tenantVehicleInfo.setFollowDriverUserType(tenantUserRel.getCarUserType());
                            SysStaticData sysStaticData = readisUtil.getSysStaticData("DRIVER_TYPE", tenantUserRel.getCarUserType().toString());
                            if (sysStaticData != null) {
                                tenantVehicleInfo.setFollowDriverUserTypeName(sysStaticData.getCodeName());
                            }

                        } else {
                            tenantVehicleInfo.setFollowDriverUserType(-1);
                            tenantVehicleInfo.setFollowDriverUserTypeName("????????????");
                        }
                    }

                    //???????????????
                    Integer shareFlg = tenantVehicleInfo.getShareFlg();
                    if (null != shareFlg && YES.equals(shareFlg)) {
                        Long linkUserId = tenantVehicleInfo.getLinkUserId();
                        if (null != linkUserId && linkUserId > -1) {
                            UserDataInfo shareLinkUser = userDataInfoRecordMapper.selectById(tenantVehicleInfo.getLinkUserId());
                            if (null != shareLinkUser) {
                                tenantVehicleInfo.setShareUserName(shareLinkUser.getLinkman());
                                tenantVehicleInfo.setShareMobilePhone(shareLinkUser.getLinkman());
                            }
                        }
                    }
                }
            }
        } else {
            Long ownTenantId = tenantVehicleInfo.getTenantId();
            SysTenantDef sysTenantDef = sysTenantDefService.getById(ownTenantId);
            //?????????????????????????????????
            if (null != sysTenantDef) {
                tenantVehicleInfo.setTenantName(sysTenantDef.getName());
                tenantVehicleInfo.setTenantLinkMan(sysTenantDef.getLinkMan());
                tenantVehicleInfo.setTenantLinkPhone(sysTenantDef.getLinkPhone());
            }
        }
        //??????????????????
//        List<Map> rtnList = getVehicleObjectLineMap(vehicleCode);
        List<VehicleObjectLineVo> vehicleObjectLine = getVehicleObjectLine(vehicleCode);
//        List<VehicleOjbectLineVo> lineVos = Lists.newArrayList();
//        if (rtnList != null && rtnList.size() > 0) {
//            for (Map map : rtnList) {
//                VehicleOjbectLineVo vehicleOjbectLineVo = new VehicleOjbectLineVo();
//                BeanUtil.copyProperties(map, vehicleOjbectLineVo);
//                lineVos.add(vehicleOjbectLineVo);
//            }
//        }
        tenantVehicleInfo.setVehicleOjbectLineArray(vehicleObjectLine);
        //??????????????????
        List<VehicleLineRel> vehicleLineRelList = vehicleLineRelMapper.getVehiclelineRels(vehicleCode, 1);
//        List<VehicleLineRelsVo> lineRelsVos = Lists.newArrayList();
//        if (vehicleLineRelList != null && vehicleLineRelList.size() > 0) {
//            for (VehicleLineRel vehicleLineRel : vehicleLineRelList) {
//                VehicleLineRelsVo vehicleLineRelsVo = new VehicleLineRelsVo();
//                BeanUtil.copyProperties(vehicleLineRel, vehicleLineRelsVo);
//                lineRelsVos.add(vehicleLineRelsVo);
//            }
//        }
        if (null != tenantVehicleInfo.getVehicleClass()) {
            if (null != tenantVehicleInfo.getVehicleClass()) {
                SysStaticData vehicleData = readisUtil.getSysStaticData("VEHICLE_CLASS", tenantVehicleInfo.getVehicleClass().toString());
                if (vehicleData != null) {
                    tenantVehicleInfo.setVehicleClassName(vehicleData.getCodeName());
                }
            }
        }
//        if (tenantVehicleInfo.getLicenceType() != null && tenantVehicleInfo.getLicenceType() == 1) {
//            tenantVehicleInfo.setLicenceTypeName("??????");
//        } else {
//            tenantVehicleInfo.setLicenceTypeName("??????");
//        }
        if (null != tenantVehicleInfo.getShareFlg() && tenantVehicleInfo.getShareFlg() == SysStaticDataEnum.SHARE_FLG.NO) {
            tenantVehicleInfo.setShareFlgName("???");
        } else {
            tenantVehicleInfo.setShareFlgName("???");
        }
        tenantVehicleInfo.setVehiclelineRels(vehicleLineRelList);
        return tenantVehicleInfo;
    }

    public static String divide(long value) {
        if (-1 == value) {
            return "";
        }
        BigDecimal bd = new BigDecimal(100);
        return (new BigDecimal(value).divide(bd).toString());
    }

    @SneakyThrows
    @Override
    @Transactional
    public boolean doUpdateVehicleInfo(VehicleInfoUptVo vehicleInfoUptVo, String accessToken) throws BusinessException {
        Long vehicleCode = vehicleInfoUptVo.getVehicleCode();
        Integer vehicleClassIn = vehicleInfoUptVo.getVehicleClass();
        //?????????????????????id
        Long tenantVehicleRelId = vehicleInfoUptVo.getRelId();
        //???????????????????????????id
        Long tenantVehicleCostRelId = vehicleInfoUptVo.getTenantVehicleCostRelId();
        //??????id
        Long tenantVehicleCertRelId = null;
        if (vehicleInfoUptVo.getTenantVehicleCertRelId() != null) {
            tenantVehicleCertRelId = vehicleInfoUptVo.getTenantVehicleCertRelId();
        }
        if (vehicleCode == null && vehicleCode < 0) {
            throw new BusinessException("?????????????????????");
        }
        if (vehicleClassIn == null && vehicleClassIn < 0) {
            throw new BusinessException("?????????????????????");
        }
        VehicleDataInfo vehicleDataInfo = baseMapper.selectById(vehicleCode);
        if (vehicleDataInfo == null) {
            throw new BusinessException("????????????????????????");
        }
        Boolean isUpdatePlateNumber = false;
        String plateNumber = vehicleDataInfo.getPlateNumber();
        String plateNumberOld = "";
        String plateNumberNew = vehicleInfoUptVo.getPlateNumber();
        if (!plateNumber.equals(plateNumberNew) && StringUtils.isNotEmpty(plateNumber)) {
            plateNumberOld = plateNumber;
            plateNumber = plateNumberNew;
            isUpdatePlateNumber = true;
        }
        TenantVehicleRel tenantVehicleRel = tenantVehicleRelMapper.selectById(tenantVehicleRelId);
        if (null == tenantVehicleRel) {
            throw new BusinessException("????????????????????????????????????");
        }

        //????????????????????????,?????????????????????
        Boolean isImmediateEffect = false;
        if (tenantVehicleRel.getAuthState() != null && AUTH_STATE2 != tenantVehicleRel.getAuthState()) {
            isImmediateEffect = true;
        }
        LoginInfo loginInfo = loginUtils.get(accessToken);
        //????????????????????????ver?????????????????????????????????  1
        updateVehicleVerAllVerState(VER_STATE_N, vehicleCode, loginInfo.getTenantId());

        VehicleDataInfoVer vehicleDataInfoVerNew = new VehicleDataInfoVer();
        vehicleDataInfoVerNew.setTenantId(null);
        BeanUtil.copyProperties(vehicleDataInfo, vehicleDataInfoVerNew);
        BeanUtil.copyProperties(vehicleInfoUptVo, vehicleDataInfoVerNew);
        vehicleDataInfoVerNew.setVehicleCode(vehicleDataInfo.getId());
        vehicleDataInfoVerNew.setVerState(VER_STATE_Y);
        vehicleDataInfoVerNew.setPlateNumber(plateNumber);
        vehicleDataInfoVerNew.setCreateTime(LocalDateTime.now());
        vehicleDataInfoVerNew.setAdrivingLicenseCopy(vehicleInfoUptVo.getAdriverLicenseCopy());
        if (vehicleClassIn != VEHICLE_CLASS1) {
            vehicleDataInfoVerNew.setTenantId(null);
        }
        if (vehicleDataInfoVerNew.getLicenceType() != null && vehicleDataInfoVerNew.getLicenceType() == SysStaticDataEnum.LICENCE_TYPE.TT) {
            vehicleDataInfoVerNew.setVehicleStatus(null);
            vehicleDataInfoVerNew.setVehicleLength(null);
            vehicleDataInfoVerNew.setVehicleLoad(null);
            vehicleDataInfoVerNew.setLightGoodsSquare(null);
        }
        int i = vehicleDataInfoVerMapper.insert(vehicleDataInfoVerNew);

        //?????????????????????
        tenantVehicleRel.setIsAuth(IS_AUTH1);
        vehicleDataInfo.setIsAuth(IS_AUTH1);

        TenantVehicleRelVer tenantVehicleRelVerNew = new TenantVehicleRelVer();
        BeanUtil.copyProperties(tenantVehicleRel, tenantVehicleRelVerNew);
        BeanUtil.copyProperties(vehicleInfoUptVo, tenantVehicleRelVerNew);
        tenantVehicleRelVerNew.setLoadEmptyPilCost(vehicleInfoUptVo.getLoadEmptyOilCost());
        tenantVehicleRelVerNew.setLoadFullPilCost(vehicleInfoUptVo.getLoadFullOilCost());

        tenantVehicleRelVerNew.setDriverUserId(vehicleDataInfoVerNew.getDriverUserId());
        tenantVehicleRelVerNew.setVerState(VER_STATE_Y);
        tenantVehicleRelVerNew.setPlateNumber(plateNumber);
        tenantVehicleRelVerNew.setVehicleCode(tenantVehicleRel.getVehicleCode());
        tenantVehicleRelVerNew.setRelId(tenantVehicleRel.getId());
        tenantVehicleRelVerNew.setTenantId(loginInfo.getTenantId());
        tenantVehicleRelVerNew.setVehicleHisId(vehicleDataInfoVerNew.getId());
        tenantVehicleRelVerNew.setCreateTime(LocalDateTime.now());
        if (vehicleInfoUptVo.getShareFlg() != null) {
            tenantVehicleRelVerNew.setShareFlg(vehicleInfoUptVo.getShareFlg());
        }
        if (vehicleInfoUptVo.getIsUseCarOilCost() != null) {
            tenantVehicleRelVerNew.setIsUserCarOilCost(vehicleInfoUptVo.getIsUseCarOilCost());
        }
        //???????????????????????????????????????????????????
        if (vehicleClassIn == VEHICLE_CLASS1 || vehicleClassIn == VEHICLE_CLASS5) {
            tenantVehicleRelVerNew.setBillReceiverMobile(null);
            tenantVehicleRelVerNew.setBillReceiverUserId(null);
            tenantVehicleRelVerNew.setBillReceiverName(null);
        } else {
            tenantVehicleRelVerNew.setBillReceiverMobile(vehicleInfoUptVo.getBillReceiverMobile());
            tenantVehicleRelVerNew.setBillReceiverName(vehicleInfoUptVo.getBillReceiverName());
            tenantVehicleRelVerNew.setBillReceiverUserId(vehicleInfoUptVo.getBillReceiverUserId());
        }
        int i1 = tenantVehicleRelVerMapper.insert(tenantVehicleRelVerNew);
        if ((vehicleClassIn == VEHICLE_CLASS5) || (null != tenantVehicleRelVerNew.getShareFlg() && tenantVehicleRelVerNew.getShareFlg().equals(YES))) {
            doSaveVehicleOrderPositionInfo(tenantVehicleRel.getId(), vehicleDataInfo.getPlateNumber());
        }
        //???????????????
        List<VehicleObjectLineVo> list = vehicleInfoUptVo.getVehicleOjbectLineArray();
        String vehicleObjectLineVerHisIds = "";

        if (list != null) {
            try {
                vehicleObjectLineVerHisIds = this.dealVehicleObjectLine(list, vehicleDataInfo, vehicleDataInfoVerNew, isImmediateEffect, false, accessToken);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        //????????????
        List<VehicleLineRelsVo> linelist = vehicleInfoUptVo.getVehicleLineRels();
        String vehicleLineRelVerHisIds = "";
        if (null != linelist) {
            for (int j = 0; j < linelist.size(); j++) {
                VehicleLineRelsVo vehicleLineRelsVo = linelist.get(j);
                Number lineId = null;
                try {
                    lineId = NumberFormat.getInstance().parse(vehicleLineRelsVo.getLineId().toString());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                String lineCodeRule = vehicleLineRelsVo.getLineCodeRule();
                Number lineType = null;
                try {
                    lineType = NumberFormat.getInstance().parse(vehicleLineRelsVo.getState().toString());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Long relId = tenantVehicleRelId;

                VehicleLineRelVer vehicleLineRelVer = new VehicleLineRelVer();
                vehicleLineRelVer.setRelId(relId);
                vehicleLineRelVer.setLineCodeRule(lineCodeRule);
                vehicleLineRelVer.setLineId(lineId == null ? null : lineId.longValue());
                vehicleLineRelVer.setPlateNumber(plateNumber);
                vehicleLineRelVer.setVehicleCode(vehicleDataInfo.getId());
                vehicleLineRelVer.setState(lineType == null ? null : lineType.intValue());
                vehicleLineRelVer.setOpId(loginInfo.getId());
                vehicleLineRelVer.setOpDate(LocalDateTime.now());
                vehicleLineRelVer.setUpdateTime(LocalDateTime.now());
                vehicleLineRelVer.setUpdateOpId(loginInfo.getId());
                vehicleLineRelVer.setVerState(VER_STATE_Y);
                vehicleLineRelVer.setVehicleHisId(vehicleDataInfoVerNew.getId());
                vehicleLineRelVerMapper.insert(vehicleLineRelVer);
                if (j == (list.size() - 1)) {
                    vehicleLineRelVerHisIds = vehicleLineRelVerHisIds + vehicleLineRelVer.getId();
                } else {
                    vehicleLineRelVerHisIds = vehicleLineRelVerHisIds + vehicleLineRelVer.getId() + ",";
                }
            }

        }

        Long tenantVehicleCostRelVerHisId = -1L;
        Long tenantVehicleCertRelVerHisId = -1L;
        //???????????????????????? ????????????????????????????????????
        if (VEHICLE_CLASS1 == vehicleClassIn || VEHICLE_CLASS2 == vehicleClassIn || VEHICLE_CLASS4 == vehicleClassIn) {

            TenantVehicleCostRelVer tenantVehicleCostRelVer = new TenantVehicleCostRelVer();
            BeanUtil.copyProperties(vehicleInfoUptVo, tenantVehicleCostRelVer);

            tenantVehicleCostRelVer.setCreateTime(LocalDateTime.now());

            //?????????????????????????????? ?????????,????????????,????????????,????????????,????????????,????????????,???????????????
            if (SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS2 == vehicleClassIn || SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS4 == vehicleClassIn) {
                //??????
                tenantVehicleCostRelVer.setResidual(null);
                //????????????
                tenantVehicleCostRelVer.setDepreciatedMonth(null);
                //?????????
                tenantVehicleCostRelVer.setInsuranceFee(null);
                //?????????
                tenantVehicleCostRelVer.setExamVehicleFee(null);
                //?????????
                tenantVehicleCostRelVer.setMaintainFee(null);
                //????????????
                tenantVehicleCostRelVer.setRepairFee(null);
                //????????????
                tenantVehicleCostRelVer.setTyreFee(null);
            } else if (SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1 == vehicleClassIn) {
                //???????????????????????????
                //?????????
                tenantVehicleCostRelVer.setManagementCost(null);
            }
            //??????????????????
            if (tenantVehicleCostRelId == null || tenantVehicleCostRelId < 0) {
                TenantVehicleCostRel costRel = new TenantVehicleCostRel();
                costRel.setVehicleCode(tenantVehicleCostRelVer.getVehicleCode());
                costRel.setPlateNumber(tenantVehicleRel.getPlateNumber());
                costRel.setTenantId(tenantVehicleCostRelVer.getTenantId());
                costRel.setRelId(tenantVehicleRel.getId());
                tenantVehicleCostRelMapper.insert(costRel);
                tenantVehicleCostRelId = costRel.getId();
            }

            tenantVehicleCostRelVer.setId(tenantVehicleCostRelId);
            tenantVehicleCostRelVer.setTenantId(loginInfo.getTenantId());
            tenantVehicleCostRelVer.setRelId(tenantVehicleRel.getId());
            tenantVehicleCostRelVer.setVerState(VER_STATE_Y);
            tenantVehicleCostRelVer.setVehicleCode(vehicleDataInfo.getId());

            //???????????????
            TenantVehicleCertRelVer tenantVehicleCertRelVer = new TenantVehicleCertRelVer();
            BeanUtil.copyProperties(vehicleInfoUptVo, tenantVehicleCertRelVer);
            tenantVehicleCertRelVer.setSeasonalVeriTimeEnd(vehicleInfoUptVo.getSeasonalVeriTimeEnd());
            tenantVehicleCertRelVer.setCreateTime(LocalDateTime.now());

            //??????????????????
            if (tenantVehicleCertRelId == null || tenantVehicleCertRelId < 0) {
                TenantVehicleCertRel certRel = new TenantVehicleCertRel();
                certRel.setVehicleCode(tenantVehicleCostRelVer.getVehicleCode());
                certRel.setTenantId(tenantVehicleCostRelVer.getTenantId());
                certRel.setPlateNumber(tenantVehicleRel.getPlateNumber());
                certRel.setRelId(tenantVehicleRel.getId());
                certRel.setCreateTime(LocalDateTime.now());
                tenantVehicleCertRelMapper.insert(certRel);
                tenantVehicleCertRelId = certRel.getId();
            }

            tenantVehicleCertRelVer.setId(tenantVehicleCertRelId);
            tenantVehicleCertRelVer.setTenantId(loginInfo.getTenantId());
            tenantVehicleCertRelVer.setRelId(tenantVehicleRel.getId());
            tenantVehicleCertRelVer.setVerState(VER_STATE_Y);
            tenantVehicleCertRelVer.setVehicleCode(vehicleDataInfo.getId());

            if (isImmediateEffect) {
                //???????????????????????????
                //????????????
                TenantVehicleCostRel tenantVehicleCostRelNew = null;
                if (tenantVehicleCostRelId > 0) {
                    tenantVehicleCostRelNew = tenantVehicleCostRelMapper.selectById(tenantVehicleCostRelId);
                    BeanUtil.copyProperties(tenantVehicleCostRelVer, tenantVehicleCostRelNew);
                    tenantVehicleCostRelMapper.updateById(tenantVehicleCostRelNew);
                }
                if (tenantVehicleCostRelNew == null) {
                    tenantVehicleCostRelNew = new TenantVehicleCostRel();
                    BeanUtil.copyProperties(tenantVehicleCostRelVer, tenantVehicleCostRelNew);
                    tenantVehicleCostRelMapper.insert(tenantVehicleCostRelNew);
                    tenantVehicleCostRelVer.setId(tenantVehicleCostRelNew.getId());
                }


                //????????????
                TenantVehicleCertRel tenantVehicleCertRelNew = null;
                if (tenantVehicleCertRelId > 0) {
                    tenantVehicleCertRelNew = tenantVehicleCertRelMapper.selectById(tenantVehicleCertRelId);
                    BeanUtil.copyProperties(tenantVehicleCertRelVer, tenantVehicleCertRelNew);
                    tenantVehicleCertRelMapper.updateById(tenantVehicleCertRelNew);
                }

                if (tenantVehicleCertRelNew == null) {
                    tenantVehicleCertRelNew = new TenantVehicleCertRel();
                    BeanUtil.copyProperties(tenantVehicleCertRelVer, tenantVehicleCertRelNew);
                    tenantVehicleCertRelMapper.insert(tenantVehicleCertRelNew);
                    //???tenantVehicleCertRel???????????????????????????id?????????VER???
                    tenantVehicleCertRelVer.setId(tenantVehicleCertRelNew.getId());
                }


                //???????????? //????????????
                vehicleDataInfoVerNew.setCreateTime(vehicleDataInfo.getCreateDate());
                Long id = vehicleDataInfo.getId();
                BeanUtil.copyProperties(vehicleDataInfoVerNew, vehicleDataInfo);
                vehicleDataInfo.setId(id);
                vehicleDataInfo.setAuthState(AUTH_STATE1);
                vehicleDataInfo.setIsAuth(VER_STATE_Y);
                if (vehicleInfoUptVo.getAdriverLicenseCopy() != null) {
                    vehicleDataInfo.setAdriverLicenseCopy(vehicleInfoUptVo.getAdriverLicenseCopy());
                }

                update(vehicleDataInfo);
                //????????????????????? //??????
                tenantVehicleRelVerNew.setCreateTime(tenantVehicleRel.getCreateTime());
                Long relId = tenantVehicleRel.getId();
                BeanUtil.copyProperties(tenantVehicleRelVerNew, tenantVehicleRel);
                tenantVehicleRel.setId(relId);
                tenantVehicleRel.setVehicleCode(vehicleDataInfo.getId());
                tenantVehicleRel.setAuthState(AUTH_STATE1);
                tenantVehicleRel.setIsAuth(IS_AUTH1);
                if (vehicleInfoUptVo.getLoadEmptyOilCost() != null) {
                    tenantVehicleRel.setLoadEmptyOilCost(vehicleInfoUptVo.getLoadEmptyOilCost());
                }
                if (vehicleInfoUptVo.getLoadFullOilCost() != null) {
                    tenantVehicleRel.setLoadFullOilCost(vehicleInfoUptVo.getLoadFullOilCost());
                }
                tenantVehicleRelService.updateById(tenantVehicleRel);
//                if(vehicleInfoUptVo.getUserId() != null){
//                    tenantVehicleRel.setUserId(vehicleInfoUptVo.getUserId());
//                    LambdaUpdateWrapper<TenantVehicleRel> lambda =Wrappers.lambdaUpdate();
//                    lambda.set(TenantVehicleRel::getUserId,vehicleInfoUptVo.getUserId())
//                          .eq(TenantVehicleRel::getId,tenantVehicleRel.getId());
//                    tenantVehicleRelService.update(lambda);
//                }
            }

            tenantVehicleCostRelVer.setCreateTime(LocalDateTime.now());
            tenantVehicleCostRelVer.setVehicleCode(vehicleCode);
            tenantVehicleCostRelVerMapper.insert(tenantVehicleCostRelVer);
            tenantVehicleCostRelVerHisId = tenantVehicleCostRelVer.getId();

            tenantVehicleCertRelVer.setCreateTime(LocalDateTime.now());
            tenantVehicleCertRelVer.setVehicleCode(vehicleCode);
            tenantVehicleCertRelVerMapper.insert(tenantVehicleCertRelVer);
            tenantVehicleCertRelVerHisId = tenantVehicleCertRelVer.getId();

        } else {

            //???????????????,??????????????????,????????????
            boolean isAutoAudit = sysCfgService.getCfgBooleanVal("IS_AUTO_AUDIT", -1);
            if (isAutoAudit && isImmediateEffect) {

                vehicleDataInfoVerNew.setVerState(VER_STATE_Y);
                vehicleDataInfoVerNew.setCreateTime(vehicleDataInfo.getCreateDate());
                vehicleDataInfoVerNew.setVehicleCode(vehicleCode);
                vehicleDataInfoVerMapper.insert(vehicleDataInfoVerNew);
                Long id = vehicleDataInfo.getId();
                BeanUtil.copyProperties(vehicleDataInfoVerNew, vehicleDataInfo);
                vehicleDataInfo.setId(id);
                vehicleDataInfo.setAuthState(AUTH_STATE2);
                vehicleDataInfo.setIsAuth(IS_AUTH0);
                vehicleDataInfo.setTenantId(null);
                this.saveOrUpdate(vehicleDataInfo);

                saveSysOperLog(SysOperLogConst.BusiCode.Vehicle, tenantVehicleRel.getId(), SysOperLogConst.OperType.Audit, "?????????-" + vehicleDataInfoVerNew.getPlateNumber() + "-????????????????????????????????????", accessToken);
                //?????????????????????,?????????????????? //??????
                tenantVehicleRelVerNew.setCreateTime(tenantVehicleRel.getCreateTime());
                Long relId = tenantVehicleRel.getId();
                BeanUtil.copyProperties(tenantVehicleRelVerNew, tenantVehicleRel);
                tenantVehicleRel.setId(relId);
                tenantVehicleRel.setAuthState(AUTH_STATE1);
                tenantVehicleRel.setIsAuth(IS_AUTH1);
                tenantVehicleRelService.saveOrUpdate(tenantVehicleRel);
                //?????????????????????
                addPtTenantVehicleRel(tenantVehicleRel.getId());
            }

        }
       /* //??????????????????
        Map iMap = new HashMap();
        iMap.put("svName", "vehicleTF");
        iMap.put("vehicleDataInfoVerHisId", vehicleDataInfoVerNew.getId());
        iMap.put("vehicleObjectLineVerHisIds", vehicleObjectLineVerHisIds);
        iMap.put("vehicleLineRelVerHisIds", vehicleLineRelVerHisIds);
        iMap.put("tenantVehicleRelVerHisId", tenantVehicleRelVerNew.getId());
        iMap.put("tenantVehicleCostRelVerHisId", tenantVehicleCostRelVerHisId);
        iMap.put("tenantVehicleCertRelVerHisId", tenantVehicleCertRelVerHisId);

        String logMsg = "??????" + vehicleDataInfo.getPlateNumber() + "????????????";
        if (isUpdatePlateNumber) {
            logMsg = "?????????[" + plateNumberOld + "]??????[" + plateNumberNew + "]";
        }
        saveSysOperLog(SysOperLogConst.BusiCode.Vehicle, tenantVehicleRel.getId(), SysOperLogConst.OperType.Update, logMsg, loginInfo.getTenantId(), accessToken);


        boolean bool = iAuditOutTF.startProcess(AUDIT_CODE_VEHICLE, tenantVehicleRel.getId(), SysOperLogConst.BusiCode.Vehicle, iMap, loginInfo.getTenantId());
        if (!bool) {
            throw new BusinessException("???????????????????????????");
        }
        String logMsg = "??????" + vehicleDataInfo.getPlateNumber() + "????????????";
        saveSysOperLog(SysOperLogConst.BusiCode.Vehicle, tenantVehicleRel.getId(), SysOperLogConst.OperType.Update, logMsg, loginInfo.getTenantId(), accessToken);
*/

        //??????????????????
        if (loginInfo.getTenantId() > -1) {
            Map iMap = new HashMap();
            iMap.put("svName", "vehicleTF");
            iMap.put("vehicleDataInfoVerHisId", vehicleDataInfoVerNew.getId());
            iMap.put("vehicleObjectLineVerHisIds", vehicleObjectLineVerHisIds);
            iMap.put("vehicleLineRelVerHisIds", vehicleLineRelVerHisIds);
            iMap.put("tenantVehicleRelVerHisId", tenantVehicleRelVerNew.getId());
            iMap.put("tenantVehicleCostRelVerHisId", tenantVehicleCostRelVerHisId);
            iMap.put("tenantVehicleCertRelVerHisId", tenantVehicleCertRelVerHisId);
            boolean bool = iAuditService.startProcess(AUDIT_CODE_VEHICLE, tenantVehicleRel.getId(), SysOperLogConst.BusiCode.Vehicle, iMap, accessToken, loginInfo.getTenantId());
            if (!bool) {
                log.error("???????????????????????????");
//                return ResponseResult.failure("???????????????????????????");
                throw new BusinessException("????????????????????????");
            }
        }
        String logMsg = "??????" + vehicleDataInfo.getPlateNumber() + "????????????";
        saveSysOperLog(SysOperLogConst.BusiCode.Vehicle, tenantVehicleRel.getId(), SysOperLogConst.OperType.Update, logMsg, loginInfo);
        return true;
    }

    @Override
    @Transactional(rollbackFor = {RuntimeException.class, Error.class})
    public AuditDto doSaveVehicleInfo(VehicleInfoVo vehicleInfoVo, String accessToken) throws Exception {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        String plateNumber = vehicleInfoVo.getPlateNumber();
        int vehicleClassIn = vehicleInfoVo.getVehicleClass();

        if (org.apache.commons.lang.StringUtils.isBlank(plateNumber)) {
            throw new BusinessException("??????????????????");
        }

       /* if (vehicleClassIn < 0) {
            throw new BusinessException("?????????????????????");
        }*/

        Integer vehicleClass = tenantVehicleRelMapper.checkVehicleClass(plateNumber, loginInfo.getTenantId());
        if (null != vehicleClass) {
            if (VEHICLE_CLASS1 == vehicleClass) {
                throw new BusinessException("?????????????????????????????????????????????????????????????????????????????????????????????");
            } else if (SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS5 == vehicleClass) {
                throw new BusinessException("???????????????????????????????????????????????????????????????????????????????????????????????????");
            } else if (SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS2 == vehicleClass) {
                throw new BusinessException("?????????????????????????????????????????????????????????????????????????????????????????????");
            } else if (SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS4 == vehicleClass) {
                throw new BusinessException("?????????????????????????????????????????????????????????????????????????????????????????????");
            }
        }

        //??????????????????????????????????????????????????????
        QueryWrapper<VehicleDataInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("plate_number", plateNumber);
        List<VehicleDataInfo> vehicleDataInfoList = baseMapper.selectList(queryWrapper);
        if (vehicleDataInfoList != null && vehicleDataInfoList.size() > 0) {
            throw new BusinessException("?????????????????????");
        }

        //????????????
        VehicleDataInfo vehicleDataInfo = new VehicleDataInfo();
        vehicleDataInfo.setTenantId(null);

        if (vehicleClassIn == VEHICLE_CLASS1) {
            vehicleDataInfo.setTenantId(loginInfo.getTenantId());
        }
        vehicleDataInfo.setAuthState(AUTH_STATE1);
        vehicleDataInfo.setIsAuth(IS_AUTH1);
        vehicleDataInfo.setCreateDate(LocalDateTime.now());

        if (vehicleInfoVo.getLicenceType() == SysStaticDataEnum.LICENCE_TYPE.TT) {
            vehicleDataInfo.setVehicleStatus(null);
            vehicleDataInfo.setVehicleLength(null);
            vehicleDataInfo.setVehicleLoad(null);
            vehicleDataInfo.setLightGoodsSquare(null);
        } else {
            if (StringUtils.isBlank(vehicleInfoVo.getVehicleLength()) || "-1".equals(vehicleInfoVo.getVehicleLength())) {
                throw new BusinessException("????????????????????????");
            }
            if (StringUtils.isBlank(vehicleInfoVo.getLightGoodsSquare()) || "-1".equals(vehicleInfoVo.getLightGoodsSquare())) {
                throw new BusinessException("????????????????????????");
            }
            if (StringUtils.isBlank(vehicleInfoVo.getVehicleLoad())) {
                throw new BusinessException("????????????????????????");
            }
        }
        BeanUtil.copyProperties(vehicleInfoVo, vehicleDataInfo);
        vehicleDataInfo.setAuthState(1);
//        vehicleDataInfo.setTenantId(loginInfo.getTenantId());
        vehicleDataInfo.setOpId(loginInfo.getId());
        vehicleDataInfo.setOperCerti(vehicleInfoVo.getOperCerti() + "");
        vehicleDataInfo.setUpdateTime(LocalDateTime.now());
        baseMapper.insert(vehicleDataInfo);
        VehicleDataInfoVer vehicleDataInfoVer = new VehicleDataInfoVer();
        BeanUtil.copyProperties(vehicleDataInfo, vehicleDataInfoVer);

        vehicleDataInfoVer.setAdrivingLicenseCopy(vehicleDataInfo.getAdriverLicenseCopy());
        vehicleDataInfoVer.setVehicleCode(vehicleDataInfo.getId());
        vehicleDataInfoVer.setVerState(VER_STATE_Y);
        vehicleDataInfoVer.setCreateTime(LocalDateTime.now());
        vehicleDataInfoVerMapper.insert(vehicleDataInfoVer);
        //?????????????????????
        TenantVehicleRel tenantVehicleRel = new TenantVehicleRel();
        BeanUtil.copyProperties(vehicleInfoVo, tenantVehicleRel);
        if (vehicleInfoVo.getIsUseCarOilCost() != null) {
            tenantVehicleRel.setIsUseCarOilCost(vehicleInfoVo.getIsUseCarOilCost());
        }
        if (vehicleInfoVo.getShareFlg() != null) {
            tenantVehicleRel.setShareFlg(vehicleInfoVo.getShareFlg());
        }
        if (vehicleInfoVo.getVehiclePicture() != null) {
            tenantVehicleRel.setVehiclePicture(vehicleInfoVo.getVehiclePicture().toString());
        }
        if (vehicleInfoVo.getDrivingLicense() != null) {
            tenantVehicleRel.setDrivingLicense(vehicleInfoVo.getDrivingLicense().toString());
        }
        if (vehicleInfoVo.getOperCerti() != null && !vehicleInfoVo.getOperCerti().equals("")) {
            tenantVehicleRel.setOperCerti(vehicleInfoVo.getOperCerti());
        }
        if (vehicleInfoVo.getAdriverLicenseCopy() != null) {
            tenantVehicleRel.setAdriverLicenseCopy(vehicleInfoVo.getAdriverLicenseCopy().toString());
        }
        if (vehicleInfoVo.getBillReceiverMobile() != null && !vehicleInfoVo.getBillReceiverMobile().equals("")) {
            tenantVehicleRel.setBillReceiverMobile(vehicleInfoVo.getBillReceiverMobile());
        }
        if (vehicleInfoVo.getBillReceiverUserId() != null) {
            tenantVehicleRel.setBillReceiverUserId(vehicleInfoVo.getBillReceiverUserId());
        }
        if (vehicleInfoVo.getBillReceiverName() != null && !vehicleInfoVo.getBillReceiverName().equals("")) {
            tenantVehicleRel.setBillReceiverName(vehicleInfoVo.getBillReceiverName());
        }
        tenantVehicleRel.setOpId(loginInfo.getId());
        tenantVehicleRel.setUpdateTime(vehicleDataInfo.getUpdateTime());
        tenantVehicleRel.setUserId(vehicleInfoVo.getUserId());
        tenantVehicleRel.setTenantId(loginInfo.getTenantId());
        tenantVehicleRel.setDriverUserId(vehicleDataInfo.getDriverUserId());
        tenantVehicleRel.setVehicleCode(vehicleDataInfo.getId());
        tenantVehicleRel.setAuthState(AUTH_STATE1);
        tenantVehicleRel.setIsAuth(IS_AUTH1);
        tenantVehicleRel.setCreateTime(LocalDateTime.now());
        tenantVehicleRel.setPlateNumber(vehicleDataInfo.getPlateNumber());
        tenantVehicleRel.setOrgId(vehicleInfoVo.getOrgId());
        tenantVehicleRel.setVehicleClass(vehicleInfoVo.getVehicleClass());
        tenantVehicleRel.setAttachedRootOrgId(vehicleInfoVo.getOrgId());
//        tenantVehicleRel.setLoadEmptyOilCost(multiply(vehicleInfoVo.getLoadEmptyOilCost()));
//        tenantVehicleRel.setLoadFullOilCost(multiply(vehicleInfoVo.getLoadFullOilCost()));
        if (StringUtils.isNotBlank(vehicleInfoVo.getLoadEmptyOilCost())) {
            tenantVehicleRel.setLoadEmptyOilCost(Long.valueOf(vehicleInfoVo.getLoadEmptyOilCost()));
        }
        if (StringUtils.isNotBlank(vehicleInfoVo.getLoadFullOilCost())) {
            tenantVehicleRel.setLoadFullOilCost(Long.valueOf(vehicleInfoVo.getLoadFullOilCost()));
        }
//        tenantVehicleRel.setIsUseCarOilCost(vehicleInfoVo.getIsUseCarOilCost());
//        tenantVehicleRel.setShareFlg(vehicleInfoVo.getShareFlg());
//        tenantVehicleRel.setVehiclePicture(vehicleInfoVo.getVehiclePicture().toString());
//        tenantVehicleRel.setOperCerti(vehicleInfoVo.getOperCerti());
//        tenantVehicleRel.setAdriverLicenseCopy(vehicleInfoVo.getAdriverLicenseCopy().toString());
//        tenantVehicleRel.setLinkUserId(vehicleInfoVo.getLinkUserId());
//        tenantVehicleRel.setDriverUserId(vehicleInfoVo.getDriverUserId());
//        tenantVehicleRel.setBillReceiverMobile(vehicleInfoVo.getBillReceiverMobile());
//        tenantVehicleRel.setBillReceiverUserId(vehicleInfoVo.getBillReceiverUserId());
//        tenantVehicleRel.setBillReceiverName(vehicleInfoVo.getBillReceiverName());
//        tenantVehicleRel.setInvestContractFilePic(vehicleInfoVo.getInvestContractFilePic());
//        tenantVehicleRel.setInvestContractFilePicUrl(vehicleInfoVo.getInvestContractFilePicUrl());

        if (vehicleClassIn == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS2 || vehicleClassIn == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS4) {
            vehicleDataInfo.setAttachUserId(tenantVehicleRel.getBillReceiverUserId());
            vehicleDataInfo.setAttachUserMobile(tenantVehicleRel.getBillReceiverMobile());
            vehicleDataInfo.setAttachUserName(tenantVehicleRel.getBillReceiverName());

            vehicleDataInfoVer.setAttachUserId(tenantVehicleRel.getBillReceiverUserId());
            vehicleDataInfoVer.setAttachUserMobile(tenantVehicleRel.getBillReceiverMobile());
            vehicleDataInfoVer.setAttachUserName(tenantVehicleRel.getBillReceiverName());
        } else {
            tenantVehicleRel.setBillReceiverMobile(null);
            tenantVehicleRel.setBillReceiverName(null);
            tenantVehicleRel.setBillReceiverUserId(null);
        }

        tenantVehicleRelMapper.insert(tenantVehicleRel);
        TenantVehicleRelVer tenantVehicleRelVer = new TenantVehicleRelVer();
        BeanUtil.copyProperties(tenantVehicleRel, tenantVehicleRelVer);
        tenantVehicleRelVer.setRelId(tenantVehicleRel.getId());
        if ("".equals(tenantVehicleRel.getAdriverLicenseCopy()) || tenantVehicleRel.getAdriverLicenseCopy() == null) {
            tenantVehicleRelVer.setAdrivingLicenseCopy(-1L);
        } else {
            tenantVehicleRelVer.setAdrivingLicenseCopy(Long.parseLong(tenantVehicleRel.getAdriverLicenseCopy()));
        }
        tenantVehicleRelVer.setLoadEmptyPilCost(tenantVehicleRel.getLoadEmptyOilCost());
        tenantVehicleRelVer.setLoadFullPilCost(tenantVehicleRel.getLoadFullOilCost());
        tenantVehicleRelVer.setVerState(VER_STATE_Y);
        tenantVehicleRelVer.setVehicleHisId(vehicleDataInfoVer.getId());
        tenantVehicleRelVer.setRelId(tenantVehicleRel.getId());
        tenantVehicleRelVer.setVehicleCode(vehicleDataInfo.getId());
        if (vehicleInfoVo.getIsUseCarOilCost() != null) {
            tenantVehicleRelVer.setIsUserCarOilCost(vehicleInfoVo.getIsUseCarOilCost());
        }
        tenantVehicleRelVerMapper.insert(tenantVehicleRelVer);

        if (tenantVehicleRel.getVehicleClass() != SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1) {
            addPtTenantVehicleRel(tenantVehicleRel.getVehicleCode());
        }

        if ((vehicleClassIn == VEHICLE_CLASS5) || (null != tenantVehicleRel.getShareFlg() && tenantVehicleRel.getShareFlg().equals(YES))) {
            doSaveVehicleOrderPositionInfo(tenantVehicleRel.getVehicleCode(), tenantVehicleRel.getPlateNumber());
        }

        //???????????????
        List<VehicleObjectLineVo> list = vehicleInfoVo.getVehicleOjbectLineArray();
        List vehicleObjectLineIds = new ArrayList();
        VehicleObjectLine vehicleObjectLine = null;
        VehicleObjectLineVer vehicleObjectLineVer = null;
        if (null != list && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {

                //??????????????????????????????????????????3?????????????????????
                QueryWrapper<VehicleObjectLine> queryWrapper1 = new QueryWrapper<>();
                queryWrapper1.eq("vehicle_code", vehicleDataInfo.getId());
                List<VehicleObjectLine> vehicleObjectLineList = vehicleObjectLineMapper.selectList(queryWrapper1);
                if (vehicleObjectLineList != null && vehicleObjectLineList.size() > 2) {
                    break;
                }

                VehicleObjectLineVo vehicleOjbectLineVo = list.get(i);

                Integer sourceProvince = null == vehicleOjbectLineVo.getSourceProvince() ? null : vehicleOjbectLineVo.getSourceProvince().intValue();
                Integer sourceRegion = null == vehicleOjbectLineVo.getSourceRegion() ? null : vehicleOjbectLineVo.getSourceRegion().intValue();
                Integer sourceCounty = null == vehicleOjbectLineVo.getSourceCounty() ? null : vehicleOjbectLineVo.getSourceCounty().intValue();
                Integer desProvince = null == vehicleOjbectLineVo.getDesProvince() ? null : vehicleOjbectLineVo.getDesProvince().intValue();
                Integer desRegion = null == vehicleOjbectLineVo.getDesRegion() ? null : vehicleOjbectLineVo.getDesRegion().intValue();
                Integer desCounty = null == vehicleOjbectLineVo.getDesCounty() ? null : vehicleOjbectLineVo.getDesCounty().intValue();
                Long carriagePriceL = null;
                if (null != vehicleOjbectLineVo.getCarriagePrice()) {
                    carriagePriceL = vehicleOjbectLineVo.getCarriagePrice();
                }
                vehicleObjectLine = new VehicleObjectLine();
                vehicleObjectLineVer = new VehicleObjectLineVer();
                vehicleObjectLine.setVehicleCode(vehicleDataInfo.getId());
                vehicleObjectLine.setSourceProvince(sourceProvince);
                vehicleObjectLine.setSourceRegion(sourceRegion);
                vehicleObjectLine.setSourceCounty(sourceCounty);
                vehicleObjectLine.setDesProvince(desProvince);
                vehicleObjectLine.setDesRegion(desRegion);
                vehicleObjectLine.setDesCounty(desCounty);
                vehicleObjectLine.setCreateDate(LocalDateTime.now());
                vehicleObjectLine.setPlateNumber(vehicleDataInfo.getPlateNumber());
                vehicleObjectLine.setTenantId(loginInfo.getTenantId());
                vehicleObjectLine.setCarriagePrice(carriagePriceL);
                vehicleObjectLineMapper.insert(vehicleObjectLine);

                vehicleObjectLineVer.setVehicleCode(vehicleDataInfo.getId());
                vehicleObjectLineVer.setSourceProvince(sourceProvince);
                vehicleObjectLineVer.setSourceRegion(sourceRegion);
                vehicleObjectLineVer.setSourceCounty(sourceCounty);
                vehicleObjectLineVer.setDesProvince(desProvince);
                vehicleObjectLineVer.setDesRegion(desRegion);
                vehicleObjectLineVer.setDesCounty(desCounty);
                vehicleObjectLineVer.setCreateTime(LocalDateTime.now());
                vehicleObjectLineVer.setPlateNumber(vehicleDataInfo.getPlateNumber());
                vehicleObjectLineVer.setTenantId(loginInfo.getTenantId());
                vehicleObjectLineVer.setCarriagePrice(carriagePriceL);
                vehicleObjectLineVer.setId(vehicleObjectLine.getId());
                vehicleObjectLineVer.setVerState(VER_STATE_Y);
                vehicleObjectLineVer.setVehicleHisId(vehicleDataInfoVer.getId());
                vehicleObjectLineVerMapper.insert(vehicleObjectLineVer);
                vehicleObjectLineIds.add(vehicleObjectLine.getId());

            }
        }
        long tenantVehicleCostRelId = -1;
        long tenantVehicleCertRelId = -1;
        //???????????????????????? ????????????????????????????????????
        if (VEHICLE_CLASS1 == vehicleClassIn || SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS2 == vehicleClassIn || SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS4 == vehicleClassIn) {
            //???????????????????????????
            TenantVehicleCostRel tenantVehicleCostRel = new TenantVehicleCostRel();
            BeanUtil.copyProperties(vehicleInfoVo, tenantVehicleCostRel);
            tenantVehicleCostRel.setTenantId(loginInfo.getTenantId());
            tenantVehicleCostRel.setRelId(tenantVehicleRel.getId());
            tenantVehicleCostRel.setVehicleCode(vehicleDataInfo.getId());
            tenantVehicleCostRel.setCreateTime(LocalDateTime.now());
            tenantVehicleCostRel.setTenantId(loginInfo.getTenantId());
            if (StringUtils.isNotBlank(vehicleInfoVo.getManageFee())) {
                tenantVehicleCostRel.setManagementCost(Long.parseLong(vehicleInfoVo.getManageFee()));
            }

            //????????????????????????????????? ?????????,????????????,????????????,????????????,????????????,????????????,???????????????
            if (SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS2 == vehicleClassIn || SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS4 == vehicleClassIn) {
                tenantVehicleCostRel.setResidual(null);//??????
                tenantVehicleCostRel.setDepreciatedMonth(null);//????????????
                tenantVehicleCostRel.setInsuranceFee(null);//?????????
                tenantVehicleCostRel.setExamVehicleFee(null);//?????????
                tenantVehicleCostRel.setMaintainFee(null);//?????????
                tenantVehicleCostRel.setRepairFee(null);//????????????
                tenantVehicleCostRel.setTyreFee(null);//????????????
            } else if (SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1 == vehicleClassIn) {//???????????????????????????
                tenantVehicleCostRel.setManagementCost(null);//?????????
            }

            tenantVehicleCostRelMapper.insert(tenantVehicleCostRel);
            TenantVehicleCostRelVer tenantVehicleCostRelVer = new TenantVehicleCostRelVer();
            BeanUtil.copyProperties(tenantVehicleCostRel, tenantVehicleCostRelVer);
            tenantVehicleCostRelVer.setVerState(VER_STATE_Y);
            tenantVehicleCostRelVerMapper.insert(tenantVehicleCostRelVer);
            tenantVehicleCostRelId = tenantVehicleCostRel.getId();
            //???????????????
            TenantVehicleCertRel tenantVehicleCertRel = new TenantVehicleCertRel();
            BeanUtil.copyProperties(vehicleInfoVo, tenantVehicleCertRel);
            tenantVehicleCertRel.setTenantId(loginInfo.getTenantId());
            tenantVehicleCertRel.setRelId(tenantVehicleRel.getId());
            tenantVehicleCertRel.setVehicleCode(vehicleDataInfo.getId());
            tenantVehicleCertRel.setCreateTime(LocalDateTime.now());
            tenantVehicleCertRelMapper.insert(tenantVehicleCertRel);
            TenantVehicleCertRelVer tenantVehicleCertRelVer = new TenantVehicleCertRelVer();
            BeanUtil.copyProperties(tenantVehicleCertRel, tenantVehicleCertRelVer);
            tenantVehicleCertRelVer.setVerState(VER_STATE_Y);
            tenantVehicleCertRelVerMapper.insert(tenantVehicleCertRelVer);
            tenantVehicleCertRelId = tenantVehicleCertRel.getId();
        }
        //????????????
        List<VehicleLineRelsVo> linelist = vehicleInfoVo.getVehicleLineRels();
        VehicleLineRel vehicleLineRel = null;
        VehicleLineRelVer vehicleLineRelVer = null;
        List vehicleLineRelIds = new ArrayList();
        if (null != linelist && linelist.size() > 0) {
            for (int i = 0; i < linelist.size(); i++) {
                VehicleLineRelsVo vehicleLineRelsVo = linelist.get(i);
                vehicleLineRel = new VehicleLineRel();
                vehicleLineRel.setLineId(vehicleLineRelsVo.getLineId() == null ? null : vehicleLineRelsVo.getLineId().longValue());
                vehicleLineRel.setLineCodeRule(vehicleLineRelsVo.getLineCodeRule());
                vehicleLineRel.setPlateNumber(vehicleDataInfo.getPlateNumber());
                vehicleLineRel.setVehicleCode(vehicleDataInfo.getId());
                vehicleLineRel.setState(vehicleLineRelsVo.getState() == null ? null : vehicleLineRelsVo.getState().intValue());
                vehicleLineRel.setOpId(loginInfo.getId());
                vehicleLineRel.setOpDate(LocalDateTime.now());
//                if (vehicleLineRelsVo.getRelId() != null) {
                vehicleLineRelMapper.insert(vehicleLineRel);
                vehicleLineRelVer = new VehicleLineRelVer();
                vehicleLineRelVer.setRelId(tenantVehicleRel.getId());
                vehicleLineRelVer.setLineCodeRule(vehicleLineRelsVo.getLineCodeRule());
                vehicleLineRelVer.setLineId(vehicleLineRelsVo.getLineId() == null ? null : vehicleLineRelsVo.getLineId().longValue());
                vehicleLineRelVer.setPlateNumber(vehicleDataInfo.getPlateNumber());
                vehicleLineRelVer.setVehicleCode(vehicleDataInfo.getId());
                vehicleLineRelVer.setState(vehicleLineRelsVo.getState() == null ? null : vehicleLineRelsVo.getState().intValue());
                vehicleLineRelVer.setOpId(loginInfo.getId());
                vehicleLineRelVer.setOpDate(LocalDateTime.now());
                vehicleLineRelVer.setVehicleHisId(vehicleDataInfoVer.getId());
                vehicleLineRelVer.setVerState(VER_STATE_Y);
                vehicleLineRelVerMapper.insert(vehicleLineRelVer);
                vehicleLineRelIds.add(vehicleLineRel.getLineId());
//                }
            }
        }

        saveSysOperLog(SysOperLogConst.BusiCode.Vehicle, tenantVehicleRel.getId(), SysOperLogConst.OperType.Add, "??????" + vehicleDataInfo.getPlateNumber() + "????????????", loginInfo);

        if (vehicleInfoVo.getAutoAudit() != null && vehicleInfoVo.getAutoAudit() == AUTO_AUDIT1 && vehicleClassIn == VEHICLE_CLASS5) {

            vehicleDataInfo.setAuthState(AUTH_STATE2);
            vehicleDataInfo.setIsAuth(IS_AUTH0);
            baseMapper.updateById(vehicleDataInfo);
            vehicleDataInfoVer.setVerState(VER_STATE_Y);
            vehicleDataInfoVer.setIsAuthSucc(SysStaticDataEnum.IS_AUTH_SUCC.YES_3);
            vehicleDataInfoVerMapper.updateById(vehicleDataInfoVer);

            tenantVehicleRel.setAuthState(AUTH_STATE2);
            tenantVehicleRel.setIsAuth(IS_AUTH0);
            tenantVehicleRelMapper.updateById(tenantVehicleRel);
            tenantVehicleRelVer.setVerState(VER_STATE_Y);
            tenantVehicleRelVer.setIsAuthSucc(SysStaticDataEnum.IS_AUTH_SUCC.YES_3);
            tenantVehicleRelVer.setVehicleHisId(vehicleDataInfoVer.getId());
            tenantVehicleRelVerMapper.updateById(tenantVehicleRelVer);

            TenantVehicleRel ptVehicleRel = new TenantVehicleRel();
            BeanUtil.copyProperties(tenantVehicleRel, ptVehicleRel);

            //?????????????????????
            doUpdateVehicleCompleteness(vehicleDataInfo.getId(), loginInfo.getTenantId());

            //?????????????????????????????????
            addPtTenantVehicleRel(vehicleDataInfo.getId());

            //?????????OCR??????????????????????????????
            saveSysOperLog(SysOperLogConst.BusiCode.Vehicle, tenantVehicleRel.getId(), SysOperLogConst.OperType.Audit, "?????????" + vehicleDataInfo.getPlateNumber() + "(OCR)??????????????????", loginInfo);

        } else {
            boolean isAutoAudit = sysCfgService.getCfgBooleanVal("IS_AUTO_AUDIT", -1);
            if (isAutoAudit && vehicleClassIn == VEHICLE_CLASS5) {
                vehicleDataInfo.setAuthState(AUTH_STATE2);
                vehicleDataInfo.setIsAuth(IS_AUTH0);
                baseMapper.updateById(vehicleDataInfo);
                vehicleDataInfoVer.setVerState(VER_STATE_Y);
                vehicleDataInfoVer.setIsAuthSucc(SysStaticDataEnum.IS_AUTH_SUCC.YES_3);
                vehicleDataInfoVerMapper.updateById(vehicleDataInfoVer);
                //?????????????????????????????????
                addPtTenantVehicleRel(vehicleDataInfo.getId());
                //?????????
                saveSysOperLog(SysOperLogConst.BusiCode.Vehicle, vehicleDataInfo.getId(), SysOperLogConst.OperType.Audit, "?????????" + vehicleDataInfo.getPlateNumber() + "????????????????????????", accessToken);
            }

            //??????????????????
            Map inMap = new HashMap();
            inMap.put("svName", "IAuditOutTF");
            AuditDto auditDto = AuditDto.of()
                    .setAuditCode(AUDIT_CODE_VEHICLE)
                    .setBusiId(tenantVehicleRel.getId())
                    .setBusiCode(SysOperLogConst.BusiCode.Vehicle)
                    .setParams(inMap)
                    .setAccessToken(accessToken);

            boolean bool = iAuditService.startProcess(AUDIT_CODE_VEHICLE, tenantVehicleRel.getId(), SysOperLogConst.BusiCode.Vehicle, inMap, accessToken);
            if (!bool) {
                throw new BusinessException("???????????????????????????");
            }
            return auditDto;
        }

        return null;
    }

    public static Long multiply(String value) {
        if (org.apache.commons.lang.StringUtils.isBlank(value) || !isNumber(value)) {
            return null;
        }
        BigDecimal bd = new BigDecimal(100);
        return new BigDecimal(value).multiply(bd).longValue();
    }

    @Override
    @Async
    @Transactional
    public String deal(byte[] bytes, ImportOrExportRecords records, String token) {
        //?????????????????????????????????
        LoginInfo loginInfo = loginUtils.get(token);
        String VEHICLE_CLASS1 = SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1 + "";//???????????????
        String VEHICLE_CLASS2 = SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS2 + "";//???????????????
        String VEHICLE_CLASS4 = SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS4 + "";//?????????
        String VEHICLE_CLASS3 = SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS5 + "";//???????????????
        List<VehicleInfoVo> failureList = new ArrayList<>();
        try {
            InputStream is = new ByteArrayInputStream(bytes);
            List<List<String>> lists = ExcelUtils.getExcelContent(is, 1, (ExcelFilesVaildate[]) null);
            //???????????????
            List<String> listsRows = lists.remove(0);
            int j = 1;
            int success = 0;
            for (List<String> rowData : lists) {
                Map param = new HashMap<String, Object>();
                StringBuffer reasonFailure = new StringBuffer();
                try {

                    checkDate(rowData);

                    int count = rowData.size();
                    String plateNumber = ExcelUtils.getExcelValue(rowData, 0);
                    if (StrUtil.isBlank(plateNumber)) {
                        reasonFailure.append("??????????????????");
                    } else {
                        //??????????????????
                        Map<String, Object> rtnMap = baseMapper.getVehicleIsRegister(SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1, plateNumber);
                        param.put("plateNumber", plateNumber);
                        //?????????
                        if (null != rtnMap && !rtnMap.isEmpty()) {
                            //????????????????????????
                            QueryWrapper<TenantVehicleRel> tenantVehicleRelQueryWrapper = new QueryWrapper<>();
                            tenantVehicleRelQueryWrapper.eq("plate_number", plateNumber);
                            tenantVehicleRelQueryWrapper.eq("tenant_id", loginInfo.getTenantId());
                            List<TenantVehicleRel> tenantVehicleRels = iTenantVehicleRelService.list(tenantVehicleRelQueryWrapper);
                            if (null != tenantVehicleRels && tenantVehicleRels.size() > 0) {
//                            String vehicleClassName = ExcelUtils.getExcelValue(rowData, 1);
//                            String vehicleClassStr = null;
//                            if (StrUtil.isBlank(vehicleClassName)) {
//                                reasonFailure.append("?????????????????????!");
//                            } else {
//                                vehicleClassStr = getCodeValue("VEHICLE_CLASS", vehicleClassName);
//                                if (org.apache.commons.lang.StringUtils.isBlank(vehicleClassStr)) {
//                                    reasonFailure.append("?????????????????????!");
//                                }
//                            }
//                            int vehicleClass = tenantVehicleRels.get(0).getVehicleClass();
//                            if (vehicleClassStr.equals(vehicleClass + "")) {
//                                param.putAll(this.toUpdateByImport(rowData, token));
//                                reasonFailure.append(DataFormat.getStringKey(param, "tmpMsg"));
//                            } else {
//                                reasonFailure.append("??????????????????????????????????????????????????????");
//                            }
                                reasonFailure.append("????????????????????????");
                            } else {
                                reasonFailure.append("????????????????????????????????????????????????????????????????????????!");
                            }
                        } else {
                            String vinNo = ExcelUtils.getExcelValue(rowData, 7);
                            if (org.apache.commons.lang.StringUtils.isBlank(vinNo)) {
                                reasonFailure.append("??????????????????!");
                            }
                            param.put("vinNo", vinNo);

                            String vehicleClassName = ExcelUtils.getExcelValue(rowData, 1);
                            String vehicleClassStr = null;
                            if (StringUtils.isBlank(vehicleClassName)) {
                                reasonFailure.append("?????????????????????!");
                            } else {
                                vehicleClassStr = getCodeValue("VEHICLE_CLASS", vehicleClassName);
                                param.put("vehicleClass", vehicleClassStr);
                                if (org.apache.commons.lang.StringUtils.isBlank(vehicleClassStr)) {
                                    reasonFailure.append("?????????????????????!");
                                }
                            }

                            String licenceTypeName = ExcelUtils.getExcelValue(rowData, 2);
                            if (org.apache.commons.lang.StringUtils.isBlank(licenceTypeName)) {
                                reasonFailure.append("?????????????????????!");
                            } else {
                                String licenceType = getCodeValue("LICENCE_TYPE", licenceTypeName);
                                if (org.apache.commons.lang.StringUtils.isBlank(licenceType)) {
                                    reasonFailure.append("?????????????????????!");
                                } else {
                                    if ("1".equals(licenceType)) {
                                        //???????????????
                                        String vehicleStatusName = ExcelUtils.getExcelValue(rowData, 3);
                                        if (org.apache.commons.lang.StringUtils.isBlank(vehicleStatusName)) {
                                            reasonFailure.append("???????????????!");
                                        } else {
                                            SysStaticData dataByCodeName = SysStaticDataRedisUtils.getSysStaticDataByCodeName(redisUtil,
                                                    "VEHICLE_STATUS", vehicleStatusName);
                                            Long vehicleStatus = null;
                                            if (dataByCodeName != null) {
                                                vehicleStatus = dataByCodeName.getId();
                                            }
                                            if (org.apache.commons.lang.StringUtils.isBlank(vehicleStatus + "")) {
                                                reasonFailure.append("???????????????!");
                                            }
                                            param.put("vehicleStatus", vehicleStatus);
                                        }
                                        String vehicleLengthName = ExcelUtils.getExcelValue(rowData, 4);
                                        if (org.apache.commons.lang.StringUtils.isBlank(vehicleLengthName)) {
                                            reasonFailure.append("???????????????!");
                                        } else {
                                            Long vehicleStatus = (Long) param.get("vehicleStatus");
                                            Long vehicleLength = null;
                                            if ((SysStaticDataEnum.VEHICLE_STATUS_ENUM.VEHICLE_CAR_BOX.getType() + "").equals(vehicleStatus)) {
                                                SysStaticData dataByCodeName = SysStaticDataRedisUtils.getSysStaticDataByCodeName(redisUtil,
                                                        "VEHICLE_LENGTH", vehicleLengthName);
                                                if (dataByCodeName != null) {
                                                    vehicleLength = dataByCodeName.getId();
                                                }
//                                            vehicleLength = getCodeValue("VEHICLE_STATUS_SUBTYPE", vehicleLengthName);
                                            } else {
                                                SysStaticData dataByCodeName = SysStaticDataRedisUtils.getSysStaticDataByCodeName(redisUtil,
                                                        "VEHICLE_LENGTH", vehicleLengthName);
                                                if (dataByCodeName != null) {
                                                    vehicleLength = dataByCodeName.getId();
                                                }
                                                //                 vehicleLength = getCodeValue("VEHICLE_LENGTH", vehicleLengthName);
                                            }
                                            if (org.apache.commons.lang.StringUtils.isBlank(vehicleLength + "") || vehicleLength.equals("-1")) {
                                                reasonFailure.append("??????????????????");
                                            }
                                            param.put("vehicleLength", vehicleLength);
                                        }
                                        String vehicleLoad = ExcelUtils.getExcelValue(rowData, 5);
                                        if (org.apache.commons.lang.StringUtils.isBlank(vehicleLoad)) {
                                            reasonFailure.append("??????(???)?????????!");
                                        }
                                        if (StrUtil.isNotBlank(vehicleLoad)) {
                                            BigDecimal vehicleLoadBig = new BigDecimal(vehicleLoad);
                                            if (vehicleLoadBig.compareTo(new BigDecimal(0)) < 0) {
                                                reasonFailure.append("??????(???)???????????????!");
                                            }

                                            try {
                                                Long vehicleLoadInteger = Long.parseLong(vehicleLoad);
                                                if (vehicleLoadInteger * 100 > Integer.MAX_VALUE) {
                                                    reasonFailure.append("??????(???)????????????!");
                                                }
                                            } catch (Exception e) {
                                                reasonFailure.append("??????(???)????????????!");
                                            }
                                        }

                                        if (vehicleLoad != null && !vehicleLoad.equals("") && Double.parseDouble(vehicleLoad) > 0) {
                                            Number value = Double.parseDouble(vehicleLoad) * 100;
                                            param.put("vehicleLoad", value.intValue());
                                        }


                                        String lightGoodsSquare = ExcelUtils.getExcelValue(rowData, 6);
                                        if (org.apache.commons.lang.StringUtils.isBlank(lightGoodsSquare)) {
                                            reasonFailure.append("??????(?????????)?????????!");
                                        }
                                        if (StringUtils.isNotBlank(lightGoodsSquare)) {

                                            try {
                                                Long lightGoodsSquareInteger = Long.parseLong(lightGoodsSquare);
                                                if (lightGoodsSquareInteger * 100 > Integer.MAX_VALUE) {
                                                    reasonFailure.append("??????(?????????)????????????!");
                                                }
                                            } catch (Exception e) {
                                                reasonFailure.append("??????(?????????)????????????!");
                                            }
                                            Number value = Double.parseDouble(lightGoodsSquare) * 100;
                                            param.put("lightGoodsSquare", value.intValue());
                                        }


                                    }
                                }
                                param.put("licenceType", licenceType);
                            }


                            String engineNo = ExcelUtils.getExcelValue(rowData, 8);
                            if (org.apache.commons.lang.StringUtils.isBlank(engineNo)) {
                                reasonFailure.append("?????????????????????!");
                            }
                            param.put("engineNo", engineNo);

                            String brandModel = ExcelUtils.getExcelValue(rowData, 9);
                            param.put("brandModel", brandModel);

                            String operCerti = ExcelUtils.getExcelValue(rowData, 10);
                            param.put("operCerti", operCerti);

                            String drivingLicenseOwner = ExcelUtils.getExcelValue(rowData, 11);
                            if (org.apache.commons.lang.StringUtils.isBlank(drivingLicenseOwner)) {
                                reasonFailure.append("?????????????????????????????????!");
                            }
                            param.put("drivingLicenseOwner", drivingLicenseOwner);
                            String etcCardNumber = ExcelUtils.getExcelValue(rowData, 12);
                            param.put("etcCardNumber", etcCardNumber);

					/*String equipmentCode = getExcelValue(l,12);
					param.put("equipmentCode", equipmentCode);*/

                            String loadEmptyOilCost = ExcelUtils.getExcelValue(rowData, 13);
                            if (StringUtils.isNotBlank(loadEmptyOilCost)) {
                                try {
                                    Long loadEmptyOilCostInteger = Long.parseLong(loadEmptyOilCost);
                                    if (loadEmptyOilCostInteger * 100 > Integer.MAX_VALUE) {
                                        reasonFailure.append("????????????????????????!");
                                    }
                                } catch (Exception e) {
                                    reasonFailure.append("????????????????????????!");
                                }

                                Number value = Double.parseDouble(loadEmptyOilCost) * 100;
                                param.put("loadEmptyOilCost", value.intValue());
                            }


                            String loadFullOilCost = ExcelUtils.getExcelValue(rowData, 14);
                            if (StringUtils.isNotBlank(loadFullOilCost)) {
                                try {
                                    Long loadFullOilCostInteger = Long.parseLong(loadFullOilCost);
                                    if (loadFullOilCostInteger * 100 > Integer.MAX_VALUE) {
                                        reasonFailure.append("????????????????????????!");
                                    }
                                } catch (Exception e) {
                                    reasonFailure.append("????????????????????????!");
                                }

                                Number value = Double.parseDouble(loadFullOilCost) * 100;
                                param.put("loadFullOilCost", value.intValue());
                            }


                            String isUseCarOilCost = ExcelUtils.getExcelValue(rowData, 15);
                            param.put("isUseCarOilCost", "???".equals(isUseCarOilCost) ? 1 : 0);

                            String driverUserPhone = ExcelUtils.getExcelValue(rowData, 16);
                            String driverUserName = "";
                            if (org.apache.commons.lang.StringUtils.isNotBlank(vehicleClassStr)
                                    && (VEHICLE_CLASS2.equals(vehicleClassStr) || VEHICLE_CLASS3.equals(vehicleClassStr) || VEHICLE_CLASS4.equals(vehicleClassStr))
                                    && org.apache.commons.lang.StringUtils.isBlank(driverUserPhone)) {
                                reasonFailure.append("??????????????????????????????!");
                            } else if (org.apache.commons.lang.StringUtils.isNotBlank(vehicleClassStr) && org.apache.commons.lang.StringUtils.isNotBlank(driverUserPhone)) {
                                //????????????
                                UserDataInfo userInfo = iUserDataInfoRecordService.getUserDataInfoByMoblile(driverUserPhone, false, token);
                                if (userInfo == null) {
                                    reasonFailure.append("??????????????????????????????!");
                                } else {
                                    QueryWrapper<TenantUserRel> queryWrapper = new QueryWrapper<>();
                                    queryWrapper.eq("USER_ID", userInfo.getId());
                                    queryWrapper.eq("TENANT_ID", loginInfo.getTenantId());
                                    queryWrapper.eq("state", com.youming.youche.conts.SysStaticDataEnum.SYS_USER_STATE.AUDIT_APPROVE);
                                    List<TenantUserRel> tenantUserRels = tenantUserRelMapper.selectList(queryWrapper);

                                    if (null == tenantUserRels && tenantUserRels.size() > 0) {
                                        reasonFailure.append("????????????????????????????????????!");
                                    } else {
                                        param.put("driverUserId", userInfo.getId());
                                        driverUserName = userInfo.getLinkman();
                                    }

                                }
                            }

                            //?????????????????????
                            if (VEHICLE_CLASS2.equals(vehicleClassStr) || VEHICLE_CLASS4.equals(vehicleClassStr)) {
                                String billReceiverMobile = ExcelUtils.getExcelValue(rowData, 38);//????????????????????????
                                String billReceiverName = ExcelUtils.getExcelValue(rowData, 39);//?????????????????????


                                if (org.apache.commons.lang.StringUtils.isBlank(billReceiverMobile)) {
                                    billReceiverMobile = driverUserPhone;
                                }

                                if (org.apache.commons.lang.StringUtils.isBlank(billReceiverName)) {
                                    billReceiverName = driverUserName;
                                }

                                param.put("billReceiverMobile", billReceiverMobile);
                                param.put("billReceiverName", billReceiverName);

                                if (org.apache.commons.lang.StringUtils.isBlank(billReceiverMobile) || org.apache.commons.lang.StringUtils.isBlank(billReceiverName)) {
                                    reasonFailure.append("???????????????,???????????????????????????!");
                                } else {

                                    UserDataInfoBackVo userDataInfoBackVo = new UserDataInfoBackVo();
                                    if (VEHICLE_CLASS2.equals(vehicleClassStr)) {//?????????????????????????????????
                                        userDataInfoBackVo = queryBillReceiverName(billReceiverMobile, 1, token);
                                    } else if (VEHICLE_CLASS4.equals(vehicleClassStr)) {//?????????????????????????????????
                                        userDataInfoBackVo = queryBillReceiverName(billReceiverMobile, 2, token);
                                    }
                                    if ("F".equals(userDataInfoBackVo.getCode())) {
                                        reasonFailure.append(userDataInfoBackVo.getMsg() + "!");
                                    } else {
                                        //????????????
                                        UserDataInfo userInfo = iUserDataInfoRecordService.getUserDataInfoByMoblile(billReceiverMobile, false, token);
                                        if (userInfo != null) {
                                            param.put("billReceiverUserId", userInfo.getId());
                                        }
                                    }
                                }

                            }

                            //???????????????????????????????????????????????????
                            if (StrUtil.isNotBlank(vehicleClassStr) && (VEHICLE_CLASS1.equals(vehicleClassStr) || VEHICLE_CLASS2.equals(vehicleClassStr) || VEHICLE_CLASS4.equals(vehicleClassStr))) {
                                if (VEHICLE_CLASS1.equals(vehicleClassStr)) {//??????????????????????????????
                                    String copilotDriverPhone = ExcelUtils.getExcelValue(rowData, 17);
                                    if (org.apache.commons.lang.StringUtils.isNotBlank(driverUserPhone) && org.apache.commons.lang.StringUtils.isNotBlank(copilotDriverPhone) && driverUserPhone.equals(copilotDriverPhone)) {
                                        reasonFailure.append("????????????????????????????????????!");

                                    } else if (org.apache.commons.lang.StringUtils.isNotBlank(copilotDriverPhone)) {

                                        //????????????
                                        UserDataInfo userInfo = iUserDataInfoRecordService.getUserDataInfoByMoblile(copilotDriverPhone, false, token);
                                        if (userInfo == null) {
                                            reasonFailure.append("????????????????????????!");
                                        } else {
                                            QueryWrapper<TenantUserRel> tenantUserRelQueryWrapper = new QueryWrapper<>();
                                            tenantUserRelQueryWrapper.eq("user_id", userInfo.getId());
                                            tenantUserRelQueryWrapper.eq("tenant_id", loginInfo.getTenantId());
                                            List<TenantUserRel> tenantUserRels = iTenantUserRelService.list(tenantUserRelQueryWrapper);
                                            if (tenantUserRels == null || tenantUserRels.size() == 0) {
                                                reasonFailure.append("?????????????????????????????????????????????!");
                                            } else {
                                                param.put("copilotDriverId", userInfo.getId());
                                            }

                                        }

                                    }
                                    String followDriverPhone = ExcelUtils.getExcelValue(rowData, 18);
                                    if (org.apache.commons.lang.StringUtils.isNotBlank(followDriverPhone)) {
                                        UserDataInfo userInfo = iUserDataInfoRecordService.getUserDataInfoByMoblile(followDriverPhone, false, token);
                                        if (userInfo == null) {
                                            reasonFailure.append("??????????????????????????????!");
                                        } else {
                                            QueryWrapper<TenantUserRel> tenantUserRelQueryWrapper = new QueryWrapper<>();
                                            tenantUserRelQueryWrapper.eq("user_id", userInfo.getId());
                                            List<TenantUserRel> tenantUserRelList = iTenantUserRelService.list(tenantUserRelQueryWrapper);
                                            if (tenantUserRelList == null || tenantUserRelList.size() == 0) {
                                                reasonFailure.append("???????????????????????????????????????????????????!");
                                            } else {
                                                param.put("followDriverId", userInfo.getId());
                                            }
                                        }
                                    }

                                }
                                param.put("vehicleClass", vehicleClassStr);
                                if ((VEHICLE_CLASS1.equals(vehicleClassStr) || VEHICLE_CLASS2.equals(vehicleClassStr))) {
                                    String orgName = ExcelUtils.getExcelValue(rowData, 19);
                                    if (StrUtil.isNotBlank(orgName)) {
                                        Long orgId = null;
                                        List<SysOrganize> sysOrganizeList = new ArrayList<>();
                                        try {
                                            sysOrganizeList = iSysOrganizeService.querySysOrganizeList(loginInfo.getTenantId(), null, null);
                                        } catch (RpcException e) {
                                            e.printStackTrace();
                                        }
                                        for (SysOrganize sysOragnize : sysOrganizeList) {
                                            if (orgName.equals(sysOragnize.getOrgName())) {
                                                orgId = sysOragnize.getId();
                                                break;
                                            }
                                        }
                                        if (orgId == null) {
                                            reasonFailure.append("?????????????????????!");
                                        }
                                        param.put("orgId", orgId);
                                    } else {
                                        //???????????????????????????parentOrgId = -1???
                                        List<SysOrganize> sysOrganizeList = new ArrayList<>();
                                        try {
                                            sysOrganizeList = iSysOrganizeService.querySysOrganizeList(loginInfo.getTenantId(), -1L, null);
                                        } catch (RpcException e) {
                                            e.printStackTrace();
                                        }
                                        if (sysOrganizeList != null && sysOrganizeList.size() > 0) {
                                            try {
                                                sysOrganizeList = iSysOrganizeService.querySysOrganizeList(loginInfo.getTenantId(), sysOrganizeList.get(0).getId(), SysStaticDataEnum.SYS_STATE_DESC.STATE_YES);
                                            } catch (RpcException e) {
                                                e.printStackTrace();
                                            }
                                            if (sysOrganizeList != null && sysOrganizeList.size() > 0) {
                                                long rootOrgid = sysOrganizeList.get(0).getId();
                                                param.put("orgId", rootOrgid);
                                            }
                                        }
                                    }
                                    String attachedUserPhone = ExcelUtils.getExcelValue(rowData, 20);
                                    if (StrUtil.isNotBlank(attachedUserPhone)) {
                                        StaffDataInfoVo staffDataInfoIn = new StaffDataInfoVo();
                                        staffDataInfoIn.setLoginAcct(attachedUserPhone);
                                        SysUser sysOperatorByLoginAcct = sysUserService.getSysOperatorByLoginAcct(attachedUserPhone, loginInfo.getTenantId());
                                        if (sysOperatorByLoginAcct == null) {
                                            reasonFailure.append("????????????????????????!");
                                        } else {
                                            param.put("userId", sysOperatorByLoginAcct.getUserInfoId());
                                        }
                                    }
                                }
                                if ((VEHICLE_CLASS1.equals(vehicleClassStr))) {//???????????????????????????
                                    String lineCodeRules = ExcelUtils.getExcelValue(rowData, 21);
                                    if (StrUtil.isNotBlank(lineCodeRules)) {
                                        String[] lineCodeRuleList=lineCodeRules.split(",");
                                        Set<String> setList=new HashSet<>();
                                        for (String line:lineCodeRuleList) {
                                            setList.add(line);
                                        }
                                        if (setList.size()!=lineCodeRuleList.length){
                                            reasonFailure.append("?????????????????????!");
                                        }
                                        StringBuffer lineCodeRulesSb = new StringBuffer();
                                        for (String s : lineCodeRuleList) {
                                            lineCodeRulesSb.append("'").append(s).append("',");
                                        }
                                        List<CmCustomerLine> lines = cmCustomerLineMapper.getCmCustomerLineByLineCodeRules(lineCodeRulesSb.substring(0, lineCodeRulesSb.length() - 1));
                                        if (lines == null || lines.isEmpty()) {
                                            reasonFailure.append("?????????????????????!");
                                        } else {
                                            if (lineCodeRuleList.length == lines.size()) {
//                                            List<Map> lineList = new ArrayList<>();
//                                            String jsonStr = JsonHelper.toJson(lines);
                                                for (CmCustomerLine line : lines) {
                                                    if (line.getId() != null) {
                                                        line.setLineId(line.getId());
                                                    }
                                                }
                                                param.put("vehicleLineRels", lines);
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
                                                if (org.apache.commons.lang.StringUtils.isNotBlank(lineMsg)) {
                                                    reasonFailure.append("???????????????" + lineMsg.substring(0, lineMsg.length() - 1) + "????????????");
                                                }
                                            }
                                        }
                                    }

                                    String shareFlg = ExcelUtils.getExcelValue(rowData, 22);
                                    param.put("shareFlg", "???".equals(shareFlg) ? 1 : 0);
                                    if ("???".equals(shareFlg)) {
                                        String linkUserPhone = ExcelUtils.getExcelValue(rowData, 23);
                                        if (org.apache.commons.lang.StringUtils.isBlank(linkUserPhone)) {
                                            reasonFailure.append("???????????????????????????!");
                                        } else {
                                            com.youming.youche.system.domain.UserDataInfo userDataInfoServicePhone = userDataInfoService.getPhone(linkUserPhone);
                                            if (userDataInfoServicePhone == null) {
                                                reasonFailure.append("???????????????????????????!");
                                            } else {
                                                param.put("linkUserId", userDataInfoServicePhone.getId());
                                            }
                                        }
                                    }
                                }
                                String excelValue = ExcelUtils.getExcelValue(rowData, 24);
                                if (StringUtils.isNotBlank(excelValue)) {
                                    try {
                                        Long excelValueInteger = Long.parseLong(excelValue);
                                        if (excelValueInteger * 100 > Integer.MAX_VALUE) {
                                            reasonFailure.append("????????????????????????!");
                                        }
                                    } catch (Exception e) {
                                        reasonFailure.append("????????????????????????");
                                    }
                                    Number value = Double.parseDouble(excelValue) * 100;
                                    param.put("price", value.intValue());
                                }

                                String excelValue1 = ExcelUtils.getExcelValue(rowData, 25);
                                if (StringUtils.isNotBlank(excelValue1)) {
                                    try {
                                        Long excelValue1Integer = Long.parseLong(excelValue1);
                                        if (excelValue1Integer * 100 > Integer.MAX_VALUE) {
                                            reasonFailure.append("??????????????????!");
                                        }
                                    } catch (Exception e) {
                                        reasonFailure.append("??????????????????");
                                    }
                                    Number value = Double.parseDouble(excelValue1) * 100;
                                    param.put("residual", value.intValue());
                                }

                                String excelValue9 = ExcelUtils.getExcelValue(rowData, 26);
                                if (StringUtils.isNotBlank(excelValue9)) {
                                    try {
                                        Long excelValue9Integer = Long.parseLong(excelValue9);
                                        if (excelValue9Integer * 100 > Integer.MAX_VALUE) {
                                            reasonFailure.append("????????????????????????!");
                                        }
                                    } catch (Exception e) {
                                        reasonFailure.append("????????????????????????");
                                    }
                                    Number value = Double.parseDouble(excelValue9) * 100;
                                    param.put("loanInterest", value.intValue());
                                }

                                String interestPeriods = ExcelUtils.getExcelValue(rowData, 27);
                                if (StringUtils.isNotBlank(interestPeriods)) {
                                    try {
                                        Integer.parseInt(interestPeriods);
                                    } catch (Exception e) {
                                        reasonFailure.append("??????????????????!");
                                    }
                                }
                                param.put("interestPeriods", interestPeriods);

                                String payInterestPeriods = ExcelUtils.getExcelValue(rowData, 28);
                                if (StringUtils.isNotBlank(payInterestPeriods)) {
                                    try {
                                        Integer.parseInt(payInterestPeriods);
                                    } catch (Exception e) {
                                        reasonFailure.append("??????????????????!");
                                    }
                                }
                                param.put("payInterestPeriods", payInterestPeriods);
                                param.put("purchaseDate", ExcelUtils.getExcelValue(rowData, 29));
                                param.put("depreciatedMonth", ExcelUtils.getExcelValue(rowData, 30));
                                String excelValue7 = ExcelUtils.getExcelValue(rowData, 31);
                                if (StringUtils.isNotBlank(excelValue7)) {
                                    try {
                                        Long excelValue7Integer = Long.parseLong(excelValue7);
                                        if (excelValue7Integer * 100 > Integer.MAX_VALUE) {
                                            reasonFailure.append("??????????????????!");
                                        }
                                    } catch (Exception e) {
                                        reasonFailure.append("??????????????????");
                                    }
                                    Number value = Double.parseDouble(excelValue7) * 100;
                                    param.put("collectionInsurance", value.intValue());
                                }


                                String excelValue6 = ExcelUtils.getExcelValue(rowData, 32);
                                if (StringUtils.isNotBlank(excelValue6)) {
                                    try {
                                        Long excelValue6Integer = Long.parseLong(excelValue6);
                                        if (excelValue6Integer * 100 > Integer.MAX_VALUE) {
                                            reasonFailure.append("?????????????????????!");
                                        }
                                    } catch (Exception e) {
                                        reasonFailure.append("?????????????????????");
                                    }
                                    Number value = Double.parseDouble(excelValue6) * 100;
                                    param.put("examVehicleFee", value.intValue());
                                }
                                String excelValue5 = ExcelUtils.getExcelValue(rowData, 33);
                                if (StringUtils.isNotBlank(excelValue5)) {
                                    try {
                                        Long excelValue5Integer = Long.parseLong(excelValue5);
                                        if (excelValue5Integer * 100 > Integer.MAX_VALUE) {
                                            reasonFailure.append("?????????????????????!");
                                        }
                                    } catch (Exception e) {
                                        reasonFailure.append("?????????????????????");
                                    }
                                    Number value = Double.parseDouble(excelValue5) * 100;
                                    param.put("maintainFee", value.intValue());
                                }

                                String excelValue2 = ExcelUtils.getExcelValue(rowData, 34);
                                if (StringUtils.isNotBlank(excelValue2)) {
                                    try {
                                        Long excelValue2Integer = Long.parseLong(excelValue2);
                                        if (excelValue2Integer * 100 > Integer.MAX_VALUE) {
                                            reasonFailure.append("?????????????????????!");
                                        }
                                    } catch (Exception e) {
                                        reasonFailure.append("?????????????????????");
                                    }
                                    Number value = Double.parseDouble(excelValue2) * 100;
                                    param.put("repairFee", value.intValue());
                                }

                                String excelValue3 = ExcelUtils.getExcelValue(rowData, 35);
                                if (StringUtils.isNotBlank(excelValue3)) {
                                    try {
                                        Long excelValue3Integer = Long.parseLong(excelValue3);
                                        if (excelValue3Integer * 100 > Integer.MAX_VALUE) {
                                            reasonFailure.append("?????????????????????!");
                                        }
                                    } catch (Exception e) {
                                        reasonFailure.append("?????????????????????");
                                    }
                                    Number value = Double.parseDouble(excelValue3) * 100;
                                    param.put("tyreFee", value.intValue());
                                }

                                String excelValue4 = ExcelUtils.getExcelValue(rowData, 36);
                                if (StringUtils.isNotBlank(excelValue4)) {
                                    try {
                                        Long excelValue4Integer = Long.parseLong(excelValue4);
                                        if (excelValue4Integer * 100 > Integer.MAX_VALUE) {
                                            reasonFailure.append("????????????????????????!");
                                        }
                                    } catch (Exception e) {
                                        reasonFailure.append("????????????????????????");
                                    }
                                    Number value = Double.parseDouble(excelValue4) * 100;
                                    param.put("otherFee", value.intValue());
                                }

                                String excelValue8 = ExcelUtils.getExcelValue(rowData, 37);
                                if (StringUtils.isNotBlank(excelValue8)) {
                                    Number value = Double.parseDouble(excelValue8) * 100;
                                    param.put("manageFee", value.intValue());//?????????
                                }


                                reasonFailure.append(ExcelUtils.checkDateTypeValueAdd(rowData, 40, param, "annualVeriTime", "??????????????????"));
                                reasonFailure.append(ExcelUtils.checkDateTypeValueAdd(rowData, 41, param, "annualVeriTimeEnd", "??????????????????"));
                                reasonFailure.append(ExcelUtils.checkDateTypeValueAdd(rowData, 42, param, "seasonalVeriTime", "??????????????????"));
                                reasonFailure.append(ExcelUtils.checkDateTypeValueAdd(rowData, 43, param, "seasonalVeriTimeEnd", "??????????????????"));
                                reasonFailure.append(ExcelUtils.checkDateTypeValueAdd(rowData, 44, param, "busiInsuranceTime", "?????????????????????"));
                                reasonFailure.append(ExcelUtils.checkDateTypeValueAdd(rowData, 45, param, "busiInsuranceTimeEnd", "?????????????????????"));
                                String busiInsuranceCode = ExcelUtils.getExcelValue(rowData, 46);//???????????????
                                if (StringUtils.isNotBlank(busiInsuranceCode) && busiInsuranceCode.length() > 20) {
                                    reasonFailure.append("?????????????????????20???");
                                }
                                param.put("busiInsuranceCode", busiInsuranceCode);


                                reasonFailure.append(ExcelUtils.checkDateTypeValueAdd(rowData, 47, param, "insuranceTime", "?????????????????????"));
                                reasonFailure.append(ExcelUtils.checkDateTypeValueAdd(rowData, 48, param, "insuranceTimeEnd", "?????????????????????"));
                                String insuranceCode = ExcelUtils.getExcelValue(rowData, 49);//???????????????
                                if (StringUtils.isNotBlank(insuranceCode) && insuranceCode.length() > 20) {
                                    reasonFailure.append("?????????????????????20???");
                                }
                                param.put("insuranceCode", insuranceCode);//???????????????

                                reasonFailure.append(ExcelUtils.checkDateTypeValueAdd(rowData, 50, param, "otherInsuranceTime", "?????????????????????"));
                                reasonFailure.append(ExcelUtils.checkDateTypeValueAdd(rowData, 51, param, "otherInsuranceTimeEnd", "?????????????????????"));
                                String otherInsuranceCode = ExcelUtils.getExcelValue(rowData, 52);//???????????????
                                if (StringUtils.isNotBlank(otherInsuranceCode) && otherInsuranceCode.length() > 20) {
                                    reasonFailure.append("?????????????????????20???");
                                }
                                param.put("otherInsuranceCode", otherInsuranceCode);//???????????????
                                String excelValue11 = ExcelUtils.getExcelValue(rowData, 53);
                                if (StringUtils.isNotBlank(excelValue11)) {
                                    Number value = Double.parseDouble(excelValue11) * 100;
                                    param.put("maintainDis", value.longValue());//????????????(??????)
                                }
                                String excelValue10 = ExcelUtils.getExcelValue(rowData, 54);
                                if (StringUtils.isNotBlank(excelValue10)) {
                                    Number value = Double.parseDouble(excelValue10) * 100;
                                    param.put("maintainWarnDis", value.longValue());//??????????????????(??????)
                                }
                                reasonFailure.append(ExcelUtils.checkDateTypeValueAdd(rowData, 55, param, "prevMaintainTime", "??????????????????"));
                                reasonFailure.append(ExcelUtils.checkDateTypeValueAdd(rowData, 56, param, "registrationTime", "????????????"));
                                param.put("registrationNumble", ExcelUtils.getExcelValue(rowData, 57));//????????????
                                reasonFailure.append(ExcelUtils.checkDateTypeValueAdd(rowData, 58, param, "vehicleValidityTimeBegin", "?????????????????????"));
                                reasonFailure.append(ExcelUtils.checkDateTypeValueAdd(rowData, 59, param, "vehicleValidityTime", "?????????????????????"));
                                reasonFailure.append(ExcelUtils.checkDateTypeValueAdd(rowData, 60, param, "operateValidityTimeBegin", "?????????????????????"));
                                reasonFailure.append(ExcelUtils.checkDateTypeValueAdd(rowData, 61, param, "operateValidityTime", "?????????????????????"));
                                param.put("vehicleModel", ExcelUtils.getExcelValue(rowData, 62));
                            }
                        }
                    }
                    if (StrUtil.isEmpty(reasonFailure)) {
                        //??????
                        if (param.containsKey("isModify")) {
                            Object seasonalVeriTimeEnd = param.get("seasonalVeriTimeEnd");
                            if (seasonalVeriTimeEnd != null) {
                                String date = String.valueOf(seasonalVeriTimeEnd);
                                param.put("seasonalVeriTimeEnd", LocalDate.parse(date, DateTimeFormatter.ofPattern(DATETIME12_FORMAT)));
                            }
                            VehicleInfoUptVo vehicleInfoUptVo = JSON.parseObject(JSON.toJSONString(param), VehicleInfoUptVo.class);
                            try {
                                doUpdateVehicleInfo(vehicleInfoUptVo, token);
                            } catch (BusinessException e) {
                                reasonFailure.append(e.getMessage());
                                VehicleInfoVo vehicleInfoVo = new VehicleInfoVo();
                                vehicleInfoVo.setPlateNumber(DataFormat.getStringKey(param, "plateNumber"));
                                vehicleInfoVo.setVinNo(DataFormat.getStringKey(param, "vinNo"));
                                vehicleInfoVo.setFailure(reasonFailure.toString());
                                failureList.add(vehicleInfoVo);
                            }
                        } else {
                            VehicleInfoVo vehicleInfoVo = JSON.parseObject(JSON.toJSONString(param), VehicleInfoVo.class);
                            try {
                                doSaveVehicleInfo(vehicleInfoVo, token);
                            } catch (BusinessException e) {
                                reasonFailure.append(e.getMessage());
                                vehicleInfoVo.setFailure(reasonFailure.toString());
                                failureList.add(vehicleInfoVo);
                            }
                        }
                        //??????????????????
                        success++;
                    } else {
                        VehicleInfoVo vehicleInfoVo = new VehicleInfoVo();
                        vehicleInfoVo.setPlateNumber(DataFormat.getStringKey(param, "plateNumber"));
                        vehicleInfoVo.setVinNo(DataFormat.getStringKey(param, "vinNo"));
                        vehicleInfoVo.setFailure(reasonFailure.toString());
                        failureList.add(vehicleInfoVo);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    reasonFailure.append(e.getMessage());
                    VehicleInfoVo vehicleInfoVo = new VehicleInfoVo();
                    vehicleInfoVo.setPlateNumber(DataFormat.getStringKey(param, "plateNumber"));
                    vehicleInfoVo.setVinNo(DataFormat.getStringKey(param, "vinNo"));
                    vehicleInfoVo.setFailure(reasonFailure.toString());
                    failureList.add(vehicleInfoVo);
                }
            }
            if (org.apache.commons.collections.CollectionUtils.isNotEmpty(failureList)) {
                String[] showName;
                String[] resourceFild;
                showName = new String[]{"????????????", "?????????", "????????????"};
                resourceFild = new String[]{"getPlateNumber", "getVinNo", "getFailure"};
                XSSFWorkbook workbook = ExportExcel.getWorkbookXlsx(failureList, showName, resourceFild, VehicleInfoVo.class, null);
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
            try {
                String[] showName;
                String[] resourceFild;
                showName = new String[]{"????????????", "?????????", "????????????"};
                resourceFild = new String[]{"getPlateNumber", "getVinNo", "getFailure"};
                XSSFWorkbook workbook = null;
                workbook = ExportExcel.getWorkbookXlsx(failureList, showName, resourceFild, VehicleInfoVo.class, null);
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                workbook.write(os);
                byte[] b = os.toByteArray();
                InputStream inputStream1 = new ByteArrayInputStream(b);
                //????????????
                FastDFSHelper client = FastDFSHelper.getInstance();
                String failureExcelPath = client.upload(inputStream1, "????????????.xlsx", inputStream1.available());
                os.close();
                inputStream1.close();
                records.setFailureUrl(failureExcelPath);
                records.setRemarks("????????????");
                records.setFailureReason("??????????????????????????????!");
                records.setState(4);
                importOrExportRecordsService.update(records);
            } catch (Exception exception) {
                records.setState(5);
                records.setFailureReason("????????????,????????????????????????????????????");
                records.setRemarks("????????????,????????????????????????????????????");
                importOrExportRecordsService.update(records);
                exception.printStackTrace();
            }
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public boolean doRemoveVehicle(Long vehicleCode, Boolean delOutVehicleDriver, String accessToken) {
        if (vehicleCode < 0) {
            throw new BusinessException("?????????????????????");
        }
        VehicleDataInfo vehicleDataInfo = baseMapper.selectById(vehicleCode);
        if (null == vehicleDataInfo) {
            throw new BusinessException("????????????????????????");
        }

        List<TenantVehicleRel> tenantVehicleRelList = tenantVehicleRelMapper.getTenantVehicleRelList(vehicleCode);
        if (tenantVehicleRelList == null && tenantVehicleRelList.size() == 0) {
            throw new BusinessException("????????????????????????");
        }
        LoginInfo loginInfo = loginUtils.get(accessToken);
        TenantVehicleRel tenantVehicleRel = null;
        if (tenantVehicleRelList != null && tenantVehicleRelList.size() > 0) {
            tenantVehicleRel = tenantVehicleRelList.get(0);
            if (tenantVehicleRelList.size() > 1) {
                for (TenantVehicleRel vehicleRel : tenantVehicleRelList) {
                    if (vehicleRel.getTenantId().equals(loginInfo.getTenantId())) {
                        tenantVehicleRel = vehicleRel;
                        break;
                    }
                }

            }

            //??????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
            if (null != tenantVehicleRel.getVehicleClass() && tenantVehicleRel.getVehicleClass().intValue() == VEHICLE_CLASS1) {
                //???????????????????????????????????????????????????????????????????????????
                removeOwnCarAndNotifyOtherTenant(vehicleDataInfo.getId(), accessToken);
                //????????????
                removeApplyRecord(vehicleCode);
                vehicleDataInfo.setTenantId(null);
            } else {
                try {
                    //???????????????????????????,??????????????????????????????
                    VehicleDataInfoVer vehicleDataInfoVer = new VehicleDataInfoVer();
                    BeanUtil.copyProperties(vehicleDataInfo, vehicleDataInfoVer);
                    vehicleDataInfoVer.setVehicleCode(vehicleDataInfo.getId());
                    vehicleDataInfoVer.setVerState(VER_STATE_HIS);
                    vehicleDataInfoVer.setCreateTime(LocalDateTime.now());
                    vehicleDataInfoVer.setIsAuthSucc(3);
                    vehicleDataInfoVerMapper.insert(vehicleDataInfoVer);
                    dissolveCooperationVehicle(tenantVehicleRel.getId(), vehicleDataInfo.getId(), accessToken);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //????????????????????????????????????
                if (delOutVehicleDriver != null && delOutVehicleDriver && tenantVehicleRel.getDriverUserId() != null) {
                    doRemoveDriverForVehicle(tenantVehicleRel.getDriverUserId(), tenantVehicleRel.getTenantId(), accessToken);
                }
            }
        }
        saveSysOperLog(SysOperLogConst.BusiCode.Vehicle, tenantVehicleRel.getId(), SysOperLogConst.OperType.Del, "????????????" + vehicleDataInfo.getPlateNumber(), accessToken);
        return true;
    }

    /**
     * ???source??????target
     *
     * @param source source
     * @param target target
     * @param <T>    ???????????????
     * @return ?????????
     * @throws Exception newInstance????????????????????????
     */
    public static <T> T mapToObj(Map source, Class<T> target) throws Exception {
        Field[] fields = target.getDeclaredFields();
        T o = target.newInstance();
        for (Field field : fields) {
            Object val;
            if ((val = source.get(field.getName())) != null) {
                field.setAccessible(true);
                field.set(o, val);
            }
        }
        return o;
    }

    /**
     * ??????????????????????????????
     */
    public static boolean isNumber(String value) {
        return isInteger(value) || isDouble(value) || isLong(value);
    }

    /**
     * ??????????????????????????????
     */
    public static boolean isInteger(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * ?????????????????????????????????
     */
    public static boolean isDouble(String value) {
        try {
            Double.parseDouble(value);
            if (value.contains(".")) {
                return true;
            }
            return false;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * ??????????????????????????????
     */
    public static boolean isLong(String value) {
        try {
            Long.parseLong(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Override
    public Page<HistoricalArchivesVo> doQueryVehicleAllHis(String plateNumber, String linkManName, String
            tenantName, String linkPhone,
                                                           String linkman, String mobilePhone, String vehicleLength, Integer vehicleStatus,
                                                           Integer vehicleClass, Page<HistoricalArchivesVo> page, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        Page<HistoricalArchivesVo> historicalArchivesVoPage = baseMapper.doQueryVehicleAllHis(page, loginInfo.getTenantId(), 9, plateNumber, linkManName, tenantName,
                linkPhone, linkman, mobilePhone, vehicleLength, vehicleStatus, vehicleClass);
        if (historicalArchivesVoPage.getRecords() != null && historicalArchivesVoPage.getRecords().size() > 0) {
            for (HistoricalArchivesVo historicalArchivesVo : historicalArchivesVoPage.getRecords()) {
                try {
                    if (historicalArchivesVo.getVehicleClass() != null && historicalArchivesVo.getVehicleClass() > -1) {
                        historicalArchivesVo.setVehicleClassName(SysStaticDataRedisUtils.getSysStaticDataByCodeValue2String(redisUtil, "VEHICLE_CLASS", historicalArchivesVo.getVehicleClass() + ""));

                    }
                    if (historicalArchivesVo.getLicenceType() != null && historicalArchivesVo.getLicenceType() > -1) {
                        historicalArchivesVo.setLicenceTypeName(SysStaticDataRedisUtils.getSysStaticDataByCodeValue2String(redisUtil, "LICENCE_TYPE", historicalArchivesVo.getLicenceType() + ""));
                    }
                    if (historicalArchivesVo.getVehicleStatus() != null && historicalArchivesVo.getVehicleStatus() > 0) {
//                        historicalArchivesVo.setVehicleStatusName(isysStaticDataService.getSysStaticDataCodeName("VEHICLE_STATUS", String.valueOf(historicalArchivesVo.getVehicleStatus())).getCodeName());
                        historicalArchivesVo.setVehicleStatusName(SysStaticDataRedisUtils.getSysStaticDataById2String(redisUtil, "VEHICLE_STATUS", historicalArchivesVo.getVehicleStatus()));
                    }
                    if (StringUtils.isNotBlank(historicalArchivesVo.getVehicleLength()) && !historicalArchivesVo.getVehicleLength().equals("-1")) {
                        if (historicalArchivesVo.getVehicleStatus() == 8) {
                            historicalArchivesVo.setVehicleLengthName(SysStaticDataRedisUtils.getSysStaticDataById2String(redisUtil, "VEHICLE_STATUS_SUBTYPE", Long.valueOf(historicalArchivesVo.getVehicleLength())));
                        } else {
                            historicalArchivesVo.setVehicleLengthName(SysStaticDataRedisUtils.getSysStaticDataById2String(redisUtil, "VEHICLE_LENGTH", Long.valueOf(historicalArchivesVo.getVehicleLength())));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (historicalArchivesVo.getVehicleStatus() != null && historicalArchivesVo.getVehicleStatus() > 0 && StringUtils.isNotBlank(historicalArchivesVo.getVehicleLength()) &&
                        historicalArchivesVo.getVehicleLength().equals("-1")) {
                    historicalArchivesVo.setVehicleInfo(historicalArchivesVo.getVehicleStatusName() + "/" + historicalArchivesVo.getVehicleLengthName());
                } else if (historicalArchivesVo.getVehicleStatus() != null && historicalArchivesVo.getVehicleStatus() > 0 && (StringUtils.isNotBlank(historicalArchivesVo.getVehicleLength()) ||
                        historicalArchivesVo.getVehicleLength().equals("-1"))) {
                    historicalArchivesVo.setVehicleInfo(historicalArchivesVo.getVehicleStatusName());
                } else {
                    historicalArchivesVo.setVehicleInfo(historicalArchivesVo.getVehicleLengthName());
                }
            }
        }
        return historicalArchivesVoPage;
    }

    @Override
    public void updateVehicleVerAllVerState(String tableName, Map<String, Object> inMap) throws BusinessException {
        long vehicleCode = DataFormat.getLongKey(inMap, "vehicleCode");
        long tenantId = DataFormat.getLongKey(inMap, "tenantId");
        int destVerState = DataFormat.getIntKey(inMap, "destVerState");
        if (destVerState < 0) {
            throw new BusinessException("destVerState???????????????");
        }
        //????????????
        if ("vehicle_data_info_ver".equals(tableName) || "all".equals(tableName)) {
            vehicleDataInfoVerMapper.updVehicleDataInfoVerState(destVerState, vehicleCode);
        }
        //???????????????
        if ("vehicle_object_line_ver".equals(tableName) || "all".equals(tableName)) {
            vehicleDataInfoVerMapper.updVehicleObjectLineVerState(destVerState, vehicleCode);
        }
        //????????????
        if ("vehicle_line_rel_ver".equals(tableName) || "all".equals(tableName)) {
            vehicleDataInfoVerMapper.updVehicleLineRelVerState(destVerState, vehicleCode);
        }
        //?????????????????????
        if ("tenant_vehicle_rel_ver".equals(tableName) || "all".equals(tableName)) {
            vehicleDataInfoVerMapper.updTenantVehicleRelVerState(destVerState, vehicleCode, tenantId);
        }
        //???????????????
        if ("tenant_vehicle_cost_rel_ver".equals(tableName) || "all".equals(tableName)) {
            vehicleDataInfoVerMapper.updTenantVehicleCostRelVerState(destVerState, vehicleCode, tenantId);
        }
        //???????????????
        if ("tenant_vehicle_cert_rel_ver".equals(tableName) || "all".equals(tableName)) {
            vehicleDataInfoVerMapper.updTenantVehicleCertRelVerState(destVerState, vehicleCode, tenantId);
        }
    }


    public void doRemoveDriverForVehicle(long userId, long tenantId, String accessToken) {
        List<ApplyRecord> applyRecordList = applyRecordService.getApplyRecordList(userId, 0, tenantId);
        if (CollectionUtils.isNotEmpty(applyRecordList)) {
            throw new BusinessException("????????????????????????????????????");
        }


        List<TenantUserRel> tenantUserRelList = tenantUserRelMapper.getTenantUserRels(userId, tenantId);
        if (tenantUserRelList != null && tenantUserRelList.size() > 0) {
            for (TenantUserRel tenantUserRel : tenantUserRelList) {
                if (tenantUserRel != null) {
                    delete(tenantUserRel.getId(), null, accessToken);
                }
            }
        }

    }

    public void delete(Long tenantUserRelId, String tenantVehicleIds, String accessToken) {
        TenantUserRel tenantUserRel = tenantUserRelMapper.selectById(tenantUserRelId);

        if (null == tenantUserRel) {
            log.error("????????????????????????????????????????????????relId====>" + tenantUserRelId);
            throw new BusinessException("????????????");
        }

        List<ApplyRecord> applyRecordList = applyRecordService.getApplyRecordList(tenantUserRel.getUserId(), 0, tenantUserRel.getTenantId());
        if (CollectionUtils.isNotEmpty(applyRecordList)) {
            throw new BusinessException("????????????????????????????????????");
        }
        List<TenantVehicleRel> tenantVehicleRelList = tenantVehicleRelMapper.getTenantVehicleRelListByDriverUserId(tenantUserRel.getTenantId(), tenantUserRel.getUserId());
        if (tenantVehicleRelList != null) {
            for (TenantVehicleRel tenantVehicleRel : tenantVehicleRelList) {
                tenantVehicleRel.setDriverUserId(tenantUserRel.getUserId());
                tenantVehicleRelMapper.updateById(tenantVehicleRel);

                TenantVehicleRelVer tenantVehicleRelVer = tenantVehicleRelVerMapper.getTenantVehicleRelVer(tenantVehicleRel.getId(), 1);
                if (tenantVehicleRelVer != null) {
                    tenantVehicleRelVer.setDriverUserId(tenantUserRel.getUserId());
                    tenantVehicleRelVerMapper.updateById(tenantVehicleRelVer);
                }
            }
        }
        TenantUserRelVer relVer = new TenantUserRelVer();
        BeanUtil.copyProperties(tenantUserRel, relVer);
        relVer.setVerState(SysStaticDataEnum.VER_STATE.VER_STATE_HIS);
        tenantUserRelVerMapper.insert(relVer);
        tenantUserRelVerMapper.deleteById(tenantUserRel.getId());
        saveSysOperLog(SysOperLogConst.BusiCode.Driver, tenantUserRel.getId(), SysOperLogConst.OperType.Remove, "????????????", accessToken);
    }

    //map???java??????
    public static Object mapToObject(Map<String, Object> map, Class<?> beanClass)
            throws Exception {
        if (map == null) {
            return null;
        }
        Object obj = beanClass.newInstance();
        org.apache.commons.beanutils.BeanUtils.populate(obj, map);
        return obj;
    }

    //????????????
    @Override
    public void removeApplyRecord(long vehicleCode) {

        List<ApplyRecord> applyRecordList = applyRecordService.selectByBusidAndState(vehicleCode, SysStaticDataEnum.APPLY_STATE.APPLY_STATE0);
        if (applyRecordList != null && applyRecordList.size() > 0) {
            for (ApplyRecord applyRecord : applyRecordList) {
                applyRecord.setState(SysStaticDataEnum.APPLY_STATE.APPLY_STATE6);
                applyRecordService.updateById(applyRecord);
            }
        }
    }

    @Override
    public void removeOwnCarAndNotifyOtherTenant(long vehicleCode, String accessToken) {
        List<TenantVehicleRel> tenantVehicleRelList = tenantVehicleRelMapper.getTenantVehicleRelList(vehicleCode);
        if (CollectionUtils.isEmpty(tenantVehicleRelList)) {
            return;
        }

        Long sourceTenatId = null;
        for (TenantVehicleRel tenantVehicleRel : tenantVehicleRelList) {
            if (tenantVehicleRel.getVehicleClass() == VEHICLE_CLASS1) {
                sourceTenatId = tenantVehicleRel.getTenantId();
                VehicleDataInfo vehicleDataInfo = baseMapper.selectById(vehicleCode);
                if (null != vehicleDataInfo) {
                    try {
                        VehicleDataInfoVer vehicleDataInfoVer = new VehicleDataInfoVer();
                        BeanUtil.copyProperties(vehicleDataInfo, vehicleDataInfoVer);
                        vehicleDataInfoVer.setVehicleCode(vehicleDataInfo.getId());
                        vehicleDataInfoVer.setVerState(VER_STATE_HIS);
                        vehicleDataInfoVer.setCreateTime(LocalDateTime.now());
                        vehicleDataInfoVer.setIsAuthSucc(3);
                        vehicleDataInfoVerMapper.insert(vehicleDataInfoVer);
                        dissolveCooperationVehicle(tenantVehicleRel.getId(), vehicleDataInfoVer.getId(), accessToken);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        if (null == sourceTenatId) {
            return;
        }
        SysTenantDef sourceTenant = sysTenantDefService.getById(sourceTenatId);
        for (TenantVehicleRel tenantVehicleRel : tenantVehicleRelList) {
            if (tenantVehicleRel.getVehicleClass() != VEHICLE_CLASS1 && tenantVehicleRel.getTenantId() > 1) {
                VehicleUpdateNotify vehicleUpdateNotify = new VehicleUpdateNotify();
                vehicleUpdateNotify.setCreateDate(LocalDateTime.now());
                vehicleUpdateNotify.setTenantId(tenantVehicleRel.getTenantId());
                vehicleUpdateNotify.setSourceTenantId(sourceTenant.getId());
                vehicleUpdateNotify.setSourceTenantName(sourceTenant.getName());
                vehicleUpdateNotify.setVehicleClass(tenantVehicleRel.getVehicleClass());
                vehicleUpdateNotify.setVehicleCode(tenantVehicleRel.getId());
                vehicleUpdateNotify.setPlateNumber(tenantVehicleRel.getPlateNumber());
                vehicleUpdateNotify.setAlreadyRead(false);
                vehicleUpdateNotifyMapper.insert(vehicleUpdateNotify);
            }
        }
    }

    public void doSaveVehicleOrderPositionInfoForOBMS(Long vehicleCode, String plateNumber) {
        Integer integer = vehicleOrderPositionInfoMapper.countOrderPositionInfo(vehicleCode);
        if (integer == 0) {
            VehicleOrderPositionInfo vehicleOrderPositionInfo = new VehicleOrderPositionInfo();
            vehicleOrderPositionInfo.setPlateNumber(plateNumber);
            vehicleOrderPositionInfo.setVehicleCode(vehicleCode);
            vehicleOrderPositionInfo.setShareFlg(1);
            vehicleOrderPositionInfoMapper.insert(vehicleOrderPositionInfo);
        }
    }

    public void addAttachVehicleAndDriver(VehicleDataInfo vehicleDataInfo, long attachTenantId, int channel, String
            accessToken)  {

        //??????????????????
        String channelName = "";
        if (channel == 1) {
            channelName = "TMS";
        } else if (channel == 2) {
            channelName = "APP";
        } else if (channel == 3) {
            channelName = "OBMS";
        }

        List<TenantVehicleRel> tenantVehicleRelList = tenantVehicleRelMapper.getZYVehicleByVehicleCode(vehicleDataInfo.getId(), 4);
        TenantVehicleRel oldTenantVehicleRel = null;
        //??????????????????????????????????????????
        if (tenantVehicleRelList != null && tenantVehicleRelList.size() > 0) {
            oldTenantVehicleRel = tenantVehicleRelList.get(0);
            if (oldTenantVehicleRel.getTenantId() == attachTenantId) {//????????????????????????????????????????????????????????????????????????
                return;
            }
            dissolveCooperationVehicle(oldTenantVehicleRel.getId(), accessToken);

            //??????????????????????????????????????????
            doRemoveDriverOnlyOneVehicle(vehicleDataInfo.getDriverUserId(), oldTenantVehicleRel.getTenantId());
        }

        //????????????????????????
        TenantVehicleRel tenantVehicleRel = new TenantVehicleRel();
        tenantVehicleRel.setTenantId(attachTenantId);
        tenantVehicleRel.setVehicleCode(vehicleDataInfo.getId());
        tenantVehicleRel.setPlateNumber(vehicleDataInfo.getPlateNumber());
        tenantVehicleRel.setTenantId(attachTenantId);
        tenantVehicleRel.setDriverUserId(vehicleDataInfo.getDriverUserId());
        tenantVehicleRel.setVehicleClass(SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS4);
        tenantVehicleRel.setAuthState(SysStaticDataEnum.AUTH_STATE.AUTH_STATE1);
        tenantVehicleRel.setIsAuth(SysStaticDataEnum.IS_AUTH.IS_AUTH1);
        tenantVehicleRel.setCreateTime(LocalDateTime.now());

        if (StringUtils.isNotBlank(vehicleDataInfo.getAttachUserMobile())) {
            tenantVehicleRel.setBillReceiverMobile(vehicleDataInfo.getAttachUserMobile());
            tenantVehicleRel.setBillReceiverName(vehicleDataInfo.getAttachUserName());
            tenantVehicleRel.setBillReceiverUserId(vehicleDataInfo.getAttachUserId());
        }

        tenantVehicleRelMapper.insert(tenantVehicleRel);

        TenantVehicleRelVer tenantVehicleRelVer = new TenantVehicleRelVer();
        BeanUtil.copyProperties(tenantVehicleRel, tenantVehicleRelVer);
        tenantVehicleRelVer.setRelId(tenantVehicleRel.getId());
        tenantVehicleRelVer.setVerState(VER_STATE_Y);
        tenantVehicleRelVer.setCreateTime(LocalDateTime.now());
        tenantVehicleRelVerMapper.insert(tenantVehicleRelVer);

       /* //??????????????????
        Map iMap = new HashMap();
        iMap.put("svName", "vehicleTF");
        iMap.put("tenantVehicleRelId", tenantVehicleRel.getId());
        //??????????????????
        boolean bool = iAuditOutTF.startProcess("100005", tenantVehicleRel.getId(), SysOperLogConst.BusiCode.Vehicle, iMap, attachTenantId);
        if (!bool) {
            throw new BusinessException("???????????????????????????");
        }

        //?????????
        String driverLogMsg = tenantVehicleRel.getPlateNumber() + "???" + channelName + "??????????????????(" + attachTenantId + ")??????????????????";

        saveSysOperLog(SysOperLogConst.BusiCode.Vehicle, tenantVehicleRel.getId(), SysOperLogConst.OperType.Add, driverLogMsg, tenantVehicleRel.getTenantId(), accessToken);
*/
        if (oldTenantVehicleRel != null) {
            //???????????????????????????
            List<TenantUserRel> exitOldTenantUserRels = tenantUserRelMapper.getTenantUserRelList(vehicleDataInfo.getDriverUserId(), oldTenantVehicleRel.getTenantId(), VEHICLE_CLASS4);
            if (exitOldTenantUserRels != null && exitOldTenantUserRels.size() > 0) {
                //??????????????????????????????????????????
                doRemoveDriverOnlyOneVehicle(vehicleDataInfo.getDriverUserId(), oldTenantVehicleRel.getTenantId());
            }
        }

        //??????????????????????????????????????????

        //?????????????????????????????????
        List<TenantUserRel> exitTenantUserRels = tenantUserRelMapper.getTenantUserRelList(vehicleDataInfo.getDriverUserId(), null, null);
        if (exitTenantUserRels == null || exitTenantUserRels.size() == 0) {//????????????????????????????????????????????????????????????
            //????????????????????????
            TenantUserRel tenantUserRel = new TenantUserRel();
            tenantUserRel.setUserId(vehicleDataInfo.getDriverUserId());
            tenantUserRel.setTenantId(attachTenantId);
            tenantUserRel.setState(SysStaticDataEnum.SYS_USER_STATE.AUDIT_NOT);
            tenantUserRel.setCarUserType(SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS4);
            tenantUserRel.setHasVer(EnumConsts.HAS_VER_STATE.HAS_VER_NO);
            tenantUserRel.setCreateDate(LocalDateTime.now());
            tenantUserRelMapper.insert(tenantUserRel);

            TenantUserRelVer tenantUserRelVer = new TenantUserRelVer();
            BeanUtil.copyProperties(tenantUserRel, tenantUserRelVer);
            tenantUserRelVer.setRelId(tenantUserRel.getId());
            tenantUserRelVer.setVerState(VER_STATE_Y);
            tenantUserRelVer.setCreateTime(LocalDateTime.now());
            tenantUserRelVerMapper.insert(tenantUserRelVer);
            //??????????????????
            Map userMap = new HashMap();
            userMap.put("svName", "vehicleTF");
            userMap.put("tenantUserRelId", tenantUserRel.getId());

            //??????????????????
            // boolean boolD = iAuditOutTF.startProcess(AUDIT_CODE_USER, tenantUserRelVer.getId(), SysOperLogConst.BusiCode.Driver, iMap, attachTenantId);

            /*if (!boolD) {
                throw new BusinessException("???????????????????????????");
            }*/

            //?????????
            String driverMsg = "??????" + tenantUserRel.getUserId() + "???" + channelName + "??????????????????" + attachTenantId + "????????????????????????";
            saveSysOperLog(SysOperLogConst.BusiCode.Driver, tenantUserRel.getUserId(), SysOperLogConst.OperType.Add, driverMsg, accessToken);

        } else {
            TenantUserRel tenantUserRel = exitTenantUserRels.get(0);
            if (tenantUserRel.getCarUserType() != SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS4) {
                throw new BusinessException("???????????????????????????????????????????????????????????????????????????????????????????????????");
            }
        }
    }

    @Override
    public void doRemoveDriverOnlyOneVehicle(long userId, long tenanId) {
        if (getTenantUserRel(userId, tenanId) != null) {
            Long vehicleCount = baseMapper.getVehicleCountByDriverUserId(userId, tenanId);
            if (vehicleCount == 1) {
                List<ApplyRecord> applyRecordList = applyRecordService.getApplyRecordList(userId, 0, tenanId);

                if (CollectionUtils.isNotEmpty(applyRecordList)) {
                    throw new BusinessException("????????????????????????????????????");
                }

            }
        }
    }

    @Override
    @Transactional
    public Boolean doSaveApplyRecordForOwnCar(ApplyRecorDto applyRecorDto, String accessToken) {
        long vehicleCode = applyRecorDto.getVehicleCode();
        if (vehicleCode <= 0) {
            log.error("????????????????????????");
            throw new BusinessException("?????????????????????!");
        }
        int vehicleClass = applyRecorDto.getApplyVehicleClass();
        if (vehicleClass <= 0) {
            log.error("????????????????????????");
            throw new BusinessException("?????????????????????!");
        }

        if (vehicleClass == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS2 || vehicleClass == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS4) {
            String billReceiverMobile = applyRecorDto.getBillReceiverMobile();
            String billReceiverName = applyRecorDto.getBillReceiverName();

            if (org.apache.commons.lang.StringUtils.isBlank(billReceiverMobile)) {
                log.error("?????????????????????????????????");
                throw new BusinessException("??????????????????????????????!");
            }

            if (org.apache.commons.lang.StringUtils.isBlank(billReceiverName)) {
                log.error("?????????????????????????????????");
                throw new BusinessException("??????????????????????????????!");
            }
        }

        /*long applyDriverUserId = DataFormat.getLongKey(imParam, "applyDriverUserId");
        if ((vehicleClass == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1
                || vehicleClass == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS4)
                && applyDriverUserId <= 0) {
            throw new BusinessException("????????????????????????");
        }*/
        String applyRemark = applyRecorDto.getApplyRemark();
        if (org.apache.commons.lang.StringUtils.isEmpty(applyRemark)) {
            log.error("????????????????????????");
            throw new BusinessException("?????????????????????!");
        }
        return this.doSaveApplyRecord(applyRecorDto, accessToken);
    }

    @Override
    public Page<VehicleDataInfoiVo> doQueryAllShareVehicle(VehicleDataInfoiDto vehicleDataInfoiDto, Integer
            pageNum, Integer pageSize) {
        Boolean fig = false;
        if (vehicleDataInfoiDto.getProvinceId() != null && vehicleDataInfoiDto.getProvinceId() > -1 ||
                vehicleDataInfoiDto.getCityId() != null && vehicleDataInfoiDto.getCityId() > -1 ||
                vehicleDataInfoiDto.getProvinceIdDes() != null && vehicleDataInfoiDto.getProvinceIdDes() > -1 ||
                vehicleDataInfoiDto.getCityIdDes() != null && vehicleDataInfoiDto.getCityIdDes() > -1) {
            fig = true;
        }
        Page<VehicleDataInfoiVo> page = new Page<>(pageNum, pageSize);
        Page<VehicleDataInfoiVo> infoiVoPage = vehicleDataInfoMapper.doQueryAllShareVehicle(page, vehicleDataInfoiDto, fig);
        List<VehicleDataInfoiVo> records = infoiVoPage.getRecords();
        List<VehicleDataInfoiVo> dataInfoiVos = vehicleDataInfoiVoTransfer.toVehicleDataInfoiVo(records, vehicleDataInfoiDto.getTenantId());
        page.setRecords(dataInfoiVos);
        return page;
    }


    public void dissolveCooperationVehicle(long relId, String accessToken) {
        TenantVehicleRel vehicleRel = tenantVehicleRelMapper.selectById(relId);
        if (vehicleRel == null) {
            throw new BusinessException("?????????????????????");
        }
        VehicleDataInfo vehicleDataInfo = baseMapper.selectById(vehicleRel.getId());
        if (null != vehicleDataInfo) {
            VehicleDataInfoVer vehicleDataInfoVer = new VehicleDataInfoVer();
            BeanUtil.copyProperties(vehicleDataInfo, vehicleDataInfoVer);
            vehicleDataInfoVer.setVerState(VER_STATE_HIS);
            vehicleDataInfoVer.setCreateTime(LocalDateTime.now());
            vehicleDataInfoVer.setIsAuthSucc(3);
            vehicleDataInfoVerMapper.insert(vehicleDataInfoVer);
            dissolveCooperationVehicle(relId, vehicleDataInfoVer.getId(), accessToken);
        }


    }

    @Override
    public void dissolveCooperationVehicle(long relId, long vehicleDataInfoVerHisId, String accessToken) throws
            BusinessException {
        TenantVehicleRel tenantVehicleRel = tenantVehicleRelMapper.selectById(relId);
        if (null == tenantVehicleRel) {
            return;
        }
        VehicleDataInfo vehicleDataInfo = baseMapper.selectById(tenantVehicleRel.getVehicleCode());
        if (null == vehicleDataInfo) {
            return;
        }
        List<TenantVehicleCostRel> tenantVehicleCostRelList = tenantVehicleCostRelMapper.getTenantVehicleCostRelList(tenantVehicleRel.getId(), tenantVehicleRel.getTenantId());
        if (null != tenantVehicleCostRelList && !tenantVehicleCostRelList.isEmpty()) {
            for (int i = 0; i < tenantVehicleCostRelList.size(); i++) {
                TenantVehicleCostRel vehicleCostRel = tenantVehicleCostRelList.get(i);
                TenantVehicleCostRelVer vehicleCostRelVer = new TenantVehicleCostRelVer();
                BeanUtil.copyProperties(vehicleCostRel, vehicleCostRelVer);
                vehicleCostRelVer.setVerState(VER_STATE_HIS);
                vehicleCostRelVer.setCreateTime(LocalDateTime.now());
                vehicleCostRelVer.setIsAuthSucc(3);
                vehicleCostRelVer.setId(vehicleDataInfoVerHisId);
                tenantVehicleCostRelVerMapper.insert(vehicleCostRelVer);
                //???????????????????????????0
                updateVehicleVerAllVerState(SysStaticDataEnum.VER_STATE.VER_STATE_N, vehicleCostRel.getId(), vehicleCostRel.getTenantId());
                tenantVehicleCostRelMapper.deleteById(tenantVehicleCostRelList.get(i).getId());
            }
        }

        //??????????????????
        List<TenantVehicleCertRel> tenantVehicleRelList = tenantVehicleCertRelMapper.getTenantVehicleRelList(tenantVehicleRel.getId(), tenantVehicleRel.getTenantId());
        if (null != tenantVehicleRelList && !tenantVehicleRelList.isEmpty()) {
            for (int i = 0; i < tenantVehicleRelList.size(); i++) {
                TenantVehicleCertRel vehicleCertRel = tenantVehicleRelList.get(i);
                TenantVehicleCertRelVer vehicleCertRelVer = new TenantVehicleCertRelVer();
                BeanUtil.copyProperties(vehicleCertRel, vehicleCertRelVer);
                vehicleCertRelVer.setVerState(VER_STATE_HIS);
                vehicleCertRelVer.setCreateTime(LocalDateTime.now());
                vehicleCertRelVer.setIsAuthSucc(3);
                vehicleCertRelVer.setId(vehicleDataInfoVerHisId);
                tenantVehicleCertRelVerMapper.insert(vehicleCertRelVer);
                //???????????????????????????0
                updateVehicleVerAllVerState(SysStaticDataEnum.VER_STATE.VER_STATE_N, vehicleCertRel.getId(), vehicleCertRel.getTenantId());
//                tenantVehicleCertRelVerMapper.deleteById(vehicleCertRel.getId());
                tenantVehicleCertRelVerMapper.deleteById(vehicleCertRel.getId());
            }
        }

        TenantVehicleRelVer tenantVehicleRelVer = new TenantVehicleRelVer();
        BeanUtil.copyProperties(tenantVehicleRel, tenantVehicleRelVer);
        tenantVehicleRelVer.setVehicleHisId(vehicleDataInfoVerHisId);
        tenantVehicleRelVer.setVerState(VER_STATE_HIS);
        tenantVehicleRelVer.setIsAuthSucc(3);
        tenantVehicleRelVer.setCreateTime(LocalDateTime.now());
        tenantVehicleRelVer.setRelId(tenantVehicleRel.getId());
        tenantVehicleRelVerMapper.insert(tenantVehicleRelVer);
        tenantVehicleRelMapper.deleteById(relId);
//        String msg = "??????-" + tenantVehicleRel.getPlateNumber() + "-?????????-" + tenantVehicleRel.getTenantId() + "-??????" +
//                getSysStaticData("VEHICLE_CLASS", tenantVehicleRel.getVehicleClass() + "") + "??????";
        String msg = "??????-" + tenantVehicleRel.getPlateNumber() + "-?????????-" + tenantVehicleRel.getTenantId() + "-??????" +
                SysStaticDataRedisUtils.getSysStaticDataByCodeValue2String(redisUtil, "VEHICLE_CLASS", tenantVehicleRel.getVehicleClass().toString()) + "??????";

        //?????????
        saveSysOperLog(SysOperLogConst.BusiCode.Vehicle, tenantVehicleRel.getId(), SysOperLogConst.OperType.Del, msg, accessToken);


    }

    private SysStaticData getSysStaticData(String codeType, String codeValue) {
        List<SysStaticData> staticDataList = (List<SysStaticData>) redisUtil.get(com.youming.youche.conts.SysStaticDataEnum.SYS_STATIC_DATA_NAME.concat(codeType));
        for (SysStaticData sysStaticData : staticDataList) {
            if (codeValue.equals(sysStaticData.getCodeValue())) {
                return sysStaticData;
            }
        }
        return new SysStaticData();
    }

    private SysStaticData getSysStaticDataValue(String codeType, String codeValue) {
        List<SysStaticData> staticDataList = (List<SysStaticData>) redisUtil.get(com.youming.youche.conts.SysStaticDataEnum.SYS_STATIC_DATA_NAME.concat(codeType));
        long codeId = Long.valueOf(codeValue);
        for (SysStaticData sysStaticData : staticDataList) {
            if (sysStaticData.getCodeId() == codeId) {
                return sysStaticData;
            }
        }
        return null;
    }


    private void saveSysOperLog(SysOperLogConst.BusiCode busiCode, Long busiId,
                                SysOperLogConst.OperType operType, String operComment, LoginInfo loginInfo) {
        SysOperLog operLog = new SysOperLog();
        operLog.setOpName(loginInfo.getName());
        operLog.setOpId(loginInfo.getId());
        operLog.setBusiCode(busiCode.getCode());
        operLog.setBusiName(busiCode.getName());
        operLog.setBusiId(busiId);
        operLog.setOperType(operType.getCode());
        operLog.setOperTypeName(operType.getName());
        operLog.setOperComment(operComment);
        operLog.setTenantId(loginInfo.getTenantId());
        sysOperLogService.save(operLog);
    }

    private void saveSysOperLog(SysOperLogConst.BusiCode busiCode, Long busiId,
                                SysOperLogConst.OperType operType, String operComment, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        saveSysOperLog(busiCode, busiId, operType, operComment, loginInfo);
    }


    @Override
    public void addPtTenantVehicleRel(Long vehicleCode) {
        //???????????????????????????
        VehicleDataInfo vehicleDataInfo = baseMapper.selectById(vehicleCode);
        if (vehicleDataInfo == null) {
            return;
        }
        //???????????????????????????
        List<TenantVehicleRel> zyVehicleByVehicleCode = tenantVehicleRelMapper.getZYVehicleByVehicleCode(vehicleCode, 1);
        if (zyVehicleByVehicleCode != null && zyVehicleByVehicleCode.size() > 0 && StringUtils.isNotBlank(zyVehicleByVehicleCode.get(0).getPlateNumber())) {
            return;
        }
        //?????????C?????????????????????
        List<TenantVehicleRel> tenantVehicleRelList = tenantVehicleRelMapper.getTenantVehicleRel(vehicleCode);
        if (tenantVehicleRelList != null && tenantVehicleRelList.size() > 0) {
            return;
        }

        Long driverUserId = null;
        List<TenantVehicleRel> vehicleRelList = tenantVehicleRelMapper.getTenantVehicleRelList(vehicleCode);
        if (vehicleRelList != null && vehicleRelList.size() > 0) {
            if (vehicleRelList.get(0) != null) {
                driverUserId = vehicleRelList.get(0).getDriverUserId();
            }
        }

        TenantVehicleRel ptVehicleRel = new TenantVehicleRel();
        ptVehicleRel.setTenantId(SysStaticDataEnum.PT_TENANT_ID);
        ptVehicleRel.setVehicleCode(vehicleCode);
        ptVehicleRel.setDriverUserId(driverUserId);
        ptVehicleRel.setPlateNumber(vehicleDataInfo.getPlateNumber());
        ptVehicleRel.setCreateTime(LocalDateTime.now());
        ptVehicleRel.setVehicleClass(VEHICLE_CLASS5);
        ptVehicleRel.setAuthState(SysStaticDataEnum.AUTH_STATE.AUTH_STATE2);
        ptVehicleRel.setIsAuth(SysStaticDataEnum.IS_AUTH.IS_AUTH0);
        ptVehicleRel.setShareFlg(SysStaticDataEnum.SHARE_FLG.YES);
        tenantVehicleRelMapper.insert(ptVehicleRel);
        TenantVehicleRelVer ptVehicleRelVer = new TenantVehicleRelVer();
        BeanUtil.copyProperties(ptVehicleRel, ptVehicleRelVer);
        ptVehicleRelVer.setRelId(ptVehicleRel.getId());
        //  TenantVehicleRel tenantVehicleRel = tenantVehicleRelMapper.selectById(ptVehicleRelVer.getId());
//        if (tenantVehicleRel != null) {
//            tenantVehicleRelVerMapper.updateById(ptVehicleRelVer);
//        } else {
//            tenantVehicleRelVerMapper.insert(ptVehicleRelVer);
//        }
        iTenantVehicleRelVerService.saveOrUpdate(ptVehicleRelVer);
    }

    private String dealVehicleObjectLine(List<VehicleObjectLineVo> vehicleObjectLineList, VehicleDataInfo
            vehicleDataInfo, VehicleDataInfoVer vehicleDataInfoVer, boolean unNeedCheck, boolean isApp, String accessToken) throws
            ParseException {
        String vehicleObjectLineVerHisIds = "";
        for (int i = 0; i < vehicleObjectLineList.size(); i++) {
            VehicleObjectLineVo vehicleOjbectLineVo = vehicleObjectLineList.get(i);
            Number sourceProvinceN = null;
            if (null != vehicleOjbectLineVo.getSourceProvince()) {
                sourceProvinceN = NumberFormat.getInstance().parse(vehicleOjbectLineVo.getSourceProvince().toString());
            }
            Number sourceRegionN = null;
            if (null != vehicleOjbectLineVo.getSourceRegion()) {
                sourceRegionN = NumberFormat.getInstance().parse(vehicleOjbectLineVo.getSourceRegion().toString());
            }
            Number sourceCountyN = null;
            if (null != vehicleOjbectLineVo.getSourceCounty()) {
                sourceCountyN = NumberFormat.getInstance().parse(vehicleOjbectLineVo.getSourceCounty().toString());
            }
            Number desProvinceN = null;
            if (null != vehicleOjbectLineVo.getDesProvince()) {
                desProvinceN = NumberFormat.getInstance().parse(vehicleOjbectLineVo.getDesProvince().toString());
            }
            Number desRegionN = null;
            if (null != vehicleOjbectLineVo.getDesRegion()) {
                desRegionN = NumberFormat.getInstance().parse(vehicleOjbectLineVo.getDesRegion().toString());
            }
            Number desCountyN = null;
            if (null != vehicleOjbectLineVo.getDesCounty()) {
                desCountyN = NumberFormat.getInstance().parse(vehicleOjbectLineVo.getDesCounty().toString());
            }
            Integer sourceProvince = null == sourceProvinceN ? null : sourceProvinceN.intValue();
            Integer sourceRegion = null == sourceRegionN ? null : sourceRegionN.intValue();
            Integer sourceCounty = null == sourceCountyN ? null : sourceCountyN.intValue();
            Integer desProvince = null == desProvinceN ? null : desProvinceN.intValue();
            Integer desRegion = null == desRegionN ? null : desRegionN.intValue();
            Integer desCounty = null == desCountyN ? null : desCountyN.intValue();
            Long carriagePrice = null;
            if (null != vehicleOjbectLineVo.getCarriagePrice()) {
                carriagePrice = vehicleOjbectLineVo.getCarriagePrice();
            }
//            if (StringUtils.isBlank(carriagePrice) || carriagePrice == "") {
//                carriagePrice = "0";
//            }
//            BigDecimal carriagePriceDouble = new BigDecimal(carriagePrice);
//            if (!isApp) {
//                //web??????????????????????????????100???APP?????????
//                carriagePriceDouble = new BigDecimal(carriagePrice).multiply(new BigDecimal(100));
//            }


            Number vehicleObjectLineIdN = null;
            if (vehicleOjbectLineVo.getId() != null) {
                vehicleObjectLineIdN = NumberFormat.getInstance().parse(vehicleOjbectLineVo.getId().toString());
            }
            Long vehicleObjectLineId = null == vehicleObjectLineIdN ? null : vehicleObjectLineIdN.longValue();

            VehicleObjectLine oldVehicleObjectLine = null;
            if (vehicleObjectLineId != null && vehicleObjectLineId > 0) {
                oldVehicleObjectLine = vehicleObjectLineMapper.selectById(vehicleObjectLineId);
            }

            VehicleObjectLineVer vehicleObjectLineVer = new VehicleObjectLineVer();

            if (oldVehicleObjectLine != null) {//??????????????????
                // id ??????????????????
                BeanUtil.copyProperties(oldVehicleObjectLine, vehicleObjectLineVer, new String[]{"id", "create_time", "update_time"});
                vehicleObjectLineVer.setCarriagePrice(carriagePrice != null ? carriagePrice : null); // ????????? ??????
                vehicleObjectLineVer.setVehicleCode(vehicleDataInfo.getId());
                vehicleObjectLineVer.setSourceProvince(sourceProvince);
                vehicleObjectLineVer.setSourceRegion(sourceRegion);
                vehicleObjectLineVer.setSourceCounty(sourceCounty);
                vehicleObjectLineVer.setDesProvince(desProvince);
                vehicleObjectLineVer.setTenantId(null);
                vehicleObjectLineVer.setDesRegion(desRegion);
                vehicleObjectLineVer.setDesCounty(desCounty);
                vehicleObjectLineVer.setPlateNumber(vehicleDataInfo.getPlateNumber());

//                vehicleObjectLineVer.setTenantId(null);
//                vehicleObjectLineVer.setCarriagePrice(carriagePriceDouble.longValue() > 0 ? carriagePriceDouble.longValue() : null)
                vehicleObjectLineVer.setVerState(VER_STATE_Y);
                vehicleObjectLineVer.setVehicleHisId(vehicleDataInfoVer.getId());
                vehicleObjectLineVer.setHisId(oldVehicleObjectLine.getId());
                if (unNeedCheck) {
                    //?????????????????????????????????????????????????????????
                    BeanUtil.copyProperties(vehicleObjectLineVer, oldVehicleObjectLine);
                    oldVehicleObjectLine.setOpId(loginUtils.get(accessToken).getUserInfoId());
                    oldVehicleObjectLine.setUpdateTime(LocalDateTime.now());
                    vehicleObjectLineMapper.updateById(oldVehicleObjectLine);
                    //??????????????????
                    vehicleObjectLineVer.setIsAuthSucc(3);

                }
                vehicleObjectLineVer.setCreateTime(LocalDateTime.now());
                //?????????????????????????????????????????????????????????????????????
//                vehicleObjectLineVerMapper.insert(vehicleObjectLineVer);
                iVehicleObjectLineVerService.saveOrUpdate(vehicleObjectLineVer);
            } else {//????????????

                //??????????????????????????????????????????3?????????????????????
                List<VehicleObjectLine> vehicleObjectLine = vehicleObjectLineMapper.getVehicleObjectLine(vehicleDataInfo.getId());

                if (vehicleObjectLine != null && vehicleObjectLine.size() < 3) {
                    VehicleObjectLine newVehicleObjectLine = new VehicleObjectLine();
                    newVehicleObjectLine.setVehicleCode(vehicleDataInfo.getId());
                    newVehicleObjectLine.setPlateNumber(vehicleDataInfo.getPlateNumber());
                    newVehicleObjectLine.setTenantId(null);

                    newVehicleObjectLine.setCreateDate(LocalDateTime.now());
                    newVehicleObjectLine.setOpId(loginUtils.get(accessToken).getUserInfoId());
                    vehicleObjectLineMapper.insert(newVehicleObjectLine);

                    newVehicleObjectLine.setSourceProvince(sourceProvince);
                    newVehicleObjectLine.setSourceRegion(sourceRegion);
                    newVehicleObjectLine.setSourceCounty(sourceCounty);
                    newVehicleObjectLine.setDesProvince(desProvince);
                    newVehicleObjectLine.setDesRegion(desRegion);
                    newVehicleObjectLine.setDesCounty(desCounty);
                    newVehicleObjectLine.setCarriagePrice(vehicleOjbectLineVo.getCarriagePrice());

                    BeanUtil.copyProperties(newVehicleObjectLine, vehicleObjectLineVer);
                    vehicleObjectLineVer.setVerState(VER_STATE_Y);
                    vehicleObjectLineVer.setVehicleHisId(vehicleDataInfoVer.getId());
                    vehicleObjectLineVer.setHisId(newVehicleObjectLine.getId());
                    vehicleObjectLineVer.setCarriagePrice(newVehicleObjectLine.getCarriagePrice()); // ????????? ??????
                    if (unNeedCheck) {//?????????????????????????????????????????????????????????
                        vehicleObjectLineVer.setIsAuthSucc(3);//??????????????????
                        vehicleObjectLineMapper.updateById(newVehicleObjectLine);
                    }
                    vehicleObjectLineVerService.saveOrUpdate(vehicleObjectLineVer);
                }

            }


            if (i == (vehicleObjectLineList.size() - 1)) {
                vehicleObjectLineVerHisIds = vehicleObjectLineVerHisIds + vehicleObjectLineVer.getHisId();
            } else {
                vehicleObjectLineVerHisIds = vehicleObjectLineVerHisIds + vehicleObjectLineVer.getHisId() + ",";
            }

        }

        return vehicleObjectLineVerHisIds;
    }

    private void doSaveVehicleOrderPositionInfo(Long vehicleCode, String plateNumber) {

        if (vehicleCode < 0 || StringUtils.isBlank(plateNumber)) {
            return;
        }
        Integer integer = vehicleOrderPositionInfoMapper.countOrderPositionInfo(vehicleCode);

        if (integer > 0) {
            return;
        }

        VehicleOrderPositionInfo vehicleOrderPositionInfo = new VehicleOrderPositionInfo();
        vehicleOrderPositionInfo.setVehicleCode(vehicleCode);
        vehicleOrderPositionInfo.setPlateNumber(plateNumber);
        vehicleOrderPositionInfo.setShareFlg(YES);
        vehicleOrderPositionInfoMapper.insert(vehicleOrderPositionInfo);
    }

    private void updateVehicleVerAllVerState(Integer destVerState, Long vehicleCode, Long tenantId) {
        if (destVerState < 0) {
            throw new BusinessException("destVerState???????????????");
        }
        Integer i = vehicleDataInfoVerMapper.updVehicleDataInfoVerState(destVerState, vehicleCode);

        Integer integer = vehicleObjectLineVerMapper.updVehicleObjectLineVer(destVerState, vehicleCode);

        Integer integer1 = vehicleLineRelVerMapper.updtVehicleLineRelVerState(destVerState, vehicleCode);

        Integer integer2 = tenantVehicleCostRelVerMapper.updTenantVehicleCostRelVer(destVerState, vehicleCode, tenantId);

        Integer integer3 = tenantVehicleRelVerMapper.updTenantVehicleRelVer(destVerState, vehicleCode, tenantId);

        Integer integer4 = tenantVehicleCertRelVerMapper.updTenantVehicleCertRelVer(destVerState, vehicleCode, tenantId);

    }

    private List<VehicleObjectLineVo> getVehicleObjectLine(Long vehicleCode) {
        List<VehicleObjectLine> vehicleObjectLineList = vehicleObjectLineMapper.getVehicleObjectLine(vehicleCode);
        List<VehicleObjectLineVo> lineVos = Lists.newArrayList();

        if (vehicleObjectLineList != null && vehicleObjectLineList.size() > 0) {

            List<SysStaticData> sysProvince = (List<SysStaticData>) redisUtil.get(com.youming.youche.conts.SysStaticDataEnum.SYS_STATIC_DATA_NAME.concat("SYS_PROVINCE"));
            List<SysStaticData> sysCity = (List<SysStaticData>) redisUtil.get(com.youming.youche.conts.SysStaticDataEnum.SYS_STATIC_DATA_NAME.concat("SYS_CITY"));
            List<SysStaticData> sysDistrict = (List<SysStaticData>) redisUtil.get(com.youming.youche.conts.SysStaticDataEnum.SYS_STATIC_DATA_NAME.concat("SYS_DISTRICT"));

            for (VehicleObjectLine vehicleObjectLine : vehicleObjectLineList) {
                VehicleObjectLineVo vehicleOjbectLineVo = getVehicleOjbectLineVo(sysProvince, sysCity, sysDistrict, vehicleObjectLine);
                lineVos.add(vehicleOjbectLineVo);
            }
        }
        return lineVos;
    }

    /**
     * ??????????????????  ??????id ????????????????????????
     *
     * @param sysProvince
     * @param sysCity
     * @param sysDistrict
     * @param vehicleObjectLine
     * @return com.youming.youche.record.vo.VehicleOjbectLineVo
     * @throws
     * @author terry
     * @date 2022/3/1 23:27
     */
    private VehicleObjectLineVo getVehicleOjbectLineVo(List<SysStaticData> sysProvince, List<SysStaticData> sysCity, List<SysStaticData> sysDistrict, VehicleObjectLine vehicleObjectLine) {
        VehicleObjectLineVo vehicleOjbectLineVo = new VehicleObjectLineVo();
        BeanUtil.copyProperties(vehicleObjectLine, vehicleOjbectLineVo);
        if (vehicleObjectLine.getSourceProvince() != null && vehicleObjectLine.getSourceProvince() > 0) {
            vehicleOjbectLineVo.setSourceProvinceName(getCityNames(sysProvince, vehicleObjectLine.getSourceProvince()));
        }
        if (vehicleObjectLine.getSourceRegion() != null && vehicleObjectLine.getSourceRegion() > 0) {
            vehicleOjbectLineVo.setSourceRegionName(getCityNames(sysCity, vehicleObjectLine.getSourceRegion()));
        }
        if (vehicleObjectLine.getSourceCounty() != null && vehicleObjectLine.getSourceCounty() > 0) {
            vehicleOjbectLineVo.setSourceCountyName(getCityNames(sysDistrict, vehicleObjectLine.getSourceCounty()));
        }
        if (vehicleObjectLine.getDesProvince() != null && vehicleObjectLine.getDesProvince() > 0) {
            vehicleOjbectLineVo.setDesProvinceName(getCityNames(sysProvince, vehicleObjectLine.getDesProvince()));
        }
        if (vehicleObjectLine.getDesRegion() != null && vehicleObjectLine.getDesRegion() > 0) {
            vehicleOjbectLineVo.setDesRegionName(getCityNames(sysCity, vehicleObjectLine.getDesRegion()));
        }
        if (vehicleObjectLine.getDesCounty() != null && vehicleObjectLine.getDesCounty() > 0) {
            vehicleOjbectLineVo.setDesCountyName(getCityNames(sysDistrict, vehicleObjectLine.getDesCounty()));
        }
        if (vehicleObjectLine.getCarriagePrice() != null && vehicleObjectLine.getCarriagePrice() != -1) {

            vehicleObjectLine.setCarriagePrice(vehicleObjectLine.getCarriagePrice());
            //??????
//                        Long carriagePrice = vehicleObjectLine.getCarriagePrice();
//                        vehicleOjbectLineVo.setCarriagePrice(Long.valueOf(CommonUtil.divide(carriagePrice)));
        }
        return vehicleOjbectLineVo;
    }

    /**
     * ??????????????????  ?????????????????????
     *
     * @param sysProvince
     * @param code
     * @return java.lang.String
     * @throws
     * @author terry
     * @date 2022/3/1 23:22
     */
    private String getCityNames(List<SysStaticData> sysProvince, Integer code) {
        long codeId = Long.valueOf(code);
        for (SysStaticData sysStaticData : sysProvince) {
            if (sysStaticData.getCodeId() == codeId) {
                return sysStaticData.getCodeName();
            }
        }
        return "";
    }

    @Deprecated
    private List<Map> getVehicleObjectLineMap(Long vehicleCode) {
        List<VehicleObjectLine> vehicleObjectLineList = vehicleObjectLineMapper.getVehicleObjectLine(vehicleCode);

        List<Map> backList = Lists.newArrayList();
        if (vehicleObjectLineList != null && vehicleObjectLineList.size() > 0) {
            for (VehicleObjectLine vehicleObjectLine : vehicleObjectLineList) {
                Map<String, Object> map = new HashMap<>();
                if (vehicleObjectLine.getSourceProvince() != null && vehicleObjectLine.getSourceProvince() > 0) {
                    try {
                        map.put("sourceProvinceName", provinceMapper.selectById(vehicleObjectLine.getSourceProvince()).getName());
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                }
                if (vehicleObjectLine.getSourceRegion() != null && vehicleObjectLine.getSourceRegion() > 0) {
                    try {
                        map.put("sourceRegionName", cityMapper.selectById(vehicleObjectLine.getSourceRegion()).getName());
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                }
                if (vehicleObjectLine.getSourceCounty() != null && vehicleObjectLine.getSourceCounty() > 0) {
                    try {
                        map.put("sourceCountyName", districtMapper.selectById(vehicleObjectLine.getSourceCounty()).getName());
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                }
                if (vehicleObjectLine.getDesProvince() != null && vehicleObjectLine.getDesProvince() > 0) {
                    try {
                        map.put("desProvinceName", provinceMapper.selectById(vehicleObjectLine.getSourceProvince()).getName());
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                }
                if (vehicleObjectLine.getDesRegion() != null && vehicleObjectLine.getDesRegion() > 0) {
                    try {
                        map.put("desRegionName", cityMapper.selectById(vehicleObjectLine.getDesRegion()).getName());
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                }
                if (vehicleObjectLine.getDesCounty() != null && vehicleObjectLine.getDesCounty() > 0) {
                    try {
                        map.put("desCountyName", districtMapper.selectById(vehicleObjectLine.getDesCounty()).getName());
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                }
                if (vehicleObjectLine.getCarriagePrice() != null && vehicleObjectLine.getCarriagePrice() != -1) {
                    try {
                        map.put("carriagePrice", CommonUtil.divide(vehicleObjectLine.getCarriagePrice()));
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                }
                if (map.size() > 0) {
                    backList.add(map);
                }
            }
        }

        return backList;
    }

    private TenantUserRel getTenantUserRel(Long userId, Long tenantId) {
        List<TenantUserRel> tenantUserRelList = tenantUserRelMapper.getTenantUserRels(userId, tenantId);
        if (tenantUserRelList == null || tenantUserRelList.isEmpty()) {
            return null;
        }

        if (tenantUserRelList.size() > 1) {
            throw new BusinessException("?????????????????????????????????????????????");
        }
        return tenantUserRelList.get(0);
    }

    @Override
    public VehicleDataInfo getVehicleDataInfo(String plateNumber) {
        QueryWrapper<VehicleDataInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("PLATE_NUMBER", plateNumber);
        List<VehicleDataInfo> list = baseMapper.selectList(queryWrapper);
        if (list != null && list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

    @Override
    public VehicleDataInfo getVehicleData(Long vehicleCode, Long tenantId) {
        LambdaQueryWrapper<VehicleDataInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(VehicleDataInfo::getId,vehicleCode)
                .eq(VehicleDataInfo::getTenantId,tenantId);
        return   baseMapper.selectOne(wrapper);
    }

    @Override
    public VehicleDataInfo getVehicle(String plateNumber, Long tenantId) {
        QueryWrapper<VehicleDataInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("plate_number", plateNumber);
        queryWrapper.eq("tenant_id", tenantId).orderByDesc("id");
        List<VehicleDataInfo> list = baseMapper.selectList(queryWrapper);
        if (list != null && list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

    @Override
    public Page<VehicleDataInfoiVo> getVehicleDataInfoPlateNumber(Integer pageNum, Integer pageSize, String plateNumber) {
        Page<VehicleDataInfoiVo> page = new Page<>(pageNum, pageSize);
        Page<VehicleDataInfoiVo> vehicleDataInfoPlateNumber = vehicleDataInfoMapper.getVehicleDataInfoPlateNumber(page, plateNumber);
        List<VehicleDataInfoiVo> records = vehicleDataInfoPlateNumber.getRecords();
        if (records != null && records.size() > 0) {
            for (VehicleDataInfoiVo record : records) {
                if (record.getVehicleClass() != null) {
                    String codeName = readisUtil.getSysStaticData("VEHICLE_CLASS", record.getVehicleClass().toString()).getCodeName();
                    record.setVehicleClassName(codeName);
                }
            }
        }

        return vehicleDataInfoPlateNumber;
    }

    @Override
    public VehicleDataInfo getVehicleDataInfo(Long vehicleCode) {
        return this.getById(vehicleCode);
    }

    @Transactional
    @Override
    public Boolean updateVehicleDataInfo(Short flag, Long vid,String plateNumber, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        UpdateWrapper<VehicleDataInfo> lambda = new UpdateWrapper<VehicleDataInfo>();
        lambda.set("idle", flag).eq("id", vid);
        update(lambda);
        boolean flags;
        VehicleIdle vehicleIdle = new VehicleIdle();
        vehicleIdle.setTenantId(loginInfo.getTenantId());
        vehicleIdle.setFlag(0);
        //??????????????????????????????
        List<VehicleIdle> vehicleIdles = vehicleIdleService.queryVehicleIdle(vid);
        if (flag == 0 && vehicleIdles != null && vehicleIdles.size() > 0) {
            vehicleIdle = vehicleIdles.get(0);
            vehicleIdle.setUpdateTime(LocalDateTime.now());
        } else {
            if (vehicleIdles != null && vehicleIdles.size() > 0) {
                vehicleIdle = vehicleIdles.get(0);
                vehicleIdle.setCreateTime(LocalDateTime.now());
                vehicleIdle.setUpdateTime(null);
            }else{
                vehicleIdle.setPlateNumber(plateNumber);
                vehicleIdle.setCreateTime(LocalDateTime.now());
                vehicleIdle.setUpdateTime(null);
                vehicleIdle.setVid(vid);
            }
        }
        flags = vehicleIdleService.saveOrUpdate(vehicleIdle);
        return flags;
    }


    @Override
    public String doSaveQuickVehicle(long tenantId, String plateNumber, long driverUserId,
                                     int vehicleStatus, String vehicleLength, int licenceType) throws Exception {

        if (StringUtils.isBlank(plateNumber) || !isCarNo(plateNumber)) {
            return "????????????????????????";
        }
        if (driverUserId < 0) {
            return "???????????????????????????";
        }
        if (licenceType < 0) {
            return "???????????????????????????";
        }
        VehicleDataInfo saveVehicleData = new VehicleDataInfo();
        saveVehicleData.setPlateNumber(plateNumber);
        saveVehicleData.setDriverUserId(driverUserId);
        saveVehicleData.setTenantId(null);
        java.util.Date date = new java.util.Date();
        Instant instant = date.toInstant();
        ZoneId zone = ZoneId.systemDefault();
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, zone);
        saveVehicleData.setCreateDate(localDateTime);
        saveVehicleData.setLicenceType(licenceType);
        saveVehicleData.setVehicleStatus(vehicleStatus);
        saveVehicleData.setVehicleLength(vehicleLength);
        saveVehicleData.setAuthState(AUTH_STATE2);
        saveVehicleData.setIsAuth(IS_AUTH0);
        saveVehicleData.setQuickFlag(SysStaticDataEnum.QUICK_FLAG.IS_QUICK);
        if (saveVehicleData.getLicenceType() == SysStaticDataEnum.LICENCE_TYPE.TT) {
            saveVehicleData.setVehicleStatus(null);
            saveVehicleData.setVehicleLength(null);
            saveVehicleData.setVehicleLoad(null);
            saveVehicleData.setLightGoodsSquare(null);
        }
        this.save(saveVehicleData);
        //???????????????????????????
        addPtTenantVehicleRelWithDriver(saveVehicleData.getId(), saveVehicleData.getDriverUserId());

        java.util.Date date1 = new java.util.Date();
        Instant instant1 = date1.toInstant();
        ZoneId zone1 = ZoneId.systemDefault();
        LocalDateTime localDateTime1 = LocalDateTime.ofInstant(instant1, zone1);
        saveVehicleData.setCreateDate(localDateTime1);
        VehicleDataInfoVer saveVehicleDataVer = new VehicleDataInfoVer();
        BeanUtil.copyProperties(saveVehicleData, saveVehicleDataVer);
        saveVehicleDataVer.setPlateNumber(plateNumber);
        saveVehicleDataVer.setDriverUserId(driverUserId);
        saveVehicleDataVer.setLicenceType(saveVehicleData.getLicenceType());
        saveVehicleDataVer.setVehicleStatus(saveVehicleData.getVehicleStatus());
        saveVehicleDataVer.setVehicleLength(saveVehicleData.getVehicleLength());
        saveVehicleDataVer.setLightGoodsSquare(saveVehicleData.getLightGoodsSquare());
        saveVehicleDataVer.setVehicleCode(saveVehicleData.getId());
        saveVehicleDataVer.setVerState(SysStaticDataEnum.VER_STATE.VER_STATE_Y);
        saveVehicleDataVer.setCreateTime(localDateTime1);
        saveVehicleDataVer.setIsAuthSucc(SysStaticDataEnum.IS_AUTH_SUCC.YES_3);
        iVehicleDataInfoVerService.save(saveVehicleDataVer);


        TenantVehicleRel tenantVehicleRel = new TenantVehicleRel();
        TenantVehicleRelVer tenantVehicleRelVer = new TenantVehicleRelVer();
        tenantVehicleRel.setVehicleClass(VEHICLE_CLASS5);
        tenantVehicleRel.setTenantId(tenantId);
        tenantVehicleRel.setAuthState(AUTH_STATE2);
        tenantVehicleRel.setIsAuth(IS_AUTH0);
        tenantVehicleRel.setShareFlg(SysStaticDataEnum.SHARE_FLG.NO);
        tenantVehicleRel.setVehicleCode(saveVehicleData.getId());
        tenantVehicleRel.setDriverUserId(driverUserId);
        tenantVehicleRel.setCreateTime(localDateTime1);
        tenantVehicleRel.setPlateNumber(plateNumber);
        Long id = iTenantVehicleRelService.saveById(tenantVehicleRel);

        BeanUtil.copyProperties(tenantVehicleRel, tenantVehicleRelVer);
        tenantVehicleRelVer.setVehicleClass(VEHICLE_CLASS5);
        tenantVehicleRelVer.setTenantId(tenantId);
        tenantVehicleRelVer.setShareFlg(SysStaticDataEnum.SHARE_FLG.NO);
        tenantVehicleRelVer.setVehicleCode(saveVehicleData.getId());
        tenantVehicleRelVer.setDriverUserId(driverUserId);
        tenantVehicleRelVer.setCreateTime(localDateTime1);
        tenantVehicleRelVer.setPlateNumber(plateNumber);
        tenantVehicleRelVer.setVerState(SysStaticDataEnum.VER_STATE.VER_STATE_Y);
        tenantVehicleRelVer.setVehicleHisId(saveVehicleDataVer.getId());
        tenantVehicleRelVer.setIsAuthSucc(SysStaticDataEnum.IS_AUTH_SUCC.YES_3);
        tenantVehicleRelVer.setRelId(id);
        iTenantVehicleRelVerService.save(tenantVehicleRelVer);
        doUpdateVehicleCompleteness(saveVehicleData.getId(), tenantId);
        //iSysOperLogService.saveSysOperLog(SysOperLogConst.BusiCode.Vehicle, id, SysOperLogConst.OperType.Add, "??????" + saveVehicleData.getPlateNumber() + "??????????????????");
        return null;
    }


    public static boolean isCarNo(String carNo) {
        Pattern p = Pattern.compile("^[???????????????????????????????????????????????????????????????????????????????????????????????????A-Z]{1}[A-Z]{1}(?:(?![A-Z]{4})[A-Z0-9]){4}[A-Z0-9???????????????]{1}$");
        Matcher m = p.matcher(carNo);
        if (!m.matches()) {
            return false;
        }
        return true;
    }


//    public void addPtTenantVehicleRelWithDriver(long vehicleCode, Long driverUserId) throws Exception {
//
//        //???????????????????????????
//        VehicleDataInfo vehicleDataInfo = this.getById(vehicleCode);
//        if (vehicleDataInfo == null) {
//            return;
//        }
//
//        //???????????????????????????
//        TenantVehicleRel zyVehicleRel = iTenantVehicleRelService.getZYVehicleByVehicleCode(vehicleCode);
//        if (zyVehicleRel != null && StringUtils.isNotBlank(zyVehicleRel.getPlateNumber())) {
//            return;
//        }
//
//        //?????????C?????????????????????
//        TenantVehicleRel vehicleRel = iTenantVehicleRelService.getTenantVehicleRel(vehicleCode, SysStaticDataEnum.PT_TENANT_ID);
//        if (vehicleRel != null && vehicleRel.getTenantId() != null && vehicleRel.getTenantId() == SysStaticDataEnum.PT_TENANT_ID) {
//            return;
//        }
//
//        TenantVehicleRel ptVehicleRel = new TenantVehicleRel();
//        ptVehicleRel.setTenantId(SysStaticDataEnum.PT_TENANT_ID);
//        ptVehicleRel.setVehicleCode(vehicleCode);
//        ptVehicleRel.setDriverUserId(driverUserId);
//        ptVehicleRel.setPlateNumber(vehicleDataInfo.getPlateNumber());
//        java.util.Date date = new java.util.Date();
//        Instant instant = date.toInstant();
//        ZoneId zone = ZoneId.systemDefault();
//        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, zone);
//        ptVehicleRel.setCreateTime(localDateTime);
//        ptVehicleRel.setVehicleClass(VEHICLE_CLASS5);
//        ptVehicleRel.setAuthState(AUTH_STATE2);
//        ptVehicleRel.setIsAuth(IS_AUTH0);
//        ptVehicleRel.setShareFlg(SysStaticDataEnum.SHARE_FLG.YES);
//        Long id = iTenantVehicleRelService.saveById(ptVehicleRel);
//        TenantVehicleRelVer ptVehicleRelVer = new TenantVehicleRelVer();
//        ptVehicleRelVer.setVehicleCode(vehicleCode);
//        ptVehicleRelVer.setRelId(id);
//        ptVehicleRelVer.setDriverUserId(driverUserId);
//        ptVehicleRelVer.setPlateNumber(ptVehicleRel.getPlateNumber());
//        ptVehicleRelVer.setCreateTime(localDateTime);
//        ptVehicleRelVer.setVehicleClass(VEHICLE_CLASS5);
//        ptVehicleRelVer.setShareFlg(SysStaticDataEnum.SHARE_FLG.YES);
//        iTenantVehicleRelVerService.saveOrUpdate(ptVehicleRelVer);
//    }


    /**
     * ?????????????????????
     *
     * @param vehicleCode
     * @throws Exception
     */
    @Override
    public void doUpdateVehicleCompleteness(long vehicleCode, Long tenantId) {

        StringBuffer updateSqlBuffer = new StringBuffer();

        VehicleDataInfo vehicleDataInfo = this.getById(vehicleCode);
        List<TenantVehicleRel> relList = iTenantVehicleRelService.getTenantVehicleRelList(vehicleCode, tenantId);
        TenantVehicleRel tenantVehicleRel = new TenantVehicleRel();
        String completeNessStr = "";
        if (relList != null && relList.size() > 0) {
            tenantVehicleRel = relList.get(0);
            if (StringUtils.isNotBlank(vehicleDataInfo.getPlateNumber())) {
                //????????????
                completeNessStr += "1";
            } else {
                completeNessStr += "0";
            }
            if (vehicleDataInfo.getVehicleStatus() != null && vehicleDataInfo.getVehicleStatus() > 0) {
                //????????????
                completeNessStr += "1";
            } else {
                completeNessStr += "0";
            }
            if (StringUtils.isNotBlank(vehicleDataInfo.getVehicleLoad())) {
                //????????????
                completeNessStr += "1";
            } else {
                completeNessStr += "0";
            }
            if (StringUtils.isNotBlank(vehicleDataInfo.getLightGoodsSquare())) {
                //????????????
                completeNessStr += "1";
            } else {
                completeNessStr += "0";
            }
            if (StringUtils.isNotBlank(vehicleDataInfo.getVinNo())) {
                //?????????
                completeNessStr += "1";
            } else {
                completeNessStr += "0";
            }
            if (StringUtils.isNotBlank(vehicleDataInfo.getEngineNo())) {
                //????????????
                completeNessStr += "1";
            } else {
                completeNessStr += "0";
            }

            if (StringUtils.isNotBlank(vehicleDataInfo.getBrandModel())) {
                //??????
                completeNessStr += "1";
            } else {
                completeNessStr += "0";
            }
            if (StringUtils.isNotBlank(vehicleDataInfo.getOperCerti())) {
                //????????????
                completeNessStr += "1";
            } else {
                completeNessStr += "0";
            }
            if (StringUtils.isNotBlank(vehicleDataInfo.getDrivingLicenseOwner())) {
                //?????????
                completeNessStr += "1";
            } else {
                completeNessStr += "0";
            }
            if (tenantVehicleRel.getLoadEmptyOilCost() != null && tenantVehicleRel.getLoadEmptyOilCost() >= 0) {
                //????????????
                completeNessStr += "1";
            } else {
                completeNessStr += "0";
            }
            if (vehicleDataInfo.getVehiclePicture() != null && vehicleDataInfo.getVehiclePicture() > 0) {
                //????????????
                completeNessStr += "1";
            } else {
                completeNessStr += "0";
            }
            if (vehicleDataInfo.getOperCertiId() != null && vehicleDataInfo.getOperCertiId() > 0) {
                //?????????
                completeNessStr += "1";
            } else {
                completeNessStr += "0";
            }
            if (StringUtils.isNotBlank(vehicleDataInfo.getDrivingLicense())) {
                //???????????????
                completeNessStr += "1";
            } else {
                completeNessStr += "0";
            }
            if (vehicleDataInfo.getAdriverLicenseCopy() != null && vehicleDataInfo.getAdriverLicenseCopy() > 0) {
                //???????????????
                completeNessStr += "1";
            } else {
                completeNessStr += "0";
            }
            if (vehicleDataInfo.getRentAgreementId() != null && vehicleDataInfo.getRentAgreementId() > 0) {
                //??????????????????
                completeNessStr += "1";
            } else {
                completeNessStr += "0";
            }
            if (StringUtils.isNotBlank(vehicleDataInfo.getVehicleLength())) {
                //??????
                completeNessStr += "1";
            } else {
                completeNessStr += "0";
            }
            if (vehicleDataInfo.getSpecialOperCertFileId() != null && vehicleDataInfo.getSpecialOperCertFileId() > 0) {
                //??????????????????
                completeNessStr += "1";
            } else {
                completeNessStr += "0";
            }
            if (tenantVehicleRel.getLoadFullOilCost() != null && tenantVehicleRel.getLoadFullOilCost() >= 0) {
                //????????????
                completeNessStr += "1";
            } else {
                completeNessStr += "0";
            }
        }


        vehicleDataInfo.setCompleteness(completeNessStr);

        this.updateById(vehicleDataInfo);
    }

    @Override
    public void doTransferOwnCar(Long driverUserId, Long origTenantId, Long destTenantId, String token) throws
            Exception {
        QueryWrapper<VehicleDataInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("driver_user_id", driverUserId);
        List<VehicleDataInfo> vehicleDataInfoList = baseMapper.selectList(queryWrapper);
        if (vehicleDataInfoList != null && !vehicleDataInfoList.isEmpty()) {
            for (VehicleDataInfo vehicleDataInfo : vehicleDataInfoList) {
                doTransferOwnCar(vehicleDataInfo.getId(), destTenantId, origTenantId, destTenantId, token);
            }
        }
    }


    public String doTransferOwnCar(Long vehicleCode, Long driverUserId, Long origTenantId, Long
            destTenantId, String token) throws Exception {

        LoginInfo loginInfo = iUserDataInfoRecordService.getLoginInfoByAccessToken(token);
        if (vehicleCode == null || vehicleCode < 0) {
            throw new BusinessException("??????ID?????????");
        }
        VehicleDataInfo vehicleDataInfo = getById(vehicleCode);
        if (vehicleDataInfo == null) {
            throw new BusinessException("?????????????????????");
        }
        QueryWrapper<TenantVehicleRel> tenantVehicleRelQueryWrapper = new QueryWrapper<>();
        tenantVehicleRelQueryWrapper.eq("vehicle_code", vehicleCode);
        List<TenantVehicleRel> list = iTenantVehicleRelService.list(tenantVehicleRelQueryWrapper);
        if (null != list && list.size() > 0) {
            //??????????????????????????????????????????
            VehicleDataInfoVer vehicleDataInfoVer = iVehicleDataInfoVerService.doAddVehicleDataInfoVerHis(vehicleCode);
            for (int i = 0; i < list.size(); i++) {
                TenantVehicleRel t = list.get(i);
                dissolveCooperationVehicle(t.getId(), vehicleDataInfoVer.getId(), token);
            }
        }
        baseMapper.doUpdateVehicleObjectLine(vehicleDataInfo.getId());//??????????????????
        //????????????
        baseMapper.doUpdateVehicleLineRelByVehicleCode(vehicleDataInfo.getId());
        baseMapper.doUpdateVehicleLineRelVerByVehicleCode(vehicleDataInfo.getId());
        //??????ETC
        iEtcMaintainService.untieEtc(origTenantId, vehicleDataInfo.getPlateNumber());
        //????????????
        vehicleDataInfo.setTenantId(null);
        vehicleDataInfo.setCopilotDriverId(null);
        //??????????????????
        vehicleDataInfo.setFollowDriverId(null);
        vehicleDataInfo.setOperCerti(null);//????????????
        vehicleDataInfo.setEtcCardNumber(null);//etc??????
        vehicleDataInfo.setEquipmentCode(null);//gps?????????
        vehicleDataInfo.setContactNumber(null);//????????????
        vehicleDataInfo.setFollowDriverId(null);//????????????id
        vehicleDataInfo.setCopilotDriverId(null);//?????????id
        vehicleDataInfo.setDrivingLicense(null);//???????????????id
        vehicleDataInfo.setAdriverLicenseCopy(null);//???????????????id
        vehicleDataInfo.setOperCertiId(null);//???????????????id
        vehicleDataInfo.setDrivingLicenseOwner(null);//?????????????????????
        vehicleDataInfo.setRentAgreementId(null);//??????????????????
        vehicleDataInfo.setRentAgreementUrl(null);//??????????????????URL
        updateById(vehicleDataInfo);
        VehicleDataInfoVer vehicleDataInfoVerNew = new VehicleDataInfoVer();
        BeanUtil.copyProperties(vehicleDataInfo, vehicleDataInfoVerNew);
        vehicleDataInfoVerNew.setDriverUserId(driverUserId);
        vehicleDataInfoVerNew.setVehicleCode(vehicleDataInfo.getId());
        vehicleDataInfoVerNew.setTenantId(null);
        vehicleDataInfoVerNew.setVerState(VER_STATE_N);
        vehicleDataInfoVerNew.setId(null);
        iVehicleDataInfoVerService.save(vehicleDataInfoVerNew);


        TenantVehicleRel tenantVehicleRelNew = new TenantVehicleRel();
        tenantVehicleRelNew.setVehicleCode(vehicleDataInfo.getId());
        tenantVehicleRelNew.setDriverUserId(driverUserId);
        tenantVehicleRelNew.setPlateNumber(vehicleDataInfo.getPlateNumber());
        tenantVehicleRelNew.setTenantId(destTenantId);
        tenantVehicleRelNew.setOpId(loginInfo.getId());
        tenantVehicleRelNew.setUserId(vehicleDataInfo.getUserId());
        tenantVehicleRelNew.setSourceTenantId(origTenantId);
        tenantVehicleRelNew.setVehicleClass(SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1);
        tenantVehicleRelNew.setVehiclePicture(vehicleDataInfo.getVehiclePicture() + "");
        tenantVehicleRelNew.setOperCerti(null);
        tenantVehicleRelNew.setDrivingLicense(null);
        //?????????????????????
        tenantVehicleRelNew.setShareFlg(SysStaticDataEnum.SHARE_FLG.NO);
        //????????????
        baseMapper.doDelVehicleOrderPositionInfo(vehicleDataInfo.getId());
        tenantVehicleRelNew.setAuthState(SysStaticDataEnum.AUTH_STATE.AUTH_STATE1);
        tenantVehicleRelNew.setIsAuth(SysStaticDataEnum.IS_AUTH.IS_AUTH1);
        iTenantVehicleRelService.save(tenantVehicleRelNew);


        TenantVehicleRelVer tenantVehicleRelVer = new TenantVehicleRelVer();
        BeanUtil.copyProperties(tenantVehicleRelNew, tenantVehicleRelVer);
        tenantVehicleRelVer.setRelId(tenantVehicleRelNew.getId());
        tenantVehicleRelVer.setVehicleClass(SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1);
        tenantVehicleRelVer.setVerState(VER_STATE_Y);
        iTenantVehicleRelVerService.save(tenantVehicleRelVer);
        //??????????????????
        Map iMap = new HashMap();
        iMap.put("svName", "vehicleTF");
        iMap.put("tenantVehicleRelId", tenantVehicleRelNew.getId());
        saveSysOperLog(SysOperLogConst.BusiCode.Vehicle, tenantVehicleRelVer.getId(), SysOperLogConst.OperType.Audit, tenantVehicleRelNew.getPlateNumber() + "?????????????????????!", loginInfo);
        boolean bool = iAuditService.startProcess(AUDIT_CODE_VEHICLE, tenantVehicleRelNew.getId(), SysOperLogConst.BusiCode.Vehicle, iMap, token);
        if (!bool) {
            throw new BusinessException("???????????????????????????");
        }
        return "Y";
    }


    /**
     * ????????????: ????????????
     *
     * @param busiCode     ????????????
     * @param busiId       ????????????id
     * @param desc         ????????????
     * @param chooseResult 1 ???????????? 2 ???????????????
     * @param instId       ??????????????????????????????????????????????????????????????????
     * @return
     */
    @Transactional
    @Override
    public Boolean sure(String busiCode, Long busiId, String desc, Integer chooseResult, Long instId, String
            accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        List<AuditNodeInst> auditNodeInstList = auditNodeInstService.queryAuditIngList(busiCode, loginInfo.getTenantId(), busiId);
        if (auditNodeInstList != null && auditNodeInstList.size() > 0) {
            if (auditNodeInstList.get(0) == null) {
                throw new BusinessException("???????????????????????????");
            }
            if (instId != null && instId > 0) {
                if (!instId.equals(auditNodeInstList.get(0).getId())) {
                    throw new BusinessException("????????????????????????????????????????????????????????????????????????");
                }
            }
            if (AuditConsts.RESULT.SUCCESS == chooseResult) {
                auditPass(auditNodeInstList.get(0).getId(), busiCode, busiId, desc, auditNodeInstList.get(0).getVersion(), auditNodeInstList.get(0).getRuleVersion(), null, loginInfo.getUserInfoId(), accessToken);
            } else {
                auditFail(auditNodeInstList.get(0).getId(), busiCode, busiId, desc, loginInfo);
            }
        }
        return true;
    }

    @Override
    public List<VehicleStaticData> getVehicleStatusWithLength(Boolean orderFlag) {
        List<SysStaticData> statusList = (List<SysStaticData>) redisUtil.get(com.youming.youche.conts.SysStaticDataEnum.SYS_STATIC_DATA_NAME.concat("VEHICLE_STATUS"));
//        List<SysStaticData> statusList = statusList = iSysStaticDataService.getSysStaticDataListByCodeName("VEHICLE_STATUS");
        List<VehicleStaticData> result = new ArrayList<>();
        if (orderFlag) {
            VehicleStaticData vehicleStaticData = new VehicleStaticData();
            vehicleStaticData.setCodeId(-1L);
            vehicleStaticData.setCodeType("VEHICLE_STATUS");
            vehicleStaticData.setCodeValue("0");
            vehicleStaticData.setId(0L);
            vehicleStaticData.setCodeName("??????");
            vehicleStaticData.setCodeDesc("??????");
            vehicleStaticData.setCodeTypeAlias("??????");
            result.add(vehicleStaticData);
        }
        if (CollectionUtils.isNotEmpty(statusList)) {
            statusList.forEach(sysStaticData -> {
                result.add(new VehicleStaticData(sysStaticData));
            });
            result.forEach(vehicleStaticData -> {
                try {
                    vehicleStaticData.setVehicleLengthList(getVehicleLenth(vehicleStaticData.getCodeId()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
        return result;
    }

    @Override
    public List<VehicleDataInfo> getDriverAllRelVehicleList(Long userId) {
        if (userId < 0) {
            throw new BusinessException("????????????????????????????????????");
        }
        List<VehicleDataInfo> list = baseMapper.getDriverAllRelVehicleList(userId);
        return list;
    }

    /**
     * ??????codeId??????????????????
     */
    private List<SysStaticData> getVehicleLenth(Long codeId) {
        List<SysStaticData> lengthList = (List<SysStaticData>) redisUtil.get(com.youming.youche.conts.SysStaticDataEnum.SYS_STATIC_DATA_NAME.concat("VEHICLE_LENGTH"));
        List<SysStaticData> result = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(lengthList)) {
            lengthList.forEach(sysStaticData -> {
                if (sysStaticData.getCodeId().equals(codeId)) {
                    result.add(sysStaticData);
                }
            });
        }
        return result;
    }

    public void auditFail(Long auditNodeInstId, String auditCode, Long busiId,
                          String desc, LoginInfo loginInfo) {
        if (StringUtils.isBlank(auditCode)) {
            throw new BusinessException("???????????????????????????");
        }

        if (busiId == null) {
            throw new BusinessException("????????????????????????ID??????");
        }
        AuditNodeInst auditNodeInst = auditNodeInstService.getById(auditNodeInstId);
        if (auditNodeInst != null && AuditConsts.RESULT.TO_AUDIT == auditNodeInst.getAuditResult()) {
            Boolean flag = false;
            List<Long> userFromAuditUser = auditUserService.selectIdByNodeIdAndTarGetObjType(loginInfo.getUserInfoId(), AuditConsts.TargetObjType.USER_TYPE);
            if (userFromAuditUser != null && userFromAuditUser.size() > 0) {
                flag = true;
            }
            if (flag) {
                Integer nodeNum = auditNodeInstService.getAuditingNodeNum(auditCode, busiId, 1);

                //??????????????????????????????????????????
                Map<String, Object> paramsMap = JsonHelper.parseJSON2Map(auditNodeInst.getParamsMap());
                paramsMap.put(AuditConsts.CallBackMapKey.NODE_NUM, nodeNum);

                AuditConfig auditConfig = auditConfigService.getById(auditNodeInst.getAuditId());
                auditNodeInst.setAuditManId(loginInfo.getUserInfoId());
                auditNodeInst.setRemark(desc);
                auditNodeInst.setStatus(AuditConsts.Status.FINISH);
                auditNodeInst.setAuditDate(LocalDateTime.now());
                auditNodeInst.setAuditResult(AuditConsts.RESULT.FAIL);
//                auditNodeInstMapper.updateById(auditNodeInst);
                auditNodeInstService.updateById(auditNodeInst);

                //??????????????????????????????????????????
                auditNodeInstService.updateAuditInstToFinish(auditNodeInst.getAuditId(), busiId);
                //??????????????????
                List<AuditNodeInst> auditNodeInsts = auditNodeInstService.queryAuditNodeInst(auditNodeInst.getAuditCode(), auditNodeInst.getBusiId(), auditNodeInst.getTenantId());
                if (auditNodeInsts != null && auditNodeInsts.size() > 0) {
                    for (AuditNodeInst nodeInst : auditNodeInsts) {
                        auditNodeInstService.removeById(nodeInst);
                    }
                }

                try {
                    auditCallback(busiId, AuditConsts.RESULT.FAIL, desc, paramsMap, auditConfig.getCallback());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //String auditCodeName = isysStaticDataService.getSysStaticDataCodeName("AUDIT_CODE",auditConfig.getAuditCode()).getCodeName();
            } else {
                throw new BusinessException("??????????????????????????????");
            }
        } else {
            throw new BusinessException("??????????????????????????????????????????????????????");
        }
    }

    /**
     * ????????????: ????????????
     * 1 ??????????????????????????????????????? ?????????
     * 2 ?????????????????????????????????????????????????????????
     * 3 ??????????????????????????????
     * 4 ?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
     * 5 ?????????????????????????????????????????????????????????????????????
     */
    private boolean auditPass(Long auditNodeInstId, String auditCode, Long busiId, String desc, Integer
            version, Integer ruleVersion, Integer type, Long userId, String accessToken) {
        LoginInfo sysUser = loginUtils.get(accessToken);
        if (StringUtils.isBlank(auditCode)) {
            throw new BusinessException("???????????????????????????");
        }

        if (busiId == null) {
            throw new BusinessException("????????????????????????ID??????");
        }
        AuditNodeInst auditNodeInst = auditNodeInstService.getById(auditNodeInstId);
        if (auditNodeInst != null && AuditConsts.RESULT.TO_AUDIT == auditNodeInst.getAuditResult()) {
            List<AuditUser> auditUserList = auditUserService.getAuditUserList(userId, AuditConsts.TargetObjType.USER_TYPE, auditNodeInstId);
            if (type != null || (auditUserList != null && auditUserList.size() > 0)) {

                Map<String, Object> map = JsonHelper.parseJSON2Map(auditNodeInst.getParamsMap());
                Integer nodeNum = auditNodeInstService.getAuditingNodeNum(auditCode, busiId, AuditConsts.Status.AUDITING);
                map.put(AuditConsts.CallBackMapKey.NODE_NUM, nodeNum);

                AuditConfig auditConfig = auditConfigService.getById(auditNodeInst.getAuditId());
                boolean isNext = false;
                boolean isVer = false;
                Integer i = auditNodeConfigService.countId(version, auditNodeInst.getNodeId());
                if (i != null && i > 0) {
                    isVer = false;
                    List<AuditNodeConfig> nextAuditNodeConfigList = auditNodeConfigService.getNextAuditNodeConfigList(auditNodeInst.getNodeId());
                    AuditNodeConfig nextAuditNode = null;
                    if (nextAuditNodeConfigList != null && nextAuditNodeConfigList.size() > 0) {
                        throw new BusinessException("????????????????????????????????????????????????????????????????????????[" + auditNodeInst.getNodeId() + "]");
                    } else if (nextAuditNodeConfigList != null && nextAuditNodeConfigList.size() == 1) {
                        nextAuditNode = nextAuditNodeConfigList.get(0);
                    }
                    if (nextAuditNode != null) {
                        isNext = successNextAuditNode(auditNodeInst, auditConfig, desc, accessToken, map, busiId, nextAuditNode.getId(), nextAuditNode.getNodeName(), ruleVersion);
                    } else {
                        successAuditNodeInst(auditNodeInst, desc, busiId, map, auditConfig, type, sysUser);
                    }

                } else {
                    isVer = true;
                    AuditNodeConfigVer auditNodeConfigVer = auditNodeConfigVerService.getAuditNodeConfigVerIsTrue(version, auditNodeInst.getNodeId(), sysUser.getTenantId());
                    if (auditNodeConfigVer != null) {
                        isNext = successNextAuditNode(auditNodeInst, auditConfig, desc, accessToken, map, busiId, auditNodeConfigVer.getId(), auditNodeConfigVer.getNodeName(), ruleVersion);
                    } else {
                        //????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
                        successAuditNodeInst(auditNodeInst, desc, busiId, map, auditConfig, type, sysUser);
                    }
                }
                if (isNext) {
                    Long parentNodeId = null;
                    if (isVer) {
                        AuditNodeConfigVer auditNodeConfigVer = auditNodeConfigVerService.getAuditNodeConfigVerIsFalse(version, auditNodeInst.getNodeId(), sysUser.getTenantId());

                        parentNodeId = auditNodeConfigVer.getParentNodeId();
                    } else {
                        AuditNodeConfig auditNodeConfig = auditNodeConfigService.getById(auditNodeInst.getNodeId());
                        parentNodeId = auditNodeConfig.getParentNodeId();
                    }
                    if (parentNodeId == -1L) {
                        try {
                            Class cls = Class.forName(auditConfig.getCallback());
                            Method m = cls.getDeclaredMethod("auditingCallBack", new Class[]{Long.class});
                            m.invoke(cls.newInstance(), busiId);
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        } catch (NoSuchMethodException e) {
                            e.printStackTrace();
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        } catch (InstantiationException e) {
                            e.printStackTrace();
                        }
                    }
                    return true;
                }
            } else {
                throw new BusinessException("??????????????????????????????");
            }
        } else {
            throw new BusinessException("??????????????????????????????????????????????????????");
        }
        return false;
    }

    @Override
    public VehicleDataInfoVo getShareVehicle(Long vehicleCode, String accessToken) {
        try {
            LoginInfo loginInfo = loginUtils.get(accessToken);
            //????????????????????????
            VehicleDataInfoVo vehicleDataInfoVo = baseMapper.getShareVehicleDataInfo(vehicleCode, loginInfo.getTenantId());

            Integer vehicleClass = null;

            if (vehicleDataInfoVo != null) {
                if (null == vehicleDataInfoVo.getVehicleClass() || vehicleDataInfoVo.getVehicleClass() < 0) {
                    vehicleClass = VEHICLE_CLASS5;
                }
                if (vehicleDataInfoVo.getVehicleClass() != null) {
                    SysStaticData sysStaticData = readisUtil.getSysStaticData("VEHICLE_CLASS", String.valueOf(vehicleDataInfoVo.getVehicleClass()));
                    if (sysStaticData != null && sysStaticData.getCodeName() != null) {
                        vehicleDataInfoVo.setVehicleClassName(sysStaticData.getCodeName());
                    }
                }
                if (vehicleDataInfoVo.getLicenceType() != null) {
                    SysStaticData staticData = readisUtil.getSysStaticData("LICENCE_TYPE", String.valueOf(vehicleDataInfoVo.getLicenceType()));
                    if (staticData != null && staticData.getCodeName() != null) {
                        vehicleDataInfoVo.setLicenceTypeName(staticData.getCodeName());
                    }
                }
                if (null != vehicleDataInfoVo.getVehicleStatus()) {
                    vehicleDataInfoVo.setVehicleStatusName(SysStaticDataRedisUtils.getSysStaticDataById2String(redisUtil, "VEHICLE_STATUS", Long.valueOf(vehicleDataInfoVo.getVehicleStatus())));
                }


                if (vehicleDataInfoVo.getVehicleStatus() != null && vehicleDataInfoVo.getVehicleStatus().equals(8)) {
                    if (vehicleDataInfoVo.getVehicleLength() != null && !vehicleDataInfoVo.getVehicleLength().equals("")) {
                        String statusSubtype = SysStaticDataRedisUtils.getSysStaticDataById2String(redisUtil, "VEHICLE_STATUS_SUBTYPE", Long.valueOf(vehicleDataInfoVo.getVehicleLength()));
                        if (statusSubtype != null) {
                            vehicleDataInfoVo.setVehicleLengthName(statusSubtype);
                        }
                    }

                } else {
                    if (vehicleDataInfoVo.getVehicleLength() != null && !vehicleDataInfoVo.getVehicleLength().equals("")) {
                        vehicleDataInfoVo.setVehicleLengthName(SysStaticDataRedisUtils.getSysStaticDataById2String(redisUtil,
                                "VEHICLE_LENGTH", Long.valueOf(vehicleDataInfoVo.getVehicleLength())));
                    }
                }

                //??????????????????
                UserDataInfo userDataInfo = userDataInfoRecordMapper.selectById(vehicleDataInfoVo.getDriverUserId());
                if (userDataInfo != null) {
                    vehicleDataInfoVo.setDriverUserName(userDataInfo.getLinkman());
                    vehicleDataInfoVo.setDriverMobilePhone(userDataInfo.getMobilePhone());
                }
                List<TenantUserRel> tenantUserRelList = tenantUserRelMapper.getTenantUserRels(vehicleDataInfoVo.getDriverUserId(), vehicleDataInfoVo.getTenantId());
                if (tenantUserRelList != null && tenantUserRelList.size() > 0) {
                    vehicleDataInfoVo.setDriverCarUserTypeName(isysStaticDataService.getSysStaticData(
                            "DRIVER_TYPE", String.valueOf(tenantUserRelList.get(0).getCarUserType())).getCodeName());
                    vehicleDataInfoVo.setDriverCarUserType(tenantUserRelList.get(0).getCarUserType());
                } else {
                    vehicleDataInfoVo.setDriverCarUserTypeName("????????????");
                    vehicleDataInfoVo.setDriverCarUserType(-1);
                }

                //?????????????????????????????????
                SysTenantDef sysTenantDef = sysTenantDefService.getById(vehicleDataInfoVo.getTenantId());
                if (sysTenantDef != null) {
                    vehicleDataInfoVo.setTenantName(sysTenantDef.getName());
                    vehicleDataInfoVo.setTenantLinkMan(sysTenantDef.getLinkMan());
                    vehicleDataInfoVo.setLinkPhone(sysTenantDef.getLinkPhone());
                }

                //??????????????????
                List<VehicleObjectLine> vehicleObjectLineList = vehicleObjectLineMapper.getVehicleObjectLine(vehicleCode);
                List<Map<String, Object>> listMap = Lists.newArrayList();
                if (vehicleObjectLineList != null && vehicleObjectLineList.size() > 0) {
                    for (VehicleObjectLine vehicleObjectLine : vehicleObjectLineList) {
                        Map<String, Object> map = new HashMap<>();
                        if (vehicleObjectLine.getSourceProvince() != null && vehicleObjectLine.getSourceProvince() > 0) {
                            map.put("sourceProvinceName", getSysStaticDataId("SYS_PROVINCE", vehicleObjectLine.getSourceProvince() + "").getCodeName());
                        }
                        if (vehicleObjectLine.getSourceRegion() != null && vehicleObjectLine.getSourceRegion() > 0) {
                            map.put("sourceRegionName", getSysStaticDataId("SYS_CITY", vehicleObjectLine.getSourceRegion() + "").getCodeName());
                        }
                        if (vehicleObjectLine.getSourceCounty() != null && vehicleObjectLine.getSourceCounty() > 0) {
                            map.put("sourceCountyName", getSysStaticDataId("SYS_DISTRICT", vehicleObjectLine.getSourceCounty() + "").getCodeName());
                        }
                        if (vehicleObjectLine.getDesProvince() != null && vehicleObjectLine.getDesProvince() > 0) {
                            map.put("desProvinceName", getSysStaticDataId("SYS_PROVINCE", vehicleObjectLine.getDesProvince() + "").getCodeName());
                        }
                        if (vehicleObjectLine.getDesRegion() != null && vehicleObjectLine.getDesRegion() > 0) {
                            map.put("desRegionName", getSysStaticDataId("SYS_CITY", vehicleObjectLine.getDesRegion() + "").getCodeName());
                        }
                        if (vehicleObjectLine.getDesCounty() != null && vehicleObjectLine.getDesCounty() > 0) {
                            map.put("desCountyName", getSysStaticDataId("SYS_DISTRICT", vehicleObjectLine.getDesCounty() + "").getCodeName());
                        }
                        if (vehicleObjectLine.getCarriagePrice() != null && vehicleObjectLine.getCarriagePrice() != -1) {
//                            map.put("carriagePrice", CommonUtil.divide(vehicleObjectLine.getCarriagePrice()));
                            map.put("carriagePrice", vehicleObjectLine.getCarriagePrice());
                        }
                        listMap.add(map);
                    }
                }
                vehicleDataInfoVo.setVehicleOjbectLineArray(listMap);
            }

            return vehicleDataInfoVo;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Map<String, Object> getAllVehicleInfoVer(Long vehicleCode, Integer verState, String plateNumbers, String
            accessToken) {
        if (vehicleCode < 0) {
            if (StringUtils.isBlank(plateNumbers)) {
                throw new BusinessException("????????????????????????????????????");
            } else {
                QueryWrapper<VehicleDataInfo> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("plate_number", plateNumbers);
                VehicleDataInfo vehicleDataInfo = baseMapper.selectOne(queryWrapper);
                if (null == vehicleDataInfo) {
                    throw new BusinessException("??????????????????");
                }
                vehicleCode = vehicleDataInfo.getId();
            }
        }
        LoginInfo loginInfo = loginUtils.get(accessToken);
        //???????????????????????????
        Map<String, Object> map = tenantVehicleRelVerMapper.getTenantVehicleInfoVer(vehicleCode, verState, loginInfo.getTenantId());

        if (null == map) {
            return null;
        }

        //?????????????????????????????????
        List<TenantVehicleRelVer> tenantVehicleRelList = tenantVehicleRelMapper.getZYVehicleByVehicleCodeVer(vehicleCode, VEHICLE_CLASS1);
        if (tenantVehicleRelList != null && tenantVehicleRelList.size() > 0) {
            TenantVehicleRelVer ownCarRel = (TenantVehicleRelVer) tenantVehicleRelList.get(0);
            SysTenantDef tenantDef = sysTenantDefService.getById(ownCarRel.getTenantId());
            if (tenantDef != null) {
                map.put("tenantId", tenantDef.getId());
                map.put("tenantName", tenantDef.getName());
                map.put("linkman", tenantDef.getLinkMan());
                map.put("linkPhone", tenantDef.getLinkPhone());
            }
        }

        Integer vehicleClass = DataFormat.getIntKey(map, "vehicleClass");
        if (vehicleClass != null) {
            SysStaticData staticData = readisUtil.getSysStaticData("VEHICLE_CLASS", vehicleClass.toString());
            if (staticData != null) {
                map.put("vehicleClassName", staticData.getCodeName());
            }
        }


        Integer licenceType = (Integer) map.get("licenceType");
        if (licenceType != null) {
            SysStaticData staticData = readisUtil.getSysStaticData("LICENCE_TYPE", licenceType.toString());
            if (staticData != null) {
                map.put("licenceTypeName", staticData.getCodeName());
            }
        }

        Integer vehicleStatus = (Integer) map.get("vehicleStatus");
        if (vehicleStatus != null) {
            map.put("vehicleStatusName", SysStaticDataRedisUtils.getSysStaticDataById2String(redisUtil, "VEHICLE_STATUS", vehicleStatus));
        }

        if (map.containsKey("vehicleLength")) {
            String length = (String) map.get("vehicleLength");
            if (length != null && !length.equals("")) {
                //Long vehicleLength = Long.parseLong(length);
                if (vehicleStatus != null && vehicleStatus == SysStaticDataEnum.VEHICLE_STATUS_ENUM.VEHICLE_CAR_BOX.getType()) {
                    map.put("vehicleLengthName", SysStaticDataRedisUtils.getSysStaticDataById2String(redisUtil, "VEHICLE_STATUS_SUBTYPE", length));
                } else {
                    map.put("vehicleLengthName", SysStaticDataRedisUtils.getSysStaticDataById2String(redisUtil, "VEHICLE_LENGTH", length));
                }
            }
        }
        //??????????????????
        if (map.containsKey("driverUserId")) {
            Long driverUserId = Long.valueOf(map.get("driverUserId").toString());
            UserDataInfo mainUserData = userDataInfoRecordMapper.selectById(driverUserId);
            if (null != mainUserData) {
                List<TenantUserRel> tenantUserRelList = tenantUserRelMapper.getTenantUserRels(driverUserId, loginInfo.getTenantId());
                if (tenantUserRelList != null && tenantUserRelList.size() > 0) {
                    if (tenantUserRelList.get(0).getCarUserType() != null && tenantUserRelList.get(0).getCarUserType() > 0) {
                        map.put("driverCarUserType", tenantUserRelList.get(0).getCarUserType());
                        map.put("driverCarUserTypeName", isysStaticDataService.getSysStaticData("DRIVER_TYPE",
                                String.valueOf(tenantUserRelList.get(0).getCarUserType())).getCodeName());
                    } else {
                        map.put("driverCarUserType", -1);
                        map.put("driverCarUserTypeName", "????????????");
                    }
                } else {
                    map.put("driverCarUserType", -1);
                    map.put("driverCarUserTypeName", "????????????");
                }
                map.put("driverUserName", mainUserData.getLinkman());
                map.put("driverMobilePhone", mainUserData.getMobilePhone());
            }
        }

        if (map.containsKey("loadEmptyOilCost")) {
//            map.put("loadEmptyOilCost", CommonUtil.divide((Long) map.get("loadEmptyOilCost")));
            map.put("loadEmptyOilCost", map.get("loadEmptyOilCost"));
        }
        if (map.containsKey("loadFullOilCost")) {
            map.put("loadFullOilCost", map.get("loadFullOilCost"));
        }

        //????????????????????????,?????????????????????
        if (vehicleClass == VEHICLE_CLASS1 || vehicleClass == VEHICLE_CLASS2 || vehicleClass == VEHICLE_CLASS4) {
            Long relId = (Long) map.get("relId");
            TenantVehicleRelInfoDto relMap = tenantVehicleCostRelVerMapper.getTenantVehicleRelInfoVer(vehicleCode, loginInfo.getTenantId(), relId, verState);
            if (null != relMap) {
                //??????????????????
//                List<String> fenString = new ArrayList<>();
//                fenString.add("price");
//                fenString.add("loanInterest");
//                fenString.add("interestPeriods");
//                fenString.add("payInterestPeriods");
//                fenString.add("collectionInsurance");
//                fenString.add("examVehicleFee");
//                fenString.add("maintainFee");
//                fenString.add("repairFee");
//                fenString.add("managementCost");
//                fenString.add("tyreFee");
//                fenString.add("otherFee");
//                fenString.add("residual");
//                CommonUtil.transFeeFenToYuan(fenString, relMap);
                Map<String, Object> tenantVehicleRelInfoDto = BeanUtil.beanToMap(relMap);
                map.putAll(tenantVehicleRelInfoDto);
                map.put("tenantVehicleRelInfoDto",tenantVehicleRelInfoDto);
//                map.setTenantVehicleRelInfoDto(relMap);

//                map.put("tenantVehicleRelInfoDto",tenantVehicleRelInfoDto);
            } else {
                map.put("tenantVehicleRelInfoDto", null);
            }
            //???????????????????????????
            long copilotDriverId = DataFormat.getLongKey(map, "copilotDriverId");
            if (copilotDriverId > 0) {
                UserDataInfo copilotDriver = userDataInfoRecordMapper.selectById(copilotDriverId);
                if (null != copilotDriver) {
                    map.put("copilotDriverUserName", copilotDriver.getLinkman());
                    map.put("copilotDriverMobilePhone", copilotDriver.getMobilePhone());

                    List<TenantUserRel> tenantUserRelList = tenantUserRelMapper.getTenantUserRels(copilotDriver.getId(), loginInfo.getTenantId());
                    if (tenantUserRelList != null && tenantUserRelList.size() > 0) {

                        if (tenantUserRelList.get(0).getCarUserType() != null && tenantUserRelList.get(0).getCarUserType() > 0) {
                            map.put("copilotDriverUserType", tenantUserRelList.get(0).getCarUserType());
                            map.put("copilotDriverUserTypeName", isysStaticDataService.getSysStaticData("DRIVER_TYPE", String.valueOf(tenantUserRelList.get(0).getCarUserType())).getCodeName());
                        } else {
                            map.put("copilotDriverUserType", -1);
                            map.put("copilotDriverUserTypeName", "????????????");
                        }
                    } else {
                        map.put("copilotDriverUserType", -1);
                        map.put("copilotDriverUserTypeName", "????????????");
                    }
                }
            }


            //????????????????????????

            if (map.containsKey("followDriverId")) {
                Long followDriverId = (Long) map.get("followDriverId");
                if (followDriverId > 0) {
                    UserDataInfo followDriver = userDataInfoRecordMapper.selectById(followDriverId);
                    if (null != followDriver) {
                        map.put("followDriverUserName", followDriver.getLinkman());
                        map.put("followDriverMobilePhone", followDriver.getMobilePhone());

                        List<TenantUserRel> tenantUserRelList = tenantUserRelMapper.getTenantUserRels(followDriver.getId(), loginInfo.getTenantId());
                        if (tenantUserRelList != null && tenantUserRelList.size() > 0) {
                            if (tenantUserRelList.get(0).getCarUserType() != null && tenantUserRelList.get(0).getCarUserType() > 0) {
                                map.put("followDriverUserType", tenantUserRelList.get(0).getCarUserType());
                                map.put("followDriverUserTypeName", isysStaticDataService.getSysStaticData("DRIVER_TYPE", String.valueOf(tenantUserRelList.get(0).getCarUserType())).getCodeName());
                            } else {
                                map.put("followDriverUserType", -1);
                                map.put("followDriverUserTypeName", "????????????");
                            }
                        } else {
                            map.put("followDriverUserType", -1);
                            map.put("followDriverUserTypeName", "????????????");
                        }
                    }
                }
            }
            //??????????????????
            Long orgId = (Long) map.get("orgId");
            if (orgId != null && orgId > -1) {
                try {
                    map.put("orgName", iSysOrganizeService.getCurrentTenantOrgNameById(loginInfo.getTenantId(), orgId));
                } catch (RpcException e) {
                    e.printStackTrace();
                }
            }
            //?????????
            Long userId = DataFormat.getLongKey(map, "userId");
            if (userId != null && userId > -1) {
                UserDataInfo userSV = userDataInfoRecordMapper.selectById(userId);
                if (userSV != null && StringUtils.isNotBlank(userSV.getLinkman())) {
                    String userName = userSV.getLinkman();
                    if (userName == null || userName.equals("")) {
                        userName = userSV.getMobilePhone();
                    }
                    map.put("userName", userName);
                }
            }
            //???????????????
            int shareFlg = DataFormat.getIntKey(map, "shareFlg");
            if (shareFlg == YES) {
                long linkUserId = DataFormat.getLongKey(map, "linkUserId");
                if (linkUserId > -1) {
                    UserDataInfo shareLinkUser = userDataInfoRecordMapper.selectById(linkUserId);
                    if (null != shareLinkUser) {
                        map.put("shareUserName", shareLinkUser.getLinkman());
                        map.put("shareMobilePhone", shareLinkUser.getMobilePhone());
                    }
                }
            }
            if (shareFlg == SysStaticDataEnum.SHARE_FLG.NO) {
                map.put("shareFlgName", "???");
            } else {
                map.put("shareFlgName", "???");
            }

        } else {
            long ownTenantId = DataFormat.getLongKey(map, "tenantId");
            SysTenantDef sysTenantDef = sysTenantDefService.getById(ownTenantId);
            //?????????????????????????????????
            if (null != sysTenantDef) {
                map.put("tenantName", sysTenantDef.getName());
                map.put("tenantLinkMan", sysTenantDef.getLinkMan());
                map.put("tenantLinkPhone", sysTenantDef.getLinkPhone());
            }
        }
        // long hisId = DataFormat.getLongKey(map, "hisId");
        //??????????????????
//        List<Map<String, Object>> rtnList = vehicleLineRelVerMapper.getVehicleObjectLineVer(verState, vehicleCode);
        List<VehicleObjectLineVer> rtnList = vehicleLineRelVerMapper.getVehicleObjectLineVerHis(verState, vehicleCode);
        if (null != rtnList && rtnList.size() > 0) {
//            List<SysStaticData> province = (List<SysStaticData>) redisUtil.get(com.youming.youche.conts.SysStaticDataEnum.SYS_STATIC_DATA_NAME.concat("SYS_PROVINCE"));
//            List<SysStaticData> sysCity = (List<SysStaticData>) redisUtil.get(com.youming.youche.conts.SysStaticDataEnum.SYS_STATIC_DATA_NAME.concat("SYS_CITY"));
//            List<SysStaticData> sys_district = (List<SysStaticData>) redisUtil.get(com.youming.youche.conts.SysStaticDataEnum.SYS_STATIC_DATA_NAME.concat("SYS_DISTRICT"));

            for (int i = 0; i < rtnList.size(); i++) {
                VehicleObjectLineVer ver = rtnList.get(i);
                if (ver.getSourceProvince() != null && ver.getSourceProvince() > 0) {
                    ver.setSourceProvinceName(getSysStaticDataId("SYS_PROVINCE", ver.getSourceProvince() + "").getCodeName());
                }
                if (ver.getSourceRegion() != null && ver.getSourceRegion() > 0) {
                    ver.setSourceRegionName(getSysStaticDataId("SYS_CITY", ver.getSourceRegion() + "").getCodeName());
                }
                if (ver.getSourceCounty() != null && ver.getSourceCounty() > 0) {
                    ver.setSourceCountyName(getSysStaticDataId("SYS_DISTRICT", ver.getSourceCounty() + "").getCodeName());
                }
                if (ver.getDesProvince() != null && ver.getDesProvince() > 0) {
                    ver.setDesProvinceName(getSysStaticDataId("SYS_PROVINCE", ver.getDesProvince() + "").getCodeName());
                }
                if (ver.getDesRegion() != null && ver.getDesRegion() > 0) {
                    ver.setDesRegionName(getSysStaticDataId("SYS_CITY", ver.getDesRegion() + "").getCodeName());
                }
                if (ver.getDesCounty() != null && ver.getDesCounty() > 0) {
                    ver.setDesCountyName(getSysStaticDataId("SYS_DISTRICT", ver.getDesCounty() + "").getCodeName());
                }
//                if (ver.getCarriagePrice() != null) {
//                    iMap.put("carriagePrice", CommonUtil.divide(carriagePrice));
//                    iMap.put("carriagePrice", carriagePrice);
//                }

            }
        }

        ///??????????????????
        List<VehicleLineRelVer> vehicleLineRelList = vehicleLineRelVerMapper.getVehiclelineRels(vehicleCode, verState);
        map.put("vehiclelineRels", vehicleLineRelList);

        if (null != rtnList) {
            map.put("vehicleOjbectLineArray", rtnList);
        }
        return map;

    }

    public boolean successNextAuditNode(AuditNodeInst auditNodeInst, AuditConfig auditConfig, String desc, String
            accessToken,
                                        Map<String, Object> map, Long busiId, Long nodeId, String nodeName, Integer ruleVersion) {
        LoginInfo loginInfo = loginUtils.get(accessToken);

        if (checkNodePreRule(nodeId, busiId, map, ruleVersion, loginInfo.getTenantId())) {
            //?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
            //???????????????????????????????????? ????????????????????????????????????????????????????????????
            auditNodeInst.setAuditManId(loginInfo.getUserInfoId());
            auditNodeInst.setRemark(desc);
            auditNodeInst.setAuditDate(LocalDateTime.now());
            auditNodeInst.setAuditResult(AuditConsts.RESULT.SUCCESS);
            auditNodeInstService.updateById(auditNodeInst);

            //????????????????????????
            AuditNodeInst nextAuditNodeInst = new AuditNodeInst();
            nextAuditNodeInst.setAuditBatch(auditNodeInst.getAuditBatch());
            nextAuditNodeInst.setAuditId(auditConfig.getId());
            nextAuditNodeInst.setAuditCode(auditConfig.getAuditCode());
            nextAuditNodeInst.setNodeId(nodeId);
            nextAuditNodeInst.setNodeName(nodeName);
            nextAuditNodeInst.setStatus(AuditConsts.Status.AUDITING);
            nextAuditNodeInst.setAuditResult(AuditConsts.RESULT.TO_AUDIT);
            nextAuditNodeInst.setParamsMap(JSONUtil.toJsonStr(map));
            nextAuditNodeInst.setBusiId(busiId);
            nextAuditNodeInst.setLogBusiCode(auditNodeInst.getLogBusiCode());
            nextAuditNodeInst.setVersion(auditNodeInst.getVersion());
            if (auditNodeInst.getNodeIndex() != null) {
                //??????????????????
                nextAuditNodeInst.setNodeIndex(auditNodeInst.getNodeIndex() + 1);
            }
            auditNodeInstService.save(nextAuditNodeInst);
            //?????????????????????
            List<Long> auditUserList = auditUserService.selectIdByNodeIdAndTarGetObjType(nextAuditNodeInst.getNodeId(), AuditConsts.TargetObjType.USER_TYPE);
            try {
                nodeAuditCallback(nextAuditNodeInst.getNodeIndex() == null ? 0 : nextAuditNodeInst.getNodeIndex(), busiId, AuditConsts.TargetObjType.USER_TYPE, listLongToStr(auditUserList), auditConfig.getNodeCallback());
                StringBuffer alert = new StringBuffer("??????????????????");
                alert.append(getAuditUserNameByNodeId(nextAuditNodeInst.getNodeId(), AuditConsts.TargetObjType.USER_TYPE));
                String msg = "";
                if (StringUtils.isNotBlank(auditNodeInst.getRemark())) {
                    msg = "????????????[" + auditNodeInst.getRemark() + "]";
                }
                String auditCodeName = isysStaticDataService.getSysStaticData("AUDIT_CODE", auditConfig.getAuditCode()).getCodeName();
                saveSysOperLog(null, busiId, SysOperLogConst.OperType.Audit, auditCodeName + "????????????" + msg, loginInfo);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        } else {
            //??????????????????????????????????????? ???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
            try {
                successAuditNodeInst(auditNodeInst, desc, busiId, map, auditConfig, null, loginInfo);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return false;
    }

    private void successAuditNodeInst(AuditNodeInst auditNodeInst, String desc, Long
            busiId, Map<String, Object> paramsMap, AuditConfig auditConfig, Integer type, LoginInfo loginInfo) {
        if (type == null || type.intValue() == AuditConsts.OperType.TASK) {
            auditNodeInst.setAuditManId(loginInfo.getUserInfoId());
            auditNodeInst.setRemark(desc);
            auditNodeInst.setAuditDate(LocalDateTime.now());
            auditNodeInst.setStatus(AuditConsts.Status.FINISH);
            auditNodeInst.setAuditResult(AuditConsts.RESULT.SUCCESS);
            auditNodeInstService.updateById(auditNodeInst);
            //??????????????????????????????????????????
            auditNodeInstService.updateAuditInstToFinish(auditNodeInst.getAuditId(), busiId);
            List<AuditNodeInst> auditNodeInsts = auditNodeInstService.queryAuditNodeInst(auditNodeInst.getAuditCode(), auditNodeInst.getBusiId(), auditNodeInst.getTenantId());
            if (auditNodeInsts != null && auditNodeInsts.size() > 0) {
                for (AuditNodeInst nodeInst : auditNodeInsts) {
                    auditNodeInstService.removeById(nodeInst);
                }
            }
        }
        try {
            if (type == null || type.intValue() == AuditConsts.OperType.WEB) {
                //???????????????????????????????????????????????????????????????
                auditCallback(busiId, AuditConsts.RESULT.SUCCESS, desc, paramsMap, auditConfig.getCallback());
            }
            if (type == null || type.intValue() == AuditConsts.OperType.TASK) {
                String msg = "";
                if (StringUtils.isNotBlank(auditNodeInst.getRemark())) {
                    msg = "????????????[" + auditNodeInst.getRemark() + "]";
                }
                String auditCodeName = isysStaticDataService.getSysStaticData("AUDIT_CODE", auditConfig.getAuditCode()).getCodeName();
                saveSysOperLog(null, busiId, SysOperLogConst.OperType.Audit, auditCodeName + "????????????" + msg, loginInfo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean checkNodePreRule(Long nodeId, Long busiId, Map params, Integer ruleVersion, Long tenantId) {
        boolean isNew = false;
        Integer integer = auditNodeRuleConfigService.checkVersionRule(nodeId, ruleVersion, tenantId);
        if (integer > 0) {
            isNew = true;
        }
        List<Map<Object, Object>> ruleList = null;
        if (isNew) {
            ruleList = auditNodeRuleConfigService.getAuditNodeRuleConfigByNodeVer(nodeId);
        } else {
            ruleList = auditNodeRuleConfigService.getAuditNodeRuleConfigByNodeVerFalse(nodeId, tenantId, ruleVersion);
        }
        //???????????????????????????????????????????????????????????????????????????????????????
        if (ruleList != null && ruleList.size() > 0) {
            boolean pass = false;
            for (Map<Object, Object> objectObjectMap : ruleList) {
                try {
                    AuditRuleConfig auditRuleConfig = new AuditRuleConfig();
                    BeanUtil.copyProperties(auditRuleConfig, objectObjectMap);
                    String ruleValue = "";
                    if (isNew) {
                        AuditNodeRuleConfig auditNodeRuleConfig = new AuditNodeRuleConfig();
                        BeanUtil.copyProperties(auditNodeRuleConfig, objectObjectMap);
                        ruleValue = auditNodeRuleConfig.getRuleValue();
                    } else {
                        AuditNodeRuleConfigVer auditNodeRuleConfigVer = new AuditNodeRuleConfigVer();
                        BeanUtil.copyProperties(auditNodeRuleConfigVer, objectObjectMap);
                        ruleValue = auditNodeRuleConfigVer.getRuleValue();
                    }
                    if (checkRule(auditRuleConfig.getRuleType(), ruleValue, auditRuleConfig.getTargetObj(), auditRuleConfig.getRuleKey(), busiId, params)) {
                        pass = true;
                        break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            //????????????????????????????????????????????????
            return pass;
        }
        return true;
    }

    public boolean checkRule(Integer ruleType, String ruleValue,
                             String targetObj, String ruleKey, Long busiId, Map<String, Object> params) throws Exception {

        Class cls = Class.forName(targetObj);
        Method m = cls.getDeclaredMethod("execute", new Class[]{Integer.class, String.class, Long.class, Map.class, String.class});
        Boolean result = (Boolean) m.invoke(cls.newInstance(), ruleType, ruleValue, busiId, params, ruleKey);
        return result;
    }

    public void auditCallback(Long busiId, Integer result, String desc,
                              Map paramsMap, String callback) throws Exception {
        Class cls = Class.forName(callback);
        Method m = null;
        if (AuditConsts.RESULT.SUCCESS == result) {
            m = cls.getDeclaredMethod("sucess", new Class[]{Long.class, String.class, Map.class});
        } else if (AuditConsts.RESULT.FAIL == result) {
            m = cls.getDeclaredMethod("fail", new Class[]{Long.class, String.class, Map.class});
        } else {
            throw new BusinessException("??????????????????????????????????????????1???2???????????????[" + result + "]");
        }

        m.invoke(cls.newInstance(), busiId, desc, paramsMap);
    }

    public void nodeAuditCallback(int nodeIndex, long busiId, int targetObjectType, String targetObjId, String
            callBack) throws Exception {
        if (StringUtils.isEmpty(callBack)) {
            return;
        }
        Class cls = Class.forName(callBack);
        Method m = cls.getDeclaredMethod("notice", new Class[]{Integer.class, Long.class, Integer.class, String.class});
        m.invoke(cls.newInstance(), nodeIndex, busiId, targetObjectType, targetObjId);
    }

    public String getAuditUserNameByNodeId(Long nodeId, Integer targetObjType) throws Exception {
        List<Long> userIdList = auditUserService.selectIdByNodeIdAndTarGetObjType(nodeId, targetObjType);
        StringBuffer alert = new StringBuffer();
        if (userIdList != null && userIdList.size() > 0) {
            alert.append("???:");
            List<String> userNameList = Lists.newArrayList();
            for (Long aLong : userIdList) {
                String userName = iUserDataInfoRecordService.getUserName(aLong);
                userNameList.add(userName);
            }
            alert.append(listToString(userNameList));
        } else {
            throw new BusinessException("????????????????????????????????????????????????");
        }
        return alert.toString();
    }

    public String listToString(List<String> list) {
        if (list == null) {
            return null;
        }
        StringBuilder result = new StringBuilder();
        boolean first = true;
        //????????????????????????","
        for (String string : list) {
            if (first) {
                first = false;
            } else {
                result.append(",");
            }
            result.append(string);
        }
        return result.toString();
    }

    public String listLongToStr(List<Long> list) {
        if (list == null) {
            return null;
        }
        StringBuilder result = new StringBuilder();
        boolean first = true;
        //????????????????????????","
        for (Long string : list) {
            if (first) {
                first = false;
            } else {
                result.append(",");
            }
            result.append(string);
        }
        return result.toString();
    }

    @Override
    public Map<String, Object> getVehicleIsRegister(String plateNumber, Integer vehicleClassIn, String accessToken) {
        if (StringUtils.isEmpty(plateNumber)) {
            throw new BusinessException("?????????????????????????????????");
        }
        LoginInfo loginInfo = loginUtils.get(accessToken);
        Integer vehicleClass = tenantVehicleRelMapper.checkVehicleClass(plateNumber, loginInfo.getTenantId());

        if (null != vehicleClass) {
//            if (VEHICLE_CLASS1 == vehicleClass) {
//                throw new BusinessException("?????????????????????????????????????????????????????????????????????????????????????????????");
//            } else if (SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS5 == vehicleClass) {
//                throw new BusinessException("???????????????????????????????????????????????????????????????????????????????????????????????????");
//            } else if (SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS2 == vehicleClass) {
//                throw new BusinessException("?????????????????????????????????????????????????????????????????????????????????????????????");
//            } else if (SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS4 == vehicleClass) {
//                throw new BusinessException("?????????????????????????????????????????????????????????????????????????????????????????????");
//            }
            throw new BusinessException("????????????????????????");
        }
        //??????????????????
        Map<String, Object> rtnMap = baseMapper.getVehicleIsRegister(vehicleClassIn, plateNumber);
        if (SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS4 == vehicleClassIn && rtnMap != null && DataFormat.getLongKey(rtnMap, "tenantId") > 0) {
            throw new BusinessException("???????????????????????????????????????????????????????????????");
        }

        //?????????
        if (null != rtnMap && !rtnMap.isEmpty()) {
            long vehicleCode = DataFormat.getLongKey(rtnMap, "vehicleCode");
            Map<String, Object> tmpMap = applyRecordService.getVehicleRecordInfo(vehicleCode, loginInfo.getTenantId());
            if (null != tmpMap && !tmpMap.isEmpty()) {
                rtnMap.putAll(tmpMap);
                int state = DataFormat.getIntKey(tmpMap, "state");
                rtnMap.put("stateName", isysStaticDataService.getSysStaticDatas("APPLY_STATE", state));
            } else {
                rtnMap.put("state", -1);
            }

            //???????????????????????????????????????????????????????????????????????????????????????
            QueryWrapper<TenantVehicleRel> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("plate_number", plateNumber);
            queryWrapper.orderByAsc("driver_c_time");
            List<TenantVehicleRel> vehicleRelList = tenantVehicleRelMapper.selectList(queryWrapper);
            if (vehicleRelList != null && vehicleRelList.size() > 0) {
                List<Map<String, Object>> driverList = new ArrayList<Map<String, Object>>();
                List<Long> driverIdList = new ArrayList<Long>();
                for (TenantVehicleRel tenantVehicleRel : vehicleRelList) {
                    if (tenantVehicleRel.getDriverUserId() != null && tenantVehicleRel.getDriverUserId() > 0) {

                        if (!driverIdList.contains(tenantVehicleRel.getDriverUserId())) {

                            driverIdList.add(tenantVehicleRel.getDriverUserId());
                            Map<String, Object> singleDriver = new HashMap<String, Object>();
                            singleDriver.put("driverId", tenantVehicleRel.getDriverUserId());
                            UserDataInfo driverInfo = userDataInfoRecordMapper.selectById(tenantVehicleRel.getDriverUserId());
                            if (driverInfo != null && driverInfo.getLinkman() != null) {
                                singleDriver.put("driverName", driverInfo.getLinkman());
                            }
                            if (driverInfo != null && driverInfo.getMobilePhone() != null) {
                                singleDriver.put("mobilePhone", driverInfo.getMobilePhone());
                            }
                            driverList.add(singleDriver);

                            if (driverIdList.size() == 1) {
                                //????????????????????????????????????????????????
                                rtnMap.put("driverUserId", tenantVehicleRel.getDriverUserId());
                                if (driverInfo != null && driverInfo.getLinkman() != null) {
                                    rtnMap.put("linkman", driverInfo.getLinkman());
                                }
                                if (driverInfo != null && driverInfo.getMobilePhone() != null) {
                                    rtnMap.put("mobilePhone", driverInfo.getMobilePhone());
                                }


                            }
                        }
                    }
                }
                rtnMap.put("driverList", driverList);
            }
        }
        //????????????????????????
        return rtnMap;
    }


    @Override
    public Map<String, Object> getVehicleIsRegisterDatainfo(String plateNumber, Integer vehicleClassIn, String accessToken) {
        if (StringUtils.isEmpty(plateNumber)) {
            throw new BusinessException("?????????????????????????????????");
        }
        LoginInfo loginInfo = loginUtils.get(accessToken);
        Integer vehicleClass = tenantVehicleRelMapper.checkVehicleClass(plateNumber, loginInfo.getTenantId());

        if (null != vehicleClass) {
//            if (VEHICLE_CLASS1 == vehicleClass) {
//                throw new BusinessException("?????????????????????????????????????????????????????????????????????????????????????????????");
//            } else if (SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS5 == vehicleClass) {
//                throw new BusinessException("???????????????????????????????????????????????????????????????????????????????????????????????????");
//            } else if (SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS2 == vehicleClass) {
//                throw new BusinessException("?????????????????????????????????????????????????????????????????????????????????????????????");
//            } else if (SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS4 == vehicleClass) {
//                throw new BusinessException("?????????????????????????????????????????????????????????????????????????????????????????????");
//            }
            throw new BusinessException("????????????????????????");
        }
        //??????????????????
        Map<String, Object> rtnMap = baseMapper.getVehicleIsRegister(vehicleClassIn, plateNumber);
        if (SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS4 == vehicleClassIn && rtnMap != null && DataFormat.getLongKey(rtnMap, "tenantId") > 0) {
            throw new BusinessException("???????????????????????????????????????????????????????????????");
        }

        //?????????
        if (null != rtnMap && !rtnMap.isEmpty()) {
//            long vehicleCode = DataFormat.getLongKey(rtnMap, "vehicleCode");
//            Map<String, Object> tmpMap = applyRecordService.getVehicleRecordInfo(vehicleCode, loginInfo.getTenantId());
//            if (null != tmpMap && !tmpMap.isEmpty()) {
//                rtnMap.putAll(tmpMap);
//                int state = DataFormat.getIntKey(tmpMap, "state");
//                rtnMap.put("stateName", isysStaticDataService.getSysStaticDatas("APPLY_STATE", state));
//            } else {
//                rtnMap.put("state", -1);
//            }
//
//            //???????????????????????????????????????????????????????????????????????????????????????
//            QueryWrapper<TenantVehicleRel> queryWrapper = new QueryWrapper<>();
//            queryWrapper.eq("plate_number", plateNumber);
//            queryWrapper.orderByAsc("driver_c_time");
//            List<TenantVehicleRel> vehicleRelList = tenantVehicleRelMapper.selectList(queryWrapper);
//            if (vehicleRelList != null && vehicleRelList.size() > 0) {
//                List<Map<String, Object>> driverList = new ArrayList<Map<String, Object>>();
//                List<Long> driverIdList = new ArrayList<Long>();
//                for (TenantVehicleRel tenantVehicleRel : vehicleRelList) {
//                    if (tenantVehicleRel.getDriverUserId() != null && tenantVehicleRel.getDriverUserId() > 0) {
//
//                        if (!driverIdList.contains(tenantVehicleRel.getDriverUserId())) {
//
//                            driverIdList.add(tenantVehicleRel.getDriverUserId());
//                            Map<String, Object> singleDriver = new HashMap<String, Object>();
//                            singleDriver.put("driverId", tenantVehicleRel.getDriverUserId());
//                            UserDataInfo driverInfo = userDataInfoRecordMapper.selectById(tenantVehicleRel.getDriverUserId());
//                            if (driverInfo != null && driverInfo.getLinkman() != null) {
//                                singleDriver.put("driverName", driverInfo.getLinkman());
//                            }
//                            if (driverInfo != null && driverInfo.getMobilePhone() != null) {
//                                singleDriver.put("mobilePhone", driverInfo.getMobilePhone());
//                            }
//                            driverList.add(singleDriver);
//
//                            if (driverIdList.size() == 1) {
//                                //????????????????????????????????????????????????
//                                rtnMap.put("driverUserId", tenantVehicleRel.getDriverUserId());
//                                if (driverInfo != null && driverInfo.getLinkman() != null) {
//                                    rtnMap.put("linkman", driverInfo.getLinkman());
//                                }
//                                if (driverInfo != null && driverInfo.getMobilePhone() != null) {
//                                    rtnMap.put("mobilePhone", driverInfo.getMobilePhone());
//                                }
//
//
//                            }
//                        }
//                    }
//                }
//                rtnMap.put("driverList", driverList);
//            }
            throw new BusinessException("????????????????????????");
        }
        //????????????????????????
        return rtnMap;
    }

    @Override
    public VehicleApplyRecordVo getVehicleInviteRecord(Long applyRecordId) {
        ApplyRecord applyRecord = applyRecordService.getById(applyRecordId);
        if (applyRecord != null) {
            if (applyRecord.getState() != 0 && applyRecord.getState() != 3 && applyRecord.getReadState() == 0) {
                applyRecord.setReadState(1);
                applyRecordService.updateById(applyRecord);
            }
        }

        VehicleApplyRecordVo vehicleApplyRecord = applyRecordService.getVehicleApplyRecord(applyRecordId);
        if (vehicleApplyRecord != null) {
            if (vehicleApplyRecord.getState() != null && vehicleApplyRecord.getState() > -1) {
                vehicleApplyRecord.setStateName(isysStaticDataService.getSysStaticDatas("APPLY_STATE", vehicleApplyRecord.getState()));
            }
            if (vehicleApplyRecord.getVehicleStatus() != null && vehicleApplyRecord.getVehicleStatus() > 0) {
                vehicleApplyRecord.setVehicleStatusName(isysStaticDataService.getSysStaticDataId("VEHICLE_STATUS", vehicleApplyRecord.getVehicleStatus().toString()));
            }
            if (StringUtils.isNotBlank(vehicleApplyRecord.getVehicleLength()) && !vehicleApplyRecord.getVehicleLength().equals("-1")) {
                try {
                    if (vehicleApplyRecord.getVehicleStatus() != null && vehicleApplyRecord.getVehicleStatus() == 8) {
                        vehicleApplyRecord.setVehicleLengthName(isysStaticDataService.getSysStaticData("VEHICLE_STATUS_SUBTYPE", vehicleApplyRecord.getVehicleLength()).getCodeName());
                    } else {
                        vehicleApplyRecord.setVehicleLengthName(isysStaticDataService.getSysStaticDataId("VEHICLE_LENGTH", vehicleApplyRecord.getVehicleLength() == null ? "0" : vehicleApplyRecord.getVehicleLength().toString()));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (vehicleApplyRecord.getVehicleStatus() != null && vehicleApplyRecord.getVehicleStatus() > 0) {
                if (StringUtils.isNotBlank(vehicleApplyRecord.getVehicleLength()) && !vehicleApplyRecord.getVehicleLength().equals("-1")) {
                    if (StringUtils.isNotBlank(vehicleApplyRecord.getVehicleStatusName())
                            && StringUtils.isNotBlank(vehicleApplyRecord.getVehicleLengthName())) {
                        vehicleApplyRecord.setVehicleInfo(vehicleApplyRecord.getVehicleStatusName() + "/" + vehicleApplyRecord.getVehicleStatusName());
                    }
                } else if (StringUtils.isNotBlank(vehicleApplyRecord.getVehicleLength()) || !vehicleApplyRecord.getVehicleLength().equals("-1")) {
                    if (StringUtils.isNotBlank(vehicleApplyRecord.getVehicleStatusName())) {
                        vehicleApplyRecord.setVehicleInfo(vehicleApplyRecord.getVehicleStatusName());
                    }
                } else {
                    if (StringUtils.isNotBlank(vehicleApplyRecord.getVehicleLengthName())) {
                        vehicleApplyRecord.setVehicleInfo(vehicleApplyRecord.getVehicleLengthName());
                    }
                }
            }

            if (vehicleApplyRecord.getLicenceType() != null && vehicleApplyRecord.getLicenceType() > 0) {
                vehicleApplyRecord.setLicenceTypeName(isysStaticDataService.getSysStaticDatas("LICENCE_TYPE", vehicleApplyRecord.getLicenceType()));
            }
            if (vehicleApplyRecord.getApplyVehicleClass() != null && vehicleApplyRecord.getApplyVehicleClass() > 0) {
                vehicleApplyRecord.setApplyVehicleClassName(isysStaticDataService.getSysStaticDatas("VEHICLE_CLASS", vehicleApplyRecord.getApplyVehicleClass()));
            }
            if (vehicleApplyRecord.getVehicleClass() != null && vehicleApplyRecord.getVehicleClass() > 0) {
                vehicleApplyRecord.setVehicleClassName(isysStaticDataService.getSysStaticDatas("VEHICLE_CLASS", vehicleApplyRecord.getVehicleClass()));
            }
            if(vehicleApplyRecord.getApplyTenantId() != null){
                SysTenantDef sysTenantDef = sysTenantDefService.getById(vehicleApplyRecord.getApplyTenantId());
                vehicleApplyRecord.setTenantName(sysTenantDef.getName());
                vehicleApplyRecord.setLinkPhone(sysTenantDef.getLinkPhone());
            }
        }

        return vehicleApplyRecord;
    }

    @Override
    @Transactional
    public boolean doSaveApplyRecord(ApplyRecorDto applyRecorDto, String accessToken) {

        LoginInfo loginInfo = loginUtils.get(accessToken);
        Long vehicleCode = applyRecorDto.getVehicleCode();
        Integer applyState = applyRecorDto.getApplyState();

        if (vehicleCode < 0) {
            log.error("???????????????????????????");
//            return ResponseResult.failure("???????????????????????????");
            throw new BusinessException("????????????????????????!");
        }
        Integer vehicleClass = applyRecorDto.getApplyVehicleClass();
        if (vehicleClass < 0) {
            log.error("????????????????????????");
            throw new BusinessException("????????????????????????!");
        }
        if (applyState < 0) {
            log.error("???????????????????????????");
            throw new BusinessException("????????????????????????!");
        }
        Long countApply = applyRecordService.getApplyCount(loginInfo.getTenantId(), vehicleClass, vehicleCode);

        if (countApply > 0) {
            log.error("????????????????????????????????????????????????");
            throw new BusinessException("?????????????????????????????????????????????!");
        }
        Long count = tenantVehicleRelMapper.getZYCount(vehicleCode, loginInfo.getTenantId(), vehicleClass);
        if (count > 0) {
            log.error("??????????????????????????????");
            throw new BusinessException("???????????????????????????!");
        }

        VehicleDataInfo vehicleDataInfo = baseMapper.selectById(vehicleCode);
        if (null == vehicleDataInfo) {
            log.error("?????????????????????????????????????????????????????????");
            throw new BusinessException("???????????????!????????????????????????????????????!");
        }

        //??????????????????????????????
        Long beApplyTenantId = DataFormat.getLongKey(vehicleDataInfo.getTenantId() + "");
        // ITenantSV tenantSV = (ITenantSV)SysContexts.getBean("tenantSV");
        SysTenantDef sysTenantDef = null;
        if (beApplyTenantId > 1) {
            sysTenantDef = sysTenantDefService.getById(beApplyTenantId);
        }
//        if (beApplyTenantId > 0 && null == sysTenantDef) {
//            log.error("??????????????????????????????????????????????????????");
//            throw new BusinessException("??????????????????????????????????????????????????????!");
//        }
        if(applyRecorDto.getPublicVehicle() != null ){
            if(applyRecorDto.getPublicVehicle() == -1 && applyRecorDto.getApplyDriverUserId() == null && applyRecorDto.getApplyVehicleClass() != null && applyRecorDto.getApplyVehicleClass() != VEHICLE_CLASS5){
                throw new BusinessException("????????????????????????????????????????????????!");
            }
        }
        Integer isAuth =null;
        if (beApplyTenantId > 1) {
            TenantVehicleRel tenantVehicleRel = tenantVehicleRelMapper.tenantVehicleRel(vehicleCode, beApplyTenantId);
            if (null != tenantVehicleRel) {
                List<Long> longList=new ArrayList<>(1);
                longList.add(tenantVehicleRel.getId());
                Map<Long, Boolean> hasPermission = iAuditNodeInstService.isHasPermission(com.youming.youche.conts.AuditConsts.AUDIT_CODE.AUDIT_CODE_VEHICLE, longList, accessToken);
                Boolean flg = hasPermission.get(tenantVehicleRel.getId());
                isAuth=flg ? 1 : 0;
                if(isAuth==1){
                    log.error("?????????????????????????????????????????????????????????????????????");
                    throw new BusinessException("??????????????????????????????????????????????????????????????????!");
                }
                if (null != tenantVehicleRel.getAuthState() && tenantVehicleRel.getAuthState().intValue() != SysStaticDataEnum.AUTH_STATE.AUTH_STATE2) {
                    log.error("?????????????????????????????????????????????????????????????????????");
                    throw new BusinessException("??????????????????????????????????????????????????????????????????!");
                }
            } else {
                beApplyTenantId = -1L;
            }
        }
        Long applyRecordId = applyRecorDto.getApplyRecordId();

        //c??????
        if (beApplyTenantId == null || beApplyTenantId <= 1) {
            if (DataFormat.getLongKey(vehicleDataInfo.getDriverUserId() + "") > 0) {
                Long count1 = applyRecordService.getApplyVehicleCountByDriverUserId(loginInfo.getTenantId(), vehicleClass, vehicleDataInfo.getDriverUserId());
                if (count1 > 0) {
                    log.error("????????????????????????????????????????????????????????????????????????????????????");
                    throw new BusinessException("?????????????????????????????????????????????????????????????????????????????????!");
                }
            }

            if (vehicleClass == VEHICLE_CLASS1 || vehicleClass == VEHICLE_CLASS2 || vehicleClass == VEHICLE_CLASS4) {
                List<TenantVehicleRel> tenantVehicleRelList = tenantVehicleRelMapper.queryTenantVehicleRelDriver(vehicleCode);
                if (tenantVehicleRelList == null || tenantVehicleRelList.isEmpty()) {
                    log.error("???????????????????????????");
                    throw new BusinessException("???????????????????????????!");
                }
            }
        }
        ApplyRecord applyRecord = new ApplyRecord();
        //????????????????????????????????????
        if (sysTenantDef != null) {
            TenantVehicleRel vehicleRel = tenantVehicleRelMapper.tenantVehicleRel(vehicleCode, sysTenantDef.getId());
            if (vehicleRel != null && vehicleRel.getDriverUserId() != null && vehicleRel.getDriverUserId() > 0) {
                applyRecord.setBelongDriverUserId(vehicleRel.getDriverUserId());
                applyRecord = applyRecordService.saveApplyRecord(applyRecord);
            }
        }
        VehicleDataInfoVer vehicleDataInfoVer = vehicleDataInfoVerMapper.getVehicleDataInfoVer(vehicleCode);
        if (null == vehicleDataInfoVer) {
            vehicleDataInfoVer = new VehicleDataInfoVer();
            BeanUtil.copyProperties(vehicleDataInfo, vehicleDataInfoVer);
            // ???????????????(??????????????????)
            vehicleDataInfoVer.setAdrivingLicenseCopy(vehicleDataInfo.getAdriverLicenseCopy());
            vehicleDataInfoVer.setOperCerti(vehicleDataInfo.getOperCerti());// ?????????
            vehicleDataInfoVer.setOperCertiId(vehicleDataInfo.getOperCertiId());//???????????????ID(?????????)
            vehicleDataInfoVer.setVerState(VER_STATE_Y);
            vehicleDataInfoVer.setVehicleCode(vehicleDataInfo.getId());
            vehicleDataInfoVer.setCreateTime(LocalDateTime.now());
            vehicleDataInfoVerMapper.insert(vehicleDataInfoVer);
        }
        //????????????
        if (applyRecordId != null && applyRecordId > 0) {
            String auditRemark = applyRecorDto.getAuditRemark();
            String applyRemark = applyRecorDto.getApplyRemark();
            Long applyFileId = applyRecorDto.getApplyFileId();
            Long applyDriverUserId = applyRecorDto.getApplyDriverUserId();
            String drivingLicense = applyRecorDto.getDrivingLicense();
            String operCerti = applyRecorDto.getOperCerti();
            String billReceiverMobile = applyRecorDto.getBillReceiverMobile();
            String billReceiverName = applyRecorDto.getBillReceiverName();
            Long billReceiverUserId = applyRecorDto.getBillReceiverUserId();
            ApplyRecord oldApplyRecord = applyRecordService.getById(applyRecordId);
            BeanUtil.copyProperties(oldApplyRecord, applyRecord);
            applyRecord.setAuditRemark(null);
            applyRecord.setAuditDate(null);
            if (applyState == SysStaticDataEnum.APPLY_STATE.APPLY_STATE0) {
                applyRecord.setApplyTenantId(loginInfo.getTenantId());
                applyRecord.setApplyType(SysStaticDataEnum.APPLY_TYPE.VEHICLE);
                applyRecord.setCreateDate(LocalDateTime.now());
//				applyRecord.setAuditRemark(auditRemark);
                applyRecord.setApplyRemark(applyRemark);
                applyRecord.setApplyFileId(applyFileId);
                applyRecord.setApplyDriverUserId(applyDriverUserId);
                applyRecord.setDrivingLicense(drivingLicense);
                applyRecord.setOperCerti(operCerti);
                applyRecord.setBillReceiverMobile(billReceiverMobile);
                applyRecord.setBillReceiverName(billReceiverName);
                applyRecord.setBillReceiverUserId(billReceiverUserId);
            }
            applyRecord.setState(applyState);
            applyRecord.setHisId(vehicleDataInfoVer.getId());
            applyRecord = applyRecordService.saveApplyRecord(applyRecord);
            oldApplyRecord.setNewApplyId(applyRecord.getId());
            applyRecordService.updateById(oldApplyRecord);
            beApplyTenantId = applyRecord.getBeApplyTenantId();
        } else {
            BeanUtils.copyProperties(applyRecorDto, applyRecord);
            applyRecord.setApplyType(SysStaticDataEnum.APPLY_TYPE.VEHICLE);
            applyRecord.setApplyVehicleClass(vehicleClass);
            applyRecord.setApplyDriverUserId(applyRecorDto.getApplyDriverUserId()); // ??????id
            applyRecord.setBusiId(vehicleCode);
            applyRecord.setApplyTenantId(loginInfo.getTenantId());
            applyRecord.setBeApplyTenantId(beApplyTenantId);
            applyRecord.setState(applyState);
            applyRecord.setCreateDate(LocalDateTime.now());
            applyRecord.setHisId(vehicleDataInfoVer.getId());
            applyRecord.setOperCerti(vehicleDataInfoVer.getOperCerti());
            applyRecord.setDrivingLicense(vehicleDataInfoVer.getDrivingLicense());
            applyRecord = applyRecordService.saveApplyRecord(applyRecord);
        }

        //?????????
        // logSV.saveSysOperLog(SysOperLogConst.BusiCode.Vehicle, applyRecord.getId(), SysOperLogConst.OperType.Add, "?????????????????????,??????:" + applyRecord.getApplyVehicleClass() + ",vehicleCode:" + vehicleCode);
        saveSysOperLog(SysOperLogConst.BusiCode.Vehicle, applyRecord.getId(), SysOperLogConst.OperType.Add, "?????????????????????,??????:" + applyRecord.getApplyVehicleClass() + ",vehicleCode:" + vehicleCode, accessToken);
        //??????????????????????????????????????????
        if (applyRecord.getApplyVehicleClass() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS5) {
            Map inMap = new HashMap();
            // TODO: 2022/3/23 ??????????????????
            this.sucessVehicle(applyRecord.getId(), "????????????????????????", inMap, loginInfo, accessToken);
            return true;
        }
        //?????????????????????
        String tenantName = sysTenantDefService.getTenantName(loginInfo.getTenantId());

        if (null != sysTenantDef && sysTenantDef.getState().equals(SysStaticDataEnum.SYS_STATE_DESC.STATE_YES)) {

            if (sysTenantDef.getAdminUser() == null || sysTenantDef.getAdminUser() < 0) {
                log.error("????????????????????????????????????????????????????????????");
                throw new BusinessException("????????????????????????????????????????????????????????????!");
            }
            UserDataInfo dataInfo = userDataInfoRecordMapper.selectById(sysTenantDef.getAdminUser());
            if (null != dataInfo && StringUtils.isNotBlank(dataInfo.getMobilePhone())) {
                if (sysTenantDef != null && StringUtils.isNotBlank(sysTenantDef.getLinkPhone())) {
//                    //????????????????????????
                    Map map = new HashMap();
                    map.put("userName", dataInfo.getLinkman());
                    map.put("tenantName", tenantName);
                    map.put("Vehicle", vehicleDataInfo.getPlateNumber());

                    SysSmsSend sysSmsSend = new SysSmsSend();
                    sysSmsSend.setUserId(loginInfo.getUserInfoId());
                    sysSmsSend.setBillId(sysTenantDef.getLinkPhone());
                    sysSmsSend.setSmsType(com.youming.youche.conts.SysStaticDataEnum.SMS_TYPE.INVITE_INFO);
                    sysSmsSend.setTemplateId(com.youming.youche.conts.EnumConsts.SmsTemplate.ADD_DRIVER_HAVE_TENANT_VEHICLE);
                    sysSmsSend.setObjType(String.valueOf(com.youming.youche.conts.SysStaticDataEnum.OBJ_TYPE.INVITE));
                    sysSmsSend.setObjId(String.valueOf(applyRecord.getId()));
                    sysSmsSend.setParamMap(map);
                    sysSmsSendService.sendSms(sysSmsSend);
                    //?????????????????????
                }
            } else {
                log.error("??????????????????????????????????????????????????????????????????????????????");
                throw new BusinessException("??????????????????????????????????????????????????????????????????????????????!");
            }

            //??????????????????
            if (beApplyTenantId > 1) {
                Map inMap = new HashMap();
                try {
                    boolean bool = iAuditService.startProcess(AUDIT_CODE_APPLY_VEHICLE, applyRecord.getId(), SysOperLogConst.BusiCode.VehicleReg, inMap, accessToken, beApplyTenantId);
                    if (!bool) {
                        log.error("???????????????????????????");
                        throw new BusinessException("????????????????????????!");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


        } else if (sysTenantDef == null || sysTenantDef.getState().equals(SysStaticDataEnum.SYS_STATE_DESC.STATE_NO)) {//????????????????????????

            Long belongDriverUserId = applyRecorDto.getBelongDriverUserId();//??????????????????????????????
            //???????????????????????????
            UserDataInfo userDataInfo = null;
            if (belongDriverUserId != null && belongDriverUserId > 0) {
                //??????????????????????????????
                userDataInfo = userDataInfoRecordMapper.selectById(belongDriverUserId);
            } else {
                List<TenantVehicleRel> tenantVehicleList = tenantVehicleRelMapper.getTenantVehicleRel(vehicleCode);
                for (TenantVehicleRel tenantVehicleRel : tenantVehicleList) {
                    if (tenantVehicleRel.getDriverUserId() != null && tenantVehicleRel.getDriverUserId() > 0) {
                        userDataInfo = userDataInfoRecordMapper.selectById(tenantVehicleRel.getDriverUserId());
                        belongDriverUserId = userDataInfo.getId();
                        break;
                    }
                }
            }
            applyRecord.setBelongDriverUserId(belongDriverUserId);
            applyRecordService.saveOrUpdate(applyRecord);
            if (null != userDataInfo) {
                //?????????????????????????????????????????????
                Map map = new HashMap();
                map.put("userName", userDataInfo.getLinkman());
                map.put("tenantName", tenantName);
                map.put("Vehicle", vehicleDataInfo.getPlateNumber());
                //????????????????????????
                SysSmsSend sysSmsSend = new SysSmsSend();
                sysSmsSend.setUserId(loginInfo.getUserInfoId());
                sysSmsSend.setBillId(userDataInfo.getMobilePhone());
                sysSmsSend.setSmsType(com.youming.youche.conts.SysStaticDataEnum.SMS_TYPE.INVITE_INFO);
                sysSmsSend.setTemplateId(com.youming.youche.conts.EnumConsts.SmsTemplate.ADD_DRIVER_NO_TENANT_VEHICLE);
                sysSmsSend.setObjType(String.valueOf(com.youming.youche.conts.SysStaticDataEnum.OBJ_TYPE.INVITE));
                sysSmsSend.setObjId(String.valueOf(applyRecord.getId()));
                sysSmsSend.setParamMap(map);
                sysSmsSendService.sendSms(sysSmsSend);
            } else if (null == userDataInfo && null != sysTenantDef && sysTenantDef.getState().equals(SysStaticDataEnum.SYS_STATE_DESC.STATE_NO)) {
                //?????????????????????????????????????????????
                //??????????????????
//                /*IAuditCallBackIntf successTF = new VehicleAuditCallBackTF();
//                successTF.sucess(applyRecord.getId(),"????????????????????????",null);*/
                this.sucessVehicle(applyRecord.getId(), "????????????????????????", null, loginInfo, accessToken);
            }

        }
        return true;
    }


    @Resource
    private TenantTrailerRelMapper tenantTrailerRelMapper;

    @Resource
    private TenantTrailerRelVerMapper tenantTrailerRelVerMapper;

    @Transactional(rollbackFor = {RuntimeException.class, Error.class})
    @Override
    public Long remove(Long id, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        TenantTrailerRel tenantTrailerRel = tenantTrailerRelMapper.getTenantTrailerRelByTrailerId(id, loginInfo.getTenantId());
        TenantTrailerRelVer trailerRelVer = new TenantTrailerRelVer();
        BeanUtil.copyProperties(tenantTrailerRel, trailerRelVer);
        trailerRelVer.setDeleteFlag(9);
        trailerRelVer.setRelId(tenantTrailerRel.getId());
        trailerRelVer.setId(null);
        tenantTrailerRelMapper.deleteById(tenantTrailerRel.getId());
        int insert = tenantTrailerRelVerMapper.insert(trailerRelVer);
        if (insert == 1) {
            return trailerRelVer.getRelId();
        }
        return 0L;
    }

    @Override
    public void sucess(Long busiId, String desc, Map paramsMap, String token) throws BusinessException {
        paramsMap.put("busiId", busiId);
        paramsMap.put("auditContent", desc);
        paramsMap.put("authState", SysStaticDataEnum.AUTH_STATE.AUTH_STATE2);
        doAuditInfo(paramsMap, token);
    }

    @Override
    public void fail(Long busiId, String desc, Map paramsMap, String token) throws BusinessException {
        paramsMap.put("busiId", busiId);
        paramsMap.put("auditContent", desc);
        paramsMap.put("authState", SysStaticDataEnum.AUTH_STATE.AUTH_STATE3);
        doAuditInfo(paramsMap, token);

    }


    public String doAuditInfo(Map<String, Object> inParam, String token) throws BusinessException {
        int auditState = DataFormat.getIntKey(inParam, "authState");
        String remark = DataFormat.getStringKey(inParam, "auditContent");
        //????????????
        if (SysStaticDataEnum.AUTH_STATE.AUTH_STATE2 == auditState) {
            auditSuccess(inParam, token);
        } else {
            auditFail(inParam, token);
        }
        return "Y";
    }

    public void auditSuccess(Map<String, Object> inParam, String token) throws BusinessException {
        long relId = DataFormat.getLongKey(inParam, "busiId");
        LoginInfo loginInfo = iUserDataInfoRecordService.getLoginInfoByAccessToken(token);
        TenantVehicleRel tenantVehicleRelTmp = iTenantVehicleRelService.getById(relId);
//        TenantVehicleRel tenantVehicleRelTmp = iTenantVehicleRelService.getTenantVehicleRel(relId, loginInfo.getTenantId());

        //????????????????????????????????????????????????
        if (null == tenantVehicleRelTmp) {
            throw new BusinessException("?????????????????????????????????????????????");
        }

        Integer vehicleClassTmp = tenantVehicleRelTmp.getVehicleClass();
        Long vehicleCode = tenantVehicleRelTmp.getVehicleCode();
        if (null == vehicleCode) {
            vehicleCode = -1L;
        }
        List<VehicleDataInfoVer> vehicleDataInfoList = iVehicleDataInfoVerService.getVehicleObjectVer(vehicleCode, null);
        VehicleDataInfoVer vehicleDataInfoVer = null;
        if (null != vehicleDataInfoList && vehicleDataInfoList.size() > 0) {
            vehicleDataInfoVer = vehicleDataInfoList.get(0);
        }
        VehicleDataInfo vehicleDataInfoTmp = getById(vehicleCode);
        if (null == vehicleDataInfoTmp) {
            throw new BusinessException("????????????????????????");
        }
        if (vehicleDataInfoTmp != null && vehicleDataInfoVer != null) {
            if (!vehicleDataInfoTmp.getPlateNumber().equals(vehicleDataInfoVer.getPlateNumber())) {
                Long aLong = orderInfoMapper.queryVehicleOrderInfoIn(vehicleDataInfoTmp.getPlateNumber());
                if (aLong != null) {
                    throw new BusinessException("?????????????????????,???????????????????????????");
                }
                vehicleDataInfoTmp.setPlateNumber(vehicleDataInfoVer.getPlateNumber());
            }
        }
        List<TenantVehicleRelVer> tenantVehicleRelVerList = iTenantVehicleRelVerService.getVehicleObjectVer(tenantVehicleRelTmp.getVehicleCode(), loginInfo.getTenantId());
        Integer vehicleClass = 0;
        if (tenantVehicleRelVerList != null && tenantVehicleRelVerList.size() > 0) {
            TenantVehicleRelVer tenantVehicleRelVer = tenantVehicleRelVerList.get(0);
            Long driverUserId = tenantVehicleRelVer.getDriverUserId();
            //??????????????????????????????????????????????????????
            TenantUserRel tmp = null;
            if (null != driverUserId && driverUserId > 0) {
                tmp = iTenantUserRelService.getAllTenantUserRelByUserId(driverUserId, loginInfo.getTenantId());
//				if (null == tmp) {
//					throw new BusinessException("?????????????????????????????????");
//				}
                if (tmp != null && null != tmp.getState() && tmp.getState() == SysStaticDataEnum.SYS_USER_STATE.AUDIT_NOT) {
                    throw new BusinessException("???????????????????????????");
                }
            }
            if (tenantVehicleRelVer.getRelId().intValue() != tenantVehicleRelTmp.getId().intValue()) {
                throw new BusinessException("????????????????????????????????????????????????????????????");
            }
            vehicleClass = tenantVehicleRelVer.getVehicleClass();
            if (vehicleClass == VEHICLE_CLASS2 || vehicleClass == VEHICLE_CLASS4) {

                if (tenantVehicleRelVer.getBillReceiverMobile() == null || org.apache.commons.lang.StringUtils.isEmpty(tenantVehicleRelVer.getBillReceiverMobile())) {
                    throw new BusinessException("???????????????????????????????????????????????????????????????");
                }

                //?????????????????????????????????????????????????????????????????????
                if (tenantVehicleRelVer.getBillReceiverUserId() == null || tenantVehicleRelVer.getBillReceiverUserId() < 0) {
                    UserReceiverInfo receiverInfo = iUserReceiverInfoService.initUserReceiverInfo(tenantVehicleRelVer.getBillReceiverMobile(), tenantVehicleRelVer.getBillReceiverName(), token);
                    tenantVehicleRelVer.setBillReceiverUserId(receiverInfo.getUserId());
                }
            }


            if (tenantVehicleRelTmp.getVehicleClass() == VEHICLE_CLASS1 && tenantVehicleRelVer.getVehicleClass() != VEHICLE_CLASS1) {
                //?????????????????????????????????
                removeVehicleAllRelTenant(tenantVehicleRelVer.getVehicleCode(), true, token);

                baseMapper.doUpdateVehicleObjectLine(tenantVehicleRelVer.getVehicleCode());//??????????????????
                baseMapper.doUpdateVehicleLineRelByVehicleCode(tenantVehicleRelVer.getVehicleCode());//????????????????????????
                baseMapper.doUpdateVehicleLineRelVerByVehicleCode(tenantVehicleRelVer.getVehicleCode());//????????????????????????


                //????????????
                removeApplyRecord(vehicleCode);

                //????????????????????????
                TenantVehicleRel newTenantVehicleRel = new TenantVehicleRel();
                BeanUtil.copyProperties(tenantVehicleRelVer, newTenantVehicleRel);
                newTenantVehicleRel.setId(null);
                newTenantVehicleRel.setAuthState(AUTH_STATE2);
                newTenantVehicleRel.setIsAuth(IS_AUTH0);
                iTenantVehicleRelService.save(newTenantVehicleRel);
                tenantVehicleRelVer.setIsAuthSucc(SysStaticDataEnum.IS_AUTH_SUCC.YES_1);
                tenantVehicleRelVer.setVerState(VER_STATE_Y);
                tenantVehicleRelVer.setRelId(newTenantVehicleRel.getId());
                tenantVehicleRelVer.setUpdateDate(LocalDateTime.now());
                tenantVehicleRelVer.setUpdateTime(LocalDateTime.now());
                iTenantVehicleRelVerService.saveOrUpdate(tenantVehicleRelVer);
//                saveSysOperLog(SysOperLogConst.BusiCode.Vehicle, tenantVehicleRelVer.getVehicleCode(), SysOperLogConst.OperType.Audit, "??????(??????????????????)", loginInfo);
                saveSysOperLog(SysOperLogConst.BusiCode.Vehicle, tenantVehicleRelVer.getRelId(), SysOperLogConst.OperType.Audit, "??????(??????????????????)", loginInfo);
            } else {
                if (vehicleClass == VEHICLE_CLASS1 && null != tenantVehicleRelTmp.getShareFlg() && tenantVehicleRelTmp.getShareFlg() == 0) {
                    baseMapper.doDelVehicleOrderPositionInfo(tenantVehicleRelTmp.getVehicleCode());
                }
                LocalDateTime oriCreateDate = tenantVehicleRelTmp.getCreateTime();//??????????????????????????????
                BeanUtil.copyProperties(tenantVehicleRelVer, tenantVehicleRelTmp);
                tenantVehicleRelTmp.setId(tenantVehicleRelVer.getRelId());
                tenantVehicleRelTmp.setLoadEmptyOilCost(tenantVehicleRelVer.getLoadEmptyPilCost());
                tenantVehicleRelTmp.setLoadFullOilCost(tenantVehicleRelVer.getLoadFullPilCost());
                tenantVehicleRelTmp.setCreateTime(oriCreateDate);
                tenantVehicleRelTmp.setAuthState(SysStaticDataEnum.AUTH_STATE.AUTH_STATE2);
                tenantVehicleRelTmp.setIsAuth(SysStaticDataEnum.IS_AUTH.IS_AUTH0);
                tenantVehicleRelTmp.setIsUseCarOilCost(tenantVehicleRelVer.getIsUserCarOilCost());
                tenantVehicleRelVer.setIsAuthSucc(SysStaticDataEnum.IS_AUTH_SUCC.YES_1);
                tenantVehicleRelVer.setUpdateDate(LocalDateTime.now());
                iTenantVehicleRelService.saveOrUpdate(tenantVehicleRelTmp);
                iTenantVehicleRelVerService.saveOrUpdate(tenantVehicleRelVer);
            }

        }

        //????????????????????????????????????
        if (null != vehicleClassTmp && vehicleClassTmp.intValue() == VEHICLE_CLASS1 && null != vehicleClass && vehicleClass.intValue() != VEHICLE_CLASS1) {
            vehicleDataInfoTmp.setTenantId(null);
        }

        //????????????????????????
        if (vehicleDataInfoVer != null) {
            BeanUtil.copyProperties(vehicleDataInfoVer, vehicleDataInfoTmp);
            vehicleDataInfoTmp.setAuthState(SysStaticDataEnum.AUTH_STATE.AUTH_STATE2);
            vehicleDataInfoTmp.setIsAuth(IS_AUTH0);
            vehicleDataInfoTmp.setId(vehicleDataInfoVer.getVehicleCode());
            vehicleDataInfoTmp.setAdriverLicenseCopy(vehicleDataInfoVer.getAdrivingLicenseCopy());
            vehicleDataInfoVer.setIsAuthSucc(IS_AUTH1);
            saveOrUpdate(vehicleDataInfoTmp);
            iVehicleDataInfoVerService.saveOrUpdate(vehicleDataInfoVer);

            //??????????????????????????????????????????????????????????????????????????????
            if (vehicleDataInfoVer.getDriverUserId() != null && iTenantUserRelService.getCarUserType(vehicleDataInfoVer.getDriverUserId(), loginInfo.getTenantId()) == null) {

                addDriverToTenant(vehicleDataInfoVer.getDriverUserId(), loginInfo.getTenantId(), VEHICLE_CLASS5, token);
            }

            if (vehicleDataInfoVer.getCopilotDriverId() != null && iTenantUserRelService.getCarUserType(vehicleDataInfoVer.getCopilotDriverId(), loginInfo.getTenantId()) == null) {
                addDriverToTenant(vehicleDataInfoVer.getCopilotDriverId(), loginInfo.getTenantId(), VEHICLE_CLASS5, token);
            }

            if (vehicleDataInfoVer.getFollowDriverId() != null && iTenantUserRelService.getCarUserType(vehicleDataInfoVer.getFollowDriverId(), loginInfo.getTenantId()) == null) {
                addDriverToTenant(vehicleDataInfoVer.getFollowDriverId(), loginInfo.getTenantId(), VEHICLE_CLASS5, token);
            }

        }


        //?????????????????????
        QueryWrapper<VehicleObjectLineVer> queryWrapper1 = new QueryWrapper<>();
        queryWrapper1.eq("vehicle_code", vehicleCode).eq("ver_state", VER_STATE_Y).orderByDesc("id");
        List<VehicleObjectLineVer> vehicleObjectLineVerList = vehicleObjectLineVerMapper.selectList(queryWrapper1);
        if (null != vehicleObjectLineVerList && vehicleObjectLineVerList.size() > 0) {
            for (int i = 0; i < vehicleObjectLineVerList.size(); i++) {
                VehicleObjectLineVer vehicleObjectLineVer = vehicleObjectLineVerList.get(i);
                if (vehicleObjectLineVer.getId() != null && vehicleObjectLineVer.getId() > 0) {
                    VehicleObjectLine vehicleObjectLine = vehicleObjectLineMapper.selectById(vehicleObjectLineVer.getHisId());
                    if (vehicleObjectLine != null) {
                        // id ??????????????????
                        BeanUtil.copyProperties(vehicleObjectLineVer, vehicleObjectLine, new String[]{"id", "create_time", "update_time"});
                        vehicleObjectLine.setSourceProvince(vehicleObjectLineVer.getSourceProvince());
                        vehicleObjectLine.setSourceRegion(vehicleObjectLineVer.getSourceRegion());
                        vehicleObjectLine.setSourceCounty(vehicleObjectLineVer.getSourceCounty());
                        vehicleObjectLine.setDesProvince(vehicleObjectLineVer.getDesProvince());
                        vehicleObjectLine.setDesRegion(vehicleObjectLineVer.getDesRegion());
                        vehicleObjectLine.setDesCounty(vehicleObjectLineVer.getDesCounty());
                        vehicleObjectLine.setId(vehicleObjectLineVer.getHisId());
                        iVehicleObjectLineService.saveOrUpdate(vehicleObjectLine);
                    }
                }
            }
        } else {
            vehicleObjectLineService.remove(vehicleCode, loginInfo.getTenantId());
        }


        if (vehicleClass.intValue() != SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS5) {

            //????????????
            baseMapper.doUpdateVehicleLineRelByVehicleCode(vehicleCode);
            QueryWrapper<VehicleLineRelVer> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("vehicle_code", vehicleCode)
                    .eq("ver_state", SysStaticDataEnum.VER_STATE.VER_STATE_Y)
                    .orderByDesc("id");
            List<VehicleLineRelVer> lineRelList = vehicleLineRelVerMapper.selectList(queryWrapper);
            if (lineRelList != null && lineRelList.size() > 0) {
                for (int i = 0; i < lineRelList.size(); i++) {
                    VehicleLineRelVer lineRelVer = lineRelList.get(i);
                    VehicleLineRel vehicleLineRel = new VehicleLineRel();
                    BeanUtil.copyProperties(lineRelVer, vehicleLineRel);
                    vehicleLineRel.setId(lineRelVer.getRelId());
//                    lineRelVer.setVerState(VER_STATE_N);
                    lineRelVer.setIsAuthSucc(VER_STATE_Y);
                    iVehicleLineRelService.saveOrUpdate(vehicleLineRel);
                    if (lineRelVer.getRelId() == null || lineRelVer.getRelId() <= 0) {
                        lineRelVer.setRelId(vehicleLineRel.getId());
                    }
                    iVehicleLineRelVerService.saveOrUpdate(lineRelVer);
                }
            }

            //??????????????????
            QueryWrapper<TenantVehicleCostRelVer> queryWrapper2 = new QueryWrapper<>();
            queryWrapper2.eq("vehicle_Code", vehicleCode).eq("tenant_id", loginInfo.getTenantId()).eq("ver_state", VER_STATE_Y).orderByDesc("id");
            List<TenantVehicleCostRelVer> tenantVehicleCostRelVerList = tenantVehicleCostRelVerMapper.selectList(queryWrapper2);
            if (null != tenantVehicleCostRelVerList && tenantVehicleCostRelVerList.size() > 0) {
                TenantVehicleCostRelVer tenantVehicleCostRelVer = tenantVehicleCostRelVerList.get(0);
                if (tenantVehicleCostRelVer.getId() != null && tenantVehicleCostRelVer.getId() > 0) {
                    TenantVehicleCostRel tenantVehicleCostRel = iTenantVehicleCostRelService.selectByRelId(tenantVehicleCostRelVer.getRelId());
                    if (tenantVehicleCostRel != null) {
                        Long id = tenantVehicleCostRel.getId();
                        BeanUtils.copyProperties(tenantVehicleCostRelVer, tenantVehicleCostRel);
                        tenantVehicleCostRel.setId(id);
                        iTenantVehicleCostRelService.saveOrUpdate(tenantVehicleCostRel);
                    }
                }
            }

            QueryWrapper<TenantVehicleCertRelVer> queryWrapper3 = new QueryWrapper<>();
            queryWrapper3.eq("vehicle_Code", vehicleCode).eq("tenant_id", loginInfo.getTenantId()).eq("ver_state", VER_STATE_Y).orderByDesc("id");
            List<TenantVehicleCertRelVer> tenantVehicleCertRelVerList = iTenantVehicleCertRelVerService.list(queryWrapper3);
            if (null != tenantVehicleCertRelVerList && tenantVehicleCertRelVerList.size() > 0) {
                TenantVehicleCertRelVer tenantVehicleCertRelVer = tenantVehicleCertRelVerList.get(0);
                if (tenantVehicleCertRelVer.getId() != null && tenantVehicleCertRelVer.getId() > 0) {
                    TenantVehicleCertRel tenantVehicleCertRel = iTenantVehicleCertRelService.selectByRelId(tenantVehicleCertRelVer.getRelId());
                    if (tenantVehicleCertRel != null) {
                        Long id = tenantVehicleCertRel.getId();
                        BeanUtil.copyProperties(tenantVehicleCertRelVer, tenantVehicleCertRel);
                        tenantVehicleCertRel.setId(id);
                        iTenantVehicleCertRelService.saveOrUpdate(tenantVehicleCertRel);
                    }
                }
            }
        }

        QueryWrapper<TenantVehicleRel> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("vehicle_code", vehicleCode).eq("vehicle_class", VEHICLE_CLASS1);
        List<TenantVehicleRel> vehicleRelList = iTenantVehicleRelService.list(queryWrapper);
        if (vehicleRelList != null && vehicleRelList.size() > 0) {
            VehicleDataInfo vehicleInfo = getById(tenantVehicleRelTmp.getVehicleCode());
            TenantVehicleRel vehicleRel = vehicleRelList.get(0);
            vehicleInfo.setTenantId(vehicleRel.getTenantId());
            updateById(vehicleInfo);
        }

        //???????????????????????????
        doUpdateVehicleCompleteness(tenantVehicleRelTmp.getVehicleCode(), loginInfo.getTenantId());

        //?????????????????????
        addPtTenantVehicleRel(tenantVehicleRelTmp.getVehicleCode());

    }

    public void auditFail(Map<String, Object> inParam, String token) throws BusinessException {
        checkDataIsCanAuth(inParam, IS_AUTH0);
        LoginInfo loginInfo = iUserDataInfoRecordService.getLoginInfoByAccessToken(token);
        long relId = DataFormat.getLongKey(inParam, "busiId");
        String remark = DataFormat.getStringKey(inParam, "auditContent");
        TenantVehicleRel tenantVehicleRel = iTenantVehicleRelService.getById(relId);
        if (null != tenantVehicleRel) {
			/*VehicleDataInfo vehicleDataInfo = vehicleSV.getObjectById(VehicleDataInfo.class,tenantVehicleRel.getVehicleCode());
			if(null != vehicleDataInfo){
				vehicleDataInfo.setIsAuth(IS_AUTH0);
			}*/
            tenantVehicleRel.setAuditContent(remark);
            tenantVehicleRel.setIsAuth(IS_AUTH0);
            iTenantVehicleRelService.updateById(tenantVehicleRel);
        }
        VehicleDataInfo vehicleDataInfo = getById(tenantVehicleRel.getVehicleCode());
        if (vehicleDataInfo != null) {
            vehicleDataInfo.setIsAuth(IS_AUTH0);
            iTenantVehicleRelService.updateById(tenantVehicleRel);
        }
    }

    public void checkDataIsCanAuth(Map<String, Object> inParam, int isAuthSucc) {

        long vehicleDataInfoVerHisId = DataFormat.getLongKey(inParam, "vehicleDataInfoVerHisId");
        String vehicleObjectLineVerHisIds = DataFormat.getStringKey(inParam, "vehicleObjectLineVerHisIds");//????????????ID
        String vehicleLineRelVerHisIds = DataFormat.getStringKey(inParam, "vehicleLineRelVerHisIds");//????????????ID
        long tenantVehicleRelVerHisId = DataFormat.getLongKey(inParam, "tenantVehicleRelVerHisId");//????????????????????????
        long tenantVehicleCostRelVerHisId = DataFormat.getLongKey(inParam, "tenantVehicleCostRelVerHisId");
        long tenantVehicleCertRelVerHisId = DataFormat.getLongKey(inParam, "tenantVehicleCertRelVerHisId");


        if (vehicleDataInfoVerHisId > 0) {
            VehicleDataInfoVer vehicleDataInfoVer = iVehicleDataInfoVerService.getById(vehicleDataInfoVerHisId);
            if (vehicleDataInfoVer == null || vehicleDataInfoVer.getVerState().equals(VER_STATE_N)) {
                throw new BusinessException("???????????????????????????????????????????????????????????????");
            }
            vehicleDataInfoVer.setIsAuthSucc(isAuthSucc);
            iVehicleDataInfoVerService.updateById(vehicleDataInfoVer);
        }

        if (tenantVehicleRelVerHisId > 0) {
            TenantVehicleRelVer tenantVehicleRelVer = iTenantVehicleRelVerService.getById(tenantVehicleRelVerHisId);
            if (tenantVehicleRelVer == null || tenantVehicleRelVer.getVerState().equals(VER_STATE_N)) {
                throw new BusinessException("???????????????????????????????????????????????????????????????");
            }
            tenantVehicleRelVer.setIsAuthSucc(isAuthSucc);
            iTenantVehicleRelVerService.updateById(tenantVehicleRelVer);
        }

        if (!"".equals(vehicleObjectLineVerHisIds)) {

            String[] vehicleObjectLineVerHisIdArray = vehicleObjectLineVerHisIds.split(",");

            for (String id : vehicleObjectLineVerHisIdArray) {
                VehicleObjectLineVer vehicleObjectLineVer = iVehicleObjectLineVerService.getById(Long.parseLong(id));
                vehicleObjectLineVer.setIsAuthSucc(isAuthSucc);
                iVehicleObjectLineVerService.updateById(vehicleObjectLineVer);
            }
        }

        if (!"".equals(vehicleLineRelVerHisIds)) {
            String[] vehicleLineRelVerHisIdArray = vehicleLineRelVerHisIds.split(",");

            for (String id : vehicleLineRelVerHisIdArray) {
                VehicleLineRelVer vehicleLineRelVer = iVehicleLineRelVerService.getById(Long.parseLong(id));
                vehicleLineRelVer.setIsAuthSucc(isAuthSucc);
                iVehicleLineRelVerService.updateById(vehicleLineRelVer);
            }
        }


        if (tenantVehicleCostRelVerHisId > 0) {
            TenantVehicleCostRelVer tenantVehicleCostRelVer = iTenantVehicleCostRelVerService.getById(tenantVehicleCostRelVerHisId);

            if (tenantVehicleCostRelVer != null) {
                tenantVehicleCostRelVer.setIsAuthSucc(isAuthSucc);
                iTenantVehicleCostRelVerService.updateById(tenantVehicleCostRelVer);
            }
        }

        if (tenantVehicleCertRelVerHisId > 0) {
            TenantVehicleCertRelVer tenantVehicleCertRelVer = iTenantVehicleCertRelVerService.getById(tenantVehicleCertRelVerHisId);

            if (tenantVehicleCertRelVer != null) {
                tenantVehicleCertRelVer.setIsAuthSucc(isAuthSucc);
                iTenantVehicleCertRelVerService.updateById(tenantVehicleCertRelVer);
            }
        }
    }

    public void addDriverToTenant(long userId, long tenantId, int carUserType, String token) throws BusinessException {

        TenantUserRel tenantUserRel = new TenantUserRel();
        tenantUserRel.setUserId(userId);
        tenantUserRel.setTenantId(tenantId);
        tenantUserRel.setState(SysStaticDataEnum.AUTH_STATE.AUTH_STATE1);
        tenantUserRel.setCarUserType(carUserType);
        tenantUserRel.setHasVer(EnumConsts.HAS_VER_STATE.HAS_VER_NO);
        tenantUserRel.setCreateDate(LocalDateTime.now());
        tenantUserRel.setCreateTime(LocalDateTime.now());
        iTenantUserRelService.saveOrUpdate(tenantUserRel);
        TenantUserRelVer tenantUserRelVer = new TenantUserRelVer();
        BeanUtil.copyProperties(tenantUserRel, tenantUserRelVer);
        tenantUserRelVer.setRelId(tenantUserRel.getId());
        tenantUserRelVer.setVerState(VER_STATE_Y);
        iTenantUserRelVerService.saveOrUpdate(tenantUserRelVer);
        Map<String, Object> paramMap = new HashMap<String, Object>();
        if (null != tenantUserRelVer) {
            paramMap.put("tenantUserRelId", tenantUserRelVer.getRelId());
            paramMap.put("tenantUserRelCarUserType", tenantUserRelVer.getCarUserType());
        }
        //??????????????????
        boolean boolD = iAuditService.startProcess(AuditConsts.AUDIT_CODE.AUDIT_CODE_USER, tenantUserRel.getId(), SysOperLogConst.BusiCode.Driver, paramMap, token);
        if (!boolD) {
            throw new BusinessException("???????????????????????????");
        }
    }


    public void removeVehicleAllRelTenant(long vehicleCode, boolean isDelOwnType, String token) throws BusinessException {
        if (vehicleCode < 0) {
            throw new BusinessException("????????????");
        }
        QueryWrapper<TenantVehicleRel> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("vehicle_code", vehicleCode);
        List<TenantVehicleRel> list = iTenantVehicleRelService.list(queryWrapper);

        if (null != list && list.size() > 0) {

            //??????????????????????????????????????????
            VehicleDataInfoVer vehicleDataInfoVer = iVehicleDataInfoVerService.doAddVehicleDataInfoVerHis(vehicleCode);
            for (int i = 0; i < list.size(); i++) {
                TenantVehicleRel t = list.get(i);
                if (t.getVehicleClass() == VEHICLE_CLASS1 && !isDelOwnType) {
                    continue;
                }
				/*
				if (null != t.getDriverUserId() && t.getDriverUserId() > 0 && t.getVehicleClass() > VEHICLE_CLASS1) {//??????????????????????????????????????????????????????????????????????????????
					//??????????????????????????????????????????
					doRemoveDriverOnlyOneVehicle(t.getDriverUserId(), t.getTenantId());
				}
				*/
                dissolveCooperationVehicle(t.getId(), vehicleDataInfoVer.getId(), token);
            }
        }
    }

    @Override
    public List<VehicleDriverVo> getVehicleDriver(String plateNumber) {
        List<VehicleDriverVo> vehicleDriver = baseMapper.getVehicleDriver(plateNumber);
        return vehicleDriver;
    }

    @Override
    public UserDataInfoBackVo queryBillReceiverName(String mobile, int flag, String token) {

        LoginInfo loginInfo = loginUtils.get(token);

        if (StringUtils.isBlank(mobile)) {
            throw new BusinessException("??????????????????????????????!");
        }

        if (flag < 0) {
            throw new BusinessException("flag????????????!");
        }
        UserDataInfoBackVo userDataInfoBackVo = new UserDataInfoBackVo();
        userDataInfoBackVo.setCode("T");
        userDataInfoBackVo.setMsg("");
        if (StrUtil.isBlank(mobile)) {
            userDataInfoBackVo.setCode("F");
            userDataInfoBackVo.setMsg("??????????????????????????????!");
        }

        if (flag < 0) {
            userDataInfoBackVo.setCode("F");
            userDataInfoBackVo.setMsg("flag????????????!");
        }

        UserDataInfo userDataInfo = userDataInfoRecordMapper.queryUserInfoByMobile(mobile);
        if (userDataInfo != null) {

            userDataInfoBackVo.setUserId(userDataInfo.getId());
            userDataInfoBackVo.setLinkman(userDataInfo.getLinkman());
            SysTenantDef sysTenantDef = sysTenantDefService.getSysTenantDefByAdminUserId(userDataInfo.getId());
            if (sysTenantDef != null) {
                if (loginInfo.getTenantId().equals(sysTenantDef.getId())) {
                    throw new BusinessException("??????????????????????????????????????????");
                }
                userDataInfoBackVo.setLinkman(sysTenantDef.getName());
                userDataInfoBackVo.setAttachTenantId(sysTenantDef.getId());
                userDataInfoBackVo.setAttachIsAdminUser(1L);
            }
        }
        return userDataInfoBackVo;
    }

    private String getCodeValue(String codeType, String codeName) {
        List<SysStaticData> sysStaticDatas = (List<SysStaticData>) redisUtil.get(com.youming.youche.conts.SysStaticDataEnum.SYS_STATIC_DATA_NAME.concat(codeType));
        if (StrUtil.isEmpty(codeName) || sysStaticDatas == null) {
            return "-1";
        }
        String codeValue = "-1";
        if (sysStaticDatas != null) {
            for (SysStaticData sysStaticData : sysStaticDatas) {
                if (codeName.equals(sysStaticData.getCodeName())) {
                    codeValue = sysStaticData.getCodeValue();
                    break;
                }
            }
        }
        return codeValue;
    }

    public Map toUpdateByImport(List<String> l, String token) throws Exception {
        LoginInfo loginInfo = loginUtils.get(token);
        String VEHICLE_CLASS1 = SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1 + "";//???????????????
        String VEHICLE_CLASS2 = SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS2 + "";//???????????????
        String VEHICLE_CLASS4 = SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS4 + "";//?????????
        String VEHICLE_CLASS3 = SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS5 + "";//???????????????
        String tmpMsg = "";
        Map param = new HashMap();
        boolean isModify = false;

        String plateNumber = ExcelUtils.getExcelValue(l, 0);
        if (org.apache.commons.lang.StringUtils.isBlank(plateNumber)) {
            tmpMsg += "????????????????????????";
        } else {
            Map<String, Object> rtnMap = baseMapper.getVehicleIsRegister(SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1, plateNumber);
            //??????????????????????????????????????????????????????????????????????????????????????????????????????????????????
            //1.????????????????????????????????????
            //2.???????????????map
            //3.??????excel????????????????????????????????????????????????
            //4.??????????????????????????????param??????????????????????????????isModify???????????????????????????
            long vehicleCode = DataFormat.getLongKey(rtnMap, "vehicleCode");
            VehicleInfoDto vehicleInfoV = getAllVehicleInfo(SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1, vehicleCode, null, token);
            if (vehicleInfoV != null) {

                Map<String, Object> vehicleInfo = JsonHelper.parseJSON2Map(JsonHelper.toJson(vehicleInfoV));
                // JSONObject.parseObject(JSONObject.toJSONString(vehicleInfoVo),Map.class);
                //            long relId = DataFormat.getLongKey(param, "relId");//???????????????
                param.putAll(vehicleInfo);
            }
            long relId = DataFormat.getLongKey(param, "relId");//???????????????
            long tenantVehicleCostRelId = DataFormat.getLongKey(param, "tenantVehicleCostRelId");//???????????????????????????
            long tenantVehicleCertRelId = DataFormat.getLongKey(param, "tenantVehicleCertRelId");//??????id
            param.put("tenantVehicleRel.id", relId);
            param.put("tenantVehicleCostRel.id", tenantVehicleCostRelId);
            param.put("tenantVehicleCertRel.id", tenantVehicleCertRelId);
            param.put("isModify", true);

            String vehicleClassStr = DataFormat.getStringKey(param, "vehicleClass");
            String vehicleClassName = ExcelUtils.getExcelValue(l, 1);

            if (org.apache.commons.lang.StringUtils.isBlank(vehicleClassName)) {
                tmpMsg += "????????????????????????";
            } else {
                String vehicleClass = getCodeValue("VEHICLE_CLASS", vehicleClassName);
                if (org.apache.commons.lang.StringUtils.isBlank(vehicleClass)) {
                    tmpMsg += "????????????????????????";
                } else if (org.apache.commons.lang.StringUtils.isNotBlank(vehicleClassStr) && !vehicleClass.equals(vehicleClassStr)) {
                    tmpMsg += "????????????????????????";
                }
            }

            String licenceTypeName = ExcelUtils.getExcelValue(l, 2);
            String licenceType = null;
            if (StrUtil.isNotBlank(licenceTypeName)) {
                isModify = true;
                licenceType = getCodeValue("LICENCE_TYPE", licenceTypeName);
                if (StrUtil.isBlank(licenceType)) {
                    tmpMsg += "????????????????????????";
                }
                param.put("licenceType", licenceType);
            } else {
                licenceType = DataFormat.getStringKey(param, "licenceType");
            }

            if ("1".equals(licenceType)) {
                //???????????????
                String vehicleStatusName = ExcelUtils.getExcelValue(l, 3);

                String vehicleStatusNameOld = DataFormat.getStringKey(param, "vehicleStatusName");
                String vehicleStatus = "";
                if (org.apache.commons.lang.StringUtils.isBlank(vehicleStatusName) && org.apache.commons.lang.StringUtils.isBlank(vehicleStatusNameOld)) {
                    tmpMsg += "??????????????????";
                } else if (org.apache.commons.lang.StringUtils.isNotBlank(vehicleStatusName)) {
                    vehicleStatus = getCodeValue("VEHICLE_STATUS", vehicleStatusName);
                    if (org.apache.commons.lang.StringUtils.isBlank(vehicleStatus)) {
                        tmpMsg += "??????????????????";
                    } else {
                        isModify = true;
                        param.put("vehicleStatus", vehicleStatus);
                    }
                }
                String vehicleLengthName = ExcelUtils.getExcelValue(l, 4);

                String vehicleLengthNameOld = DataFormat.getStringKey(param, "vehicleLengthName");
                if (StrUtil.isBlank(vehicleLengthName) && StrUtil.isBlank(vehicleLengthNameOld)) {
                    tmpMsg += "??????????????????";
                } else if (StrUtil.isNotBlank(vehicleLengthName)) {
                    String vehicleLength = "";
                    if ((SysStaticDataEnum.VEHICLE_STATUS_ENUM.VEHICLE_CAR_BOX.getType() + "").equals(vehicleStatus)) {
                        vehicleLength = getCodeValue("VEHICLE_STATUS_SUBTYPE", vehicleLengthName);
                    } else {
                        vehicleLength = getCodeValue("VEHICLE_LENGTH", vehicleLengthName);
                    }

                    if (StrUtil.isBlank(vehicleLength)) {
                        tmpMsg += "??????????????????";
                    } else {
                        isModify = true;
                        param.put("vehicleLength", vehicleLength);
                    }
                }
                String vehicleLoad = ExcelUtils.getExcelValue(l, 5);

                String vehicleLoadOld = DataFormat.getStringKey(param, "vehicleLoad");

                if (org.apache.commons.lang.StringUtils.isBlank(vehicleLoad) && org.apache.commons.lang.StringUtils.isBlank(vehicleLoadOld)) {
                    tmpMsg += "??????(???)????????????";
                }

                if (org.apache.commons.lang.StringUtils.isNotBlank(vehicleLoad)) {
                    BigDecimal vehicleLoadBig = new BigDecimal(vehicleLoad);
                    if (vehicleLoadBig.compareTo(new BigDecimal(0)) < 0) {
                        tmpMsg += "??????(???)??????????????????";
                    } else {
                        isModify = true;
                        param.put("vehicleLoad", vehicleLoad);
                    }
                }

                String lightGoodsSquare = ExcelUtils.getExcelValue(l, 6);

                String lightGoodsSquareOld = DataFormat.getStringKey(param, "lightGoodsSquare");

                if (org.apache.commons.lang.StringUtils.isBlank(lightGoodsSquare) && org.apache.commons.lang.StringUtils.isBlank(lightGoodsSquareOld)) {
                    tmpMsg += "??????(?????????)????????????";
                }
                if (StrUtil.isNotBlank(lightGoodsSquare)) {
                    isModify = true;
                    param.put("lightGoodsSquare", lightGoodsSquare);
                }

            }

            isModify = ExcelUtils.modifyValue(l, 7, param, "vinNo") || isModify;
            isModify = ExcelUtils.modifyValue(l, 8, param, "engineNo") || isModify;
            isModify = ExcelUtils.modifyValue(l, 9, param, "brandModel") || isModify;
            isModify = ExcelUtils.modifyValue(l, 10, param, "operCerti") || isModify;
            isModify = ExcelUtils.modifyValue(l, 11, param, "drivingLicenseOwner") || isModify;
            isModify = ExcelUtils.modifyValue(l, 12, param, "etcCardNumber") || isModify;
            isModify = ExcelUtils.modifyValue(l, 13, param, "loadEmptyOilCost") || isModify;
            isModify = ExcelUtils.modifyValue(l, 14, param, "loadFullOilCost") || isModify;
            String isUseCarOilCost = ExcelUtils.getExcelValue(l, 15);
            if (org.apache.commons.lang.StringUtils.isNotBlank(isUseCarOilCost)) {
                isModify = true;
                param.put("isUseCarOilCost", "???".equals(isUseCarOilCost) ? 1 : 0);
            }

            String driverUserPhone = ExcelUtils.getExcelValue(l, 16);
            String driverUserName = "";
            if (org.apache.commons.lang.StringUtils.isNotBlank(driverUserPhone)) {
                //????????????
                QueryWrapper<UserDataInfo> userDataInfoQueryWrapper = new QueryWrapper<>();
                userDataInfoQueryWrapper.eq("mobile_phone", driverUserPhone);
                List<UserDataInfo> userDataInfoList = iUserDataInfoRecordService.list(userDataInfoQueryWrapper);
                UserDataInfo userInfo = null;
                if (userDataInfoList != null && userDataInfoList.size() > 0) {
                    userInfo = userDataInfoList.get(0);
                }
                if (userInfo == null) {
                    tmpMsg += "??????????????????????????????";
                } else {
                    QueryWrapper<TenantUserRel> tenantUserRelQueryWrapper = new QueryWrapper<>();
                    tenantUserRelQueryWrapper.eq("user_id", userInfo.getId());
                    tenantUserRelQueryWrapper.eq("tenant_id", loginInfo.getTenantId());
                    List<TenantUserRel> tenantUserRels = iTenantUserRelService.list(tenantUserRelQueryWrapper);
                    TenantUserRel tenantUserRel = null;
                    if (tenantUserRels != null && tenantUserRels.size() > 0) {
                        tenantUserRel = tenantUserRels.get(0);
                    }

                    if (null == tenantUserRel) {
                        tmpMsg += "????????????????????????????????????";
                    } else {
                        isModify = true;
                        param.put("driverUserId", userInfo.getId());
                        driverUserName = userInfo.getLinkman();
                    }
                }
            } else {
                driverUserPhone = DataFormat.getStringKey(param, "driverMobilePhone");
            }

            //?????????????????????
            if (VEHICLE_CLASS2.equals(vehicleClassStr) || VEHICLE_CLASS4.equals(vehicleClassStr)) {

                String billReceiverMobile = ExcelUtils.getExcelValue(l, 38);//????????????????????????
                String billReceiverName = ExcelUtils.getExcelValue(l, 39);//?????????????????????


                if (org.apache.commons.lang.StringUtils.isBlank(billReceiverMobile)) {
                    billReceiverMobile = driverUserPhone;
                }

                if (org.apache.commons.lang.StringUtils.isBlank(billReceiverName)) {
                    billReceiverName = driverUserName;
                }

                param.put("billReceiverMobile", billReceiverMobile);
                param.put("billReceiverName", billReceiverName);

                if (StrUtil.isNotBlank(billReceiverMobile) || org.apache.commons.lang.StringUtils.isBlank(billReceiverName)) {
                    UserDataInfoBackVo userDataInfoBackVo = new UserDataInfoBackVo();
                    if (VEHICLE_CLASS2.equals(vehicleClassStr)) {//?????????????????????????????????
                        userDataInfoBackVo = queryBillReceiverName(billReceiverMobile, 1, token);
                    } else if (VEHICLE_CLASS4.equals(vehicleClassStr)) {//?????????????????????????????????
                        userDataInfoBackVo = queryBillReceiverName(billReceiverMobile, 2, token);
                    }
                    if ("F".equals(userDataInfoBackVo.getCode())) {
                        tmpMsg += userDataInfoBackVo.getMsg();
                    } else {
                        isModify = true;
                        UserDataInfo userInfo = iUserDataInfoRecordService.getUserDataInfoByMoblile(billReceiverMobile, false, token);
                        if (userInfo != null) {
                            param.put("billReceiverUserId", userInfo.getId());
                        }
                    }
                }

            }

            //??????????????????????????????
            if (org.apache.commons.lang.StringUtils.isNotBlank(vehicleClassStr) && (VEHICLE_CLASS1.equals(vehicleClassStr) || VEHICLE_CLASS2.equals(vehicleClassStr) || VEHICLE_CLASS4.equals(vehicleClassStr))) {
                if (VEHICLE_CLASS1.equals(vehicleClassStr)) {

                    String copilotDriverPhone = ExcelUtils.getExcelValue(l, 17);
                    if (org.apache.commons.lang.StringUtils.isNotBlank(driverUserPhone) && org.apache.commons.lang.StringUtils.isNotBlank(copilotDriverPhone) && driverUserPhone.equals(copilotDriverPhone)) {
                        tmpMsg += "???????????????????????????????????????";
                    } else if (org.apache.commons.lang.StringUtils.isNotBlank(copilotDriverPhone)) {
                        //????????????????????????
                        UserDataInfo userInfo = iUserDataInfoRecordService.getUserDataInfoByMoblile(copilotDriverPhone, false, token);
                        if (userInfo == null) {
                            tmpMsg += "????????????????????????";
                        } else {
                            QueryWrapper<TenantUserRel> tenantUserRelQueryWrapper = new QueryWrapper<>();
                            tenantUserRelQueryWrapper.eq("user_id", userInfo.getId());
                            List<TenantUserRel> tenantUserRelList = iTenantUserRelService.list(tenantUserRelQueryWrapper);
                            if (tenantUserRelList != null && tenantUserRelList.size() > 0) {
                                tmpMsg += "????????????????????????????????????????????????";
                            } else {
                                isModify = true;
                                param.put("copilotDriverId", userInfo.getId());
                            }
                        }

                    }

                    String followDriverPhone = ExcelUtils.getExcelValue(l, 18);
                    if (org.apache.commons.lang.StringUtils.isNotBlank(followDriverPhone)) {
                        //????????????
                        UserDataInfo userInfo = iUserDataInfoRecordService.getUserDataInfoByMoblile(followDriverPhone, false, token);
                        if (userInfo == null) {
                            tmpMsg += "?????????????????????????????????";
                        } else {
                            QueryWrapper<TenantUserRel> tenantUserRelQueryWrapper = new QueryWrapper<>();
                            tenantUserRelQueryWrapper.eq("user_id", userInfo.getId());
                            List<TenantUserRel> tenantUserRelList = iTenantUserRelService.list(tenantUserRelQueryWrapper);
                            if (tenantUserRelList != null && tenantUserRelList.size() > 0) {
                                tmpMsg += "???????????????????????????????????????????????????";
                            } else {
                                isModify = true;
                                param.put("followDriverId", userInfo.getId());
                            }
                        }
                    }
                }

                if (VEHICLE_CLASS1.equals(vehicleClassStr) || VEHICLE_CLASS2.equals(vehicleClassStr)) {
                    Long orgId = null;
                    String orgName = ExcelUtils.getExcelValue(l, 19);
                    if (StrUtil.isNotBlank(orgName)) {
                        List<SysOrganize> sysOrganizeList = new ArrayList<>();
                        try {
                            sysOrganizeList = iSysOrganizeService.querySysOrganizeList(loginInfo.getTenantId(), null, null);
                        } catch (RpcException e) {
                            e.printStackTrace();
                        }
                        for (SysOrganize sysOragnize : sysOrganizeList) {
                            if (orgName.equals(sysOragnize.getOrgName())) {
                                orgId = sysOragnize.getId();
                                break;
                            }
                        }
                        if (orgId == null) {
                            tmpMsg += "????????????????????????";
                        } else {
                            isModify = true;
                        }
                        param.put("orgId", orgId);
                    } else {
                        //???????????????????????????parentOrgId = -1???
                        List<SysOrganize> sysOrganizeList = new ArrayList<>();
                        try {
                            sysOrganizeList = iSysOrganizeService.querySysOrganizeList(loginInfo.getTenantId(), -1L, null);
                        } catch (RpcException e) {
                            e.printStackTrace();
                        }
                        if (sysOrganizeList != null && sysOrganizeList.size() > 0) {
                            try {
                                sysOrganizeList = iSysOrganizeService.querySysOrganizeList(loginInfo.getTenantId(), -1L, SysStaticDataEnum.SYS_STATE_DESC.STATE_YES);
                            } catch (RpcException e) {
                                e.printStackTrace();
                            }
                            if (sysOrganizeList != null && sysOrganizeList.size() > 0) {
                                long rootOrgid = sysOrganizeList.get(0).getId();
                                param.put("orgId", rootOrgid);
                            }
                        }

                    }
                    String attachedUserPhone = ExcelUtils.getExcelValue(l, 20);
                    if (StrUtil.isNotBlank(attachedUserPhone)) {
                        StaffDataInfoVo staffDataInfoIn = new StaffDataInfoVo();
                        staffDataInfoIn.setLoginAcct(attachedUserPhone);
                        com.youming.youche.system.domain.UserDataInfo info = userDataInfoService.getPhone(attachedUserPhone);
                        if (info == null) {
                            tmpMsg += "???????????????????????????";
                        } else {
                            isModify = true;
                            param.put("userId", info.getId());
                        }
                    }
                }
                if (VEHICLE_CLASS1.equals(vehicleClassStr)) {
                    String lineCodeRules = ExcelUtils.getExcelValue(l, 21);
                    if (StrUtil.isNotBlank(lineCodeRules)) {
                        String[] lineCodeRuleList = lineCodeRules.split(",");
                        for (int i = 0; i < lineCodeRuleList.length; i++) {
                            for (int j = 1; j < lineCodeRuleList.length; j++) {
                                if (lineCodeRuleList[i].equals(lineCodeRuleList[j])){
                                    tmpMsg += "?????????????????????";
                                }
                            }
                        }
                        StringBuffer lineCodeRulesSb = new StringBuffer();
                        for (String s : lineCodeRuleList) {
                            lineCodeRulesSb.append("'").append(s).append("',");
                        }
                        List<CmCustomerLine> lines = cmCustomerLineMapper.getCmCustomerLineByLineCodeRules(lineCodeRulesSb.substring(0, lineCodeRules.length() - 1));
                        if (lines == null || lines.isEmpty()) {
                            tmpMsg += "????????????????????????";
                        } else {
                            if (lineCodeRuleList.length == lines.size()) {
                                List<Map> lineList = new ArrayList<>();
                                String jsonStr = JsonHelper.toJson(lines);
                                isModify = true;
                                param.put("vehicleLineRels", jsonStr);
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
                                if (org.apache.commons.lang.StringUtils.isNotBlank(lineMsg)) {
                                    tmpMsg += "???????????????" + lineMsg.substring(0, lineMsg.length() - 1) + "????????????";
                                }
                            }
                        }
                    }

                    String shareFlg = ExcelUtils.getExcelValue(l, 22);
                    if (org.apache.commons.lang.StringUtils.isNotBlank(shareFlg)) {
                        isModify = true;
                        param.put("shareFlg", "???".equals(shareFlg) ? 1 : 0);
                    }
                    if ("???".equals(shareFlg)) {
                        String linkUserPhone = ExcelUtils.getExcelValue(l, 23);
                        String shareMobilePhone = DataFormat.getStringKey(param, "shareMobilePhone");
                        if (StrUtil.isBlank(linkUserPhone) && org.apache.commons.lang.StringUtils.isBlank(shareMobilePhone)) {
                            tmpMsg += "??????????????????????????????";
                        } else if (org.apache.commons.lang.StringUtils.isNotBlank(linkUserPhone)) {

                            List<BackUserDto> pagination = customerInfoMapper.doQueryBackUserListPhone(loginInfo.getTenantId(), linkUserPhone);
                            if (pagination == null || pagination.size() == 0) {
                                tmpMsg += "??????????????????????????????";
                            } else {
                                isModify = true;
                                //  Map o = (Map) pagination.get(0);
                                param.put("linkUserId", pagination.get(0).getUserId());
                            }
                        }
                    }
                }

                isModify = ExcelUtils.modifyValue(l, 24, param, "price") || isModify;
                isModify = ExcelUtils.modifyValue(l, 25, param, "residual") || isModify;
                isModify = ExcelUtils.modifyValue(l, 26, param, "loanInterest") || isModify;
                isModify = ExcelUtils.modifyValue(l, 27, param, "interestPeriods") || isModify;
                isModify = ExcelUtils.modifyValue(l, 28, param, "payInterestPeriods") || isModify;
                isModify = ExcelUtils.modifyValue(l, 29, param, "purchaseDate") || isModify;
                isModify = ExcelUtils.modifyValue(l, 30, param, "depreciatedMonth") || isModify;
                isModify = ExcelUtils.modifyValue(l, 31, param, "collectionInsurance") || isModify;
                isModify = ExcelUtils.modifyValue(l, 32, param, "examVehicleFee") || isModify;
                isModify = ExcelUtils.modifyValue(l, 33, param, "maintainFee") || isModify;
                isModify = ExcelUtils.modifyValue(l, 34, param, "repairFee") || isModify;
                isModify = ExcelUtils.modifyValue(l, 35, param, "tyreFee") || isModify;
                isModify = ExcelUtils.modifyValue(l, 36, param, "otherFee") || isModify;
                isModify = ExcelUtils.modifyValue(l, 37, param, "managementCost") || isModify;

                tmpMsg += ExcelUtils.checkDateTypeValue(l, 40, param, "annualVeriTime", "??????????????????", isModify);
                tmpMsg += ExcelUtils.checkDateTypeValue(l, 41, param, "annualVeriTimeEnd", "??????????????????", isModify);
                tmpMsg += ExcelUtils.checkDateTypeValue(l, 42, param, "seasonalVeriTime", "??????????????????", isModify);
                tmpMsg += ExcelUtils.checkDateTypeValue(l, 43, param, "seasonalVeriTimeEnd", "??????????????????", isModify);
                tmpMsg += ExcelUtils.checkDateTypeValue(l, 44, param, "busiInsuranceTime", "?????????????????????", isModify);
                tmpMsg += ExcelUtils.checkDateTypeValue(l, 45, param, "busiInsuranceTimeEnd", "?????????????????????", isModify);
                isModify = ExcelUtils.modifyValue(l, 46, param, "busiInsuranceCode") || isModify;//???????????????
                tmpMsg += ExcelUtils.checkDateTypeValue(l, 47, param, "insuranceTime", "?????????????????????", isModify);
                tmpMsg += ExcelUtils.checkDateTypeValue(l, 48, param, "insuranceTimeEnd", "?????????????????????", isModify);
                isModify = ExcelUtils.modifyValue(l, 49, param, "insuranceCode") || isModify;//???????????????
                tmpMsg += ExcelUtils.checkDateTypeValue(l, 50, param, "otherInsuranceTime", "?????????????????????", isModify);
                tmpMsg += ExcelUtils.checkDateTypeValue(l, 51, param, "otherInsuranceTimeEnd", "?????????????????????", isModify);
                isModify = ExcelUtils.modifyValue(l, 52, param, "otherInsuranceCode") || isModify;//???????????????
                isModify = ExcelUtils.modifyValue(l, 53, param, "maintainDis") || isModify;
                isModify = ExcelUtils.modifyValue(l, 54, param, "maintainWarnDis") || isModify;
                tmpMsg += ExcelUtils.checkDateTypeValue(l, 55, param, "prevMaintainTime", "??????????????????", isModify);
                tmpMsg += ExcelUtils.checkDateTypeValue(l, 56, param, "registrationTime", "????????????", isModify);
                isModify = ExcelUtils.modifyValue(l, 57, param, "registrationNumble") || isModify;//????????????
                tmpMsg += ExcelUtils.checkDateTypeValue(l, 58, param, "vehicleValidityTimeBegin", "?????????????????????", isModify);
                tmpMsg += ExcelUtils.checkDateTypeValue(l, 59, param, "vehicleValidityTime", "?????????????????????", isModify);
                tmpMsg += ExcelUtils.checkDateTypeValue(l, 60, param, "operateValidityTimeBegin", "?????????????????????", isModify);
                tmpMsg += ExcelUtils.checkDateTypeValue(l, 61, param, "operateValidityTime", "?????????????????????", isModify);
            }
        }

        //???????????????????????????????????????????????????????????????
        if (org.apache.commons.lang.StringUtils.isBlank(tmpMsg) && !isModify) {
            tmpMsg += "???????????????????????????";
        }

        param.put("tmpMsg", tmpMsg);
        return param;
    }


    //**********************************  ????????? ***********************************//

    @Override
    @Transactional
    public boolean save(OBMSVehicleDataVo obmsVehicleDataVo, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        boolean ret = isInCommonLine(obmsVehicleDataVo.getVehicleOjbectLineArray());
        if (ret) {
            throw new BusinessException("??????????????????????????????!");
        }
//        if (vehicleClass < 0) {
//            vehicleClass = 1;//?????????????????????????????????vehicleClass=1?????????????????????vehicleClass????????????????????????????????????1
//        }
        int vehicleClass = 1;
        //??????????????????????????????????????????????????????
//        Map rtnMap = vehicleTF.getVehicleIsRegisterForOBMS(plateNumber,vehicleClass);
//        Map rtnMap = vehicleDataInfoMapper.getVehicleIsRegisterForOBMS(obmsVehicleDataVo.getPlateNumber(),vehicleClass);
        Map rtnMap = vehicleDataInfoMapper.getVehicleIsRegister(vehicleClass, obmsVehicleDataVo.getPlateNumber());
        if (null != rtnMap) {
            throw new BusinessException("?????????????????????");
        }

        //????????????
        VehicleDataInfo vehicleDataInfo = new VehicleDataInfo();
        vehicleDataInfo.setTenantId(-1L);
        BeanUtil.copyProperties(obmsVehicleDataVo, vehicleDataInfo);
        vehicleDataInfo.setAuthState(AUTH_STATE1);
        vehicleDataInfo.setIsAuth(IS_AUTH1);
        if (StringUtils.isBlank(vehicleDataInfo.getVehicleLoad())) {
            vehicleDataInfo.setVehicleLoad(null);
        }
        baseMapper.insert(vehicleDataInfo);
        VehicleDataInfoVer vehicleDataInfoVer = new VehicleDataInfoVer();
        BeanUtil.copyProperties(vehicleDataInfo, vehicleDataInfoVer);
        vehicleDataInfoVer.setVerState(VER_STATE_Y);
        vehicleDataInfoVerMapper.insert(vehicleDataInfoVer);


  /*
  long attachIsAdminUser = obmsVehicleDataVo.getAttachUserId();
        long attachTenantId =loginInfo.getTenantId();
        String attachUserName = obmsVehicleDataVo.getAttachUserName() ;
        if (attachIsAdminUser == 1 && attachTenantId > 0) {//?????????????????????,??????????????????????????????????????????
            //?????????????????????

       //????????????????????????,???????????????????????????
            Param param = new Param("plateNumber", plateNumber);
            param.addParam("vehicleClass", SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS4);
            List exitTenantVehicleRels = vehicleOBMSSV.getObjects(TenantVehicleRel.class, param, false);//?????????????????????????????????
            TenantVehicleRel oldTenantVehicleRel = null;
            if (exitTenantVehicleRels != null && exitTenantVehicleRels.size() > 0) {
                oldTenantVehicleRel = (TenantVehicleRel) exitTenantVehicleRels.get(0);
            }

            if (oldTenantVehicleRel == null || oldTenantVehicleRel.getTenantId() != attachTenantId) {
                TenantVehicleRel vehicleRel = vehicleTF.getTenantVehicleRel(vehicleDataInfo.getVehicleCode(), attachTenantId);
                if (vehicleRel == null) {
                    vehicleTF.addAttachVehicleAndDriver(vehicleDataInfo, attachTenantId, 3);//?????????????????????????????????
                }
            }
        }
*/

//        vehicleTF.doSaveVehicleOrderPositionInfoForOBMS(vehicleDataInfo.getVehicleCode(),vehicleDataInfo.getPlateNumber());
        Integer integer = vehicleOrderPositionInfoMapper.countOrderPositionInfo(vehicleDataInfo.getId());
        if (integer == 0) {
            VehicleOrderPositionInfo vehicleOrderPositionInfo = new VehicleOrderPositionInfo();
            vehicleOrderPositionInfo.setPlateNumber(vehicleDataInfo.getPlateNumber());
            vehicleOrderPositionInfo.setShareFlg(YES);
            vehicleOrderPositionInfoMapper.insert(vehicleOrderPositionInfo);
        }
        //???????????????
        List<VehicleObjectLineVo> vehicleObjectLines = obmsVehicleDataVo.getVehicleOjbectLineArray();
        VehicleObjectLineVer vehicleObjectLineVer = null;
        List vehicleObjectLineIds = new ArrayList();
        if (null != vehicleObjectLines) {
            for (int i = 0; i < vehicleObjectLines.size(); i++) {

                //??????????????????????????????????????????3?????????????????????
                List<VehicleObjectLine> lines = vehicleObjectLineMapper.getVehicleObjectLine(vehicleDataInfo.getId());
                if (lines != null && lines.size() < 3) {
                    VehicleObjectLineVo vehicleOjbectLineVo = vehicleObjectLines.get(i);
                    VehicleObjectLine vehicleObjectLine = new VehicleObjectLine();
                    BeanUtil.copyProperties(vehicleOjbectLineVo, vehicleObjectLine);
                    vehicleObjectLineVer = new VehicleObjectLineVer();

                    vehicleObjectLine.setVehicleCode(vehicleDataInfo.getId());
                    vehicleObjectLine.setPlateNumber(vehicleDataInfo.getPlateNumber());
                    vehicleObjectLineMapper.insert(vehicleObjectLine);
                    BeanUtil.copyProperties(vehicleObjectLine, vehicleObjectLineVer);
                    vehicleObjectLineVer.setId(vehicleObjectLine.getId());
                    vehicleObjectLineVer.setVerState(VER_STATE_Y);
                    vehicleObjectLineVer.setVehicleHisId(vehicleDataInfoVer.getId());
                    vehicleObjectLineVerMapper.insert(vehicleObjectLineVer);
                    vehicleObjectLineIds.add(vehicleObjectLine.getId());
                }
            }
        }
        //?????????????????????
        doUpdateVehicleCompleteness(vehicleDataInfo.getId(), loginInfo.getTenantId());

        saveSysOperLog(SysOperLogConst.BusiCode.Vehicle, vehicleDataInfo.getId(), SysOperLogConst.OperType.Add, "??????" + vehicleDataInfo.getPlateNumber() + "????????????", loginInfo);
//        sysOperLogTF.saveSysOperLog(SysOperLogConst.BusiCode.Vehicle.getCode(), vehicleDataInfo.getVehicleCode(), SysOperLogConst.OperType.Add.getCode(), "??????"+vehicleDataInfo.getPlateNumber()+"????????????");

        boolean isAutoAudit = sysCfgService.getCfgBooleanVal("IS_AUTO_AUDIT", -1);
        if (isAutoAudit) {
            vehicleDataInfo.setAuthState(AUTH_STATE2);
            vehicleDataInfo.setIsAuth(IS_AUTH0);
            baseMapper.updateById(vehicleDataInfo);
            vehicleDataInfoVer.setVerState(VER_STATE_Y);
            vehicleDataInfoVerMapper.insert(vehicleDataInfoVer);
            //?????????????????????
            addPtTenantVehicleRelWithDriver(vehicleDataInfo.getId(), vehicleDataInfoVer.getDriverUserId());
            //?????????
            saveSysOperLog(SysOperLogConst.BusiCode.Vehicle, vehicleDataInfo.getId(), SysOperLogConst.OperType.Audit, "??????" + vehicleDataInfo.getPlateNumber() + "????????????", loginInfo);
        }
        return true;
    }

    /**
     * ????????????????????????????????????
     *
     * @return
     */
    private boolean isInCommonLine(List<VehicleObjectLineVo> lines) {
        //???????????????,??????
        if (null != lines) {
            for (int i = 0; i < lines.size(); i++) {
                VehicleObjectLineVo vehicleObjectLine = lines.get(i);
                Integer sourceProvince = vehicleObjectLine.getSourceProvince();
                Integer sourceRegion = vehicleObjectLine.getSourceRegion();
                Integer sourceCounty = vehicleObjectLine.getSourceCounty();
                Integer desProvince = vehicleObjectLine.getDesProvince();
                Integer desRegion = vehicleObjectLine.getDesRegion();
                Integer desCounty = vehicleObjectLine.getDesCounty();
                Long carriagePrice = vehicleObjectLine.getCarriagePrice();
                //????????????????????????????????????
                if ((sourceProvince != null && (desProvince == null || (carriagePrice == null)))
                        || ((sourceProvince == null || (carriagePrice == null)) && desProvince != null)
                        || ((sourceProvince == null || desProvince == null) && (carriagePrice != null))
                ) {
                    throw new BusinessException("??????????????????????????????????????????????????????????????????????????????!");
                }

                for (int j = i + 1; j < lines.size(); j++) {
                    VehicleObjectLineVo vehicleObjectLine1 = lines.get(j);
                    Integer sourceProvincej = vehicleObjectLine1.getSourceCounty();
                    Integer sourceRegionj = vehicleObjectLine1.getSourceRegion();
                    Integer sourceCountyj = vehicleObjectLine1.getSourceCounty();
                    Integer desProvincej = vehicleObjectLine1.getDesProvince();
                    Integer desRegionj = vehicleObjectLine1.getDesRegion();
                    Integer desCountyj = vehicleObjectLine1.getDesCounty();
                    if ((sourceProvince != null && sourceProvincej != null && sourceProvince.equals(sourceProvincej))
                            && (sourceRegion != null && sourceRegionj != null && sourceRegion.equals(sourceRegionj))
                            && (desProvince != null && desProvincej != null && desProvince.equals(desProvincej))
                            && (desRegion != null && desRegionj != null && desRegion.equals(desRegionj))
                    ) {
                        //???????????????
                        if (((sourceCounty == null && sourceCountyj == null) && (desCounty == null && desCountyj == null))
                                || ((sourceCounty != null && sourceCountyj != null && sourceCounty.equals(sourceCountyj)) && (desCounty != null && desCountyj != null && desCounty.equals(desCountyj)))
                                || ((sourceCounty != null && sourceCountyj != null && sourceCounty.equals(sourceCountyj)) && (desCounty == null && desCountyj == null))
                                || ((sourceCounty == null && sourceCountyj == null) && (desCounty != null && desCountyj != null && desCounty.equals(desCountyj)))
                        ) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public void addPtTenantVehicleRelWithDriver(long vehicleCode, Long driverUserId) {
        //???????????????????????????
        VehicleDataInfo vehicleDataInfo = baseMapper.selectById(vehicleCode);
        if (vehicleDataInfo == null) {
            return;
        }
        //???????????????????????????

        TenantVehicleRel zyVehicleRel = iTenantVehicleRelService.getZYVehicleByVehicleCode(vehicleCode);
        if (zyVehicleRel != null && org.apache.commons.lang.StringUtils.isNotBlank(zyVehicleRel.getPlateNumber())) {
            return;
        }
        //?????????C?????????????????????
        TenantVehicleRel vehicleRel = iTenantVehicleRelService.getTenantVehicleRel(vehicleCode, SysStaticDataEnum.PT_TENANT_ID);
        if (vehicleRel != null && vehicleRel.getTenantId() != null && vehicleRel.getTenantId() == SysStaticDataEnum.PT_TENANT_ID) {
            return;
        }

        TenantVehicleRel ptVehicleRel = new TenantVehicleRel();
        ptVehicleRel.setTenantId(SysStaticDataEnum.PT_TENANT_ID);
        ptVehicleRel.setVehicleCode(vehicleCode);
        ptVehicleRel.setDriverUserId(driverUserId);
        ptVehicleRel.setPlateNumber(vehicleDataInfo.getPlateNumber());
        ptVehicleRel.setCreateTime(LocalDateTime.now());
        ptVehicleRel.setVehicleClass(VEHICLE_CLASS5);
        ptVehicleRel.setAuthState(SysStaticDataEnum.AUTH_STATE.AUTH_STATE2);
        ptVehicleRel.setIsAuth(SysStaticDataEnum.IS_AUTH.IS_AUTH0);
        ptVehicleRel.setShareFlg(SysStaticDataEnum.SHARE_FLG.YES);
        iTenantVehicleRelService.save(ptVehicleRel);

        TenantVehicleRelVer ptVehicleRelVer = new TenantVehicleRelVer();
        BeanUtil.copyProperties(ptVehicleRel, ptVehicleRelVer);
        ptVehicleRelVer.setRelId(ptVehicleRel.getId());
        iTenantVehicleRelVerService.saveOrUpdate(ptVehicleRelVer);
    }

    @Override
    public boolean update(OBMSVehicleDataVo obmsVehicleDataVo, String accessToken) {
        long vehicleCode = obmsVehicleDataVo.getVehicleCode();

        if (vehicleCode < 0) {
            throw new BusinessException("?????????????????????");
        }
        VehicleDataInfo vehicleDataInfo = get(vehicleCode);
//        VehicleDataInfo vehicleDataInfo = vehicleOBMSSV.getObjectById(VehicleDataInfo.class, vehicleCode);
        if (null == vehicleDataInfo) {
            throw new BusinessException("??????????????????");
        }

        //??????????????????????????????????????????????????????????????????
//        TenantVehicleRel exitTenantVehicleRel = vehicleTF.getZYVehicleByVehicleCode(vehicleCode);
        List<TenantVehicleRel> zyVehicleRel = tenantVehicleRelMapper.getZYVehicleByVehicleCode(vehicleDataInfo.getId(), VEHICLE_CLASS1);
        TenantVehicleRel exitTenantVehicleRel = null;
        if (zyVehicleRel.size() > 0) {
            exitTenantVehicleRel = zyVehicleRel.get(0);
        }
        boolean unNeedCheck = true;//????????????????????????????????????????????????????????????????????????
        if (exitTenantVehicleRel != null && exitTenantVehicleRel.getAuthState() != null && SysStaticDataEnum.AUTH_STATE.AUTH_STATE2 == exitTenantVehicleRel.getAuthState()) {
            unNeedCheck = false;
        }


        //vehicle_data_info_ver????????????ver_state???1??????????????????0
//        vehicleTF.updateVehicleVerAllVerState("vehicle_data_info_ver", inMap);
        updateVehicleVerAllVerState("vehicle_data_info_ver", obmsVehicleDataVo.getVehicleCode(), vehicleDataInfo.getTenantId(), VER_STATE_N);


        VehicleDataInfoVer vehicleDataInfoVer = new VehicleDataInfoVer();
//        BeanUtil.copyProperties(vehicleDataInfo, vehicleDataInfoVer);
//        BeanUtil.copyProperties(obmsVehicleDataVo,vehicleDataInfoVer);
//        vehicleDataInfoVer.setVerState(VER_STATE_N);
//        vehicleDataInfoVerMapper.insert(vehicleDataInfoVer);
        //????????????
        BeanUtil.copyProperties(vehicleDataInfo, vehicleDataInfoVer);
        vehicleDataInfoVer.setPlateNumber(obmsVehicleDataVo.getPlateNumber());
        vehicleDataInfoVer.setLicenceType(obmsVehicleDataVo.getLicenceType());
        vehicleDataInfoVer.setVehicleLength(obmsVehicleDataVo.getVehicleLength());
        vehicleDataInfoVer.setVehicleStatus(obmsVehicleDataVo.getVehicleStatus());
        vehicleDataInfoVer.setVehicleLoad(obmsVehicleDataVo.getVehicleLoad());
        vehicleDataInfoVer.setLightGoodsSquare(obmsVehicleDataVo.getLightGoodsSquare());
        vehicleDataInfoVer.setVinNo(obmsVehicleDataVo.getVinNo());
        vehicleDataInfoVer.setOperCerti(obmsVehicleDataVo.getOperCerti());
        vehicleDataInfoVer.setSpecialOperCertFileUrl(obmsVehicleDataVo.getSpecialOperCertFileUrl());
        vehicleDataInfoVer.setSpecialOperCertFileId(obmsVehicleDataVo.getSpecialOperCertFileId());
        vehicleDataInfoVer.setEngineNo(obmsVehicleDataVo.getEngineNo());
        vehicleDataInfoVer.setDrivingLicenseSn(obmsVehicleDataVo.getDrivingLicenseSn());
        vehicleDataInfoVer.setDrivingLicenseOwner(obmsVehicleDataVo.getDrivingLicenseOwner());
        vehicleDataInfoVer.setDriverUserId(obmsVehicleDataVo.getDriverUserId());
        vehicleDataInfoVer.setVehiclePicture(obmsVehicleDataVo.getVehiclePicture());
        vehicleDataInfoVer.setVehiclePicUrl(obmsVehicleDataVo.getVehiclePicUrl());
        vehicleDataInfoVer.setDrivingLicense(String.valueOf(obmsVehicleDataVo.getDrivingLicense()));
        vehicleDataInfoVer.setDrivingLicenseUrl(obmsVehicleDataVo.getDrivingLicenseUrl());
        vehicleDataInfoVer.setAdrivingLicenseCopy(obmsVehicleDataVo.getAdriverLicenseCopy());


        vehicleDataInfoVer.setTenantId(vehicleDataInfo.getTenantId());
        vehicleDataInfoVer.setVerState(VER_STATE_Y);

        BeanUtil.copyProperties(vehicleDataInfoVer, vehicleDataInfo);
        vehicleDataInfo.setIsAuth(IS_AUTH1);
        vehicleDataInfo.setAdriverLicenseCopy(vehicleDataInfoVer.getAdrivingLicenseCopy());//???????????????(??????????????????)
        //??????????????????????????????????????????????????????

        boolean flag = false;
        if (vehicleDataInfo.getDriverUserId() == null) {
            if (vehicleDataInfoVer.getDriverUserId() != null) {
                //?????????
                flag = true;
            }
        } else {
            if (!vehicleDataInfo.getDriverUserId().equals(vehicleDataInfoVer.getDriverUserId())) {
                //?????????
                flag = true;
            }
        }
        if (flag) {
            LambdaQueryWrapper<TenantUserRel> wrapper = Wrappers.lambdaQuery();
            wrapper.eq(TenantUserRel::getUserId, vehicleDataInfoVer.getDriverUserId())
                    .eq(TenantUserRel::getCarUserType, SysStaticDataEnum.CAR_USER_TYPE.OWN_CAR);
            List<TenantUserRel> userRelList = tenantUserRelMapper.selectList(wrapper);
            if (userRelList != null && userRelList.size() > 0) {
                unNeedCheck = false;
            }
        }

/*        if ((vehicleDataInfo.getDriverUserId() == null && vehicleDataInfoVer.getDriverUserId() != null)
                || !vehicleDataInfo.getDriverUserId().equals(vehicleDataInfoVer.getDriverUserId())) {//???????????????
//            TenantUserRel

        }*/

        if (vehicleDataInfoVer.getLicenceType() == SysStaticDataEnum.LICENCE_TYPE.TT) {
            vehicleDataInfoVer.setVehicleStatus(null);
            vehicleDataInfoVer.setVehicleLength(null);
            vehicleDataInfoVer.setVehicleLoad(null);
            vehicleDataInfoVer.setLightGoodsSquare(null);
        }

        if (unNeedCheck) {//??????????????????????????????????????????????????????

            //????????????????????????
//            if (vehicleDataInfo.getDriverUserId().equals(vehicleDataInfoVer.getDriverUserId())) {//??????????????????
//                BeanUtil.copyProperties(obmsVehicleDataVo, vehicleDataInfo);
//                vehicleDataInfo.setIsAuth(IS_AUTH0);
//                vehicleDataInfo.setAuthState(SysStaticDataEnum.AUTH_STATE.AUTH_STATE2);
//
//            } else {//???????????????,?????????????????????????????????????????????????????????????????????????????????
//                addDriverToSocialCarByTenantId(vehicleDataInfo.getId(), vehicleDataInfoVer.getDriverUserId(), false);
//            }
            //????????????????????????  2022-5-11 ????????? ?????????????????????????????????
            if (vehicleDataInfo.getDriverUserId() == null || vehicleDataInfoVer.getDriverUserId() == null) {//??????????????????
                BeanUtil.copyProperties(obmsVehicleDataVo, vehicleDataInfo);
                vehicleDataInfo.setAdriverLicenseCopy(obmsVehicleDataVo.getAdriverLicenseCopy());
                vehicleDataInfo.setAdriverLicenseCopyUrl(obmsVehicleDataVo.getAdriverLicenseCopyUrl());
                vehicleDataInfo.setIsAuth(IS_AUTH0);
                vehicleDataInfo.setAuthState(SysStaticDataEnum.AUTH_STATE.AUTH_STATE2);

            } else {//???????????????,?????????????????????????????????????????????????????????????????????????????????
                addDriverToSocialCarByTenantId(vehicleDataInfo.getId(), vehicleDataInfoVer.getDriverUserId(), false);
            }
            vehicleDataInfoVer.setIsAuthSucc(3);//??????????????????
            BeanUtil.copyProperties(vehicleDataInfoVer, vehicleDataInfo);
            vehicleDataInfo.setAdriverLicenseCopy(vehicleDataInfoVer.getAdrivingLicenseCopy());//???????????????(??????????????????)
            vehicleDataInfo.setIsAuth(IS_AUTH0);

   /*         if (attachIsAdminUser == 1 && attachTenantId > 0) {//?????????????????????,??????????????????????????????????????????
                TenantVehicleRel vehicleRel = vehicleTF.getTenantVehicleRel(vehicleDataInfo.getVehicleCode(), attachTenantId);
                if (vehicleRel == null) {
                    vehicleTF.addAttachVehicleAndDriver(vehicleDataInfo, attachTenantId, 3);//?????????????????????????????????
                }
            }*/

            //?????????????????????
//            vehicleTF.doUpdateVehicleCompleteness(vehicleDataInfo.getVehicleCode());
            doUpdateVehicleCompleteness(vehicleDataInfo.getId(), vehicleDataInfo.getTenantId());
        }

        baseMapper.updateById(vehicleDataInfo);
        vehicleDataInfoVerMapper.updateById(vehicleDataInfoVer);

//??????????????????
        //vehicle_object_line_ver????????????ver_state???1??????????????????0
        this.updateVehicleVerAllVerState("vehicle_object_line_ver", obmsVehicleDataVo.getVehicleCode(),
                vehicleDataInfo.getTenantId(), VER_STATE_N);

        //??????????????????vehicle_object_line_ver?????????
        List<VehicleObjectLineVo> lines = obmsVehicleDataVo.getVehicleOjbectLineArray();
        String vehicleObjectLineVerHisIds = "";
        if (null != lines && lines.size() > 0) {
            try {
//                dealVehicleObjectLine(lines, vehicleDataInfo, vehicleDataInfoVer, unNeedCheck, false, accessToken);
                this.dealVehicleObjectLinehis(lines, vehicleDataInfo, vehicleDataInfoVer, true, false, accessToken);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            vehicleObjectLineService.remove(vehicleCode, vehicleDataInfo.getTenantId());
        }


        saveSysOperLog(SysOperLogConst.BusiCode.Vehicle, vehicleDataInfo.getId(), SysOperLogConst.OperType.Update, "??????" + vehicleDataInfo.getPlateNumber() + "????????????", accessToken);

        return true;
    }

    private void dealVehicleObjectLinehis(List<VehicleObjectLineVo> vehicleObjectLineList, VehicleDataInfo
            vehicleDataInfo, VehicleDataInfoVer vehicleDataInfoVer, boolean unNeedCheck, boolean isApp, String accessToken) throws
            ParseException {
        String vehicleObjectLineVerHisIds = "";
        for (int i = 0; i < vehicleObjectLineList.size(); i++) {
            VehicleObjectLineVo vehicleOjbectLineVo = vehicleObjectLineList.get(i);
            Number sourceProvinceN = null;
            if (null != vehicleOjbectLineVo.getSourceProvince()) {
                sourceProvinceN = NumberFormat.getInstance().parse(vehicleOjbectLineVo.getSourceProvince().toString());
            }
            Number sourceRegionN = null;
            if (null != vehicleOjbectLineVo.getSourceRegion()) {
                sourceRegionN = NumberFormat.getInstance().parse(vehicleOjbectLineVo.getSourceRegion().toString());
            }
            Number sourceCountyN = null;
            if (null != vehicleOjbectLineVo.getSourceCounty()) {
                sourceCountyN = NumberFormat.getInstance().parse(vehicleOjbectLineVo.getSourceCounty().toString());
            }
            Number desProvinceN = null;
            if (null != vehicleOjbectLineVo.getDesProvince()) {
                desProvinceN = NumberFormat.getInstance().parse(vehicleOjbectLineVo.getDesProvince().toString());
            }
            Number desRegionN = null;
            if (null != vehicleOjbectLineVo.getDesRegion()) {
                desRegionN = NumberFormat.getInstance().parse(vehicleOjbectLineVo.getDesRegion().toString());
            }
            Number desCountyN = null;
            if (null != vehicleOjbectLineVo.getDesCounty()) {
                desCountyN = NumberFormat.getInstance().parse(vehicleOjbectLineVo.getDesCounty().toString());
            }
            Integer sourceProvince = null == sourceProvinceN ? null : sourceProvinceN.intValue();
            Integer sourceRegion = null == sourceRegionN ? null : sourceRegionN.intValue();
            Integer sourceCounty = null == sourceCountyN ? null : sourceCountyN.intValue();
            Integer desProvince = null == desProvinceN ? null : desProvinceN.intValue();
            Integer desRegion = null == desRegionN ? null : desRegionN.intValue();
            Integer desCounty = null == desCountyN ? null : desCountyN.intValue();
            Long carriagePrice = null;
            if (null != vehicleOjbectLineVo.getCarriagePrice()) {
                carriagePrice = vehicleOjbectLineVo.getCarriagePrice();
            }
//            if (StringUtils.isBlank(carriagePrice) || carriagePrice == "") {
//                carriagePrice = "0";
//            }
//            BigDecimal carriagePriceDouble = new BigDecimal(carriagePrice);
//            if (!isApp) {
//                //web??????????????????????????????100???APP?????????
//                carriagePriceDouble = new BigDecimal(carriagePrice).multiply(new BigDecimal(100));
//            }


            Number vehicleObjectLineIdN = null;
            if (vehicleOjbectLineVo.getId() != null) {
                vehicleObjectLineIdN = NumberFormat.getInstance().parse(vehicleOjbectLineVo.getId().toString());
            }
            Long vehicleObjectLineId = null == vehicleObjectLineIdN ? null : vehicleObjectLineIdN.longValue();

            VehicleObjectLine oldVehicleObjectLine = null;
            if (vehicleObjectLineId != null && vehicleObjectLineId > 0) {
                oldVehicleObjectLine = vehicleObjectLineMapper.selectById(vehicleObjectLineId);
            }

            if (oldVehicleObjectLine != null) {//??????????????????
                // id ??????????????????
                oldVehicleObjectLine.setCarriagePrice(carriagePrice != null ? carriagePrice : null); // ????????? ??????
                oldVehicleObjectLine.setVehicleCode(vehicleDataInfo.getId());
                oldVehicleObjectLine.setSourceProvince(sourceProvince);
                oldVehicleObjectLine.setSourceRegion(sourceRegion);
                oldVehicleObjectLine.setSourceCounty(sourceCounty);
                oldVehicleObjectLine.setDesProvince(desProvince);
                oldVehicleObjectLine.setTenantId(null);
                oldVehicleObjectLine.setDesRegion(desRegion);
                oldVehicleObjectLine.setDesCounty(desCounty);
                oldVehicleObjectLine.setPlateNumber(vehicleDataInfo.getPlateNumber());


                if (unNeedCheck) {
                    //?????????????????????????????????????????????????????????

                    oldVehicleObjectLine.setOpId(loginUtils.get(accessToken).getUserInfoId());
                    oldVehicleObjectLine.setUpdateTime(LocalDateTime.now());
                    vehicleObjectLineMapper.updateById(oldVehicleObjectLine);
                    //??????????????????
//                    vehicleObjectLineVer.setIsAuthSucc(3);
                }
                oldVehicleObjectLine.setCreateTime(LocalDateTime.now());
                //?????????????????????????????????????????????????????????????????????
//                vehicleObjectLineVerMapper.insert(vehicleObjectLineVer);
//                iVehicleObjectLineVerService.saveOrUpdate(oldVehicleObjectLine);
                iVehicleObjectLineService.saveOrUpdate(oldVehicleObjectLine);

            } else {//????????????

                //??????????????????????????????????????????3?????????????????????
                List<VehicleObjectLine> vehicleObjectLine = vehicleObjectLineMapper.getVehicleObjectLine(vehicleDataInfo.getId());

                if (vehicleObjectLine != null && vehicleObjectLine.size() < 3) {
                    VehicleObjectLine newVehicleObjectLine = new VehicleObjectLine();
                    newVehicleObjectLine.setVehicleCode(vehicleDataInfo.getId());
                    newVehicleObjectLine.setPlateNumber(vehicleDataInfo.getPlateNumber());
                    newVehicleObjectLine.setTenantId(null);

                    newVehicleObjectLine.setCreateDate(LocalDateTime.now());
                    newVehicleObjectLine.setOpId(loginUtils.get(accessToken).getUserInfoId());
                    vehicleObjectLineMapper.insert(newVehicleObjectLine);

                    newVehicleObjectLine.setSourceProvince(sourceProvince);
                    newVehicleObjectLine.setSourceRegion(sourceRegion);
                    newVehicleObjectLine.setSourceCounty(sourceCounty);
                    newVehicleObjectLine.setDesProvince(desProvince);
                    newVehicleObjectLine.setDesRegion(desRegion);
                    newVehicleObjectLine.setDesCounty(desCounty);
                    newVehicleObjectLine.setCarriagePrice(carriagePrice);

                    newVehicleObjectLine.setCarriagePrice(carriagePrice != null ? carriagePrice : null); // ????????? ??????
                    if (unNeedCheck) {//?????????????????????????????????????????????????????????
//                        vehicleObjectLineVer.setIsAuthSucc(3);//??????????????????
                        vehicleObjectLineMapper.updateById(newVehicleObjectLine);
                    }
                    iVehicleObjectLineService.saveOrUpdate(oldVehicleObjectLine);
                }
            }
        }
    }


    /**
     * ????????????????????????????????????
     *
     * @param vehicleCode  ????????????
     * @param driverUserId ????????????
     * @param isDealOwnCar ?????????????????????????????? true-??????false-??????
     * @return
     * @throws Exception
     */
    private void addDriverToSocialCarByTenantId(Long vehicleCode, Long driverUserId, boolean isDealOwnCar) {
        if (vehicleCode == null || vehicleCode < 0) {
            throw new BusinessException("??????ID?????????");
        }
        //??????????????????
        LambdaQueryWrapper<TenantVehicleRel> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(TenantVehicleRel::getVehicleCode, vehicleCode);
        List<TenantVehicleRel> tenantVehicleRels = tenantVehicleRelMapper.selectList(wrapper);
        TenantVehicleRel tenantVehicleRel = new TenantVehicleRel();
        if (tenantVehicleRels.size() > 0) {
            tenantVehicleRel = tenantVehicleRels.get(0);
        } else {
            return;
        }
        if (driverUserId == null || driverUserId < 0) {
            tenantVehicleRel.setDriverUserId(null);
        } else {
            tenantVehicleRel.setDriverUserId(driverUserId);
        }
        tenantVehicleRelMapper.updateById(tenantVehicleRel);
    }

    private void updateVehicleVerAllVerState(String tableName, Long vehicleCode, Long tenantId, int destVerState) {
//        long vehicleCode = DataFormat.getLongKey(inMap, "vehicleCode");
//        long tenantId = DataFormat.getLongKey(inMap, "tenantId");
//        int destVerState = DataFormat.getIntKey(inMap, "destVerState");
        if (destVerState < 0) {
            throw new BusinessException("destVerState???????????????");
        }
        //????????????
        if ("vehicle_data_info_ver".equals(tableName) || "all".equals(tableName)) {
            vehicleDataInfoVerMapper.updVehicleDataInfoVerState(destVerState, vehicleCode);
        }
        //???????????????
        if ("vehicle_object_line_ver".equals(tableName) || "all".equals(tableName)) {
            vehicleDataInfoVerMapper.updVehicleObjectLineVerState(destVerState, vehicleCode);
        }
        //????????????
        if ("vehicle_line_rel_ver".equals(tableName) || "all".equals(tableName)) {
            vehicleDataInfoVerMapper.updVehicleLineRelVerState(destVerState, vehicleCode);
        }
        //?????????????????????
        if ("tenant_vehicle_rel_ver".equals(tableName) || "all".equals(tableName)) {
            vehicleDataInfoVerMapper.updTenantVehicleRelVerState(destVerState, vehicleCode, tenantId);
        }
        //???????????????
        if ("tenant_vehicle_cost_rel_ver".equals(tableName) || "all".equals(tableName)) {
            vehicleDataInfoVerMapper.updTenantVehicleCostRelVerState(destVerState, vehicleCode, tenantId);
        }
        //???????????????
        if ("tenant_vehicle_cert_rel_ver".equals(tableName) || "all".equals(tableName)) {
            vehicleDataInfoVerMapper.updTenantVehicleCertRelVerState(destVerState, vehicleCode, tenantId);
        }
    }

    void checkDate(List<String> strings) {
        if (ExcelUtils.getExcelValue(strings, 10) == null) {
            strings.set(10, "");
        }
        for (int i = 10; i < 63; i++) {
            if (ExcelUtils.getExcelValue(strings, i) == null) {
                strings.add(i, "");
            }
        }
    }

    @Override
    public OBMSVehicleInfoDto obmsGet(Long vehicleCode, String accessToken) {

        //???????????????????????????
        OBMSVehicleInfoDto tenantVehicleInfoOBMS = baseMapper.getTenantVehicleInfoOBMS(VEHICLE_CLASS1, vehicleCode);
        if (null == tenantVehicleInfoOBMS) {
            return null;
        }
        tenantVehicleInfoOBMS.setLicenceTypeName(SysStaticDataRedisUtils.getSysStaticDataByCodeValue2String(redisUtil,
                "LICENCE_TYPE", String.valueOf(tenantVehicleInfoOBMS.getLicenceType())));
        try {
            tenantVehicleInfoOBMS.setVehicleStatusName(SysStaticDataRedisUtils.getSysStaticDataById2String(redisUtil,
                    "VEHICLE_STATUS", tenantVehicleInfoOBMS.getVehicleStatus()));
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        try {
            if (tenantVehicleInfoOBMS.getVehicleStatus() == 8) {
                tenantVehicleInfoOBMS.setVehicleLengthName(SysStaticDataRedisUtils.getSysStaticDataById2String(redisUtil,
                        "VEHICLE_STATUS_SUBTYPE", Long.valueOf(tenantVehicleInfoOBMS.getVehicleLength())));
            } else {
                tenantVehicleInfoOBMS.setVehicleLengthName(SysStaticDataRedisUtils.getSysStaticDataById2String(redisUtil,
                        "VEHICLE_LENGTH", Long.valueOf(tenantVehicleInfoOBMS.getVehicleLength())));
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        int shareFlg = 0;
        try {
            shareFlg = Integer.parseInt(tenantVehicleInfoOBMS.getShareFlg());
        } catch (NumberFormatException e) {
            e.printStackTrace();
            shareFlg = 1;
        }
        if (shareFlg == SysStaticDataEnum.SHARE_FLG.NO) {
            tenantVehicleInfoOBMS.setShareFlgName("???");
        } else {
            tenantVehicleInfoOBMS.setShareFlgName("???");
        }

        if (tenantVehicleInfoOBMS.getAuthState() == SysStaticDataEnum.AUTH_STATE.AUTH_STATE2) {
            tenantVehicleInfoOBMS.setAuthStateName("?????????");
        } else {
            tenantVehicleInfoOBMS.setAuthStateName("?????????");
        }

        //??????????????????
        UserDataInfo userDataInfo = userDataInfoRecordMapper.selectById(tenantVehicleInfoOBMS.getDriverUserId());
        if (null != userDataInfo) {
            tenantVehicleInfoOBMS.setDriverUserPhone(userDataInfo.getMobilePhone());
            tenantVehicleInfoOBMS.setDriverUserName(userDataInfo.getLinkman());
        }

        List<VehicleObjectLineVo> vehicleObjectLine = getVehicleObjectLine(vehicleCode);
        for (VehicleObjectLineVo vo : vehicleObjectLine) {
//            if (vo.getCarriagePrice()!=null && vo.getCarriagePrice()==0){
//                vo.setCarriagePrice(null);
//            }
        }
        tenantVehicleInfoOBMS.setVehicleOjbectLineArray(vehicleObjectLine);
        return tenantVehicleInfoOBMS;
    }


    @Override
    public List<VehicleObjectLineVo> getVehicleObjectLines(Long vehicleCode) {
        if (vehicleCode <= 0) {
            throw new BusinessException("?????????????????????");
        }
        List<VehicleObjectLineVo> vehicleObjectLine = getVehicleObjectLine(vehicleCode);
        return vehicleObjectLine;
    }

    @Override
    public List<TenantVehicleRel> getVehicleByOrganize(Long id) {
        QueryWrapper<TenantVehicleRel> tenantVehicleRelQueryWrapper = new QueryWrapper<>();
        tenantVehicleRelQueryWrapper.eq("org_id", id);
        List<TenantVehicleRel> tenantVehicleRels = tenantVehicleRelMapper.selectList(tenantVehicleRelQueryWrapper);
        return tenantVehicleRels;
    }

    @Override
    public Boolean isBindOtherVehicle(Long driverUserId, String plateNumber, LoginInfo user) {
        Long tenantId = user.getTenantId();
        VehicleDto vehicle = getVehicle(driverUserId, tenantId);
        if (null == vehicle || vehicle.getVehicleDataInfos() != null && vehicle.getVehicleDataInfos().size() <= 0) {
            return false;
        }
        List<VehicleDataInfo> vehicleDataInfos = vehicle.getVehicleDataInfos();
        for (VehicleDataInfo dataInfo : vehicleDataInfos) {
            if (plateNumber.equals(dataInfo.getPlateNumber())) {
                return false;
            }
        }
        return true;
    }

    //    @Override
    public Boolean isBindOtherVehicle(Long driverUserId, String plateNumber) {
        return null;
    }

    public VehicleDto getVehicle(Long userId, Long tenantId) {
        List<VehicleDataInfo> list = vehicleDataInfoMapper.getVehicle(userId, tenantId);
        List<TenantVehicleRelVer> vehicle = tenantVehicleRelVerMapper.getVehicle(userId, tenantId);
        if (list == null && vehicle == null || list.isEmpty() && vehicle.isEmpty()) {
            return null;
        }
        VehicleDto vehicleDto = new VehicleDto();
        vehicleDto.setVehicleDataInfos(list);
        vehicleDto.setTenantVehicleRelVers(vehicle);
        return vehicleDto;
    }


    @Override
    public String[] getPositionByBillId(String billId, String platNumber) {
        String[] returnInfo = null;

        LambdaQueryWrapper<VehicleDataInfo> vehicleDataInfoLambdaQueryWrapper = new LambdaQueryWrapper<>();
        vehicleDataInfoLambdaQueryWrapper.eq(VehicleDataInfo::getPlateNumber, platNumber);
        List<VehicleDataInfo> vehicleDataInfos = vehicleDataInfoMapper.selectList(vehicleDataInfoLambdaQueryWrapper);

        if (vehicleDataInfos == null || vehicleDataInfos.size() == 0) {
            return returnInfo;
        }
        VehicleDataInfo dataInfo = vehicleDataInfos.get(0);
        Integer locationServ = dataInfo.getLocationServ();
        String position = "";
        if (locationServ != null && locationServ.intValue() == OrderConsts.GPS_TYPE.G7) {
            String equipmentCode = dataInfo.getEquipmentCode();
            if (org.apache.commons.lang.StringUtils.isNotEmpty(equipmentCode)) {
                Object o = redisUtil.get(com.youming.youche.conts.EnumConsts.RemoteCache.Vehicle_G7_Position + equipmentCode);
                if (o != null) {
                    position = o.toString();
                }
            } else {
                throw new BusinessException("????????????[" + platNumber + "]?????????G7?????????????????????????????????????????????G7");
            }
        } else if (locationServ != null && locationServ.intValue() == OrderConsts.GPS_TYPE.YL) {
            Object o = redisUtil.get(com.youming.youche.conts.EnumConsts.RemoteCache.Vehicle_YL_Position + platNumber);
            if (o != null) {
                position = o.toString();
            }
        } else if (locationServ != null && locationServ.intValue() == OrderConsts.GPS_TYPE.BD) {
            Object o = redisUtil.get(com.youming.youche.conts.EnumConsts.RemoteCache.Vehicle_BD_Position + platNumber);
            if (o != null) {
                position = o.toString();
            }
        } else {
            Object o = redisUtil.get(com.youming.youche.conts.EnumConsts.RemoteCache.Vehicle_Gps_Position + billId);
            if (o != null) {
                position = o.toString();
            }
        }
        if (org.apache.commons.lang.StringUtils.isNotEmpty(position)) {
            //??????????????????????????????(key:VGP_????????????,value:??????|??????|??????)
            String[] pos = position.split("\\|");
            if (pos != null && pos.length >= 3) {
                returnInfo = new String[3];
                returnInfo[0] = pos[0];
                returnInfo[1] = pos[1];
                if (pos.length >= 5) {
                    returnInfo[2] = pos[4];
                } else {
                    returnInfo[2] = pos[2];
                }

            }
        }
        return returnInfo;
    }

    @Override
    public VehicleCertInfoDto getTenantVehicleCertRel(String plateNumber) {
        LambdaQueryWrapper<TenantVehicleCertRel> relLambdaQueryWrapper = new LambdaQueryWrapper<>();
        relLambdaQueryWrapper.eq(TenantVehicleCertRel::getPlateNumber, plateNumber);
        List<TenantVehicleCertRel> tenantVehicleCertRels = tenantVehicleCertRelMapper.selectList(relLambdaQueryWrapper);
        if (tenantVehicleCertRels == null || tenantVehicleCertRels.isEmpty()) {
            return null;
        }

        LambdaQueryWrapper<VehicleDataInfo> vehicleQueryWrapper = new LambdaQueryWrapper<>();
        vehicleQueryWrapper.eq(VehicleDataInfo::getPlateNumber, plateNumber);
        List<VehicleDataInfo> listTmp = getBaseMapper().selectList(vehicleQueryWrapper);
        if (listTmp == null || listTmp.isEmpty()) {
            return null;
        }

        VehicleDataInfo vehicleDataInfo = listTmp.get(0);
        TenantVehicleCertRel tenantVehicleCertRel = tenantVehicleCertRels.get(0);

        VehicleCertInfoDto out = new VehicleCertInfoDto();
        try {
            org.apache.commons.beanutils.BeanUtils.copyProperties(out, tenantVehicleCertRel);
            org.apache.commons.beanutils.BeanUtils.copyProperties(out, vehicleDataInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return out;
    }


    @Override
    public void sucessVehicle(Long busiId, String desc, Map paramsMap, LoginInfo user, String tokeng) {
        if (null == busiId) {
            throw new BusinessException("???????????????????????????");
        }
        ApplyRecord applyRecord = applyRecordService.getById(busiId);
        if (null == applyRecord || null == applyRecord.getBusiId()) {
            throw new BusinessException("????????????????????????");
        }
        if (null != applyRecord.getState() && applyRecord.getState().intValue() != APPLY_STATE0 && applyRecord.getState().intValue() != APPLY_STATE3) {
            throw new BusinessException("??????????????????????????????");
        }
        if (null == applyRecord.getApplyVehicleClass()) {
            throw new BusinessException("???????????????????????????");
        }
        int auditState = DataFormat.getIntKey(paramsMap, "auditState");
        applyRecord.setAuditDate(LocalDateTime.now());
        applyRecord.setAuditRemark(desc);
        applyRecord.setAuditOpId(user.getId());
        if (auditState < 0) {
            applyRecord.setState(APPLY_STATE1);
        } else {
            applyRecord.setState(auditState);
        }
        applyRecordService.updateById(applyRecord);
        int vehicleClass = applyRecord.getApplyVehicleClass().intValue();
        Long count = tenantVehicleRelService.getZYCount(applyRecord.getBusiId(), applyRecord.getApplyTenantId(), applyRecord.getApplyVehicleClass());
        if (count == null || count > 0) {
            throw new BusinessException("?????????????????????????????????");
        }

        VehicleDataInfo vehicleDataInfo = getById(applyRecord.getBusiId());
        if (null == vehicleDataInfo) {
            throw new BusinessException("????????????????????????");
        }

        TenantVehicleRel vehicleRel = tenantVehicleRelService.getTenantVehicleRel(vehicleDataInfo.getId(), applyRecord.getApplyTenantId());
        if (vehicleRel != null && vehicleRel.getVehicleClass().intValue() == applyRecord.getApplyVehicleClass().intValue()) {
            throw new BusinessException("??????????????????????????????????????????");
        }

        if (vehicleRel != null && vehicleRel.getVehicleClass().intValue() != applyRecord.getApplyVehicleClass().intValue()) {
            applyRecord.setIsModifyVehicleClass(true);
        } else {
            applyRecord.setIsModifyVehicleClass(false);
        }

        //????????? --> ?????????(???????????????)
        if (vehicleClass == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1 && null != applyRecord.getBeApplyTenantId() && applyRecord.getBeApplyTenantId().longValue() > 0) {
            auditCallBackService.transferTenant(applyRecord, tokeng);
        }
        //??????????????????????????????????????????????????????--APP
        else if (vehicleClass == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1 && (null == applyRecord.getBeApplyTenantId() || applyRecord.getBeApplyTenantId().longValue() <= 0)) {
            auditCallBackService.invitationiJoinOwnApp(applyRecord, tokeng);
        }
        //?????????????????????????????????????????????????????????????????????
        else if ((vehicleClass == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS5 || vehicleClass == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS2 || vehicleClass == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS4) && null != applyRecord.getBeApplyTenantId() && applyRecord.getBeApplyTenantId().longValue() > 0) {
            auditCallBackService.invitationiJoinTmp(applyRecord, tokeng);
        }
        //????????????????????????????????????????????????????????????????????????--APP
        else if ((vehicleClass == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS5 || vehicleClass == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS2 || vehicleClass == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS4) && (null == applyRecord.getBeApplyTenantId() || applyRecord.getBeApplyTenantId().longValue() <= 0)) {
            //invitationiJoinTmpApp(applyRecord);
            auditCallBackService.invitationiJoinTmp(applyRecord, tokeng);
        }
    }

    @Override
    public Boolean sure(String busiCode, Long busiId, String desc, Integer chooseResult, Long instId, Integer type, Long tenantId, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        if (tenantId == null) {
            tenantId = loginInfo.getTenantId();
        }
        AuditNodeInst auditNodeInst = iAuditNodeInstService.queryAuditIng(busiCode, busiId, tenantId);
        if (auditNodeInst == null) {
            throw new BusinessException("???????????????????????????");
        }

        if (instId != null && instId > 0) {
            if (instId.longValue() != auditNodeInst.getId().longValue()) {
                throw new BusinessException("????????????????????????????????????????????????????????????????????????");
            }
        }

        if (AuditConsts.RESULT.SUCCESS == chooseResult) {
            return auditPass(auditNodeInst.getId(), busiCode, busiId, desc, auditNodeInst.getVersion(), auditNodeInst.getRuleVersion(), type, loginInfo.getUserInfoId(), accessToken);
        } else {
            auditFail(auditNodeInst.getId(), busiCode, busiId, desc, loginInfo);
        }
        return false;
    }

    @Override
    public Integer updateEquipmentCode(String plateNumber, String equipmentCode, int equipmentType) {
        return this.updateEquipmentCode(plateNumber, equipmentCode, equipmentType, null);
    }

    @Override
    public Integer updateEquipmentCode(String plateNumber, String equipmentCode, int equipmentType, Integer operatorType) {
        VehicleDataInfo vehicleDataInfo = this.getVehicleDataInfo(plateNumber);
        if (null == vehicleDataInfo) {
            return 0;
        }

        //?????? > G7 > ?????? > app
        if (0 == equipmentType) {
            if (null == operatorType) {
                throw new BusinessException("???????????????????????????????????????operatorType");
            }
            if (!operatorType.equals(vehicleDataInfo.getLocationServ())) {
                //??????????????????????????????
                return 1;
            }
            vehicleDataInfo.setLocationServ(null);
            vehicleDataInfo.setEquipmentCode(null);
            this.update(vehicleDataInfo);
            return 1;
        } else if (OrderConsts.GPS_TYPE.BD == equipmentType) {
            // ????????????????????????????????????
            vehicleDataInfo.setEquipmentCode(equipmentCode);
            vehicleDataInfo.setLocationServ(equipmentType);
            this.update(vehicleDataInfo);
            return 1;
        } else if (OrderConsts.GPS_TYPE.G7 == equipmentType &&
                (null == vehicleDataInfo.getLocationServ() ||
                        vehicleDataInfo.getLocationServ().equals(OrderConsts.GPS_TYPE.G7) ||
                        vehicleDataInfo.getLocationServ().equals(OrderConsts.GPS_TYPE.YL) ||
                        vehicleDataInfo.getLocationServ().equals(OrderConsts.GPS_TYPE.APP))) {
            //G7 ????????????  ?????????app
            vehicleDataInfo.setEquipmentCode(equipmentCode);
            vehicleDataInfo.setLocationServ(equipmentType);
            this.update(vehicleDataInfo);
            return 1;
        } else if (OrderConsts.GPS_TYPE.YL == equipmentType && (
                null == vehicleDataInfo.getLocationServ() ||
                        vehicleDataInfo.getLocationServ().equals(OrderConsts.GPS_TYPE.YL) ||
                        vehicleDataInfo.getLocationServ().equals(OrderConsts.GPS_TYPE.APP))) {
            //YL ????????????App
            vehicleDataInfo.setEquipmentCode(equipmentCode);
            vehicleDataInfo.setLocationServ(equipmentType);
            this.update(vehicleDataInfo);
            return 1;
        } else if (OrderConsts.GPS_TYPE.APP == equipmentType && (
                null == vehicleDataInfo.getLocationServ() ||
                        vehicleDataInfo.getLocationServ().equals(OrderConsts.GPS_TYPE.APP))) {
            vehicleDataInfo.setEquipmentCode(equipmentCode);
            vehicleDataInfo.setLocationServ(equipmentType);
            this.update(vehicleDataInfo);
            return 1;
        }

        return 0;
    }

    @Override
    public VehicleDataInfoVxDto getVehicleByPlateNumberVx(String plateNumber) {
        VehicleDataInfoVxDto numerVx = null;
        if (StringUtils.isNotBlank(plateNumber)) {
            numerVx = vehicleDataInfoMapper.getVehicleByPlateNumerVx(plateNumber);
            if (numerVx != null) {
                Integer vehicleClass = numerVx.getVehicleClass();
                if (vehicleClass == null || vehicleClass < 0) {
                    vehicleClass = SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS5;
                }
                String codeName = readisUtil.getSysStaticData("VEHICLE_CLASS", vehicleClass.toString()).getCodeName();

                numerVx.setVehicleClassName(codeName);
            }
        }
        return numerVx;
    }

    @Override
    public boolean checkVehicleCompleteNess(String plateNumber, int goodsType) {
        VehicleDataInfo vehicleDataInfo = this.getVehicleDataInfo(plateNumber);
        if (vehicleDataInfo == null) {
            return false;
        }
        this.doUpdateVehicleCompleteness(vehicleDataInfo.getId(), vehicleDataInfo.getTenantId());

        if (vehicleDataInfo.getCompleteness() == null) {
            throw new BusinessException("??????????????????????????????????????????????????????");
        }
        String completeNess = "";
        if (vehicleDataInfo.getLicenceType() == null) {
            return false;
        }
        if (vehicleDataInfo.getLicenceType() == SysStaticDataEnum.LICENCE_TYPE.TT) {//??????
            if (goodsType == SysStaticDataEnum.GOODS_TYPE.COMMON_GOODS) {
                completeNess = readisUtil.getSysStaticData("VEHICLE_COMPLETENESS", "3").getCodeName();
            } else {
                completeNess = readisUtil.getSysStaticData("VEHICLE_COMPLETENESS", "4").getCodeName();
            }
        } else {//??????
            completeNess = readisUtil.getSysStaticData("VEHICLE_COMPLETENESS", goodsType + "").getCodeName();
        }

        if (org.apache.commons.lang.StringUtils.isEmpty(completeNess)) {
            throw new BusinessException("????????????????????????????????????????????????");
        }

        char[] config = completeNess.toCharArray();
        char[] completeness = vehicleDataInfo.getCompleteness().toCharArray();

        if (config.length != completeness.length) {
            throw new BusinessException("????????????????????????????????????");
        }

        for (int index = 0; index < config.length; index++) {
            if (config[index] > completeness[index]) {
                LOGGER.info("======?????????????????????======" + vehicleDataInfo.getCompleteness());
                return false;
            }
        }
        return true;
    }

    @Override
    public List<VehicleDataInfoVxVo> getVehicle(String plateNumber, long tenantId, boolean isSelf) {
        List<VehicleDataInfoVxVo> dataInfoVxVos = vehicleDataInfoMapper.getVehicleByPlateNumer(plateNumber, tenantId);
        if (dataInfoVxVos != null && dataInfoVxVos.size() > 0) {
            for (VehicleDataInfoVxVo infoVxVo : dataInfoVxVos) {

                infoVxVo.setCarOwner(infoVxVo.getLinkman());
                infoVxVo.setCarPhone(infoVxVo.getMobilePhone());
                if (null != infoVxVo.getVehicleLength() && infoVxVo.getVehicleStatus() != null) {

                    Integer vehicleStatus = infoVxVo.getVehicleStatus();
                    if (vehicleStatus == 8) {
                        infoVxVo.setVehicleLengthName(readisUtil.getSysStaticData("VEHICLE_STATUS_SUBTYPE", infoVxVo.getVehicleLength().toString()).getCodeName());
                    } else {
                        infoVxVo.setVehicleLengthName(readisUtil.getSysStaticData("VEHICLE_LENGTH", infoVxVo.getVehicleLength().toString()).getCodeName());
                    }
                } else {
                    infoVxVo.setVehicleLengthName("");
                }
                if (null != infoVxVo.getVehicleStatus()) {

                    infoVxVo.setVehicleStatusName(readisUtil.getSysStaticData("VEHICLE_STATUS", infoVxVo.getVehicleStatus().toString()).getCodeName());
                } else {
                    infoVxVo.setVehicleStatusName("");
                }

                infoVxVo.setTenantName(infoVxVo.getName());

//            BigInteger tenantIdRtn = (BigInteger) objs[6];
//            if (null != tenantIdRtn && tenantIdRtn.longValue() == 0) {
//                map.put("tenantId", null);
//
//            } else {
//                map.put("tenantId", tenantIdRtn);
//            }

                //????????????????????????
                Long vehicleCode = infoVxVo.getVehicleCode();
                TenantVehicleRel zyVehicleRel = tenantVehicleRelService.getZYVehicleByVehicleCode(vehicleCode.longValue());
                if (zyVehicleRel != null) {
                    infoVxVo.setTenantId(zyVehicleRel.getTenantId());

                } else {
                    infoVxVo.setTenantId(null);
                    infoVxVo.setTenantName("???????????????");

                    VehicleDataInfo vehicleDataInfo = this.getById(vehicleCode);
                    if (vehicleDataInfo != null && vehicleDataInfo.getTenantId() != null && vehicleDataInfo.getTenantId() > 0) {
                        vehicleDataInfo.setTenantId(null);
                        this.update(vehicleDataInfo);
                    }
                }

//            long tenantRet = DataFormat.getLongKey(map, "tenantId");
//            if (tenantRet < 0) {
//                map.put("tenantName", "???????????????");
//            }

                Integer vehicleClass = infoVxVo.getVehicleClass();
                if (null != vehicleClass) {

                    Long tenantIdTmp = infoVxVo.getTmpTenantId();
                    if (null == tenantIdTmp || tenantIdTmp != tenantId) {
                        infoVxVo.setVehicleClass(VEHICLE_CLASS5);
                        infoVxVo.setVehicleClassName("?????????");
//                    map.put("vehicleClassName",SysStaticDataUtil.getSysStaticDataCodeName("VEHICLE_CLASS",VEHICLE_CLASS5+""));

                    } else {
                        infoVxVo.setVehicleClass(vehicleClass);
                        infoVxVo.setVehicleClassName(readisUtil.getSysStaticData("VEHICLE_CLASS", vehicleClass + "").getCodeName());

                    }
                    if (zyVehicleRel != null) {//?????????????????? ????????????????????????
                        SysTenantDef sysTenantDef = sysTenantDefService.getById(zyVehicleRel.getTenantId());

                        if ((sysTenantDef != null && sysTenantDef.getState().intValue() == SysStaticDataEnum.SYS_STATE_DESC.STATE_NO)) {
                            infoVxVo.setTenantId(null);
                            infoVxVo.setTenantName(null);

//                        map.put("vehicleClass", VEHICLE_CLASS5);
//                        map.put("vehicleClassName", SysStaticDataUtil.getSysStaticDataCodeName("VEHICLE_CLASS", VEHICLE_CLASS5 + ""));
                        }
                    }
                } else {
                    infoVxVo.setVehicleClass(VEHICLE_CLASS5);
                    infoVxVo.setVehicleClassName("?????????");
                }
                infoVxVo.setUserId(infoVxVo.getDriverUserId());


                infoVxVo.setCopilotCarOwner(infoVxVo.getCopilotLinkman());
                infoVxVo.setCopilotCarPhone(infoVxVo.getLinkmanMobilePhone());


                if (infoVxVo.getBillReceiverMobile() != null && infoVxVo.getBillReceiverUserId() == null) {
                    String billReceiverMobile = infoVxVo.getBillReceiverMobile();

                    UserDataInfo userDataInfo = userDataInfoRecordMapper.getUserDataInfoByMoblile(billReceiverMobile);
                    if (userDataInfo != null) {
                        infoVxVo.setBillReceiverUserId(userDataInfo.getId());
                        infoVxVo.setBillReceiverName(userDataInfo.getLinkman());
                    }
                }
            }
        }
        return dataInfoVxVos;
    }

    @Override
    public List<DriverDataInfoDto> doQueryCarDriver(String loginAcct, Long tenantId) {
        return vehicleDataInfoMapper.doQueryCarDriver(loginAcct, tenantId);
    }

    @Override
    public StaffDataInfoDto getStaffInfoByUserId(Long userId, Long tenantId) {
        StaffDataInfoVo vo = new StaffDataInfoVo();
        vo.setUserId(userId);
        List<StaffDataInfoDto> list = userDataInfoRecordMapper.queryStaffInfo(vo, tenantId);
        if (CollectionUtils.isNotEmpty(list)) {
            StaffDataInfoDto map = list.get(0);
            StaffDataInfoDto staffDataInfoOut = new StaffDataInfoDto();
            BeanUtil.copyProperties(staffDataInfoOut, map);
            UserDataInfo userDataInfo = iUserDataInfoRecordService.getUserDataInfo(staffDataInfoOut.getUserId());
            if (userDataInfo != null) {
                staffDataInfoOut.setUserPrice(userDataInfo.getUserPrice());
                staffDataInfoOut.setUserPriceUrl(userDataInfo.getUserPriceUrl());
            }
            return staffDataInfoOut;
        }

        return null;
    }

    @Override
    public Page<VehicleDataInfoVxVo> getVehiclePage(Page<VehicleDataInfoVxVo> page, String plateNumber, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        Page<VehicleDataInfoVxVo> dataInfoVxVos = vehicleDataInfoMapper.getVehicleByPlateNumerPage(page, plateNumber, loginInfo.getTenantId());

        if (dataInfoVxVos != null && dataInfoVxVos.getTotal() > 0) {
            List<VehicleDataInfoVxVo> records = dataInfoVxVos.getRecords();
            for (VehicleDataInfoVxVo infoVxVo : records) {

                infoVxVo.setCarOwner(infoVxVo.getLinkman());
                infoVxVo.setCarPhone(infoVxVo.getMobilePhone());
                if (null != infoVxVo.getVehicleLength() && infoVxVo.getVehicleStatus() != null) {

                    Integer vehicleStatus = infoVxVo.getVehicleStatus();
                    if (vehicleStatus == 8) {
                        infoVxVo.setVehicleLengthName(readisUtil.getSysStaticData("VEHICLE_STATUS_SUBTYPE", infoVxVo.getVehicleLength().toString()).getCodeName());
                    } else {
                        infoVxVo.setVehicleLengthName(readisUtil.getSysStaticData("VEHICLE_LENGTH", infoVxVo.getVehicleLength().toString()).getCodeName());
                    }
                } else {
                    infoVxVo.setVehicleLengthName("");
                }
                if (null != infoVxVo.getVehicleStatus()) {

                    infoVxVo.setVehicleStatusName(readisUtil.getSysStaticData("VEHICLE_STATUS", infoVxVo.getVehicleStatus().toString()).getCodeName());
                } else {
                    infoVxVo.setVehicleStatusName("");
                }

                infoVxVo.setTenantName(infoVxVo.getName());

                //????????????????????????
                Long vehicleCode = infoVxVo.getVehicleCode();
                TenantVehicleRel zyVehicleRel = tenantVehicleRelService.getZYVehicleByVehicleCode(vehicleCode.longValue());
                if (zyVehicleRel != null) {
                    infoVxVo.setTenantId(zyVehicleRel.getTenantId());

                } else {
                    infoVxVo.setTenantId(null);
                    infoVxVo.setTenantName("???????????????");

                    VehicleDataInfo vehicleDataInfo = this.getById(vehicleCode);
                    if (vehicleDataInfo != null && vehicleDataInfo.getTenantId() != null && vehicleDataInfo.getTenantId() > 0) {
                        vehicleDataInfo.setTenantId(null);
                        this.update(vehicleDataInfo);
                    }
                }

                Integer vehicleClass = infoVxVo.getVehicleClass();
                if (null != vehicleClass) {

                    Long tenantIdTmp = infoVxVo.getTmpTenantId();
                    if (null == tenantIdTmp || tenantIdTmp != loginInfo.getTenantId()) {
                        infoVxVo.setVehicleClass(VEHICLE_CLASS5);
                        infoVxVo.setVehicleClassName("?????????");
                    } else {
                        infoVxVo.setVehicleClass(vehicleClass);
                        infoVxVo.setVehicleClassName(readisUtil.getSysStaticData("VEHICLE_CLASS", vehicleClass + "").getCodeName());
                    }
                    if (zyVehicleRel != null) {//?????????????????? ????????????????????????
                        SysTenantDef sysTenantDef = sysTenantDefService.getById(zyVehicleRel.getTenantId());

                        if ((sysTenantDef != null && sysTenantDef.getState() == SysStaticDataEnum.SYS_STATE_DESC.STATE_NO)) {
                            infoVxVo.setTenantId(null);
                            infoVxVo.setTenantName(null);

                        }
                    }
                } else {
                    infoVxVo.setVehicleClass(VEHICLE_CLASS5);
                    infoVxVo.setVehicleClassName("?????????");
                }
                infoVxVo.setUserId(infoVxVo.getDriverUserId());

                infoVxVo.setCopilotCarOwner(infoVxVo.getCopilotLinkman());
                infoVxVo.setCopilotCarPhone(infoVxVo.getLinkmanMobilePhone());

                if (infoVxVo.getBillReceiverMobile() != null && infoVxVo.getBillReceiverUserId() == null) {
                    String billReceiverMobile = infoVxVo.getBillReceiverMobile();

                    UserDataInfo userDataInfo = userDataInfoRecordMapper.getUserDataInfoByMoblile(billReceiverMobile);
                    if (userDataInfo != null) {
                        infoVxVo.setBillReceiverUserId(userDataInfo.getId());
                        infoVxVo.setBillReceiverName(userDataInfo.getLinkman());
                    }
                }
            }
            dataInfoVxVos.setRecords(records);
        }
        return dataInfoVxVos;
    }

    @Override
    public VehicleCountDto doQueryVehicleCountByDriver(Long driverUserId) {
        if (driverUserId < 0) {
            throw new BusinessException("?????????????????????");
        }
        List<Map> rtnList = baseMapper.getVehicleByDriverUserId(driverUserId);
        VehicleCountDto dto = new VehicleCountDto();
        dto.setAuthState(AUTH_STATE2);
        dto.setAuthStateName(getSysStaticData("CUSTOMER_AUTH_STATE", AUTH_STATE2 + "").getCodeName());
        for (int i = 0; i < rtnList.size(); i++) {
            Map rtnMap = rtnList.get(i);
            int authState = DataFormat.getIntKey(rtnMap, "authState");
            if (authState < 0) {
                int sysAuthState = DataFormat.getIntKey(rtnMap, "sysAuthState");
                rtnMap.put("authState", sysAuthState);
                String sysAuditContent = DataFormat.getStringKey(rtnMap, "sysAuditContent");
                rtnMap.put("auditContent", sysAuditContent);
                int isAuth = DataFormat.getIntKey(rtnMap, "sysIsAuth");
                rtnMap.put("isAuth", isAuth);
            }
            int authStateRtn = DataFormat.getIntKey(rtnMap, "authState");
            dto.setAuthStateName(getSysStaticData("CUSTOMER_AUTH_STATE", authStateRtn + "").getCodeName());
            if (authStateRtn != AUTH_STATE2) {
                dto.setRtnMap(rtnMap);
                break;
            }
        }
//        long vehicleCount = this.getVehicleCountByDriverUserId(driverUserId);
        List<QueryVehicleByDriverDto> queryVehicleByDriverDtos = this.doQueryVehicleByDriver(driverUserId);
        if (queryVehicleByDriverDtos != null && queryVehicleByDriverDtos.size()>0) {
            dto.setVehicleCount(Long.parseLong(queryVehicleByDriverDtos.size() + ""));
        }

        return dto;
    }

    @Override
    public Long getVehicleCountByDriverUserId(Long driverUserId) {
        LambdaQueryWrapper<VehicleDataInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(VehicleDataInfo::getDriverUserId, driverUserId);
        int count = this.count(queryWrapper);
        return Long.valueOf(count);
    }

    @Transactional
    @Override
    public void doSaveVehicle(SaveVehicleAppVo vo, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);

        if (StringUtils.isBlank(vo.getPlateNumber()) || !isCarNo(vo.getPlateNumber())) {
            throw new BusinessException("????????????????????????");
        }

        if (vo.getDriverUserId() == null || vo.getDriverUserId() < 0) {
            throw new BusinessException("?????????????????????");
        }

        long count = this.getVehicleDataInfoByPlateNumber(vo.getPlateNumber());
        if (count > 0) {
            throw new BusinessException("???????????????????????????????????????");
        }

        if (StringUtils.isBlank(vo.getVehiclePicture())) {
            throw new BusinessException("????????????????????????");
        }
        if (StringUtils.isBlank(vo.getDrivingLicense())) {
            throw new BusinessException("???????????????????????????????????????");
        }
        if (StringUtils.isBlank(vo.getAdriverLicenseCopy())) {
            throw new BusinessException("???????????????????????????????????????");
        }

        if (vo.getLicenceType() == null || vo.getLicenceType() < 0) {
            throw new BusinessException("????????????????????????");
        }

        if (vo.getLicenceType() != null && vo.getLicenceType() == SysStaticDataEnum.LICENCE_TYPE.ZC) {
            if (vo.getVehicleStatus() == null || vo.getVehicleStatus() < 0) {
                throw new BusinessException("????????????????????????");
            }
            if (StringUtils.isBlank(vo.getVehicleLength()) || "-1".equals(vo.getVehicleLength())) {
                throw new BusinessException("????????????????????????");
            }
            if (vo.getVehicleLoad() == null || vo.getVehicleLoad() < 0) {
                throw new BusinessException("???????????????????????????");
            }
            if (StringUtils.isBlank(vo.getLightGoodsSquare())) {
                throw new BusinessException("??????????????????");
            }
        }

        if (StringUtils.isBlank(vo.getVinNo())) {
            throw new BusinessException("?????????????????????");
        }

        if (StringUtils.isBlank(vo.getEngineNo())) {
            throw new BusinessException("????????????????????????");
        }

        if (StringUtils.isBlank(vo.getBrandModel())) {
            throw new BusinessException("????????????????????????");
        }

        if (StringUtils.isBlank(vo.getDrivingLicenseOwner())) {
            throw new BusinessException("?????????????????????????????????");
        }

        VehicleDataInfo saveVehicleData = new VehicleDataInfo();
        VehicleDataInfoVer saveVehicleDataVer = new VehicleDataInfoVer();
        BeanUtil.copyProperties(vo, saveVehicleData);

        if (saveVehicleData.getLicenceType() == SysStaticDataEnum.LICENCE_TYPE.TT) {
            saveVehicleData.setVehicleStatus(null);
            saveVehicleData.setVehicleLength(null);
            saveVehicleData.setVehicleLoad(null);
            saveVehicleData.setLightGoodsSquare(null);
        }

        saveVehicleData.setAuthState(AUTH_STATE1);
        saveVehicleData.setIsAuth(IS_AUTH1);
        saveVehicleData.setTenantId(null);
        saveVehicleData.setCreateTime(LocalDateTime.now());
        saveVehicleData.setCreateDate(LocalDateTime.now());

        if (!"".equals(vo.getBillReceiverMobile())) {
            saveVehicleData.setAttachUserMobile(vo.getBillReceiverMobile());
            saveVehicleData.setAttachUserName(vo.getBillReceiverName());
        }

        if (vo.getBillReceiverUserId() != null && vo.getBillReceiverUserId() > 0) {
            saveVehicleData.setAttachUserId(vo.getBillReceiverUserId());
        }

        this.saveOrUpdate(saveVehicleData);
        BeanUtil.copyProperties(saveVehicleData, saveVehicleDataVer);
        saveVehicleDataVer.setAdrivingLicenseCopy(Long.valueOf(vo.getAdriverLicenseCopy()));
        saveVehicleDataVer.setVerState(VER_STATE_Y);
        saveVehicleDataVer.setCreateTime(LocalDateTime.now());
        iVehicleDataInfoVerService.saveOrUpdate(saveVehicleDataVer);

        List<TenantUserRel> list = tenantUserRelService.getTenantUserRelsAllTenant(vo.getDriverUserId(), SysStaticDataEnum.CAR_USER_TYPE.OWN_CAR);

        //???????????????
        //?????????????????????????????????????????????????????????????????????????????????????????????
        //??????????????????????????????????????????????????????????????????????????????????????????
        if (list != null && list.size() > 0) {

            TenantUserRel tenantUserRel = list.get(0);

            //???????????????????????????????????????????????????
            if (vo.getBillReceiverUserId() != null && vo.getBillReceiverUserId() > 0) {
                LambdaQueryWrapper<SysTenantDef> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(SysTenantDef::getAdminUser, vo.getBillReceiverUserId());
                queryWrapper.eq(SysTenantDef::getId, tenantUserRel.getTenantId());
                List<SysTenantDef> sysTenantDefList = sysTenantDefService.list(queryWrapper);
                if (sysTenantDefList != null && sysTenantDefList.size() > 0) {
                    throw new BusinessException("????????????????????????????????????????????????????????????");
                }
            }

            saveVehicleData.setTenantId(tenantUserRel.getTenantId());
            saveVehicleData.setDriverUserId(vo.getDriverUserId());
            this.saveOrUpdate(saveVehicleData);

            BeanUtil.copyProperties(saveVehicleData, saveVehicleDataVer);
            saveVehicleDataVer.setVehicleCode(saveVehicleData.getId());
            saveVehicleDataVer.setVerState(VER_STATE_Y);
            saveVehicleDataVer.setCreateTime(LocalDateTime.now());
            iVehicleDataInfoVerService.saveOrUpdate(saveVehicleDataVer);

            TenantVehicleRel tenantVehicleRel = new TenantVehicleRel();
            TenantVehicleRelVer tenantVehicleRelVer = new TenantVehicleRelVer();
            BeanUtil.copyProperties(vo, tenantVehicleRel);
            tenantVehicleRel.setPlateNumber(vo.getPlateNumber());
            tenantVehicleRel.setVehicleClass(tenantUserRel.getCarUserType());
            tenantVehicleRel.setTenantId(tenantUserRel.getTenantId());
            tenantVehicleRel.setAuthState(AUTH_STATE1);
            tenantVehicleRel.setIsAuth(IS_AUTH1);
            tenantVehicleRel.setShareFlg(SysStaticDataEnum.SHARE_FLG.NO);
            tenantVehicleRel.setVehicleCode(saveVehicleData.getId());
            tenantVehicleRel.setDriverUserId(vo.getDriverUserId());
            tenantVehicleRel.setCreateTime(LocalDateTime.now());
            tenantVehicleRelService.save(tenantVehicleRel);
            BeanUtil.copyProperties(tenantVehicleRel, tenantVehicleRelVer);
            tenantVehicleRelVer.setRelId(tenantVehicleRel.getId());
            tenantVehicleRelVer.setVerState(VER_STATE_Y);
            tenantVehicleRelVer.setVehicleHisId(saveVehicleDataVer.getId());
            iTenantVehicleRelVerService.save(tenantVehicleRelVer);

            saveSysOperLog(SysOperLogConst.BusiCode.Vehicle, tenantVehicleRel.getId(), SysOperLogConst.OperType.Add, "????????????APP??????", accessToken);

            //??????????????????
            Map inMap = new HashMap();
            inMap.put("svName", "vehicleTF");
            inMap.put("tenantVehicleRelId", tenantVehicleRel.getId());
            inMap.put("tenantVehicleCostRelId", tenantVehicleRel.getId());

            boolean bool = iAuditService.startProcess(AUDIT_CODE_VEHICLE, tenantVehicleRel.getId(), SysOperLogConst.BusiCode.Vehicle, inMap, accessToken, tenantVehicleRel.getTenantId());
            if (!bool) {
                throw new BusinessException("???????????????????????????");
            }

        } else {

            //??????????????????
            if (vo.getAutoAudit() != null && vo.getAutoAudit()) {
                //?????????OCR??????????????????????????????
                saveVehicleData.setAuthState(AUTH_STATE2);
                saveVehicleData.setIsAuth(IS_AUTH0);
                this.save(saveVehicleData);
                saveVehicleDataVer.setVerState(VER_STATE_Y);
                iVehicleDataInfoVerService.saveOrUpdate(saveVehicleDataVer);
            } else {
                boolean isAutoAudit = SysCfgUtil.getCfgBooleanVal("IS_AUTO_AUDIT", accessToken, loginUtils, redisUtil);
                if (isAutoAudit) {
                    saveVehicleData.setAuthState(AUTH_STATE2);
                    saveVehicleData.setIsAuth(IS_AUTH0);
                    this.saveOrUpdate(saveVehicleData);
                    saveVehicleDataVer.setVerState(VER_STATE_Y);
                    iVehicleDataInfoVerService.saveOrUpdate(saveVehicleDataVer);
                    //?????????
                    saveSysOperLog(SysOperLogConst.BusiCode.Vehicle, saveVehicleData.getId(), SysOperLogConst.OperType.Audit, "????????????APP????????????", accessToken);
                }
            }

            //3.0???????????????????????????????????????
            TenantVehicleRel tenantVehicleRel = new TenantVehicleRel();
            BeanUtil.copyProperties(vo, tenantVehicleRel);
            tenantVehicleRel.setPlateNumber(vo.getPlateNumber());
            tenantVehicleRel.setVehicleClass(SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS5);
            tenantVehicleRel.setTenantId(SysStaticDataEnum.PT_TENANT_ID);
            tenantVehicleRel.setAuthState(AUTH_STATE2);
            tenantVehicleRel.setIsAuth(IS_AUTH0);
            tenantVehicleRel.setShareFlg(SysStaticDataEnum.SHARE_FLG.YES);
            tenantVehicleRel.setVehicleCode(saveVehicleData.getId());
            tenantVehicleRel.setPlateNumber(saveVehicleData.getPlateNumber());
            tenantVehicleRel.setDriverUserId(vo.getDriverUserId());
            tenantVehicleRel.setCreateTime(LocalDateTime.now());
            tenantVehicleRelService.save(tenantVehicleRel);

            this.doSaveVehicleOrderPositionInfo(saveVehicleData.getId(), saveVehicleData.getPlateNumber());

            //?????????????????????
            this.doUpdateVehicleCompleteness(saveVehicleData.getId(), loginInfo.getTenantId());

        }

        //????????????
        List<LineDataDto> lineDataList = vo.getLineData();
        VehicleObjectLine vehicleObjectLine = null;
        VehicleObjectLineVer vehicleObjectLineVer = null;
        for (int i = 0; i < 3; i++) {//3???

            //??????????????????????????????????????????3?????????????????????
            LambdaQueryWrapper<VehicleObjectLine> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(VehicleObjectLine::getVehicleCode, saveVehicleData.getId());
            List<VehicleObjectLine> paramList = vehicleObjectLineService.list(queryWrapper);
            if (paramList != null && paramList.size() > 2) {
                break;
            }

            vehicleObjectLine = new VehicleObjectLine();
            vehicleObjectLineVer = new VehicleObjectLineVer();

            LineDataDto tranMap = null;
            try {
                tranMap = lineDataList.get(i);//???????????????3??????
            } catch (Exception e) {
                tranMap = new LineDataDto();
            }

            //????????????
            if (tranMap.getSourceProvince() != null && tranMap.getSourceProvince() > -1) {
                vehicleObjectLine.setSourceProvince(tranMap.getSourceProvince());
            }

            if (tranMap.getSourceRegion() != null && tranMap.getSourceRegion() > -1) {
                vehicleObjectLine.setSourceRegion(tranMap.getSourceRegion());
            }

            if (tranMap.getSourceCounty() != null && tranMap.getSourceCounty() > -1) {
                vehicleObjectLine.setSourceCounty(tranMap.getSourceCounty());
            }

            //????????????
            if (tranMap.getDesProvince() != null && tranMap.getDesProvince() > -1) {
                vehicleObjectLine.setDesProvince(tranMap.getDesProvince());
            }

            if (tranMap.getDesRegion() != null && tranMap.getDesRegion() > -1) {
                vehicleObjectLine.setDesRegion(tranMap.getDesRegion());
            }

            if (tranMap.getDesCounty() != null && tranMap.getDesCounty() > -1) {
                vehicleObjectLine.setDesCounty(tranMap.getDesCounty());
            }

            //??????
            if (tranMap.getCarriagePrice() != null && CommonUtil.isNumber(tranMap.getCarriagePrice())) {
                Number num = Double.parseDouble(tranMap.getCarriagePrice());
                vehicleObjectLine.setCarriagePrice(num.longValue());
            }

            //????????????
            vehicleObjectLine.setTenantId(null);
            vehicleObjectLine.setVehicleCode(saveVehicleData.getId());
            vehicleObjectLineService.save(vehicleObjectLine);
            BeanUtil.copyProperties(vehicleObjectLine, vehicleObjectLineVer);
            vehicleObjectLineVer.setVerState(VER_STATE_Y);
            vehicleObjectLineVer.setVehicleHisId(saveVehicleDataVer.getId());
            vehicleObjectLineVerService.save(vehicleObjectLineVer);
        }

        //??????????????????
        if (vo.getBillReceiverUserId() != null && vo.getBillReceiverUserId() > 0) {
            SysTenantDef tenantInfo = sysTenantDefService.getSysTenantDefByAdminUserId(vo.getBillReceiverUserId());
            if (tenantInfo != null && !tenantInfo.getState().equals(SysStaticDataEnum.SYS_STATE_DESC.STATE_NO)) {
                TenantVehicleRel vehicleRel = tenantVehicleRelService.getTenantVehicleRel(saveVehicleData.getId(), tenantInfo.getId());
                if (vehicleRel == null) {
                    addAttachVehicleAndDriver(saveVehicleData, tenantInfo.getId(), 2, accessToken);
                }
            }
        }

    }

    @Override
    public Long getVehicleDataInfoByPlateNumber(String plateNumber) {
        LambdaQueryWrapper<VehicleDataInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(VehicleDataInfo::getPlateNumber, plateNumber);
        return Long.valueOf(this.count(queryWrapper));
    }

    @Override
    public List<QueryTenantDto> doQueryTenantByVehicleCodeApp(Long vehicleCode, Long driverUserId) {

        List<TenantUserRel> list = tenantUserRelService.getTenantUserRelsAllTenant(driverUserId, SysStaticDataEnum.CAR_USER_TYPE.OWN_CAR);

        int vehicleClass = VEHICLE_CLASS1; // ??????????????????????????????????????????
        if (null == list || list.isEmpty()) {
            vehicleClass = VEHICLE_CLASS5;
        }

        Map<Long, Long> orderCountMap = null;
        if (vehicleClass == VEHICLE_CLASS5) { // ??????????????????????????????
            orderCountMap = this.getOrderCountByVehicleCode(vehicleCode);
        }


        List<QueryTenantDto> queryTenantDtos = baseMapper.doQueryTenantByVehicleCodeApp(vehicleCode);

        for (QueryTenantDto queryTenantDto : queryTenantDtos) {

            int vehicleClassRtn = queryTenantDto.getVehicleClass();
            long tenantId = queryTenantDto.getTenantId();
            if (vehicleClassRtn > -1) {
                queryTenantDto.setVehicleClassName(getSysStaticData("VEHICLE_CLASS", "" + vehicleClassRtn).getCodeName());
            }
            if ((vehicleClassRtn == VEHICLE_CLASS2 || vehicleClassRtn == VEHICLE_CLASS4 || vehicleClassRtn == VEHICLE_CLASS5)) {
                if (orderCountMap == null) {
                    queryTenantDto.setOrderCount(0L);
                } else {
                    queryTenantDto.setOrderCount(orderCountMap.get(tenantId) == null ? 0L : orderCountMap.get(tenantId));
                }
            } else {
                Long orderCount = this.getOrderCountByVehicleCode(vehicleCode, tenantId);
                queryTenantDto.setOrderCount(orderCount == null ? 0L : orderCount);
            }
        }

        return queryTenantDtos;
    }

    @Override
    public Map<Long, Long> getOrderCountByVehicleCode(Long vehicleCode) {
        Map<Long, Long> returnMap = new HashMap<Long, Long>();

        List<OrderCountDto> orderCountByVehicleCode = baseMapper.getOrderCountByVehicleCode(vehicleCode);
        if (orderCountByVehicleCode != null && orderCountByVehicleCode.size() > 0) {
            for (OrderCountDto object : orderCountByVehicleCode) {
                returnMap.put(object.getTenantId(), object.getCont());
            }
        }

        List<OrderCountDto> orderCountHByVehicleCode = baseMapper.getOrderCountHByVehicleCode(vehicleCode);
        if (orderCountHByVehicleCode != null && orderCountHByVehicleCode.size() > 0) {
            for (OrderCountDto object : orderCountHByVehicleCode) {
                if (returnMap.get(object.getTenantId()) != null) {
                    returnMap.put(object.getTenantId(), object.getCont() + returnMap.get(object.getCont()));
                } else {
                    returnMap.put(object.getTenantId(), object.getCont());
                }
            }
        }

        return returnMap;
    }

    @Override
    public Long getOrderCountByVehicleCode(Long vehicleCode, Long tenantId) {
        Long orderCountLByVehicleCode = baseMapper.getOrderCountLByVehicleCode(vehicleCode, tenantId);
        Long orderCountLHByVehicleCode = baseMapper.getOrderCountLHByVehicleCode(vehicleCode, tenantId);
        return orderCountLByVehicleCode + orderCountLHByVehicleCode;
    }

    @Override
    public void doUpdateVehicle(UpdateVehicleVo vo, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);

        VehicleDataInfoVer saveVehicleDataVer = new VehicleDataInfoVer();

        Long vehicleCode = vo.getVehicleCode();
        String plateNumber = vo.getPlateNumber();
        if (StringUtils.isBlank(plateNumber) || !isCarNo(plateNumber)) {
            throw new BusinessException("????????????????????????");
        }

        VehicleDataInfo vehicleDataInfo = this.getById(vehicleCode);
        if (null == vehicleDataInfo) {
            throw new BusinessException("?????????????????????");
        }

        if (vo.getDriverUserId() < 0) {
            throw new BusinessException("?????????????????????");
        }

        if (org.apache.commons.lang.StringUtils.isBlank(vo.getVehiclePicture())) {
            throw new BusinessException("????????????????????????");
        }

        if (org.apache.commons.lang.StringUtils.isBlank(vo.getDrivingLicense())) {
            throw new BusinessException("???????????????????????????????????????");
        }

        if (org.apache.commons.lang.StringUtils.isBlank(vo.getAdriverLicenseCopy())) {
            throw new BusinessException("???????????????????????????????????????");
        }

        if (vo.getLicenceType() == null || vo.getLicenceType() < 0) {
            throw new BusinessException("????????????????????????");
        }

        if (vo.getLicenceType() == SysStaticDataEnum.LICENCE_TYPE.ZC) {

            if (vo.getVehicleStatus() == null || vo.getVehicleStatus() < 0) {
                throw new BusinessException("????????????????????????");
            }

            if (StringUtils.isBlank(vo.getVehicleLength()) || "-1".equals(vo.getVehicleLength())) {
                throw new BusinessException("????????????????????????");
            }

            if (vo.getVehicleLoad() == null || vo.getVehicleLoad() < 0) {
                throw new BusinessException("???????????????????????????");
            }

            if (StringUtils.isBlank(vo.getLightGoodsSquare())) {
                throw new BusinessException("??????????????????");
            }

        }

        if (StringUtils.isBlank(vo.getVinNo())) {
            throw new BusinessException("?????????????????????");
        }

        if (StringUtils.isBlank(vo.getEngineNo())) {
            throw new BusinessException("????????????????????????");
        }
//        String drivingLicenseSn = vo.getDrivingLicenseSn();
//        String operCerti = vo.getOperCerti();

        if (org.apache.commons.lang.StringUtils.isBlank(vo.getBrandModel())) {
            throw new BusinessException("????????????????????????");
        }

        if (org.apache.commons.lang.StringUtils.isBlank(vo.getDrivingLicenseOwner())) {
            throw new BusinessException("?????????????????????????????????");
        }

        //?????????????????????
//        long billReceiverUserId = vo.getBillReceiverUserId();
//        String billReceiverMobile = vo.getBillReceiverMobile();
//        String billReceiverName = vo.getBillReceiverName();

        Map clearVerDataParamMap = new HashMap();
        clearVerDataParamMap.put("vehicleCode", vehicleCode);
        clearVerDataParamMap.put("destVerState", VER_STATE_N);

        //????????????vehicle_data_info_ver??????ver_state???????????????(0)
        this.updateVehicleVerAllVerState("vehicle_data_info_ver", clearVerDataParamMap);

        BeanUtil.copyProperties(vehicleDataInfo, saveVehicleDataVer);
        BeanUtil.copyProperties(vo, saveVehicleDataVer);
        saveVehicleDataVer.setAdrivingLicenseCopy(Long.valueOf(vo.getAdriverLicenseCopy()));

        if (saveVehicleDataVer.getLicenceType() == SysStaticDataEnum.LICENCE_TYPE.TT) {
            saveVehicleDataVer.setVehicleStatus(null);
            saveVehicleDataVer.setVehicleLength(null);
            saveVehicleDataVer.setVehicleLoad(null);
            saveVehicleDataVer.setLightGoodsSquare(null);
        }

        //???????????????????????????
        if (!"".equals(vo.getBillReceiverMobile())) {
            saveVehicleDataVer.setAttachUserMobile(vo.getBillReceiverMobile());
            saveVehicleDataVer.setAttachUserName(vo.getBillReceiverName());
        }

        if (vo.getBillReceiverUserId() != null && vo.getBillReceiverUserId() > 0) {
            saveVehicleDataVer.setAttachUserId(vo.getBillReceiverUserId());
        }

        //saveVehicleDataVer.setTenantId(null);
        saveVehicleDataVer.setVerState(VER_STATE_Y);
        saveVehicleDataVer.setCreateTime(LocalDateTime.now());
        iVehicleDataInfoVerService.save(saveVehicleDataVer);
        vehicleDataInfo.setIsAuth(IS_AUTH1);
        this.saveOrUpdate(vehicleDataInfo);

        boolean unNeedCheck = true;

        //?????????????????????????????????????????????????????????????????????????????????????????????
        //??????????????????????????????????????????????????????????????????????????????????????????
        List<TenantUserRel> list = tenantUserRelService.getTenantUserRelsAllTenant(vo.getDriverUserId(), SysStaticDataEnum.CAR_USER_TYPE.OWN_CAR);

        TenantVehicleRel vehicleRel = tenantVehicleRelService.getZYVehicleByPlateNumber(saveVehicleDataVer.getPlateNumber());

        if (vehicleRel != null) {//?????????????????????????????????

            clearVerDataParamMap.put("tenantId", vehicleRel.getTenantId());
            this.updateVehicleVerAllVerState("tenant_vehicle_rel_ver", clearVerDataParamMap);

            TenantVehicleRelVer tenantVehicleRelVer = new TenantVehicleRelVer();
            BeanUtil.copyProperties(vehicleRel, tenantVehicleRelVer);
            BeanUtil.copyProperties(vo, tenantVehicleRelVer);
            tenantVehicleRelVer.setRelId(vehicleRel.getId());
            tenantVehicleRelVer.setCreateTime(LocalDateTime.now());
            tenantVehicleRelVer.setVerState(VER_STATE_Y);
            tenantVehicleRelVer.setVehicleHisId(saveVehicleDataVer.getId());
            iTenantVehicleRelVerService.save(tenantVehicleRelVer);

            vehicleRel.setIsAuth(IS_AUTH1);
            tenantVehicleRelService.saveOrUpdate(vehicleRel);

            if (vehicleRel.getAuthState() != null && SysStaticDataEnum.AUTH_STATE.AUTH_STATE2 != vehicleRel.getAuthState()) {
                BeanUtil.copyProperties(vo, vehicleDataInfo);
                vehicleDataInfo.setIsAuth(IS_AUTH1);//????????????,?????????????????????
                this.saveOrUpdate(vehicleDataInfo);
            }

            saveSysOperLog(SysOperLogConst.BusiCode.Vehicle, vehicleRel.getId(), SysOperLogConst.OperType.Update, "????????????APP??????", accessToken);

            //??????????????????
            Map inMap = new HashMap();
            inMap.put("svName", "vehicleTF");
            inMap.put("tenantVehicleRelId", vehicleRel.getId());
            inMap.put("tenantVehicleCostRelId", vehicleRel.getId());

            boolean bool = iAuditService.startProcess(AUDIT_CODE_VEHICLE, vehicleRel.getId(), SysOperLogConst.BusiCode.Vehicle, inMap, accessToken, vehicleRel.getTenantId());
            if (!bool) {
                throw new BusinessException("???????????????????????????");
            }

            unNeedCheck = false;

        } else {
            //App????????????,??????????????????????????? 2018-11-27
            BeanUtil.copyProperties(vo, vehicleDataInfo);

            //??????????????????
            vehicleDataInfo.setAttachUserId(saveVehicleDataVer.getAttachUserId());
            vehicleDataInfo.setAttachUserName(saveVehicleDataVer.getAttachUserName());
            vehicleDataInfo.setAttachUserMobile(saveVehicleDataVer.getAttachUserMobile());

            vehicleDataInfo.setIsAuth(IS_AUTH0);
            this.update(vehicleDataInfo);

            //??????ver??????????????????????????????
            saveVehicleDataVer.setIsAuthSucc(3);//??????????????????
            iVehicleDataInfoVerService.updateById(saveVehicleDataVer);

            //C????????????????????????
            saveSysOperLog(SysOperLogConst.BusiCode.Vehicle, vehicleCode, SysOperLogConst.OperType.Update, "????????????APP??????,?????????????????????????????????", accessToken);

            //???????????????????????????,?????????????????????OCR??????
            boolean isAutoAudit = SysCfgUtil.getCfgBooleanVal("IS_AUTO_AUDIT", accessToken, loginUtils, redisUtil);
            if (isAutoAudit) {
                vehicleDataInfo.setIsAuth(IS_AUTH0);
                this.save(vehicleDataInfo);
                saveVehicleDataVer.setVerState(VER_STATE_N);
                iVehicleDataInfoVerService.save(saveVehicleDataVer);
                //?????????
                saveSysOperLog(SysOperLogConst.BusiCode.Vehicle, vehicleDataInfo.getId(), SysOperLogConst.OperType.Audit, "????????????APP??????????????????", accessToken);
            }
            this.doUpdateVehicleCompleteness(vehicleDataInfo.getId(), loginInfo.getTenantId());
        }

        //??????????????????
        List<VehicleObjectLineVo> lineDataList = vo.getLineData();
        String vehicleObjectLineVerHisIds = "";
        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(lineDataList)) {
            //????????????vehicle_object_line_ver??????ver_state???????????????(0)
            this.updateVehicleVerAllVerState("vehicle_object_line_ver", clearVerDataParamMap);

            try {
                vehicleObjectLineVerHisIds = dealVehicleObjectLine(lineDataList, vehicleDataInfo, saveVehicleDataVer, unNeedCheck, true, accessToken);
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }

        //???????????????????????????????????????????????????????????????????????????
        if (vo.getBillReceiverUserId() != null && vo.getBillReceiverUserId() > 0) {
            SysTenantDef tenantInfo = sysTenantDefService.getSysTenantDefByAdminUserId(vo.getBillReceiverUserId());
            if (tenantInfo != null && !tenantInfo.getState().equals(SysStaticDataEnum.SYS_STATE_DESC.STATE_NO)) {
                vehicleDataInfo.setDriverUserId(saveVehicleDataVer.getDriverUserId());//???????????????????????????
                TenantVehicleRel tenantVehicleRel = tenantVehicleRelService.getTenantVehicleRel(vehicleDataInfo.getId(), tenantInfo.getId());
                if (tenantVehicleRel == null) {
                    addAttachVehicleAndDriver(vehicleDataInfo, tenantInfo.getId(), 2, accessToken);
                }
            }
        }

    }

    @Override
    public void doRemoveVehicleWx(String vehicleCodeString, String accessToken) {

        List<Long> vehicleCodeList = CommonUtil.convertLongIdList(vehicleCodeString);

        if (!vehicleCodeList.isEmpty()) {

            //2.3????????????
            for (Long vehicleCode : vehicleCodeList) {

                //??????????????????????????????????????????
                VehicleDataInfoVer vehicleDataInfoVer = iVehicleDataInfoVerService.doAddVehicleDataInfoVerHis(vehicleCode);
                if (null == vehicleDataInfoVer) {
                    continue;
                }

                TenantVehicleRel vehicleRel = tenantVehicleRelService.getZYVehicleByVehicleCode(vehicleCode);
                if (vehicleRel != null) {
                    throw new BusinessException("????????????????????????????????????");
                } else {

                    LambdaQueryWrapper<TenantVehicleRel> queryWrapper = new LambdaQueryWrapper<>();
                    queryWrapper.eq(TenantVehicleRel::getVehicleCode, vehicleCode);
                    List<TenantVehicleRel> tenantVehicleRelList = tenantVehicleRelService.list();

                    for (TenantVehicleRel tenantVehicleRel : tenantVehicleRelList) {
                        this.dissolveCooperationVehicle(tenantVehicleRel.getId(), vehicleDataInfoVer.getId(), "APP");
                        saveSysOperLog(SysOperLogConst.BusiCode.Vehicle, tenantVehicleRel.getId(), SysOperLogConst.OperType.Del, "????????????APP??????", accessToken);
                    }

                    //????????????
                    removeApplyRecord(vehicleCode);

                    VehicleDataInfo vehicleDataInfo = this.getById(vehicleCode);
                    this.removeById(vehicleDataInfo);

                    //?????????????????????
                    addPtTenantVehicleRel(vehicleCode);
                }
            }
        }

    }

    @Override
    public void doQuitTenant(String relIds, String accessToken) {
        List<Long> relIdList = CommonUtil.strToList(relIds, "long");

        if (!relIdList.isEmpty()) {

            List<TenantVehicleRel> tenantVehicleRelList = tenantVehicleRelService.doQueryTenantVehicleRelByRelIds(relIdList);
            if (null != tenantVehicleRelList && !tenantVehicleRelList.isEmpty()) {
                for (int i = 0; i < tenantVehicleRelList.size(); i++) {

                    TenantVehicleRel tenantVehicleRel = tenantVehicleRelList.get(i);
                    if (null != tenantVehicleRel.getVehicleClass() && tenantVehicleRel.getVehicleClass() == VEHICLE_CLASS1) {
                        throw new BusinessException("??????????????????????????????");
                    }

                    if (null != tenantVehicleRel.getDriverUserId() && tenantVehicleRel.getDriverUserId() > 0) {
                        if (getTenantUserRel(tenantVehicleRel.getDriverUserId(), tenantVehicleRel.getTenantId()) != null) {
                        }
                    }

                    VehicleDataInfoVer vehicleDataInfoVer = iVehicleDataInfoVerService.doAddVehicleDataInfoVerHis(tenantVehicleRel.getVehicleCode());
                    this.dissolveCooperationVehicle(tenantVehicleRel.getId(), vehicleDataInfoVer.getId(), accessToken);
                    saveSysOperLog(SysOperLogConst.BusiCode.Vehicle, tenantVehicleRel.getId(), SysOperLogConst.OperType.Del, "????????????APP????????????", accessToken);
                }
            }

        }

    }

    @Override
    public VehicleInfoAppDto getVehicleInfoApp(Long vehicleCode) {

        VehicleInfoAppDto result = baseMapper.getTenantVehicleInfoApp(vehicleCode);

        if (null == result) {
            return null;
        }

        if (result.getAuthState() != null && result.getAuthState() == AUTH_STATE1) {
            VehicleDataInfoVer vehicle = iVehicleDataInfoVerService.getVehicleDataInfoVer(vehicleCode);
            if (null != vehicle) {
                result.setLicenceType(vehicle.getLicenceType());
                result.setVehicleLength(vehicle.getVehicleLength());
                result.setVehicleStatus(vehicle.getVehicleStatus());
                result.setVehicleLoad(vehicle.getVehicleLoad());
                result.setVinNo(vehicle.getVinNo());
                result.setEngineNo(vehicle.getEngineNo());
                result.setDrivingLicenseSn(vehicle.getDrivingLicenseSn());
                result.setOperCerti(vehicle.getOperCerti());
                result.setBrandModel(vehicle.getBrandModel());
                result.setVehiclePicture(vehicle.getVehiclePicture());
                result.setVehiclePicUrl(vehicle.getVehiclePicUrl());
                result.setDrivingLicenseSn(vehicle.getDrivingLicense());
                result.setDrivingLicenseUrl(vehicle.getDrivingLicenseUrl());
                result.setAdriverLicenseCopy(vehicle.getAdrivingLicenseCopy());
                result.setAdriverLicenseCopyUrl(vehicle.getAdriverLicenseCopyUrl());
                result.setOperCertiId(vehicle.getOperCertiId());
                result.setOperCertiUrl(vehicle.getOperCertiUrl());
                //??????????????????
                List<VehicleObjectLineDto> vehicleObjectLineVer = vehicleObjectLineVerService.getVehicleObjectLineVer(vehicleCode, null, VER_STATE_Y, 1);
                if (null != vehicleObjectLineVer) {
                    result.setVehicleOjbectLineArray(vehicleObjectLineVer);
                }
            }
        } else {
            //??????????????????
            List<VehicleObjectLineDto> vehicleObjectLineForApp = vehicleObjectLineService.getVehicleObjectLineForApp(vehicleCode, null);
            result.setVehicleOjbectLineArray(vehicleObjectLineForApp);
        }

        result.setVehicleClass(VEHICLE_CLASS1);
        result.setVehicleClassName(getSysStaticData("VEHICLE_CLASS", VEHICLE_CLASS1 + "").getCodeName());

        if (result.getLicenceType() != null) {
            result.setLicenceTypeName(getSysStaticData("LICENCE_TYPE", result.getLicenceType() + "").getCodeName());
        }

        if (result.getVehicleStatus() != null) {
            result.setVehicleStatusName(SysStaticDataRedisUtils.getSysStaticDataById2String(redisUtil, "VEHICLE_STATUS", result.getVehicleStatus()));
        }
        if (result.getVehicleStatus() != null && result.getVehicleStatus() == SysStaticDataEnum.VEHICLE_STATUS_ENUM.VEHICLE_CAR_BOX.getType() && result.getVehicleLength() != null) {
            result.setVehicleLengthName(SysStaticDataRedisUtils.getSysStaticDataById2String(redisUtil, "VEHICLE_STATUS_SUBTYPE", result.getVehicleLength() != null && !result.getVehicleLength().equals("") ? result.getVehicleLength() : "0"));
        } else {
            result.setVehicleLengthName(SysStaticDataRedisUtils.getSysStaticDataById2String(redisUtil, "VEHICLE_LENGTH", result.getVehicleLength() != null && !result.getVehicleLength().equals("") ? result.getVehicleLength() : "0"));
        }

        if (result.getVehicleLoad() != null) {
            result.setVehicleLoadName(getSysStaticData("VEHICLE_LOAD", result.getVehicleLoad() + "").getCodeName());
        }

        FastDFSHelper client = null;
        try {
            client = FastDFSHelper.getInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        String vehiclePicUrl = result.getVehiclePicUrl();
        if (org.apache.commons.lang.StringUtils.isNotBlank(vehiclePicUrl)) {
            String url = null;
            try {
                url = client.getHttpURL(vehiclePicUrl).split("\\?")[0];
            } catch (Exception e) {
                e.printStackTrace();
            }
            result.setVehiclePicFullUrl(url);
        }

        String drivingLicenseUrl = result.getDrivingLicenseUrl();
        if (org.apache.commons.lang.StringUtils.isNotBlank(drivingLicenseUrl)) {
            String url = null;
            try {
                url = client.getHttpURL(drivingLicenseUrl).split("\\?")[0];
            } catch (Exception e) {
                e.printStackTrace();
            }
            result.setDrivingLicenseFullUrl(url);
        }

        String adriverLicenseCopyUrl = result.getAdriverLicenseCopyUrl();
        if (org.apache.commons.lang.StringUtils.isNotBlank(adriverLicenseCopyUrl)) {
            String url = null;
            try {
                url = client.getHttpURL(adriverLicenseCopyUrl).split("\\?")[0];
            } catch (Exception e) {
                e.printStackTrace();
            }
            result.setAdriverLicenseCopyFullUrl(url);
        }

        String operCertiUrl = result.getOperCertiUrl();
        if (org.apache.commons.lang.StringUtils.isNotBlank(operCertiUrl)) {
            String url = null;
            try {
                url = client.getHttpURL(operCertiUrl).split("\\?")[0];
            } catch (Exception e) {
                e.printStackTrace();
            }
            result.setOperCertiFullUrl(url);
        }

        String specialOperCertFileUrl = result.getSpecialOperCertFileUrl();
        if (org.apache.commons.lang.StringUtils.isNotBlank(specialOperCertFileUrl)) {
            String url = null;
            try {
                url = client.getHttpURL(specialOperCertFileUrl).split("\\?")[0];
            } catch (Exception e) {
                e.printStackTrace();
            }
            result.setSpecialOperCertFileFullUrl(url);
        }

        Integer authState = result.getAuthState();
        String auditContent = result.getAuditContent();
        if (authState != null) {
            result.setAuthStateName(getSysStaticData("CUSTOMER_AUTH_STATE", authState + "").getCodeName());
        }
        if (authState != null && authState == AUTH_STATE2 && org.apache.commons.lang.StringUtils.isBlank(auditContent)) {
            result.setAuthStateName(getSysStaticData("CUSTOMER_AUTH_STATE", authState + "").getCodeName());
        } else if (org.apache.commons.lang.StringUtils.isNotBlank(auditContent)) {
            result.setAuthStateName("??????????????????????????????");
            result.setAuthState(AUTH_STATE3);
        }

        return result;
    }

    @Override
    public List<QueryVehicleByDriverDto> doQueryVehicleByDriver(Long driverUserId) {
        List<QueryVehicleByDriverDto> queryVehicleByDriverDtos = baseMapper.doQueryVehicleByDriver(driverUserId);

        for (QueryVehicleByDriverDto queryVehicleByDriverDto : queryVehicleByDriverDtos) {

            Integer shareFlg = queryVehicleByDriverDto.getShareFlg();
            if (shareFlg != null && shareFlg == -1) {
                queryVehicleByDriverDto.setShareFlg(1);
            }

            Integer authState = queryVehicleByDriverDto.getAuthState();
            if (authState != null) {
                queryVehicleByDriverDto.setAuthStateName(getSysStaticData("CUSTOMER_AUTH_STATE", authState + "").getCodeName());
            }

            FastDFSHelper client = null;
            try {
                client = FastDFSHelper.getInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
            String vehiclePicUrl = queryVehicleByDriverDto.getVehiclePicUrl();
            if (org.apache.commons.lang.StringUtils.isNotBlank(vehiclePicUrl)) {
                String url = null;
                try {
                    url = client.getHttpURL(vehiclePicUrl).split("\\?")[0];
                } catch (Exception e) {
                    e.printStackTrace();
                }
                queryVehicleByDriverDto.setVehiclePicFullUrl(url);
            }

            Long vehicleStatusRtn = queryVehicleByDriverDto.getVehicleStatus();
            String vehicleLengthRtn = queryVehicleByDriverDto.getVehicleLength();
            if (vehicleStatusRtn != null && vehicleStatusRtn > 0) {
                queryVehicleByDriverDto.setVehicleStatusName(getSysStaticDataCodeNameById("VEHICLE_STATUS", "" + vehicleStatusRtn));
            }
            if (StringUtils.isNotBlank(vehicleLengthRtn) && !"-1".equals(vehicleLengthRtn)) {
                queryVehicleByDriverDto.setVehicleLengthName(getSysStaticDataCodeNameById("VEHICLE_LENGTH", "" + vehicleLengthRtn));
            }

            Integer licenceType = queryVehicleByDriverDto.getLicenceType();
            if (licenceType != null && licenceType > -1) {
                queryVehicleByDriverDto.setLicenceTypeName(getSysStaticData("LICENCE_TYPE", "" + licenceType).getCodeName());
            }
        }

        return queryVehicleByDriverDtos;
    }

    private String getSysStaticDataCodeNameById(String codeType, String id) {
        List<SysStaticData> staticDataList = (List<SysStaticData>) redisUtil.get(com.youming.youche.conts.SysStaticDataEnum.SYS_STATIC_DATA_NAME.concat(codeType));
        for (SysStaticData sysStaticData : staticDataList) {
            if (id.equals(sysStaticData.getId() + "")) {
                return sysStaticData.getCodeName();
            }
        }
        return "";
    }

    public SysStaticData getSysStaticDataId(String codeType, String codeValue) {
        List<SysStaticData> list = (List<SysStaticData>) redisUtil.get(com.youming.youche.conts.SysStaticDataEnum.SYS_STATIC_DATA_NAME.concat(codeType));
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
    public void updateMaintenance(String plateNumber, Integer maintenanceDis, LocalDateTime lastMaintenanceDate) {
        VehicleDataInfo vehicleDataInfo = vehicleDataInfoMapper.getVehicleDataInfo(plateNumber);
        if (null == vehicleDataInfo) {
            throw new BusinessException("????????????????????????");
        }

        vehicleDataInfo.setMaintenanceDis(maintenanceDis);
        vehicleDataInfo.setLastMaintenanceDate(lastMaintenanceDate);
        this.update(vehicleDataInfo);
    }

    @Override
    public List<VehicleDataInfo> getCarTenantId(Long tenantId) {
        QueryWrapper<VehicleDataInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id",tenantId);
        List<VehicleDataInfo> list = this.list(queryWrapper);
        return list;
    }

    @Override
    public List<VehicleReportVehicleDataDto> getVehicleDataAll() {
        return baseMapper.getVehicleDataAll();
    }

    @Override
    public TenantVehicleRelInfoDto getVehicleInsuranceFee(Integer vehicleClass, Long vehicleCode, Long tenantId) {
        VehicleInfoDto tenantVehicleInfo = baseMapper.getTenantVehicleInfo(vehicleClass, vehicleCode, tenantId);
        TenantVehicleRelInfoDto vehicleRelInfoMap = tenantVehicleCertRelMapper.getTenantVehicleRelInfo(vehicleCode, tenantId, tenantVehicleInfo.getRelId());
        return vehicleRelInfoMap;
    }

}
