package com.xfgl.common.core.enums;

/**
 * 文件类型
 *
 * @author ruoyi
 */
public enum FileTypeStatus
{
    FILE("1", "文档类型"),
    PIC("2", "图片类型");

    private final String code;
    private final String info;

    FileTypeStatus(String code, String info)
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
