package com.youming.youche.system.provider.mapper.tenant;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youming.youche.system.domain.tenant.SysTenantBusinessState;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * <p>
 * 车队经营状况Mapper接口
 * </p>
 *
 * @author Terry
 * @since 2022-01-09
 */
public interface SysTenantBusinessStateMapper extends BaseMapper<SysTenantBusinessState> {
    /***
     * @Description: 修改拜访记录
     * @Author: luwei
     * @Date: 2022/1/18 2:33 下午
     * @Param tenantId: 车队id
     * @Param visitId: sys_tenant_visit关联键
     * @return: int
     * @Version: 1.0
     **/
    @Update("update sys_tenant_visit_record set tenant_id = #{tenantId} where visit_id = #{visitId}")
    int correlateVisitRecord(@Param("tenantId") long tenantId, @Param("visitId") long visitId);

    /***
     * @Description: 修改车队经营信息
     * @Author: luwei
     * @Date: 2022/8/24 15:32
     * @Param sysTenantBusinessState: 车队经营状况实体
      * @Param tenantId:
     * @return: int
     * @Version: 1.0
     **/
    int updateBusinessState(@Param("sysTenantBusinessState") SysTenantBusinessState sysTenantBusinessState, @Param("tenantId") Long tenantId);

}
