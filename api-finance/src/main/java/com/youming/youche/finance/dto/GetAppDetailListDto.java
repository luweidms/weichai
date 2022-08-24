package com.youming.youche.finance.dto;

import com.youming.youche.finance.commons.util.CommonUtil;
import lombok.Data;

import java.io.Serializable;

/**
 * @author zengwen
 * @date 2022/5/20 13:02
 */
@Data
public class GetAppDetailListDto implements Serializable {

    /**
     * ID
     */
    private Long id;

    /**
     * 司机名字
     */
    private String carDriverName;

    /**
     * 司机手机号码
     */
    private String carDriverPhone;

    /**
     * 车牌号
     */
    private String plateNumber;

    /**
     * 总费用
     */
    private Long totalFee;

    /**
     *
     */
    private Double totalFeeDouble;

    public Double getTotalFeeDouble() {
        return CommonUtil.getDoubleFormatLongMoney(totalFee == null ? 0L : totalFee, 2);
    }

}
