package com.youming.youche.order.domain.order;

    import com.youming.youche.commons.base.BaseDomain;
    import lombok.Data;
    import lombok.EqualsAndHashCode;
    import lombok.experimental.Accessors;

/**
* <p>
    * 
    * </p>
* @author liangyan
* @since 2022-03-23
*/
    @Data
        @EqualsAndHashCode(callSuper = true)
    @Accessors(chain = true)
    public class BusiSubjectsDtl extends BaseDomain {

    private static final long serialVersionUID = 1L;


    private Long booType;

    /**
     * 业务ID
     */
    private Long businessId;

    /**
     * 明细业务ID
     */
    private Long dtlBusinessId;

    /**
     * 是否包含返现
     */
    private Integer hasBack;

    /**
     * 是否包含利润
     */
    private Integer hasIncome;

    /**
     * 收支状态:收in支out转io
     */
    private String inoutSts;

    /**
     * 备注
     */
    private String remark;

    /**
     * 顺序ID
     */
    private Integer sortId;

    /**
     * 科目名称
     */
    private Long subjectsId;


}
