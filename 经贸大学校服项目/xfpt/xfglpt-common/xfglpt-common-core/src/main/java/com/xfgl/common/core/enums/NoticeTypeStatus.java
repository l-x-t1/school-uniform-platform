package com.xfgl.common.core.enums;

/**
 * 公告类型
 *
 * @author ruoyi
 */
public enum NoticeTypeStatus
{
    CGGG("0", "采购公告"),
    CJGG("1", "成交公告"),
    RWGG("2", "入围公告");

    private final String code;
    private final String info;

    NoticeTypeStatus(String code, String info)
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
