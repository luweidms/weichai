package com.youming.youche.finance.business.controller.ac;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.commons.base.BaseController;
import com.youming.youche.commons.response.ResponseResult;
import com.youming.youche.commons.web.Header;
import com.youming.youche.finance.api.ac.IHurryBillRecordService;
import com.youming.youche.finance.domain.ac.HurryBillRecord;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
* <p>
*  前端控制器
* </p>
* @author zengwen
* @since 2022-04-24
*/
@RestController
@RequestMapping("hurry/bill/record")
public class HurryBillRecordController extends BaseController<HurryBillRecord, IHurryBillRecordService> {

    @DubboReference(version = "1.0.0")
    IHurryBillRecordService hurryBillRecordService;

    @Override
    public IHurryBillRecordService getService() {
        return hurryBillRecordService;
    }

    /**
     * 催票通知 21163
     */
    @PostMapping("/doQueryNoticeOfReminder")
    public ResponseResult doQueryNoticeOfReminder(Long userId, Integer type,
                                                  @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                                  @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        Page<HurryBillRecord> page = hurryBillRecordService.doQueryNoticeOfReminder(accessToken, userId, type, pageNum, pageSize);
        return ResponseResult.success(page);
    }
}
