package com.youming.youche.order.vo;

import lombok.Data;

import java.io.PipedReader;
import java.io.Serializable;

/**
 * @author: luona
 * @date: 2022/5/18
 * @description: T0DO
 * @version: 1.0
 */
@Data
public class CustParamVo implements Serializable {
    private Long customerId;
    private Integer isEdit;
}
