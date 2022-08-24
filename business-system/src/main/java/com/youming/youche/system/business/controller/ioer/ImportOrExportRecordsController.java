package com.youming.youche.system.business.controller.ioer;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.commons.base.BaseController;
import com.youming.youche.commons.domain.ImportOrExportRecords;
import com.youming.youche.commons.domain.SysUser;
import com.youming.youche.commons.response.ResponseResult;
import com.youming.youche.commons.web.Header;
import com.youming.youche.system.api.ISysUserService;
import com.youming.youche.system.api.ioer.ImportOrExportRecordsService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description TODO导入导出详情
 */
@RestController
@RequestMapping("/sys/records")
public class ImportOrExportRecordsController extends BaseController<ImportOrExportRecords, ImportOrExportRecordsService> {

    @DubboReference(version = "1.0.0")
    ImportOrExportRecordsService recordsService;

    @DubboReference(version = "1.0.0")
    ISysUserService sysUserService;

    @Value("${spring.profiles.active}")
    private String configuration;

    @Override
    public ImportOrExportRecordsService getService() {
        return recordsService;
    }

    /**
     * 查询记录分页列表
     *
     * @param records
     * @param pageNum
     * @param pageSize
     * @Title：queryRecordsInfoPage
     * @Description：TODO(这里用一句话描述这个方法的作用)
     * @Author：卢威
     * @Date：2021年4月19日 下午4:46:03
     */
    @GetMapping(value = "/queryRecordsInfoPage")
    public ResponseResult queryRecordsInfoPage(ImportOrExportRecords records, @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum, @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        Page<ImportOrExportRecords> pageRecordList = recordsService.getImportOrExportRecordsList(records, accessToken, pageNum, pageSize);
        for (ImportOrExportRecords importOrExportRecords : pageRecordList.getRecords()
        ) {
            if (importOrExportRecords.getOpId() != null) {
                SysUser sysUser = sysUserService.getById(importOrExportRecords.getOpId());
                if (sysUser != null && sysUser.getName() != null) {
                    importOrExportRecords.setOpName(sysUser.getName());
                }
            }
        }
        if (configuration.equals("pro")) {
            for (ImportOrExportRecords importOrExportRecords : pageRecordList.getRecords()
            ) {
                if(StrUtil.isNotBlank(importOrExportRecords.getFailureUrl())){
                    importOrExportRecords.setFailureUrl(importOrExportRecords.getFailureUrl().replaceAll("group1/M00/",""));
                }
                if(StrUtil.isNotBlank(importOrExportRecords.getMediaUrl())){
                    importOrExportRecords.setMediaUrl(importOrExportRecords.getMediaUrl().replaceAll("group1/M00/",""));
                }
            }
        }
        return ResponseResult.success(pageRecordList);

    }
}
