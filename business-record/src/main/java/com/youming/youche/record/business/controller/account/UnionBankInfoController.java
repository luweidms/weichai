package com.youming.youche.record.business.controller.account;


import com.youming.youche.commons.base.BaseController;
import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.response.ResponseResult;
import com.youming.youche.record.api.account.IUnionBankInfoService;
import com.youming.youche.record.domain.account.UnionBankInfo;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 大小额行号表 前端控制器
 * </p>
 *
 * @author hzx
 * @since 2022-04-22
 */
@RestController
@RequestMapping("union/bank/info")
public class UnionBankInfoController extends BaseController {

    @DubboReference(version = "1.0.0")
    IUnionBankInfoService iUnionBankInfoService;

    @Override
    public IBaseService getService() {
        return null;
    }

    /**
     * 接口编号 10053
     *
     * 接口入参：
     *         cityId    地市编码
     *         bankId  银行编号
     *         keyWord  关键字
     *
     * 接口出参：
     *         branchId    支行编号 BankNo
     *         branchName 支行名称 BankDisplayName
     *
     * 获取支行编号
     */
    @GetMapping("getUnionBankInfoList")
    public ResponseResult getUnionBankInfoList(String cityId,String bankId,String keyWord){
        if(org.apache.commons.lang3.StringUtils.isEmpty(cityId)){
            throw new BusinessException("地市信息不能为空");
        }
        List<UnionBankInfo> unionBankInfoList = iUnionBankInfoService.getUnionBankInfoList(cityId, bankId, keyWord);
        return ResponseResult.success(unionBankInfoList);
    }

}
