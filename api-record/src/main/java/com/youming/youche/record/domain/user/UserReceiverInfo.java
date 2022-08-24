package com.youming.youche.record.domain.user;

import com.baomidou.mybatisplus.annotation.TableField;
import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 收款人(代收人)
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class UserReceiverInfo extends BaseDomain {

    private static final long serialVersionUID = 1L;

    /**
     * 用户编号
     */
    private Long userId;

    /**
     * 收款人名称
     */
    private String receiverName;

    /**
     * 操作人id
     */
    private Long opId;

    /**
     * 租户id
     */
    private Long tenantId;

    /**
     * 源租户id，割接数据辅助字段
     */
    private Long originTenantId;

    /**
     * 用户信息
     */
    @TableField(exist = false)
    private UserDataInfo userDataInfo;

}
