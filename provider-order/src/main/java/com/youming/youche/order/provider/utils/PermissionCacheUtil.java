package com.youming.youche.order.provider.utils;

import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.web.BaseUser;
import com.youming.youche.order.api.order.ISysRoleOperRelService;
import com.youming.youche.system.constant.PermissionConsts;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class PermissionCacheUtil {

    @Resource
    private ISysRoleOperRelService sysRoleOperRelService;


//
//
//    /**
//     * TODO 判断当前登录人是否拥有订单的"成本信息"权限
//     * @return true-有权限，false-无权限
//     * @throws Exception
//     */
//    public static boolean hasOrderCostPermission(LoginInfo user)  {
////        if (null == user.getTenantId()) {
////            return false;
////        }
////        return  hasEntity(user.getTenantId(), user.getId(), ORDER_COST);
//        return true;
//    }

//    /**
//     * 根据operatorId查询实体ID
//     * @param operatorId
//     * @return
//     * @throws Exception
//     */
//    public static boolean hasEntity(long tenantId,long operatorId, long entityId){
//        List<Integer> roleList = getRoleIdsByOperatorId(tenantId,operatorId);//获取所有的角色
//        List<Long> entitys = getEntityIdByRoles(tenantId,roleList);
//        for (Long entity : entitys) {
//            if (entity.equals(entityId)) {
//                return true;
//            }
//        }
//        return false;
//    }

//    /**
//     *
//     * 根据操作员ID获取获对改操作员的所有角色ID
//     * @param  operatorId
//     **/
//    public static List<Integer> getRoleIdsByOperatorId(Long tenantId, Long operatorId) {
//        if (null == operatorId || operatorId <= 0) {
//            return null;
//        }
//
//        List<Integer> roleIds = new ArrayList<>();
//        List<SysRoleOperRel> list = getCurrentTenantAllSysRoleOperRel(tenantId);
//
//        if (list != null) {
//            for (SysRoleOperRel rel : list) {
//                if (rel.getOperatorId().equals(operatorId)) {
//                    roleIds.add(rel.getRoleId());
//                }
//            }
//        }
//
//        return roleIds;
//    }
//
//    /**
//     *
//     * 获取当前租户下所有 操作员——角色 对应关系
//     */
//    public static List<SysRoleOperRel>  getCurrentTenantAllSysRoleOperRel(Long tenantId){
//        String key = PermissionConsts.CacheKey.ALL_SYS_ROLE_OPER_REL_LIST + "_" + tenantId;
//        LambdaQueryWrapper<SysRoleOperRel> lambda=new QueryWrapper<SysRoleOperRel>().lambda();
//        lambda.eq(SysRoleOperRel::get)
//        sysRoleOperRelService.list()
//        return (List<SysRoleOperRel>) CacheFactory.get(SysRoleOperRelCache.class, key);
//    }
//
//
//    /**
//     * 当前租户下，根据角色获取该角色对应的实体ID
//     */
//    public  static List<Long> getEntityIdByRoles(Long tenantId, List<Integer> roleIdList) {
//        List<Long> result = new ArrayList<>();
//        if (CollectionUtils.isEmpty(roleIdList)) {
//            return result;
//        }
//
//        if (containSuperRole(tenantId, roleIdList)) {
//            List<SysEntity> entityList = getAllSysEntity(tenantId);
//            entityList.forEach(e -> result.add(e.getEntityId()));
//
//            if (SysStaticDataEnum.PT_TENANT_ID == tenantId) {
//                entityList = getAllSysEntity(-4L);
//                entityList.forEach(e -> result.add(e.getEntityId()));
//            }
//            return result;
//        }
//
//        for(Integer roleId : roleIdList){
//            List<SysRoleGrant> roleGrantList = getCurrentTenantAllSysRoleGrantByRoleId(tenantId,roleId);
//            if (CollectionUtils.isNotEmpty(roleGrantList)) {
//                for(SysRoleGrant grant:roleGrantList){
//                    result.add(grant.getEntityId());
//                }
//            }
//        }
//
//        return result;
//    }
//
//
//    private static boolean containSuperRole(Long tenantId, List<Integer> roleIdList) {
//        if (CollectionUtils.isEmpty(roleIdList)) {
//            return false;
//        }
//        for (Integer roleId : roleIdList) {
//            SysRole sysRole = getSysRoleByRoleId(tenantId, roleId);
//            if (null != sysRole && sysRole.getRoleType() == 1) {
//                return true;
//            }
//        }
//        return false;
//    }
//
//    public static SysRole getSysRoleByRoleId(Long tenantId, Integer roleId) {
//        if (null == roleId) {
//            return null;
//        }
//        List<SysRole> roleList = getCurrentTenantAllSysRole(tenantId);
//        if (CollectionUtils.isEmpty(roleList)) {
//            return null;
//        }
//
//        for (SysRole role : roleList) {
//            if (role.getId().equals(roleId)) {
//                return role;
//            }
//        }
//        return null;
//    }


    /**
     *
     * 使用ISysRoleService.hasAllData的方法代替
     *
     *
     * 判断当前登录人是否拥有"所有数据"权限
     * @return true-有权限，false-无权限
     * @throws Exception
     *
     */
    @Deprecated
    public static boolean hasAllData(LoginInfo loginInfo) throws Exception {
//        if (null == loginInfo.getTenantId()) {
//            return false;
//        }
//        return  hasEntity(user.getTenantId(), user.getOperId(), PermissionConsts.ENTITY_ID.ALL_DATA);
        return false;
    }

}
