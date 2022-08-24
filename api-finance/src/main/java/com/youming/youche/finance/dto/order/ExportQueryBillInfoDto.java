package com.youming.youche.finance.dto.order;

import lombok.Data;

import java.io.Serializable;

/**
 * 应付管理-账单--导出打印（账单部分）
 *
 * @author hzx
 * @date 2022/4/11 18:27
 */
@Data
public class ExportQueryBillInfoDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String orgName;
    private String dependTime;
    private String countTotal;
    private String costPrice;
    private String confirMamount;
    private String diff1;
    private String diff2;
    private String diff3;
    private String diff4;
    private String diff5;

}
