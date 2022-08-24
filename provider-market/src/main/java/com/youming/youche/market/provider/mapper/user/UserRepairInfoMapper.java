package com.youming.youche.market.provider.mapper.user;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.market.domain.user.UserRepairInfo;
import com.youming.youche.market.dto.facilitator.RepairInfoDto;
import com.youming.youche.market.dto.user.UserRepairInfoDto;
import com.youming.youche.market.dto.user.VehicleAfterServingDto;
import com.youming.youche.market.vo.facilitator.RepairInfoVo;
import com.youming.youche.market.vo.user.UserRepairInfoVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 维修记录
 *
 * @author hzx
 * @date 2022/3/12 11:11
 */
public interface UserRepairInfoMapper extends BaseMapper<UserRepairInfo> {

    /**
     * 查询维修保养
     */
    Page<UserRepairInfoDto> queryUserRepairAuth(Page<UserRepairInfoVo> page, @Param("vo") UserRepairInfoVo userRepairInfoVo, @Param("busiIdStr") String busiIdStr);

    Page<RepairInfoVo>  queryRepairInfo(Page<RepairInfoVo> page,
                                        @Param("inParam") RepairInfoDto inParam,
                                        @Param("serviceUserId") Long serviceUserId,
                                        @Param("isSum")Boolean isSum,
                                        @Param("productId") List<Long> productId,
                                        @Param("baseUser")LoginInfo baseUser);

    List<UserRepairInfoVo>  getUserRepairByProductIds (@Param("productIds") List<Long> productIds);


    List<UserRepairInfo>  getUserRepairInfo(@Param("serviceUserId") Long serviceUserId,
                                            @Param("endTime") String endTime,
                                            @Param("startTime") String startTime);

    /**
     * 车辆报表查询维修费和保养费
     */
    List<VehicleAfterServingDto> getVehicleAfterServingByMonth(@Param("plateNumber") String plateNumber, @Param("tenantId") Long tenantId, @Param("month") String month);

}
