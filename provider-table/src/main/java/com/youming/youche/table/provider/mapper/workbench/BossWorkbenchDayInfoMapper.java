package com.youming.youche.table.provider.mapper.workbench;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youming.youche.table.domain.workbench.BossWorkbenchDayInfo;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
* <p>
* 老板工作台-每日运用报表数据Mapper接口
* </p>
* @author zengwen
* @since 2022-05-09
*/
public interface BossWorkbenchDayInfoMapper extends BaseMapper<BossWorkbenchDayInfo> {

    /**
     * 获取老板工作台每日运营报表数据
     */
    List<BossWorkbenchDayInfo> getBossWorkbenchDayInfoList(@Param("tenantId") Long tenantId, @Param("startTime") String startTime, @Param("endTime") String endTime);

    /**
     * 获取老板工作台最新每日运营报表数据
     */
    BossWorkbenchDayInfo selectBossWorkbenchDayInfoNew(@Param("tenantId") Long tenantId, @Param("time") String time, @Param("localDateTime") LocalDateTime localDateTime);
}
