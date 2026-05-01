package com.xfgl.school.domain.vo;

import com.xfgl.common.core.annotation.Excel;
import lombok.Data;

@Data
public class UserName {

    @Excel(name = "学生姓名")
    private String name;

    @Excel(name = "电话")
    private String phone;
}
