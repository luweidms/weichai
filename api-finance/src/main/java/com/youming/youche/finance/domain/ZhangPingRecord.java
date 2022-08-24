package com.youming.youche.finance.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
* <p>
    * 
    * </p>
* @author WuHao
* @since 2022-04-13
*/
    @Data
        @EqualsAndHashCode(callSuper = true)
    @Accessors(chain = true)
    public class ZhangPingRecord extends BaseDomain implements Serializable {

    private static final long serialVersionUID = 1L;


    private Integer accountType;

    private Long afterBalance;

    private Long afterEtcBalance;

    private Long afterMarginBalance;

    private Long afterOilBalance;

    private Long afterPledgeOilcardFee;

    private Long afterRechargeOilBalance;

    private Long batchId;

    private Long beforeBalance;

    private Long beforeEtcBalance;

    private Long beforeMarginBalance;

    private Long beforeOilBalance;

    private Long beforePledgeOilcardFee;

    private Long beforeRechargeOilBalance;

    private LocalDateTime createDate;

    private Integer isReport;

    private LocalDateTime opDate;

    private String opRemark;

    private Long opUserId;

    private String opUserName;

    private LocalDateTime reportDate;

    private String reportRemark;

    private Long tenantId;

    private Long userId;

    private Integer userType;

    private String vehicleAffiliation;

    private Long zhangPingBalance;

    private Long zhangPingEtcBalance;

    private Long zhangPingMarginBalance;

    private Long zhangPingOilBalance;

    private Long zhangPingPledgeOilcard;

    private Long zhangPingRechargeOilBalance;

    private Integer zhangPingType;

    @TableField(exist=false)
    private Long BeforRepairFund;

    @TableField(exist=false)
    private Long ZhangPingRepairFund;

    @TableField(exist=false)
    private Long AfrerRepairFund;


}
