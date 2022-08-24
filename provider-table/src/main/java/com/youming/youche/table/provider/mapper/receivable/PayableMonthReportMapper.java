package com.youming.youche.table.provider.mapper.receivable;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.table.domain.receivable.PayableMonthReport;
import com.youming.youche.table.dto.receivable.PayableMonthReportDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
* <p>
* 应付月报表Mapper接口
* </p>
* @author zengwen
* @since 2022-05-11
*/
    public interface PayableMonthReportMapper extends BaseMapper<PayableMonthReport> {

    /**
     * 获取应付月报
     */
    Page<PayableMonthReportDto> getPayableMonthReportPage(@Param("tenantId") Long tenantId, Page<PayableMonthReportDto> page);

    /**
     * 获取应付月报
     * @param tenantId
     * @return
     */
    List<PayableMonthReportDto> getPayableMonthReportList(@Param("tenantId") Long tenantId);

    }
