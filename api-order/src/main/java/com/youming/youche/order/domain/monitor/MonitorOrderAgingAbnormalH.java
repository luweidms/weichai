package com.youming.youche.order.domain.monitor;

import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 异常数据历史表
 *
 * @author hzx
 * @date 2022/3/9 11:48
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class MonitorOrderAgingAbnormalH extends BaseDomain {

    //'订单号'
    private Long orderId;

    // '车牌号'
    private String plateNumber;

    // '开始时间'
    private String startDate;

    // '截至时间'
    private String endDate;

    // '线路详情'
    private String lineDetail;

    // '起始位置纬度'
    private String startNand;

    // '起始位置经度'
    private String startEand;

    // '截至地经度'
    private String endNand;

    // '截至地纬度'
    private String endEand;

    // '类型:1 堵车缓行 2 异常停留'
    private Integer type;

    // '类型:0 为起始地 >0 为经停点(经停点序号)'
    private Integer lineType;

    // '持续时间(小时)'
    private Long continueTime;

    // '车队id'
    private Long tenantId;

}

