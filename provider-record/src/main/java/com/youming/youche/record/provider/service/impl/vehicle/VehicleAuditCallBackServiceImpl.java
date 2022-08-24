package com.youming.youche.record.provider.service.impl.vehicle;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.youming.youche.commons.constant.SysOperLogConst;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.domain.SysUser;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.util.DataFormat;
import com.youming.youche.commons.util.LoginUtils;
import com.youming.youche.commons.util.RedisUtil;
import com.youming.youche.conts.SysStaticDataEnum;
import com.youming.youche.record.api.apply.IApplyRecordService;
import com.youming.youche.record.api.audit.IAuditCallBackService;
import com.youming.youche.record.api.etc.IEtcMaintainService;
import com.youming.youche.record.api.sys.ISysUserService;
import com.youming.youche.record.api.tenant.ITenantUserRelService;
import com.youming.youche.record.api.tenant.ITenantUserRelVerService;
import com.youming.youche.record.api.tenant.ITenantVehicleRelService;
import com.youming.youche.record.api.tenant.IUserReceiverInfoService;
import com.youming.youche.record.api.user.IUserDataInfoRecordService;
import com.youming.youche.record.api.user.IUserDataInfoVerService;
import com.youming.youche.record.api.vehicle.ITenantVehicleRelVerService;
import com.youming.youche.record.api.vehicle.IVehicleDataInfoService;
import com.youming.youche.record.api.vehicle.IVehicleDataInfoVerService;
import com.youming.youche.record.common.AuditConsts;
import com.youming.youche.record.common.CommonUtil;
import com.youming.youche.record.common.EnumConsts;
import com.youming.youche.record.domain.apply.ApplyRecord;
import com.youming.youche.record.domain.tenant.TenantUserRel;
import com.youming.youche.record.domain.tenant.TenantUserRelVer;
import com.youming.youche.record.domain.tenant.TenantVehicleRel;
import com.youming.youche.record.domain.tenant.TenantVehicleRelVer;
import com.youming.youche.record.domain.user.UserDataInfo;
import com.youming.youche.record.domain.user.UserDataInfoVer;
import com.youming.youche.record.domain.user.UserReceiverInfo;
import com.youming.youche.record.domain.vehicle.VehicleDataInfo;
import com.youming.youche.record.domain.vehicle.VehicleDataInfoVer;
import com.youming.youche.record.provider.mapper.tenant.TenantVehicleRelMapper;
import com.youming.youche.record.provider.mapper.vehicle.VehicleDataInfoMapper;
import com.youming.youche.system.api.ISysAttachService;
import com.youming.youche.system.api.ISysOperLogService;
import com.youming.youche.system.api.ISysStaticDataService;
import com.youming.youche.system.api.ISysTenantDefService;
import com.youming.youche.system.api.audit.IAuditService;
import com.youming.youche.system.domain.SysAttach;
import com.youming.youche.system.domain.SysOperLog;
import com.youming.youche.system.domain.SysTenantDef;
import com.youming.youche.system.dto.ApplyRecordDto;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @version:
 * @Title: VehicleAuditCallBackServiceImpl 邀请车辆审核
 * @Package: com.youming.youche.record.provider.service.impl.vehicle
 * @Description: TODO(用一句话描述该文件做什么)
 * @author:DengYuanYe
 * @date: 2022/2/22 21:20
 * @company:
 */
@DubboService(version = "1.0.0")
@Service
public class VehicleAuditCallBackServiceImpl implements IAuditCallBackService {

    @Resource
    IApplyRecordService applyRecordService;

    @Resource
    IUserDataInfoRecordService iUserDataInfoRecordService;

    @Resource
    IUserReceiverInfoService iUserReceiverInfoService;

    @Resource
    IUserDataInfoVerService iUserDataInfoVerService;

    @Resource
    TenantVehicleRelMapper tenantVehicleRelMapper;

    @Resource
    IVehicleDataInfoService iVehicleDataInfoService;

    @Resource
    IVehicleDataInfoVerService iVehicleDataInfoVerService;

    @Resource
    ITenantVehicleRelService iTenantVehicleRelService;

    @DubboReference(version = "1.0.0")
    ITenantVehicleRelVerService iTenantVehicleRelVerService;

    @DubboReference(version = "1.0.0")
    ISysTenantDefService iSysTenantDefService;

    @Resource
    VehicleDataInfoMapper vehicleDataInfoMapper;

    @Resource
    IEtcMaintainService iEtcMaintainService;

    @DubboReference(version = "1.0.0")
    ISysOperLogService iSysOperLogService;

    @DubboReference(version = "1.0.0")
    ISysAttachService iSysAttachService;

    @DubboReference(version = "1.0.0")
    IAuditService iAuditService;

    @Resource
    RedisUtil redisUtil;

    @Resource
    LoginUtils loginUtils;

    @Resource
    ISysUserService iSysUserService;

    @Resource
    ITenantUserRelService iTenantUserRelService;

    @Resource
    ITenantUserRelVerService iTenantUserRelVerService;

    @DubboReference(version = "1.0.0")
    ISysStaticDataService iSysStaticDataService;


