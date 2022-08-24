package com.youming.youche.record.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName LicenseDetailsVo
 * @Description 添加描述
 * @Author zag
 * @Date 2022/5/4 18:38
 */
@Data
public class LicenseDetailsVo implements Serializable {

    private String licenceTypeName;
    private Integer licenceType;
    private String  brandModel;
    private int nsOneDealNum;
    private int nsOneNotDealNum;
    private int nsTwoDealNum;
    private int nsTwoNotDealNum;
    private int syxDealNum;
    private int syxNotDealNum;
    private int jqxDealNum;
    private int jqxNotDealNum;
    private int otherDealNum;
    private int otherNotDealNum;
    private int wzDealNum;
    private int wzNotDealNum;
    private int sgDealNum;
    private int sgNotDealNum;

}
