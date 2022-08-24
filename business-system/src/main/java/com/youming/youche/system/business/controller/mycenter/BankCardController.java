package com.youming.youche.system.business.controller.mycenter;

import com.youming.youche.commons.base.BaseController;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.response.ResponseResult;
import com.youming.youche.system.api.mycenter.IBankCardService;
import com.youming.youche.system.domain.mycenter.AccountBankRel;
import com.youming.youche.system.vo.mycenter.BankCardBindVo;
import com.youming.youche.system.vo.mycenter.BankCardListVo;
import com.youming.youche.system.vo.mycenter.BankCardVo;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName BankCardController
 * @Description 账户绑定银行卡
 * @Author zag
 * @Date 2022/2/15 11:10
 */
@RestController
@RequestMapping("/bankaccount/bankcode")
public class BankCardController extends BaseController<AccountBankRel, IBankCardService> {

    @DubboReference(version = "1.0.0")
    IBankCardService bankCardService;

    @Override
    public IBankCardService getService() {
        return bankCardService;
    }

    /**
     * @description 获取账户绑定银行卡列表
     * @author zag
     * @date 2022/2/16 13:16
     * @return com.youming.youche.commons.response.ResponseResult
     */
    @GetMapping({ "getList/{accountId}" })
    public ResponseResult getList(@PathVariable Long accountId) {
        List<AccountBankRel> list = bankCardService.getListByAccountId(accountId);
        List<BankCardListVo> newList=new ArrayList<>();
        for (AccountBankRel item:list){
            BankCardListVo vo=new BankCardListVo();
            BeanUtils.copyProperties(item,vo);
            newList.add(vo);
        }
        return ResponseResult.success(newList);
    }

    /**
     * 获取用户银行卡列表
     * @author zag
     * @date 2022/5/21 14:14
     * @param userId
     * @return com.youming.youche.commons.response.ResponseResult
     */
    @GetMapping({ "getAll/{userId}" })
    public ResponseResult getAll(@PathVariable Long userId){
        List<AccountBankRel> list = bankCardService.getListByUserId(userId);
        List<BankCardListVo> newList=new ArrayList<>();
        for (AccountBankRel item:list){
            BankCardListVo vo=new BankCardListVo();
            BeanUtils.copyProperties(item,vo);
            newList.add(vo);
        }
        return ResponseResult.success(newList);
    }

    /**
     * @description 获取账户绑定银行卡详情
     * @author zag
     * @date 2022/2/16 13:16
     * @param id
     * @return com.youming.youche.commons.response.ResponseResult
     */
    @GetMapping({ "get/{id}" })
    @Override
    public ResponseResult get(@PathVariable Long id){
        AccountBankRel accountBankRel=bankCardService.get(id);
        BankCardVo bankCardVo=new BankCardVo();
        BeanUtils.copyProperties(accountBankRel,bankCardVo);
        bankCardVo.setBillId(accountBankRel.getBillid());
        return ResponseResult.success(bankCardVo);
    }

    /**
     * @description 账户银行卡绑定
     * @author zag
     * @date 2022/2/16 13:41
     * @return com.youming.youche.commons.response.ResponseResult
     */
    @PostMapping({ "bind" })
    public ResponseResult bind(@Valid @RequestBody BankCardBindVo bankCardBindVo){
        if(!StringUtils.isBlank(bankCardBindVo.getOrigAuthRespNo())){
            if(StringUtils.isBlank(bankCardBindVo.getAuthAmt())){
                throw new BusinessException("鉴权金额不能为空");
            }
        }
        bankCardService.bind(bankCardBindVo);
        return ResponseResult.success("账户银行卡绑定成功");
    }

    /**
     * @description 账户银行卡解绑
     * @author zag
     * @date 2022/2/16 13:41
     * @return com.youming.youche.commons.response.ResponseResult
     */
    @PostMapping({ "unbind/{id}" })
    public ResponseResult unbind(@PathVariable Long id) {
        bankCardService.unbind(id);
        return ResponseResult.success("账户银行卡解绑成功");
    }

    /**
     * @description 小额鉴权申请
     * @author zag
     * @date 2022/2/16 13:41
     * @return com.youming.youche.commons.response.ResponseResult
     */
    @PostMapping({ "authApply" })
    public ResponseResult authenticationApply(@Valid @RequestBody BankCardBindVo bankCardBindVo){
        String respNo=bankCardService.authenticationApply(bankCardBindVo);
        return ResponseResult.success((Object) respNo);
    }


}
