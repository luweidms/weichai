package com.youming.youche.system.provider.service.ac;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.util.LoginUtils;
import com.youming.youche.commons.util.RedisUtil;
import com.youming.youche.finance.api.munual.IPayoutIntfThreeService;
import com.youming.youche.finance.vo.munual.QueryPayoutIntfsVo;
import com.youming.youche.market.api.facilitator.IConsumeOilFlowService;
import com.youming.youche.system.api.ISysTenantDefService;
import com.youming.youche.system.api.ac.IOrderAccountService;
import com.youming.youche.system.domain.UserDataInfo;
import com.youming.youche.system.domain.ac.OrderAccount;
import com.youming.youche.system.dto.SysTenantOutDto;
import com.youming.youche.system.dto.ac.AccountDetailsWXDto;
import com.youming.youche.system.dto.ac.OrderAccountOutDto;
import com.youming.youche.system.provider.mapper.UserDataInfoMapper;
import com.youming.youche.system.provider.mapper.ac.OrderAccountMapper;
import com.youming.youche.system.provider.utis.SysStaticDataRedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * <p>
 * 订单账户表 服务实现类
 * </p>
 *
 * @author Terry
 * @since 2022-01-24
 */
@Slf4j
@DubboService(version = "1.0.0")
public class OrderAccountServiceImpl extends BaseServiceImpl<OrderAccountMapper, OrderAccount> implements IOrderAccountService {

    @Resource
    LoginUtils loginUtils;

    @Resource
    RedisUtil redisUtil;

    @Resource
    ISysTenantDefService sysTenantDefService;

    @Resource
    UserDataInfoMapper userDataInfoMapper;

    @DubboReference(version = "1.0.0")
    IConsumeOilFlowService consumeOilFlowService;

    @DubboReference(version = "1.0.0")
    IPayoutIntfThreeService payoutIntfThreeService;


    @Override
    public List<OrderAccount> getOrderAccount(Long userId, Long tenantId, Integer userType) {
        if (userId == null) {
            throw new BusinessException("用户id不能为空");
        } else if (tenantId == null) {
            throw new BusinessException("车队id不能为空");
        }
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("user_id", userId);
        queryWrapper.eq(tenantId, "tenant_id");
        if (userType > 0) {
            queryWrapper.eq("user_type", userType);
        }

        List<OrderAccount> list = baseMapper.selectList(queryWrapper);
        return list;
    }

    @Override
    public AccountDetailsWXDto getAccountDetailsWX(String name, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        Long userId = loginInfo.getUserInfoId();
        UserDataInfo userDataInfo = userDataInfoMapper.selectById(userId);
        Integer userType = userDataInfo.getUserType();
        Long tenantId = loginInfo.getTenantId();

        LambdaQueryWrapper<OrderAccount> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderAccount::getUserId, userId);
        if (userType > 0) {
            queryWrapper.eq(OrderAccount::getUserType, userType);
        }

        List<OrderAccount> accountList = baseMapper.selectList(queryWrapper);

        HashSet hs=new HashSet<>();
        for (OrderAccount ac : accountList) {
            hs.add(ac.getSourceTenantId());
        }
        Iterator it = hs.iterator();
        List<OrderAccountOutDto> orderAccountOuts = new ArrayList<>();

        while(it.hasNext()) {
            // 车队ID
            Long curId = (Long)it.next();
            if(curId == 0){
                continue;
            }
            // 总现金
            long totalBalance = 0;
            // 总未到期
            long totalMarginBalance = 0;

            //账户状态
            Integer accState = null;
            //账户状态名称
            String accStateName = null;

            //应收逾期
            long receivableOverdueBalance = 0;
            //应付预期
            long payableOverdueBalance = 0;

            for (OrderAccount ac : accountList) {
                if(ac.getSourceTenantId() == curId){
                    totalBalance += ac.getBalance() == null ? 0L : ac.getBalance();
                    receivableOverdueBalance += ac.getReceivableOverdueBalance() == null ? 0L : ac.getReceivableOverdueBalance();
                    payableOverdueBalance += ac.getPayableOverdueBalance() == null ? 0L : ac.getPayableOverdueBalance();
                    if (ac.getMarginBalance() > 0) {
                        totalMarginBalance += ac.getMarginBalance();
                    }
                    accStateName = SysStaticDataRedisUtils.getSysStaticDataCodeName(redisUtil, tenantId, "ACC_STATE", String.valueOf(ac.getAccState()));
                }
            }
            OrderAccountOutDto oa = new OrderAccountOutDto();
            oa.setReceivableOverdueBalance(receivableOverdueBalance);
            oa.setPayableOverdueBalance(payableOverdueBalance);
            oa.setTotalBalance(totalBalance);
            oa.setTotalMarginBalance(totalMarginBalance);
            oa.setAccState(accState);
            oa.setAccStateName(accStateName);
            oa.setSourceTenantId(curId);
            try{
                SysTenantOutDto st = sysTenantDefService.getSysTenantDefById(curId);
                if(st != null){
                    oa.setName(st.getCompanyName());
                }
                oa.setMobilePhone(st.getLinkPhone());
            }catch (Exception e){
                e.printStackTrace();
            }

            if(StringUtils.isBlank(name)||(StringUtils.isNotBlank(name) && StringUtils.isNotBlank(oa.getName()) && oa.getName().contains(name))){
                orderAccountOuts.add(oa);
            }

            // 未到期金额  PC端的逻辑和原小程序的逻辑不一致
            try {
                totalMarginBalance = consumeOilFlowService.getTotalMarginBalance(userId, curId);
                oa.setTotalMarginBalance(totalMarginBalance);
            } catch (Exception e) {
                log.info("获取车队服务商的未到期明细异常:{}", e.getMessage());
                oa.setTotalMarginBalance(0L);
            }

            try {
                //receivableOverdueBalance = payoutIntfThreeService.getDueDateDetailsSum(userId, null, null, null, null, curId, null, accessToken);

                QueryPayoutIntfsVo queryPayoutIntfsVo = new QueryPayoutIntfsVo();
                queryPayoutIntfsVo.setSourceUserId(userId);
                queryPayoutIntfsVo.setPayTenantId(curId);
                Map m = payoutIntfThreeService.queryPayoutIntfsSum(accessToken,queryPayoutIntfsVo);
                if(m.get("noVerificatMoney") != null) {
                    String noVerificatMoney = String.valueOf(m.get("noVerificatMoney"));
                    receivableOverdueBalance = Long.parseLong(noVerificatMoney);
                }
            } catch (Exception e) {
                log.info("应收逾期数据:" + e.getMessage());
            }
            oa.setReceivableOverdueBalance(receivableOverdueBalance);
        }

        AccountDetailsWXDto dto = new AccountDetailsWXDto();
        dto.setItems(orderAccountOuts);
        dto.setUserId(userId);

        return dto;
    }
}
