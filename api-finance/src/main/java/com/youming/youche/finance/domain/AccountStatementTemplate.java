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
    public class AccountStatementTemplate extends BaseDomain {

    private static final long serialVersionUID = 1L;


    private String channelType;

    private Long opId;

    private String opName;

    private Integer state;

    private Long tenantId;

    private Long updateOpId;

    private Long ver;


}
