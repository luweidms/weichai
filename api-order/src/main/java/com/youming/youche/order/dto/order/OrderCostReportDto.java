package com.youming.youche.order.dto.order;

import com.youming.youche.order.domain.order.OrderCostDetailReport;
import com.youming.youche.order.domain.order.OrderCostOtherReport;
import com.youming.youche.order.domain.order.OrderMainReport;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
public class OrderCostReportDto implements Serializable {
    private Long id;
    /**
     * 审核备注
     */
    private  Integer costId;
    private String auditRemark;

    /**
     * 空载校准里程 单位(米)
     */
    private Long capacityLoadMileage;

    /**
     * 校准时间
     */
    private LocalDateTime checkTime;

    /**
     * 交车仪表盘公里数 单位(米)
     */
    private Long endKm;

    private String endKmStr;

    /**
     * 交车仪表盘公里数 附件
     */
    private Long endKmFile;

    /**
     * 交车仪表盘公里数 附件URL
     */
    private String endKmUrl;

    /**
     * 是否可以审核 0否 1是 枚举SysStaticDataEnum.IS_AUTH
     */
    private Integer isAudit;

    /**
     * 是否曾经审核通过 0否 1是 枚举SysStaticDataEnum.IS_AUTH
     */
    private Integer isAuditPass;

    /**
     * 是否加满油(0未加满油 1已加满油)
     */
    private Integer isFullOil;

    /**
     * 载重校准里程 单位(米)
     */
    private Long loadMileage;

    /**
     * 装货仪表盘公里数 单位(米)
     */
    private Long loadingKm;

    private String loadingKmStr;

    /**
     * 装货仪表盘公里数 附件
     */
    private Long loadingKmFile;

    /**
     * 装货仪表盘公里数 附件URL
     */
    private String loadingKmUrl;

    /**
     * 操作人
     */
    private Long opId;

    /**
     * 操作人
     */
    private String opName;

    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 车牌号
     */
    private String plateNumber;

    /**
     * 出车仪表盘公里数 单位(米)
     */
    private Long startKm;

    private String startKmStr;

    /**
     * 出车仪表盘公里数附件
     */
    private Long startKmFile;

    /**
     * 出车仪表盘公里数附件URL
     */
    private String startKmUrl;

    /**
     * 上报状态 0未提交 1已提交 2待审核 3审核中 4审核不通过 5审核通过
     */
    private Integer state;

    /**
     * 提交时间
     */
    private LocalDateTime subTime;

    /**
     * 提交人
     */
    private Long subUserId;

    /**
     * 提交人
     */
    private String subUserName;

    /**
     * 租户ID
     */
    private Long tenantId;

    /**
     * 卸货仪表盘公里数 单位(米)
     */
    private Long unloadingKm;

    private String unloadingKmStr;

    /**
     * 卸货仪表盘公里数 附件
     */
    private Long unloadingKmFile;

    /**
     * 卸货仪表盘公里数 附件URL
     */
    private String unloadingKmUrl;
    private String  startKmUrlPath;
    private String  loadingKmUrlPath;
    private String  unloadingKmUrlPath;
    private String  endKmUrlPath;
    private String  stateName;
    private String  isFullOilName;

    private  String oilTotalAmount;
    private  String etcTotalAmount;
 //   private  List<OrderCostDetailReport> costDetailReports;
    private  List<OrderCostDetailReport> oilCostDataList;
  //  private  List<Map> etcCostDataList;
    private  String otherTotalAmount;
    private  List<OrderCostOtherReport> otherCostDataListStr;
    private Long userId;
    private OrderMainReport orderMainReport;
    private String operation;
    private  List<Map> costDataList;
    //司机手机号
    private String driverPhone;
    //司机姓名
    private String carDriverMan;
    private String sourceRegionName;
    private String desRegionName;
    private String orderStateName;
    private Boolean isDoSave;
    private Integer orderCostState;
}
