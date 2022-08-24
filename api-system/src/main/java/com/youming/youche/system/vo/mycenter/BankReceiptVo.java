package com.youming.youche.system.vo.mycenter;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName BankReceiptVo
 * @Description 添加描述
 * @Author zag
 * @Date 2022/2/23 13:34
 */
@Data
public class BankReceiptVo implements Serializable {
    /** 文件链接，可直接下载，有效期1小时 */
    private String fileUrl;

    /** 回单编号 */
    private String receiptNo;
}
