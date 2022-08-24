package com.youming.youche.record.provider.mapper.trailer;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youming.youche.components.workbench.WorkbenchDto;
import com.youming.youche.record.domain.trailer.TenantTrailerRel;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;


/**
 * <p>
 * Mapper接口
 * </p>
 *
 * @author Terry
 * @since 2022-01-19
 */
public interface TenantTrailerRelMapper extends BaseMapper<TenantTrailerRel> {

    TenantTrailerRel getTenantTrailerRelByTrailerId(@Param("trailerId")Long trailerId,
                                                    @Param("tenantId")Long tenantId);

    TenantTrailerRel getTenantTrailerRel(@Param("trailerNumber")String trailerNumber,
                                         @Param("tenantId")Long tenantId);

    Integer updtRecordAuditStatus(@Param("trailerId")Long trailerId, @Param("newStatus")Integer newStatus,
                                  @Param("oldStatus")Integer oldStatus);

    Integer updtTrailerRelAuditStatus(@Param("trailerId")Long trailerId, @Param("newStatus")int newStatus,
                                      @Param("oldStatus")int oldStatus,@Param("tenantId") Long tenantId);

    Integer updtLineRelStatus(@Param("trailerId")Long trailerId, @Param("newStatus")int newStatus, @Param("oldStatus")int oldStatus);

    Integer updtTrailerVerAuditStatus(@Param("trailerId")Long trailerId,@Param("newStatus") int newStatus,
                                      @Param("oldStatus")int oldStatus,@Param("tenantId") Long tenantId);

    Integer updtTrailerRelVerAuditStatus(@Param("trailerId")Long trailerId,@Param("newStatus")Integer newStatus,
                                         @Param("oldStatus")Integer oldStatus,@Param("tenantId")Long tenantId);

    Integer updtLineRelVerStatus(@Param("trailerId")Long trailerId,@Param("newStatus")Integer newStatus,
                                 @Param("oldStatus")Integer oldStatus);

    List<WorkbenchDto> getTableTrailerCount(@Param("localDateTime") LocalDateTime localDateTime);
}