    @Override
    @Transactional
    public void sucess(Long busiId, String desc, Map paramsMap, String token) throws BusinessException {
        LoginInfo user = iUserDataInfoRecordService.getLoginInfoByAccessToken(token);
        if (null == busiId) {
            throw new BusinessException("业务主键数据错误！");
        }
        ApplyRecord applyRecord = applyRecordService.getById(busiId);
        if (null == applyRecord || null == applyRecord.getBusiId()) {
            throw new BusinessException("申请记录不存在！");
        }
        if (null != applyRecord.getState() && applyRecord.getState().intValue() != SysStaticDataEnum.APPLY_STATE.APPLY_STATE0 && applyRecord.getState().intValue() != SysStaticDataEnum.APPLY_STATE.APPLY_STATE3) {
            throw new BusinessException("该申请记录已被处理！");
        }
        if (null == applyRecord.getApplyVehicleClass()) {
            throw new BusinessException("业务类型数据错误！");
        }
        int auditState = DataFormat.getIntKey(paramsMap, "auditState");
        applyRecord.setAuditDate(LocalDateTime.now());
        applyRecord.setAuditRemark(desc);
        applyRecord.setAuditOpId(user.getUserInfoId());
        if (auditState < 0) {
            applyRecord.setState(SysStaticDataEnum.APPLY_STATE.APPLY_STATE1);
        } else {
            applyRecord.setState(auditState);
        }
        int vehicleClass = applyRecord.getApplyVehicleClass().intValue();
        long count = tenantVehicleRelMapper.getZYCount(applyRecord.getBusiId(), applyRecord.getApplyTenantId(), applyRecord.getApplyVehicleClass());
        if (count > 0) {
            throw new BusinessException("车辆在对方车队已存在！");
        }
        VehicleDataInfo vehicleDataInfo = iVehicleDataInfoService.getById(applyRecord.getBusiId());
        if (null == vehicleDataInfo) {
            throw new BusinessException("车辆信息不存在！");
        }

        TenantVehicleRel vehicleRel = iTenantVehicleRelService.getTenantVehicleRel(vehicleDataInfo.getId(), applyRecord.getApplyTenantId());
        if (vehicleRel != null && vehicleRel.getVehicleClass().intValue() == applyRecord.getApplyVehicleClass().intValue()) {
            throw new BusinessException("车辆类型在申请车队已经存在！");
        }

        if (vehicleRel != null && vehicleRel.getVehicleClass().intValue() != applyRecord.getApplyVehicleClass().intValue()) {
            applyRecord.setIsModifyVehicleClass(true);
        } else {
            applyRecord.setIsModifyVehicleClass(false);
        }
        applyRecordService.updateById(applyRecord);
        //自有车 --> 有车主(转移自有车)
        if (vehicleClass == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1 && null != applyRecord.getBeApplyTenantId() && applyRecord.getBeApplyTenantId().longValue() > 0) {
            transferTenant(applyRecord, token);
        }
        //外调车，招商车或挂靠车邀请加入自有车--APP
        else if (vehicleClass == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1 && (null == applyRecord.getBeApplyTenantId() || applyRecord.getBeApplyTenantId().longValue() <= 0)) {
            invitationiJoinOwnApp(applyRecord, token);
        }
        //邀请成为外调车，招商车或挂靠车，车辆有归属车队
        else if ((vehicleClass == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS5 || vehicleClass == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS2 || vehicleClass == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS4) && null != applyRecord.getBeApplyTenantId() && applyRecord.getBeApplyTenantId().longValue() > 0) {
            invitationiJoinTmp(applyRecord, token);
        }
        //邀请成为外调车，招商车或挂靠车，车辆没有归属车队--APP
        else if ((vehicleClass == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS5 || vehicleClass == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS2 || vehicleClass == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS4) && (null == applyRecord.getBeApplyTenantId() || applyRecord.getBeApplyTenantId().longValue() <= 0)) {
            invitationiJoinTmp(applyRecord, token);
        }
    }

    @Transactional
    @Override
    public void fail(Long busiId, String desc, Map paramsMap, String token) throws BusinessException {
        if (null == busiId) {
            throw new BusinessException("业务主键数据错误！");
        }
        ApplyRecord applyRecord = applyRecordService.getById(busiId);
        if (null == applyRecord) {
            throw new BusinessException("业务主键数据错误！");
        }
        int auditState = DataFormat.getIntKey(paramsMap, "auditState");
        if (auditState < 0) {
            applyRecord.setState(SysStaticDataEnum.APPLY_STATE.APPLY_STATE2);
        } else {
            applyRecord.setState(auditState);
        }
        LoginInfo user = iUserDataInfoRecordService.getLoginInfoByAccessToken(token);
        applyRecord.setAuditRemark(desc);
        applyRecord.setAuditDate(LocalDateTime.now());
        applyRecord.setAuditOpId(user.getUserInfoId());
        applyRecordService.updateById(applyRecord);

        String rem = "成为";
        if (null != applyRecord.getApplyVehicleClass()) {
            rem = rem + iSysStaticDataService.getSysStaticData("VEHICLE_CLASS", applyRecord.getApplyVehicleClass() + "").getCodeName() + "车";
        }
        saveSysOperLog(SysOperLogConst.BusiCode.Vehicle, SysOperLogConst.OperType.Audit, "驳回邀请车辆" + rem, token, applyRecord.getId());
    }


