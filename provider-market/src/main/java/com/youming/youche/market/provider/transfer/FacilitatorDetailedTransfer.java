package com.youming.youche.market.provider.transfer;

import com.youming.youche.market.domain.facilitator.TenantServiceRelVer;
import com.youming.youche.market.provider.utis.ReadisUtil;
import com.youming.youche.market.vo.facilitator.ServiceInfoVo;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import static com.youming.youche.market.constant.facilitator.ServiceProductConstant.SYS_STATE_DESC;

@Component
public class FacilitatorDetailedTransfer  {

    @Resource
    private ReadisUtil readisUtil;



    public ServiceInfoVo getFacilitatorDetailedVo(ServiceInfoVo serviceInfoVo, TenantServiceRelVer serviceRelVer){
        if(serviceInfoVo!=null){

            if(serviceRelVer!=null){
                    if(serviceRelVer.getBalanceType() != null){
                        Integer balanceType = serviceRelVer.getBalanceType();
                        if(balanceType!=null&&balanceType==1) {
                            serviceRelVer.setBalanceTypeName("账期");
                        }else if(balanceType!=null&&balanceType==2) {
                            serviceRelVer.setBalanceTypeName("月结");
                        }else if(balanceType!=null&&balanceType==3) {
                            serviceRelVer.setBalanceTypeName("无账期");
                        }
                    }
                    if(serviceRelVer.getState() != null){
                        String stateName = readisUtil.getSysStaticData("DATE_STS", serviceRelVer.getState().toString()).getCodeName();
                        serviceRelVer.setStateName(stateName);
                    }
                serviceInfoVo.setTenantServiceRelVer(serviceRelVer);
                return serviceInfoVo;
            }
            return serviceInfoVo;

        }
      return null;
    }
}
