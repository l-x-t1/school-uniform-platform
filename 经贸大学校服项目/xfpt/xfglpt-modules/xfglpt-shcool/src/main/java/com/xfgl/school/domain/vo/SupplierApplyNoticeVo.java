package com.xfgl.school.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;
import lombok.Data;

@Data
public class SupplierApplyNoticeVo {
    private Long noticeId;
    private Long schoolId;
    private String noticeTitle;
    private String noticeCode;
    private String schoolName;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date bmjzTime;
    private Integer signupLimit;
    private Long signupCount;
    private String isSamplesPic;
    private String samplesPic;
    private String noticeFile;
    private String otherFile;
    private Long applyId;
    private String applyStatus;
    private String qualificationStatus;
    private String statusText;
}
