package com.youming.youche.table.provider.mapper.statistic;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youming.youche.finance.dto.order.OrderInfoDto;
import com.youming.youche.record.domain.cm.CmCustomerInfo;
import com.youming.youche.table.domain.statistic.StatementCustomerDay;
import com.youming.youche.table.domain.workbench.BossWorkbenchCustomerInfo;
import com.youming.youche.table.dto.statistic.DepartmentReportCostFeeDto;
import com.youming.youche.table.dto.statistic.StatementDepartmentDetailDto;
import com.youming.youche.table.dto.statistic.StatementDepartmentDto;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 客户日报Mapper接口
 * </p>
 *
 * @author luwei
 * @since 2022-05-09
 */
public interface StatementCustomerDayMapper extends BaseMapper<StatementCustomerDay> {

    /**
     * 客户日报汇总
     */
    StatementDepartmentDto dailySummary(@Param("startDate") String startDate, @Param("endDate") String endDate, @Param("tenantId") Long tenantId);

    /**
     * 客户日报明细
     */
    List<StatementDepartmentDetailDto> dailyDetail(@Param("startDate") String startDate, @Param("endDate") String endDate, @Param("tenantId") Long tenantId);

    /***
     * @Description: 按日期、客户查询应收订单数据
     * @Author: luwei
     * @Date: 2022/5/9 1:46 下午
     * @Param startDate:
     * @Param endDate:
     * @Param tenantId:
     * @Param customerId:
     * @return: java.util.List<com.youming.youche.finance.dto.order.OrderInfoDto>
     * @Version: 1.0
     **/
    List<OrderInfoDto> queryOrderReceivable(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, @Param("tenantId") Long tenantId, @Param("customerId") Long customerId);

    /***
     * @Description: 查询客户id
     * @Author: luwei
     * @Date: 2022/5/9 1:46 下午
     * @Param tenantId: 车队id
     * @return: java.util.List<com.youming.youche.record.domain.cm.CmCustomerInfo>
     * @Version: 1.0
     **/
    List<CmCustomerInfo> queryCustomerId(@Param("tenantId") Long tenantId);

    /**
     * 统计费用上报
     */
    List<DepartmentReportCostFeeDto> sumReportCostFee(@Param("customerId") Long customerId, @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime, @Param("type") Integer type, @Param("tenantId") Long tenantId);

    /***
     * @Description: 客户id查询借支
     * @Author: luwei
     * @Date: 2022/5/5 10:17 上午

     * @return: com.youming.youche.finance.domain.OaLoan
     * @Version: 1.0
     **/
    Long queryAdvanceCid(@Param("customerId") Long customerId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, @Param("lauach") Integer lauach, @Param("tenantId") Long tenantId);

    /**
     * 老板工作台  获取客户收入排行
     */
    List<BossWorkbenchCustomerInfo> getTableBossCustomerInfo();
}
