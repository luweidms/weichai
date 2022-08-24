package com.youming.youche.order.domain.monitor;

import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 订单到达历史表
 *
 * @author hzx
 * @date 2022/3/9 14:51
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class MonitorOrderAgingArriveH extends BaseDomain {

    // '订单号',
    private Long orderId;

    // '车牌号',
    private String plateNumber;

    // '预估到达时间',
    private String estArriveDate;

    // '实际到达时间',
    private String realArriveDate;

    // '线路详情',
    private String lineDetail;

    // '当前位置纬度',
    private String currNand;

    // '当前位置经度',
    private String currEand;

    // '目的地经度',
    private String endNand;

    // '目的地纬度',
    private String endEand;

    // '时限比例：已用时间/经停时限（已用百分比)',
    private String timeLimitRatio;

    // '里程比例',
    private String mileageRatio;

    // '类型:1 预估迟到  2 实际迟到 ',
    private Integer type;

    // '迟到时间(小时)',
    private Long lateTime;

    // '剩余公里数',
    private Long surplusDistance;

    // '车队id',
    private Long tenantId;

}
