package com.youming.youche.task.service;

import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.youming.youche.finance.api.ac.ICmSalaryInfoNewService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Component;

/**
 * @author zengwen
 * @date 2022/6/15 17:57
 */
@Component
public class SalaryXxlJob {

    @DubboReference(version = "1.0.0", async = true)
    ICmSalaryInfoNewService cmSalaryInfoNewService;

    @XxlJob("newSalaryDataJobHandler")
    public void newSalaryDataJobHandler() {
        XxlJobHelper.log("XXL-JOB, 加载司机工资单数据业务开始");
        cmSalaryInfoNewService.newSalaryData();
        XxlJobHelper.log("XXL-JOB, 加载司机工资单数据业务结束");
    }
}
