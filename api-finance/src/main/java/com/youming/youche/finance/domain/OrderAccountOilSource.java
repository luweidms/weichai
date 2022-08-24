package com.youming.youche.finance.domain;

    import com.youming.youche.commons.base.BaseDomain;
    import lombok.Data;
    import lombok.EqualsAndHashCode;
    import lombok.experimental.Accessors;

/**
* <p>
    * 
    * </p>
* @author luona
* @since 2022-04-07
*/
    @Data
        @EqualsAndHashCode(callSuper = true)
    @Accessors(chain = true)
    public class OrderAccountOilSource extends BaseDomain {

    private static final long serialVersionUID = 1L;


    private Long accId;

    private Long oilBalance;

    private Long opId;

    private Long parentId;

    private Long rechargeOilBalance;

    private Long tenantId;

    private Long updateOpId;

    private Long userId;

    private Integer userType;


}
