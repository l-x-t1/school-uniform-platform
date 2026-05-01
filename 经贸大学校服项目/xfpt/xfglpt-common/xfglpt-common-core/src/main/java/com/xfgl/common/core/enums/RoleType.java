package com.xfgl.common.core.enums;

public enum RoleType {
    JYJ("jyj", "教育局")
    , SJYJ("sjyj", "市教育局")
    , XX("xuexiao", "学校");

    private final String code;
    private final String info;

    RoleType(String code, String info)
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
