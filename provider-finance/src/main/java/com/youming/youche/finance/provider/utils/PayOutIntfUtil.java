package com.youming.youche.finance.provider.utils;

import com.youming.youche.commons.domain.SysCfg;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.util.RedisUtil;
import com.youming.youche.conts.EnumConsts;
import com.youming.youche.order.api.order.IAccountBankRelService;
import com.youming.youche.order.domain.order.AccountBankRel;
import com.youming.youche.order.dto.PinganBankInfoOutDto;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Random;

import static com.youming.youche.conts.EnumConsts.ACCOUNT_BANK_REL.BANK_TYPE_0;
import static com.youming.youche.order.constant.BaseConstant.NEED_BILL_BANK_CARD_QUANTITY_LIMIT;

/**
 * @author zengwen
 * @date 2022/4/27 19:10
 */
@Component
public class PayOutIntfUtil {

    @Resource
    RedisUtil redisUtil;

    @DubboReference(version = "1.0.0")
    IAccountBankRelService accountBankRelService;

    public PinganBankInfoOutDto getRandomPinganBankInfoOut(Long userId, Long tenantId) {
        List<AccountBankRel> accountBankRelList = accountBankRelService.getCollectAmount(tenantId);//观华提供
        if (accountBankRelList != null && accountBankRelList.size() > 0) {
            PinganBankInfoOutDto pinganBankInfoOut = new PinganBankInfoOutDto();
            int bankSize = accountBankRelList.size();
            Double needBillBankCardQuantityLimit = Double.parseDouble(String.valueOf(this.getSysCfg(NEED_BILL_BANK_CARD_QUANTITY_LIMIT,"0").getCfgValue()));
            if (bankSize < needBillBankCardQuantityLimit) {
                throw new BusinessException("请前往银行卡管理界面绑定至少"+needBillBankCardQuantityLimit+"张不同账户名的银行卡！");
            }
            Random ra =new Random();
            int index = ra.nextInt(bankSize);
            AccountBankRel o = accountBankRelList.get(index);
            if (o.getBankType() == BANK_TYPE_0) {
                pinganBankInfoOut.setPrivatePinganAcctIdM(o.getPinganCollectAcctId());
                pinganBankInfoOut.setPrivatePinganAcctIdN(o.getPinganPayAcctId());
                pinganBankInfoOut.setPrivateAcctName(o.getAcctName());
                pinganBankInfoOut.setPrivateAcctNo(o.getAcctNo());
                pinganBankInfoOut.setPrivateBankCode(o.getBankName());
            }
            return pinganBankInfoOut;
        } else {
            throw new BusinessException("车队没有绑定对私收款账户，请检查");
        }
    }

    public SysCfg getSysCfg(String cfgName, String cfgSystem){
        SysCfg sysCfg = (SysCfg) redisUtil.get(EnumConsts.SysCfg.SYS_CFG_NAME.concat(cfgName));

        if (null != sysCfg && (Integer.parseInt(cfgSystem) == -1 || Integer.parseInt(cfgSystem)==(sysCfg.getCfgSystem()))) {
            return sysCfg;
        }

        return new SysCfg();
    }
}
