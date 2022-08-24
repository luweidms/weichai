package com.youming.youche.table.provider.mapper.workbench;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youming.youche.table.domain.workbench.BossWorkbenchInfo;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;

/**
* <p>
* 老板工作台Mapper接口
* </p>
* @author zengwen
* @since 2022-05-09
*/
public interface BossWorkbenchInfoMapper extends BaseMapper<BossWorkbenchInfo> {

    /**
     * 获取最新的老板工作台数据
     */
    BossWorkbenchInfo selectBossWorkbenchInfoNew(@Param("tenantId") Long tenantId, @Param("localDateTime") LocalDateTime localDateTime);

    LocalDateTime maxLocalDateTime();
}
