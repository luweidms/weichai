package com.youming.youche.record.dto.license;

import lombok.Data;

import java.io.Serializable;

@Data
public class ZzxqDto implements Serializable {
    private static final long serialVersionUID = 1L;
    private String  plateNumber; // 车牌号
    private String  createDate; // 创建时间
    private Integer licenceType; // 许可证类型
    private String  brandModel; // 车辆类型
    private String  vehicleModel; // 车辆品牌
    private String  violationTime; //  违规时间
    private String  renfaTime;
    private String  claimDate; // 索赔日期
    private String  reportDate; // 报告日期
    private Long  accidentStatus; // 事故状态
    private String  annualVeriTime; // 年度检验时间
    private String  annualVeriTimeEnd; // 年度检验时间
    private String  insuranceTime; // 保险时间
    private String  insuranceTimeEnd; // 保险时间结束
    private String  busiInsuranceTime; // 商务保险时间
    private String  busiInsuranceTimeEnd; // 商务保险时间结束
    private String  otherInsuranceTime; // 其他保险时间
    private String  otherInsuranceTimeEnd; // 其他保险时间结束
    private Integer nsDealNum; // 是否当天检测
    private Integer nsNotDealNum; // 是否当天检测
    private Integer syxDealNum; // 是否当天检测
    private Integer syxNotDealNum; // 是否当天检测
    private Integer jxxDealNum; // 是否当天检测
    private Integer jxxNotDealNum; // 是否当天检测
    private Integer wzDealNum; // 是否当天检测
    private Integer wzNotDealNum; // 是否当天检测
    private Integer sgDealNum; // 是否当天检测
    private Integer sgNotDealNum; // 是否当天检测
    private Integer otherDealNum; // 是否当天检测
    private Integer otherNotDealNum; // 是否当天检测


    public Integer getSyxNotDealNum() {
        if("".equals(syxNotDealNum)||syxNotDealNum==null){
            return 0;//去除该属性的前后空格并进行非空非null判断
        }
        return syxNotDealNum;
    }

    public Integer getSgDealNum() {
        if("".equals(sgDealNum)||sgDealNum==null){
            return 0;//去除该属性的前后空格并进行非空非null判断
        }
        return sgDealNum;
    }
    public Integer getJxxDealNum() {
        if("".equals(jxxDealNum)||jxxDealNum==null){
            return 0;//去除该属性的前后空格并进行非空非null判断
        }
        return jxxDealNum;
    }
}
