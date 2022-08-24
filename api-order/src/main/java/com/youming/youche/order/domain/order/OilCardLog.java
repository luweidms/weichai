package com.youming.youche.order.domain.order;

import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * <p>
 *
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-03-22
 */
@Data
@Accessors(chain = true)
public class OilCardLog  extends BaseDomain{

    private static final long serialVersionUID = 1L;


    /**
     * 车辆司机名称
     */
    private String carDriverMan;

    /**
     * 司机类型
     */
    private Integer carUserType;

    /**
     * 油卡id
     */
    private Long cardId;
    /**
     * 创建时间
     */
    private LocalDateTime logDate;
    /**
     * 描述
     */
    private String logDesc;
    /**
     * 使用状态
     */
    private Integer logSts;
    /**
     * 使用类型
     */
    private Integer logType;
    /**
     * 油卡金额
     */
    private Long oilFee;
    /**
     * 油卡费用
     */
    private Long oilFeeMerchant;
    /**
     * 订单号
     */
    private Long orderId;
    /**
     * 车牌号码
     */
    private String plateNumber;
    /**
     *组织
     */
    private Long rootOrgId;
    /**
     *租户
     */
    private Long tenantId;
    /**
     *使用用户ID
     */
    private Long userId;



}
