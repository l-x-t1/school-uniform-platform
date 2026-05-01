package com.xfgl.common.core.enums;

/**
 * 发布状态
 *
 * @author ruoyi
 */
public enum NoticeStatus
{
    CX("2", "撤销"),
    FB("1", "发布"),
    WFB("0", "未发布");

    private final String code;
    private final String info;

    NoticeStatus(String code, String info)
    {
        this.code = code;
        this.info = info;
    }

    public String getCode()
    {
        return code;
    }

    public String getInfo()
    {
        return info;
    }
}
