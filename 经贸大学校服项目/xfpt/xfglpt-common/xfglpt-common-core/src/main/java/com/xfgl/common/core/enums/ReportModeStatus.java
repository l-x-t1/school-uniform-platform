package com.xfgl.common.core.enums;

/**
 * 招标模式菜单
 *选购公告0  招标机构合作合同1  评估招标结果 2
 * @author ruoyi
 */
public enum ReportModeStatus
{
    XGGG("0", "选购公告"),
    HZHT("1", "招标机构合作合同"),
    PGZBJG("2", "评估招标结果");

    private final String code;
    private final String info;

    ReportModeStatus(String code, String info)
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
