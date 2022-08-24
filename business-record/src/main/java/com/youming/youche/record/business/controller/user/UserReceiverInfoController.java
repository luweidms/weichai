package com.youming.youche.record.business.controller.user;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.commons.base.BaseController;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.response.ResponseCode;
import com.youming.youche.commons.response.ResponseResult;
import com.youming.youche.commons.util.DataFormat;
import com.youming.youche.commons.web.Header;
import com.youming.youche.record.api.tenant.IUserReceiverInfoService;
import com.youming.youche.record.domain.tenant.TenantReceiverRel;
import com.youming.youche.record.domain.user.UserReceiverInfo;
import com.youming.youche.record.dto.tenant.TenantReceiverRelDto;
import com.youming.youche.record.vo.tenant.TenantReceiverRelVo;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("user/userReceiverInfo/")
public class UserReceiverInfoController extends BaseController<UserReceiverInfo, IUserReceiverInfoService> {


    @Override
    public IUserReceiverInfoService getService() {
        return userReceiverInfoService;
    }

    @DubboReference(version = "1.0.0")
    IUserReceiverInfoService userReceiverInfoService;


    /**
     * 查询收款人管理列表信息
     * @param pageNum 分页参数
     * @param pageSize 分页参数
     * @param tenantReceiverRelVo 首款人列表查询条件
     * @return
     * @throws Exception
     */
    @GetMapping("get/list")
    public ResponseResult queryUserReceiverInfo(@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                                @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                                                TenantReceiverRelVo tenantReceiverRelVo) throws Exception {
        try {
            String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
            Page<TenantReceiverRelDto> page = new Page<>(pageNum, pageSize);
            Page<TenantReceiverRelDto> page1 = userReceiverInfoService.queryAll(page, accessToken, tenantReceiverRelVo);
            return ResponseResult.success(page1);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseResult.failure("查询失败");
        }

    }


    /**
     * 修改收款人信息
     *
     * @param tenantReceiverRel 车队与收款人的关联
     * @return
     */
    @PutMapping("tenantUpdate")
    public ResponseResult tenantUpdate(@RequestBody TenantReceiverRel tenantReceiverRel) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        boolean updated = userReceiverInfoService.tenantUpdate(tenantReceiverRel, accessToken);
        return updated ? ResponseResult.success("编辑成功") : ResponseResult.failure(ResponseCode.INTERFACE_ADDRESS_INVALID);
    }

    /**
     * 根据id查询收款人信息
     *
     * @param relId 收款人主键
     * @return 收款人信息
     */
    @GetMapping("gets/{relId}")
    public ResponseResult gets(@PathVariable("relId") Long relId) {
        if (relId <= 0) {
            throw new BusinessException("不存在的收款人信息");
        }
        TenantReceiverRel tenantReceiverRel = userReceiverInfoService.contractById(relId);
        return ResponseResult.success(tenantReceiverRel);
    }

    /**
     * 根据手机号查询 收款人
     *
     * @param mobilePhone 手机号
     * @return 收款人信息
     */
    @GetMapping("checkReceiverInfo")
    public ResponseResult checkReceiverInfo(String mobilePhone) {
        try {
            String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
            TenantReceiverRel tenantReceiverRel = userReceiverInfoService.checkUserReceiver(mobilePhone, accessToken);
            return ResponseResult.success(null == tenantReceiverRel ? "" : tenantReceiverRel);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseResult.failure("查询失败");
        }
    }


    /**
     * 根据id删除收款人信息
     *
     * @param relId 主键id
     * @return
     */
    @DeleteMapping("removeById/{relId}")
    public ResponseResult removeById(@PathVariable("relId") Long relId) {
        if (relId <= 0) {
            throw new BusinessException("不存在的收款人信息");
        }
        boolean deleted = userReceiverInfoService.deleteTeantReceiverById(relId);
        return deleted ? ResponseResult.success("删除成功") : ResponseResult.failure(ResponseCode.INTERFACE_ADDRESS_INVALID);
    }

    /**
     * 收款人管理新增
     *
     * @param tenantReceiverRel 新增收款人信息
     * @return
     */
    @PostMapping("user/create")
    public ResponseResult tenantAdd(@RequestBody TenantReceiverRel tenantReceiverRel) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        boolean created = false;
        try {
            created = userReceiverInfoService.tenantAdd(tenantReceiverRel, accessToken);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return created ? ResponseResult.success("添加成功") : null;
    }

    /**
     * 10062 查询代收人信息
     *
     * @param phone 手机号码
     * @return 代收人信息
     */
    @PostMapping("getUserReceiverInfo")
    public ResponseResult getUserReceiverInfo(String phone) {
        if (StringUtils.isBlank(phone)) {
            throw new BusinessException("请传入手机号码");
        }
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));

        UserReceiverInfo userReceiverInfo = userReceiverInfoService.getUserReceiverInfo(phone, accessToken);

        return ResponseResult.success(userReceiverInfo);
    }

}
