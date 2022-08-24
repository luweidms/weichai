package com.youming.youche.task.service;

import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.youming.youche.table.api.receivable.IDailyAccountsReceivableService;
import com.youming.youche.table.api.receivable.IMonthlyReportReceivableService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Component;

/**
 * hzx
 * 应收
 */
@Component
public class ReceivableXxlJob {

    @DubboReference(version = "1.0.0")
    IDailyAccountsReceivableService iDailyAccountsReceivableService;

    @DubboReference(version = "1.0.0")
    IMonthlyReportReceivableService iMonthlyReportReceivableService;

    /**
     * 应收日报
     *
     * @Date 2022/5/6 上午
     * @author hzx
     */
    @XxlJob("dailyReceivable")
    private void dailyReceivable() throws Exception {
        XxlJobHelper.log("XXL-JOB, 应收日报开始");
        iDailyAccountsReceivableService.execute();
        XxlJobHelper.log("XXL-JOB, 应收日报结束");
    }

    /**
     * 应收月报
     *
     * @Date 2022/5/6 上午
     * @author hzx
     */
    @XxlJob("monthlyReport")
    private void monthlyReport() throws Exception {
        XxlJobHelper.log("XXL-JOB, 应收月报开始");
        iMonthlyReportReceivableService.execute();
        XxlJobHelper.log("XXL-JOB, 应收月报结束");
    }


}
