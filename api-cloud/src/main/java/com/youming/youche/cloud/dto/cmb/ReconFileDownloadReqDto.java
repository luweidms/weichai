package com.youming.youche.cloud.dto.cmb;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName ReconFileDownloadReqDto
 * @Description 添加描述
 * @Author zag
 * @Date 2022/1/13 11:20
 */
@Data
public class ReconFileDownloadReqDto implements Serializable {
    /** 平台编号 */
    private String platformNo;

    /** 对账单文件日期 */
    private String clearDate;
}
