package com.youming.youche.table.provider.mapper.receivable;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.table.domain.receivable.PayableDayReport;
import com.youming.youche.table.dto.receivable.PayableDayReportDto;
import com.youming.youche.table.dto.receivable.PayableDayReportSumDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* <p>
* Mapper接口
* </p>
* @author zengwen
* @since 2022-05-10
*/
public interface PayableDayReportMapper extends BaseMapper<PayableDayReport> {
    /**
     * 获取应付日报数据
     * @param tenantId
     * @param time
     * @param userName
     * @return
     */
    List<PayableDayReportDto> getPayableDayReportList(@Param("tenantId") Long tenantId, @Param("time") String time, @Param("userName") String userName);

    /**
     * 获取应付日报数据
     * @param page
     * @param tenantId
     * @param time
     * @param userName
     * @return
     */
    Page<PayableDayReportDto> getPayableDayReportPage(Page<PayableDayReportDto> page, @Param("tenantId") Long tenantId, @Param("time") String time, @Param("userName") String userName);

    /**
     * 获取应付日报总数据
     * @param tenantId
     * @param time
     * @param userName
     * @return
     */
    PayableDayReportSumDto getPayableDayReportSum(@Param("tenantId") Long tenantId, @Param("time") String time, @Param("userName") String userName);
}
