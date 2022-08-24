package com.youming.youche.system.api;

import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.system.domain.SysUserOrgRel;
import com.youming.youche.system.vo.CreateSysUserOrgVo;

import java.util.List;

/**
 * <p>
 * 用户组织关系表 服务类
 * </p>
 *
 * @author Terry
 * @since 2022-01-04
 */
public interface ISysUserOrgRelService extends IBaseService<SysUserOrgRel> {

	/**
	 * 方法实现说明  根据用户信息id和租户车队id）查询所属部门
	 * @author      terry
	 * @param userInfoId
	* @param tenantId
	 * @return     List< com.youming.youche.system.domain.SysUserOrgRel>
	 * @exception
	 * @date        2022/1/28 0:08
	 */
	List<SysUserOrgRel> getByUserDataIdAndTenantId(Long userInfoId, Long tenantId);
	/**
	 * 根据用户Id查询所关联的所有组织ID (包括废弃组织)
	 */
	List<Long> selectIdByUserInfoIdAndTenantId(Long userInfoId, Long tenantId);

	List<SysUserOrgRel> getByAccessTokenAndTenantId(String accessToken, Long tenantId);

	int saveUserOragnizeRel(List<Long> orgId, List<Long> userInfoId, Long tenantId, Long opId);

	int updateUserOragnizeRel(List<Long> orgId, List<Long> userInfoId, Long tenantId, Long opId);

	int delUserOragnizeRel( List<Long> userInfoId, Long tenantId);

	SysUserOrgRel selectByOrgIdAndUserId(Long orgId, Long userInfoId);

	List<SysUserOrgRel> selectByTenantIdAndOrgIds(Long tenantId, List<Long> orgId);

	boolean remove(CreateSysUserOrgVo userOrgVo, String accessToken);

	boolean create(CreateSysUserOrgVo sysUserOrgVo, String accessToken);

	boolean remove(Long orgId, String userInfoIds, String accessToken);

	List<String> getOrgNameByUserId(Long userId);

	/**
	 * 根据userId获取该员工的默认组织
	 */
	SysUserOrgRel getStasffDefaultOragnizeByUserId(Long userId, Long tenantId);

}
