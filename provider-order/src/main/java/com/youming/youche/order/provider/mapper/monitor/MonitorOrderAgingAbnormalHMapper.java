package com.youming.youche.order.provider.mapper.monitor;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youming.youche.order.domain.monitor.MonitorOrderAgingAbnormalH;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 异常数据历史表
 *
 * @author hzx
 * @date 2022/3/11 16:17
 */
public interface MonitorOrderAgingAbnormalHMapper extends BaseMapper<MonitorOrderAgingAbnormalH> {

    List<Map<String, Object>> getValidityTrailerQuery(@Param("fieldName") String fieldName, @Param("breifName") String breifName,
                                                      @Param("tenantId") Long tenantId, @Param("expireDate") Date expireDate,
                                                      @Param("state") Integer state, @Param("plateNumber") String plateNumber);

    List<Map<String, Object>> getSearchVehicleQuery(@Param("fieldName") String fieldName, @Param("breifName") String breifName,
                                                    @Param("tenantId") Long tenantId, @Param("expireDate") Date expireDate,
                                                    @Param("state") Integer state, @Param("plateNumber") String plateNumber,
                                                    @Param("vehicleClass") Integer vehicleClass);

    List<Map<String, Object>> getSearchTrailerQuery(@Param("fieldName") String fieldName, @Param("breifName") String breifName,
                                                    @Param("tenantId") Long tenantId, @Param("expireDate") Date expireDate,
                                                    @Param("state") Integer state, @Param("plateNumber") String plateNumber);

}
