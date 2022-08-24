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
 * 车辆表 服务实现类
 * </p>
 *
 * @author Terry
 * @since 2021-11-22
 */
@DubboService(version = "1.0.0")
public class VehicleDataInfoServiceImpl extends BaseServiceImpl<VehicleDataInfoMapper, VehicleDataInfo>
        implements IVehicleDataInfoService {

    private static final Integer YES = 1;

    //0 : 不可审核数据，只用做历史作用
    public static final Integer VER_STATE_N = 0;

    //1 : 可以审核数据
    public static final Integer VER_STATE_Y = 1;

    //是否显示审核按钮 0否 1是
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

        //校验车牌
        if (vehicleCode == null || vehicleCode < 0) {
            if (StringUtils.isBlank(plateNumbers)) {
                throw new BusinessException("车牌号或者车辆编码错误！");
            } else {
                VehicleDataInfo vehicleDataInfo = getVehicleDataInfo(plateNumbers);
                if (null == vehicleDataInfo) {
                    throw new BusinessException("车辆不存在！");
                }
                vehicleCode = vehicleDataInfo.getId();
            }
        }
        //查询车辆在租户下的类型
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
//                //如果传的车量类型与车队的不一样，则取车队的现有的值
//                vehicleClass = tenantVehicleClass;
//            }
        }

        //查询车辆的基础信息
        VehicleInfoDto tenantVehicleInfo = baseMapper.getTenantVehicleInfo(vehicleClass, vehicleCode, vehicleRel.getTenantId());
        if (null == tenantVehicleInfo) {
            return null;
        }
        //判断车量是否有自有属性
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
                    //其他车队的自有车
                    tenantVehicleInfo.setOtherOwnCar(true);
                } else {
                    //其他车队的自有车
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

            //车辆没有归属车队，获取车辆的归属司机
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

            //查询司机信息
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
                    tenantVehicleInfo.setDriverCarUserTypeName("平台司机");
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
        //判断如果是自有车,再查询成本信息
        if (vehicleClass == VEHICLE_CLASS1 || vehicleClass == VEHICLE_CLASS2 || vehicleClass == VEHICLE_CLASS4) {
            long relId = tenantVehicleInfo.getRelId();
            TenantVehicleRelInfoDto vehicleRelInfoMap = tenantVehicleCertRelMapper.getTenantVehicleRelInfo(vehicleCode, loginInfo.getTenantId(), relId);
            if (null != vehicleRelInfoMap) {
//                 if(null != vehicleRelInfoMap.getPrice()){
//
//                 }
                //处理回显费用
                tenantVehicleInfo.setTenantVehicleRelInfoDto(vehicleRelInfoMap);
            } else {
                tenantVehicleInfo.setTenantVehicleRelInfoDto(new TenantVehicleRelInfoDto());
            }

            //车辆归属组织
            Long orgId = tenantVehicleInfo.getOrgId();
            if (orgId != null && orgId > -1) {
                try {
                    tenantVehicleInfo.setOrgName(iSysOrganizeService.getCurrentTenantOrgNameById(loginInfo.getTenantId(), orgId));
                } catch (RpcException e) {
                    e.printStackTrace();
                }
            }
            //归属人
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

            //查询副驾驶司机信息
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
                        throw new BusinessException("司机资料不全，请先核对司机资料");
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
                        tenantVehicleInfo.setCopilotDriverUserTypeName("平台司机");
                    }
                }
            }
            Long followDriverId = tenantVehicleInfo.getFollowDriverId();
            if (null != followDriverId && followDriverId > 0) {
                //查询随车司机信息
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
                        tenantVehicleInfo.setFollowDriverUserTypeName("平台司机");
                    }
                }
            }
        } else {
            Long ownTenantId = tenantVehicleInfo.getTenantId();
            SysTenantDef sysTenantDef = sysTenantDefService.getById(ownTenantId);
            //车辆归属自有车车队信息
            if (null != sysTenantDef) {
                tenantVehicleInfo.setTenantName(sysTenantDef.getName());
                tenantVehicleInfo.setTenantLinkMan(sysTenantDef.getLinkMan());
                tenantVehicleInfo.setTenantLinkPhone(sysTenantDef.getLinkPhone());
            }
        }
        //查询心愿线路
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
        //查询绑定线路
        List<VehicleLineRel> vehicleLineRelList = vehicleLineRelMapper.getVehiclelineRels(vehicleCode, 1);
