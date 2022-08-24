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
* @author zengwen
* @since 2022-04-23
*/
    @Data
        @EqualsAndHashCode(callSuper = true)
    @Accessors(chain = true)
    public class SysOperator extends BaseDomain {

    private static final long serialVersionUID = 1L;


    private String billId;

    private LocalDateTime createDate;

    private Integer lockFlag;

    private String loginAcct;

    private Long opId;

    private String operatorName;

    private String password;

    private String remark;

    private Integer state;

    private String tenantCode;

    private Long tenantId;

    private Integer tryTimes;

    private Long userId;

    private Integer userType;

    private Long orgId;

    private String address;

    private Long companyUserId;

    private String companyUserName;

    private Integer countyCode;

    private String linkMan;

    private String orgDataDefault;

    private String orgName;

    private Integer orgType;

    private Long parentOrgId;

    private Integer provinceCode;

    private Integer regionCode;

    private Integer sts;

    private LocalDateTime stsDate;

            /**
            * 1：APP-ANDROID2：APP-IOS3:WEB
            */
    private Integer channelType;

    private Long operatorId;


}
