package com.xfgl.common.core.enums;

/**
 * 招标模式菜单
 *选购公告0  招标机构合作合同1  评估招标结果 2
 * @author ruoyi
 */
public enum OrgabizationModeType
{
    XCTP("0", "选用组织现场投票确定企业"),
    XSTP("1", "选用组织线上投票确定企业"),
    ZZQD("2", "选用组织自主确定企业");

    private final String code;
    private final String info;

    OrgabizationModeType(String code, String info)
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
