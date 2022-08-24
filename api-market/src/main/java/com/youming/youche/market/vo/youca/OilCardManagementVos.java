package com.youming.youche.market.vo.youca;

import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 油卡管理表
 * </p>
 *
 * @author Terry
 * @since 2022-02-09
 */
@Data
public class OilCardManagementVos implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    /**
     * 实体油卡卡号
     */
    private String oilCarNum;

    /**
     * 油卡状态code_type='oilcard_status'
     */
    private Integer oilCardStatus;

    /**
     * 服务商用户id
     */
    private Long userId;

    /**
     * 服务商联系人
     */
    private String linkman;

    /**
     * 创建时间
     */
    private LocalDateTime createDate;

    /**
     * 租户
     */
    private Long tenantId;

    /**
     * 当前抵押订单号
     */
    private Long pledgeOrderId;

    /**
     * 当前抵押金额
     */
    private Long pledgeFee;

    /**
     * 油卡类型
     */
    private Integer cardType;

    /**
     * 理论余额
     */
    private Long cardBalance;

    /**
     * 卡类型(1中石油 2中石化)
     */
    private Integer oilCardType;
    private String oilCardStatusName;
    private String companyName;
    private String cardTypeName;
    private String oilCardTypeName;
    private String vehicleNumberStr;
    private Boolean isNeedWarn;//是否需要提醒

}
