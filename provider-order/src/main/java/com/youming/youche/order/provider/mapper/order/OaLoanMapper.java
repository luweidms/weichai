package com.youming.youche.order.provider.mapper.order;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.order.domain.order.OaLoan;
import com.youming.youche.order.dto.order.OaLoanListDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 借支信息表Mapper接口
 * </p>
 *
 * @author liangyan
 * @since 2022-03-22
 */
public interface OaLoanMapper extends BaseMapper<OaLoan> {

    List<OaLoan> selectSalary(Long userId, Long tenantId, String carPhone, List<Integer> loanType, List<Integer> sts);

    /**
     * 根据orderId查询本订单借支的总数量以及总金额
     *
     * @param orderId
     * @param userId
     * @return
     */
    int queryOaloanCount(@Param("orderId") Long orderId, @Param("userId") Long userId);

    Page<OaLoanListDto> queryOaLoanList(Page<OaLoanListDto> page,
                                        @Param("tenantId") Long tenantId, @Param("oaLoanId") String oaLoanId, @Param("loanSubjects") String loanSubjects,
                                        @Param("orderId") Long orderId, @Param("plateNumber") String plateNumber, @Param("userName") String userName,
                                        @Param("mobilePhone") String mobilePhone, @Param("states") String states, @Param("queryType") Integer queryType,
                                        @Param("lidStr") String lidStr, @Param("orgStr") String orgStr,
                                        @Param("getLoanBelongAdminSubjectList") String getLoanBelongAdminSubjectList,
                                        @Param("getLoanBelongDriverSubjectList") String getLoanBelongDriverSubjectList,@Param("waitDeal") Boolean waitDeal);


    OaLoan queryOaLoanById(@Param("LId") Long LId, @Param("busiCodes") List<String> busiCodes, @Param("busiCode") String busiCode);
}
