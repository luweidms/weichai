package com.youming.youche.cloud.dto.cmb;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName BnkAccListQryRepDto
 * @Description 添加描述
 * @Author zag
 * @Date 2022/1/13 10:35
 */
@Data
public class BnkAccListQryRepDto implements Serializable {
    /** 银行处理流水 */
    private String respNo;

    /** 平台编号 */
    private String platformNo;

    /** 绑定账户列表 */
    private List<BindAccInfoRepDto> accList;
}
