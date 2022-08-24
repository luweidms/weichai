package com.youming.youche.market.provider.service.facilitator;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.market.api.facilitator.ICmbBankAccountInfoService;
import com.youming.youche.market.domain.facilitator.CmbBankAccountInfo;
import com.youming.youche.market.dto.user.AccountBankByTenantIdDto;
import com.youming.youche.market.provider.mapper.facilitator.CmbBankAccountInfoMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;


/**
 * <p>
 * 招行帐户信息表 服务实现类
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-02-09
 */
@DubboService(version = "1.0.0")
@Service
public class CmbBankAccountInfoServiceImpl extends BaseServiceImpl<CmbBankAccountInfoMapper, CmbBankAccountInfo> implements ICmbBankAccountInfoService {

    @Resource
    private CmbBankAccountInfoMapper cmbBankAccountInfoMapper;


//    @Override
//    public IPage queryBankAccountTranFlowList(BankAccountTranFlowInDto bankAccountTranFlowInDto) {
//
//        List<CmbBankAccountInfo> bankAccountInfoList = queryBankAccountList(Long.parseLong(bankAccountTranFlowInDto.getUserId()), bankAccountTranFlowInDto.getAcclevel());
//        Long userId = Long.parseLong(bankAccountTranFlowInDto.getUserId());
//        String tranType = bankAccountTranFlowInDto.getTranType();
//        List<String> mbrNoList = new ArrayList<>();
//        LambdaQueryWrapper<CmbBankAccountInfo> lambda=new QueryWrapper<CmbBankAccountInfo>().lambda();
//        if(bankAccountInfoList == null){
//            lambda.eq(CmbBankAccountInfo::getUserid,userId);
//        }else{
//            bankAccountInfoList.forEach(temp -> {
//                mbrNoList.add(temp.getMbrNo());
//            });
//            lambda.or().in(CmbBankAccountInfo::getpay)
//        }
//        return null;
//    }

    @Override
    public List<CmbBankAccountInfo> queryBankAccountList(Long userId, String accLevel) {
        LambdaQueryWrapper<CmbBankAccountInfo> lambda=new QueryWrapper<CmbBankAccountInfo>().lambda();
        if("0".equals(accLevel) || "1".equals(accLevel)){
            lambda.eq(CmbBankAccountInfo::getUserid,userId)
                    .eq(CmbBankAccountInfo::getAcclevel,accLevel);
        }
        return this.list(lambda);
    }

    @Override
    public AccountBankByTenantIdDto getCmbBankAccountInfo(Long tenantId) {

        List<AccountBankByTenantIdDto> list = cmbBankAccountInfoMapper.getCmbBankAccountInfo(tenantId, "1", "C35");
        if(list != null && list.size() > 0){
            return list.get(0);
        }
        List<AccountBankByTenantIdDto> list1 = cmbBankAccountInfoMapper.getCmbBankAccountInfo(tenantId, "1", "P01");
        if(list1 != null && list1.size() > 0){
            return list1.get(0);
        }
        return null;
    }
}
