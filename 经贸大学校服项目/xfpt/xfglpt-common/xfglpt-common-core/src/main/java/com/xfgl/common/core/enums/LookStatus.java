package com.xfgl.common.core.enums;

/**
 * 阅读状态
 *
 * @author ruoyi
 */
public enum LookStatus
{
    YSE("0", "未查看"),
    NO("1", "已查看");

    private final String code;
    private final String info;

    LookStatus(String code, String info)
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
