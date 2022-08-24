package com.youming.youche.table.provider.mapper.receivable;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.finance.dto.receivable.DailyAccountsReceivableExecuteDto;
import com.youming.youche.table.domain.receivable.DailyAccountsReceivable;
import com.youming.youche.table.dto.receivable.ReceivableDetailsDto;
import com.youming.youche.table.vo.receivable.ReceivableDetailsVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 应收日报Mapper接口
 * </p>
 *
 * @author hzx
 * @since 2022-04-30
 */
public interface DailyAccountsReceivableMapper extends BaseMapper<DailyAccountsReceivable> {

    // 日报（数据源）
    List<DailyAccountsReceivableExecuteDto> executeQuery();

    // 日报（结果）
    Page<DailyAccountsReceivable> queryInfo(Page<DailyAccountsReceivable> page, @Param("tenantId") Long tenantId, @Param("ids") String ids, @Param("month") String month);
    // 日报（结果）-导出
    List<DailyAccountsReceivable> queryInfoExport(@Param("tenantId") Long tenantId, @Param("ids") String ids, @Param("month") String month);

    /**
     * @param page 分页
     * @param vo   查询参数
     * @param type 线路属性和订单类型
     */
    List<ReceivableDetailsDto> receivableDetails(@Param("vo") ReceivableDetailsVo vo, @Param("type") String type);

}
