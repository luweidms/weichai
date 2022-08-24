package com.youming.youche.market.domain.facilitator;


import com.youming.youche.commons.base.BaseDomain;
import com.youming.youche.market.annotation.SysStaticDataInfoDict;
import com.youming.youche.market.annotation.Translatable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

import static com.youming.youche.conts.EnumConsts.SysStaticData.SERVICE_BUSI_TYPE;
import static com.youming.youche.market.constant.facilitator.ServiceProductConstant.SYS_STATE_DESC;

/**
 * <p>
 * 服务商表
 * </p>
 *
 * @author Terry
 * @since 2022-01-22
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class ServiceInfo   extends BaseDomain {

    private static final long serialVersionUID = 1L;


    /**
     * 用户编码
     */
    private Long serviceUserId;

    /**
     * 公司地址
     */
    private String companyAddress;

    /**
     * 服务商名称
     */
    private String serviceName;

    /**
     * 服务商类型（1.油站、2.维修、3.etc供应商）
     */
    private Integer serviceType;

    /**
     * 状态（1.有效、2.无效）
     */
    private Integer state;


    /**
     * 操作人
     */
    private Long opId;

    /**
     * 是否有开票能力（1.有，2.无）
     */
    private Integer isBillAbility;

    /**
     * 运营后台审核人
     */
    private String authManId;

    /**
     * 运营后台审核人名称
     */
    private String authManName;

    /**
     * 运营后台审核时间
     */
    private String authDate;

    /**
     * 运营审核状态，1:待审核,2:已审核,3:审核未通过
     */
    private Integer authFlag;

    /**
     * 审核意见
     */
    private String authReason;

    /**
     * 是否认证
     */
    private Integer isAuth;

    /**
     * 租户ID
     */
    private Long tenantId;

    /**
     * logo
     */
    private String logoId;

    /**
     * logo地址
     */
    private String logoUrl;

    /**
     * 平台代收
     */
    private Integer agentCollection;




}
