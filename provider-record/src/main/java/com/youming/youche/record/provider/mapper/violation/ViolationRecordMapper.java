package com.youming.youche.record.provider.mapper.violation;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.record.domain.violation.ViolationRecord;
import com.youming.youche.record.dto.violation.ViolationDto;
import com.youming.youche.record.vo.violation.ViolationRecordVo;
import com.youming.youche.record.vo.violation.ViolationVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @date 2022/1/7 16:16
 */
public interface ViolationRecordMapper extends BaseMapper<ViolationRecord> {

    /**
     * 查询车辆违章信息
     */
    Page<ViolationDto> doQuery(Page<ViolationDto> objectPage, @Param("param") ViolationVo param, @Param("tenantId") Long tenantId);

    List<ViolationDto> doQueryExport(@Param("param") ViolationVo param, @Param("tenantId") Long tenantId);

    /**
     * 通过文书号查询违章在系统是否存在
     *
     * @param violationWritNo 文书号
     * @return 返回查询条数
     */
    long queryCountByViolationWritNo(@Param("violationWritNo") String violationWritNo);

    Page<ViolationRecordVo> getViolationDetails(Page<ViolationRecordVo> page,
                                                @Param("monthTimeStart") String monthTimeStart,
                                                @Param("monthTimeEnd") String monthTimeEnd,
                                                @Param("licenceType") Integer licenceType,
                                                @Param("recordState") Integer recordState,
                                                @Param("tenantId") Long tenantId);

    List<ViolationRecordVo> getViolationDetailsExport(@Param("monthTimeStart") String monthTimeStart,
                                                      @Param("monthTimeEnd") String monthTimeEnd,
                                                      @Param("licenceType") Integer licenceType,
                                                      @Param("recordState") Integer recordState,
                                                      @Param("tenantId") Long tenantId);

    /**
     * 车辆报表查询违章罚款金额
     */
    Long getVehicleViolationAmountByMonth(@Param("vehicleCode") Long vehicleCode, @Param("tenantId") Long tenantId, @Param("month") String month);

}