//        List<VehicleLineRelsVo> lineRelsVos = Lists.newArrayList();
//        if (vehicleLineRelList != null && vehicleLineRelList.size() > 0) {
//            for (VehicleLineRel vehicleLineRel : vehicleLineRelList) {
//                VehicleLineRelsVo vehicleLineRelsVo = new VehicleLineRelsVo();
//                BeanUtil.copyProperties(vehicleLineRel, vehicleLineRelsVo);
//                lineRelsVos.add(vehicleLineRelsVo);
//            }
//        }
        //共享联系人
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
//            tenantVehicleInfo.setLicenceTypeName("整车");
//        } else {
//            tenantVehicleInfo.setLicenceTypeName("拖车");
//        }
        if (null != tenantVehicleInfo.getShareFlg() && tenantVehicleInfo.getShareFlg() == SysStaticDataEnum.SHARE_FLG.NO) {
            tenantVehicleInfo.setShareFlgName("否");
        } else {
            tenantVehicleInfo.setShareFlgName("是");
        }
        tenantVehicleInfo.setVehiclelineRels(vehicleLineRelList);
        return tenantVehicleInfo;
    }

    @Override
    public VehicleInfoDto getAllVehicleInfoVerHis(Integer vehicleClass, Long vehicleCode, String plateNumbers, String accessToken) {
        //校验车牌
        if (vehicleCode == null || vehicleCode < 0) {
            if (StringUtils.isBlank(plateNumbers)) {
                throw new BusinessException("车牌号或者车辆编码错误！");
            } else {
                VehicleDataInfo vehicleDataInfo = getVehicleDataInfo(plateNumbers);
                if (null == vehicleDataInfo) {
                    throw new BusinessException("车辆不存在！");
                }
                vehicleCode = vehicleDataInfo.getId();
            }
        }
        //查询车辆在租户下的类型
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
////                //如果传的车量类型与车队的不一样，则取车队的现有的值
////                vehicleClass = tenantVehicleClass;
////            }
//            // 查询主表里 是否还有这条数据
//            QueryWrapper<TenantVehicleRel> queryWrapper = new QueryWrapper<>();
//            queryWrapper.eq("id",vehicleRelVer.getVehicleHisId())
//                    .eq("vehicle_code",vehicleRelVer.getVehicleCode())
//                    .eq("TENANT_ID",vehicleRelVer.getTenantId());
//            TenantVehicleRel tenantVehicleRel = tenantVehicleRelMapper.selectOne(queryWrapper);
//            if (tenantVehicleRel!=null){// 只是修改或者其他操作 没有被邀请走
//                vehicleRelVer=null;
//            }
//        }

        //查询车辆的基础信息
//        VehicleInfoDto tenantVehicleInfo = baseMapper.getAllVehicleInfoVerHis(vehicleClass, vehicleCode, vehicleRelVer.getTenantId());
        List<VehicleInfoDto> allVehicleInfoVerHis = baseMapper.getAllVehicleInfoVerHis(vehicleClass, vehicleCode, loginInfo.getTenantId());
        VehicleInfoDto tenantVehicleInfo = null;
        if (allVehicleInfoVerHis.size() > 0 && allVehicleInfoVerHis != null) {
            tenantVehicleInfo = allVehicleInfoVerHis.get(0);
        }
        if (null == tenantVehicleInfo) {
            return null;
        }
        //判断车量是否有自有属性
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
                    //其他车队的自有车
                    tenantVehicleInfo.setOtherOwnCar(true);
                } else {
                    //其他车队的自有车
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

            //车辆没有归属车队，获取车辆的归属司机
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

            //查询司机信息
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
                    tenantVehicleInfo.setDriverCarUserTypeName("平台司机");
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
        //判断如果是自有车,再查询成本信息
        if (vehicleClass == VEHICLE_CLASS1 || vehicleClass == VEHICLE_CLASS2 || vehicleClass == VEHICLE_CLASS4) {
            long relId = tenantVehicleInfo.getRelId();
            TenantVehicleRelInfoDto vehicleRelInfoMap = tenantVehicleCertRelMapper.getTenantVehicleRelInfo(vehicleCode, loginInfo.getTenantId(), relId);
            if (null != vehicleRelInfoMap) {
//                 if(null != vehicleRelInfoMap.getPrice()){
//
//                 }
                //处理回显费用
                tenantVehicleInfo.setTenantVehicleRelInfoDto(vehicleRelInfoMap);
            } else {
                tenantVehicleInfo.setTenantVehicleRelInfoDto(new TenantVehicleRelInfoDto());
            }

            //车辆归属组织
            Long orgId = tenantVehicleInfo.getOrgId();
            if (orgId != null && orgId > -1) {
                try {
                    tenantVehicleInfo.setOrgName(iSysOrganizeService.getCurrentTenantOrgNameById(loginInfo.getTenantId(), orgId));
                } catch (RpcException e) {
                    e.printStackTrace();
                }
            }
            //归属人
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
            //查询司机信息
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
                    tenantVehicleInfo.setDriverCarUserTypeName("平台司机");
                }
            }
            //查询副驾驶司机信息
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
//                        throw new BusinessException("司机资料不全，请先核对司机资料");
//                    }
                    TenantUserRel tenantUserRel = userRels.get(0);
                    if (tenantUserRel.getCarUserType() != null && tenantUserRel.getCarUserType() > 0) {
                        tenantVehicleInfo.setCopilotDriverUserType(tenantUserRel.getCarUserType());
                        SysStaticData staticData = readisUtil.getSysStaticData("DRIVER_TYPE", tenantUserRel.getCarUserType().toString());
                        tenantVehicleInfo.setCopilotDriverUserTypeName(staticData.getCodeName());
                    } else {
                        tenantVehicleInfo.setCopilotDriverUserType(-1);
                        tenantVehicleInfo.setCopilotDriverUserTypeName("平台司机");
                    }
                }
                Long followDriverId = tenantVehicleInfo.getFollowDriverId();
                if (null != followDriverId && followDriverId > 0) {
                    //查询随车司机信息
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
                            tenantVehicleInfo.setFollowDriverUserTypeName("平台司机");
                        }
                    }

                    //共享联系人
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
            //车辆归属自有车车队信息
            if (null != sysTenantDef) {
                tenantVehicleInfo.setTenantName(sysTenantDef.getName());
                tenantVehicleInfo.setTenantLinkMan(sysTenantDef.getLinkMan());
                tenantVehicleInfo.setTenantLinkPhone(sysTenantDef.getLinkPhone());
            }
        }
        //查询心愿线路
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
        //查询绑定线路
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
//            tenantVehicleInfo.setLicenceTypeName("整车");
//        } else {
//            tenantVehicleInfo.setLicenceTypeName("拖车");
//        }
        if (null != tenantVehicleInfo.getShareFlg() && tenantVehicleInfo.getShareFlg() == SysStaticDataEnum.SHARE_FLG.NO) {
            tenantVehicleInfo.setShareFlgName("否");
        } else {
            tenantVehicleInfo.setShareFlgName("是");
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
        //租户车辆关系表id
        Long tenantVehicleRelId = vehicleInfoUptVo.getRelId();
        //租户车辆成本关系表id
        Long tenantVehicleCostRelId = vehicleInfoUptVo.getTenantVehicleCostRelId();
        //证照id
        Long tenantVehicleCertRelId = null;
        if (vehicleInfoUptVo.getTenantVehicleCertRelId() != null) {
            tenantVehicleCertRelId = vehicleInfoUptVo.getTenantVehicleCertRelId();
        }
        if (vehicleCode == null && vehicleCode < 0) {
            throw new BusinessException("车辆主键错误！");
        }
        if (vehicleClassIn == null && vehicleClassIn < 0) {
            throw new BusinessException("车辆类型错误！");
        }
        VehicleDataInfo vehicleDataInfo = baseMapper.selectById(vehicleCode);
        if (vehicleDataInfo == null) {
            throw new BusinessException("车辆信息不存在！");
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
            throw new BusinessException("车辆租户关系数据不存在！");
        }

        //是否需要立即生效,未审核直接生效
        Boolean isImmediateEffect = false;
        if (tenantVehicleRel.getAuthState() != null && AUTH_STATE2 != tenantVehicleRel.getAuthState()) {
            isImmediateEffect = true;
        }
        LoginInfo loginInfo = loginUtils.get(accessToken);
        //修改车辆相关所有ver表的状态为不可审核状态  1
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

        //租户车辆关系表
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
        //自有车与外调车不保存账单接收人信息
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
        //心愿线路表
        List<VehicleObjectLineVo> list = vehicleInfoUptVo.getVehicleOjbectLineArray();
        String vehicleObjectLineVerHisIds = "";

        if (list != null) {
            try {
                vehicleObjectLineVerHisIds = this.dealVehicleObjectLine(list, vehicleDataInfo, vehicleDataInfoVerNew, isImmediateEffect, false, accessToken);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        //绑定线路
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
        //自有车才需要记录 租户车辆成本表和证照信息
        if (VEHICLE_CLASS1 == vehicleClassIn || VEHICLE_CLASS2 == vehicleClassIn || VEHICLE_CLASS4 == vehicleClassIn) {

            TenantVehicleCostRelVer tenantVehicleCostRelVer = new TenantVehicleCostRelVer();
            BeanUtil.copyProperties(vehicleInfoUptVo, tenantVehicleCostRelVer);

            tenantVehicleCostRelVer.setCreateTime(LocalDateTime.now());

            //招商与挂靠车，不记录 （残值,折旧月数,保险费用,审车费用,保养费用,维修费用,轮胎费用）
            if (SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS2 == vehicleClassIn || SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS4 == vehicleClassIn) {
                //残值
                tenantVehicleCostRelVer.setResidual(null);
                //折旧月数
                tenantVehicleCostRelVer.setDepreciatedMonth(null);
                //保险费
                tenantVehicleCostRelVer.setInsuranceFee(null);
                //审车费
                tenantVehicleCostRelVer.setExamVehicleFee(null);
                //保养费
                tenantVehicleCostRelVer.setMaintainFee(null);
                //维修费用
                tenantVehicleCostRelVer.setRepairFee(null);
                //轮胎费用
                tenantVehicleCostRelVer.setTyreFee(null);
            } else if (SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1 == vehicleClassIn) {
                //自有车不记录管理费
                //管理费
                tenantVehicleCostRelVer.setManagementCost(null);
            }
            //不存在则新建
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

            //证照信息表
            TenantVehicleCertRelVer tenantVehicleCertRelVer = new TenantVehicleCertRelVer();
            BeanUtil.copyProperties(vehicleInfoUptVo, tenantVehicleCertRelVer);
            tenantVehicleCertRelVer.setSeasonalVeriTimeEnd(vehicleInfoUptVo.getSeasonalVeriTimeEnd());
            tenantVehicleCertRelVer.setCreateTime(LocalDateTime.now());

            //不存在则新建
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
                //未认证同步最新数据
                //成本信息
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


                //证照信息
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
                    //当tenantVehicleCertRel表没有数据时可以把id设置到VER表
                    tenantVehicleCertRelVer.setId(tenantVehicleCertRelNew.getId());
                }


                //主表信息 //时间问题
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
                //租户车辆关系表 //时间
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

            //未认证车辆,修改为外调车,自动认证
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

                saveSysOperLog(SysOperLogConst.BusiCode.Vehicle, tenantVehicleRel.getId(), SysOperLogConst.OperType.Audit, "外调车-" + vehicleDataInfoVerNew.getPlateNumber() + "-车辆基础资料自动审核通过", accessToken);
                //租户车辆关系表,关系还未认证 //时间
                tenantVehicleRelVerNew.setCreateTime(tenantVehicleRel.getCreateTime());
                Long relId = tenantVehicleRel.getId();
                BeanUtil.copyProperties(tenantVehicleRelVerNew, tenantVehicleRel);
                tenantVehicleRel.setId(relId);
                tenantVehicleRel.setAuthState(AUTH_STATE1);
                tenantVehicleRel.setIsAuth(IS_AUTH1);
                tenantVehicleRelService.saveOrUpdate(tenantVehicleRel);
                //增加平台车记录
                addPtTenantVehicleRel(tenantVehicleRel.getId());
            }

        }
       /* //启动审核流程
        Map iMap = new HashMap();
        iMap.put("svName", "vehicleTF");
        iMap.put("vehicleDataInfoVerHisId", vehicleDataInfoVerNew.getId());
        iMap.put("vehicleObjectLineVerHisIds", vehicleObjectLineVerHisIds);
        iMap.put("vehicleLineRelVerHisIds", vehicleLineRelVerHisIds);
        iMap.put("tenantVehicleRelVerHisId", tenantVehicleRelVerNew.getId());
        iMap.put("tenantVehicleCostRelVerHisId", tenantVehicleCostRelVerHisId);
        iMap.put("tenantVehicleCertRelVerHisId", tenantVehicleCertRelVerHisId);

        String logMsg = "车辆" + vehicleDataInfo.getPlateNumber() + "档案修改";
        if (isUpdatePlateNumber) {
            logMsg = "将车牌[" + plateNumberOld + "]改为[" + plateNumberNew + "]";
        }
        saveSysOperLog(SysOperLogConst.BusiCode.Vehicle, tenantVehicleRel.getId(), SysOperLogConst.OperType.Update, logMsg, loginInfo.getTenantId(), accessToken);


        boolean bool = iAuditOutTF.startProcess(AUDIT_CODE_VEHICLE, tenantVehicleRel.getId(), SysOperLogConst.BusiCode.Vehicle, iMap, loginInfo.getTenantId());
        if (!bool) {
            throw new BusinessException("启动审核流程失败！");
        }
        String logMsg = "车辆" + vehicleDataInfo.getPlateNumber() + "档案修改";
        saveSysOperLog(SysOperLogConst.BusiCode.Vehicle, tenantVehicleRel.getId(), SysOperLogConst.OperType.Update, logMsg, loginInfo.getTenantId(), accessToken);
*/

        //启动审核流程
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
                log.error("启动审核流程失败！");
//                return ResponseResult.failure("启动审核流程失败！");
                throw new BusinessException("启动审核流程失败");
            }
        }
        String logMsg = "车辆" + vehicleDataInfo.getPlateNumber() + "档案修改";
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
            throw new BusinessException("车牌号错误！");
        }

       /* if (vehicleClassIn < 0) {
            throw new BusinessException("车辆类型错误！");
        }*/

        Integer vehicleClass = tenantVehicleRelMapper.checkVehicleClass(plateNumber, loginInfo.getTenantId());
        if (null != vehicleClass) {
            if (VEHICLE_CLASS1 == vehicleClass) {
                throw new BusinessException("该车辆已经为公司自有车，不允许重复添加，可到修改界面修改信息！");
            } else if (SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS5 == vehicleClass) {
                throw new BusinessException("该车辆已经为公司临时外调车，不允许重复添加，可到修改界面修改信息！");
            } else if (SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS2 == vehicleClass) {
                throw new BusinessException("该车辆已经为公司招商车，不允许重复添加，可到修改界面修改信息！");
            } else if (SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS4 == vehicleClass) {
                throw new BusinessException("该车辆已经为公司挂靠车，不允许重复添加，可到修改界面修改信息！");
            }
        }

        //保存数据安全性，再次校验车辆是否注册
        QueryWrapper<VehicleDataInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("plate_number", plateNumber);
        List<VehicleDataInfo> vehicleDataInfoList = baseMapper.selectList(queryWrapper);
        if (vehicleDataInfoList != null && vehicleDataInfoList.size() > 0) {
            throw new BusinessException("车辆已被注册！");
        }

        //车辆主表
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
                throw new BusinessException("请填写车辆长度！");
            }
            if (StringUtils.isBlank(vehicleInfoVo.getLightGoodsSquare()) || "-1".equals(vehicleInfoVo.getLightGoodsSquare())) {
                throw new BusinessException("请填写车辆容积！");
            }
            if (StringUtils.isBlank(vehicleInfoVo.getVehicleLoad())) {
                throw new BusinessException("请填写车辆载重！");
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
        //租户车辆关系表
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

        //心愿线路表
        List<VehicleObjectLineVo> list = vehicleInfoVo.getVehicleOjbectLineArray();
        List vehicleObjectLineIds = new ArrayList();
        VehicleObjectLine vehicleObjectLine = null;
        VehicleObjectLineVer vehicleObjectLineVer = null;
        if (null != list && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {

                //先判断车辆有多少心愿线路，有3条了则不再增加
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
        //自有车才需要记录 租户车辆成本表和证照信息
        if (VEHICLE_CLASS1 == vehicleClassIn || SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS2 == vehicleClassIn || SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS4 == vehicleClassIn) {
            //租户车辆成本关系表
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

            //招商车与挂靠车，不记录 （残值,折旧月数,保险费用,审车费用,保养费用,维修费用,轮胎费用）
            if (SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS2 == vehicleClassIn || SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS4 == vehicleClassIn) {
                tenantVehicleCostRel.setResidual(null);//残值
                tenantVehicleCostRel.setDepreciatedMonth(null);//折旧月数
                tenantVehicleCostRel.setInsuranceFee(null);//保险费
                tenantVehicleCostRel.setExamVehicleFee(null);//审车费
                tenantVehicleCostRel.setMaintainFee(null);//保养费
                tenantVehicleCostRel.setRepairFee(null);//维修费用
                tenantVehicleCostRel.setTyreFee(null);//轮胎费用
            } else if (SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1 == vehicleClassIn) {//自有车不记录管理费
                tenantVehicleCostRel.setManagementCost(null);//管理费
            }

            tenantVehicleCostRelMapper.insert(tenantVehicleCostRel);
            TenantVehicleCostRelVer tenantVehicleCostRelVer = new TenantVehicleCostRelVer();
            BeanUtil.copyProperties(tenantVehicleCostRel, tenantVehicleCostRelVer);
            tenantVehicleCostRelVer.setVerState(VER_STATE_Y);
            tenantVehicleCostRelVerMapper.insert(tenantVehicleCostRelVer);
            tenantVehicleCostRelId = tenantVehicleCostRel.getId();
            //证照信息表
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
        //绑定线路
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

        saveSysOperLog(SysOperLogConst.BusiCode.Vehicle, tenantVehicleRel.getId(), SysOperLogConst.OperType.Add, "车辆" + vehicleDataInfo.getPlateNumber() + "档案新增", loginInfo);

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

            //更新车量完整性
            doUpdateVehicleCompleteness(vehicleDataInfo.getId(), loginInfo.getTenantId());

            //判断是否添加平台车记录
            addPtTenantVehicleRel(vehicleDataInfo.getId());

            //外调车OCR识别成功自动审核成功
            saveSysOperLog(SysOperLogConst.BusiCode.Vehicle, tenantVehicleRel.getId(), SysOperLogConst.OperType.Audit, "外调车" + vehicleDataInfo.getPlateNumber() + "(OCR)自动审核通过", loginInfo);

        } else {
            boolean isAutoAudit = sysCfgService.getCfgBooleanVal("IS_AUTO_AUDIT", -1);
            if (isAutoAudit && vehicleClassIn == VEHICLE_CLASS5) {
                vehicleDataInfo.setAuthState(AUTH_STATE2);
                vehicleDataInfo.setIsAuth(IS_AUTH0);
                baseMapper.updateById(vehicleDataInfo);
                vehicleDataInfoVer.setVerState(VER_STATE_Y);
                vehicleDataInfoVer.setIsAuthSucc(SysStaticDataEnum.IS_AUTH_SUCC.YES_3);
                vehicleDataInfoVerMapper.updateById(vehicleDataInfoVer);
                //判断是否添加平台车记录
                addPtTenantVehicleRel(vehicleDataInfo.getId());
                //写日志
                saveSysOperLog(SysOperLogConst.BusiCode.Vehicle, vehicleDataInfo.getId(), SysOperLogConst.OperType.Audit, "外调车" + vehicleDataInfo.getPlateNumber() + "档案自动审核通过", accessToken);
            }

            //启动审核流程
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
                throw new BusinessException("启动审核流程失败！");
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
        //查询车辆在租户下的类型
        LoginInfo loginInfo = loginUtils.get(token);
        String VEHICLE_CLASS1 = SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1 + "";//招商挂靠车
        String VEHICLE_CLASS2 = SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS2 + "";//招商挂靠车
        String VEHICLE_CLASS4 = SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS4 + "";//挂靠车
        String VEHICLE_CLASS3 = SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS5 + "";//临时外调车
        List<VehicleInfoVo> failureList = new ArrayList<>();
        try {
            InputStream is = new ByteArrayInputStream(bytes);
            List<List<String>> lists = ExcelUtils.getExcelContent(is, 1, (ExcelFilesVaildate[]) null);
            //移除表头行
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
                        reasonFailure.append("车牌号未导入");
                    } else {
                        //车辆是否注册
                        Map<String, Object> rtnMap = baseMapper.getVehicleIsRegister(SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1, plateNumber);
                        param.put("plateNumber", plateNumber);
                        //已注册
                        if (null != rtnMap && !rtnMap.isEmpty()) {
                            //车辆修改特殊逻辑
                            QueryWrapper<TenantVehicleRel> tenantVehicleRelQueryWrapper = new QueryWrapper<>();
                            tenantVehicleRelQueryWrapper.eq("plate_number", plateNumber);
                            tenantVehicleRelQueryWrapper.eq("tenant_id", loginInfo.getTenantId());
                            List<TenantVehicleRel> tenantVehicleRels = iTenantVehicleRelService.list(tenantVehicleRelQueryWrapper);
                            if (null != tenantVehicleRels && tenantVehicleRels.size() > 0) {
//                            String vehicleClassName = ExcelUtils.getExcelValue(rowData, 1);
//                            String vehicleClassStr = null;
//                            if (StrUtil.isBlank(vehicleClassName)) {
//                                reasonFailure.append("车辆种类未输入!");
//                            } else {
//                                vehicleClassStr = getCodeValue("VEHICLE_CLASS", vehicleClassName);
//                                if (org.apache.commons.lang.StringUtils.isBlank(vehicleClassStr)) {
//                                    reasonFailure.append("车辆种类不正确!");
//                                }
//                            }
//                            int vehicleClass = tenantVehicleRels.get(0).getVehicleClass();
//                            if (vehicleClassStr.equals(vehicleClass + "")) {
//                                param.putAll(this.toUpdateByImport(rowData, token));
//                                reasonFailure.append(DataFormat.getStringKey(param, "tmpMsg"));
//                            } else {
//                                reasonFailure.append("已存在的车辆在导入中不允许修改类型！");
//                            }
                                reasonFailure.append("该车辆已经已存在");
                            } else {
                                reasonFailure.append("车辆已加入管理车平台，请通过邀请的方式邀请到车队!");
                            }
                        } else {
                            String vinNo = ExcelUtils.getExcelValue(rowData, 7);
                            if (org.apache.commons.lang.StringUtils.isBlank(vinNo)) {
                                reasonFailure.append("车架号未输入!");
                            }
                            param.put("vinNo", vinNo);

                            String vehicleClassName = ExcelUtils.getExcelValue(rowData, 1);
                            String vehicleClassStr = null;
                            if (StringUtils.isBlank(vehicleClassName)) {
                                reasonFailure.append("车辆种类未输入!");
                            } else {
                                vehicleClassStr = getCodeValue("VEHICLE_CLASS", vehicleClassName);
                                param.put("vehicleClass", vehicleClassStr);
                                if (org.apache.commons.lang.StringUtils.isBlank(vehicleClassStr)) {
                                    reasonFailure.append("车辆种类不正确!");
                                }
                            }

                            String licenceTypeName = ExcelUtils.getExcelValue(rowData, 2);
                            if (org.apache.commons.lang.StringUtils.isBlank(licenceTypeName)) {
                                reasonFailure.append("牌照类型未输入!");
                            } else {
                                String licenceType = getCodeValue("LICENCE_TYPE", licenceTypeName);
                                if (org.apache.commons.lang.StringUtils.isBlank(licenceType)) {
                                    reasonFailure.append("牌照类型不正确!");
                                } else {
                                    if ("1".equals(licenceType)) {
                                        //整车的处理
                                        String vehicleStatusName = ExcelUtils.getExcelValue(rowData, 3);
                                        if (org.apache.commons.lang.StringUtils.isBlank(vehicleStatusName)) {
                                            reasonFailure.append("车型未输入!");
                                        } else {
                                            SysStaticData dataByCodeName = SysStaticDataRedisUtils.getSysStaticDataByCodeName(redisUtil,
                                                    "VEHICLE_STATUS", vehicleStatusName);
                                            Long vehicleStatus = null;
                                            if (dataByCodeName != null) {
                                                vehicleStatus = dataByCodeName.getId();
                                            }
                                            if (org.apache.commons.lang.StringUtils.isBlank(vehicleStatus + "")) {
                                                reasonFailure.append("车型不正确!");
                                            }
                                            param.put("vehicleStatus", vehicleStatus);
                                        }
                                        String vehicleLengthName = ExcelUtils.getExcelValue(rowData, 4);
                                        if (org.apache.commons.lang.StringUtils.isBlank(vehicleLengthName)) {
                                            reasonFailure.append("车长未输入!");
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
                                                reasonFailure.append("车长不正确，");
                                            }
                                            param.put("vehicleLength", vehicleLength);
                                        }
                                        String vehicleLoad = ExcelUtils.getExcelValue(rowData, 5);
                                        if (org.apache.commons.lang.StringUtils.isBlank(vehicleLoad)) {
                                            reasonFailure.append("载重(吨)未输入!");
                                        }
                                        if (StrUtil.isNotBlank(vehicleLoad)) {
                                            BigDecimal vehicleLoadBig = new BigDecimal(vehicleLoad);
                                            if (vehicleLoadBig.compareTo(new BigDecimal(0)) < 0) {
                                                reasonFailure.append("载重(吨)不能为负数!");
                                            }

                                            try {
                                                Long vehicleLoadInteger = Long.parseLong(vehicleLoad);
                                                if (vehicleLoadInteger * 100 > Integer.MAX_VALUE) {
                                                    reasonFailure.append("载重(吨)超出范围!");
                                                }
                                            } catch (Exception e) {
                                                reasonFailure.append("载重(吨)超出范围!");
                                            }
                                        }

                                        if (vehicleLoad != null && !vehicleLoad.equals("") && Double.parseDouble(vehicleLoad) > 0) {
                                            Number value = Double.parseDouble(vehicleLoad) * 100;
                                            param.put("vehicleLoad", value.intValue());
                                        }


                                        String lightGoodsSquare = ExcelUtils.getExcelValue(rowData, 6);
                                        if (org.apache.commons.lang.StringUtils.isBlank(lightGoodsSquare)) {
                                            reasonFailure.append("容积(立方米)未输入!");
                                        }
                                        if (StringUtils.isNotBlank(lightGoodsSquare)) {

                                            try {
                                                Long lightGoodsSquareInteger = Long.parseLong(lightGoodsSquare);
                                                if (lightGoodsSquareInteger * 100 > Integer.MAX_VALUE) {
                                                    reasonFailure.append("容积(立方米)超出范围!");
                                                }
                                            } catch (Exception e) {
                                                reasonFailure.append("容积(立方米)超出范围!");
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
                                reasonFailure.append("发动机号未输入!");
                            }
                            param.put("engineNo", engineNo);

                            String brandModel = ExcelUtils.getExcelValue(rowData, 9);
                            param.put("brandModel", brandModel);

                            String operCerti = ExcelUtils.getExcelValue(rowData, 10);
                            param.put("operCerti", operCerti);

                            String drivingLicenseOwner = ExcelUtils.getExcelValue(rowData, 11);
                            if (org.apache.commons.lang.StringUtils.isBlank(drivingLicenseOwner)) {
                                reasonFailure.append("行驶证上的所有人未输入!");
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
                                        reasonFailure.append("空载油耗超出范围!");
                                    }
                                } catch (Exception e) {
                                    reasonFailure.append("空载油耗超出范围!");
                                }

                                Number value = Double.parseDouble(loadEmptyOilCost) * 100;
                                param.put("loadEmptyOilCost", value.intValue());
                            }


                            String loadFullOilCost = ExcelUtils.getExcelValue(rowData, 14);
                            if (StringUtils.isNotBlank(loadFullOilCost)) {
                                try {
                                    Long loadFullOilCostInteger = Long.parseLong(loadFullOilCost);
                                    if (loadFullOilCostInteger * 100 > Integer.MAX_VALUE) {
                                        reasonFailure.append("载重油耗超出范围!");
                                    }
                                } catch (Exception e) {
                                    reasonFailure.append("载重油耗超出范围!");
                                }

                                Number value = Double.parseDouble(loadFullOilCost) * 100;
                                param.put("loadFullOilCost", value.intValue());
                            }


                            String isUseCarOilCost = ExcelUtils.getExcelValue(rowData, 15);
                            param.put("isUseCarOilCost", "是".equals(isUseCarOilCost) ? 1 : 0);

                            String driverUserPhone = ExcelUtils.getExcelValue(rowData, 16);
                            String driverUserName = "";
                            if (org.apache.commons.lang.StringUtils.isNotBlank(vehicleClassStr)
                                    && (VEHICLE_CLASS2.equals(vehicleClassStr) || VEHICLE_CLASS3.equals(vehicleClassStr) || VEHICLE_CLASS4.equals(vehicleClassStr))
                                    && org.apache.commons.lang.StringUtils.isBlank(driverUserPhone)) {
                                reasonFailure.append("归属司机手机号未输入!");
                            } else if (org.apache.commons.lang.StringUtils.isNotBlank(vehicleClassStr) && org.apache.commons.lang.StringUtils.isNotBlank(driverUserPhone)) {
                                //查询号码
                                UserDataInfo userInfo = iUserDataInfoRecordService.getUserDataInfoByMoblile(driverUserPhone, false, token);
                                if (userInfo == null) {
                                    reasonFailure.append("归属司机手机号不存在!");
                                } else {
                                    QueryWrapper<TenantUserRel> queryWrapper = new QueryWrapper<>();
                                    queryWrapper.eq("USER_ID", userInfo.getId());
                                    queryWrapper.eq("TENANT_ID", loginInfo.getTenantId());
                                    queryWrapper.eq("state", com.youming.youche.conts.SysStaticDataEnum.SYS_USER_STATE.AUDIT_APPROVE);
                                    List<TenantUserRel> tenantUserRels = tenantUserRelMapper.selectList(queryWrapper);

                                    if (null == tenantUserRels && tenantUserRels.size() > 0) {
                                        reasonFailure.append("归属司机只能是本车队司机!");
                                    } else {
                                        param.put("driverUserId", userInfo.getId());
                                        driverUserName = userInfo.getLinkman();
                                    }

                                }
                            }

                            //处理账单接收人
                            if (VEHICLE_CLASS2.equals(vehicleClassStr) || VEHICLE_CLASS4.equals(vehicleClassStr)) {
                                String billReceiverMobile = ExcelUtils.getExcelValue(rowData, 38);//账单接收人手机号
                                String billReceiverName = ExcelUtils.getExcelValue(rowData, 39);//账单接收人姓名


                                if (org.apache.commons.lang.StringUtils.isBlank(billReceiverMobile)) {
                                    billReceiverMobile = driverUserPhone;
                                }

                                if (org.apache.commons.lang.StringUtils.isBlank(billReceiverName)) {
                                    billReceiverName = driverUserName;
                                }

                                param.put("billReceiverMobile", billReceiverMobile);
                                param.put("billReceiverName", billReceiverName);

                                if (org.apache.commons.lang.StringUtils.isBlank(billReceiverMobile) || org.apache.commons.lang.StringUtils.isBlank(billReceiverName)) {
                                    reasonFailure.append("招商挂靠车,账单接收人不能为空!");
                                } else {

                                    UserDataInfoBackVo userDataInfoBackVo = new UserDataInfoBackVo();
                                    if (VEHICLE_CLASS2.equals(vehicleClassStr)) {//校验招商车的账单接收人
                                        userDataInfoBackVo = queryBillReceiverName(billReceiverMobile, 1, token);
                                    } else if (VEHICLE_CLASS4.equals(vehicleClassStr)) {//校验挂靠车的账单接收人
                                        userDataInfoBackVo = queryBillReceiverName(billReceiverMobile, 2, token);
                                    }
                                    if ("F".equals(userDataInfoBackVo.getCode())) {
                                        reasonFailure.append(userDataInfoBackVo.getMsg() + "!");
                                    } else {
                                        //查询号码
                                        UserDataInfo userInfo = iUserDataInfoRecordService.getUserDataInfoByMoblile(billReceiverMobile, false, token);
                                        if (userInfo != null) {
                                            param.put("billReceiverUserId", userInfo.getId());
                                        }
                                    }
                                }

                            }

                            //自有车，招商，挂靠车才有的属性处理
                            if (StrUtil.isNotBlank(vehicleClassStr) && (VEHICLE_CLASS1.equals(vehicleClassStr) || VEHICLE_CLASS2.equals(vehicleClassStr) || VEHICLE_CLASS4.equals(vehicleClassStr))) {
                                if (VEHICLE_CLASS1.equals(vehicleClassStr)) {//自有车才有的属性处理
                                    String copilotDriverPhone = ExcelUtils.getExcelValue(rowData, 17);
                                    if (org.apache.commons.lang.StringUtils.isNotBlank(driverUserPhone) && org.apache.commons.lang.StringUtils.isNotBlank(copilotDriverPhone) && driverUserPhone.equals(copilotDriverPhone)) {
                                        reasonFailure.append("主驾和副驾不能是相同的人!");

                                    } else if (org.apache.commons.lang.StringUtils.isNotBlank(copilotDriverPhone)) {

                                        //查询号码
                                        UserDataInfo userInfo = iUserDataInfoRecordService.getUserDataInfoByMoblile(copilotDriverPhone, false, token);
                                        if (userInfo == null) {
                                            reasonFailure.append("副驾手机号不存在!");
                                        } else {
                                            QueryWrapper<TenantUserRel> tenantUserRelQueryWrapper = new QueryWrapper<>();
                                            tenantUserRelQueryWrapper.eq("user_id", userInfo.getId());
                                            tenantUserRelQueryWrapper.eq("tenant_id", loginInfo.getTenantId());
                                            List<TenantUserRel> tenantUserRels = iTenantUserRelService.list(tenantUserRelQueryWrapper);
                                            if (tenantUserRels == null || tenantUserRels.size() == 0) {
                                                reasonFailure.append("副驾只能是本车队司机或平台司机!");
                                            } else {
                                                param.put("copilotDriverId", userInfo.getId());
                                            }

                                        }

                                    }
                                    String followDriverPhone = ExcelUtils.getExcelValue(rowData, 18);
                                    if (org.apache.commons.lang.StringUtils.isNotBlank(followDriverPhone)) {
                                        UserDataInfo userInfo = iUserDataInfoRecordService.getUserDataInfoByMoblile(followDriverPhone, false, token);
                                        if (userInfo == null) {
                                            reasonFailure.append("随车司机手机号不存在!");
                                        } else {
                                            QueryWrapper<TenantUserRel> tenantUserRelQueryWrapper = new QueryWrapper<>();
                                            tenantUserRelQueryWrapper.eq("user_id", userInfo.getId());
                                            List<TenantUserRel> tenantUserRelList = iTenantUserRelService.list(tenantUserRelQueryWrapper);
                                            if (tenantUserRelList == null || tenantUserRelList.size() == 0) {
                                                reasonFailure.append("随车司机只能是本车队司机或平台司机!");
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
                                            reasonFailure.append("归属部门不正确!");
                                        }
                                        param.put("orgId", orgId);
                                    } else {
                                        //获取隐藏的根组织（parentOrgId = -1）
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
                                            reasonFailure.append("归属人手机不正确!");
                                        } else {
                                            param.put("userId", sysOperatorByLoginAcct.getUserInfoId());
                                        }
                                    }
                                }
                                if ((VEHICLE_CLASS1.equals(vehicleClassStr))) {//自有车才处理的属性
                                    String lineCodeRules = ExcelUtils.getExcelValue(rowData, 21);
                                    if (StrUtil.isNotBlank(lineCodeRules)) {
                                        String[] lineCodeRuleList=lineCodeRules.split(",");
                                        Set<String> setList=new HashSet<>();
                                        for (String line:lineCodeRuleList) {
                                            setList.add(line);
                                        }
                                        if (setList.size()!=lineCodeRuleList.length){
                                            reasonFailure.append("线路绑定不正确!");
                                        }
                                        StringBuffer lineCodeRulesSb = new StringBuffer();
                                        for (String s : lineCodeRuleList) {
                                            lineCodeRulesSb.append("'").append(s).append("',");
                                        }
                                        List<CmCustomerLine> lines = cmCustomerLineMapper.getCmCustomerLineByLineCodeRules(lineCodeRulesSb.substring(0, lineCodeRulesSb.length() - 1));
                                        if (lines == null || lines.isEmpty()) {
                                            reasonFailure.append("线路绑定不正确!");
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
                                                    reasonFailure.append("线路绑定中" + lineMsg.substring(0, lineMsg.length() - 1) + "不正确，");
                                                }
                                            }
                                        }
                                    }

                                    String shareFlg = ExcelUtils.getExcelValue(rowData, 22);
                                    param.put("shareFlg", "是".equals(shareFlg) ? 1 : 0);
                                    if ("是".equals(shareFlg)) {
                                        String linkUserPhone = ExcelUtils.getExcelValue(rowData, 23);
                                        if (org.apache.commons.lang.StringUtils.isBlank(linkUserPhone)) {
                                            reasonFailure.append("共享联系电话未输入!");
                                        } else {
                                            com.youming.youche.system.domain.UserDataInfo userDataInfoServicePhone = userDataInfoService.getPhone(linkUserPhone);
                                            if (userDataInfoServicePhone == null) {
                                                reasonFailure.append("共享联系电话不正确!");
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
                                            reasonFailure.append("购车价格超出范围!");
                                        }
                                    } catch (Exception e) {
                                        reasonFailure.append("购车价格超出范围");
                                    }
                                    Number value = Double.parseDouble(excelValue) * 100;
                                    param.put("price", value.intValue());
                                }

                                String excelValue1 = ExcelUtils.getExcelValue(rowData, 25);
                                if (StringUtils.isNotBlank(excelValue1)) {
                                    try {
                                        Long excelValue1Integer = Long.parseLong(excelValue1);
                                        if (excelValue1Integer * 100 > Integer.MAX_VALUE) {
                                            reasonFailure.append("残值超出范围!");
                                        }
                                    } catch (Exception e) {
                                        reasonFailure.append("残值超出范围");
                                    }
                                    Number value = Double.parseDouble(excelValue1) * 100;
                                    param.put("residual", value.intValue());
                                }

                                String excelValue9 = ExcelUtils.getExcelValue(rowData, 26);
                                if (StringUtils.isNotBlank(excelValue9)) {
                                    try {
                                        Long excelValue9Integer = Long.parseLong(excelValue9);
                                        if (excelValue9Integer * 100 > Integer.MAX_VALUE) {
                                            reasonFailure.append("贷款利息超出范围!");
                                        }
                                    } catch (Exception e) {
                                        reasonFailure.append("贷款利息超出范围");
                                    }
                                    Number value = Double.parseDouble(excelValue9) * 100;
                                    param.put("loanInterest", value.intValue());
                                }

                                String interestPeriods = ExcelUtils.getExcelValue(rowData, 27);
                                if (StringUtils.isNotBlank(interestPeriods)) {
                                    try {
                                        Integer.parseInt(interestPeriods);
                                    } catch (Exception e) {
                                        reasonFailure.append("还款期数过大!");
                                    }
                                }
                                param.put("interestPeriods", interestPeriods);

                                String payInterestPeriods = ExcelUtils.getExcelValue(rowData, 28);
                                if (StringUtils.isNotBlank(payInterestPeriods)) {
                                    try {
                                        Integer.parseInt(payInterestPeriods);
                                    } catch (Exception e) {
                                        reasonFailure.append("已还款数过大!");
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
                                            reasonFailure.append("保险超出范围!");
                                        }
                                    } catch (Exception e) {
                                        reasonFailure.append("保险超出范围");
                                    }
                                    Number value = Double.parseDouble(excelValue7) * 100;
                                    param.put("collectionInsurance", value.intValue());
                                }


                                String excelValue6 = ExcelUtils.getExcelValue(rowData, 32);
                                if (StringUtils.isNotBlank(excelValue6)) {
                                    try {
                                        Long excelValue6Integer = Long.parseLong(excelValue6);
                                        if (excelValue6Integer * 100 > Integer.MAX_VALUE) {
                                            reasonFailure.append("审车费超出范围!");
                                        }
                                    } catch (Exception e) {
                                        reasonFailure.append("审车费超出范围");
                                    }
                                    Number value = Double.parseDouble(excelValue6) * 100;
                                    param.put("examVehicleFee", value.intValue());
                                }
                                String excelValue5 = ExcelUtils.getExcelValue(rowData, 33);
                                if (StringUtils.isNotBlank(excelValue5)) {
                                    try {
                                        Long excelValue5Integer = Long.parseLong(excelValue5);
                                        if (excelValue5Integer * 100 > Integer.MAX_VALUE) {
                                            reasonFailure.append("保养费超出范围!");
                                        }
                                    } catch (Exception e) {
                                        reasonFailure.append("保养费超出范围");
                                    }
                                    Number value = Double.parseDouble(excelValue5) * 100;
                                    param.put("maintainFee", value.intValue());
                                }

                                String excelValue2 = ExcelUtils.getExcelValue(rowData, 34);
                                if (StringUtils.isNotBlank(excelValue2)) {
                                    try {
                                        Long excelValue2Integer = Long.parseLong(excelValue2);
                                        if (excelValue2Integer * 100 > Integer.MAX_VALUE) {
                                            reasonFailure.append("维修费超出范围!");
                                        }
                                    } catch (Exception e) {
                                        reasonFailure.append("维修费超出范围");
                                    }
                                    Number value = Double.parseDouble(excelValue2) * 100;
                                    param.put("repairFee", value.intValue());
                                }

                                String excelValue3 = ExcelUtils.getExcelValue(rowData, 35);
                                if (StringUtils.isNotBlank(excelValue3)) {
                                    try {
                                        Long excelValue3Integer = Long.parseLong(excelValue3);
                                        if (excelValue3Integer * 100 > Integer.MAX_VALUE) {
                                            reasonFailure.append("轮胎费超出范围!");
                                        }
                                    } catch (Exception e) {
                                        reasonFailure.append("轮胎费超出范围");
                                    }
                                    Number value = Double.parseDouble(excelValue3) * 100;
                                    param.put("tyreFee", value.intValue());
                                }

                                String excelValue4 = ExcelUtils.getExcelValue(rowData, 36);
                                if (StringUtils.isNotBlank(excelValue4)) {
                                    try {
                                        Long excelValue4Integer = Long.parseLong(excelValue4);
                                        if (excelValue4Integer * 100 > Integer.MAX_VALUE) {
                                            reasonFailure.append("其他费用超出范围!");
                                        }
                                    } catch (Exception e) {
                                        reasonFailure.append("其他费用超出范围");
                                    }
                                    Number value = Double.parseDouble(excelValue4) * 100;
                                    param.put("otherFee", value.intValue());
                                }

                                String excelValue8 = ExcelUtils.getExcelValue(rowData, 37);
                                if (StringUtils.isNotBlank(excelValue8)) {
                                    Number value = Double.parseDouble(excelValue8) * 100;
                                    param.put("manageFee", value.intValue());//管理费
                                }


                                reasonFailure.append(ExcelUtils.checkDateTypeValueAdd(rowData, 40, param, "annualVeriTime", "上次年审时间"));
                                reasonFailure.append(ExcelUtils.checkDateTypeValueAdd(rowData, 41, param, "annualVeriTimeEnd", "年审到期时间"));
                                reasonFailure.append(ExcelUtils.checkDateTypeValueAdd(rowData, 42, param, "seasonalVeriTime", "上次季审时间"));
                                reasonFailure.append(ExcelUtils.checkDateTypeValueAdd(rowData, 43, param, "seasonalVeriTimeEnd", "季审到期时间"));
                                reasonFailure.append(ExcelUtils.checkDateTypeValueAdd(rowData, 44, param, "busiInsuranceTime", "上次商业险时间"));
                                reasonFailure.append(ExcelUtils.checkDateTypeValueAdd(rowData, 45, param, "busiInsuranceTimeEnd", "商业险到期时间"));
                                String busiInsuranceCode = ExcelUtils.getExcelValue(rowData, 46);//商业险单号
                                if (StringUtils.isNotBlank(busiInsuranceCode) && busiInsuranceCode.length() > 20) {
                                    reasonFailure.append("商业险单号超过20位");
                                }
                                param.put("busiInsuranceCode", busiInsuranceCode);


                                reasonFailure.append(ExcelUtils.checkDateTypeValueAdd(rowData, 47, param, "insuranceTime", "上次交强险时间"));
                                reasonFailure.append(ExcelUtils.checkDateTypeValueAdd(rowData, 48, param, "insuranceTimeEnd", "交强险到期时间"));
                                String insuranceCode = ExcelUtils.getExcelValue(rowData, 49);//交强险单号
                                if (StringUtils.isNotBlank(insuranceCode) && insuranceCode.length() > 20) {
                                    reasonFailure.append("交强险单号超过20位");
                                }
                                param.put("insuranceCode", insuranceCode);//交强险单号

                                reasonFailure.append(ExcelUtils.checkDateTypeValueAdd(rowData, 50, param, "otherInsuranceTime", "上次其他险时间"));
                                reasonFailure.append(ExcelUtils.checkDateTypeValueAdd(rowData, 51, param, "otherInsuranceTimeEnd", "其他险到期时间"));
                                String otherInsuranceCode = ExcelUtils.getExcelValue(rowData, 52);//其他险单号
                                if (StringUtils.isNotBlank(otherInsuranceCode) && otherInsuranceCode.length() > 20) {
                                    reasonFailure.append("其他险单号超过20位");
                                }
                                param.put("otherInsuranceCode", otherInsuranceCode);//其他险单号
                                String excelValue11 = ExcelUtils.getExcelValue(rowData, 53);
                                if (StringUtils.isNotBlank(excelValue11)) {
                                    Number value = Double.parseDouble(excelValue11) * 100;
                                    param.put("maintainDis", value.longValue());//保养周期(公里)
                                }
                                String excelValue10 = ExcelUtils.getExcelValue(rowData, 54);
                                if (StringUtils.isNotBlank(excelValue10)) {
                                    Number value = Double.parseDouble(excelValue10) * 100;
                                    param.put("maintainWarnDis", value.longValue());//保养预警公里(公里)
                                }
                                reasonFailure.append(ExcelUtils.checkDateTypeValueAdd(rowData, 55, param, "prevMaintainTime", "上次保养时间"));
                                reasonFailure.append(ExcelUtils.checkDateTypeValueAdd(rowData, 56, param, "registrationTime", "登记日期"));
                                param.put("registrationNumble", ExcelUtils.getExcelValue(rowData, 57));//登记证号
                                reasonFailure.append(ExcelUtils.checkDateTypeValueAdd(rowData, 58, param, "vehicleValidityTimeBegin", "行驶证生效时间"));
                                reasonFailure.append(ExcelUtils.checkDateTypeValueAdd(rowData, 59, param, "vehicleValidityTime", "行驶证失效时间"));
                                reasonFailure.append(ExcelUtils.checkDateTypeValueAdd(rowData, 60, param, "operateValidityTimeBegin", "营运证生效时间"));
                                reasonFailure.append(ExcelUtils.checkDateTypeValueAdd(rowData, 61, param, "operateValidityTime", "营运证失效时间"));
                                param.put("vehicleModel", ExcelUtils.getExcelValue(rowData, 62));
                            }
                        }
                    }
                    if (StrUtil.isEmpty(reasonFailure)) {
                        //新增
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
                        //返回成功数量
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
                showName = new String[]{"车牌号码", "车架号", "失败原因"};
                resourceFild = new String[]{"getPlateNumber", "getVinNo", "getFailure"};
                XSSFWorkbook workbook = ExportExcel.getWorkbookXlsx(failureList, showName, resourceFild, VehicleInfoVo.class, null);
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                workbook.write(os);
                byte[] b = os.toByteArray();
                InputStream inputStream1 = new ByteArrayInputStream(b);
                //上传文件
                FastDFSHelper client = FastDFSHelper.getInstance();
                String failureExcelPath = client.upload(inputStream1, "车辆信息.xlsx", inputStream1.available());
                os.close();
                is.close();
                records.setFailureUrl(failureExcelPath);
                if (success > 0) {
                    records.setRemarks(success + "条成功" + failureList.size() + "条失败");
                    records.setState(2);
                }
                if (success == 0) {
                    records.setRemarks(failureList.size() + "条失败");
                    records.setState(4);
                }
            } else {
                records.setRemarks(success + "条成功");
                records.setState(3);
            }
            importOrExportRecordsService.update(records);
        } catch (Exception e) {
            try {
                String[] showName;
                String[] resourceFild;
                showName = new String[]{"车牌号码", "车架号", "失败原因"};
                resourceFild = new String[]{"getPlateNumber", "getVinNo", "getFailure"};
                XSSFWorkbook workbook = null;
                workbook = ExportExcel.getWorkbookXlsx(failureList, showName, resourceFild, VehicleInfoVo.class, null);
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                workbook.write(os);
                byte[] b = os.toByteArray();
                InputStream inputStream1 = new ByteArrayInputStream(b);
                //上传文件
                FastDFSHelper client = FastDFSHelper.getInstance();
                String failureExcelPath = client.upload(inputStream1, "车辆信息.xlsx", inputStream1.available());
                os.close();
                inputStream1.close();
                records.setFailureUrl(failureExcelPath);
                records.setRemarks("导入失败");
                records.setFailureReason("文件上传失败，请重试!");
                records.setState(4);
                importOrExportRecordsService.update(records);
            } catch (Exception exception) {
                records.setState(5);
                records.setFailureReason("导出失败,请检查模版数据是否正确！");
                records.setRemarks("导出失败,请检查模版数据是否正确！");
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
            throw new BusinessException("车辆编号错误！");
        }
        VehicleDataInfo vehicleDataInfo = baseMapper.selectById(vehicleCode);
        if (null == vehicleDataInfo) {
            throw new BusinessException("车辆信息不存在！");
        }

        List<TenantVehicleRel> tenantVehicleRelList = tenantVehicleRelMapper.getTenantVehicleRelList(vehicleCode);
        if (tenantVehicleRelList == null && tenantVehicleRelList.size() == 0) {
            throw new BusinessException("车辆信息不存在！");
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

            //删除自有车删除所有的绑定关系，删除其他类型的车只删除车辆与车队的关系，不删除主表数据，绑定多量车的司机不删除
            if (null != tenantVehicleRel.getVehicleClass() && tenantVehicleRel.getVehicleClass().intValue() == VEHICLE_CLASS1) {
                //删除自有车需要将此车辆在车队存在的其他类型移到历史
                removeOwnCarAndNotifyOtherTenant(vehicleDataInfo.getId(), accessToken);
                //清除邀请
                removeApplyRecord(vehicleCode);
                vehicleDataInfo.setTenantId(null);
            } else {
                try {
                    //先创建历史记录信息,再将车辆移到历史档案
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
                //勾选了删除司机才删除司机
                if (delOutVehicleDriver != null && delOutVehicleDriver && tenantVehicleRel.getDriverUserId() != null) {
                    doRemoveDriverForVehicle(tenantVehicleRel.getDriverUserId(), tenantVehicleRel.getTenantId(), accessToken);
                }
            }
        }
        saveSysOperLog(SysOperLogConst.BusiCode.Vehicle, tenantVehicleRel.getId(), SysOperLogConst.OperType.Del, "移除车辆" + vehicleDataInfo.getPlateNumber(), accessToken);
        return true;
    }

    /**
     * 把source转为target
     *
     * @param source source
     * @param target target
     * @param <T>    返回值类型
     * @return 返回值
     * @throws Exception newInstance可能会抛出的异常
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
     * 判断字符串是否是数字
     */
    public static boolean isNumber(String value) {
        return isInteger(value) || isDouble(value) || isLong(value);
    }

    /**
     * 判断字符串是否是整数
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
     * 判断字符串是否是浮点数
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
     * 判断字符串是否是整数
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
            throw new BusinessException("destVerState参数错误！");
        }
        //车辆主表
        if ("vehicle_data_info_ver".equals(tableName) || "all".equals(tableName)) {
            vehicleDataInfoVerMapper.updVehicleDataInfoVerState(destVerState, vehicleCode);
        }
        //心愿线路表
        if ("vehicle_object_line_ver".equals(tableName) || "all".equals(tableName)) {
            vehicleDataInfoVerMapper.updVehicleObjectLineVerState(destVerState, vehicleCode);
        }
        //绑定线路
        if ("vehicle_line_rel_ver".equals(tableName) || "all".equals(tableName)) {
            vehicleDataInfoVerMapper.updVehicleLineRelVerState(destVerState, vehicleCode);
        }
        //车辆租户关系表
        if ("tenant_vehicle_rel_ver".equals(tableName) || "all".equals(tableName)) {
            vehicleDataInfoVerMapper.updTenantVehicleRelVerState(destVerState, vehicleCode, tenantId);
        }
        //车辆成本表
        if ("tenant_vehicle_cost_rel_ver".equals(tableName) || "all".equals(tableName)) {
            vehicleDataInfoVerMapper.updTenantVehicleCostRelVerState(destVerState, vehicleCode, tenantId);
        }
        //证照信息表
        if ("tenant_vehicle_cert_rel_ver".equals(tableName) || "all".equals(tableName)) {
            vehicleDataInfoVerMapper.updTenantVehicleCertRelVerState(destVerState, vehicleCode, tenantId);
        }
    }


    public void doRemoveDriverForVehicle(long userId, long tenantId, String accessToken) {
        List<ApplyRecord> applyRecordList = applyRecordService.getApplyRecordList(userId, 0, tenantId);
        if (CollectionUtils.isNotEmpty(applyRecordList)) {
            throw new BusinessException("司机处理中，请稍后再试！");
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
            log.error("删除司机错误，不存在的关系记录，relId====>" + tenantUserRelId);
            throw new BusinessException("数据错误");
        }

        List<ApplyRecord> applyRecordList = applyRecordService.getApplyRecordList(tenantUserRel.getUserId(), 0, tenantUserRel.getTenantId());
        if (CollectionUtils.isNotEmpty(applyRecordList)) {
            throw new BusinessException("司机处理中，请稍后再试！");
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
        saveSysOperLog(SysOperLogConst.BusiCode.Driver, tenantUserRel.getId(), SysOperLogConst.OperType.Remove, "移除会员", accessToken);
    }

    //map转java对象
    public static Object mapToObject(Map<String, Object> map, Class<?> beanClass)
            throws Exception {
        if (map == null) {
            return null;
        }
        Object obj = beanClass.newInstance();
        org.apache.commons.beanutils.BeanUtils.populate(obj, map);
        return obj;
    }

    //清除邀请
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

        //日志渠道区分
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
        //原有的挂靠关系转移到历史档案
        if (tenantVehicleRelList != null && tenantVehicleRelList.size() > 0) {
            oldTenantVehicleRel = tenantVehicleRelList.get(0);
            if (oldTenantVehicleRel.getTenantId() == attachTenantId) {//该租户下已经有这个挂靠车，直接返回，不做任何处理
                return;
            }
            dissolveCooperationVehicle(oldTenantVehicleRel.getId(), accessToken);

            //将原来车队的司机移到历史档案
            doRemoveDriverOnlyOneVehicle(vehicleDataInfo.getDriverUserId(), oldTenantVehicleRel.getTenantId());
        }

        //建立新的挂靠关系
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

       /* //启动审核流程
        Map iMap = new HashMap();
        iMap.put("svName", "vehicleTF");
        iMap.put("tenantVehicleRelId", tenantVehicleRel.getId());
        //启动车辆审核
        boolean bool = iAuditOutTF.startProcess("100005", tenantVehicleRel.getId(), SysOperLogConst.BusiCode.Vehicle, iMap, attachTenantId);
        if (!bool) {
            throw new BusinessException("启动审核流程失败！");
        }

        //写日志
        String driverLogMsg = tenantVehicleRel.getPlateNumber() + "在" + channelName + "被添加为车队(" + attachTenantId + ")的外来挂靠车";

        saveSysOperLog(SysOperLogConst.BusiCode.Vehicle, tenantVehicleRel.getId(), SysOperLogConst.OperType.Add, driverLogMsg, tenantVehicleRel.getTenantId(), accessToken);
*/
        if (oldTenantVehicleRel != null) {
            //先判断司机是否存在
            List<TenantUserRel> exitOldTenantUserRels = tenantUserRelMapper.getTenantUserRelList(vehicleDataInfo.getDriverUserId(), oldTenantVehicleRel.getTenantId(), VEHICLE_CLASS4);
            if (exitOldTenantUserRels != null && exitOldTenantUserRels.size() > 0) {
                //将原来车队的司机移到历史档案
                doRemoveDriverOnlyOneVehicle(vehicleDataInfo.getDriverUserId(), oldTenantVehicleRel.getTenantId());
            }
        }

        //判断新车队的司机关系是否存在

        //查询这个车辆自有车信息
        List<TenantUserRel> exitTenantUserRels = tenantUserRelMapper.getTenantUserRelList(vehicleDataInfo.getDriverUserId(), null, null);
        if (exitTenantUserRels == null || exitTenantUserRels.size() == 0) {//不存在挂靠司机，需要增加一个挂靠司机关系
            //建立新的司机关系
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
            //启动审核流程
            Map userMap = new HashMap();
            userMap.put("svName", "vehicleTF");
            userMap.put("tenantUserRelId", tenantUserRel.getId());

            //启动司机审核
            // boolean boolD = iAuditOutTF.startProcess(AUDIT_CODE_USER, tenantUserRelVer.getId(), SysOperLogConst.BusiCode.Driver, iMap, attachTenantId);

            /*if (!boolD) {
                throw new BusinessException("启动审核流程失败！");
            }*/

            //写日志
            String driverMsg = "司机" + tenantUserRel.getUserId() + "在" + channelName + "被添加为车队" + attachTenantId + "的外来挂靠车司机";
            saveSysOperLog(SysOperLogConst.BusiCode.Driver, tenantUserRel.getUserId(), SysOperLogConst.OperType.Add, driverMsg, accessToken);

        } else {
            TenantUserRel tenantUserRel = exitTenantUserRels.get(0);
            if (tenantUserRel.getCarUserType() != SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS4) {
                throw new BusinessException("被挂靠车队已经存在车量绑定的司机，且不是外来挂靠司机，请更换司机！");
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
                    throw new BusinessException("司机处理中，请稍后再试！");
                }

            }
        }
    }

    @Override
    @Transactional
    public Boolean doSaveApplyRecordForOwnCar(ApplyRecorDto applyRecorDto, String accessToken) {
        long vehicleCode = applyRecorDto.getVehicleCode();
        if (vehicleCode <= 0) {
            log.error("找不到车辆主键！");
            throw new BusinessException("找不到车辆主键!");
        }
        int vehicleClass = applyRecorDto.getApplyVehicleClass();
        if (vehicleClass <= 0) {
            log.error("找不到车辆类型！");
            throw new BusinessException("找不到车辆类型!");
        }

        if (vehicleClass == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS2 || vehicleClass == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS4) {
            String billReceiverMobile = applyRecorDto.getBillReceiverMobile();
            String billReceiverName = applyRecorDto.getBillReceiverName();

            if (org.apache.commons.lang.StringUtils.isBlank(billReceiverMobile)) {
                log.error("请输入账单接收人手机！");
                throw new BusinessException("请输入账单接收人手机!");
            }

            if (org.apache.commons.lang.StringUtils.isBlank(billReceiverName)) {
                log.error("请输入账单接收人名称！");
                throw new BusinessException("请输入账单接收人名称!");
            }
        }

        /*long applyDriverUserId = DataFormat.getLongKey(imParam, "applyDriverUserId");
        if ((vehicleClass == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1
                || vehicleClass == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS4)
                && applyDriverUserId <= 0) {
            throw new BusinessException("请选择接收司机！");
        }*/
        String applyRemark = applyRecorDto.getApplyRemark();
        if (org.apache.commons.lang.StringUtils.isEmpty(applyRemark)) {
            log.error("请输入申请说明！");
            throw new BusinessException("请输入申请说明!");
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
            throw new BusinessException("车辆关系不存在");
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
                //更新原来记录状态为0
                updateVehicleVerAllVerState(SysStaticDataEnum.VER_STATE.VER_STATE_N, vehicleCostRel.getId(), vehicleCostRel.getTenantId());
                tenantVehicleCostRelMapper.deleteById(tenantVehicleCostRelList.get(i).getId());
            }
        }

        //删除证照信息
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
                //更新原来记录状态为0
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
//        String msg = "车辆-" + tenantVehicleRel.getPlateNumber() + "-与车队-" + tenantVehicleRel.getTenantId() + "-解除" +
//                getSysStaticData("VEHICLE_CLASS", tenantVehicleRel.getVehicleClass() + "") + "关系";
        String msg = "车辆-" + tenantVehicleRel.getPlateNumber() + "-与车队-" + tenantVehicleRel.getTenantId() + "-解除" +
                SysStaticDataRedisUtils.getSysStaticDataByCodeValue2String(redisUtil, "VEHICLE_CLASS", tenantVehicleRel.getVehicleClass().toString()) + "关系";

        //写日志
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
        //车辆不存在，不创建
        VehicleDataInfo vehicleDataInfo = baseMapper.selectById(vehicleCode);
        if (vehicleDataInfo == null) {
            return;
        }
        //有自有属性，不创建
        List<TenantVehicleRel> zyVehicleByVehicleCode = tenantVehicleRelMapper.getZYVehicleByVehicleCode(vehicleCode, 1);
        if (zyVehicleByVehicleCode != null && zyVehicleByVehicleCode.size() > 0 && StringUtils.isNotBlank(zyVehicleByVehicleCode.get(0).getPlateNumber())) {
            return;
        }
        //已经有C端记录，不创建
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
//                //web端保存心愿线路要乘以100，APP不需要
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

            if (oldVehicleObjectLine != null) {//修改原有记录
                // id 不进行浅拷贝
                BeanUtil.copyProperties(oldVehicleObjectLine, vehicleObjectLineVer, new String[]{"id", "create_time", "update_time"});
                vehicleObjectLineVer.setCarriagePrice(carriagePrice != null ? carriagePrice : null); // 传来的 价格
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
                    //无自有车属性，不需要审核，修改直接生效
                    BeanUtil.copyProperties(vehicleObjectLineVer, oldVehicleObjectLine);
                    oldVehicleObjectLine.setOpId(loginUtils.get(accessToken).getUserInfoId());
                    oldVehicleObjectLine.setUpdateTime(LocalDateTime.now());
                    vehicleObjectLineMapper.updateById(oldVehicleObjectLine);
                    //自动审核通过
                    vehicleObjectLineVer.setIsAuthSucc(3);

                }
                vehicleObjectLineVer.setCreateTime(LocalDateTime.now());
                //最后设置，避免复制属性值时覆盖掉原来记录的时间
//                vehicleObjectLineVerMapper.insert(vehicleObjectLineVer);
                iVehicleObjectLineVerService.saveOrUpdate(vehicleObjectLineVer);
            } else {//新增记录

                //先判断车辆有多少心愿线路，有3条了则不再增加
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
                    vehicleObjectLineVer.setCarriagePrice(newVehicleObjectLine.getCarriagePrice()); // 传来的 价格
                    if (unNeedCheck) {//无自有车属性，不需要审核，修改直接生效
                        vehicleObjectLineVer.setIsAuthSucc(3);//自动审核通过
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
            throw new BusinessException("destVerState参数错误！");
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
     * 方法实现说明  通过id 获取星愿线路名称
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
            //待补
//                        Long carriagePrice = vehicleObjectLine.getCarriagePrice();
//                        vehicleOjbectLineVo.setCarriagePrice(Long.valueOf(CommonUtil.divide(carriagePrice)));
        }
        return vehicleOjbectLineVo;
    }

    /**
     * 方法实现说明  获取省市区名称
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
            throw new BusinessException("司机资料不全，请先核对司机资料");
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
        //判断是否记录闲置信息
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
            return "车牌号格式错误！";
        }
        if (driverUserId < 0) {
            return "司机不能为空错误！";
        }
        if (licenceType < 0) {
            return "车辆牌照类型错误！";
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
        //增加一条平台车记录
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
        //iSysOperLogService.saveSysOperLog(SysOperLogConst.BusiCode.Vehicle, id, SysOperLogConst.OperType.Add, "车辆" + saveVehicleData.getPlateNumber() + "快速新增成功");
        return null;
    }


    public static boolean isCarNo(String carNo) {
        Pattern p = Pattern.compile("^[京津沪渝冀豫云辽黑湘皖鲁新苏浙赣鄂桂甘晋蒙陕吉闽贵粤青藏川宁琼使领A-Z]{1}[A-Z]{1}(?:(?![A-Z]{4})[A-Z0-9]){4}[A-Z0-9挂学警港澳]{1}$");
        Matcher m = p.matcher(carNo);
        if (!m.matches()) {
            return false;
        }
        return true;
    }


//    public void addPtTenantVehicleRelWithDriver(long vehicleCode, Long driverUserId) throws Exception {
//
//        //车辆不存在，不创建
//        VehicleDataInfo vehicleDataInfo = this.getById(vehicleCode);
//        if (vehicleDataInfo == null) {
//            return;
//        }
//
//        //有自有属性，不创建
//        TenantVehicleRel zyVehicleRel = iTenantVehicleRelService.getZYVehicleByVehicleCode(vehicleCode);
//        if (zyVehicleRel != null && StringUtils.isNotBlank(zyVehicleRel.getPlateNumber())) {
//            return;
//        }
//
//        //已经有C端记录，不创建
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
     * 更新车量完整性
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
                //车牌号码
                completeNessStr += "1";
            } else {
                completeNessStr += "0";
            }
            if (vehicleDataInfo.getVehicleStatus() != null && vehicleDataInfo.getVehicleStatus() > 0) {
                //车辆类型
                completeNessStr += "1";
            } else {
                completeNessStr += "0";
            }
            if (StringUtils.isNotBlank(vehicleDataInfo.getVehicleLoad())) {
                //车辆载重
                completeNessStr += "1";
            } else {
                completeNessStr += "0";
            }
            if (StringUtils.isNotBlank(vehicleDataInfo.getLightGoodsSquare())) {
                //车辆容积
                completeNessStr += "1";
            } else {
                completeNessStr += "0";
            }
            if (StringUtils.isNotBlank(vehicleDataInfo.getVinNo())) {
                //车架号
                completeNessStr += "1";
            } else {
                completeNessStr += "0";
            }
            if (StringUtils.isNotBlank(vehicleDataInfo.getEngineNo())) {
                //发动机号
                completeNessStr += "1";
            } else {
                completeNessStr += "0";
            }

            if (StringUtils.isNotBlank(vehicleDataInfo.getBrandModel())) {
                //品牌
                completeNessStr += "1";
            } else {
                completeNessStr += "0";
            }
            if (StringUtils.isNotBlank(vehicleDataInfo.getOperCerti())) {
                //运输证号
                completeNessStr += "1";
            } else {
                completeNessStr += "0";
            }
            if (StringUtils.isNotBlank(vehicleDataInfo.getDrivingLicenseOwner())) {
                //所有人
                completeNessStr += "1";
            } else {
                completeNessStr += "0";
            }
            if (tenantVehicleRel.getLoadEmptyOilCost() != null && tenantVehicleRel.getLoadEmptyOilCost() >= 0) {
                //空载油耗
                completeNessStr += "1";
            } else {
                completeNessStr += "0";
            }
            if (vehicleDataInfo.getVehiclePicture() != null && vehicleDataInfo.getVehiclePicture() > 0) {
                //车辆图片
                completeNessStr += "1";
            } else {
                completeNessStr += "0";
            }
            if (vehicleDataInfo.getOperCertiId() != null && vehicleDataInfo.getOperCertiId() > 0) {
                //运输证
                completeNessStr += "1";
            } else {
                completeNessStr += "0";
            }
            if (StringUtils.isNotBlank(vehicleDataInfo.getDrivingLicense())) {
                //行驶证正本
                completeNessStr += "1";
            } else {
                completeNessStr += "0";
            }
            if (vehicleDataInfo.getAdriverLicenseCopy() != null && vehicleDataInfo.getAdriverLicenseCopy() > 0) {
                //行驶证副本
                completeNessStr += "1";
            } else {
                completeNessStr += "0";
            }
            if (vehicleDataInfo.getRentAgreementId() != null && vehicleDataInfo.getRentAgreementId() > 0) {
                //车辆租赁协议
                completeNessStr += "1";
            } else {
                completeNessStr += "0";
            }
            if (StringUtils.isNotBlank(vehicleDataInfo.getVehicleLength())) {
                //车长
                completeNessStr += "1";
            } else {
                completeNessStr += "0";
            }
            if (vehicleDataInfo.getSpecialOperCertFileId() != null && vehicleDataInfo.getSpecialOperCertFileId() > 0) {
                //危险品营运证
                completeNessStr += "1";
            } else {
                completeNessStr += "0";
            }
            if (tenantVehicleRel.getLoadFullOilCost() != null && tenantVehicleRel.getLoadFullOilCost() >= 0) {
                //载重油耗
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
            throw new BusinessException("车辆ID为空！");
        }
        VehicleDataInfo vehicleDataInfo = getById(vehicleCode);
        if (vehicleDataInfo == null) {
            throw new BusinessException("车辆信息为空！");
        }
        QueryWrapper<TenantVehicleRel> tenantVehicleRelQueryWrapper = new QueryWrapper<>();
        tenantVehicleRelQueryWrapper.eq("vehicle_code", vehicleCode);
        List<TenantVehicleRel> list = iTenantVehicleRelService.list(tenantVehicleRelQueryWrapper);
        if (null != list && list.size() > 0) {
            //先创建一份主表的历史记录信息
            VehicleDataInfoVer vehicleDataInfoVer = iVehicleDataInfoVerService.doAddVehicleDataInfoVerHis(vehicleCode);
            for (int i = 0; i < list.size(); i++) {
                TenantVehicleRel t = list.get(i);
                dissolveCooperationVehicle(t.getId(), vehicleDataInfoVer.getId(), token);
            }
        }
        baseMapper.doUpdateVehicleObjectLine(vehicleDataInfo.getId());//清空心愿线路
        //线路绑定
        baseMapper.doUpdateVehicleLineRelByVehicleCode(vehicleDataInfo.getId());
        baseMapper.doUpdateVehicleLineRelVerByVehicleCode(vehicleDataInfo.getId());
        //解绑ETC
        iEtcMaintainService.untieEtc(origTenantId, vehicleDataInfo.getPlateNumber());
        //清除数据
        vehicleDataInfo.setTenantId(null);
        vehicleDataInfo.setCopilotDriverId(null);
        //解除随车司机
        vehicleDataInfo.setFollowDriverId(null);
        vehicleDataInfo.setOperCerti(null);//运营证号
        vehicleDataInfo.setEtcCardNumber(null);//etc卡号
        vehicleDataInfo.setEquipmentCode(null);//gps设备号
        vehicleDataInfo.setContactNumber(null);//随车手机
        vehicleDataInfo.setFollowDriverId(null);//随车司机id
        vehicleDataInfo.setCopilotDriverId(null);//副驾驶id
        vehicleDataInfo.setDrivingLicense(null);//行驶证正本id
        vehicleDataInfo.setAdriverLicenseCopy(null);//行驶证副本id
        vehicleDataInfo.setOperCertiId(null);//运输证图片id
        vehicleDataInfo.setDrivingLicenseOwner(null);//行驶证上所有人
        vehicleDataInfo.setRentAgreementId(null);//车辆租赁协议
        vehicleDataInfo.setRentAgreementUrl(null);//车辆租赁协议URL
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
        //将共享标识关闭
        tenantVehicleRelNew.setShareFlg(SysStaticDataEnum.SHARE_FLG.NO);
        //位置信息
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
        //启动审核流程
        Map iMap = new HashMap();
        iMap.put("svName", "vehicleTF");
        iMap.put("tenantVehicleRelId", tenantVehicleRelNew.getId());
        saveSysOperLog(SysOperLogConst.BusiCode.Vehicle, tenantVehicleRelVer.getId(), SysOperLogConst.OperType.Audit, tenantVehicleRelNew.getPlateNumber() + "加入车队自有车!", loginInfo);
        boolean bool = iAuditService.startProcess(AUDIT_CODE_VEHICLE, tenantVehicleRelNew.getId(), SysOperLogConst.BusiCode.Vehicle, iMap, token);
        if (!bool) {
            throw new BusinessException("启动审核流程失败！");
        }
        return "Y";
    }


    /**
     * 实现功能: 审核操作
     *
     * @param busiCode     业务编码
     * @param busiId       业务主键id
     * @param desc         审核描述
     * @param chooseResult 1 审核通过 2 审核不通过
     * @param instId       通过这个判断当前待审核的数据是否跟数据库一致
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
                throw new BusinessException("该业务数据不能审核");
            }
            if (instId != null && instId > 0) {
                if (!instId.equals(auditNodeInstList.get(0).getId())) {
                    throw new BusinessException("页面审核的数据不是最新数据，请刷新后在进行审批！");
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
            vehicleStaticData.setCodeName("不限");
            vehicleStaticData.setCodeDesc("不限");
            vehicleStaticData.setCodeTypeAlias("不限");
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
            throw new BusinessException("参数错误，司机编号不合法");
        }
        List<VehicleDataInfo> list = baseMapper.getDriverAllRelVehicleList(userId);
        return list;
    }

    /**
     * 根据codeId找关联的车长
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
            throw new BusinessException("审核的业务编码为空");
        }

        if (busiId == null) {
            throw new BusinessException("传入的业务主键的ID为空");
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

                //审核不通过后，整个流程都结束
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

                //把该流程的状态都改为流程完成
                auditNodeInstService.updateAuditInstToFinish(auditNodeInst.getAuditId(), busiId);
                //移除到历史表
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
                throw new BusinessException("你没有权限审核该业务");
            }
        } else {
            throw new BusinessException("该流程节点已经审核处理过，不能再审核");
        }
    }

    /**
     * 实现功能: 审核逻辑
     * 1 判断当前的数据的状态是否是 待审核
     * 2 判断当前操作的人是否有权限审核这条数据
     * 3 查询是否有下一个节点
     * 4 如果有下一个节点，判断是否满足前置规则，不满足，流程结束调用回调方法；满足，更改当前的数据，并插入一条新的数据
     * 5 如果没有下一个节点，流程审核结束，调用回调方法
     */
    private boolean auditPass(Long auditNodeInstId, String auditCode, Long busiId, String desc, Integer
            version, Integer ruleVersion, Integer type, Long userId, String accessToken) {
        LoginInfo sysUser = loginUtils.get(accessToken);
        if (StringUtils.isBlank(auditCode)) {
            throw new BusinessException("审核的业务编码为空");
        }

        if (busiId == null) {
            throw new BusinessException("传入的业务主键的ID为空");
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
                        throw new BusinessException("配置的审核节点的下一个节点重复，当前的节点的主键[" + auditNodeInst.getNodeId() + "]");
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
                        //审核流程结束，修改当前的实例数据，并把该流程的数据的状态都改为流程完成，并调用回调函数通知业务侧
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
                throw new BusinessException("你没有权限审核该业务");
            }
        } else {
            throw new BusinessException("该流程节点已经审核处理过，不能再审核");
        }
        return false;
    }

    @Override
    public VehicleDataInfoVo getShareVehicle(Long vehicleCode, String accessToken) {
        try {
            LoginInfo loginInfo = loginUtils.get(accessToken);
            //查出车辆基本信息
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

                //查询司机信息
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
                    vehicleDataInfoVo.setDriverCarUserTypeName("平台司机");
                    vehicleDataInfoVo.setDriverCarUserType(-1);
                }

                //车辆归属自有车车队信息
                SysTenantDef sysTenantDef = sysTenantDefService.getById(vehicleDataInfoVo.getTenantId());
                if (sysTenantDef != null) {
                    vehicleDataInfoVo.setTenantName(sysTenantDef.getName());
                    vehicleDataInfoVo.setTenantLinkMan(sysTenantDef.getLinkMan());
                    vehicleDataInfoVo.setLinkPhone(sysTenantDef.getLinkPhone());
                }

                //查询心愿线路
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
                throw new BusinessException("车牌号或者车辆编码错误！");
            } else {
                QueryWrapper<VehicleDataInfo> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("plate_number", plateNumbers);
                VehicleDataInfo vehicleDataInfo = baseMapper.selectOne(queryWrapper);
                if (null == vehicleDataInfo) {
                    throw new BusinessException("车辆不存在！");
                }
                vehicleCode = vehicleDataInfo.getId();
            }
        }
        LoginInfo loginInfo = loginUtils.get(accessToken);
        //先查出车辆基本信息
        Map<String, Object> map = tenantVehicleRelVerMapper.getTenantVehicleInfoVer(vehicleCode, verState, loginInfo.getTenantId());

        if (null == map) {
            return null;
        }

        //判断车量是否有自有属性
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
        //查询司机信息
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
                        map.put("driverCarUserTypeName", "平台司机");
                    }
                } else {
                    map.put("driverCarUserType", -1);
                    map.put("driverCarUserTypeName", "平台司机");
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

        //判断如果是自有车,再查询成本信息
        if (vehicleClass == VEHICLE_CLASS1 || vehicleClass == VEHICLE_CLASS2 || vehicleClass == VEHICLE_CLASS4) {
            Long relId = (Long) map.get("relId");
            TenantVehicleRelInfoDto relMap = tenantVehicleCostRelVerMapper.getTenantVehicleRelInfoVer(vehicleCode, loginInfo.getTenantId(), relId, verState);
            if (null != relMap) {
                //处理回显费用
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
            //查询副驾驶司机信息
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
                            map.put("copilotDriverUserTypeName", "平台司机");
                        }
                    } else {
                        map.put("copilotDriverUserType", -1);
                        map.put("copilotDriverUserTypeName", "平台司机");
                    }
                }
            }


            //查询随车司机信息

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
                                map.put("followDriverUserTypeName", "平台司机");
                            }
                        } else {
                            map.put("followDriverUserType", -1);
                            map.put("followDriverUserTypeName", "平台司机");
                        }
                    }
                }
            }
            //车辆归属组织
            Long orgId = (Long) map.get("orgId");
            if (orgId != null && orgId > -1) {
                try {
                    map.put("orgName", iSysOrganizeService.getCurrentTenantOrgNameById(loginInfo.getTenantId(), orgId));
                } catch (RpcException e) {
                    e.printStackTrace();
                }
            }
            //归属人
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
            //共享联系人
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
                map.put("shareFlgName", "否");
            } else {
                map.put("shareFlgName", "是");
            }

        } else {
            long ownTenantId = DataFormat.getLongKey(map, "tenantId");
            SysTenantDef sysTenantDef = sysTenantDefService.getById(ownTenantId);
            //车辆归属自有车车队信息
            if (null != sysTenantDef) {
                map.put("tenantName", sysTenantDef.getName());
                map.put("tenantLinkMan", sysTenantDef.getLinkMan());
                map.put("tenantLinkPhone", sysTenantDef.getLinkPhone());
            }
        }
        // long hisId = DataFormat.getLongKey(map, "hisId");
        //查询心愿线路
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

        ///查询绑定线路
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
            //判断下个节点是否有前置规则，如果没有，修改当前实例数据的值，并插入一条新的数据
            //如果有前置规则，判断通过 修改当前实例数据的值，并插入一条新的数据
            auditNodeInst.setAuditManId(loginInfo.getUserInfoId());
            auditNodeInst.setRemark(desc);
            auditNodeInst.setAuditDate(LocalDateTime.now());
            auditNodeInst.setAuditResult(AuditConsts.RESULT.SUCCESS);
            auditNodeInstService.updateById(auditNodeInst);

            //插入新的流程数据
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
                //兼容旧的数据
                nextAuditNodeInst.setNodeIndex(auditNodeInst.getNodeIndex() + 1);
            }
            auditNodeInstService.save(nextAuditNodeInst);
            //节点的回调方法
            List<Long> auditUserList = auditUserService.selectIdByNodeIdAndTarGetObjType(nextAuditNodeInst.getNodeId(), AuditConsts.TargetObjType.USER_TYPE);
            try {
                nodeAuditCallback(nextAuditNodeInst.getNodeIndex() == null ? 0 : nextAuditNodeInst.getNodeIndex(), busiId, AuditConsts.TargetObjType.USER_TYPE, listLongToStr(auditUserList), auditConfig.getNodeCallback());
                StringBuffer alert = new StringBuffer("下个节点审核");
                alert.append(getAuditUserNameByNodeId(nextAuditNodeInst.getNodeId(), AuditConsts.TargetObjType.USER_TYPE));
                String msg = "";
                if (StringUtils.isNotBlank(auditNodeInst.getRemark())) {
                    msg = "，原因：[" + auditNodeInst.getRemark() + "]";
                }
                String auditCodeName = isysStaticDataService.getSysStaticData("AUDIT_CODE", auditConfig.getAuditCode()).getCodeName();
                saveSysOperLog(null, busiId, SysOperLogConst.OperType.Audit, auditCodeName + "审核通过" + msg, loginInfo);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        } else {
            //如果有前置规则，判断不通过 修改当前的实例数据，并把该流程的数据的状态都改为流程完成，并调用回调函数通知业务侧
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
            //把该流程的状态都改为流程完成
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
                //没有调整代码块的顺序，是怕影响写日志的顺序
                auditCallback(busiId, AuditConsts.RESULT.SUCCESS, desc, paramsMap, auditConfig.getCallback());
            }
            if (type == null || type.intValue() == AuditConsts.OperType.TASK) {
                String msg = "";
                if (StringUtils.isNotBlank(auditNodeInst.getRemark())) {
                    msg = "，原因：[" + auditNodeInst.getRemark() + "]";
                }
                String auditCodeName = isysStaticDataService.getSysStaticData("AUDIT_CODE", auditConfig.getAuditCode()).getCodeName();
                saveSysOperLog(null, busiId, SysOperLogConst.OperType.Audit, auditCodeName + "审核通过" + msg, loginInfo);
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
        //节点有配置规则，如果有多个规则，有一个满足就可以到下个节点
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
            //前置规则都没有满足，不需要走流程
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
            throw new BusinessException("传入的回调方法的结果类型只能1，2，传入的是[" + result + "]");
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
            alert.append("人:");
            List<String> userNameList = Lists.newArrayList();
            for (Long aLong : userIdList) {
                String userName = iUserDataInfoRecordService.getUserName(aLong);
                userNameList.add(userName);
            }
            alert.append(listToString(userNameList));
        } else {
            throw new BusinessException("下个节点的审核的对象类型设置错误");
        }
        return alert.toString();
    }

    public String listToString(List<String> list) {
        if (list == null) {
            return null;
        }
        StringBuilder result = new StringBuilder();
        boolean first = true;
        //第一个前面不拼接","
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
        //第一个前面不拼接","
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
            throw new BusinessException("请传入正确的车辆号码！");
        }
        LoginInfo loginInfo = loginUtils.get(accessToken);
        Integer vehicleClass = tenantVehicleRelMapper.checkVehicleClass(plateNumber, loginInfo.getTenantId());

        if (null != vehicleClass) {
//            if (VEHICLE_CLASS1 == vehicleClass) {
//                throw new BusinessException("该车辆已经为公司自有车，不允许重复添加，可到修改界面修改信息！");
//            } else if (SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS5 == vehicleClass) {
//                throw new BusinessException("该车辆已经为公司临时外调车，不允许重复添加，可到修改界面修改信息！");
//            } else if (SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS2 == vehicleClass) {
//                throw new BusinessException("该车辆已经为公司招商车，不允许重复添加，可到修改界面修改信息！");
//            } else if (SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS4 == vehicleClass) {
//                throw new BusinessException("该车辆已经为公司挂靠车，不允许重复添加，可到修改界面修改信息！");
//            }
            throw new BusinessException("该车牌号已注册！");
        }
        //车辆是否注册
        Map<String, Object> rtnMap = baseMapper.getVehicleIsRegister(vehicleClassIn, plateNumber);
        if (SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS4 == vehicleClassIn && rtnMap != null && DataFormat.getLongKey(rtnMap, "tenantId") > 0) {
            throw new BusinessException("该车辆已经为其他公司的挂靠车，不允许添加！");
        }

        //已注册
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

            //车辆可以绑定多个司机需求增加，查询车辆在各个车队绑定的司机
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
                                //第一个绑定的司机为车辆的归属司机
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
        //未注册直接返回空
        return rtnMap;
    }


    @Override
    public Map<String, Object> getVehicleIsRegisterDatainfo(String plateNumber, Integer vehicleClassIn, String accessToken) {
        if (StringUtils.isEmpty(plateNumber)) {
            throw new BusinessException("请传入正确的车辆号码！");
        }
        LoginInfo loginInfo = loginUtils.get(accessToken);
        Integer vehicleClass = tenantVehicleRelMapper.checkVehicleClass(plateNumber, loginInfo.getTenantId());

        if (null != vehicleClass) {
//            if (VEHICLE_CLASS1 == vehicleClass) {
//                throw new BusinessException("该车辆已经为公司自有车，不允许重复添加，可到修改界面修改信息！");
//            } else if (SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS5 == vehicleClass) {
//                throw new BusinessException("该车辆已经为公司临时外调车，不允许重复添加，可到修改界面修改信息！");
//            } else if (SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS2 == vehicleClass) {
//                throw new BusinessException("该车辆已经为公司招商车，不允许重复添加，可到修改界面修改信息！");
//            } else if (SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS4 == vehicleClass) {
//                throw new BusinessException("该车辆已经为公司挂靠车，不允许重复添加，可到修改界面修改信息！");
//            }
            throw new BusinessException("该车牌号已注册！");
        }
        //车辆是否注册
        Map<String, Object> rtnMap = baseMapper.getVehicleIsRegister(vehicleClassIn, plateNumber);
        if (SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS4 == vehicleClassIn && rtnMap != null && DataFormat.getLongKey(rtnMap, "tenantId") > 0) {
            throw new BusinessException("该车辆已经为其他公司的挂靠车，不允许添加！");
        }

        //已注册
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
//            //车辆可以绑定多个司机需求增加，查询车辆在各个车队绑定的司机
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
//                                //第一个绑定的司机为车辆的归属司机
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
            throw new BusinessException("该车牌号已注册！");
        }
        //未注册直接返回空
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
            log.error("车辆主键参数错误！");