    /***
     * 邀请加入自有车
     * @param applyRecord
     * @throws Exception
     */
    @Override
    public void invitationiJoinOwnApp(ApplyRecord applyRecord, String token){

        VehicleDataInfo vehicleDataInfo = iVehicleDataInfoService.getById(applyRecord.getBusiId());
        if (null == vehicleDataInfo) {
            throw new BusinessException("车辆信息不存在！");
        }
        //判断是内部转移还是外部邀请
        int authState = getAuthState(applyRecord);

        //加入自有车需要将此车辆在车队存在的其他类型移到历史
        dealMoreVehicleInTenantForOwn(applyRecord.getApplyTenantId(), applyRecord.getBusiId(), token);

        vehicleDataInfo.setOperCerti(applyRecord.getOperCerti());
        vehicleDataInfo.setDrivingLicense(applyRecord.getDrivingLicense());
        vehicleDataInfo.setDriverUserId(applyRecord.getApplyDriverUserId());
        vehicleDataInfo.setTenantId(applyRecord.getApplyTenantId());
        iVehicleDataInfoService.update(vehicleDataInfo);

        Map<String, Object> inMap = new HashMap<String, Object>();
        inMap.put("vehicleCode", vehicleDataInfo.getId());
        inMap.put("tenantId", applyRecord.getBeApplyTenantId());
        inMap.put("destVerState", SysStaticDataEnum.VER_STATE.VER_STATE_N);

        //更新vehicle_data_info_ver表的verState状态为0
        iVehicleDataInfoService.updateVehicleVerAllVerState("vehicle_data_info_ver", inMap);

        VehicleDataInfoVer vehicleDataInfoVerNew = new VehicleDataInfoVer();
        BeanUtil.copyProperties(vehicleDataInfo, vehicleDataInfoVerNew);

        vehicleDataInfoVerNew.setVehicleCode(vehicleDataInfo.getId());
        vehicleDataInfoVerNew.setId(null);
        vehicleDataInfoVerNew.setTenantId(applyRecord.getApplyTenantId());
        vehicleDataInfoVerNew.setVerState(SysStaticDataEnum.VER_STATE.VER_STATE_Y);
        iVehicleDataInfoVerService.save(vehicleDataInfoVerNew);

        TenantVehicleRel tenantVehicleRel = new TenantVehicleRel();
        tenantVehicleRel.setVehicleCode(applyRecord.getBusiId());
        tenantVehicleRel.setTenantId(applyRecord.getApplyTenantId());
        tenantVehicleRel.setDriverUserId(applyRecord.getApplyDriverUserId());
        tenantVehicleRel.setVehicleClass(applyRecord.getApplyVehicleClass());
        tenantVehicleRel.setPlateNumber(vehicleDataInfo.getPlateNumber());
        tenantVehicleRel.setAuthState(authState);
        tenantVehicleRel.setIsAuth(SysStaticDataEnum.IS_AUTH.IS_AUTH1);
        tenantVehicleRel.setOperCerti(applyRecord.getOperCerti());
        tenantVehicleRel.setDrivingLicense(applyRecord.getDrivingLicense());
        tenantVehicleRel.setCreateTime(LocalDateTime.now());
        iTenantVehicleRelService.save(tenantVehicleRel);

        //更新tenant_vehicle_rel_ver表的verState状态为0
        iVehicleDataInfoService.updateVehicleVerAllVerState("tenant_vehicle_rel_ver", inMap);

        TenantVehicleRelVer tenantVehicleRelVer = new TenantVehicleRelVer();
        BeanUtil.copyProperties(tenantVehicleRel, tenantVehicleRelVer);
        tenantVehicleRelVer.setRelId(tenantVehicleRel.getId());
        tenantVehicleRelVer.setVerState(SysStaticDataEnum.VER_STATE.VER_STATE_Y);
        iTenantVehicleRelVerService.save(tenantVehicleRelVer);

        //删除平台车记录
        removePtVehicle(vehicleDataInfo.getPlateNumber(), null);

        saveSysOperLog(SysOperLogConst.BusiCode.Vehicle, SysOperLogConst.OperType.Audit, tenantVehicleRel.getPlateNumber() + "加入车队自有车!", token, tenantVehicleRel.getId());

        //启动审核流程
        Map<String, Object> iMap = new HashMap<String, Object>();
        iMap.put("svName", "vehicleTF");
        iMap.put("tenantVehicleRelId", tenantVehicleRel.getId());
        boolean bool = iAuditService.startProcess(AuditConsts.AUDIT_CODE.AUDIT_CODE_VEHICLE, tenantVehicleRel.getId(), SysOperLogConst.BusiCode.Vehicle, iMap, token,applyRecord.getApplyTenantId());
        if (!bool) {
            throw new BusinessException("启动审核流程失败！");
        }

        //在会员租户关系表新增自有司机类型记录
        addNewDriver(applyRecord, vehicleDataInfo, authState, token);

    }


