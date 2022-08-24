package com.youming.youche.market.domain.facilitator;

import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class Facilitator extends BaseDomain {
    /**
     * 创建时间
     */
    private String createDate;
    /**
     * 服务商用户id
     */
    private Long serviceUserId;
    /**
     * 登录账户
     */
    private String loginAcct;
    /**
     * 服务商名
     */
    private String serviceName;
    /**
     *  '服务商类型（1.油站、2.维修、3.etc供应商）',
     */
    private Integer serviceType;
    /**
     * 联系人姓名
     */
    private String linkman;
    /**
     * 联系人电话
     */
    private String linkPhone;
    /**
     * 租户id
     */
    private Long tenantId;
    /**
     * 租户姓名
     */
    private String tenantName;
    /**
     * 租户人
     */
    private String tenantLinkMan;
    /**
     * 租户电话
     */
    private String tenantLinkPhone;
    /**
     * '状态（1.有效、2.无效）',
     */
    private Integer state;
    /**
     * 审核状态
     */
    private Integer authFlag;
    /**
     * '审核原因',
     */
    private String authReason;
    /**
     * 审核人
     */
    private String authManName;
    /**
     *  '审核状态（1.未审核、2.审核通过、3.审核未通过）',
     */
    private Integer authState;
    /**
     * 站点数量
     */
    private Integer productNum;

}
