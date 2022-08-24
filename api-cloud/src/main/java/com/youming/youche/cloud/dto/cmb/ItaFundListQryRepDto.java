package com.youming.youche.cloud.dto.cmb;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName ItaFundListQryRepDto
 * @Description 添加描述
 * @Author zag
 * @Date 2022/1/13 10:44
 */
@Data
public class ItaFundListQryRepDto implements Serializable {
    /** 银行处理流水 */
    private String respNo;

    /** 平台编号 */
    private String platformNo;

    /** 总条数 */
    private String totalNum;

    /** 页码，同请求 */
    private String pageNum;

    /** 每页条数，同请求 */
    private String pageSize;

    /** 资金明细列表 */
    private List<FundInfoRepDto> item;
}
