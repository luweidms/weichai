package com.youming.youche.market.provider.mapper.etc;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.market.domain.etc.EtcMaintain;
import com.youming.youche.market.dto.etc.EtcBindVehicleDto;
import com.youming.youche.market.dto.etc.EtcMaintainDto;
import com.youming.youche.market.vo.etc.EtcMaintainQueryVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * ETC表Mapper接口
 * </p>
 *
 * @author Terry
 * @since 2021-11-22
 */
public interface EtcMaintainMapper extends BaseMapper<EtcMaintain> {

    Page<EtcMaintainDto> selectAll(Page<EtcMaintainDto> page, @Param("etcMaintainQueryVo") EtcMaintainQueryVo etcMaintainQueryVo);

    Page<EtcBindVehicleDto> seletcVehicleByPlateNumber(Page<EtcBindVehicleDto> page
            , @Param("tenantId") Long tenantId
            , @Param("plateNumber") String plateNumber);

    int checkEtcBindVehicle(@Param("tenantId") Long tenantId, @Param("bindVehicle") String bindVehicle);
}
