package com.youming.youche.finance.domain;

    import java.time.LocalDateTime;
    import com.youming.youche.commons.base.BaseDomain;
    import lombok.Data;
    import lombok.EqualsAndHashCode;
    import lombok.experimental.Accessors;

/**
* <p>
    * 
    * </p>
* @author zengwen
* @since 2022-04-14
*/
    @Data
        @EqualsAndHashCode(callSuper = true)
    @Accessors(chain = true)
    public class AccountStatementTemplateField extends BaseDomain {

    private static final long serialVersionUID = 1L;


    private String channelType;

    private LocalDateTime createDate;

    private String fieldCode;

    private String fieldDesc;

    private Integer fieldIndex;

    private Integer isCancel;

    private Integer isDefault;

    private Integer isSelect;

    private String remark;

    private String tableName;

    private Long templateId;

    private Long tenantId;

    private LocalDateTime updateDate;

    private Long updateOpId;


}
