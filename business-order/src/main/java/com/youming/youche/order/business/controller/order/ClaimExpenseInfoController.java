package com.youming.youche.order.business.controller.order;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.commons.base.BaseController;
import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.commons.response.ResponseResult;
import com.youming.youche.commons.web.Header;
import com.youming.youche.finance.constant.OaLoanConsts;
import com.youming.youche.finance.dto.ClaimExpenseInfoOut;
import com.youming.youche.order.api.order.IClaimExpenseInfoService;
import com.youming.youche.order.domain.order.ClaimExpenseInfo;
import com.youming.youche.order.dto.ClaimExpenseInfoDto;
import com.youming.youche.order.vo.ClaimExpenseInfoInVo;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
* <p>
* 车管报销表 前端控制器
* </p>
* @author liangyan
* @since 2022-03-22
*/
@RestController
@RequestMapping("claim/expense/info")
public class ClaimExpenseInfoController extends BaseController<ClaimExpenseInfo, IClaimExpenseInfoService> {

    @DubboReference(version = "1.0.0")
    IClaimExpenseInfoService claimExpenseInfoService;
    @Override
    public IClaimExpenseInfoService getService() {
        return claimExpenseInfoService;
    }

    /**
     * 借支报销-报销列表查询
     * 接口编码：21009
     */
    @GetMapping("queryClaimExpense")
    public ResponseResult queryClaimExpense(ClaimExpenseInfoInVo infoIn,Integer pageSize,Integer pageNum) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        infoIn.setExpenseType(OaLoanConsts.EXPENSE_TYPE.DRIVER);

//        if(StringUtils.isNotBlank(infoIn.getQueryMonth())){
//            String startTime = infoIn.getQueryMonth().substring(0,4)+"-"+infoIn.getQueryMonth().substring(4,6)+"-01";
//            String endTime = monthAddFrist(startTime);
//            infoIn.setStartTime(startTime);
//            infoIn.setEndTime(endTime);
//        }
        Page<ClaimExpenseInfoDto> claimExpenseInfoPage = claimExpenseInfoService.doQuery(infoIn,pageSize,pageNum,accessToken);
        return ResponseResult.success(claimExpenseInfoPage);
    }

    /**
     * 月份加一
     * @param date
     * @return
     */
    public static String monthAddFrist(String date){

        DateFormat df=new SimpleDateFormat("yyyy-MM-dd");
        try {
            Calendar ct=Calendar.getInstance();
            ct.setTime(df.parse(date));
            ct.add(Calendar.MONTH, +1);
            return df.format(ct.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }
}
