package com.youming.youche.system.business.controller;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.youming.youche.commons.base.BaseController;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.response.ResponseResult;
import com.youming.youche.components.fdfs.FastDFSHelper;
import com.youming.youche.system.api.ISysAttachService;
import com.youming.youche.system.domain.SysAttach;
import com.youming.youche.system.vo.SysAttachVo;
import org.apache.dubbo.config.annotation.DubboReference;
import org.csource.fastdfs.ClientGlobal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * <p>
 * 图片资源表 前端控制器
 * </p>
 *
 * @author Terry
 * @since 2022-01-17
 */
@RestController
@RequestMapping("/sys/attach")
public class SysAttachController extends BaseController<SysAttach, ISysAttachService> {
    @DubboReference(version = "1.0.0")
    ISysAttachService sysAttachService;

    private static final Logger LOGGER = LoggerFactory.getLogger(SysAttachController.class);

    @Value("${spring.profiles.active}")
    private String configuration;

    @Override
    public ISysAttachService getService() {
        return sysAttachService;
    }

    /***
     * @Description: 文件上传
     * @Author: luwei
     * @Date: 2022/1/17 4:14 下午
     * @Param fileItem:
     * @return: java.lang.String
     * @Version: 1.0
     **/
    @PostMapping({"doUpload"})
    public ResponseResult doUpload(MultipartFile fileItem) {
        if (fileItem == null) {
            throw new BusinessException("未获取到上传的文件！");
        } else {
            try {
                FastDFSHelper client = FastDFSHelper.getInstance();
                String fileExtName = "";
                String fileName = fileItem.getOriginalFilename();
                int lastPos = 0;
                int pos = 0;
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("=======fileName======" + fileName);
                }
                if ((pos = fileName.lastIndexOf("/")) > 0) {
                    lastPos = pos;
                } else if ((pos = fileName.lastIndexOf("\\")) > 0) {
                    lastPos = pos;
                }
                if (lastPos > 0) {
                    fileName = fileName.substring(lastPos + 1);
                }
                if (fileName.length() > 100) {
                    fileName = fileName.substring(fileName.length() - 100);
                }
                if (fileName.contains(".")) {
                    fileExtName = fileName.substring(fileName.lastIndexOf(".") + 1);
                    if (!FastDFSHelper.allowFileExtSets.contains(fileExtName.toLowerCase())) {
                        throw new BusinessException("上传的文件后缀不在范围内!");
                    } else {
                        String path = client.upload(fileItem.getInputStream(), fileName, fileItem.getSize());
                        if (configuration.equals("pro")) {
                            path = path.replaceAll("group1/M00/", "");
                        }
                        LOGGER.info("path:" + path);
                        SysAttach attach = new SysAttach();
                        attach.setFileName(fileName);
                        attach.setFileSize(fileItem.getSize());
                        attach.setFileType(fileExtName);
                        attach.setStorePath(path);
                        attach.setRemark("tracker_server:" + ClientGlobal.g_tracker_group.tracker_servers[0].getHostName());
                        Long flowId = sysAttachService.saveById(attach);
                        return ResponseResult.success(flowId);
                    }
                } else {
                    throw new BusinessException("未知文件后缀!");
                }
            } catch (Exception e) {
                e.getMessage();
                return ResponseResult.failure(e.getMessage());
            }
        }
    }

    /***
     * @Description: 文件删除
     * @Author: luwei
     * @Date: 2022/1/17 4:18 下午
     * @Param flowId:
     * @return: java.lang.String
     * @Version: 1.0
     **/
    @PostMapping("doDel")
    public ResponseResult doDel(Long flowId) {
        try {
            SysAttach attach = sysAttachService.get(flowId);
            if (attach != null) {
                FastDFSHelper client = FastDFSHelper.getInstance();
                client.delete(attach.getStorePath());
                sysAttachService.remove(flowId);
                return ResponseResult.success();
            } else {
                return ResponseResult.failure("删除失败，未知文件");
            }
        } catch (Exception e) {
            return ResponseResult.failure(e.getMessage());
        }
    }

    /***
     * @Description: 查看图片
     * @Author: luwei
     * @Date: 2022/1/18 6:49 下午
     * @Param flowIds:
     * @return: com.youming.youche.commons.response.ResponseResult
     * @Version: 1.0
     **/
    @GetMapping("doQuery")
    public ResponseResult doQuery(String flowIds) {
        String[] flowIdStr = flowIds.split(",");
        Long[] flowIdArray = new Long[flowIdStr.length];
        int i = 0;
        String[] var5 = flowIdStr;
        int var6 = flowIdStr.length;
        for (int var7 = 0; var7 < var6; ++var7) {
            String flowId = var5[var7];
            if (StrUtil.isNotEmpty(flowId)) {
                flowIdArray[i++] = Long.parseLong(flowId);
            }
        }
        try {
            if (i == 0) {
                return ResponseResult.success(new ArrayList(0));
            } else {
                List<SysAttach> list = sysAttachService.listByIds(Arrays.asList(flowIdArray));
                List<SysAttachVo> listVo = new ArrayList<>();
                FastDFSHelper client = FastDFSHelper.getInstance();
                Iterator var9 = list.iterator();
                while (var9.hasNext()) {
                    SysAttach sysAttach = (SysAttach) var9.next();
                    SysAttachVo sysAttachVo = new SysAttachVo();
                    BeanUtil.copyProperties(sysAttach, sysAttachVo);
                    if (StrUtil.isNotEmpty(sysAttachVo.getStorePath())) {
                        String tag = sysAttachVo.getStorePath().substring(sysAttachVo.getStorePath().lastIndexOf(".") + 1);
                        sysAttachVo.setStorePath(sysAttachVo.getStorePath().substring(0, sysAttachVo.getStorePath().indexOf(".")) + "_big." + tag);
                    }
                    sysAttachVo.setFullPath(client.getHttpURL(sysAttach.getStorePath(), sysAttach.getFileName()));
                    listVo.add(sysAttachVo);
                }
                return ResponseResult.success(listVo);
            }
        } catch (Exception e) {
            return ResponseResult.failure(e.getMessage());
        }
    }


    /**
     * 方法实现说明 返回业务相关的资源
     *
     * @param businessId
     * @param businessCode
     * @return com.youming.youche.commons.response.ResponseResult
     * @throws
     * @author terry
     * @date 2022/1/23 16:33
     */
    @Deprecated()
    @GetMapping({"get/{businessId}/{businessCode}"})
    public ResponseResult get(@PathVariable Long businessId, @PathVariable Integer businessCode) {
        List<SysAttach> list = sysAttachService.selectAllByBusinessIdAndBusinessCode(businessId, businessCode);
        return ResponseResult.success(list);
    }

//    public static void main(String[] args) {
//        SysAttach sysAttachVo = new SysAttach();
//        sysAttachVo.setStorePath("group1/M00/00/BF/rBEQD2LOWzWAfkWiAADYOEJX0OU844.png");
//        String tag = sysAttachVo.getStorePath().substring(sysAttachVo.getStorePath().indexOf(".") + 1);
//        System.out.println(sysAttachVo.getStorePath().substring(0, sysAttachVo.getStorePath().indexOf(".")) + "_big." + tag);
//    }

}
