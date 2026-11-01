package com.xfgl.school.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * 发货单统计返回对象
 */
@Data
public class DeliveryOrderStatisticsVo {
    private Long id;

    private Long noticeId;

    private Long manufacturerId;

    private String noticeName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date paymentDeadline;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date expectedArrivalDate;

    private String manufacturerName;

    private String status;

    private String statusText;
}
