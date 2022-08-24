package com.youming.youche.market.provider.transfer;

import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.market.dto.facilitator.ServiceProductDto;
import com.youming.youche.market.provider.transfer.base.BaseTransfer;
import com.youming.youche.market.provider.utis.CommonUtil;
import com.youming.youche.market.provider.utis.ReadisUtil;
import com.youming.youche.market.vo.facilitator.ServiceProductVo;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.youming.youche.conts.SysStaticDataEnum.SERVICE_BUSI_TYPE.OIL;
import static com.youming.youche.market.constant.facilitator.ServiceProductConstant.CUSTOMER_AUTH_STATE;
import static com.youming.youche.market.constant.facilitator.ServiceProductConstant.SERVICE_AUTH_STATE;
import static com.youming.youche.market.constant.facilitator.ServiceProductConstant.SYS_CITY;
import static com.youming.youche.market.constant.facilitator.ServiceProductConstant.SYS_PROVINCE;
import static com.youming.youche.market.constant.facilitator.ServiceProductConstant.SYS_STATE_DESC;

@Component
@RequiredArgsConstructor
public class ServiceProductDtoTransfer extends BaseTransfer<ServiceProductDto, ServiceProductVo> {


    private final ReadisUtil readisUtil;

    /**
     * 转vo
     *
     * @param serviceProductDto
     * @return
     */
    public List<ServiceProductVo> getServiceProductVo(  List<Long> busiIdList,List<ServiceProductDto> serviceProductDto, LoginInfo user) {
        List<ServiceProductVo> serviceProductVos=null;
        try {
            if (serviceProductDto != null && serviceProductDto.size() > 0) {
                serviceProductVos = super.toVO(serviceProductDto);

                for (int i = 0; i < serviceProductVos.size(); i++) {
                    ServiceProductVo serviceProductVo = serviceProductVos.get(i);
                    ServiceProductDto productDto = serviceProductDto.get(i);
                    if(productDto.getAuthState()!=null){
                        String authState = readisUtil.getSysStaticData(CUSTOMER_AUTH_STATE, productDto.getAuthState().toString()).getCodeName();
                        serviceProductVo.setAuthState(authState);
                    }

                    if(productDto.getState() != null){
                        String stateName = readisUtil.getSysStaticData(SYS_STATE_DESC, productDto.getState().toString()).getCodeName();
                        serviceProductVo.setStateName(stateName);
                    }

                    if(productDto.getProvinceId() != null){
                        String provinceId = readisUtil.getSysStaticData(SYS_PROVINCE, productDto.getProvinceId().toString()).getCodeName();
                        serviceProductVo.setProvinceName(provinceId);
                    }

                    if(productDto.getCityId() != null){
                        String cityId = readisUtil.getSysStaticData(SYS_CITY, productDto.getCityId().toString()).getCodeName();
                        serviceProductVo.setCityName(cityId);
                    }

                    if(productDto.getServiceAuthState() != null){
                        String serviceAuthState = readisUtil.getSysStaticData(SERVICE_AUTH_STATE, productDto.getServiceAuthState().toString()).getCodeName();
                        serviceProductVo.setServiceAuthStateName(serviceAuthState);
                    }


                    busiIdList.add(productDto.getProductId());

                    Integer isBill = productDto.getIsBill();
                    if (isBill >= 0) {
                        if (isBill == 1) {
                            serviceProductVo.setIsBillName("是");
                        } else {
                            serviceProductVo.setIsBillName("否");
                        }
                    }

                    Integer isBillAbility = productDto.getIsBillAbility();
                    if (isBillAbility >= 0) {
                        if (isBillAbility == 1) {
                            serviceProductVo.setIsBillAbilityName("支持");
                        } else {
                            serviceProductVo.setIsBillAbilityName("不支持");
                        }
                    }
                    String floatBalanceBill = productDto.getFloatBalanceBill();
                    Long fixedBalanceBill = productDto.getFixedBalanceBill();
                    Long originalPrice = productDto.getOriginalPrice();
                    String serviceChargeBill = productDto.getServiceChargeBill();

                    Integer isShare = productDto.getIsShare();
                    Integer serviceType = productDto.getServiceType();

                    if (isShare == 1 && serviceType == OIL) {
                        if (isBillAbility == 1) {//需要开票
                            Long oilPriceBill = this.calculationSharePrice(floatBalanceBill,
                                    fixedBalanceBill, originalPrice, serviceChargeBill);
                       /* if (oilPriceBill > 0) {
                            map.put("oilPrice", CommonUtil.getDoubleFormatLongMoney(oilPriceBill, 2, 1));
                        }*/
                            serviceProductVo.setOilPriceBill(CommonUtil.getDoubleFormatLongMoney(oilPriceBill, 2, 1));
                            serviceProductVo.setOilPrice(CommonUtil.getDoubleFormatLongMoney(oilPriceBill, 2, 1));
                        }
                   /* long oilPrice = this.calculationSharePrice(floatBalance, fixedBalance, originalPrice, serviceCharge);
                    if (oilPrice > 0) {
                        map.put("oilPrice", CommonUtil.getDoubleFormatLongMoney(oilPrice, 2, 1));
                    }*/
                    }
                }
         }
        } catch(Exception e){
            e.printStackTrace();
        }
           return serviceProductVos;
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
        if (StringUtils.isNotBlank(serviceCharge)) {
            serviceChargeDouble = Double.valueOf(serviceCharge) / 100;
        }
        if (fixedBalance != null && fixedBalance > 0) {
            //平台若按固定价计算，显示：平台设置单价 *（1+平台设置手续费比例）
            double oilPriceDouble = CommonUtil.getDoubleFormatLongMoney(fixedBalance, 2);
            double shareOilPrice = oilPriceDouble * (1D + serviceChargeDouble);
            oilPrice = CommonUtil.getLongByString(String.valueOf(shareOilPrice));
        } else if (StringUtils.isNotBlank(floatBalance) && originalPrice != null) {
            double oilPriceDouble = (Double.valueOf(floatBalance) / 100) * (CommonUtil.getDoubleFormatLongMoney(originalPrice, 2));
            // 平台若按浮动价计算，显示：平台设置折扣 * 油站归属省份的全国油价 *（1+平台设置手续费比例）；
            double shareOilPrice = oilPriceDouble * (1D + serviceChargeDouble);
            oilPrice = CommonUtil.getLongByString(String.valueOf(shareOilPrice));
        }
        return oilPrice;
    }
}
