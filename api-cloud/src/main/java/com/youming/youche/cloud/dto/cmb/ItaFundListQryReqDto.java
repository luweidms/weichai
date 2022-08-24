package com.youming.youche.cloud.dto.cmb;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName ItaFundListQryReqDto
 * @Description 添加描述
 * @Author zag
 * @Date 2022/1/13 10:44
 */
@Data
public class ItaFundListQryReqDto implements Serializable {
    /** 平台编号 */
    private String platformNo;

    /** 对手方账号 */
    private String oppAccNo;

    /** 页码 */
    private String pageNum;

    /** 每页条数 */
    private String pageSize;
}
