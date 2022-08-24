package com.youming.youche.cloud.dto.cmb;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName FileDownloadRepDto
 * @Description 添加描述
 * @Author zag
 * @Date 2022/1/13 10:41
 */
@Data
public class FileDownloadRepDto implements Serializable {
    /** 文件链接，可直接下载，有效期1小时 */
    private String fileUrl;

    /** 回单编号 */
    private String receiptNo;
}
