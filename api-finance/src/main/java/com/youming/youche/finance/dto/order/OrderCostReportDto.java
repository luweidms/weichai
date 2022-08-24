package com.youming.youche.finance.dto.order;

import com.youming.youche.finance.domain.order.OrderCostDetailReport;
import com.youming.youche.finance.domain.order.OrderCostOtherReport;
import com.youming.youche.finance.domain.order.OrderCostReport;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 上报费用返回
 */

@Data
public class OrderCostReportDto implements Serializable {
    private Long id; //主键
    private  Long orderId;//订单号
    private String plateNumber;//车牌号
    private String userName;//用户姓名
    private  String linkPhone; //手机号
    private String sourceName; //线路名称
    private String dependTime; //靠台时间
    private  String subTime; //提交时间
    private String subUserName; //提交时间中止
    private Integer state; //状态
    private  String stateName; //状态名称
    private  Integer isAudit; //是否可以审核
    private  Integer sourceRegion;//始发市
    private Integer desRegion;//到达市
    private  Boolean  isDoSave; //是否可以上报
    private String sourceRegionName;//始发市名称
    private  String desRegionName ;//到达市名称
    private  String orderStateName; //订单状态名称

    private  String isFullOilName; //是否是油名称
    private  Integer isFullOil; // 是否是油

    private  Long amount; //金额
    private  String paymentWayName; //成本模式

    private  String startKmUrl; //出车url
    private  String unloadingKmUrl; //卸货仪表盘公里数 附件URL
    private  String loadingKmUrl; //装货仪表盘公里数 附件URL
    private  String endKmUrl; //交车仪表盘公里数 附件URL

    private Long capacityLoadMileage; //空载校准里程 单位(米)

    private Integer orderCostState; //上报状态

    private  Double startKm; //出车仪表盘公里数 单位(米)
    private String startKmStr; //出车仪表盘公里数 单位(米)名称
    private  Double loadingKm; //卸货仪表盘公里数 单位(米)
    private String loadingKmStr; //卸货仪表盘公里数 单位(米)名称
    private  Double unloadingKm; //卸货仪表盘公里数 单位(米)
    private String unloadingKmStr; //卸货仪表盘公里数 单位(米)名称
    private  Double endKm; //交车仪表盘公里数 单位(米)
    private String endKmStr; //交车仪表盘公里数 单位(米)名称
    private  String otherTotalAmount; //其他费用金额
    private Long loadMileage; //载重校准里程 单位(米)
    private OrderCostReport orderCostReport; //
    private LocalDateTime subTimes; //提交时间
    private List<OrderCostDetailReport> oilCostDataList; //
//    private  List<OrderCostDetailReport>  etcCostDataList;

    private  List<OrderCostOtherReport> otherCostDataList; //
    /**
     * 车辆id
     */
    private Long vehicleCode;

    /**
     * 车辆种类
     */
    private Integer vehicleClass;


}
