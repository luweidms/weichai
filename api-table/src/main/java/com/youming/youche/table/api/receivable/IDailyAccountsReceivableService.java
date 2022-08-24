package com.youming.youche.table.api.receivable;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.commons.base.IBaseService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.youming.youche.commons.domain.ImportOrExportRecords;
import com.youming.youche.table.domain.receivable.DailyAccountsReceivable;
import com.youming.youche.table.dto.receivable.ReceivableDetailsDto;
import com.youming.youche.table.vo.receivable.ReceivableDetailsVo;

import java.util.List;

/**
 * <p>
 * 应收日报 服务类
 * </p>
 *
 * @author hzx
 * @since 2022-04-30
 */
public interface IDailyAccountsReceivableService extends IBaseService<DailyAccountsReceivable> {

    /**
     * 日报查询
     *
     * @param name        客户名称
     * @param month       月份
     * @param pageNum
     * @param pageSize
     * @param accessToken
     * @return
     */
    Page<DailyAccountsReceivable> queryDay(String name, String month, Integer pageNum, Integer pageSize, String accessToken);

    /**
     * 日报记录导出
     *
     * @param name                  客户名称
     * @param month                 月份
     * @param importOrExportRecords
     * @param accessToken
     */
    void queryDayExport(String name, String month, ImportOrExportRecords importOrExportRecords, String accessToken);

    /**
     * 日报报执行计划
     */
    void execute();

    /**
     * 应收详情列表查询
     *
     * @param vo
     * @param accessToken
     * @param pageNum
     * @param pageSize
     * @return
     */
    Page<ReceivableDetailsDto> receivableDetails(ReceivableDetailsVo vo, String accessToken, Integer pageNum, Integer pageSize);

    /**
     * 应收详情导出
     *
     * @param vo
     * @param importOrExportRecords
     * @param accessToken
     */
    void receivableDetailsExport(ReceivableDetailsVo vo, ImportOrExportRecords importOrExportRecords, String accessToken);

}
