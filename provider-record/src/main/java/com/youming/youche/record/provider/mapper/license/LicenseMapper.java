package com.youming.youche.record.provider.mapper.license;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.record.domain.license.License;
import com.youming.youche.record.dto.LicenseDto;
import com.youming.youche.record.dto.license.LicenseDetailsDto;
import com.youming.youche.record.dto.license.ZzxqDto;
import com.youming.youche.record.vo.LicenseVo;
import org.apache.ibatis.annotations.Param;
import org.mybatis.spring.annotation.MapperScan;

import java.util.List;

@MapperScan
public interface LicenseMapper extends BaseMapper<License> {


    Page<LicenseVo> queryAllFindByType(Page<LicenseVo> page, @Param("tenantId") Long tenantId,@Param("licenseDto") LicenseDto licenseDto);
    List<ZzxqDto> queryAllFindByAll(@Param("tenantId") Long tenantId);

    List<LicenseVo> queryAll(Long tenantId, LicenseDto licenseDto);

    /**
     * 根据证照类型查询证照到期列表（分页）
     * @author zag
     * @date 2022/5/3 17:17
     * @param page
     * @param tenantId
     * @param licenseDto
     * @return com.baomidou.mybatisplus.extension.plugins.pagination.Page<com.youming.youche.record.vo.LicenseVo>
     */
    Page<LicenseVo> selectListByType(Page<LicenseVo> page, @Param("tenantId") Long tenantId,@Param("licenseDto") LicenseDto licenseDto);

    /**
     * 根据证照类型查询证照到期列表
     * @author zag
     * @date 2022/5/3 18:22
     * @param tenantId
     * @param licenseDto
     * @return java.util.List<com.youming.youche.record.vo.LicenseVo>
     */
    List<LicenseVo> selectListByType(@Param("tenantId") Long tenantId,@Param("licenseDto") LicenseDto licenseDto);

    /**
     * 查询车辆最新证照到期信息
     * @author zag
     * @date 2022/5/4 18:35
     * @param tenantId 
     * @return java.util.List<com.youming.youche.record.dto.license.LicenseDetailsDto>
     */
    List<LicenseDetailsDto> selectLicenseDetails(@Param("tenantId") Long tenantId);

}
