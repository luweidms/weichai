package com.youming.youche.finance.vo.order;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author zengwen
 * @date 2022/4/14 11:25
 */
@Data
public class PayByCashVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 业务主键 多个,分割
     */
    private Long flowId;

    /**
     * 0 线下打款 1线上打款
     */
    private Integer isAutomatic;

    private Integer status;

    /**
     * 收款方用户类型
     */
    private Integer userType;

    /**
     * 备注
     */
    private String desc;

    /**
     * 1 审核通过 2审核不通过
     */
    private Integer chooseResult;

    /**
     * 收款虚拟卡号
     */
    private String  accId;

    /**
     * 付款虚拟卡号
     */
    private String payAccId;

    /**
     * 图片Id
     */
    private String fileId;

    private String serviceFee;
}
