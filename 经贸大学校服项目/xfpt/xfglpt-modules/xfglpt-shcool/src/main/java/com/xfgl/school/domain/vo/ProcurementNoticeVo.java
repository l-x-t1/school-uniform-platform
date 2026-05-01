package com.xfgl.school.domain.vo;

import lombok.Data;
import lombok.ToString;

import java.util.Date;

/**
 * 选购公告Vo
 */
@Data
@ToString
public class ProcurementNoticeVo {

    /**
     * 公告名称
     */
    private String noticeName;

    /**
     * 选购模式
     */
    private String purchaseMode;

    /**
     * 发布时间
     */
    private Date releaseTime;

    /**
     * 选购公告id
     */
    private Long id;

}
