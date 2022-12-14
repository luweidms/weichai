package com.youming.youche.market.provider.transfer;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.conts.SysStaticDataEnum;
import com.youming.youche.market.api.facilitator.*;
import com.youming.youche.market.domain.facilitator.ServiceInfo;
import com.youming.youche.market.domain.facilitator.ServiceProduct;
import com.youming.youche.market.domain.facilitator.TenantServiceRel;
import com.youming.youche.market.domain.facilitator.UserDataInfo;
import com.youming.youche.market.dto.facilitator.ServiceInfoDto;
import com.youming.youche.market.dto.facilitator.ServiceProductDto;
import com.youming.youche.market.provider.mapper.facilitator.ServiceInfoMapper;
import com.youming.youche.market.provider.mapper.facilitator.ServiceProductMapper;
import com.youming.youche.market.provider.utis.CommonUtil;
import com.youming.youche.market.provider.utis.ReadisUtil;
import com.youming.youche.market.vo.facilitator.ServiceInfoVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

import static com.youming.youche.conts.SysStaticDataEnum.SERVICE_BUSI_TYPE.OIL;
import static com.youming.youche.conts.SysStaticDataEnum.SERVICE_BUSI_TYPE.OIL_CARD;
import static com.youming.youche.market.constant.facilitator.ServiceProductConstant.*;

@Component
public class ServiceInfoTransfer {
    @Resource
    private ServiceInfoMapper serviceInfoMapper;
    @Lazy
    @Autowired
    private IServiceProductService serviceProductService;
    @Autowired
    private IUserDataInfoMarketService userDataInfoService;
    @Lazy
    @Autowired
    private IServiceInfoService serviceInfoService;
    @Autowired
    private ITenantServiceRelService tenantServiceRelService;
    @Autowired
    private IOilPriceProvinceService oilPriceProvinceService;
    @Autowired
    private ISysStaticDataMarketService sysStaticDataService;
    @Resource
    private ReadisUtil readisUtil;
    @Resource
    private ServiceProductMapper serviceProductMapper;


