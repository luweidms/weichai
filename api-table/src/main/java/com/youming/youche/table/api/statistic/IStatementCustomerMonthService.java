package com.youming.youche.table.api.statistic;

import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.commons.domain.ImportOrExportRecords;
import com.youming.youche.table.domain.statistic.StatementCustomerMonth;
import com.youming.youche.table.dto.statistic.StatementDepartmentDto;

/**
 * <p>
 * 客户月报 服务类
 * </p>
 *
 * @author luwei
 * @since 2022-05-09
 */
public interface IStatementCustomerMonthService extends IBaseService<StatementCustomerMonth> {

    /**
     * 月报查询
     */
    StatementDepartmentDto report(String startMonth, String endMonth, String accessToken);

    /***
     * @Description: 部门月报导出
     * @Author: luwei
     * @Date: 2022/5/6 11:27 下午
     * @Param startDate:
     * @Param endDate:
     * @Param accessToken:
     * @return: void
     * @Version: 1.0
     **/
    void  export(String startMonth, String endMonth, String accessToken, ImportOrExportRecords importOrExportRecords);


    /***
     * @Description: 部门月报表统计
     * @Author: luwei
     * @Date: 2022/5/4 4:26 下午
     * @Param startDate:开始日期
     * @Param endDate: 结束日期
     * @Param flag: 日报月表表示符号（1：日报，2月报）
     * @return: void
     * @Version: 1.0
     **/
    void customerStatisticsMonthReport(String startDate, String endDate, Long tenantId);
}
