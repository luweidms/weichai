package com.youming.youche.finance.vo.order;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author zengwen
 * @date 2022/4/14 11:25
 */
@Data
public class AccountStatementCreateVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer createType;

    private List<String> billMonths;

    private List<ReceiverUserInDto> receiverUserInDtos;

    private List<AccountStatementInVo> accountStatementInVos;
}
