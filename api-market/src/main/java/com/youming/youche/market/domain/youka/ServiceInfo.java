package com.youming.youche.market.domain.youka;

    import java.time.LocalDateTime;
    import com.baomidou.mybatisplus.annotation.TableField;
    import com.youming.youche.commons.base.BaseDomain;
    import lombok.Data;
    import lombok.EqualsAndHashCode;
    import lombok.experimental.Accessors;

/**
* <p>
    * 服务商表
    * </p>
* @author Terry
* @since 2022-02-09
*/
    @Data
        @EqualsAndHashCode(callSuper = true)
    @Accessors(chain = true)
    public class ServiceInfo extends BaseDomain {

    private static final long serialVersionUID = 1L;


            /**
            * 用户编码
            */
        @TableField("SERVICE_USER_ID")
    private Long serviceUserId;

            /**
            * 公司地址
            */
        @TableField("COMPANY_ADDRESS")
    private String companyAddress;

            /**
            * 服务商名称
            */
        @TableField("SERVICE_NAME")
    private String serviceName;

            /**
            * 服务商类型（1.油站、2.维修、3.etc供应商）
            */
        @TableField("SERVICE_TYPE")
    private Integer serviceType;

            /**
            * 状态（1.有效、2.无效）
            */
        @TableField("STATE")
    private Integer state;

            /**
            * 操作人
            */
        @TableField("OP_ID")
    private Long opId;

            /**
            * 是否有开票能力（1.有，2.无）
            */
        @TableField("IS_BILL_ABILITY")
    private Integer isBillAbility;

            /**
            * 运营后台审核人
            */
        @TableField("AUTH_MAN_ID")
    private String authManId;

            /**
            * 运营后台审核人名称
            */
        @TableField("AUTH_MAN_NAME")
    private String authManName;

            /**
            * 运营后台审核时间
            */
        @TableField("AUTH_DATE")
    private String authDate;

            /**
            * 运营审核状态，1:待审核,2:已审核,3:审核未通过
            */
        @TableField("AUTH_FLAG")
    private Integer authFlag;

            /**
            * 审核意见
            */
        @TableField("AUTH_REASON")
    private String authReason;

            /**
            * 是否认证
            */
        @TableField("IS_AUTH")
    private Integer isAuth;

            /**
            * 租户ID
            */
        @TableField("TENANT_ID")
    private Long tenantId;

            /**
            * logo
            */
        @TableField("LOGO_ID")
    private String logoId;

            /**
            * logo地址
            */
        @TableField("LOGO_URL")
    private String logoUrl;

            /**
            * 平台代收
            */
        @TableField("AGENT_COLLECTION")
    private Integer agentCollection;
    /**
     * 创建时间
     */
    private LocalDateTime createDate;
    /**
     * 操作时间
     */
    private LocalDateTime opDate;


}
