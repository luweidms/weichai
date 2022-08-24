package com.youming.youche.table.business.controller.vehicleReport;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.pagehelper.PageInfo;
import com.youming.youche.commons.base.BaseController;
import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.commons.domain.ImportOrExportRecords;
import com.youming.youche.commons.response.ResponseResult;
import com.youming.youche.commons.web.Header;
import com.youming.youche.system.api.ioer.ImportOrExportRecordsService;
import com.youming.youche.table.api.vehicleReport.IVehicleReportService;
import com.youming.youche.table.domain.vehicleReport.VehicleReport;
import com.youming.youche.table.dto.vehiclereport.VehicleReportDto;
import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author wuhao
 * @since 2022-05-06
 */
@RestController
@RequestMapping("vehicle/report")
public class VehicleReportController extends BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(VehicleReportController.class);

    @DubboReference(version = "1.0.0")
    IVehicleReportService vehicleReportService;

    @DubboReference(version = "1.0.0")
    ImportOrExportRecordsService importOrExportRecordsService;

    @Override
    public IBaseService getService() {
        return null;
    }

    /**
     * 车辆报表列表查询
     */
    @GetMapping("vehicleReport")
    public ResponseResult vehicleReport(String carNumber, String startMonth, String endMonth, String currentLine, String department,
                                        @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                        @RequestParam(value = "pageSize", defaultValue = "20") Integer pageSize) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        PageInfo<VehicleReport> vehicleReportDtoPage = vehicleReportService.queryVehicleReportData(carNumber, startMonth, endMonth, currentLine, department, pageSize, pageNum, accessToken);
        return ResponseResult.success(vehicleReportDtoPage);
    }

    /**
     * 车辆报表导出
     */
    @GetMapping("download")
    public ResponseResult downloadExcelFile(String carNumber, String startMouth, String endMonth, String currentLine, String department) {
        try {
            String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
            //TODO 根据token获取用户信息
            ImportOrExportRecords record = new ImportOrExportRecords();
            record.setName("车辆报表导出");
            record.setBussinessType(2);
            record.setState(1);
            record = importOrExportRecordsService.saveRecords(record, accessToken);
            vehicleReportService.CarDownload(accessToken, record, carNumber, startMouth, endMonth, currentLine, department);
            return ResponseResult.success("正在导出文件,您可以在“系统设置-导入导出结果查询”查看导出结果");
        } catch (Exception e) {
            LOGGER.error("导出司机账户列表异常" + e);
            return ResponseResult.failure("导出车辆报表");
        }
    }

//    @GetMapping("s")
//    public ResponseResult test(Long tenantId){
//        VehicleReport s = vehicleReportService.selectOr(tenantId);
//        return ResponseResult.success(s);
//    }

}
