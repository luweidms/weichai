package com.youming.youche.system.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 *
 * 返回车队员工汇聚信息
 * @author terry
 * @date 2022/1/12 17:24
 * */

@Data
@Accessors(chain = true)
public class TenantStaffDto implements Serializable {


    private static final long serialVersionUID = 1038197571537825244L;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long staffId;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long userInfoId;
    /** 登录账号 */
    private String loginAcct;
    /** 手机号码 */
    private String mobilePhone;
    /** 姓名 */
    private String linkman;
    /** 员工工号 */
    private String employeeNumber;
    /** 职位 */
    private String staffPosition;
    /** 状态 */
    private Integer lockFlag;
    /** 状态名称 */
    private String lockFlagName;
    /** 拥有角色Id */
    private String roleIds;
    /** 拥有角色 */
    private String roleNames;
    /** 操作员表主键 */
    private Long operatorId;

    private String orgIds;

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
    private Integer bankBindState  = 0;

    private Integer bandCardEver;

    private Long userPrice;
    private String userPriceUrl;


    private Long adminUser;


}
