package com.youming.youche.record.domain.license;

import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class License extends BaseDomain {
    /**
     * 车牌号码
     */
    private String plateNumber;
    /**
     * 牌照类型(1:整车，2：拖头)
     */
    private Integer licenceType;
    /**
     * 车长
     */
    private String vehicleLength;
    /**
     * 车辆的年审日期
     */
    private String annualreviewData;
    /**
     * 证照类型（1行驶证年审，2、车辆年审 3 交强险 4商业险 5其他险）
     */
    private String annualreviewType;
    /**
     * 车辆年审的有效期截止日期
     */
    private String effectiveDate;
    /**
     * 车辆年审费用
     */
    private String annualreviewCost;
    /**
     * 创建时间
     */
    private String createDate;
    /**
     * 用户名
     */
    private String name;

}