//            return ResponseResult.failure("车辆主键参数错误！");
            throw new BusinessException("车辆主键参数错误!");
        }
        Integer vehicleClass = applyRecorDto.getApplyVehicleClass();
        if (vehicleClass < 0) {
            log.error("车辆类型参数错误");
            throw new BusinessException("车辆类型参数错误!");
        }
        if (applyState < 0) {
            log.error("申请状态参数错误！");
            throw new BusinessException("申请状态参数错误!");
        }
        Long countApply = applyRecordService.getApplyCount(loginInfo.getTenantId(), vehicleClass, vehicleCode);

        if (countApply > 0) {
            log.error("本车辆尚有未处理完成的邀请记录！");
            throw new BusinessException("本车辆尚有未处理完成的邀请记录!");
        }
        Long count = tenantVehicleRelMapper.getZYCount(vehicleCode, loginInfo.getTenantId(), vehicleClass);
        if (count > 0) {
            log.error("车辆在本车队已存在！");
            throw new BusinessException("车辆在本车队已存在!");
        }

        VehicleDataInfo vehicleDataInfo = baseMapper.selectById(vehicleCode);
        if (null == vehicleDataInfo) {
            log.error("车辆不存在！请到邀请界面重新发起邀请！");
            throw new BusinessException("车辆不存在!请到邀请界面重新发起邀请!");
        }

        //获取被邀请的车队信息
        Long beApplyTenantId = DataFormat.getLongKey(vehicleDataInfo.getTenantId() + "");
        // ITenantSV tenantSV = (ITenantSV)SysContexts.getBean("tenantSV");
        SysTenantDef sysTenantDef = null;
        if (beApplyTenantId > 1) {
            sysTenantDef = sysTenantDefService.getById(beApplyTenantId);
        }
