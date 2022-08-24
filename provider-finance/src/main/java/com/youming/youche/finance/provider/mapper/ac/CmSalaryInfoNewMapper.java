package com.youming.youche.finance.provider.mapper.ac;

import com.youming.youche.finance.domain.ac.CmSalaryInfoNew;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youming.youche.finance.domain.ac.CmSalaryTemplate;
import com.youming.youche.finance.domain.ac.CmTemplateField;
import com.youming.youche.finance.dto.SubsidyInfoDto;
import com.youming.youche.finance.vo.ModifySalaryInfoVo;
import com.youming.youche.finance.vo.OrderSalaryInfoVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * Mapper接口
 * </p>
 *
 * @author zengwen
 * @since 2022-04-18
 */
public interface CmSalaryInfoNewMapper extends BaseMapper<CmSalaryInfoNew> {
    /**
     * 获取结算补贴信息
     *
     * @param id
     * @param tenantId
     * @return
     */
    SubsidyInfoDto getSubsidyInfo(@Param("id") Long id, @Param("tenantId") Long tenantId);

    /**
     * 结算补贴
     *
     * @param orderId
     * @param salary
     * @return
     */
    int balanceSubsidy(@Param("orderId") Long orderId, @Param("salary") Double salary);


    /**
     * 修改补贴金额结算后状态
     *
     * @param orderId
     * @return
     */
    int subsidyState(@Param("orderId") Long orderId);

    /**
     * 结算后补贴金额累加到已結算金额
     *
     * @param salaryId
     * @param paidSalaryFee
     * @return
     */
    int subsidyTotal(@Param("salaryId") Long salaryId, @Param("paidSalaryFee") Double paidSalaryFee);

    /**
     * 获取车队最近的一个工资模板
     *
     * @param tenantId
     * @return
     */
    CmSalaryTemplate getCmSalaryTemplate(@Param("tenantId") Long tenantId);


    /**
     * 查询车队月份的模板
     *
     * @param tenantId
     * @param templateMonth
     * @return
     */
    CmSalaryTemplate getCmSalaryTemplateMonth(@Param("tenantId") Long tenantId, @Param("templateMonth") String templateMonth);


    /**
     * 查询车队工资单数量，可以按照状态过滤
     *
     * @param settleMonth
     * @param tenantId
     * @param state
     * @param userType
     * @return
     */
    Long getCmSalaryInfoCount(@Param("settleMonth") String settleMonth, @Param("tenantId") Long tenantId, @Param("state") int state, @Param("userType") int userType);

    /**
     * 根据月份查询工资信息
     *
     * @param tenantId
     * @param month
     * @return
     */
    List<CmTemplateField> getCmSalaryTemplateByMon(@Param("tenantId") Long tenantId, @Param("month") String month);

    List<CmSalaryInfoNew> selectSalary(String trim, String substring, Long tenantId);

    /**
     * 修改补贴金额
     *
     * @param id
     * @param subsidy
     * @return
     */
    int changeSubsidy(@Param("id") Long id, @Param("subsidy") Double subsidy);

    /**
     * 修改工资信息
     *
     * @param modifySalaryInfoVo
     * @return
     */
    int modifySalaryInfo(@Param("modifySalaryInfoVo") ModifySalaryInfoVo modifySalaryInfoVo);

    /**
     * 根据id查询工资信息
     *
     * @param id
     * @return
     */
    CmSalaryInfoNew salaryInfoById(@Param("id") Long id);


    /**
     * 结算获取订单信息
     * @param orderId
     * @param tenantId
     * @return
     */
    OrderSalaryInfoVo getOrderSalaryInfo(@Param("orderId") Long orderId, @Param("tenantId") Long tenantId);

}
