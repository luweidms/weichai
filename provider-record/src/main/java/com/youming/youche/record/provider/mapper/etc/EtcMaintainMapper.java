package com.youming.youche.record.provider.mapper.etc;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youming.youche.record.domain.etc.EtcMaintain;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Date:2021/12/22
 */
public interface EtcMaintainMapper extends BaseMapper<EtcMaintain> {

    List<EtcMaintain> queryEtcMaintainByVehicleCode(@Param("vehicleCode")String vehicleCode,
                                                    @Param("teantId")Long teantId);
}
