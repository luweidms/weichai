package com.youming.youche.record.dto.license;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName LicenseDetailsDto
 * @Description 添加描述
 * @Author zag
 * @Date 2022/5/4 18:16
 */
@Data
public class LicenseDetailsDto implements Serializable {

    /** 证照类型（1：整车；2：牵引车） */
    private Integer licenceType;
    /** 品牌 */
    private String  brandModel;
    /** 业务类型（1：行驶证年审，2：营运证年审，3：交强险，4：商业险，5：其他险，6：违章，7：事故） */
    private Integer  businessType;
    /** 已处理 */
    private Integer  processed;
    /** 未处理 */
    private Integer  notProcessed;

    private String licenceTypeName;
    public String getLicenceTypeName(){
        if(licenceType==1){
            this.setLicenceTypeName("整车");
        }else {
            this.setLicenceTypeName("拖车");
        }
        return licenceTypeName;
    }

    private String businessTypeName;
    public String getBusinessTypeName(){
        switch (businessType){
            case 1:
                this.setBusinessTypeName("行驶证年审");
                break;
            case 2:
                this.setBusinessTypeName("营运证年审");
                break;
            case 3:
                this.setBusinessTypeName("交强险");
                break;
            case 4:
                this.setBusinessTypeName("商业险");
                break;
            case 5:
                this.setBusinessTypeName("其他险");
                break;
            case 6:
                this.setBusinessTypeName("违章");
                break;
            case 7:
                this.setBusinessTypeName("事故");
                break;
        }
        return this.businessTypeName;
    }

}
