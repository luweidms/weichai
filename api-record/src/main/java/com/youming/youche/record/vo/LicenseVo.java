package com.youming.youche.record.vo;

import com.github.pagehelper.util.StringUtil;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

@Data
public class LicenseVo implements Serializable {
    /**
     * 车牌号码
     */
    private String plateNumber;
    /**
     * 牌照类型(1:整车，2：拖头)
     */
    private Integer licenceType;

    /**
     * 牌照类型 1整车 2拖头
     */
    private String licenceTypeName;
    public String getLicenceTypeName() {
        if (this.licenceType != null && this.licenceType == 1) {
            this.setLicenceTypeName("整车");
        } else {
            this.setLicenceTypeName("拖车");
        }
        return licenceTypeName;
    }

    /**
     * 车长
     */
    private String vehicleLength;
    /**
     * 车长
     */
    private String vehicleLengthName;

    /**
     * 车辆类型
     */
    private Integer vehicleStatus;

    /**
     * 车辆类型
     */
    private String vehicleStatusName;

    private String vehicleInfo;

    /**
     * 日期
     */
    private String annualreviewData;
    /**
     * 的有效期截止日期
     */
    private String effectiveDate;
    /**
     * 车辆年审费用
     */

    private String annualreviewCost;
    /**
     * 证照类型（1行驶证年审，2、运营证年审 3 交强险 4商业险 5其他险）
     */
    private String annualreviewType;
    /**
     * 牌照类型 1整车 2拖头
     */
    private String annualreviewTypeName;
    public String getAnnualreviewTypeName() {
        switch (annualreviewType){
            case "1":
                this.setAnnualreviewTypeName("行驶证年审");
                break;
            case "2":
                this.setAnnualreviewTypeName("营运证年审");
                break;
            case "3":
                this.setAnnualreviewTypeName("交强险");
                break;
            case "4":
                this.setAnnualreviewTypeName("商业险");
                break;
            case "5":
                this.setAnnualreviewTypeName("其他险");
                break;
        }
        return annualreviewTypeName;
    }
    /**
     * 创建时间
     */
    private String createDate;
    /**
     * 用户名
     */
    private String name;
    /**
     * 过期
     */
    private String leftDate;

}
