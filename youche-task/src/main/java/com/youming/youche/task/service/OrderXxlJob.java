package com.youming.youche.task.service;

import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.youming.youche.commons.web.Header;
import com.youming.youche.order.api.ITransferInfoService;
import com.youming.youche.order.api.order.IOrderLimitService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 转单超时的订单的处理
 */
@Component
//@EnableScheduling
public class OrderXxlJob {

    private static Logger logger = LoggerFactory.getLogger(OrderXxlJob.class);

    @DubboReference(version = "1.0.0")
    ITransferInfoService transferInfoService;


    @DubboReference(version = "1.0.0",async = true)
    IOrderLimitService orderLimitService;

    /**
     * 转单超时的订单的处理
     * @author liangyan
     * @date 2022/4/16 7:28
     * @return void
     */
    @XxlJob("timeOutTransferJobHandler")
    public void timeOutTransferJobHandler() throws Exception{
        XxlJobHelper.log("XXL-JOB, 处理转单超时开始");
        transferInfoService.execution();
        XxlJobHelper.log("XXL-JOB, 处理转单超时结束");
    }

    /**
     * 未到期金额转可用金额
     * @author liangyan
     * @date 2022/4/16 7:28
     * @return void
     */
    @XxlJob("beforeMaturityTransferAvailableJobHandler")
    public void beforeMaturityTransferAvailableJobHandler() throws Exception{
        XxlJobHelper.log("XXL-JOB, 处理未到期金额转可用金额开始");
        orderLimitService.execution();
        XxlJobHelper.log("XXL-JOB, 处理未到期金额转可用金额结束");
    }


}
