package com.youming.youche.finance.domain;

    import com.baomidou.mybatisplus.annotation.FieldFill;
    import com.baomidou.mybatisplus.annotation.TableField;
    import com.youming.youche.commons.base.BaseDomain;
    import lombok.Data;
    import lombok.EqualsAndHashCode;
    import lombok.experimental.Accessors;

/**
* <p>
    * 
    * </p>
* @author zengwen
* @since 2022-04-15
*/
    @Data
        @EqualsAndHashCode(callSuper = true)
    @Accessors(chain = true)
    public class AccountStatementDetailsExt extends BaseDomain {

    private static final long serialVersionUID = 1L;

    private Long accountStatementId;

    @TableField(value = "field_ext_0")
    private Long fieldExt0;

    @TableField(value = "field_ext_1")
    private Long fieldExt1;

    @TableField(value = "field_ext_2")
    private Long fieldExt2;

    @TableField(value = "field_ext_3")
    private Long fieldExt3;

    @TableField(value = "field_ext_4")
    private Long fieldExt4;

    @TableField(value = "field_ext_5")
    private Long fieldExt5;

    @TableField(value = "field_ext_6")
    private Long fieldExt6;

    @TableField(value = "field_ext_7")
    private Long fieldExt7;

    @TableField(value = "field_ext_8")
    private Long fieldExt8;

    @TableField(value = "field_ext_9")
    private Long fieldExt9;

    private Long relSeq;


}
