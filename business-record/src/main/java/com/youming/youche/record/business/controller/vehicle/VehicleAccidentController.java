package com.youming.youche.record.business.controller.vehicle;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.commons.base.BaseController;
import com.youming.youche.commons.domain.ImportOrExportRecords;
import com.youming.youche.commons.response.ResponseResult;
import com.youming.youche.commons.web.Header;
import com.youming.youche.record.api.vehicle.IVehicleAccidentService;
import com.youming.youche.record.domain.vehicle.VehicleAccident;
import com.youming.youche.record.vo.VehicledentAccidentVo;
import com.youming.youche.system.api.ISysUserOrgRelService;
import com.youming.youche.system.api.ioer.ImportOrExportRecordsService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author hzx
 * @description 车辆事故
 * @date 2022/1/15 16:05
 */
@RestController
@RequestMapping("vehicle/accident")
public class VehicleAccidentController extends BaseController<VehicleAccident, IVehicleAccidentService> {

    private static final Logger LOGGER = LoggerFactory.getLogger(VehicleAnnualReviewController.class);

    @DubboReference(version = "1.0.0")
    private IVehicleAccidentService iVehicleAccidentService;

    @DubboReference(version = "1.0.0")
    private ISysUserOrgRelService iSysUserOrgRelService;

    @DubboReference(version = "1.0.0")
    private ImportOrExportRecordsService importOrExportRecordsService;

    @Override
    public IVehicleAccidentService getService() {
        return iVehicleAccidentService;
    }

    /**
     * 新增事故记录
     */
    @PostMapping("insertAccidentRecord")
    public ResponseResult insertAccidentRecord(VehicleAccident vehicleAccident) {
        try {
            String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
            iVehicleAccidentService.insertAccidentRecord(vehicleAccident, accessToken);
            return ResponseResult.success();
        } catch (Exception e) {
            LOGGER.error("新增异常" + e);
            return ResponseResult.failure("新增异常");
        }
    }

    /**
     * 新增定损
     */
    @PostMapping("insertTheDamageRecord")
    public ResponseResult insertTheDamageRecord(VehicleAccident vehicleAccident) {
        try {
            String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
            iVehicleAccidentService.insertTheDamageRecord(vehicleAccident, accessToken);
            return ResponseResult.success();
        } catch (Exception e) {
            LOGGER.error("新增异常" + e);
            return ResponseResult.failure("新增异常");
        }
    }

    /**
     * 新增核赔
     */
    @PostMapping("insertNuclearDamageRecord")
    public ResponseResult insertNuclearDamageRecord(VehicleAccident vehicleAccident) {
        try {
            String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
            iVehicleAccidentService.insertNuclearDamageRecord(vehicleAccident, accessToken);
            return ResponseResult.success();
        } catch (Exception e) {
            LOGGER.error("新增异常" + e);
            return ResponseResult.failure("新增异常");
        }
    }

    /**
     * 查询事故详情
     *
     * @param id 事故记录主键id
     */
    @PostMapping("queryDetails")
    public ResponseResult selectAccidentById(@RequestParam("id") Long id) {
        try {
            VehicleAccident vehicleAccident = iVehicleAccidentService.selectAccidentById(id);
            return ResponseResult.success(vehicleAccident);
        } catch (Exception e) {
            LOGGER.error("查询详情异常" + e);
            return ResponseResult.failure("查询详情异常");
        }
    }

    /**
     * 修改事故记录
     */
    @PostMapping("updateAccidentRecord")
    public ResponseResult updateAccidentRecord(VehicleAccident vehicleAccident) {
        try {
            String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
            iVehicleAccidentService.updateAccidentRecord(vehicleAccident, accessToken);
            return ResponseResult.success();
        } catch (Exception e) {
            LOGGER.error("修改异常" + e);
            return ResponseResult.failure("修改异常");
        }
    }

    /**
     * 查询车辆事故列表
     *
     * @param vehicleCode       车牌
     * @param accidentStatus    事故状态
     * @param reportDateStart   报案日期-生效日期
     * @param reportDateEnd     报案日期-失效日期
     * @param accidentDateStart 出险日期-生效日期
     * @param accidentDateEnd   出险日期-失效日期
     * @param createDateStart   创建日期-生效日期
     * @param createDateEnd     创建日期-失效日期
     * @param pageNum           分页参数
     * @param pageSize          分页参数
     */
    @GetMapping("queryAllRecord")
    public ResponseResult queryAllRecord(@RequestParam(value = "vehicleCode", required = false) String vehicleCode,
                                         @RequestParam(value = "accidentStatus", required = false) String accidentStatus,
                                         @RequestParam(value = "reportDateStart", required = false) String reportDateStart,
                                         @RequestParam(value = "reportDateEnd", required = false) String reportDateEnd,
                                         @RequestParam(value = "accidentDateStart", required = false) String accidentDateStart,
                                         @RequestParam(value = "accidentDateEnd", required = false) String accidentDateEnd,
                                         @RequestParam(value = "createDateStart", required = false) String createDateStart,
                                         @RequestParam(value = "createDateEnd", required = false) String createDateEnd,
                                         @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                         @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        try {
            String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
            Page<VehicleAccident> objectPage = new Page<>(pageNum, pageSize);
            Page<VehicleAccident> vehicleAccidentPage = iVehicleAccidentService.queryAllRecord(objectPage, vehicleCode, accidentStatus, reportDateStart, reportDateEnd,
                    accidentDateStart, accidentDateEnd, createDateStart, createDateEnd, accessToken);
            return ResponseResult.success(vehicleAccidentPage);
        } catch (Exception e) {
            LOGGER.error("查询异常" + e);
            return ResponseResult.failure("查询异常");
        }
    }

