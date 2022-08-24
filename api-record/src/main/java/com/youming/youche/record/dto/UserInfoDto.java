package com.youming.youche.record.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
public class UserInfoDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer info; // 微信登录标志  1登录成功  2 未绑定OpenId
    private String userType; // 用户类型
    private Long tenantId; // 租户编号，只有一个租户的情况下返回
    private Boolean isMultiTenantUser; // 是否需要进入车队选择页面  true 进入 false 不进入
    private List<Map> tenantList; // isMultiTenantUser为true的情况下，返回所有的车队信息 key为车队id  value为车队名称
    private String tokenId; // 登录tokenId

    private String pushRelatId; // 关系ID
    private String billId; // 手机号
    private String userId; // 用户编号
    private String userPicture; // 用户头像url
    private String userName; // 用户名称
    private String orgId; // 组织编号
    private String companyName; // 公司名称
    private String checkFlag; // 是否认证	    是否认证1、未认证 2、认证中 3、未通过 5、已认证
    private String isSetPasswd; // 是否已设置登录密码 0-否，1-是

    public String isAuth; //判断用户是否为已认证
    public String isBindCard; //判断是否已经绑卡,司机只要判断对私卡就可以
    /**
     * 司机类型 1-自有车司机，3-外调车司机
     */
    public int carUserType;

    /**
     * 联系人手机
     */
    private String mobilePhone;

    private Boolean isAdminUser; // 是否管理员

    /**
     * 租户名称(车队全称)
     */
    private String tenantName;
    /**
     * 管理员用户编号
     */
    private Long adminUserId;
    /**
     * 服务商名称
     */
    private String serviceName;
    /**
     * 服务商类型
     */
    private String serviceType;
    /**
     * 服务商用户名称
     */
    private String linkman;
    /**
     * 审核状态
     */
    private String authState;
    /**
     * 服务商类型名称
     */
    private String serviceTypeName;
    /**
     * 审核状态名称
     */
    private String authStateName;
}
