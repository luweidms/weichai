package com.youming.youche.finance.domain.ac;

    import java.time.LocalDateTime;

    import com.baomidou.mybatisplus.annotation.FieldStrategy;
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
* @since 2022-04-18
*/
    @Data
        @EqualsAndHashCode(callSuper = true)
    @Accessors(chain = true)
    public class CmSalaryInfoNewExt extends BaseDomain {

    private static final long serialVersionUID = 1L;


    private String channelType;

    private LocalDateTime createDate;
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String fieldExt0;
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String fieldExt1;
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String fieldExt10;
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String fieldExt11;
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String fieldExt12;
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String fieldExt13;
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String fieldExt14;
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String fieldExt15;
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String fieldExt16;
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String fieldExt17;
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String fieldExt18;
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String fieldExt19;
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String fieldExt2;
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String fieldExt20;
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String fieldExt21;
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String fieldExt22;
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String fieldExt23;
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String fieldExt24;
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String fieldExt25;
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String fieldExt26;
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String fieldExt27;
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String fieldExt28;
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String fieldExt29;
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String fieldExt3;
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String fieldExt4;
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String fieldExt5;
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String fieldExt6;
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String fieldExt7;
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String fieldExt8;
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String fieldExt9;

    private Long opId;

    private String opName;

    private Long salaryId;

    private Long tenantId;

    private LocalDateTime updateDate;

    private Long updateOpId;


}
