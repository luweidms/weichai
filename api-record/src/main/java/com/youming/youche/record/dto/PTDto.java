package com.youming.youche.record.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author hzx
 * @date 2022/4/21 19:38
 */
@Data
public class PTDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private String acctName; // 账户名
    private String acctNo; // 银行卡号
    private String bankName; // 开户行
    private String bankProvCity; // 开户省市
    private String branchName; // 开户支行
    private String tailNumber;
    private List<Map> tailNumberNew;
    private String adminUserTailNumber; // 账户信息
}
