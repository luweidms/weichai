package com.youming.youche.table.domain.workbench;

import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 *
 * </p>
 * @author zengwen
 * @since 2022-05-03
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class OperationWorkbenchInfo extends BaseDomain {

    private static final long serialVersionUID = 1L;


    /**
     * 车辆年审预警数量
     */
    private Long vehicleAnnualReviewAmount;

    /**
     * 挂车年审预警数量
     */
    private Long trailerAnnualReviewAmount;

    /**
     * 驾驶证预警数量
     */
    private Long driverLicenseReviewAmount;

    /**
     * 从业资格预警数量
     */
    private Long occupationRequireReviewAmount;

    /**
     * 交强险预警数量
     */
    private Long compulsoryInsuranceReviewAmount;

    /**
     * 商业险预警数量
     */
    private Long commercialInsuranceReviewAmount;

    /**
     * 导入失败次数
     */
    private Long importFailReviewAmount;

    /**
     * 总车辆数
     */
    private Long carSumReviewAmount;

    /**
     * 自有车数量
     */
    private Long ownCarSumReviewAmount;

    /**
     * 外调车数量
     */
    private Long temporaryCarSumReviewAmount;

    /**
     * 招商车数量
     */
    private Long attractCarSumReviewAmount;

    /**
     * 挂车数量
     */
    private Long trailerCarSumReviewAmount;

    /**
     * 维保数量(待我审)
     */
    private Long maintenanceReviewAmount;

    /**
     * 维保数量(我发起)
     */
    private Long maintenanceMeReviewAmount;

    /**
     * 车辆费用(待我审)
     */
    private Long vehicleCostReviewAmount;

    /**
     * 车辆费用(我发起)
     */
    private Long vehicleCostMeReviewAmount;

    /**
     * 管理费用(待我审)
     */
    private Long manageCostReviewAmount;

    /**
     * 管理费用(我发起)
     */
    private Long manageCostMeReviewAmount;

    /**
     * 费用借支(待我审)
     */
    private Long costBorrowReviewAmount;

    /**
     * 费用借支(我发起)
     */
    private Long costBorrowMeReviewAmount;

    /**
     * 改单审核数量
     */
    private Long modifyExamineReviewAmount;

    /**
     * 失效审核数量
     */
    private Long invalidExamineReviewAmount;

    /**
     * 异常审核数量
     */
    private Long exceptionExamineReviewAmount;

    /**
     * 回单审核数量
     */
    private Long receiptExamineReviewAmount;

    /**
     * 今日开单数量
     */
    private Long billTodayReviewAmount;

    /**
     * 待预付数量
     */
    private Long prepaidOrderReviewAmount;

    /**
     * 在途数量
     */
    private Long intransitOrderReviewAmount;

    /**
     * 回单数量
     */
    private Long retrialOrderReviewAmount;

    /**
     * 客户档案数量
     */
    private Long customerReviewAmount;

    /**
     * 线路档案数量
     */
    private Long lineReviewAmount;

    /**
     * 司机档案数量
     */
    private Long driverReviewAmount;

    /**
     * 车辆档案数量
     */
    private Long carReviewAmount;

    /**
     * 服务商档案数量
     */
    private Long managerReviewAmount;

    /**
     * 服务站点数量
     */
    private Long managerSiteReviewAmount;

    /**
     * 租户ID
     */
    private Long tenantId;

    /**
     * 用户ID
     */
    private Long userInfoId;

}