    /**
     * 删除车辆的平台归属记录
     *
     * @param plateNumber
     * @param vehicleCode
     * @throws Exception
     */
    private void removePtVehicle(String plateNumber, Long vehicleCode) throws BusinessException {
        QueryWrapper<TenantVehicleRel> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("tenant_id", 1);
        if (StringUtils.isNotBlank(plateNumber)) {
            queryWrapper.eq("plate_number", plateNumber);
        }
        if (vehicleCode != null && vehicleCode > 0) {
            queryWrapper.eq("vehicle_code", vehicleCode);
        }
        List<TenantVehicleRel> ptVehicleRelList = iTenantVehicleRelService.list(queryWrapper);
        if (ptVehicleRelList != null && ptVehicleRelList.size() > 0) {
            for (TenantVehicleRel vehicleRel : ptVehicleRelList) {
                iTenantVehicleRelService.removeById(vehicleRel.getId());
            }
        }
    }

    /***
     * 将车队非自有车移到历史
     * @param tenantId
     * @param vehicleCode
     * @throws Exception
     */
    public void dealMoreVehicleInTenantForOwn(Long tenantId, long vehicleCode, String token) throws BusinessException {

        if (null == tenantId || vehicleCode < 0) {
            return;
        }

        List<TenantVehicleRel> list = iTenantVehicleRelService.doQueryTenantVehicleRel(tenantId, vehicleCode);//查询与本车队的关系
        List<TenantVehicleRel> pingTaiRelList = iTenantVehicleRelService.doQueryTenantVehicleRel(SysStaticDataEnum.PT_TENANT_ID, vehicleCode);//查询与平台的关系
        if (pingTaiRelList != null && pingTaiRelList.size() > 0) {
            list.addAll(pingTaiRelList);
        }

        if (null != list && list.size() > 0) {

            //先创建一份主表的历史记录信息
            VehicleDataInfoVer vehicleDataInfoVer = iVehicleDataInfoVerService.doAddVehicleDataInfoVerHis(vehicleCode);
            for (int i = 0; i < list.size(); i++) {
                TenantVehicleRel t = list.get(i);
                iVehicleDataInfoService.dissolveCooperationVehicle(t.getId(), vehicleDataInfoVer.getId(), token);
            }
        }
    }


    /***
     * 邀请成为外调车,招商车或挂靠车，车辆有归属车队
     * @param applyRecord
     * @throws Exception
     */
    @Override
    public void invitationiJoinTmp(ApplyRecord applyRecord, String token) throws BusinessException {

        VehicleDataInfo vehicleDataInfo = iVehicleDataInfoService.getById(applyRecord.getBusiId());
        if (null == vehicleDataInfo) {
            throw new BusinessException("车辆信息不存在！");
        }
        int authState = SysStaticDataEnum.AUTH_STATE.AUTH_STATE1;
        TenantVehicleRel vehicleRel = iTenantVehicleRelService.getTenantVehicleRel(vehicleDataInfo.getId(), applyRecord.getApplyTenantId());

        if (vehicleRel != null && vehicleRel.getVehicleClass().equals(applyRecord.getApplyVehicleClass())) {
            return;
        } else if (vehicleRel != null) {
            authState = SysStaticDataEnum.AUTH_STATE.AUTH_STATE2;//车辆在车队已经存在，只是转换一下类型，状态为已认证
        }
        SysTenantDef sysTenantDef = iSysTenantDefService.getSysTenantDef(applyRecord.getBeApplyTenantId(), true); //被邀请车队不可能为空

        //将此车辆在车队存在的其他类型移到历史
        dealMoreVehicleInTenant(applyRecord.getApplyTenantId(), applyRecord.getBusiId(), token);

        //邀请为挂靠车，先删除原有挂靠关系(移到车队的历史档案中)，再建立与新的车队的挂靠关系
        if (applyRecord.getApplyVehicleClass() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS4) {
            QueryWrapper<TenantVehicleRel> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("vehicle_code", vehicleDataInfo.getId()).eq("vehicle_class", SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS4);

            List<TenantVehicleRel> vehicleRelList = iTenantVehicleRelService.list(queryWrapper);
            if (vehicleRelList != null && vehicleRelList.size() > 0) {

                //先创建主表历史记录
                VehicleDataInfoVer vehicleDataInfoVerHis = iVehicleDataInfoVerService.doAddVehicleDataInfoVerHis(vehicleDataInfo.getId());

                for (TenantVehicleRel oldVehicleRel : vehicleRelList) {
                    //删除司机关系
                    if (oldVehicleRel.getDriverUserId() != null && oldVehicleRel.getDriverUserId() > 0) {
                        iVehicleDataInfoService.doRemoveDriverOnlyOneVehicle(oldVehicleRel.getDriverUserId(), oldVehicleRel.getTenantId());
                    }
                    //删除车辆
                    iVehicleDataInfoService.dissolveCooperationVehicle(oldVehicleRel.getId(), vehicleDataInfoVerHis.getId(), token);
                }
            }
        }

        //增加新的关系
        TenantVehicleRel tenantVehicleRel = new TenantVehicleRel();
        tenantVehicleRel.setVehicleCode(applyRecord.getBusiId());
        tenantVehicleRel.setTenantId(applyRecord.getApplyTenantId());
        tenantVehicleRel.setDriverUserId(applyRecord.getBelongDriverUserId());
        tenantVehicleRel.setVehicleClass(applyRecord.getApplyVehicleClass());
        tenantVehicleRel.setPlateNumber(vehicleDataInfo.getPlateNumber());
        tenantVehicleRel.setAuthState(authState);
        tenantVehicleRel.setIsAuth(SysStaticDataEnum.IS_AUTH.IS_AUTH1);
        tenantVehicleRel.setBillReceiverMobile(applyRecord.getBillReceiverMobile());
        tenantVehicleRel.setBillReceiverUserId(applyRecord.getBillReceiverUserId());
        tenantVehicleRel.setBillReceiverName(applyRecord.getBillReceiverName());
        tenantVehicleRel.setCreateTime(LocalDateTime.now());

        //招商挂靠车，账单接收人在系统不存在，则在系统生成一个收款人
        if (tenantVehicleRel.getBillReceiverUserId() == null && (tenantVehicleRel.getVehicleClass() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS2 || tenantVehicleRel.getVehicleClass() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS4)) {
            UserReceiverInfo receiverInfo = iUserReceiverInfoService.initUserReceiverInfo(tenantVehicleRel.getBillReceiverMobile(), tenantVehicleRel.getBillReceiverName(), token);
            tenantVehicleRel.setBillReceiverUserId(receiverInfo.getUserId());
        }
        iTenantVehicleRelService.save(tenantVehicleRel);
        TenantVehicleRelVer tenantVehicleRelVer = new TenantVehicleRelVer();
        BeanUtil.copyProperties(tenantVehicleRel, tenantVehicleRelVer);
        tenantVehicleRelVer.setRelId(tenantVehicleRel.getId());
        tenantVehicleRelVer.setVerState(SysStaticDataEnum.VER_STATE.VER_STATE_Y);
        tenantVehicleRelVer.setId(null);
        iTenantVehicleRelVerService.save(tenantVehicleRelVer);
        saveSysOperLog(SysOperLogConst.BusiCode.Vehicle, SysOperLogConst.OperType.Add, tenantVehicleRel.getPlateNumber() + "已加入车队!", token, tenantVehicleRel.getId());

        if (authState == SysStaticDataEnum.AUTH_STATE.AUTH_STATE1) {
            //启动车辆审核
            Map<String, Object> iMap = new HashMap<String, Object>();
            iMap.put("svName", "vehicleTF");
            iMap.put("tenantVehicleRelId", tenantVehicleRel.getId());
            boolean bool = iAuditService.startProcess(AuditConsts.AUDIT_CODE.AUDIT_CODE_VEHICLE, tenantVehicleRel.getId(), SysOperLogConst.BusiCode.Vehicle, iMap, token,applyRecord.getApplyTenantId());
            if (!bool) {
                throw new BusinessException("启动审核流程失败！");
            }
        }
        //增加司机
        addNewDriver(applyRecord, vehicleDataInfo, authState, token);
    }

