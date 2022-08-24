package com.youming.youche.system.constant;

/**
 * 权限相关常量
 */
@Deprecated
public class PermissionConsts {

    /**
     * 与权限有关的缓存key
     */
    public static class CacheKey {
        public static final String ALL_SYS_MENU_LIST = "ALL_SYS_MENU_LIST";
        public static final String ALL_SYS_MENU_FUNC_LIST = "ALL_SYS_MENU_FUNC_LIST";
        public static final String ALL_SYS_ROLE_LIST = "ALL_SYS_ROLE_LIST";
        public static final String ALL_SYS_ROLE_ID_LIST = "ALL_SYS_ROLE_ID_LIST";
        public static final String ALL_SYS_ENTITY_LIST = "ALL_SYS_ENTITY_LIST";
        public static final String ALL_SYS_ROLE_GRANT_LIST = "ALL_SYS_ROLE_GRANT_LIST";
        public static final String ALL_SYS_TENANT_GRANT_LIST = "ALL_SYS_TENANT_GRANT_LIST";
        public static final String ALL_SYS_ROLE_OPER_REL_LIST = "ALL_SYS_ROLE_OPER_REL_LIST";
    }


    /**
     * 实体权限
     */
    public static class ENTITY_ID {
        public static final int ENTITY_ID_1400104 = 1400104; //会员档案--修改手机号码
        public static final int ENTITY_ID_1400105 = 1400105; //会员档案--修改会员类型
        public static final int ENTITY_ID_120002 = 120002; //考勤管理权限

    }


}
