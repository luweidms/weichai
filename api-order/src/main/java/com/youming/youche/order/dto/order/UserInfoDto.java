package com.youming.youche.order.dto.order;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserInfoDto implements Serializable {

    private static final long serialVersionUID = -7907798707424114072L;

    private String userPriceUrl; // 用户头像，待确认
    private Integer balance; // 我的钱包可提现金额，分为单位
    private Long vehicleSum; // 我的车辆数量
    private Integer vehicleVer; // 我的车辆认证标志 0 未认证  1已认证
    private Integer driverVer; //  司机身份认证标志 0 未认证  1已认证
    private Integer orderSum; // 在途订单数量
    private String userName;
    private Integer userLevel; // 等级值，1铜牌会员、2银牌会员、3金牌会员、4钻石会员
    private String userLevelName;
    private String reward;
    private String interest;

}
