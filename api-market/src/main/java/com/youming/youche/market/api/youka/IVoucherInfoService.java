package com.youming.youche.market.api.youka;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.market.domain.youka.VoucherInfo;
import com.youming.youche.market.dto.youca.RechargeConsumeRecordOut;
import com.youming.youche.market.dto.youca.VoucherInfoDto;

import java.util.List;

/**
* <p>
    * 代金券信息表 服务类
    * </p>
* @author Terry
* @since 2022-03-08
*/
    public interface IVoucherInfoService extends IBaseService<VoucherInfo> {

    /**
     * 查询油卡充值/消费(返利)记录
     *
     * @param strList       实体油卡卡号
     * @param tenantId      车队id
     * @param serviceUserId 服务商用户id
     */
    List<VoucherInfoDto> doQueryRechargeConsumeRecordList(List<String> strList, Long tenantId, Long serviceUserId);

    /**
     * 油卡充值/消费记录 查询
     * @param serviceUserId 服务商
     * @param startTime 记录开始 yyyy-MM-dd
     * @param endTime 记录结束 yyyy-MM-dd
     * @param recordType 记录类型：1充值，2油卡消费
     * @param cardType 卡类型：1中石油，2中石化
     * @param tenantName 车队名称
     * @param cardNum 油卡卡号
     * @param voucherId
     * @param plateNumber 车牌号
     * @param fromType  来源类型：1供应商油卡，2自购油卡，3客户油卡
     * @param serviceName 服务商名称
     * @param rebate 返利 0 无  1有
     * @param tenantId  车队
     * @param dealRemark 处理结果
     * @return
     * @throws Exception
     */
    IPage<RechargeConsumeRecordOut> getRechargeConsumeRecords(String orderId, String serviceUserId, String recordStartTime,
                                                              String recordEndTime, String recordType, Integer cardType,
                                                              String tenantName, String cardNum, String voucherId,
                                                              String plateNumber, Integer fromType,
                                                              String serviceName, Integer rebate,
                                                              String accessToken, String address, String dealRemark,Integer pageNum,Integer pageSize);

    /**
     * 油卡充值/消费记录 统计 (服务商)
     * startTime 记录开始 yyyy-MM-dd
     * endTime 记录结束 yyyy-MM-dd
     * recordType 记录类型：1充值，2油卡消费
     * cardType 卡类型：1中石油，2中石化
     * tenantName 车队名称
     * cardNum 油卡卡号
     */
    List sumRechargeConsumeRecords(String orderId,String serviceUserId,String startTime,String endTime,
                                   String recordType,Integer cardType,String tenantName,String cardNum,String voucherId,
                                   String plateNumber,Integer fromType,String serviceName,Integer rebate,
                                   String accessToken,String address,String dealRemark);

    /**
     * 判断充值打款记录是否需要提现到实体卡
     * @param busiCode 业务号(payoutIntf表字段busiCode)
     * @return true需要提现(需生成300打款记录)，false不需要提现(无需生成300打款记录)
     * @throws Exception
     */
    boolean judgeRechargeIsNeedWithdrawal(String busiCode, LoginInfo loginInfo);

}
