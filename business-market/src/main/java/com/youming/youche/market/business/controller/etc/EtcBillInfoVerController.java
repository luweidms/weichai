package com.youming.youche.market.business.controller.etc;


import com.youming.youche.commons.base.BaseController;
import com.youming.youche.market.api.etc.IEtcBillInfoVerService;
import com.youming.youche.market.domain.etc.EtcBillInfo;
import com.youming.youche.market.domain.etc.EtcBillInfoVer;
import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  前端控制器
 *  etc账单VER表
 *  聂杰伟
 * </p>
 * @author Terry
 * @since 2022-03-11
 */
@RestController
@RequestMapping("etc-bill-info-ver")
public class EtcBillInfoVerController extends BaseController<EtcBillInfoVer, IEtcBillInfoVerService> {

    private  static  final Logger LOGGER = LoggerFactory.getLogger(EtcBillInfoVerController.class);

    @DubboReference(version = "1.0.0")
    private IEtcBillInfoVerService iEtcBillInfoVerService;
    @Override
    public IEtcBillInfoVerService getService() {
        return iEtcBillInfoVerService;
    }

}
