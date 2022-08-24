package com.youming.youche.market.dto.youca;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 *
 * </p>
 *
 * @author XXX
 * @since 2022-03-24
 */
@Data
public class OilCardLogDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 司机名称
     */
    private String carDriverMan;

    /**
     * 时间
     */
    private LocalDateTime logDate;

    /**
     * 油费
     */
    private Long oilFee;

    /**
     * 订单号
     */
    private Long orderId;

    /**
     * 车牌号
     */
    private String plateNumber;

    /**
     * 备注
     */
    private String  remark;


}
