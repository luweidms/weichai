package com.youming.youche.order.domain.order;

    import com.youming.youche.commons.base.BaseDomain;
    import lombok.Data;
    import lombok.EqualsAndHashCode;
    import lombok.experimental.Accessors;

/**
* <p>
    * 
    * </p>
* @author CaoYaJie
* @since 2022-03-21
*/
    @Data
        @EqualsAndHashCode(callSuper = true)
    @Accessors(chain = true)
    public class OrderOilDepotSchemeVer extends BaseDomain {

    private static final long serialVersionUID = 1L;

    /**
     * 靠台距离  单位/m
     */
    private Long dependDistance;
    /**
     * 是否修改
     */
    private Integer isUpdate;
    /**
     * 油站经度
     */
    private String oilDepotEand;
    /**
     * 加油费用
     */
    private Long oilDepotFee;
    /**
     * 油站id
     */
    private Long oilDepotId;
    /**
     * 加油升数
     */
    private Long oilDepotLitre;
    /**
     * 油站名字
     */
    private String oilDepotName;
    /**
     * 油站纬度
     */
    private String oilDepotNand;
    /**
     * 油单价
     */
    private Long oilDepotPrice;
    /**
     * 订单号
     */
    private Long orderId;
    /**
     * 车队id
     */
    private Long tenantId;


}
