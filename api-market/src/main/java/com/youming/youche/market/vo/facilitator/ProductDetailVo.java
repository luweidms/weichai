package com.youming.youche.market.vo.facilitator;

import com.youming.youche.conts.EnumConsts;
import com.youming.youche.market.annotation.SysStaticDataInfoDict;
import com.youming.youche.system.domain.SysOperLog;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ProductDetailVo implements Serializable {
    private Long productId;
    private Long tenantId;
    private String productName;
    private String serviceCall;
    @SysStaticDataInfoDict(dictDataSource = EnumConsts.SysStaticDataAL.SYS_PROVINCE,dictText = "provinceName")
    private Integer provinceId;
    @SysStaticDataInfoDict(dictDataSource =EnumConsts.SysStaticDataAL.SYS_CITY,dictText = "cityName")
    private Integer cityId;
    @SysStaticDataInfoDict(dictDataSource =EnumConsts.SysStaticDataAL.SYS_DISTRICT,dictText = "countyName")
    private Integer countyId;

    private String mobilePhone;
    private String linkman;
    private Integer isBillAbility;
    private Integer isShare;
    private String floatBalance;
    private String floatBalanceBill;
    private Long fixedBalance;
    private Long fixedBalanceBill;
    private String serviceCharge;
    private String serviceChargeBill;
    private Long curOilPrice;
    private Long curOilPriceBill;
    @SysStaticDataInfoDict(dictDataSource = "CUSTOMER_AUTH_STATE")
    private Integer authState;
    @SysStaticDataInfoDict(dictDataSource = EnumConsts.SysStaticData.SYS_STATE_DESC)
    private Integer state;

    private Integer cooperationNum;
    private String address;
    private String nand;
    private String eand;
    private String introduce;
    @SysStaticDataInfoDict(dictDataSource = EnumConsts.SysStaticData.SYS_STATE_DESC )
    private Integer productState;

    @SysStaticDataInfoDict(dictDataSource = "PRODUCT_CARD_TYPE")
    private Integer oilCardType;

    @SysStaticDataInfoDict(dictDataSource = "LOCATION_TYPE")
    private Integer locationType ;
    private Long productPicId;



    private String productPicUrl;


    private List<SysOperLog> logs;
}
