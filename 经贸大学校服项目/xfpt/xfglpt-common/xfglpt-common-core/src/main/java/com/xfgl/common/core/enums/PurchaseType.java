package com.xfgl.common.core.enums;

/**
 * 选举类型
 *
 * @author ruoyi
 */
public enum PurchaseType
{
    ZZXJ("0", "选用组织自主确定企业"),
    JZXJ("1", "一校为主模式"),
    DSFZB("2", "选用组织委托招标机构确定企业");

    private final String code;
    private final String info;

    PurchaseType(String code, String info)
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
