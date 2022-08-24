package com.youming.youche.market.business.controller.youka;


import com.youming.youche.commons.base.BaseController;
import com.youming.youche.market.api.youka.IOilEntityService;
import com.youming.youche.market.domain.youka.OilEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author
 * @since 2022-02-14
 */
@RestController
@RequestMapping("oil-entity")
public class OilEntityController  extends BaseController<OilEntity, IOilEntityService> {


    @Override
    public IOilEntityService getService() {
        return null;
    }
}
