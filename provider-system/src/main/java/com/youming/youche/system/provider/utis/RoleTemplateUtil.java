//package com.youming.youche.system.provider.utis;
//
//import cn.hutool.core.util.StrUtil;
//import com.youming.youche.commons.domain.SysStaticData;
//import com.youming.youche.commons.util.RedisUtil;
//import com.youming.youche.conts.SysStaticDataEnum;
//import com.youming.youche.system.domain.SysRole;
//import com.youming.youche.system.dto.RoleEntityPairDto;
//import org.apache.commons.collections.CollectionUtils;
//import org.springframework.stereotype.Component;
//
//import javax.annotation.Resource;
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.Iterator;
//import java.util.List;
//
///**
// * @Description : 角色模版工具类
// * @Author : luwei
// * @Date : 2022/1/18 11:38 上午
// * @Version : 1.0
// **/
//public class RoleTemplateUtil {
//
////    /**
////     * 角色类型
////     */
////    private static final String ROLE_CODE_TYPE = "ROLE_TEMPLATE";
////    /**
////     * 实体类型
////     */
////    private static final String ENTITY_CODE_TYPE = "ENTITY_TEMPLATE";
//
//
////    public List<RoleEntityPairDto> getDefaultRoleFromTemplate(RedisUtil redisUtil,Long tenantId, Long userId) {
////        List<SysStaticData> defaultRoleList = getSysStaticDataList(redisUtil,SysStaticDataEnum.PT_TENANT_ID, ROLE_CODE_TYPE);
////        List<RoleEntityPairDto> result = new ArrayList<>();
////        if (CollectionUtils.isEmpty(defaultRoleList)) {
////            return result;
////        }
////
////        for (SysStaticData data : defaultRoleList) {
////            String roleName = data.getCodeName();
////            String value = data.getCodeValue();
////
////            SysRole role = buildSysRole(tenantId, roleName, userId);
////            List<Long> entityIdList = getCodeIdList(redisUtil,SysStaticDataEnum.PT_TENANT_ID, ENTITY_CODE_TYPE, value);
////            RoleEntityPairDto pair = new RoleEntityPairDto(role, entityIdList);
////
////            result.add(pair);
////        }
////
////        return result;
////    }
//
//    private List<SysStaticData> getSysStaticDataList(RedisUtil redisUtil, Long tenantId, String codeType) {
//        List<SysStaticData> staticDataList = null;
//        if (StrUtil.isNotEmpty(codeType)) {
//            staticDataList = (List<SysStaticData>) redisUtil.get(SysStaticDataEnum.SYS_STATIC_DATA_NAME.concat(tenantId + "_" + codeType));
//        }
//        if (staticDataList == null || staticDataList.isEmpty()) {
//            staticDataList = (List<SysStaticData>) redisUtil.get(SysStaticDataEnum.SYS_STATIC_DATA_NAME.concat(codeType));
//        }
//        return staticDataList;
//    }
//
//    private List<SysStaticData> getSysStaticDataList(RedisUtil redisUtil,Long tenantId, String codeType, String codeValue) {
//        List<SysStaticData> list = getSysStaticDataList(redisUtil,tenantId, codeType);
//        List<SysStaticData> resultList = new ArrayList<>();
//
//        if (CollectionUtils.isEmpty(list)) {
//            return resultList;
//        }
//
//        Iterator<SysStaticData> iterator = list.iterator();
//        while (iterator.hasNext()) {
//            SysStaticData data = iterator.next();
//            if (data.getCodeValue().equals(codeValue)) {
//                resultList.add(data);
//            }
//        }
//        return resultList;
//    }
//
////    private List<Long> getCodeIdList(RedisUtil redisUtil,Long tenantId, String codeType, String codeValue) {
////        List<SysStaticData> lists = getSysStaticDataList(redisUtil,tenantId, codeType, codeValue);
////        List<Long> result = new ArrayList<>(lists.size());
////        if (CollectionUtils.isEmpty(lists)) {
////            return result;
////        }
////
////        for (SysStaticData data : lists) {
////            result.add(data.getCodeId());
////        }
////        return result;
////    }
//
////    private SysRole buildSysRole(Long tenantId, String roleName, Long userId) {
////        SysRole sysRole = new SysRole();
////        sysRole.setTenantId(tenantId);
////        sysRole.setRoleName(roleName);
////        sysRole.setRoleType(2);
////        sysRole.setState(SysStaticDataEnum.SYS_STATE_DESC.STATE_YES);
////        sysRole.setUpdateTime(LocalDateTime.now());
////        sysRole.setOpUserId(userId);
////        sysRole.setRemark("系统自动创建" + roleName);
////        return sysRole;
////    }
//
//
//}
