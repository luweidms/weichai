package com.youming.youche.order.provider.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.order.domain.OilCardManagement;
import com.youming.youche.order.domain.service.ServiceInfo;
import com.youming.youche.order.dto.order.GetOilCardByCardNumDto;
import com.youming.youche.order.dto.order.OilCardPledgeOrderListDto;
import com.youming.youche.order.vo.GetOilCardByCardNumVo;
import com.youming.youche.order.vo.OilCardPledgeOrderListVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* <p>
* 油卡管理表Mapper接口
* </p>
* @author liangyan
* @since 2022-03-07
*/
    public interface OilCardManagementMapper extends BaseMapper<OilCardManagement> {

        ServiceInfo updateOilCarNum(@Param("cardNum") String cardNum, @Param("tenantId") Long tenantId);

        List<GetOilCardByCardNumDto> doQuery(@Param("vo") GetOilCardByCardNumVo vo);

        Page<OilCardPledgeOrderListDto> queryOilCardPledgeOrderInfo(Page<OilCardPledgeOrderListDto> page, @Param("vo") OilCardPledgeOrderListVo vo);
    }
