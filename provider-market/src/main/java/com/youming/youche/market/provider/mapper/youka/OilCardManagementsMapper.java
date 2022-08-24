package com.youming.youche.market.provider.mapper.youka;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.market.domain.youka.OilCardManagement;
import com.youming.youche.market.domain.youka.OilCardVehicleRel;
import com.youming.youche.market.domain.youka.ServiceInfo;
import com.youming.youche.market.dto.youca.OilCardManagementDto;
import com.youming.youche.market.dto.youca.OilCardManagementOutDto;
import com.youming.youche.market.dto.youca.TenantVehicleRelDto;
import com.youming.youche.market.vo.youca.OilCardManagementVo;
import com.youming.youche.market.vo.youca.TenantVehicleRelVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface OilCardManagementsMapper extends BaseMapper<OilCardManagement> {

    IPage<OilCardManagementDto> seletcyoukaByAll(Page<OilCardManagementDto> pageInfo, @Param("oilCardManagementVo") OilCardManagementVo oilCardManagementVo,@Param("tenantId") Long tenantId);

    IPage<TenantVehicleRelDto> seletcyoukaByCar(Page<OilCardManagementDto> pageInfo, @Param("tenantVehicleRelVo")TenantVehicleRelVo tenantVehicleRelVo, @Param("tenantId") Long tenantId);

    List<OilCardManagement> findByOilCardNum(@Param("oilCarNum")String oilCarNum, @Param("tenantId")Long tenantId);


    List<OilCardManagementDto> OilCardList(Long tenantId);

    List<OilCardManagement> getOilCardManagementList(Long tenantId);

    void updateManagement(String cardNum, Long balance, Long tenantId);


    List<OilCardVehicleRel> getOilCardVehicleRelList(@Param("oilCardNums") List<String> oilCardNums, @Param("tenantId") Long tenantId);

    List getOilCardManagementByCard(String oilCarNum, long tenantId);

    List<ServiceInfo> getServiceInfo(Integer cardType, Long tenantId);

    List<OilCardManagementOutDto> findByPlateNumber(@Param("plateNumber")String plateNumber, @Param("tenantId") Long tenantId, @Param("oilCardStatus")Integer oilCardStatus);
}
