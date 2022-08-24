package com.youming.youche.finance.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author zengwen
 * @date 2022/6/25 15:23
 */
@Data
public class QueryAccountStatementUserVo implements Serializable {

    private String billReceiverName;
    private String billReceiverMobile;
    private String monList;
}
