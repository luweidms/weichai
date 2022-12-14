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
     * ????????????: ?????????????????????????????????????????????????????????
     *
     * @param linkman   ????????????
     * @param loginAcct ????????????
     * @param current   ????????????
     * @param size      ????????????
     * @return ??????????????????????????????????????????
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
            return ResponseResult.failure("????????????");
        }
    }

    /**
     * ????????????:????????????????????????
     *
     * @param plateNumber        ????????????
     * @param linkman            ??????
     * @param mobilePhone        ????????????
     * @param billReceiverMobile ????????????
     * @param linkManName        ???????????????
     * @param linkPhone          ????????????
     * @param vehicleLength      ??????(??????)
     * @param tenantName         ????????????
     * @param vehicleStatus      ??????(??????)
     * @param startTime          ??????????????????
     * @param endTime            ??????????????????
     * @param authStateIn        ????????????
     * @param shareFlgIn         ????????????
     * @param isAuth             ????????????
     * @param vehicleClass       ????????????
     * @param vehicleGps         ????????????
     * @param bdEffectDate       ??????????????????????????????
     * @param bdInvalidDate      ??????????????????????????????
     * @param pageNum            ????????????
     * @param pageSize           ????????????
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
     * ????????????:????????????????????????
     *
     * @param isWorking    ???????????? ??????????????????:1????????????2??????
     * @param plateNumber  ????????????
     * @param linkman      ??????
     * @param mobilePhone  ????????????
     * @param authStateIn  ????????????
     * @param vehicleGps   ????????????
     * @param vehicleClass ???????????? 1??????????????? 2??????????????? 3??????????????? 4??????????????? 5???????????????
     * @param pageNum      ????????????
     * @param pageSize     ????????????
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
     * ????????????: ????????????(?????????????????????????????????)
     *
     * @param vehicleInfoUptVo ????????????
     */
    @PostMapping("doUpdateVehicleInfo")
    public ResponseResult doUpdateVehicleInfo(@RequestBody VehicleInfoUptVo vehicleInfoUptVo) throws Exception {
        if (vehicleInfoUptVo.getVehicleClass() == null) {
            throw new BusinessException("????????????????????????!");
        } else if (vehicleInfoUptVo.getVehicleClass().intValue() == 3) {
            //???????????????
            if (vehicleInfoUptVo.getVehicleCode() == null) {
                throw new BusinessException("?????????????????????!");
            }
        } else {
            if (null == vehicleInfoUptVo.getRelId() || vehicleInfoUptVo.getRelId() <= 0) {
                throw new BusinessException("????????????????????????????????????");
            }
            if (null == vehicleInfoUptVo.getVehicleCode() || vehicleInfoUptVo.getVehicleCode() < 0) {
                throw new BusinessException("?????????????????????");
            }
            if (null == vehicleInfoUptVo.getTenantVehicleCostRelId() || vehicleInfoUptVo.getTenantVehicleCostRelId() < 0) {
//                throw new BusinessException("?????????????????????");
                vehicleInfoUptVo.setTenantVehicleCostRelId(-1L);
            }
            if (null == vehicleInfoUptVo.getTenantVehicleCertRelId() || vehicleInfoUptVo.getTenantVehicleCertRelId() < 0) {
//                throw new BusinessException("???????????????????????????");
                vehicleInfoUptVo.setTenantVehicleCertRelId(-1L);
            }
        }
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));

        boolean s = iVehicleDataInfoService.doUpdateVehicleInfo(vehicleInfoUptVo, accessToken);
        return s ? ResponseResult.success(ResponseCode.UPDATE_SUCCESS) : ResponseResult.failure(ResponseCode.UPDATE_FAILURE);

    }

    /**
     * ???????????????
     *
     * @param vehicleInfoUptVo ???????????????
     */
    @PostMapping("doUpdateTransferCar")
    public ResponseResult doUpdateTransferCar(@RequestBody VehicleInfoUptVo vehicleInfoUptVo) throws Exception {

        if (vehicleInfoUptVo.getVehicleCode() == null || vehicleInfoUptVo.getVehicleCode() <= 0) {
            throw new BusinessException("?????????????????????!");
        }
        if (vehicleInfoUptVo.getVehicleClass() == null || vehicleInfoUptVo.getVehicleClass() <= 0) {
            throw new BusinessException("?????????????????????!");
        }

        if (vehicleInfoUptVo.getVehicleClass() == null) {
            throw new BusinessException("????????????????????????!");
        }
        if (vehicleInfoUptVo.getVehicleClass().intValue() == 3) {
            //???????????????
            if (vehicleInfoUptVo.getVehicleCode() == null) {
                throw new BusinessException("?????????????????????!");
            }
        } else {
            if (null == vehicleInfoUptVo.getRelId() || vehicleInfoUptVo.getRelId() <= 0) {
                throw new BusinessException("????????????????????????????????????");
            }
            if (null == vehicleInfoUptVo.getVehicleCode() || vehicleInfoUptVo.getVehicleCode() < 0) {
                throw new BusinessException("?????????????????????");
            }
            if (null == vehicleInfoUptVo.getTenantVehicleCostRelId() || vehicleInfoUptVo.getTenantVehicleCostRelId() < 0) {
//                throw new BusinessException("?????????????????????");
                vehicleInfoUptVo.setTenantVehicleCostRelId(-1L);
            }
            if (null == vehicleInfoUptVo.getTenantVehicleCertRelId() || vehicleInfoUptVo.getTenantVehicleCertRelId() < 0) {
//                throw new BusinessException("???????????????????????????");
                vehicleInfoUptVo.setTenantVehicleCertRelId(-1L);
            }
        }
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));

        boolean s = iVehicleDataInfoService.doUpdateVehicleInfo(vehicleInfoUptVo, accessToken);
        return s ? ResponseResult.success(ResponseCode.UPDATE_SUCCESS) : ResponseResult.failure(ResponseCode.UPDATE_FAILURE);

    }

    /**
     * ????????????: ????????????
     *
     * @param vehicleInfoVo ????????????
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
     * ????????????????????????
     *
     * @param plateNumber ?????????
     * @param pageNum     ????????????
     * @param pageSize    ????????????
     * @return
     */
    @GetMapping("getVehicleDataInfo")
    public ResponseResult getVehicleDataInfo(@RequestParam("plateNumber") String plateNumber,
                                             @RequestParam("pageNum") Integer pageNum,
                                             @RequestParam("pageSize") Integer pageSize) {
        return ResponseResult.success(iVehicleDataInfoService.getVehicleDataInfoPlateNumber(pageNum, pageSize, plateNumber));
    }

    /**
     * ????????????: ??????????????????
     *
     * @param obmsVehicleDataVo ????????????????????????
     */
    @PostMapping("obms/save")
    public ResponseResult OBMSVehicleDataVo(@RequestBody @Valid OBMSVehicleDataVo obmsVehicleDataVo) throws Exception {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        checkParam(obmsVehicleDataVo);
        boolean save = iVehicleDataInfoService.save(obmsVehicleDataVo, accessToken);
        return save ? ResponseResult.success(ResponseCode.SAVE_SUCCESS) : ResponseResult.failure(ResponseCode.SAVE_FAILURE);
    }

    /**
     * ????????????: ??????????????????
     *
     * @param obmsVehicleDataVo ??????????????????
     */
    @PutMapping("obms/update")
    public ResponseResult OBMSUpdate(@RequestBody @Valid OBMSVehicleDataVo obmsVehicleDataVo) throws Exception {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));

        checkParam(obmsVehicleDataVo);
        boolean save = iVehicleDataInfoService.update(obmsVehicleDataVo, accessToken);
        return save ? ResponseResult.success(ResponseCode.UPDATE_SUCCESS) : ResponseResult.failure(ResponseCode.UPDATE_FAILURE);
    }

    /**
     * ????????????: ????????????????????????
     *
     * @param vehicleCode ????????????
     * @return ??????????????????
     */
    @GetMapping("obms/{vehicleCode}")
    public ResponseResult OBMSget(@PathVariable Long vehicleCode) throws Exception {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        if (vehicleCode < 0) {
            throw new BusinessException("?????????????????????ID!");
        }
        return ResponseResult.success(iVehicleDataInfoService.obmsGet(vehicleCode, accessToken));
    }

    /**
     * ????????????????????????
     *
     * @param obmsVehicleDataVo ??????????????????
     */
    private void checkParam(@RequestBody @Valid OBMSVehicleDataVo obmsVehicleDataVo) {
        if (!ChkIntfData.chkPlateNumber(obmsVehicleDataVo.getPlateNumber())) {
            throw new BusinessException("????????????????????????!");
        }

        if (obmsVehicleDataVo.getLicenceType() == SysStaticDataEnum.LICENCE_TYPE.ZC) {
            if (null == obmsVehicleDataVo.getVehicleLength() || Integer.parseInt(obmsVehicleDataVo.getVehicleLength()) <= 0) {
                throw new BusinessException("???????????????!");
            }
            if (obmsVehicleDataVo.getVehicleStatus() <= 0) {
                throw new BusinessException("???????????????!");
            }
            if (StringUtils.isEmpty(obmsVehicleDataVo.getVehicleLoad())) {
                throw new BusinessException("???????????????!");
            }
            if (StringUtils.isEmpty(obmsVehicleDataVo.getLightGoodsSquare())) {
                throw new BusinessException("???????????????!");
            }
        }
    }

    /**
     * ????????????: ????????????
     *
     * @param vehicleCode         ????????????
     * @param delOutVehicleDriver ???????????????????????????????????????????????????????????????
     */
    @PostMapping("doRemoveVehicle")
    public ResponseResult doRemoveVehicle(@RequestParam("vehicleCode") Long vehicleCode,
                                          @RequestParam("delOutVehicleDriver") Boolean delOutVehicleDriver) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        boolean del = iVehicleDataInfoService.doRemoveVehicle(vehicleCode, delOutVehicleDriver, accessToken);
        return del ? ResponseResult.success(ResponseCode.DEL_SUCCESS) : ResponseResult.failure(ResponseCode.DEL_FAILURE);

    }

    /**
     * ????????????: ????????????
     *
     * @param busiCode     ????????????
     * @param busiId       ????????????id
     * @param desc         ????????????
     * @param chooseResult 1 ???????????? 2 ???????????????
     * @param instId       ??????????????????????????????????????????????????????????????????
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
            return ResponseResult.failure("????????????");
        }
    }

    /**
     * ????????????: ????????????????????????
     *
     * @param plateNumber   ????????????
     * @param linkman       ??????
     * @param mobilePhone   ????????????
     * @param linkManName   ???????????????
     * @param linkPhone     ????????????
     * @param vehicleClass  ????????????
     * @param vehicleLength ??????(??????)
     * @param vehicleStatus ??????(??????)
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
            return ResponseResult.failure("????????????");
        }
    }

    /**
     * ????????????: ???????????? ??? ???????????? ????????????
     */
    @GetMapping("getInvitationCount")
    public ResponseResult getInvitationCount() {
        try {
            String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
            InvitationDataVo invitationCount = iApplyRecordService.getInvitationCount(accessToken);
            return ResponseResult.success(invitationCount);
        } catch (Exception e) {
            LOGGER.error("????????????????????????????????????????????????" + e);
            return ResponseResult.failure("????????????");
        }
    }

    /**
     * ????????????: ?????????????????????????????????
     *
     * @param recordType        0????????????   1????????????
     * @param plateNumber       ????????????
     * @param tenantName        ????????????
     * @param linkPhone         ????????????
     * @param applyVehicleClass ??????????????????
     * @param state             ??????0 : ?????????  1 : ????????? 2???????????? 3?????????????????? 4????????????????????? 5????????????????????????
     * @param driverMobile      ????????????
     * @param driverName        ????????????
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
            return ResponseResult.failure("????????????");
        }
    }

    /**
     * ????????????:??????????????????????????????
     *
     * @param vehicleCode ????????????
     */
    @GetMapping("getShareVehicle")
    public ResponseResult getShareVehicle(@RequestParam("vehicleCode") Long vehicleCode) {

        try {
            String accessToken = Header.getAuthorization(request.getHeader("Authorization"));

            VehicleDataInfoVo shareVehicle = iVehicleDataInfoService.getShareVehicle(vehicleCode, accessToken);
            if (shareVehicle != null) {
                return ResponseResult.success(shareVehicle);
            } else {
                return ResponseResult.success("????????????");
            }

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseResult.failure("????????????");
        }
    }

    /**
     * ????????????: ??????????????????????????????
     *
     * @param applyRecordId ??????????????????id
     */
    @GetMapping("getVehicleInviteRecord")
    public ResponseResult getVehicleInviteRecord(@RequestParam("applyRecordId") Long applyRecordId) {
        VehicleApplyRecordVo vehicleInviteRecord = iVehicleDataInfoService.getVehicleInviteRecord(applyRecordId);
        return ResponseResult.success(vehicleInviteRecord);
    }

    /**
     * ????????????: ??????????????????????????????
     *
     * @param vehicleCode ????????????
     * @return ??????????????????
     */
    @GetMapping("getVehicleObjectLine/{vehicleCode}")
    public ResponseResult getVehicleObjectLine(@PathVariable("vehicleCode") Long vehicleCode) {
        List<VehicleObjectLineVo> vehicleObjectLines = iVehicleDataInfoService.getVehicleObjectLines(vehicleCode);
        return ResponseResult.success(vehicleObjectLines);
    }

    /**
     * ????????????: ????????????id(vehicleCode)??????????????????
     *
     * @param vehicleClass ????????????
     * @param vehicleCode  ????????????
     * @param plateNumbers ?????????
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
     * ??????????????? ??????
     *
     * @param vehicleClass ????????????
     * @param vehicleCode  ????????????
     * @param plateNumbers ?????????
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
     * ????????????: ??????????????????????????????
     *
     * @param vehicleCode  ??????id
     * @param verState     1??????????????????  9???????????????
     * @param plateNumbers ??????
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
     * ????????????: ??????????????????,??????????????????
     *
     * @param vehicleCode ??????id
     * @param plateNumber ??????
     * @return
     */
    @GetMapping("getAllVehicleInfoVerAudit/{vehicleCode}/{plateNumber}")
    public ResponseResult getAllVehicleInfoVerAudit(@PathVariable("vehicleCode") Long vehicleCode,
                                                    @PathVariable("plateNumber") String plateNumber) {
        if (vehicleCode <= 0 && vehicleCode != -1) {
            throw new BusinessException("???????????????????????????");
        }
        if (StringUtils.isNotEmpty(plateNumber) && !ChkIntfData.chkPlateNumber(plateNumber)) {
            throw new BusinessException("?????????????????????");
        }
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        Map<String, Object> vehicleInfoVer = iVehicleDataInfoService.getAllVehicleInfoVer(vehicleCode, SysStaticDataEnum.VER_STATE.VER_STATE_Y, plateNumber, accessToken);

        return ResponseResult.success(vehicleInfoVer);
    }

    /**
     * ????????????: ???????????????????????????
     *
     * @param plateNumber    ?????????
     * @param vehicleClassIn ????????????
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
     * ????????????: ???????????????????????????
     *
     * @param plateNumber    ?????????
     * @param vehicleClassIn ????????????
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
     * ????????????: ?????????????????????????????????
     *
     * @param mobile ??????????????????
     * @param flag   1-????????????2-?????????
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
     * ??????????????????
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
     * ???????????????????????????????????????
     */
    @GetMapping("getStaticDataOption")
    public ResponseResult getStaticDataOption(@RequestParam("codeType") String codeType) {
        List<SysStaticData> sysStaticDataListByCodeName = iSysStaticDataService.getSysStaticDataListByCodeName(codeType);
        return ResponseResult.success(sysStaticDataListByCodeName);
    }

    /**
     * ????????????
     *
     * @param Id ???????????????
     */
    @SysOperatorSaveLog(code = SysOperLogConst.BusiCode.Trailer, type = SysOperLogConst.OperType.Del, comment = "??????????????????")
    @Override
    public ResponseResult remove(@PathVariable Long Id) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        Long deleted = iVehicleDataInfoService.remove(Id, accessToken);
        return deleted != 0 ? ResponseResult.success(BusinessIdDto.of().setId(Id))
                : ResponseResult.failure(ResponseCode.INTERFACE_ADDRESS_INVALID);
    }

    /**
     * ????????????
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
        return applyRecord ? ResponseResult.success("????????????") : ResponseResult.failure();

    }

    /***
     *?????????????????????(?????????????????????/?????????/?????????  ?????? ?????????????????????)
     * ?????????
     *      vehicleCode ????????????
     *      applyState  ????????????    0????????????  3??????????????????
     *      vehicleClass   ????????????
     *      plateNumber        ????????????
     *      tenantId            ???????????????ID
     *      applyRemark        ????????????
     *      applyFileId        ??????ID
     *      drivingLicense        ???????????????ID
     *      operCerti        ???????????????ID
     *      applyDriverUserId        ?????????????????????????????????id
     *      applyRemark        ????????????
     *      applyRecordId        ??????????????????ID ??? ??????????????????????????????????????????
     */
    @PostMapping("doSaveApplyRecordForOwnCar")
    public ResponseResult doSaveApplyRecordForOwnCar(@RequestBody ApplyRecorDto applyRecorDto) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        Boolean applyRecordForOwnCar = iVehicleDataInfoService.doSaveApplyRecordForOwnCar(applyRecorDto, accessToken);
        return applyRecordForOwnCar ? ResponseResult.success("????????????") : ResponseResult.failure();
    }

    /**
     * ????????????????????????
     *
     * @param vehicleDataInfoiDto ??????????????????
     * @param pageNum             ????????????
     * @param pageSize            ????????????
     */
    @GetMapping("doQueryAllShareVehicle")
    public ResponseResult doQueryAllShareVehicle(VehicleDataInfoiDto vehicleDataInfoiDto,
                                                 @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                                 @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        Page<VehicleDataInfoiVo> page = iVehicleDataInfoService.doQueryAllShareVehicle(vehicleDataInfoiDto, pageNum, pageSize);
        return ResponseResult.success(page);
    }

    /**
     * ????????????: ???????????????????????????
     *
     * @param startTime     ??????????????????
     * @param endTime       ??????????????????
     * @param plateNumber   ????????????
     * @param linkman       ??????
     * @param mobilePhone   ????????????
     * @param tenantName    ????????????
     * @param linkManName   ???????????????
     * @param linkPhone     ????????????
     * @param vehicleLength ??????(??????)
     * @param vehicleStatus ??????(??????)
     * @param authStateIn   ????????????
     * @param shareFlgIn    ????????????
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
     * ?????????????????????
     *
     * @return
     * @throws Exception
     */
    @PostMapping("doAuditInfo")
    public ResponseResult doAuditInfo(@RequestBody DoAuditInfoVo doAuditInfoVo) throws Exception {

        if (doAuditInfoVo == null) {
            throw new BusinessException("????????????");
        }
        if (doAuditInfoVo.getVehicleCode() == null) {
            throw new BusinessException("?????????????????????ID!");
        }
        if (doAuditInfoVo.getAuthState() == null) {
            throw new BusinessException("?????????????????????!");
        }
        if (doAuditInfoVo.getAuthState() < 0) {
            throw new BusinessException("?????????????????????!");
        }
        if (doAuditInfoVo.getAuthState() == 3 && (doAuditInfoVo.getAuditContent() == null || StringUtils.isEmpty(doAuditInfoVo.getAuditContent()))) {
            throw new BusinessException("????????????????????????????????????!");
        }

        return ResponseResult.success();
    }

    /**
     * ????????????: ??????????????????????????????
     *
     * @param plateNumber ????????????
     */
    @GetMapping("getVehicleDriver")
    public ResponseResult getVehicleDriver(@RequestParam("plateNumber") String plateNumber) {
        List<VehicleDriverVo> vehicleDriver = iVehicleDataInfoService.getVehicleDriver(plateNumber);
        return ResponseResult.success(vehicleDriver);
    }

    /**
     * ????????????: ??????????????????????????????
     *
     * @param mobile ?????????
     * @param flag   1-????????????2-?????????
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
     * ??????????????????
     */
    @PostMapping("downloadExcelFile")
    public ResponseResult downloadExcelFile(HttpServletResponse response, @RequestBody VehicleDataInfoExcelDto vehicleDataInfoExcelDto) {
        try {
            String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
            ImportOrExportRecords record = new ImportOrExportRecords();
            record.setName("??????????????????");
            record.setBussinessType(2);
            record.setState(1);
            record = importOrExportRecordsService.saveRecords(record, accessToken);
            iTenantVehicleRelService.acquireExcelFile(record, vehicleDataInfoExcelDto.getPlateNumber(), vehicleDataInfoExcelDto.getLinkman(), vehicleDataInfoExcelDto.getMobilePhone(), vehicleDataInfoExcelDto.getBillReceiverMobile(), vehicleDataInfoExcelDto.getLinkManName(), vehicleDataInfoExcelDto.getLinkPhone(), vehicleDataInfoExcelDto.getVehicleLength(),
                    vehicleDataInfoExcelDto.getTenantName(), vehicleDataInfoExcelDto.getVehicleStatus(), vehicleDataInfoExcelDto.getStartTime(), vehicleDataInfoExcelDto.getEndTime(), vehicleDataInfoExcelDto.getAuthStateIn(), vehicleDataInfoExcelDto.getShareFlgIn(), vehicleDataInfoExcelDto.getIsAuth(), vehicleDataInfoExcelDto.getVehicleClass(), vehicleDataInfoExcelDto.getVehicleGps(), vehicleDataInfoExcelDto.getBdEffectDate(), vehicleDataInfoExcelDto.getBdInvalidDate(), vehicleDataInfoExcelDto.getFieldName(), accessToken);
            return ResponseResult.success("??????????????????,???????????????????????????-?????????????????????????????????????????????");
        } catch (Exception e) {
            return ResponseResult.failure("????????????");
        }
    }

    /***
     * ????????????/??????(??????)??????
     */
    @PostMapping("import")
    public ResponseResult driverImport(@RequestParam("file") MultipartFile file) {
        try {
            String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
            ExcelParse parse = new ExcelParse();
            parse.loadExcel(file.getOriginalFilename(), file.getInputStream());
            // ??????????????????
            int sheetNo = 1;
            int rows = parse.getRealRowCount(sheetNo);
            if (rows > 100) {
                throw new BusinessException("?????????????????????100?????????");
            }
            ImportOrExportRecords record = new ImportOrExportRecords();
            //????????????
            FastDFSHelper client = FastDFSHelper.getInstance();
            String mediaExcelPath = client.upload(file.getInputStream(), "????????????.xlsx", file.getSize());
            record.setMediaUrl(mediaExcelPath);
            record.setName("??????????????????");
            record.setMediaName(file.getOriginalFilename());
            record.setBussinessType(1);
            record.setState(1);
            record = importOrExportRecordsService.saveRecords(record, accessToken);
            iVehicleDataInfoService.deal(file.getBytes(), record, accessToken);
            return ResponseResult.success("??????????????????,???????????????????????????-?????????????????????????????????????????????");
        } catch (Exception e) {
            LOGGER.error("(??????)????????????????????????" + e);
            return ResponseResult.failure("????????????");
        }
    }

    //???????????????
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
     * ????????????: ????????????
     *
     * @param flag 1???????????? ???????????????
     * @param vid  ??????id
     */
    @PostMapping("doSaveIdle")
    public ResponseResult doSaveIdle(@RequestParam(value = "flag", defaultValue = "0") Short flag, @RequestParam("vid") Long vid) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        TenantVehicleRel tenantVehicleRel = iTenantVehicleRelService.getTenantVehicleRel(vid);
        boolean s = iVehicleDataInfoService.updateVehicleDataInfo(flag, vid, tenantVehicleRel.getPlateNumber(), accessToken);
        String vehicleClasName = "";
        if (tenantVehicleRel.getVehicleClass() == 1) {
            vehicleClasName = "?????????-";
        } else if (tenantVehicleRel.getVehicleClass() == 2) {
            vehicleClasName = "?????????-";
        } else if (tenantVehicleRel.getVehicleClass() == 3) {
            vehicleClasName = "???????????????-";
        } else {
            vehicleClasName = "?????????-";
        }
        if (flag == 1) {
            sysOperLogService.save(SysOperLogConst.BusiCode.Vehicle, tenantVehicleRel.getId(),
                    SysOperLogConst.OperType.Idle, vehicleClasName + tenantVehicleRel.getPlateNumber() + "-??????", accessToken);
        } else {
            sysOperLogService.save(SysOperLogConst.BusiCode.Vehicle, tenantVehicleRel.getId(),
                    SysOperLogConst.OperType.Enable, vehicleClasName + tenantVehicleRel.getPlateNumber() + "-??????", accessToken);
        }
        return s ? ResponseResult.success("????????????") : ResponseResult.failure("????????????");
    }

    /**
     * ????????????????????????????????????
     *
     * @param id ??????id
     */
    @GetMapping({"getAllByOrganize"})
    public ResponseResult getAllByOrganize(@RequestParam("id") Long id) {
        try {
            List<TenantVehicleRel> vehicleByOrganize = iVehicleDataInfoService.getVehicleByOrganize(id);
            return ResponseResult.success(vehicleByOrganize);
        } catch (Exception e) {
            LOGGER.error("?????????????????????????????????" + e);
            return ResponseResult.failure("????????????");
        }
    }

    /**
     * 40012 ??????????????????
     *
     * @param plateNumber ?????????
     */
    @GetMapping("getVehicleByPlateNumberVx")
    public ResponseResult getVehicleByPlateNumberVx(@RequestParam("plateNumber") String plateNumber) {
        return ResponseResult.success(
                iVehicleDataInfoService.getVehicleByPlateNumberVx(plateNumber)
        );
    }

    /**
     * 14511 ??????????????????-??????--??????????????????--???????????????
     *
     * @param plateNumber       ?????????
     * @param tenantName        ????????????
     * @param applyVehicleClass ????????????
     * @param states            ????????????
     * @param pageNum           ????????????
     * @param pageSize          ????????????
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
     * 14005 ???????????? ????????????????????????
     * ?????????
     * <ul>
     *     <li>loginAcct        ?????????????????????     </li>
     * 	   <li>linkman          ??????              </li>
     * 	   <li>employeeNumber   ????????????          </li>
     * 	   <li>staffPosition    ??????              </li>
     * 	   <li>lockFlag         ??????  1?????????2?????? </li>
     * 	   <li>orgId            ?????????????????????</li>
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
     * 14510 ????????????-??????????????????????????????????????????--????????????
     *
     * @param plateNumber ?????????
     */
    @PostMapping("getVehicleForMiniProgram")
    public ResponseResult getVehicleForMiniProgram(String plateNumber,
                                                   @RequestParam(value = "pageNum", defaultValue = "1", required = false) Integer pageNum,
                                                   @RequestParam(value = "pageSize", defaultValue = "20", required = false) Integer pageSize) {
        if (StringUtils.isBlank(plateNumber)) {
            throw new BusinessException("????????????????????????");
        }
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        Page<VehicleDataInfoVxVo> vehiclePage = iVehicleDataInfoService.getVehiclePage(new Page<VehicleDataInfoVxVo>(pageNum, pageSize), plateNumber, accessToken);
        return ResponseResult.success(vehiclePage);
    }

    /**
     * ????????????????????????
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
     * 14500 ?????????????????????
     */
    @PostMapping("doSaveVehicle")
    public ResponseResult doSaveVehicle(@RequestBody SaveVehicleAppVo vo) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));

        iVehicleDataInfoService.doSaveVehicle(vo, accessToken);
        return ResponseResult.success();
    }

    /**
     * 14503 APP??????????????????
     *
     * @param vehicleCode  ????????????
     * @param driverUserId ????????????
     */
    @GetMapping("doQueryTenantByVehicleCodeApp")
    public ResponseResult doQueryTenantByVehicleCodeApp(Long vehicleCode, Long driverUserId) {

        if (vehicleCode < 0) {
            throw new BusinessException("?????????????????????");
        }

        if (vehicleCode < 0) {
            throw new BusinessException("?????????????????????");
        }

        return ResponseResult.success(
                iVehicleDataInfoService.doQueryTenantByVehicleCodeApp(vehicleCode, driverUserId)
        );

    }

    /**
     * 14501 ?????????????????????
     */
    @PostMapping("doUpdateVehicle")
    public ResponseResult doUpdateVehicle(@RequestBody UpdateVehicleVo vo) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        iVehicleDataInfoService.doUpdateVehicle(vo, accessToken);
        return ResponseResult.success();
    }


    /**
     * 14502 ????????????
     *
     * @param vehicleCodeString vehicleCodes ????????????(?????????????????????)
     */
    @PostMapping("doRemoveVehicleWx")
    public ResponseResult doRemoveVehicleWx(@RequestBody String vehicleCodeString) {

        if (org.apache.commons.lang.StringUtils.isBlank(vehicleCodeString)) {
            throw new BusinessException("?????????????????????????????????");
        }

        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        iVehicleDataInfoService.doRemoveVehicleWx(vehicleCodeString, accessToken);
        return ResponseResult.success();
    }

    /**
     * 14504 ?????????????????????
     *
     * @param relIds ??????????????????id (?????????????????????)
     */
    @GetMapping("doQuitTenant")
    public ResponseResult doQuitTenant(String relIds) {
        if (StringUtils.isBlank(relIds)) {
            throw new BusinessException("?????????????????????????????????");
        }
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        iVehicleDataInfoService.doQuitTenant(relIds, accessToken);
        return ResponseResult.success();
    }

    /**
     * 14505 ????????????
     *
     * @param vehicleCode ????????????
     */
    @GetMapping("getVehicleInfoApp")
    public ResponseResult getVehicleInfoApp(Long vehicleCode) {
        if (vehicleCode == null || vehicleCode < 0) {
            throw new BusinessException("?????????????????????");
        }

        return ResponseResult.success(
                iVehicleDataInfoService.getVehicleInfoApp(vehicleCode)
        );

    }

    /**
     * 14506 ??????????????????
     *
     * @param driverUserId ????????????
     */
    @GetMapping("doQueryVehicleByDriver")
    public ResponseResult doQueryVehicleByDriver(Long driverUserId,
                                                 @RequestParam(value = "pageNum", defaultValue = "1", required = false) Integer pageNum,
                                                 @RequestParam(value = "pageSize", defaultValue = "20", required = false) Integer pageSize) {
        if (driverUserId == null || driverUserId < 0) {
            throw new BusinessException("????????????????????????????????????");
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
     * 14507 ????????????--????????????--APP
     *
     * @param applyId ??????????????????id
     * @param dec     ??????
     */
    @PostMapping("applyRecordSuccess")
    public ResponseResult applyRecordSuccess(@RequestParam("applyId") Long applyId, @RequestParam("dec") String dec) {

        if (applyId == null || applyId < 0) {
            throw new BusinessException("??????ID?????????");
        }

        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        iAuditCallBackService.sucess(applyId, dec, null, accessToken);
        return ResponseResult.success();
    }

    /**
     * 14508 ????????????--????????????--APP
     *
     * @param applyId ??????????????????id
     * @param dec     ??????
     */
    @PostMapping("applyRecordFail")
    public ResponseResult applyRecordFail(@RequestParam("applyId") Long applyId, @RequestParam("dec") String dec) {

        if (applyId == null || applyId < 0) {
            throw new BusinessException("??????ID?????????");
        }

        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        iAuditCallBackService.fail(applyId, dec, null, accessToken);
        return ResponseResult.success();
    }

    /**
     * 14521 ????????????????????????????????????
     *
     * @param driverUserId ????????????
     */
    @GetMapping("doQueryZYVehicleByDriver")
    public ResponseResult doQueryZYVehicleByDriver(Long driverUserId) {
        if (driverUserId == null || driverUserId < 0) {
            throw new BusinessException("????????????????????????????????????");
        }

        return ResponseResult.success(
                iTenantVehicleRelService.getTenantVehicleRels(driverUserId, SysStaticDataEnum.CAR_USER_TYPE.OWN_CAR)
        );

    }

}
