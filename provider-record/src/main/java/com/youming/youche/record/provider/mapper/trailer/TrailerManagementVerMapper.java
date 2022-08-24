package com.youming.youche.record.provider.mapper.trailer;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youming.youche.record.domain.trailer.TrailerManagementVer;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * <p>
 * Mapper接口
 * </p>
 *
 * @author Terry
 * @since 2022-01-19
 */
public interface TrailerManagementVerMapper extends BaseMapper<TrailerManagementVer> {

    List<TrailerManagementVer> getTrailerManagementVerList(@Param("trailerId")Long trailerId,
                                                           @Param("isAutit")Integer isAutit,
                                                           @Param("tenantId")Long tenantId);
    void updtTrailerVerAuditStatus(@Param("newStatus")Integer newStatus,
                                   @Param("trailerId")Long trailerId,
                                   @Param("oldStatus")Integer oldStatus,
                                   @Param("tenantId")Long tenantId);
}
