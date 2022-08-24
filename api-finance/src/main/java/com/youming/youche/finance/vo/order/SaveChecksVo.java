package com.youming.youche.finance.vo.order;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 账单核销入参
 *
 * @author hzx
 * @date 2022/2/16 10:19
 */
@Data
public class SaveChecksVo implements Serializable {

    private static final long serialVersionUID = 1L;

    // 账单号
    String billNumber;

    // 核销信息
    List<CheckDatas> lists;

}
