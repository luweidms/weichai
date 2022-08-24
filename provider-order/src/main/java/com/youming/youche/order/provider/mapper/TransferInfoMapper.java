package com.youming.youche.order.provider.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youming.youche.order.domain.TransferInfo;
import org.apache.ibatis.annotations.Param;

import java.util.Date;

/**
* <p>
* Mapper接口
* </p>
* @author liangyan
* @since 2022-03-14
*/
    public interface TransferInfoMapper extends BaseMapper<TransferInfo> {
    void updateOrderTransferState(@Param("orderId") Long orderId,@Param("acceptTenantId") Long tenantId,@Param("transferOrderState") Integer billYes,@Param("opDate") Date date,@Param("plateNumber") String plateNumber,@Param("remark") String remark,@Param("transferOrderId") Long toOrderId);
}
