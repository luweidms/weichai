package com.youming.youche.task.service;

import com.xxl.job.core.handler.annotation.XxlJob;
import com.youming.youche.market.api.facilitator.IServiceInfoService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ServiceInfoXxJob {
    private static Logger logger = LoggerFactory.getLogger(ServiceInfoXxJob.class);
    @DubboReference(version = "1.0.0")
    private IServiceInfoService serviceInfoService;



    /**
     * 加载服务商账单
     */
    @XxlJob("updateServiceInfoBill")
    public void updateServiceInfoBill() {
        logger.info("加载服务商账单开始");
        serviceInfoService.dynamicUpdateServiceInfoBill();
        logger.info("加载服务商账单结束");
    }
}
