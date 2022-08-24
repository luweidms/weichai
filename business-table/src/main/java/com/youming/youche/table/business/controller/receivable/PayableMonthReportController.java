package com.youming.youche.table.business.controller.receivable;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.commons.domain.ImportOrExportRecords;
import com.youming.youche.commons.response.ResponseResult;
import com.youming.youche.commons.web.Header;
import com.youming.youche.system.api.ioer.ImportOrExportRecordsService;
import com.youming.youche.table.api.receivable.IPayableMonthReportService;
import com.youming.youche.table.dto.receivable.PayableMonthReportDto;
import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.youming.youche.commons.base.BaseController;

import java.util.List;

/**
* <p>
* 应付月报表 前端控制器
* </p>
* @author zengwen
* @since 2022-05-11
*/
@RestController
@RequestMapping("payable/month/report")
public class PayableMonthReportController extends BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(PayableMonthReportController.class);

    @Override
    public IBaseService getService() {
        return null;
    }

    @DubboReference(version = "1.0.0")
    IPayableMonthReportService iPayableMonthReportService;

    @DubboReference(version = "1.0.0")
    ImportOrExportRecordsService importOrExportRecordsService;

    /**
     * 获取应付月报数据
     */
    @GetMapping("getPayableMonthReport")
    public ResponseResult getPayableMonthReport(@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                                @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        Page<PayableMonthReportDto> payableMonthReport = iPayableMonthReportService.getPayableMonthReport(accessToken, pageNum, pageSize);
        return ResponseResult.success(payableMonthReport);
    }

    /**
     * 导出应付月报数据
     */
    @PostMapping("/downloadExcelFile")
    public ResponseResult downloadExcelFile(String time, String userName) {
        try {
            String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
            ImportOrExportRecords record = new ImportOrExportRecords();
            record.setName("应付月报数据导出");
            record.setBussinessType(2);
            record.setState(1);
            record = importOrExportRecordsService.saveRecords(record, accessToken);
            iPayableMonthReportService.downloadExcelFile(accessToken, record);
            return ResponseResult.success("正在导出文件,您可以在“系统设置-导入导出结果查询”查看导出结果");
        } catch (Exception e) {
            LOGGER.error("导出异常审核异常" + e);
            return ResponseResult.failure("导出成功异常审核异常");
        }
    }
}
