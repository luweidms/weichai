package com.youming.youche.market.provider.transfer;

import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.conts.EnumConsts;
import com.youming.youche.conts.SysStaticDataEnum;
import com.youming.youche.market.api.facilitator.IAccountBankUserTypeRelService;
import com.youming.youche.market.provider.utis.ReadisUtil;
import com.youming.youche.market.vo.facilitator.ServiceInfoFleetVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.youming.youche.conts.EnumConsts.SysStaticData.CUSTOMER_AUTH_STATE;
import static com.youming.youche.conts.EnumConsts.SysStaticData.SERVICE_BUSI_TYPE;
import static com.youming.youche.conts.EnumConsts.SysStaticData.SYS_STATE_DESC;
import static com.youming.youche.conts.SysStaticDataEnum.INVITE_AUTH_STATE.PASS;
import static com.youming.youche.market.constant.facilitator.FacilitatorConstant.INVITE_AUTH_STATE;

@Component
@RequiredArgsConstructor
public class ServiceInfoFleetTransfer {

    private  final ReadisUtil readisUtil;

    private final IAccountBankUserTypeRelService accountBankUserTypeRelService;


    public List<ServiceInfoFleetVo> toServiceInfoFleetVo(List<Long> busiIdList,List<ServiceInfoFleetVo> infoFleetVoList, LoginInfo user){
        if(infoFleetVoList != null && infoFleetVoList.size() > 0 ){
            for (ServiceInfoFleetVo serviceInfoFleetVo : infoFleetVoList) {
                if(serviceInfoFleetVo.getAuthState() != null){
                    String authStateName = readisUtil.getSysStaticData(CUSTOMER_AUTH_STATE, serviceInfoFleetVo.getAuthState().toString()).getCodeName();
                    serviceInfoFleetVo.setAuthStateName(authStateName);
                }
                if(serviceInfoFleetVo.getState() != null){
                    String stateName = readisUtil.getSysStaticData("DATE_STS", serviceInfoFleetVo.getState().toString()).getCodeName();
                    serviceInfoFleetVo.setStateName(stateName);
                }
                if(serviceInfoFleetVo.getServiceType() != null){
                    String serviceTypeName = readisUtil.getSysStaticData(SERVICE_BUSI_TYPE, serviceInfoFleetVo.getServiceType().toString()).getCodeName();
                    serviceInfoFleetVo.setServiceTypeName(serviceTypeName);
                }
                if(serviceInfoFleetVo.getInvitationState() == null){
                    serviceInfoFleetVo.setInvitationState(PASS);
                }
                if(serviceInfoFleetVo.getInvitationState() != null){
                    String invitationStateName = readisUtil.getSysStaticData(INVITE_AUTH_STATE, serviceInfoFleetVo.getInvitationState().toString()).getCodeName();
                    serviceInfoFleetVo.setInvitationStateName(invitationStateName);
                }
                Boolean isBindCard = false;//0未绑定  1 已绑定
                String bindTypeName = "未绑定";
                Long serviceUserId = serviceInfoFleetVo.getServiceUserId();
                if (serviceInfoFleetVo.getServiceUserId() != null && serviceInfoFleetVo.getServiceUserId() > 0) {
                    if (accountBankUserTypeRelService.isUserTypeBindCard(serviceUserId,EnumConsts.ACCOUNT_BANK_REL.BANK_TYPE_0,SysStaticDataEnum.USER_TYPE.SERVICE_USER)
                            && accountBankUserTypeRelService.isUserTypeBindCard(serviceUserId, EnumConsts.ACCOUNT_BANK_REL.BANK_TYPE_1,SysStaticDataEnum.USER_TYPE.SERVICE_USER)) {
                        bindTypeName = "已绑定";
                        isBindCard = true;
                    }
                }
                serviceInfoFleetVo.setIsBindCard(isBindCard);
                serviceInfoFleetVo.setBindTypeName(bindTypeName);
                busiIdList.add(serviceInfoFleetVo.getRelId());
            }
            return infoFleetVoList;

        }
        return null;
    }
}
