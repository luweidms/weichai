package com.youming.youche.table.provider.mapper.statistic;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youming.youche.table.domain.statistic.StatementCustomerMonth;
import com.youming.youche.table.dto.statistic.StatementDepartmentDetailDto;
import com.youming.youche.table.dto.statistic.StatementDepartmentDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 客户月报Mapper接口
 * </p>
 *
 * @author luwei
 * @since 2022-05-09
 */
public interface StatementCustomerMonthMapper extends BaseMapper<StatementCustomerMonth> {

    /**
     * 部门月报汇总
     */
    StatementDepartmentDto dailySummary(@Param("startMonth") String startMonth, @Param("endMonth") String endMonth, @Param("tenantId") Long tenantId);

    /**
     * 部门月报明细
     */
    List<StatementDepartmentDetailDto> monthDetail(@Param("startMonth") String startMonth, @Param("endMonth") String endMonth, @Param("tenantId") Long tenantId);


}
