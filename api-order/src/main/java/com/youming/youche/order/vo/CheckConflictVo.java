package com.youming.youche.order.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class CheckConflictVo implements Serializable {

    private static final long serialVersionUID = 5946054551724714975L;
    /**
     * 判断车辆是否冲突 true:冲突，false:不冲突
     */
    private boolean plateNumberConflict;
    /**
     * 判断主驾是否冲突 true:冲突，false:不冲突
     */
    private boolean driverConflict;
    /**
     * 判断副驾是否冲突 true:冲突，false:不冲突
     */
    private boolean copilotDriverConflict;
}