    /***
     * 将车队非自有车移到历史
     * @param tenantId
     * @param vehicleCode
     * @throws Exception
     */
    public void dealMoreVehicleInTenant(Long tenantId, long vehicleCode, String token) throws BusinessException {

        if (null == tenantId || vehicleCode < 0) {
            return;
        }

        List<TenantVehicleRel> list = iTenantVehicleRelService.doQueryTenantVehicleRel(tenantId, vehicleCode);//查询与本车队的关系

        if (null != list && list.size() > 0) {

            //先创建一份主表的历史记录信息
            VehicleDataInfoVer vehicleDataInfoVer = iVehicleDataInfoVerService.doAddVehicleDataInfoVerHis(vehicleCode);

            for (int i = 0; i < list.size(); i++) {
                TenantVehicleRel t = list.get(i);
//				if (null != t.getDriverUserId() && t.getDriverUserId() > 0) {
//					//将原来车队的司机移到历史档案
//					doRemoveDriverOnlyOneVehicle(t.getDriverUserId(), t.getTenantId());
//				}
                iVehicleDataInfoService.dissolveCooperationVehicle(t.getId(), vehicleDataInfoVer.getId(), token);
            }
        }
    }


    /***
     * 自有车--有车主(转移自有车)
     * @param applyRecord
     * @throws Exception
     */
    @Override
    public void transferTenant(ApplyRecord applyRecord, String token){
        LoginInfo loginInfo = loginUtils.get(token);
        VehicleDataInfo vehicleDataInfo = iVehicleDataInfoService.getById(applyRecord.getBusiId());
        if (null == vehicleDataInfo) {
            throw new BusinessException("车辆信息不存在！");
        }
        Long driverUserId = vehicleDataInfo.getDriverUserId();
        Long copilotDriverId = vehicleDataInfo.getCopilotDriverId();
        Long followDriverId = vehicleDataInfo.getFollowDriverId();
        //自有车判断多个租户同时申请逻辑(只允许转移给一个租户)
        TenantVehicleRel tenantVehicleRelObj = iTenantVehicleRelService.getZYVehicleByVehicleCode(applyRecord.getBusiId());
        if (null != tenantVehicleRelObj && null != tenantVehicleRelObj.getTenantId() && tenantVehicleRelObj.getTenantId().longValue() != applyRecord.getBeApplyTenantId().longValue()) {
            String tenantName = iSysTenantDefService.getSysTenantDef(tenantVehicleRelObj.getTenantId()).getName();
            throw new BusinessException("该自有车已经转移到车队：" + tenantName + ",请驳回该申请，并提示申请车队重新发起申请！");
        }
        //转移自有车需要将此车辆在车队存在的其他类型移到历史
        iVehicleDataInfoService.removeOwnCarAndNotifyOtherTenant(applyRecord.getBusiId(), token);
//        vehicleDataInfoMapper.doUpdateVehicleObjectLine(vehicleDataInfo.getId());//清空心愿线路
        vehicleDataInfoMapper.doUpdateVehicleLineRelByVehicleCode(vehicleDataInfo.getId());//清空绑定线路信息
        vehicleDataInfoMapper.doUpdateVehicleLineRelVerByVehicleCode(vehicleDataInfo.getId());
        iVehicleDataInfoService.removeApplyRecord(vehicleDataInfo.getId());

        //解除副驾关系
        vehicleDataInfo.setCopilotDriverId(null);
        //解除随车司机
        vehicleDataInfo.setFollowDriverId(null);
        vehicleDataInfo.setOperCerti(null);//运营证号
        vehicleDataInfo.setEtcCardNumber(null);//etc卡号
        //vehicleDataInfo.setEquipmentCode(null);//gps设备号
        vehicleDataInfo.setContactNumber(null);//随车手机
        //vehicleDataInfo.setDrivingLicense(null);//行驶证正本id
        //vehicleDataInfo.setAdriverLicenseCopy(null);//行驶证副本id
        //vehicleDataInfo.setOperCertiId(null);//运输证图片id
        //vehicleDataInfo.setDrivingLicenseOwner(null);//行驶证上所有人
        //  TODO   审核回显
//        vehicleDataInfo.setRentAgreementId(null);//车辆租赁协议
//        vehicleDataInfo.setRentAgreementUrl(null);//车辆租赁协议URL
        //解绑ETC
        iEtcMaintainService.untieEtc(applyRecord.getBeApplyTenantId(), vehicleDataInfo.getPlateNumber());
        if (null != applyRecord.getDrivingLicense() && CommonUtil.isNumber(applyRecord.getDrivingLicense())) {
            SysAttach sysAttach = iSysAttachService.getById(Long.parseLong(applyRecord.getDrivingLicense()));
            vehicleDataInfo.setDrivingLicenseUrl(sysAttach.getStorePath());
        }
        //新增关系表数据
        //历史遗留问题 operCerti运输证字段;operCertiId运输证图片;operCertiUrl运输证图片url;
        String operCertiId = applyRecord.getOperCerti();
        if (StringUtils.isNotEmpty(operCertiId) && CommonUtil.isNumber(operCertiId)) {
            vehicleDataInfo.setOperCertiId(new Long(operCertiId));
        }

        vehicleDataInfo.setDrivingLicense(applyRecord.getDrivingLicense());
        vehicleDataInfo.setTenantId(applyRecord.getApplyTenantId());
        vehicleDataInfo.setDriverUserId(applyRecord.getApplyDriverUserId());

        Map<String, Object> inMap = new HashMap<String, Object>();
        inMap.put("vehicleCode", vehicleDataInfo.getId());
        inMap.put("tenantId", applyRecord.getBeApplyTenantId());
        inMap.put("destVerState", SysStaticDataEnum.VER_STATE.VER_STATE_N);
        iVehicleDataInfoService.updateVehicleVerAllVerState("vehicle_data_info_ver", inMap);

        VehicleDataInfoVer vehicleDataInfoVer = new VehicleDataInfoVer();
        BeanUtil.copyProperties(vehicleDataInfo, vehicleDataInfoVer);
        vehicleDataInfoVer.setAdrivingLicenseCopy(vehicleDataInfo.getAdriverLicenseCopy());// 行驶证副本(图片保存编号)
        vehicleDataInfoVer.setOperCerti(vehicleDataInfo.getOperCerti());// 运营证
        vehicleDataInfoVer.setOperCertiId(vehicleDataInfo.getOperCertiId());//运营证图片ID(运输证)
        vehicleDataInfoVer.setVehicleCode(vehicleDataInfo.getId());
        vehicleDataInfoVer.setOperateValidityTime(vehicleDataInfo.getOperateValidityTime());// 营运证有效期
        vehicleDataInfoVer.setVehicleValidityTime(vehicleDataInfo.getVehicleValidityTime());//行驶证有效期
        vehicleDataInfoVer.setId(null);
        vehicleDataInfoVer.setVerState(SysStaticDataEnum.VER_STATE.VER_STATE_Y);
        vehicleDataInfoVer.setCreateTime(LocalDateTime.now());
        iVehicleDataInfoVerService.save(vehicleDataInfoVer);
        vehicleDataInfoVer.setTenantId(applyRecord.getBeApplyTenantId());
        vehicleDataInfoVer.setId(null);
        vehicleDataInfoVer.setCopilotDriverId(copilotDriverId);
        vehicleDataInfoVer.setFollowDriverId(followDriverId);
        iVehicleDataInfoVerService.save(vehicleDataInfoVer);

        TenantVehicleRel tenantVehicleRel = new TenantVehicleRel();
        tenantVehicleRel.setVehicleCode(applyRecord.getBusiId());
        tenantVehicleRel.setTenantId(applyRecord.getApplyTenantId());
        tenantVehicleRel.setDriverUserId(applyRecord.getApplyDriverUserId());

        tenantVehicleRel.setVehicleClass(applyRecord.getApplyVehicleClass());
        tenantVehicleRel.setPlateNumber(vehicleDataInfo.getPlateNumber());
        tenantVehicleRel.setVehiclePicture(vehicleDataInfo.getVehiclePicture() + "");

        //来源租户，用于自有司机迁移
        tenantVehicleRel.setSourceTenantId(applyRecord.getBeApplyTenantId());
        tenantVehicleRel.setAuthState(SysStaticDataEnum.AUTH_STATE.AUTH_STATE1);
        tenantVehicleRel.setOperCerti(applyRecord.getOperCerti());

        //将共享标识关闭
        tenantVehicleRel.setShareFlg(SysStaticDataEnum.SHARE_FLG.NO);

        vehicleDataInfoMapper.doDelVehicleOrderPositionInfo(tenantVehicleRel.getVehicleCode());
        iTenantVehicleRelService.save(tenantVehicleRel);
        //更新tenant_vehicle_rel_ver表的verState状态为0
        iVehicleDataInfoService.updateVehicleVerAllVerState("tenant_vehicle_rel_ver", inMap);

        TenantVehicleRelVer tenantVehicleRelVer = new TenantVehicleRelVer();
        BeanUtil.copyProperties(tenantVehicleRel, tenantVehicleRelVer);
        tenantVehicleRelVer.setVerState(SysStaticDataEnum.VER_STATE.VER_STATE_Y);
        tenantVehicleRelVer.setVehicleHisId(vehicleDataInfoVer.getId());
        tenantVehicleRelVer.setCreateTime(LocalDateTime.now());
        tenantVehicleRelVer.setRelId(tenantVehicleRel.getId());
        tenantVehicleRelVer.setId(null);
        iTenantVehicleRelVerService.save(tenantVehicleRelVer);
        tenantVehicleRelVer.setTenantId(applyRecord.getBeApplyTenantId());
        tenantVehicleRelVer.setDriverUserId(driverUserId);
        tenantVehicleRelVer.setId(null);
        iTenantVehicleRelVerService.save(tenantVehicleRelVer);
        //原车队信息
        TenantVehicleRel tenantVehicleRel1 = iTenantVehicleRelService.getTenantVehicleRel(applyRecord.getBusiId());
        iSysOperLogService.save(SysOperLogConst.BusiCode.Vehicle, tenantVehicleRel1.getId(),SysOperLogConst.OperType.Audit,
                tenantVehicleRel.getPlateNumber() + "加入车队自有车!",loginInfo.getName(),loginInfo.getId(),
                applyRecord.getApplyTenantId());
//        saveSysOperLog(SysOperLogConst.BusiCode.Vehicle, SysOperLogConst.OperType.Audit, tenantVehicleRel.getPlateNumber() + "加入车队自有车!", token, applyRecord.getApplyTenantId());
        Long tenantId = null;
        if (null != tenantVehicleRel1) {
            tenantId = applyRecord.getApplyTenantId();
            String tenantName = iSysTenantDefService.getSysTenantDef(tenantId).getName();
            iSysOperLogService.save(SysOperLogConst.BusiCode.Vehicle, tenantVehicleRel1.getId(),SysOperLogConst.OperType.Audit,
                    tenantVehicleRel.getPlateNumber() + "转移至" + tenantName + "车队自有车!",loginInfo.getName(),loginInfo.getId(),
                    applyRecord.getApplyTenantId());
//            saveSysOperLog(SysOperLogConst.BusiCode.Vehicle, SysOperLogConst.OperType.Audit, tenantVehicleRel.getPlateNumber() + "转移至" + tenantName + "车队自有车!", token, tenantVehicleRel1.getId());
        }
        iVehicleDataInfoService.updateById(vehicleDataInfo);
        //启动审核流程
        Map iMap = new HashMap();
        iMap.put("svName", "vehicleTF");
        iMap.put("tenantVehicleRelId", tenantVehicleRel.getId());
        //启动车辆审核
        boolean bool = iAuditService.startProcess(AuditConsts.AUDIT_CODE.AUDIT_CODE_VEHICLE, tenantVehicleRel.getId(), SysOperLogConst.BusiCode.Vehicle, iMap, token,tenantId);
        if (!bool) {
            throw new BusinessException("启动审核流程失败！");
        }
        //判断是内部转移还是外部邀请
        int authState = getAuthState(applyRecord);
        //在会员租户关系表新增自有司机类型记录
        addNewDriver(applyRecord, vehicleDataInfo, authState, token);
    }


