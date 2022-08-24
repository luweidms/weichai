package com.youming.youche.order.provider.mapper.order;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youming.youche.order.domain.order.AccountBankRel;
import com.youming.youche.order.dto.DirverxDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * Mapper接口
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-03-19
 */
public interface AccountBankRelMapper extends BaseMapper<AccountBankRel> {


    /**
     * 查询用户绑卡信息
     * @param userId
     * @param accountName
     * @param acctNo
     * @param bankType
     * @param userType
     * @return
     */
     List<AccountBankRel> getBankInfo(@Param("userId") Long userId,@Param("accountName") String accountName,
                                            @Param("acctNo") String acctNo,@Param("bankType") Integer bankType,
                                            @Param("userType") Integer userType);


    /**
     * 获取路歌子账户信息
     * @param tenantId
     * @param billLookUp
     * @return
     */
    List<AccountBankRel> getAccountBankList(@Param("tenantId") Long tenantId,@Param("billLookUp") String billLookUp);

    /**
     * 根据用户编号查询账号信息
     * @param userId
     * @param userType
     * @param bankType
     * @return
     */
    List<AccountBankRel> queryAccountBankRel(@Param("userId")Long userId,@Param("userType") Integer userType,@Param("bankType") Integer bankType);

    /**
     * 获取银行卡
     * @param userId
     * @param userType
     * @param bankType
     * @return
     */
    List<AccountBankRel> selectBankCard(@Param("userId")Long userId,@Param("userType") Integer userType, Integer bankType);

    List<AccountBankRel>  selectAcctUserTypeRelList (@Param("userId") Long userId,
                                                     @Param("acctNameOrNo") String acctNameOrNo);

    List<DirverxDto> selectS(@Param("bankAcctNo")String bankAcctNo, @Param("pinganAcctNo")String pinganAcctNo, @Param("userId")Long userId);
}
