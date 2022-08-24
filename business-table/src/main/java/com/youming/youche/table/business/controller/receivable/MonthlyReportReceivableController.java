package com.youming.youche.table.business.controller.receivable;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.commons.domain.ImportOrExportRecords;
import com.youming.youche.commons.response.ResponseResult;
import com.youming.youche.commons.web.Header;
import com.youming.youche.system.api.ioer.ImportOrExportRecordsService;
import com.youming.youche.table.api.receivable.IMonthlyReportReceivableService;
import com.youming.youche.table.domain.receivable.DailyAccountsReceivable;
import com.youming.youche.table.domain.receivable.MonthlyReportReceivable;
import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.youming.youche.commons.base.BaseController;

import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author hzx
 * @since 2022-04-30
 */
@RestController
@RequestMapping("monthly/report/receivable")
public class MonthlyReportReceivableController extends BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MonthlyReportReceivableController.class);

    @DubboReference(version = "1.0.0")
    IMonthlyReportReceivableService iMonthlyReportReceivableService;

    @DubboReference(version = "1.0.0")
    ImportOrExportRecordsService importOrExportRecordsService;

    @Override
    public IBaseService getService() {
        return null;
    }

    /**
     * 查询应收月报
     */
    @GetMapping("query")
    public ResponseResult queryMonth(@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                     @RequestParam(value = "pageSize", defaultValue = "20") Integer pageSize) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        Page<MonthlyReportReceivable> monthlyReportReceivables = iMonthlyReportReceivableService.queryMonth(accessToken, pageNum, pageSize);
        return ResponseResult.success(monthlyReportReceivables);
    }

    /**
     * 查询应收月报导出
     */
    @GetMapping("query/export")
    public ResponseResult queryExport() {
        try {
            String accessToken = Header.getAuthorization(request.getHeader("Authorization"));

            ImportOrExportRecords record = new ImportOrExportRecords();
            record.setName("应收月报信息导出");
            record.setBussinessType(2);
            record.setState(1);
            record = importOrExportRecordsService.saveRecords(record, accessToken);
            iMonthlyReportReceivableService.queryMonthExport(record, accessToken);
            return ResponseResult.success("正在导出文件,您可以在“系统设置-导入导出结果查询”查看导出结果");
        } catch (Exception e) {
            LOGGER.error("导出失败应收月报列表异常" + e);
            return ResponseResult.failure("导出成功\"应收月报异常");
        }
    }

}
