package com.youming.youche.system.provider.mapper.mycenter;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youming.youche.system.domain.mycenter.CmbAccountTransactionRecord;
import com.youming.youche.system.dto.mycenter.BankFlowDetailsAppOutDto;
import com.youming.youche.system.vo.mycenter.BankFlowDetailsAppVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @InterfaceName BankAccountTranMapper
 * @Description 添加描述
 * @Author zag
 * @Date 2022/2/19 20:13
 */
public interface BankAccountTranMapper extends BaseMapper<CmbAccountTransactionRecord> {

    /**
     * 查询支付密码
     */
    String selectPayPwd(@Param("userId") Long userId);

    /**
     * 修改支付密码
     */
    int updatePayPwd(@Param("pwd") String pwd,@Param("userId") Long userId);

    /**
     * 查询银行流水-APP、微信
     * @return
     */
    List<BankFlowDetailsAppOutDto> getBankFlowDetailsToAppAndWx(@Param("bankFlowDetailsAppVo") BankFlowDetailsAppVo bankFlowDetailsAppVo);

}