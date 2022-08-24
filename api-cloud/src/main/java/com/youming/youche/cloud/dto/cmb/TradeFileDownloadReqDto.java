package com.youming.youche.cloud.dto.cmb;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName TradeFileDownloadReqDto
 * @Description 添加描述
 * @Author zag
 * @Date 2022/1/13 13:44
 */
@Data
public class TradeFileDownloadReqDto implements Serializable {
    /** 平台编号 */
    private String platformNo;

    /** 平台编号 */
    private String fileType;

    /** 平台编号 */
    private String isInnerAcc;

    /** 平台编号 */
    private String mbrNo;

    /** 平台编号 */
    private String startDate;

    /** 平台编号 */
    private String endDate;
}
