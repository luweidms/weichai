package com.youming.youche.system.provider.mapper.ioer;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.commons.domain.ImportOrExportRecords;
import com.youming.youche.components.workbench.WorkbenchDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ImportOrExportRecordsMapper extends BaseMapper<ImportOrExportRecords> {

    /**
     * @author 向子俊
     * @Description //TODO 导入导出详情
     * @date 18:26 2022/1/21 0021
     * @Param [records]
     * @return java.util.List<com.youming.youche.domain.ioer.ImportOrExportRecords>
     */
    Page<ImportOrExportRecords> getImportOrExportRecordsList(
            Page<ImportOrExportRecords> recordPage,
            @Param("records") ImportOrExportRecords records);

    /**
     * @author 向子俊
     * @Description //TODO 导入导出详情
     * @date 18:26 2022/1/21 0021
     * @Param [recordsId]
     * @return com.youming.youche.domain.ioer.ImportOrExportRecords
     */
    ImportOrExportRecords getImportOrExportRecords(@Param("recordsId") Long recordsId) throws Exception;

    /**
     * @author 向子俊
     * @Description //TODO 新添导入/导出记录
     * @date 18:26 2022/1/21 0021
     * @Param [records]
     * @return int
     */
    int insertRecords(@Param("records") ImportOrExportRecords records) throws Exception;

    /**
     * @author 向子俊
     * @Description //TODO 修改导入/导出记录
     * @date 19:31 2022/1/21 0021
     * @Param [records]
     * @return int
     */
    int updateRecords(@Param("records") ImportOrExportRecords records) throws Exception;

    /**
     * 运营工作台  导入失败次数
     * @return
     */
    List<WorkbenchDto> getTableImportFailCount();
}
