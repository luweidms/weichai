package com.youming.youche.task.service;

import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.youming.youche.finance.api.munual.IPayoutIntfThreeService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Component;

/**
 * @author zengwen
 * @date 2022/4/27 16:16
 */
@Component
public class PayoutXxlJob {

    @DubboReference(version = "1.0.0")
    IPayoutIntfThreeService iPayoutIntfThreeService;

    /**
     * 虚拟账户到虚拟账户之后的本地业务 [669]
     */
    @XxlJob("payOutToBusiJobHandler")
    public void payOutToBusiJobHandler() {
        XxlJobHelper.log("XXL-JOB, 虚拟账户到虚拟账户之后的本地业务开始");
        iPayoutIntfThreeService.payOutToBusi();
        XxlJobHelper.log("XXL-JOB, 虚拟账户到虚拟账户之后的本地业务结束");
    }

    /**
     * CMB银行虚拟账户到虚拟账户的支付 [2004]
     */
    @XxlJob("cmbOutToBankJobHandler")
    public void cmbOutToBankJobHandler() {
        XxlJobHelper.log("XXL-JOB, 银行虚拟账户到虚拟账户的支付业务开始");
        iPayoutIntfThreeService.cmbOutToBank();
        XxlJobHelper.log("XXL-JOB, 银行虚拟账户到虚拟账户的支付业务结束");
    }

    /**
     * 线上打款自动确认收款 [10030]
     */
    @XxlJob("confirmMoneyJobHandler")
    public void confirmMoneyJobHandler() {
        XxlJobHelper.log("XXL-JOB, 线上打款自动确认收款业务开始");
        iPayoutIntfThreeService.confirmMoney();
        XxlJobHelper.log("XXL-JOB, 线上打款自动确认收款业务结束");
    }

    /**
     * CMB银行虚拟账户到虚拟账户的支付 [668]
     */
    @XxlJob("payOutToBankJobHandler")
    public void payOutToBankJobHandler() {
        XxlJobHelper.log("XXL-JOB, CMB银行虚拟账户到虚拟账户的支付业务开始");
        iPayoutIntfThreeService.payOutToBank();
        XxlJobHelper.log("XXL-JOB, CMB银行虚拟账户到虚拟账户的支付业务结束");
    }
}
