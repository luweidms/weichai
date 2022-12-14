package com.youming.youche.market.provider.transfer;

import com.youming.youche.commons.util.DataFormat;
import com.youming.youche.market.api.facilitator.IServiceProductService;
import com.youming.youche.market.dto.facilitator.CooperationDto;
import com.youming.youche.market.dto.facilitator.CooperationTenantDto;
import com.youming.youche.market.provider.utis.CommonUtil;
import com.youming.youche.market.vo.facilitator.InvitationInfoVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.youming.youche.conts.SysStaticDataEnum.SERVICE_BUSI_TYPE.*;

@Component
public class CooperationTenantTransfer {



    public List<CooperationTenantDto> toCooperationTenantDto(List<CooperationTenantDto> list){
        for (CooperationTenantDto tenantDto : list) {
            long longKey = DataFormat.getLongKey(tenantDto.getFixedBalanceBill());
            if(longKey > 0){
                tenantDto.setFixedBalanceBill(CommonUtil.divide(longKey));
            }else{
                tenantDto.setFixedBalanceBill("");
            }
            long fixedBalance = DataFormat.getLongKey(tenantDto.getFixedBalance());
            if(fixedBalance > 0){
                tenantDto.setFixedBalance(fixedBalance);
            }else{
                tenantDto.setFixedBalance(null);
            }
            tenantDto.setIsBillAbility(tenantDto.getIsBillAbility());
        }
        return list;
    }

    public InvitationInfoVo getProductInfo(InvitationInfoVo invitationInfoVo, CooperationDto cooperationDto, Integer serviceType){
        String floatBalanceBill = cooperationDto.getFloatBalanceBill();
        Long fixedBalanceBill = cooperationDto.getFixedBalanceBill();
        String serviceChargeBill = cooperationDto.getServiceChargeBill();

        Long originalPrice = cooperationDto.getOilPrice();

        StringBuilder productInfo = new StringBuilder();
        if(serviceType == OIL_CARD){
            if(StringUtils.isNotBlank(floatBalanceBill)){
                productInfo.append("??????:?????? ??????"+floatBalanceBill+"%");
            }
            if(fixedBalanceBill > -1){
                productInfo.append("??????:?????? ??????"+CommonUtil.divide(fixedBalanceBill)+"???/???");
            }
            /*if(productInfo.length() > 0){
                productInfo.append(",");
            }*/
            /*if(StringUtils.isNotBlank(floatBalance)){
                productInfo.append("????????????:?????? ??????"+floatBalance+"%");
            }
            if(fixedBalance > -1){
                productInfo.append("????????????:?????? ??????"+CommonUtil.divide(fixedBalance)+"???/???");
            }*/
        }else if(serviceType == OIL){

            //?????????
            Long oilPriceBill = this.calculationSharePrice(floatBalanceBill, fixedBalanceBill, originalPrice, serviceChargeBill);
            if(oilPriceBill > 0){
                productInfo.append("??????:"+CommonUtil.divide(oilPriceBill)+"???/???");
            }

            /*if(productInfo.length() > 0){
                productInfo.append(",");
            }*/

           /* //????????????
            long oilPrice = serviceProductTF.calculationSharePrice(floatBalance, fixedBalance, originalPrice, serviceCharge);
            if(oilPrice > 0){
                productInfo.append("????????????:"+CommonUtil.divide(oilPrice)+"???/???");
            }*/

        }else if(serviceType == ETC){

        }else{
            productInfo.append("???");
        }
        invitationInfoVo.setProductInfo(productInfo.toString());
       return   invitationInfoVo;
    }

    /**
     * ??????-????????????
     *
     * @param floatBalance
     * @param fixedBalance
     * @param originalPrice
     * @return
     */
    public long calculationSharePrice(String floatBalance, Long fixedBalance, Long originalPrice, String serviceCharge) {
        long oilPrice = 0;
        Double serviceChargeDouble = 0D;
        if (org.apache.commons.lang.StringUtils.isNotBlank(serviceCharge)) {
            serviceChargeDouble = Double.valueOf(serviceCharge) / 100;
        }
        if (fixedBalance != null && fixedBalance > 0) {
            //????????????????????????????????????????????????????????? *???1+??????????????????????????????
            double oilPriceDouble = CommonUtil.getDoubleFormatLongMoney(fixedBalance, 2);
            double shareOilPrice = oilPriceDouble * (1D + serviceChargeDouble);
            oilPrice = CommonUtil.getLongByString(String.valueOf(shareOilPrice));
        } else if (org.apache.commons.lang.StringUtils.isNotBlank(floatBalance)) {
            double oilPriceDouble = (Double.valueOf(floatBalance) / 100) * (CommonUtil.getDoubleFormatLongMoney(originalPrice, 2));
            // ????????????????????????????????????????????????????????? * ????????????????????????????????? *???1+?????????????????????????????????
            double shareOilPrice = oilPriceDouble * (1D + serviceChargeDouble);
            oilPrice = CommonUtil.getLongByString(String.valueOf(shareOilPrice));
        }
        return oilPrice;
    }

}
