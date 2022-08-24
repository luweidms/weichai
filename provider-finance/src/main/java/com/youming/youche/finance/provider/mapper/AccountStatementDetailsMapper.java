package com.youming.youche.finance.provider.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.finance.domain.AccountStatementDetails;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author zengwen
 * @date 2022/4/14 12:45
 */
public interface AccountStatementDetailsMapper extends BaseMapper<AccountStatementDetails> {

    List<AccountStatementDetails> getAccountStatementDetails(@Param("accountStatementId") Long accountStatementId, @Param("state") Integer state);

    List<AccountStatementDetails> getMatchEtcFeeToBill(@Param("tenantId") Long tenantId, @Param("receiverUserId") Long receiverUserId, @Param("receiverPhone") String receiverPhone);

    Page<AccountStatementDetails> getAppDetailList(@Param("accountStatementId") Long accountStatementId,
                                                   @Param("plateNumber") String plateNumber,
                                                   @Param("carDriverName") String carDriverName,
                                                   @Param("carDriverPhone") String carDriverPhone,
                                                   @Param("answerName") String answerName,
                                                   @Param("answerPhone") String answerPhone,
                                                   @Param("tenantId") Long tenantId,
                                                   @Param("vehicleClass") Integer vehicleClass,
                                                   Page<AccountStatementDetails> page);
}
