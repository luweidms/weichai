package com.youming.youche.system.api.ioer;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.commons.domain.ImportOrExportRecords;
import com.youming.youche.components.workbench.WorkbenchDto;

import java.util.List;

public interface ImportOrExportRecordsService extends IBaseService<ImportOrExportRecords> {

    /**
     * 查询导入/导出记录列表
     */
    Page<ImportOrExportRecords> getImportOrExportRecordsList(ImportOrExportRecords records, String accessToken,int pageNum,int pageSize);

    /**
     * 查询导入/导出详情
     */
    ImportOrExportRecords getImportOrExportRecords(Long recordsId) throws Exception;

    /**
     * 保存导入/导出记录
     */
    ImportOrExportRecords saveRecords(ImportOrExportRecords records, String accessToken) ;

    /**
     * 营运工作台 导入失败次数
     */
    List<WorkbenchDto> getTableImportFailCount();
}
