package com.youming.youche.market.provider.transfer;

import com.youming.youche.conts.EnumConsts;
import com.youming.youche.conts.SysStaticDataEnum;
import com.youming.youche.market.dto.facilitator.CooperationNumDto;
import com.youming.youche.market.provider.mapper.facilitator.ServiceProductMapper;
import com.youming.youche.market.provider.utis.ReadisUtil;
import com.youming.youche.market.vo.facilitator.ServiceProductInfoVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ServiceProductInfoVoTransfer {
    @Resource
    private ServiceProductMapper serviceProductMapper;

    private final ReadisUtil readisUtil;

    public List<ServiceProductInfoVo>  getServiceProductVo(List<ServiceProductInfoVo> list){
        if(list != null && list.size() > 0){
            List<CooperationNumDto> cooperationNumDtos = new ArrayList<>();
            for (ServiceProductInfoVo serviceProductInfoVo : list) {
                if(serviceProductInfoVo.getIsShare() == SysStaticDataEnum.IS_SHARE.YES){
                    serviceProductInfoVo.setIsShareName("共享");
                }else{
                    serviceProductInfoVo.setIsShareName("不共享");
                }
                if(serviceProductInfoVo.getServiceAuthState()!=null){
                    if(serviceProductInfoVo.getServiceAuthState()== SysStaticDataEnum.CUSTOMER_AUTH_STATE.WAIT_AUTH){
                        serviceProductInfoVo.setServiceAuthStateName("待审核");
                    }else if(serviceProductInfoVo.getServiceAuthState()== SysStaticDataEnum.CUSTOMER_AUTH_STATE.AUTH_PASS){
                        serviceProductInfoVo.setServiceAuthStateName("审核通过");
                    }else{
                        serviceProductInfoVo.setServiceAuthStateName("驳回");
                    }
                }
                if(serviceProductInfoVo.getServiceType() > 0){
                    String serviceTypeName = readisUtil.getSysStaticData(EnumConsts.SysStaticData.SERVICE_BUSI_TYPE, serviceProductInfoVo.getServiceType().toString()).getCodeName();
                    serviceProductInfoVo.setServiceTypeName(serviceTypeName);
                }
                if(serviceProductInfoVo.getState() >= 0){
                    String stateName = readisUtil.getSysStaticData(EnumConsts.SysStaticData.SYS_STATE_DESC, serviceProductInfoVo.getState().toString()).getCodeName();
                    serviceProductInfoVo.setStateName(stateName);
                }
                if(serviceProductInfoVo.getAuthState() != null){
                    String authStateName = readisUtil.getSysStaticData(EnumConsts.SysStaticData.CUSTOMER_AUTH_STATE, serviceProductInfoVo.getAuthState().toString()).getCodeName();
                    serviceProductInfoVo.setAuthStateName(authStateName);
                }
                CooperationNumDto cooperationNum = serviceProductMapper.countCooperationNum(serviceProductInfoVo, SysStaticDataEnum.PT_TENANT_ID, SysStaticDataEnum.AUTH_STATE.AUTH_STATE2);
                if(cooperationNum!=null){
                    cooperationNumDtos.add(cooperationNum);
                }else{
                    serviceProductInfoVo.setProductNum(0);
                }

                if(cooperationNumDtos != null && cooperationNumDtos.size()>0) {
                    for (CooperationNumDto numDto : cooperationNumDtos) {
                        if (numDto != null) {
                            Long productId = numDto.getProductId();
                            if (serviceProductInfoVo.getProductId().equals(productId)) {
                                Integer cooperationNums = numDto.getCooperationNum();
                                if (cooperationNums >= 0) {
                                    serviceProductInfoVo.setProductNum(cooperationNums);
                                } else {
                                    serviceProductInfoVo.setProductNum(0);
                                }
                            }
                        }
                    }
                }
            }

//           if(cooperationNumDtos != null && cooperationNumDtos.size()>0) {
//               for (CooperationNumDto numDto : cooperationNumDtos) {
//                   if(numDto != null){
//                       Long productId = numDto.getProductId();
//                       for (ServiceProductInfoVo infoVo : list) {
//                           if (infoVo.getProductId() == productId) {
//                               Integer cooperationNum = numDto.getCooperationNum();
//                               if (cooperationNum >= 0) {
//                                   infoVo.setProductNum(cooperationNum);
//                               }
//                           }
//                       }
//                   }
//               }
//           }
        }
        return  list;
    }
}
