package com.youming.youche.record.api.license;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.commons.domain.ImportOrExportRecords;
import com.youming.youche.record.domain.license.License;
import com.youming.youche.record.dto.license.LicenseDetailsDto;
import com.youming.youche.record.dto.license.ZzxqDto;
import com.youming.youche.record.vo.LicenseDetailsVo;
import com.youming.youche.record.vo.LicenseVo;
import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.record.dto.LicenseDto;

import java.util.List;


public interface ILicenseService extends IBaseService<License> {

    /**
     * 查询征召信息
     */
    Page<LicenseVo> queryAllFindByType(Page<LicenseVo> page, String accessToken, LicenseDto licenseDto);

    /**
     * 证照详情
     */
    List<ZzxqDto> queryAllFindByAll(String accessToken);

    /**
     * 证照详情导出
     */
    void queryAllFindByAllListExport(String accessToken, ImportOrExportRecords record);

    /**
     * 查询车队指定牌照类型车辆年审信息
     *
     * @param licenseDto 牌照类型、证照类型
     */
    List<LicenseVo> queryAll(String accessToken, LicenseDto licenseDto);

    /**
     * 证照到期导出
     */
    void queryAlllist(String accessToken, ImportOrExportRecords record, LicenseDto licenseDto) throws Exception;

    /**
     * 查询证照到期信息
     */
    Page<LicenseVo> selectListByType(Page<LicenseVo> page, String accessToken, LicenseDto licenseDto);

    /**
     * 证照过期信息导出
     */
    void exportList(String accessToken, ImportOrExportRecords record, LicenseDto licenseDto);

    /**
     * 查询证照详情信息
     */
    List<LicenseDetailsVo> selectLicenseDetails(String accessToken);

    /**
     * 证照详情导出
     */
    void exportDetails(String accessToken, ImportOrExportRecords record);
}
