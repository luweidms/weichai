package com.youming.youche.task.service;

import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.youming.youche.order.api.monitor.IMonitorOrderAgingAbnormalService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Component;

@Component
public class MonitorOrderAgingJob {

    @DubboReference(version = "1.0.0")
    IMonitorOrderAgingAbnormalService iMonitorOrderAgingAbnormalService;

    /**
     * 异常大屏-订单时效监控
     *
     * @Date 2022/5/6 上午
     * @author hzx
     */
    @XxlJob("doTask")
    private void doTask() throws Exception {
        XxlJobHelper.log("XXL-JOB, 异常大屏-订单时效监控开始");
        iMonitorOrderAgingAbnormalService.doTask(null);
        XxlJobHelper.log("XXL-JOB, 异常大屏-订单时效监控结束");
    }

}
