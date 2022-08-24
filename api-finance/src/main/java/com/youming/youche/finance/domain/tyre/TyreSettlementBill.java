package com.youming.youche.finance.domain.tyre;

import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 轮胎结算账单汇总
 * </p>
 *
 * @author hzx
 * @since 2022-04-15
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class TyreSettlementBill extends BaseDomain {

    private static final long serialVersionUID = 1L;


    /**
     * 车队id
     */
    private Long tenantId;

    /**
     * 车队标识id
     */
    private String identification;

    /**
     * 公司名称
     */
    private String truckTeamName;

    /**
     * 账单日期，例201908
     */
    private String billMonth;

    /**
     * gps里程收费
     */
    private Double mileage;

    /**
     * 救援费用
     */
    private Double rescueCosts;

    /**
     * 故障胎赔付金额
     */
    private Double compensationMoney;

    /**
     * 服务费用
     */
    private Double serviceCosts;

    /**
     * 结算总额
     */
    private Double totalSettlement;

    /**
     * 期初资产
     */
    private Double initialAssets;

    /**
     * 新增期初资产
     */
    private Double newInitialAssets;

    /**
     * 退出期初资产
     */
    private Double quitInitialAssets;

    /**
     * 累计期初资产
     */
    private Double cumulativeInitialAssets;

    /**
     * 累计轮胎回购款
     */
    private Double cumulativeTyreRepurchase;

    /**
     * 累计结算回购款
     */
    private Double cumulativeSettlementRepurchase;

    /**
     * 累计轮胎使用押金
     */
    private Double cumulativeUseDeposit;

    /**
     * 累计已结算轮胎使用押金
     */
    private Double cumulativeSettleUseDeposit;

    /**
     * 本期应结算回购款
     */
    private Double currentSettlementRepurchase;

    /**
     * 本期应结算轮胎使用押金
     */
    private Double currentSettlementUseDeposit;

    /**
     * 范围外安装销售
     */
    private Double installationSales;

    /**
     * 范围外拆胎回购
     */
    private Double tyreRepurchase;

    /**
     * 总金额
     */
    private Double totalSum;

    /**
     * 本期应抵扣新增资产
     */
    private Double deductionNewAssets;

    /**
     * 本期应抵扣退出资产
     */
    private Double deductionQuitAssets;

    /**
     * 资产合计
     */
    private Double totalAssets;

    /**
     * 付款方式
     */
    private Integer payClass;

    /**
     * 业务编码
     */
    private String busiCode;

    /**
     * 1待确认支付、2待付款、3确认中 4已付款 5已结算
     */
    private Integer tyrePayState;


}
