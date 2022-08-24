package com.youming.youche.market.vo.facilitator;

import com.youming.youche.market.annotation.SysStaticDataInfoDict;
import lombok.Data;

import java.io.Serializable;
@Data
public class ServiceInvitationVxVo implements Serializable {
    private Long id;
    /**
     * 租户id
     */
    private Long tenantId;

    private String name;
    /**
     * 站点名称
     */
    private String productName;

    /**
     * 联系人
     */
    private String linkman;

    /**
     * 联系电话
     */
    private String linkPhone;

    /**
     * 站点数量
     */
    private Integer productNum;

    /**
     * 合作类型 1：初次合作，2：修改合作
     */
    private Integer cooperationType;

    private String cooperationTypeName;
    /**
     *  '审核状态（1.未审核、2.审核通过、3.审核未通过）',
     */
    @SysStaticDataInfoDict(dictDataSource = "authState")
    private Integer authState;
    /**
     * 创建日期
     */
    private String createDate;
}
