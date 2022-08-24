package com.youming.youche.record.domain.account;

import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 大小额行号表
 * </p>
 *
 * @author hzx
 * @since 2022-04-22
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class UnionBankInfo extends BaseDomain {

    /**
     * 行名全称
     */
    private String bankName;

    /**
     * 行名全称--用于显示
     */
    private String bankDisplayName;

    /**
     * 城市代码
     */
    private String cityCode;

    /**
     * 行别代码
     */
    private String bankClsCode;

    /**
     * 支付行号
     */
    private String bankNo;

    /**
     * 行号状态
     */
    private Integer status;


}
