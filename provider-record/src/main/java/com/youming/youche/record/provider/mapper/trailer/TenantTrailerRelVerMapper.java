package com.youming.youche.record.provider.mapper.trailer;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youming.youche.record.domain.trailer.TenantTrailerRelVer;
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
public interface TenantTrailerRelVerMapper extends BaseMapper<TenantTrailerRelVer> {

    List<TenantTrailerRelVer> getTenantTrailerRelVerList(@Param("trailerId")Long trailerId,
                                                         @Param("tenantId")Long tenantId,
                                                         @Param("isAutit")Integer isAutit);

    String getTrailerLineRelVerStr(@Param("trailerId")Long trailerId);

    void updtTrailerRelVerAuditStatus(@Param("newStatus")Integer newStatus,
                                      @Param("trailerId")Long trailerId,
                                      @Param("oldStatus")Integer oldStatus,
                                      @Param("tenantId")Long tenantId);

}
