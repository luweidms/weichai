package com.youming.youche.table.provider.mapper.workbench;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youming.youche.table.domain.workbench.BossWorkbenchCustomerInfo;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
* <p>
* 老板工作台   客户收入排行表Mapper接口
* </p>
* @author zengwen
* @since 2022-05-09
*/
public interface BossWorkbenchCustomerInfoMapper extends BaseMapper<BossWorkbenchCustomerInfo> {
    /**
     * 客户收入排行
     * @param tenantId
     * @return
     */
    List<BossWorkbenchCustomerInfo> getTableCustomerInfoList(@Param("tenantId") Long tenantId);

    /**
     * 获取老板工作台员工排行最新记录
     */
    BossWorkbenchCustomerInfo selectBossWorkbenchCustomerInfoNew(@Param("tenantId") Long tenantId, @Param("customerId") Long customerId, @Param("localDateTime") LocalDateTime localDateTime);
}
