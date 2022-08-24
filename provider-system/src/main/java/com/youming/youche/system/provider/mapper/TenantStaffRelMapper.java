package com.youming.youche.system.provider.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.system.domain.TenantStaffRel;
import com.youming.youche.system.dto.TenantStaffDto;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 员工信息Mapper接口
 * </p>
 *
 * @author Terry
 * @since 2022-01-04
 */
public interface TenantStaffRelMapper extends BaseMapper<TenantStaffRel> {

	@Select("SELECT a.* FROM tenant_staff_rel a" + " LEFT JOIN sys_tenant_def std ON a.tenant_id = std.id"
			+ " WHERE a.user_info_id = #{userId}" + " AND a.state = 1" + " AND a.lock_flag = 1" + " AND std.state = 1")
	List<TenantStaffRel> getTenantStaffRelByUserId(Long userId);


	IPage<TenantStaffDto> selectAll(Page<TenantStaffRel> page, @Param("phone") String phone,
                                    @Param("linkman") String linkman,
                                    @Param("number") String number,
                                    @Param("position") String position,
                                    @Param("tenantId") Long tenantId,
                                    @Param("lockFlag") Integer lockFlag,
									@Param("orgId") String orgId);

	List<TenantStaffDto>  selectByUserInfoId(@Param("tenantId")Long tenantId,@Param("userInfoId")Long userInfoId);

	Page<TenantStaffRel> queryStaffInfo(Page<TenantStaffRel>page,
										@Param("lockFlag")Integer lockFlag,
										@Param("tenantId")Long tenantId,
										@Param("userAccount")Long userAccount,
										@Param("staffName")String staffName);

	Page<TenantStaffRel> queryOrderStaffInfo(Page<TenantStaffRel>page,
										@Param("lockFlag")Integer lockFlag,
										@Param("tenantId")Long tenantId,
										@Param("userAccount")Long userAccount,
										@Param("staffName")String staffName);

}
