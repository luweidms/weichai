package com.youming.youche.order.dto;

import com.youming.youche.order.domain.OrderAccount;
import com.youming.youche.order.vo.OrderAccountOutVo;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class OrderAccountBalanceDto implements Serializable {
    List<OrderAccount> accountList;
    OrderAccountOutVo oa;
}
