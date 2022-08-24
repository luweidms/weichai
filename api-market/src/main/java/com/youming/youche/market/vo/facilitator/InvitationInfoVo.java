package com.youming.youche.market.vo.facilitator;

import com.youming.youche.market.dto.facilitator.CooperationDto;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class InvitationInfoVo implements Serializable {
    private String linkman;

    private String companyAddress;

    private String loginAcct;

    private String serviceTypeName;

    private String serviceName;

    private Long userPrice;

    private String authReason;



    private String applyReason;

    private String fileId;

    private String productName;

    private Long productId;

    private Integer provinceId;

    private String provinceName;

    private Integer cityId;

    private String cityName;

    private Integer countyId;

    private String countyName;

    private Integer  isBill;

    private Integer paymentDays;

    private Integer paymentMonth;

    private Integer balanceType;

    private String balanceTypeName;

    private String quotaAmtStr;

    private String useQuotaAmtStr;

    private List<CooperationDto> applyList;

    private Long inviteId;

    private String authStateName;

    private Integer serviceType;

    private String productInfo;
}
