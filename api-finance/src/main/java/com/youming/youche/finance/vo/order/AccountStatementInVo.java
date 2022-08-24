package com.youming.youche.finance.vo.order;

import lombok.Data;

import java.io.Serializable;

/**
 * @author zengwen
 * @date 2022/4/14 11:29
 */
@Data
public class AccountStatementInVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long vehicleCode;//车辆vehicleCode
    private String plateNumber;//车牌号码
    private String linkman;//司机名字
    private String mobilePhone;//司机手机号(可能为空)
    private Long driverUserId;//司机ID(可能为空)
    private String billReceiverMobile;//账单接收人手机号
    private Long billReceiverUserId;//账单接收人用户编号
    private String billReceiverName;//账单接收人名称
    private int userType;//账单接收人用户类型
}
