package com.youming.youche.order.business.controller;


import com.youming.youche.commons.base.BaseController;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.response.ResponseResult;
import com.youming.youche.commons.web.Header;
import com.youming.youche.order.api.ICreditRatingRuleService;
import com.youming.youche.order.domain.CreditRatingRule;
import com.youming.youche.order.dto.CreditRatingRuleDto;
import com.youming.youche.order.dto.SaveOrUpdateCreditRatingRuleDto;
import com.youming.youche.order.vo.CreditRatingRuleVo;
import com.youming.youche.order.vo.QueryMemberBenefitsVo;
import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;


/**
* <p>
*  前端控制器
* </p>
* @author liangyan
* @since 2022-03-04
*/
@RestController
@RequestMapping("/order/creditRatingRule")
public class CreditRatingRuleController  {
    private static final Logger LOGGER = LoggerFactory.getLogger(CreditRatingRuleController.class);

    @DubboReference(version = "1.0.0")
    ICreditRatingRuleService iCreditRatingRuleService;
    @Resource
    protected HttpServletRequest request;
    public ICreditRatingRuleService getService() {
        return iCreditRatingRuleService;
    }

    /***
     * 录入订单页面 指派车辆选择（外调车、业务招商车、自有车（承包模式）、指派车队）的时候查询该司机成本模式比例
     * @Param carUserType   会员类型：1社会车、2招商车、3自有车、4、个体户、5、外调合同车
     * @Param guideMerchant   招商指导价格（对应单位分） 承包价 中标价
     * @Param guidePrice   社会指导价格（对应单位分） 拦标价
     * @return
     * @throws Exception
     */
    @GetMapping("/getLevel")
    public ResponseResult getLevel(@RequestParam("carUserType") Integer carUserType,
                                   @RequestParam("guideMerchant") Float guideMerchant,
                                   @RequestParam("guidePrice")Double guidePrice) {
            String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
            CreditRatingRuleVo level = iCreditRatingRuleService.getLevel(carUserType, accessToken,guideMerchant,guidePrice);

            return ResponseResult.success(level);
    }

    /**
     * 公司自有车、外调车、业务招商车司机权益比例设置接口
     * @Param  carUserType  1社会车、2招商车、3自有车、4、个体户、5、外调合同车
     * @return
     */
    @GetMapping("/queryMemberBenefits")
    public ResponseResult queryMemberBenefits()  {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        QueryMemberBenefitsVo queryMemberBenefitsVo = iCreditRatingRuleService.queryMemberBenefits(accessToken);
        return ResponseResult.success(queryMemberBenefitsVo);
    }

    /**
     * 司机权益 新增司机各种比例接口
     * @Param  carUserType  1社会车、2招商车、3自有车、4、个体户、5、外调合同车
     * @return
     */
    @PostMapping("/saveOrUpdateCreditRatingRule")
    public ResponseResult saveOrUpdateCreditRatingRule(@RequestBody SaveOrUpdateCreditRatingRuleDto saveOrUpdateCreditRatingRuleDto)  {
            String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
            iCreditRatingRuleService.saveOrUpdateCreditRatingRule(saveOrUpdateCreditRatingRuleDto,accessToken);
            return ResponseResult.success("修改成功");
    }

    /**
     * 14007
     * 查询司机权益接口
     *
     * @param tenantId    租户ID
     * @param carUserType 车量类型 1-公司自有车，2-招商车挂靠车，3-临时外调车
     * @return
     * @throws Exception
     */
    @PostMapping("queryCreditRatingRule")
    public ResponseResult queryCreditRatingRule(Long tenantId, Integer carUserType) {
        if (carUserType == 1) {
            carUserType = 3;
        } else if (carUserType == 2) {
            carUserType = 2;
        } else if (carUserType == 3) {
            carUserType = 5;
        }
        CreditRatingRule creditRatingRule = iCreditRatingRuleService.queryCreditRatingRuleWx(tenantId, carUserType);
        return ResponseResult.success(creditRatingRule);
    }

}
