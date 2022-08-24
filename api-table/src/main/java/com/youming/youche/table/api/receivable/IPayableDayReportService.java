package com.youming.youche.table.api.receivable;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.commons.base.IBaseService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.youming.youche.commons.domain.ImportOrExportRecords;
import com.youming.youche.table.domain.receivable.PayableDayReport;
import com.youming.youche.table.dto.receivable.PayableDayReportDto;
import com.youming.youche.table.dto.receivable.PayableDayReportSumDto;

import java.util.List;

/**
* <p>
    *  服务类
    * </p>
* @author zengwen
* @since 2022-05-10
*/
    public interface IPayableDayReportService extends IBaseService<PayableDayReport> {

    /**
     * 加载应付日报数据
     */
    void initPayableDayReportData();

    /**
     * 获取应付日报数据
     */
    Page<PayableDayReportDto> getPayableDayReportList(String accessToken, String userName, String time, Integer pageNum, Integer pageSize);

    /**
     * 获取应付日报总数据
     */
    PayableDayReportSumDto getPayableDayReportSum(String accessToken, String userName, String time);

    /**
     * 导出应付日报数据
     */
    void downloadExcelFile(String accessToken, ImportOrExportRecords importOrExportRecords, String userName, String time);

    }
