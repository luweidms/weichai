package com.youming.youche.table.api.receivable;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.commons.base.IBaseService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.youming.youche.commons.domain.ImportOrExportRecords;
import com.youming.youche.table.domain.receivable.MonthlyReportReceivable;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author hzx
 * @since 2022-04-30
 */
public interface IMonthlyReportReceivableService extends IBaseService<MonthlyReportReceivable> {

    /**
     * 分页应收月报查询
     *
     * @param accessToken
     * @return
     */
    Page<MonthlyReportReceivable> queryMonth(String accessToken, Integer pageNum, Integer pageSize);

    /**
     * 应收月报查询导出
     *
     * @param accessToken
     * @return
     */
    List<MonthlyReportReceivable> queryMonthExport(String accessToken);

    /**
     * 应收月报查询
     *
     * @param importOrExportRecords
     * @param accessToken
     */
    void queryMonthExport(ImportOrExportRecords importOrExportRecords, String accessToken);

    /**
     * 月报执行计划
     */
    void execute();

}
