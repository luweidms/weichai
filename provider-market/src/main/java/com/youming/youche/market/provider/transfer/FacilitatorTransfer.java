package com.youming.youche.market.provider.transfer;

import com.youming.youche.commons.domain.SysStaticData;
import com.youming.youche.commons.util.RedisUtil;
import com.youming.youche.conts.EnumConsts;
import com.youming.youche.conts.SysStaticDataEnum;
import com.youming.youche.market.api.facilitator.IAccountBankUserTypeRelService;
import com.youming.youche.market.api.facilitator.ISysStaticDataMarketService;
import com.youming.youche.market.domain.facilitator.Facilitator;
import com.youming.youche.market.provider.transfer.base.BaseTransfer;
import com.youming.youche.market.vo.facilitator.FacilitatorVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.youming.youche.conts.EnumConsts.SysStaticData.SERVICE_BUSI_TYPE;
import static com.youming.youche.market.constant.facilitator.ServiceProductConstant.*;

@Component
@RequiredArgsConstructor
public class FacilitatorTransfer extends BaseTransfer<Facilitator,FacilitatorVo> {

    private final IAccountBankUserTypeRelService accountBankUserTypeRelService;
    private final ISysStaticDataMarketService sysStaticDataService;
    private final RedisUtil redisUtil;



    public  List<FacilitatorVo> setFacilitatorVo(List<Facilitator> list,String accessToken) {
        List<FacilitatorVo> facilitatorVos=null;
        if(list != null && list.size() > 0){
            facilitatorVos = super.toVO(list);
            Integer authFlag =null;
            for (int i = 0; i < facilitatorVos.size(); i++) {
                Facilitator facilitator = list.get(i);
                FacilitatorVo facilitatorVo = facilitatorVos.get(i);

                String serviceTypeName = getSysStaticData(SERVICE_BUSI_TYPE, facilitator.getServiceType().toString()).getCodeName();
                facilitatorVo.setServiceTypeName(serviceTypeName);
                String AuthStateName = getSysStaticData(CUSTOMER_AUTH_STATE, facilitator.getAuthState().toString()).getCodeName();
                facilitatorVo.setAuthStateName(AuthStateName);
                String stateName = getSysStaticData(SYS_STATE_DESC, facilitator.getState().toString()).getCodeName();
                facilitatorVo.setStateName(stateName);
                authFlag = facilitator.getAuthFlag();
                if (authFlag==SysStaticDataEnum.EXPENSE_STATE.AUDIT) {
                    facilitatorVo.setHasVer(0);
                } else {
                    facilitatorVo.setHasVer(1);
                }
                Boolean isBindCard = false;//0未绑定  1 已绑定
                Long serviceUserId = facilitator.getServiceUserId();
                String bindTypeName = "未绑定";
                if (serviceUserId != null && serviceUserId > 0) {
                    if (accountBankUserTypeRelService.isUserTypeBindCard(serviceUserId,EnumConsts.ACCOUNT_BANK_REL.BANK_TYPE_1,SysStaticDataEnum.USER_TYPE.SERVICE_USER)) {
                        bindTypeName = "已绑定";
                        isBindCard = true;
                    }
                }
                facilitatorVo.setIsBindCard(isBindCard);
                facilitatorVo.setBindTypeName(bindTypeName);
            }
        }
        return facilitatorVos;
    }
     public SysStaticData getSysStaticData(String codeType, String codeValue){
         List<SysStaticData> list = (List<SysStaticData>) redisUtil.get(SysStaticDataEnum.SYS_STATIC_DATA_NAME.concat(codeType));
         if (list != null && list.size() > 0) {
             for (SysStaticData sysStaticData : list) {
                 if (sysStaticData.getCodeValue().equals(codeValue)) {
                     return sysStaticData;
                 }
             }
         }
         return new SysStaticData();
     }

}
