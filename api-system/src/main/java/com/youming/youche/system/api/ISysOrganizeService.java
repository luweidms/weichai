package com.youming.youche.system.api;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.conts.SysStaticDataEnum;
import com.youming.youche.system.domain.SysOrganize;

import java.util.List;

/**
 * <p>
 * 组织表 服务类
 * </p>
 *
 * @author Terry
 * @since 2022-01-05
 */
public interface ISysOrganizeService extends IBaseService<SysOrganize> {

	/**
	 * 获取当前租户下所有组织
	 * @param state 组织的状态（0：废弃， 1：可用， null：全部）
	 * @param hiddenNode 是否包含隐藏的根节点(如果不包含隐藏根节点无法构建组织树)
	 */
	List<SysOrganize> querySysOrganizeTree(String accessToken, Integer state, boolean hiddenNode);

	/**
	 * 获取当前租户下所有组织
	 * @param state 组织的状态（0：废弃， 1：可用， null：全部）
	 * @param hiddenNode 是否包含隐藏的根节点(如果不包含隐藏根节点无法构建组织树)
	 */
	List<SysOrganize> querySysOrganizeTreeTrailer(String accessToken, Integer state, boolean hiddenNode);

	/**
	 * 获取当前租户下所有组织
	 * @param i
	 * @param state 组织的状态（0：废弃， 1：可用， null：全部）
	 * @param pageNum
	 * @param orgName
	 * @return
	 */
	IPage<SysOrganize> querySysOrganize(String accessToken, Integer state, Integer pageNum, Integer pageSize, String orgName);

	/**
	 * 获取当前租户下所有组织
	 * @param state 组织的状态（0：废弃， 1：可用， null：全部）
	 */
	List<SysOrganize> querySysOrganize(Long tenantId, Integer state);
	/**
	 * 获取当前租户下所有组织
	 * @param state 组织的状态（0：废弃， 1：可用， null：全部）
	 */
	List<SysOrganize> querySysOrganize(String accessToken, Integer state);

	/**
	 * 根据组织ID获取数据组织名称
	 * @param orgId 网点id
	 * @return
	 */
	String getOrgNameByOrgId(Long orgId,Long tenantId);

	/**
	 * 当前租户下，根据orgId获取组织名称
	 */
	String getCurrentTenantOrgNameById(Long tenantId, Long orgId);

	/**
	 * 获取当前部门以及所有子部门
	 *
	 * @param tenantId
	 * @param orgId 部门编号
	 * @return
	 * @throws Exception
	 */
	List<Long> getSubOrgList(Long tenantId,List<Long> orgId);

	/**
	 * 获取当前部门以及所有子部门
	 *
	 * @param accessToken 登录者令牌
	 * @return
	 * @throws Exception
	 */
	List<Long> getSubOrgList(String accessToken);

	/**
	 * 方法实现说明  新增部门
	 * @author      terry
	 * @param sysOrganize
	* @param accessToken
	 * @return      boolean
	 * @exception
	 * @date        2022/5/31 13:31
	 */
	boolean create(SysOrganize sysOrganize, String accessToken);

	/**
	 * 方法实现说明  更新部门
	 * @author      terry
	 * @param sysOrganize
	* @param accessToken
	 * @return      boolean
	 * @exception
	 * @date        2022/5/31 13:31
	 */
	boolean update(SysOrganize sysOrganize, String accessToken);
	/**
	 * 方法实现说明  删除部门
	 * @author      terry
	 * @param id	部门id
	* @param accessToken
	 * @return      boolean
	 * @exception
	 * @date        2022/5/31 13:31
	 */
	boolean remove(Long id, String accessToken);

	/**
	 * 方法实现说明  查询用户所属部门
	 * @author      terry
	* @param accessToken 令牌
	 * @return      boolean
	 * @exception
	 * @date        2022/5/31 13:31
	 */
    List<SysOrganize> selectAllByAccessToken(String accessToken);

	/**
	 * 方法实现说明  根据车队，上级id查询车队子部门，
	 * @author      terry
	 * @param tid 车队id
	 * @param parentOrgId 上级id
	 * @param accessToken
	 * @return      boolean
	 * @date        2022/5/31 13:31
	 */
	List<SysOrganize> querySysOrganizeList(Long tid,Long parentOrgId,Integer status);


	/**
	 * 根据用户Id查询所关联的所有组织ID
	 * @param userInfoId 用户信息id
	 * @return List<SysOrganize>
	 */
	List<SysOrganize> selectByUserInfoIdAndTenantId(Long userInfoId, Long tenantId);

	/**
	 * 方法实现说明  根据车队id，查询车队部门，
	 * @author      terry
	 * @param tenantId 车队id
	 * @return      boolean
	 * @date        2022/5/31 13:31
	 */
	List<SysOrganize> getSysOrganizeBytenantId(Long tenantId);

	/**
	 * 获取显示的根组织
	 */
	SysOrganize getRootOragnize(Long tenantId);

	/**
	 * @param name        归属部门
	 * @param accessToken
	 * @return
	 */
	String queryOrgIdByName(String name, String accessToken);

	/**
	 * 根据租户获取组织，用于下拉列表
	 */
	List<SysOrganize> getSysOragnizeList(String accessToken);

}
