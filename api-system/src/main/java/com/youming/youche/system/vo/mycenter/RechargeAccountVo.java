package com.youming.youche.system.vo.mycenter;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName RechargeAccountVo
 * @Description 添加描述
 * @Author zag
 * @Date 2022/2/20 16:29
 */
@Data
public class RechargeAccountVo implements Serializable {

    /** 帐户名 */
    private String acctName;
    /** 证件编号 */
    private String acctNo;
    /** 省 */
    private String provinceName;
    /** 市 */
    private String cityName;
    /** 银行名称 */
    private String bankName;
    /** 支行名称 */
    private String branchName;
}
