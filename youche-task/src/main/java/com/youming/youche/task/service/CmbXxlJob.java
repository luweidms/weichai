package com.youming.youche.task.service;

import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.youming.youche.system.api.mycenter.IBankAccountService;
import com.youming.youche.system.api.mycenter.IBankAccountTranService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Component;

/**
 * @ClassName CmbXxlJob
 * @Description 银行帐户定时任务
 * @Author zag
 * @Date 2022/3/25 10:50
 */
@Component
public class CmbXxlJob {

    @DubboReference(version = "1.0.0")
    IBankAccountService bankAccountService;

    @DubboReference(version = "1.0.0")
    IBankAccountTranService bankAccountTranService;

    /**
     * 同步商户进件结果
     * @author zag
     * @date 2022/3/25 11:26
     * @return void
     */
    @XxlJob("merchRegQryJobHandler")
    public void merchRegQryJobHandler() throws Exception{
        XxlJobHelper.log("XXL-JOB, 同步商户进件结果开始");
        bankAccountService.syncBnkAccRegResult();
        XxlJobHelper.log("XXL-JOB, 同步商户进件结果结束");
    }

    /**
     * 同步帐户余额
     * @author zag
     * @date 2022/3/25 11:28
     * @return void
     */
    @XxlJob("mbrBalQryJobHandler")
    public void mbrBalQryJobHandler() throws Exception{
        XxlJobHelper.log("XXL-JOB, 同步帐户余额开始");
        bankAccountService.syncBnkAccBalance();
        XxlJobHelper.log("XXL-JOB, 同步帐户余额结束");
    }

    /**
     * 同步帐户充值记录
     * @author zag
     * @date 2022/3/25 11:35
     * @return void
     */
    @XxlJob("chargeFundListQryJobHandler")
    public void chargeFundListQryJobHandler(String tranDate) throws Exception{
        XxlJobHelper.log("XXL-JOB, 同步帐户充值记录开始");
        bankAccountTranService.syncRechargeRecord(tranDate);
        XxlJobHelper.log("XXL-JOB, 同步帐户充值记录结束");
    }

    /**
     * 同步帐户提现退票记录
     * @author zag
     * @date 2022/3/25 11:30
     * @return void
     */
    @XxlJob("refundListQryJobHandler")
    public void refundListQryJobHandler(String clearDate) throws Exception{
        XxlJobHelper.log("XXL-JOB, 同步帐户提现退票记录开始");
        bankAccountTranService.syncRefundRecord(clearDate);
        XxlJobHelper.log("XXL-JOB, 同步帐户提现退票记录结束");
    }

    /**
     * 同步帐户提现交易最终状态
     * @author zag
     * @date 2022/5/17 18:08
     * @return void
     */
    @XxlJob("withdrawQryJobHandler")
    public void withdrawQryJobHandler() throws Exception{
        XxlJobHelper.log("XXL-JOB, 同步帐户提现交易最终状态开始");
        bankAccountTranService.syncWithdrawTranStatus();
        XxlJobHelper.log("XXL-JOB, 同步帐户提现交易最终状态结束");
    }

    /**
     * 同步帐户交易记录
     * @author zag
     * @date 2022/3/25 11:32
     * @return void
     */
    @XxlJob("tranListQryJobHandler")
    public void tranListQryJobHandler(String tranDate) throws Exception{
        XxlJobHelper.log("XXL-JOB, 同步帐户交易记录开始");
        bankAccountTranService.syncTranRecord(tranDate);
        XxlJobHelper.log("XXL-JOB, 同步帐户交易记录结束");
    }



}
