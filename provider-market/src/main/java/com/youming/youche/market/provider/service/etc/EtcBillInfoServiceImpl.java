package com.youming.youche.market.provider.service.etc;

import com.alibaba.nacos.api.utils.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.market.api.etc.IEtcBillInfoService;
import com.youming.youche.market.domain.etc.EtcBillInfo;
import com.youming.youche.market.dto.etc.EtcBillInfoDto;
import com.youming.youche.market.provider.mapper.etc.EtcBillInfoMapper;
import com.youming.youche.market.vo.etc.EtcBillInfoVo;
import org.apache.dubbo.config.annotation.DubboService;

import java.util.List;


/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author Terry
 * @since 2022-03-11
 */
@DubboService(version = "1.0.0")
public class EtcBillInfoServiceImpl extends BaseServiceImpl<EtcBillInfoMapper, EtcBillInfo> implements IEtcBillInfoService {


}
