package com.youming.youche.table.provider.mapper.workbench;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youming.youche.table.domain.workbench.OperationWorkbenchInfo;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;

/**
* <p>
* Mapper接口
* </p>
* @author zengwen
* @since 2022-04-30
*/
public interface OperationWorkbenchInfoMapper extends BaseMapper<OperationWorkbenchInfo> {

    /**
     * 查找最新的运营工作台数据
     */
    OperationWorkbenchInfo selectOperationWorkbenchInfoNew(@Param("tenantId") Long tenantId, @Param("userInfoId") Long userInfoId, @Param("localDateTime") LocalDateTime localDateTime);

    LocalDateTime maxLocalDateTime();
}
