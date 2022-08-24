package com.youming.youche.cloud.dto.ocr;

import lombok.Data;

import java.io.Serializable;

/**
 * @author luwei
 */
@Data
public class IdcardVerificationResult implements Serializable {
    /**
     * 验证证件号是否正确
     */
    private Boolean validNumber;
    /**
     * 验证证件人生日是否正确
     */
    private Boolean validBirth;
    /**
     * 验证证件人性别是否正确
     */
    private Boolean validSex;
    /**
     * 验证证件日期是否正确
     */
    private Boolean validDate;
}
