package com.youming.youche.cloud.dto.ocr;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class DriverLicenseDto implements Serializable {

    /**
     * 证件类型
     */
    private String type;
    /**
     * 证件号
     */
    private String number;
    /**
     * 司机姓名
     */
    private String name;
    /**
     * 司机性别
     */
    private String sex;
    /**
     * 国籍
     */
    private String nationality;
    /**
     * 地址
     */
    private String address;
    /**
     * 发行日期
     */
    private String birth;
    /**
     * 发证日期
     */
    private String issueDate;
    /**
     * 行驶证类别
     */
    private String propertyClass;
    /**
     * 有效期限起始日
     */
    private String validFrom;
    /**
     * 有效期限终止日
     */
    private String validTo;
    /**
     *
     */
    private String issuingAuthority;
    /**
     * 文件编号
     */
    private String fileNumber;
    /**
     * 记录
     */
    private String record;
    /**
     * 证件号
     */
    private String accumulatedScores;
    /**
     * 状态
     */
    private List<String> status = null;
    /**
     * 生产日期
     */
    private String generationDate;
    /**
     * 当前时间
     */
    private String currentTime;
    /**
     * 位置
     */
    private Object textLocation;
}
