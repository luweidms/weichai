package com.youming.youche.system.api;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.system.domain.SysUserRole;
import com.youming.youche.system.dto.OrganizeStaffDto;
import com.youming.youche.system.vo.CreateSysUserRoleVo;
import com.youming.youche.system.vo.DelSysUserRoleVo;

/**
 * <p>
 * 系统角色操作员关系表;ROLE_ID : 对应sys_role中的role_id;op_id : 对应sys_operator中的operator_id 服务类
 * </p>
 *
 * @author Terry
 * @since 2021-12-22
 */
public interface ISysUserRoleService extends IBaseService<SysUserRole> {

	/**
	 * 方法实现说明 用户与角色绑定
	 * @author      terry
	 * @param userRoleVo {@link CreateSysUserRoleVo}
	 *
	 * @return      com.youming.youche.commons.response.ResponseResult
	 * @exception
	 * @date        2022/5/31 15:43
	 */
	boolean create(CreateSysUserRoleVo userRoleVo, String accessToken);

	/**
	 * 方法实现说明 查询角色列表
	 * @author      terry
	 * @param pageNum
	 * @param pageSize
	 * @param roleId 角色id
	 * @param linkman 姓名
	 * @param loginacct 登录账号
	 * @param staffPosition 员工id
	 * @exception
	 * @date        2022/5/31 15:44
	 * @return com.baomidou.mybatisplus.core.metadata.IPage<com.youming.youche.system.dto.OrganizeStaffDto>
	 */
	IPage<OrganizeStaffDto> getRole(Long roleId, String linkman, String loginacct, String staffPosition,
			Integer pageNum, Integer pageSize, String accessToken);

	/**
	 * 方法实现说明 用户与角色解绑
	 * @author      terry
	 * @param delSysUserRoleVo
	 * @return
	 * @exception
	 * @date        2022/5/31 15:47
	 */
	boolean remove(DelSysUserRoleVo delSysUserRoleVo, String accessToken);

	/**
	 * 方法实现说明 删除角色
	 * @author      terry
	 * @param roleId
	 * @return      boolean
	 * @exception
	 * @date        2022/5/31 15:48
	 */
	boolean removeRole(Long roleId);

	/**
	 * 方法实现说明 用户解绑所有角色
	 * @author      terry
	 * @param userId
	 * @return      boolean
	 * @exception
	 * @date        2022/5/31 15:48
	 */
	boolean removeByUserId(Long userId);
}
