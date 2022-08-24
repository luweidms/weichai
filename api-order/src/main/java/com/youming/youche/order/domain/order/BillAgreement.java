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
* @since 2022-03-19
*/
    @Data
        @EqualsAndHashCode(callSuper = true)
    @Accessors(chain = true)
    public class BillAgreement extends BaseDomain {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    private Long relId;

    /**
     * 委托协议
     */
    private String agreement;

    /**
     * 开票信息（bill_info_2表主键）
     */
    private Long billInfoId;

    /**
     * 开票方式（开票平台 USER_ID）
     */
    private Long billMethod;


}
