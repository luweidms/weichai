package com.youming.youche.market.provider.transfer;

import com.youming.youche.conts.EnumConsts;
import com.youming.youche.market.dto.facilitator.ProductDetailDto;
import com.youming.youche.market.provider.utis.CommonUtil;
import com.youming.youche.market.provider.utis.ReadisUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static com.youming.youche.conts.EnumConsts.SysStaticData.*;
import static com.youming.youche.market.constant.facilitator.ServiceProductConstant.SYS_DISTRICT;

@Component
@RequiredArgsConstructor
public class ProductDetailTransfer {

    private  final ReadisUtil readisUtil;


    public ProductDetailDto toProductDetailDto(ProductDetailDto productDetailDto){
        if(productDetailDto.getIsBillAbility() != null){
            if(productDetailDto.getIsBillAbility() == 1){
                productDetailDto.setIsBillAbilityName("支持");
            }else if(productDetailDto.getIsBillAbility() == 2){
                productDetailDto.setIsBillAbilityName("不支持");
            }
        }

        if(productDetailDto.getFixedBalanceBill() !=  null && productDetailDto.getFixedBalanceBill() > 0){
            String doubleFormatLongMoney = CommonUtil.getDoubleFormatLongMoney(productDetailDto.getFixedBalanceBill(), 2, 1);
            productDetailDto.setFixedBalanceBillName(doubleFormatLongMoney);
        }else if(null != productDetailDto.getFixedBalanceBill() && productDetailDto.getFixedBalanceBill() == 0){
            productDetailDto.setFixedBalanceBillName(productDetailDto.getFixedBalanceBillName());
        }
        if(productDetailDto.getFixedBalance() !=  null && productDetailDto.getFixedBalance() > 0){
            String money = CommonUtil.getDoubleFormatLongMoney(productDetailDto.getFixedBalance(), 2, 1);
            productDetailDto.setFixedBalanceName(money);
        }
        if(productDetailDto.getProvinceId() !=null&& productDetailDto.getProvinceId()>=0){
            String codeName = readisUtil.getSysStaticData(SYS_PROVINCE, productDetailDto.getProvinceId() + "").getCodeName();
            productDetailDto.setProvinceName(codeName);
        }
        if(productDetailDto.getCityId()!=null && productDetailDto.getCityId()>=0){
            String codeName = readisUtil.getSysStaticData(SYS_CITY, productDetailDto.getCityId() + "").getCodeName();
            productDetailDto.setCityName(codeName);
        }
        if(productDetailDto.getCountyId()!=null && productDetailDto.getCountyId()>=0){
            String codeName = readisUtil.getSysStaticData(SYS_DISTRICT, productDetailDto.getCountyId() + "").getCodeName();
            productDetailDto.setCountyName(codeName);
        }
        if(productDetailDto.getAuthState() != null && productDetailDto.getAuthState() >= 0){
            String codeName = readisUtil.getSysStaticData(CUSTOMER_AUTH_STATE, productDetailDto.getAuthState() + "").getCodeName();
            productDetailDto.setAuthStateName(codeName);
        }
        if(productDetailDto.getState() != null && productDetailDto.getState() >= 0){
            String codeName = readisUtil.getSysStaticData(EnumConsts.SysStaticData.SYS_STATE_DESC, productDetailDto.getState() + "").getCodeName();
            productDetailDto.setStateName(codeName);
        }
        if(productDetailDto.getOilCardType() != null && productDetailDto.getOilCardType() >= 0){
            String codeName = readisUtil.getSysStaticData("PRODUCT_CARD_TYPE", productDetailDto.getOilCardType() + "").getCodeName();
            productDetailDto.setOilCardTypeName(codeName);
        }
        if(productDetailDto.getLocationType() != null && productDetailDto.getLocationType()  >= 0){
            String codeName = readisUtil.getSysStaticData("LOCATION_TYPE", productDetailDto.getLocationType() + "").getCodeName();
            productDetailDto.setLocationTypeName(codeName);
        }
        return productDetailDto;
    }

}
