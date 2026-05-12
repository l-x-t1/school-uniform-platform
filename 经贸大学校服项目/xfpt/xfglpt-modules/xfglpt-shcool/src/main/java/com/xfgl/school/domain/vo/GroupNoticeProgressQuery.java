package com.xfgl.school.domain.vo;

import java.math.BigDecimal;

public class GroupNoticeProgressQuery {
    private Long schoolId;
    private Long deptId;
    private String dateType;
    private Boolean enableDateRange;
    private String startTime;
    private String endTime;
    private BigDecimal orderRateMin;
    private BigDecimal orderRateMax;
    private String manufacturerName;

    public Long getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(Long schoolId) {
        this.schoolId = schoolId;
    }

    public Long getDeptId() {
        return deptId;
    }

    public void setDeptId(Long deptId) {
        this.deptId = deptId;
    }

    public String getDateType() {
        return dateType;
    }

    public void setDateType(String dateType) {
        this.dateType = dateType;
    }

    public Boolean getEnableDateRange() {
        return enableDateRange;
    }

    public void setEnableDateRange(Boolean enableDateRange) {
        this.enableDateRange = enableDateRange;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public BigDecimal getOrderRateMin() {
        return orderRateMin;
    }

    public void setOrderRateMin(BigDecimal orderRateMin) {
        this.orderRateMin = orderRateMin;
    }

    public BigDecimal getOrderRateMax() {
        return orderRateMax;
    }

    public void setOrderRateMax(BigDecimal orderRateMax) {
        this.orderRateMax = orderRateMax;
    }

    public String getManufacturerName() {
        return manufacturerName;
    }

    public void setManufacturerName(String manufacturerName) {
        this.manufacturerName = manufacturerName;
    }
}
