package com.youming.youche.table.provider.mapper.workbench;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youming.youche.table.domain.workbench.BossWorkbenchMonthInfo;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
* <p>
* 老板工作台-每月运营报表数据Mapper接口
* </p>
* @author zengwen
* @since 2022-05-09
*/
public interface BossWorkbenchMonthInfoMapper extends BaseMapper<BossWorkbenchMonthInfo> {
    /**
     * 获取每月运营报表数据
     * @param tenantId
     * @param startMonth
     * @param endMonth
     * @return
     */
    List<BossWorkbenchMonthInfo> getBossWorkbenchMonthInfoList(@Param("tenantId") Long tenantId, @Param("startMonth") String startMonth, @Param("endMonth") String endMonth);

    /**
     * 获取每月运营报表最新数据
     */
    BossWorkbenchMonthInfo selectBossWorkbenchMonthInfoNew(@Param("tenantId") Long tenantId, @Param("time") String time, @Param("localDateTime") LocalDateTime localDateTime);
}
