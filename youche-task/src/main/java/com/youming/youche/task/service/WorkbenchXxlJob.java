
package com.youming.youche.task.service;

import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.youming.youche.table.api.workbench.IOperationWorkbenchInfoService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Component;

/**
 * @author zengwen
 * @date 2022/5/4 13:52
 */
@Component
public class WorkbenchXxlJob {

    @DubboReference(version = "1.0.0", async = true)
    IOperationWorkbenchInfoService iOperationWorkbenchInfoService;

    /**
     * 加载营运工作台数据
     */
    @XxlJob("initOperationWorkbenchJobHandler")
    public void initOperationWorkbenchJobHandler() {
        XxlJobHelper.log("XXL-JOB, 加载营运工作台数据业务开始");
        iOperationWorkbenchInfoService.initOperationWorkbenchInfoData();
        XxlJobHelper.log("XXL-JOB, 加载营运工作台数据业务结束");
    }

    /**
     * 加载财务工作台数据
     */
    @XxlJob("initFinancialWorkbenchJobHandler")
    public void initFinancialWorkbenchJobHandler() {
        XxlJobHelper.log("XXL-JOB, 加载财务工作台数据业务开始");
        iOperationWorkbenchInfoService.initFinancialWorkbenchInfoData();
        XxlJobHelper.log("XXL-JOB, 加载财务工作台数据业务结束");
    }

    /**
     * 加载老板工作台数据
     */
    @XxlJob("initBossWorkbenchJobHandler")
    public void initBossWorkbenchJobHandler() {
        XxlJobHelper.log("XXL-JOB, 加载老板工作台数据业务开始");
        iOperationWorkbenchInfoService.initBossWorkbenchInfoData();
        XxlJobHelper.log("XXL-JOB, 加载老板工作台数据业务结束");
    }

    /**
     * 加载车队小程序营运报表日报数据
     */
    @XxlJob("initWechatOperationWorkInfoDayDataJobHandler")
    public void initWechatOperationWorkInfoDayDataJobHandler() {
        XxlJobHelper.log("XXL-JOB, 加载车队小程序营运报表日报数据业务开始");
        iOperationWorkbenchInfoService.initWechatOperationWorkInfoDayData();
        XxlJobHelper.log("XXL-JOB, 加载车队小程序营运报表日报数据业务结束");
    }

    /**
     * 加载车队小程序营运报表月报数据
     */
    @XxlJob("initWechatOperationWorkInfoMonthDataJobHandler")
    public void initWechatOperationWorkInfoMonthDataJobHandler() {
        XxlJobHelper.log("XXL-JOB, 加载车队小程序营运报表月报数据业务开始");
        iOperationWorkbenchInfoService.initWechatOperationWorkInfoMonthData();
        XxlJobHelper.log("XXL-JOB, 加载车队小程序营运报表月报数据业务结束");
    }
}
