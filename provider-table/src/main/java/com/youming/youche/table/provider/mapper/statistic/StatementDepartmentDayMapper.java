package com.youming.youche.table.provider.mapper.statistic;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youming.youche.finance.dto.order.OrderInfoDto;
import com.youming.youche.table.domain.statistic.StatementDepartmentDay;
import com.youming.youche.table.domain.workbench.BossWorkbenchDayInfo;
import com.youming.youche.table.domain.workbench.WechatOperationWorkbenchInfo;
import com.youming.youche.table.dto.statistic.DepartmentReportCostFeeDto;
import com.youming.youche.table.dto.statistic.StatementDepartmentDto;
import com.youming.youche.table.dto.statistic.StatementDepartmentDetailDto;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 部门日报Mapper接口
 * </p>
 *
 * @author luwei
 * @since 2022-04-27
 */
public interface StatementDepartmentDayMapper extends BaseMapper<StatementDepartmentDay> {

    /**
     * 部门日报汇总
     */
    StatementDepartmentDto dailySummary(@Param("startDate") String startDate, @Param("endDate") String endDate, @Param("tenantId") Long tenantId);

    /**
     * 部门日报明细
     */
    List<StatementDepartmentDetailDto> dailyDetail(@Param("startDate") String startDate, @Param("endDate") String endDate, @Param("tenantId") Long tenantId);

    /**
     * 按日期、部门查询应收订单数据
     */
    List<OrderInfoDto> queryOrderReceivable(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, @Param("tenantId") Long tenantId,@Param("orgId") Long orgId);

    /**
     * 统计费用上报
     */
    List<DepartmentReportCostFeeDto> sumReportCostFee(@Param("orgId") Long orgId, @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime, @Param("type") Integer type,@Param("tenantId") Long tenantId);

    /***
     * @Description: 统计车辆费用
     * @Author: luwei
     * @Date: 2022/5/5 4:26 下午
     * @return: java.util.List<com.youming.youche.table.dto.statistic.DepartmentReportCostFeeDto>
     * @Version: 1.0
     **/
    List<DepartmentReportCostFeeDto> sumCarFee(@Param("orgId") Long orgId, @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime, @Param("vehicleClass") Integer vehicleClass,@Param("tenantId") Long tenantId);


    /***
     * @Description: 统计车辆维保
     * @Author: luwei
     * @Date: 2022/7/12 11:48
     * @Param orgId:
     * @Param startTime:
     * @Param endTime:
     * @Param vehicleClass:
     * @Param tenantId:
     * @Param appRepairState:
     * @return: java.lang.String
     * @Version: 1.0
     **/
    Long maintenanceSum(@Param("orgId") Long orgId, @Param("state") Integer state,@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime,@Param("tenantId") Long tenantId);

    /***
     * @Description: 统计年审费用
     * @Author: luwei
     * @Date: 2022/5/5 5:05 下午
     * @Param orgId:
     * @Param startTime:
     * @Param endTime:
     * @return: com.youming.youche.table.dto.statistic.DepartmentReportCostFeeDto
     * @Version: 1.0
     **/
    String carAnnualFee(@Param("orgId") Long orgId, @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime,@Param("vehicleClass") Integer vehicleClass,@Param("tenantId") Long tenantId);

    /***
     * @Description: 统计车辆事故
     * @Author: luwei
     * @Date: 2022/5/5 5:38 下午
     * @Param orgId:
     * @Param startTime:
     * @Param endTime:
     * @return: java.lang.Long
     * @Version: 1.0
     **/
    String carAccidentFee(@Param("orgId") Long orgId, @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime,@Param("vehicleClass") Integer vehicleClass,@Param("tenantId") Long tenantId);


    /***
     * @Description: 统计司机上报费用
     * @Author: luwei
     * @Date: 2022/7/10 02:30
     * @Param orgId:
      * @Param startTime:
      * @Param endTime:
      * @Param tenantId:
     * @return: java.lang.String
     * @Version: 1.0
     **/
    Long driverFee(@Param("orgId") Long orgId, @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime,@Param("tenantId") Long tenantId);

    /**
     * 老板工作台  获取每日运营报表
     */
    List<BossWorkbenchDayInfo> getTableBossBusinessDayInfo();

    /**
     * 车队小程序 每日营运 经营数据
     */
    List<WechatOperationWorkbenchInfo> getTableWechatOperationWorkbenchInfo();

}
