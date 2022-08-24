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
    public class OilCardInfo extends BaseDomain {

    private static final long serialVersionUID = 1L;

    /**
     * 持卡人
     */
    private String cardHolder;
    /**
     * 1导入 2补卡
     */
    private Long cardSourceType;
    /**
     * 卡种类
     */
    private Integer cardType;
    /**
     * ETC卡类型
     */
    private Integer etcCardType;
    /**
     * 卡号
     */
    private String oilCardNum;
    /**
     * 卡类型(中石油 中石化)
     */
    private Integer oilCardType;
    /**
     * 操作人ID
     */
    private Long opId;
    /**
     * 操作人
     */
    private String opName;
    /**
     * 操作时间
     */
    private LocalDateTime opTime;
    /**
     * 付费类型(1预付费 2后付费)
     */
    private Integer paymentType;
    /**
     * 车牌号
     */
    private String plateNumber;
    /**
     * 油卡序号
     */
    private String serialNumber;
    /**
     * 服务商ID
     */
    private Long serviceUserId;
    /**
     * 主卡卡号
     */
    private String sourceCard;
    /**
     * 1未使用 2已发卡 3已交付
     */
    private Integer state;
    /**
     * 车队id
     */
    private Long tenantId;


}
