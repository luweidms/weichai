package com.youming.youche.record.provider.mapper.base;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youming.youche.record.domain.base.BeidouPaymentRecord;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Date:2021/12/22
 */
public interface BeidouPaymentRecordMapper extends BaseMapper<BeidouPaymentRecord> {

    List<BeidouPaymentRecord> queryBeiDouVehicleList(@Param("startTime")String startTime,
                                                     @Param("endTime")String endTime);

    List<BeidouPaymentRecord> getRecordByVehicle (@Param("vehicleCode")String vehicleCode);
}
