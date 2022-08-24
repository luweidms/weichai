package com.youming.youche.finance.domain;

import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.Date;

/**
* <p>
    * 
    * </p>
* @author luona
* @since 2022-04-20
*/
    @Data
        @EqualsAndHashCode(callSuper = true)
    @Accessors(chain = true)
    public class OilTurnEntity extends BaseDomain {

    private static final long serialVersionUID = 1L;


    private Long batchId;

    private Long assignTotal;

    private Long carDriverId;

    private Integer carUserType;

    private String companyName;

    private LocalDateTime createDate;

    private LocalDateTime dependTime;

    private Integer desRegion;

    private Integer isReport;

    private Long noVerificationAmount;

    private String oilCardNum;

    private Long orderId;

    private String plateNumber;

    private String reportRemark;

    private LocalDateTime reportTime;

    private Integer rootOrgId;

    private Integer sourceRegion;

    private Integer state;

    private Long tenantId;

    private String vehicleAffiliation;

    private Long vehicleCode;

    private String vehicleLengh;

    private Integer vehicleStatus;

    private Date verificationDate;


}
