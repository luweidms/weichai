package com.youming.youche.finance.domain.ac;

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
* @since 2022-04-18
*/
    @Data
        @EqualsAndHashCode(callSuper = true)
    @Accessors(chain = true)
    public class CmSalaryComplainNew extends BaseDomain {

    private static final long serialVersionUID = 1L;

    private String complainReason;

    private LocalDateTime opDate;

    private Long opId;

    private String opName;

    private String opPhone;

    private String opReason;

    private Integer salaryComplainType;

    private String salaryComplainTypeName;

    private Long sid;

    private Integer state;

    private Long tenantId;

    private Long userId;

    private String userName;


}
