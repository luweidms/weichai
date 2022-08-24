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
    public class OilCardDeliver extends BaseDomain {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    private Long deliverId;
    /**
     * 申请记录ID
     */
    private Long applyId;
    /**
     * 交付数量
     */
    private Integer cardCount;
    /**
     * 卡种类 1油卡 2etc
     */
    private Integer cardType;
    /**
     * 收件人
     */
    private String consignee;
    /**
     * 联系电话
     */
    private String consigneeTel;
    /**
     * 快递单号
     */
    private String courierNumber;
    /**
     * 快递公司id
     */
    private Long courierServicesId;
    /**
     * 处理时间
     */
    private LocalDateTime dealTime;
    /**
     * 交付方式 1邮寄 2代领
     */
    private Integer deliverType;
    /**
     * 操作人
     */
    private Long opId;
    /**
     * 操作人
     */
    private String opName;
    /**
     * 产品ID
     */
    private Long productId;
    /**
     * 服务商ID
     */
    private Long serviceUserId;
    /**
     * 领卡人
     */
    private String takeCardName;
    /**
     * 领卡人手机号
     */
    private String takeCardTel;
    /**
     * 车队id
     */
    private Long tenantId;


}
