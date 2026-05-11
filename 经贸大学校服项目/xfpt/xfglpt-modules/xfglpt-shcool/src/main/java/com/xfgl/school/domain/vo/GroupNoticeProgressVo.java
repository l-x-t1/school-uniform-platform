package com.xfgl.school.domain.vo;

import java.math.BigDecimal;

public class GroupNoticeProgressVo {
    private Long id;
    private Long noticeId;
    private Long supplierId;
    private String noticeName;
    private String manufacturerName;
    private Long promotionCount;
    private Long expectedSalesCount;
    private Long orderCount;
    private BigDecimal orderRate;
    private String timeRange;
    private String remainTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getNoticeId() {
        return noticeId;
    }

    public void setNoticeId(Long noticeId) {
        this.noticeId = noticeId;
    }

    public Long getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Long supplierId) {
        this.supplierId = supplierId;
    }

    public String getNoticeName() {
        return noticeName;
    }

    public void setNoticeName(String noticeName) {
        this.noticeName = noticeName;
    }

    public String getManufacturerName() {
        return manufacturerName;
    }

    public void setManufacturerName(String manufacturerName) {
        this.manufacturerName = manufacturerName;
    }

    public Long getPromotionCount() {
        return promotionCount;
    }

    public void setPromotionCount(Long promotionCount) {
        this.promotionCount = promotionCount;
    }

    public Long getExpectedSalesCount() {
        return expectedSalesCount;
    }

    public void setExpectedSalesCount(Long expectedSalesCount) {
        this.expectedSalesCount = expectedSalesCount;
    }

    public Long getOrderCount() {
        return orderCount;
    }

    public void setOrderCount(Long orderCount) {
        this.orderCount = orderCount;
    }

    public BigDecimal getOrderRate() {
        return orderRate;
    }

    public void setOrderRate(BigDecimal orderRate) {
        this.orderRate = orderRate;
    }

    public String getTimeRange() {
        return timeRange;
    }

    public void setTimeRange(String timeRange) {
        this.timeRange = timeRange;
    }

    public String getRemainTime() {
        return remainTime;
    }

    public void setRemainTime(String remainTime) {
        this.remainTime = remainTime;
    }
}
