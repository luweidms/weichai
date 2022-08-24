package com.youming.youche.order.provider.mapper.order;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.capital.domain.SysTenantDef;
import com.youming.youche.capital.domain.UserDataInfo;
import com.youming.youche.finance.domain.payable.SysOperator;
import com.youming.youche.order.domain.order.OilRechargeAccountDetailsFlow;
import com.youming.youche.order.vo.OilRechargeAccountDetailsFlowVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * <p>
 * Mapper接口
 * </p>
 *
 * @author liangyan
 * @since 2022-03-23
 */
public interface OilRechargeAccountDetailsFlowMapper extends BaseMapper<OilRechargeAccountDetailsFlow> {

   List<SysTenantDef>  selectAllByBatchId ( @Param("relationName") String relationName,
                                      @Param("relationPhone") String relationPhone);

    List<UserDataInfo> searchAllByBatchIdUserDataInfos(@Param("relationName") String relationName,
                            @Param("relationPhone") String relationPhone);

    List<OilRechargeAccountDetailsFlow> searchAllByBatchIdDetailsFlows( @Param("userIds") List<Long> userIds );


    List<OilRechargeAccountDetailsFlow> searchAllByAccountTypeFlows (@Param("vo") OilRechargeAccountDetailsFlowVo vo,
                                                                     @Param("adminUser") Long adminUser,
                                                                     @Param("busiTypeStr") List<String> busiTypeStr);

    List<SysOperator> selectAllByBatchIdSysOperators  (  @Param("opIdSet") Set<Long> opIdSet);

    List<OilRechargeAccountDetailsFlow> selectAllByOtherBatchIdFlows(@Param("busiCodeSet")Set<String> busiCodeSet,
                                                                     @Param("userId")Long userId);

    List<UserDataInfo> relationUserList (@Param("userIds")Set<Long> userIds);

    List<SysTenantDef>  relationTenantList (@Param("userIds")Set<Long> userIds);
}
