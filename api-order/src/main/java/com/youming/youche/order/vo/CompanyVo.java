package com.youming.youche.order.vo;

import lombok.Data;

@Data
public class CompanyVo {

    private static final long serialVersionUID = 1L;
    /**
     * 公司名称
     */
    private String companyName;
    /**
     * 客户编号
     */
    private Long customerId;
    /**
     * 客户简称
     */
    private String customerName;

    private Long num;

    private Long orgId;
}
