package com.youming.youche.record.dto.tenant;

import com.youming.youche.commons.base.BaseDomain;
import com.youming.youche.record.domain.user.UserDataInfo;
import com.youming.youche.record.domain.user.UserReceiverInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

@Data
public class TenantReceiverRelDto implements Serializable {
    /**
     * 主键
     */
    private Long id;
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
     * 创建时间
     */
    private Date createDate;
    /**
     * 修改时间
     */
    private Date updateDate;
    /**
     * 用户id
     */
    private Long userId;
    /**
     * 接受人
     */
    private String receiverName;
    /**
     * 用户名称
     */
    private String linkman;
    /**
     * 用户手机号
     */
    private String mobilePhone;
    /**
     * 绑定卡
     */
    private Long bindCard;
    /**
     * 用防护信息
     */
    private UserDataInfo userDataInfo;
    /**
     * 收款人(代收人)
     */
    private UserReceiverInfo userReceiverInfo;
    /**
     * 绑定状态
     */
    private Integer bindState;
    private String bindStateName;

}
