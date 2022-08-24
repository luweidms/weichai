package com.youming.youche.market.domain.facilitator;

import lombok.Data;

import java.io.Serializable;

@Data
public class TenantProductOut implements Serializable {
    private Long productId; // 产品id
    private String productName; // 产品名臣
    private String serviceCall; // 服务电话
    private Integer provinceId; // 省id
    private String provinceName; // 省名称
    private Integer countyId; // 区县id
    private String countyName; // 区县名称
    private Integer cityId; // 市id
    private String cityName; // 市名称
    private String address; // 地址
    private String introduce; // 描述
    private Integer state; // 状态
    private String stateName; // 状态名称
    private String floatBalance; // 余额
    private Long fixedBalance; // 固定余额
    private String floatBalanceBill; // 余额账单
    private Long fixedBalanceBill; // 固定余额账单
    private String nand; // 经度
    private String eand; // 维度
    private Long relId; //关系主键id
    private String fixedBalanceName; // 固定余额名称
    private String fixedBalanceBillName; // 固定余额账单名称

    private Integer isBillAbility;  // 是否账单
    private String isBillAbilityName;

    private String oilPrice; // 油价格
    private String oilPriceBill;  // 油价格账单

    private Integer cooperationNum;//合作车队数量

    private String loginAcct; // 账号
    private String serviceName; // 服务商名称
    private String linkman; // 名称
    private Long serviceUserId; // 服务商用户id
    private Integer serviceType; // 类型
    private Integer isShare; // 是否共享

    private String serviceCharge; // 服务费
    private String serviceChargeBill; // 服务账单

    private Integer oilCardType; // 油卡类型
    private String oilCardTypeName;

    private Integer localeBalanceState;

    private Integer isAuth; // 审核

    private Integer locationType; // 位置类型
    private Long productPicId; // 产品图片 ID

    private String locationTypeName;

    public String getServiceCharge() {
        return serviceCharge;
    }

    public void setServiceCharge(String serviceCharge) {
        this.serviceCharge = serviceCharge;
    }

    public String getServiceChargeBill() {
        return serviceChargeBill;
    }

    public void setServiceChargeBill(String serviceChargeBill) {
        this.serviceChargeBill = serviceChargeBill;
    }

    public Integer getIsShare() {
        return isShare;
    }

    public void setIsShare(Integer isShare) {
        this.isShare = isShare;
    }

    public Integer getServiceType() {
        return serviceType;
    }

    public void setServiceType(Integer serviceType) {
        this.serviceType = serviceType;
    }

    public String getLoginAcct() {
        return loginAcct;
    }

    public void setLoginAcct(String loginAcct) {
        this.loginAcct = loginAcct;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getLinkman() {
        return linkman;
    }

    public void setLinkman(String linkman) {
        this.linkman = linkman;
    }

    public Long getServiceUserId() {
        return serviceUserId;
    }

    public void setServiceUserId(Long serviceUserId) {
        this.serviceUserId = serviceUserId;
    }

    public Integer getCooperationNum() {
        return cooperationNum;
    }

    public void setCooperationNum(Integer cooperationNum) {
        this.cooperationNum = cooperationNum;
    }

    public String getOilPrice() {
        return oilPrice;
    }

    public void setOilPrice(String oilPrice) {
        this.oilPrice = oilPrice;
    }

    public String getOilPriceBill() {
        return oilPriceBill;
    }

    public void setOilPriceBill(String oilPriceBill) {
        this.oilPriceBill = oilPriceBill;
    }

    public Integer getIsBillAbility() {
        return isBillAbility;
    }

    public void setIsBillAbility(Integer isBillAbility) {
        this.isBillAbility = isBillAbility;
    }

    public String getIsBillAbilityName() {
        if(isBillAbility != null){
            if(isBillAbility == 1){
                return "支持";
            }else if(isBillAbility == 2){
                return "不支持";
            }
        }
        return isBillAbilityName;
    }

    public void setIsBillAbilityName(String isBillAbilityName) {
        this.isBillAbilityName = isBillAbilityName;
    }

    public String getFloatBalanceBill() {
        return floatBalanceBill;
    }

    public void setFloatBalanceBill(String floatBalanceBill) {
        this.floatBalanceBill = floatBalanceBill;
    }

    public Long getFixedBalanceBill() {
        return fixedBalanceBill;
    }

    public void setFixedBalanceBill(Long fixedBalanceBill) {
        this.fixedBalanceBill = fixedBalanceBill;
    }

//    public String getFixedBalanceBillName() {
//        if(fixedBalanceBill != null && fixedBalanceBill > -1){
//            return CommonUtil.getDoubleFormatLongMoney(fixedBalanceBill,2,1);
//        }
//        return fixedBalanceBillName;
//    }

    public void setFixedBalanceBillName(String fixedBalanceBillName) {
        this.fixedBalanceBillName = fixedBalanceBillName;
    }

//    public String getFixedBalanceName() throws Exception{
//        if(fixedBalance != null && fixedBalance > -1){
//            return CommonUtil.getDoubleFormatLongMoney(fixedBalance,2,1);
//        }
//        return fixedBalanceName;
//    }

    public void setFixedBalanceName(String fixedBalanceName) {
        this.fixedBalanceName = fixedBalanceName;
    }

//    public String getStateName() {
//        if(state != null && state >= 0){
//            return SysStaticDataUtil.getSysStaticDataCodeName("SYS_STATE_DESC",String.valueOf(state));
//        }
//        return stateName;
//    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    public Long getRelId() {
        return relId;
    }

