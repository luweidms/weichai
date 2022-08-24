package com.youming.youche.order.provider.transfer;


import com.youming.youche.conts.EnumConsts;
import com.youming.youche.order.domain.order.OrderAgingInfo;
import com.youming.youche.order.dto.OrderAgingInfoDto;
import com.youming.youche.order.dto.order.OrderAgingListOutDto;
import com.youming.youche.order.provider.utils.ReadisUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.net.PortUnreachableException;
import java.util.List;

import static com.youming.youche.conts.EnumConsts.SysStaticData.ORDER_STATE;
import static com.youming.youche.conts.EnumConsts.SysStaticDataAL.SYS_CITY;

@Component
@RequiredArgsConstructor
public class OrderAgingListOutDtoTransfer {
    private final ReadisUtil readisUtil;

    public List<OrderAgingListOutDto> toOrderAgingListOutDtoList(List<OrderAgingListOutDto> list){
        if(list != null && list.size() >0){
            for (OrderAgingListOutDto agingListOutDto : list) {
                if(agingListOutDto.getSourceRegion() != null){
                    String codeName = readisUtil.getSysStaticData(SYS_CITY, agingListOutDto.getSourceRegion().toString()).getCodeName();
                    agingListOutDto.setSourceRegionName(codeName);
                }
                if(agingListOutDto.getDesRegion() != null){
                    String codeName = readisUtil.getSysStaticData(SYS_CITY, agingListOutDto.getDesRegion().toString()).getCodeName();
                    agingListOutDto.setDesRegionName(codeName);
                }
                if(agingListOutDto.getAuditState() != null){
                    String codeName = readisUtil.getSysStaticData(EnumConsts.SysStaticData.PRO_STATE, agingListOutDto.getAuditState().toString()).getCodeName();
                    agingListOutDto.setAuditStateName(codeName);
                }
                if(agingListOutDto.getOrderState() != null){
                    String codeName = readisUtil.getSysStaticData(ORDER_STATE, agingListOutDto.getOrderState().toString()).getCodeName();
                    agingListOutDto.setOrderStateName(codeName);
                }
                if(agingListOutDto.getAgingSts() != null){
                    String codeName = readisUtil.getSysStaticData(EnumConsts.SysStaticData.PRO_STATE, agingListOutDto.getAgingSts().toString()).getCodeName();
                    agingListOutDto.setAgingStsName(codeName);
                }
                if(agingListOutDto.getAppealSts() != null){
                    String codeName = readisUtil.getSysStaticData(EnumConsts.SysStaticData.PRO_STATE, agingListOutDto.getAppealSts().toString()).getCodeName();
                    agingListOutDto.setAuditStateName(codeName);
                }else{
                    agingListOutDto.setAuditStateName("无");
                }
            }
            return list;
        }
        return list;
    }
    public OrderAgingInfo toOrderAging ( OrderAgingInfo orderAgingInfo){
        if(orderAgingInfo.getAgingSts() != null){
            String codeName = readisUtil.getSysStaticData(EnumConsts.SysStaticData.PRO_STATE, orderAgingInfo.getAgingSts().toString()).getCodeName();
            orderAgingInfo.setAgingStsName(codeName);
        }else {
            orderAgingInfo.setAuditStateName("无");
        }
        return  orderAgingInfo;
    }
}
