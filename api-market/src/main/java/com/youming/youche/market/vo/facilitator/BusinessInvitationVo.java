package com.youming.youche.market.vo.facilitator;

import com.youming.youche.market.dto.facilitator.CooperationDataInfoDto;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
@Data
public class BusinessInvitationVo implements Serializable {
    /**
     * 车队名称
     */
    private String tenantName;

    /**
     * 联系人
     */
    private String linkman;

    /**
     * 联系电话
     */
    private String linkPhone;

    /**
     * 创建时间
     */
    private String createDate;

    /**
     * 合作类型 1：初次合作，2：修改合作
     */
    private Integer cooperationType;

    private Long id;

    /**
     * 车队ID
     */
    private Long tenantId;

    private List<CooperationDataInfoDto> cooperationList;

    private Integer isBill;

    private String isBillName;
    /**
     * 账期
     */
    private Integer paymentDays;
    /**
     * 账期结算月份
     */
    private Integer paymentMonth;

    /**
     * 结算方式，1账期，2月结
     */
    private Integer balanceType;

    private String balanceTypeName;

    /**
     * 服务商类型（1.油站、2.维修、3.etc供应商）
     */
    private Integer serviceType;

    /**
     * 授信金额
     */
    private Long quotaAmt;


    private String quotaAmtName;

    /**
     * 已使用授信金额
     */
    private Long useQuotaAmt;

    private Integer isBillNew;

    private String isBillNameNew;

    private Integer paymentDaysNew;

    private Integer paymentMonthNew;

    private Integer balanceTypeNew;

    private String balanceTypeNameNew;
    /**
     * 授信金额
     */
    private Long quotaAmtNew;

    private Long quotaAmtRelNew;

    private String quotaAmtRelNameNew;

    private String quotaAmtNameNew;

    private String remark;
    /**
     * 附件URL
     */
    private String fileId;

    /**
     * 附件URL
     */
    private String fileUrl;
}
