package com.youming.youche.order.domain.order;

import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
* <p>
    * 
    * </p>
* @author CaoYaJie
* @since 2022-03-24
*/
    @Data
        @EqualsAndHashCode(callSuper = true)
    @Accessors(chain = true)
    public class OrderDriverSwitchInfo extends BaseDomain {

    private static final long serialVersionUID = 1L;

    /**
     * 确认时间
     */
    private LocalDateTime affirmDate;

    /**
     * 切换结束时间
     */
    private LocalDateTime endDate;

    /**
     * 拒绝时间
     */
    private LocalDateTime fefuseDate;

    /**
     * 原始里程
     */
    private Long formerMileage;

    /**
     * 原始里程附件ID
     */
    private String formerMileageFileId;

    /**
     * 原始里程附件URL
     */
    private String formerMileageFileUrl;

    /**
     * 发起备注
     */
    private String formerRemark;

    /**
     * 原始用户ID
     */
    private Long formerUserId;

    /**
     * 原始用户名称
     */
    private String formerUserName;

    /**
     * 原始用户手机
     */
    private String formerUserPhone;

    /**
     * 订单号
     */
    private Long orderId;

    /**
     * 发起人ID
     */
    private Long originUserId;

    /**
     * 发起人名称
     */
    private String originUserName;

    /**
     * 发起人手机号
     */
    private String originUserPhone;

    /**
     * 接收副驾Id
     */
    private Long receiveCopilot;

    /**
     * 接收里程
     */
    private Long receiveMileage;

    /**
     * 接收里程附件ID
     */
    private String receiveMileageId;

    /**
     * 接收里程附件URL
     */
    private String receiveMileageUrl;

    /**
     * 接收备注
     */
    private String receiveRemark;

    /**
     * 接收用户ID
     */
    private Long receiveUserId;

    /**
     * 接收用户名称
     */
    private String receiveUserName;

    /**
     * 接收用户手机
     */
    private String receiveUserPhone;

    /**
     * 切换状态 0:待确认 1:切换成功 2:已取消 3:被驳回
     */
    private Integer state;

    /**
     * 租户ID
     */
    private Long tenantId;

    /**
     * 是否为小程序切换司机
     */
    private int isWxChange;

}
