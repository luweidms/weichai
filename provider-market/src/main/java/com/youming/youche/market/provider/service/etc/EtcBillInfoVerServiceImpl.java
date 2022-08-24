package com.youming.youche.market.provider.service.etc;



import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.market.api.etc.IEtcBillInfoVerService;
import com.youming.youche.market.domain.etc.EtcBillInfoVer;
import com.youming.youche.market.provider.mapper.etc.EtcBillInfoVerMapper;
import org.apache.dubbo.config.annotation.DubboService;


/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author Terry
 * @since 2022-03-11
 */
@DubboService(version = "1.0.0")
public class EtcBillInfoVerServiceImpl extends BaseServiceImpl<EtcBillInfoVerMapper, EtcBillInfoVer> implements IEtcBillInfoVerService {


}
