package com.youming.youche.order.domain.monitor;

import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 订单靠台历史表
 *
 * @author hzx
 * @date 2022/3/9 14:05
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class MonitorOrderAgingDependH extends BaseDomain {

    // '订单号',
    private Long orderId;

    // '车牌号',
    private String plateNumber;

    // '预估靠台时间',
    private String dependTime;

    // '实际靠台时间',
    private String carDependTime;

    // '线路详情',
    private String lineDetail;

    // '当前位置纬度',
    private String currNand;

    // '当前位置经度',
    private String currEand;

    // '靠台地经度',
    private String dependNand;

    // '靠台地纬度',
    private String dependEand;

    // '类型:1 预估晚 2 晚靠台 3：实际晚靠台',
    private Integer type;

    // '类型:0 为起始地 >0 为经停点(经停点序号)',
    private Integer lineType;

    // '剩余(晚靠台)时间',
    private Integer surplusDate;

    // '剩余公里数',
    private Integer surplusDistance;

    // '车队id',
    private Long tenantId;

}
