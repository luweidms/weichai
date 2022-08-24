package com.youming.youche.record.api.contract;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import com.youming.youche.record.domain.contract.Contract;
import com.youming.youche.commons.base.IBaseService;

import com.youming.youche.record.dto.ContractDto;
import com.youming.youche.record.vo.ContractVo;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author Mrchen
 * @since 2022-01-14
 */
public interface IContractService extends IBaseService<Contract> {

    // 根据id删除数据
    boolean delete(long id);

    // 修改
    boolean ContractUpdate(Contract contract, String accessToken);

    // 新增
    boolean ContractAdd(Contract contract,String accessToken);

    // 分页查询所有记录
    Page<ContractVo> queryAll(Page<ContractVo> page, String accessToken, ContractDto contractDto);

}
