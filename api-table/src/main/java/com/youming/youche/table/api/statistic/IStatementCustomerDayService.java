package com.youming.youche.table.api.statistic;

import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.commons.domain.ImportOrExportRecords;
import com.youming.youche.table.domain.statistic.StatementCustomerDay;
import com.youming.youche.table.domain.workbench.BossWorkbenchCustomerInfo;
import com.youming.youche.table.dto.statistic.StatementDepartmentDto;

import java.util.List;

/**
 * <p>
 * 客户日报 服务类
 * </p>
 *
 * @author luwei
 * @since 2022-05-09
 */
public interface IStatementCustomerDayService extends IBaseService<StatementCustomerDay> {


    /***
     * @Description: 客户日报查询
     * @Author: luwei
     * @Date: 2022/5/4 4:26 下午
     * @Param startDate:
     * @Param endDate:
     * @Param accessToken:
     * @return: com.youming.youche.table.dto.statistic.StatementDepartmentDto
     * @Version: 1.0
     **/
    StatementDepartmentDto report(String startDate, String endDate, String accessToken);


    /***
     * @Description: 客户日报导出
     * @Author: luwei
     * @Date: 2022/5/6 11:27 下午
     * @Param startDate:
     * @Param endDate:
     * @Param accessToken:
     * @return: void
     * @Version: 1.0
     **/
    void  exportReport(String startDate, String endDate, String accessToken, ImportOrExportRecords importOrExportRecords);

    /***
     * @Description: 客户日报报表统计
     * @Author: luwei
     * @Date: 2022/5/4 4:26 下午
     * @Param startDate:开始日期
     * @Param endDate: 结束日期
     * @Param flag: 日报月表表示符号（1：日报，2月报）
     * @return: void
     * @Version: 1.0
     **/
    void customerStatisticsDayReport(String startDate, String endDate, Long tenantId);


    /**
     * 老板工作台  获取客户收入排行
     */
    List<BossWorkbenchCustomerInfo> getTableBossCustomerInfo();
}
