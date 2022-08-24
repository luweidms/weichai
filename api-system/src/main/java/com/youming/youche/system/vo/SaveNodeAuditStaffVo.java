package com.youming.youche.system.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @version:
 * @Title: SaveNodeAuditStaffVo
 * @Package: com.youming.youche.system.vo
 * @Description: TODO(用一句话描述该文件做什么)
 * @author:DengYuanYe
 * @date: 2022/2/10 17:11
 * @company:
 */
@Data
public class SaveNodeAuditStaffVo implements Serializable {
   /**
    * 阶段id
    */
   private Long nodeId;
   /**
    * 业务编码
    */
   private String auditCode;
   /**
    * 审核人员id
    */
   private String targetObjId;
   /**
    * 父节点id
    */
   private Long parentNodeId;
   /**
    * 审核人员类型
    */
   private Integer targetObjType;
   /**
    * 审核失败是否退回发起人0否1是
    */
   private Integer flag;
}
