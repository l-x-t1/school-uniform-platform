package com.xfgl.system.api.domain;

import java.io.Serializable;

/**
 * 公告发布人信息（轻量 DTO，避免 XfptNotice Feign 反序列化问题）
 */
public class NoticePublisherVo implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long userId;
    private String createBy;

    public NoticePublisherVo() {}

    public NoticePublisherVo(Long userId, String createBy) {
        this.userId = userId;
        this.createBy = createBy;
    }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getCreateBy() { return createBy; }
    public void setCreateBy(String createBy) { this.createBy = createBy; }
}