    /**
     * 车辆事故记录导出
     *
     * @param vehicleCode       车牌
     * @param accidentStatus    事故状态
     * @param reportDateStart   报案日期-生效日期
     * @param reportDateEnd     报案日期-失效日期
     * @param accidentDateStart 出险日期-生效日期
     * @param accidentDateEnd   出险日期-失效日期
     * @param createDateStart   创建日期-生效日期
     * @param createDateEnd     创建日期-失效日期
     */
    @GetMapping("export")
    public ResponseResult export(@RequestParam(value = "vehicleCode", required = false) String vehicleCode,
                                 @RequestParam(value = "accidentStatus", required = false) String accidentStatus,
                                 @RequestParam(value = "reportDateStart", required = false) String reportDateStart,
                                 @RequestParam(value = "reportDateEnd", required = false) String reportDateEnd,
                                 @RequestParam(value = "accidentDateStart", required = false) String accidentDateStart,
                                 @RequestParam(value = "accidentDateEnd", required = false) String accidentDateEnd,
                                 @RequestParam(value = "createDateStart", required = false) String createDateStart,
                                 @RequestParam(value = "createDateEnd", required = false) String createDateEnd
    ) {
        try {
            String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
            //TODO 根据token获取用户信息
            ImportOrExportRecords record = new ImportOrExportRecords();
            record.setName("车辆事故信息导出");
            record.setBussinessType(2);
            record.setState(1);
            record = importOrExportRecordsService.saveRecords(record, accessToken);
            iVehicleAccidentService.queryAllRecordExport(vehicleCode, accidentStatus, reportDateStart, reportDateEnd,
                    accidentDateStart, accidentDateEnd, createDateStart, createDateEnd, accessToken, record);
            return ResponseResult.success("正在导出文件,您可以在“系统设置-导入导出结果查询”查看导出结果");
        } catch (Exception e) {
            LOGGER.error("导出失败车辆事故列表异常" + e);
            return ResponseResult.failure("导出成功车辆事故异常");
        }
    }


    /**
     * 实现功能: 分页查询事故明细表
     *
     * @param monthTime      事故月份
     * @param licenceType    牌照类型 1:整车，2：拖头
     * @param accidentStatus 处理状态 1、已登记、2、已维修、3、已核赔
     * @param pageNum        分页参数
     * @param pageSize       分页参数
     * @return
     */
    @GetMapping("getVehicledentAccident")
    public ResponseResult getVehicledentAccident(@RequestParam("monthTime") String monthTime,
                                                 @RequestParam("licenceType") Integer licenceType,
                                                 @RequestParam("accidentStatus") Integer accidentStatus,
                                                 @RequestParam("pageNum") Integer pageNum,
                                                 @RequestParam("pageSize") Integer pageSize) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        Page<VehicledentAccidentVo> page = new Page<VehicledentAccidentVo>(pageNum, pageSize);
        Page<VehicledentAccidentVo> vehicledentAccident = iVehicleAccidentService.getVehicledentAccident(page, monthTime,
                licenceType, accidentStatus, accessToken);
        //封装归属部门信息
        if (vehicledentAccident.getRecords() != null && vehicledentAccident.getRecords().size() > 0) {
            for (VehicledentAccidentVo record : vehicledentAccident.getRecords()) {
                if (record.getUserId() != null) {
                    List<String> orgNameByUserId = iSysUserOrgRelService.getOrgNameByUserId(record.getUserId());
                    if (orgNameByUserId != null && orgNameByUserId.size() > 0) {
                        if (orgNameByUserId.size() == 1) {
                            record.setDepartmentName(orgNameByUserId.get(0));
                        } else {
                            String orgName = "";
                            int a = orgNameByUserId.size() - 1;
                            for (int i = 0; i < orgNameByUserId.size(); i++) {
                                if (a == i) {
                                    orgName = orgName + orgNameByUserId.get(i);
                                } else {
                                    orgName = orgName + orgNameByUserId.get(i) + ",";
                                }
                            }
                            record.setDepartmentName(orgName);
                        }
                    }
                }
            }
        }
        return ResponseResult.success(vehicledentAccident);
    }

    /**
     * 事故明细导出
     *
     * @param monthTime      事故月份
     * @param licenceType    牌照类型 1:整车，2：拖头
     * @param accidentStatus 处理状态 1、已登记、2、已维修、3、已核赔
     */
    @GetMapping("getVehicledentAccident/export")
    public ResponseResult getVehicledentAccidentExport(String monthTime,
                                                       Integer licenceType,
                                                       Integer accidentStatus) {
        try {
            String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
            //TODO 根据token获取用户信息
            ImportOrExportRecords record = new ImportOrExportRecords();
            record.setName("事故明细信息导出");
            record.setBussinessType(2);
            record.setState(1);
            record = importOrExportRecordsService.saveRecords(record, accessToken);
            iVehicleAccidentService.getVehicledentAccidentExport(monthTime, licenceType, accidentStatus, accessToken, record);
            return ResponseResult.success("正在导出文件,您可以在“系统设置-导入导出结果查询”查看导出结果");
        } catch (Exception e) {
            LOGGER.error("导出失败事故明细列表异常" + e);
            return ResponseResult.failure("导出成功事故明细异常");
        }
    }

}
