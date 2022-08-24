package com.youming.youche.record.domain.tenant;

import com.baomidou.mybatisplus.annotation.TableField;
import com.youming.youche.commons.base.BaseDomain;
import com.youming.youche.record.domain.user.UserDataInfo;
import com.youming.youche.record.domain.user.UserReceiverInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * 车队与收款人的关联关系
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class TenantReceiverRel extends BaseDomain {

    /**
     * 租户id
     */
    private Long tenantId;
    /**
     * 收款人id
     */
    private Long receiverId;
    /**
     * 备注
     */
    private String remark;

    /**
     * 用户编号
     */
    @TableField(exist = false)
    private Long userId;

    /**
     * 收款人名称
     */
    @TableField(exist = false)
    private String receiverName;

    /**
     * 联系人
     */
    @TableField(exist = false)
    private String linkman;

    /**
     * 联系人手机号
     */
    @TableField(exist = false)
    private String mobilePhone;

    /**
     * 绑定卡
     */
    @TableField(exist = false)
    private Boolean bindCard;

    /**
     * 用户信息
     */
    @TableField(exist = false)
    private UserDataInfo userDataInfo;

    /**
     * 收款人信息
     */
    @TableField(exist = false)
    private UserReceiverInfo userReceiverInfo;
}
