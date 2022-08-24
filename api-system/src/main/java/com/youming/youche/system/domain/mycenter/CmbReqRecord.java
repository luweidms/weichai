package com.youming.youche.system.domain.mycenter;

import com.baomidou.mybatisplus.annotation.TableField;
import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @ClassName CmbReqRecord
 * @Description 添加描述
 * @Author zag
 * @Date 2022/3/2 10:23
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class CmbReqRecord extends BaseDomain {

    @TableField("reqNo")
    private String reqNo;
    @TableField("tranFunc")
    private String tranFunc;
    @TableField("reqData")
    private String reqData;
    @TableField("respNo")
    private String respNo;
    @TableField("respCode")
    private String respCode;
    @TableField("respMsg")
    private String respMsg;
    @TableField("respData")
    private String respData;
}
