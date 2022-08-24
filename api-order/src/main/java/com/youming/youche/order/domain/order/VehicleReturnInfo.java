package com.youming.youche.order.domain.order;

    import com.baomidou.mybatisplus.annotation.TableField;
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
* @since 2022-03-25
*/
    @Data
        @EqualsAndHashCode(callSuper = true)
    @Accessors(chain = true)
    public class VehicleReturnInfo extends BaseDomain {

    private static final long serialVersionUID = 1L;

    /**
     * 开始时间
     */
    private LocalDateTime beginDate;
    /**
     * 出发省份
     */
    private Integer beginProvince;
    /**
     * 出发省份名称
     */
    private String beginProvinceName;
    /**
     * 出发地市编号
     */
    private Integer beginRegion;
    /**
     * 出发地市名称
     */
    private String beginRegionName;
    /**
     * 创建时间
     */
    private LocalDateTime createDate;
    /**
     * 目的省份
     */
    private Integer endProvince;
    /**
     * 目的省份名称
     */
    private String endProvinceName;
    /**
     * 目的地市编号
     */
    private Integer endRegion;
    /**
     * 目的地市名称
     */
    private String endRegionName;
    /**
     * 有效期
     */
    private LocalDateTime expiryDate;
    /**
     * 操作人
     */
    private Long opId;
    /**
     * 车牌号码
     */
    private String plateNumber;
    /**
     * 回货金额
     */
    private Long returnAmt;
    /**
     * 状态
     */
    private Integer state;
    /**
     * 用户id
     */
    private Long userId;
    /**
     * 车辆ID
     */
    private Long vehicleCode;

    /**
     * 订单号
     */
    @TableField(exist = false)
    private Long orderId;


}
