package com.youming.youche.order.domain.order;

    import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
* <p>
    * 
    * </p>
* @author CaoYaJie
* @since 2022-03-20
*/
    @Data
        @EqualsAndHashCode(callSuper = true)
    @Accessors(chain = true)
    public class WorkOrderInfo extends BaseDomain {

    private static final long serialVersionUID = 1L;

    /**
     * 审核意见
     */
    private Integer amountReference;
    /**
     * 工单承运商ID
     */
    private Long carrierId;
    /**
     * 工单承运商
     */
    private String carrierName;
    /**
     * 处理错误信息
     */
    private String dealRemark;
    /**
     * 处理状态
     */
    private Integer dealState;
    /**
     * 处理类型
     */
    private Integer dealType;
    /**
     * 计划到达时间
     */
    private LocalDateTime destPlanTime;
    /**
     * 执行任务司机手机号
     */
    private String driverPhoneNo;
    /**
     * 任务单号
     */
    private String jobCode;
    /**
     * 车头
     */
    private String licensePlate;
    /**
     * 线路参考里程
     */
    private Float mileageReference;
    /**
     * 线路id
     */
    private Long routeCode;
    /**
     * 线路名
     */
    private String routeName;
    /**
     * 来源渠道
     */
    private Integer sourceType;
    /**
     * 计划出发时间
     */
    private LocalDateTime srcPlanTime;
    /**
     * 派车时间
     */
    private LocalDateTime submitTime;
    /**
     * 车队id
     */
    private Long tenantId;
    /**
     * 挂车
     */
    private String trailerLicensePlate;
    /**
     * 挂车车辆规格
     */
    private String trailerLicenseSize;
    /**
     * 更新时间
     */
    private LocalDateTime updatedTime;
    /**
     * 参考装载重量
     */
    private Float weightReference;


}
