package com.youming.youche.system.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class BillPlatformDto implements Serializable {

    /**
     * 用户id
     */
    private Long userId;
    /**
     *  车牌号
     */
    private String platName;
    /**
     * 系统前缀
     */
    private String sysPre;
    /**
     * 最大可开票金额
     */
    private String maxBillAmount;
    /**
     * 绑定数量
     */
    private String bindCards;
    /**
     *  托运协议模板
     */
    private String templateUrl;



}