//        if (beApplyTenantId > 0 && null == sysTenantDef) {
//            log.error("被邀请车队不存在，请核对信息是否正确");
//            throw new BusinessException("被邀请车队不存在，请核对信息是否正确!");
//        }
        if(applyRecorDto.getPublicVehicle() != null ){
            if(applyRecorDto.getPublicVehicle() == -1 && applyRecorDto.getApplyDriverUserId() == null && applyRecorDto.getApplyVehicleClass() != null && applyRecorDto.getApplyVehicleClass() != VEHICLE_CLASS5){
                throw new BusinessException("平台合作车必须绑定司机才可以邀请!");
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
                    log.error("此车辆在归属车队审核尚未通过，不允许申请转移！");
                    throw new BusinessException("此车辆在归属车队审核尚未通过，不允许申请转移!");
                }
                if (null != tenantVehicleRel.getAuthState() && tenantVehicleRel.getAuthState().intValue() != SysStaticDataEnum.AUTH_STATE.AUTH_STATE2) {
                    log.error("此车辆在归属车队审核尚未通过，不允许申请转移！");
                    throw new BusinessException("此车辆在归属车队审核尚未通过，不允许申请转移!");
                }
            } else {
                beApplyTenantId = -1L;
            }
        }
        Long applyRecordId = applyRecorDto.getApplyRecordId();

        //c端车
        if (beApplyTenantId == null || beApplyTenantId <= 1) {
            if (DataFormat.getLongKey(vehicleDataInfo.getDriverUserId() + "") > 0) {
                Long count1 = applyRecordService.getApplyVehicleCountByDriverUserId(loginInfo.getTenantId(), vehicleClass, vehicleDataInfo.getDriverUserId());
                if (count1 > 0) {
                    log.error("此车辆对应司机已经被邀请为其他类型的司机，不允许再邀请！");
                    throw new BusinessException("此车辆对应司机已经被邀请为其他类型的司机，不允许再邀请!");
                }
            }

            if (vehicleClass == VEHICLE_CLASS1 || vehicleClass == VEHICLE_CLASS2 || vehicleClass == VEHICLE_CLASS4) {
                List<TenantVehicleRel> tenantVehicleRelList = tenantVehicleRelMapper.queryTenantVehicleRelDriver(vehicleCode);
                if (tenantVehicleRelList == null || tenantVehicleRelList.isEmpty()) {
                    log.error("请先绑定司机再邀请");
                    throw new BusinessException("请先绑定司机再邀请!");
                }
            }
        }
        ApplyRecord applyRecord = new ApplyRecord();
        //查询车辆在车队绑定的司机
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
            // 行驶证副本(图片保存编号)
            vehicleDataInfoVer.setAdrivingLicenseCopy(vehicleDataInfo.getAdriverLicenseCopy());
            vehicleDataInfoVer.setOperCerti(vehicleDataInfo.getOperCerti());// 运营证
            vehicleDataInfoVer.setOperCertiId(vehicleDataInfo.getOperCertiId());//运营证图片ID(运输证)
            vehicleDataInfoVer.setVerState(VER_STATE_Y);
            vehicleDataInfoVer.setVehicleCode(vehicleDataInfo.getId());
            vehicleDataInfoVer.setCreateTime(LocalDateTime.now());
            vehicleDataInfoVerMapper.insert(vehicleDataInfoVer);
        }
        //再次申请
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
            applyRecord.setApplyDriverUserId(applyRecorDto.getApplyDriverUserId()); // 司机id
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

        //写日志
        // logSV.saveSysOperLog(SysOperLogConst.BusiCode.Vehicle, applyRecord.getId(), SysOperLogConst.OperType.Add, "邀请已注册车辆,类型:" + applyRecord.getApplyVehicleClass() + ",vehicleCode:" + vehicleCode);
        saveSysOperLog(SysOperLogConst.BusiCode.Vehicle, applyRecord.getId(), SysOperLogConst.OperType.Add, "邀请已注册车辆,类型:" + applyRecord.getApplyVehicleClass() + ",vehicleCode:" + vehicleCode, accessToken);
        //外调车，不用审核直接审核通过
        if (applyRecord.getApplyVehicleClass() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS5) {
            Map inMap = new HashMap();
            // TODO: 2022/3/23 系统自动审核
            this.sucessVehicle(applyRecord.getId(), "系统自动审核通过", inMap, loginInfo, accessToken);
            return true;
        }
        //查询车队的名称
        String tenantName = sysTenantDefService.getTenantName(loginInfo.getTenantId());

        if (null != sysTenantDef && sysTenantDef.getState().equals(SysStaticDataEnum.SYS_STATE_DESC.STATE_YES)) {

            if (sysTenantDef.getAdminUser() == null || sysTenantDef.getAdminUser() < 0) {
                log.error("车辆归属车队超管未设置，请先指定车队超管");
                throw new BusinessException("车辆归属车队超管未设置，请先指定车队超管!");
            }
            UserDataInfo dataInfo = userDataInfoRecordMapper.selectById(sysTenantDef.getAdminUser());
            if (null != dataInfo && StringUtils.isNotBlank(dataInfo.getMobilePhone())) {
                if (sysTenantDef != null && StringUtils.isNotBlank(sysTenantDef.getLinkPhone())) {
//                    //有归属租户的司机
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
                    //有属租户的车辆
                }
            } else {
                log.error("车辆归属车队超管手机号不存在，请先设置车队超管手机号");
                throw new BusinessException("车辆归属车队超管手机号不存在，请先设置车队超管手机号!");
            }

            //启动审核流程
            if (beApplyTenantId > 1) {
                Map inMap = new HashMap();
                try {
                    boolean bool = iAuditService.startProcess(AUDIT_CODE_APPLY_VEHICLE, applyRecord.getId(), SysOperLogConst.BusiCode.VehicleReg, inMap, accessToken, beApplyTenantId);
                    if (!bool) {
                        log.error("启动审核流程失败！");
                        throw new BusinessException("启动审核流程失败!");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


        } else if (sysTenantDef == null || sysTenantDef.getState().equals(SysStaticDataEnum.SYS_STATE_DESC.STATE_NO)) {//无车队或车队停用

            Long belongDriverUserId = applyRecorDto.getBelongDriverUserId();//页面传进来的归属司机
            //获取车辆的归属司机
            UserDataInfo userDataInfo = null;
            if (belongDriverUserId != null && belongDriverUserId > 0) {
                //页面传进来的归属司机
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
                //有归属司机，邀请信息发送给司机
                Map map = new HashMap();
                map.put("userName", userDataInfo.getLinkman());
                map.put("tenantName", tenantName);
                map.put("Vehicle", vehicleDataInfo.getPlateNumber());
                //无归属租户的车辆
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
                //无司机，车队停用，自动审核通过
                //自动审核通过
//                /*IAuditCallBackIntf successTF = new VehicleAuditCallBackTF();
//                successTF.sucess(applyRecord.getId(),"系统自动审核通过",null);*/
                this.sucessVehicle(applyRecord.getId(), "系统自动审核通过", null, loginInfo, accessToken);
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
        //审核通过
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

        //回写审核失败原因到车辆租户关系表
        if (null == tenantVehicleRelTmp) {
            throw new BusinessException("没有查到待审核的车辆主表数据！");
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
            throw new BusinessException("车辆信息不存在！");
        }
        if (vehicleDataInfoTmp != null && vehicleDataInfoVer != null) {
            if (!vehicleDataInfoTmp.getPlateNumber().equals(vehicleDataInfoVer.getPlateNumber())) {
                Long aLong = orderInfoMapper.queryVehicleOrderInfoIn(vehicleDataInfoTmp.getPlateNumber());
                if (aLong != null) {
                    throw new BusinessException("该车辆已经接单,不能修改车牌号码！");
                }
                vehicleDataInfoTmp.setPlateNumber(vehicleDataInfoVer.getPlateNumber());
            }
        }
        List<TenantVehicleRelVer> tenantVehicleRelVerList = iTenantVehicleRelVerService.getVehicleObjectVer(tenantVehicleRelTmp.getVehicleCode(), loginInfo.getTenantId());
        Integer vehicleClass = 0;
        if (tenantVehicleRelVerList != null && tenantVehicleRelVerList.size() > 0) {
            TenantVehicleRelVer tenantVehicleRelVer = tenantVehicleRelVerList.get(0);
            Long driverUserId = tenantVehicleRelVer.getDriverUserId();
            //审核司机之前判断司机是否已经审核通过
            TenantUserRel tmp = null;
            if (null != driverUserId && driverUserId > 0) {
                tmp = iTenantUserRelService.getAllTenantUserRelByUserId(driverUserId, loginInfo.getTenantId());
//				if (null == tmp) {
//					throw new BusinessException("未获取到车队司机数据！");
//				}
                if (tmp != null && null != tmp.getState() && tmp.getState() == SysStaticDataEnum.SYS_USER_STATE.AUDIT_NOT) {
                    throw new BusinessException("请先审核司机信息！");
                }
            }
            if (tenantVehicleRelVer.getRelId().intValue() != tenantVehicleRelTmp.getId().intValue()) {
                throw new BusinessException("车辆信息其他人已修改，请重新修改再审核！");
            }
            vehicleClass = tenantVehicleRelVer.getVehicleClass();
            if (vehicleClass == VEHICLE_CLASS2 || vehicleClass == VEHICLE_CLASS4) {

                if (tenantVehicleRelVer.getBillReceiverMobile() == null || org.apache.commons.lang.StringUtils.isEmpty(tenantVehicleRelVer.getBillReceiverMobile())) {
                    throw new BusinessException("账单接收人数据为空，请先补全账单接收人信息");
                }

                //账单接收人在系统不存在，则在系统生成一个收款人
                if (tenantVehicleRelVer.getBillReceiverUserId() == null || tenantVehicleRelVer.getBillReceiverUserId() < 0) {
                    UserReceiverInfo receiverInfo = iUserReceiverInfoService.initUserReceiverInfo(tenantVehicleRelVer.getBillReceiverMobile(), tenantVehicleRelVer.getBillReceiverName(), token);
                    tenantVehicleRelVer.setBillReceiverUserId(receiverInfo.getUserId());
                }
            }


            if (tenantVehicleRelTmp.getVehicleClass() == VEHICLE_CLASS1 && tenantVehicleRelVer.getVehicleClass() != VEHICLE_CLASS1) {
                //清除所有的车辆绑定记录
                removeVehicleAllRelTenant(tenantVehicleRelVer.getVehicleCode(), true, token);

                baseMapper.doUpdateVehicleObjectLine(tenantVehicleRelVer.getVehicleCode());//清空心愿线路
                baseMapper.doUpdateVehicleLineRelByVehicleCode(tenantVehicleRelVer.getVehicleCode());//清空绑定线路信息
                baseMapper.doUpdateVehicleLineRelVerByVehicleCode(tenantVehicleRelVer.getVehicleCode());//清空绑定线路信息


                //清除邀请
                removeApplyRecord(vehicleCode);

                //建立新的车辆关系
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
//                saveSysOperLog(SysOperLogConst.BusiCode.Vehicle, tenantVehicleRelVer.getVehicleCode(), SysOperLogConst.OperType.Audit, "新增(修改车辆类型)", loginInfo);
                saveSysOperLog(SysOperLogConst.BusiCode.Vehicle, tenantVehicleRelVer.getRelId(), SysOperLogConst.OperType.Audit, "新增(修改车辆类型)", loginInfo);
            } else {
                if (vehicleClass == VEHICLE_CLASS1 && null != tenantVehicleRelTmp.getShareFlg() && tenantVehicleRelTmp.getShareFlg() == 0) {
                    baseMapper.doDelVehicleOrderPositionInfo(tenantVehicleRelTmp.getVehicleCode());
                }
                LocalDateTime oriCreateDate = tenantVehicleRelTmp.getCreateTime();//创建时间不能被覆盖掉
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

        //如果车辆类型不等于自有车
        if (null != vehicleClassTmp && vehicleClassTmp.intValue() == VEHICLE_CLASS1 && null != vehicleClass && vehicleClass.intValue() != VEHICLE_CLASS1) {
            vehicleDataInfoTmp.setTenantId(null);
        }

        //复制车辆主表数据
        if (vehicleDataInfoVer != null) {
            BeanUtil.copyProperties(vehicleDataInfoVer, vehicleDataInfoTmp);
            vehicleDataInfoTmp.setAuthState(SysStaticDataEnum.AUTH_STATE.AUTH_STATE2);
            vehicleDataInfoTmp.setIsAuth(IS_AUTH0);
            vehicleDataInfoTmp.setId(vehicleDataInfoVer.getVehicleCode());
            vehicleDataInfoTmp.setAdriverLicenseCopy(vehicleDataInfoVer.getAdrivingLicenseCopy());
            vehicleDataInfoVer.setIsAuthSucc(IS_AUTH1);
            saveOrUpdate(vehicleDataInfoTmp);
            iVehicleDataInfoVerService.saveOrUpdate(vehicleDataInfoVer);

            //判断司机是否在本系统存在，如果不存在就添加为外调司机
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


        //复制心愿线路表
        QueryWrapper<VehicleObjectLineVer> queryWrapper1 = new QueryWrapper<>();
        queryWrapper1.eq("vehicle_code", vehicleCode).eq("ver_state", VER_STATE_Y).orderByDesc("id");
        List<VehicleObjectLineVer> vehicleObjectLineVerList = vehicleObjectLineVerMapper.selectList(queryWrapper1);
        if (null != vehicleObjectLineVerList && vehicleObjectLineVerList.size() > 0) {
            for (int i = 0; i < vehicleObjectLineVerList.size(); i++) {
                VehicleObjectLineVer vehicleObjectLineVer = vehicleObjectLineVerList.get(i);
                if (vehicleObjectLineVer.getId() != null && vehicleObjectLineVer.getId() > 0) {
                    VehicleObjectLine vehicleObjectLine = vehicleObjectLineMapper.selectById(vehicleObjectLineVer.getHisId());
                    if (vehicleObjectLine != null) {
                        // id 不进行浅拷贝
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

            //绑定线路
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

            //复制成本信息
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

        //更新车辆完整性信息
        doUpdateVehicleCompleteness(tenantVehicleRelTmp.getVehicleCode(), loginInfo.getTenantId());

        //增加平台车记录
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
        String vehicleObjectLineVerHisIds = DataFormat.getStringKey(inParam, "vehicleObjectLineVerHisIds");//心愿线路ID
        String vehicleLineRelVerHisIds = DataFormat.getStringKey(inParam, "vehicleLineRelVerHisIds");//绑定线路ID
        long tenantVehicleRelVerHisId = DataFormat.getLongKey(inParam, "tenantVehicleRelVerHisId");//租户车辆引用关系
        long tenantVehicleCostRelVerHisId = DataFormat.getLongKey(inParam, "tenantVehicleCostRelVerHisId");
        long tenantVehicleCertRelVerHisId = DataFormat.getLongKey(inParam, "tenantVehicleCertRelVerHisId");


        if (vehicleDataInfoVerHisId > 0) {
            VehicleDataInfoVer vehicleDataInfoVer = iVehicleDataInfoVerService.getById(vehicleDataInfoVerHisId);
            if (vehicleDataInfoVer == null || vehicleDataInfoVer.getVerState().equals(VER_STATE_N)) {
                throw new BusinessException("该数据其他人已经修改，请重新修改后再审核！");
            }
            vehicleDataInfoVer.setIsAuthSucc(isAuthSucc);
            iVehicleDataInfoVerService.updateById(vehicleDataInfoVer);
        }

        if (tenantVehicleRelVerHisId > 0) {
            TenantVehicleRelVer tenantVehicleRelVer = iTenantVehicleRelVerService.getById(tenantVehicleRelVerHisId);
            if (tenantVehicleRelVer == null || tenantVehicleRelVer.getVerState().equals(VER_STATE_N)) {
                throw new BusinessException("该数据其他人已经修改，请重新修改后再审核！");
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
        //启动司机审核
        boolean boolD = iAuditService.startProcess(AuditConsts.AUDIT_CODE.AUDIT_CODE_USER, tenantUserRel.getId(), SysOperLogConst.BusiCode.Driver, paramMap, token);
        if (!boolD) {
            throw new BusinessException("启动审核流程失败！");
        }
    }


    public void removeVehicleAllRelTenant(long vehicleCode, boolean isDelOwnType, String token) throws BusinessException {
        if (vehicleCode < 0) {
            throw new BusinessException("参数错误");
        }
        QueryWrapper<TenantVehicleRel> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("vehicle_code", vehicleCode);
        List<TenantVehicleRel> list = iTenantVehicleRelService.list(queryWrapper);

        if (null != list && list.size() > 0) {

            //先创建一份主表的历史记录信息
            VehicleDataInfoVer vehicleDataInfoVer = iVehicleDataInfoVerService.doAddVehicleDataInfoVerHis(vehicleCode);
            for (int i = 0; i < list.size(); i++) {
                TenantVehicleRel t = list.get(i);
                if (t.getVehicleClass() == VEHICLE_CLASS1 && !isDelOwnType) {
                    continue;
                }
				/*
				if (null != t.getDriverUserId() && t.getDriverUserId() > 0 && t.getVehicleClass() > VEHICLE_CLASS1) {//非自有车删除车量才删除司机，自有车删除车量不删除司机
					//将原来车队的司机移到历史档案
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
            throw new BusinessException("参数错误，手机号为空!");
        }

        if (flag < 0) {
            throw new BusinessException("flag参数错误!");
        }
        UserDataInfoBackVo userDataInfoBackVo = new UserDataInfoBackVo();
        userDataInfoBackVo.setCode("T");
        userDataInfoBackVo.setMsg("");
        if (StrUtil.isBlank(mobile)) {
            userDataInfoBackVo.setCode("F");
            userDataInfoBackVo.setMsg("参数错误，手机号为空!");
        }

        if (flag < 0) {
            userDataInfoBackVo.setCode("F");
            userDataInfoBackVo.setMsg("flag参数错误!");
        }

        UserDataInfo userDataInfo = userDataInfoRecordMapper.queryUserInfoByMobile(mobile);
        if (userDataInfo != null) {

            userDataInfoBackVo.setUserId(userDataInfo.getId());
            userDataInfoBackVo.setLinkman(userDataInfo.getLinkman());
            SysTenantDef sysTenantDef = sysTenantDefService.getSysTenantDefByAdminUserId(userDataInfo.getId());
            if (sysTenantDef != null) {
                if (loginInfo.getTenantId().equals(sysTenantDef.getId())) {
                    throw new BusinessException("账单接收人不能是本车队的超管");
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
        String VEHICLE_CLASS1 = SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1 + "";//招商挂靠车
        String VEHICLE_CLASS2 = SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS2 + "";//招商挂靠车
        String VEHICLE_CLASS4 = SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS4 + "";//挂靠车
        String VEHICLE_CLASS3 = SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS5 + "";//临时外调车
        String tmpMsg = "";
        Map param = new HashMap();
        boolean isModify = false;

        String plateNumber = ExcelUtils.getExcelValue(l, 0);
        if (org.apache.commons.lang.StringUtils.isBlank(plateNumber)) {
            tmpMsg += "车牌号码未输入，";
        } else {
            Map<String, Object> rtnMap = baseMapper.getVehicleIsRegister(SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1, plateNumber);
            //为了不影响原有的功能，需要拆分一个修改的数据逻辑，全部的修改逻辑走这块代码。
            //1.需要先调用车辆的查询功能
            //2.构造修改的map
            //3.判断excel导入的数据对不对，覆盖原来的数据
            //4.调用原来的修改逻辑，param里面放入一个特殊参数isModify表示该条数据是修改
            long vehicleCode = DataFormat.getLongKey(rtnMap, "vehicleCode");
            VehicleInfoDto vehicleInfoV = getAllVehicleInfo(SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1, vehicleCode, null, token);
            if (vehicleInfoV != null) {

                Map<String, Object> vehicleInfo = JsonHelper.parseJSON2Map(JsonHelper.toJson(vehicleInfoV));
                // JSONObject.parseObject(JSONObject.toJSONString(vehicleInfoVo),Map.class);
                //            long relId = DataFormat.getLongKey(param, "relId");//车辆和租户
                param.putAll(vehicleInfo);
            }
            long relId = DataFormat.getLongKey(param, "relId");//车辆和租户
            long tenantVehicleCostRelId = DataFormat.getLongKey(param, "tenantVehicleCostRelId");//租户车辆成本关系表
            long tenantVehicleCertRelId = DataFormat.getLongKey(param, "tenantVehicleCertRelId");//证照id
            param.put("tenantVehicleRel.id", relId);
            param.put("tenantVehicleCostRel.id", tenantVehicleCostRelId);
            param.put("tenantVehicleCertRel.id", tenantVehicleCertRelId);
            param.put("isModify", true);

            String vehicleClassStr = DataFormat.getStringKey(param, "vehicleClass");
            String vehicleClassName = ExcelUtils.getExcelValue(l, 1);

            if (org.apache.commons.lang.StringUtils.isBlank(vehicleClassName)) {
                tmpMsg += "车辆种类不能为空";
            } else {
                String vehicleClass = getCodeValue("VEHICLE_CLASS", vehicleClassName);
                if (org.apache.commons.lang.StringUtils.isBlank(vehicleClass)) {
                    tmpMsg += "车辆种类填写错误";
                } else if (org.apache.commons.lang.StringUtils.isNotBlank(vehicleClassStr) && !vehicleClass.equals(vehicleClassStr)) {
                    tmpMsg += "车辆种类不能修改";
                }
            }

            String licenceTypeName = ExcelUtils.getExcelValue(l, 2);
            String licenceType = null;
            if (StrUtil.isNotBlank(licenceTypeName)) {
                isModify = true;
                licenceType = getCodeValue("LICENCE_TYPE", licenceTypeName);
                if (StrUtil.isBlank(licenceType)) {
                    tmpMsg += "牌照类型不正确，";
                }
                param.put("licenceType", licenceType);
            } else {
                licenceType = DataFormat.getStringKey(param, "licenceType");
            }

            if ("1".equals(licenceType)) {
                //整车的处理
                String vehicleStatusName = ExcelUtils.getExcelValue(l, 3);

                String vehicleStatusNameOld = DataFormat.getStringKey(param, "vehicleStatusName");
                String vehicleStatus = "";
                if (org.apache.commons.lang.StringUtils.isBlank(vehicleStatusName) && org.apache.commons.lang.StringUtils.isBlank(vehicleStatusNameOld)) {
                    tmpMsg += "车型未输入，";
                } else if (org.apache.commons.lang.StringUtils.isNotBlank(vehicleStatusName)) {
                    vehicleStatus = getCodeValue("VEHICLE_STATUS", vehicleStatusName);
                    if (org.apache.commons.lang.StringUtils.isBlank(vehicleStatus)) {
                        tmpMsg += "车型不正确，";
                    } else {
                        isModify = true;
                        param.put("vehicleStatus", vehicleStatus);
                    }
                }
                String vehicleLengthName = ExcelUtils.getExcelValue(l, 4);

                String vehicleLengthNameOld = DataFormat.getStringKey(param, "vehicleLengthName");
                if (StrUtil.isBlank(vehicleLengthName) && StrUtil.isBlank(vehicleLengthNameOld)) {
                    tmpMsg += "车长未输入，";
                } else if (StrUtil.isNotBlank(vehicleLengthName)) {
                    String vehicleLength = "";
                    if ((SysStaticDataEnum.VEHICLE_STATUS_ENUM.VEHICLE_CAR_BOX.getType() + "").equals(vehicleStatus)) {
                        vehicleLength = getCodeValue("VEHICLE_STATUS_SUBTYPE", vehicleLengthName);
                    } else {
                        vehicleLength = getCodeValue("VEHICLE_LENGTH", vehicleLengthName);
                    }

                    if (StrUtil.isBlank(vehicleLength)) {
                        tmpMsg += "车长不正确，";
                    } else {
                        isModify = true;
                        param.put("vehicleLength", vehicleLength);
                    }
                }
                String vehicleLoad = ExcelUtils.getExcelValue(l, 5);

                String vehicleLoadOld = DataFormat.getStringKey(param, "vehicleLoad");

                if (org.apache.commons.lang.StringUtils.isBlank(vehicleLoad) && org.apache.commons.lang.StringUtils.isBlank(vehicleLoadOld)) {
                    tmpMsg += "载重(吨)未输入，";
                }

                if (org.apache.commons.lang.StringUtils.isNotBlank(vehicleLoad)) {
                    BigDecimal vehicleLoadBig = new BigDecimal(vehicleLoad);
                    if (vehicleLoadBig.compareTo(new BigDecimal(0)) < 0) {
                        tmpMsg += "载重(吨)不能为负数，";
                    } else {
                        isModify = true;
                        param.put("vehicleLoad", vehicleLoad);
                    }
                }

                String lightGoodsSquare = ExcelUtils.getExcelValue(l, 6);

                String lightGoodsSquareOld = DataFormat.getStringKey(param, "lightGoodsSquare");

                if (org.apache.commons.lang.StringUtils.isBlank(lightGoodsSquare) && org.apache.commons.lang.StringUtils.isBlank(lightGoodsSquareOld)) {
                    tmpMsg += "容积(立方米)未输入，";
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
                param.put("isUseCarOilCost", "是".equals(isUseCarOilCost) ? 1 : 0);
            }

            String driverUserPhone = ExcelUtils.getExcelValue(l, 16);
            String driverUserName = "";
            if (org.apache.commons.lang.StringUtils.isNotBlank(driverUserPhone)) {
                //查询号码
                QueryWrapper<UserDataInfo> userDataInfoQueryWrapper = new QueryWrapper<>();
                userDataInfoQueryWrapper.eq("mobile_phone", driverUserPhone);
                List<UserDataInfo> userDataInfoList = iUserDataInfoRecordService.list(userDataInfoQueryWrapper);
                UserDataInfo userInfo = null;
                if (userDataInfoList != null && userDataInfoList.size() > 0) {
                    userInfo = userDataInfoList.get(0);
                }
                if (userInfo == null) {
                    tmpMsg += "归属司机手机号不存在";
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
                        tmpMsg += "归属司机只能是本车队司机";
                    } else {
                        isModify = true;
                        param.put("driverUserId", userInfo.getId());
                        driverUserName = userInfo.getLinkman();
                    }
                }
            } else {
                driverUserPhone = DataFormat.getStringKey(param, "driverMobilePhone");
            }

            //处理账单接收人
            if (VEHICLE_CLASS2.equals(vehicleClassStr) || VEHICLE_CLASS4.equals(vehicleClassStr)) {

                String billReceiverMobile = ExcelUtils.getExcelValue(l, 38);//账单接收人手机号
                String billReceiverName = ExcelUtils.getExcelValue(l, 39);//账单接收人姓名


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
                    if (VEHICLE_CLASS2.equals(vehicleClassStr)) {//校验招商车的账单接收人
                        userDataInfoBackVo = queryBillReceiverName(billReceiverMobile, 1, token);
                    } else if (VEHICLE_CLASS4.equals(vehicleClassStr)) {//校验挂靠车的账单接收人
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

            //自有车才有的属性处理
            if (org.apache.commons.lang.StringUtils.isNotBlank(vehicleClassStr) && (VEHICLE_CLASS1.equals(vehicleClassStr) || VEHICLE_CLASS2.equals(vehicleClassStr) || VEHICLE_CLASS4.equals(vehicleClassStr))) {
                if (VEHICLE_CLASS1.equals(vehicleClassStr)) {

                    String copilotDriverPhone = ExcelUtils.getExcelValue(l, 17);
                    if (org.apache.commons.lang.StringUtils.isNotBlank(driverUserPhone) && org.apache.commons.lang.StringUtils.isNotBlank(copilotDriverPhone) && driverUserPhone.equals(copilotDriverPhone)) {
                        tmpMsg += "主驾和副驾不能是相同的人，";
                    } else if (org.apache.commons.lang.StringUtils.isNotBlank(copilotDriverPhone)) {
                        //查询用户是否存在
                        UserDataInfo userInfo = iUserDataInfoRecordService.getUserDataInfoByMoblile(copilotDriverPhone, false, token);
                        if (userInfo == null) {
                            tmpMsg += "副驾手机号不存在";
                        } else {
                            QueryWrapper<TenantUserRel> tenantUserRelQueryWrapper = new QueryWrapper<>();
                            tenantUserRelQueryWrapper.eq("user_id", userInfo.getId());
                            List<TenantUserRel> tenantUserRelList = iTenantUserRelService.list(tenantUserRelQueryWrapper);
                            if (tenantUserRelList != null && tenantUserRelList.size() > 0) {
                                tmpMsg += "副驾只能是本车队司机或平台司机，";
                            } else {
                                isModify = true;
                                param.put("copilotDriverId", userInfo.getId());
                            }
                        }

                    }

                    String followDriverPhone = ExcelUtils.getExcelValue(l, 18);
                    if (org.apache.commons.lang.StringUtils.isNotBlank(followDriverPhone)) {
                        //查询号码
                        UserDataInfo userInfo = iUserDataInfoRecordService.getUserDataInfoByMoblile(followDriverPhone, false, token);
                        if (userInfo == null) {
                            tmpMsg += "随车司机手机号不存在，";
                        } else {
                            QueryWrapper<TenantUserRel> tenantUserRelQueryWrapper = new QueryWrapper<>();
                            tenantUserRelQueryWrapper.eq("user_id", userInfo.getId());
                            List<TenantUserRel> tenantUserRelList = iTenantUserRelService.list(tenantUserRelQueryWrapper);
                            if (tenantUserRelList != null && tenantUserRelList.size() > 0) {
                                tmpMsg += "随车司机只能是本车队司机或平台司机";
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
                            tmpMsg += "归属部门不正确，";
                        } else {
                            isModify = true;
                        }
                        param.put("orgId", orgId);
                    } else {
                        //获取隐藏的根组织（parentOrgId = -1）
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
                            tmpMsg += "归属人手机不正确，";
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
                                    tmpMsg += "线路绑定不正确";
                                }
                            }
                        }
                        StringBuffer lineCodeRulesSb = new StringBuffer();
                        for (String s : lineCodeRuleList) {
                            lineCodeRulesSb.append("'").append(s).append("',");
                        }
                        List<CmCustomerLine> lines = cmCustomerLineMapper.getCmCustomerLineByLineCodeRules(lineCodeRulesSb.substring(0, lineCodeRules.length() - 1));
                        if (lines == null || lines.isEmpty()) {
                            tmpMsg += "线路绑定不正确，";
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
                                    tmpMsg += "线路绑定中" + lineMsg.substring(0, lineMsg.length() - 1) + "不正确，";
                                }
                            }
                        }
                    }

                    String shareFlg = ExcelUtils.getExcelValue(l, 22);
                    if (org.apache.commons.lang.StringUtils.isNotBlank(shareFlg)) {
                        isModify = true;
                        param.put("shareFlg", "是".equals(shareFlg) ? 1 : 0);
                    }
                    if ("是".equals(shareFlg)) {
                        String linkUserPhone = ExcelUtils.getExcelValue(l, 23);
                        String shareMobilePhone = DataFormat.getStringKey(param, "shareMobilePhone");
                        if (StrUtil.isBlank(linkUserPhone) && org.apache.commons.lang.StringUtils.isBlank(shareMobilePhone)) {
                            tmpMsg += "共享联系电话未输入，";
                        } else if (org.apache.commons.lang.StringUtils.isNotBlank(linkUserPhone)) {

                            List<BackUserDto> pagination = customerInfoMapper.doQueryBackUserListPhone(loginInfo.getTenantId(), linkUserPhone);
                            if (pagination == null || pagination.size() == 0) {
                                tmpMsg += "共享联系电话不正确，";
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

                tmpMsg += ExcelUtils.checkDateTypeValue(l, 40, param, "annualVeriTime", "上次年审时间", isModify);
                tmpMsg += ExcelUtils.checkDateTypeValue(l, 41, param, "annualVeriTimeEnd", "年审到期时间", isModify);
                tmpMsg += ExcelUtils.checkDateTypeValue(l, 42, param, "seasonalVeriTime", "上次季审时间", isModify);
                tmpMsg += ExcelUtils.checkDateTypeValue(l, 43, param, "seasonalVeriTimeEnd", "季审到期时间", isModify);
                tmpMsg += ExcelUtils.checkDateTypeValue(l, 44, param, "busiInsuranceTime", "上次商业险时间", isModify);
                tmpMsg += ExcelUtils.checkDateTypeValue(l, 45, param, "busiInsuranceTimeEnd", "商业险到期时间", isModify);
                isModify = ExcelUtils.modifyValue(l, 46, param, "busiInsuranceCode") || isModify;//商业险单号
                tmpMsg += ExcelUtils.checkDateTypeValue(l, 47, param, "insuranceTime", "上次交强险时间", isModify);
                tmpMsg += ExcelUtils.checkDateTypeValue(l, 48, param, "insuranceTimeEnd", "交强险到期时间", isModify);
                isModify = ExcelUtils.modifyValue(l, 49, param, "insuranceCode") || isModify;//商业险单号
                tmpMsg += ExcelUtils.checkDateTypeValue(l, 50, param, "otherInsuranceTime", "上次其他险时间", isModify);
                tmpMsg += ExcelUtils.checkDateTypeValue(l, 51, param, "otherInsuranceTimeEnd", "其他险到期时间", isModify);
                isModify = ExcelUtils.modifyValue(l, 52, param, "otherInsuranceCode") || isModify;//其他险单号
                isModify = ExcelUtils.modifyValue(l, 53, param, "maintainDis") || isModify;
                isModify = ExcelUtils.modifyValue(l, 54, param, "maintainWarnDis") || isModify;
                tmpMsg += ExcelUtils.checkDateTypeValue(l, 55, param, "prevMaintainTime", "上次保养时间", isModify);
                tmpMsg += ExcelUtils.checkDateTypeValue(l, 56, param, "registrationTime", "登记日期", isModify);
                isModify = ExcelUtils.modifyValue(l, 57, param, "registrationNumble") || isModify;//登记证号
                tmpMsg += ExcelUtils.checkDateTypeValue(l, 58, param, "vehicleValidityTimeBegin", "行驶证生效时间", isModify);
                tmpMsg += ExcelUtils.checkDateTypeValue(l, 59, param, "vehicleValidityTime", "行驶证失效时间", isModify);
                tmpMsg += ExcelUtils.checkDateTypeValue(l, 60, param, "operateValidityTimeBegin", "营运证生效时间", isModify);
                tmpMsg += ExcelUtils.checkDateTypeValue(l, 61, param, "operateValidityTime", "营运证失效时间", isModify);
            }
        }

        //开始处理修改数据，需要判断是否全部没有修改
        if (org.apache.commons.lang.StringUtils.isBlank(tmpMsg) && !isModify) {
            tmpMsg += "没有修改任何数据，";
        }

        param.put("tmpMsg", tmpMsg);
        return param;
    }


    //**********************************  运营端 ***********************************//

    @Override
    @Transactional
    public boolean save(OBMSVehicleDataVo obmsVehicleDataVo, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        boolean ret = isInCommonLine(obmsVehicleDataVo.getVehicleOjbectLineArray());
        if (ret) {
            throw new BusinessException("心愿线路不能设置相同!");
        }
//        if (vehicleClass < 0) {
//            vehicleClass = 1;//因为原来方法里写死是查vehicleClass=1的，我加了一个vehicleClass参数，所以如果没有值就为1
//        }
        int vehicleClass = 1;
        //保存数据安全性，再次校验车辆是否注册
//        Map rtnMap = vehicleTF.getVehicleIsRegisterForOBMS(plateNumber,vehicleClass);
//        Map rtnMap = vehicleDataInfoMapper.getVehicleIsRegisterForOBMS(obmsVehicleDataVo.getPlateNumber(),vehicleClass);
        Map rtnMap = vehicleDataInfoMapper.getVehicleIsRegister(vehicleClass, obmsVehicleDataVo.getPlateNumber());
        if (null != rtnMap) {
            throw new BusinessException("车辆已被注册！");
        }

        //车辆主表
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
        if (attachIsAdminUser == 1 && attachTenantId > 0) {//被挂靠人为超管,给这个车队增加一条挂靠车关系
            //董生说挂靠不做

       //运营后台修改数据,没有车队不需要审核
            Param param = new Param("plateNumber", plateNumber);
            param.addParam("vehicleClass", SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS4);
            List exitTenantVehicleRels = vehicleOBMSSV.getObjects(TenantVehicleRel.class, param, false);//查询这个车辆自有车信息
            TenantVehicleRel oldTenantVehicleRel = null;
            if (exitTenantVehicleRels != null && exitTenantVehicleRels.size() > 0) {
                oldTenantVehicleRel = (TenantVehicleRel) exitTenantVehicleRels.get(0);
            }

            if (oldTenantVehicleRel == null || oldTenantVehicleRel.getTenantId() != attachTenantId) {
                TenantVehicleRel vehicleRel = vehicleTF.getTenantVehicleRel(vehicleDataInfo.getVehicleCode(), attachTenantId);
                if (vehicleRel == null) {
                    vehicleTF.addAttachVehicleAndDriver(vehicleDataInfo, attachTenantId, 3);//添加或转移挂靠司机与车
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
        //心愿线路表
        List<VehicleObjectLineVo> vehicleObjectLines = obmsVehicleDataVo.getVehicleOjbectLineArray();
        VehicleObjectLineVer vehicleObjectLineVer = null;
        List vehicleObjectLineIds = new ArrayList();
        if (null != vehicleObjectLines) {
            for (int i = 0; i < vehicleObjectLines.size(); i++) {

                //先判断车辆有多少心愿线路，有3条了则不再增加
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
        //更新车量完整性
        doUpdateVehicleCompleteness(vehicleDataInfo.getId(), loginInfo.getTenantId());

        saveSysOperLog(SysOperLogConst.BusiCode.Vehicle, vehicleDataInfo.getId(), SysOperLogConst.OperType.Add, "车辆" + vehicleDataInfo.getPlateNumber() + "档案新增", loginInfo);
//        sysOperLogTF.saveSysOperLog(SysOperLogConst.BusiCode.Vehicle.getCode(), vehicleDataInfo.getVehicleCode(), SysOperLogConst.OperType.Add.getCode(), "车辆"+vehicleDataInfo.getPlateNumber()+"档案新增");

        boolean isAutoAudit = sysCfgService.getCfgBooleanVal("IS_AUTO_AUDIT", -1);
        if (isAutoAudit) {
            vehicleDataInfo.setAuthState(AUTH_STATE2);
            vehicleDataInfo.setIsAuth(IS_AUTH0);
            baseMapper.updateById(vehicleDataInfo);
            vehicleDataInfoVer.setVerState(VER_STATE_Y);
            vehicleDataInfoVerMapper.insert(vehicleDataInfoVer);
            //添加平台车记录
            addPtTenantVehicleRelWithDriver(vehicleDataInfo.getId(), vehicleDataInfoVer.getDriverUserId());
            //写日志
            saveSysOperLog(SysOperLogConst.BusiCode.Vehicle, vehicleDataInfo.getId(), SysOperLogConst.OperType.Audit, "车辆" + vehicleDataInfo.getPlateNumber() + "自动审核", loginInfo);
        }
        return true;
    }

    /**
     * 心愿线路检查线路是否相同
     *
     * @return
     */
    private boolean isInCommonLine(List<VehicleObjectLineVo> lines) {
        //心愿线路表,检查
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
                //检查一条心愿线路是否完整
                if ((sourceProvince != null && (desProvince == null || (carriagePrice == null)))
                        || ((sourceProvince == null || (carriagePrice == null)) && desProvince != null)
                        || ((sourceProvince == null || desProvince == null) && (carriagePrice != null))
                ) {
                    throw new BusinessException("心愿线路填写不完整，一条心愿线路需要填写完整才能保存!");
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
                        //分情况判断
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
        //车辆不存在，不创建
        VehicleDataInfo vehicleDataInfo = baseMapper.selectById(vehicleCode);
        if (vehicleDataInfo == null) {
            return;
        }
        //有自有属性，不创建

        TenantVehicleRel zyVehicleRel = iTenantVehicleRelService.getZYVehicleByVehicleCode(vehicleCode);
        if (zyVehicleRel != null && org.apache.commons.lang.StringUtils.isNotBlank(zyVehicleRel.getPlateNumber())) {
            return;
        }
        //已经有C端记录，不创建
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
            throw new BusinessException("车辆编码错误！");
        }
        VehicleDataInfo vehicleDataInfo = get(vehicleCode);
//        VehicleDataInfo vehicleDataInfo = vehicleOBMSSV.getObjectById(VehicleDataInfo.class, vehicleCode);
        if (null == vehicleDataInfo) {
            throw new BusinessException("车辆不存在！");
        }

        //查询是否是某个车队的自有车，是自有车需要审核
//        TenantVehicleRel exitTenantVehicleRel = vehicleTF.getZYVehicleByVehicleCode(vehicleCode);
        List<TenantVehicleRel> zyVehicleRel = tenantVehicleRelMapper.getZYVehicleByVehicleCode(vehicleDataInfo.getId(), VEHICLE_CLASS1);
        TenantVehicleRel exitTenantVehicleRel = null;
        if (zyVehicleRel.size() > 0) {
            exitTenantVehicleRel = zyVehicleRel.get(0);
        }
        boolean unNeedCheck = true;//车辆是某个车队的自有车需要车队审核，否则直接生效
        if (exitTenantVehicleRel != null && exitTenantVehicleRel.getAuthState() != null && SysStaticDataEnum.AUTH_STATE.AUTH_STATE2 == exitTenantVehicleRel.getAuthState()) {
            unNeedCheck = false;
        }


        //vehicle_data_info_ver表的所有ver_state为1的记录更新为0
//        vehicleTF.updateVehicleVerAllVerState("vehicle_data_info_ver", inMap);
        updateVehicleVerAllVerState("vehicle_data_info_ver", obmsVehicleDataVo.getVehicleCode(), vehicleDataInfo.getTenantId(), VER_STATE_N);


        VehicleDataInfoVer vehicleDataInfoVer = new VehicleDataInfoVer();
//        BeanUtil.copyProperties(vehicleDataInfo, vehicleDataInfoVer);
//        BeanUtil.copyProperties(obmsVehicleDataVo,vehicleDataInfoVer);
//        vehicleDataInfoVer.setVerState(VER_STATE_N);
//        vehicleDataInfoVerMapper.insert(vehicleDataInfoVer);
        //车辆主表
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
        vehicleDataInfo.setAdriverLicenseCopy(vehicleDataInfoVer.getAdrivingLicenseCopy());//行驶证副本(图片保存编号)
        //如果司机修改为自有车司机也是需要审核

        boolean flag = false;
        if (vehicleDataInfo.getDriverUserId() == null) {
            if (vehicleDataInfoVer.getDriverUserId() != null) {
                //修改了
                flag = true;
            }
        } else {
            if (!vehicleDataInfo.getDriverUserId().equals(vehicleDataInfoVer.getDriverUserId())) {
                //修改了
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
                || !vehicleDataInfo.getDriverUserId().equals(vehicleDataInfoVer.getDriverUserId())) {//修改了司机
//            TenantUserRel

        }*/

        if (vehicleDataInfoVer.getLicenceType() == SysStaticDataEnum.LICENCE_TYPE.TT) {
            vehicleDataInfoVer.setVehicleStatus(null);
            vehicleDataInfoVer.setVehicleLength(null);
            vehicleDataInfoVer.setVehicleLoad(null);
            vehicleDataInfoVer.setLightGoodsSquare(null);
        }

        if (unNeedCheck) {//无自有车属性，直接修改通过，无需审核

            //判断是否修改司机
//            if (vehicleDataInfo.getDriverUserId().equals(vehicleDataInfoVer.getDriverUserId())) {//没有修改司机
//                BeanUtil.copyProperties(obmsVehicleDataVo, vehicleDataInfo);
//                vehicleDataInfo.setIsAuth(IS_AUTH0);
//                vehicleDataInfo.setAuthState(SysStaticDataEnum.AUTH_STATE.AUTH_STATE2);
//
//            } else {//修改了司机,把该车在其他车队的类型修改成与司机在其他车队匹配的类型
//                addDriverToSocialCarByTenantId(vehicleDataInfo.getId(), vehicleDataInfoVer.getDriverUserId(), false);
//            }
            //判断是否修改司机  2022-5-11 新需求 运营端不能修改司机信息
            if (vehicleDataInfo.getDriverUserId() == null || vehicleDataInfoVer.getDriverUserId() == null) {//没有修改司机
                BeanUtil.copyProperties(obmsVehicleDataVo, vehicleDataInfo);
                vehicleDataInfo.setAdriverLicenseCopy(obmsVehicleDataVo.getAdriverLicenseCopy());
                vehicleDataInfo.setAdriverLicenseCopyUrl(obmsVehicleDataVo.getAdriverLicenseCopyUrl());
                vehicleDataInfo.setIsAuth(IS_AUTH0);
                vehicleDataInfo.setAuthState(SysStaticDataEnum.AUTH_STATE.AUTH_STATE2);

            } else {//修改了司机,把该车在其他车队的类型修改成与司机在其他车队匹配的类型
                addDriverToSocialCarByTenantId(vehicleDataInfo.getId(), vehicleDataInfoVer.getDriverUserId(), false);
            }
            vehicleDataInfoVer.setIsAuthSucc(3);//自动审核通过
            BeanUtil.copyProperties(vehicleDataInfoVer, vehicleDataInfo);
            vehicleDataInfo.setAdriverLicenseCopy(vehicleDataInfoVer.getAdrivingLicenseCopy());//行驶证副本(图片保存编号)
            vehicleDataInfo.setIsAuth(IS_AUTH0);

   /*         if (attachIsAdminUser == 1 && attachTenantId > 0) {//被挂靠人为超管,给这个车队增加一条挂靠车关系
                TenantVehicleRel vehicleRel = vehicleTF.getTenantVehicleRel(vehicleDataInfo.getVehicleCode(), attachTenantId);
                if (vehicleRel == null) {
                    vehicleTF.addAttachVehicleAndDriver(vehicleDataInfo, attachTenantId, 3);//添加或转移挂靠司机与车
                }
            }*/

            //更新车量完整性
//            vehicleTF.doUpdateVehicleCompleteness(vehicleDataInfo.getVehicleCode());
            doUpdateVehicleCompleteness(vehicleDataInfo.getId(), vehicleDataInfo.getTenantId());
        }

        baseMapper.updateById(vehicleDataInfo);
        vehicleDataInfoVerMapper.updateById(vehicleDataInfoVer);

//处理心愿线路
        //vehicle_object_line_ver表的所有ver_state为1的记录更新为0
        this.updateVehicleVerAllVerState("vehicle_object_line_ver", obmsVehicleDataVo.getVehicleCode(),
                vehicleDataInfo.getTenantId(), VER_STATE_N);

        //重新生成新的vehicle_object_line_ver表记录
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


        saveSysOperLog(SysOperLogConst.BusiCode.Vehicle, vehicleDataInfo.getId(), SysOperLogConst.OperType.Update, "车辆" + vehicleDataInfo.getPlateNumber() + "档案修改", accessToken);

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
//                //web端保存心愿线路要乘以100，APP不需要
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

            if (oldVehicleObjectLine != null) {//修改原有记录
                // id 不进行浅拷贝
                oldVehicleObjectLine.setCarriagePrice(carriagePrice != null ? carriagePrice : null); // 传来的 价格
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
                    //无自有车属性，不需要审核，修改直接生效

                    oldVehicleObjectLine.setOpId(loginUtils.get(accessToken).getUserInfoId());
                    oldVehicleObjectLine.setUpdateTime(LocalDateTime.now());
                    vehicleObjectLineMapper.updateById(oldVehicleObjectLine);
                    //自动审核通过
//                    vehicleObjectLineVer.setIsAuthSucc(3);
                }
                oldVehicleObjectLine.setCreateTime(LocalDateTime.now());
                //最后设置，避免复制属性值时覆盖掉原来记录的时间
//                vehicleObjectLineVerMapper.insert(vehicleObjectLineVer);
//                iVehicleObjectLineVerService.saveOrUpdate(oldVehicleObjectLine);
                iVehicleObjectLineService.saveOrUpdate(oldVehicleObjectLine);

            } else {//新增记录

                //先判断车辆有多少心愿线路，有3条了则不再增加
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

                    newVehicleObjectLine.setCarriagePrice(carriagePrice != null ? carriagePrice : null); // 传来的 价格
                    if (unNeedCheck) {//无自有车属性，不需要审核，修改直接生效
//                        vehicleObjectLineVer.setIsAuthSucc(3);//自动审核通过
                        vehicleObjectLineMapper.updateById(newVehicleObjectLine);
                    }
                    iVehicleObjectLineService.saveOrUpdate(oldVehicleObjectLine);
                }
            }
        }
    }


    /**
     * 处理车量与司机不匹配问题
     *
     * @param vehicleCode  车辆编号
     * @param driverUserId 用户编号
     * @param isDealOwnCar 是否要处理自有车关系 true-要，false-不要
     * @return
     * @throws Exception
     */
    private void addDriverToSocialCarByTenantId(Long vehicleCode, Long driverUserId, boolean isDealOwnCar) {
        if (vehicleCode == null || vehicleCode < 0) {
            throw new BusinessException("车辆ID为空！");
        }
        //更新司机信息
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
            throw new BusinessException("destVerState参数错误！");
        }
        //车辆主表
        if ("vehicle_data_info_ver".equals(tableName) || "all".equals(tableName)) {
            vehicleDataInfoVerMapper.updVehicleDataInfoVerState(destVerState, vehicleCode);
        }
        //心愿线路表
        if ("vehicle_object_line_ver".equals(tableName) || "all".equals(tableName)) {
            vehicleDataInfoVerMapper.updVehicleObjectLineVerState(destVerState, vehicleCode);
        }
        //绑定线路
        if ("vehicle_line_rel_ver".equals(tableName) || "all".equals(tableName)) {
            vehicleDataInfoVerMapper.updVehicleLineRelVerState(destVerState, vehicleCode);
        }
        //车辆租户关系表
        if ("tenant_vehicle_rel_ver".equals(tableName) || "all".equals(tableName)) {
            vehicleDataInfoVerMapper.updTenantVehicleRelVerState(destVerState, vehicleCode, tenantId);
        }
        //车辆成本表
        if ("tenant_vehicle_cost_rel_ver".equals(tableName) || "all".equals(tableName)) {
            vehicleDataInfoVerMapper.updTenantVehicleCostRelVerState(destVerState, vehicleCode, tenantId);
        }
        //证照信息表
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

        //查询车辆的基础信息
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
            tenantVehicleInfoOBMS.setShareFlgName("否");
        } else {
            tenantVehicleInfoOBMS.setShareFlgName("是");
        }

        if (tenantVehicleInfoOBMS.getAuthState() == SysStaticDataEnum.AUTH_STATE.AUTH_STATE2) {
            tenantVehicleInfoOBMS.setAuthStateName("已认证");
        } else {
            tenantVehicleInfoOBMS.setAuthStateName("未认证");
        }

        //查询司机信息
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
            throw new BusinessException("车辆编码错误！");
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
                throw new BusinessException("车牌号码[" + platNumber + "]对应的G7的设备号为空，但是同步的类型为G7");
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
            //记录车辆当前位置信息(key:VGP_随车手机,value:纬度|经度|时间)
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
            throw new BusinessException("业务主键数据错误！");
        }
        ApplyRecord applyRecord = applyRecordService.getById(busiId);
        if (null == applyRecord || null == applyRecord.getBusiId()) {
            throw new BusinessException("申请记录不存在！");
        }
        if (null != applyRecord.getState() && applyRecord.getState().intValue() != APPLY_STATE0 && applyRecord.getState().intValue() != APPLY_STATE3) {
            throw new BusinessException("该申请记录已被处理！");
        }
        if (null == applyRecord.getApplyVehicleClass()) {
            throw new BusinessException("业务类型数据错误！");
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
            throw new BusinessException("车辆在对方车队已存在！");
        }

        VehicleDataInfo vehicleDataInfo = getById(applyRecord.getBusiId());
        if (null == vehicleDataInfo) {
            throw new BusinessException("车辆信息不存在！");
        }

        TenantVehicleRel vehicleRel = tenantVehicleRelService.getTenantVehicleRel(vehicleDataInfo.getId(), applyRecord.getApplyTenantId());
        if (vehicleRel != null && vehicleRel.getVehicleClass().intValue() == applyRecord.getApplyVehicleClass().intValue()) {
            throw new BusinessException("车辆类型在申请车队已经存在！");
        }

        if (vehicleRel != null && vehicleRel.getVehicleClass().intValue() != applyRecord.getApplyVehicleClass().intValue()) {
            applyRecord.setIsModifyVehicleClass(true);
        } else {
            applyRecord.setIsModifyVehicleClass(false);
        }

        //自有车 --> 有车主(转移自有车)
        if (vehicleClass == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1 && null != applyRecord.getBeApplyTenantId() && applyRecord.getBeApplyTenantId().longValue() > 0) {
            auditCallBackService.transferTenant(applyRecord, tokeng);
        }
        //外调车，招商车或挂靠车邀请加入自有车--APP
        else if (vehicleClass == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1 && (null == applyRecord.getBeApplyTenantId() || applyRecord.getBeApplyTenantId().longValue() <= 0)) {
            auditCallBackService.invitationiJoinOwnApp(applyRecord, tokeng);
        }
        //邀请成为外调车，招商车或挂靠车，车辆有归属车队
        else if ((vehicleClass == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS5 || vehicleClass == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS2 || vehicleClass == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS4) && null != applyRecord.getBeApplyTenantId() && applyRecord.getBeApplyTenantId().longValue() > 0) {
            auditCallBackService.invitationiJoinTmp(applyRecord, tokeng);
        }
        //邀请成为外调车，招商车或挂靠车，车辆没有归属车队--APP
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
            throw new BusinessException("该业务数据不能审核");
        }

        if (instId != null && instId > 0) {
            if (instId.longValue() != auditNodeInst.getId().longValue()) {
                throw new BusinessException("页面审核的数据不是最新数据，请刷新后在进行审批！");
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

        //北斗 > G7 > 易流 > app
        if (0 == equipmentType) {
            if (null == operatorType) {
                throw new BusinessException("重置定位设备信息时必须传入operatorType");
            }
            if (!operatorType.equals(vehicleDataInfo.getLocationServ())) {
                //类型不一致，直接忽略
                return 1;
            }
            vehicleDataInfo.setLocationServ(null);
            vehicleDataInfo.setEquipmentCode(null);
            this.update(vehicleDataInfo);
            return 1;
        } else if (OrderConsts.GPS_TYPE.BD == equipmentType) {
            // 北斗可以覆盖所有定位类型
            vehicleDataInfo.setEquipmentCode(equipmentCode);
            vehicleDataInfo.setLocationServ(equipmentType);
            this.update(vehicleDataInfo);
            return 1;
        } else if (OrderConsts.GPS_TYPE.G7 == equipmentType &&
                (null == vehicleDataInfo.getLocationServ() ||
                        vehicleDataInfo.getLocationServ().equals(OrderConsts.GPS_TYPE.G7) ||
                        vehicleDataInfo.getLocationServ().equals(OrderConsts.GPS_TYPE.YL) ||
                        vehicleDataInfo.getLocationServ().equals(OrderConsts.GPS_TYPE.APP))) {
            //G7 可以覆盖  易流、app
            vehicleDataInfo.setEquipmentCode(equipmentCode);
            vehicleDataInfo.setLocationServ(equipmentType);
            this.update(vehicleDataInfo);
            return 1;
        } else if (OrderConsts.GPS_TYPE.YL == equipmentType && (
                null == vehicleDataInfo.getLocationServ() ||
                        vehicleDataInfo.getLocationServ().equals(OrderConsts.GPS_TYPE.YL) ||
                        vehicleDataInfo.getLocationServ().equals(OrderConsts.GPS_TYPE.APP))) {
            //YL 可以覆盖App
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
            throw new BusinessException("车辆的资料完整性未更新，请先修改车辆");
        }
        String completeNess = "";
        if (vehicleDataInfo.getLicenceType() == null) {
            return false;
        }
        if (vehicleDataInfo.getLicenceType() == SysStaticDataEnum.LICENCE_TYPE.TT) {//拖头
            if (goodsType == SysStaticDataEnum.GOODS_TYPE.COMMON_GOODS) {
                completeNess = readisUtil.getSysStaticData("VEHICLE_COMPLETENESS", "3").getCodeName();
            } else {
                completeNess = readisUtil.getSysStaticData("VEHICLE_COMPLETENESS", "4").getCodeName();
            }
        } else {//整车
            completeNess = readisUtil.getSysStaticData("VEHICLE_COMPLETENESS", goodsType + "").getCodeName();
        }

        if (org.apache.commons.lang.StringUtils.isEmpty(completeNess)) {
            throw new BusinessException("该货物类型车辆的资料完整性未配置");
        }

        char[] config = completeNess.toCharArray();
        char[] completeness = vehicleDataInfo.getCompleteness().toCharArray();

        if (config.length != completeness.length) {
            throw new BusinessException("车辆的资料完整性配置错误");
        }

        for (int index = 0; index < config.length; index++) {
            if (config[index] > completeness[index]) {
                LOGGER.info("======车辆信息不完整======" + vehicleDataInfo.getCompleteness());
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

                //判断是否是自有车
                Long vehicleCode = infoVxVo.getVehicleCode();
                TenantVehicleRel zyVehicleRel = tenantVehicleRelService.getZYVehicleByVehicleCode(vehicleCode.longValue());
                if (zyVehicleRel != null) {
                    infoVxVo.setTenantId(zyVehicleRel.getTenantId());

                } else {
                    infoVxVo.setTenantId(null);
                    infoVxVo.setTenantName("平台合作车");

                    VehicleDataInfo vehicleDataInfo = this.getById(vehicleCode);
                    if (vehicleDataInfo != null && vehicleDataInfo.getTenantId() != null && vehicleDataInfo.getTenantId() > 0) {
                        vehicleDataInfo.setTenantId(null);
                        this.update(vehicleDataInfo);
                    }
                }

//            long tenantRet = DataFormat.getLongKey(map, "tenantId");
//            if (tenantRet < 0) {
//                map.put("tenantName", "平台合作车");
//            }

                Integer vehicleClass = infoVxVo.getVehicleClass();
                if (null != vehicleClass) {

                    Long tenantIdTmp = infoVxVo.getTmpTenantId();
                    if (null == tenantIdTmp || tenantIdTmp != tenantId) {
                        infoVxVo.setVehicleClass(VEHICLE_CLASS5);
                        infoVxVo.setVehicleClassName("共享车");
//                    map.put("vehicleClassName",SysStaticDataUtil.getSysStaticDataCodeName("VEHICLE_CLASS",VEHICLE_CLASS5+""));

                    } else {
                        infoVxVo.setVehicleClass(vehicleClass);
                        infoVxVo.setVehicleClassName(readisUtil.getSysStaticData("VEHICLE_CLASS", vehicleClass + "").getCodeName());

                    }
                    if (zyVehicleRel != null) {//车队停用逻辑 并且有自有车司机
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
                    infoVxVo.setVehicleClassName("共享车");
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

                //判断是否是自有车
                Long vehicleCode = infoVxVo.getVehicleCode();
                TenantVehicleRel zyVehicleRel = tenantVehicleRelService.getZYVehicleByVehicleCode(vehicleCode.longValue());
                if (zyVehicleRel != null) {
                    infoVxVo.setTenantId(zyVehicleRel.getTenantId());

                } else {
                    infoVxVo.setTenantId(null);
                    infoVxVo.setTenantName("平台合作车");

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
                        infoVxVo.setVehicleClassName("共享车");
                    } else {
                        infoVxVo.setVehicleClass(vehicleClass);
                        infoVxVo.setVehicleClassName(readisUtil.getSysStaticData("VEHICLE_CLASS", vehicleClass + "").getCodeName());
                    }
                    if (zyVehicleRel != null) {//车队停用逻辑 并且有自有车司机
                        SysTenantDef sysTenantDef = sysTenantDefService.getById(zyVehicleRel.getTenantId());

                        if ((sysTenantDef != null && sysTenantDef.getState() == SysStaticDataEnum.SYS_STATE_DESC.STATE_NO)) {
                            infoVxVo.setTenantId(null);
                            infoVxVo.setTenantName(null);

                        }
                    }
                } else {
                    infoVxVo.setVehicleClass(VEHICLE_CLASS5);
                    infoVxVo.setVehicleClassName("共享车");
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
            throw new BusinessException("司机编码错误！");
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
            throw new BusinessException("车牌号格式错误！");
        }

        if (vo.getDriverUserId() == null || vo.getDriverUserId() < 0) {
            throw new BusinessException("司机参数错误！");
        }

        long count = this.getVehicleDataInfoByPlateNumber(vo.getPlateNumber());
        if (count > 0) {
            throw new BusinessException("该车辆已存在，请重新添加！");
        }

        if (StringUtils.isBlank(vo.getVehiclePicture())) {
            throw new BusinessException("请选择车辆图片！");
        }
        if (StringUtils.isBlank(vo.getDrivingLicense())) {
            throw new BusinessException("请选择车辆行驶证正本图片！");
        }
        if (StringUtils.isBlank(vo.getAdriverLicenseCopy())) {
            throw new BusinessException("请选择车辆行驶证副本图片！");
        }

        if (vo.getLicenceType() == null || vo.getLicenceType() < 0) {
            throw new BusinessException("请选择牌照类型！");
        }

        if (vo.getLicenceType() != null && vo.getLicenceType() == SysStaticDataEnum.LICENCE_TYPE.ZC) {
            if (vo.getVehicleStatus() == null || vo.getVehicleStatus() < 0) {
                throw new BusinessException("请选择车辆类型！");
            }
            if (StringUtils.isBlank(vo.getVehicleLength()) || "-1".equals(vo.getVehicleLength())) {
                throw new BusinessException("请选择车辆长度！");
            }
            if (vo.getVehicleLoad() == null || vo.getVehicleLoad() < 0) {
                throw new BusinessException("请选择可载重吨位！");
            }
            if (StringUtils.isBlank(vo.getLightGoodsSquare())) {
                throw new BusinessException("请输入容积！");
            }
        }

        if (StringUtils.isBlank(vo.getVinNo())) {
            throw new BusinessException("请输入车架号！");
        }

        if (StringUtils.isBlank(vo.getEngineNo())) {
            throw new BusinessException("请输入发动机号！");
        }

        if (StringUtils.isBlank(vo.getBrandModel())) {
            throw new BusinessException("请输入车辆品牌！");
        }

        if (StringUtils.isBlank(vo.getDrivingLicenseOwner())) {
            throw new BusinessException("请输入行驶证上所有人！");
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

        //新增车辆：
        //如果该司机是某一车队的有效自有车司机，则新建的车辆由车队审核；
        //若该司机不是某一车队的有效自有车司机，则新建的车辆由平台审核
        if (list != null && list.size() > 0) {

            TenantUserRel tenantUserRel = list.get(0);

            //判断挂靠人是否为司机归属车队的超管
            if (vo.getBillReceiverUserId() != null && vo.getBillReceiverUserId() > 0) {
                LambdaQueryWrapper<SysTenantDef> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(SysTenantDef::getAdminUser, vo.getBillReceiverUserId());
                queryWrapper.eq(SysTenantDef::getId, tenantUserRel.getTenantId());
                List<SysTenantDef> sysTenantDefList = sysTenantDefService.list(queryWrapper);
                if (sysTenantDefList != null && sysTenantDefList.size() > 0) {
                    throw new BusinessException("挂靠人不能是本车队的超管，请修改挂靠人！");
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

            saveSysOperLog(SysOperLogConst.BusiCode.Vehicle, tenantVehicleRel.getId(), SysOperLogConst.OperType.Add, "车辆通过APP新增", accessToken);

            //启动审核流程
            Map inMap = new HashMap();
            inMap.put("svName", "vehicleTF");
            inMap.put("tenantVehicleRelId", tenantVehicleRel.getId());
            inMap.put("tenantVehicleCostRelId", tenantVehicleRel.getId());

            boolean bool = iAuditService.startProcess(AUDIT_CODE_VEHICLE, tenantVehicleRel.getId(), SysOperLogConst.BusiCode.Vehicle, inMap, accessToken, tenantVehicleRel.getTenantId());
            if (!bool) {
                throw new BusinessException("启动审核流程失败！");
            }

        } else {

            //自动审核生效
            if (vo.getAutoAudit() != null && vo.getAutoAudit()) {
                //外调车OCR识别成功自动审核成功
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
                    //写日志
                    saveSysOperLog(SysOperLogConst.BusiCode.Vehicle, saveVehicleData.getId(), SysOperLogConst.OperType.Audit, "车辆通过APP自动审核", accessToken);
                }
            }

            //3.0迭代增加，增加一条平台记录
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

            //更新车量完整性
            this.doUpdateVehicleCompleteness(saveVehicleData.getId(), loginInfo.getTenantId());

        }

        //心愿线路
        List<LineDataDto> lineDataList = vo.getLineData();
        VehicleObjectLine vehicleObjectLine = null;
        VehicleObjectLineVer vehicleObjectLineVer = null;
        for (int i = 0; i < 3; i++) {//3条

            //先判断车辆有多少心愿线路，有3条了则不再增加
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
                tranMap = lineDataList.get(i);//有可能不够3条的
            } catch (Exception e) {
                tranMap = new LineDataDto();
            }

            //起始城市
            if (tranMap.getSourceProvince() != null && tranMap.getSourceProvince() > -1) {
                vehicleObjectLine.setSourceProvince(tranMap.getSourceProvince());
            }

            if (tranMap.getSourceRegion() != null && tranMap.getSourceRegion() > -1) {
                vehicleObjectLine.setSourceRegion(tranMap.getSourceRegion());
            }

            if (tranMap.getSourceCounty() != null && tranMap.getSourceCounty() > -1) {
                vehicleObjectLine.setSourceCounty(tranMap.getSourceCounty());
            }

            //目的城市
            if (tranMap.getDesProvince() != null && tranMap.getDesProvince() > -1) {
                vehicleObjectLine.setDesProvince(tranMap.getDesProvince());
            }

            if (tranMap.getDesRegion() != null && tranMap.getDesRegion() > -1) {
                vehicleObjectLine.setDesRegion(tranMap.getDesRegion());
            }

            if (tranMap.getDesCounty() != null && tranMap.getDesCounty() > -1) {
                vehicleObjectLine.setDesCounty(tranMap.getDesCounty());
            }

            //价格
            if (tranMap.getCarriagePrice() != null && CommonUtil.isNumber(tranMap.getCarriagePrice())) {
                Number num = Double.parseDouble(tranMap.getCarriagePrice());
                vehicleObjectLine.setCarriagePrice(num.longValue());
            }

            //保存线路
            vehicleObjectLine.setTenantId(null);
            vehicleObjectLine.setVehicleCode(saveVehicleData.getId());
            vehicleObjectLineService.save(vehicleObjectLine);
            BeanUtil.copyProperties(vehicleObjectLine, vehicleObjectLineVer);
            vehicleObjectLineVer.setVerState(VER_STATE_Y);
            vehicleObjectLineVer.setVehicleHisId(saveVehicleDataVer.getId());
            vehicleObjectLineVerService.save(vehicleObjectLineVer);
        }

        //添加挂靠信息
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

        int vehicleClass = VEHICLE_CLASS1; // 自有司机只看自有车的合作记录
        if (null == list || list.isEmpty()) {
            vehicleClass = VEHICLE_CLASS5;
        }

        Map<Long, Long> orderCountMap = null;
        if (vehicleClass == VEHICLE_CLASS5) { // 查询非自有的合作记录
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
            throw new BusinessException("车牌号格式错误！");
        }

        VehicleDataInfo vehicleDataInfo = this.getById(vehicleCode);
        if (null == vehicleDataInfo) {
            throw new BusinessException("该车辆不存在！");
        }

        if (vo.getDriverUserId() < 0) {
            throw new BusinessException("司机参数错误！");
        }

        if (org.apache.commons.lang.StringUtils.isBlank(vo.getVehiclePicture())) {
            throw new BusinessException("请选择车辆图片！");
        }

        if (org.apache.commons.lang.StringUtils.isBlank(vo.getDrivingLicense())) {
            throw new BusinessException("请选择车辆行驶证正本图片！");
        }

        if (org.apache.commons.lang.StringUtils.isBlank(vo.getAdriverLicenseCopy())) {
            throw new BusinessException("请选择车辆行驶证副本图片！");
        }

        if (vo.getLicenceType() == null || vo.getLicenceType() < 0) {
            throw new BusinessException("请选择牌照类型！");
        }

        if (vo.getLicenceType() == SysStaticDataEnum.LICENCE_TYPE.ZC) {

            if (vo.getVehicleStatus() == null || vo.getVehicleStatus() < 0) {
                throw new BusinessException("请选择车辆类型！");
            }

            if (StringUtils.isBlank(vo.getVehicleLength()) || "-1".equals(vo.getVehicleLength())) {
                throw new BusinessException("请选择车辆长度！");
            }

            if (vo.getVehicleLoad() == null || vo.getVehicleLoad() < 0) {
                throw new BusinessException("请选择可载重吨位！");
            }

            if (StringUtils.isBlank(vo.getLightGoodsSquare())) {
                throw new BusinessException("请输入容积！");
            }

        }

        if (StringUtils.isBlank(vo.getVinNo())) {
            throw new BusinessException("请输入车架号！");
        }

        if (StringUtils.isBlank(vo.getEngineNo())) {
            throw new BusinessException("请输入发动机号！");
        }
//        String drivingLicenseSn = vo.getDrivingLicenseSn();
//        String operCerti = vo.getOperCerti();

        if (org.apache.commons.lang.StringUtils.isBlank(vo.getBrandModel())) {
            throw new BusinessException("请输入车辆品牌！");
        }

        if (org.apache.commons.lang.StringUtils.isBlank(vo.getDrivingLicenseOwner())) {
            throw new BusinessException("请输入行驶证上所有人！");
        }

        //账单接收人信息
//        long billReceiverUserId = vo.getBillReceiverUserId();
//        String billReceiverMobile = vo.getBillReceiverMobile();
//        String billReceiverName = vo.getBillReceiverName();

        Map clearVerDataParamMap = new HashMap();
        clearVerDataParamMap.put("vehicleCode", vehicleCode);
        clearVerDataParamMap.put("destVerState", VER_STATE_N);

        //更新所有vehicle_data_info_ver表的ver_state状态为无效(0)
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

        //添加账单接收人信息
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

        //如果该司机是某一车队的有效自有车司机，则新建的车辆由车队审核；
        //若该司机不是某一车队的有效自有车司机，则新建的车辆由平台审核
        List<TenantUserRel> list = tenantUserRelService.getTenantUserRelsAllTenant(vo.getDriverUserId(), SysStaticDataEnum.CAR_USER_TYPE.OWN_CAR);

        TenantVehicleRel vehicleRel = tenantVehicleRelService.getZYVehicleByPlateNumber(saveVehicleDataVer.getPlateNumber());

        if (vehicleRel != null) {//有自有属性的车需要审核

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
                vehicleDataInfo.setIsAuth(IS_AUTH1);//数据生效,未认证需要审核
                this.saveOrUpdate(vehicleDataInfo);
            }

            saveSysOperLog(SysOperLogConst.BusiCode.Vehicle, vehicleRel.getId(), SysOperLogConst.OperType.Update, "车辆通过APP修改", accessToken);

            //启动审核流程
            Map inMap = new HashMap();
            inMap.put("svName", "vehicleTF");
            inMap.put("tenantVehicleRelId", vehicleRel.getId());
            inMap.put("tenantVehicleCostRelId", vehicleRel.getId());

            boolean bool = iAuditService.startProcess(AUDIT_CODE_VEHICLE, vehicleRel.getId(), SysOperLogConst.BusiCode.Vehicle, inMap, accessToken, vehicleRel.getTenantId());
            if (!bool) {
                throw new BusinessException("启动审核流程失败！");
            }

            unNeedCheck = false;

        } else {
            //App修改数据,没有车队不需要审核 2018-11-27
            BeanUtil.copyProperties(vo, vehicleDataInfo);

            //设置挂靠信息
            vehicleDataInfo.setAttachUserId(saveVehicleDataVer.getAttachUserId());
            vehicleDataInfo.setAttachUserName(saveVehicleDataVer.getAttachUserName());
            vehicleDataInfo.setAttachUserMobile(saveVehicleDataVer.getAttachUserMobile());

            vehicleDataInfo.setIsAuth(IS_AUTH0);
            this.update(vehicleDataInfo);

            //修改ver表状态为自动审核通过
            saveVehicleDataVer.setIsAuthSucc(3);//自动审核通过
            iVehicleDataInfoVerService.updateById(saveVehicleDataVer);

            //C端车启动平台审核
            saveSysOperLog(SysOperLogConst.BusiCode.Vehicle, vehicleCode, SysOperLogConst.OperType.Update, "车辆通过APP修改,无归属车队自动审核通过", accessToken);

            //这块应该是没有用的,现在修改不会有OCR操作
            boolean isAutoAudit = SysCfgUtil.getCfgBooleanVal("IS_AUTO_AUDIT", accessToken, loginUtils, redisUtil);
            if (isAutoAudit) {
                vehicleDataInfo.setIsAuth(IS_AUTH0);
                this.save(vehicleDataInfo);
                saveVehicleDataVer.setVerState(VER_STATE_N);
                iVehicleDataInfoVerService.save(saveVehicleDataVer);
                //写日志
                saveSysOperLog(SysOperLogConst.BusiCode.Vehicle, vehicleDataInfo.getId(), SysOperLogConst.OperType.Audit, "车辆通过APP修改自动审核", accessToken);
            }
            this.doUpdateVehicleCompleteness(vehicleDataInfo.getId(), loginInfo.getTenantId());
        }

        //心愿线路修改
        List<VehicleObjectLineVo> lineDataList = vo.getLineData();
        String vehicleObjectLineVerHisIds = "";
        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(lineDataList)) {
            //更新所有vehicle_object_line_ver表的ver_state状态为无效(0)
            this.updateVehicleVerAllVerState("vehicle_object_line_ver", clearVerDataParamMap);

            try {
                vehicleObjectLineVerHisIds = dealVehicleObjectLine(lineDataList, vehicleDataInfo, saveVehicleDataVer, unNeedCheck, true, accessToken);
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }

        //如果挂靠人是个车队超管则建立车辆与该车队的挂靠关系
        if (vo.getBillReceiverUserId() != null && vo.getBillReceiverUserId() > 0) {
            SysTenantDef tenantInfo = sysTenantDefService.getSysTenantDefByAdminUserId(vo.getBillReceiverUserId());
            if (tenantInfo != null && !tenantInfo.getState().equals(SysStaticDataEnum.SYS_STATE_DESC.STATE_NO)) {
                vehicleDataInfo.setDriverUserId(saveVehicleDataVer.getDriverUserId());//设置最新的司机信息
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

            //2.3迭代修改
            for (Long vehicleCode : vehicleCodeList) {

                //先创建一份主表的历史记录信息
                VehicleDataInfoVer vehicleDataInfoVer = iVehicleDataInfoVerService.doAddVehicleDataInfoVerHis(vehicleCode);
                if (null == vehicleDataInfoVer) {
                    continue;
                }

                TenantVehicleRel vehicleRel = tenantVehicleRelService.getZYVehicleByVehicleCode(vehicleCode);
                if (vehicleRel != null) {
                    throw new BusinessException("您无权限删除车队自有车！");
                } else {

                    LambdaQueryWrapper<TenantVehicleRel> queryWrapper = new LambdaQueryWrapper<>();
                    queryWrapper.eq(TenantVehicleRel::getVehicleCode, vehicleCode);
                    List<TenantVehicleRel> tenantVehicleRelList = tenantVehicleRelService.list();

                    for (TenantVehicleRel tenantVehicleRel : tenantVehicleRelList) {
                        this.dissolveCooperationVehicle(tenantVehicleRel.getId(), vehicleDataInfoVer.getId(), "APP");
                        saveSysOperLog(SysOperLogConst.BusiCode.Vehicle, tenantVehicleRel.getId(), SysOperLogConst.OperType.Del, "车辆通过APP移除", accessToken);
                    }

                    //清除邀请
                    removeApplyRecord(vehicleCode);

                    VehicleDataInfo vehicleDataInfo = this.getById(vehicleCode);
                    this.removeById(vehicleDataInfo);

                    //添加平台车记录
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
                        throw new BusinessException("自有车不能退出车队！");
                    }

                    if (null != tenantVehicleRel.getDriverUserId() && tenantVehicleRel.getDriverUserId() > 0) {
                        if (getTenantUserRel(tenantVehicleRel.getDriverUserId(), tenantVehicleRel.getTenantId()) != null) {
                        }
                    }

                    VehicleDataInfoVer vehicleDataInfoVer = iVehicleDataInfoVerService.doAddVehicleDataInfoVerHis(tenantVehicleRel.getVehicleCode());
                    this.dissolveCooperationVehicle(tenantVehicleRel.getId(), vehicleDataInfoVer.getId(), accessToken);
                    saveSysOperLog(SysOperLogConst.BusiCode.Vehicle, tenantVehicleRel.getId(), SysOperLogConst.OperType.Del, "车辆通过APP退出车队", accessToken);
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
                //查询心愿线路
                List<VehicleObjectLineDto> vehicleObjectLineVer = vehicleObjectLineVerService.getVehicleObjectLineVer(vehicleCode, null, VER_STATE_Y, 1);
                if (null != vehicleObjectLineVer) {
                    result.setVehicleOjbectLineArray(vehicleObjectLineVer);
                }
            }
        } else {
            //查询心愿线路
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
            result.setAuthStateName("认证失败，请重新认证");
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
            throw new BusinessException("不存在的车辆信息");
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
