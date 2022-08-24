package com.youming.youche.system.provider.utis;

import com.youming.youche.system.domain.SysOrganize;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 组织架构工具类
 */
public class OrganizeUtil {

	/**
	 * 在sysOragnizeList里搜索orgId下所有的子组织
	 */
	public static List<Long> getAllChildOrgId(Collection<SysOrganize> sysOragnizeList, Long parentId) {
		List<Long> result = new ArrayList<>();

		getChildOrgId(sysOragnizeList, parentId, result);

		return result;
	}

	/**
	 * 递归获取子组织Id
	 * @param sysOragnizeList
	 * @param parentId
	 * @param result
	 */
	private static void getChildOrgId(Collection<SysOrganize> sysOragnizeList, Long parentId, List<Long> result) {
		if (CollectionUtils.isNotEmpty(sysOragnizeList) && result != null) {
			List<SysOrganize> childrenList = getChildSysOragnize(sysOragnizeList, parentId);
			for (SysOrganize sysOragnize : childrenList) {
				result.add(sysOragnize.getId());
				getChildOrgId(sysOragnizeList, sysOragnize.getId(), result);
			}
		}
	}

	/**
	 * 在sysOragnizeList里搜索下一级组织。
	 */
	public static List<SysOrganize> getChildSysOragnize(Collection<SysOrganize> sysOragnizeList, Long parentId) {
		List<SysOrganize> result = new ArrayList<SysOrganize>();
		if (CollectionUtils.isNotEmpty(sysOragnizeList)) {
			for (SysOrganize sysOragnize : sysOragnizeList) {
				if (sysOragnize.getParentOrgId().equals(parentId)) {
					result.add(sysOragnize);
				}
			}
		}
		return result;
	}

	/**
	 * 在sysOragnizeList搜索组织。
	 */
	public static SysOrganize getSysOrganizeById(Collection<SysOrganize> sysOragnizeList, Long orgId) {
		if (CollectionUtils.isNotEmpty(sysOragnizeList)) {
			for (SysOrganize sysOragnize : sysOragnizeList) {
				if (sysOragnize.getId().equals(orgId)) {
					return sysOragnize;
				}
			}
		}
		return null;
	}

	public static void main(String[] args) {
		System.out.println(Float.parseFloat("1234567"));
	}

}
