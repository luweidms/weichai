package com.youming.youche.record.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class StaffDataInfoDto implements Serializable {
    private static final long serialVersionUID = 875336885318393540L;
    private Long staffId;
    /**
     * 用户编号
     */
    private Long userId;
    /**
     * 车队id
     */
    private Long tenantId;
    /**
     * 操作人
     */
    private Long orgId;
    /**
     * 登录账号
     */
    private String loginAcct;
    /**
     * 手机号码
     */
    private String mobilePhone;
    /**
     * 姓名
     */
    private String linkman;
    /**
     * 员工工号
     */
    private String employeeNumber;
    /**
     * 职位
     */
    private String staffPosition;
    /**
     * 状态
     */
    private Integer lockFlag;
    /**
     * 状态名称
     */
    private String lockFlagName;
    /**
     * 拥有角色Id
     */
    private String roleIds;
    /**
     * 拥有角色
     */
    private String roleNames;
    /**
     * 操作员表主键
     */
    private Long operatorId;
    /**
     * 操作人
     */
    private String orgIds;
    /**
     * 操作人名称
     */
    private String orgNames;
    //    /** 员工的默认组织ID */
//    private Long orgId;
//    /** 员工的默认组织名称 */
//    private String orgName;
    /**
     * 身份证号码
     */
    private String identification;
//    /** 银行卡信息 */
//    private AccountBankRel accountBankRel;

    /**
     * 银行卡绑定情况名称
     * 默认未绑定,有绑定再设置值
     */
    private String bankBindStateName;
    /**
     * 银行卡绑定情况
     * 默认未绑定,有绑定再设置值
     */
    private Integer bankBindState = 0;

    private Integer bandCardEver;
    /**
     * 用户头像id
     */
    private Long userPrice;
    /**
     * 用户头像url
     */
    private String userPriceUrl;

    // 部门id
    private Integer attachedOrgId;

//    public String getLockFlagName() {
//        setLockFlagName(SysStaticDataUtil.getSysStaticDataCodeName("ZH_USER_STATE", lockFlag + ""));
//        return lockFlagName;
//    }
//
//    public String getRoleNames() {
//        Long tenantId = SysContexts.getCurrentOperator().getTenantId();
//        if (StringUtils.isNotEmpty(roleIds)) {
//            String[] rs = roleIds.split(",");
//            String names = "";
//            for (String s : rs) {
//                String str = PermissionCacheUtil.getCurrentTenantSysRoleName(tenantId, Integer.valueOf(s));
//                if (StringUtils.isNotBlank(str)) {
//                    names = names + str + ";";
//                }
//            }
//            if (StringUtils.isNotEmpty(names)) {
//                setRoleNames(names.substring(0, names.length() - 1));
//            }
//        }
//        return roleNames;
//    }
//
//    public String getOrgNames() {
//        Long tenantId = SysContexts.getCurrentOperator().getTenantId();
//        if (StringUtils.isNotEmpty(orgIds)) {
//            String[] rs = orgIds.split(",");
//            String names = "";
//            for (String s : rs) {
//                SysOragnize oragnize = OragnizeCacheUtil.getCurrentTenantSysOragnizeById(tenantId, Long.parseLong(s));
//                if (oragnize != null && StringUtils.isNotEmpty(oragnize.getOrgName())) {
//                    names = names + oragnize.getOrgName() + ";";
//                }
//            }
//            if (StringUtils.isNotEmpty(names)) {
//                setOrgNames(names.substring(0, names.length() - 1));
//            }
//        }
//        return orgNames;
//    }
//
//    public String getBankBindStateName() {
//        return SysStaticDataUtil.getSysStaticDataCodeName("BIND_STATE", getBankBindState() + "");
//    }

}
