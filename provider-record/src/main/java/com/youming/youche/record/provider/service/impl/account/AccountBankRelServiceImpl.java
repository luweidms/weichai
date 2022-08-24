package com.youming.youche.record.provider.service.impl.account;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.util.LoginUtils;
import com.youming.youche.conts.SysStaticDataEnum;
import com.youming.youche.record.api.account.IAccountBankRelService;
import com.youming.youche.record.common.EnumConsts;
import com.youming.youche.record.domain.account.AccountBankRel;
import com.youming.youche.record.domain.account.AccountBankUserTypeRel;
import com.youming.youche.record.dto.PTDto;
import com.youming.youche.record.provider.mapper.account.AccountBankRelMapper;
import com.youming.youche.record.provider.mapper.account.AccountBankUserTypeRelMapper;
import com.youming.youche.system.api.ISysTenantDefService;
import com.youming.youche.system.api.IUserDataInfoService;
import com.youming.youche.system.domain.SysTenantDef;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 银行卡表 服务实现类
 * </p>
 *
 * @author Terry
 * @since 2021-11-19
 */
@DubboService(version = "1.0.0")
public class AccountBankRelServiceImpl extends BaseServiceImpl<AccountBankRelMapper, AccountBankRel>
		implements IAccountBankRelService {

	@Autowired
	AccountBankRelMapper accountBankRelMapper;

	@Autowired
	AccountBankUserTypeRelMapper accountBankUserTypeRelMapper;

	@DubboReference(version = "1.0.0")
	ISysTenantDefService iSysTenantDefService;

	@Resource
	LoginUtils loginUtils;

	@DubboReference(version = "1.0.0")
	IUserDataInfoService iUserDataInfoService;

	@Override
	public boolean isUserTypeBindCardAll(long userId,Integer userType){
		List<String> resultList=accountBankRelMapper.isUserTypeBindCardAll(userId,userType);
		if (resultList != null && resultList.size() > 0) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public List<AccountBankRel> queryAccountBankRel(long userId, Integer userType, Integer bankType) {
		List<AccountBankRel> accountBankRelList=accountBankRelMapper.queryAccountBankRel(userId,userType,bankType);
		if(accountBankRelList!=null && accountBankRelList.size()>0){
			for(AccountBankRel accountBankRel:accountBankRelList){
				if (accountBankRel != null) {
					QueryWrapper<AccountBankUserTypeRel> accountBankUserTypeRelQueryWrapper = new QueryWrapper<>();
					accountBankUserTypeRelQueryWrapper.eq("bank_Rel_Id",accountBankRel.getId());
					if (bankType != null && bankType.intValue() > 0) {
						accountBankUserTypeRelQueryWrapper.eq("bank_Type",bankType);
					}
					if (userType != null && userType.intValue() > 0) {
						accountBankUserTypeRelQueryWrapper.eq("user_Type",userType);
					}
					List<AccountBankUserTypeRel> acctUserTypeRelList =accountBankUserTypeRelMapper.selectList(accountBankUserTypeRelQueryWrapper);
					accountBankRel.setAccountBankUserTypeRels(acctUserTypeRelList);
				}

			}
		}
		return accountBankRelList;
	}

	@Override
	public List<AccountBankRel> getTenantCardListByPlatNumber(String platNumber) {
		return accountBankRelMapper.getTenantCardListByPlatNumber(platNumber);
	}

	@Override
	public PTDto getPTAccountAndTailNumber(Integer bankType, String accessToken, Long... userId) {
		LoginInfo loginInfo = loginUtils.get(accessToken);
		Integer userType = iUserDataInfoService.selectUserType(loginInfo.getUserInfoId()).getUserType();
		//获取平台账户
		PTDto dto = new PTDto();
		AccountBankRel rel = this.getAccountBankRelById(SysStaticDataEnum.PT_ACCOUNT_BANK_REL_ID);

		dto.setAcctName(rel.getAcctName());
		dto.setAcctNo(rel.getAcctNo());
		dto.setBankName(rel.getBankName());
		dto.setBankProvCity(rel.getProvinceName() + rel.getCityName());
		dto.setBranchName(rel.getBranchName());

		if (userId != null && userId.length > 0) {
			dto.setTailNumber(getTailNumber(bankType, userId[0], userType));
			dto.setTailNumberNew(getTailNumberNew(bankType, userId[0], userType));
		} else {
			dto.setTailNumber(getTailNumber(bankType, loginInfo.getId(), userType));
			dto.setTailNumberNew(getTailNumberNew(bankType, loginInfo.getId(), userType));
		}
		if (userType == SysStaticDataEnum.USER_TYPE.CUSTOMER_USER) {//如果是员工，获取超管
			SysTenantDef sysTenantDef = iSysTenantDefService.getSysTenantDef(loginInfo.getTenantId());
			dto.setAdminUserTailNumber(null == sysTenantDef ? "" : getTailNumber(bankType, sysTenantDef.getAdminUser(), SysStaticDataEnum.USER_TYPE.ADMIN_USER));
		} else {
			dto.setAdminUserTailNumber(getTailNumber(bankType, loginInfo.getId(), userType));
		}
		return dto;
	}

	@Override
	public AccountBankRel getAccountBankRelById(Long relId) {
		return this.getById(relId);
	}

	@Override
	public Boolean isUserBindCardEver(Long userId) {
		return isUserBindCardEver(userId, EnumConsts.ACCOUNT_BANK_REL.BANK_TYPE_0);
	}

	@Override
	public Boolean isUserBindCardEver(Long userId, Integer bankType) {
		LambdaQueryWrapper<AccountBankRel> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.eq(AccountBankRel::getUserId, userId);
		queryWrapper.eq(AccountBankRel::getIsDefaultAcct, 1);

		if (null != bankType) {
			queryWrapper.eq(AccountBankRel::getBankType, bankType);
		}

		List<AccountBankRel> list = this.list(queryWrapper);
		if (list == null || list.isEmpty()) {
			return false;
		}

		return StringUtils.isNotEmpty(list.get(0).getAcctName());
	}

	@Override
	public AccountBankRel getAccountBankRelByAcctNo(String acctNo) {
		LambdaQueryWrapper<AccountBankRel> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.eq(AccountBankRel::getAcctNo, acctNo);
		queryWrapper.last("limit 1");

		return baseMapper.selectOne(queryWrapper);
	}

	private String getTailNumber(Integer bankType, Long userId, Integer userType) {
		List<AccountBankRel> list = null;
		if (null == bankType || bankType < 0) {
			list = this.queryAccountBankRel(userId, userType, null);
		} else {
			list = this.queryAccountBankRel(userId, userType, bankType);
		}


		String tailNumber = "";
		if (CollectionUtils.isEmpty(list)) {
			return tailNumber;
		}

		for (AccountBankRel accountBankRel : list) {
			String acctNo = accountBankRel.getAcctNo();
			if (StringUtils.isNotBlank(acctNo)) {
				if (accountBankRel.getIsDefaultAcct() != null && accountBankRel.getIsDefaultAcct() == 1) {
					tailNumber = acctNo.substring(acctNo.length() - 4) + "【默认账户】" + (tailNumber.length() == 0 ? "" : "、") + tailNumber;
				} else {
					tailNumber += ((tailNumber.length() == 0 ? "" : "、") + (acctNo.substring(acctNo.length() - 4)));
				}
			}
		}

		return tailNumber;
	}

	private List<Map> getTailNumberNew(Integer bankType, Long userId, Integer userType) {
		List<Map> tailList = null;

		List<AccountBankRel> list = null;
		if (null == bankType || bankType < 0) {
			list = this.queryAccountBankRel(userId, userType, null);
		} else {
			list = this.queryAccountBankRel(userId, userType, bankType);
		}

		if (!CollectionUtils.isEmpty(list)) {
			tailList = new ArrayList<>();
			for (AccountBankRel accountBankRel : list) {
				String acctNo = accountBankRel.getAcctNo();
				Map tailNumberMap = new HashMap();
				if (StringUtils.isNotBlank(acctNo)) {
					tailNumberMap.put("shortAcctNo", acctNo.substring(acctNo.length() - 4));
					tailNumberMap.put("acctNo", acctNo);
					tailNumberMap.put("isDefalut", accountBankRel.getIsDefaultAcct());
				}
			}
		}
		return tailList;
	}

}
