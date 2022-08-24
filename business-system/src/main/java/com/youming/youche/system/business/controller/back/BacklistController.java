package com.youming.youche.system.business.controller.back;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.commons.base.BaseController;
import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.response.ResponseResult;
import com.youming.youche.commons.web.Header;
import com.youming.youche.system.api.back.IBacklistService;
import com.youming.youche.system.domain.back.Backlist;
import com.youming.youche.system.vo.QueryBacklistParamVo;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 黑名单 前端控制器
 * </p>
 *
 * @author hzx
 * @since 2022-04-22
 */
@RestController
@RequestMapping("backlist")
public class BacklistController extends BaseController {

    @DubboReference(version = "1.0.0")
    IBacklistService iBacklistService;

    @Override
    public IBaseService getService() {
        return null;
    }

    /**
     * 接口编号：14514
     * 黑名单-查询
     */
    @GetMapping("query")
    public ResponseResult query(String queryParam, Integer backType,
                                @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        Page<Backlist> query = iBacklistService.query(queryParam, backType != null && backType > 0 ? backType : null,
                accessToken, pageNum, pageSize);
        return ResponseResult.success(query);
    }

    /**
     * 接口编号：14516
     * 发布黑名单
     */
    @PostMapping("save")
    public ResponseResult save(@RequestBody Backlist backlist) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        iBacklistService.save(backlist, accessToken);
        return ResponseResult.success();
    }

    /**
     * 接口编号：14518
     * 黑名单-查询司机信息，根据司机姓名查询司机信息
     * <p>
     * 司机列表查询
     * <p>
     * 入参：loginAcct 账号
     * linkman 姓名
     * carUserType 司机类型
     * attachTenantName 归属车队名称
     * attachTennantLinkman 车队联系人
     * attachTennantLinkPhone 车队联系人电话
     * state 认证状态
     * <p>
     * 出参：loginAcct 账号
     * linkman 姓名
     * carUserType 司机类型
     * carUserTypeName 司机类型名称
     * attachTenantId 归属车队id
     * attachTenantName 归属车队名称
     * attachTennantLinkman 车队联系人
     * attachTennantLinkPhone 车队联系人电话
     * vehicleNum 车辆数量
     * state 认证状态
     * stateName 认证状态名称
     * hasVer 审核状态
     * userId 用户编号
     * createDate 创建时间
     */
    @GetMapping("queryDriver")
    public ResponseResult queryDriver(String loginAcct, String linkman, Integer carUserType,
                                      String attachTenantName, String attachTennantLinkman,
                                      String attachTennantLinkPhone, Integer state,
                                      @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                      @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        Page page = iBacklistService.doQueryCarDriver(loginAcct, linkman, carUserType, attachTenantName, attachTennantLinkman,
                attachTennantLinkPhone, state, accessToken, pageNum, pageSize);
        return ResponseResult.success(page);
    }

    /**
     * 接口编号：14515
     * 查询订单：判断一个司机一个月内与当前车队是否有合作订单
     */
    @PostMapping("haveOrderLastMonth")
    public ResponseResult haveOrderLastMonth(Long userId) {
        if (userId <= 0) {
            throw new BusinessException("请选择司机");
        }
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        Boolean result = iBacklistService.haveOrderLastMonth(userId, accessToken);
        return ResponseResult.success(result);
    }

    /**
     * 接口编号：14519
     * 检查黑名单
     */
    @GetMapping("checkBacklist")
    public ResponseResult checkBacklist(QueryBacklistParamVo vo,
                                        @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                        @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        Page<Backlist> backlistPage = iBacklistService.checkBacklist(vo, accessToken, pageNum, pageSize);
        return ResponseResult.success(backlistPage);
    }

    /**
     * 14520
     * 黑名单-获取详情
     */
    @PostMapping("getBacklistById")
    public ResponseResult getBacklistById(Long id) {
        Backlist byId = iBacklistService.getById(id);
        if (byId == null) {
            byId = new Backlist();
        }
        return ResponseResult.success(byId);
    }

    /**
     * 移除黑名单
     *
     * @param id
     * @return
     */
    @PostMapping("remove")
    public ResponseResult removeBackList(String id) {
        boolean b = iBacklistService.removeById(id);
        if (b) {
            return ResponseResult.success();
        } else {
            return ResponseResult.failure();
        }
    }

}
