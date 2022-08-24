package com.youming.youche.order.domain;

    import com.youming.youche.commons.base.BaseDomain;
    import lombok.Data;
    import lombok.EqualsAndHashCode;
    import lombok.experimental.Accessors;

/**
* <p>
    * 
    * </p>
* @author zengwen
* @since 2022-06-13
*/
    @Data
        @EqualsAndHashCode(callSuper = true)
    @Accessors(chain = true)
    public class BusiSubjectsDtlOperate extends BaseDomain {

    private static final long serialVersionUID = 1L;

    /**
     * 字段名
     */
    private String columName;
    /**
     * busi_subjects_dtl主键ID.
     */
    private Long dtlId;
    /**
     * 操作流水字段.
     */
    private String flowColum;
    /**
     * 操作
     */
    private String operate;
    /**
     * 顺序ID
     */
    private Integer sortId;


}
