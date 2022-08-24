package com.youming.youche.system.business.controller;


import com.youming.youche.commons.base.BaseController;
import com.youming.youche.commons.response.ResponseCode;
import com.youming.youche.commons.response.ResponseResult;
import com.youming.youche.commons.web.Header;
import com.youming.youche.system.api.ISysExpenseService;
import com.youming.youche.system.domain.SysExpense;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * <p>
 * 费用类型 前端控制器
 * </p>
 *
 * @author Terry
 * @since 2022-01-20
 */
@RestController
@RequestMapping("/sys/expense")
public class SysExpenseController extends BaseController<SysExpense, ISysExpenseService> {

    @DubboReference(version = "1.0.0")
    ISysExpenseService sysExpenseService;

    @Override
    public ISysExpenseService getService() {
        return sysExpenseService;
    }

    /**
     * 方法实现说明  新增上报费用
     * @author      terry
     * @param domain {@link SysExpense}
     * @return      com.youming.youche.commons.response.ResponseResult
     * @exception
     * @date        2022/5/31 11:09
     */
    @Override
    public ResponseResult create(@Valid @RequestBody SysExpense domain) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        boolean created = sysExpenseService.create(domain, accessToken);
        return created ? ResponseResult.success("创建成功") : ResponseResult.failure();
    }

    /**
     * 方法实现说明  获取当前登录者所有的上报费用
     * @author      terry
     * @param
     * @return      com.youming.youche.commons.response.ResponseResult
     * @exception
     * @date        2022/5/31 11:10
     */
    @GetMapping({"getAll"})
    public ResponseResult get() {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        List<SysExpense> sysExpenses = sysExpenseService.selectAll(accessToken);
        return ResponseResult.success(sysExpenses);
    }

    /**
     * 方法实现说明  根据主键修改上报费用
     * @author      terry
     * @param id
     * @return      com.youming.youche.commons.response.ResponseResult
     * @exception
     * @date        2022/5/31 11:11
     */
    @PatchMapping({"{id}"})
    public ResponseResult update(@PathVariable("id")Long id) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        boolean updated = sysExpenseService.update(id,accessToken);
        return updated ? ResponseResult.success("修改成功") : ResponseResult.failure("修改失败");
    }

}
