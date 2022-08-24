package com.youming.youche.record.api.violation;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.commons.domain.ImportOrExportRecords;
import com.youming.youche.commons.domain.SysUser;
import com.youming.youche.record.domain.violation.ViolationRecord;
import com.youming.youche.record.dto.violation.ViolationDto;
import com.youming.youche.record.vo.violation.ViolationRecordVo;
import com.youming.youche.record.vo.violation.ViolationVo;

/**
 * @date 2022/1/7 15:27
 */
public interface IViolationRecordService extends IBaseService<ViolationRecord> {

    /**
     * 查询车辆违章信息
     */
    Page<ViolationDto> doQuery(Page<ViolationDto> objectPage, ViolationVo violationVo, String accessToken);

    /**
     * 车辆违章导出
     */
    void doQueryExport(ViolationVo violationVo, String accessToken, ImportOrExportRecords importOrExportRecords);

    /**
     * 获取违章具体信息
     */
    ViolationRecord queryById(Long recordId);

    /**
     * 新增或修改
     */
    Long saveOrUpdateViolationRecord(ViolationRecord record, SysUser sysUser, String accessToken) throws Exception;

    /**
     * 实现功能: 分页查询违章明细
     *
     * @param monthTime   查询月份
     * @param licenceType 牌照类型 1整车 2拖头
     * @param recordState 处理状态 0、未处理；1、处理中；2、已完成
     * @return
     */
    Page<ViolationRecordVo> getViolationDetails(Page<ViolationRecordVo> page, String monthTime, Integer licenceType, Integer recordState, String accessToken);

    /**
     * 违章明细导出
     */
    void getViolationDetailsExport(String monthTime, Integer licenceType, Integer recordState, String accessToken, ImportOrExportRecords importOrExportRecords);

    /**
     * 车辆报表查询违章罚款金额
     */
    Long getVehicleViolationAmountByMonth(Long vehicleCode, Long tenantId, String month);

}
