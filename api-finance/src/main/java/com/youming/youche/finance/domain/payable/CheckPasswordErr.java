package com.youming.youche.finance.domain.payable;

    import java.time.LocalDateTime;
    import com.youming.youche.commons.base.BaseDomain;
    import lombok.Data;
    import lombok.EqualsAndHashCode;
    import lombok.experimental.Accessors;

/**
* <p>
    * 
    * </p>
* @author zag
* @since 2022-04-07
*/
    @Data
        @EqualsAndHashCode(callSuper = true)
    @Accessors(chain = true)
    public class CheckPasswordErr extends BaseDomain {

    private static final long serialVersionUID = 1L;

    private Long id;

    private LocalDateTime checkDate;

    private Integer checkType;

    private Integer errorTimes;

    private LocalDateTime operDate;

    private Integer status;

    private Long tenantId;

    private Long userId;


}
