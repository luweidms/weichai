package com.youming.youche.system.vo.mycenter;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @ClassName BindBankCardVo
 * @Description 添加描述
 * @Author zag
 * @Date 2022/2/20 14:17
 */
@Data
public class BankCardBindVo implements Serializable {

    @NotNull(message = "账户Id不能为空")
    private Long acctId;
    /** 用户Id */
    private Long userId;
    /** 鉴权响应流水号 */
    private String origAuthRespNo;
    /** 鉴权金额 */
    private String authAmt;
    @NotBlank(message = "开户行Id不能为空")
    private String bankId;
    @NotBlank(message = "开户行名称不能为空")
    private String bankName;
    @NotBlank(message = "账户名称不能为空")
    private String acctName;
    @NotBlank(message = "银行卡号不能为空")
    private String acctNo;
    @NotBlank(message = "银行预留手机号不能为空")
    private String billId;
    @NotNull(message = "开户省Id信息不能为空")
    private Long provinceId;
    @NotBlank(message = "开户省不能为空")
    private String provinceName;
    @NotNull(message = "开户市Id信息不能为空")
    private Long  cityId;
    @NotBlank(message = "开户市不能为空")
    private String  cityName;
    @NotNull(message = "开户区Id信息不能为空")
    private Long districtId;
    @NotBlank(message = "开户区不能为空")
    private String districtName;
    /** 身份证号 */
    private String identification;
}