    /**
     * 判断是内部转移还是外部邀请
     *
     * @param record
     * @return
     */
    public int getAuthState(ApplyRecord record) {

        QueryWrapper<TenantVehicleRel> queryWrapper = new QueryWrapper();
        queryWrapper.eq("vehicle_code", record.getBusiId());
        queryWrapper.eq("tenant_id", record.getApplyTenantId());
        List<TenantVehicleRel> vehicleRelList = iTenantVehicleRelService.list(queryWrapper);
        int authState = SysStaticDataEnum.AUTH_STATE.AUTH_STATE1;
        if (vehicleRelList != null && vehicleRelList.size() > 0) {//车队内邀请转移类型
            authState = SysStaticDataEnum.AUTH_STATE.AUTH_STATE2;
        }

        return authState;
    }

    private void saveSysOperLog(SysOperLogConst.BusiCode busiCode, SysOperLogConst.OperType operType, String operCommet, String accessToken, Long busid) {
        SysOperLog operLog = new SysOperLog();
        operLog.setBusiCode(busiCode.getCode());
        operLog.setBusiName(busiCode.getName());
        operLog.setBusiId(busid);
        operLog.setOperType(operType.getCode());
        operLog.setOperTypeName(operType.getName());
        operLog.setOperComment(operCommet);
        iSysOperLogService.save(operLog, accessToken);
    }

