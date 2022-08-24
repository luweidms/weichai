package com.youming.youche.finance.domain;

import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
* <p>
    * 
    * </p>
* @author Terry
* @since 2022-03-09
*/
    @Data
        @EqualsAndHashCode(callSuper = true)
    @Accessors(chain = true)
    public class OaLoadVerification extends BaseDomain {

    private static final long serialVersionUID = 1L;


//    private Long vId;

    private Long amount;

    private String appName;

    private Long billAmount;

    private Long cashAmount;

    private String file2Url;

    private String file3Url;

    private String file4Url;

    private String file5Url;

    private Long lId;

    private Integer loanType;

    private LocalDateTime opDate;

    private Long opId;

    private String opName;

    private Long orgId;

    private String remark;

    private Long rootOrgId;

    private Long salary;

    private Integer sts;

    private Long tenantId;


}
