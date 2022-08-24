package com.youming.youche.record.business.controller.vd;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.pagehelper.PageInfo;
import com.youming.youche.commons.base.BaseController;
import com.youming.youche.commons.constant.SysOperLogConst;
import com.youming.youche.commons.domain.ImportOrExportRecords;
import com.youming.youche.commons.domain.SysStaticData;
import com.youming.youche.commons.dto.AuditDto;
import com.youming.youche.commons.dto.BusinessIdDto;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.response.ResponseCode;
import com.youming.youche.commons.response.ResponseResult;
import com.youming.youche.commons.vo.VehicleApplyRecordVo;
import com.youming.youche.commons.web.Header;
import com.youming.youche.components.fdfs.FastDFSHelper;
import com.youming.youche.record.api.apply.IApplyRecordService;
import com.youming.youche.record.api.audit.IAuditCallBackService;
import com.youming.youche.record.api.tenant.ITenantVehicleRelService;
import com.youming.youche.record.api.user.IUserDataInfoRecordService;
import com.youming.youche.record.api.vehicle.IVehicleDataInfoService;
import com.youming.youche.record.api.vehicle.IVehicleIdleService;
import com.youming.youche.record.common.ChkIntfData;
import com.youming.youche.record.common.SysStaticDataEnum;
import com.youming.youche.record.domain.tenant.TenantVehicleRel;
import com.youming.youche.record.domain.vehicle.VehicleDataInfo;
import com.youming.youche.record.domain.vehicle.VehicleStaticData;
import com.youming.youche.record.dto.*;
import com.youming.youche.record.vo.*;
import com.youming.youche.system.api.ISysOperLogService;
import com.youming.youche.system.api.ISysStaticDataService;
import com.youming.youche.system.api.audit.IAuditService;
import com.youming.youche.system.api.ioer.ImportOrExportRecordsService;
import com.youming.youche.system.aspect.SysOperatorSaveLog;
import com.youming.youche.system.utils.excel.ExcelParse;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Date:2021/12/20
 */
@RestController
@RequestMapping("vehicle/data/info")
public class VehicleDataInfoController extends BaseController<VehicleDataInfo, IVehicleDataInfoService> {

    @DubboReference(version = "1.0.0")
    IVehicleDataInfoService iVehicleDataInfoService;

    @DubboReference(version = "1.0.0")
    IVehicleIdleService vehicleIdleService;

    @DubboReference(version = "1.0.0")
    ITenantVehicleRelService iTenantVehicleRelService;

    @DubboReference(version = "1.0.0")
    IApplyRecordService iApplyRecordService;


    @DubboReference(version = "1.0.0")
    IUserDataInfoRecordService iUserDataInfoRecordService;

    @Lazy
    @DubboReference(version = "1.0.0")
    ISysStaticDataService iSysStaticDataService;

    @DubboReference(version = "1.0.0")
    ImportOrExportRecordsService importOrExportRecordsService;

    @DubboReference(version = "1.0.0")
    ISysOperLogService sysOperLogService;

    @DubboReference(version = "1.0.0")
    IAuditService iAuditService;

    @DubboReference(version = "1.0.0")
    IAuditCallBackService iAuditCallBackService;

    private static final Logger LOGGER = LoggerFactory.getLogger(VehicleDataInfoController.class);


    @Override
    public IVehicleDataInfoService getService() {
        return iVehicleDataInfoService;
    }

    /**
     * 实现功能: 分页查询本车队所有司机以及所有平台司机
     *
     * @param linkman   司机姓名
     * @param loginAcct 司机账号
     * @param current   分页参数
     * @param size      分页参数
     * @return 本车队所有司机及所有平台司机
     */
    @GetMapping("doQueryCarDriver")
    public ResponseResult doQueryCarDriver(@RequestParam("linkman") String linkman,
                                           @RequestParam("loginAcct") String loginAcct,
                                           @RequestParam("pageNum") Integer current,
                                           @RequestParam("pageSize") Integer size) {
        try {
            String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
            Page<UserDataInfoBackVo> page = new Page<>(current, size);
            Page<UserDataInfoBackVo> page1 = iUserDataInfoRecordService.queryAllTenantDriverOrPtDriver(page, linkman, loginAcct, accessToken);
            return ResponseResult.success(page1);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseResult.failure("查询失败");
        }
    }

