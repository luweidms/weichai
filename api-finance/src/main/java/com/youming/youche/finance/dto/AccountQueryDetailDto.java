package com.youming.youche.finance.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.youming.youche.finance.commons.util.CommonUtil;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
/**
 * @author zengwen
 * @date 2022/4/12 17:23
 */
@Data
public class AccountQueryDetailDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createDate;

    /**
     * ID
     */
    private String accountDatailsId;

    /**
     * 费用科目ID
     */
    private Long subjectsId;

    /**
     * 账户ID
     */
    private Long accountId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 业务编码
     */
    private Long businessNumber;

    /**
     * 费用科目名称
     */
    private String subjectsName;

    /**
     * 金额（分）
     */
    private Long amount;

    /**
     * 费用类型 1：消费  2：充值
     */
    private Integer costType;


    private String costTypeName;

    /**
     * 备注
     */
    private String note;

    /**
     * 联系人手机
     */
    private String mobilePhone;

    /**
     * 联系人姓名
     */
    private String linkman;

    /**
     * 登录名
     */
    private String loginName;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 订单ID
     */
    private String orderId;

    /**
     * 车牌号
     */
    private String plateNumer;

    private String businessNumberName;

    private double amountDouble;

    private Integer businessTypes;

    private String businessTypesName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime opDate;

    private Long tenandId;

    private boolean ownOrder;

    private String teamName;//车队名称

    public double getAmountDouble() {
        if (amount != null) {
            setAmountDouble(CommonUtil.getDoubleFormatLongMoney(getAmount(), 2));
        }
        return amountDouble;
    }

}
