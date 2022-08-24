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
                productInfo.append("价格:浮动 优惠"+floatBalanceBill+"%");
            }
            if(fixedBalanceBill > -1){
                productInfo.append("价格:固定 优惠"+CommonUtil.divide(fixedBalanceBill)+"元/升");
            }
            /*if(productInfo.length() > 0){
                productInfo.append(",");
            }*/
            /*if(StringUtils.isNotBlank(floatBalance)){
                productInfo.append("不开票价:浮动 优惠"+floatBalance+"%");
            }
            if(fixedBalance > -1){
                productInfo.append("不开票价:固定 优惠"+CommonUtil.divide(fixedBalance)+"元/升");
            }*/
        }else if(serviceType == OIL){

            //开票价
            Long oilPriceBill = this.calculationSharePrice(floatBalanceBill, fixedBalanceBill, originalPrice, serviceChargeBill);
            if(oilPriceBill > 0){
                productInfo.append("价格:"+CommonUtil.divide(oilPriceBill)+"元/升");
            }

            /*if(productInfo.length() > 0){
                productInfo.append(",");
            }*/

           /* //不开票价
            long oilPrice = serviceProductTF.calculationSharePrice(floatBalance, fixedBalance, originalPrice, serviceCharge);
            if(oilPrice > 0){
                productInfo.append("不开票价:"+CommonUtil.divide(oilPrice)+"元/升");
            }*/

        }else if(serviceType == ETC){

        }else{
            productInfo.append("无");
        }
        invitationInfoVo.setProductInfo(productInfo.toString());
       return   invitationInfoVo;
    }

    /**
     * 共享-计算费用
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
            //平台若按固定价计算，显示：平台设置单价 *（1+平台设置手续费比例）
            double oilPriceDouble = CommonUtil.getDoubleFormatLongMoney(fixedBalance, 2);
            double shareOilPrice = oilPriceDouble * (1D + serviceChargeDouble);
            oilPrice = CommonUtil.getLongByString(String.valueOf(shareOilPrice));
        } else if (org.apache.commons.lang.StringUtils.isNotBlank(floatBalance)) {
            double oilPriceDouble = (Double.valueOf(floatBalance) / 100) * (CommonUtil.getDoubleFormatLongMoney(originalPrice, 2));
            // 平台若按浮动价计算，显示：平台设置折扣 * 油站归属省份的全国油价 *（1+平台设置手续费比例）；
            double shareOilPrice = oilPriceDouble * (1D + serviceChargeDouble);
            oilPrice = CommonUtil.getLongByString(String.valueOf(shareOilPrice));
        }
        return oilPrice;
    }

}
