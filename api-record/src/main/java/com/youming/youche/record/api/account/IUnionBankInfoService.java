package com.youming.youche.record.api.account;

import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.record.domain.account.UnionBankInfo;

import java.util.List;

/**
 * <p>
 * 大小额行号表 服务类
 * </p>
 *
 * @author hzx
 * @since 2022-04-22
 */
public interface IUnionBankInfoService extends IBaseService<UnionBankInfo> {

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
    List<UnionBankInfo> getUnionBankInfoList(String cityCode, String bankId, String keyWord);

    /**
     * 获取支行编号
     *
     * @param cityCode    地市信息
     * @param bankClsCode 银行信息
     * @param bankName    银行信息
     * @param keyWord     关键字
     */
    List<UnionBankInfo> getUnionBankInfoList(String cityCode, String bankClsCode, String bankName, String keyWord);

}
