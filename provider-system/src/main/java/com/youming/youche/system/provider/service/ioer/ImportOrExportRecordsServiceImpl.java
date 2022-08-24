package com.youming.youche.system.provider.service.ioer;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.domain.ImportOrExportRecords;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.util.LoginUtils;
import com.youming.youche.components.workbench.WorkbenchDto;
import com.youming.youche.system.api.ioer.ImportOrExportRecordsService;
import com.youming.youche.system.provider.mapper.ioer.ImportOrExportRecordsMapper;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * @author 向子俊
 * @Description //TODO 导入导出详情
 * @date 17:23 2022/1/21 0021
 */
@DubboService(version = "1.0.0")
public class ImportOrExportRecordsServiceImpl extends BaseServiceImpl<ImportOrExportRecordsMapper, ImportOrExportRecords>
        implements ImportOrExportRecordsService {

    @Resource
    private ImportOrExportRecordsMapper recordsMapper;

    @Resource
    private LoginUtils loginUtils;


    @Override
    public Page<ImportOrExportRecords> getImportOrExportRecordsList(ImportOrExportRecords records, String accessToken, int pageNum, int pageSize) {
        LoginInfo user = loginUtils.get(accessToken);
        if (user == null) {
            throw new BusinessException("网络异常，稍后重试");
        }
        records.setTenantId(user.getTenantId());
        Page<ImportOrExportRecords> recordPage = new Page<>(pageNum, pageSize);
        return recordsMapper.getImportOrExportRecordsList(recordPage, records);
    }

    @Override
    public ImportOrExportRecords getImportOrExportRecords(Long recordsId) throws Exception {
        return recordsMapper.getImportOrExportRecords(recordsId);
    }

    @Override
    public ImportOrExportRecords saveRecords(ImportOrExportRecords records, String accessToken)  {
        if (Optional.ofNullable(records).isPresent()) {
            LoginInfo user = loginUtils.get(accessToken);
            LocalDateTime dateTime = LocalDateTime.now();
            if (null != records.getId()) {
                records.setUpdateTime(dateTime);
                super.update(records);
            } else {
                records.setOpId(user.getId());
                records.setTenantId(user.getTenantId());
                records.setCreateTime(dateTime);
                super.save(records);
            }
        }
        return records;
    }

     @Override
    public List<WorkbenchDto> getTableImportFailCount() {
        return recordsMapper.getTableImportFailCount();
    }


}