    public void setRelId(Long relId) {
        this.relId = relId;
    }

//    public String getProvinceName() {
//        if(provinceId != null && provinceId > 0){
//            return SysStaticDataUtil.getProvinceDataList("SYS_PROVINCE",String.valueOf(provinceId)).getName();
//        }
//        return provinceName;
//    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

//    public String getCountyName() {
//        if(countyId != null && countyId > 0){
//            return SysStaticDataUtil.getDistrictDataList("SYS_DISTRICT",String.valueOf(countyId)).getName();
//        }
//        return countyName;
//    }

    public void setCountyName(String countyName) {
        this.countyName = countyName;
    }

//    public String getCityName() {
//        if(cityId != null && cityId > 0){
//            return SysStaticDataUtil.getCityDataList("SYS_CITY",String.valueOf(cityId)).getName();
//        }
//        return cityName;
//    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getNand() {
        return nand;
    }

    public void setNand(String nand) {
        this.nand = nand;
    }

    public String getEand() {
        return eand;
    }

    public void setEand(String eand) {
        this.eand = eand;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getServiceCall() {
        return serviceCall;
    }

    public void setServiceCall(String serviceCall) {
        this.serviceCall = serviceCall;
    }

    public Integer getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(Integer provinceId) {
        this.provinceId = provinceId;
    }

    public Integer getCountyId() {
        return countyId;
    }

    public void setCountyId(Integer countyId) {
        this.countyId = countyId;
    }

    public Integer getCityId() {
        return cityId;
    }

    public void setCityId(Integer cityId) {
        this.cityId = cityId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getIntroduce() {
        return introduce;
    }

    public void setIntroduce(String introduce) {
        this.introduce = introduce;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public String getFloatBalance() {
        return floatBalance;
    }

    public void setFloatBalance(String floatBalance) {
        this.floatBalance = floatBalance;
    }

    public Long getFixedBalance() {
        return fixedBalance;
    }

    public void setFixedBalance(Long fixedBalance) {
        this.fixedBalance = fixedBalance;
    }

    public Integer getOilCardType() {
        return oilCardType;
    }

    public void setOilCardType(Integer oilCardType) {
        this.oilCardType = oilCardType;
    }

//    public String getOilCardTypeName() {
//        if(oilCardType != null && oilCardType >= 0){
//            return SysStaticDataUtil.getSysStaticDataCodeName("PRODUCT_CARD_TYPE",String.valueOf(oilCardType));
//        }
//        return oilCardTypeName;
//    }

    public void setOilCardTypeName(String oilCardTypeName) {
        this.oilCardTypeName = oilCardTypeName;
    }

    public Integer getLocaleBalanceState() {
        return localeBalanceState;
    }

    public void setLocaleBalanceState(Integer localeBalanceState) {
        this.localeBalanceState = localeBalanceState;
    }

    public Integer getIsAuth() {
        return isAuth;
    }

    public void setIsAuth(Integer isAuth) {
        this.isAuth = isAuth;
    }

    public Integer getLocationType() {
        return locationType;
    }

    public void setLocationType(Integer locationType) {
        this.locationType = locationType;
    }

    public Long getProductPicId() {
        return productPicId;
    }

    public void setProductPicId(Long productPicId) {
        this.productPicId = productPicId;
    }

//    public String getLocationTypeName() {
//        if(locationType != null && locationType >= 0){
//            return SysStaticDataUtil.getSysStaticDataCodeName("LOCATION_TYPE",String.valueOf(locationType));
//        }
//        return locationTypeName;
//    }

    public void setLocationTypeName(String locationTypeName) {
        this.locationTypeName = locationTypeName;
    }
}
