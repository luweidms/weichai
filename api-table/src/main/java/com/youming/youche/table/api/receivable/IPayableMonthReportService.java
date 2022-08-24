package com.youming.youche.table.api.receivable;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.commons.domain.ImportOrExportRecords;
import com.youming.youche.table.domain.receivable.PayableMonthReport;
import com.youming.youche.table.dto.receivable.PayableMonthReportDto;

import java.util.List;

/**
* <p>
    * 应付月报表 服务类
    * </p>
* @author zengwen
* @since 2022-05-11
*/
public interface IPayableMonthReportService extends IBaseService<PayableMonthReport> {

    /**
     * 加载应付月报数据
     */
    void initPayableMonthReport();

    /**
     * 获取应付月报数据
     */
    Page<PayableMonthReportDto> getPayableMonthReport(String accessToken, Integer pageNum, Integer pageSize);

    /**
     * 导出应付月报数据
     */
    void downloadExcelFile(String accessToken, ImportOrExportRecords importOrExportRecords);

}