    /**
     * 增加邀请司机
     *
     * @param applyRecord
     * @param vehicleDataInfo
     * @param authState
     * @throws Exception
     */
    public void addNewDriver(ApplyRecord applyRecord, VehicleDataInfo vehicleDataInfo, int authState, String token) throws BusinessException{

        if (applyRecord.getApplyDriverUserId() != null && applyRecord.getApplyDriverUserId() > 0) {

            UserDataInfo userDataInfo = iUserDataInfoRecordService.getById(applyRecord.getApplyDriverUserId());

            if (null == userDataInfo) {
                throw new BusinessException("接收司机信息不存在！");
            }

            //判断司机是否已经存在车队
            if (!existsDriver(applyRecord.getApplyDriverUserId(), applyRecord.getApplyTenantId())) {

                UserDataInfoVer userDataInfoVer = iUserDataInfoVerService.getUserDataInfoVerNoTenant(applyRecord.getApplyDriverUserId());
                if (userDataInfoVer != null) {
                    userDataInfoVer.setVerState(SysStaticDataEnum.VER_STATE.VER_STATE_N);
                    iUserDataInfoVerService.save(userDataInfoVer);
                }

                //转成自有司机，需要踢出去
                if (applyRecord.getApplyVehicleClass() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1) {
                    userDataInfo.setTenantId(applyRecord.getApplyTenantId());
                    iUserDataInfoRecordService.saveOrUpdate(userDataInfo);
                    kickUserEnds(vehicleDataInfo.getDriverUserId(), "APP");
                }

                //移动操作员
                SysUser sysOperator = iSysUserService.getSysUserByUserIdOrPhone(vehicleDataInfo.getDriverUserId(), null);
                if (sysOperator != null) {
                    sysOperator.setTenantId(applyRecord.getApplyTenantId());
                    iSysUserService.saveOrUpdate(sysOperator);
                }

                TenantUserRel tenantUserRel = new TenantUserRel();
                tenantUserRel.setUserId(applyRecord.getApplyDriverUserId());
                tenantUserRel.setTenantId(applyRecord.getApplyTenantId());
                tenantUserRel.setState(authState);
                tenantUserRel.setCarUserType(applyRecord.getApplyVehicleClass());
                tenantUserRel.setHasVer(EnumConsts.HAS_VER_STATE.HAS_VER_NO);
                tenantUserRel.setCreateDate(LocalDateTime.now());
                iTenantUserRelService.save(tenantUserRel);

                TenantUserRelVer tenantUserRelVer = new TenantUserRelVer();
                BeanUtil.copyProperties(tenantUserRel,tenantUserRelVer);
                tenantUserRelVer.setRelId(tenantUserRel.getId());
                tenantUserRelVer.setVerState(SysStaticDataEnum.VER_STATE.VER_STATE_Y);
                iTenantUserRelVerService.save(tenantUserRelVer);

                Map<String, Object> paramMap = new HashMap<String, Object>();
                if (null != tenantUserRelVer) {
                    paramMap.put("tenantUserRelId", tenantUserRelVer.getRelId());
                    paramMap.put("tenantUserRelCarUserType", tenantUserRelVer.getCarUserType());
                }
                //启动司机审核
                boolean boolD = iAuditService.startProcess(AuditConsts.AUDIT_CODE.AUDIT_CODE_USER, tenantUserRel.getId(), SysOperLogConst.BusiCode.Driver, paramMap, token,applyRecord.getApplyTenantId());
                if (!boolD) {
                    throw new BusinessException("启动审核流程失败！");
                }
            }
        }
    }


    private boolean existsDriver(Long driverUserId, Long tenantId) throws BusinessException {
        Integer count = vehicleDataInfoMapper.existsDriver(driverUserId, tenantId, -1L);
        return count > 0;

    }


    private boolean kickUserEnds(long userId, String channleType) {
        Set<Object> tokens = redisUtil.sgetAll("USER_TOKEN_MAP" + userId);
        if (!tokens.isEmpty()) {
            Iterator var4 = tokens.iterator();

            while (var4.hasNext()) {
                String tokenId = (String) var4.next();
                boolean isrem = false;
                if (StringUtils.isNotEmpty(tokenId) && tokenId.startsWith(channleType)) {
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
}
