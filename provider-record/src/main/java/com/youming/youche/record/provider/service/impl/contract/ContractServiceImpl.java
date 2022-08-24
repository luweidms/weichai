package com.youming.youche.record.provider.service.impl.contract;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.constant.SysOperLogConst;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.util.LoginUtils;
import com.youming.youche.record.api.contract.IContractService;
import com.youming.youche.record.domain.contract.Contract;
import com.youming.youche.record.dto.ContractDto;
import com.youming.youche.record.provider.mapper.contract.ContractMapper;
import com.youming.youche.record.vo.ContractVo;
import com.youming.youche.system.api.ISysOperLogService;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;


/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author Mrchen
 * @since 2022-01-14
 */
@DubboService(version = "1.0.0")
public class ContractServiceImpl extends BaseServiceImpl<ContractMapper, Contract> implements IContractService {

    @Resource
    private ContractMapper contractMapper;

    @Resource
    private LoginUtils loginUtils;

    @Resource
    private ISysOperLogService sysOperLogService;

    @Override
    public boolean delete(long id) {
        return contractMapper.ContrcatdDel(id);
    }


    @Override
    public boolean ContractUpdate(Contract contract, String accessToken) {
        sysOperLogService.save(SysOperLogConst.BusiCode.contract, Long.valueOf(contract.getId()), SysOperLogConst.OperType.Update, "修改合同信息", accessToken);
        return contractMapper.ContrcatdUpdata(contract);
    }

    @Override
    public boolean ContractAdd(Contract contract, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        contract.setTenantId(loginInfo.getTenantId());

//        baseMapper.insert(contract);
        save(contract);
        sysOperLogService.save(SysOperLogConst.BusiCode.contract, Long.valueOf(contract.getId()), SysOperLogConst.OperType.Add, "新增合同信息", loginInfo);
        return true;
    }

    @Override
    public Page<ContractVo> queryAll(Page<ContractVo> page, String accessToken, ContractDto contractDto) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        Page<ContractVo> ContractVoPage = baseMapper.queryAll(page, loginInfo.getTenantId(), contractDto);
        return ContractVoPage;
    }

//    private SysUser get(String accessToken) {
//        SysUser sysUser = null;
//        if (redisUtil.hasKey("user_info:" + accessToken)) {
//            sysUser = (SysUser) redisUtil.get("user:" + accessToken);
//        }
//        return sysUser;
//    }


}
