package com.youming.youche.market.business.controller.etc;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.commons.base.BaseController;
import com.youming.youche.commons.response.ResponseResult;
import com.youming.youche.commons.web.Header;
import com.youming.youche.market.api.etc.IEtcBillInfoService;
import com.youming.youche.market.api.etc.IEtcMaintainService;
import com.youming.youche.market.domain.etc.EtcBillInfo;
import com.youming.youche.market.dto.etc.EtcBillInfoDto;
import com.youming.youche.market.vo.etc.EtcBillInfoVo;
import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 前端控制器
 * </p>
 *etc账单表
 * 聂杰伟
 * @author Terry
 * @since 2022-03-11
 */
@RestController
@RequestMapping("etc-bill-info")
public class EtcBillInfoController extends BaseController<EtcBillInfo, IEtcBillInfoService> {

    private  static  final Logger LOGGER = LoggerFactory.getLogger(EtcBillInfoController.class);

    @DubboReference(version = "1.0.0")
    private IEtcBillInfoService iEtcBillInfoService;

    @Override
    public IEtcBillInfoService getService() {
        return iEtcBillInfoService;
    }



}
