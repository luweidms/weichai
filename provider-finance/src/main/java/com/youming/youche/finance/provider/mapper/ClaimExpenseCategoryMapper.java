package com.youming.youche.finance.provider.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.finance.domain.ClaimExpenseCategory;
import com.youming.youche.finance.dto.ClaimExpenseCategoryDto;
import com.youming.youche.finance.dto.DriverWxDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 车管类目表Mapper接口
 * </p>
 *
 * @author Terry
 * @since 2022-01-24
 */
public interface ClaimExpenseCategoryMapper extends BaseMapper<ClaimExpenseCategory> {


    List<DriverWxDto> selectOr(@Param("userId") Long userId, @Param("orderId")String orderId, @Param("startTime")String startTime,
                               @Param("endTime")String endTime, @Param("sourceRegion")String sourceRegion, @Param("desRegion")String desRegion,
                               @Param("name")String name, @Param("carDriverPhone")String carDriverPhone, @Param("plateNumber")String plateNumber,
                               @Param("tenantId")Long tenantId,@Param("tenantUserId")Long tenantUserId);
}
