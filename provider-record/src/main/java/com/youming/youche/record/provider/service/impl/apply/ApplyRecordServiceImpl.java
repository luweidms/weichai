package com.youming.youche.record.provider.service.impl.apply;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.domain.SysStaticData;
import com.youming.youche.commons.util.LoginUtils;
import com.youming.youche.commons.util.RedisUtil;
import com.youming.youche.commons.vo.VehicleApplyRecordVo;
import com.youming.youche.components.fdfs.FastDFSHelper;
import com.youming.youche.conts.AuditConsts;
import com.youming.youche.conts.SysStaticDataEnum;
import com.youming.youche.record.api.apply.IApplyRecordService;
import com.youming.youche.record.api.tenant.ITenantUserRelService;
import com.youming.youche.record.api.vehicle.IVehicleDataInfoService;
import com.youming.youche.record.common.CommonUtil;
import com.youming.youche.record.domain.apply.ApplyRecord;
import com.youming.youche.record.domain.tenant.TenantUserRel;
import com.youming.youche.record.domain.vehicle.VehicleDataInfo;
import com.youming.youche.record.dto.ApplyRecordVehicleDto;
import com.youming.youche.record.dto.ApplyRecordVehicleInfoDto;
import com.youming.youche.record.dto.VehicleApplyRecordDto;
import com.youming.youche.record.provider.mapper.apply.ApplyRecordMapper;
import com.youming.youche.record.vo.InvitationDataVo;
import com.youming.youche.record.vo.InvitationVo;
import com.youming.youche.system.api.ISysAttachService;
import com.youming.youche.system.api.ISysStaticDataService;
import com.youming.youche.system.api.ISysTenantDefService;
import com.youming.youche.system.api.audit.IAuditNodeInstService;
import com.youming.youche.system.domain.SysAttach;
import com.youming.youche.system.domain.SysTenantDef;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.youming.youche.conts.SysStaticDataEnum.APPLY_STATE.*;


/**
 * <p>
 * 申请记录表，会员跟车辆统一 服务实现类
 * </p>
 *
 * @author Terry
 * @since 2021-11-19
 */
@DubboService(version = "1.0.0")
@Service
public class ApplyRecordServiceImpl extends BaseServiceImpl<ApplyRecordMapper, ApplyRecord> implements IApplyRecordService {


    @Resource
    private ApplyRecordMapper applyRecordMapper;

    @Resource
    private LoginUtils loginUtils;

    @DubboReference(version = "1.0.0")
    ISysStaticDataService isysStaticDataService;

    @DubboReference(version = "1.0.0")
    IAuditNodeInstService iAuditNodeInstService;

    @DubboReference(version = "1.0.0")
    ISysAttachService iSysAttachService;

    @Resource
    RedisUtil redisUtil;

    @Lazy
    @Resource
    IVehicleDataInfoService iVehicleDataInfoService;

    @Resource
    ITenantUserRelService iTenantUserRelService;

    @DubboReference(version = "1.0.0")
    ISysTenantDefService iSysTenantDefService;

    @Override
    public List<ApplyRecord> getApplyRecordList(Long driverUserId, Long applyTenantId,Integer applyCarUserType, Integer state) {
        QueryWrapper<ApplyRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("BUSI_ID", driverUserId).eq("STATE", state);
        if (applyTenantId != null) {
            queryWrapper.eq("APPLY_TENANT_ID", applyTenantId);
        }
        if (applyCarUserType != null) {
            queryWrapper.eq("APPLY_CAR_USER_TYPE", applyCarUserType);
        }
        return applyRecordMapper.selectList(queryWrapper);
    }
    @Override
    public InvitationDataVo getInvitationCount(String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        InvitationDataVo invitationDataVo = new InvitationDataVo();
        Long invitationCount = baseMapper.getInvitationCount(loginInfo.getTenantId());
        Long invitationCountTO = baseMapper.getInvitationCountTO(loginInfo.getTenantId());
        invitationDataVo.setInviteToMeVehicles(invitationCountTO);
        invitationDataVo.setInviteVehicles(invitationCount);
        return invitationDataVo;
    }

