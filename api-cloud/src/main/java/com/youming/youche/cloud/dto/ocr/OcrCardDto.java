package com.youming.youche.cloud.dto.ocr;

import lombok.Data;

import java.io.Serializable;

@Data
public class OcrCardDto implements Serializable {

    /**
     * 姓名
     */
    private String name;
    /**
     * 性别
     */
    private String sex;
    /**
     * 籍贯
     */
    private String ethnicity;
    /**
     * 出生日期
     */
    private String birth;

    /**
     * 身份证号
     */
    private String number;
    /**
     * 地址
     */
    private String address;
    /**
     * 发行
     */
    private String issue;
    /**
     * 有效期限起始日
     */
    private String validFrom;
    /**
     * 有效期限终止日
     */
    private String validTo;
    /**
     * 验证结果
     */
    private IdcardVerificationResult verificationResult;

    private Object textLocation;



}
