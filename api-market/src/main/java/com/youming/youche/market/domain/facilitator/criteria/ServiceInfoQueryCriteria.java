package com.youming.youche.market.domain.facilitator.criteria;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 服务商档案查询参数
 */
@Data
@Accessors(chain = true)
public class ServiceInfoQueryCriteria implements Serializable {
    /**
     * 用户编码
     */
    private Long serviceUserId;
    /**
     * 服务商账号
     */
    private String loginAcct;
    /**
     * 服务商名称
     */
    private String serviceName;
    /**
     * 联系人
     */
    private String linkman;
    /**
     * 服务商类型
     */
    private Integer serviceType;

    /**
     * 账号状态 状态（1.有效、2.无效）
     */
    private Integer state;
//    /**
//     * 认证状态
//     */
//    private Integer certifyState;
    /**
     * 审核状态（1.待审核、2.审核通过、3.审核不通过）
     */
    private Integer authState;
    /**
     * 可审核否 0 否 1是
     */
    private Integer isAuth;

}
