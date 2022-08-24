package com.youming.youche.table.api.statistic;

import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.commons.domain.ImportOrExportRecords;
import com.youming.youche.table.domain.statistic.StatementDepartmentDay;
import com.youming.youche.table.domain.workbench.BossWorkbenchDayInfo;
import com.youming.youche.table.domain.workbench.WechatOperationWorkbenchInfo;
import com.youming.youche.table.dto.statistic.StatementDepartmentDto;

import java.util.List;

/**
 * <p>
 * 部门日报 服务类
 * </p>
 *
 * @author luwei
 * @since 2022-04-27
 */
public interface IStatementDepartmentDayService extends IBaseService<StatementDepartmentDay> {

    /***
     * @Description: 部门日报查询
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
     * @Description: 部门日报导出
     * @Author: luwei
     * @Date: 2022/5/6 11:27 下午
     * @Param startDate:
      * @Param endDate:
      * @Param accessToken:
     * @return: void
     * @Version: 1.0
     **/
    void  exportReport(String startDate, String endDate, String accessToken,ImportOrExportRecords importOrExportRecords);

    /***
     * @Description: 部门日报报表统计
     * @Author: luwei
     * @Date: 2022/5/4 4:26 下午
     * @Param startDate:开始日期
     * @Param endDate: 结束日期
     * @Param flag: 日报月表表示符号（1：日报，2月报）
     * @return: void
     * @Version: 1.0
     **/
    void departmentStatisticsDayReport(String startDate, String endDate, Long tenantId);





    /**
     * 老板工作台  获取每日运营报表
     * @return
     */
    List<BossWorkbenchDayInfo> getTableBossBusinessDayInfo();

    /**
     * 车队小程序 每日营运 经营数据
     */
    List<WechatOperationWorkbenchInfo> getTableWechatOperationWorkbenchInfo();
}
