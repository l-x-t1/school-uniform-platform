package com.xfgl.school.domain.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Size exchange record list item.
 */
@Data
public class XfptSizeExchangeVo {

    private Long id;

    private Long afterSaleId;

    private String afterSaleNo;

    private Long orderId;

    private Long orderItemId;

    private String orderNo;

    private Long goodsId;

    private String productName;

    private String productCode;

    private String categoryName;

    private String imageUrl;

    private Long supplierId;

    private String manufacturerName;

    private BigDecimal price;

    private Date operateTime;

    private String status;

    private String sizeName;

    private String targetSizeName;

    private Integer applyNum;

    private Long deptId;

    private String reason;

    private String handleResult;

    private String handleRemark;

    private Date createTime;
}
