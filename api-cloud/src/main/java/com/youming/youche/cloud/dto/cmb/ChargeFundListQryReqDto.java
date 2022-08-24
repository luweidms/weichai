package com.youming.youche.cloud.dto.cmb;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName ChargeFundListQryReqDto
 * @Description 添加描述
 * @Author zag
 * @Date 2022/1/13 10:39
 */
@Data
public class ChargeFundListQryReqDto implements Serializable {
    /** 平台编号 */
    private String platformNo;

    /** 充值子商户编号 */
    private String mbrNo;

    /** 查询开始日期 */
    private String startDate;

    /** 查询结束日期 */
    private String endDate;

    /** 页码 */
    private String pageNum;

    /** 每页条数 */
    private String pageSize;
}
