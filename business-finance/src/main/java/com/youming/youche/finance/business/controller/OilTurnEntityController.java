package com.youming.youche.finance.business.controller;


import com.youming.youche.commons.base.BaseController;
import com.youming.youche.commons.base.IBaseService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
* <p>
*  前端控制器
* </p>
* @author luona
* @since 2022-04-20
*/
@RestController
@RequestMapping("oil-turn-entity")
public class OilTurnEntityController extends BaseController {

    @Override
    public IBaseService getService() {
        return null;
    }
}
