package com.youming.youche.table.provider.mapper.workbench;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youming.youche.table.domain.workbench.FinancialWorkbenchInfo;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;

/**
* <p>
* Mapper接口
* </p>
* @author zengwen
* @since 2022-04-30
*/
public interface FinancialWorkbenchInfoMapper extends BaseMapper<FinancialWorkbenchInfo> {

    /**
     * 获取最新的财务工作台数据
     */
    FinancialWorkbenchInfo selectFinancialWorkbenchInfoNew(@Param("tenantId") Long tenantId, @Param("userInfoId") Long userInfoId, @Param("localDateTime") LocalDateTime localDateTime);

    /**
     * 获取财务工作台最新数据的创建时间
     */
    LocalDateTime maxLocalDateTime();
}
