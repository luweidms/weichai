package com.youming.youche.system.api.mycenter;

import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.system.domain.mycenter.AccountBankRel;
import com.youming.youche.system.vo.mycenter.BankCardBindVo;

import java.util.List;

/**
 * @InterfaceName IBankCardService
 * @Description 添加描述
 * @Author zag
 * @Date 2022/2/19 19:43
 */
public interface IBankCardService extends IBaseService<AccountBankRel> {

    /** 根据AccountId获取账户银行卡列表
     * */
    List<AccountBankRel> getListByAccountId(Long accountId);

    /** 根据UserId获取用户银行卡列表
     * */
    List<AccountBankRel> getListByUserId(Long userId);

    /** 账户银行卡绑定
     * */
    void bind(BankCardBindVo bankCardBindVo);

    /** 账户银行卡解绑
     * */
    void unbind(Long id);

    /** 银行卡小额鉴权申请
     * */
    String authenticationApply(BankCardBindVo bankCardBindVo);

}