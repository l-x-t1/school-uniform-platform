package com.xfgl.common.core.enums;

/**
 * 是否
 *
 * @author ruoyi
 */
public enum YesOrNoStatus
{
    NO("0", "否"),
    YES("1", "是");

    private final String code;
    private final String info;

    YesOrNoStatus(String code, String info)
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
