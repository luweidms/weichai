package com.youming.youche.record.provider.mapper.sys;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youming.youche.record.domain.sys.SysSmsBusiness;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 短信服务商表Mapper接口
 * </p>
 *
 * @author Terry
 * @since 2021-11-19
 */
public interface SysSmsBusinessMapper extends BaseMapper<SysSmsBusiness> {


    String MAXBid(@Param("phone") String phone,@Param("tenantId")  Long tenantId);
}
