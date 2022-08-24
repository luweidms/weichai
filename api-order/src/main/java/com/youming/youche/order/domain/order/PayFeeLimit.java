package com.youming.youche.order.domain.order;

    import com.youming.youche.commons.base.BaseDomain;
import com.youming.youche.conts.SysStaticDataEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
* <p>
    * 
    * </p>
* @author CaoYaJie
* @since 2022-03-17
*/
    @Data
        @EqualsAndHashCode(callSuper = true)
    @Accessors(chain = true)
    public class PayFeeLimit extends BaseDomain {

    private static final long serialVersionUID = 1L;

    /**
     * 类型
     */
    private Integer type;
    /**
     * 科目
     */
    private Integer subType;
    /**
     * 科目名称
     */
    private String subTypeName;
    /**
     * 比较值
     */
    private Long value;
    /**
     * 創建时间
     */
    private LocalDateTime createDate;
    /**
     * 操作人id
     */
    private Long opId;
    /**
     * 操作人
     */
    private String opName;
    /**
     * 操作时间
     */
    private LocalDateTime opDate;
    /**
     * 备注
     */
    private String remark;
    /**
     * 车队id
     */
    private Long tenantId;

    public BigDecimal getValueBig() {

        if (subType < SysStaticDataEnum.PAY_FEE_LIMIT_SUB_TYPE.MAX_SCAN_CODE_ADD_OIL_TIMES_302 || subType == SysStaticDataEnum.PAY_FEE_LIMIT_SUB_TYPE.OIL_CARD_DEPOSIT_303) {//金额类型数据
            return new BigDecimal(value).divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP);
        } else {
            return new BigDecimal(value);
        }
    }

    public String getPayType() {

        if (value == 0l) {
            return "私人账户付款";
        } else if (value == 1l) {
            return "对公账户付款";
        }else{
            return "未设置付款账户";
        }
    }

    public String getNeedBill() {

        if (value == 0l) {
            return "不需要开票";
        } else if (value == 1l) {
            return "需要开票";
        } else {
            return "需要开票";
        }

    }

}
