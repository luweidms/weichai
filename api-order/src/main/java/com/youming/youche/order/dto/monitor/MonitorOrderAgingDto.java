package com.youming.youche.order.dto.monitor;

import lombok.Data;

import java.io.Serializable;

/**
 * 运营大屏
 *
 * @author hzx
 * @date 2022/3/9 11:14
 */
@Data
public class MonitorOrderAgingDto implements Serializable {

    private static final long serialVersionUID = -1L;

    // 时效
    private Integer aging;

    // 晚靠台
    private Integer depend;

    // 异常停留
    private Integer stay;

    // 堵车缓行
    private Integer amble;

    // 预计延迟
    private Integer estLast;

    // 迟到
    private Integer late;

    public MonitorOrderAgingDto() {
    }

    public MonitorOrderAgingDto(Integer aging, Integer depend, Integer stay, Integer amble, Integer estLast, Integer late) {
        this.aging = aging;
        this.depend = depend;
        this.stay = stay;
        this.amble = amble;
        this.estLast = estLast;
        this.late = late;
    }

}
