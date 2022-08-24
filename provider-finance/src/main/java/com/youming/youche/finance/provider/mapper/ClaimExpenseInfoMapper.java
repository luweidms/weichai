package com.youming.youche.finance.provider.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.components.workbench.WorkbenchDto;
import com.youming.youche.finance.domain.ClaimExpenseInfo;
import com.youming.youche.finance.dto.ClaimExpenseCategoryDto;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 车管报销表Mapper接口
 * </p>
 *
 * @author Terry
 * @since 2022-01-24
 */
public interface ClaimExpenseInfoMapper extends BaseMapper<ClaimExpenseInfo> {


    IPage<ClaimExpenseCategoryDto> doQuery (@Param("expenseType") Integer expenseType,
                                            @Param("specialExpenseNum") String specialExpenseNum,
                                            @Param("stairCategory") String stairCategory,
                                            @Param("secondLevelCategory")String secondLevelCategory,
                                            @Param("expenseSts")Integer expenseSts,
                                            @Param("userName")String userName,
                                            @Param("userId")String userId,
                                            @Param("userPhone") String userPhone,
                                            @Param("plateNumber") String plateNumber,
                                            @Param("startTime") LocalDateTime startTime,
                                            @Param("endTime") LocalDateTime endTime,
                                            @Param("flowId")String flowId,
                                            @Param("orderId")String orderId,
                                            @Param("oneOrgId")String oneOrgId,
                                            @Param("twoOrgId")String twoOrgId,
                                            @Param("threeOrgId")String threeOrgId,
                                            @Param("stairCategoryList") List<String> stairCategoryList,
                                            @Param("secondLevelCategoryList")List<String> secondLevelCategoryList,
                                            @Param("waitDeal")Boolean waitDeal,
                                            @Param("dataPermissionIds")List<Long> dataPermissionIds,
                                            @Param("subOrgList")List<Long> subOrgList,
                                            @Param("lids") List<Long> lids,
                                            @Param("tenantId")Long tenantId,
                                            @Param("expenseStsList")List<String> expenseStsList,
                                            @Param("aBoolean")Boolean aBoolean,
                                            @Param("type")Integer type,
                                            Page<ClaimExpenseCategoryDto> page);


    /**
     * 营运工作台  管理费用  待我审
     */
    List<WorkbenchDto> getTableManageCostCount();

    /**
     * 营运工作台  管理费用  我发起
     */
    List<WorkbenchDto> getTableManageCostMeCount();
}
