package com.youming.youche.order.provider.mapper.monitor;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youming.youche.order.domain.monitor.MonitorOrderAgingDepend;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 订单靠台表
 *
 * @author hzx
 * @date 2022/3/9 14:10
 */
public interface MonitorOrderAgingDependMapper extends BaseMapper<MonitorOrderAgingDepend> {

    List<MonitorOrderAgingDepend> queryAgingDependList(@Param("isOrderHis") String isOrderHis, @Param("isHis") String isHis,
                                                       @Param("orderId") Long orderId, @Param("type") Integer type,
                                                       @Param("tenantId") Long tenantId, @Param("sourceRegion") Integer sourceRegion,
                                                       @Param("desRegion") Integer desRegion, @Param("orgId") Long orgId,
                                                       @Param("plateNumber") String plateNumber);

    List<Map> queryAgingDependListNew(@Param("tenantId") Long tenantId, @Param("plateNumber") String plateNumber,
                                      @Param("orgId") Long orgId,
                                      @Param("sourceRegion") Integer sourceRegion, @Param("desRegion") Integer desRegionr);

}
