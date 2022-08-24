package com.youming.youche.record.provider.mapper.trailer;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.components.workbench.WorkbenchDto;
import com.youming.youche.record.domain.trailer.TrailerManagement;
import com.youming.youche.record.vo.OrderInfoVo;
import com.youming.youche.record.vo.TrailerManagementVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;


/**
 * <p>
 * Mapper接口
 * </p>
 *
 * @author Terry
 * @since 2022-01-19
 */
public interface TrailerManagementMapper extends BaseMapper<TrailerManagement> {

    Page<TrailerManagementVo> doQueryTrailerList(Page<TrailerManagementVo> page,
                                                 @Param("tenantId") Long tenantId,
                                                 @Param("trailerNumber") String trailerNumber,
                                                 @Param("isState") Integer isState,
                                                 @Param("sourceProvince") Integer sourceProvince,
                                                 @Param("sourceRegion") Integer sourceRegion,
                                                 @Param("sourceCounty") Integer sourceCounty,
                                                 @Param("trailerMaterial") Integer trailerMaterial);

    Page<TrailerManagementVo> doQueryTrailerListByOrgId(Page<TrailerManagementVo> page,
                                                 @Param("tenantId") Long tenantId,
                                                 @Param("trailerNumber") String trailerNumber,
                                                 @Param("isState") Integer isState,
                                                 @Param("sourceProvince") Integer sourceProvince,
                                                 @Param("sourceRegion") Integer sourceRegion,
                                                 @Param("sourceCounty") Integer sourceCounty,
                                                 @Param("trailerMaterial") Integer trailerMaterial,
                                                 @Param("orgId") Long orgId);

    List<TrailerManagementVo> doQueryTrailerListExport(@Param("tenantId") Long tenantId,
                                                       @Param("trailerNumber") String trailerNumber,
                                                       @Param("isState") Integer isState,
                                                       @Param("sourceProvince") Integer sourceProvince,
                                                       @Param("sourceRegion") Integer sourceRegion,
                                                       @Param("sourceCounty") Integer sourceCounty,
                                                       @Param("trailerMaterial") Integer trailerMaterial);

    Page<TrailerManagementVo> doQueryTrailerListDel(Page<TrailerManagementVo> page,
                                                    @Param("tenantId") Long tenantId,
                                                    @Param("trailerNumber") String trailerNumber,
                                                    @Param("isState") Integer isState,
                                                    @Param("sourceProvince") Integer sourceProvince,
                                                    @Param("sourceRegion") Integer sourceRegion,
                                                    @Param("sourceCounty") Integer sourceCounty,
                                                    @Param("trailerMaterial") Integer trailerMaterial);

    List<TrailerManagementVo> doQueryTrailerListDelExport(@Param("tenantId") Long tenantId,
                                                          @Param("trailerNumber") String trailerNumber,
                                                          @Param("isState") Integer isState,
                                                          @Param("sourceProvince") Integer sourceProvince,
                                                          @Param("sourceRegion") Integer sourceRegion,
                                                          @Param("sourceCounty") Integer sourceCounty,
                                                          @Param("trailerMaterial") Integer trailerMaterial);

    TrailerManagement getTrailerManagement(@Param("trailerNumber") String trailerNumber,
                                           @Param("tenantId") Long tenantId);

    Long maxId();

    Page<OrderInfoVo> queryTrailerOrderList(Page<OrderInfoVo> page,
                                            @Param("tenantId") Long tenantId,
                                            @Param("trailerPlate") String trailerPlate,
                                            @Param("plateNumber") String plateNumber,
                                            @Param("carUserName") String carUserName,
                                            @Param("carUserPhone") String carUserPhone,
                                            @Param("orderId") Long orderId,
                                            @Param("sourceRegion") Integer sourceRegion,
                                            @Param("desRegion") Integer desRegion,
                                            @Param("dependTimeBegin") String dependTimeBegin,
                                            @Param("dependTimeEnd") String dependTimeEnd);

    String getSourceProvince(@Param("id") Integer id);

    String getSourceRegion(@Param("id") Integer id);

    String getSourceCounty(@Param("id") Integer id);

    List<TrailerManagement> getTrailerQuery(@Param("trailerNumber") String trailerNumber,
                                            @Param("tenantId") Long tenantId);

    List<WorkbenchDto> getTableTrailerCarCount();

    /**
     * 模糊查询挂车信息
     *
     * @param page
     * @param trailerNumber
     * @param tenantId
     * @return
     */
    Page<TrailerManagement> getLocalNotUsedTrailerPage(Page<TrailerManagement> page, @Param("trailerNumber") String trailerNumber, @Param("tenantId") Long tenantId);
}