    public ServiceInfoVo acquireServiceInfoVo(Long serviceUserId){
        UserDataInfo userDataInfo = userDataInfoService.get(serviceUserId);
        if(userDataInfo==null){
            throw new BusinessException("?????????????????????");
        }
        LambdaQueryWrapper<ServiceInfo> lambdaServiceInfo = new QueryWrapper<ServiceInfo>().lambda();
        lambdaServiceInfo.eq(ServiceInfo::getServiceUserId,serviceUserId);

        ServiceInfo serviceInfo = serviceInfoMapper.selectOne(lambdaServiceInfo);
        ServiceInfoVo serviceInfoOut = new ServiceInfoVo();
        if(serviceInfo==null){
            LambdaQueryWrapper<ServiceProduct> lambdaServiceProduct = new QueryWrapper<ServiceProduct>().lambda();
            lambdaServiceProduct.eq(ServiceProduct::getChildAccountUserId,userDataInfo.getId());
            List<ServiceProduct> serviceProductByChild = serviceProductService.list(lambdaServiceProduct);
            if(serviceProductByChild==null||serviceProductByChild.size()==0) {
                throw new BusinessException("???????????????????????????");
            }
            LambdaQueryWrapper<ServiceInfo> lambdaServiceInfodata = new QueryWrapper<ServiceInfo>().lambda();
            lambdaServiceInfodata.eq(ServiceInfo::getServiceUserId,serviceProductByChild.get(0).getServiceUserId());
            serviceInfo=serviceInfoMapper.selectOne(lambdaServiceInfodata);

            serviceInfoOut.setLoginAcct(userDataInfo.getMobilePhone());
            serviceInfoOut.setServiceType(serviceProductByChild.get(0).getBusinessType());
            serviceInfoOut.setServiceTypeName(serviceInfoOut.getServiceTypeName()+"?????????");
            serviceInfoOut.setServiceName(serviceInfo.getServiceName());
            serviceInfoOut.setIsBill(serviceInfo.getIsBillAbility());
        }else{
            TenantServiceRel serviceRel = tenantServiceRelService.getTenantServiceRel(serviceInfo.getTenantId(), serviceInfo.getServiceUserId());
            BeanUtils.copyProperties(userDataInfo,serviceInfoOut);
            serviceInfoOut.setLoginAcct(userDataInfo.getMobilePhone());
            BeanUtils.copyProperties(serviceInfo,serviceInfoOut);
            if(serviceRel == null){
                serviceInfoOut.setAuthState(serviceInfo.getIsAuth());
            }else{
                serviceInfoOut.setAuthState(serviceRel.getAuthState());
            }
            //????????????????????????
            serviceInfoOut.setUserPriceUrl(serviceInfoOut.getUserPriceUrl());
            serviceInfoOut.setIdenPictureBackUrl(serviceInfoOut.getIdenPictureBackUrl());
            serviceInfoOut.setIdenPictureFrontUrl(serviceInfoOut.getIdenPictureFrontUrl());
            serviceInfoOut.setIsBill(serviceInfo.getIsBillAbility());
        }
        if(serviceInfoOut != null){
            if(serviceInfoOut.getIsBill() != null && serviceInfoOut.getIsBill() >= 0){
                if(serviceInfoOut.getIsBill() == 1){
                    serviceInfoOut.setIsBillName("???");
                }else {
                    serviceInfoOut.setIsBillName("???");
                }
            }
            if(serviceInfoOut.getIsBillAbility() !=null &&  serviceInfoOut.getIsBillAbility()>0){
                if(1==serviceInfoOut.getIsBillAbility()){
                    serviceInfoOut.setIsBillAbilityName("???");
                }else {
                    serviceInfoOut.setIsBillAbilityName("???");
                }
            }
            if(serviceInfoOut.getBalanceType()!=null && serviceInfoOut.getBalanceType()==1) {
                serviceInfoOut.setBalanceTypeName("??????");
            }else if(serviceInfoOut.getBalanceType()!=null&&serviceInfoOut.getBalanceType()==2) {
                serviceInfoOut.setBalanceTypeName("??????");
            }else if(serviceInfoOut.getBalanceType()!=null&&serviceInfoOut.getBalanceType()==3) {
                serviceInfoOut.setBalanceTypeName("?????????");
            }
            if(null != serviceInfoOut.getQuotaAmt()){
                serviceInfoOut.setQuotaAmtStr(CommonUtil.divide(serviceInfoOut.getQuotaAmt()));
            }
            if (null != serviceInfoOut.getUseQuotaAmt()){
                serviceInfoOut.setUseQuotaAmtStr(CommonUtil.divide(serviceInfoOut.getUseQuotaAmt()));
            }else {
                serviceInfoOut.setUseQuotaAmtStr("0.00");
            }
        }
        return serviceInfoOut;
    }
    public ServiceInfoDto getServiceInfoDto(UserDataInfo userDataInfo, String loginAcct, LoginInfo user) throws Exception {
        ServiceInfoDto serviceInfoDto = new ServiceInfoDto();
        if(userDataInfo!=null){
            // ???????????????
            serviceInfoDto.setServiceUserId(userDataInfo.getId());
            serviceInfoDto.setLoginAcct(loginAcct);
            serviceInfoDto.setLinkman(userDataInfo.getLinkman());
            serviceInfoDto.setUserPriceUrl(userDataInfo.getUserPriceUrl());
            serviceInfoDto.setUserPrice(userDataInfo.getUserPrice());
            serviceInfoDto.setIdentification(userDataInfo.getIdentification());
            serviceInfoDto.setIdenPictureBackUrl(userDataInfo.getIdenPictureBackUrl());
            serviceInfoDto.setIdenPictureBack(userDataInfo.getIdenPictureBack());
            serviceInfoDto.setIdenPictureFrontUrl(userDataInfo.getIdenPictureFrontUrl());
            serviceInfoDto.setIdenPictureFront(userDataInfo.getIdenPictureFront());
            //??????????????????
            ServiceInfo serviceInfo = serviceInfoService.getServiceInfoByServiceUserId(userDataInfo.getId());
            Long serviceId = null;
            //??????????????????
            if(serviceInfo != null){
                serviceInfoDto.setServiceName(serviceInfo.getServiceName());
                serviceInfoDto.setServiceType(serviceInfo.getServiceType());
                serviceInfoDto.setCompanyAddress(serviceInfo.getCompanyAddress());
                //?????????????????????
                TenantServiceRel tenantServiceRel = tenantServiceRelService.getTenantServiceRel(user.getTenantId(), userDataInfo.getId());
                if(tenantServiceRel != null){
                    serviceInfoDto.setIsBill(tenantServiceRel.getIsBill());
                    serviceInfoDto.setPaymentDays(tenantServiceRel.getPaymentDays());
                    serviceInfoDto.setBalanceType(tenantServiceRel.getBalanceType());
                    serviceInfoDto.setPaymentMonth(tenantServiceRel.getPaymentMonth());
                    serviceInfoDto.setQuotaAmtStr(CommonUtil.divide(tenantServiceRel.getQuotaAmt()==null?-1:tenantServiceRel.getQuotaAmt()));
                }
                serviceInfoDto.setInfo(2);
                serviceInfoDto.setIsBillAbility(serviceInfo.getIsBillAbility());
                serviceId = serviceInfo.getServiceUserId();
            }else {//????????????????????????
                serviceInfoDto.setInfo(1);
                serviceInfoDto.setServiceUserId(null);
            }
            if(serviceInfo != null){
                List<ServiceProductDto> list = null;
                if(Objects.equals(user.getTenantId(),serviceInfo.getTenantId())){
                    list =  serviceProductService.getServiceProductApply(serviceId, serviceInfo.getServiceType(),user);
                }else{
                    list = serviceProductMapper.getServiceProductApply(serviceId, serviceInfo.getServiceType(), SysStaticDataEnum.INVITE_AUTH_STATE.WAIT, SysStaticDataEnum.SYS_STATE_DESC.STATE_YES, null);
                }
                if(list != null && list.size() > 0){
                    for (ServiceProductDto serviceProductDto : list) {
                        getProductInfo(serviceProductDto,serviceInfo.getServiceType());
                        if(serviceProductDto.getProvinceId() != null){
                            String provinceName = readisUtil.getSysStaticData(SYS_PROVINCE,serviceProductDto.getProvinceId().toString()).getCodeName();
                            serviceProductDto.setProvinceName(provinceName);
                        }
                        if( serviceProductDto.getCityId() != null){
                            String cityName = readisUtil.getSysStaticData(SYS_CITY, serviceProductDto.getCityId().toString()).getCodeName();
                            serviceProductDto.setCityName(cityName);
                        }
                        if(serviceProductDto.getCountyId() != null){
                            String countyName = readisUtil.getSysStaticData(SYS_DISTRICT, serviceProductDto.getCountyId().toString()).getCodeName();
                            serviceProductDto.setCountyName(countyName);
                        }
                    }
                }
                serviceInfoDto.setApplyMap(list);
            }else {
                serviceInfoDto.setApplyMap(null);
            }
        }else {
            serviceInfoDto.setInfo(0);
        }
        return serviceInfoDto;
    }
    public void getProductInfo(ServiceProductDto inMap, Integer serviceType) {

        String floatBalanceBill = inMap.getFloatBalanceBill();
        Long fixedBalanceBill = inMap.getFixedBalanceBill();
        String serviceChargeBill = inMap.getServiceChargeBill();

        Long originalPrice = inMap.getOilPrice();
        StringBuilder productInfo = new StringBuilder();
        if (serviceType == OIL_CARD) {
            if (org.apache.commons.lang3.StringUtils.isNotBlank(floatBalanceBill)) {
                productInfo.append("??????:?????? ??????" + floatBalanceBill + "%");
            }
            if (fixedBalanceBill > -1) {
                productInfo.append("??????:?????? ??????" + CommonUtil.divide(fixedBalanceBill) + "???/???");
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
        } else if (serviceType == OIL) {
            //?????????
            Long oilPriceBill = oilPriceProvinceService.calculationSharePrice(floatBalanceBill, fixedBalanceBill, originalPrice, serviceChargeBill);
            if (oilPriceBill > 0) {
                productInfo.append("??????:" + CommonUtil.divide(oilPriceBill) + "???/???");
            }

            /*if(productInfo.length() > 0){
                productInfo.append(",");
            }*/

           /* //????????????
            long oilPrice = serviceProductTF.calculationSharePrice(floatBalance, fixedBalance, originalPrice, serviceCharge);
            if(oilPrice > 0){
                productInfo.append("????????????:"+CommonUtil.divide(oilPrice)+"???/???");
            }*/

        }
        inMap.setProductInfo(productInfo.toString());
    }
}
