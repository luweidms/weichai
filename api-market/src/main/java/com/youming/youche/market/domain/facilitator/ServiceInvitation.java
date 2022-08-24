package com.youming.youche.market.domain.facilitator;

import com.baomidou.mybatisplus.annotation.TableField;
import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 服务商申请合作
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-01-28
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class ServiceInvitation extends BaseDomain {



    /**
     * 服务商ID
     */
    @TableField("SERVICE_USER_ID")
    private Long serviceUserId;

    /**
     * 租户ID
     */
    @TableField("TENANT_ID")
    private Long tenantId;

    /**
     * 用户ID
     */
    @TableField("USER_ID")
    private Long userId;

    /**
     * 申请说明
     */
    @TableField("APPLY_REASON")
    private String applyReason;

    /**
     * 附件ID
     */
    @TableField("FILE_ID")
    private String fileId;

    /**
     * 附件URL
     */
    @TableField("FILE_URL")
    private String fileUrl;

    /**
     * 审核结果（1.待审核、2.审核通过、3.审核未通过）
     */
    @TableField("AUTH_STATE")
    private Integer authState;

    /**
     * 审核时间
     */
    @TableField("AUTH_DATE")
    private String authDate;

    /**
     * 审核意见
     */
    @TableField("AUTH_REASON")
    private String authReason;

    /**
     * 审核人
     */
    @TableField("AUTH_MAN_ID")
    private Long authManId;

    /**
     * 创建时间
     */
    @TableField("CREATE_DATE")
    private String createDate;

    /**
     * 是否已读
     */
    @TableField("IS_READ")
    private Integer isRead;

    /**
     * 合作类型 1：初次合作，2：修改合作
     */
    @TableField("COOPERATION_TYPE")
    private Integer cooperationType;

    /**
     * 我(服务商)邀请的
     */
    @TableField("APP_SERVICE_USER_ID")
    private Long appServiceUserId;

    /**
     * 服务商申请合作明细表
     */
    @TableField(exist = false)
    private ServiceInvitationDtl serviceInvitationDtl;


}
