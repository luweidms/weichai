package com.youming.youche.finance.domain.order;

import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * <p>
 *
 * </p>
 *
 * @author Terry
 * @since 2022-03-09
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class OrderCostReport extends BaseDomain {

    private static final long serialVersionUID = 1L;


    private String auditRemark;

    private Long capacityLoadMileage;

    private LocalDateTime checkTime;

    private Long endKm;

    private Long endKmFile;

    private String endKmUrl;

    private Integer isAudit;

    private Integer isAuditPass;

    private Integer isFullOil;

    private Long loadMileage;

    private Long loadingKm;

    private Long loadingKmFile;

    private String loadingKmUrl;

    private Long opId;

    private String opName;

    private Long orderId;

    private String plateNumber;

    private Long startKm;

    private Long startKmFile;

    private String startKmUrl;

    private Integer state;

    private LocalDateTime subTime;

    private Long subUserId;

    private String subUserName;

    private Long tenantId;

    private Long unloadingKm;

    private Long unloadingKmFile;

    private String unloadingKmUrl;


}
