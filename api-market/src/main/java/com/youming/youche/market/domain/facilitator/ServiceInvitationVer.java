package com.youming.youche.market.domain.facilitator;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 服务商申请合作
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-01-28
 */
@Data
@Accessors(chain = true)
public class ServiceInvitationVer implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 历史主键
     */
    private Long hisId;

    private Long id;
    /**
     * 服务商ID
     */
    private Long serviceUserId;

    /**
     * 站点ID
     */
    private Long productId;

    /**
     * 租户ID
     */
    private Long tenantId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 申请说明
     */
    private String applyReason;

    /**
     * 附件ID
     */
    private String fileId;

    /**
     * 附件URL
     */
    private String fileUrl;

    /**
     * 审核结果（1.待审核、2.审核通过、3.审核未通过）
     */
    private Integer authState;

    /**
     * 审核时间
     */
    private String authDate;

    /**
     * 审核意见
     */
    private String authReason;

    /**
     * 审核人
     */
    private Long authManId;

    /**
     * 创建时间
     */
    private String createDate;



    /**
     * 修改时间
     */
    private String updateDate;

    /**
     * 修改操作人
     */
    private Long updateOpId;

    /**
     * 是否已读
     */
    private Integer isRead;

    /**
     * 合作类型 1：初次合作，2：修改合作
     */
    private Integer cooperationType;

    /**
     * 我(服务商)邀请的
     */
    private Long appServiceUserId;


}
