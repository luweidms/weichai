package com.youming.youche.cloud.dto.cmb;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName MbrBalQryReqDto
 * @Description 添加描述
 * @Author zag
 * @Date 2022/1/13 10:55
 */
@Data
public class MbrBalQryReqDto implements Serializable {
    /** 平台编号 */
    private String platformNo;

    /** 子商户编号，查询平台子户时，传值请参考参数规定中的subAccNo */
    private String mbrNo;
}
