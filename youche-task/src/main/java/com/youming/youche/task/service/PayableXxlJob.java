package com.youming.youche.task.service;

import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.youming.youche.table.api.receivable.IPayableDayReportService;
import com.youming.youche.table.api.receivable.IPayableMonthReportService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Component;

/**
 * @author zengwen
 * @date 2022/5/10 15:30
 */
@Component
public class PayableXxlJob {

    @DubboReference(version = "1.0.0")
    IPayableDayReportService iPayableDayReportService;

    @DubboReference(version = "1.0.0")
    IPayableMonthReportService iPayableMonthReportService;

    /**
     * 加载应付日报数据
     */
    @XxlJob("initPayableDayReportDataJobHandler")
    public void initPayableDayReportDataJobHandler() {
        XxlJobHelper.log("XXL-JOB, 加载应付日报数据业务开始");
        iPayableDayReportService.initPayableDayReportData();
        XxlJobHelper.log("XXL-JOB, 加载应付日报数据业务结束");
    }

    /**
     * 加载应付月报数据
     */
    @XxlJob("initPayableMonthReportDataJobHandler")
    public void initPayableMonthReportDataJobHandler() {
        XxlJobHelper.log("XXL-JOB, 加载应付月报数据业务开始");
        iPayableMonthReportService.initPayableMonthReport();
        XxlJobHelper.log("XXL-JOB, 加载应付月报数据业务结束");
    }
}
