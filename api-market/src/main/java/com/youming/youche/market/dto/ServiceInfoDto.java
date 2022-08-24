package com.youming.youche.market.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author : [terry]
 * @version : [v1.0]
 * @className : ServiceInfoDto
 * @description : [描述说明该类的功能]
 * @createTime : [2022/4/27 21:46]
 */
@Data
public class ServiceInfoDto implements Serializable {


    private static final long serialVersionUID = 5875349473253852826L;
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
    private String serviceTypeName;

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
