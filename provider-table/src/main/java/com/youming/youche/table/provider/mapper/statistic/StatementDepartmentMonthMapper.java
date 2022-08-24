package com.youming.youche.table.provider.mapper.statistic;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youming.youche.table.domain.statistic.StatementDepartmentMonth;
import com.youming.youche.table.domain.workbench.BossWorkbenchMonthInfo;
import com.youming.youche.table.domain.workbench.WechatOperationWorkbenchInfo;
import com.youming.youche.table.dto.statistic.StatementDepartmentDto;
import com.youming.youche.table.dto.statistic.StatementDepartmentDetailDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 部门日报Mapper接口
 * </p>
 *
 * @author luwei
 * @since 2022-04-27
 */
public interface StatementDepartmentMonthMapper extends BaseMapper<StatementDepartmentMonth> {

    /**
     * 部门月报汇总
     */
    StatementDepartmentDto dailySummary(@Param("startMonth") String startMonth, @Param("endMonth") String endMonth, @Param("tenantId") Long tenantId);

    /**
     * 部门月报明细
     */
    List<StatementDepartmentDetailDto> monthDetail(@Param("startMonth") String startMonth, @Param("endMonth") String endMonth, @Param("tenantId") Long tenantId);

    /**
     * 统计司机工资
     */
    Long sumSalary(@Param("startDateTime") String startDateTime,@Param("endDateTime") String endDateTime,@Param("orgId")Long orgId);




    /**
     * 老板工作台  每月运营报表
     */
    List<BossWorkbenchMonthInfo> getTableBossBusinessMonthInfo();

    /**
     * 车队小程序 每月营运 经营数据
     */
    List<WechatOperationWorkbenchInfo> getTableWechatOperationWorkbenchInfo();

}
