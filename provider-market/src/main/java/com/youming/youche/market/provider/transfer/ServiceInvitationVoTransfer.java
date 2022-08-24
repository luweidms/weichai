package com.youming.youche.market.provider.transfer;

import com.youming.youche.market.provider.utis.ReadisUtil;
import com.youming.youche.market.vo.facilitator.ServiceInvitationVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.youming.youche.market.constant.facilitator.FacilitatorConstant.INVITE_AUTH_STATE;
import static com.youming.youche.market.constant.facilitator.ServiceProductConstant.SYS_SERVICE_BUSI_TYPE;

@Component
@RequiredArgsConstructor
public class ServiceInvitationVoTransfer {

    private final ReadisUtil readisUtil;

    public List<ServiceInvitationVo> toServiceInvitationVo(List<ServiceInvitationVo> serviceInvitationVos) {
        if (serviceInvitationVos != null && serviceInvitationVos.size() > 0) {

            for (ServiceInvitationVo invitationVo : serviceInvitationVos) {
                if (invitationVo.getServiceType() != null) {
                    String serviceTypeName = readisUtil.getSysStaticData(SYS_SERVICE_BUSI_TYPE, invitationVo.getServiceType().toString()).getCodeName();
                    invitationVo.setServiceTypeName(serviceTypeName);
                }
                if(invitationVo.getAuthState() != null){
                    String authStateName = readisUtil.getSysStaticData(INVITE_AUTH_STATE, invitationVo.getAuthState().toString()).getCodeName();
                    invitationVo.setAuthStateName(authStateName);
                }
            }
            return serviceInvitationVos;
        }
        return null;
    }
}
