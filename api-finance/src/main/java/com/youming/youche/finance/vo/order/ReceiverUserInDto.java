package com.youming.youche.finance.vo.order;

import lombok.Data;

import java.io.Serializable;

/**
 * @author zengwen
 * @date 2022/4/14 11:28
 */
@Data
public class ReceiverUserInDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String billReceiverMobile;//账单接收人手机号
    private Long billReceiverUserId;//账单接收人用户编号
    private String billReceiverName;//账单接收人名称
}
