package com.youming.youche.record.provider.mapper.contract;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.record.domain.contract.Contract;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youming.youche.record.dto.ContractDto;
import com.youming.youche.record.vo.ContractVo;
import org.apache.ibatis.annotations.Param;
import org.mybatis.spring.annotation.MapperScan;

/**
 * <p>
 * Mapper接口
 * </p>
 *
 * @author Mrchen
 * @since 2022-01-14
 */
@MapperScan
public interface ContractMapper extends BaseMapper<Contract> {

    boolean ContrcatdDel(long id);


    boolean ContrcatdAdd(Contract contract);

    boolean ContrcatdUpdata(Contract contract);



    Page<ContractVo> queryAll(Page<ContractVo> page,@Param("tenantId")  Long tenantId,@Param("contractDto") ContractDto contractDto);
}
