package com.youming.youche.finance.vo.order;


import lombok.Data;

import java.io.Serializable;

// 费用上报 审核入参
@Data
public class OrderCostReportVo  implements Serializable {

    private static final long serialVersionUID = 3966015636015046602L;
    private Long id;
    private  String auditRemark;//审核原因
    private  String remark ;//审核原因
    /**载重校准里程**/
    private Long loadMileage;

    /**空载校准里程**/
    private Long capacityLoadMileage;

    // 订单号
    private  String orderId;

    private  String busiCode; // 业务编码
    private Long busiId;//业务id
    private String desc;//结果的描述
    private Integer chooseResult;//审核结果

    private String loadMileages ; //载重校准里程
    private String capacityLoadMileages;//空载校准里程
}
