package com.youming.youche.order.provider.service.order;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.common.collect.Lists;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.util.LoginUtils;
import com.youming.youche.conts.EnumConsts;
import com.youming.youche.order.api.order.IAccountDetailsSummaryService;
import com.youming.youche.order.domain.order.AccountDetails;
import com.youming.youche.order.domain.order.AccountDetailsSummary;
import com.youming.youche.order.provider.mapper.order.AccountDetailsSummaryMapper;
import com.youming.youche.system.api.IUserDataInfoService;
import com.youming.youche.system.domain.UserDataInfo;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;


/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-03-22
 */
@DubboService(version = "1.0.0")
@Service
public class AccountDetailsSummaryServiceImpl extends BaseServiceImpl<AccountDetailsSummaryMapper, AccountDetailsSummary> implements IAccountDetailsSummaryService {

    @DubboReference(version = "1.0.0")
    IUserDataInfoService iUserDataInfoService;

    @Resource
    LoginUtils loginUtils;

    @Override
    public void saveAccountDetailsSummary(AccountDetails details) {
        AccountDetailsSummary accountDetailsSummary = new AccountDetailsSummary();
        BeanUtils.copyProperties(details, accountDetailsSummary);
        accountDetailsSummary.setId(null);
        this.save(accountDetailsSummary);
    }

    /**
     * 已付尾款
     *
     * @param orderId 订单号
     * @return
     * @throws Exception
     */
    @Override
    public List doQueryPaidPayment(Long orderId, Long tenantId, Long userId, int userType) {
        LambdaQueryWrapper<AccountDetailsSummary> summaryLambdaQueryWrapper = Wrappers.lambdaQuery();
        summaryLambdaQueryWrapper.eq(AccountDetailsSummary::getOrderId, orderId);
        summaryLambdaQueryWrapper.eq(AccountDetailsSummary::getUserId, userId);
        if (userType > 0) {
            summaryLambdaQueryWrapper.eq(AccountDetailsSummary::getUserType, userType);
        }
        summaryLambdaQueryWrapper.in(AccountDetailsSummary::getSubjectsId, new Long[]{EnumConsts.SubjectIds.FINAL_PAYFOR_RECEIVABLE_IN, EnumConsts.SubjectIds.ADVANCE_PAY_RECEIVABLE_IN
                , EnumConsts.SubjectIds.ACCOUNT_STATEMENT_MARGIN_RECEIVABLE, EnumConsts.SubjectIds.ACCOUNT_STATEMENT_DEDUCTION});
        List<AccountDetailsSummary> accountDetailsSummaryList = super.list(summaryLambdaQueryWrapper);
        return accountDetailsSummaryList;
    }

    @Override
    public List<AccountDetailsSummary> doQueryAmountPaidApp(Long orderId, Long payeeUserId, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);

        UserDataInfo userDataInfo = iUserDataInfoService.selectUserType(loginInfo.getUserInfoId());
        LambdaQueryWrapper<AccountDetailsSummary> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AccountDetailsSummary::getOrderId, orderId);
        queryWrapper.ne(AccountDetailsSummary::getAmount, 0L);

        if (userDataInfo != null && userDataInfo.getUserType() > 0) {
            queryWrapper.eq(AccountDetailsSummary::getUserType, userDataInfo.getUserType());
        }

        List<Long> subjectIds = Lists.newArrayList(EnumConsts.SubjectIds.BEFORE_RECEIVABLE_OVERDUE_SUB,EnumConsts.SubjectIds.BEFORE_Virtual_SUB
                ,EnumConsts.SubjectIds.BEFORE_Entity_SUB,EnumConsts.SubjectIds.BEFORE_ETC_SUB,EnumConsts.SubjectIds.ARRIVE_CHARGE_PAYABLE_OVERDUE_SUB
                ,EnumConsts.SubjectIds.EXCEPTION_IN_RECEIVABLE_OVERDUE_SUB,EnumConsts.SubjectIds.EXCEPTION_FEE_OUT,EnumConsts.SubjectIds.PRESCRIPTION_FINE,
                EnumConsts.SubjectIds.FINAL_PAYFOR_RECEIVABLE_IN,EnumConsts.SubjectIds.ADVANCE_PAY_RECEIVABLE_IN
                ,EnumConsts.SubjectIds.ACCOUNT_STATEMENT_MARGIN_RECEIVABLE,EnumConsts.SubjectIds.ACCOUNT_STATEMENT_DEDUCTION,
                EnumConsts.SubjectIds.INSURANCE_SUB,EnumConsts.SubjectIds.OIL_CARD_RELEASE_RECEIVABLE_IN,EnumConsts.SubjectIds.EXCEPTION_FINE_FEE,
                EnumConsts.SubjectIds.ARRIVE_CHARGE_DEDUCTION_PRESCRIPTION,EnumConsts.SubjectIds.ARRIVE_CHARGE_DEDUCTION_EXCEPTION);
        queryWrapper.in(AccountDetailsSummary::getSubjectsId, subjectIds);

        return baseMapper.selectList(queryWrapper);
    }
}
