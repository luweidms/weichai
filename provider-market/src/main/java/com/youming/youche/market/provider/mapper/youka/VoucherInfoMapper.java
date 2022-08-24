package com.youming.youche.market.provider.mapper.youka;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.market.domain.youka.RechargeConsumeRecord;
import com.youming.youche.market.domain.youka.VoucherInfo;
import com.youming.youche.market.dto.youca.RechargeConsumeRecordOut;
import com.youming.youche.market.dto.youca.VoucherInfoDto;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
* <p>
* 代金券信息表Mapper接口
* </p>
* @author Terry
* @since 2022-03-08
*/
    public interface VoucherInfoMapper extends BaseMapper<VoucherInfo> {


    List<VoucherInfoDto> doQueryRechargeConsumeRecord(@Param("strList") List<String> strList, @Param("tenantId") Long tenantId, @Param("serviceUserId") Long serviceUserId);


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
    IPage<RechargeConsumeRecordOut> getRechargeConsumeRecords(@Param("orderId") String orderId,
                                                              @Param("serviceUserId") String serviceUserId,
                                                              @Param("startTime") LocalDateTime recordStartTime,
                                                              @Param("endTime") LocalDateTime recordEndTime,
                                                              @Param("recordType")String recordType,
                                                              @Param("cardType") Integer cardType,
                                                              @Param("tenantName") String tenantName,
                                                              @Param("cardNum") String cardNum,
                                                              @Param("voucherId") String voucherId,
                                                              @Param("plateNumber") String plateNumber,
                                                              @Param("fromType") Integer fromType,
                                                              @Param("serviceName") String serviceName,
                                                              @Param("rebate") Integer rebate,
                                                              @Param("tenantId") Long tenantId,
                                                              @Param("address")String address,
                                                              @Param("dealRemark") String dealRemark,
                                                              Page<RechargeConsumeRecordOut> page );

    List<RechargeConsumeRecord> sumRechargeConsume(@Param("orderId")String orderId,  @Param("serviceUserId")String serviceUserId,@Param("startTime") String startTime, @Param("endTime")  String endTime, @Param("recordType")String recordType,@Param("cardType") Integer cardType, @Param("tenantName")String tenantName,  @Param("cardNum")String cardNum,@Param("voucherId") String voucherId,@Param("plateNumber") String plateNumber, @Param("fromType") Integer fromType, @Param("serviceName") String serviceName, @Param("rebate") Integer rebate, @Param("tenantId") Long tenantId, @Param("address")String address, @Param("dealRemark") String dealRemark);
}
