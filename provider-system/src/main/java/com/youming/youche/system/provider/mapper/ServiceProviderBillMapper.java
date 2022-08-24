package com.youming.youche.system.provider.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.system.domain.ServiceProviderBill;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youming.youche.system.dto.BillRecordsDto;
import com.youming.youche.system.dto.ServiceBillDto;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 服务商账单表Mapper接口
 * </p>
 *
 * @author liangyan
 * @since 2022-03-17
 */
public interface ServiceProviderBillMapper extends BaseMapper<ServiceProviderBill> {

    /**
     * 账单记录--维保
     */
    Page<BillRecordsDto> getServiceProviderBillRecords(@Param("page") Page<BillRecordsDto> page, @Param("serviceProviderType") Integer serviceProviderType, @Param("billNo") String billNo, @Param("tenantId") Long tenantId);


    /**
     * 账单记录--核销
     */
    Page<BillRecordsDto> getServiceProviderBillLog(@Param("page") Page<BillRecordsDto> page, @Param("serviceProviderType") Integer serviceProviderType, @Param("billNo") String billNo, @Param("tenantId") Long tenantId);


    /**
     * 获取服务商账单结算信息
     * @param billNo
     * @return
     */
    ServiceBillDto getServiceBillInfo(@Param("billNo") Long billNo);


    /**
     * 结算信息--核销
     *
     * @param serviceProviderType
     * @param billNo
     * @param tenantId
     * @return
     */
    BillRecordsDto getServiceProviderBillInfo(@Param("serviceProviderType") Integer serviceProviderType, @Param("billNo") String billNo, @Param("tenantId") Long tenantId);


    /**
     * 结算信息--维保
     *
     * @param serviceProviderType
     * @param billNo
     * @param tenantId
     * @return
     */
    BillRecordsDto getServiceProviderBillInfoLog(@Param("serviceProviderType") Integer serviceProviderType, @Param("billNo") String billNo, @Param("tenantId") Long tenantId);


    /**
     * 账单结算
     *
     * @param realityBillAmout
     * @param billNo
     * @param tenantId
     * @return
     */
    int ServiceProviderBillBalance(@Param("realityBillAmout") Double realityBillAmout, @Param("billNo") String billNo, @Param("tenantId") Long tenantId);


}
