package com.youming.youche.system.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.pagehelper.PageInfo;
import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.system.domain.ServiceProviderBill;
import com.youming.youche.system.dto.BillRecordsDto;
import com.youming.youche.system.dto.ServiceBillDto;

import java.time.LocalDateTime;

/**
* <p>
    * 服务商账单表 服务类
    * </p>
* @author liangyan
* @since 2022-03-17
*/
    public interface IServiceProviderBillService extends IBaseService<ServiceProviderBill> {

    /**
     * 通过服务商名称、业务单号、服务商类型、打款状态查询list
     * @param serviceProviderName 服务商名称
     * @param billRecordsNo 业务单号
     * @param serviceProviderType 服务商类型 服务商类型 1.油站、2.维修、3.etc供应商
     * @param paymentStatus 打款状态 0：未打款:1：待确认:2：打款中:3：已打款
     * @param accessToken
     * @return
     */
    PageInfo<ServiceProviderBill> getServiceProviderBillList (Integer pageNum, Integer pageSize,
                                                              String serviceProviderName,
                                                              String billRecordsNo, Integer serviceProviderType,
                                                              Integer paymentStatus,String billNo, String accessToken );


    ServiceProviderBill queryServiceProviderBill(Long serviceUserId, LocalDateTime time,Long tenantId);

    /**
     * 账单记录
     * @param serviceProviderType
     * @param billNo
     * @param tenantId
     * @param pageNum
     * @param pageSize
     * @param accessToken
     * @return
     */
    Page<BillRecordsDto> getServiceProviderBillRecords(Integer serviceProviderType,String billNo, Long tenantId,Integer pageNum, Integer pageSize,String accessToken);

    /**
     * 结算信息
     * @param billNo
     * @return
     */
    ServiceBillDto getServiceProviderBillInfo(Long billNo);


    /**
     * 账单结算
     * @param realityBillAmout
     * @param billNo
     * @param tenantId
     * @return
     */
    int ServiceProviderBillBalance(Double realityBillAmout, String billNo, Long tenantId,String accessToken);



    Long saveServiceProviderBillReturnId(ServiceProviderBill serviceProviderBill);

    /**
     * 服务商小程序获取应收逾期金额
     */
    Long getReceivableOverdueBalance(String accessToken);

}