    /**
     * 实现功能:分页查询车辆信息
     *
     * @param plateNumber        车牌号码
     * @param linkman            司机
     * @param mobilePhone        司机手机
     * @param billReceiverMobile 车队手机
     * @param linkManName        车主联系人
     * @param linkPhone          车主手机
     * @param vehicleLength      车型(车长)
     * @param tenantName         车队名称
     * @param vehicleStatus      车型(类型)
     * @param startTime          注册时间开始
     * @param endTime            注册时间结束
     * @param authStateIn        认证状态
     * @param shareFlgIn         是否共享
     * @param isAuth             审核状态
     * @param vehicleClass       车辆类型
     * @param vehicleGps         定位类型
     * @param bdEffectDate       北斗购买车辆开始时间
     * @param bdInvalidDate      北斗购买车辆结束时间
     * @param pageNum            分页参数
     * @param pageSize           分页参数
     * @return
     */
    @GetMapping("doQueryVehicleAll")
    public ResponseResult doQueryVehicleAll(@RequestParam("plateNumber") String plateNumber,
                                            @RequestParam("linkman") String linkman,
                                            @RequestParam("mobilePhone") String mobilePhone,
                                            @RequestParam("billReceiverMobile") String billReceiverMobile,
                                            @RequestParam("linkManName") String linkManName,
                                            @RequestParam("linkPhone") String linkPhone,
                                            @RequestParam("vehicleLength") String vehicleLength,
                                            @RequestParam("tenantName") String tenantName,
                                            @RequestParam("vehicleStatus") Integer vehicleStatus,
                                            @RequestParam("startTime") String startTime,
                                            @RequestParam("endTime") String endTime,
                                            @RequestParam("authStateIn") Integer authStateIn,
                                            @RequestParam("shareFlgIn") Integer shareFlgIn,
                                            @RequestParam("isAuth") Integer isAuth,
                                            @RequestParam("vehicleClass") Integer vehicleClass,
                                            @RequestParam("vehicleGps") Integer vehicleGps,
                                            @RequestParam("bdEffectDate") String bdEffectDate,
                                            @RequestParam("bdInvalidDate") String bdInvalidDate,
                                            @RequestParam("pageNum") Integer pageNum,
                                            @RequestParam("pageSize") Integer pageSize) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        Page<TenantVehicleRelVo> tenantVehicleRelVoPage = iTenantVehicleRelService.doQueryVehicleAll(plateNumber, linkman, mobilePhone, billReceiverMobile, linkManName, linkPhone, vehicleLength,
                tenantName, vehicleStatus, startTime, endTime, authStateIn, shareFlgIn, isAuth, vehicleClass, vehicleGps, bdEffectDate, bdInvalidDate, accessToken, pageNum, pageSize);
        return ResponseResult.success(tenantVehicleRelVoPage);
    }

    /**
     * 实现功能:分页查询车辆信息
     *
     * @param isWorking    是否接单 用户是否接单:1不接单，2接单
     * @param plateNumber  车牌号码
     * @param linkman      司机
     * @param mobilePhone  司机手机
     * @param authStateIn  认证状态
     * @param vehicleGps   车辆类型
     * @param vehicleClass 车辆类别 1自有公司车 2招商挂靠车 3临时外调车 4外来挂靠车 5外调合同车
     * @param pageNum      分页参数
     * @param pageSize     分页参数
     * @return
     */
    @GetMapping("doQueryVehicleAllIsOrder")
    public ResponseResult doQueryVehicleAllIsOrder(@RequestParam("plateNumber") String plateNumber,
                                                   @RequestParam("linkman") String linkman,
                                                   @RequestParam("mobilePhone") String mobilePhone,
                                                   @RequestParam("vehicleGps") Integer vehicleGps,
                                                   @RequestParam("authStateIn") Integer authStateIn,
                                                   @RequestParam("isWorking") Integer isWorking,
                                                   @RequestParam("pageNum") Integer pageNum,
                                                   @RequestParam("pageSize") Integer pageSize,
                                                   @RequestParam("vehicleClass") Integer vehicleClass,
                                                   @RequestParam("orgId") Long orgId) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        Page<TenantVehicleRelVo> page = new Page<>(pageNum, pageSize);
        Page<TenantVehicleRelVo> tenantVehicleRelVoPage = iTenantVehicleRelService.doQueryVehicleAllIsOrder(isWorking, plateNumber, linkman, mobilePhone, null, null, null, null,
                null, null, null, null, authStateIn, null, null, vehicleClass, vehicleGps, null, null, accessToken, page, orgId);
        return ResponseResult.success(tenantVehicleRelVoPage);
    }


    /**
     * 实现功能: 修改车辆(自有车，招商车，外调车)
     *
     * @param vehicleInfoUptVo 车辆信息
     */
    @PostMapping("doUpdateVehicleInfo")
    public ResponseResult doUpdateVehicleInfo(@RequestBody VehicleInfoUptVo vehicleInfoUptVo) throws Exception {
        if (vehicleInfoUptVo.getVehicleClass() == null) {
            throw new BusinessException("车辆类型不能为空!");
        } else if (vehicleInfoUptVo.getVehicleClass().intValue() == 3) {
            //修改外调车
            if (vehicleInfoUptVo.getVehicleCode() == null) {
                throw new BusinessException("找不到车辆编码!");
            }
        } else {
            if (null == vehicleInfoUptVo.getRelId() || vehicleInfoUptVo.getRelId() <= 0) {
                throw new BusinessException("车辆租户关系数据不存在！");
            }
            if (null == vehicleInfoUptVo.getVehicleCode() || vehicleInfoUptVo.getVehicleCode() < 0) {
                throw new BusinessException("车辆主键错误！");
            }
            if (null == vehicleInfoUptVo.getTenantVehicleCostRelId() || vehicleInfoUptVo.getTenantVehicleCostRelId() < 0) {
//                throw new BusinessException("车辆费用错误！");
                vehicleInfoUptVo.setTenantVehicleCostRelId(-1L);
            }
            if (null == vehicleInfoUptVo.getTenantVehicleCertRelId() || vehicleInfoUptVo.getTenantVehicleCertRelId() < 0) {
//                throw new BusinessException("车辆证照主键错误！");
                vehicleInfoUptVo.setTenantVehicleCertRelId(-1L);
            }
        }
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));

        boolean s = iVehicleDataInfoService.doUpdateVehicleInfo(vehicleInfoUptVo, accessToken);
        return s ? ResponseResult.success(ResponseCode.UPDATE_SUCCESS) : ResponseResult.failure(ResponseCode.UPDATE_FAILURE);

    }

    /**
     * 修改外调车
     *
     * @param vehicleInfoUptVo 外调车信息
     */
    @PostMapping("doUpdateTransferCar")
    public ResponseResult doUpdateTransferCar(@RequestBody VehicleInfoUptVo vehicleInfoUptVo) throws Exception {

        if (vehicleInfoUptVo.getVehicleCode() == null || vehicleInfoUptVo.getVehicleCode() <= 0) {
            throw new BusinessException("找不到车辆编码!");
        }
        if (vehicleInfoUptVo.getVehicleClass() == null || vehicleInfoUptVo.getVehicleClass() <= 0) {
            throw new BusinessException("请选择车辆类型!");
        }

        if (vehicleInfoUptVo.getVehicleClass() == null) {
            throw new BusinessException("车辆类型不能为空!");
        }
        if (vehicleInfoUptVo.getVehicleClass().intValue() == 3) {
            //修改外调车
            if (vehicleInfoUptVo.getVehicleCode() == null) {
                throw new BusinessException("找不到车辆编码!");
            }
        } else {
            if (null == vehicleInfoUptVo.getRelId() || vehicleInfoUptVo.getRelId() <= 0) {
                throw new BusinessException("车辆租户关系数据不存在！");
            }
            if (null == vehicleInfoUptVo.getVehicleCode() || vehicleInfoUptVo.getVehicleCode() < 0) {
                throw new BusinessException("车辆主键错误！");
            }
            if (null == vehicleInfoUptVo.getTenantVehicleCostRelId() || vehicleInfoUptVo.getTenantVehicleCostRelId() < 0) {
//                throw new BusinessException("车辆费用错误！");
                vehicleInfoUptVo.setTenantVehicleCostRelId(-1L);
            }
            if (null == vehicleInfoUptVo.getTenantVehicleCertRelId() || vehicleInfoUptVo.getTenantVehicleCertRelId() < 0) {
//                throw new BusinessException("车辆证照主键错误！");
                vehicleInfoUptVo.setTenantVehicleCertRelId(-1L);
            }
        }
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));

        boolean s = iVehicleDataInfoService.doUpdateVehicleInfo(vehicleInfoUptVo, accessToken);
        return s ? ResponseResult.success(ResponseCode.UPDATE_SUCCESS) : ResponseResult.failure(ResponseCode.UPDATE_FAILURE);

    }

    /**
     * 实现功能: 新增车辆
     *
     * @param vehicleInfoVo 车辆信息
     */
    @PostMapping("doSaveVehicleInfo")
    public ResponseResult doSaveVehicleInfo(@RequestBody VehicleInfoVo vehicleInfoVo) throws Exception {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        AuditDto s = iVehicleDataInfoService.doSaveVehicleInfo(vehicleInfoVo, accessToken);
        if (null != s) {
            return iAuditService.startProcess(s) ? ResponseResult.success(ResponseCode.UPDATE_SUCCESS) : ResponseResult.failure(ResponseCode.UPDATE_FAILURE);
        }
        return ResponseResult.success(ResponseCode.UPDATE_SUCCESS);
    }

    /**
     * 模糊查询车辆信息
     *
     * @param plateNumber 车牌号
     * @param pageNum     分页参数
     * @param pageSize    分页参数
     * @return
     */
    @GetMapping("getVehicleDataInfo")
    public ResponseResult getVehicleDataInfo(@RequestParam("plateNumber") String plateNumber,
                                             @RequestParam("pageNum") Integer pageNum,
                                             @RequestParam("pageSize") Integer pageSize) {
        return ResponseResult.success(iVehicleDataInfoService.getVehicleDataInfoPlateNumber(pageNum, pageSize, plateNumber));
    }

    /**
     * 实现功能: 新增运营车辆
     *
     * @param obmsVehicleDataVo 新增运营车辆信息
     */
    @PostMapping("obms/save")
    public ResponseResult OBMSVehicleDataVo(@RequestBody @Valid OBMSVehicleDataVo obmsVehicleDataVo) throws Exception {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        checkParam(obmsVehicleDataVo);
        boolean save = iVehicleDataInfoService.save(obmsVehicleDataVo, accessToken);
        return save ? ResponseResult.success(ResponseCode.SAVE_SUCCESS) : ResponseResult.failure(ResponseCode.SAVE_FAILURE);
    }

    /**
     * 实现功能: 修改运营车辆
     *
     * @param obmsVehicleDataVo 运营车辆信息
     */
    @PutMapping("obms/update")
    public ResponseResult OBMSUpdate(@RequestBody @Valid OBMSVehicleDataVo obmsVehicleDataVo) throws Exception {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));

        checkParam(obmsVehicleDataVo);
        boolean save = iVehicleDataInfoService.update(obmsVehicleDataVo, accessToken);
        return save ? ResponseResult.success(ResponseCode.UPDATE_SUCCESS) : ResponseResult.failure(ResponseCode.UPDATE_FAILURE);
    }

    /**
     * 实现功能: 获取运营车辆详情
     *
     * @param vehicleCode 车牌编号
     * @return 运营车辆详情
     */
    @GetMapping("obms/{vehicleCode}")
    public ResponseResult OBMSget(@PathVariable Long vehicleCode) throws Exception {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        if (vehicleCode < 0) {
            throw new BusinessException("获取不到车辆的ID!");
        }
        return ResponseResult.success(iVehicleDataInfoService.obmsGet(vehicleCode, accessToken));
    }

    /**
     * 检测入参是否正确
     *
     * @param obmsVehicleDataVo 运营车辆信息
     */
    private void checkParam(@RequestBody @Valid OBMSVehicleDataVo obmsVehicleDataVo) {
        if (!ChkIntfData.chkPlateNumber(obmsVehicleDataVo.getPlateNumber())) {
            throw new BusinessException("请输入正确车牌号!");
        }

        if (obmsVehicleDataVo.getLicenceType() == SysStaticDataEnum.LICENCE_TYPE.ZC) {
            if (null == obmsVehicleDataVo.getVehicleLength() || Integer.parseInt(obmsVehicleDataVo.getVehicleLength()) <= 0) {
                throw new BusinessException("请选择车型!");
            }
            if (obmsVehicleDataVo.getVehicleStatus() <= 0) {
                throw new BusinessException("请选择车型!");
            }
            if (StringUtils.isEmpty(obmsVehicleDataVo.getVehicleLoad())) {
                throw new BusinessException("请输入载重!");
            }
            if (StringUtils.isEmpty(obmsVehicleDataVo.getLightGoodsSquare())) {
                throw new BusinessException("请输入容积!");
            }
        }
    }

    /**
     * 实现功能: 删除车辆
     *
     * @param vehicleCode         车辆编号
     * @param delOutVehicleDriver 解绑司机和车队的关系，判读是否存在邀请记录
     */
    @PostMapping("doRemoveVehicle")
    public ResponseResult doRemoveVehicle(@RequestParam("vehicleCode") Long vehicleCode,
                                          @RequestParam("delOutVehicleDriver") Boolean delOutVehicleDriver) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        boolean del = iVehicleDataInfoService.doRemoveVehicle(vehicleCode, delOutVehicleDriver, accessToken);
        return del ? ResponseResult.success(ResponseCode.DEL_SUCCESS) : ResponseResult.failure(ResponseCode.DEL_FAILURE);

    }

    /**
     * 实现功能: 审核车辆
     *
     * @param busiCode     业务编码
     * @param busiId       业务主键id
     * @param desc         审核描述
     * @param chooseResult 1 审核通过 2 审核不通过
     * @param instId       通过这个判断当前待审核的数据是否跟数据库一致
     */
    @PostMapping("sure")
    private ResponseResult sure(@RequestParam("busiCode") String busiCode,
                                @RequestParam("busiId") Long busiId,
                                @RequestParam("desc") String desc,
                                @RequestParam("chooseResult") Integer chooseResult,
                                @RequestParam("instId") Long instId) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));

        try {
            Boolean sure = iVehicleDataInfoService.sure(busiCode, busiId, desc, chooseResult, instId, accessToken);
            return ResponseResult.success(sure);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseResult.failure("删除异常");
        }
    }

    /**
     * 实现功能: 车辆历史档案列表
     *
     * @param plateNumber   车牌号码
     * @param linkman       司机
     * @param mobilePhone   司机手机
     * @param linkManName   车主联系人
     * @param linkPhone     车主手机
     * @param vehicleClass  车辆类型
     * @param vehicleLength 车型(车长)
     * @param vehicleStatus 车型(类型)
     */
    @GetMapping("doQueryVehicleAllHis")
    public ResponseResult doQueryVehicleAllHis(@RequestParam("plateNumber") String plateNumber,
                                               @RequestParam("linkManName") String linkManName,
                                               @RequestParam("tenantName") String tenantName,
                                               @RequestParam("linkPhone") String linkPhone,
                                               @RequestParam("linkman") String linkman,
                                               @RequestParam("mobilePhone") String mobilePhone,
                                               @RequestParam("vehicleLength") String vehicleLength,
                                               @RequestParam("vehicleStatus") Integer vehicleStatus,
                                               @RequestParam("vehicleClass") Integer vehicleClass,
                                               @RequestParam("pageNum") Integer pageNum,
                                               @RequestParam("pageSize") Integer pageSize) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        try {
            Page<HistoricalArchivesVo> page = new Page<>(pageNum, pageSize);
            Page<HistoricalArchivesVo> archivesVoPage = iVehicleDataInfoService.doQueryVehicleAllHis(plateNumber, linkManName, tenantName, linkPhone, linkman, mobilePhone,
                    vehicleLength, vehicleStatus, vehicleClass, page, accessToken);

            return ResponseResult.success(archivesVoPage);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseResult.failure("查询异常");
        }
    }

    /**
     * 实现功能: 我邀请的 和 邀请我的 数量接口
     */
    @GetMapping("getInvitationCount")
    public ResponseResult getInvitationCount() {
        try {
            String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
            InvitationDataVo invitationCount = iApplyRecordService.getInvitationCount(accessToken);
            return ResponseResult.success(invitationCount);
        } catch (Exception e) {
            LOGGER.error("查询我邀请的以及邀请我的数量异常" + e);
            return ResponseResult.failure("查询异常");
        }
    }

    /**
     * 实现功能: 查询邀请我和我邀请列表
     *
     * @param recordType        0我邀请的   1邀请我的
     * @param plateNumber       车牌号码
     * @param tenantName        归属车队
     * @param linkPhone         车队手机
     * @param applyVehicleClass 申请车辆类型
     * @param state             状态0 : 处理中  1 : 已通过 2：被驳回 3：平台仲裁中 4：平台仲裁通过 5：平台仲裁未通过
     * @param driverMobile      司机手机
     * @param driverName        司机姓名
     * @return
     */
    @GetMapping("doQueryVehicleApplyRecordAll")
    public ResponseResult doQueryVehicleApplyRecordAll(@RequestParam("recordType") Integer recordType,
                                                       @RequestParam("applyType") Integer applyType,
                                                       @RequestParam("plateNumber") String plateNumber,
                                                       @RequestParam("tenantName") String tenantName,
                                                       @RequestParam("linkPhone") String linkPhone,
                                                       @RequestParam("applyVehicleClass") Integer applyVehicleClass,
                                                       @RequestParam("state") Integer state,
                                                       @RequestParam("driverMobile") String driverMobile,
                                                       @RequestParam("driverName") String driverName,
                                                       @RequestParam("pageNum") Integer pageNum,
                                                       @RequestParam("pageSize") Integer pageSize) {
        try {
            String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
            Page<InvitationVo> page = new Page<>(pageNum, pageSize);
            Page<InvitationVo> voPage = iApplyRecordService.doQueryVehicleApplyRecordAll(recordType, applyType, plateNumber, tenantName, linkPhone, applyVehicleClass, state,
                    driverMobile, driverName, page, accessToken);
            return ResponseResult.success(voPage);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseResult.failure("查询异常");
        }
    }

    /**
     * 实现功能:我的邀请查看车辆详情
     *
     * @param vehicleCode 车辆编号
     */
    @GetMapping("getShareVehicle")
    public ResponseResult getShareVehicle(@RequestParam("vehicleCode") Long vehicleCode) {

        try {
            String accessToken = Header.getAuthorization(request.getHeader("Authorization"));

            VehicleDataInfoVo shareVehicle = iVehicleDataInfoService.getShareVehicle(vehicleCode, accessToken);
            if (shareVehicle != null) {
                return ResponseResult.success(shareVehicle);
            } else {
                return ResponseResult.success("数据为空");
            }

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseResult.failure("查询异常");
        }
    }

    /**
     * 实现功能: 邀请列表查询操作详情
     *
     * @param applyRecordId 申请记录主键id
     */
    @GetMapping("getVehicleInviteRecord")
    public ResponseResult getVehicleInviteRecord(@RequestParam("applyRecordId") Long applyRecordId) {
        VehicleApplyRecordVo vehicleInviteRecord = iVehicleDataInfoService.getVehicleInviteRecord(applyRecordId);
        return ResponseResult.success(vehicleInviteRecord);
    }

    /**
     * 实现功能: 邀请列表查询操作详情
     *
     * @param vehicleCode 车辆编号
     * @return 心愿线路信息
     */
    @GetMapping("getVehicleObjectLine/{vehicleCode}")
    public ResponseResult getVehicleObjectLine(@PathVariable("vehicleCode") Long vehicleCode) {
        List<VehicleObjectLineVo> vehicleObjectLines = iVehicleDataInfoService.getVehicleObjectLines(vehicleCode);
        return ResponseResult.success(vehicleObjectLines);
    }

    /**
     * 实现功能: 根据车辆id(vehicleCode)查询车辆信息
     *
     * @param vehicleClass 车辆类型
     * @param vehicleCode  车辆编号
     * @param plateNumbers 车牌号
     */
    @GetMapping("getAllVehicleInfo")
    public ResponseResult getAllVehicleInfo(@RequestParam("vehicleClass") Integer vehicleClass,
                                            @RequestParam("vehicleCode") Long vehicleCode,
                                            @RequestParam("plateNumbers") String plateNumbers) {

        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        VehicleInfoDto allVehicleInfo = iVehicleDataInfoService.getAllVehicleInfo
                (vehicleClass, vehicleCode, plateNumbers, accessToken);
        return ResponseResult.success(allVehicleInfo);

    }

    /**
     * 被邀请车辆 详情
     *
     * @param vehicleClass 车辆类型
     * @param vehicleCode  车辆编号
     * @param plateNumbers 车牌号
     */
    @GetMapping("getAllVehicleInfoVerHis")
    public ResponseResult getAllVehicleInfoVerHis(@RequestParam("vehicleClass") Integer vehicleClass,
                                                  @RequestParam("vehicleCode") Long vehicleCode,
                                                  @RequestParam("plateNumbers") String plateNumbers) {

        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        VehicleInfoDto allVehicleInfo = iVehicleDataInfoService.getAllVehicleInfoVerHis
                (vehicleClass, vehicleCode, plateNumbers, accessToken);
        return ResponseResult.success(allVehicleInfo);

    }

    /**
     * 实现功能: 查询车辆历史记录详情
     *
     * @param vehicleCode  车辆id
     * @param verState     1查可审核数据  9查历史数据
     * @param plateNumbers 车牌
     */
    @GetMapping("getAllVehicleInfoVer")
    public ResponseResult getAllVehicleInfoVer(@RequestParam("vehicleCode") Long vehicleCode,
                                               @RequestParam("verState") Integer verState,
                                               @RequestParam("plateNumbers") String plateNumbers) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        Map<String, Object> vehicleInfoVer = iVehicleDataInfoService.getAllVehicleInfoVer(vehicleCode, verState, plateNumbers, accessToken);
        return ResponseResult.success(vehicleInfoVer);
    }

    /**
     * 实现功能: 获取修改记录,用于审核显示
     *
     * @param vehicleCode 车辆id
     * @param plateNumber 车牌
     * @return
     */
    @GetMapping("getAllVehicleInfoVerAudit/{vehicleCode}/{plateNumber}")
    public ResponseResult getAllVehicleInfoVerAudit(@PathVariable("vehicleCode") Long vehicleCode,
                                                    @PathVariable("plateNumber") String plateNumber) {
        if (vehicleCode <= 0 && vehicleCode != -1) {
            throw new BusinessException("车辆主键参数错误！");
        }
        if (StringUtils.isNotEmpty(plateNumber) && !ChkIntfData.chkPlateNumber(plateNumber)) {
            throw new BusinessException("车牌号码错误！");
        }
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        Map<String, Object> vehicleInfoVer = iVehicleDataInfoService.getAllVehicleInfoVer(vehicleCode, SysStaticDataEnum.VER_STATE.VER_STATE_Y, plateNumber, accessToken);

        return ResponseResult.success(vehicleInfoVer);
    }

    /**
     * 实现功能: 获取车辆的注册信息
     *
     * @param plateNumber    车牌号
     * @param vehicleClassIn 车辆类型
     */
    @GetMapping("getVehicleIsRegister")
    public ResponseResult getVehicleIsRegister(@RequestParam(value = "plateNumber", required = false) String plateNumber,
                                               @RequestParam("vehicleClassIn") Integer vehicleClassIn) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        if (vehicleClassIn == null || vehicleClassIn < 0) {
            vehicleClassIn = 1;
        }
        Map<String, Object> map = iVehicleDataInfoService.getVehicleIsRegister(plateNumber, vehicleClassIn, accessToken);
        return ResponseResult.success(map == null ? "N" : map);
    }

    /**
     * 实现功能: 获取车辆的注册信息
     *
     * @param plateNumber    车牌号
     * @param vehicleClassIn 车辆类型
     */
    @GetMapping("getVehicleIsRegisterDatainfo")
    public ResponseResult getVehicleIsRegisterDatainfo(@RequestParam(value = "plateNumber", required = false) String plateNumber,
                                                       @RequestParam("vehicleClassIn") Integer vehicleClassIn) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        if (vehicleClassIn == null || vehicleClassIn < 0) {
            vehicleClassIn = 1;
        }
        Map<String, Object> map = iVehicleDataInfoService.getVehicleIsRegisterDatainfo(plateNumber, vehicleClassIn, accessToken);
        return ResponseResult.success(map == null ? "N" : map);
    }

    /**
     * 实现功能: 根据手机号查询用户名称
     *
     * @param mobile 接收人手机号
     * @param flag   1-招商车，2-挂靠车
     * @return
     */
    @GetMapping("queryUserInfoByMobile")
    public ResponseResult queryUserInfoByMobile(@RequestParam("mobile") String mobile,
                                                @RequestParam("flag") Integer flag) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        Map<String, String> map = iUserDataInfoRecordService.queryUserInfoByMobile(mobile, flag, accessToken);
        return ResponseResult.success(map);
    }

    /**
     * 获取车辆车型
     */
    @GetMapping("queryVehicleStatusWithLength")
    public ResponseResult queryVehicleStatusWithLength(@RequestParam(value = "orderFlag", defaultValue = "true") Boolean orderFlag) {
        try {
            List<VehicleStaticData> vehicleStaticDataList = iVehicleDataInfoService.getVehicleStatusWithLength(orderFlag);
            return ResponseResult.success(vehicleStaticDataList);
        } catch (Exception e) {
            return ResponseResult.failure(e);
        }
    }

    /**
     * 查询车辆长度的静态数据列表
     */
    @GetMapping("getStaticDataOption")
    public ResponseResult getStaticDataOption(@RequestParam("codeType") String codeType) {
        List<SysStaticData> sysStaticDataListByCodeName = iSysStaticDataService.getSysStaticDataListByCodeName(codeType);
        return ResponseResult.success(sysStaticDataListByCodeName);
    }

    /**
     * 删除车辆
     *
     * @param Id 车辆编号、
     */
    @SysOperatorSaveLog(code = SysOperLogConst.BusiCode.Trailer, type = SysOperLogConst.OperType.Del, comment = "挂车档案移除")
    @Override
    public ResponseResult remove(@PathVariable Long Id) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        Long deleted = iVehicleDataInfoService.remove(Id, accessToken);
        return deleted != 0 ? ResponseResult.success(BusinessIdDto.of().setId(Id))
                : ResponseResult.failure(ResponseCode.INTERFACE_ADDRESS_INVALID);
    }

    /**
     * 申请转移
     *
     * @param applyRecorDto
     * @return
     */
    @PostMapping("doSaveApplyRecord")
    public ResponseResult doSaveApplyRecord(@RequestBody ApplyRecorDto applyRecorDto) {

        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        applyRecorDto.setApplyState(SysStaticDataEnum.APPLY_STATE.APPLY_STATE0);
        boolean applyRecord = false;
        applyRecord = iVehicleDataInfoService.doSaveApplyRecord(applyRecorDto, accessToken);
        return applyRecord ? ResponseResult.success("邀请成功") : ResponseResult.failure();

    }

    /***
     *新增已注册车辆(邀请成为自有车/招商车/外调车  或者 申请转移自有车)
     * 入参：
     *      vehicleCode 车辆编号
     *      applyState  申请类型    0普通申请  3平台仲裁申请
     *      vehicleClass   车辆类型
     *      plateNumber        车牌号码
     *      tenantId            被邀请租户ID
     *      applyRemark        申请备注
     *      applyFileId        附件ID
     *      drivingLicense        行驶证图片ID
     *      operCerti        运营证图片ID
     *      applyDriverUserId        申请转移自有车接收司机id
     *      applyRemark        申请说明
     *      applyRecordId        邀请记录主键ID ， 再次邀请或者申请平台仲裁才有
     */
    @PostMapping("doSaveApplyRecordForOwnCar")
    public ResponseResult doSaveApplyRecordForOwnCar(@RequestBody ApplyRecorDto applyRecorDto) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        Boolean applyRecordForOwnCar = iVehicleDataInfoService.doSaveApplyRecordForOwnCar(applyRecorDto, accessToken);
        return applyRecordForOwnCar ? ResponseResult.success("邀请成功") : ResponseResult.failure();
    }

    /**
     * 共享车库列表查询
     *
     * @param vehicleDataInfoiDto 列表查询条件
     * @param pageNum             分页参数
     * @param pageSize            分页参数
     */
    @GetMapping("doQueryAllShareVehicle")
    public ResponseResult doQueryAllShareVehicle(VehicleDataInfoiDto vehicleDataInfoiDto,
                                                 @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                                 @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        Page<VehicleDataInfoiVo> page = iVehicleDataInfoService.doQueryAllShareVehicle(vehicleDataInfoiDto, pageNum, pageSize);
        return ResponseResult.success(page);
    }

    /**
     * 实现功能: 运行端查询列量列表
     *
     * @param startTime     注册开始时间
     * @param endTime       注册时间结束
     * @param plateNumber   车牌号码
     * @param linkman       司机
     * @param mobilePhone   司机手机
     * @param tenantName    车队名称
     * @param linkManName   车队联系人
     * @param linkPhone     车主手机
     * @param vehicleLength 车型(车长)
     * @param vehicleStatus 车型(类型)
     * @param authStateIn   认证状态
     * @param shareFlgIn    是否共享
     * @return
     */
    @GetMapping("doQueryVehicleAllDis")
    public ResponseResult doQueryVehicleAllDis(@RequestParam("startTime") String startTime,
                                               @RequestParam("authStateIn") Integer authStateIn,
                                               @RequestParam("shareFlgIn") Integer shareFlgIn,
                                               @RequestParam("isAuth") Integer isAuth,
                                               @RequestParam("endTime") String endTime,
                                               @RequestParam("plateNumber") String plateNumber,
                                               @RequestParam("linkManName") String linkManName,
                                               @RequestParam("linkPhone") String linkPhone,
                                               @RequestParam("linkman") String linkman,
                                               @RequestParam("mobilePhone") String mobilePhone,
                                               @RequestParam("tenantName") String tenantName,
                                               @RequestParam("vehicleLength") String vehicleLength,
                                               @RequestParam("vehicleStatus") Integer vehicleStatus,
                                               @RequestParam("pageNum") Integer pageNum,
                                               @RequestParam("pageSize") Integer pageSize) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        PageInfo<VehicleDataVo> page1 = iTenantVehicleRelService.doQueryVehicleAllDis(authStateIn, shareFlgIn, isAuth, startTime, endTime, plateNumber,
                linkManName, linkPhone, linkman, mobilePhone, tenantName, vehicleLength, vehicleStatus, accessToken,pageNum,pageSize);
        return ResponseResult.success(page1);
    }

    /**
     * 运营端车辆审核
     *
     * @return
     * @throws Exception
     */
    @PostMapping("doAuditInfo")
    public ResponseResult doAuditInfo(@RequestBody DoAuditInfoVo doAuditInfoVo) throws Exception {

        if (doAuditInfoVo == null) {
            throw new BusinessException("参数错误");
        }
        if (doAuditInfoVo.getVehicleCode() == null) {
            throw new BusinessException("获取不到车辆的ID!");
        }
        if (doAuditInfoVo.getAuthState() == null) {
            throw new BusinessException("请选择是否通过!");
        }
        if (doAuditInfoVo.getAuthState() < 0) {
            throw new BusinessException("请选择是否通过!");
        }
        if (doAuditInfoVo.getAuthState() == 3 && (doAuditInfoVo.getAuditContent() == null || StringUtils.isEmpty(doAuditInfoVo.getAuditContent()))) {
            throw new BusinessException("审核不通过请填写审核原因!");
        }

        return ResponseResult.success();
    }

    /**
     * 实现功能: 获取车辆相关联的司机
     *
     * @param plateNumber 车牌编号
     */
    @GetMapping("getVehicleDriver")
    public ResponseResult getVehicleDriver(@RequestParam("plateNumber") String plateNumber) {
        List<VehicleDriverVo> vehicleDriver = iVehicleDataInfoService.getVehicleDriver(plateNumber);
        return ResponseResult.success(vehicleDriver);
    }

    /**
     * 实现功能: 运营端查询挂靠人信息
     *
     * @param mobile 手机号
     * @param flag   1-招商车，2-挂靠车
     * @return
     */
    @GetMapping("queryBillReceiverName")
    public ResponseResult queryBillReceiverName(@RequestParam("mobile") String mobile,
                                                @RequestParam("flag") Integer flag) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        UserDataInfoBackVo userDataInfoBackVo = iVehicleDataInfoService.queryBillReceiverName(mobile, flag, accessToken);
        return ResponseResult.success(userDataInfoBackVo);
    }


    /**
     * 车辆信息导出
     */
    @PostMapping("downloadExcelFile")
    public ResponseResult downloadExcelFile(HttpServletResponse response, @RequestBody VehicleDataInfoExcelDto vehicleDataInfoExcelDto) {
        try {
            String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
            ImportOrExportRecords record = new ImportOrExportRecords();
            record.setName("车辆信息导出");
            record.setBussinessType(2);
            record.setState(1);
            record = importOrExportRecordsService.saveRecords(record, accessToken);
            iTenantVehicleRelService.acquireExcelFile(record, vehicleDataInfoExcelDto.getPlateNumber(), vehicleDataInfoExcelDto.getLinkman(), vehicleDataInfoExcelDto.getMobilePhone(), vehicleDataInfoExcelDto.getBillReceiverMobile(), vehicleDataInfoExcelDto.getLinkManName(), vehicleDataInfoExcelDto.getLinkPhone(), vehicleDataInfoExcelDto.getVehicleLength(),
                    vehicleDataInfoExcelDto.getTenantName(), vehicleDataInfoExcelDto.getVehicleStatus(), vehicleDataInfoExcelDto.getStartTime(), vehicleDataInfoExcelDto.getEndTime(), vehicleDataInfoExcelDto.getAuthStateIn(), vehicleDataInfoExcelDto.getShareFlgIn(), vehicleDataInfoExcelDto.getIsAuth(), vehicleDataInfoExcelDto.getVehicleClass(), vehicleDataInfoExcelDto.getVehicleGps(), vehicleDataInfoExcelDto.getBdEffectDate(), vehicleDataInfoExcelDto.getBdInvalidDate(), vehicleDataInfoExcelDto.getFieldName(), accessToken);
            return ResponseResult.success("正在导出文件,您可以在“系统设置-导入导出结果查询”查看导出结果");
        } catch (Exception e) {
            return ResponseResult.failure("导出失败");
        }
    }

    /***
     * 导入管理/会员(司机)档案
     */
    @PostMapping("import")
    public ResponseResult driverImport(@RequestParam("file") MultipartFile file) {
        try {
            String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
            ExcelParse parse = new ExcelParse();
            parse.loadExcel(file.getOriginalFilename(), file.getInputStream());
            // 获取真实行数
            int sheetNo = 1;
            int rows = parse.getRealRowCount(sheetNo);
            if (rows > 100) {
                throw new BusinessException("最多一次性导入100条数据");
            }
            ImportOrExportRecords record = new ImportOrExportRecords();
            //上传文件
            FastDFSHelper client = FastDFSHelper.getInstance();
            String mediaExcelPath = client.upload(file.getInputStream(), "车辆信息.xlsx", file.getSize());
            record.setMediaUrl(mediaExcelPath);
            record.setName("车辆档案导入");
            record.setMediaName(file.getOriginalFilename());
            record.setBussinessType(1);
            record.setState(1);
            record = importOrExportRecordsService.saveRecords(record, accessToken);
            iVehicleDataInfoService.deal(file.getBytes(), record, accessToken);
            return ResponseResult.success("正在导出文件,您可以在“系统设置-导入导出结果查询”查看导出结果");
        } catch (Exception e) {
            LOGGER.error("(车辆)档案列表查询异常" + e);
            return ResponseResult.failure("查询异常");
        }
    }

    //获取流文件
    private void inputStreamToFile(InputStream ins, File file) {
        try {
            OutputStream os = new FileOutputStream(file);
            int bytesRead = 0;
            byte[] buffer = new byte[8192];
            while ((bytesRead = ins.read(buffer, 0, 8192)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            os.close();
            ins.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 实现功能: 启动闲置
     *
     * @param flag 1，闲置、 其他，启动
     * @param vid  挂车id
     */
    @PostMapping("doSaveIdle")
    public ResponseResult doSaveIdle(@RequestParam(value = "flag", defaultValue = "0") Short flag, @RequestParam("vid") Long vid) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        TenantVehicleRel tenantVehicleRel = iTenantVehicleRelService.getTenantVehicleRel(vid);
        boolean s = iVehicleDataInfoService.updateVehicleDataInfo(flag, vid, tenantVehicleRel.getPlateNumber(), accessToken);
        String vehicleClasName = "";
        if (tenantVehicleRel.getVehicleClass() == 1) {
            vehicleClasName = "自有车-";
        } else if (tenantVehicleRel.getVehicleClass() == 2) {
            vehicleClasName = "招商车-";
        } else if (tenantVehicleRel.getVehicleClass() == 3) {
            vehicleClasName = "临时外调车-";
        } else {
            vehicleClasName = "自有车-";
        }
        if (flag == 1) {
            sysOperLogService.save(SysOperLogConst.BusiCode.Vehicle, tenantVehicleRel.getId(),
                    SysOperLogConst.OperType.Idle, vehicleClasName + tenantVehicleRel.getPlateNumber() + "-闲置", accessToken);
        } else {
            sysOperLogService.save(SysOperLogConst.BusiCode.Vehicle, tenantVehicleRel.getId(),
                    SysOperLogConst.OperType.Enable, vehicleClasName + tenantVehicleRel.getPlateNumber() + "-启用", accessToken);
        }
        return s ? ResponseResult.success("操作成功") : ResponseResult.failure("操作失败");
    }

    /**
     * 获取当前部门下的所有车辆
     *
     * @param id 部门id
     */
    @GetMapping({"getAllByOrganize"})
    public ResponseResult getAllByOrganize(@RequestParam("id") Long id) {
        try {
            List<TenantVehicleRel> vehicleByOrganize = iVehicleDataInfoService.getVehicleByOrganize(id);
            return ResponseResult.success(vehicleByOrganize);
        } catch (Exception e) {
            LOGGER.error("部门下车辆列表查询异常" + e);
            return ResponseResult.failure("查询异常");
        }
    }

    /**
     * 40012 查询车辆信息
     *
     * @param plateNumber 车牌号
     */
    @GetMapping("getVehicleByPlateNumberVx")
    public ResponseResult getVehicleByPlateNumberVx(@RequestParam("plateNumber") String plateNumber) {
        return ResponseResult.success(
                iVehicleDataInfoService.getVehicleByPlateNumberVx(plateNumber)
        );
    }

    /**
     * 14511 邀请信息审核-后台--车辆邀请列表--小程序接口
     *
     * @param plateNumber       车牌号
     * @param tenantName        车队名称
     * @param applyVehicleClass 车辆类型
     * @param states            申请状态
     * @param pageNum           分页参数
     * @param pageSize          分页参数
     * @return
     */
    @GetMapping("doQueryVehicleApplyRecordAllForMiniProgram")
    public ResponseResult doQueryVehicleApplyRecordAllForMiniProgram(String plateNumber, String tenantName, String applyVehicleClass, String states,
                                                                     @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                                                     @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {

        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        Page<InvitationVo> voPage = iApplyRecordService.doQueryVehicleApplyRecordAllWX(1, null, plateNumber,
                tenantName, null, applyVehicleClass, states,
                null, null, pageNum, pageSize, accessToken);

        return ResponseResult.success(voPage);

    }

    /**
     * 14005 当前车队 员工分页列表查询
     * 入参：
     * <ul>
     *     <li>loginAcct        账号（手机号）     </li>
     * 	   <li>linkman          姓名              </li>
     * 	   <li>employeeNumber   员工工号          </li>
     * 	   <li>staffPosition    职位              </li>
     * 	   <li>lockFlag         状态  1启用，2禁用 </li>
     * 	   <li>orgId            员工关联的部门</li>
     * </ul>
     */
    @GetMapping("queryStaffInfo")
    public ResponseResult queryStaffInfo(StaffDataInfoVo vo,
                                         @RequestParam(value = "pageNum", defaultValue = "1", required = false) Integer pageNum,
                                         @RequestParam(value = "pageSize", defaultValue = "20", required = false) Integer pageSize) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        Page<StaffDataInfoDto> staffDataInfoDtoPage = iUserDataInfoRecordService.queryStaffInfo(vo, pageNum, pageSize, accessToken);
        return ResponseResult.success(staffDataInfoDtoPage);
    }

    /**
     * 14510 在线接单-通过车牌号码查询车辆信息列表--微信接口
     *
     * @param plateNumber 车牌号
     */
    @PostMapping("getVehicleForMiniProgram")
    public ResponseResult getVehicleForMiniProgram(String plateNumber,
                                                   @RequestParam(value = "pageNum", defaultValue = "1", required = false) Integer pageNum,
                                                   @RequestParam(value = "pageSize", defaultValue = "20", required = false) Integer pageSize) {
        if (StringUtils.isBlank(plateNumber)) {
            throw new BusinessException("车牌号参数错误！");
        }
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        Page<VehicleDataInfoVxVo> vehiclePage = iVehicleDataInfoService.getVehiclePage(new Page<VehicleDataInfoVxVo>(pageNum, pageSize), plateNumber, accessToken);
        return ResponseResult.success(vehiclePage);
    }

    /**
     * 查询闲置车辆列表
     *
     * @return
     * @throws Exception
     */
    @GetMapping("queryIdleVehicles")
    public ResponseResult queryIdleVehicles(IdleVehiclesVo idleVehiclesVo) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        return ResponseResult.success(vehicleIdleService.queryIdleVehicles(idleVehiclesVo, accessToken));
    }

    /**
     * 14500 小程序新增车辆
     */
    @PostMapping("doSaveVehicle")
    public ResponseResult doSaveVehicle(@RequestBody SaveVehicleAppVo vo) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));

        iVehicleDataInfoService.doSaveVehicle(vo, accessToken);
        return ResponseResult.success();
    }

    /**
     * 14503 APP合作车队列表
     *
     * @param vehicleCode  车辆编码
     * @param driverUserId 司机编码
     */
    @GetMapping("doQueryTenantByVehicleCodeApp")
    public ResponseResult doQueryTenantByVehicleCodeApp(Long vehicleCode, Long driverUserId) {

        if (vehicleCode < 0) {
            throw new BusinessException("车辆编码错误！");
        }

        if (vehicleCode < 0) {
            throw new BusinessException("司机编码错误！");
        }

        return ResponseResult.success(
                iVehicleDataInfoService.doQueryTenantByVehicleCodeApp(vehicleCode, driverUserId)
        );

    }

    /**
     * 14501 小程序修改车辆
     */
    @PostMapping("doUpdateVehicle")
    public ResponseResult doUpdateVehicle(@RequestBody UpdateVehicleVo vo) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        iVehicleDataInfoService.doUpdateVehicle(vo, accessToken);
        return ResponseResult.success();
    }


    /**
     * 14502 删除车辆
     *
     * @param vehicleCodeString vehicleCodes 车辆编码(多个，逗号分隔)
     */
    @PostMapping("doRemoveVehicleWx")
    public ResponseResult doRemoveVehicleWx(@RequestBody String vehicleCodeString) {

        if (org.apache.commons.lang.StringUtils.isBlank(vehicleCodeString)) {
            throw new BusinessException("请选择需要删除的车辆！");
        }

        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        iVehicleDataInfoService.doRemoveVehicleWx(vehicleCodeString, accessToken);
        return ResponseResult.success();
    }

    /**
     * 14504 小程序退出车队
     *
     * @param relIds 车辆车队关系id (多个，逗号分隔)
     */
    @GetMapping("doQuitTenant")
    public ResponseResult doQuitTenant(String relIds) {
        if (StringUtils.isBlank(relIds)) {
            throw new BusinessException("请选择需要退出的车队！");
        }
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        iVehicleDataInfoService.doQuitTenant(relIds, accessToken);
        return ResponseResult.success();
    }

    /**
     * 14505 查看车辆
     *
     * @param vehicleCode 车辆编码
     */
    @GetMapping("getVehicleInfoApp")
    public ResponseResult getVehicleInfoApp(Long vehicleCode) {
        if (vehicleCode == null || vehicleCode < 0) {
            throw new BusinessException("车辆编码错误！");
        }

        return ResponseResult.success(
                iVehicleDataInfoService.getVehicleInfoApp(vehicleCode)
        );

    }

    /**
     * 14506 我的车辆列表
     *
     * @param driverUserId 司机编码
     */
    @GetMapping("doQueryVehicleByDriver")
    public ResponseResult doQueryVehicleByDriver(Long driverUserId,
                                                 @RequestParam(value = "pageNum", defaultValue = "1", required = false) Integer pageNum,
                                                 @RequestParam(value = "pageSize", defaultValue = "20", required = false) Integer pageSize) {
        if (driverUserId == null || driverUserId < 0) {
            throw new BusinessException("参数错误，司机编码错误！");
        }

        List<QueryVehicleByDriverDto> queryVehicleByDriverDtos = iVehicleDataInfoService.doQueryVehicleByDriver(driverUserId);

        List<QueryVehicleByDriverDto> collect = queryVehicleByDriverDtos.stream().skip((pageNum - 1) * pageSize).limit(pageSize).collect(Collectors.toList());

        Page<QueryVehicleByDriverDto> queryVehicleByDriverDtoPage = new Page<>();
        queryVehicleByDriverDtoPage.setRecords(collect);
        queryVehicleByDriverDtoPage.setTotal(queryVehicleByDriverDtos.size());
        queryVehicleByDriverDtoPage.setCurrent(pageNum);
        queryVehicleByDriverDtoPage.setSize(pageSize);

        return ResponseResult.success(
                queryVehicleByDriverDtoPage
        );

    }

    /**
     * 14507 邀请我的--成功接口--APP
     *
     * @param applyId 申请记录主键id
     * @param dec     备注
     */
    @PostMapping("applyRecordSuccess")
    public ResponseResult applyRecordSuccess(@RequestParam("applyId") Long applyId, @RequestParam("dec") String dec) {

        if (applyId == null || applyId < 0) {
            throw new BusinessException("数据ID错误！");
        }

        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        iAuditCallBackService.sucess(applyId, dec, null, accessToken);
        return ResponseResult.success();
    }

    /**
     * 14508 邀请我的--失败接口--APP
     *
     * @param applyId 申请记录主键id
     * @param dec     备注
     */
    @PostMapping("applyRecordFail")
    public ResponseResult applyRecordFail(@RequestParam("applyId") Long applyId, @RequestParam("dec") String dec) {

        if (applyId == null || applyId < 0) {
            throw new BusinessException("数据ID错误！");
        }

        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        iAuditCallBackService.fail(applyId, dec, null, accessToken);
        return ResponseResult.success();
    }

    /**
     * 14521 查询某个司机的所有自有车
     *
     * @param driverUserId 司机编号
     */
    @GetMapping("doQueryZYVehicleByDriver")
    public ResponseResult doQueryZYVehicleByDriver(Long driverUserId) {
        if (driverUserId == null || driverUserId < 0) {
            throw new BusinessException("参数错误，司机编码错误！");
        }

        return ResponseResult.success(
                iTenantVehicleRelService.getTenantVehicleRels(driverUserId, SysStaticDataEnum.CAR_USER_TYPE.OWN_CAR)
        );

    }

}
