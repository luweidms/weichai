package com.youming.youche.system.domain.ioer;

import com.baomidou.mybatisplus.annotation.TableField;
import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;

import java.io.Serializable;

/**
 * 导入导出记录表
 */
@Data
@Deprecated
public class ImportOrExportRecords extends com.youming.youche.commons.domain.ImportOrExportRecords {

   /* *//**
     * 业务编码
     *//*
    private String bussinessCode;
    *//**
     * 车队id
     *//*
    private Long tenantId;
    *//**
     * 操作人
     *//*
    private Long opId;
    *//**
     * 操作人名称
     *//*
    @TableField(exist = false)
    private String opName;
    *//**
     * 1. 导入 2.导出
     *//*
    private Integer bussinessType;
    *//**
     *  文件名称
     *//*
    private String mediaName;
    *//**
     * 文件Url
     *//*
    private String mediaUrl;
    *//**
     * 失败文件URL
     *//*
    private String failureUrl;
    *//**
     * 业务名称
     *//*
    private String name;
    private String remarks;
    *//**
     * 失败原因
     *//*
    private String failureReason;
    *//**
     * 获取:  状态
     * 导入 ：1.导入中 2.导入部分成功 3.导入成功 4.导入失败
     * 导出：1.导出中 2.导出成功 3.导出中断：请重新下载！ 4.导出失败：没有数据! 5、导入
     *//*
    private Integer state;*/


}
