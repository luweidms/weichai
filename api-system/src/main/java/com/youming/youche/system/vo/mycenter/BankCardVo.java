package com.youming.youche.system.vo.mycenter;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName BankCardVo
 * @Description 添加描述
 * @Author zag
 * @Date 2022/2/20 14:52
 */
@Data
public class BankCardVo implements Serializable {

    /** id */
    private Long id;
    /** 账户Id */
    private Long acctId;
    /** 银行Id */
    private String bankId;
    /** 银行名称 */
    private String bankName;
    /** 账户名称 */
    private String acctName;
    /** 银行卡号 */
    private String acctNo;
    /** 手机号 */
    private String billId;
    /** 省Id */
    private Long provinceId;
    /** 省 */
    private String provinceName;
    /** 市Id */
    private Long  cityId;
    /** 市名称 */
    private String  cityName;
    /** 区Id */
    private Long districtId;
    /** 区名称 */
    private String districtName;
    /** 银行名称 */
    private String identification;
}
