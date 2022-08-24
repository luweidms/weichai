package com.youming.youche.market.domain.youka;

    import java.time.LocalDateTime;
    import com.youming.youche.commons.base.BaseDomain;
    import lombok.Data;
    import lombok.EqualsAndHashCode;
    import lombok.experimental.Accessors;

/**
* <p>
    * 
    * </p>
* @author XXX
* @since 2022-03-24
*/
    @Data
        @EqualsAndHashCode(callSuper = true)
    @Accessors(chain = true)
    public class OilCardApply extends BaseDomain {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    private Long applyId;
    /**
     * 申请单号
     */
    private String applyNum;
    /**
     * 申请数量
     */
    private Integer applyOilCount;
    /**
     * 备注
     */
    private String applyRemark;
    /**
     * 申请类型 1服务商 2车队
     */
    private Integer applySourceType;
    /**
     * 申请时间
     */
    private LocalDateTime applyTime;
    /**
     * 1油卡 2ECT卡
     */
    private Integer applyType;
    /**
     * 申请人id
     */
    private Long applyUserId;
    /**
     * 申请人
     */
    private String applyUserName;
    /**
     * 申请人手机号
     */
    private String applyUserPhone;
    /**
     * 审核备注
     */
    private String auditRemark;
    /**
     * 处理时间
     */
    private LocalDateTime dealTime;
    /**
     * 已交付数量
     */
    private Integer deliverCount;
    /**
     * 附件
     */
    private Long fileId1;
    /**
     * 附件
     */
    private Long fileId10;
    /**
     * 附件
     */
    private Long fileId2;
    /**
     * 附件
     */
    private Long fileId3;
    /**
     * 附件
     */
    private Long fileId4;
    /**
     * 附件
     */
    private Long fileId5;
    /**
     * 附件
     */
    private Long fileId6;
    /**
     * 附件
     */
    private Long fileId7;
    /**
     * 附件
     */
    private Long fileId8;
    /**
     * 附件
     */
    private Long fileId9;
    /**
     * 附件URL
     */
    private String fileUrl1;
    /**
     * 附件URL
     */
    private String fileUrl10;
    /**
     * 附件URL
     */
    private String fileUrl2;
    /**
     * 附件URL
     */
    private String fileUrl3;
    /**
     * 附件URL
     */
    private String fileUrl4;
    /**
     * 附件URL
     */
    private String fileUrl5;
    /**
     * 附件URL
     */
    private String fileUrl6;
    /**
     * 附件URL
     */
    private String fileUrl7;
    /**
     * 附件URL
     */
    private String fileUrl8;
    /**
     * 附件URL
     */
    private String fileUrl9;
    /**
     * 附件URL
     */
    private Integer isNeedBill;
    /**
     * 附件URL
     */
    private Integer isShare;
    /**
     * 产品id
     */
    private Long productId;
    /**
     * 已发卡数量
     */
    private Integer sendCardCount;
    /**
     * 服务商ID'
     */
    private Long serviceUserId;
    /**
     * 1待审核 2待发卡 3审核不通过 4待交付 5已交付 6已完成
     */
    private Integer state;
    /**
     * 车队id
     */
    private Long tenantId;


}
