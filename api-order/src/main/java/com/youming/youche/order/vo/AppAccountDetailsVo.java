package com.youming.youche.order.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author: luona
 * @date: 2022/5/10
 * @description: T0DO
 * @version: 1.0
 */
@Data
public class AppAccountDetailsVo implements Serializable {
    private String month;//查询年月
    private String type;//0. 全部 1. 支出、2. 收入
    private Long userId;//用户id
    private String requestType;

}
