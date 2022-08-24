package com.youming.youche.finance.vo;

import com.youming.youche.finance.vo.order.ReceiverUserInDto;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author zengwen
 * @date 2022/6/25 15:46
 */
@Data
public class CreateAccountStatementVo implements Serializable {

    private List<String> billMonths;

    private List<ReceiverUserInDto> receiverUserInDtos;
}