    @Override
    public Page<InvitationVo> doQueryVehicleApplyRecordAll(Integer recordType, Integer applyType, String plateNumber, String tenantName,
                                                           String linkPhone, Integer applyVehicleClass, Integer state, String driverMobile,
                                                           String driverName, Page<InvitationVo> page, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        Page<InvitationVo> invitationVoList = null;
        //邀请我的
        if (recordType.equals(1)) {
            invitationVoList = baseMapper.doQueryVehicleApplyRecordAllTo(page,loginInfo.getTenantId(), applyType, plateNumber,tenantName,
                    linkPhone, driverMobile, driverName,state, applyVehicleClass);
        }
        //我邀请的auditCommon
        if (recordType.equals(0)) {
            invitationVoList = baseMapper.doQueryVehicleApplyRecordAll(page,loginInfo.getTenantId(), applyType, plateNumber,tenantName,
                    linkPhone, driverMobile, driverName,state, applyVehicleClass);
        }
        if (invitationVoList != null && invitationVoList.getRecords().size() > 0) {
            List busiIds = new ArrayList();
            for (InvitationVo invitationVo : invitationVoList.getRecords()) {
                busiIds.add(invitationVo.getEid());
                try {
                    if (invitationVo.getState() != null && invitationVo.getState() > -1) {
                        invitationVo.setStateName(isysStaticDataService.getSysStaticDatas("APPLY_STATE", invitationVo.getState()));
                    }
                    if (invitationVo.getApplyVehicleClass() != null && invitationVo.getApplyVehicleClass() > -1) {
                        invitationVo.setApplyVehicleClassName(isysStaticDataService.getSysStaticDatas("VEHICLE_CLASS", invitationVo.getApplyVehicleClass()));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (recordType.equals(1)) {
                    if (invitationVo.getState().equals(SysStaticDataEnum.APPLY_STATE.APPLY_STATE0) || invitationVo.getState().equals(SysStaticDataEnum.APPLY_STATE.APPLY_STATE3)) {
                        invitationVo.setReadFlg(0);
                    }
                }
                if (recordType.equals(0)) {
                    if (!invitationVo.getState().equals(SysStaticDataEnum.APPLY_STATE.APPLY_STATE0) && invitationVo.getState().equals(SysStaticDataEnum.APPLY_STATE.APPLY_STATE3) && invitationVo.getState().equals(0)) {
                        invitationVo.setReadFlg(0);
                    }
                }
                if (invitationVo.getApplyCarUserType() != null && invitationVo.getApplyCarUserType() > -1) {
                    invitationVo.setApplyCarUserTypeName(isysStaticDataService.getSysStaticData("DRIVER_TYPE", invitationVo.getApplyCarUserType() + "").getCodeName());
                } else {
                    invitationVo.setApplyCarUserTypeName("");
                }
                if (StringUtils.isEmpty(invitationVo.getCreateDate())) {
                    invitationVo.setCreateDate("");
                }
                if (StringUtils.isEmpty(invitationVo.getPlateNumber())) {
                    invitationVo.setPlateNumber("");
                }
            }
            dealPermission(AuditConsts.AUDIT_CODE.AUDIT_CODE_APPLY_VEHICLE,busiIds,invitationVoList.getRecords(),accessToken);
        }
        return invitationVoList;
    }

    @Override
    public Page<InvitationVo> doQueryVehicleApplyRecordAllWX(Integer recordType, Integer applyType, String plateNumber, String tenantName, String linkPhone,
                                                             String applyVehicleClass, String state, String driverMobile, String driverName,
                                                             Integer pageNum, Integer pageSize, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        Page<InvitationVo> page = new Page<>(pageNum, pageSize);

        List<InvitationVo> invitationVoList = null;
        //邀请我的
        if (recordType.equals(1)) {
            invitationVoList = baseMapper.doQueryVehicleApplyRecordAllToWX( loginInfo.getTenantId(), applyType, plateNumber, tenantName,
                    linkPhone, driverMobile, driverName, state, applyVehicleClass);
        }

        if (invitationVoList != null && invitationVoList.size() > 0) {

            // 复用pc查询服务，获取小程序所需数据，更新相应回参数据
            List<InvitationVo> result = invitationVoList.stream().filter(dto -> dto.getApplyType() == com.youming.youche.record.common.SysStaticDataEnum.APPLY_TYPE.VEHICLE)
                    .filter(dto -> dto.getState() == APPLY_STATE0 || dto.getState() == APPLY_STATE1 || dto.getState() == APPLY_STATE2)
                    .collect(Collectors.toList());

            page.setTotal(result.size());
            invitationVoList.clear();
            invitationVoList = result.stream().skip((pageNum - 1) * pageSize).limit(pageSize).collect(Collectors.toList());
            page.setRecords(invitationVoList);
            page.setSize(pageSize);
            page.setCurrent(pageNum);


            List busiIds = new ArrayList();
            for (InvitationVo invitationVo : page.getRecords()) {
                busiIds.add(invitationVo.getEid());
                try {
                    if (invitationVo.getState() != null && invitationVo.getState() > -1) {
                        invitationVo.setStateName(isysStaticDataService.getSysStaticDatas("APPLY_STATE", invitationVo.getState()));
                    }
                    if (invitationVo.getApplyVehicleClass() != null && invitationVo.getApplyVehicleClass() > -1) {
                        invitationVo.setApplyVehicleClassName(isysStaticDataService.getSysStaticDatas("VEHICLE_CLASS", invitationVo.getApplyVehicleClass()));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (recordType.equals(1)) {
                    if (invitationVo.getState().equals(SysStaticDataEnum.APPLY_STATE.APPLY_STATE0) || invitationVo.getState().equals(SysStaticDataEnum.APPLY_STATE.APPLY_STATE3)) {
                        invitationVo.setReadFlg(0);
                    }
                }
                if (recordType.equals(0)) {
                    if (!invitationVo.getState().equals(SysStaticDataEnum.APPLY_STATE.APPLY_STATE0) && invitationVo.getState().equals(SysStaticDataEnum.APPLY_STATE.APPLY_STATE3) && invitationVo.getState().equals(0)) {
                        invitationVo.setReadFlg(0);
                    }
                }
                if (invitationVo.getApplyCarUserType() != null && invitationVo.getApplyCarUserType() > -1) {
                    invitationVo.setApplyCarUserTypeName(isysStaticDataService.getSysStaticData("DRIVER_TYPE", invitationVo.getApplyCarUserType() + "").getCodeName());
                } else {
                    invitationVo.setApplyCarUserTypeName("");
                }
                if (StringUtils.isEmpty(invitationVo.getCreateDate())) {
                    invitationVo.setCreateDate("");
                }
                if (StringUtils.isEmpty(invitationVo.getPlateNumber())) {
                    invitationVo.setPlateNumber("");
                }
            }
            dealPermission(AuditConsts.AUDIT_CODE.AUDIT_CODE_APPLY_VEHICLE, busiIds, page.getRecords(), accessToken);
        }

        return page;
    }

    public void dealPermission(String busiCode,List busiIds,List<InvitationVo> items,String accessToken){
        Map<Long, Boolean> hasPermissionMap = null;


        if(!busiIds.isEmpty()) {
//            hasPermissionMap = iAuditNodeInstService.isHasPermission(AuditConsts.AUDIT_CODE.AUDIT_CODE_TRAILER, busiIds, accessToken);
            hasPermissionMap = iAuditNodeInstService.isHasPermission(busiCode, busiIds, accessToken);

            if(null == hasPermissionMap){
                return;
            }
            for (int i = 0; i < items.size(); i++) {
                InvitationVo rtnMap = items.get(i);
//                long eid = DataFormat.getLongKey(rtnMap,"eid");
//                Boolean flg = hasPermissionMap.get(rtnMap.getEid());
                rtnMap.setAudit(hasPermissionMap.get(rtnMap.getEid())?1:0);
//                rtnMap.put("isAuth",hasPermissionMap.get(rtnMap.getEid())?1:0);
            }
        }
    }

    @Override
    public List<ApplyRecord> getApplyRecord(Long busiId, Long applyTenantId, int state) {
        QueryWrapper<ApplyRecord> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("busi_id",busiId).
                eq("apply_tenant_id",applyTenantId).
                eq("state",state);
        return baseMapper.selectList(queryWrapper);
    }

    @Override
    public List<ApplyRecord> getApplyRecord(Long busiId, Integer applyCarUserType, int state) {
        QueryWrapper<ApplyRecord> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("busi_id",busiId).
                eq("apply_car_user_type",applyCarUserType).
                eq("state",state);
        return baseMapper.selectList(queryWrapper);
    }

    @Override
    public void invalidBeApplyRecord(Long userId, Long beApplyTenantId) {
        List<ApplyRecord> applyRecordList = getBeApplyRecord(userId, beApplyTenantId, SysStaticDataEnum.APPLY_STATE.APPLY_STATE0);
        if (CollectionUtils.isNotEmpty(applyRecordList)) {
            for (ApplyRecord applyRecord : applyRecordList) {
                applyRecord.setState(SysStaticDataEnum.APPLY_STATE.APPLY_STATE6);
                updateById(applyRecord);
            }
        }
    }

    public List<ApplyRecord> getBeApplyRecord(Long busiId, Long beApplyTenantId, int state) {
        QueryWrapper<ApplyRecord> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("busi_id",busiId).
                eq("be_Apply_Tenant_Id",beApplyTenantId)
                .eq("state",state);
        return baseMapper.selectList(queryWrapper);
    }




    @Override
    public List<ApplyRecord> selectByBusidAndState(long busId, int state) {
        LambdaQueryWrapper<ApplyRecord> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(ApplyRecord::getBusiId,busId).eq(ApplyRecord::getState,state);
        return baseMapper.selectList(wrapper);
    }

    @Override
    public List<ApplyRecord> getApplyRecordList(Long userId, int state, Long tenantId) {
        return baseMapper.getApplyRecordList(userId,state,tenantId);
    }

    @Override
    public Map<String, Object> getVehicleRecordInfo(long vehicleCode, Long tenantId) {
        return baseMapper.getVehicleRecordInfo(vehicleCode,tenantId);
    }

    @Override
    public VehicleApplyRecordVo getVehicleApplyRecord(Long applyRecordId) {
        return baseMapper.getVehicleApplyRecord(applyRecordId);
    }

    @Override
    public Long getApplyCount(Long tenantId, Integer vehicleClass, Long vehicleCode) {
        return baseMapper.getApplyCount(tenantId, vehicleClass, vehicleCode);
    }

    @Override
    public Long getApplyVehicleCountByDriverUserId(Long tenantId, Integer vehicleClass, Long driverUserId) {
        return baseMapper.getApplyVehicleCountByDriverUserId(tenantId, vehicleClass, driverUserId);
    }

    @Override
    public ApplyRecord saveApplyRecord(ApplyRecord applyRecord) {
        this.save(applyRecord);
        return applyRecord;
    }

    @Override
    public VehicleApplyRecordDto getVehicleApplyRecordForMiniProgram(Long applyRecordId, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        this.updateReadState(applyRecordId);
        VehicleApplyRecordDto vehicleApplyRecordNew = baseMapper.getVehicleApplyRecordNew(applyRecordId, 1, loginInfo.getTenantId());
        if (null != vehicleApplyRecordNew) {
            int stateRtn = vehicleApplyRecordNew.getReadState();
            int licenceType = vehicleApplyRecordNew.getLicenceType() == null ? -1 : vehicleApplyRecordNew.getLicenceType();
            if (stateRtn > -1) {
                vehicleApplyRecordNew.setStateName(getSysStaticData("APPLY_STATE", "" + stateRtn).getCodeName());
            }
            long vehicleStatus = vehicleApplyRecordNew.getVehicleStatus() == null ? 0 : vehicleApplyRecordNew.getVehicleStatus();
            String vehicleLength = vehicleApplyRecordNew.getVehicleLength();
            if (vehicleStatus > 0) {
                vehicleApplyRecordNew.setVehicleStatusName(getSysStaticDataById("VEHICLE_STATUS", vehicleStatus));
            }
            if (StringUtils.isNotBlank(vehicleLength) && !"-1".equals(vehicleLength)) {
                if (vehicleStatus == 8) {
                    vehicleApplyRecordNew.setVehicleLengthName(getSysStaticDataById("VEHICLE_STATUS_SUBTYPE", Long.parseLong(vehicleLength)));
                } else {
                    vehicleApplyRecordNew.setVehicleLengthName(getSysStaticDataById("VEHICLE_LENGTH", Long.parseLong(vehicleLength)));
                }
            }
            if (vehicleStatus > 0 && StringUtils.isNotBlank(vehicleLength) && !"-1".equals(vehicleLength)) {
                String vehicleStatusV = vehicleApplyRecordNew.getVehicleStatusName();
                String vehicleLengthV = vehicleApplyRecordNew.getVehicleLengthName();
                vehicleApplyRecordNew.setVehicleInfo(vehicleStatusV + "/" + vehicleLengthV);
            } else if (vehicleStatus > 0 && (StringUtils.isBlank(vehicleLength) || "-1".equals(vehicleLength))) {
                String vehicleStatusV = vehicleApplyRecordNew.getVehicleStatusName();
                vehicleApplyRecordNew.setVehicleInfo(vehicleStatusV);
            } else {
                String vehicleLengthV = vehicleApplyRecordNew.getVehicleLengthName();
                vehicleApplyRecordNew.setVehicleInfo(vehicleLengthV);
            }
            if (licenceType > -1) {
                vehicleApplyRecordNew.setLicenceTypeName(getSysStaticData("LICENCE_TYPE", "" + licenceType).getCodeName());
            }
            int applyVehicleClassRtn = vehicleApplyRecordNew.getApplyVehicleClass() == null ? -1 : vehicleApplyRecordNew.getApplyVehicleClass();
            if (applyVehicleClassRtn > -1) {
                vehicleApplyRecordNew.setApplyVehicleClassName(getSysStaticData("VEHICLE_CLASS", "" + applyVehicleClassRtn).getCodeName());
            }

            int vehicleClassRtn = vehicleApplyRecordNew.getVehicleClass() == null ? -1 : vehicleApplyRecordNew.getVehicleClass();
            if (vehicleClassRtn > -1) {
                vehicleApplyRecordNew.setVehicleClassName(getSysStaticData("VEHICLE_CLASS", "" + vehicleClassRtn).getCodeName());
            }
            if (null == vehicleApplyRecordNew.getAuditRemark()) {
                vehicleApplyRecordNew.setAuditRemark("");
            }
            if (StringUtils.isEmpty(vehicleApplyRecordNew.getApplyVehicleClassName())) {
                vehicleApplyRecordNew.setApplyVehicleClassName("");
            }
            if (StringUtils.isEmpty(vehicleApplyRecordNew.getPlateNumber())) {
                vehicleApplyRecordNew.setPlateNumber("");
            }
        }
        long applyFileId = vehicleApplyRecordNew.getApplyFileId() == null ? 0 : vehicleApplyRecordNew.getApplyFileId();
        if (applyFileId > 0) {
            SysAttach sysAttach = iSysAttachService.getById(applyFileId);
            vehicleApplyRecordNew.setApplyFileUrl(sysAttach.getStorePath());
            FastDFSHelper client = null;
            try {
                client = FastDFSHelper.getInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                vehicleApplyRecordNew.setApplyFileBigUrl(client.getHttpURL(sysAttach.getStorePath(), true));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        long drivingLicense = vehicleApplyRecordNew.getDrivingLicense() == null ? 0 : vehicleApplyRecordNew.getDrivingLicense();
        if (drivingLicense > 0) {
            SysAttach sysAttach = iSysAttachService.getById(drivingLicense);
            vehicleApplyRecordNew.setDrivingLicenseUrl(sysAttach.getStorePath());
            FastDFSHelper client = null;
            try {
                client = FastDFSHelper.getInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                vehicleApplyRecordNew.setDrivingLicenseBigUrl(client.getHttpURL(sysAttach.getStorePath(), true));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        VehicleDataInfo dataInfo = iVehicleDataInfoService.getVehicleDataInfo(vehicleApplyRecordNew.getPlateNumber());
        long operCerti =dataInfo != null &&  dataInfo.getOperCertiId() != null? dataInfo.getOperCertiId() : 0;
        if (operCerti > 0) {
            SysAttach sysAttach = iSysAttachService.getById(operCerti);
            vehicleApplyRecordNew.setOperCertiUrl(sysAttach.getStorePath());
            FastDFSHelper client = null;
            try {
                client = FastDFSHelper.getInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                vehicleApplyRecordNew.setOperCertiBigUrl(client.getHttpURL(sysAttach.getStorePath(), true));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return vehicleApplyRecordNew;
    }

    @Override
    public Long queryApplyCount(String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        LambdaQueryWrapper<ApplyRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ApplyRecord::getState, 0);
        queryWrapper.eq(ApplyRecord::getBeApplyTenantId, loginInfo.getTenantId());
        return Long.valueOf(this.count(queryWrapper));
    }

    @Override
    public ApplyRecordVehicleDto getApplyRecord(Long applyId) {
        ApplyRecordVehicleDto applyRecord = baseMapper.getApplyRecord(applyId);

        applyRecord.setApplyFileUrl("");
        applyRecord.setApplyCarCertPicUrl("");

        int stateRtn = applyRecord.getState();
        if (stateRtn > -1) {
            applyRecord.setStateName(getSysStaticData("APPLY_STATE", "" + stateRtn).getCodeName());
            if (stateRtn == 1) {
                applyRecord.setStateName("已接受邀请");
            } else if (stateRtn == 2) {
                applyRecord.setStateName("已驳回邀请");
            }
        }

        if (applyRecord.getApplyCarUserType() != null && applyRecord.getApplyCarUserType() > -1) {
            applyRecord.setApplyTypeName(getSysStaticData("DRIVER_TYPE", "" + applyRecord.getApplyCarUserType()).getCodeName());
        }

        if (applyRecord.getApplyVehicleClass() != null && applyRecord.getApplyVehicleClass() > -1) {
            applyRecord.setApplyTypeName(getSysStaticData("VEHICLE_CLASS", "" + applyRecord.getApplyVehicleClass()).getCodeName());
        }

        FastDFSHelper client = null;
        try {
            client = FastDFSHelper.getInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        String flowId = applyRecord.getApplyFile();
        if (applyRecord.getApplyVehicleClass() != null && applyRecord.getApplyVehicleClass() == SysStaticDataEnum.CAR_USER_TYPE.OWN_CAR) {
            flowId = applyRecord.getApplyFile();
        } else {
            flowId = applyRecord.getApplyFileId();
        }

        if (StringUtils.isNotEmpty(flowId)) {
            SysAttach sysAttach = iSysAttachService.getById(Long.valueOf(flowId));
            if (sysAttach != null) {
                String url = null;
                try {
                    String tag = sysAttach.getStorePath().substring(sysAttach.getStorePath().lastIndexOf(".") + 1);
                    url = client.getHttpURL(sysAttach.getStorePath()).split("\\?")[0];
                    url = url.substring(0,url.lastIndexOf("."))+ "_big." + tag;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                applyRecord.setApplyFileUrl(url);
            }
        }

        String operCertflowId = applyRecord.getOperCert();
        if (StringUtils.isNotEmpty(operCertflowId)) {
            SysAttach sysAttach = iSysAttachService.getById(Long.valueOf(operCertflowId));
            if (sysAttach != null) {
                String operCertUrl = null;
                try {
                    operCertUrl = client.getHttpURL(sysAttach.getStorePath()).split("\\?")[0];
                } catch (Exception e) {
                    e.printStackTrace();
                }
                applyRecord.setApplyCarCertPicUrl(operCertUrl);
            }
        }

        if (applyRecord.getApplyType() != null && applyRecord.getApplyType() == 2) {
            long vehicleCode = applyRecord.getPlateNumber();
            VehicleDataInfo vehicleDataInfo = iVehicleDataInfoService.getVehicleDataInfo(vehicleCode);
            applyRecord.setPlateNumberStr(vehicleDataInfo.getPlateNumber());
        }

        applyRecord.setVehicles(new ArrayList<>());
        applyRecord.setCanChoose(true); //新邀请时可以选

        if (applyRecord.getApplyType() != null && applyRecord.getApplyType() == 2) {
            //邀请为自有车、邀请车辆时，不显示车辆
            return applyRecord;
        }

//        long userId = applyRecord.getPlateNumber();
        if (applyRecord.getPlateNumber() != null) {
            TenantUserRel tenantUserRel = iTenantUserRelService.getTenantUserRelByUserId(applyRecord.getPlateNumber(), applyRecord.getApplyTenantId());
            if (null != tenantUserRel) {
                applyRecord.setCanChoose(true); //修改类型时不可以选
            }

            String applyPlateNumbers = applyRecord.getApplyPlateNumbers();
            List<String> plateNumberList = CommonUtil.convertStringList(applyPlateNumbers);

            //如果司机归属车队停用，所有C端车+归属于停用车队的自有车
            //C端司机   所有C端车
            Long tenantId = null;
            tenantUserRel = iTenantUserRelService.getTenantUserRelByUserIdAndType(applyRecord.getPlateNumber(), SysStaticDataEnum.CAR_USER_TYPE.OWN_CAR);
            if (null != tenantUserRel) {
                SysTenantDef sysTenantDef = iSysTenantDefService.getSysTenantDef(tenantUserRel.getTenantId(), true);
                if (sysTenantDef.getState().equals(SysStaticDataEnum.SYS_STATE_DESC.STATE_NO)) {
                    tenantId = sysTenantDef.getId();
                }
            }

            List<ApplyRecordVehicleInfoDto> applyRecordVehicle = baseMapper.getApplyRecordVehicle(tenantId, applyRecord.getPlateNumber());

            if (applyRecordVehicle != null && !applyRecordVehicle.isEmpty()) {
                for (ApplyRecordVehicleInfoDto dto : applyRecordVehicle) {

                    String vehicleStatusName = getSysStaticData("VEHICLE_STATUS", dto.getVehicleStatus()).getCodeName();
                    dto.setVehicleTypeString(((dto.getVehicleLength() + "米/").equals("0米/") ? "" : dto.getVehicleLength() + "米/") +
                            (vehicleStatusName != null ? vehicleStatusName : ""));
                    if (null != plateNumberList && plateNumberList.contains(dto.getPlateNumber())) {
                        dto.setChecked(true);
                    } else {
                        dto.setChecked(false);
                    }
                }

                applyRecord.setVehicles(applyRecordVehicle);
            }

        }

        return applyRecord;
    }

    @Override
    public List<ApplyRecord> getUpdateMessageFlagAllApplyRecordData(Long id, Integer state) {
        LambdaQueryWrapper<ApplyRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ApplyRecord::getId, id);
        queryWrapper.eq(ApplyRecord::getState, state);
        return this.list(queryWrapper);
    }

    /**
     * 修改邀请记录为已读
     *
     * @param applyId 申请记录主键
     */
    private void updateReadState(long applyId) {
        ApplyRecord applyRecord = this.getById(applyId);
        if (applyRecord != null && applyRecord.getState() != 0 && applyRecord.getState() != 3 && applyRecord.getReadState() == 0) {
            applyRecord.setReadState(1);
            this.updateById(applyRecord);
        }
    }

    public SysStaticData getSysStaticData(String codeType, String codeValue) {
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

    public String getSysStaticDataById(String codeType, Long id) {
        List<SysStaticData> list = (List<SysStaticData>) redisUtil.get(SysStaticDataEnum.SYS_STATIC_DATA_NAME.concat(codeType));
        if (list != null && list.size() > 0) {
            for (SysStaticData sysStaticData : list) {
                if (sysStaticData.getId().equals(id)) {
                    return sysStaticData.getCodeName();
                }
            }
        }
        return "";
    }

}
