package com.youming.youche.finance.api;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.commons.domain.ImportOrExportRecords;
import com.youming.youche.finance.domain.AccountDetails;
import com.youming.youche.finance.dto.AccountQueryDetailDto;
import com.youming.youche.finance.vo.AccountQueryDetailVo;
import com.youming.youche.order.domain.OrderAccount;
import com.youming.youche.order.domain.order.BusiSubjectsRel;
import com.youming.youche.record.domain.vehicle.VehicleDataInfo;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author zengwen
 * @date 2022/4/12 17:16
 */
public interface IAccountDetailsThreeService extends IBaseService<AccountDetails> {

    /**
     * 查询司机账户账单明细
     * @param accessToken
     * @param accountQueryDetailVo
     * @return
     */
    IPage<AccountQueryDetailDto> getAccountQueryDetails(String accessToken, AccountQueryDetailVo accountQueryDetailVo);

    /**
     * 导出司机账户账单明细
     * @param accessToken
     * @param accountQueryDetailVo
     * @param importOrExportRecords
     */
    void downloadExcelFile(String accessToken, AccountQueryDetailVo accountQueryDetailVo, ImportOrExportRecords importOrExportRecords);


    /**
     * 写入账户明细表(司机借支核销)2017-05-24增加
     * @param businessTypes 业务类型
     * @param businessCode 业务编号
     * @param orderId 订单编号
     * @param otherUserId 对方用户编号
     * @param otherName 对方名称
     * @param subjectsList 费用信息集合
     *                     VehicleDataInfo
     * @throws Exception
     * @return long
     */
    Long insetAccDetOaLoan(Integer businessTypes, Long businessCode, Long otherUserId, String otherName, String vehicleAffiliation,
                           VehicleDataInfo v, List<BusiSubjectsRel> subjectsList, Long soNbr, Long orderId, String toUserName, OrderAccount account,
                           Long tenantId, Integer userType, String accessToken) ;



    void saveAccountDetails(AccountDetails details);
    /**
     * 资金帐户创建公共接口
     * @param userId  用户编号
     * @param vehicleAffiliation 资金渠道类型
     * @param tenantId 账户归属租户id
     * @param sourceTenantId 资金来源租户id
     * @param oilAffiliation 油资金渠道
     * @param userType 用户类型
     * @throws Exception
     * @return OrderAccount
     */
    OrderAccount queryOrderAccount(Long userId, String vehicleAffiliation, Long tenantId, Long sourceTenantId, String oilAffiliation,Integer userType,String accessToken) ;





}
